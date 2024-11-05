# JTetrisJavaFX

An implementation of the classic game of Tetris, written in JavaFX.

## Usage

Clone the project into a directory of your choice.

From the project root directory, execute

`./gradlew run`

## Key Bindings
- *left arrow* - Move left.
- *right arrow* - Move right.
- *up arrow* - Rotate counterclockwise.
- *down arrow* - Rotate clockwise.
- *spacebar* - Toggles downward acceleration. Press again to stop the
acceleration.
- *p* - Pause the game.
- *q* - Quit the game.

## General Design

The game reads from and updates a backend Java module, [also written
by me](https://github.com/BrandonIrizarry/JTetrisBackend),
representing the game state.

There are two main animations:

1. Downward motion

    This includes the descent of the current piece as triggered by
    gravity, as well as user-applied acceleration.

    Since this descent is automatic, it must be independent from any
    actual gameplay-related animations, which are themselves handled
    in a separate animation (see below.)

    This animation is also responsible for updating the side-panel
    statistics, since events triggered by a move-down are what
    ultimately, for example, increase your score.

2. Other user motions

    This includes left-right movement, as well as rotations.

    This animation is also responsible for rendering the player area itself.
