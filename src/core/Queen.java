package core;

import java.util.ArrayList;

public class Queen extends Piece {

    public Queen(Position pos, PieceColor color, int numberOfMoves) {
        super(PieceType.QUEEN, pos, color, numberOfMoves);
        this.setMovesList(new ArrayList<Move>());
    }

    public static int getPositionValue(int index) {
        int[] queenTable = new int[]{-20, -10, -10, -5, -5, -10, -10, -20, -10, 0, 0, 0, 0, 0, 0, -10, -10, 0, 5, 5,
                5, 5, 0, -10, -5, 0, 5, 5, 5, 5, 0, -5, 0, 0, 5, 5, 5, 5, 0, -5, -10, 5, 5, 5, 5, 5, 0, -10, -10, 0, 5,
                0, 0, 0, 0, -10, -20, -10, -10, -5, -5, -10, -10, -20};
        return queenTable[index];
    }
}