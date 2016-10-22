package engine;

import java.util.ArrayList;
import java.util.Iterator;

public class Knight extends Piece {

	public Knight(Position position, PieceColor color, int numberOfMoves) {
		super(PieceType.KNIGHT, position, color, numberOfMoves);
		this.setMovesList(getInitMovesList());
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

	private ArrayList<Move> getInitMovesList() {
		ArrayList<Move> initMovesList = new ArrayList<Move>();
		int colorFactor = this.getColor().getColorFactor();
		initMovesList.add(new Move(this, new Position(this.getPos().getRow() + (2 * colorFactor), this.getPos().getCol() - 1), false));
		initMovesList.add(new Move(this, new Position(this.getPos().getRow() + (2 * colorFactor), this.getPos().getCol() + 1), false));
		return initMovesList;
	}

	public ArrayList<Move> getLegalMoves(ArrayList<Piece> pieceList) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		//Adding moves in the L-shape
		for (int row = this.getPos().getRow() - 2; row <= this.getPos()
				.getRow() + 2; row++) {
			for (int col = this.getPos().getCol() - 2; col <= this.getPos()
					.getCol() + 2; col++) {
				int rowDiff = row - this.getPos().getRow();
				int colDiff = col - this.getPos().getCol();

				if(Math.pow(rowDiff,2) + Math.pow(colDiff,2) == 5.0){
					possibleMoves.add(new Move(this, new Position(row, col), false));
				}

			}
		}
		//Removing moves that land on a same color piece
		// Iterator has to be used to avoid concurrent modification exception
		// i.e. so that we can remove from the arraylist as we loop through it
		Iterator<Move> iter = possibleMoves.iterator();
		while (iter.hasNext()) {
			Move move = iter.next();
			if (!Board.isSquareEmpty(pieceList, move.getPosition())) {
				if (Board.getPiece(pieceList, move.getPosition()).getColor() == getColor()) {
					iter.remove();
				}
			}
		}

		return possibleMoves;
	}

}