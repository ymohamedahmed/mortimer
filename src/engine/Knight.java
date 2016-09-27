package engine;

import java.util.ArrayList;

public class Knight extends Piece {

	public Knight(Position position, Color color, int numberOfMoves) {
		super(PieceType.KNIGHT, position, color, numberOfMoves);
	}

	public static ArrayList<Move> getLegalMoves(Piece piece, ArrayList<Piece> pieceList) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		for (int row = piece.getPos().getRow() - 2; row <= piece.getPos()
				.getRow() + 2; row++) {
			for (int col = piece.getPos().getCol() - 2; col <= piece.getPos()
					.getCol() + 2; col++) {
				int rowDiff = row - piece.getPos().getRow();
				int colDiff = col - piece.getPos().getCol();
				
				if(Math.pow(rowDiff,2) + Math.pow(colDiff,2) == 5.0){
					possibleMoves.add(new Move(piece, new Position(row, col)));
				}
				
			}
		}
		return Move.removeIllegalMoves(pieceList, piece.getColor(),possibleMoves);
	}

}
