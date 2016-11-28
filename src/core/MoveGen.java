package core;

public class MoveGen {
	private BitBoard board;

	public MoveGen(BitBoard board) {
		this.board = board;
	}

	void addPawnPushes(int colorFactor) {
		int[] offsets = { 8, 64 - 8 };
		long[] promotions_mask = { Constants.ROW_8, Constants.ROW_1 };
		long[] startWithMask = { Constants.ROW_3, Constants.ROW_6 };
		int side = (colorFactor == 1) ? 0 : 1;
		int offset = offsets[side];
		long pawns = board.bitboards[side | Constants.PAWN];
		long emptySquares = ~(board.bitboards[Constants.WHITE] | board.bitboards[Constants.BLACK]);
		long pushes = circularLeftShift(pawns, offset) & emptySquares;
		double promotions = pushes & promotions_mask[side];
		double doublePushes = circularLeftShift(pushes & startWithMask[side], offset) & emptySquares;
	}

	long circularLeftShift(long target, int shift) {
		return target << shift | target >> (64 - shift);
	}
	
	long bitScanForward(long target){
		return 0;
	}
}
