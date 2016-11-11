package core;

import java.util.ArrayList;

public class Board {

    public static ArrayList<Piece> updatePieceList(Move move, ArrayList<Piece> pieceList) {
        for (Piece piece : pieceList) {
            if (piece.getColor() == move.getPiece().getColor()
                    && piece.getPieceType() == move.getPiece().getPieceType()) {
                piece.setPosition(move.getPosition());
            }
        }
        return pieceList;
    }

    public static boolean isSquareEmpty(ArrayList<Piece> pieceList, Position position) {
        for (Piece piece : pieceList) {
            if (piece.getPos().getRow() == position.getRow() && piece.getPos().getCol() == position.getCol()) {
                return false;
            }
        }
        return true;
    }

    public static Piece getPiece(ArrayList<Piece> pieceList, Position pos) {
        for (Piece piece : pieceList) {
            if (piece.getPos().getRow() == pos.getRow() && piece.getPos().getCol() == pos.getCol()) {
                return piece;
            }
        }
        return null;

    }

    public static boolean offGrid(Position position) {
        return position.getRow() > 7 || position.getCol() > 7 || position.getRow() < 0 || position.getCol() < 0;
    }

    public static int noOfPieces(ArrayList<Piece> pieceList, PieceType type, PieceColor color) {
        int number = 0;
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == type && piece.getColor() == color) {
                number++;
            }
        }
        return number;
    }

    public static int noOfPiecesColor(ArrayList<Piece> pieceList, PieceColor color) {
        int answer = 0;
        for (Piece piece : pieceList) {
            if (color == piece.getColor()) {
                answer++;
            }
        }
        return answer;
    }

    public static int noOfMovesTotal(ArrayList<Piece> pieceList) {
        int total = 0;
        for (Piece piece : pieceList) {
            total += piece.getNumberOfMoves();
        }
        return total;
    }

    public static ArrayList<Piece> getPieceByColorAndType(ArrayList<Piece> pieceList, PieceColor color,
                                                          PieceType type) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (Piece piece : pieceList) {
            if (piece.getColor() == color && piece.getPieceType() == type) {
                pieces.add(piece);
            }
        }
        return pieces;
    }

    public static ArrayList<Piece> getPieceByType(ArrayList<Piece> pieceList, PieceType type) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == type) {
                pieces.add(piece);
            }
        }
        return pieces;
    }

    public static ArrayList<Piece> getPieceByColumnAndColorAndType(ArrayList<Piece> pieceList, int column,
                                                                   PieceColor color, PieceType type) {
        ArrayList<Piece> pieces = new ArrayList<>();
        if (column >= 0 && column <= 7) {
            for (Piece piece : pieceList) {
                if (piece.getPos().getCol() == column && piece.getColor() == color && piece.getPieceType() == type) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    public static PieceColor getColorOfSquare(Position pos) {
        PieceColor color = PieceColor.WHITE;
        int row = pos.getRow();
        int col = pos.getCol();
        if (row % 2 == 0 && col % 2 == 0) {
            color = PieceColor.WHITE;
        } else if (row % 2 == 1 && col % 2 == 1) {
            color = PieceColor.WHITE;
        } else {
            color = PieceColor.BLACK;
        }
        return color;
    }
}