package eval;

import core.BitBoard;
import core.CoreConstants;

public class EvalInfo {
	public int[] kingIndex = new int[2];
	public long[] pawnAttacks = new long[2];
	public long[] knightAttacks = new long[2];
	public long[] bishopAttacks = new long[2];
	public long[] rookAttacks = new long[2];
	public long[] queenAttacks = new long[2];
	public long[] attackedSquares = new long[2];
	public long[] attacksFromSquares = new long[64];
	public long[] kingAttacks = new long[2];
	public long pinnedPieces;
	public long pinnedMobility[] = new long[64];
	public long piecesGivingCheck;
	private long[] bishopAttackKing = { 0, 0 };
	private long[] rookAttackKing = { 0, 0 };
	private BitBoard board;

	private void checkPinnerRay(long ray, long mines, long attackSlider) {
		long pinner = ray & attackSlider;
		if (pinner != 0) {
			long pinned = ray & mines;
			pinnedPieces |= pinned;
			pinnedMobility[BitBoard.bitScanForward(pinned)] = ray;
		}
	}

	private void checkPinnerBishop(int kingIndex, long bishopAttacks, long all, long mines, long bishopsOrQueens,
			int side) {
		if ((bishopAttacks & mines) == 0 || (CoreConstants.BISHOP_TABLE[kingIndex] & bishopsOrQueens) == 0) {
			return;
		}
		long xray = getBishopMoves(board, all & ~(mines & bishopAttacks), kingIndex, side);
		if ((xray & ~bishopAttacks & bishopsOrQueens) != 0) {
			int rank = kingIndex / 8;
			int file = kingIndex % 8;
			checkPinnerRay(xray & CoreConstants.ROW_UPWARD[rank] & CoreConstants.LEFT_FILES[file], mines,
					bishopsOrQueens);
			checkPinnerRay(xray & CoreConstants.ROW_UPWARD[rank] & CoreConstants.RIGHT_FILES[file], mines,
					bishopsOrQueens);
			checkPinnerRay(xray & CoreConstants.ROW_DOWNARD[rank] & CoreConstants.LEFT_FILES[file], mines,
					bishopsOrQueens);
			checkPinnerRay(xray & CoreConstants.ROW_DOWNARD[rank] & CoreConstants.RIGHT_FILES[file], mines,
					bishopsOrQueens);
		}
	}

	private void checkPinnerRook(int kingIndex, long rookAttacks, long all, long mines, long rooksOrQueens, int side) {
		if ((rookAttacks & mines) == 0 || (CoreConstants.ROOK_TABLE[kingIndex] & rooksOrQueens) == 0) {
			return;
		}
		long xray = getRookMoves(board, all & ~(mines & rookAttacks), kingIndex, side);
		if ((xray & ~rookAttacks & rooksOrQueens) != 0) {
			int rank = kingIndex / 8;
			int file = kingIndex % 8;
			checkPinnerRay(xray & CoreConstants.ROW_UPWARD[rank], mines, rooksOrQueens);
			checkPinnerRay(xray & CoreConstants.LEFT_FILES[file], mines, rooksOrQueens);
			checkPinnerRay(xray & CoreConstants.ROW_DOWNARD[rank], mines, rooksOrQueens);
			checkPinnerRay(xray & CoreConstants.RIGHT_FILES[file], mines, rooksOrQueens);
		}
	}

	public void generate(BitBoard board) {
		this.board = board;
		kingIndex[0] = BitBoard.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		kingIndex[1] = BitBoard.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		long square = 1L;
		long all = board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK];
		long pawns = board.bitboards[CoreConstants.WHITE_PAWN] | board.bitboards[CoreConstants.BLACK_PAWN];
		long knights = board.bitboards[CoreConstants.WHITE_KNIGHT] | board.bitboards[CoreConstants.BLACK_KNIGHT];
		long bishops = board.bitboards[CoreConstants.WHITE_BISHOP] | board.bitboards[CoreConstants.BLACK_BISHOP];
		long rooks = board.bitboards[CoreConstants.WHITE_ROOK] | board.bitboards[CoreConstants.BLACK_ROOK];
		long queens = board.bitboards[CoreConstants.WHITE_QUEEN] | board.bitboards[CoreConstants.BLACK_QUEEN];
		long kings = board.bitboards[CoreConstants.WHITE_KING] | board.bitboards[CoreConstants.BLACK_KING];
		long whites = board.bitboards[CoreConstants.WHITE];
		long blacks = board.bitboards[CoreConstants.BLACK];

