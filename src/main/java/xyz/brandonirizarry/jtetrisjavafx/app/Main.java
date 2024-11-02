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
import xyz.brandonirizarry.jtetris.game.Game;

import static xyz.brandonirizarry.jtetrisjavafx.constants.Constants.*;

public class Main extends Application {
    public static Game game;
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

        var startingLevel = getStartingLevelFromUser();

        // Important: this needs to be called before any of the animations are
        // started, since they use the game state in their logic, which could include
        // a preconfigured level setting.
        Main.game = new Game(numRows, numColumns, startingLevel);

        // Set up the player-area animation loop and rendering logic.
        new MainRenderer(gameGraphicsContext);

        // Set up the falling motion as a separate animation loop.
        new DownwardVelocity(sideGraphicsContext);

        gameCanvas.setOnKeyPressed(e -> {
            var signal = switch (e.getCode()) {
                case KeyCode.P -> GameSignal.TOGGLE_PAUSE;
                case KeyCode.Q -> GameSignal.QUIT;
                case KeyCode.LEFT -> GameSignal.MOVE_LEFT;
                case KeyCode.RIGHT -> GameSignal.MOVE_RIGHT;
                case KeyCode.UP -> GameSignal.ROTATE_COUNTERCLOCKWISE;
                case KeyCode.DOWN -> GameSignal.ROTATE_CLOCKWISE;
                case KeyCode.SPACE -> GameSignal.TOGGLE_BOOST;
                default -> GameSignal.NONE;
            };

            AnimationDriver.gameSignals.add(signal);
        });


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
