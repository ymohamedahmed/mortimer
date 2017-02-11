package core;

import java.util.ArrayList;
import java.util.Iterator;

public class MoveGen {

	public ArrayList<Move> generateMoves(BitBoard board, boolean legal) {
		ArrayList<Move> moves = new ArrayList<>();
		// Add pawn moves first
		int side = board.toMove;
		addPawnPushes(board, moves, side);
		long pawnBoard = board.bitboards[side + 2];
		while (pawnBoard != 0) {
			addPawnAttacks(board, moves, bitScanForward(pawnBoard), side);
			pawnBoard &= pawnBoard - 1;
		}
		long knightBoard = board.bitboards[side + 4];
		while (knightBoard != 0) {
			addKnightMoves(board, moves, bitScanForward(knightBoard), side);
			knightBoard &= knightBoard - 1;
		}
		long rookBoard = board.bitboards[side + 6];
		while (rookBoard != 0) {
			addRookMoves(board, moves, bitScanForward(rookBoard), side);
			rookBoard &= rookBoard - 1;
		}
		long bishopBoard = board.bitboards[side + 8];
		while (bishopBoard != 0) {
			addBishopMoves(board, moves, bitScanForward(bishopBoard), side);
			bishopBoard &= bishopBoard - 1;
		}
		long queenBoard = board.bitboards[side + 10];
		while (queenBoard != 0) {
			addQueenMoves(board, moves, bitScanForward(queenBoard), side);
			queenBoard &= queenBoard - 1;
		}
		long kingBoard = board.bitboards[side + 12];
		while (kingBoard != 0) {
			addKingMoves(board, moves, bitScanForward(kingBoard), side);
			kingBoard &= kingBoard - 1;
		}

		Iterator<Move> iter = moves.iterator();
		while (iter.hasNext()) {
			Move move = iter.next();
			if (board.board[move.getFinalPos()] == CoreConstants.WHITE_KING
					|| board.board[move.getFinalPos()] == CoreConstants.BLACK_KING) {
				iter.remove();
			}
		}
		if (legal) {
			moves = removeCheckMoves(board, moves, side);
		}
		return moves;
	}

	private boolean kingInKingSquare(BitBoard board, int side) {
		int myKingIndex = BitBoard.bitScanForward(board.bitboards[12 + side]);
		int enemyKingIndex = BitBoard.bitScanForward(board.bitboards[12 + ((side == 0) ? 1 : 0)]);
		if (myKingIndex + 1 == enemyKingIndex) {
			return true;
		}
		if (myKingIndex - 1 == enemyKingIndex) {
			return true;
		}
		if (myKingIndex + 8 == enemyKingIndex) {
			return true;
		}
		if (myKingIndex - 8 == enemyKingIndex) {
			return true;
		}
		if (myKingIndex + 7 == enemyKingIndex) {
			return true;
		}
		if (myKingIndex - 7 == enemyKingIndex) {
			return true;
		}
		if (myKingIndex + 9 == enemyKingIndex) {
			return true;
		}
		if (myKingIndex - 9 == enemyKingIndex) {
			return true;
		}
		return false;
	}

	private ArrayList<Move> removeCheckMoves(BitBoard board, ArrayList<Move> moveList, int side) {

		// Iterator has to be used to avoid concurrent modification exception
		// i.e. so that we can remove from the arraylist as we loop through it
		Iterator<Move> iter = moveList.iterator();
		while (iter.hasNext()) {
			Move move = iter.next();
			int pieceSide = move.getPieceType() % 2;
			if (pieceSide == side) {
				board.move(move);
				boolean check = board.check(side);
				boolean kingInKingSquare = kingInKingSquare(board, side);
				board.undo();
				if (check | kingInKingSquare) {
					iter.remove();
				}
			}
		}
		return moveList;
	}

	private void addMoves(int pieceType, int index, long moves, ArrayList<Move> moveList, boolean enPassant,
			boolean promotion, byte castling) {
		while (moves != 0) {
			Move move = new Move(pieceType, index, bitScanForward(moves));
			move.setCastling(castling);
			move.setPromotion(promotion);
			move.setEnPassant(enPassant);
			moveList.add(move);
			moves &= moves - 1;
		}
	}

