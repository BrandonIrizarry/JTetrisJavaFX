package xyz.brandonirizarry.jtetrisjavafx.app;

public sealed interface GameSignal {
    final class TogglePause implements GameSignal {
        private int numAnimations;
        private boolean exhausted = false;

        public TogglePause(int numAnimations) {
            this.numAnimations = numAnimations;
        }

        public void decrement() {
            if (this.exhausted) return;

            this.numAnimations--;

            if (this.numAnimations == 0) {
                this.exhausted = true;
            }
        }

        public boolean isExhausted() {
            return this.exhausted;
        }
    }

    record Quit() implements GameSignal { }
    record MoveLeft() implements GameSignal { }
    record MoveRight() implements GameSignal { }
    record RotateCounterclockwise() implements GameSignal { }
    record RotateClockwise() implements GameSignal { }
    record ToggleBoost() implements GameSignal { }
    record None() implements GameSignal { }
}
