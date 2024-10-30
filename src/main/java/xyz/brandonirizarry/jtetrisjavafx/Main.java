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

import static xyz.brandonirizarry.jtetrisjavafx.constants.Constants.*;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var gameCanvas = new Canvas(boardWidth, boardHeight);
        gameCanvas.setFocusTraversable(true);
        gameCanvas.setOnKeyPressed(e -> keyPresses.add(e.getCode()));
        var gameGraphicsContext = gameCanvas.getGraphicsContext2D();

        var sideCanvas = new Canvas(sideWidth, boardHeight);
        var sideGraphicsContext = sideCanvas.getGraphicsContext2D();

        // This sets up the falling motion as a separate animation loop.
        var downwardVelocity = new DownwardVelocity(sideGraphicsContext);

        var keyPressAnimationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/30), e -> handleKeyPress(keyPresses.poll(), downwardVelocity))
        );

        keyPressAnimationLoop.setCycleCount(Animation.INDEFINITE);
        keyPressAnimationLoop.play();

        // This will run the 'update' method 60 times per second
        Timeline[] mainAnimationLoop = new Timeline[1];

        mainAnimationLoop[0] = new Timeline(
                new KeyFrame(Duration.millis(1000.0/30), e -> {
                    updatePlayerArea(gameGraphicsContext);

                    if (game.isGameLost()) {
                        mainAnimationLoop[0].pause();
                    }
                })
        );

        mainAnimationLoop[0].setCycleCount(Animation.INDEFINITE);
        mainAnimationLoop[0].play();

        var splitPane = new SplitPane(
                new StackPane(gameCanvas),
                new StackPane(sideCanvas)
        );

        var scene = new Scene(splitPane, boardWidth + sideWidth, boardHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JTetris");
        primaryStage.show();

        game.start();
    }

    private void drawSquare(GraphicsContext graphicsContext, int rowIndex, int columnIndex, Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(columnIndex *  squareUnit, rowIndex * squareUnit, squareUnit, squareUnit);
    }

    private void updatePlayerArea(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0, 0, boardWidth, boardHeight);
        graphicsContext.setFill(Color.PAPAYAWHIP);
        graphicsContext.fillRect(0, 0, boardWidth, boardHeight);

        var gameState = game.export();

        for (var rowIndex = 0; rowIndex < numRows; rowIndex++ ) {
            for (var columnIndex = 0; columnIndex < numColumns; columnIndex++) {
                switch (gameState[rowIndex][columnIndex]) {
                    case Empty -> { }
                    case Tetromino -> drawSquare(graphicsContext, rowIndex, columnIndex, Color.PURPLE);
                    case Garbage -> drawSquare(graphicsContext, rowIndex, columnIndex, Color.DARKGRAY);
                }
            }
        }
    }

    void handleKeyPress(KeyCode keyPress, DownwardVelocity downwardVelocity) {
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
            case KeyCode.SPACE -> downwardVelocity.toggleBoost();
            default -> { }
        }
    }
}

/**
 * A class for managing state related to downward movement (for
 * example, what happens when a piece lands.)<br><br>
 *
 * This class is also responsible for updating the side-panel
 * display, since all game statistics displayed there are updated
 * only whenever a piece lands.
 */
class DownwardVelocity {
    final Timeline animationLoop;
    final double initialRate = 1.0;
    final double boostedRate = 20.0;
    double currentRate = initialRate;
    boolean boostOn = false;

    DownwardVelocity(GraphicsContext sideGraphicsContext) {
        this.updateSidebar(sideGraphicsContext);

        this.animationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0), e -> {
                    var freeFallInProgress = game.moveDown();

                    if (!freeFallInProgress) {
                        this.turnOffBoost();
                    }

                    this.updateSidebar(sideGraphicsContext);
                    this.currentRate = game.getLevel() + 0.5;
                })
        );

        this.animationLoop.setCycleCount(Animation.INDEFINITE);
        this.animationLoop.setRate(this.currentRate);
        this.animationLoop.play();
    }

    private void turnOnBoost() {
        this.animationLoop.setRate(this.boostedRate);
        boostOn = true;
    }

    private void turnOffBoost() {
        this.animationLoop.setRate(this.currentRate);
        boostOn = false;
    }

    void toggleBoost() {
        if (boostOn) {
            this.turnOffBoost();
        } else {
            this.turnOnBoost();
        }
    }

    void updateSidebar(GraphicsContext sideGraphicsContext) {
        sideGraphicsContext.clearRect(0, 0, boardWidth, boardHeight);
        sideGraphicsContext.setFill(Color.PAPAYAWHIP);
        sideGraphicsContext.fillRect(0, 0, boardWidth, boardHeight);

        var score = game.getScore();
        var level = game.getLevel();
        var numLinesCleared = game.getNumLinesCleared();

        var scoreText = "Score: %d".formatted(score);
        var levelText = "Level: %d".formatted(level);
        var numLinesClearedText = "Lines cleared: %d".formatted(numLinesCleared);

        sideGraphicsContext.setFill(Color.BLACK);
        sideGraphicsContext.fillText(scoreText, 0, 10);
        sideGraphicsContext.fillText(levelText, 0, 30);
        sideGraphicsContext.fillText(numLinesClearedText, 0, 50);
    }
}
