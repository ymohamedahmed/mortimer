package eval;

import core.BitBoard;
import core.CoreConstants;

public class Endgame extends EvalConstants {
	// The purpose of this class is to consider various potential endgames and
	// to evaluate the board as such
	// This class is based on the Endgame class in Carballo
	// https://github.com/albertoruibal/carballo

	// This method takes the board and decides based on the remaining pieces
	// which method is bessed for evaluation
	public static int evaluate(BitBoard board, int[] scaleFactor, int wPawns, int bPawns, int wKnights, int bKnights,
			int wBishops, int bBishops, int wRooks, int bRooks, int wQueens, int bQueens) {
		scaleFactor[0] = SCALE_FACTOR_DEFAULT;
		// The number of pieces remaining is used to decide which evaluation
		// method is appropriate
		int wNonPawnMat = wKnights + wBishops + wRooks + wQueens;
		int bNonPawnMat = bKnights + bBishops + bRooks + bQueens;
		int wMat = wNonPawnMat + wPawns;
		int bMat = bNonPawnMat + bPawns;

		if (wPawns == 0 && bPawns == 0) {
			if ((bMat == 0 && wMat == 2 && wKnights == 1 && wBishops == 1)
					|| (wMat == 0 && bMat == 2 && bKnights == 1 && bBishops == 1)) {
				// If there is a bishop and knight on one side
				return Endgame.endgameKingBishopKnightKing(board, wMat > bMat);
			}
			// If both sides have the same number of pieces it is approximated
			// to a draw
			if (wMat == 1 && bMat == 1) {
				if (wRooks == 1 && bRooks == 1) {
					return DRAW;
				}
				if (wQueens == 1 && bQueens == 1) {
					return DRAW;
				}
			}
		} else if ((wPawns == 1 && bPawns == 0) || (wPawns == 0 && bPawns == 1)) {
			if (wNonPawnMat == 0 && bNonPawnMat == 0) {
				// If one player has a pawn and the other no pieces
				return Endgame.endgameKingPawnKing(board, wMat > bMat);
			}
			if ((wNonPawnMat == 1 && bNonPawnMat == 0) || (wNonPawnMat == 0 && bNonPawnMat == 1)) {
				if ((wQueens == 1 && bPawns == 1) || (bQueens == 1 && wPawns == 1)) {
					// If one player has a pawn and the other a queen
					return endgameKingQueenKingPawn(board, wQueens > bQueens);
				}
			}
			if (wNonPawnMat == 1 && bNonPawnMat == 1) {
				if (wRooks == 1 && bRooks == 1) {
					// Scale factor is used to manipulate final value
					// accordingly
					scaleFactor[0] = scaleKingRookPawnKingRook(board, wPawns > bPawns);
				}
				if (wBishops == 1 && bBishops == 1) {
					// One side has a bishop and pawn and the other one bishop
					return endgameKingBishopPawnKingBishop(board, wPawns > bPawns);
				}
				if ((wBishops == 1 && wPawns == 1 && bKnights == 1)
						|| (bBishops == 1 && bPawns == 1 && wKnights == 1)) {
					// One side has a bishop and pawn and the other one knights
					return endgameKingBishopPawnKingKnight(board, wPawns > bPawns);
				}
			}
		}
		if (bMat == 0 && (wBishops >= 2 || wRooks > 0 || wQueens > 0)
				|| wMat == 0 && (bBishops >= 2 || bRooks > 0 || bQueens > 9)) {
			// One side has no pieces (except king) and the other has a few
			// significant pieces
			// i.e. two or more bishops
			// or a queen or rook
			return Endgame.endgameKingWithNoPieces(board, wMat > bMat, wKnights + bKnights, wBishops + bBishops,
					wRooks + bRooks, wQueens + bQueens);
		}
		if (wRooks == 1 && bRooks == 1 && ((wPawns == 2 && bPawns == 1) || (wPawns == 1 && bPawns == 2))) {
			scaleFactor[0] = scaleKingRookPawnPawnKingRookPawn(board, wPawns > bPawns);
		}
		if (scaleFactor[0] == SCALE_FACTOR_DRAW) {
			return DRAW;
		}
		// If none of these conditions are met, then the game isn't in the
		// endgame yet so return no value
		return NO_VALUE;
	}

