package eval;

import core.BitBoard;
import core.CoreConstants;

public class Evaluation extends EvalConstants {

	public static double evaluate(BitBoard board, int color) {
		// Calculate the number of each type of piece per side $\label{code:evalFunction}$
		int whiteKing = board.checkmate(CoreConstants.WHITE, CoreConstants.WHITE) ? 0 : 1;
		int blackKing = board.checkmate(CoreConstants.BLACK, CoreConstants.BLACK) ? 0 : 1;
		int whiteQueens = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_QUEEN]);
		int blackQueens = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_QUEEN]);
		int whiteRooks = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_ROOK]);
		int blackRooks = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_ROOK]);
		int whiteBishops = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_BISHOP]);
		int blackBishops = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_BISHOP]);
		int whiteKnights = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_KNIGHT]);
		int blackKnights = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_KNIGHT]);
		int whitePawns = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_PAWN]);
		int blackPawns = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_PAWN]);
		// Workout the difference between how many pieces each side has per type
		int kDiff = whiteKing - blackKing;
		int qDiff = whiteQueens - blackQueens;
		int rDiff = whiteRooks - blackRooks;
		int bDiff = whiteBishops - blackBishops;
		int nDiff = whiteKnights - blackKnights;
		int pDiff = whitePawns - blackPawns;

		// Work out the difference between the material score of white and black
		int materialScore = (20000 * kDiff) + (900 * qDiff) + (500 * rDiff) + (330 * bDiff)
				+ (320 * nDiff) + (100 * pDiff);

		// Workout if we have reached the endgame yet
		boolean ending = false;
		int totalNonQueenPiecesWhite = whiteRooks + whiteBishops + whiteKnights + whitePawns;
		int totalNonQueenPiecesBlack = blackRooks + blackBishops + blackKnights + blackPawns;
		int minorPiecesWhite = whiteBishops + whiteKnights + whitePawns;
		int minorPiecesBlack = blackBishops + blackKnights + blackPawns;
		// Endgame if:
		// both sides have no queens or
		// every side with a queen has no other piece or one minor piece
		if ((whiteQueens == 0 && blackQueens == 0)
				|| (whiteQueens == 0 && blackQueens == 1
						&& (minorPiecesBlack == 1 || totalNonQueenPiecesBlack == 0))
				|| (blackQueens == 0 && whiteQueens == 1
						&& (minorPiecesWhite == 1 || totalNonQueenPiecesWhite == 0))
				|| (blackQueens == 1 && (minorPiecesBlack == 1 || totalNonQueenPiecesBlack == 0)
						&& whiteQueens == 1)
						&& (minorPiecesWhite == 1 || totalNonQueenPiecesWhite == 0)) {
			ending = true;
		}
		// Calculate positional scores for each piece on the board
		int posScore = 0;
		for (int index = 0; index < 64; index++) {
			int piece = board.getBoardArray()[index];
			if (piece != CoreConstants.EMPTY) {
				// White pieces have even IDs
				if (piece % 2 == 0) {
					if (piece == CoreConstants.WHITE_PAWN) {
						posScore += pawnTable[index];
					} else if (piece == CoreConstants.WHITE_BISHOP) {
						posScore += bishopTable[index];
					} else if (piece == CoreConstants.WHITE_KNIGHT) {
						posScore += knightTable[index];
					} else if (piece == CoreConstants.WHITE_ROOK) {
						posScore += rookTable[index];
					} else if (piece == CoreConstants.WHITE_QUEEN) {
						posScore += queenTable[index];
					} else if (piece == CoreConstants.WHITE_KING) {
						if (ending) {
							posScore += kingEndTable[index];
						} else {
							posScore += kingMiddleTable[index];
						}
					}
					// Black piece have odd IDs
					// Subtract black scores from total
				} else {
					int mirrorIndex = 56 + (2 * (index % 8)) - index;
					if (piece == CoreConstants.BLACK_PAWN) {
						posScore -= pawnTable[mirrorIndex];
					} else if (piece == CoreConstants.BLACK_BISHOP) {
						posScore -= bishopTable[mirrorIndex];
					} else if (piece == CoreConstants.BLACK_KNIGHT) {
						posScore -= knightTable[mirrorIndex];
					} else if (piece == CoreConstants.BLACK_ROOK) {
						posScore -= rookTable[mirrorIndex];
					} else if (piece == CoreConstants.BLACK_QUEEN) {
						posScore -= queenTable[mirrorIndex];
					} else if (piece == CoreConstants.BLACK_KING) {
						if (ending) {
							posScore -= kingEndTable[mirrorIndex];
						} else {
							posScore -= kingMiddleTable[mirrorIndex];
						}
					}
				}
			}
		}
		return color * (materialScore + posScore);
	}
	public static double fastEval(BitBoard board, int colorFactor){
		int whiteQueens = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_QUEEN]);
		int blackQueens = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_QUEEN]);
		int whiteRooks = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_ROOK]);
		int blackRooks = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_ROOK]);
		int whiteBishops = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_BISHOP]);
		int blackBishops = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_BISHOP]);
		int whiteKnights = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_KNIGHT]);
		int blackKnights = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_KNIGHT]);
		int whitePawns = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.WHITE_PAWN]);
		int blackPawns = BitBoard.hammingWeight(board.getBitBoards()[CoreConstants.BLACK_PAWN]);
		// Workout the difference between how many pieces each side has per type
		int qDiff = whiteQueens - blackQueens;
		int rDiff = whiteRooks - blackRooks;
		int bDiff = whiteBishops - blackBishops;
		int nDiff = whiteKnights - blackKnights;
		int pDiff = whitePawns - blackPawns;

		// Work out the difference between the material score of white and black
		int materialScore = (900 * qDiff) + (500 * rDiff) + (330 * bDiff)
				+ (320 * nDiff) + (100 * pDiff);
		return colorFactor * materialScore;
	}
}
