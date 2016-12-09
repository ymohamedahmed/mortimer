package core;

import java.util.ArrayList;
import java.util.Iterator;

public class Pawn extends Piece {

	public Pawn(Position position, PieceColor color, int numberOfMoves) {
		super(PieceType.PAWN, position, color, numberOfMoves);
	}

	public static int getPositionValue(int index) {
		int[] pawnTable = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 50, 50, 50, 50, 50, 50, 50, 50, 10, 10, 20, 30, 30, 20,
				10, 10, 5, 5, 10, 27, 27, 10, 5, 5, 0, 0, 0, 25, 25, 0, 0, 0, 5, -5, -10, 0, 0, -10, -5, 5, 5, 10, 10,
				-25, -25, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0 };
		return pawnTable[index];
	}

}