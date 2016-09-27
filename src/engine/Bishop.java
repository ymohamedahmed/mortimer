package engine;

import java.util.ArrayList;

public class Bishop extends Piece {

	public Bishop(Position pos, Color color, int numberOfMoves) {
		super(PieceType.BISHOP, pos, color, numberOfMoves);
	}

	public static ArrayList<Move> getLegalMoves(Piece piece, ArrayList<Piece> pieceList) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		for(int rowMove = -1; rowMove <= 1; rowMove += 2){
			for(int colMove = -1; colMove <= 1; colMove +=2){
				boolean emptyDiagonal = true;
				boolean offBoard = false;
				int row = piece.getPos().getRow();
				int col = piece.getPos().getCol();
				while(emptyDiagonal && !offBoard){
					row += rowMove;
					col += colMove;
					Position pos = new Position(row,col);
					if(Board.offGrid(pos)){
						offBoard = true;
					}
					else{
						if(!Board.isSquareEmpty(pieceList, pos)){
							possibleMoves.add(new Move(piece, pos));
							emptyDiagonal = false;	
						}else{
							possibleMoves.add(new Move(piece, pos));
						}		
					}
					
				}
			}
		}
		return Move.removeIllegalMoves(pieceList, piece.getColor(), possibleMoves);
	}

}
