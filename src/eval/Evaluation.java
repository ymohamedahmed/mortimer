package eval;

import core.BitBoard;
import core.CoreConstants;
import core.MoveGen;

public class Evaluation extends EvalConstants {
	// Evaluation conditions
	// 0th index is white, 1st index is black
	private int[] pawnMat = { 0, 0 };
	private int[] nonPawnMat = { 0, 0 };
	private int[] pieceSquare = { 0, 0 };
	private int[] spatial = { 0, 0 };
	private int[] positional = { 0, 0 };
	private int[] attacks = { 0, 0 };
	private int[] kingAttackedCount = { 0, 0 };
	private int[] kingSafety = { 0, 0 };
	private int[] pawnStruct = { 0, 0 };
	private int[] passedPawns = { 0, 0 };
	private long[] pawnCanAttack = { 0, 0 };
	private long[] mobilitySquares = { 0, 0 };
	private long[] kingZone = { 0, 0 };

	public int evaluate(MoveGen moveGen, BitBoard board, int color) {
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

		pawnMat[0] = whitePawns * PIECE_VALUE_PHASE[PAWN];
		pawnMat[1] = blackPawns * PAWN;
		nonPawnMat[0] = (whiteKnights * PIECE_VALUE_PHASE[KNIGHT]) + (whiteBishops * PIECE_VALUE_PHASE[BISHOP])
				+ (whiteRooks * PIECE_VALUE_PHASE[ROOK]) + (whiteQueens * PIECE_VALUE_PHASE[QUEEN])
				+ ((whiteBishops == 2) ? BISHOP_PAIR : 0);
		nonPawnMat[1] = (blackKnights * PIECE_VALUE_PHASE[KNIGHT]) + (blackBishops * PIECE_VALUE_PHASE[BISHOP])
				+ (blackRooks * PIECE_VALUE_PHASE[ROOK]) + (blackQueens * PIECE_VALUE_PHASE[QUEEN])
				+ ((blackBishops == 2) ? BISHOP_PAIR : 0);
		int nonPawnMaterial = end(nonPawnMat[0] + nonPawnMat[1]);
		int gamePhase = nonPawnMaterial >= MAT_MIDGAME_MAX ? PHASE_MIDGAME
				: (nonPawnMaterial <= MAT_ENDGAME_MIN) ? PHASE_ENDGAME
						: ((nonPawnMaterial - MAT_ENDGAME_MIN) * PHASE_MIDGAME) / (MAT_MIDGAME_MAX - MAT_ENDGAME_MIN);
		mobilitySquares[0] = ~board.bitboards[CoreConstants.WHITE];
		mobilitySquares[1] = ~board.bitboards[CoreConstants.BLACK];
		long whitePawnsBoard = board.bitboards[CoreConstants.WHITE_PAWN];
		long blackPawnsBoard = board.bitboards[CoreConstants.BLACK_PAWN];
		if(gamePhase > 0){
			
		}else{
			
		}
		return color * 0;
	}

	public int open(int phase) {
		return (phase + 0x8000) >> 16;
	}

	public int end(int phase) {
		return (short) (phase * 0xffff);
	}
}