	// NOTE: for all these methods the negative value is returned if it is being
	// evaluated from the black perspective, this is important for the minimax
	// search (check Search.java)
	private static int endgameKingWithNoPieces(BitBoard board, boolean wDominating, int knights, int bishops, int rooks,
			int queens) {
		int wKingIndex = BitBoard.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		int bKingIndex = BitBoard.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		// This is a very strong position hence return a value of known win with
		// some added components based on specifically which pieces the player
		// has
		int value = KNOWN_WIN + (knights * PIECE_VALUE[KNIGHT]) + (bishops * PIECE_VALUE[BISHOP])
				+ (rooks * PIECE_VALUE[ROOK]) + (queens * PIECE_VALUE[QUEEN])
				+ CLOSER_SQUARES[Board.distance(wKingIndex, bKingIndex)]
				+ (wDominating ? TO_CORNERS[bKingIndex] : TO_COLOR_CORNERS[wKingIndex]);
		return (wDominating ? value : -value);
	}

	private static int endgameKingBishopKnightKing(BitBoard board, boolean wDominating) {
		int wKingIndex = BitBoard.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		int bKingIndex = BitBoard.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		long bishops = board.bitboards[CoreConstants.WHITE_BISHOP] | board.bitboards[CoreConstants.BLACK_BISHOP];
		// Flip index is used so that the internal board representation can
		// match the pre-computed values used
		if ((bishops & BLACK_SQUARES) != 0) {
			wKingIndex = Board.flipHorizontalIndex(wKingIndex);
			bKingIndex = Board.flipHorizontalIndex(bKingIndex);
		}
		int value = KNOWN_WIN + CLOSER_SQUARES[Board.distance(wKingIndex, bKingIndex)]
				+ (wDominating ? TO_CORNERS[bKingIndex] : TO_CORNERS[wKingIndex]);
		return (wDominating ? value : -value);
	}

	private static int endgameKingPawnKing(BitBoard board, boolean wDominating) {
		Bitbase bitbase = new Bitbase();
		// Posibilities are stored in an array since King Pawn King situation
		// doesn't have too many permutations
		if (!bitbase.probe(board)) {
			return DRAW;
		}
		return wDominating
				? KNOWN_WIN + PAWN + (int) BitBoard.bitScanForward(board.bitboards[CoreConstants.WHITE_PAWN]) / 8
				: -KNOWN_WIN - PIECE_VALUE[PAWN]
						- (7 - (int) BitBoard.bitScanForward(board.bitboards[CoreConstants.BLACK_PAWN]) / 8);
	}

