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

	long getRookMoves(int index, int side) {
		int rivalIndex = littleEndianToRival(index);
		long rookBlockers = (board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK])
				& Constants.occupancyMaskRook[rivalIndex];
		int lookupIndex = (int) (rookBlockers
				* Constants.magicNumbersRook[rivalIndex]) >>> Constants.magicShiftRook[index];
		long moveSquares = Constants.magicMovesRook[rivalIndex][lookupIndex] & ~board.bitboards[side];
		return moveSquares;
	}

	long getBishopMoves(int index, int side) {
		int rivalIndex = littleEndianToRival(index);
		long bishopBlockers = (board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK])
				& Constants.occupancyMaskBishop[rivalIndex];
		int lookupIndex = (int) (bishopBlockers
				* Constants.magicNumbersBishop[rivalIndex]) >>> Constants.magicShiftBishop[index];
		long moveSquares = Constants.magicMovesBishop[rivalIndex][lookupIndex] & ~board.bitboards[side];
		return moveSquares;
	}

	long addQueenMoves(int index, int side) {
		return (getRookMoves(index, side) | getBishopMoves(index, side));
	}

	void occupancyVariation(boolean rook) {
		for (int index = 0; index < 64; index++) {
			long mask = rook ? Constants.occupancyMaskRook[index] : Constants.occupancyMaskBishop[index];
		}
	}

	// Based on
	// https://en.wikipedia.org/wiki/Hamming_weight#Efficient_implementation
	long hammingWeight(long board) {
		board = board - ((board >>> 1) & 0x55555555);
		board = (board & 0x33333333) + ((board >>> 2) & 0x33333333);
		return (((board + (board >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24;
	}

	int littleEndianToRival(int index) {
		int row = (int) index / (int) 8;
		int sumRivalLittleEndian = 7 + (row * 16);
		int rivalIndex = sumRivalLittleEndian - index;
		return rivalIndex;
	}

	long circularLeftShift(long target, int shift) {
		return target << shift | target >>> (64 - shift);
	}

	int bitScanForward(long target) {
		int index64[] = { 0, 47, 1, 56, 48, 27, 2, 60, 57, 49, 41, 37, 28, 16, 3, 61, 54, 58, 35, 52, 50, 42, 21, 44,
				38, 32, 29, 23, 17, 11, 4, 62, 46, 55, 26, 59, 40, 36, 15, 53, 34, 51, 20, 43, 31, 22, 10, 45, 25, 39,
				14, 33, 19, 30, 9, 24, 13, 18, 8, 12, 7, 6, 5, 63 };
		long mask = 0x03f79d71b4cb0a89L;
		assert (target != 0);
		return index64[(int) ((target ^ (target - 1)) * mask) >>> 58];
	}

	void initialiseKnightLookupTable() {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				int square = row * 8 + col;
				long target = 0L;
				if (col >= 2 && row <= 6) {
					target |= 1 << (square + 6);
				}
				if (col >= 1 && row <= 5) {
					target |= 1 << (square + 15);
				}
				if (col <= 6 && row <= 5) {
					target |= 1 << (square + 17);
				}
				if (col <= 5 && row <= 6) {
					target |= 1 << (square + 10);
				}
				if (col >= 2 && row >= 1) {
					target |= 1 << (square - 10);
				}
				if (col >= 1 && row >= 2) {
					target |= 1 << (square - 17);
				}
				if (col <= 6 && row >= 2) {
					target |= 1 << (square - 15);
				}
				if (col <= 5 && row >= 1) {
					target |= 1 << (square - 6);
				}
				Constants.KNIGHT_TABLE[square] = target;
			}
		}
	}

	void initialiseKingLookupTable() {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				int square = (row * 8) + col;
				long target = 0L;
				if (col >= 1 && row <= 6) {
					target |= 1 << (square + 7);
				}
				if (row <= 6) {
					target |= 1 << (square + 8);
				}
				if (col <= 6 && row <= 6) {
					target |= 1 << (square + 9);
				}
				if (col <= 6) {
					target |= 1 << (square + 1);
				}
				if (col <= 6 && row >= 1) {
					target |= 1 << (square - 7);
				}
				if (row >= 1) {
					target |= 1 << (square - 8);
				}
				if (col >= 1 && row >= 1) {
					target |= 1 << (square - 9);
				}
				if (col >= 1) {
					target |= 1 << (square - 1);
				}
				Constants.KING_TABLE[square] = target;
			}
		}
	}

}
