package engine;

import java.util.ArrayList;

public class Queen extends Piece {

	public Queen(Position pos, Color color, int numberOfMoves) {
		super(PieceType.QUEEN, pos, color, numberOfMoves);
	}

	public static ArrayList<Move> getLegalMoves(Piece piece,
			ArrayList<Piece> pieceList) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		for (Move move : Bishop.getLegalMoves(piece, pieceList)) {
			possibleMoves.add(move);
		}
		for (Move move : Rook.getLegalMoves(piece, pieceList)) {
			possibleMoves.add(move);
		}
		return Move.removeIllegalMoves(pieceList, piece.getColor(),
				possibleMoves);
	}

}
