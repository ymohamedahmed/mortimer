package core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveGenTest {
	@Test
	public void testHammingWeight() {
		MoveGen moveGen = new MoveGen(null, null);
		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();	
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
		assertEquals(0, moveGen.getIndexSetBits(0b010101L)[0]);
		assertEquals(4, moveGen.hammingWeight(0b0001010101));
		assertEquals(2, moveGen.hammingWeight(0b11000));
		assertEquals(5, moveGen.hammingWeight(0b00010101011));
		assertEquals(0, moveGen.hammingWeight(0b0000000000));
	}
}
