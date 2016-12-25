package eval;

import core.CoreConstants;

public class EvalConstants {

	// Max move 'thinking' time (ms)
	public static final double MAX_TIME = 1000.0;
	// Max search depth
	public static final double MAX_DEPTH = 20;

	// Evaluation constants from
	// https://github.com/albertoruibal/carballo/blob/master/core/src/main/java/com/alonsoruibal/chess/evaluation/CompleteEvaluator.java

	public static final int WHITE = 1;
	public static final int BLACK = -1;
	public static final int PAWN = 1;
	public static final int KNIGHT = 2;
	public static final int BISHOP = 3;
	public static final int ROOK = 4;
	public static final int QUEEN = 5;
	public static final int KING = 6;
	public static final int MATE = 30000;
	public static final int WIN = 20000;
	public static final int DRAW = 0;

	public static final int[] PIECE_VALUE = { 0, 100, 325, 325, 500, 975 };
	public static final int[] PIECE_VALUE_PHASE = { 0, 100, S(80, 325), 325, 500, 975 };
	public static final int BISHOP_PAIR = S(50, 50);
	public static final int PHASE_MIDGAME = 1000;
	public static final int PHASE_ENDGAME = 0;
	public static final int MAT_ENDGAME_MIN = PIECE_VALUE[QUEEN] + PIECE_VALUE[ROOK];
	public static final int MAT_MIDGAME_MAX = (3 * PIECE_VALUE[KNIGHT]) + (3 * PIECE_VALUE[BISHOP])
			+ (4 * PIECE_VALUE[ROOK]) + (2 * PIECE_VALUE[QUEEN]);
	public static final int[][] MOBILITY = { {}, {},
			{ S(-12, -16), S(2, 2), S(5, 7), S(7, 9), S(8, 11), S(10, 13), S(11, 14), S(11, 15), S(12, 16) },
			{ S(-16, -16), S(-1, -1), S(3, 3), S(6, 6), S(8, 8), S(9, 9), S(11, 11), S(12, 12), S(13, 13), S(13, 13),
					S(14, 14), S(15, 15), S(15, 15), S(16, 16) },
			{ S(-14, -21), S(-1, -2), S(3, 4), S(5, 7), S(7, 10), S(8, 12), S(9, 13), S(10, 15), S(11, 16), S(11, 17),
					S(12, 18), S(13, 19), S(13, 20), S(14, 20), S(14, 21) },
			{ S(-27, -27), S(-9, -9), S(-2, -2), S(2, 2), S(5, 5), S(8, 8), S(10, 10), S(12, 12), S(13, 13), S(14, 14),
					S(16, 16), S(17, 17), S(18, 18), S(19, 19), S(19, 19), S(20, 20), S(21, 21), S(22, 22), S(22, 22),
					S(23, 23), S(24, 24), S(24, 24), S(25, 25), S(25, 25), S(26, 26), S(26, 26), S(27, 27),
					S(27, 27) } };
	public static final long WHITE_SPACE = (CoreConstants.FILE_C | CoreConstants.FILE_D | CoreConstants.FILE_E
			| CoreConstants.FILE_F) & (CoreConstants.ROW_2 | CoreConstants.ROW_3 | CoreConstants.ROW_4);
	public static final long BLACK_SPACE = (CoreConstants.FILE_C | CoreConstants.FILE_D | CoreConstants.FILE_E
			| CoreConstants.FILE_F) & (CoreConstants.ROW_5 | CoreConstants.ROW_6 | CoreConstants.ROW_7);
	public static final int SPACE = S(2, 0);

	public static final int[] PAWN_ATTACKS = { 0, 0, S(11, 15), S(12, 16), S(17, 23), S(19, 25), 0 };
	public static final int[] MINOR_ATTACKS = { 0, S(3, 5), S(7, 9), S(7, 9), S(10, 14), S(11, 15), 0 };
	public static final int[] MAJOR_ATTACKS = { 0, S(2, 2), S(3, 4), S(3, 4), S(5, 6), S(5, 7), 0 };

	public static final int HUNG_PIECES = S(16, 25);
	public static final int PINNED_PIECE = S(7, 15);

