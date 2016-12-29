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
	private int[] scaleFactor = { 0 };

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

		int endgameValue = Endgame.evaluate(board, scaleFactor, whitePawns, blackPawns, whiteKnights, blackKnights,
				whiteBishops, blackBishops, whiteRooks, blackRooks, whiteQueens, blackQueens);
		if (endgameValue != NO_VALUE) {
			return endgameValue;
		}
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
		long pawns = whitePawnsBoard | blackPawnsBoard;
		EvalInfo ei = new EvalInfo();
		ei.generate(board);
		if (gamePhase > 0) {
			long whiteSafe = WHITE_SPACE & ~ei.pawnAttacks[1] & (~ei.attackedSquares[1] | ei.attackedSquares[0]);
			long blackSafe = BLACK_SPACE & ~ei.pawnAttacks[0] & (~ei.attackedSquares[0] | ei.attackedSquares[1]);
			long whiteBehindPawn = ((whitePawnsBoard >>> 8) | (whitePawnsBoard >>> 16) | (whitePawnsBoard >>> 24));
			long blackBehindPawns = ((blackPawnsBoard << 8) | (blackPawnsBoard << 16) | (blackPawnsBoard << 24));
			spatial[0] = SPACE * (((board.hammingWeight(whiteSafe) + board.hammingWeight(whiteSafe & whiteBehindPawn))
					* (whiteKnights + whiteBishops)) / 4);
			spatial[1] = SPACE * (((board.hammingWeight(blackSafe) + board.hammingWeight(blackSafe & blackBehindPawns))
					* (blackKnights + blackBishops)) / 4);
		} else {
			spatial[0] = 0;
			spatial[1] = 1;
		}
		pawnCanAttack[0] = ei.pawnAttacks[0];
		pawnCanAttack[1] = ei.pawnAttacks[1];
		for (int i = 0; i < 5; i++) {
			whitePawnsBoard = whitePawnsBoard << 8;
			whitePawnsBoard &= ~((board.bitboards[CoreConstants.BLACK_PAWN]) | ei.pawnAttacks[1]);
			blackPawnsBoard = blackPawnsBoard >>> 8;
			blackPawnsBoard &= ~((board.bitboards[CoreConstants.WHITE_PAWN]) | ei.pawnAttacks[0]);
			if (whitePawnsBoard == 0 && blackPawnsBoard == 0) {
				break;
			}
			pawnCanAttack[0] |= ((whitePawnsBoard & ~CoreConstants.FILE_A) << 9)
					| ((whitePawnsBoard & ~CoreConstants.FILE_H) << 7);
			pawnCanAttack[1] |= ((blackPawnsBoard & ~CoreConstants.FILE_H) >>> 9)
					| ((blackPawnsBoard & ~CoreConstants.FILE_A) >>> 7);
		}
		attacks[0] = evalAttacks(board, ei, 0, board.bitboards[CoreConstants.BLACK]);
		attacks[1] = evalAttacks(board, ei, 1, board.bitboards[CoreConstants.WHITE]);
		kingZone[0] = CoreConstants.KING_TABLE[ei.kingIndex[0]];
		kingZone[0] |= (kingZone[0] << 8);
		kingZone[1] = CoreConstants.KING_TABLE[ei.kingIndex[1]];
		kingZone[1] |= (kingZone[1] >>> 8);
		long all = board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK];
		long pieceAttacks, safeAttacks, kingAttacks;
		long square = 1;
		for (int index = 0; index < 64; index++) {
			if ((square & all) != 0) {
				boolean isWhite = ((board.bitboards[CoreConstants.WHITE] & square) != 0);
				int col = isWhite ? 0 : 1;
				int enemy = isWhite ? 1 : 0;
				long mines = isWhite ? board.bitboards[CoreConstants.WHITE] : board.bitboards[CoreConstants.BLACK];
				long others = isWhite ? board.bitboards[CoreConstants.BLACK] : board.bitboards[CoreConstants.WHITE];
				int pieceIndex = isWhite ? index : 63 - index;
				int rank = (int) index / 8;
				int file = index % 8;
				int relativeRank = isWhite ? rank : 7 - rank;
				pieceAttacks = ei.attacksFromSquares[index];
				if ((square & pawns) != 0) {
					pieceSquare[0] += POS_PAWN[pieceIndex];
					long myPawns = pawns & mines;
					long otherPawns = pawns & others;
					long adjacentFiles = CoreConstants.ADJACENT_FILE[file];
					long ranksForward = CoreConstants.ROW_FORWARD[col][rank];
					long pawnFile = CoreConstants.FILE[file];
					long routeToPromotion = pawnFile & ranksForward;
					long otherPawnsAheadAdjacent = ranksForward & adjacentFiles & otherPawns;
					long pushSquare = isWhite ? square << 8 : square >>> 8;
					boolean suported = (square & ei.pawnAttacks[col]) != 0;
					boolean doubled = (myPawns & routeToPromotion) != 0;
					boolean opposed = (otherPawns & routeToPromotion) != 0;
					boolean passed = !doubled && !opposed && otherPawnsAheadAdjacent == 0;
					if (!passed) {
						long myPawnsAheadAdjacent = ranksForward & adjacentFiles & myPawns;
						long myPawnsBesideAndBehindAdjacent = CoreConstants.ROW_BACKWARD_INCLUSIVE[col][rank]
								& adjacentFiles & myPawns;
						boolean isolated = (myPawns & adjacentFiles) == 0;
						boolean candidate = !doubled && !opposed
								&& (((otherPawnsAheadAdjacent & ~pieceAttacks) == 0)
										|| (board.hammingWeight(myPawnsBesideAndBehindAdjacent) >= board
												.hammingWeight(otherPawnsAheadAdjacent & ~pieceAttacks)));
						/*boolean backward = !isolated && !candidate && myPawnsBesideAndBehindAdjacent == 0
								&& (pieceAttacks & otherPawns) == 0 && (CoreConstants.ROW_BACKWARD_INCLUSIVE[col]);*/
					}
				}
			}
		}
		return color * 0;
	}

	private int evalAttacks(BitBoard board, EvalInfo ei, int color, long enemy) {
		return 0;
	}

	public int open(int phase) {
		return (phase + 0x8000) >> 16;
	}

	public int end(int phase) {
		return (short) (phase * 0xffff);
	}
}
