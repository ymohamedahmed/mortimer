package engine;

import java.util.ArrayList;

public class Knight extends Piece {

	public Knight(Position position, Color color, int numberOfMoves) {
		super(PieceType.KNIGHT, position, color, numberOfMoves);
	}

	public static ArrayList<Move> getLegalMoves(Piece piece, ArrayList<Piece> pieceList) {
		ArrayList<Move> possibleMoves = new ArrayList<Move>();
		for (int row = piece.getPos().getRow() - 2; row <= piece.getPos()
				.getRow() + 2; row++) {
			for (int col = piece.getPos().getCol() - 2; col <= piece.getPos()
					.getCol() + 2; col++) {
				int rowDiff = row - piece.getPos().getRow();
				int colDiff = col - piece.getPos().getCol();
				
				if(Math.pow(rowDiff,2) + Math.pow(colDiff,2) == 5.0){
					possibleMoves.add(new Move(piece, new Position(row, col)));
				}
				
			}
		}
		return Move.removeIllegalMoves(pieceList, piece.getColor(),possibleMoves);
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
