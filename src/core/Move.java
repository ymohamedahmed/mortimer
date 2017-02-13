package core;

public class Move {
	private int oldPosition;
	private int finalPosition;
	private int pieceType;
	private byte castling;
	private boolean promotion;
	private boolean enPassant;

	// Move class used to store the crucial information about a particular move
	public Move(int pieceType, int oldPosition, int finalPosition) {
		this.oldPosition = oldPosition;
		this.finalPosition = finalPosition;
		this.pieceType = pieceType;
	}

	// Returns the index of the square where the piece will move to
	public int getFinalPos() {
		return finalPosition;
	}

	// Returns the id number of the piece making the move
	public int getPieceType() {
		return pieceType;
	}

	// Returns the index where the piece came from
	public int getOldPos() {
		return oldPosition;
	}

	// Returns the castling flag of the move
	// So that it can be judged whether or not the move is a castling move
	// Also what type of castling i.e. white queenside, black kingside etc.
	public byte getCastlingFlag() {
		return castling;
	}

	// Returns boolean to show whether or not move is a pawn promotion
	public boolean isPromotion() {
		return promotion;
	}

	// Returns boolean to show whether or not it is a pawn en passant attack
	public boolean isEnPassant() {
		return enPassant;
	}

	// Setters for castling, promotion and en passant
	public void setCastling(byte castling) {
		this.castling = castling;
	}

	public void setPromotion(boolean promotion) {
		this.promotion = promotion;
	}

	public void setEnPassant(boolean enPassant) {
		this.enPassant = enPassant;
	}

}