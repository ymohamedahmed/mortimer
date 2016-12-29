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
		int wKingIndex = board.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		int bKingIndex = board.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		int value = KNOWN_WIN + (knights * PIECE_VALUE[KNIGHT]) + (bishops * PIECE_VALUE[BISHOP])
				+ (rooks * PIECE_VALUE[ROOK]) + (queens * PIECE_VALUE[QUEEN])
				+ CLOSER_SQUARES[Board.distance(wKingIndex, bKingIndex)]
				+ (wDominating ? TO_CORNERS[bKingIndex] : TO_COLOR_CORNERS[wKingIndex]);
		return (wDominating ? value : -value);
	}

	private static int endgameKBNK(BitBoard board, boolean wDominating) {
		int wKingIndex = board.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		int bKingIndex = board.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		return 0;
	}

	private static int endgameKPK(BitBoard board, boolean wDominating) {
		Bitbase bitbase = new Bitbase();
		if (!bitbase.probe(board)) {
			return DRAW;
		}
		return wDominating
				? KNOWN_WIN + PAWN + (int) board.bitScanForward(board.bitboards[CoreConstants.WHITE_PAWN]) / 8
				: -KNOWN_WIN - PIECE_VALUE[PAWN]
						- (7 - (int) board.bitScanForward(board.bitboards[CoreConstants.BLACK_PAWN]) / 8);
	}

	private static int scaleKRPKR(BitBoard board, boolean wDominating) {
		return 0;
	}

	private static int endgameKQKP(BitBoard board, boolean wDominating) {
		return 0;
	}

	private static int endgameKBPKN(BitBoard board, boolean wDominating) {
		return 0;
	}

	private static int endgameKBPKB(BitBoard board, boolean wDominating) {
		return 0;
	}

	private static int scaleKRPPKRP(BitBoard board, boolean wDominating) {
		return 0;
	}
}
