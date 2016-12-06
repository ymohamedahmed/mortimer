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

	byte getType(int square) {
		return board[square];
	}

	long getBitboard(int type) {
		return bitboards[type];
	}
	void printBoard(long board){
		System.out.println(board);
	}
	class Flags {
		byte castlingRights;
		byte enPassantSquare;
		byte sideToMove;
	}
}
