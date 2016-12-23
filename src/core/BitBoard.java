package core;

import java.util.Random;

/**
 * Created by yousuf on 11/22/16.
 */
public class BitBoard {
	public long[] bitboards = new long[14];
	public byte[] board = new byte[64];
	public long[] epTargetSquares = new long[2];
	private static int[][] zobristTable = new int[64][12];
	// Castling flag
	// WHITE
	// wQSideLegal | wKSideLegal | wKingMoved | wRQSideMoved | wRKsideMoved
	// BLACK
	// bQSideLegal | bKSideLegal | bKingMoved | bRQSideMoved | bRKSideMoved
	long[] castling = new long[] { 0L, 0L };
	public int toMove = Constants.WHITE;
	int moveNumber = 0;
	// History arrays
	long[] moveHistory;
	long[] whiteHistory;
	long[] blackHistory;
	long[][] pawnHistory;
	long[][] rookHistory;
	long[][] queenHistory;
	long[][] bishopHistory;
	long[][] knightHistory;
	long[][] kingHistory;
	boolean[][] rQSideMoved;
	boolean[][] rKSideMoved;
	byte[][] boardHistory;
	long[][] castlingHistory;
	// Stores en passant target squares for each side
	long[][] epHistory;

	public BitBoard() {
		moveHistory = new long[Constants.MAX_MOVES];
		whiteHistory = new long[Constants.MAX_MOVES];
		blackHistory = new long[Constants.MAX_MOVES];
		pawnHistory = new long[2][Constants.MAX_MOVES];
		rookHistory = new long[2][Constants.MAX_MOVES];
		queenHistory = new long[2][Constants.MAX_MOVES];
		bishopHistory = new long[2][Constants.MAX_MOVES];
		knightHistory = new long[2][Constants.MAX_MOVES];
		kingHistory = new long[2][Constants.MAX_MOVES];
		epHistory = new long[2][Constants.MAX_MOVES];
		boardHistory = new byte[Constants.MAX_MOVES][64];
		epHistory = new long[2][Constants.MAX_MOVES];
		castlingHistory = new long[2][Constants.MAX_MOVES];
	}

	boolean isEmpty() {
		return 0 == ~(bitboards[Constants.WHITE] | bitboards[Constants.BLACK]);
	}

	public void addPiece(byte piece, int square) {
		board[square] = piece;
		long bitboard = (long) Math.pow(2, square);
		if (square == 63) {
			bitboard = 0x8000_0000_0000_0000L;
		}
		// printBoard(bitboard);
		bitboards[piece & 1] |= bitboard;
		bitboards[piece] |= bitboard;
	}

	public void printBoardArray(byte[] board) {
		String line = "";
		for (int row = 56; row >= 0; row -= 8) {
			for (int col = 0; col < 8; col++) {
				line += " " + board[row + col];
			}
			System.out.println(line);
			line = "";
		}
	}

	public void move(Move move) {
		storeHistory();
		moveNumber++;
		toMove = (toMove == 0) ? 1 : 0;

		int finalIndex = move.getFinalPos();
		int oldIndex = move.getOldPos();
		byte piece = board[oldIndex];
		int side = piece % 2;
		int enemy = (piece == 0) ? 1 : 0;
		boolean capture = board[finalIndex] != Constants.EMPTY;

		// update rook castling flag
		if (piece == Constants.WHITE_ROOK) {
			if (oldIndex == 0) {
				castling[side] |= 0b010;
			}
			if (oldIndex == 7) {
				castling[side] |= 0b001;
			}
		} else if (piece == Constants.BLACK_ROOK) {
			if (oldIndex == 56) {
				castling[side] |= 0b010;
			}
			if (oldIndex == 63) {
				castling[side] |= 0b001;
			}
		} else if (piece == Constants.WHITE_KING) {
			castling[side] |= 0b100;
		} else if (piece == Constants.BLACK_KING) {
			castling[side] |= 0b100;
		}

		if (capture) {
			removePiece(finalIndex);
		}
		if (move.isEnPassant()) {
			int offset = (side == 0) ? -8 : 8;
			removePiece(finalIndex + offset);
		}
		removePiece(oldIndex);
		addPiece(piece, finalIndex);

		updateCastlingFlags(enemy);
		epTargetSquares[enemy] = 0;
		if (piece == Constants.WHITE_PAWN) {
			if (finalIndex - oldIndex == 16) {
				int square = finalIndex - 8;
				epTargetSquares[1] = 1L << square;
			}
		} else if (piece == Constants.BLACK_PAWN) {
			if (finalIndex - oldIndex == -16) {
				int square = finalIndex + 8;
				epTargetSquares[0] = 1L << square;
			}
		}
		// TODO move rook during castling
		byte castle = move.getCastlingFlag();
		if (castle != 0) {
			System.out.println("CASTLING FLAG: " + Long.toBinaryString(castling[side]));
			int rookOldIndex = 0;
			int rookFinalIndex = 0;
			switch (castle) {
			case Constants.wQSide:
				rookOldIndex = 0;
				rookFinalIndex = 3;
				break;
			case Constants.wKSide:
				rookOldIndex = 7;
				rookFinalIndex = 5;
				break;
			case Constants.bQSide:
				rookOldIndex = 56;
				rookFinalIndex = 59;
				break;
			case Constants.bKSide:
				rookOldIndex = 63;
				rookFinalIndex = 61;
				break;
			}
			removePiece(rookOldIndex);
			addPiece((castle <= 2) ? Constants.WHITE_ROOK : Constants.BLACK_ROOK, rookFinalIndex);
		}

	}

