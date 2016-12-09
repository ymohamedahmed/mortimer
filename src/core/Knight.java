package core;

import java.util.ArrayList;
import java.util.Iterator;

public class Knight extends Piece {

	public Knight(Position position, PieceColor color, int numberOfMoves) {
		super(PieceType.KNIGHT, position, color, numberOfMoves);
	}

	public static int getPositionValue(int index) {
		int[] knightTable = new int[]{
				-50, -40, -30, -30, -30, -30, -40, -50,
				-40, -20, 0, 0, 0, 0, -20, -40,
				-30, 0, 10, 15, 15, 10, 0, -30,
				-30, 5, 15, 20, 20, 15, 5, -30,
				-30, 0, 15, 20, 20, 15, 0, -30,
				-30, 5, 10, 15, 15, 10, 5, -30,
				-40, -20, 0, 5, 5, 0, -20, -40,
				-50, -40, -20, -30, -30, -20, -40, -50
		};
		return knightTable[index];
	}
}