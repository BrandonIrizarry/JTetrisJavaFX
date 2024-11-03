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
    private static int numAnimations = 0;

    protected static final double frameRate = 30.0;
    protected GraphicsContext graphicsContext;
    protected Timeline animationLoop;
    protected Timeline signalListener = new Timeline(
            new KeyFrame(Duration.millis(1000.0/frameRate), e -> handleGameSignal(gameSignals.poll()))
    );

    private boolean isPaused = false;

    protected void togglePause() {
        if (this.isPaused) {
            this.resume();
        } else {
            this.pause();
        }

        this.isPaused = !this.isPaused;
    }

    public static int getNumAnimations() {
        return numAnimations;
    }

    abstract protected void handleGameSignal(GameSignal gameSignal);
    abstract protected void update();
    abstract protected void resume();
    abstract protected void pause();
    abstract protected void onQuit();

    {
        this.signalListener.setCycleCount(Animation.INDEFINITE);
        this.signalListener.play();
        numAnimations++;
    }
}
