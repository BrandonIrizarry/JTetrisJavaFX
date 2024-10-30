package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.SplitPane;
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
        gameCanvas.setOnKeyPressed(e -> keyPresses.add(e.getCode()));
        var gameGraphicsContext = gameCanvas.getGraphicsContext2D();

        var sideCanvas = new Canvas(sideWidth, boardHeight);
        var sideGraphicsContext = sideCanvas.getGraphicsContext2D();

        // Set up the player-area animation loop and rendering logic.
        new MainRenderer(gameGraphicsContext);

        // Set up the falling motion as a separate animation loop.
        new DownwardVelocity(sideGraphicsContext);

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
}
