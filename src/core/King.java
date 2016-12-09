package core;

import java.util.ArrayList;

public class King extends Piece {
    private int noOfCastleMoves;
    public King(Position pos, PieceColor color, int numberOfMoves) {
        super(PieceType.KING, pos, color, numberOfMoves);
        this.setMovesList(new ArrayList<Move>());
    }

    public static int getPositionValue(int index, ArrayList<Piece> pieceList) {
        int value = 0;
        boolean endGame = false;
        int[] kingTableMidgame = new int[]{-30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40,
                -30, -30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30, -20, -30, -30, -40,
                -40, -30, -30, -20, -10, -20, -20, -20, -20, -20, -20, -10, 20, 20, 0, 0, 0, 0, 20, 20, 20, 30, 10, 0,
                0, 10, 30, 20};
        int[] kingTableEndgame = new int[]{-30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40,
                -30, -30, -40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30, -20, -30, -30, -40,
                -40, -30, -30, -20, -10, -20, -20, -20, -20, -20, -20, -10, 20, 20, 0, 0, 0, 0, 20, 20, 20, 30, 10, 0,
                0, 10, 30, 20};
        boolean noBlackQueen = Board.noOfPieces(pieceList, PieceType.QUEEN, PieceColor.BLACK) == 0;
        boolean noWhiteQueen = Board.noOfPieces(pieceList, PieceType.QUEEN, PieceColor.WHITE) == 0;
        // Both sides have no queens
        if (noBlackQueen && noWhiteQueen) {
            endGame = true;
        }
        // Each side only has one queen and one king
        if ((!noBlackQueen && Board.noOfPiecesColor(pieceList, PieceColor.BLACK) == 2)
                && (!noWhiteQueen && Board.noOfPiecesColor(pieceList, PieceColor.WHITE) == 2)) {
            endGame = true;
        }
        // Each side only has one queen and one minor piece (e.g. knight or
        // bishop)
        int blackMinorPieces = Board.noOfPieces(pieceList, PieceType.KNIGHT, PieceColor.BLACK)
                + Board.noOfPieces(pieceList, PieceType.BISHOP, PieceColor.BLACK);
        int whiteMinorPieces = Board.noOfPieces(pieceList, PieceType.KNIGHT, PieceColor.WHITE)
                + Board.noOfPieces(pieceList, PieceType.BISHOP, PieceColor.WHITE);

        if ((!noBlackQueen && blackMinorPieces <= 1) && (!noWhiteQueen && whiteMinorPieces <= 1)) {
            endGame = true;
        }
        if (endGame) {
            value = kingTableEndgame[index];
        } else {
            value = kingTableMidgame[index];
        }
        return value;
    }

    public int getNoOfCastleMoves() {
        return noOfCastleMoves;
    }

    public void setNoOfCastleMoves(int noOfCastleMoves) {
        this.noOfCastleMoves = noOfCastleMoves;
    }

   

   
}