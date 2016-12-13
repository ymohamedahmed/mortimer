package core;

/**
 * Created by yousuf on 11/22/16.
 */
public class BitBoard {
	public long[] bitboards = new long[14];
	public byte[] board = new byte[64];
	Flags flags = new Flags();

	boolean isEmpty() {
		return 0 == ~(bitboards[Constants.WHITE] | bitboards[Constants.BLACK]);
	}

	void addPiece(byte piece, int square) {
		board[square] = piece;
		long bitboard = 1L << square;
		bitboards[piece & 1] |= bitboard;
		bitboards[piece] |= bitboard;
	}
	void move(Move move){
		
	}
	void removePiece(int square) {
		byte piece = board[square];
		board[square] = Constants.EMPTY;
		long bitboard = ~(1 << square);
		bitboards[piece & 1] &= bitboard;
		bitboards[piece] &= bitboard;
	}

	void reset() {
		for (int i = 0; i < 64; i++) {
			board[i] = Constants.EMPTY;
		}
		for (int i = 0; i < 14; i++) {
			bitboards[i] = 0;
		}
		flags.castlingRights = Constants.FULL_CASTLING_RIGHTS;
		flags.enPassantSquare = Constants.NULL_SQUARE;
		flags.sideToMove = Constants.WHITE;
	}

	void resetToInitialSetup() {
		// Adding pawns
		for (int col = 0; col <= 7; col++) {
			board[8 + col] = Constants.WHITE_PAWN;
			board[48 + col] = Constants.BLACK_PAWN;
		}
		// Adding rooks
		for (int col = 0; col <= 7; col += 7) {
			board[0 + col] = Constants.WHITE_ROOK;
			board[56 + col] = Constants.BLACK_ROOK;
		}
		// Adding knights
		for (int col = 1; col <= 6; col += 5) {
			board[0 + col] = Constants.WHITE_KNIGHT;
			board[56 + col] = Constants.BLACK_KNIGHT;
		}
		// Adding bishops
		for (int col = 2; col <= 5; col += 3) {
			board[0 + col] = Constants.WHITE_BISHOP;
			board[56 + col] = Constants.BLACK_BISHOP;
		}
		// Adding queens
		board[0 + 3] = Constants.WHITE_QUEEN;
		board[56 + 3] = Constants.BLACK_QUEEN;
		// Adding kings
		board[0 + 4] = Constants.WHITE_KING;
		board[56 + 4] = Constants.BLACK_KING;
		bitboards[Constants.WHITE] = 0x000000000000FFFFL;
		bitboards[Constants.BLACK] = 0xFFFF000000000000L;
		bitboards[Constants.WHITE_PAWN] = 0x000000000000FF00L;
		bitboards[Constants.WHITE_KNIGHT] = 0x0000000000000042L;
		bitboards[Constants.WHITE_BISHOP] = 0x0000000000000024L;
		bitboards[Constants.WHITE_ROOK] = 0x0000000000000081L;
		bitboards[Constants.WHITE_QUEEN] = 0x0000000000000010L;
		bitboards[Constants.WHITE_KING] = 0x0000000000000008L;
		bitboards[Constants.BLACK_PAWN] = 0x00FF000000000000L;
		bitboards[Constants.BLACK_KNIGHT] = 0x4200000000000000L;
		bitboards[Constants.BLACK_BISHOP] = 0x2400000000000000L;
		bitboards[Constants.BLACK_ROOK] = 0x8100000000000000L;
		bitboards[Constants.BLACK_QUEEN] = 0x1000000000000000L;
		bitboards[Constants.BLACK_KING] = 0x8000000000000000L;

	}

	byte getType(int square) {
		return board[square];
	}

	long getBitboard(int type) {
		return bitboards[type];
	}

	void printBoard(long board) {
		String result = "";
		byte[] boardArr = new byte[64];
		while (board != 0) {
			int index = bitScanForward(board);
			boardArr[index] = 1;
			board &= board - 1;
		}
		for (int row = 7; row >= 0; row--) {
			String line = "";
			for (int col = 0; col <= 7; col++) {
				line += String.valueOf(boardArr[(8 * row) + col]);
			}
			result += (line + "\n");
		}
		System.out.println(result);
	}

	int bitScanForward(long bb) {
		int pos = Long.numberOfTrailingZeros(bb);
		return pos == 64 ? -1 : pos;
	}

	class Flags {
		byte castlingRights;
		byte enPassantSquare;
		byte sideToMove;
	}
}