	private void addMovesWithOffset(int pieceType, long moves, ArrayList<Move> moveList, boolean enPassant,
			boolean promotion, byte castling, int offset) {
		while (moves != 0) {
			int to = bitScanForward(moves);
			int from = (to - offset) % 64;
			if (from < 0) {
				from = 64 + from;
			}
			Move move = new Move(pieceType, from, to);
			move.setCastling(castling);
			move.setPromotion(promotion);
			move.setEnPassant(enPassant);
			moveList.add(move);
			moves &= moves - 1;
		}
	}

	private void addRookMoves(BitBoard board, ArrayList<Move> moveList, int index, int side) {
		int pieceType = (side == 0) ? CoreConstants.WHITE_ROOK : CoreConstants.BLACK_ROOK;
		long rookBlockers = (board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK])
				& CoreConstants.occupancyMaskRook[index];
		int lookupIndex = (int) ((rookBlockers
				* CoreConstants.magicNumbersRook[index]) >>> CoreConstants.magicShiftRook[index]);
		long moveSquares = CoreConstants.magicMovesRook[index][lookupIndex] & ~board.bitboards[side];
		addMoves(pieceType, index, moveSquares, moveList, false, false, CoreConstants.noCastle);
	}

	private void addBishopMoves(BitBoard board, ArrayList<Move> moveList, int index, int side) {
		int pieceType = (side == 0) ? CoreConstants.WHITE_BISHOP : CoreConstants.BLACK_BISHOP;
		long bishopBlockers = (board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK])
				& CoreConstants.occupancyMaskBishop[index];
		int lookupIndex = (int) ((bishopBlockers
				* CoreConstants.magicNumbersBishop[index]) >>> CoreConstants.magicShiftBishop[index]);
		long moveSquares = CoreConstants.magicMovesBishop[index][lookupIndex] & ~board.bitboards[side];
		addMoves(pieceType, index, moveSquares, moveList, false, false, CoreConstants.noCastle);
	}

	private void addQueenMoves(BitBoard board, ArrayList<Move> moveList, int index, int side) {
		int pieceType = (side == 0) ? CoreConstants.WHITE_QUEEN : CoreConstants.BLACK_QUEEN;
		// Or of the rook moves and bishop moves are the queen moves
		long rookBlockers = (board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK])
				& CoreConstants.occupancyMaskRook[index];
		int lookupIndexRook = (int) ((rookBlockers
				* CoreConstants.magicNumbersRook[index]) >>> CoreConstants.magicShiftRook[index]);
		long moveSquaresRook = CoreConstants.magicMovesRook[index][lookupIndexRook] & ~board.bitboards[side];

		long bishopBlockers = (board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK])
				& CoreConstants.occupancyMaskBishop[index];
		int lookupIndexBishop = (int) ((bishopBlockers
				* CoreConstants.magicNumbersBishop[index]) >>> CoreConstants.magicShiftBishop[index]);
		long moveSquaresBishop = CoreConstants.magicMovesBishop[index][lookupIndexBishop] & ~board.bitboards[side];

		long queenMoves = moveSquaresRook | moveSquaresBishop;
		addMoves(pieceType, index, queenMoves, moveList, false, false, CoreConstants.noCastle);
	}

	private void addKnightMoves(BitBoard board, ArrayList<Move> moveList, int index, int side) {
		int pieceType = (side == 0) ? CoreConstants.WHITE_KNIGHT : CoreConstants.BLACK_KNIGHT;
		long knightMoves = CoreConstants.KNIGHT_TABLE[index] & ~board.bitboards[side];
		addMoves(pieceType, index, knightMoves, moveList, false, false, CoreConstants.noCastle);
	}

	private void addKingMoves(BitBoard board, ArrayList<Move> moveList, int index, int side) {
		long moves = CoreConstants.KING_TABLE[index] & ~board.bitboards[side];
		int pieceType = (side == 0) ? CoreConstants.WHITE_KING : CoreConstants.BLACK_KING;
		addMoves(pieceType, index, moves, moveList, false, false, CoreConstants.noCastle);
		// Check for castling moves
		if (side == CoreConstants.WHITE) {
			if ((board.castling[side] & 0b10000) == 16) {
				addMoves(pieceType, index, CoreConstants.wqueenside, moveList, false, false, CoreConstants.wQSide);
			}
			if ((board.castling[side] & 0b01000) == 8) {
				addMoves(pieceType, index, CoreConstants.wkingside, moveList, false, false, CoreConstants.wKSide);
			}
		} else {
			if ((board.castling[side] & 0b10000) == 16) {
				addMoves(pieceType, index, CoreConstants.bqueenside, moveList, false, false, CoreConstants.bQSide);
			}
			if ((board.castling[side] & 0b01000) == 8) {
				addMoves(pieceType, index, CoreConstants.bkingside, moveList, false, false, CoreConstants.bKSide);
			}
		}
	}

	private void addPawnPushes(BitBoard board, ArrayList<Move> moveList, int side) {
		int pieceType = (side == 0) ? CoreConstants.WHITE_PAWN : CoreConstants.BLACK_PAWN;
		int[] offsets = { 8, 56 };
		long[] promotions_mask = { CoreConstants.ROW_8, CoreConstants.ROW_1 };
		long[] startWithMask = { CoreConstants.ROW_3, CoreConstants.ROW_6 };
		int offset = offsets[side];
		long pawns = board.bitboards[side | CoreConstants.WHITE_PAWN];
		long emptySquares = ~(board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK]);
		long pushes = circularLeftShift(pawns, offset) & emptySquares;
		addMovesWithOffset(pieceType, pushes & ~promotions_mask[side], moveList, false, false, CoreConstants.noCastle,
				offset);
		long promotions = pushes & promotions_mask[side];
		addMovesWithOffset(pieceType, promotions, moveList, false, true, CoreConstants.noCastle, offset);
		long doublePushes = circularLeftShift(pushes & startWithMask[side], offset) & emptySquares;
		addMovesWithOffset(pieceType, doublePushes, moveList, false, false, CoreConstants.noCastle, offset + offset);
	}

	private void addPawnAttacks(BitBoard board, ArrayList<Move> moveList, int index, int side) {
		int enemy = (side == 0) ? 1 : 0;
		int pawnType = (side == 0) ? CoreConstants.WHITE_PAWN : CoreConstants.BLACK_PAWN;
		long[] promotions_mask = { CoreConstants.ROW_8, CoreConstants.ROW_1 };
		long attacks = CoreConstants.PAWN_ATTACKS_TABLE[side][index] & board.bitboards[enemy];
		addMoves(pawnType, index, attacks & ~promotions_mask[side], moveList, false, false, CoreConstants.noCastle);
		long promotions = attacks & promotions_mask[side];
		addMoves(pawnType, index, promotions, moveList, false, true, CoreConstants.noCastle);
		long enPassant = CoreConstants.PAWN_ATTACKS_TABLE[side][index] & board.epTargetSquares[side];
		addMoves(pawnType, index, enPassant, moveList, true, false, CoreConstants.noCastle);
	}

	private long getPawnEastAttacks(long board, int side) {
		long result;
		// WHITE
		if (side == 0) {
			result = ((board << 9) & ~CoreConstants.FILE_A);

		} else {
			result = ((board >>> 7) & ~CoreConstants.FILE_A);
		}
		return result;
	}

	private long getPawnWestAttacks(long board, int side) {
		long result;
		// WHITE
		if (side == 0) {
			result = ((board << 7) & ~CoreConstants.FILE_H);
		} else {
			result = ((board >>> 9) & ~CoreConstants.FILE_H);
		}
		return result;
	}

	private long circularLeftShift(long target, int shift) {
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

			mask = rook ? CoreConstants.occupancyMaskRook[index] : CoreConstants.occupancyMaskBishop[index];
			getIndexSetBits(setBitsMask, mask);
			bitCount = Long.bitCount(mask);
			varCount = (int) (1L << bitCount);

			for (i = 0; i < varCount; i++) {
				CoreConstants.occupancyVariation[index][i] = 0;
				getIndexSetBits(setBitsIndex, i);
				for (j = 0; setBitsIndex[j] != -1; j++) {
					CoreConstants.occupancyVariation[index][i] |= (1L << setBitsMask[setBitsIndex[j]]);
				}
			}

			variations = (int) (1L << bitCount);
			for (i = 0; i < variations; i++) {
				validMoves = 0;
				if (rook) {
					magicIndex = (int) ((CoreConstants.occupancyVariation[index][i]
							* CoreConstants.magicNumbersRook[index]) >>> CoreConstants.magicShiftRook[index]);
					for (j = index + 8; j < 64; j += 8) {
						validMoves |= (1L << j);
						if ((CoreConstants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 8; j >= 0; j -= 8) {
						validMoves |= (1L << j);
						if ((CoreConstants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index + 1; j % 8 != 0; j++) {
						validMoves |= (1L << j);
						if ((CoreConstants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 1; j % 8 != 7 && j >= 0; j--) {
						validMoves |= (1L << j);
						if ((CoreConstants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					CoreConstants.magicMovesRook[index][magicIndex] = validMoves;
				} else {
					magicIndex = (int) ((CoreConstants.occupancyVariation[index][i]
							* CoreConstants.magicNumbersBishop[index]) >>> CoreConstants.magicShiftBishop[index]);
					for (j = index + 9; j % 8 != 0 && j < 64; j += 9) {
						validMoves |= (1L << j);
						if ((CoreConstants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 9; j % 8 != 7 && j >= 0; j -= 9) {
						validMoves |= (1L << j);
						if ((CoreConstants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index + 7; j % 8 != 7 && j < 64; j += 7) {
						validMoves |= (1L << j);
						if ((CoreConstants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					for (j = index - 7; j % 8 != 0 && j >= 0; j -= 7) {
						validMoves |= (1L << j);
						if ((CoreConstants.occupancyVariation[index][i] & (1L << j)) != 0) {
							break;
						}
					}
					CoreConstants.magicMovesBishop[index][magicIndex] = validMoves;

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
			long NNE = (target << 17) & ~CoreConstants.FILE_A;
			long NEE = (target << 10) & ~CoreConstants.FILE_A & ~CoreConstants.FILE_B;
			long SEE = (target >>> 6) & ~CoreConstants.FILE_A & ~CoreConstants.FILE_B;
			long SSE = (target >>> 15) & ~CoreConstants.FILE_A;
			long NNW = (target << 15) & ~CoreConstants.FILE_H;
			long NWW = (target << 6) & ~CoreConstants.FILE_G & ~CoreConstants.FILE_H;
			long SWW = (target >>> 10) & ~CoreConstants.FILE_G & ~CoreConstants.FILE_H;
			long SSW = (target >>> 17) & ~CoreConstants.FILE_H;

			CoreConstants.KNIGHT_TABLE[square] = NNE | NEE | SEE | SSE | NNW | NWW | SWW | SSW;
		}
	}

	public void initialiseKingLookupTable() {
		for (int square = 0; square < 64; square++) {
			long target = (long) Math.pow(2, square);
			if (square == 63) {
				target = 0x8000_0000_0000_0000L;
			}
			long N = (target << 8) & ~CoreConstants.ROW_1;
			long S = (target >>> 8) & ~CoreConstants.ROW_8;
			long E = (target << 1) & ~CoreConstants.FILE_A;
			long W = (target >>> 1) & ~CoreConstants.FILE_H;
			long NE = (target << 9) & ~CoreConstants.FILE_A;
			long NW = (target << 7) & ~CoreConstants.FILE_H;
			long SE = (target >>> 7) & ~CoreConstants.FILE_A;
			long SW = (target >>> 9) & ~CoreConstants.FILE_H;
			CoreConstants.KING_TABLE[square] = N | S | E | W | NE | NW | SE | SW;
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
				CoreConstants.PAWN_ATTACKS_TABLE[side][index] = getPawnEastAttacks(board, side)
						| getPawnWestAttacks(board, side);
			}
		}

	}
	// LOOKUP TABLES FOR BISHOP AND ROOK ARE USED DURING EVALUATION ONLY

	public void initialiseBishopAndRookEvalLookupTable() {
		long square = 1;
		int index = 0;
		while (square != 0) {
			CoreConstants.ROOK_TABLE[index] = squareAttackedSlider(square, 8, CoreConstants.ROW_8)
					| squareAttackedSlider(square, -8, CoreConstants.ROW_1)
					| squareAttackedSlider(square, -1, CoreConstants.FILE_A)
					| squareAttackedSlider(square, 1, CoreConstants.FILE_H);
			CoreConstants.BISHOP_TABLE[index] = squareAttackedSlider(square, 9,
					CoreConstants.ROW_8 | CoreConstants.FILE_H)
					| squareAttackedSlider(square, 7, CoreConstants.ROW_8 | CoreConstants.FILE_A)
					| squareAttackedSlider(square, -7, CoreConstants.ROW_1 | CoreConstants.FILE_H)
					| squareAttackedSlider(square, -9, CoreConstants.ROW_1 | CoreConstants.FILE_A);

			square <<= 1;
			index++;
		}
	}

	long squareAttackedSlider(long square, int shift, long border) {
		long ret = 0;
		while ((square & border) == 0) {
			if (shift > 0) {
				square <<= shift;
			} else {
				square >>>= -shift;
			}
			ret |= square;
		}
		return ret;
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
