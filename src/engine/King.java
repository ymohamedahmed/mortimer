package engine;

import java.util.ArrayList;

public class King extends Piece {
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

    public ArrayList<Move> getLegalMoves(ArrayList<Piece> pieceList) {
        ArrayList<Move> possibleMoves = new ArrayList<Move>();
        for (int row = this.getPos().getRow() - 1; row <= this.getPos().getRow() + 1; row++) {
            for (int col = this.getPos().getCol() - 1; col <= this.getPos().getCol() + 1; col++) {
                Position pos = new Position(row, col);
                try {
                    Piece piece = Board.getPiece(pieceList, pos);
                    if (piece.getColor() != getColor()) {
                        possibleMoves.add(new Move(this, pos, false));
                    }
                } catch (NullPointerException e) {
                    possibleMoves.add(new Move(this, pos, false));
                }

            }
        }
        // Checking to see if castling is legal
        for (Piece piece : pieceList) {
            if (piece.getColor() == this.getColor() && piece.getPieceType() == PieceType.ROOK) {
                int kingColumn = 0;
                if (piece.getPos().getCol() == 0) {
                    kingColumn = 2;
                } else if (piece.getPos().getCol() == 7) {
                    kingColumn = 6;
                }
                if (castling(piece, new Position(this.getPos().getRow(), kingColumn), pieceList)) {
                    possibleMoves.add(new Move(this, new Position(this.getPos().getRow(), kingColumn), true));
                }
            }
        }

        return possibleMoves;
    }

    public boolean check(ArrayList<Piece> pieceList, Position kingPos) {
        for (Piece piece : pieceList) {
            if (getColor() != piece.getColor()) {
                ArrayList<Move> possibleMoves = piece.getMovesList();
                for (Move move : possibleMoves) {
                    if (kingPos.getRow() == move.getPosition().getRow()
                            && kingPos.getCol() == move.getPosition().getCol()) {
                        // TODO add check dialog
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkmate(ArrayList<Piece> pieceList) {
        ArrayList<Move> kingMoves = getMovesList();
        ArrayList<Move> enemyMoves = new ArrayList<Move>();
        ArrayList<Move> friendlyMoves = new ArrayList<Move>();
        // Separating moves into friendly and enemy moves
        for (Piece piece : pieceList) {
            for (Move move : piece.getMovesList()) {
                if (this.getColor() != piece.getColor()) {
                    enemyMoves.add(move);
                } else if (this.getColor() == piece.getColor()) {
                    friendlyMoves.add(move);
                }
            }
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
            if (!check(pieceListTemp, this.getPos())) {
                return false;
            }
        }
        return true;

    }

    private boolean castling(Piece rook, Position kingNewPos, ArrayList<Piece> pieceList) {
        //Work out which type of castling is being used
        CastleType castleType;
        if (kingNewPos.getCol() == 6) {
            castleType = CastleType.KINGSIDE;
        } else if (kingNewPos.getCol() == 2) {
            castleType = CastleType.QUEENSIDE;
        } else {
            return false;
        }
        //Only allowed if each piece is yet to move
        if (this.getNumberOfMoves() != 0 || rook.getNumberOfMoves() != 0) {
            return false;
        }
        // Checking that none of the squares the king moves through cause check
        if (check(pieceList, this.getPos())
                || check(pieceList,
                new Position(this.getPos().getRow(), this.getPos().getCol() + castleType.getCastleFactor()))
                || check(pieceList, kingNewPos)) {
            return false;
        }
        if (!Board.isSquareEmpty(pieceList,
                new Position(this.getPos().getRow(), this.getPos().getCol() + castleType.getCastleFactor()))
                || !Board.isSquareEmpty(pieceList, kingNewPos)) {
            return false;
        }
        return !(castleType == CastleType.QUEENSIDE && !Board.isSquareEmpty(pieceList,
                new Position(kingNewPos.getRow(), kingNewPos.getCol() + castleType.getCastleFactor())));
    }
}