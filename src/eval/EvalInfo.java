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

	public void generate(BitBoard board) {
		kingIndex[0] = board.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]);
		kingIndex[1] = board.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]);
		int index = 0;
		long square = 1L;
		long all = board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK];
		long pawns = board.bitboards[CoreConstants.WHITE_PAWN] | board.bitboards[CoreConstants.BLACK_PAWN];
		long knights = board.bitboards[CoreConstants.WHITE_KNIGHT] | board.bitboards[CoreConstants.BLACK_KNIGHT];
		long bishops = board.bitboards[CoreConstants.WHITE_BISHOP] | board.bitboards[CoreConstants.BLACK_BISHOP];
		long rooks = board.bitboards[CoreConstants.WHITE_ROOK] | board.bitboards[CoreConstants.BLACK_ROOK];
		long queens = board.bitboards[CoreConstants.WHITE_QUEEN] | board.bitboards[CoreConstants.BLACK_QUEEN];
		long kings = board.bitboards[CoreConstants.WHITE_KING] | board.bitboards[CoreConstants.BLACK_KING];
		while (index < 64) {
			if ((square & all) != 0) {
				long moves = 0;
				int color = (board.bitboards[CoreConstants.WHITE] & square) != 0 ? CoreConstants.WHITE
						: CoreConstants.BLACK;
				int enemy = color == 0 ? 1 : 0;
				if ((square & pawns) != 0) {
					moves = (CoreConstants.PAWN_ATTACKS_TABLE[color][index] & board.bitboards[enemy]);
					pawnAttacks[color] |= moves;
				} else if ((square & knights) != 0) {
					moves = (CoreConstants.KNIGHT_TABLE[index] & board.bitboards[enemy]);
					knightAttacks[color] |= moves;
				} else if ((square & bishops) != 0) {
					moves = (getBishopMoves(board, index, color) & board.bitboards[enemy]);
					bishopAttacks[color] |= moves;
				} else if ((square & rooks) != 0) {
					moves = (getRookMoves(board, index, color) & board.bitboards[enemy]);
					rookAttacks[color] |= moves;
				} else if ((square & queens) != 0) {
					moves = (getBishopMoves(board, index, color) | getRookMoves(board, index, color))
							& board.bitboards[enemy];
					queenAttacks[color] |= moves;
				}else if((square & kings) != 0){
					moves = CoreConstants.KING_TABLE[index] & board.bitboards[enemy];
					kingAttacks[color] |= moves;
				}
				attacksFromSquares[index] = moves;
			} else {
				attacksFromSquares[index] = 0;
			}
			index++;
			square <<= 1;
		}
		attackedSquares[0] = pawnAttacks[0] | knightAttacks[0] | bishopAttacks[0] | rookAttacks[0] | queenAttacks[0] | kingAttacks[0];
		attackedSquares[1] = pawnAttacks[1] | knightAttacks[1] | bishopAttacks[1] | rookAttacks[1] | queenAttacks[1] | kingAttacks[1];
	}

	private long getBishopMoves(BitBoard board, int index, int side) {
		long bishopBlockers = (board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK])
				& CoreConstants.occupancyMaskBishop[index];
		int lookupIndex = (int) ((bishopBlockers
				* CoreConstants.magicNumbersBishop[index]) >>> CoreConstants.magicShiftBishop[index]);
		long moveSquares = CoreConstants.magicMovesBishop[index][lookupIndex] & ~board.bitboards[side];
		return moveSquares;
	}

	private long getRookMoves(BitBoard board, int index, int side) {
		long rookBlockers = (board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK])
				& CoreConstants.occupancyMaskRook[index];
		int lookupIndex = (int) ((rookBlockers
				* CoreConstants.magicNumbersRook[index]) >>> CoreConstants.magicShiftRook[index]);
		long moveSquares = CoreConstants.magicMovesRook[index][lookupIndex] & ~board.bitboards[side];
		return moveSquares;
	}
}
