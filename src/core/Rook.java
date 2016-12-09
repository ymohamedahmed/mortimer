package core;

import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(Position pos, PieceColor color, int numberOfMoves) {
        super(PieceType.ROOK, pos, color, numberOfMoves);
    }

    public static int getPositionValue(int index) {
        int[] rookTable = new int[]{
                0, 0, 0, 0, 0, 0, 0, 0,
                50, 50, 50, 50, 50, 50, 50, 50,
                10, 10, 20, 30, 30, 20, 10, 10,
                5, 5, 10, 27, 27, 10, 5, 5,
                0, 0, 0, 25, 25, 0, 0, 0,
                5, -5, -10, 0, 0, -10, -5, 5,
                5, 10, 10, -25, -25, 10, 10, 5,
                0, 0, 0, 0, 0, 0, 0, 0
        };
        return rookTable[index];
    }

}