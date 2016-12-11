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
		moveGen.occupancyVariation(true);
		
		moveGen.generateMoveDatabase(true);
		
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
		
		//Testing magic bitboards
		board.addPiece(Constants.WHITE_ROOK, 36);
		board.removePiece(0);
		board.printBoard(board.bitboards[Constants.WHITE_ROOK]);
		board.printBoard(moveGen.getRookMoves(23, Constants.WHITE));

		assertEquals(4, moveGen.hammingWeight(0b0001010101));
		assertEquals(2, moveGen.hammingWeight(0b11000));
		assertEquals(5, moveGen.hammingWeight(0b00010101011));
		assertEquals(0, moveGen.hammingWeight(0b0000000000));
	}
}
