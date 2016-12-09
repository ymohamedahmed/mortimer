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

	// Occupancy Mask
	// Manipulated from http://www.rivalchess.com/magic-bitboards/ to match my
	// board representation
	public static final long occupancyMaskBishop[] = { 0x40201008040200L, 0x402010080400L, 0x4020100a00L, 0x40221400L,
			0x2442800L, 0x204085000L, 0x20408102000L, 0x2040810204000L, 0x20100804020000L, 0x40201008040000L,
			0x4020100a0000L, 0x4022140000L, 0x244280000L, 0x20408500000L, 0x2040810200000L, 0x4081020400000L,
			0x10080402000200L, 0x20100804000400L, 0x4020100a000a00L, 0x402214001400L, 0x24428002800L, 0x2040850005000L,
			0x4081020002000L, 0x8102040004000L, 0x8040200020400L, 0x10080400040800L, 0x20100a000a1000L,
			0x40221400142200L, 0x2442800284400L, 0x4085000500800L, 0x8102000201000L, 0x10204000402000L,
			0x4020002040800L, 0x8040004081000L, 0x100a000a102000L, 0x22140014224000L, 0x44280028440200L,
			0x8500050080400L, 0x10200020100800L, 0x20400040201000L, 0x2000204081000L, 0x4000408102000L,
			0xa000a10204000L, 0x14001422400000L, 0x28002844020000L, 0x50005008040200L, 0x20002010080400L,
			0x40004020100800L, 0x20408102000L, 0x40810204000L, 0xa1020400000L, 0x142240000000L, 0x284402000000L,
			0x500804020000L, 0x201008040200L, 0x402010080400L, 0x2040810204000L, 0x4081020400000L, 0xa102040000000L,
			0x14224000000000L, 0x28440200000000L, 0x50080402000000L, 0x20100804020000L, 0x40201008040200L };
	public static long occupancyMaskRook[] = { 0x101010101017eL, 0x202020202027cL, 0x404040404047aL, 0x8080808080876L,
			0x1010101010106eL, 0x2020202020205eL, 0x4040404040403eL, 0x8080808080807eL, 0x1010101017e00L,
			0x2020202027c00L, 0x4040404047a00L, 0x8080808087600L, 0x10101010106e00L, 0x20202020205e00L,
			0x40404040403e00L, 0x80808080807e00L, 0x10101017e0100L, 0x20202027c0200L, 0x40404047a0400L,
			0x8080808760800L, 0x101010106e1000L, 0x202020205e2000L, 0x404040403e4000L, 0x808080807e8000L,
			0x101017e010100L, 0x202027c020200L, 0x404047a040400L, 0x8080876080800L, 0x1010106e101000L,
			0x2020205e202000L, 0x4040403e404000L, 0x8080807e808000L, 0x1017e01010100L, 0x2027c02020200L,
			0x4047a04040400L, 0x8087608080800L, 0x10106e10101000L, 0x20205e20202000L, 0x40403e40404000L,
			0x80807e80808000L, 0x17e0101010100L, 0x27c0202020200L, 0x47a0404040400L, 0x8760808080800L,
			0x106e1010101000L, 0x205e2020202000L, 0x403e4040404000L, 0x807e8080808000L, 0x7e010101010100L,
			0x7c020202020200L, 0x7a040404040400L, 0x76080808080800L, 0x6e101010101000L, 0x5e202020202000L,
			0x3e404040404000L, 0x7e808080808000L, 0x7e01010101010100L, 0x7c02020202020200L, 0x7a04040404040400L,
			0x7608080808080800L, 0x6e10101010101000L, 0x5e20202020202000L, 0x3e40404040404000L, 0x7e80808080808000L };
	public static long magicNumbersRook[] = {};
	public static long magicNumbersBishop[] = {};
	public static long magicShiftRook[] = {};
	public static long magicShiftBishop[] = {};
	public static long magicMovesRook[][] = {};
	public static long magicMovesBishop[][] = {};
}
