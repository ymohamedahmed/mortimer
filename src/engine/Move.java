package engine;

public class Move {
    private Piece piece;
    private Position finalPosition;
    private boolean castling;
    private boolean capture;
    private boolean enPassant;

    public Move(Piece piece, Position finalPosition, boolean castling) {
        this.piece = piece;
		this.finalPosition = finalPosition;
        this.castling = castling;
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