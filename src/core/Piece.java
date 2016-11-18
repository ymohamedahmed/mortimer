package core;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class Piece {
    private PieceType pieceType;
    private Position pos;
    private PieceColor color;
    private int numberOfMoves;
    private ArrayList<Move> movesList;

    public Piece(PieceType pieceType, Position pos, PieceColor color, int numberOfMoves) {
        this.pieceType = pieceType;
        this.pos = pos;
        this.color = color;
        this.numberOfMoves = numberOfMoves;
    }

    public static int getIndex(Piece piece) {
        int index = 0;
        if (piece.getColor() == PieceColor.WHITE) {
            index = 8 * (7 - piece.getPos().getRow()) + piece.getPos().getCol();
        } else {
            index = 8 * ((piece.getPos().getRow())) + piece.getPos().getCol();
        }
        return index;
    }
    public static int getPieceTableValue(Piece piece, ArrayList<Piece> pieceList) {
        int value = 0;
        int index = Piece.getIndex(piece);
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

    public static Image getImage(PieceType type, PieceColor color) {
        String piece = String.valueOf(type).toLowerCase() + (color == PieceColor.BLACK ? "B" : "W");
        return new Image(Piece.class.getResource("/images/" + piece + ".png").toExternalForm());
    }

    public static ArrayList<Piece> getInitialPieceList() {
        //Adding pieces in the initial setup
        ArrayList<Piece> init = new ArrayList<>();

        for (int column = 0; column <= 7; column++) {
            init.add(new Pawn(new Position(1, column), PieceColor.WHITE, 0));
            init.add(new Pawn(new Position(6, column), PieceColor.BLACK, 0));
        }
        init.add(new Rook(new Position(0, 0), PieceColor.WHITE, 0));
        init.add(new Rook(new Position(0, 7), PieceColor.WHITE, 0));
        init.add(new Rook(new Position(7, 0), PieceColor.BLACK, 0));
        init.add(new Rook(new Position(7, 7), PieceColor.BLACK, 0));

        init.add(new Knight(new Position(0, 1), PieceColor.WHITE, 0));
        init.add(new Knight(new Position(0, 6), PieceColor.WHITE, 0));
        init.add(new Knight(new Position(7, 1), PieceColor.BLACK, 0));
        init.add(new Knight(new Position(7, 6), PieceColor.BLACK, 0));

        init.add(new Bishop(new Position(0, 2), PieceColor.WHITE, 0));
        init.add(new Bishop(new Position(0, 5), PieceColor.WHITE, 0));
        init.add(new Bishop(new Position(7, 2), PieceColor.BLACK, 0));
        init.add(new Bishop(new Position(7, 5), PieceColor.BLACK, 0));

        init.add(new Queen(new Position(0, 3), PieceColor.WHITE, 0));
        init.add(new Queen(new Position(7, 3), PieceColor.BLACK, 0));

        init.add(new King(new Position(0, 4), PieceColor.WHITE, 0));
        init.add(new King(new Position(7, 4), PieceColor.BLACK, 0));
        return init;
    }

    public ArrayList<Move> getMovesList() {
        return movesList;
    }

    public void setMovesList(ArrayList<Move> moves) {
        this.movesList = moves;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public Position getPos() {
        return pos;
    }

    public void setPosition(Position position) {
        this.pos = position;
    }

    public PieceColor getColor() {
        return color;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public void setNumberOfMoves(int numberOfMoves) {
        this.numberOfMoves = numberOfMoves;
    }

}