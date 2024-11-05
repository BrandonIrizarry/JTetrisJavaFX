package xyz.brandonirizarry.jtetrisjavafx.app;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static xyz.brandonirizarry.jtetrisjavafx.app.Main.game;
import static xyz.brandonirizarry.jtetrisjavafx.constants.Constants.*;

/**
 * A class for managing state related to downward movement (for
 * example, what happens when a piece lands.)<br><br>
 *
 * This class is also responsible for updating the side-panel
 * display, since all game statistics displayed there are updated
 * only whenever a piece lands.
 */
public class DownwardVelocity extends AnimationDriver {
    private final double initialRate = 1.0/frameRate;
    private final double boostedRate = this.initialRate * 20.0;
    private double currentRate = initialRate;
    private boolean boostOn = false;

    DownwardVelocity(GraphicsContext sideGraphicsContext) {
        this.graphicsContext = sideGraphicsContext;
        this.update();

        this.animationLoop = new Timeline(
                new KeyFrame(Duration.millis(1000.0/frameRate), e -> {
                    var freeFallInProgress = game.moveDown();

                    if (boostOn) {
                        if (!freeFallInProgress) {
                            this.turnOffBoost();
                            this.animationLoop.setRate(this.currentRate);
                        }
                    } else {
                        var level = game.getLevel();
                        this.currentRate = ((level + 1.0) * 0.25)/frameRate;
                        this.turnOffBoost();
                    }

                    this.update();
                })
        );

        this.animationLoop.setCycleCount(Animation.INDEFINITE);
        this.animationLoop.setRate(this.currentRate);
        this.animationLoop.play();
    }

    @Override
    protected void update() {
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
        this.graphicsContext.fillText(scoreText, textMargin, 10 + textMargin);
        this.graphicsContext.fillText(levelText, textMargin, 30 + textMargin);
        this.graphicsContext.fillText(numLinesClearedText, textMargin, 50 + textMargin);
        this.graphicsContext.fillText("Rate: %f".formatted(this.currentRate), textMargin, 70 + textMargin); // debug

        // Display available keybindings.
        this.graphicsContext.fillText("left: left-arrow", textMargin, 200);
        this.graphicsContext.fillText("right: right-arrow", textMargin, 220);
        this.graphicsContext.fillText("rotate counterclockwise: up-arrow", textMargin, 240);
        this.graphicsContext.fillText("rotate clockwise: down-arrow", textMargin, 260);
        this.graphicsContext.fillText("toggle fast-drop: spacebar", textMargin, 280);
        this.graphicsContext.fillText("pause game: p", textMargin, 300);
        this.graphicsContext.fillText("quit game: q", textMargin, 320);
    }

    @Override
    protected void handleGameSignal(GameSignal gameSignal) {
        switch (gameSignal) {
            case GameSignal.TogglePause tp -> this.togglePause(tp);
            case GameSignal.Quit() -> this.pause();
            case GameSignal.ToggleBoost() -> this.toggleBoost();
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
        this.turnOffBoost();
    }

    @Override
    protected void onQuit() { }

    private void turnOnBoost() {
        this.animationLoop.setRate(this.boostedRate);
        boostOn = true;
    }

    private void turnOffBoost() {
        this.animationLoop.setRate(this.currentRate);
        boostOn = false;
    }

    private void toggleBoost() {
        if (boostOn) {
            this.turnOffBoost();
        } else {
            this.turnOnBoost();
        }
    }
}
