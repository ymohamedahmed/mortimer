package core;

import java.util.ArrayList;
import java.util.Iterator;

public class MoveGen {
	private BitBoard board;

	public MoveGen(BitBoard board) {
		this.board = board;
	}

	public ArrayList<Move> generateMoves(int side, boolean legal) {
		ArrayList<Move> moves = new ArrayList<>();
		// Add pawn moves first
		addPawnPushes(moves, side);
		long pawnBoard = board.bitboards[side + 2];
		while (pawnBoard != 0) {
			addPawnAttacks(moves, bitScanForward(pawnBoard), side);
			pawnBoard &= pawnBoard - 1;
		}
		long knightBoard = board.bitboards[side + 4];
		while (knightBoard != 0) {
			addKnightMoves(moves, bitScanForward(knightBoard), side);
			knightBoard &= knightBoard - 1;
		}
		long rookBoard = board.bitboards[side + 6];
		while (rookBoard != 0) {
			addRookMoves(moves, bitScanForward(rookBoard), side);
			rookBoard &= rookBoard - 1;
		}
		long bishopBoard = board.bitboards[side + 8];
		while (bishopBoard != 0) {
			addBishopMoves(moves, bitScanForward(bishopBoard), side);
			bishopBoard &= bishopBoard - 1;
		}
		long queenBoard = board.bitboards[side + 10];
		while (queenBoard != 0) {
			addQueenMoves(moves, bitScanForward(queenBoard), side);
			queenBoard &= queenBoard - 1;
		}
		long kingBoard = board.bitboards[side + 12];
		/*
		 * System.out.println("KING BOARD: "); board.printBoard(kingBoard);
		 */
		while (kingBoard != 0) {
			addKingMoves(moves, bitScanForward(kingBoard), side);
			// System.out.println("KING INDEX: " + bitScanForward(kingBoard));
			kingBoard &= kingBoard - 1;
		}

		int enemy = (side == 0) ? 1 : 0;
		if (side == 0) {
			for (Move move : moves) {
				int finalIndex = move.getFinalPos();
				if (finalIndex == 57) {
					board.flags.castlingAttackedSquare[Constants.bQSide] |= 0b1000;
				}
				if (finalIndex == 58) {
					board.flags.castlingAttackedSquare[Constants.bQSide] |= 0b0100;
				}
				if (finalIndex == 59) {
					board.flags.castlingAttackedSquare[Constants.bQSide] |= 0b0010;
				}
				if (finalIndex == 60) {
					board.flags.castlingAttackedSquare[Constants.bQSide] |= 0b0001;
					board.flags.castlingAttackedSquare[Constants.bKSide] |= 0b100;
				}
				if (finalIndex == 61) {
					board.flags.castlingAttackedSquare[Constants.bKSide] |= 0b010;
				}
				if (finalIndex == 62) {
					board.flags.castlingAttackedSquare[Constants.bKSide] |= 0b001;
				}
			}
		} else {
			for (Move move : moves) {
				int finalIndex = move.getFinalPos();
				if (finalIndex == 1) {
					board.flags.castlingAttackedSquare[Constants.wQSide] |= 0b1000;
				}
				if (finalIndex == 2) {
					board.flags.castlingAttackedSquare[Constants.wQSide] |= 0b0100;
				}
				if (finalIndex == 3) {
					board.flags.castlingAttackedSquare[Constants.wQSide] |= 0b0010;
				}
				if (finalIndex == 4) {
					board.flags.castlingAttackedSquare[Constants.wQSide] |= 0b0001;
					board.flags.castlingAttackedSquare[Constants.wKSide] |= 0b100;
				}
				if (finalIndex == 5) {
					board.flags.castlingAttackedSquare[Constants.wKSide] |= 0b010;
				}
				if (finalIndex == 6) {
					board.flags.castlingAttackedSquare[Constants.wKSide] |= 0b001;
				}
			}
		}

		return (legal) ? removeCheckMoves(moves, side) : moves;
	}

	ArrayList<Move> removeCheckMoves(ArrayList<Move> moveList, int side) {

		// Iterator has to be used to avoid concurrent modification exception
		// i.e. so that we can remove from the arraylist as we loop through it
		Iterator<Move> iter = moveList.iterator();
		while (iter.hasNext()) {
			Move move = iter.next();
			board.move(move);
			boolean check = board.check(side);
			board.undo(move);
			if (check) {
				iter.remove();
			}
		}
		return moveList;
	}