	private static int scaleKingRookPawnKingRook(BitBoard board, boolean wDominating) {
		int domCol = wDominating ? 0 : 1;
		int nonDomCol = domCol == 0 ? 1 : 0;
		long otherRook = board.bitboards[CoreConstants.WHITE_ROOK + nonDomCol];
		long domKing = board.bitboards[CoreConstants.WHITE_KING + domCol];
		long otherKing = board.bitboards[CoreConstants.WHITE_KING + nonDomCol];
		int domkingIndex = BitBoard.bitScanForward(domKing);
		int rank8 = wDominating ? 7 : 0;
		int rank7 = wDominating ? 6 : 1;
		int rank6 = wDominating ? 5 : 2;
		int rank2 = wDominating ? 1 : 6;
		long pawns = board.bitboards[CoreConstants.WHITE_PAWN] | board.bitboards[CoreConstants.BLACK_PAWN];
		int pIndex = BitBoard.bitScanForward(pawns);
		int pFileIndex = pIndex % 8;
		long pFile = CoreConstants.FILE[pFileIndex];
		long fileAndAdjacent = CoreConstants.FILE[pFileIndex] | CoreConstants.ADJACENT_FILE[pFileIndex];
		boolean white2Move = board.toMove == 0;
		if ((CoreConstants.ROW_BACKWARD[domCol][rank6] & pawns) != 0
				&& (CoreConstants.ROW_BACKWARD[domCol][rank6] & domKing) != 0
				&& (CoreConstants.ROW_FORWARD[domCol][rank6] & fileAndAdjacent & otherKing) != 0
				&& (CoreConstants.ROW[rank6] & otherRook) != 0) {
			return SCALE_FACTOR_DRAW;
		}
		if ((CoreConstants.ROW[rank6] & pawns) != 0
				&& (CoreConstants.ROW_FORWARD[domCol][rank6] & fileAndAdjacent & otherKing) != 0
				&& (CoreConstants.ROW_BACKWARD_INCLUSIVE[domCol][rank2] & otherRook) != 0
				|| ((white2Move != wDominating) && (Board.distance(pIndex, domkingIndex) >= 3))) {
			return SCALE_FACTOR_DRAW;
		}
		if ((CoreConstants.ROW[rank7] & pawns) != 0
				&& (CoreConstants.ROW_FORWARD[domCol][rank6] & pFile & otherKing) != 0
				&& (CoreConstants.ROW_BACKWARD_INCLUSIVE[domCol][rank2] & otherRook) != 0 && (white2Move != wDominating)
				|| (Board.distance(pIndex, domkingIndex) >= 2)) {
			return SCALE_FACTOR_DRAW;
		}
		if (((CoreConstants.FILE_A | CoreConstants.FILE_B | CoreConstants.FILE_G | CoreConstants.FILE_H) & pawns) != 0
				&& (CoreConstants.ROW[rank8] & fileAndAdjacent & otherKing) != 0
				&& (CoreConstants.ROW[rank8] & otherRook) != 0) {
			return SCALE_FACTOR_DRAW;
		}
		return SCALE_FACTOR_DEFAULT;
	}

	private static int endgameKingQueenKingPawn(BitBoard board, boolean wDominating) {
		long row1And2 = wDominating ? CoreConstants.ROW_1 | CoreConstants.ROW_2
				: CoreConstants.ROW_7 | CoreConstants.ROW_8;
		long pawns = board.bitboards[CoreConstants.WHITE_PAWN] | board.bitboards[CoreConstants.BLACK_PAWN];
		int domCol = wDominating ? 0 : 1;
		int enemyCol = domCol == 0 ? 1 : 0;
		long pawnZone = 0;
		if ((CoreConstants.FILE_A & pawns) != 0) {
			pawnZone = CoreConstants.LEFT_FILES[3] & row1And2;
		} else if ((CoreConstants.FILE_C & pawns) != 0) {
			pawnZone = CoreConstants.LEFT_FILES[4] & row1And2;
		} else if ((CoreConstants.FILE_F & pawns) != 0) {
			pawnZone = CoreConstants.RIGHT_FILES[3] & row1And2;
		} else if ((CoreConstants.FILE_H & pawns) != 0) {
			pawnZone = CoreConstants.RIGHT_FILES[4] & row1And2;
		} else {
			return NO_VALUE;
		}
		long domKing = board.bitboards[domCol + CoreConstants.WHITE_KING];
		long enemyKing = board.bitboards[enemyCol + CoreConstants.WHITE_KING];
		int domKingIndex = BitBoard.bitScanForward(domKing);
		int pIndex = BitBoard.bitScanForward(pawns);
		if ((pawnZone & enemyKing) != 0 && Board.distance(domKingIndex, pIndex) >= 1) {
			return DRAW;
		}

		return NO_VALUE;
	}

	private static int endgameKingBishopPawnKingKnight(BitBoard board, boolean wDominating) {
		int domCol = wDominating ? 0 : 1;
		int enemyCol = domCol == 0 ? 1 : 0;
		long domBishop = board.bitboards[domCol + CoreConstants.WHITE_BISHOP];
		long domBishopSquares = ((domBishop & WHITE_SQUARES) != 0) ? WHITE_SQUARES : BLACK_SQUARES;
		long pawns = board.bitboards[CoreConstants.WHITE_PAWN] | board.bitboards[CoreConstants.BLACK_PAWN];
		int index = BitBoard.bitScanForward(pawns);
		long pawnRoute = CoreConstants.ROW_FORWARD[domCol][(int) index / 8] & CoreConstants.FILE[index % 8];
		long otherKing = board.bitboards[CoreConstants.WHITE_KING + enemyCol];
		if ((pawnRoute & otherKing) != 0 && (domBishopSquares & otherKing) == 0) {
			return DRAW;
		}
		return NO_VALUE;
	}

