package eval;

import core.BitBoard;
import core.CoreConstants;

//Used to consider the endgame where one side has a king and one side has a king and a pawn
public class Bitbase {
	// Based on the Stockfish class bitbase.cpp
	private int index(boolean white2Move, int wKingIndex, int bKingIndex, int pIndex) {
		return wKingIndex + (bKingIndex << 6) + ((white2Move ? 1 : 0) << 12) + ((7 - (pIndex % 8)) << 13)
				+ ((6 - (pIndex >> 3)) << 15);
	}

	public boolean probe(int wKingIndex, int bKingIndex, int wPawnIndex, boolean white2Move) {
		int index = index(white2Move, Board.mirrorIndex(wKingIndex), Board.mirrorIndex(bKingIndex),
				Board.mirrorIndex(wPawnIndex));
		return ((EvalConstants.BITBASE[index / 32] & (1 << (index & 0x1F)))) != 0;
	}

	public boolean probe(BitBoard board) {
		int wKingIndex = Board.mirrorIndex(board.bitScanForward(board.bitboards[CoreConstants.WHITE_KING]));
		int bKingIndex = Board.mirrorIndex(board.bitScanForward(board.bitboards[CoreConstants.BLACK_KING]));
		int pIndex = Board.mirrorIndex(board
				.bitScanForward(board.bitboards[CoreConstants.WHITE_PAWN] | board.bitboards[CoreConstants.BLACK_PAWN]));
		boolean white2Move = board.toMove == 0;
		if (board.bitboards[CoreConstants.BLACK_PAWN] != 0) {
			int offset = wKingIndex;
			wKingIndex = 63 - bKingIndex;
			bKingIndex = 63 - offset;
			pIndex = 63 - pIndex;
			white2Move = !white2Move;
		}
		int file = 7 - (pIndex & 7);
		if(file > 3){
		}
		return false;
	}

}
