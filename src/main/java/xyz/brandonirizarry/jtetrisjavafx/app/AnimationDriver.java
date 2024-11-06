package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * An interface to factor out some commonality between the various animations
 * our game uses.
 */
public abstract class AnimationDriver {
    public static final Queue<GameSignal> gameSignals = new ArrayDeque<>();
    protected static final double frameRate = 30.0;
    private static int numAnimations = 0;

    protected GraphicsContext graphicsContext;
    protected Timeline animationLoop;
    protected Timeline signalListener = new Timeline(
            new KeyFrame(Duration.millis(1000.0/frameRate), e -> {
                var signal = gameSignals.poll();

                // This function in turn calls the implemented
                // 'handleGameSignal', but does so while eliminating the
                // need for the child class to handle the case of a
                // null signal.
                this.handlePossiblyNullGameSignal(signal);
            })
    );

    private boolean paused = false;

    public static int getNumAnimations() {
        return numAnimations;
    }

    protected boolean isPaused() {
        return this.paused;
    }

    /*
     * An initializer shared by child animations.
     */
    {
        this.signalListener.setCycleCount(Animation.INDEFINITE);
        this.signalListener.play();
        numAnimations++;
    }

    protected void togglePause(GameSignal.TogglePause tp) {
        // If all animations have consumed a pause signal, this animation shouldn't
        // react to that signal.
        if (tp.isExhausted()) return;

        if (this.paused) {
            this.resume();
        } else {
            this.pause();
        }

        this.paused = !this.paused;

        // Mark the fact that this animation has consumed a pause signal.
        tp.decrement();

        // We may as well stop forwarding the signal if the current animation
        // was the last to consume it: only forward the signal if the
        // TogglePause object isn't yet exhausted.
        if (!tp.isExhausted()) {
            gameSignals.offer(tp);
        }
    }

    /**
     * A method to filter out null game signals so that child classes
     * don't need to handle this case.
     *
     * @param gameSignal The game signal as polled directly from the gameSignals queue.
     *                   Note that it can be possibly null.
     */
    private void handlePossiblyNullGameSignal(GameSignal gameSignal) {
        if (gameSignal == null) return;

        this.handleGameSignal(gameSignal);
    }

    abstract protected void handleGameSignal(GameSignal gameSignal);
    abstract protected void update();
    abstract protected void resume();
    abstract protected void pause();
    abstract protected void onQuit();
}
