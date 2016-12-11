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
				* Constants.magicNumbersRook[rivalIndex]) >>> Constants.magicShiftRook[rivalIndex];
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

	// Modified algorithm based on tutorial from
	// http://www.rivalchess.com/magic-bitboards/
	void occupancyVariation(boolean rook) {
		int bitCount;
		int i, j;
		for (int index = 0; index <= 63; index++) {
			long mask = rook ? Constants.occupancyMaskRook[index] : Constants.occupancyMaskBishop[index];
			int[] setBitsMask = new int[64];
			int[] setBitsIndex = new int[64];
			getIndexSetBits(setBitsMask, mask);
			bitCount = hammingWeight(mask);
			int varCount = (int) (1L << bitCount);
			for (i = 0; i < varCount; i++) {
				Constants.occupancyVariation[i] = 0;
				getIndexSetBits(setBitsIndex, i);
				for (j = 0; setBitsIndex[j] != -1; j++) {
					Constants.occupancyVariation[i] |= (1L << setBitsMask[setBitsIndex[j]]);
				}
			}
		}
	}

	void generateMoveDatabase(boolean rook) {
		for (int index = 0; index < 64; index++) {
			long bitCount = rook ? hammingWeight(Constants.occupancyMaskRook[index])
					: hammingWeight(Constants.occupancyMaskBishop[index]);
			long variations = (int) (1L << bitCount);
			for (int i = 0; i < variations; i++) {
				long validMoves = 0;
				int j = 0;
				int magicIndex = 0;
				if (rook) {
					magicIndex = (int) ((Constants.occupancyVariation[i]
							* Constants.magicNumbersRook[index]) >>> Constants.magicShiftRook[index]);
					for (j = index + 9; j < 64; j += 8) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 8; j >= 0; j -= 8) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index + 1; j % 8 != 0; j++) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 1; j % 8 != 7 && j >= 0; j--) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[i] & (1L << j)) != 0) {
							break;
						}
					}
					Constants.magicMovesRook[index][magicIndex] = validMoves;
				} else {
					magicIndex = (int) ((Constants.occupancyVariation[i]
							* Constants.magicNumbersBishop[index]) >>> Constants.magicShiftBishop[index]);
					for (j = index + 9; j % 8 != 0 && j < 64; j += 9) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 9; j % 8 != 7 && j >= 0; j -= 9) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index + 7; j % 8 != 7 && j < 64; j += 7) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 8; j % 8 != 0 && j >= 0; j -= 7) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[i] & (1L << j)) != 0) {
							break;
						}
					}
					Constants.magicMovesBishop[index][magicIndex] = validMoves;

				}
			}
		}
	}

	// Based on
	// https://en.wikipedia.org/wiki/Hamming_weight#Efficient_implementation
	byte hammingWeight(long board) {
		board = board - ((board >>> 1) & 0x55555555);
		board = (board & 0x33333333) + ((board >>> 2) & 0x33333333);
		return (byte) ((((board + (board >>> 4)) & 0x0F0F0F0F) * 0x01010101) >>> 24);
	}

	void getIndexSetBits(int[] setBits, long board) {
		int size = hammingWeight(board);
		int[] setB = new int[size];
		int i = 0;
		while (i < size) {
			setBits[i] = bitScanForward(board);
			board &= board - 1;
			i++;
		}
		for (int index = 0; index <= 63; index++) {
			if (index < size) {
				setBits[index] = setB[index];
			} else {
				setBits[index] = -1;
			}
		}
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

	int bitScanForward(long bb) {
		int pos = Long.numberOfTrailingZeros(bb);
		return pos == 64 ? -1 : pos;
	}

	void initialiseKnightLookupTable() {
		for (int square = 0; square < 64; square++) {
			long target = (long) Math.pow(2, square);
			if (square == 63) {
				target = 0x8000_0000_0000_0000L;
			}
			long NNE = (target << 17) & ~Constants.FILE_A;
			long NEE = (target << 10) & ~Constants.FILE_A & ~Constants.FILE_B;
			long SEE = (target >>> 6) & ~Constants.FILE_A & ~Constants.FILE_B;
			long SSE = (target >>> 15) & ~Constants.FILE_A;
			long NNW = (target << 15) & ~Constants.FILE_H;
			long NWW = (target << 6) & ~Constants.FILE_G & ~Constants.FILE_H;
			long SWW = (target >>> 10) & ~Constants.FILE_G & ~Constants.FILE_H;
			long SSW = (target >>> 17) & ~Constants.FILE_H;

			Constants.KNIGHT_TABLE[square] = NNE | NEE | SEE | SSE | NNW | NWW | SWW | SSW;
		}
	}

	void initialiseKingLookupTable() {
		for (int square = 0; square < 64; square++) {
			long target = (long) Math.pow(2, square);
			if (square == 63) {
				target = 0x8000_0000_0000_0000L;
			}
			long N = (target << 8) & ~Constants.ROW_1;
			long S = (target >>> 8) & ~Constants.ROW_8;
			long E = (target << 1) & ~Constants.FILE_A;
			long W = (target >>> 1) & ~Constants.FILE_H;
			long NE = (target << 9) & ~Constants.FILE_A;
			long NW = (target << 7) & ~Constants.FILE_H;
			long SE = (target >>> 7) & ~Constants.FILE_A;
			long SW = (target >>> 9) & ~Constants.FILE_H;
			Constants.KING_TABLE[square] = N | S | E | W | NE | NW | SE | SW;
		}
	}
}
