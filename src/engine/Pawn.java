package engine;

import java.util.ArrayList;

public class Pawn extends Piece {

	public Pawn(Position position, Color color, int numberOfMoves) {
		super(PieceType.PAWN, position, color, numberOfMoves);

	}

	public static ArrayList<Move> getLegalMoves(Piece piece, ArrayList<Piece> pieceList) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();

		int colorFactor = piece.getColor().getColorFactor();

		if (piece.getNumberOfMoves() == 0
				&& Board.isSquareEmpty(pieceList, new Position(piece.getPos()
						.getRow() + colorFactor, piece.getPos().getCol()))) {
			Position doubleForward = new Position(piece.getPos().getRow()
					+ (2 * colorFactor), piece.getPos().getCol());
			possibleMoves.add(new Move(piece, doubleForward));
		}
		if (piece.getNumberOfMoves() >= 0) {
			Position singleForward = new Position(piece.getPos().getRow()
					+ colorFactor, piece.getPos().getCol());
			possibleMoves.add(new Move(piece, singleForward));
		}
		for (Move move : possibleMoves) {
			if (Board.isSquareEmpty(pieceList, move.getPosition())) {
				possibleMoves.remove(move);
			}
		}
		Position diagonal1 = new Position(piece.getPos().getRow() + colorFactor,
				piece.getPos().getCol() - 1);
		Position diagonal2 = new Position(piece.getPos().getRow() + colorFactor,
				piece.getPos().getCol() + 1);

		if (!Board.isSquareEmpty(pieceList, diagonal1)
				|| enPassant(pieceList, new Move(piece, diagonal1), colorFactor)) {
			possibleMoves.add(new Move(piece, diagonal1));
		}
		if (!Board.isSquareEmpty(pieceList, diagonal2)
				|| enPassant(pieceList, new Move(piece, diagonal2), colorFactor)) {
			possibleMoves.add(new Move(piece, diagonal2));
		}
		return Move.removeIllegalMoves(pieceList, piece.getColor(), possibleMoves);
	}

	public static boolean enPassant(ArrayList<Piece> pieceList, Move move,
			int colorFactor) {
		for (Piece piece : pieceList) {
			if (piece.getColor() != move.getPiece().getColor()
					&& piece.getPieceType() == PieceType.PAWN
					&& piece.getNumberOfMoves() == 1) {
				if (move.getPosition().getRow() == piece.getPos().getRow()
						+ colorFactor
						&& move.getPosition().getCol() == piece.getPos()
								.getCol()) {
					if (piece.getPos().getRow() == 3
							|| piece.getPos().getRow() == 4) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean pawnPromotion(Piece piece, ArrayList<Piece> pieceList) {
		boolean promotion = false;
		if ((piece.getColor() == Color.BLACK && piece.getPos().getRow() == 6 && Board
				.isSquareEmpty(pieceList, new Position(7, piece.getPos()
						.getCol())))
				|| (piece.getColor() == Color.WHITE
						&& piece.getPos().getRow() == 1 && Board.isSquareEmpty(
						pieceList, new Position(0, piece.getPos().getCol())))) {
			promotion = true;

		}
		return promotion;
	}
}
