package xyz.brandonirizarry.jtetrisjavafx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static xyz.brandonirizarry.jtetrisjavafx.constants.Constants.*;

/**
 * A class for managing state related to downward movement (for
 * example, what happens when a piece lands.)<br><br>
 *
 * This class is also responsible for updating the side-panel
 * display, since all game statistics displayed there are updated
 * only whenever a piece lands.
 */
public class DownwardVelocity {
    Timeline animationLoop;
    final double initialRate = 1.0/frameRate;
    final double boostedRate = this.initialRate * 20.0;
    double currentRate = initialRate;
    boolean boostOn = false;
    GraphicsContext graphicsContext;

    DownwardVelocity(GraphicsContext sideGraphicsContext) {
        this.graphicsContext = sideGraphicsContext;
        this.updateSidebar();

        this.animationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/frameRate), e -> {
                    var freeFallInProgress = game.moveDown();

                    if (!freeFallInProgress) {
                        this.turnOffBoost();
                        this.animationLoop.setRate(this.currentRate);
                    }

                    this.updateSidebar();
                    this.currentRate = (game.getLevel() + 1.0)/frameRate;
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

    void updateSidebar() {
        this.graphicsContext.clearRect(0, 0, boardWidth, boardHeight);
        this.graphicsContext.setFill(Color.PAPAYAWHIP);
        this.graphicsContext.fillRect(0, 0, boardWidth, boardHeight);

        var score = game.getScore();
        var level = game.getLevel();
        var numLinesCleared = game.getNumLinesCleared();

        var scoreText = "Score: %d".formatted(score);
        var levelText = "Level: %d".formatted(level);
        var numLinesClearedText = "Lines cleared: %d".formatted(numLinesCleared);

        this.graphicsContext.setFill(Color.BLACK);
        this.graphicsContext.fillText(scoreText, 0, 10);
        this.graphicsContext.fillText(levelText, 0, 30);
        this.graphicsContext.fillText(numLinesClearedText, 0, 50);
        this.graphicsContext.fillText("Rate: %f".formatted(this.currentRate), 0, 70); // debug
    }
}