	public void storeHistory() {
		whiteHistory[moveNumber] = bitboards[0];
		blackHistory[moveNumber] = bitboards[1];
		pawnHistory[0][moveNumber] = bitboards[2];
		pawnHistory[1][moveNumber] = bitboards[3];
		knightHistory[0][moveNumber] = bitboards[4];
		knightHistory[1][moveNumber] = bitboards[5];
		rookHistory[0][moveNumber] = bitboards[6];
		rookHistory[1][moveNumber] = bitboards[7];
		bishopHistory[0][moveNumber] = bitboards[8];
		bishopHistory[1][moveNumber] = bitboards[9];
		queenHistory[0][moveNumber] = bitboards[10];
		queenHistory[1][moveNumber] = bitboards[11];
		kingHistory[0][moveNumber] = bitboards[12];
		kingHistory[1][moveNumber] = bitboards[13];
		boardHistory[moveNumber] = board.clone();
		epHistory[0][moveNumber] = epTargetSquares[0];
		epHistory[1][moveNumber] = epTargetSquares[1];
		castlingHistory[0][moveNumber] = castling[0];
		castlingHistory[1][moveNumber] = castling[1];
	}

	public void undo() {
		moveNumber--;
		bitboards[0] = whiteHistory[moveNumber];
		bitboards[1] = blackHistory[moveNumber];
		bitboards[2] = pawnHistory[0][moveNumber];
		bitboards[3] = pawnHistory[1][moveNumber];
		bitboards[4] = knightHistory[0][moveNumber];
		bitboards[5] = knightHistory[1][moveNumber];
		bitboards[6] = rookHistory[0][moveNumber];
		bitboards[7] = rookHistory[1][moveNumber];
		bitboards[8] = bishopHistory[0][moveNumber];
		bitboards[9] = bishopHistory[1][moveNumber];
		bitboards[10] = queenHistory[0][moveNumber];
		bitboards[11] = queenHistory[1][moveNumber];
		bitboards[12] = kingHistory[0][moveNumber];
		bitboards[13] = kingHistory[1][moveNumber];
		board = boardHistory[moveNumber].clone();
		epTargetSquares[0] = epHistory[0][moveNumber];
		epTargetSquares[1] = epHistory[1][moveNumber];
		castling[0] = castlingHistory[0][moveNumber];
		castling[1] = castlingHistory[1][moveNumber];
		toMove = (toMove == 0) ? 1 : 0;

	}

