package core;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

public class MoveGenTest {
	BitBoard board = new BitBoard();

	@Test
	public void testHammingWeight() {

		board.resetToInitialSetup();
		MoveGen moveGen = new MoveGen(board);
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();
		moveGen.initialisePawnLookupTable();
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);

		ArrayList<Move> moves = moveGen.generateMoves(0, false);
		System.out.println("SIZE : " + moves.size());
		System.out.println("PAWN MOVED");
		moves = moveGen.generateMoves(0, false);
		System.out.println("perft 1 : " + perft(moveGen, 1));
		System.out.println("perft 2 : " + perft(moveGen, 2));
		System.out.println("perft 3 : " + perft(moveGen, 3));
		assertEquals(56, moveGen.mirrorIndex(0));
		assertEquals(63, moveGen.mirrorIndex(7));
		assertEquals(48, moveGen.mirrorIndex(8));
		assertEquals(40, moveGen.mirrorIndex(16));
	}

	public long perft(MoveGen moveGen, int depth) {
		if (depth == 0) {
			return 1;
		}
		ArrayList<Move> moveList = moveGen.generateMoves(0, false);
		int nMoves = moveList.size();
		long nodes = 0;
		for (int i = 0; i < nMoves; i++) {
			board.move(moveList.get(i));
			nodes += perft(moveGen, depth - 1);
			board.undo(moveList.get(i));
		}
		return nodes;
	}
}
