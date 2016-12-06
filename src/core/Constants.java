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
	public static final long FILE_A = 0x0101010101010101L << 0;
	public static final long FILE_B = 0x0101010101010101L << 1;
	public static final long FILE_C = 0x0101010101010101L << 2;
	public static final long FILE_D = 0x0101010101010101L << 3;
	public static final long FILE_E = 0x0101010101010101L << 4;
	public static final long FILE_F = 0x0101010101010101L << 5;
	public static final long FILE_G = 0x0101010101010101L << 6;
	public static final long FILE_H = 0x0101010101010101L << 7;

	// Castling
	public static final byte WHITE_KINGSIDE = 1;
	public static final byte WHITE_QUEENSIDE = 2;
	public static final byte BLACK_KINGSIDE = 4;
	public static final byte BLACK_QUEENSIDE = 8;
	public static final byte FULL_CASTLING_RIGHTS = 1 | 2 | 4 | 8;

	// Lookup tables
	public static long KNIGHT_TABLE[] = new long[64];
	public static long KING_TABLE[] = new long[64];
	public static long PAWN_TABLE[][] = new long[2][64];
	public static long occupancyMaskRook[] = new long[64];
	public static long occupancyMaskBishop[] = new long[64];

}
