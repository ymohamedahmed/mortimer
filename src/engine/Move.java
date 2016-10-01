package engine;

import java.util.ArrayList;

public class Move {
	Piece piece;
	Position finalPosition;

	public Move(Piece piece, Position finalPosition) {
		this.piece = piece;
		this.finalPosition = finalPosition;
	}

	public static ArrayList<Move> removeIllegalMoves(
			ArrayList<Piece> pieceList, Color color,
			ArrayList<Move> possibleMoves) {
		return removeCheckMoves(pieceList, color,
				removePositionsOffBoard(possibleMoves));
	}

	private static ArrayList<Move> removeCheckMoves(ArrayList<Piece> pieceList,
			Color color, ArrayList<Move> possibleMoves) {
		Piece king = null;
		// Extracting the King from the array of pieces
		for (Piece piece : pieceList) {
			if (piece.getPieceType() == PieceType.KING
					&& piece.getColor() == color) {
				king = piece;
			}
		}
		for (Move move : possibleMoves) {
			if(move.getPiece().getColor() == king.getColor()){
				ArrayList<Piece> pieceListTemp = pieceList;
				for(Piece piece : pieceListTemp){
					if(piece == move.getPiece()){
						piece.setPosition(move.getPosition());
					}
				}
				if(King.check(king, pieceListTemp, king.getPos())){
					possibleMoves.remove(move);
				}
			}
		}
		return possibleMoves;
	}

	private static ArrayList<Move> removePositionsOffBoard(ArrayList<Move> moves) {
		for (Move move : moves) {
			if (move.getPosition().getRow() > 7
					|| move.getPosition().getRow() < 0
					|| move.getPosition().getCol() > 7
					|| move.getPosition().getRow() < 0) {
				moves.remove(move);
			}
		}
		return moves;
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
}