		bishopAttackKing[0] = getBishopMoves(board, all, kingIndex[0], 0);
		checkPinnerBishop(kingIndex[0], bishopAttackKing[0], all, whites, (bishops | queens) & blacks, 0);
		bishopAttackKing[1] = getBishopMoves(board, all, kingIndex[1], 1);
		checkPinnerBishop(kingIndex[1], bishopAttackKing[1], all, blacks, (bishops | queens) & whites, 1);

		rookAttackKing[0] = getRookMoves(board, all, kingIndex[0], 0);
		checkPinnerRook(kingIndex[0], rookAttackKing[0], all, whites, (rooks | queens) & blacks, 0);
		rookAttackKing[1] = getRookMoves(board, all, kingIndex[1], 1);
		checkPinnerRook(kingIndex[1], rookAttackKing[1], all, blacks, (rooks | queens) & whites, 1);

		for (int index = 0; index < 64; index++) {
			if ((square & all) != 0) {
				long moves = 0;
				int color = (board.bitboards[CoreConstants.WHITE] & square) != 0 ? CoreConstants.WHITE
						: CoreConstants.BLACK;
				long pinnedSquares = (square & pinnedPieces) != 0 ? pinnedMobility[index]
						: EvalConstants.WHITE_SQUARES | EvalConstants.BLACK_SQUARES;
				if ((square & pawns) != 0) {
					moves = CoreConstants.PAWN_ATTACKS_TABLE[color][index];
					pawnAttacks[color] |= moves & pinnedSquares;
				} else if ((square & knights) != 0) {
					moves = CoreConstants.KNIGHT_TABLE[index];
					knightAttacks[color] |= moves & pinnedSquares;
				} else if ((square & bishops) != 0) {
					moves = getBishopMoves(board, all, index, color);
					bishopAttacks[color] |= moves & pinnedSquares;
				} else if ((square & rooks) != 0) {
					moves = getRookMoves(board, all, index, color);
					rookAttacks[color] |= moves & pinnedSquares;
				} else if ((square & queens) != 0) {
					moves = (getBishopMoves(board, all, index, color) | getRookMoves(board, all, index, color));
					queenAttacks[color] |= moves & pinnedSquares;
				} else if ((square & kings) != 0) {
					moves = CoreConstants.KING_TABLE[index];
					kingAttacks[color] |= moves;
				}
				attacksFromSquares[index] = moves & pinnedSquares;
			} else {
				attacksFromSquares[index] = 0;
			}
			square <<= 1;
		}
		attackedSquares[0] = pawnAttacks[0] | knightAttacks[0] | bishopAttacks[0] | rookAttacks[0] | queenAttacks[0]
				| kingAttacks[0];
		attackedSquares[1] = pawnAttacks[1] | knightAttacks[1] | bishopAttacks[1] | rookAttacks[1] | queenAttacks[1]
				| kingAttacks[1];
	}

	private long getBishopMoves(BitBoard board, long all, int index, int side) {
		if (index != -1) {
			long bishopBlockers = all & CoreConstants.occupancyMaskBishop[index];
			int lookupIndex = (int) ((bishopBlockers
					* CoreConstants.magicNumbersBishop[index]) >>> CoreConstants.magicShiftBishop[index]);
			long moveSquares = CoreConstants.magicMovesBishop[index][lookupIndex] & ~board.bitboards[side];
			return moveSquares;
		} else {
			return 0;
		}
	}

	private long getRookMoves(BitBoard board, long all, int index, int side) {
		if (index != -1) {
			long rookBlockers = all & CoreConstants.occupancyMaskRook[index];
			int lookupIndex = (int) ((rookBlockers
					* CoreConstants.magicNumbersRook[index]) >>> CoreConstants.magicShiftRook[index]);
			long moveSquares = CoreConstants.magicMovesRook[index][lookupIndex] & ~board.bitboards[side];
			return moveSquares;
		} else {
			return 0;
		}
	}
}
