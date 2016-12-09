package core;

import java.util.ArrayList;

public class Bishop extends Piece {

	public Bishop(Position pos, PieceColor color, int numberOfMoves) {
		super(PieceType.BISHOP, pos, color, numberOfMoves);
		this.setMovesList(new ArrayList<Move>());
	}

	public static int getPositionValue(int index) {
		int[] bishopTable = new int[] { -20, -10, -10, -10, -10, -10, -10, -20, -10, 0, 0, 0, 0, 0, 0, -10, -10, 0, 5,
				10, 10, 5, 0, -10, -10, 5, 5, 10, 10, 5, 5, -10, -10, 0, 10, 10, 10, 10, 0, -10, -10, 10, 10, 10, 10,
				10, 10, -10, -10, 5, 0, 0, 0, 0, 5, -10, -20, -10, -40, -10, -10, -40, -10, -20 };
		return bishopTable[index];
	}

}