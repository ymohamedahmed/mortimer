package engine;

import java.util.ArrayList;

public class King extends Piece {
	public King(Position pos, Color color, int numberOfMoves) {
		super(PieceType.KING, pos, color, numberOfMoves);
	}

	public static ArrayList<Move> getLegalMoves(Piece king,
			ArrayList<Piece> pieceList) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		for (int row = king.getPos().getRow() - 1; row <= king.getPos()
				.getRow() + 1; row++) {
			for (int col = king.getPos().getCol() - 1; col <= king.getPos()
					.getCol() + 1; col++) {
				possibleMoves.add(new Move(king, new Position(row, col)));
			}
		}
		// Checking to see if castling is legal
		for (Piece piece : pieceList) {
			if (piece.getColor() == king.getColor()
					&& piece.getPieceType() == PieceType.ROOK) {
				int kingColumn = 0;
				if (piece.getPos().getCol() == 0) {
					kingColumn = 2;
				} else if (piece.getPos().getCol() == 7) {
					kingColumn = 6;
				}
				// TODO add remaining castling code
			}
		}
		return Move.removeIllegalMoves(pieceList, king.getColor(),
				possibleMoves);
	}

	public boolean castling(Piece king, Piece rook, Position kingNewPos,
			Position kingOldPos, ArrayList<Piece> pieceList) {
		CastleType castleType;
		if (kingNewPos.getCol() == 6) {
			castleType = CastleType.KINGSIDE;
		} else if (kingNewPos.getCol() == 2) {
			castleType = CastleType.QUEENSIDE;
			;
		} else {
			return false;
		}
		if (king.getNumberOfMoves() != 0 || rook.getNumberOfMoves() != 0) {
			return false;
		}
		// Checking that none of the square the king moves through cause check

		// TODO add remaining castling code after check added
		return false;
	}

	public static boolean check(Piece king, ArrayList<Piece> pieceList,
			Position kingPos) {
		for (Piece piece : pieceList) {
			if (king.getColor() != piece.getColor()) {
				ArrayList<Move> possibleMoves = piece.getLegalMove(piece,
						pieceList);
				for (Move move : possibleMoves) {
					if (king.getPos() == move.getPosition()) {
						// TODO add check dialog
						if(checkmate(king, pieceList)){
							//TODO add checkmate dialog
							//Game over
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean checkmate(Piece king, ArrayList<Piece> pieceList) {
		ArrayList<Move> kingMoves = getLegalMoves(king, pieceList);
		ArrayList<Move> enemyMoves = new ArrayList<Move>();
		ArrayList<Move> friendlyMoves = new ArrayList<Move>();
		// Separating moves into friendly and enemy moves
		for (Piece piece : pieceList) {
			for (Move move : Piece.getLegalMove(piece, pieceList)) {
				if (king.getColor() != piece.getColor()) {
					enemyMoves.add(move);
				} else if (king.getColor() == piece.getColor()) {
					friendlyMoves.add(move);
				}
			}
		}
		if (!check(king, pieceList, king.getPos())) {
			return false;
		}
		// If king has a move which doesn't result in check
		for (Move move : kingMoves) {
			if (!enemyMoves.contains(move)) {
				return false;
			}
		}
		for (Move move : friendlyMoves) {
			ArrayList<Piece> pieceListTemp = pieceList;
			for (Piece piece : pieceListTemp) {
				if (piece == move.getPiece()) {
					piece.setPosition(move.getPosition());
				}
			}
			if(!check(king, pieceListTemp, king.getPos())){
				return false;
			}
		}
		return true;

	}

}
