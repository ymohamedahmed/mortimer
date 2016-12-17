package core;

public class Move {
	private int oldPosition;
	private int finalPosition;
	private int pieceType;
	private byte castling;
	private boolean promotion;
	private boolean enPassant;

	public Move(int pieceType, int oldPosition, int finalPosition) {
		this.oldPosition = oldPosition;
		this.finalPosition = finalPosition;
		this.pieceType = pieceType;
	}

	public int getFinalPos() {
		return finalPosition;
	}

	public int getPieceType() {
		return pieceType;
	}

	public int getOldPos() {
		return oldPosition;
	}

	public byte getCastlingFlag() {
		return castling;
	}

	public boolean isPromotion() {
		return promotion;
	}

	public boolean isEnPassant() {
		return enPassant;
	}

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