	public static final int[] PAWN_BACKWARDS = { S(20, 15), S(10, 15) };
	public static final int[] PAWN_ISOLATED = { S(20, 20), S(10, 20) };
	public static final int[] PAWN_DOUBLED = { S(8, 16), S(10, 20) };
	public static final int PAWN_UNSUPPORTED = S(2, 4);
	public static final int[] PAWN_CANDIDATE = { 0, S(10, 13), S(10, 13), S(14, 18), S(22, 28), S(34, 43), S(50, 63),
			0 };
	public static final int[] PAWN_PASSER = { 0, S(20, 25), S(20, 25), S(28, 35), S(44, 55), S(68, 85), S(100, 125),
			0 };
	public static final int[] PAWN_PASSER_OUTSIDE = { 0, 0, 0, S(2, 3), S(7, 9), S(14, 18), S(24, 30), 0 };
	public static final int[] PAWN_PASSER_CONNECTED = { 0, 0, 0, S(2, 3), S(7, 9), S(14, 18), S(24, 30), 0 };
	public static final int[] PAWN_PASSER_SUPPORTED = { 0, 0, 0, S(6, 6), S(17, 17), S(33, 33), S(55, 55), 0 };
	public static final int[] PAWN_PASSER_MOBILE = { 0, 0, 0, S(2, 2), S(6, 6), S(12, 12), S(20, 20), 0 };
	public static final int[] PAWN_PASSER_RUNNER = { 0, 0, 0, S(6, 6), S(18, 18), S(36, 36), S(60, 60), 0 };
	public static final int[] PAWN_PASSER_OTHER_KING_DISTANCE = { 0, 0, 0, S(0, 2), S(0, 6), S(0, 12), S(0, 20), 0 };
	public static final int[] PAWN_PASSER_MY_KING_DISTANCE = { 0, 0, 0, S(0, 1), S(0, 3), S(0, 6), S(0, 10), 0 };

	public static final int[] PAWN_SHIELD_CENTER = { 0, S(55, 0), S(41, 0), S(28, 0), S(14, 0), 0, 0, 0 };
	public static final int[] PAWN_SHIELD = { 0, S(35, 0), S(26, 0), S(18, 0), S(9, 0), 0, 0, 0 };
	public static final int[] PAWN_STORM_CENTER = { 0, 0, 0, S(8, 0), S(15, 0), S(30, 0), 0, 0 };
	public static final int[] PAWN_STORM = { 0, 0, 0, S(5, 0), S(10, 0), S(20, 0), 0, 0 };

	public static final int PAWN_BLOCKADE = S(5, 0);

	public static final int[] KNIGHT_OUTPOST = { S(15, 10), S(22, 15) };

	public static final int[] BISHOP_OUTPOST = { S(7, 4), S(10, 7) };
	public static final int BISHOP_MY_PAWNS_IN_COLOR_PENALTY = S(2, 4);
	public static final int[] BISHOP_TRAPPED_PENALTY = { S(40, 40), S(80, 80) };
	public static final long[] BISHOP_TRAPPING = { 0, Board.C2, 0, 0, 0, 0, Board.F2, 0, Board.B3, 0, 0, 0, 0, 0, 0,
			Board.G3, Board.B4, 0, 0, 0, 0, 0, 0, Board.G4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Board.B5, 0,
			0, 0, 0, 0, 0, Board.G5, Board.B6, 0, 0, 0, 0, 0, 0, Board.G6, 0, Board.C7, 0, 0, 0, 0, Board.F7, 0 };
	public static final long[] BISHOP_TRAPPING_GUARD = { 0, 0, 0, 0, 0, 0, 0, 0, Board.C2, 0, 0, 0, 0, 0, 0, Board.F2,
			Board.C3, 0, 0, 0, 0, 0, 0, Board.F3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Board.C6, 0, 0, 0, 0,
			0, 0, Board.F6, Board.C7, 0, 0, 0, 0, 0, 0, Board.F7, 0, 0, 0, 0, 0, 0, 0, 0 };

	public static final int[] ROOK_OUTPOST = { S(2, 1), S(3, 2) };
	public static final int[] ROOK_FILE = { S(15, 10), S(7, 5) };
	public static final int ROOK_7 = S(7, 10);
	public static final int[] ROOK_TRAPPED_PENALTY = { S(40, 0), S(30, 0), S(20, 0), S(10, 0) };

