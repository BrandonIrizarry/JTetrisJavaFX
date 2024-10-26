package xyz.brandonirizarry.jtetrisjavafx;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import xyz.brandonirizarry.game.DownwardCollisionType;
import xyz.brandonirizarry.game.Game;

public class DownwardVelocity {
    final Timeline animationLoop;
    final double defaultRate = 1.0;
    final double fastRate = 20.0;
    double currentRate = defaultRate;

    DownwardVelocity(Timeline mainAnimationLoop, Game game) {
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
