package xyz.brandonirizarry.jtetrisjavafx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import xyz.brandonirizarry.game.DownwardCollisionType;
import xyz.brandonirizarry.game.Game;

import java.util.ArrayDeque;
import java.util.Queue;

public class Main extends Application {
    private static final int NUM_ROWS = 20;
    private static final int NUM_COLUMNS = 10;
    private static final double SQUARE_UNIT = 25.0;
    private static final double BOARD_HEIGHT = NUM_ROWS * SQUARE_UNIT;
    private static final double BOARD_WIDTH = NUM_COLUMNS * SQUARE_UNIT;
    private static final double SIDE_WIDTH = BOARD_WIDTH * 0.66;

    private static final Game game = new Game(NUM_ROWS, NUM_COLUMNS);
    private static final Queue<KeyCode> keyPresses = new ArrayDeque<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var gameCanvas = new Canvas(BOARD_WIDTH, BOARD_HEIGHT);
        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(e -> Main.keyPresses.add(e.getCode()));
        var gameGraphicsContext = gameCanvas.getGraphicsContext2D();
        configureAnimations(gameGraphicsContext);

        var sideCanvas = new Canvas(SIDE_WIDTH, BOARD_HEIGHT);
        var sideGraphicsContext = sideCanvas.getGraphicsContext2D();

        var splitPane = new SplitPane(
                new StackPane(gameCanvas),
                new StackPane(sideCanvas)
        );

        var scene = new Scene(splitPane, BOARD_WIDTH + SIDE_WIDTH, BOARD_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JTetris");
        primaryStage.show();

        game.start();
    }

    private void drawSquare(GraphicsContext graphicsContext, int rowIndex, int columnIndex, Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(columnIndex *  SQUARE_UNIT, rowIndex * SQUARE_UNIT, SQUARE_UNIT, SQUARE_UNIT);
    }

    private void configureAnimations(GraphicsContext graphicsContext) {
        // This will run the 'update' method 60 times per second
        var mainAnimationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/30), e -> updatePlayerArea(graphicsContext))
        );

        var moveDownAnimationLoop = new DownwardVelocity(mainAnimationLoop);

        var keyPressAnimationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/30), e -> handleKeyPress(Main.keyPresses.poll(), moveDownAnimationLoop))
        );

        mainAnimationLoop.setCycleCount(Animation.INDEFINITE);
        keyPressAnimationLoop.setCycleCount(Animation.INDEFINITE);
        mainAnimationLoop.play();
        keyPressAnimationLoop.play();
    }

    private static class DownwardVelocity {
        final Timeline animationLoop;
        final double defaultRate = 1.0;
        final double fastRate = 20.0;
        double currentRate = defaultRate;

        DownwardVelocity(Timeline mainAnimationLoop) {
            this.animationLoop = new Timeline(
                    new KeyFrame(Duration.millis(1000.0), e -> {
                        var collisionType = game.moveDown();

                        if (collisionType == DownwardCollisionType.GameLost) {
                            mainAnimationLoop.pause();
                        } else if (collisionType != DownwardCollisionType.FreeFall) {
                            this.decelerate();
                        }
                    })
            );

            this.animationLoop.setCycleCount(Animation.INDEFINITE);
            this.animationLoop.play();
        }

        private void accelerate() {
            this.animationLoop.setRate(this.fastRate);
            this.currentRate = this.fastRate;
        }

        private void decelerate() {
            this.animationLoop.setRate(this.defaultRate);
            this.currentRate = this.defaultRate;
        }

        void toggleAcceleration() {
            if (this.currentRate == this.fastRate) {
                this.decelerate();
            } else if (this.currentRate == this.defaultRate) {
                this.accelerate();
            }
        }
    }

    private void handleKeyPress(KeyCode keyPress, DownwardVelocity moveDownAnimationLoop) {
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
            case KeyCode.SPACE -> moveDownAnimationLoop.toggleAcceleration();
            default -> { }
        }
    }

    private void updatePlayerArea(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        graphicsContext.setFill(Color.PAPAYAWHIP);
        graphicsContext.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        var gameState = Main.game.export();

        for (var rowIndex = 0; rowIndex < NUM_ROWS; rowIndex++ ) {
            for (var columnIndex = 0; columnIndex < NUM_COLUMNS; columnIndex++) {
                switch (gameState[rowIndex][columnIndex]) {
                    case Empty -> { }
                    case Tetromino -> drawSquare(graphicsContext, rowIndex, columnIndex, Color.PURPLE);
                    case Garbage -> drawSquare(graphicsContext, rowIndex, columnIndex, Color.DARKGRAY);
                }
            }
        }
    }
}
