package core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveGenTest {
	@Test
	public void testHammingWeight() {
		MoveGen moveGen = new MoveGen(null, null);
		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		board.printBoard(board.bitboards[0]);
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();
		board.printBoard(Constants.KNIGHT_TABLE[5]);
		board.printBoard(Constants.KING_TABLE[5]);
		assertEquals(0, moveGen.getIndexSetBits(0b010101L)[0]);
		assertEquals(4, moveGen.hammingWeight(0b0001010101));
		assertEquals(2, moveGen.hammingWeight(0b11000));
		assertEquals(5, moveGen.hammingWeight(0b00010101011));
		assertEquals(0, moveGen.hammingWeight(0b0000000000));
	}
}
