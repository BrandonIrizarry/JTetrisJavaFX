package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static xyz.brandonirizarry.jtetrisjavafx.app.Main.game;
import static xyz.brandonirizarry.jtetrisjavafx.constants.Constants.*;

public class MainRenderer extends AnimationDriver {
    Timeline animationLoop;

    MainRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;

        this.animationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/frameRate), e -> {
                    this.handleGameSignal(gameSignals.poll());
                    this.update();

                    if (game.isGameLost()) {
                        this.onQuit();
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

        this.graphicsContext.setFill(Color.DARKGRAY);
        this.graphicsContext.fillRect(boardWidth - textMargin, 0, textMargin, boardHeight);

        var gameState = game.export();

        for (var rowIndex = 0; rowIndex < numRows; rowIndex++ ) {
            for (var columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                switch (gameState[rowIndex][columnIndex]) {
                    case Empty -> { }
                    case Tetromino -> drawSquare(this.graphicsContext, rowIndex, columnIndex, Color.PURPLE);
                    case Garbage -> drawSquare(this.graphicsContext, rowIndex, columnIndex, Color.GREEN);
                }
            }
        }
    }

    @Override
    public void handleGameSignal(GameSignal gameSignal) {
        // Necessary, because the 'ordinal()' method on KeyCode enum is invoked
        // to perform the switch expression coming up.
        if (gameSignal == null) {
            return;
        }

        // Let's check up on our keypresses.
        switch (gameSignal) {
            case MOVE_LEFT -> game.moveLeft();
            case MOVE_RIGHT -> game.moveRight();
            case ROTATE_COUNTERCLOCKWISE -> game.rotateCounterclockwise();
            case ROTATE_CLOCKWISE -> game.rotateClockwise();
            case QUIT -> this.onQuit();
            default -> gameSignals.offer(gameSignal);
        }
    }

    @Override
    protected void pause() {
        this.animationLoop.pause();
    }

    @Override
    protected void resume() {
        this.animationLoop.play();
    }

    @Override
    protected void onQuit() {
        this.pause();

        this.graphicsContext.clearRect(0, 0, boardWidth, boardHeight);
        this.graphicsContext.setFill(Color.PAPAYAWHIP);
        this.graphicsContext.fillRect(0, 0, boardWidth, boardHeight);

        var gameOverMessage = "Game Over";
        this.graphicsContext.setFill(Color.BLACK);
        this.graphicsContext.fillText(
                gameOverMessage, boardWidth / 2 - gameOverMessage.length() * 3, boardHeight / 2
        );
    }
}