	private static long getBishopMoves(BitBoard board, int index, int side) {
		long bishopBlockers = (board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK])
				& CoreConstants.occupancyMaskBishop[index];
		int lookupIndex = (int) ((bishopBlockers
				* CoreConstants.magicNumbersBishop[index]) >>> CoreConstants.magicShiftBishop[index]);
		long moveSquares = CoreConstants.magicMovesBishop[index][lookupIndex] & ~board.bitboards[side];
		return moveSquares;
	}

	private static int endgameKingBishopPawnKingBishop(BitBoard board, boolean wDominating) {
		int domCol = wDominating ? 0 : 1;
		int enemyCol = domCol == 0 ? 1 : 0;
		long domBishop = board.bitboards[domCol + CoreConstants.WHITE_BISHOP];
		long otherBishop = board.bitboards[enemyCol + CoreConstants.WHITE_BISHOP];
		long domBishopSquares = ((domBishop & WHITE_SQUARES) != 0) ? WHITE_SQUARES : BLACK_SQUARES;
		long pawns = board.bitboards[CoreConstants.WHITE_PAWN] | board.bitboards[CoreConstants.BLACK_PAWN];
		int index = BitBoard.bitScanForward(pawns);
		long pawnRoute = CoreConstants.ROW_FORWARD[domCol][(int) index / 8] & CoreConstants.FILE[index % 8];
		long otherKing = board.bitboards[CoreConstants.WHITE_KING + enemyCol];
		if ((pawnRoute & otherKing) != 0 && (domBishopSquares & otherKing) == 0) {
			return DRAW;
		}
		long otherBishopSquares = ((otherBishop & WHITE_SQUARES) != 0) ? WHITE_SQUARES : BLACK_SQUARES;
		if (domBishopSquares != otherBishopSquares) {
			int otherBishopIndex = BitBoard.bitScanForward(otherBishop);
			if ((otherBishop & pawnRoute) != 0 || (getBishopMoves(board, otherBishopIndex, enemyCol)
					& board.bitboards[enemyCol] & pawnRoute) != 0) {
				return DRAW;
			}
		}
		return NO_VALUE;
	}

	private static int scaleKingRookPawnPawnKingRookPawn(BitBoard board, boolean wDominating) {
		int domCol = wDominating ? 0 : 1;
		int enemyCol = domCol == 0 ? 1 : 0;
		long domPawns = board.bitboards[CoreConstants.WHITE_PAWN + domCol];
		int[] pawnIndices = { 0, 0 };
		int i = 0;
		while (domPawns != 0) {
			pawnIndices[i] = BitBoard.bitScanForward(domPawns);
			domPawns &= domPawns - 1;
			i++;
		}
		long inFrontOfPawn1 = CoreConstants.ROW_FORWARD[domCol][(int) pawnIndices[0] / 8]
				& (CoreConstants.FILE[pawnIndices[0] % 8] | CoreConstants.ADJACENT_FILE[pawnIndices[0] % 8]);
		long inFrontOfPawn2 = CoreConstants.ROW_FORWARD[domCol][(int) pawnIndices[1] / 8]
				& (CoreConstants.FILE[pawnIndices[1] % 8] | CoreConstants.ADJACENT_FILE[pawnIndices[1] % 8]);
		long otherPawn = board.bitboards[CoreConstants.WHITE_PAWN + enemyCol];
		if ((inFrontOfPawn1 & otherPawn) == 0 || (inFrontOfPawn2 & otherPawn) == 0) {
			return SCALE_FACTOR_DEFAULT;
		}
		long otherKing = board.bitboards[CoreConstants.WHITE_KING + enemyCol];
		if ((inFrontOfPawn1 & otherKing) != 0 && (inFrontOfPawn1 & otherKing) != 1) {
			return SCALE_FACTOR_DRAWISH;
		}
		return SCALE_FACTOR_DEFAULT;
	}
}
