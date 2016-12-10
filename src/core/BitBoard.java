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
		long bitboard = 1 << square;
		bitboards[piece & 1] |= bitboard;
		bitboards[piece] |= bitboard;
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

	}

	byte getType(int square) {
		return board[square];
	}

	long getBitboard(int type) {
		return bitboards[type];
	}

	void printBoard(long board) {
		for (int shift = 0; shift <= 56; shift += 8) {
			System.out.println((byte) ((board >>> shift) & 0xFF));
		}
	}

	class Flags {
		byte castlingRights;
		byte enPassantSquare;
		byte sideToMove;
	}
}
