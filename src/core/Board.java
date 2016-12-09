package core;

import java.util.ArrayList;
import java.util.Random;

public class Board {
    private static int[][] table = new int[64][12];
    private static boolean initialised = false;

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

    // Used as a part of Zobrist Hashing
    private static int getPieceIndex(PieceType type, PieceColor color) {
        int result = 0;
        switch (type) {
            case PAWN:
                result = (color == PieceColor.WHITE) ? 0 : 6;
                break;
            case ROOK:
                result = (color == PieceColor.WHITE) ? 1 : 7;
                break;
            case KNIGHT:
                result = (color == PieceColor.WHITE) ? 2 : 8;
                break;
            case BISHOP:
                result = (color == PieceColor.WHITE) ? 3 : 9;
                break;
            case QUEEN:
                result = (color == PieceColor.WHITE) ? 4 : 10;
                break;
            case KING:
                result = (color == PieceColor.WHITE) ? 5 : 11;
                break;
        }
        return result;
    }

    private static void initialiseZobrist() {
        for (int x = 0; x <= 63; x++) {
            for (int y = 0; y <= 11; y++) {
                table[x][y] = new Random().nextInt((int) Math.pow(2, 64) - 1);
            }
        }
        initialised = true;
    }

    public static int hash(ArrayList<Piece> pieceList) {
        int hash = 0;
        if (!initialised) {
            Board.initialiseZobrist();
        }
        for (int index = 0; index <= 63; index++) {
            Position position = Piece.getPosition(index);
            if (!Board.isSquareEmpty(pieceList, position)) {
                Piece piece = Board.getPiece(pieceList, position);
                int j = Board.getPieceIndex(piece.getPieceType(), piece.getColor());
                hash = hash ^ table[index][j];
            }
        }
        return hash;
    }
}