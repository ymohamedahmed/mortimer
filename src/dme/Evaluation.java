package dme;

import engine.Board;
import engine.Color;
import engine.Piece;
import engine.PieceType;

import java.util.ArrayList;

public class Evaluation {
    private static double evalMat(ArrayList<Piece> pieceList) {
        int kingConst = 200;
        int queenConst = 9;
        int rookConst = 5;
        int knightConst = 3;
        int bishopConst = 3;
        int pawnConst = 1;
        int kingW = Board.noOfPieces(pieceList, PieceType.KING, Color.WHITE);
        int kingB = Board.noOfPieces(pieceList, PieceType.KING, Color.BLACK);
        int queenW = Board.noOfPieces(pieceList, PieceType.QUEEN, Color.WHITE);
        int queenB = Board.noOfPieces(pieceList, PieceType.QUEEN, Color.BLACK);
        int rookW = Board.noOfPieces(pieceList, PieceType.ROOK, Color.WHITE);
        int rookB = Board.noOfPieces(pieceList, PieceType.ROOK, Color.BLACK);
        int knightW = Board.noOfPieces(pieceList, PieceType.KNIGHT, Color.WHITE);
        int knightB = Board.noOfPieces(pieceList, PieceType.KNIGHT, Color.BLACK);
        int bishopW = Board.noOfPieces(pieceList, PieceType.BISHOP, Color.WHITE);
        int bishopB = Board.noOfPieces(pieceList, PieceType.BISHOP, Color.BLACK);
        int pawnW = Board.noOfPieces(pieceList, PieceType.PAWN, Color.WHITE);
        int pawnB = Board.noOfPieces(pieceList, PieceType.PAWN, Color.BLACK);

        double eval = kingConst * (kingW - kingB) + queenConst * (queenW - queenB) + rookConst * (rookW - rookB)
                + knightConst * (knightW - knightB) + bishopConst * (bishopW - bishopB) + pawnConst * (pawnW - pawnB);
        return eval;
    }

    private static double evalMob(ArrayList<Piece> pieceList) {
        double mobilityFactor = 0.1;
        int whiteMoves = 0;
        int blackMoves = 0;
        for (Piece piece : pieceList) {
            int noOfMoves = Piece.getLegalMove(piece, pieceList).size();
            if (piece.getColor() == Color.BLACK) {
                blackMoves += noOfMoves;
            } else if (piece.getColor() == Color.WHITE) {
                whiteMoves += noOfMoves;
            }
        }
        return mobilityFactor * (whiteMoves - blackMoves);
    }

    private static double evalPos(ArrayList<Piece> pieceList) {
        double positionFactor = 50;
        int whiteScore = 0;
        int blackScore = 0;
        for (Piece piece : pieceList) {
            int score = Piece.getPieceTableValue(piece, pieceList);
            if (piece.getColor() == Color.BLACK) {
                blackScore += score;
            } else if (piece.getColor() == Color.WHITE) {
                whiteScore += score;
            }
        }
        return positionFactor * (whiteScore - blackScore);

    }

    public static double totalEvaluation(ArrayList<Piece> pieceList, Color color) {
        return (color.getColorFactor() * (evalMat(pieceList) + evalMob(pieceList))) + evalPos(pieceList);
    }


}
