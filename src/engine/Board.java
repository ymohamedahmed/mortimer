package engine;

import java.util.ArrayList;

public class Board {
	public static boolean isSquareEmpty(ArrayList<Piece> pieceList,
			Position position) {
		for (Piece piece : pieceList) {
			if (piece.getPos().getRow() == position.getRow()
					&& piece.getPos().getCol() == position.getCol()) {
				return false;
			}
		}
		return true;
	}

	public static boolean offGrid(Position position) {
		if (position.getRow() > 7 || position.getCol() > 7
				|| position.getRow() < 0 || position.getCol() < 0) {
			return true;
		}
		return false;
	}
}
