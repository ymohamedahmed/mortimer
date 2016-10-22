package engine;

import java.util.ArrayList;
import java.util.Iterator;

public class Pawn extends Piece {

    public Pawn(Position position, PieceColor color, int numberOfMoves) {
        super(PieceType.PAWN, position, color, numberOfMoves);
        this.setMovesList(getInitMovesList());
    }

    public static boolean enPassant(ArrayList<Piece> pieceList, Move move, int colorFactor) {
        for (Piece piece : pieceList) {
            if (piece.getColor() != move.getPiece().getColor() && piece.getPieceType() == PieceType.PAWN
                    && piece.getNumberOfMoves() == 1) {
                if (move.getPosition().getRow() == piece.getPos().getRow() + colorFactor
                        && move.getPosition().getCol() == piece.getPos().getCol()) {
                    if (piece.getPos().getRow() == 3 || piece.getPos().getRow() == 4) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static int getPositionValue(int index) {
        int[] pawnTable = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50, 50, 50, 10, 10, 20, 30, 30, 20,
                10, 10, 5, 5, 10, 27, 27, 10, 5, 5, 0, 0, 0, 25, 25, 0, 0, 0, 5, -5, -10, 0, 0, -10, -5, 5, 5, 10, 10,
                -25, -25, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0};
        return pawnTable[index];
    }

    private ArrayList<Move> getInitMovesList() {
        ArrayList<Move> initMovesList = new ArrayList<Move>();
        int colorFactor = this.getColor().getColorFactor();
        Position doubleForward = new Position(this.getPos().getRow() + (2 * colorFactor), this.getPos().getCol());
        Position singleForward = new Position(this.getPos().getRow() + colorFactor, this.getPos().getCol());
        initMovesList.add(new Move(this, singleForward, false));
        initMovesList.add(new Move(this, doubleForward, false));
        return initMovesList;
    }

    public ArrayList<Move> getLegalMoves(ArrayList<Piece> pieceList) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        int colorFactor = this.getColor().getColorFactor();
        if (getNumberOfMoves() == 0 && Board.isSquareEmpty(pieceList,
                new Position(this.getPos().getRow() + colorFactor, this.getPos().getCol()))) {
            Position doubleForward = new Position(this.getPos().getRow() + (2 * colorFactor), this.getPos().getCol());
            possibleMoves.add(new Move(this, doubleForward, false));
        }
        if (getNumberOfMoves() >= 0) {
            Position singleForward = new Position(this.getPos().getRow() + colorFactor, this.getPos().getCol());
            possibleMoves.add(new Move(this, singleForward, false));
        }

        // Iterator has to be used to avoid concurrent modification exception
        // i.e. so that we can remove from the arraylist as we loop through it
        Iterator<Move> iter = possibleMoves.iterator();
        while (iter.hasNext()) {
            Move nextMove = iter.next();
            if (!Board.isSquareEmpty(pieceList, nextMove.getPosition())) {
                iter.remove();
            }
        }
        Position diagonal1 = new Position(this.getPos().getRow() + colorFactor, this.getPos().getCol() - 1);
        Position diagonal2 = new Position(this.getPos().getRow() + colorFactor, this.getPos().getCol() + 1);
        try {
            Piece pieceDiagonal1 = Board.getPiece(pieceList, diagonal1);
            Piece pieceDiagonal2 = Board.getPiece(pieceList, diagonal2);
            boolean enPassantDiagonal1 = enPassant(pieceList, new Move(this, diagonal1, false), colorFactor);
            boolean enPassantDiagonal2 = enPassant(pieceList, new Move(this, diagonal2, false), colorFactor);
            if ((!Board.isSquareEmpty(pieceList, diagonal1) && pieceDiagonal1.getColor() != getColor())
                    || enPassantDiagonal1) {
                Move move = new Move(this, diagonal1, false);
                move.setEnPassant(enPassantDiagonal1);
                possibleMoves.add(move);
            }
            if ((!Board.isSquareEmpty(pieceList, diagonal2) && pieceDiagonal2.getColor() != getColor())
                    || enPassantDiagonal2) {
                Move move = new Move(this, diagonal2, false);
                move.setEnPassant(enPassantDiagonal2);
                possibleMoves.add(move);
            }
        } catch (NullPointerException e) {

        }
        return possibleMoves;
    }

    public boolean pawnPromotion(Piece piece, ArrayList<Piece> thisList) {
        boolean promotion = false;
        if ((piece.getColor() == PieceColor.BLACK && piece.getPos().getRow() == 6
                && Board.isSquareEmpty(thisList, new Position(7, piece.getPos().getCol())))
                || (piece.getColor() == PieceColor.WHITE && piece.getPos().getRow() == 1
                && Board.isSquareEmpty(thisList, new Position(0, piece.getPos().getCol())))) {
            promotion = true;

        }
        return promotion;
    }
}