package xyz.brandonirizarry.jtetrisjavafx.app.animation;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import xyz.brandonirizarry.jtetrisjavafx.app.GameSignal;

import static xyz.brandonirizarry.jtetrisjavafx.app.Main.game;
import static xyz.brandonirizarry.jtetrisjavafx.constants.Constants.*;

public class MainRenderer extends AnimationDriver {
    private boolean isQuit = false;

    public MainRenderer(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;

        this.animationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/frameRate), e -> {
                    this.update();

                    if (game.isGameLost() || isQuit) {
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
        // The signal listener queues events even while the game is paused.
        // Hence it's possible for the game state to get updated while the game is
        // paused, which creates a weird effect after the player unpauses.
        // This if-statement guards against that.
        if (gameSignal instanceof GameSignal.UserMotion && this.isPaused()) {
            return;
        }

        // Let's check up on our keypresses.
        switch (gameSignal) {
            case GameSignal.UserMotion.MoveLeft() -> game.moveLeft();
            case GameSignal.UserMotion.MoveRight() -> game.moveRight();
            case GameSignal.UserMotion.RotateCounterclockwise() -> game.rotateCounterclockwise();
            case GameSignal.UserMotion.RotateClockwise() -> game.rotateClockwise();
            case GameSignal.Quit() -> {
                this.resume();
                this.isQuit = true;
            }
            case GameSignal.TogglePause tp -> this.togglePause(tp);
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
