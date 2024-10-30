package xyz.brandonirizarry.jtetrisjavafx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
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

        // Set up the player-area animation loop and rendering logic.
        new MainRenderer(gameGraphicsContext);

        // Set up the falling motion as a separate animation loop.
        var downwardVelocity = new DownwardVelocity(sideGraphicsContext);

        var keyPressAnimationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/frameRate), e -> handleKeyPress(keyPresses.poll(), downwardVelocity))
        );

        keyPressAnimationLoop.setCycleCount(Animation.INDEFINITE);
        keyPressAnimationLoop.play();

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
