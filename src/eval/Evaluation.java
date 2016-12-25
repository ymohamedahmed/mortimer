package eval;

import core.BitBoard;
import core.CoreConstants;

public class Evaluation {

	public int evaluate(BitBoard board, int color) {
		return color * (material(board) + positional(board) + mobility(board) + space(board) + attacks(board)
				+ pawnStructure(board) + passedPawns(board));
	}

	private int material(BitBoard board) {
		int whitePawns = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_PAWN]);
		int whiteKnights = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_KNIGHT]);
		int whiteBishops = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_BISHOP]);
		int whiteRooks = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_ROOK]);
		int whiteQueens = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_QUEEN]);

		int blackPawns = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_PAWN]);
		int blackKnights = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_KNIGHT]);
		int blackBishops = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_BISHOP]);
		int blackRooks = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_ROOK]);
		int blackQueens = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_QUEEN]);

		int pawnScore = EvalConstants.PIECE_VALUE[EvalConstants.PAWN] * (whitePawns - blackPawns);
		int knightScore = EvalConstants.PIECE_VALUE[EvalConstants.KNIGHT] * (whiteKnights - blackKnights);
		int bishopScore = EvalConstants.PIECE_VALUE[EvalConstants.BISHOP] * (whiteBishops - blackBishops);
		int rookScore = EvalConstants.PIECE_VALUE[EvalConstants.ROOK] * (whiteRooks - blackRooks);
		int queenScore = EvalConstants.PIECE_VALUE[EvalConstants.QUEEN] * (whiteQueens - blackQueens);
		int bishopPairScore = ((whiteBishops == 2) ? EvalConstants.BISHOP_PAIR : 0)
				- ((blackBishops == 2) ? EvalConstants.BISHOP_PAIR : 0);

		int nonPawnMaterial = end(whiteKnights + whiteBishops + whiteRooks + whiteQueens + blackKnights + blackBishops
				+ blackRooks + blackQueens);
		int gamePhase = nonPawnMaterial >= EvalConstants.MAT_MIDGAME_MAX ? EvalConstants.PHASE_MIDGAME
				: (nonPawnMaterial <= EvalConstants.MAT_ENDGAME_MIN) ? EvalConstants.PHASE_ENDGAME
						: ((nonPawnMaterial - EvalConstants.MAT_ENDGAME_MIN) * EvalConstants.PHASE_MIDGAME)
								/ (EvalConstants.MAT_MIDGAME_MAX - EvalConstants.MAT_ENDGAME_MIN);
		return pawnScore + knightScore + bishopScore + rookScore + queenScore + bishopPairScore;
	}

	private int positional(BitBoard board) {
		return 0;
	}

	private int mobility(BitBoard board) {
		return 0;
	}

	private int space(BitBoard board) {
		return 0;
	}

	private int attacks(BitBoard board) {
		return 0;
	}

	private int pawnStructure(BitBoard board) {
		return 0;
	}

	private int passedPawns(BitBoard board) {
		return 0;
	}

	public int open(int phase) {
		return (phase + 0x8000) >> 16;
	}

	public int end(int phase) {
		return (short) (phase * 0xffff);
	}
}
