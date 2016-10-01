package engine;

import java.util.ArrayList;

public class King extends Piece {
    public King(Position pos, Color color, int numberOfMoves) {
        super(PieceType.KING, pos, color, numberOfMoves);
    }

    public static ArrayList<Move> getLegalMoves(Piece king, ArrayList<Piece> pieceList) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        for (int row = king.getPos().getRow() - 1; row <= king.getPos().getRow() + 1; row++) {
            for (int col = king.getPos().getCol() - 1; col <= king.getPos().getCol() + 1; col++) {
                possibleMoves.add(new Move(king, new Position(row, col)));
            }
        }
        // Checking to see if castling is legal
        for (Piece piece : pieceList) {
            if (piece.getColor() == king.getColor() && piece.getPieceType() == PieceType.ROOK) {
                int kingColumn = 0;
                if (piece.getPos().getCol() == 0) {
                    kingColumn = 2;
                } else if (piece.getPos().getCol() == 7) {
                    kingColumn = 6;
                }
                // TODO add remaining castling code
            }
        }
        return Move.removeIllegalMoves(pieceList, king.getColor(), possibleMoves);
    }

    public static boolean check(Piece king, ArrayList<Piece> pieceList, Position kingPos) {
        for (Piece piece : pieceList) {
            if (king.getColor() != piece.getColor()) {
                ArrayList<Move> possibleMoves = getLegalMove(piece, pieceList);
                for (Move move : possibleMoves) {
                    if (king.getPos() == move.getPosition()) {
                        // TODO add check dialog
                        if (checkmate(king, pieceList)) {
                            // TODO add checkmate dialog
                            // Game over
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkmate(Piece king, ArrayList<Piece> pieceList) {
        ArrayList<Move> kingMoves = getLegalMoves(king, pieceList);
        ArrayList<Move> enemyMoves = new ArrayList<Move>();
        ArrayList<Move> friendlyMoves = new ArrayList<Move>();
        // Separating moves into friendly and enemy moves
        for (Piece piece : pieceList) {
            for (Move move : Piece.getLegalMove(piece, pieceList)) {
                if (king.getColor() != piece.getColor()) {
                    enemyMoves.add(move);
                } else if (king.getColor() == piece.getColor()) {
                    friendlyMoves.add(move);
                }
            }
        }
        if (!check(king, pieceList, king.getPos())) {
            return false;
        }
        // If king has a move which doesn't result in check
        for (Move move : kingMoves) {
            if (!enemyMoves.contains(move)) {
                return false;
            }
        }
        for (Move move : friendlyMoves) {
            ArrayList<Piece> pieceListTemp = pieceList;
            for (Piece piece : pieceListTemp) {
                if (piece == move.getPiece()) {
                    piece.setPosition(move.getPosition());
                }
            }
            if (!check(king, pieceListTemp, king.getPos())) {
                return false;
            }
        }
        return true;

    }

    public static int getPositionValue(int index, ArrayList<Piece> pieceList) {
        int value = 0;
        boolean endGame = false;
        int[] kingTableMidgame = new int[]{
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -20, -30, -30, -40, -40, -30, -30, -20,
                -10, -20, -20, -20, -20, -20, -20, -10,
                20, 20, 0, 0, 0, 0, 20, 20,
                20, 30, 10, 0, 0, 10, 30, 20
        };
        int[] kingTableEndgame = new int[]{
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -30, -40, -40, -50, -50, -40, -40, -30,
                -20, -30, -30, -40, -40, -30, -30, -20,
                -10, -20, -20, -20, -20, -20, -20, -10,
                20, 20, 0, 0, 0, 0, 20, 20,
                20, 30, 10, 0, 0, 10, 30, 20
        };
        boolean noBlackQueen = Board.noOfPieces(pieceList, PieceType.QUEEN, Color.BLACK) == 0;
        boolean noWhiteQueen = Board.noOfPieces(pieceList, PieceType.QUEEN, Color.WHITE) == 0;
        //Both sides have no queens
        if (noBlackQueen && noWhiteQueen) {
            endGame = true;
        }
        //Each side only has one queen and one king
        if ((!noBlackQueen && Board.noOfPiecesColor(pieceList, Color.BLACK) == 2) && (!noWhiteQueen && Board.noOfPiecesColor(pieceList, Color.WHITE) == 2)) {
            endGame = true;
        }
        //Each side only has one queen and one minor piece (e.g. knight or bishop)
        int blackMinorPieces = Board.noOfPieces(pieceList, PieceType.KNIGHT, Color.BLACK) + Board.noOfPieces(pieceList, PieceType.BISHOP, Color.BLACK);
        int whiteMinorPieces = Board.noOfPieces(pieceList, PieceType.KNIGHT, Color.WHITE) + Board.noOfPieces(pieceList, PieceType.BISHOP, Color.WHITE);

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

    public boolean castling(Piece king, Piece rook, Position kingNewPos, Position kingOldPos,
                            ArrayList<Piece> pieceList) {
        CastleType castleType;
        if (kingNewPos.getCol() == 6) {
            castleType = CastleType.KINGSIDE;
        } else if (kingNewPos.getCol() == 2) {
            castleType = CastleType.QUEENSIDE;
        } else {
            return false;
        }
        if (king.getNumberOfMoves() != 0 || rook.getNumberOfMoves() != 0) {
            return false;
        }
        // Checking that none of the square the king moves through cause check
        if (check(king, pieceList, king.getPos())
                || check(king, pieceList,
                new Position(king.getPos().getRow(), king.getPos().getCol() + castleType.getCastleFactor()))
                || check(king, pieceList, kingNewPos)) {
            return false;
        }
        if (!Board.isSquareEmpty(pieceList,
                new Position(king.getPos().getRow(), king.getPos().getCol() + castleType.getCastleFactor()))
                || !Board.isSquareEmpty(pieceList, kingNewPos)) {
            return false;
        }
        return !(castleType == CastleType.QUEENSIDE && !Board.isSquareEmpty(pieceList,
                new Position(kingNewPos.getRow(), kingNewPos.getCol() + castleType.getCastleFactor())));
    }
}
