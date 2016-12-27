package eval;

import core.BitBoard;
import core.CoreConstants;
import core.MoveGen;

public class Attacks {
	// https://github.com/albertoruibal/carballo/blob/master/core/src/main/java/com/alonsoruibal/chess/bitboard/AttacksInfo.java
	private BitBoard board;

	public long[] attackedSquaresPinned = { 0, 0 };
	public long[] attackedSquare = { 0, 0 };
	public long[] attacksFromSquare = { 0, 0 };
	public long[] pawnAttacks = { 0, 0 };
	public long[] knightAttacks = { 0, 0 };
	public long[] bishopAttacks = { 0, 0 };
	public long[] rookAttacks = { 0, 0 };
	public long[] queenAttacks = { 0, 0 };
	public long[] kingAttacks = { 0, 0 };
	public int[] kingIndex = { 0, 0 };
	public long[] pinnedMobility = new long[64];
	public long[] bishopAttackKing = { 0, 0 };
	public long[] rookAttackKing = { 0, 0 };
	public long[] mayPin = { 0, 0 };
	public long piecesCheck = 0;
	public long interposeCheckSquare = 0;
	public long pinnedPieces = 0;

	public void generate(BitBoard board) {
		this.board = board;
		MoveGen moveGen = new MoveGen();
		long rooks = board.bitboards[CoreConstants.WHITE_ROOK] | board.bitboards[CoreConstants.BLACK_ROOK];
		long bishops = board.bitboards[CoreConstants.WHITE_BISHOP] | board.bitboards[CoreConstants.BLACK_BISHOP];
		long queens = board.bitboards[CoreConstants.WHITE_QUEEN] | board.bitboards[CoreConstants.BLACK_QUEEN];
		long all = board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK];
		long mines = board.bitboards[board.toMove];
		long king = board.bitboards[CoreConstants.WHITE_KING + board.toMove];
		kingIndex[0] = board.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		kingIndex[1] = board.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		bishopAttackKing[0] = moveGen.getBishopAttacks(kingIndex[0], all);
		checkPinnerBishop(kingIndex[0], bishopAttackKing[0], all, board.bitboards[CoreConstants.WHITE],
				(bishops | queens) & board.bitboards[CoreConstants.BLACK]);
		bishopAttackKing[1] = bbAttacks.getBishopAttacks(kingIndex[1], all);
		checkPinnerBishop(kingIndex[1], bishopAttackKing[1], all, board.bitboards[CoreConstants.BLACK],
				(bishops | queens) & board.bitboards[CoreConstants.WHITE]);

		rookAttackKing[0] = bbAttacks.getRookAttacks(kingIndex[0], all);
		checkPinnerRook(kingIndex[0], rookAttackKing[0], all, board.bitboards[CoreConstants.WHITE],
				(rooks | queens) & board.bitboards[CoreConstants.BLACK]);
		rookAttackKing[1] = bbAttacks.getRookAttacks(kingIndex[B], all);
		checkPinnerRook(kingIndex[1], rookAttackKing[1], all, board.bitboards[CoreConstants.BLACK],
				(rooks | queens) & board.bitboards[CoreConstants.WHITE]);

		long pieceAttacks;
	}

	private void checkPinnerRay(long ray, long mines, long attackSlider) {
		long pinner = ray & attackSlider;
		if (pinner != 0) {
			long pinned = ray & mines;
			pinnedPieces |= pinned;
			pinnedMobility[board.bitScanForward(pinned)] = ray;
		}
	}

	private void checkPinnerBishop(int kingIndex, long bishopAttacks, long all, long mines, long otherBishopQueens) {

	}

	private void checkPinnerRook(int kingIndex, long rookAttacks, long all, long mines, long otherRookQueens) {

	}
}
