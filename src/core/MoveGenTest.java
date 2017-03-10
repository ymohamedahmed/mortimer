package core;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Test;

public class MoveGenTest {

	@Test
	public void testHammingWeight() {
		long startTime = 0;
		long endTime = 0;
		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		MoveGen.initialiseKnightLookupTable();
		MoveGen.initialiseKingLookupTable();
		MoveGen.initialisePawnLookupTable();
		MoveGen.generateMoveDatabase(true);
		MoveGen.generateMoveDatabase(false);
		endTime = System.currentTimeMillis();
		System.out.println("lookup table init time: " + (endTime - startTime) + "ms");

		startTime = System.currentTimeMillis();
		long perft1 = perft(board, 1);
		System.out.println("perft 1: " + perft1);
		endTime = System.currentTimeMillis();
		System.out.println("perft 1 time: " + (endTime - startTime) + "ms");

		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		long perft2 = perft(board, 2);
		System.out.println("perft 2: " + perft2);
		endTime = System.currentTimeMillis();
		System.out.println("perft 2 time: " + (endTime - startTime) + "ms");

		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		long perft3 = perft(board, 3);
		System.out.println("perft 3: " + perft3);
		endTime = System.currentTimeMillis();
		System.out.println("perft 3 time: " + (endTime - startTime) + "ms");

		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		long perft4 = perft(board, 4);
		System.out.println("perft 4: " + perft4);
		endTime = System.currentTimeMillis();
		System.out.println("perft 4 time: " + (endTime - startTime) + "ms");

		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		long perft5 = perft(board, 5);
		System.out.println("perft 5: " + perft5);
		endTime = System.currentTimeMillis();
		System.out.println("perft 5 time: " + (endTime - startTime) + "ms");

		assertEquals(3, BitBoard.hammingWeight(0b010101));
		assertEquals(1, BitBoard.hammingWeight(0b010));
		assertEquals(5, BitBoard.hammingWeight(0b01010111));
		assertEquals(9, BitBoard.hammingWeight(0b010101111111));
		assertEquals(20, perft1);
		assertEquals(400, perft2);
		assertEquals(8902, perft3);
		assertEquals(197281, perft4);
		assertEquals(4865609, perft5);
		board.loadFen("7k/5K2/6Q1/8/8/8/8/8 b");
		assertEquals(true, board.stalemate(1));
	}

	public long perft(BitBoard board, int depth) {
		long nodes = 0;
		LinkedList<Move> moveList = MoveGen.generateMoves(board, true);
		int nMoves = moveList.size();
		if (depth == 1) {
			return nMoves;
		}

		for (int i = 0; i < nMoves; i++) {
			board.move(moveList.get(i));
			nodes += perft(board, depth - 1);
			board.undo();
		}
		return nodes;
	}

	public static int flipHorizontalIndex(int index) {
		return (index & 0xF8) | (7 - (index & 7));
	}
}
