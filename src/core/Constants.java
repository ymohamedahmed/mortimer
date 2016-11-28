package core;

public class Constants {
	// constants
	public static final byte WHITE_PAWN = 2;
	public static final byte BLACK_PAWN = 3;
	public static final byte WHITE_KNIGHT = 4;
	public static final byte BLACK_KNIGHT = 5;
	public static final byte WHITE_ROOK = 6;
	public static final byte BLACK_ROOK = 7;
	public static final byte WHITE_BISHOP = 8;
	public static final byte BLACK_BISHOP = 9;
	public static final byte WHITE_QUEEN = 10;
	public static final byte BLACK_QUEEN = 11;
	public static final byte WHITE_KING = 12;
	public static final byte BLACK_KING = 13;
	public static final byte EMPTY = 0;
	public static final byte WHITE = 0;
	public static final byte BLACK = 1;
	public static final byte PAWN = 2;
	public static final byte KNIGHT = 4;
	public static final byte ROOK = 6;
	public static final byte BISHOP = 8;
	public static final byte QUEEN = 10;
	public static final byte KING = 12;
	public static final int NULL_SQUARE = 64;
	public static final long ROW_1 = 0xFF << 0;
	public static final long ROW_3 = 0xFF << 16;
	public static final long ROW_6 = 0xFF << 40;
	public static final long ROW_8 = 0xFF << 56;

	// Castling
	public static final byte WHITE_KINGSIDE = 1;
	public static final byte WHITE_QUEENSIDE = 2;
	public static final byte BLACK_KINGSIDE = 4;
	public static final byte BLACK_QUEENSIDE = 8;
	public static final byte FULL_CASTLING_RIGHTS = 1 | 2 | 4 | 8;
}
