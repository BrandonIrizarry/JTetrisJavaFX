package xyz.brandonirizarry.jtetrisjavafx.constants;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import xyz.brandonirizarry.jtetris.game.Game;

public final class Constants {
    public static final int numRows = 20;
    public static final int numColumns = 10;
    public static final double squareUnit = 25.0;
    public static final double boardHeight;
    public static final double boardWidth;
    public static final double sideWidth;
    public static final double textMargin = 10.0;

    static {
        boardHeight = numRows * squareUnit;
        boardWidth = numColumns * squareUnit + textMargin;
        sideWidth = boardWidth;
    }

    public static void drawSquare(GraphicsContext graphicsContext, int rowIndex, int columnIndex, Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(columnIndex *  squareUnit, rowIndex * squareUnit, squareUnit, squareUnit);
    }
}
