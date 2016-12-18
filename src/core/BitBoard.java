package core;

import java.util.ArrayList;

/**
 * Created by yousuf on 11/22/16.
 */
public class BitBoard {
	public long[] bitboards = new long[14];
	public byte[] board = new byte[64];
	public Flags flags = new Flags();
	private History history = new History();

	boolean isEmpty() {
		return 0 == ~(bitboards[Constants.WHITE] | bitboards[Constants.BLACK]);
	}

	void addPiece(byte piece, int square) {
		board[square] = piece;
		long bitboard = 1L << square;
		bitboards[piece & 1] |= bitboard;
		bitboards[piece] |= bitboard;
	}

	void move(Move move) {
		int finalIndex = move.getFinalPos();
		int oldIndex = move.getOldPos();
		byte piece = board[oldIndex];
		int side = piece % 2;
		boolean capture = board[finalIndex] != Constants.EMPTY;
		// update rook castling flag
		if (piece == Constants.WHITE_ROOK) {
			flags.wrookqueensidemoved = (flags.wrookqueensidemoved) ? true : (oldIndex == 0);
			flags.wrookkingsidemoved = (flags.wrookkingsidemoved) ? true : (oldIndex == 7);
		}
		if (piece == Constants.BLACK_ROOK) {
			flags.wrookqueensidemoved = (flags.brookqueensidemoved) ? true : (oldIndex == 56);
			flags.wrookkingsidemoved = (flags.brookkingsidemoved) ? true : (oldIndex == 63);
		}
		if (capture) {
			history.capturedPiece = (capture) ? board[finalIndex] : Constants.EMPTY;
			removePiece(finalIndex);
		}
		addPiece(piece, finalIndex);
		removePiece(oldIndex);
		updateCastlingFlags(move);
		history.flags = flags;
		flags.enPassantSquares[side] = 0;
		if (piece == Constants.WHITE_PAWN) {
			if (finalIndex - oldIndex == 16) {
				flags.enPassantSquares[side] = (long) Math.pow(2, oldIndex + 8);
			}
		} else if (piece == Constants.BLACK_PAWN) {
			if (finalIndex - oldIndex == -16) {
				flags.enPassantSquares[side] = (long) Math.pow(2, oldIndex - 8);
			}
		}
		// TODO move rook during castling
		byte castle = move.getCastlingFlag();
		if (castle != 0) {
			int rookOldIndex = 0;
			int rookFinalIndex = 0;
			switch (castle) {
			case 1:
				rookOldIndex = 0;
				rookFinalIndex = 3;
				break;
			case 2:
				rookOldIndex = 7;
				rookFinalIndex = 5;
				break;
			case 3:
				rookOldIndex = 56;
				rookFinalIndex = 59;
				break;
			case 4:
				rookOldIndex = 63;
				rookFinalIndex = 61;
				break;
			}
			removePiece(rookOldIndex);
			addPiece((castle <= 2) ? Constants.WHITE_ROOK : Constants.BLACK_ROOK, rookFinalIndex);
		}

	}

	void undo(Move move) {
		int finalIndex = move.getFinalPos();
		int oldIndex = move.getOldPos();
		byte piece = board[finalIndex];
		addPiece(piece, oldIndex);
		removePiece(finalIndex);
		flags = history.flags;
		addPiece(history.capturedPiece, finalIndex);
	}

	void updateCastlingFlags(Move move) {
		if (!flags.wkingmoved) {
			if (move.getPieceType() == Constants.WHITE_KING) {
				flags.wqueenside = false;
				flags.wkingside = false;
				flags.wkingmoved = true;
			} else {
				// Consider queenside
				if (!flags.wrookqueensidemoved) {
					boolean condition1 = board[0] == Constants.WHITE_ROOK;
					boolean condition2 = board[1] == Constants.EMPTY;
					boolean condition3 = board[2] == Constants.EMPTY;
					boolean condition4 = board[3] == Constants.EMPTY;
					boolean condition5 = board[4] == Constants.WHITE_KING;
					boolean condition6 = !flags.wrookqueensidemoved;

					// Now test whether king would be in check (more expensive
					// calculation)
					if (condition1 && condition2 && condition3 && condition4 && condition5 && condition6) {
						move(new Move(Constants.WHITE_KING, 4, 3));
						boolean check = check(Constants.WHITE, new MoveGen(this).generateMoves(Constants.BLACK, true));
						if (check) {
							flags.wqueenside = false;
						}
						if (!check) {

						}

					}
				} else {
					flags.wqueenside = false;
				}

				// Consider kingside
			}
		}
		if (move.getPieceType() == Constants.BLACK_KING) {
			flags.bqueenside = false;
			flags.bkingside = false;
		}

	}

	boolean check(int side, ArrayList<Move> moves) {
		int kingIndex = (side == Constants.WHITE) ? bitScanForward(bitboards[Constants.WHITE_KING])
				: bitScanForward(bitboards[Constants.BLACK_KING]);
		for (Move move : moves) {
			if (move.getFinalPos() == kingIndex) {
				return true;
			}
		}
		return false;
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

		flags.toMove = Constants.WHITE;
		flags.wqueenside = false;
		flags.wkingside = false;
		flags.bqueenside = false;
		flags.bkingside = false;
	}

	public void resetToInitialSetup() {
		for (int index = 0; index < 64; index++) {
			board[index] = Constants.EMPTY;
		}
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
		boolean wqueenside;
		boolean wkingside;
		boolean bqueenside;
		boolean bkingside;
		// Whether or not pieces have moved
		boolean wkingmoved;
		boolean bkingmoved;
		boolean wrookqueensidemoved;
		boolean wrookkingsidemoved;
		boolean brookqueensidemoved;
		boolean brookkingsidemoved;
		// Next side to play
		int toMove;
		long[] enPassantSquares = new long[2];
	}

	class History {
		byte capturedPiece;
		Flags flags;
	}
}
