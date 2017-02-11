package eval;

import core.BitBoard;
import core.CoreConstants;

public class Endgame extends EvalConstants {
	public static int evaluate(BitBoard board, int[] scaleFactor, int wPawns, int bPawns, int wKnights, int bKnights,
			int wBishops, int bBishops, int wRooks, int bRooks, int wQueens, int bQueens) {
		scaleFactor[0] = SCALE_FACTOR_DEFAULT;
		int wNonPawnMat = wKnights + wBishops + wRooks + wQueens;
		int bNonPawnMat = bKnights + bBishops + bRooks + bQueens;
		int wMat = wNonPawnMat + wPawns;
		int bMat = bNonPawnMat + bPawns;

		if (wPawns == 0 && bPawns == 0) {
			if ((bMat == 0 && wMat == 2 && wKnights == 1 && wBishops == 1)
					|| (wMat == 0 && bMat == 2 && bKnights == 1 && bBishops == 1)) {
				return Endgame.endgameKBNK(board, wMat > bMat);
			}
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
				return Endgame.endgameKPK(board, wMat > bMat);
			}
			if ((wNonPawnMat == 1 && bNonPawnMat == 0) || (wNonPawnMat == 0 && bNonPawnMat == 1)) {
				if ((wQueens == 1 && bPawns == 1) || (bQueens == 1 && wPawns == 1)) {
					return endgameKQKP(board, wQueens > bQueens);
				}
			}
			if (wNonPawnMat == 1 && bNonPawnMat == 1) {
				if (wRooks == 1 && bRooks == 1) {
					scaleFactor[0] = scaleKRPKR(board, wPawns > bPawns);
				}
				if (wBishops == 1 && bBishops == 1) {
					return endgameKBPKB(board, wPawns > bPawns);
				}
				if ((wBishops == 1 && wPawns == 1 && bKnights == 1)
						|| (bBishops == 1 && bPawns == 1 && wKnights == 1)) {
					return endgameKBPKN(board, wPawns > bPawns);
				}
			}
		}
		if (bMat == 0 && (wBishops >= 2 || wRooks > 0 || wQueens > 0)
				|| wMat == 0 && (bBishops >= 2 || bRooks > 0 || bQueens > 9)) {
			return Endgame.endgameKXK(board, wMat > bMat, wKnights + bKnights, wBishops + bBishops, wRooks + bRooks,
					wQueens + bQueens);
		}
		if (wRooks == 1 && bRooks == 1 && ((wPawns == 2 && bPawns == 1) || (wPawns == 1 && bPawns == 2))) {
			scaleFactor[0] = scaleKRPPKRP(board, wPawns > bPawns);
		}
		if (scaleFactor[0] == SCALE_FACTOR_DRAW) {
			return DRAW;
		}
		return NO_VALUE;
	}

	private static int endgameKXK(BitBoard board, boolean wDominating, int knights, int bishops, int rooks,
			int queens) {
		int wKingIndex = BitBoard.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		int bKingIndex = BitBoard.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		int value = KNOWN_WIN + (knights * PIECE_VALUE[KNIGHT]) + (bishops * PIECE_VALUE[BISHOP])
				+ (rooks * PIECE_VALUE[ROOK]) + (queens * PIECE_VALUE[QUEEN])
				+ CLOSER_SQUARES[Board.distance(wKingIndex, bKingIndex)]
				+ (wDominating ? TO_CORNERS[bKingIndex] : TO_COLOR_CORNERS[wKingIndex]);
		return (wDominating ? value : -value);
	}

	private static int endgameKBNK(BitBoard board, boolean wDominating) {
		int wKingIndex = BitBoard.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		int bKingIndex = BitBoard.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		long bishops = board.bitboards[CoreConstants.WHITE_BISHOP] | board.bitboards[CoreConstants.BLACK_BISHOP];
		if ((bishops & BLACK_SQUARES) != 0) {
			wKingIndex = Board.flipHorizontalIndex(wKingIndex);
			bKingIndex = Board.flipHorizontalIndex(bKingIndex);
		}
		int value = KNOWN_WIN + CLOSER_SQUARES[Board.distance(wKingIndex, bKingIndex)]
				+ (wDominating ? TO_CORNERS[bKingIndex] : TO_CORNERS[wKingIndex]);
		return (wDominating ? value : -value);
	}

	private static int endgameKPK(BitBoard board, boolean wDominating) {
		Bitbase bitbase = new Bitbase();
		if (!bitbase.probe(board)) {
			return DRAW;
		}
		return wDominating
				? KNOWN_WIN + PAWN + (int) BitBoard.bitScanForward(board.bitboards[CoreConstants.WHITE_PAWN]) / 8
				: -KNOWN_WIN - PIECE_VALUE[PAWN]
						- (7 - (int) BitBoard.bitScanForward(board.bitboards[CoreConstants.BLACK_PAWN]) / 8);
	}

	private static int scaleKRPKR(BitBoard board, boolean wDominating) {
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

	private static int endgameKQKP(BitBoard board, boolean wDominating) {
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

	private static int endgameKBPKN(BitBoard board, boolean wDominating) {
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

	private static int endgameKBPKB(BitBoard board, boolean wDominating) {
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

	private static int scaleKRPPKRP(BitBoard board, boolean wDominating) {
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
		if((inFrontOfPawn1 & otherPawn) == 0 || (inFrontOfPawn2& otherPawn) == 0){
			return SCALE_FACTOR_DEFAULT;
		}
		long otherKing = board.bitboards[CoreConstants.WHITE_KING + enemyCol];
		if((inFrontOfPawn1 & otherKing) != 0 && (inFrontOfPawn1 & otherKing) != 1){
			return SCALE_FACTOR_DRAWISH;
		}
		return SCALE_FACTOR_DEFAULT;
	}
}
