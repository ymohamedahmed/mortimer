package eval;

public class EvalConstants {
	// Max move 'thinking' time (ms)
	public static final double MAX_TIME = 1000.0;
	// Max search depth
	public static final double MAX_DEPTH = 20;

	public static final int WHITE = 1;
	public static final int BLACK = -1;
	public static final int PAWN = 0;
	public static final int KNIGHT = 1;
	public static final int BISHOP = 2;
	public static final int ROOK = 3;
	public static final int QUEEN = 4;
	public static final int MATE = 30000;
	public static final int WIN = 20000;
	public static final int DRAW = 0;

	public static final int[] PIECE_VALUE = { 100, 325, 325, 500, 975 };
	public static final int[] PIECE_VALUE_PHASE = { 100, phase(80, 325), 325, 500, 975 };
	public static final int BISHOP_PAIR = phase(50, 50);
	public static final int PHASE_MIDGAME = 1000;
	public static final int PHASE_ENDGAME = 0;
	public static final int MAT_ENDGAME_MIN = PIECE_VALUE[QUEEN] + PIECE_VALUE[ROOK];
	public static final int MAT_MIDGAME_MAX = (3 * PIECE_VALUE[KNIGHT]) + (3 * PIECE_VALUE[BISHOP])
			+ (4 * PIECE_VALUE[ROOK]) + (2 * PIECE_VALUE[QUEEN]);
	public static final int[][] MOBILITY = { {}, {},
			{ phase(-12, -16), phase(2, 2), phase(5, 7), phase(7, 9), phase(8, 11), phase(10, 13), phase(11, 14),
					phase(11, 15), phase(12, 16) },
			{ phase(-16, -16), phase(-1, -1), phase(3, 3), phase(6, 6), phase(8, 8), phase(9, 9), phase(11, 11),
					phase(12, 12), phase(13, 13), phase(13, 13), phase(14, 14), phase(15, 15), phase(15, 15),
					phase(16, 16) },
			{ phase(-14, -21), phase(-1, -2), phase(3, 4), phase(5, 7), phase(7, 10), phase(8, 12), phase(9, 13),
					phase(10, 15), phase(11, 16), phase(11, 17), phase(12, 18), phase(13, 19), phase(13, 20),
					phase(14, 20), phase(14, 21) },
			{ phase(-27, -27), phase(-9, -9), phase(-2, -2), phase(2, 2), phase(5, 5), phase(8, 8), phase(10, 10),
					phase(12, 12), phase(13, 13), phase(14, 14), phase(16, 16), phase(17, 17), phase(18, 18),
					phase(19, 19), phase(19, 19), phase(20, 20), phase(21, 21), phase(22, 22), phase(22, 22),
					phase(23, 23), phase(24, 24), phase(24, 24), phase(25, 25), phase(25, 25), phase(26, 26),
					phase(26, 26), phase(27, 27), phase(27, 27) } };
//	public static final long WHITE_SPACE

	public static int phase(int opening, int endgame) {
		return (opening << 16) + endgame;
	}

	public int open(int phase) {
		return (phase + 0x8000) >> 16;
	}

	public int end(int phase) {
		return (short) (phase * 0xffff);
	}
}
