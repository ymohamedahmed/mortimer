package eval;

import org.junit.Test;

import core.BitBoard;
import core.MoveGen;

public class EvalTest {

	@Test
	public void test() {
		BitBoard board = new BitBoard();
		MoveGen.initialiseKnightLookupTable();
		MoveGen.initialiseKingLookupTable();
		MoveGen.initialisePawnLookupTable();
		MoveGen.generateMoveDatabase(true);
		MoveGen.generateMoveDatabase(false);

		board.resetToInitialSetup();
		System.out.println(Evaluation.evaluate(board, 1));
		System.out.println(Evaluation.evaluate(board, -1));
		board.loadFen("1k1r4/pp1b1R2/3q2pp/4p3/2B5/4Q3/PPP2B2/2K5");
		System.out.println(Evaluation.evaluate(board, 1));
		board.loadFen("3r1k2/4npp1/1ppr3p/p6P/P2PPPP1/1NR5/5K2/2R5");
		System.out.println(Evaluation.evaluate(board, 1));
		board.loadFen("2q1rr1k/3bbnnp/p2p1pp1/2pPp3/PpP1P1P1/1P2BNNP/2BQ1PRK/7R");
		System.out.println(Evaluation.evaluate(board, 1));
		board.loadFen("rnbqkb1r/p3pppp/1p6/2ppP3/3N4/2P5/PPP1QPPP/R1B1KB1R");
		System.out.println(Evaluation.evaluate(board, 1));
		board.loadFen("r1b2rk1/2q1b1pp/p2ppn2/1p6/3QP3/1BN1B3/PPP3PP/R4RK1");
		System.out.println(Evaluation.evaluate(board, 1));
		board.loadFen("2r3k1/pppR1pp1/4p3/4P1P1/5P2/1P4K1/P1P5/8");
		System.out.println(Evaluation.evaluate(board, 1));
		board.loadFen("1nk1r1r1/pp2n1pp/4p3/q2pPp1N/b1pP1P2/B1P2R2/2P1B1PP/R2Q2K1");
		System.out.println(Evaluation.evaluate(board, 1));
		board.loadFen("4b3/p3kp2/6p1/3pP2p/2pP1P2/4K1P1/P3N2P/8");
		System.out.println(Evaluation.evaluate(board, 1));
	}

}