	void addMoves(int pieceType, int index, long moves, ArrayList<Move> moveList, boolean enPassant, boolean promotion,
			byte castling) {
		while (moves != 0) {
			Move move = new Move(pieceType, index, bitScanForward(moves));
			move.setCastling(castling);
			move.setPromotion(promotion);
			move.setEnPassant(enPassant);
			moveList.add(move);
			moves &= moves - 1;
		}
	}

	void addMovesWithOffset(int pieceType, long moves, ArrayList<Move> moveList, boolean enPassant, boolean promotion,
			byte castling, int offset) {
		while (moves != 0) {
			int to = bitScanForward(moves);
			int from = Math.abs(to - offset) % 64;
			Move move = new Move(pieceType, from, to);
			move.setCastling(castling);
			move.setPromotion(promotion);
			move.setEnPassant(enPassant);
			moveList.add(move);
			moves &= moves - 1;
		}
	}

	void addRookMoves(ArrayList<Move> moveList, int index, int side) {
		int pieceType = (side == 0) ? Constants.WHITE_ROOK : Constants.BLACK_ROOK;
		long rookBlockers = (board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK])
				& Constants.occupancyMaskRook[index];
		int lookupIndex = (int) ((rookBlockers
				* Constants.magicNumbersRook[index]) >>> Constants.magicShiftRook[index]);
		long moveSquares = Constants.magicMovesRook[index][lookupIndex] & ~board.bitboards[side];

		/*
		 * System.out.println("ROOK MOVES"); board.printBoard(moveSquares);
		 */

