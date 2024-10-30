package xyz.brandonirizarry.jtetrisjavafx.constants;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
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

    static {
        game = new Game(numRows, numColumns);
        boardHeight = numRows * squareUnit;
        boardWidth = numColumns * squareUnit;
        sideWidth = boardWidth * 0.66;
    }

    public static void drawSquare(GraphicsContext graphicsContext, int rowIndex, int columnIndex, Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(columnIndex *  squareUnit, rowIndex * squareUnit, squareUnit, squareUnit);
    }
}
