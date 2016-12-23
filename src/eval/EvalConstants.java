package eval;

public class EvalConstants {
	// Max move 'thinking' time (ms)
	public static final double MAX_TIME = 4000.0;
	// Max search depth
	public static final double MAX_DEPTH = 15;

	public static int WHITE = 1;
	public static int BLACK = -1;
	public static int PAWN = 0;
	public static int KNIGHT = 1;
	public static int BISHOP = 2;
	public static int ROOK = 3;
	public static int QUEEN = 4;
	public static int[] PIECE_VALUE = { 100, 325, 325, 500, 975 };
}
