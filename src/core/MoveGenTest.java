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

	@Test
	public void testCheck() {
		BitBoard board = new BitBoard();
		// Initialise the various lookup tables so that move generation works
		// properly
		MoveGen.initialiseKnightLookupTable();
		MoveGen.initialiseKingLookupTable();
		MoveGen.initialisePawnLookupTable();
		MoveGen.generateMoveDatabase(true);
		MoveGen.generateMoveDatabase(false);
		// Test cases where white is not in check
		String[] whiteIsNotInCheckTestCases = { "rnbqkbnr/1ppppppp/p7/8/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/8/p7/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/2pppppp/pp6/8/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/1p1ppppp/p1p5/8/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/1pp1pppp/p2p4/8/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/p7/8/3P4/1P6/P1P1PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/8/p7/3P4/1P6/P1P1PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/p7/8/3P4/2P5/PP2PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/8/p7/3P4/2P5/PP2PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/p7/8/3P4/4P3/PPP2PPP/RNBQKBNR" };
		// Test cases where white is in check
		String[] whiteIsInCheckTestCases = { "rnbqk1nr/pppp1ppp/8/4p3/1b1P4/4P3/PPP2PPP/RNBQKBNR",
				"r1bqk1nr/pppp1ppp/8/4P3/1b1n4/4P3/PPP1KPPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/4P3/1b1nKq2/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/3Kq3/1b1n4/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/2q5/1bKn4/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/8/1bqn4/3KP3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/3q4/1b1nK3/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/4q3/1b1n1K2/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/5q2/1b1n2K1/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/6q1/1b1n3K/4P3/PPP2PPP/RNBQ1BNR" };
		// Test cases where black is not in check
		String[] blackIsNotInCheckTestCases = {
				"rnb1kbnr/1p1ppppp/p1p5/q7/3PP3/5N2/PPP2PPP/RNBQKB1R",
				"rnbqk1nr/1ppp1ppp/p3p3/8/1b1P4/5N2/P1P1PPPP/RNBQKB1R",
				"rnb1kbnr/1p1ppppp/p7/q1P5/8/5N2/PPP1PPPP/RNBQKB1R",
				"rnbqk1nr/p1pp1ppp/1p2p3/8/1b1P4/5N2/P1P1PPPP/RNBQKB1R",
				"rnbqkbnr/p1pppppp/8/8/1p1P4/2K5/PPP1PPPP/RNBQ1BNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/4P3/PPP2PPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/3P4/8/P1P1PPPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/4P3/8/PPP2PPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/N7/PPP1PPPP/R1BQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/5N2/PPP1PPPP/RNBQKB1R" };
		// Test cases where black is in check
		String[] blackIsInCheckTestCases = { "rnbqkbnr/ppp1pppp/8/1B1p4/4P3/8/PPPP1PPP/RNBQK1NR",
				"rnb1kbnr/pppBpppp/8/3p4/4P3/8/PPPP1PPP/RNBQK1NR",
				"rnb2bnr/pppkpppp/8/4N3/3pP3/8/PPPP1PPP/RNBQK2R",
				"rnb2bnr/ppp1pppp/4k3/4N3/3pP1Q1/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/8/4k3/3pPQ2/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/4k3/5Q2/3pP3/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/3k4/4Q3/3pP3/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/2k5/3Q4/3pP3/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/1k6/2Q5/3pP3/8/PPPP1PPP/RNB1K2R",
				"rnb2bnr/ppp1pppp/k7/1Q6/3pP3/8/PPPP1PPP/RNB1K2R" };
		// Check each test case matches the expected output
		for (String fen : whiteIsNotInCheckTestCases) {
			board.loadFen(fen);
			assertEquals(false, board.check(CoreConstants.WHITE));
		}
		for (String fen : whiteIsInCheckTestCases) {
			board.loadFen(fen);
			assertEquals(true, board.check(CoreConstants.WHITE));
		}
		for (String fen : blackIsNotInCheckTestCases) {
			board.loadFen(fen);
			assertEquals(false, board.check(CoreConstants.BLACK));
		}
		for (String fen : blackIsInCheckTestCases) {
			board.loadFen(fen);
			assertEquals(true, board.check(CoreConstants.BLACK));
		}
	}

	@Test
	public void testCheckmate() {
		BitBoard board = new BitBoard();
		// Initialise the various lookup tables so that move generation works
		// properly
		MoveGen.initialiseKnightLookupTable();
		MoveGen.initialiseKingLookupTable();
		MoveGen.initialisePawnLookupTable();
		MoveGen.generateMoveDatabase(true);
		MoveGen.generateMoveDatabase(false);
		// Test cases where white is not in checkmate
		String[] whiteIsNotInCheckmateTestCases = {
				"rnbqkbnr/1ppppppp/p7/8/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/8/p7/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/2pppppp/pp6/8/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/1p1ppppp/p1p5/8/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/1pp1pppp/p2p4/8/3P4/P7/1PP1PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/p7/8/3P4/1P6/P1P1PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/8/p7/3P4/1P6/P1P1PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/p7/8/3P4/2P5/PP2PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/8/p7/3P4/2P5/PP2PPPP/RNBQKBNR",
				"rnbqkbnr/1ppppppp/p7/8/3P4/4P3/PPP2PPP/RNBQKBNR" };
		// Test cases where white is in checkmate
		String[] whiteIsInCheckmateTestCases = { "rnb1kbnr/pppp1ppp/4p3/8/3P4/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/pppp1ppp/8/4p3/3P4/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/1ppp1ppp/p7/4P3/8/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/p1pp1ppp/1p6/4P3/8/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/pp1p1ppp/2p5/4P3/8/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/ppp2ppp/3p4/4P3/8/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/pppp1p1p/6p1/4P3/8/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/1ppp1ppp/8/p3P3/8/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/p1pp1ppp/8/1p2P3/8/8/PPP1PPPP/RNq1KBNR",
				"rnb1kbnr/pp1p1ppp/8/2p1P3/8/8/PPP1PPPP/RNq1KBNR" };
		// Test cases where black is not in checkmate
		String[] blackIsNotInCheckmateTestCases = {
				"rnb1kbnr/1p1ppppp/p1p5/q7/3PP3/5N2/PPP2PPP/RNBQKB1R",
				"rnbqk1nr/1ppp1ppp/p3p3/8/1b1P4/5N2/P1P1PPPP/RNBQKB1R",
				"rnb1kbnr/1p1ppppp/p7/q1P5/8/5N2/PPP1PPPP/RNBQKB1R",
				"rnbqk1nr/p1pp1ppp/1p2p3/8/1b1P4/5N2/P1P1PPPP/RNBQKB1R",
				"rnbqkbnr/p1pppppp/8/8/1p1P4/2K5/PPP1PPPP/RNBQ1BNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/4P3/PPP2PPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/3P4/8/P1P1PPPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/4P3/8/PPP2PPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/N7/PPP1PPPP/R1BQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/5N2/PPP1PPPP/RNBQKB1R" };
		// Test cases where black is in checkmate
		String[] blackIsInCheckmateTestCases = { "rnbqkbnr/2pppQpp/4P3/1p6/8/p7/PPPP1PPP/RNB1KBNR",
				"rnbqkbnr/2pppBpp/1p4P1/8/4P3/p7/PPPP1P1P/RNBQK1NR", "1R4Q1/8/7k/8/7R/8/8/7K",
				"1R6/8/8/6Qk/6R1/8/8/7K", "1R6/7k/6Q1/5K2/8/8/8/8", "6Qk/8/8/3B4/8/8/8/7K",
				"6Qk/8/8/3B2K1/8/8/8/8", "3R4/7k/6Q1/6K1/8/8/8/8", "3R3k/8/6Q1/6K1/8/8/8/8",
				"5k2/3R1Q2/8/6K1/8/8/8/8" };
		// Check each test case matches the expected output
		for (String fen : whiteIsNotInCheckmateTestCases) {
			board.loadFen(fen);
			assertEquals(false, board.checkmate(CoreConstants.WHITE));
		}
		for (String fen : whiteIsInCheckmateTestCases) {
			board.loadFen(fen);
			assertEquals(true, board.checkmate(CoreConstants.WHITE));
		}
		for (String fen : blackIsNotInCheckmateTestCases) {
			board.loadFen(fen);
			assertEquals(false, board.checkmate(CoreConstants.BLACK, CoreConstants.BLACK));
		}
		for (String fen : blackIsInCheckmateTestCases) {
			board.loadFen(fen);
			assertEquals(true, board.checkmate(CoreConstants.BLACK, CoreConstants.BLACK));
		}
	}

	@Test
	public void testStalemate() {
		BitBoard board = new BitBoard();
		// Initialise the various lookup tables so that move generation works
		// properly
		MoveGen.initialiseKnightLookupTable();
		MoveGen.initialiseKingLookupTable();
		MoveGen.initialisePawnLookupTable();
		MoveGen.generateMoveDatabase(true);
		MoveGen.generateMoveDatabase(false);
		// Test cases where white is next to move and game is not in stalemate
		String[] whiteToMoveNotInStalemateTestCases = {
				"rnbqk1nr/pppp1ppp/8/4p3/1b1P4/4P3/PPP2PPP/RNBQKBNR",
				"r1bqk1nr/pppp1ppp/8/4P3/1b1n4/4P3/PPP1KPPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/4P3/1b1nKq2/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/3Kq3/1b1n4/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/2q5/1bKn4/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/8/1bqn4/3KP3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/3q4/1b1nK3/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/4q3/1b1n1K2/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/5q2/1b1n2K1/4P3/PPP2PPP/RNBQ1BNR",
				"r1b1k1nr/pppp1ppp/8/6q1/1b1n3K/4P3/PPP2PPP/RNBQ1BNR" };
		// Test cases where white is next to move and game is in stalemate
		String[] whiteToMoveInStalemateTestCases = { "7k/8/8/8/8/8/5q2/7K", "7k/8/8/8/8/6q1/8/7K",
				"8/6k1/8/8/8/6q1/8/7K", "8/7k/8/8/8/6q1/8/7K", "6k1/8/8/8/8/6q1/8/7K",
				"8/6k1/8/8/8/8/5q2/7K", "8/7k/8/8/8/8/5q2/7K", "6k1/8/8/8/8/8/5q2/7K",
				"8/8/5k2/8/8/6q1/8/7K", "8/8/6k1/8/8/6q1/8/7K" };
		// Test cases where black is next to move and game is not in stalemate
		String[] blackToMoveNotInStalemateTestCases = {
				"rnb1kbnr/1p1ppppp/p1p5/q7/3PP3/5N2/PPP2PPP/RNBQKB1R",
				"rnbqk1nr/1ppp1ppp/p3p3/8/1b1P4/5N2/P1P1PPPP/RNBQKB1R",
				"rnb1kbnr/1p1ppppp/p7/q1P5/8/5N2/PPP1PPPP/RNBQKB1R",
				"rnbqk1nr/p1pp1ppp/1p2p3/8/1b1P4/5N2/P1P1PPPP/RNBQKB1R",
				"rnbqkbnr/p1pppppp/8/8/1p1P4/2K5/PPP1PPPP/RNBQ1BNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/4P3/PPP2PPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/3P4/8/P1P1PPPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/4P3/8/PPP2PPP/RNBQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/N7/PPP1PPPP/R1BQKBNR",
				"rnb1kbnr/pp1ppppp/8/q1P5/8/5N2/PPP1PPPP/RNBQKB1R" };
		// Test cases where black is next to move and game is in stalemate
		String[] blackToMoveInStalemateTestCases = { "7K/8/8/8/8/8/5Q2/7k b", "7K/8/8/8/8/6Q1/8/7k b",
				"8/6K1/8/8/8/6Q1/8/7k b", "8/7K/8/8/8/6Q1/8/7k b", "6K1/8/8/8/8/6Q1/8/7k b",
				"8/6K1/8/8/8/8/5Q2/7k b", "8/7K/8/8/8/8/5Q2/7k b", "6K1/8/8/8/8/8/5Q2/7k b",
				"8/8/5K2/8/8/6Q1/8/7k b", "8/8/6K1/8/8/6Q1/8/7k b"};
		// Check each test case matches the expected output
		for (String fen : whiteToMoveNotInStalemateTestCases) {
			board.loadFen(fen);
			assertEquals(false, board.stalemate(CoreConstants.WHITE));
		}
		for (String fen : whiteToMoveInStalemateTestCases) {
			board.loadFen(fen);
			assertEquals(true, board.stalemate(CoreConstants.WHITE));
		}
		for (String fen : blackToMoveNotInStalemateTestCases) {
			board.loadFen(fen);
			assertEquals(false, board.stalemate(CoreConstants.BLACK));
		}
		for (String fen : blackToMoveInStalemateTestCases) {
			board.loadFen(fen);
			assertEquals(true, board.stalemate(CoreConstants.BLACK));
		}
	}

}
