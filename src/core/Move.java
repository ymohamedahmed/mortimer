package core;

public class Move {
	private int oldPosition;
	private int finalPosition;
	private boolean castling;
	private boolean capture;
	private boolean enPassant;

	public Move(int oldPosition, int finalPosition, boolean castling, boolean capture) {
		this.oldPosition = oldPosition;
		this.finalPosition = finalPosition;
		this.castling = castling;
	}

	public int getPosition() {
		return finalPosition;
	}

	public void setPosition(int position) {
		this.finalPosition = position;
	}

	public boolean isCastling() {
		return castling;
	}

	public void setCastling(boolean castling) {
		this.castling = castling;
	}

	public boolean isCapture() {
		return capture;
	}

	public void setCapture(boolean capture) {
		this.capture = capture;
	}

	public boolean isEnPassant() {
		return enPassant;
	}

	public void setEnPassant(boolean enPassant) {
		this.enPassant = enPassant;
	}

}