package engine;

import java.util.ArrayList;

public class Board {
    public static boolean isSquareEmpty(ArrayList<Piece> pieceList,
                                        Position position) {
        for (Piece piece : pieceList) {
            if (piece.getPos().getRow() == position.getRow()
                    && piece.getPos().getCol() == position.getCol()) {
                return false;
            }
        }
        return true;
    }

    public static Piece getPiece(ArrayList<Piece> pieceList, Position pos) {
        for (Piece piece : pieceList) {
            if (piece.getPos() == pos) {
                return piece;
            }
        }
        return null;

    }

    public static boolean offGrid(Position position) {
        return position.getRow() > 7 || position.getCol() > 7
                || position.getRow() < 0 || position.getCol() < 0;
    }

    public static int noOfPieces(ArrayList<Piece> pieceList, PieceType type, Color color) {
        int number = 0;
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == type && piece.getColor() == color) {
                number++;
            }
        }
        return number;
    }

    public static int noOfPiecesColor(ArrayList<Piece> pieceList, Color color) {
        int answer = 0;
        for (Piece piece : pieceList) {
            if (color == piece.getColor()) {
                answer++;
            }
        }
        return answer;
    }
}
