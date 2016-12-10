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

	// Modified algorithm based on tutorial from
	// http://www.rivalchess.com/magic-bitboards/
	void occupancyVariation(boolean rook) {
		int[] bitCount = new int[64];
		int i, j;
		for (int index = 0; index < 64; index++) {
			long mask = rook ? Constants.occupancyMaskRook[index] : Constants.occupancyMaskBishop[index];
			int[] setBits = getIndexSetBits(mask);
			bitCount[index] = hammingWeight(mask);
			int varCount = (int) (1L << bitCount[index]);
			for (i = 0; i < varCount; i++) {
				Constants.occupancyVariation[index][i] = 0;
				int[] setBitsVariation = getIndexSetBits(i);
				for (j = 0; setBitsVariation[j] != -1; j++) {
					Constants.occupancyVariation[index][i] |= (1L << setBits[setBitsVariation[j]]);
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
					magicIndex = (int) ((Constants.occupancyVariation[index][i]
							* Constants.magicNumbersRook[index]) >>> Constants.magicShiftRook[index]);
					for (j = index + 9; j < 64; j += 8) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 8; j >= 0; j -= 8) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index + 1; j % 8 != 0; j++) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 1; j % 8 != 7 && j >= 0; j--) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					Constants.magicMovesRook[index][magicIndex] = validMoves;
				} else {
					magicIndex = (int) ((Constants.occupancyVariation[index][i]
							* Constants.magicNumbersBishop[index]) >>> Constants.magicShiftBishop[index]);
					for (j = index + 9; j % 8 != 0 && j < 64; j += 9) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 9; j % 8 != 7 && j >= 0; j -= 9) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index + 7; j % 8 != 7 && j < 64; j += 7) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 8; j % 8 != 0 && j >= 0; j -= 7) {
						validMoves |= (1L << j);
						if ((Constants.occupancyVariation[index][i] & (1L << j)) != 0) {
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

	int[] getIndexSetBits(long board) {
		int size = hammingWeight(board);
		int[] setBits = new int[size];
		int i = 0;
		while (i < size) {
			setBits[i] = bitScanForward(board);
			board &= board - 1;
			i++;
		}
		return setBits;
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
