package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.scene.input.KeyCode;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * An interface to factor out some commonality between the various animations
 * our game uses.
 */
public interface AnimationDriver {
    Queue<KeyCode> keyPresses = new ArrayDeque<>();
    double frameRate = 30.0;

    void handleKeyPress(KeyCode keyPress);
    void update();
    void pause();
    void resume();
}
