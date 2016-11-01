package dme;

import engine.*;

import java.util.ArrayList;

public class Search {
    public Move rootNegamax(ArrayList<Piece> pieceList, PieceColor color) {
        int depth = 8;
        double maxScore = 0;
        Move bestMove = null;
        for (Move move : Board.getAllMovesColor(clonePieceList(pieceList), color)) {
            double score = negamax(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, pieceList, depth,
                    color.getColorFactor());
            System.out.println("Score  " + score);
            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private double negamax(double alpha, double beta, ArrayList<Piece> pieceList, int depth, int colorFactor) {
        if (depth == 0) {
            return new Evaluation().totalEvaluation(pieceList, PieceColor.getColorByFactor(colorFactor));
        }
        // TODO add sorting algorithm here
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Move move : Board.getAllMovesColor(pieceList, PieceColor.getColorByFactor(colorFactor))) {
            // Move piece
            ArrayList<Piece> pieceListTemp = clonePieceList(pieceList);
            Position oldPosition = move.getPiece().getPos();
            Piece pieceTemp = Board.getPiece(pieceListTemp, move.getPiece().getPos());
            if (move.isCapture()) {
                Piece capPiece = Board.getPiece(pieceListTemp, move.getPosition());
                pieceListTemp.remove(capPiece);
            }
            pieceTemp.setPosition(move.getPosition());
            pieceTemp.setNumberOfMoves(move.getPiece().getNumberOfMoves() + 1);
            //updateMoveList(pieceListTemp, false);
            double v = -negamax(-beta, -alpha, pieceListTemp, depth - 1, -colorFactor);
            // Undo Move
            pieceTemp.setPosition(oldPosition);
            pieceTemp.setNumberOfMoves(move.getPiece().getNumberOfMoves() - 1);
            bestValue = Math.max(bestValue, v);
            alpha = Math.max(alpha, v);
            if (alpha >= beta) {
                break;
            }
        }
        return bestValue;
    }

    private ArrayList<Piece> clonePieceList(ArrayList<Piece> pieceList) {
        ArrayList<Piece> clonedList = new ArrayList<>();
        for (Piece piece : pieceList) {
            PieceType type = piece.getPieceType();
            if (type == PieceType.PAWN) {
                clonedList.add(new Pawn(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
            } else if (type == PieceType.KNIGHT) {
                clonedList.add(new Knight(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
            } else if (type == PieceType.BISHOP) {
                clonedList.add(new Bishop(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
            } else if (type == PieceType.ROOK) {
                clonedList.add(new Rook(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
            } else if (type == PieceType.QUEEN) {
                clonedList.add(new Queen(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
            } else if (type == PieceType.KING) {
                clonedList.add(new King(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
            }
        }
        return clonedList;
    }
}