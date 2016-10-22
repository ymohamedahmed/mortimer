package engine;

import java.util.ArrayList;

public class Bishop extends Piece {

    public Bishop(Position pos, PieceColor color, int numberOfMoves) {
        super(PieceType.BISHOP, pos, color, numberOfMoves);
        this.setMovesList(new ArrayList<Move>());
    }

    public static int getPositionValue(int index) {
        int[] bishopTable = new int[]{
                -20, -10, -10, -10, -10, -10, -10, -20,
                -10, 0, 0, 0, 0, 0, 0, -10,
                -10, 0, 5, 10, 10, 5, 0, -10,
                -10, 5, 5, 10, 10, 5, 5, -10,
                -10, 0, 10, 10, 10, 10, 0, -10,
                -10, 10, 10, 10, 10, 10, 10, -10,
                -10, 5, 0, 0, 0, 0, 5, -10,
                -20, -10, -40, -10, -10, -40, -10, -20
        };
        return bishopTable[index];
    }

    public ArrayList<Move> getLegalMoves(ArrayList<Piece> pieceList) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        for (int rowMove = -1; rowMove <= 1; rowMove += 2) {
            for (int colMove = -1; colMove <= 1; colMove += 2) {
                boolean emptyDiagonal = true;
                boolean offBoard = false;
                int row = getPos().getRow();
                int col = getPos().getCol();
                while (emptyDiagonal && !offBoard) {
                    row += rowMove;
                    col += colMove;
                    Position pos = new Position(row, col);
                    if (Board.offGrid(pos)) {
                        offBoard = true;
                    } else {
                        if (!Board.isSquareEmpty(pieceList, pos)) {
                            if (getColor() != Board.getPiece(pieceList, pos).getColor()) {
                                possibleMoves.add(new Move(this, pos, false));
                            }
                            emptyDiagonal = false;
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