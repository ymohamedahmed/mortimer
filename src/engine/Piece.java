package engine;

import javafx.scene.image.Image;

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

    public static Image getImage(PieceType type, Color color) {
        String piece = String.valueOf(type).toLowerCase() + (color == Color.BLACK ? "B" : "W");
        return new Image(Piece.class.getResource("/images/" + piece + ".png").toExternalForm());
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

    public static ArrayList<Piece> getInitialPieceList() {
        ArrayList<Piece> init = new ArrayList<>();

        for (int column = 0; column <= 7; column++) {
            init.add(new Pawn(new Position(1, column), Color.WHITE, 0));
            init.add(new Pawn(new Position(6, column), Color.BLACK, 0));
        }
        init.add(new Rook(new Position(0, 0), Color.WHITE, 0));
        init.add(new Rook(new Position(0, 7), Color.WHITE, 0));
        init.add(new Rook(new Position(7, 0), Color.BLACK, 0));
        init.add(new Rook(new Position(7, 7), Color.BLACK, 0));

        init.add(new Knight(new Position(0, 1), Color.WHITE, 0));
        init.add(new Knight(new Position(0, 6), Color.WHITE, 0));
        init.add(new Knight(new Position(7, 1), Color.BLACK, 0));
        init.add(new Knight(new Position(7, 6), Color.BLACK, 0));

        init.add(new Bishop(new Position(0, 2), Color.WHITE, 0));
        init.add(new Bishop(new Position(0, 5), Color.WHITE, 0));
        init.add(new Bishop(new Position(7, 2), Color.BLACK, 0));
        init.add(new Bishop(new Position(7, 5), Color.BLACK, 0));

        init.add(new Queen(new Position(0, 3), Color.WHITE, 0));
        init.add(new Queen(new Position(7, 3), Color.BLACK, 0));

        init.add(new King(new Position(0, 4), Color.WHITE, 0));
        init.add(new King(new Position(7, 4), Color.BLACK, 0));

        return init;
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