		addMoves(pieceType, index, moveSquares, moveList, false, false, Constants.noCastle);
	}

	void addBishopMoves(ArrayList<Move> moveList, int index, int side) {
		int pieceType = (side == 0) ? Constants.WHITE_BISHOP : Constants.BLACK_BISHOP;
		long bishopBlockers = (board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK])
				& Constants.occupancyMaskBishop[index];
		int lookupIndex = (int) ((bishopBlockers
				* Constants.magicNumbersBishop[index]) >>> Constants.magicShiftBishop[index]);
		long moveSquares = Constants.magicMovesBishop[index][lookupIndex] & ~board.bitboards[side];
		/*
		 * System.out.println("BISHOP MOVES"); board.printBoard(moveSquares);
		 */
		addMoves(pieceType, index, moveSquares, moveList, false, false, Constants.noCastle);
	}

	void addQueenMoves(ArrayList<Move> moveList, int index, int side) {
		int pieceType = (side == 0) ? Constants.WHITE_QUEEN : Constants.BLACK_QUEEN;
		// Or of the rook moves and bishop moves are the queen moves
		long rookBlockers = (board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK])
				& Constants.occupancyMaskRook[index];
		int lookupIndexRook = (int) ((rookBlockers
				* Constants.magicNumbersRook[index]) >>> Constants.magicShiftRook[index]);
		long moveSquaresRook = Constants.magicMovesRook[index][lookupIndexRook] & ~board.bitboards[side];

		long bishopBlockers = (board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK])
				& Constants.occupancyMaskBishop[index];
		int lookupIndexBishop = (int) ((bishopBlockers
				* Constants.magicNumbersBishop[index]) >>> Constants.magicShiftBishop[index]);
		long moveSquaresBishop = Constants.magicMovesBishop[index][lookupIndexBishop] & ~board.bitboards[side];

		long queenMoves = moveSquaresRook | moveSquaresBishop;

		/*
		 * System.out.println("Q(BISHOP), Q(ROOK), QUEEN MOVES");
		 * board.printBoard(moveSquaresBishop);
		 * board.printBoard(moveSquaresRook); board.printBoard(queenMoves);
		 */

		addMoves(pieceType, index, queenMoves, moveList, false, false, Constants.noCastle);
	}

	void addKnightMoves(ArrayList<Move> moveList, int index, int side) {
		int pieceType = (side == 0) ? Constants.WHITE_KNIGHT : Constants.BLACK_KNIGHT;
		long knightMoves = Constants.KNIGHT_TABLE[index] & ~board.bitboards[side];
		addMoves(pieceType, index, knightMoves, moveList, false, false, Constants.noCastle);
	}

	void addKingMoves(ArrayList<Move> moveList, int index, int side) {
		long moves = Constants.KING_TABLE[index] & ~board.bitboards[side];
		/*
		 * System.out.println("KING MOVES");
		 * board.printBoard(Constants.KING_TABLE[index]);
		 */
		int pieceType = (side == 0) ? Constants.WHITE_KING : Constants.BLACK_KING;
		addMoves(pieceType, index, moves, moveList, false, false, Constants.noCastle);
		// Check for castling moves
		if (side == Constants.WHITE) {
			if (board.flags.wqueenside) {
				addMoves(pieceType, index, Constants.wqueenside, moveList, false, false, Constants.wQSide);
			}
			if (board.flags.wkingside) {
				addMoves(pieceType, index, Constants.wkingside, moveList, false, false, Constants.wKSide);
			}
		} else {
			if (board.flags.bqueenside) {
				addMoves(pieceType, index, Constants.bqueenside, moveList, false, false, Constants.bQSide);
			}
			if (board.flags.bkingside) {
				addMoves(pieceType, index, Constants.bkingside, moveList, false, false, Constants.bKSide);
			}
		}
	}

	void addPawnPushes(ArrayList<Move> moveList, int side) {
		int pieceType = (side == 0) ? Constants.WHITE_PAWN : Constants.BLACK_PAWN;
		int[] offsets = { 8, 56 };
		long[] promotions_mask = { Constants.ROW_8, Constants.ROW_1 };
		long[] startWithMask = { Constants.ROW_3, Constants.ROW_6 };
		int offset = offsets[side];
		long pawns = board.bitboards[side | Constants.PAWN];
		long emptySquares = ~(board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK]);
		long pushes = circularLeftShift(pawns, offset) & emptySquares;
		addMovesWithOffset(pieceType, pushes & ~promotions_mask[side], moveList, false, false, Constants.noCastle,
				offset);
		long promotions = pushes & promotions_mask[side];
		addMovesWithOffset(pieceType, promotions, moveList, false, true, Constants.noCastle, offset);
		long doublePushes = circularLeftShift(pushes & startWithMask[side], offset) & emptySquares;
		addMovesWithOffset(pieceType, doublePushes, moveList, false, false, Constants.noCastle, offset + offset);
	}

	void addPawnAttacks(ArrayList<Move> moveList, int index, int side) {
		int enemy = (side == 0) ? 1 : 0;
		int pawnType = (side == 0) ? Constants.WHITE_PAWN : Constants.BLACK_PAWN;
		long[] promotions_mask = { Constants.ROW_8, Constants.ROW_1 };
		long attacks = Constants.PAWN_ATTACKS_TABLE[side][index] & board.bitboards[enemy];
		addMoves(pawnType, index, attacks & ~promotions_mask[side], moveList, false, false, Constants.noCastle);
		long promotions = attacks & promotions_mask[side];
		addMoves(pawnType, index, promotions, moveList, false, true, Constants.noCastle);
		long enPassant = attacks & board.flags.enPassantSquares[enemy];
		addMoves(pawnType, index, enPassant, moveList, true, false, Constants.noCastle);
	}

	long getPawnEastAttacks(long board, int side) {
		long result;
		// WHITE
		if (side == 0) {
			result = ((board << 9) & ~Constants.FILE_A);

		} else {
			result = ((board >>> 7) & ~Constants.FILE_A);
		}
		return result;
	}

	long getPawnWestAttacks(long board, int side) {
		long result;
		// WHITE
		if (side == 0) {
			result = ((board << 7) & ~Constants.FILE_H);
		} else {
			result = ((board >>> 9) & ~Constants.FILE_H);
		}
		return result;
	}

	long circularLeftShift(long target, int shift) {
		return target << shift | target >>> (64 - shift);
	}

	// Modified algorithm based on tutorial from
	// http://www.rivalchess.com/magic-bitboards/

	public void generateMoveDatabase(boolean rook) {
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

	public void initialiseKnightLookupTable() {
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

	public void initialiseKingLookupTable() {
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

	public void initialisePawnLookupTable() {
		// Complete for white then use symmetry to complete for white
		for (int side = 0; side <= 1; side++) {
			for (int index = 0; index < 64; index++) {
				long board = (long) Math.pow(2, index);
				if (index == 63) {
					board = 0x8000_0000_0000_0000L;
				}
				long attacks = ((board << 9) & ~Constants.FILE_A) | ((board << 7) & ~Constants.FILE_H);
				Constants.PAWN_ATTACKS_TABLE[side][index] = getPawnEastAttacks(board, side)
						| getPawnWestAttacks(board, side);
			}
		}

	}

	int mirrorIndex(int index) {
		int row = Math.floorDiv(index, 8);
		int offset = (index < 32) ? 56 - (16 * row) : (16 * row) - 56;
		return index + offset;
	}

	// For Debugging
	public void printMoveList(ArrayList<Move> moves) {
		for (Move move : moves) {
			System.out.println(move.getOldPos() + " TO " + move.getFinalPos());
		}
	}
}