	void updateCastlingFlags(int side) {
		if (side == 0) {
			// Consider queenside(conditions required for castling)
			// Checking if squares are attacked
			// Then test whether king would be in check (more expensive
			// calculation)
			castling[side] &= 0b01111;
			if (board[0] == Constants.WHITE_ROOK) {
				if (board[1] == Constants.EMPTY) {
					if (board[2] == Constants.EMPTY) {
						if (board[3] == Constants.EMPTY) {
							if (board[4] == Constants.WHITE_KING) {
								if (!((castling[side] & 0b00100) == 4)) {
									if (!((castling[side] & 0b00010) == 2)) {
										if (!isSquareAttacked(1, side)) {
											if (!isSquareAttacked(2, side)) {
												if (!isSquareAttacked(3, side)) {
													if (!isSquareAttacked(4, side)) {
														castling[side] |= 0b10000;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			// Consider kingside
			castling[side] &= 0b10111;
			if (board[7] == Constants.WHITE_ROOK) {
				if (board[6] == Constants.EMPTY) {
					if (board[5] == Constants.EMPTY) {
						if (board[4] == Constants.WHITE_KING) {
							if (!((castling[side] & 0b00100) == 4)) {
								if (!((castling[side] & 0b00001) == 1)) {
									if (!isSquareAttacked(4, side)) {
										if (!isSquareAttacked(5, side)) {
											if (!isSquareAttacked(6, side)) {
												castling[side] |= 0b01000;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			// Consider queenside(conditions required for castling denoted by c)
			castling[side] &= 0b01111;
			if (board[56] == Constants.BLACK_ROOK) {
				if (board[57] == Constants.EMPTY) {
					if (board[58] == Constants.EMPTY) {
						if (board[59] == Constants.EMPTY) {
							if (board[60] == Constants.BLACK_KING) {
								if (!((castling[side] & 0b00100) == 4)) {
									if (!((castling[side] & 0b00010) == 2)) {
										if (!isSquareAttacked(57, side)) {
											if (!isSquareAttacked(58, side)) {
												if (!isSquareAttacked(59, side)) {
													if (!isSquareAttacked(60, side)) {
														castling[side] |= 0b10000;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			// Consider kingside
			castling[side] &= 0b10111;
			if (board[63] == Constants.WHITE_ROOK) {
				if (board[62] == Constants.EMPTY) {
					if (board[61] == Constants.EMPTY) {
						if (board[60] == Constants.WHITE_KING) {
							if (!((castling[side] & 0b00100) == 4)) {
								if (!((castling[side] & 0b00001) == 1)) {
									if (!isSquareAttacked(60, side)) {
										if (!isSquareAttacked(61, side)) {
											if (!isSquareAttacked(62, side)) {
												castling[side] |= 0b01000;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	// https://chessprogramming.wikispaces.com/Checks+and+Pinned+Pieces+(Bitboards)
	boolean check(int side) {
		int kingIndex = (side == Constants.WHITE) ? bitScanForward(bitboards[Constants.WHITE_KING])
				: bitScanForward(bitboards[Constants.BLACK_KING]);
		long enemyPawns = bitboards[3 - side];
		long enemyKnights = bitboards[5 - side];
		long enemyRookQueen = bitboards[11 - side];
		long enemyBishopQueen = enemyRookQueen;
		long occupiedBoard = bitboards[Constants.WHITE] | bitboards[Constants.BLACK];
		enemyRookQueen |= bitboards[7 - side];
		enemyBishopQueen |= bitboards[9 - side];
		long result = (Constants.PAWN_ATTACKS_TABLE[side][kingIndex] & enemyPawns)
				| (Constants.KNIGHT_TABLE[kingIndex] & enemyKnights)
				| (bishopAttacks(occupiedBoard, kingIndex, side) & enemyBishopQueen)
				| (rookAttacks(occupiedBoard, kingIndex, side) & enemyRookQueen);
		return (bitScanForward(result) != -1);
	}

	public boolean isSquareAttacked(int index, int side) {
		long enemyPawns = bitboards[3 - side];
		long enemyKnights = bitboards[5 - side];
		long enemyRookQueen = bitboards[11 - side];
		long enemyBishopQueen = enemyRookQueen;
		long occupiedBoard = bitboards[Constants.WHITE] | bitboards[Constants.BLACK];
		enemyRookQueen |= bitboards[7 - side];
		enemyBishopQueen |= bitboards[9 - side];
		long result = (Constants.PAWN_ATTACKS_TABLE[side][index] & enemyPawns)
				| (Constants.KNIGHT_TABLE[index] & enemyKnights)
				| (bishopAttacks(occupiedBoard, index, side) & enemyBishopQueen)
				| (rookAttacks(occupiedBoard, index, side) & enemyRookQueen);
		return (bitScanForward(result) != -1);
	}

	// Bishop and Rook attacks for purpose of determing status of check
	long bishopAttacks(long occupiedBoard, int index, int side) {
		long bishopBlockers = (occupiedBoard) & Constants.occupancyMaskBishop[index];
		int lookupIndex = (int) ((bishopBlockers
				* Constants.magicNumbersBishop[index]) >>> Constants.magicShiftBishop[index]);
		long moveSquares = Constants.magicMovesBishop[index][lookupIndex] & ~bitboards[side];
		return moveSquares;
	}

	long rookAttacks(long occupiedBoard, int index, int side) {
		long rookBlockers = (occupiedBoard) & Constants.occupancyMaskRook[index];
		int lookupIndex = (int) ((rookBlockers
				* Constants.magicNumbersRook[index]) >>> Constants.magicShiftRook[index]);
		long moveSquares = Constants.magicMovesRook[index][lookupIndex] & ~bitboards[side];
		return moveSquares;
	}

	public void removePiece(int square) {
		byte piece = board[square];
		board[square] = Constants.EMPTY;
		long bitboard = ~((square == 63) ? 0x8000_0000_0000_0000L : (long) Math.pow(2, square));
		bitboards[piece & 1] &= bitboard;
		bitboards[piece] &= bitboard;
	}

	void reset() {
		for (int i = 0; i < 64; i++) {
			board[i] = Constants.EMPTY;
		}
		for (int i = 0; i < 14; i++) {
			bitboards[i] = 0;
		}

		toMove = Constants.WHITE;
	}

	public void resetToInitialSetup() {

		for (int index = 0; index < 64; index++) {
			board[index] = Constants.EMPTY;
		}
		// Adding pawns
		for (int col = 0; col <= 7; col++) {
			board[8 + col] = Constants.WHITE_PAWN;
			board[48 + col] = Constants.BLACK_PAWN;
		}
		// Adding rooks
		for (int col = 0; col <= 7; col += 7) {
			board[0 + col] = Constants.WHITE_ROOK;
			board[56 + col] = Constants.BLACK_ROOK;
		}
		// Adding knights
		for (int col = 1; col <= 6; col += 5) {
			board[0 + col] = Constants.WHITE_KNIGHT;
			board[56 + col] = Constants.BLACK_KNIGHT;
		}
		// Adding bishops
		for (int col = 2; col <= 5; col += 3) {
			board[0 + col] = Constants.WHITE_BISHOP;
			board[56 + col] = Constants.BLACK_BISHOP;
		}
		// Adding queens
		board[0 + 3] = Constants.WHITE_QUEEN;
		board[56 + 3] = Constants.BLACK_QUEEN;
		// Adding kings
		board[0 + 4] = Constants.WHITE_KING;
		board[56 + 4] = Constants.BLACK_KING;
		bitboards[Constants.WHITE] = 0x000000000000FFFFL;
		bitboards[Constants.BLACK] = 0xFFFF000000000000L;
		bitboards[Constants.WHITE_PAWN] = 0x000000000000FF00L;
		bitboards[Constants.WHITE_KNIGHT] = 0x0000000000000042L;
		bitboards[Constants.WHITE_BISHOP] = 0x0000000000000024L;
		bitboards[Constants.WHITE_ROOK] = 0x0000000000000081L;
		bitboards[Constants.WHITE_QUEEN] = 0x0000000000000008L;
		bitboards[Constants.WHITE_KING] = 0x0000000000000010L;
		bitboards[Constants.BLACK_PAWN] = 0x00FF000000000000L;
		bitboards[Constants.BLACK_KNIGHT] = 0x4200000000000000L;
		bitboards[Constants.BLACK_BISHOP] = 0x2400000000000000L;
		bitboards[Constants.BLACK_ROOK] = 0x8100000000000000L;
		bitboards[Constants.BLACK_QUEEN] = 0x0800_0000_0000_0000L;
		bitboards[Constants.BLACK_KING] = 0x1000000000000000L;
		toMove = Constants.WHITE;
		castling[0] = 0L;
		castling[1] = 0L;
	}

	byte getType(int square) {
		return board[square];
	}

	long getBitboard(int type) {
		return bitboards[type];
	}

	void printBoard(long board) {
		String result = "";
		byte[] boardArr = new byte[64];
		while (board != 0) {
			int index = bitScanForward(board);
			boardArr[index] = 1;
			board &= board - 1;
		}
		for (int row = 7; row >= 0; row--) {
			String line = "";
			for (int col = 0; col <= 7; col++) {
				line += String.valueOf(boardArr[(8 * row) + col]);
			}
			result += (line + "\n");
		}
		System.out.println(result);
	}

	int bitScanForward(long bb) {
		int pos = Long.numberOfTrailingZeros(bb);
		return pos == 64 ? -1 : pos;
	}

	public static void initialiseZobrist() {
		for (int x = 0; x <= 63; x++) {
			for (int y = 0; y <= 11; y++) {
				zobristTable[x][y] = new Random().nextInt((int) Math.pow(2, 64) - 1);
			}
		}
	}

	// assuming zobrist has been initialised
	public int hash() {
		int hash = 0;
		for (int index = 0; index < 64; index++) {
			if (board[index] != Constants.EMPTY) {
				int j = board[index] - 2;
				hash = hash ^ zobristTable[index][j];
			}
		}
		return hash;
	}
}
