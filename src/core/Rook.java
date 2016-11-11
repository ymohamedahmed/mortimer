package core;

import java.util.ArrayList;

public class Rook extends Piece {

    public Rook(Position pos, PieceColor color, int numberOfMoves) {
        super(PieceType.ROOK, pos, color, numberOfMoves);
        setMovesList(new ArrayList<Move>());
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

    public ArrayList<Move> getLegalMoves(ArrayList<Piece> pieceList) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        for (int rowMove = -1; rowMove <= 1; rowMove++) {
            for (int colMove = -1; colMove <= 1; colMove++) {
                boolean emptyLine = true;
                boolean offBoard = false;
                int row = getPos().getRow();
                int col = getPos().getCol();
                if (Math.pow(rowMove, 2) + Math.pow(colMove, 2) == 1.0) {
                    while (emptyLine && !offBoard) {
                        row += rowMove;
                        col += colMove;
                        Position pos = new Position(row, col);
                        if (Board.offGrid(pos)) {
                            offBoard = true;
                        } else if (!Board.isSquareEmpty(pieceList, pos)) {
                            if (getColor() != Board.getPiece(pieceList, pos).getColor()) {
                                possibleMoves.add(new Move(this, pos, false));
                            }
                            emptyLine = false;
                        } else {
                            possibleMoves.add(new Move(this, pos, false));
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

}