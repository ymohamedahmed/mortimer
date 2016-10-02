package engine;

import javafx.scene.control.Label;

import java.util.ArrayList;

public class Piece {
    PieceType pieceType;
    Position pos;
    Color color;
    int numberOfMoves;


    public Piece(PieceType pieceType, Position pos, Color color,
                 int numberOfMoves) {
        this.pieceType = pieceType;
        this.pos = pos;
        this.color = color;
        this.numberOfMoves = numberOfMoves;
    }

    public static int getPieceTableValue(Piece piece, ArrayList<Piece> pieceList) {
        int value = 0;
        int index = (7 - piece.getPos().getRow()) + piece.getPos().getCol();
        switch (piece.getPieceType()) {
            case PAWN:
                value = Pawn.getPositionValue(index);
                break;
            case KNIGHT:
                value = Knight.getPositionValue(index);
                break;
            case BISHOP:
                value = Bishop.getPositionValue(index);
                break;
            case ROOK:
                value = Rook.getPositionValue(index);
                break;
            case QUEEN:
                value = Queen.getPositionValue(index);
                break;
            case KING:
                value = King.getPositionValue(index, pieceList);
                break;


        }
        return value;
    }

    public static Label getImage(PieceType type, Color color) {
        String piece = "";
        if (color == Color.WHITE) {
            switch (type) {
                case PAWN:
                    piece = "\u2659";
                    break;
                case KNIGHT:
                    piece = "\u2658";
                    break;
                case BISHOP:
                    piece = "\u2657";
                    break;
                case ROOK:
                    piece = "\u2656";
                    break;
                case QUEEN:
                    piece = "\u2655";
                    break;
                case KING:
                    piece = "\u2654";
                    break;
            }
        } else if (color == Color.BLACK) {
            switch (type) {
                case PAWN:
                    piece = "\u265F";
                    break;
                case KNIGHT:
                    piece = "\u265E";
                    break;
                case BISHOP:
                    piece = "\u265D";
                    break;
                case ROOK:
                    piece = "\u265C";
                    break;
                case QUEEN:
                    piece = "\u265B";
                    break;
                case KING:
                    piece = "\u265A";
                    break;
            }

        }
        return new Label(piece);
    }

    public static ArrayList<Move> getLegalMove(Piece piece, ArrayList<Piece> pieceList) {
        ArrayList<Move> legalMoves = new ArrayList<Move>();
        switch (piece.getPieceType()) {
            case PAWN:
                legalMoves = Pawn.getLegalMoves(piece, pieceList);
                break;
            case KNIGHT:
                legalMoves = Knight.getLegalMoves(piece, pieceList);
                break;
            case BISHOP:
                legalMoves = Bishop.getLegalMoves(piece, pieceList);
                break;
            case ROOK:
                legalMoves = Rook.getLegalMoves(piece, pieceList);
                break;
            case QUEEN:
                legalMoves = Queen.getLegalMoves(piece, pieceList);
                break;
            case KING:
                legalMoves = King.getLegalMoves(piece, pieceList);
                break;
        }
        return legalMoves;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public Position getPos() {
        return pos;
    }

    public void setPosition(Position position) {
        this.pos = position;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public void setNumberOfMoves(int numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
    }
}
