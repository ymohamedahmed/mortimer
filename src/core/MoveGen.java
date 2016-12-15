package core;

import java.util.ArrayList;

public class MoveGen {
	private BitBoard board;
	private ArrayList<Move> moveList;

	public MoveGen(BitBoard board, ArrayList<Move> moveList) {
		this.board = board;
		this.moveList = moveList;
	}

	void generateMoves() {

	}

	void addMoves(int index, long moves, ArrayList<Move> moveList) {
		while (moves != 0) {
			moveList.add(new Move(index, bitScanForward(moves)));
			moves &= moves - 1;
		}
	}

	long getPawnEastAttacks(int side) {
		long result;
		// WHITE
		if (side == 0) {
			result = ((board.bitboards[Constants.WHITE_PAWN] << 9) & ~Constants.FILE_A)
					& board.bitboards[Constants.BLACK];
		} else {
			result = ((board.bitboards[Constants.BLACK_PAWN] >>> 7) & ~Constants.FILE_A)
					& board.bitboards[Constants.WHITE];
		}
		return result;
	}

	long getPawnWestAttacks(int side) {
		long result;
		// WHITE
		if (side == 0) {
			result = ((board.bitboards[Constants.WHITE_PAWN] << 7) & ~Constants.FILE_H)
					& board.bitboards[Constants.BLACK];
		} else {
			result = ((board.bitboards[Constants.BLACK_PAWN] >>> 9) & ~Constants.FILE_H)
					& board.bitboards[Constants.WHITE];
		}
		return result;
	}

	long getRookMoves(int index, int side) {
		long rookBlockers = (board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK])
				& Constants.occupancyMaskRook[index];
		int lookupIndex = (int) ((rookBlockers
				* Constants.magicNumbersRook[index]) >>> Constants.magicShiftRook[index]);
		long moveSquares = Constants.magicMovesRook[index][lookupIndex] & ~board.bitboards[side];
		return moveSquares;
	}

	long getBishopMoves(int index, int side) {
		long bishopBlockers = (board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK])
				& Constants.occupancyMaskBishop[index];
		int lookupIndex = (int) ((bishopBlockers
				* Constants.magicNumbersBishop[index]) >>> Constants.magicShiftBishop[index]);
		long moveSquares = Constants.magicMovesBishop[index][lookupIndex] & ~board.bitboards[side];
		return moveSquares;
	}

	long getQueenMoves(int index, int side) {
		return (getRookMoves(index, side) | getBishopMoves(index, side));
	}

	void getKnightMoves(int index, int side) {

	}

	void getPawnMoves(int index, int side) {

	}

	void getKingMoves(int index, int side) {

	}
	// Modified algorithm based on tutorial from
	// http://www.rivalchess.com/magic-bitboards/

	void generateMoveDatabase(boolean rook) {
		long validMoves = 0;
		int variations;
		int varCount;
		int index, i, j;
		long mask;
		int magicIndex;
		int[] setBitsMask = new int[64];
		int[] setBitsIndex = new int[64];
		int bitCount = 0;

		for (index = 0; index < 64; index++) {

			mask = rook ? Constants.occupancyMaskRook[index] : Constants.occupancyMaskBishop[index];
			getIndexSetBits(setBitsMask, mask);
			bitCount = Long.bitCount(mask);
			varCount = (int) (1L << bitCount);

			for (i = 0; i < varCount; i++) {
				Constants.occupancyVariation[index][i] = 0;
				getIndexSetBits(setBitsIndex, i);
				for (j = 0; setBitsIndex[j] != -1; j++) {
					Constants.occupancyVariation[index][i] |= (1L << setBitsMask[setBitsIndex[j]]);
				}
			}

			variations = (int) (1L << bitCount);
			for (i = 0; i < variations; i++) {
				validMoves = 0;
				if (rook) {
					magicIndex = (int) ((Constants.occupancyVariation[index][i]
							* Constants.magicNumbersRook[index]) >>> Constants.magicShiftRook[index]);
					for (j = index + 8; j < 64; j += 8) {
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
					for (j = index - 7; j % 8 != 0 && j >= 0; j -= 7) {
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

	void getIndexSetBits(int[] setBits, long board) {
		int onBits = 0;
		while (board != 0) {
			setBits[onBits] = Long.numberOfTrailingZeros(board);
			board ^= (1L << setBits[onBits++]);
		}
		setBits[onBits] = -1;
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

	void initialisePawnLookupTable() {
		// Complete for white then use symmetry to complete for white
		for (int index = 0; index < 64; index++) {
			long board = (long) Math.pow(2, index);
			if (index == 63) {
				board = 0x8000_0000_0000_0000L;
			}
			long attacks = ((board << 9) & ~Constants.FILE_A) | ((board << 7) & ~Constants.FILE_H);
		}

	}
}
