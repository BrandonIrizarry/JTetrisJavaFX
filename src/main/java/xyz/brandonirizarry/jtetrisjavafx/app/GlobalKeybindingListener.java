package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class GlobalKeybindingListener implements AnimationDriver {
    Timeline animationLoop;
    final List<AnimationDriver> drivers;
    private boolean isPaused = false;

    GlobalKeybindingListener(AnimationDriver... drivers) {
        this.drivers = List.of(drivers);

        this.animationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/frameRate), e -> {
                    var keyPress = keyPresses.poll();

                    this.handleKeyPress(keyPress);
                })
        );

        this.animationLoop.setCycleCount(Animation.INDEFINITE);
        this.animationLoop.play();
    }

    @Override
    public void handleKeyPress(KeyCode keyPress) {
        if (keyPress == null) {
            return;
        }

        // Let's check up on our keypresses.
        switch (keyPress) {
            case KeyCode.P -> this.togglePause();
            default -> keyPresses.offer(keyPress);
        }
    }

    /**
     * There is no graphics context associated with this animation driver,
     * and so this particular implementation of 'update' is empty.
     */
    @Override
    public void update() { }

    /**
     * Since this animation loop is never meant to be paused,
     * this method remains empty here.
     */
    @Override
    public void pause() { }

    @Override
    public void resume() { }

    private void togglePause() {
        if (this.isPaused) {
            this.drivers.forEach(AnimationDriver::resume);
            this.isPaused = false;
        } else {
            this.drivers.forEach(AnimationDriver::pause);
            this.isPaused = true;
        }
    }
}
