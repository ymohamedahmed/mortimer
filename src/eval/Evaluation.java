package eval;

import core.BitBoard;
import core.CoreConstants;

public class Evaluation {

	public int evaluate(BitBoard board, int color) {
		return color * material(board);
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

		return pawnScore + knightScore + bishopScore + rookScore + queenScore;
	}

}
