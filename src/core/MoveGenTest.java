package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import eval.Evaluation;

public class MoveGenTest {
	BitBoard board = new BitBoard();

	@Test
	public void testHammingWeight() {
		long startTime = 0;
		long endTime = 0;
		board.resetToInitialSetup();
		MoveGen moveGen = new MoveGen();
		startTime = System.currentTimeMillis();
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();
		moveGen.initialisePawnLookupTable();
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);
		endTime = System.currentTimeMillis();
		System.out.println(new Evaluation().evaluate(board, 1));
		System.out.println("lookup table init time: " + (endTime - startTime) + "ms");

		startTime = System.currentTimeMillis();
		System.out.println("perft 1: " + perft(moveGen, 1));
		endTime = System.currentTimeMillis();
		System.out.println("perft 1 time: " + (endTime - startTime) + "ms");

		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		System.out.println("perft 2: " + perft(moveGen, 2));
		endTime = System.currentTimeMillis();
		System.out.println("perft 2 time: " + (endTime - startTime) + "ms");

		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		System.out.println("perft 3: " + perft(moveGen, 3));
		endTime = System.currentTimeMillis();
		System.out.println("perft 3 time: " + (endTime - startTime) + "ms");

		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		System.out.println("perft 4: " + perft(moveGen, 4));
		endTime = System.currentTimeMillis();
		System.out.println("perft 4 time: " + (endTime - startTime) + "ms");

		board.resetToInitialSetup();
		startTime = System.currentTimeMillis();
		System.out.println("perft 5: " + perft(moveGen, 5));
		endTime = System.currentTimeMillis();
		System.out.println("perft 5 time: " + (endTime - startTime) + "ms");

		assertEquals(56, moveGen.mirrorIndex(0));
		assertEquals(63, moveGen.mirrorIndex(7));
		assertEquals(48, moveGen.mirrorIndex(8));
		assertEquals(40, moveGen.mirrorIndex(16));
		assertEquals(3, BitBoard.hammingWeight(0b010101));
		assertEquals(1, BitBoard.hammingWeight(0b010));
		assertEquals(5, BitBoard.hammingWeight(0b01010111));
		assertEquals(9, BitBoard.hammingWeight(0b010101111111));
	}

	public long perft(MoveGen moveGen, int depth) {
		long nodes = 0;
		if (depth == 0) {
			return 1;
		}
		ArrayList<Move> moveList = moveGen.generateMoves(board, true);
		int nMoves = moveList.size();

		for (int i = 0; i < nMoves; i++) {
			board.move(moveList.get(i));
			nodes += perft(moveGen, depth - 1);
			board.undo();
		}
		return nodes;
	}

	public static int flipHorizontalIndex(int index) {
		return (index & 0xF8) | (7 - (index & 7));
	}
}
