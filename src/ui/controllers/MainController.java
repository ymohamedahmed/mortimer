package ui.controllers;

import engine.Piece;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class MainController {

    public Canvas chessPane;

    public void initialize() {
        setupChessPane(Piece.getInitialPieceList());
    }


    private void setupChessPane(ArrayList<Piece> pieceList) {
        GraphicsContext g = chessPane.getGraphicsContext2D();
        double width = chessPane.getWidth();
        double height = chessPane.getHeight();
        double cellSize = Math.ceil(Math.min(width / 8.0, height / 8.0));


        int squareNo = 0;
        while (squareNo <= 63) {
            int x = squareNo % 8;
            int y = squareNo / 8;
            if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
                g.setFill(CellColor.COLOR1.getColor());
            } else {
                g.setFill(CellColor.COLOR2.getColor());
            }
            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            squareNo++;
        }

        for (Piece piece : pieceList) {
            g.drawImage(Piece.getImage(piece.getPieceType(), piece.getColor()), (7 - piece.getPos().getCol()) * cellSize, (7 - piece.getPos().getRow()) * cellSize, cellSize, cellSize);


        }

    }

    private enum CellColor {
        COLOR1(Color.rgb(140, 82, 66)), COLOR2(Color.rgb(255, 255, 206));
        private Color color;

        CellColor(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

}
