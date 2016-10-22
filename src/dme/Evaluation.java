package dme;

import engine.Board;
import engine.Piece;
import engine.PieceColor;
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
        int kingW = Board.noOfPieces(pieceList, PieceType.KING, PieceColor.WHITE);
        int kingB = Board.noOfPieces(pieceList, PieceType.KING, PieceColor.BLACK);
        int queenW = Board.noOfPieces(pieceList, PieceType.QUEEN, PieceColor.WHITE);
        int queenB = Board.noOfPieces(pieceList, PieceType.QUEEN, PieceColor.BLACK);
        int rookW = Board.noOfPieces(pieceList, PieceType.ROOK, PieceColor.WHITE);
        int rookB = Board.noOfPieces(pieceList, PieceType.ROOK, PieceColor.BLACK);
        int knightW = Board.noOfPieces(pieceList, PieceType.KNIGHT, PieceColor.WHITE);
        int knightB = Board.noOfPieces(pieceList, PieceType.KNIGHT, PieceColor.BLACK);
        int bishopW = Board.noOfPieces(pieceList, PieceType.BISHOP, PieceColor.WHITE);
        int bishopB = Board.noOfPieces(pieceList, PieceType.BISHOP, PieceColor.BLACK);
        int pawnW = Board.noOfPieces(pieceList, PieceType.PAWN, PieceColor.WHITE);
        int pawnB = Board.noOfPieces(pieceList, PieceType.PAWN, PieceColor.BLACK);

        double eval = kingConst * (kingW - kingB) + queenConst * (queenW - queenB) + rookConst * (rookW - rookB)
                + knightConst * (knightW - knightB) + bishopConst * (bishopW - bishopB) + pawnConst * (pawnW - pawnB);
        return eval;
    }

    private static double evalMob(ArrayList<Piece> pieceList) {
        double mobilityFactor = 0.1;
        int whiteMoves = 0;
        int blackMoves = 0;
        for (Piece piece : pieceList) {
            int noOfMoves = piece.getMovesList().size();
            if (piece.getColor() == PieceColor.BLACK) {
                blackMoves += noOfMoves;
            } else if (piece.getColor() == PieceColor.WHITE) {
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
            if (piece.getColor() == PieceColor.BLACK) {
                blackScore += score;
            } else if (piece.getColor() == PieceColor.WHITE) {
                whiteScore += score;
            }
        }
        return positionFactor * (whiteScore - blackScore);

    }

    public static double totalEvaluation(ArrayList<Piece> pieceList, PieceColor pieceColor) {
        return (pieceColor.getColorFactor() * (evalMat(pieceList) + evalMob(pieceList))) + evalPos(pieceList);
    }


}