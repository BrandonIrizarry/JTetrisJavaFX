package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static xyz.brandonirizarry.jtetrisjavafx.constants.Constants.*;

public class MainRenderer implements AnimationDriver {
    GraphicsContext graphicsContext;
    Timeline animationLoop;

    MainRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;

        this.animationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/frameRate), e -> {
                    this.handleKeyPress(keyPresses.poll());
                    this.update();

                    if (game.isGameLost()) {
                        this.animationLoop.pause();
                    }
                })
        );

        this.animationLoop.setCycleCount(Animation.INDEFINITE);
        this.animationLoop.play();
    }

    @Override
    public void update() {
        this.graphicsContext.clearRect(0, 0, boardWidth, boardHeight);
        this.graphicsContext.setFill(Color.PAPAYAWHIP);
        this.graphicsContext.fillRect(0, 0, boardWidth, boardHeight);

        var gameState = game.export();

        for (var rowIndex = 0; rowIndex < numRows; rowIndex++ ) {
            for (var columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                switch (gameState[rowIndex][columnIndex]) {
                    case Empty -> { }
                    case Tetromino -> drawSquare(this.graphicsContext, rowIndex, columnIndex, Color.PURPLE);
                    case Garbage -> drawSquare(this.graphicsContext, rowIndex, columnIndex, Color.DARKGRAY);
                }
            }
        }
    }

    @Override
    public void handleKeyPress(KeyCode keyPress) {
        // Necessary, because the 'ordinal()' method on KeyCode enum is invoked
        // to perform the switch expression coming up.
        if (keyPress == null) {
            return;
        }

        // Let's check up on our keypresses.
        switch (keyPress) {
            case KeyCode.LEFT -> game.moveLeft();
            case KeyCode.RIGHT -> game.moveRight();
            case KeyCode.UP -> game.rotateCounterclockwise();
            case KeyCode.DOWN -> game.rotateClockwise();
            default -> keyPresses.offer(keyPress);
        }
    }
}
