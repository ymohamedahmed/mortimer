package engine;

import java.util.ArrayList;

public class Queen extends Piece {

	public Queen(Position pos, Color color, int numberOfMoves) {
		super(PieceType.QUEEN, pos, color, numberOfMoves);
	}

	public static ArrayList<Move> getLegalMoves(Piece piece, ArrayList<Piece> pieceList) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		return Move.removeIllegalMoves(pieceList, piece.getColor(),
				possibleMoves);
	}

}
