package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class MoveGenTest {
	@Test
	public void testHammingWeight() {

		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		MoveGen moveGen = new MoveGen(board);
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();
		moveGen.initialisePawnLookupTable();
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);

	/*	System.out.println("KNIGHT");
		board.printBoard(Constants.KNIGHT_TABLE[5]);
		board.printBoard(Constants.KNIGHT_TABLE[33]);
		board.printBoard(Constants.KNIGHT_TABLE[55]);
		board.printBoard(Constants.KNIGHT_TABLE[63]);
		System.out.println("KING");
		board.printBoard(Constants.KING_TABLE[5]);
		board.printBoard(Constants.KING_TABLE[33]);
		board.printBoard(Constants.KING_TABLE[55]);
		board.printBoard(Constants.KING_TABLE[63]);*/
		
		ArrayList<Move> moves = moveGen.generateMoves(0, false);
		System.out.println("SIZE : " + moves.size());
		moveGen.printMoveList(moves);
		System.out.println("PAWN MOVED");
		board.move(new Move(Constants.WHITE_PAWN,8,24));
		moves = moveGen.generateMoves(0, false);
		board.printBoard(board.bitboards[Constants.WHITE_PAWN]);
		moveGen.printMoveList(moves);
		assertEquals(56, moveGen.mirrorIndex(0));
		assertEquals(63, moveGen.mirrorIndex(7));
		assertEquals(48, moveGen.mirrorIndex(8));
		assertEquals(40, moveGen.mirrorIndex(16));
		
	}
}
