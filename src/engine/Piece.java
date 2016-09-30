package engine;

import java.util.ArrayList;

public class Piece {
	PieceType pieceType;
	Position pos;
	Color color;
	int numberOfMoves;

	public Piece(PieceType pieceType, Position pos, Color color,
			int numberOfMoves) {
		this.pieceType = pieceType;
		this.pos = pos;
		this.color = color;
		this.numberOfMoves = numberOfMoves;
	}

	public PieceType getPieceType() {
		return pieceType;
	}

	public void setPieceType(PieceType pieceType) {
		this.pieceType = pieceType;
	}

	public Position getPos() {
		return pos;
	}

	public void setPosition(Position position) {
		this.pos = position;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getNumberOfMoves() {
		return numberOfMoves;
	}

	public void setNumberOfMoves(int numberOfMoves) {
		this.numberOfMoves = numberOfMoves;
	}

	public static ArrayList<Move> getLegalMove(Piece piece, ArrayList<Piece> pieceList) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		switch (piece.getPieceType()) {
		case PAWN:
			legalMoves = Pawn.getLegalMoves(piece, pieceList);
			break;
		case KNIGHT:
			legalMoves = Knight.getLegalMoves(piece, pieceList);
			break;
		case BISHOP:
			legalMoves = Bishop.getLegalMoves(piece, pieceList);
			break;
		case ROOK:
			legalMoves = Rook.getLegalMoves(piece, pieceList);
			break;
		case QUEEN:
			legalMoves = Queen.getLegalMoves(piece, pieceList);
			break;
		case KING:
			legalMoves = King.getLegalMoves(piece, pieceList);
			break;
		}
		return legalMoves;
	}
}
