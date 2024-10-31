package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static xyz.brandonirizarry.jtetrisjavafx.constants.Constants.*;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        var gameCanvas = new Canvas(boardWidth, boardHeight);
        gameCanvas.setFocusTraversable(true);
        var gameGraphicsContext = gameCanvas.getGraphicsContext2D();

        var sideCanvas = new Canvas(sideWidth, boardHeight);
        var sideGraphicsContext = sideCanvas.getGraphicsContext2D();

        // Set up the player-area animation loop and rendering logic.
        var mainRenderer = new MainRenderer(gameGraphicsContext);

        // Set up the falling motion as a separate animation loop.
        var downwardVelocity = new DownwardVelocity(sideGraphicsContext);

        // Set up a global keybinding listener to handle key events concerning all animations
        // (for example, pausing the game.)
        var globalKeybindingListener = new GlobalKeybindingListener(mainRenderer, downwardVelocity);
        gameCanvas.setOnKeyPressed(e -> {
            if (!globalKeybindingListener.getIsPaused()) {
                // If the game is paused, don't accept any keypresses, except for
                // a few specific ones (see below.)
                AnimationDriver.keyPresses.add(e.getCode());
            } else if (e.getCode() == KeyCode.P || e.getCode() == KeyCode.Q) {
                // These keycodes are enabled whether or not the game is paused.
                AnimationDriver.keyPresses.add(e.getCode());
            }
        });



        var splitPane = new SplitPane(
                new StackPane(gameCanvas),
                new StackPane(sideCanvas)
        );

        var startingLevel = getStartingLevelFromUser();

        var scene = new Scene(splitPane, boardWidth + sideWidth, boardHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JTetris");
        primaryStage.show();

        game.start();
    }

    private int getStartingLevelFromUser() {
        var dialog = new TextInputDialog();

        dialog.setTitle("JTetrisFX");
        dialog.setHeaderText("Choose your starting level (1-10)");

        var levelSelection = dialog.showAndWait().orElse("0");

        if (levelSelection.isEmpty()) {
            levelSelection = "0";
        }

        var level = Integer.parseInt(levelSelection);

        if (level < 0 || level > 10) {
            var alerter = new Alert(Alert.AlertType.INFORMATION, "Game will start at level 0", ButtonType.OK);
            alerter.setHeaderText("Invalid level");
            alerter.showAndWait();

            level = 0;
        }

        return level;
    }
}
