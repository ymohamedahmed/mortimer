package core;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.junit.Test;

public class MoveGenTest {

	@Test
	public void testPerft() {
		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		// Initialise the various lookup tables so that move generation works
		// properly
		MoveGen.initialiseKnightLookupTable();
		MoveGen.initialiseKingLookupTable();
		MoveGen.initialisePawnLookupTable();
		MoveGen.generateMoveDatabase(true);
		MoveGen.generateMoveDatabase(false);

		// Work out perft for the first five depths
		long perft1 = perft(board, 1);
		long perft2 = perft(board, 2);
		long perft3 = perft(board, 3);
		long perft4 = perft(board, 4);
		long perft5 = perft(board, 5);
		assertEquals(20, perft1);
		assertEquals(400, perft2);
		assertEquals(8902, perft3);
		assertEquals(197281, perft4);
		assertEquals(4865609, perft5);
	}

	@Test
	public void testCheck() {
		BitBoard board = new BitBoard();
		boolean whiteIsInCheck = true;
		boolean blackIsInCheck = true;
		// Initialise the various lookup tables so that move generation works
		// properly
		MoveGen.initialiseKnightLookupTable();
		MoveGen.initialiseKingLookupTable();
		MoveGen.initialisePawnLookupTable();
		MoveGen.generateMoveDatabase(true);
		MoveGen.generateMoveDatabase(false);
		
		String[] whiteIsInCheckTestCases = {
				"rnbqk1nr/pppp1ppp/8/4p3/1b1P4/4P3/PPP2PPP/RNBQKBNR",
				"r1bqk1nr/pppp1ppp/8/4P3/1b1n4/4P3/PPP1KPPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/4P3/1b1nKq2/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/3Kq3/1b1n4/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/2q5/1bKn4/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/8/1bqn4/3KP3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/3q4/1b1nK3/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/4q3/1b1n1K2/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/5q2/1b1n2K1/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/6q1/1b1n3K/4P3/PPP2PPP/RNBQ1BNR"
		};
		String[] blackIsInCheckTestCases = { 
				"rnbqkbnr/ppp1pppp/8/1B1p4/4P3/8/PPPP1PPP/RNBQK1NR",
				"rnb1kbnr/pppBpppp/8/3p4/4P3/8/PPPP1PPP/RNBQK1NR",
				"rnb2bnr/pppkpppp/8/4N3/3pP3/8/PPPP1PPP/RNBQK2R",
				"rnb2bnr/ppp1pppp/4k3/4N3/3pP1Q1/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/8/4k3/3pPQ2/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/4k3/5Q2/3pP3/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/3k4/4Q3/3pP3/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/2k5/3Q4/3pP3/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/1k6/2Q5/3pP3/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/k7/1Q6/3pP3/8/PPPP1PPP/RNB1K2R" };
		for(String fen : whiteIsInCheckTestCases){
			board.loadFen(fen);
			whiteIsInCheck &= board.check(CoreConstants.WHITE);
		}
		for (String fen : blackIsInCheckTestCases) {
			board.loadFen(fen);
			blackIsInCheck &= board.check(CoreConstants.BLACK);
		}
		assertEquals(true, whiteIsInCheck);
		assertEquals(true, blackIsInCheck);
	}

	@Test
	public void testCheckmate() {

	}

	@Test
	public void testStalemate() {

	}

	// Perft tests the move generation by traversing the strictly legal game
	// tree $\label{code:perft}$
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
}
