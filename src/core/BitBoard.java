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

	public void addPiece(byte piece, int square) {
		board[square] = piece;
		long bitboard = (long) Math.pow(2, square);
		if (square == 63) {
			bitboard = 0x8000_0000_0000_0000L;
		}
		// printBoard(bitboard);
		bitboards[piece & 1] |= bitboard;
		bitboards[piece] |= bitboard;
	}

	public void printBoardArray(byte[] board) {
		String line = "";
		for (int row = 56; row >= 0; row -= 8) {
			for (int col = 0; col < 8; col++) {
				line += " " + board[row + col];
			}
			System.out.println(line);
			line = "";
		}
	}

	public void move(Move move) {
		history = new History();
		int finalIndex = move.getFinalPos();
		int oldIndex = move.getOldPos();
		byte piece = board[oldIndex];
		int side = piece % 2;
		int enemy = (piece == 0) ? 1 : 0;
		boolean capture = board[finalIndex] != Constants.EMPTY;
		// update rook castling flag
		if (piece == Constants.WHITE_ROOK) {
			flags.wrookqueensidemoved = (flags.wrookqueensidemoved) ? true : (oldIndex == 0);
			flags.wrookkingsidemoved = (flags.wrookkingsidemoved) ? true : (oldIndex == 7);
		} else if (piece == Constants.BLACK_ROOK) {
			flags.wrookqueensidemoved = (flags.brookqueensidemoved) ? true : (oldIndex == 56);
			flags.wrookkingsidemoved = (flags.brookkingsidemoved) ? true : (oldIndex == 63);
		} else if (piece == Constants.WHITE_KING) {
			flags.wkingmoved = (flags.wkingmoved) ? true : (oldIndex == 4);
		} else if (piece == Constants.BLACK_KING) {
			flags.bkingmoved = (flags.bkingmoved) ? true : (oldIndex == 60);
		}
		if (capture) {
			history.capturedPiece = board[finalIndex];
			removePiece(finalIndex);
		}
		removePiece(oldIndex);
		addPiece(piece, finalIndex);
		updateCastlingFlags(enemy);
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

	public void undo(Move move) {
		int finalIndex = move.getFinalPos();
		int oldIndex = move.getOldPos();
		byte piece = board[finalIndex];
		addPiece(piece, oldIndex);
		removePiece(finalIndex);
		// flags = history.flags;
		if (history.capturedPiece != Constants.EMPTY) {
			addPiece(history.capturedPiece, finalIndex);
		}
	}

	void updateCastlingFlags(int side) {
		if (side == 0) {
			// Consider queenside(conditions required for castling denoted by c)
			boolean c1 = board[0] == Constants.WHITE_ROOK;
			boolean c2 = board[1] == Constants.EMPTY;
			boolean c3 = board[2] == Constants.EMPTY;
			boolean c4 = board[3] == Constants.EMPTY;
			boolean c5 = board[4] == Constants.WHITE_KING;
			boolean c6 = !flags.wrookqueensidemoved;
			boolean c7 = !flags.wkingmoved;
			boolean c8 = (flags.castlingAttackedSquare[Constants.wQSide] & 1) == 0;

			// Now test whether king would be in check (more expensive
			// calculation)
			if (c1 && c2 && c3 && c4 && c5 && c6 && c7 && c8) {
				flags.wqueenside = true;
			} else {
				flags.wqueenside = false;
			}
			// Consider kingside
			boolean c9 = board[7] == Constants.WHITE_ROOK;
			boolean c10 = board[6] == Constants.EMPTY;
			boolean c11 = board[5] == Constants.EMPTY;
			boolean c12 = board[4] == Constants.WHITE_KING;
			boolean c13 = !flags.wrookkingsidemoved;
			boolean c14 = !flags.wkingmoved;
			boolean c15 = (flags.castlingAttackedSquare[Constants.wKSide] & 1) == 0;

			// Now test whether king would be in check (more expensive
			// calculation)
			if (c9 && c10 && c11 && c12 && c13 && c14 && c15) {
				flags.wkingside = true;
			} else {
				flags.wkingside = false;
			}
		} else {
			// Consider queenside(conditions required for castling denoted by c)
			boolean c1 = board[56] == Constants.BLACK_ROOK;
			boolean c2 = board[57] == Constants.EMPTY;
			boolean c3 = board[58] == Constants.EMPTY;
			boolean c4 = board[59] == Constants.EMPTY;
			boolean c5 = board[60] == Constants.BLACK_KING;
			boolean c6 = !flags.brookqueensidemoved;
			boolean c7 = !flags.bkingmoved;
			boolean c8 = (flags.castlingAttackedSquare[Constants.bQSide] & 1) == 0;

			// Now test whether king would be in check (more expensive
			// calculation)
			if (c1 && c2 && c3 && c4 && c5 && c6 && c7 && c8) {
				flags.bqueenside = true;
			} else {
				flags.bqueenside = false;
			}
			// Consider kingside
			boolean c9 = board[63] == Constants.WHITE_ROOK;
			boolean c10 = board[62] == Constants.EMPTY;
			boolean c11 = board[61] == Constants.EMPTY;
			boolean c12 = board[60] == Constants.WHITE_KING;
			boolean c13 = !flags.brookkingsidemoved;
			boolean c14 = !flags.bkingmoved;
			boolean c15 = (flags.castlingAttackedSquare[Constants.bKSide] & 1) == 0;

			// Now test whether king would be in check (more expensive
			// calculation)
			if (c9 && c10 && c11 && c12 && c13 && c14 && c15) {
				flags.wkingside = true;
			} else {
				flags.wkingside = false;
			}
		}
	}

	// https://chessprogramming.wikispaces.com/Checks+and+Pinned+Pieces+(Bitboards)
	boolean check(int side) {
		int kingIndex = (side == Constants.WHITE) ? bitScanForward(bitboards[Constants.WHITE_KING])
				: bitScanForward(bitboards[Constants.BLACK_KING]);
		long enemyPawns = bitboards[3 - side];
		long enemyKnights = bitboards[5 - side];
		long enemyRookQueen = bitboards[11 - side];
		long enemyBishopQueen = enemyRookQueen;
		long occupiedBoard = bitboards[Constants.WHITE] | bitboards[Constants.BLACK];
		enemyRookQueen |= bitboards[7 - side];
		enemyBishopQueen |= bitboards[9 - side];
		long result = (Constants.PAWN_ATTACKS_TABLE[side][kingIndex] & enemyPawns)
				| (Constants.KNIGHT_TABLE[kingIndex] & enemyKnights)
				| (bishopAttacks(occupiedBoard, kingIndex, side) & enemyBishopQueen)
				| (rookAttacks(occupiedBoard, kingIndex, side) & enemyRookQueen);
		return (bitScanForward(result) != -1);
	}

	// Bishop and Rook attacks for purpose of determing status of check
	long bishopAttacks(long occupiedBoard, int index, int side) {
		long bishopBlockers = (occupiedBoard) & Constants.occupancyMaskBishop[index];
		int lookupIndex = (int) ((bishopBlockers
				* Constants.magicNumbersBishop[index]) >>> Constants.magicShiftBishop[index]);
		long moveSquares = Constants.magicMovesBishop[index][lookupIndex] & ~bitboards[side];
		return moveSquares;
	}

	long rookAttacks(long occupiedBoard, int index, int side) {
		long rookBlockers = (occupiedBoard) & Constants.occupancyMaskRook[index];
		int lookupIndex = (int) ((rookBlockers
				* Constants.magicNumbersRook[index]) >>> Constants.magicShiftRook[index]);
		long moveSquares = Constants.magicMovesRook[index][lookupIndex] & ~bitboards[side];
		return moveSquares;
	}

	public void removePiece(int square) {
		byte piece = board[square];
		board[square] = Constants.EMPTY;
		long bitboard = ~((square == 63) ? 0x8000_0000_0000_0000L : (long) Math.pow(2, square));
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
		bitboards[Constants.WHITE_QUEEN] = 0x0000000000000008L;
		bitboards[Constants.WHITE_KING] = 0x0000000000000010L;
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
		long[] castlingAttackedSquare = new long[5];
	}

	class History {
		byte capturedPiece;
		Flags flags;
	}
}
