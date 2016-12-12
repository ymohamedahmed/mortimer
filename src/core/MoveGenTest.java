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
		System.out.println("ROOKS");
		/*board.printBoard(Constants.occupancyMaskRook[20]);
		board.printBoard(Constants.occupancyMaskRook[33]);*/
		board.printBoard(moveGen.getRookMoves(20, Constants.WHITE));
		board.printBoard(moveGen.getRookMoves(36, Constants.WHITE));
		board.printBoard(moveGen.getRookMoves(41, Constants.WHITE));
		
/*		System.out.println("BISHOPS");
		board.printBoard(moveGen.getBishopMoves(20, Constants.WHITE));
		board.printBoard(moveGen.getBishopMoves(36, Constants.WHITE));
		board.printBoard(moveGen.getBishopMoves(41, Constants.WHITE));*/
	
		assertEquals(7, moveGen.littleEndianToRival(0));
		assertEquals(41, moveGen.littleEndianToRival(46));
		assertEquals(63, moveGen.littleEndianToRival(56));
		assertEquals(4, moveGen.hammingWeight(0b0001010101));
		assertEquals(2, moveGen.hammingWeight(0b11000));
		assertEquals(5, moveGen.hammingWeight(0b00010101011));
		assertEquals(0, moveGen.hammingWeight(0b0000000000));
	}
}
