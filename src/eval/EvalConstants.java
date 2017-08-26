package eval;

public class EvalConstants {
	// Constants are used as a part of the evaluation function and provide
	// values used in the calculations
	// Max move 'thinking' time (ms)
	public static double THINKING_TIME = 30.0;
	public static double MAX_THINKING_TIME = 1000.0;
	public static double MIN_THINKING_TIME = 200.0;
	// Max search depth
	public static final double MAX_DEPTH = 15;
	public static final double MIN_DEPTH = 2;

	// New identifiers used for colours and piece types
	public static final int WHITE = 1;
	public static final int BLACK = -1;
	public static final int PAWN = 1;
	public static final int KNIGHT = 2;
	public static final int BISHOP = 3;
	public static final int ROOK = 4;
	public static final int QUEEN = 5;
	public static final int KING = 6;

	// Piece Square tables from the Chess Programming wikispace
	// https://chessprogramming.wikispaces.com/Simplified+evaluation+function
	public static int[] pawnTable = { 0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 10, -20, -20, 10, 10, 5, 5, -5,
			-10, 0, 0, -10, -5, 5, 0, 0, 0, 20, 20, 0, 0, 0, 5, 5, 10, 25, 25, 10, 5, 5, 10, 10, 20,
			30, 30, 20, 10, 10, 50, 50, 50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0 };
	public static int[] knightTable = { -50, -40, -30, -30, -30, -30, -40, -50, -40, -20, 0, 5, 5,
			0, -20, -40, -30, 5, 10, 15, 15, 10, 5, -30, -30, 0, 15, 20, 20, 15, 0, -30, -30, 5, 15,
			20, 20, 15, 5, -30, -30, 0, 10, 15, 15, 10, 0, -30, -40, -20, 0, 0, 0, 0, -20, -40, -50,
			-40, -30, -30, -30, -30, -40, -50 };
	public static int[] bishopTable = { -20, -10, -10, -10, -10, -10, -10, -20, -10, 5, 0, 0, 0, 0,
			5, -10, -10, 10, 10, 10, 10, 10, 10, -10, -10, 0, 10, 10, 10, 10, 0, -10, -10, 5, 5, 10,
			10, 5, 5, -10, -10, 0, 5, 10, 10, 5, 0, -10, -10, 0, 0, 0, 0, 0, 0, -10, -20, -10, -10,
			-10, -10, -10, -10, -20 };
	public static int[] rookTable = { 0, 0, 0, 5, 5, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0,
			0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0,
			-5, 5, 10, 10, 10, 10, 10, 10, 5, 0, 0, 0, 0, 0, 0, 0, 0 };
	public static int[] queenTable = { -20, -10, -10, -5, -5, -10, -10, -20, -10, 0, 5, 0, 0, 0, 0,
			-10, -10, 5, 5, 5, 5, 5, 0, -10, 0, 0, 5, 5, 5, 5, 0, -5, -5, 0, 5, 5, 5, 5, 0, -5, -10,
			0, 5, 5, 5, 5, 0, -10, -10, 0, 0, 0, 0, 0, 0, -10, -20, -10, -10, -5, -5, -10, -10,
			-20 };
	public static int[] kingMiddleTable = { 20, 30, 10, 0, 0, 10, 30, 20, 20, 20, 0, 0, 0, 0, 20,
			20, -10, -20, -20, -20, -20, -20, -20, -10, -20, -30, -30, -40, -40, -30, -30, -20, -30,
			-40, -40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30, -30, -40,
			-40, -50, -50, -40, -40, -30, -30, -40, -40, -50, -50, -40, -40, -30 };
	public static int[] kingEndTable = { -50, -30, -30, -30, -30, -30, -30, -50, -30, -30, 0, 0, 0,
			0, -30, -30, -30, -10, 20, 30, 30, 20, -10, -30, -30, -10, 30, 40, 40, 30, -10, -30,
			-30, -10, 30, 40, 40, 30, -10, -30, -30, -10, 20, 30, 30, 20, -10, -30, -30, -20, -10,
			0, 0, -10, -20, -30, -50, -40, -30, -20, -20, -30, -40, -50 };
}
