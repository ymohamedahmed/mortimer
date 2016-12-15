package core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveGenTest {
	@Test
	public void testHammingWeight() {

		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		MoveGen moveGen = new MoveGen(board, null);
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);

		System.out.println("KNIGHT");
		board.printBoard(Constants.KNIGHT_TABLE[5]);
		board.printBoard(Constants.KNIGHT_TABLE[33]);
		board.printBoard(Constants.KNIGHT_TABLE[55]);
		board.printBoard(Constants.KNIGHT_TABLE[63]);
		System.out.println("KING");
		board.printBoard(Constants.KING_TABLE[5]);
		board.printBoard(Constants.KING_TABLE[33]);
		board.printBoard(Constants.KING_TABLE[55]);
		board.printBoard(Constants.KING_TABLE[63]);

		// Testing magic bitboards
		System.out.println("ROOKS");
		board.printBoard(moveGen.getRookMoves(0, Constants.WHITE));
		board.printBoard(moveGen.getRookMoves(17, Constants.WHITE));
		board.printBoard(moveGen.getRookMoves(33, Constants.WHITE));
		board.printBoard(moveGen.getRookMoves(36, Constants.WHITE));
		board.printBoard(moveGen.getRookMoves(41, Constants.WHITE));

		System.out.println("BISHOPS");
		board.printBoard(moveGen.getBishopMoves(0, Constants.WHITE));
		board.printBoard(moveGen.getBishopMoves(17, Constants.WHITE));		
		board.printBoard(moveGen.getBishopMoves(36, Constants.WHITE));
		board.printBoard(moveGen.getBishopMoves(41, Constants.WHITE));
		
		System.out.println("PAWNS");
	
		assertEquals(56, moveGen.mirrorIndex(0));
		assertEquals(63, moveGen.mirrorIndex(7));
		assertEquals(48, moveGen.mirrorIndex(8));
		assertEquals(40, moveGen.mirrorIndex(16));
		
	}
}
