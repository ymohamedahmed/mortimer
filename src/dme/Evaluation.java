package dme;

import java.util.ArrayList;

import core.Board;
import core.Piece;
import core.PieceColor;
import core.PieceType;

public class Evaluation {
    private double evalMat(ArrayList<Piece> pieceList) {
        double queenConst = 900;
        double rookConst = 500;
        double knightConst = 330;
        double bishopConst = 320;
        double pawnConst = 100;
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
        double eval = queenConst * (queenW - queenB) + rookConst * (rookW - rookB)
                + knightConst * (knightW - knightB) + bishopConst * (bishopW - bishopB) + pawnConst * (pawnW - pawnB);
        return eval;
    }

    private double evalMob(ArrayList<Piece> pieceList) {
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
    private double developedScores(ArrayList<Piece> pieceList){
    	double score = 0;
        double queenConst = 1000;
        double rookConst = 600;
        double knightConst = 350;
        double bishopConst = 400;
        double pawnConst = 70;
        for(Piece piece : pieceList){
        	if(piece.getNumberOfMoves() != 0){
        		switch(piece.getPieceType()){
        			case QUEEN:
        				score += queenConst;
        				break;
        			case ROOK:
        				score += rookConst;
        				break;
        			case KNIGHT:
        				score += knightConst;
        				break;
        			case BISHOP:
        				score += bishopConst;
        				break;
        			case PAWN:
        				score += pawnConst;
        				break;
        			default:
        				score += 0;
        				break;
        		}
        	}
        }
        return score;
    }
    private double advancedPawns(ArrayList<Piece> pieceList){
    	double score = 0;
    	for(Piece piece : pieceList){    		
    		if(piece.getPieceType() == PieceType.PAWN){
    			
    		}
    	}
    	return score;
    }
    private double bishopPair(ArrayList<Piece> pieceList, PieceColor color){
    	double bishopPairBonus = 50;
    	int noOfBishops = Board.noOfPieces(pieceList, PieceType.BISHOP, color);
    	return (noOfBishops == 2) ? bishopPairBonus : 0;
    }
    private double knightOnEdge(ArrayList<Piece> pieceList){
    	double knightOnEdgePenalty = -50;
    	double score = 0;
    	for(Piece piece : pieceList){
    		if(piece.getPieceType() == PieceType.KNIGHT){
    			if(isKnightOnEdge(piece)){
    				score += knightOnEdgePenalty;
    			}
    		}
    	}
    	return score;
    }
    
    private boolean isKnightOnEdge(Piece piece){
    	return (piece.getPos().getCol() == 0 || piece.getPos().getCol() == 7);
    }
    private double evalPos(ArrayList<Piece> pieceList) {
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

    public double totalEvaluation(ArrayList<Piece> pieceList, PieceColor pieceColor) {
        return (pieceColor.getColorFactor() * (evalMat(pieceList) + evalMob(pieceList))) + evalPos(pieceList);
        //return (pieceColor.getColorFactor() * (evalMat(pieceList) + evalMob(pieceList)));
    }


}