	public static final long[] ROOK_TRAPPING = { 0, (Board.A1 | Board.A2), (Board.A1 | Board.A2 | Board.B1 | Board.B2),
			0, 0, (Board.H1 | Board.H2 | Board.G1 | Board.G2), (Board.H1 | Board.H2), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, (Board.A7 | Board.A8), (Board.A7 | Board.A8 | Board.B7 | Board.B8), 0, 0,
			(Board.H7 | Board.H8 | Board.G7 | Board.G8), (Board.H7 | Board.H8), 0 };

	public static final int PIECE_ATTACKS_KING[] = { 0, 0, S(30, 0), S(20, 0), S(40, 0), S(80, 0) };
	public static final int[] KING_SAFETY_PONDER = { 0, 0, 32, 48, 56, 60, 62, 63, 64, 64, 64, 64, 64, 64, 64, 64 };
	public static final int TEMPO = S(15, 5); // Add to moving side score

	public static final long[] OUTPOST_MASK = { 0x00007e7e7e000000L, 0x0000007e7e7e0000L };
	public static final int POS_PAWN[] = { S(-18, 4), S(-6, 2), S(0, 0), S(6, -2), S(6, -2), S(0, 0), S(-6, 2),
			S(-18, 4), S(-21, 1), S(-9, -1), S(-3, -3), S(3, -5), S(3, -5), S(-3, -3), S(-9, -1), S(-21, 1), S(-20, 1),
			S(-8, -1), S(-2, -3), S(4, -5), S(4, -5), S(-2, -3), S(-8, -1), S(-20, 1), S(-19, 2), S(-7, 0), S(-1, -2),
			S(12, -4), S(12, -4), S(-1, -2), S(-7, 0), S(-19, 2), S(-17, 3), S(-5, 1), S(1, -1), S(10, -3), S(10, -3),
			S(1, -1), S(-5, 1), S(-17, 3), S(-16, 4), S(-4, 2), S(2, 0), S(8, -2), S(8, -2), S(2, 0), S(-4, 2),
			S(-16, 4), S(-15, 6), S(-3, 4), S(3, 2), S(9, 0), S(9, 0), S(3, 2), S(-3, 4), S(-15, 6), S(-18, 4),
			S(-6, 2), S(0, 0), S(6, -2), S(6, -2), S(0, 0), S(-6, 2), S(-18, 4) };
	public static final int POS_KNIGHT[] = { S(-27, -22), S(-17, -17), S(-9, -12), S(-4, -9), S(-4, -9), S(-9, -12),
			S(-17, -17), S(-27, -22), S(-21, -15), S(-11, -8), S(-3, -4), S(2, -2), S(2, -2), S(-3, -4), S(-11, -8),
			S(-21, -15), S(-15, -10), S(-5, -4), S(3, 1), S(8, 3), S(8, 3), S(3, 1), S(-5, -4), S(-15, -10), S(-9, -6),
			S(1, -1), S(9, 4), S(14, 8), S(14, 8), S(9, 4), S(1, -1), S(-9, -6), S(-5, -4), S(5, 1), S(13, 6),
			S(18, 10), S(18, 10), S(13, 6), S(5, 1), S(-5, -4), S(-6, -4), S(4, 2), S(12, 7), S(17, 9), S(17, 9),
			S(12, 7), S(4, 2), S(-6, -4), S(-10, -8), S(0, -1), S(8, 3), S(13, 5), S(13, 5), S(8, 3), S(0, -1),
			S(-10, -8), S(-20, -15), S(-10, -10), S(-2, -5), S(3, -2), S(3, -2), S(-2, -5), S(-10, -10), S(-20, -15) };
	public static final int POS_BISHOP[] = { S(-7, 0), S(-8, -1), S(-11, -2), S(-13, -2), S(-13, -2), S(-11, -2),
			S(-8, -1), S(-7, 0), S(-3, -1), S(3, 1), S(0, 0), S(-2, 0), S(-2, 0), S(0, 0), S(3, 1), S(-3, -1),
			S(-6, -2), S(0, 0), S(7, 3), S(6, 2), S(6, 2), S(7, 3), S(0, 0), S(-6, -2), S(-8, -2), S(-2, 0), S(6, 2),
			S(15, 5), S(15, 5), S(6, 2), S(-2, 0), S(-8, -2), S(-8, -2), S(-2, 0), S(6, 2), S(15, 5), S(15, 5), S(6, 2),
			S(-2, 0), S(-8, -2), S(-6, -2), S(0, 0), S(7, 3), S(6, 2), S(6, 2), S(7, 3), S(0, 0), S(-6, -2), S(-3, -1),
			S(3, 1), S(0, 0), S(-2, 0), S(-2, 0), S(0, 0), S(3, 1), S(-3, -1), S(-2, 0), S(-3, -1), S(-6, -2),
			S(-8, -2), S(-8, -2), S(-6, -2), S(-3, -1), S(-2, 0) };
	public static final int POS_ROOK[] = { S(-4, 0), S(0, 0), S(4, 0), S(8, 0), S(8, 0), S(4, 0), S(0, 0), S(-4, 0),
			S(-4, 0), S(0, 0), S(4, 0), S(8, 0), S(8, 0), S(4, 0), S(0, 0), S(-4, 0), S(-4, 0), S(0, 0), S(4, 0),
			S(8, 0), S(8, 0), S(4, 0), S(0, 0), S(-4, 0), S(-4, 0), S(0, 0), S(4, 0), S(8, 0), S(8, 0), S(4, 0),
			S(0, 0), S(-4, 0), S(-4, 1), S(0, 1), S(4, 1), S(8, 1), S(8, 1), S(4, 1), S(0, 1), S(-4, 1), S(-4, 3),
			S(0, 3), S(4, 3), S(8, 3), S(8, 3), S(4, 3), S(0, 3), S(-4, 3), S(-4, 5), S(0, 5), S(4, 5), S(8, 5),
			S(8, 5), S(4, 5), S(0, 5), S(-4, 5), S(-4, -2), S(0, -2), S(4, -2), S(8, -2), S(8, -2), S(4, -2), S(0, -2),
			S(-4, -2) };
	public static final int POS_QUEEN[] = { S(-9, -15), S(-6, -10), S(-4, -8), S(-2, -7), S(-2, -7), S(-4, -8),
			S(-6, -10), S(-9, -15), S(-6, -10), S(-1, -5), S(1, -3), S(3, -2), S(3, -2), S(1, -3), S(-1, -5),
			S(-6, -10), S(-4, -8), S(1, -3), S(5, 0), S(6, 2), S(6, 2), S(5, 0), S(1, -3), S(-4, -8), S(-2, -7),
			S(3, -2), S(6, 2), S(9, 5), S(9, 5), S(6, 2), S(3, -2), S(-2, -7), S(-2, -7), S(3, -2), S(6, 2), S(9, 5),
			S(9, 5), S(6, 2), S(3, -2), S(-2, -7), S(-4, -8), S(1, -3), S(5, 0), S(6, 2), S(6, 2), S(5, 0), S(1, -3),
			S(-4, -8), S(-6, -10), S(-1, -5), S(1, -3), S(3, -2), S(3, -2), S(1, -3), S(-1, -5), S(-6, -10), S(-9, -15),
			S(-6, -10), S(-4, -8), S(-2, -7), S(-2, -7), S(-4, -8), S(-6, -10), S(-9, -15) };
	public static final int POS_KING[] = { S(34, -58), S(39, -35), S(14, -19), S(-6, -13), S(-6, -13), S(14, -19),
			S(39, -35), S(34, -58), S(31, -35), S(36, -10), S(11, 2), S(-9, 8), S(-9, 8), S(11, 2), S(36, -10),
			S(31, -35), S(28, -19), S(33, 2), S(8, 17), S(-12, 23), S(-12, 23), S(8, 17), S(33, 2), S(28, -19),
			S(25, -13), S(30, 8), S(5, 23), S(-15, 32), S(-15, 32), S(5, 23), S(30, 8), S(25, -13), S(20, -13),
			S(25, 8), S(0, 23), S(-20, 32), S(-20, 32), S(0, 23), S(25, 8), S(20, -13), S(15, -19), S(20, 2), S(-5, 17),
			S(-25, 23), S(-25, 23), S(-5, 17), S(20, 2), S(15, -19), S(5, -35), S(10, -10), S(-15, 2), S(-35, 8),
			S(-35, 8), S(-15, 2), S(10, -10), S(5, -35), S(-5, -58), S(0, -35), S(-25, -19), S(-45, -13), S(-45, -13),
			S(-25, -19), S(0, -35), S(-5, -58) };

	public static int S(int opening, int endgame) {
		return (opening << 16) + endgame;
	}


}
