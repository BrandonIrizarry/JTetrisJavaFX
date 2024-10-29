package xyz.brandonirizarry.jtetrisjavafx.constants;

import javafx.scene.input.KeyCode;
import xyz.brandonirizarry.jtetris.game.Game;

import java.util.ArrayDeque;
import java.util.Queue;

public final class Constants {
    public static final Game game;
    public static final int numRows = 20;
    public static final int numColumns = 10;
    public static final double squareUnit = 25.0;
    public static final double boardHeight;
    public static final double boardWidth;
    public static final double sideWidth;
    public static final Queue<KeyCode> keyPresses = new ArrayDeque<>();

    static {
        game = new Game(numRows, numColumns);
        boardHeight = numRows * squareUnit;
        boardWidth = numColumns * squareUnit;
        sideWidth = boardWidth * 0.66;
    }
}
