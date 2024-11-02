package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * An interface to factor out some commonality between the various animations
 * our game uses.
 */
public abstract class AnimationDriver {
    public static final Queue<GameSignal> gameSignals = new ArrayDeque<>();

    protected static final double frameRate = 30.0;
    protected GraphicsContext graphicsContext;
    protected Timeline animationLoop;

    private boolean isPaused = false;

    public void togglePause() {
        if (this.isPaused) {
            this.resume();
        } else {
            this.pause();
        }

        this.isPaused = !this.isPaused;
    }

    abstract protected void handleGameSignal(GameSignal gameSignal);
    abstract protected void update();
    abstract protected void resume();
    abstract protected void pause();
    abstract protected void onQuit();
}
