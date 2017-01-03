package eval;

import core.BitBoard;
import core.CoreConstants;

public class AltEval extends AltEvalConstants {
	public int evaluate(BitBoard board, int color){
		return 0;
	}
	public int mat(BitBoard board){
		int whitePawns = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_PAWN]);
		int blackPawns = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_PAWN]);
		int whiteKnights = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_KNIGHT]);
		int blackKnights = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_KNIGHT]);
		int whiteBishops = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_BISHOP]);
		int blackBishops = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_BISHOP]);
		int whiteRooks = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_ROOK]);
		int blackRooks = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_ROOK]);
		int whiteQueens = BitBoard.hammingWeight(board.bitboards[CoreConstants.WHITE_QUEEN]);
		int blackQueens = BitBoard.hammingWeight(board.bitboards[CoreConstants.BLACK_QUEEN]);
		return 0;
	}
	public int pawnStruct(BitBoard board){
		return 0;
	}
	public int pieceSquare(BitBoard board){
		return 0;
	}
	public int mobility(BitBoard board){
		return 0;
	}
	public int kingSafety(BitBoard board){
		return 0;
	}
	
}
