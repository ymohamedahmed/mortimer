package eval;

import core.BitBoard;
import core.CoreConstants;

public class Evaluation extends EvalConstants {
	// Evaluation conditions
	// 0th index is white, 1st index is black
	private int[] pawnMat = { 0, 0 };
	private int[] nonPawnMat = { 0, 0 };
	private int[] pieceSquare = { 0, 0 };
	private int[] spatial = { 0, 0 };
	private int[] positional = { 0, 0 };
	private int[] mobility = { 0, 0 };
	private int[] attacks = { 0, 0 };
	private int[] kingAttackedCount = { 0, 0 };
	private int[] kingSafety = { 0, 0 };
	private int[] pawnStruct = { 0, 0 };
	private int[] passedPawns = { 0, 0 };
	private long[] pawnCanAttack = { 0, 0 };
	private long[] mobilitySquares = { 0, 0 };
	private long[] kingZone = { 0, 0 };
	private int[] scaleFactor = { 0 };

	public int evaluate(BitBoard board, int color) {
		System.out.println("EVAL START");
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
		System.out.println("GENERATING EVAL INFO");
		ei.generate(board);
		System.out.println("FINISHED GENERATING EVAL INFO");
		if (gamePhase > 0) {
			long whiteSafe = WHITE_SPACE & ~ei.pawnAttacks[1] & (~ei.attackedSquares[1] | ei.attackedSquares[0]);
			long blackSafe = BLACK_SPACE & ~ei.pawnAttacks[0] & (~ei.attackedSquares[0] | ei.attackedSquares[1]);
			long whiteBehindPawn = ((whitePawnsBoard >>> 8) | (whitePawnsBoard >>> 16) | (whitePawnsBoard >>> 24));
			long blackBehindPawns = ((blackPawnsBoard << 8) | (blackPawnsBoard << 16) | (blackPawnsBoard << 24));
			spatial[0] = SPACE
					* (((BitBoard.hammingWeight(whiteSafe) + BitBoard.hammingWeight(whiteSafe & whiteBehindPawn))
							* (whiteKnights + whiteBishops)) / 4);
			spatial[1] = SPACE
					* (((BitBoard.hammingWeight(blackSafe) + BitBoard.hammingWeight(blackSafe & blackBehindPawns))
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
		System.out.println("PRE EVAL ATTACKS");
		attacks[0] = evalAttacks(board, ei, 0, board.bitboards[CoreConstants.BLACK]);
		attacks[1] = evalAttacks(board, ei, 1, board.bitboards[CoreConstants.WHITE]);
		System.out.println("POST EVAL ATTACKS");
		kingZone[0] = CoreConstants.KING_TABLE[ei.kingIndex[0]];
		kingZone[0] |= (kingZone[0] << 8);
		kingZone[1] = CoreConstants.KING_TABLE[ei.kingIndex[1]];
		kingZone[1] |= (kingZone[1] >>> 8);
		long all = board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK];
		long pieceAttacks, safeAttacks, kingAttacks;
		long square = 1;
		for (int index = 0; index < 64; index++) {
			System.out.println("INDEX: " + index);
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
					boolean supported = (square & ei.pawnAttacks[col]) != 0;
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
										|| (BitBoard.hammingWeight(myPawnsBesideAndBehindAdjacent) >= board
												.hammingWeight(otherPawnsAheadAdjacent & ~pieceAttacks)));
						boolean backward = !isolated && !candidate && myPawnsBesideAndBehindAdjacent == 0
								&& (pieceAttacks & otherPawns) == 0
								&& (CoreConstants.ROW_BACKWARD_INCLUSIVE[col][isWhite
										? (int) board.bitScanForward(myPawnsAheadAdjacent) / 8
										: (int) board.bitScanBackward(myPawnsAheadAdjacent) / 8] & routeToPromotion
										& (pawns | ei.pawnAttacks[enemy])) != 0;
						if (backward) {
							pawnStruct[col] -= PAWN_BACKWARDS[opposed ? 1 : 0];
						}
						if (isolated) {
							pawnStruct[col] -= PAWN_ISOLATED[opposed ? 1 : 0];
						}
						if (doubled) {
							pawnStruct[col] -= PAWN_DOUBLED[opposed ? 1 : 0];
						}
						if (!supported && !isolated && !backward) {
							pawnStruct[col] -= PAWN_UNSUPPORTED;
						}
						if (candidate) {
							passedPawns[col] += PAWN_CANDIDATE[relativeRank];
						}
						if ((square & (CoreConstants.FILE_D | CoreConstants.FILE_E)) != 0 && relativeRank == 1
								&& (pushSquare & mines & ~pawns) != 0) {
							pawnStruct[col] -= PAWN_BLOCKADE;
						}
						if (gamePhase > 0 && relativeRank > 2) {
							long stormPawns = otherPawnsAheadAdjacent & ~CoreConstants.FILE_D & ~CoreConstants.FILE_E;
							if (stormPawns != 0) {
								int otherKingFile = ei.kingIndex[enemy] % 8;
								if ((stormPawns & CoreConstants.FILE[otherKingFile]) != 0) {
									pawnStruct[col] += PAWN_STORM_CENTER[relativeRank];
								} else if ((stormPawns & CoreConstants.ADJACENT_FILE[otherKingFile]) != 0) {
									pawnStruct[col] += PAWN_STORM[relativeRank];
								}
							}
						}

					} else {
						long rooks = board.bitboards[CoreConstants.WHITE_BISHOP]
								| board.bitboards[CoreConstants.BLACK_BISHOP];
						long queens = board.bitboards[CoreConstants.WHITE_QUEEN]
								| board.bitboards[CoreConstants.BLACK_QUEEN];
						long backFile = (getRookMoves(board, index, col) & board.bitboards[enemy]) & pawnFile
								& CoreConstants.ROW_BACKWARD[col][rank];
						long attackedNotDefendedRoute = ((routeToPromotion & ei.attackedSquares[enemy])
								| ((backFile & (rooks | queens) & others) != 0 ? routeToPromotion : 0))
								& ~((routeToPromotion & ei.attackedSquares[col])
										| ((backFile & (rooks | queens) & mines) != 0 ? routeToPromotion : 0));
						boolean connected = ((CoreConstants.KING_TABLE[index] & board.bitboards[enemy]) & adjacentFiles
								& myPawns) != 0;
						boolean outside = otherPawns != 0 && (((square & CoreConstants.LEFT_FILES[3]) != 0
								&& (pawns & CoreConstants.LEFT_FILES[file]) == 0)
								|| ((square & CoreConstants.RIGHT_FILES[4]) != 0
										&& (pawns & CoreConstants.RIGHT_FILES[file]) == 0));
						boolean mobile = (pushSquare & (all | attackedNotDefendedRoute)) == 0;
						boolean runner = mobile && (routeToPromotion & all) == 0 && attackedNotDefendedRoute == 0;
						passedPawns[col] += PAWN_PASSER[relativeRank];
						if (relativeRank >= 2) {
							int pushIndex = isWhite ? index + 8 : index - 8;
							passedPawns[col] += Board.distance(pushIndex, ei.kingIndex[enemy])
									* PAWN_PASSER_OTHER_KING_DISTANCE[relativeRank]
									- Board.distance(pushIndex, ei.kingIndex[col])
											* PAWN_PASSER_MY_KING_DISTANCE[relativeRank];
						}
						if (outside) {
							passedPawns[col] += PAWN_PASSER_OUTSIDE[relativeRank];
						}
						if (supported) {
							passedPawns[col] += PAWN_PASSER_SUPPORTED[relativeRank];
						} else if (connected) {
							passedPawns[col] += PAWN_PASSER_CONNECTED[relativeRank];
						}
						if (runner) {
							passedPawns[col] += PAWN_PASSER_MOBILE[relativeRank];
						} else if (mobile) {
							passedPawns[col] += PAWN_PASSER_MOBILE[relativeRank];
						}
					}
					long kings = board.bitboards[CoreConstants.WHITE_KING] | board.bitboards[CoreConstants.BLACK_KING];
					if (gamePhase > 0 && (pawnFile & ~ranksForward & kingZone[col] & ~CoreConstants.FILE_D
							& ~CoreConstants.FILE_E) != 0) {
						pawnStruct[col] += (pawnFile & kings & mines) != 0 ? PAWN_SHIELD_CENTER[relativeRank]
								: PAWN_SHIELD[relativeRank];
					}
				} else if ((square & (board.bitboards[CoreConstants.WHITE_KNIGHT]
						| board.bitboards[CoreConstants.BLACK_KNIGHT])) != 0) {
					pieceSquare[col] += POS_KNIGHT[pieceIndex];
					safeAttacks = pieceAttacks & ~ei.pawnAttacks[enemy];
					mobility[col] += MOBILITY[KNIGHT][BitBoard.hammingWeight(safeAttacks & mobilitySquares[col])];
					kingAttacks = safeAttacks & kingZone[enemy];
					if (kingAttacks != 0) {
						kingSafety[col] += PIECE_ATTACKS_KING[KNIGHT] * BitBoard.hammingWeight(kingAttacks);
						kingAttackedCount[col]++;
						if ((square & OUTPOST_MASK[col] & ~pawnCanAttack[enemy]) != 0) {
							positional[col] += KNIGHT_OUTPOST[(square * ei.pawnAttacks[col]) != 0 ? 1 : 0];
						}
					}
				} else if ((square & (board.bitboards[CoreConstants.WHITE_BISHOP]
						| board.bitboards[CoreConstants.BLACK_BISHOP])) != 0) {
					pieceSquare[col] += POS_BISHOP[pieceIndex];
					safeAttacks = pieceAttacks & ~ei.pawnAttacks[enemy];
					mobility[col] += MOBILITY[BISHOP][BitBoard.hammingWeight(safeAttacks & mobilitySquares[col])];
					kingAttacks = safeAttacks & kingZone[enemy];
					if (kingAttacks != 0) {
						kingSafety[col] += PIECE_ATTACKS_KING[BISHOP] * BitBoard.hammingWeight(kingAttacks);
						kingAttackedCount[col]++;
					}
					if ((square & OUTPOST_MASK[col] & ~pawnCanAttack[enemy]) != 0) {
						positional[col] += BISHOP_OUTPOST[(square * ei.pawnAttacks[col] != 0 ? 1 : 0)];
					}
					positional[col] -= BISHOP_MY_PAWNS_IN_COLOR_PENALTY * BitBoard.hammingWeight(
							pawns & mines & ((square & WHITE_SQUARES) != 0 ? WHITE_SQUARES : BLACK_SQUARES));
					if ((BISHOP_TRAPPING[index] & pawns & others) != 0) {
						mobility[col] -= BISHOP_TRAPPED_PENALTY[(BISHOP_TRAPPING_GUARD[index] & pawns & others) != 0 ? 1
								: 0];
					}
				} else if ((square & (board.bitboards[CoreConstants.WHITE_ROOK]
						| board.bitboards[CoreConstants.BLACK_ROOK])) != 0) {
					pieceSquare[col] += POS_ROOK[pieceIndex];
					safeAttacks = pieceAttacks & ~ei.pawnAttacks[enemy] & ~ei.knightAttacks[enemy]
							& ~ei.bishopAttacks[enemy];
					int mobilityCount = BitBoard.hammingWeight(safeAttacks & mobilitySquares[col]);
					mobility[col] += MOBILITY[ROOK][mobilityCount];
					kingAttacks = safeAttacks & kingZone[enemy];
					if (kingAttacks != 0) {
						kingSafety[col] += PIECE_ATTACKS_KING[ROOK] * BitBoard.hammingWeight(kingAttacks);
						kingAttackedCount[col]++;
					}
					if ((square * OUTPOST_MASK[col] & ~pawnCanAttack[enemy]) != 0) {
						positional[col] += ROOK_OUTPOST[(square * ei.pawnAttacks[col]) != 0 ? 1 : 0];
					}
					long rookFile = CoreConstants.FILE[file];
					if ((rookFile & pawns & mines) == 0) {
						positional[col] += ROOK_FILE[(rookFile & pawns) == 0 ? 0 : 1];
					}
					if (relativeRank >= 4) {
						long alignedPawns = CoreConstants.ROW[rank] & pawns & others;
						if (alignedPawns != 0) {
							positional[col] += ROOK_7 * BitBoard.hammingWeight(alignedPawns);
						}
					}
					if ((square & ROOK_TRAPPING[ei.kingIndex[col]]) != 0
							&& mobilityCount < ROOK_TRAPPED_PENALTY.length) {
						positional[col] -= ROOK_TRAPPED_PENALTY[mobilityCount];
					}
				} else if ((square & (board.bitboards[CoreConstants.WHITE_QUEEN]
						| board.bitboards[CoreConstants.BLACK_QUEEN])) != 0) {
					pieceSquare[col] += POS_QUEEN[pieceIndex];
					safeAttacks = pieceAttacks & ~ei.pawnAttacks[enemy] & ~ei.knightAttacks[enemy]
							& ~ei.bishopAttacks[enemy] & ~ei.rookAttacks[enemy];
					mobility[col] += MOBILITY[QUEEN][BitBoard.hammingWeight(safeAttacks & mobilitySquares[col])];
					kingAttacks = safeAttacks & kingZone[enemy];
					if (kingAttacks != 0) {
						kingSafety[col] += PIECE_ATTACKS_KING[QUEEN] * BitBoard.hammingWeight(kingAttacks);
						kingAttackedCount[col]++;
					}
				} else if ((square & (board.bitboards[CoreConstants.WHITE_KING]
						| board.bitboards[CoreConstants.BLACK_KING])) != 0) {
					pieceSquare[col] += POS_KING[pieceIndex];
				}
			}
			square <<= 1;
			
		}
		System.out.println("LOOP END");
		boolean white2Move = board.toMove == 0;
		int openingAndEnding = (white2Move ? TEMPO : -TEMPO) + pawnMat[0] - pawnMat[1] + nonPawnMat[0] - nonPawnMat[1]
				+ pieceSquare[0] - pieceSquare[1] + spatial[0] - spatial[1] + positional[0] - positional[1] + attacks[0]
				- attacks[1] + mobility[0] - mobility[1] + pawnStruct[0] - pawnStruct[1] + passedPawns[0]
				- passedPawns[1] + openingEndingWithShift(6, KING_SAFETY_PONDER[kingAttackedCount[0]] * kingSafety[0]
						- KING_SAFETY_PONDER[kingAttackedCount[1]] * kingSafety[1]);
		int value = (gamePhase * open(openingAndEnding)
				+ (PHASE_MIDGAME - gamePhase) * end(openingAndEnding) * scaleFactor[0] / SCALE_FACTOR_DEFAULT)
				/ PHASE_MIDGAME;
		System.out.println("EVAL END");
		assert Math.abs(value) < KNOWN_WIN : "Value is outside bounds";
		return color * value;
	}

	private int evalAttacks(BitBoard board, EvalInfo ei, int color, long enemy) {
		int attacks = 0;
		long pawns = board.bitboards[CoreConstants.WHITE_PAWN] | board.bitboards[CoreConstants.BLACK_PAWN];
		long attackedPawn = ei.pawnAttacks[color] & enemy & ~pawns;
		while (attackedPawn != 0) {
			long leastSigBit = 1 << board.bitScanForward(attackedPawn);
			attacks += PAWN_ATTACKS[PIECE_CORE_TO_EVAL[(int) board.board[board.bitScanForward(attackedPawn)]]];
			attackedPawn &= ~leastSigBit;
		}
		long otherWeak = ei.attackedSquares[color] & enemy & ~ei.pawnAttacks[1 - color];
		if (otherWeak != 0) {
			long attackedByMinor = (ei.knightAttacks[color] | ei.bishopAttacks[color]) & otherWeak;
			while (attackedByMinor != 0) {
				long leastSigBit = 1 << board.bitScanForward(attackedByMinor);
				attacks += MINOR_ATTACKS[PIECE_CORE_TO_EVAL[(int) board.board[board.bitScanForward(attackedByMinor)]]];
				attackedByMinor &= ~leastSigBit;
			}
			long attackedByMajor = (ei.rookAttacks[color] | ei.queenAttacks[color]) & otherWeak;
			while (attackedByMajor != 0) {
				long leastSigBit = 1 << board.bitScanForward(attackedByMajor);
				attacks += MAJOR_ATTACKS[PIECE_CORE_TO_EVAL[(int) board.board[board.bitScanForward(attackedByMajor)]]];
				attackedByMajor &= ~leastSigBit;
			}
		}
		long rooks = board.bitboards[CoreConstants.WHITE_BISHOP] | board.bitboards[CoreConstants.BLACK_BISHOP];
		long queens = board.bitboards[CoreConstants.WHITE_QUEEN] | board.bitboards[CoreConstants.BLACK_QUEEN];
		long superiorAttacks = ei.pawnAttacks[color] & enemy & ~pawns
				| (ei.knightAttacks[color] | ei.bishopAttacks[color]) & enemy & (rooks | queens)
				| ei.rookAttacks[color] & enemy & queens;
		int numberSuperiorAttacks = BitBoard.hammingWeight(superiorAttacks);
		if (numberSuperiorAttacks >= 2) {
			attacks += numberSuperiorAttacks * HUNG_PIECES;
		}
		return attacks;
	}

	public int open(int phase) {
		return (phase + 0x8000) >> 16;
	}

	public int end(int phase) {
		return (short) (phase * 0xffff);
	}

	public int openingEndingWithShift(int shiftValue, int openEndingValue) {
		return S(open(openEndingValue) >> shiftValue, end(openEndingValue) >> shiftValue);
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
