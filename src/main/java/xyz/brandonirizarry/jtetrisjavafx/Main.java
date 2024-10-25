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

    public void update(GraphicsContext graphicsContext, KeyCode keyPress) {
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

        handleKeyPress(keyPress);
    }

    private void drawSquare(GraphicsContext graphicsContext, int rowIndex, int columnIndex, Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(columnIndex *  SQUARE_UNIT, rowIndex * SQUARE_UNIT, SQUARE_UNIT, SQUARE_UNIT);
    }

    private void configureAnimations(GraphicsContext graphicsContext) {
        // This will run the 'update' method 60 times per second
        var mainAnimationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/30), e -> update(graphicsContext, Main.keyPresses.poll()))
        );

        var moveDownAnimationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/3), e -> {
                    var collisionType = game.moveDown();

                    if (collisionType == DownwardCollisionType.GameLost) {
                        mainAnimationLoop.pause();
                    }
                })
        );

        mainAnimationLoop.setCycleCount(Animation.INDEFINITE);
        moveDownAnimationLoop.setCycleCount(Animation.INDEFINITE);
        mainAnimationLoop.play();
        moveDownAnimationLoop.play();
    }

    private void handleKeyPress(KeyCode keyPress) {
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
            default -> { }
        }
    }
}
