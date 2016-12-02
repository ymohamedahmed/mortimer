package core;

import java.util.ArrayList;

public class MoveGen {
	private BitBoard board;
	private ArrayList<Move> moveList;

	public MoveGen(BitBoard board, ArrayList<Move> moveList) {
		this.board = board;
		this.moveList = moveList;
	}

	void addPawnPushes(int side) {
		int[] offsets = { 8, 64 - 8 };
		long[] promotions_mask = { Constants.ROW_8, Constants.ROW_1 };
		long[] startWithMask = { Constants.ROW_3, Constants.ROW_6 };
		int offset = offsets[side];
		long pawns = board.bitboards[side | Constants.PAWN];
		long emptySquares = ~(board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK]);
		long pushes = circularLeftShift(pawns, offset) & emptySquares;
		// add moves
		double promotions = pushes & promotions_mask[side];
		// add moves
		double doublePushes = circularLeftShift(pushes & startWithMask[side], offset) & emptySquares;
		// add moves
	}

	void addPawnAttacks(int side) {
		int offsets[][] = { { 7, 55 }, { 9, 57 } };
		long promotions_mask[] = { Constants.ROW_8, Constants.ROW_1 };
		long file_mask[] = { ~Constants.FILE_H, ~Constants.FILE_A };
		long pawns = board.bitboards[side | Constants.PAWN];
		long enemy = board.bitboards[~side];
		for (int direction = 0; direction < 2; direction++) {
			int offset = offsets[direction][side];
			long targets = circularLeftShift(pawns, offset) & file_mask[direction];
			long attacks = enemy & targets;
			// add moves
			long enPassantAttacks = targets & (1 << board.flags.enPassantSquare);
			// add moves
			long promotions = attacks & promotions_mask[side];
			// add moves
		}
	}

	void addMovesWithOffset(int offset, long target, long flags) {
		while (target > 0) {
			int to = bitScanForward(target);
			int from = (to - offset) % 64;
			int capture = board.board[to];
			target &= target - 1;
		}
	}

	void addKnightMoves(int side) {
		long knights = board.bitboards[Constants.KNIGHT | side];
		long enemy = ~board.bitboards[side];
		while (knights > 0) {
			int from = bitScanForward(knights);
			long targets = Constants.KNIGHT_TABLE[from] & enemy;
			// add moves
			knights &= knights - 1;
		}
	}

	void addKingMoves(int side) {
		long kings = board.bitboards[Constants.KING | side];
		long enemy = ~board.bitboards[side];
		while (kings > 0) {
			int from = bitScanForward(kings);
			long targets = Constants.KING_TABLE[from] & enemy;
			// add moves
			kings &= kings - 1;
		}
	}

	void addRookMoves(int side) {

	}

	void addBishopMoves(int side) {

	}

	void addQueenMoves(int side) {

	}

	long circularLeftShift(long target, int shift) {
		return target << shift | target >> (64 - shift);
	}

	int bitScanForward(long target) {
		int index64[] = { 0, 47, 1, 56, 48, 27, 2, 60, 57, 49, 41, 37, 28, 16, 3, 61, 54, 58, 35, 52, 50, 42, 21, 44,
				38, 32, 29, 23, 17, 11, 4, 62, 46, 55, 26, 59, 40, 36, 15, 53, 34, 51, 20, 43, 31, 22, 10, 45, 25, 39,
				14, 33, 19, 30, 9, 24, 13, 18, 8, 12, 7, 6, 5, 63 };
		long mask = 0x03f79d71b4cb0a89L;
		assert (target != 0);
		return index64[(int) ((target ^ (target - 1)) * mask) >> 58];
	}

	void initialiseKnightLookupTable() {
		for (int row = 0; row < 8; row++) {
			for (int file = 0; file < 8; file++) {
				int square = row * 8 + file;
				long target = 0L;
				if (file >= 2 && row <= 6) {
					target |= 1 << (square + 6);
				}
				if (file >= 1 && row <= 5) {
					target |= 1 << (square + 15);
				}
				if (file <= 6 && row <= 5) {
					target |= 1 << (square + 17);
				}
				if (file <= 5 && row <= 6) {
					target |= 1 << (square + 10);
				}
				if (file >= 2 && row >= 1) {
					target |= 1 << (square - 10);
				}
				if (file >= 1 && row >= 2) {
					target |= 1 << (square - 17);
				}
				if (file <= 6 && row >= 2) {
					target |= 1 << (square - 15);
				}
				if (file <= 5 && row >= 1) {
					target |= 1 << (square - 6);
				}
				Constants.KNIGHT_TABLE[square] = target;
			}
		}
	}

}
