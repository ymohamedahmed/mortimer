package engine;

import java.util.ArrayList;

public class Move {
	Piece piece;
	Position finalPosition;

	public Move(Piece piece, Position finalPosition) {
		this.piece = piece;
		this.finalPosition = finalPosition;
	}

	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece) {
		this.piece = piece;
	}
	public Position getPosition() {
		return finalPosition;
	}

	public void setPosition(Position position) {
		this.finalPosition = position;
	}
	
	//Todo FINISH THIS
	public static ArrayList<Move>  removeIllegalMoves(ArrayList<Piece> pieceList,
			Color color, ArrayList<Move> possibleMoves) {
		return null;
	}
	private void removeCheckMoves(){
		
	}
}
