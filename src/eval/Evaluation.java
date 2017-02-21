package eval;

import core.BitBoard;
import core.CoreConstants;

public class Evaluation extends EvalConstants {
	// Based on the CompleteEvaluator class in the open source chess engine
	// Carballo https://github.com/albertoruibal/carballo
	public int evaluate(BitBoard board, int color) {
		try {
			// Instantiating evaluation criteria
			int[] pawnMat = { 0, 0 };
			int[] nonPawnMat = { 0, 0 };
			int[] pieceSquare = { 0, 0 };
			int[] spatial = { 0, 0 };
			int[] positional = { 0, 0 };
			int[] mobility = { 0, 0 };
			int[] attacks = { 0, 0 };
			int[] kingAttackedCount = { 0, 0 };
			int[] kingSafety = { 0, 0 };
			int[] pawnStruct = { 0, 0 };
			int[] passedPawns = { 0, 0 };
			long[] pawnCanAttack = { 0, 0 };
			long[] mobilitySquares = { 0, 0 };
			long[] kingZone = { 0, 0 };
			int[] scaleFactor = { 0 };
			// Count the number of each type of piece by type and colour
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
			// Work out the endgame value, if the game has not reached the
			// endgame the function will return no value
			int endgameValue = Endgame.evaluate(board, scaleFactor, whitePawns, blackPawns,
					whiteKnights, blackKnights, whiteBishops, blackBishops, whiteRooks, blackRooks,
					whiteQueens, blackQueens);
			if (endgameValue != NO_VALUE) {
				return endgameValue;
			}
			// Calculate the material values for pawns and non pawns
			pawnMat[0] = whitePawns * PIECE_VALUE_PHASE[PAWN];
			pawnMat[1] = blackPawns * PIECE_VALUE_PHASE[PAWN];
			nonPawnMat[0] = (whiteKnights * PIECE_VALUE_PHASE[KNIGHT])
					+ (whiteBishops * PIECE_VALUE_PHASE[BISHOP])
					+ (whiteRooks * PIECE_VALUE_PHASE[ROOK])
					+ (whiteQueens * PIECE_VALUE_PHASE[QUEEN])
					+ ((whiteBishops == 2) ? BISHOP_PAIR : 0);
			nonPawnMat[1] = (blackKnights * PIECE_VALUE_PHASE[KNIGHT])
					+ (blackBishops * PIECE_VALUE_PHASE[BISHOP])
					+ (blackRooks * PIECE_VALUE_PHASE[ROOK])
					+ (blackQueens * PIECE_VALUE_PHASE[QUEEN])
					+ ((blackBishops == 2) ? BISHOP_PAIR : 0);
			int nonPawnMaterial = end(nonPawnMat[0] + nonPawnMat[1]);
			// Calculate the game phase based on thresholds for the above
			// calculations
			int gamePhase = nonPawnMaterial >= MAT_MIDGAME_MAX ? PHASE_MIDGAME
					: (nonPawnMaterial <= MAT_ENDGAME_MIN) ? PHASE_ENDGAME
							: ((nonPawnMaterial - MAT_ENDGAME_MIN) * PHASE_MIDGAME)
									/ (MAT_MIDGAME_MAX - MAT_ENDGAME_MIN);
			// Work out square which can be moved to potentially
			mobilitySquares[0] = ~board.bitboards[CoreConstants.WHITE];
			mobilitySquares[1] = ~board.bitboards[CoreConstants.BLACK];
			// Generate the information to be used later
			EvalInfo ei = new EvalInfo();
			ei.generate(board);
			// Lookup the white and black pawn bitboards
			long whitePawnsBoard = board.bitboards[CoreConstants.WHITE_PAWN];
			long blackPawnsBoard = board.bitboards[CoreConstants.BLACK_PAWN];

			// This means if the game is not in the endgame
			if (gamePhase > 0) {
				// Calculate the safe spaces for both players
				long whiteSafe = WHITE_SPACE & ~ei.pawnAttacks[1]
						& (~ei.attackedSquares[1] | ei.attackedSquares[0]);
				long blackSafe = BLACK_SPACE & ~ei.pawnAttacks[0]
						& (~ei.attackedSquares[0] | ei.attackedSquares[1]);
				long whiteBehindPawn = ((whitePawnsBoard >>> 8) | (whitePawnsBoard >>> 16)
						| (whitePawnsBoard >>> 24));
				long blackBehindPawn = ((blackPawnsBoard << 8) | (blackPawnsBoard << 16)
						| (blackPawnsBoard << 24));
				// Based on the number of safe spaces generate bonuses
				spatial[0] = SPACE * (((BitBoard.hammingWeight(whiteSafe)
						+ BitBoard.hammingWeight(whiteSafe & whiteBehindPawn))
						* (whiteKnights + whiteBishops)) / 4);
				spatial[1] = SPACE * (((BitBoard.hammingWeight(blackSafe)
						+ BitBoard.hammingWeight(blackSafe & blackBehindPawn))
						* (blackKnights + blackBishops)) / 4);
			} else {
				// In the game the number of safe spaces is irrelevant since the
				// board is so empty
				spatial[0] = 0;
				spatial[1] = 0;
			}
			// Evaluate the pawn attacks
			pawnCanAttack[0] = ei.pawnAttacks[0];
			pawnCanAttack[1] = ei.pawnAttacks[1];
			for (int i = 0; i < 5; i++) {
				whitePawnsBoard = whitePawnsBoard << 8;
				whitePawnsBoard &= ~((board.bitboards[CoreConstants.BLACK_PAWN])
						| ei.pawnAttacks[1]);
				blackPawnsBoard = blackPawnsBoard >>> 8;
				blackPawnsBoard &= ~((board.bitboards[CoreConstants.WHITE_PAWN])
						| ei.pawnAttacks[0]);
				if (whitePawnsBoard == 0 && blackPawnsBoard == 0) {
					break;
				}
				pawnCanAttack[0] |= ((whitePawnsBoard & ~CoreConstants.FILE_A) << 9)
						| ((whitePawnsBoard & ~CoreConstants.FILE_H) << 7);
				pawnCanAttack[1] |= ((blackPawnsBoard & ~CoreConstants.FILE_H) >>> 9)
						| ((blackPawnsBoard & ~CoreConstants.FILE_A) >>> 7);
			}
			// Evaluate generally the attacks available to each player
			attacks[0] = evalAttacks(board, ei, 0, board.bitboards[CoreConstants.BLACK]);
			attacks[1] = evalAttacks(board, ei, 1, board.bitboards[CoreConstants.WHITE]);
			try {
				// Work out the king zone to be used later for additional
				// evaluation
				kingZone[0] = CoreConstants.KING_TABLE[ei.kingIndex[0]];
				kingZone[0] |= (kingZone[0] << 8);
				kingZone[1] = CoreConstants.KING_TABLE[ei.kingIndex[1]];
				kingZone[1] |= (kingZone[1] >>> 8);
			} catch (Exception e) {

			}
			// All the piece on the board
			long all = board.bitboards[CoreConstants.WHITE] | board.bitboards[CoreConstants.BLACK];
			long pieceAttacks, safeAttacks, kingAttacks;
			// Pieces separated by type
			long pawns = board.bitboards[CoreConstants.WHITE_PAWN]
					| board.bitboards[CoreConstants.BLACK_PAWN];
			long knights = board.bitboards[CoreConstants.WHITE_KNIGHT]
					| board.bitboards[CoreConstants.BLACK_KNIGHT];
			long bishops = board.bitboards[CoreConstants.WHITE_BISHOP]
					| board.bitboards[CoreConstants.BLACK_BISHOP];
			long rooks = board.bitboards[CoreConstants.WHITE_ROOK]
					| board.bitboards[CoreConstants.BLACK_ROOK];
			long queens = board.bitboards[CoreConstants.WHITE_QUEEN]
					| board.bitboards[CoreConstants.BLACK_QUEEN];
			long kings = board.bitboards[CoreConstants.WHITE_KING]
					| board.bitboards[CoreConstants.BLACK_KING];
			long square = 1;
			// Now consider each square on the board individually
			for (int index = 0; index < 64; index++) {
				// Evaluation only occurs if the square is not empty
				if ((square & all) != 0) {
					boolean isWhite = ((board.bitboards[CoreConstants.WHITE] & square) != 0);
					int col = isWhite ? 0 : 1;
					int enemy = isWhite ? 1 : 0;
					// Mines are all the pieces which are the player's own
					long mines = isWhite ? board.bitboards[CoreConstants.WHITE]
							: board.bitboards[CoreConstants.BLACK];
					// Find the enemy pieces
					long others = isWhite ? board.bitboards[CoreConstants.BLACK]
							: board.bitboards[CoreConstants.WHITE];
					// Flip the board for black pieces
					int pieceIndex = isWhite ? index : 63 - index;
					// Work out the row and column of the piece
					int rank = (int) index / 8;
					int file = index % 8;
					int relativeRank = isWhite ? rank : 7 - rank;
					// Consider all the attacks from that square
					pieceAttacks = ei.attacksFromSquares[index];
					// If the square contains a pawn
					if ((square & pawns) != 0) {
						// Consider the positional strength of the pawn
						pieceSquare[col] += POS_PAWN[pieceIndex];
						// Evaluate the structure of the pawns
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
						// Consider various penalties based on the structure of
						// the pawns
						// Consider if the pawn is not passed (i.e. they have no
						// clear path to the final row)
						if (!passed) {
							long myPawnsAheadAdjacent = ranksForward & adjacentFiles & myPawns;
							long myPawnsBesideAndBehindAdjacent = CoreConstants.ROW_BACKWARD_INCLUSIVE[col][rank]
									& adjacentFiles & myPawns;
							boolean isolated = (myPawns & adjacentFiles) == 0;
							boolean candidate = !doubled && !opposed
									&& (((otherPawnsAheadAdjacent & ~pieceAttacks) == 0)
											|| (BitBoard.hammingWeight(
													myPawnsBesideAndBehindAdjacent) >= BitBoard
															.hammingWeight(otherPawnsAheadAdjacent
																	& ~pieceAttacks)));
							boolean backward = !isolated && !candidate
									&& myPawnsBesideAndBehindAdjacent == 0
									&& (pieceAttacks & otherPawns) == 0
									&& (CoreConstants.ROW_BACKWARD_INCLUSIVE[col][isWhite
											? (int) BitBoard.bitScanForward(myPawnsAheadAdjacent)
													/ 8
											: (int) BitBoard.bitScanBackward(myPawnsAheadAdjacent)
													/ 8]
											& routeToPromotion
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
							if ((square & (CoreConstants.FILE_D | CoreConstants.FILE_E)) != 0
									&& relativeRank == 1 && (pushSquare & mines & ~pawns) != 0) {
								pawnStruct[col] -= PAWN_BLOCKADE;
							}
							// Consider opening game dependent structure
							if (gamePhase > 0 && relativeRank > 2) {
								long stormPawns = otherPawnsAheadAdjacent & ~CoreConstants.FILE_D
										& ~CoreConstants.FILE_E;
								if (stormPawns != 0) {
									int otherKingFile = ei.kingIndex[enemy] % 8;
									if ((stormPawns & CoreConstants.FILE[otherKingFile]) != 0) {
										pawnStruct[col] += PAWN_STORM_CENTER[relativeRank];
									} else if ((stormPawns
											& CoreConstants.ADJACENT_FILE[otherKingFile]) != 0) {
										pawnStruct[col] += PAWN_STORM[relativeRank];
									}
								}
							}

						} else {
							// Consider if pawn is indeed passed
							// Judge whether or not the path is defended
							long backFile = (getRookMoves(board, index, col)
									& board.bitboards[enemy]) & pawnFile
									& CoreConstants.ROW_BACKWARD[col][rank];
							long attackedNotDefendedRoute = ((routeToPromotion
									& ei.attackedSquares[enemy])
									| ((backFile & (rooks | queens) & others) != 0
											? routeToPromotion : 0))
									& ~((routeToPromotion & ei.attackedSquares[col])
											| ((backFile & (rooks | queens) & mines) != 0
													? routeToPromotion : 0));
							boolean connected = ((CoreConstants.KING_TABLE[index]
									& board.bitboards[enemy]) & adjacentFiles & myPawns) != 0;
							boolean outside = otherPawns != 0 && (((square
									& CoreConstants.LEFT_FILES[3]) != 0
									&& (pawns & CoreConstants.LEFT_FILES[file]) == 0)
									|| ((square & CoreConstants.RIGHT_FILES[4]) != 0
											&& (pawns & CoreConstants.RIGHT_FILES[file]) == 0));
							boolean mobile = (pushSquare & (all | attackedNotDefendedRoute)) == 0;
							boolean runner = mobile && (routeToPromotion & all) == 0
									&& attackedNotDefendedRoute == 0;
							passedPawns[col] += PAWN_PASSER[relativeRank];
							// Add various benefits to encourage passed pawns
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

						if (gamePhase > 0 && (pawnFile & ~ranksForward & kingZone[col]
								& ~CoreConstants.FILE_D & ~CoreConstants.FILE_E) != 0) {
							pawnStruct[col] += (pawnFile & kings & mines) != 0
									? PAWN_SHIELD_CENTER[relativeRank] : PAWN_SHIELD[relativeRank];
						}
						// Consider knights
					} else if ((square & knights) != 0) {
						// Evaluate position of the knight
						pieceSquare[col] += POS_KNIGHT[pieceIndex];
						safeAttacks = pieceAttacks & ~ei.pawnAttacks[enemy];
						// Mobility (i.e. number of moves available)
						mobility[col] += MOBILITY[KNIGHT][BitBoard
								.hammingWeight(safeAttacks & mobilitySquares[col])];
						kingAttacks = safeAttacks & kingZone[enemy];
						// Consider if the knight can attack the enemy king
						if (kingAttacks != 0) {
							kingSafety[col] += PIECE_ATTACKS_KING[KNIGHT]
									* BitBoard.hammingWeight(kingAttacks);
							kingAttackedCount[col]++;
						}
						if ((square & OUTPOST_MASK[col] & ~pawnCanAttack[enemy]) != 0) {
							positional[col] += KNIGHT_OUTPOST[(square * ei.pawnAttacks[col]) != 0
									? 1 : 0];
						}
						// Consider bishops
					} else if ((square & bishops) != 0) {
						// Evaluate position of the bishops
						pieceSquare[col] += POS_BISHOP[pieceIndex];
						// Evaluate the attacks of the bishop
						safeAttacks = pieceAttacks & ~ei.pawnAttacks[enemy];
						mobility[col] += MOBILITY[BISHOP][BitBoard
								.hammingWeight(safeAttacks & mobilitySquares[col])];
						kingAttacks = safeAttacks & kingZone[enemy];
						if (kingAttacks != 0) {
							kingSafety[col] += PIECE_ATTACKS_KING[BISHOP]
									* BitBoard.hammingWeight(kingAttacks);
							kingAttackedCount[col]++;
						}
						if ((square & OUTPOST_MASK[col] & ~pawnCanAttack[enemy]) != 0) {
							positional[col] += BISHOP_OUTPOST[(square * ei.pawnAttacks[col] != 0 ? 1
									: 0)];
						}
						// Penalise if the bishop is blocked by a pawn
						positional[col] -= BISHOP_MY_PAWNS_IN_COLOR_PENALTY * BitBoard
								.hammingWeight(pawns & mines & ((square & WHITE_SQUARES) != 0
										? WHITE_SQUARES : BLACK_SQUARES));
						if ((BISHOP_TRAPPING[index] & pawns & others) != 0) {
							mobility[col] -= BISHOP_TRAPPED_PENALTY[(BISHOP_TRAPPING_GUARD[index]
									& pawns & others) != 0 ? 1 : 0];
						}
						// Consider rooks
					} else if ((square & rooks) != 0) {
						// Evaluate the position of the rook
						pieceSquare[col] += POS_ROOK[pieceIndex];
						safeAttacks = pieceAttacks & ~ei.pawnAttacks[enemy]
								& ~ei.knightAttacks[enemy] & ~ei.bishopAttacks[enemy];
						// Benefits for having a large number of moves available
						// (more probably one will be good)
						int mobilityCount = BitBoard
								.hammingWeight(safeAttacks & mobilitySquares[col]);
						mobility[col] += MOBILITY[ROOK][mobilityCount];
						kingAttacks = safeAttacks & kingZone[enemy];
						// Consider if the rook can attack the enemy king
						if (kingAttacks != 0) {
							kingSafety[col] += PIECE_ATTACKS_KING[ROOK]
									* BitBoard.hammingWeight(kingAttacks);
							kingAttackedCount[col]++;
						}
						if ((square * OUTPOST_MASK[col] & ~pawnCanAttack[enemy]) != 0) {
							positional[col] += ROOK_OUTPOST[(square * ei.pawnAttacks[col]) != 0 ? 1
									: 0];
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
						// Penalties for rooks that can't escape
						if ((square & ROOK_TRAPPING[ei.kingIndex[col]]) != 0
								&& mobilityCount < ROOK_TRAPPED_PENALTY.length) {
							positional[col] -= ROOK_TRAPPED_PENALTY[mobilityCount];
						}
						// Consider queens
					} else if ((square & queens) != 0) {
						// Evaluate the position of the queen
						pieceSquare[col] += POS_QUEEN[pieceIndex];
						// Consider attacks available to the queen
						safeAttacks = pieceAttacks & ~ei.pawnAttacks[enemy]
								& ~ei.knightAttacks[enemy] & ~ei.bishopAttacks[enemy]
								& ~ei.rookAttacks[enemy];
						// Consider number of moves available
						mobility[col] += MOBILITY[QUEEN][BitBoard
								.hammingWeight(safeAttacks & mobilitySquares[col])];
						kingAttacks = safeAttacks & kingZone[enemy];
						// Consider if the queen can attack the king
						if (kingAttacks != 0) {
							kingSafety[col] += PIECE_ATTACKS_KING[QUEEN]
									* BitBoard.hammingWeight(kingAttacks);
							kingAttackedCount[col]++;
						}
					} else if ((square & kings) != 0) {
						// King is judged on position, safety is considered
						// later
						pieceSquare[col] += POS_KING[pieceIndex];
					}
				}
				square <<= 1;

			}
			// Check if it is white's turn
			boolean white2Move = board.toMove == 0;

			// Combine all the conditions evaluated
			int openingAndEnding = (white2Move ? TEMPO : -TEMPO) + pawnMat[0] - pawnMat[1]
					+ nonPawnMat[0] - nonPawnMat[1] + pieceSquare[0] - pieceSquare[1] + spatial[0]
					- spatial[1] + positional[0] - positional[1] + attacks[0] - attacks[1]
					+ mobility[0] - mobility[1] + pawnStruct[0] - pawnStruct[1] + passedPawns[0]
					- passedPawns[1]
					+ openingEndingWithShift(6,
							KING_SAFETY_PONDER[kingAttackedCount[0]] * kingSafety[0]
									- KING_SAFETY_PONDER[kingAttackedCount[1]] * kingSafety[1]);
			// Consider the phase of the game
			int value = (gamePhase * open(openingAndEnding) + (PHASE_MIDGAME - gamePhase)
					* end(openingAndEnding) * scaleFactor[0] / SCALE_FACTOR_DEFAULT)
					/ PHASE_MIDGAME;
			assert Math.abs(value) < KNOWN_WIN : "Value is outside bounds";
			// Multiply value by -1 if black is being evaluated, since
			// evaluation is done from white's perspective
			return color * value;
		} catch (Exception e) {
			return NO_VALUE;
		}
	}

	// Evaluate the attacks available to each piece
	int evalAttacks(BitBoard board, EvalInfo ei, int color, long enemy) {
		int attacks = 0;
		long pawns = board.bitboards[CoreConstants.WHITE_PAWN]
				| board.bitboards[CoreConstants.BLACK_PAWN];
		// Find all the pawns that can attack
		long attackedPawn = ei.pawnAttacks[color] & enemy & ~pawns;
		while (attackedPawn != 0) {
			long leastSigBit = lsb(attackedPawn);
			// Benefit for pawns that have attacks available
			attacks += PAWN_ATTACKS[PIECE_CORE_TO_EVAL[(int) board.board[BitBoard
					.bitScanForward(leastSigBit)]]];
			attackedPawn &= ~leastSigBit;
		}
		long otherWeak = ei.attackedSquares[color] & enemy & ~ei.pawnAttacks[1 - color];
		if (otherWeak != 0) {
			// Consider minor(knight and bishop) and major(rook and queen)
			// pieces that have attacks available
			long attackedByMinor = (ei.knightAttacks[color] | ei.bishopAttacks[color]) & otherWeak;
			while (attackedByMinor != 0) {
				long leastSigBit = lsb(attackedByMinor);
				attacks += MINOR_ATTACKS[PIECE_CORE_TO_EVAL[(int) board.board[BitBoard
						.bitScanForward(leastSigBit)]]];
				attackedByMinor &= ~leastSigBit;
			}
			long attackedByMajor = (ei.rookAttacks[color] | ei.queenAttacks[color]) & otherWeak;
			while (attackedByMajor != 0) {
				long leastSigBit = lsb(attackedByMajor);
				attacks += MAJOR_ATTACKS[PIECE_CORE_TO_EVAL[(int) board.board[BitBoard
						.bitScanForward(leastSigBit)]]];
				attackedByMajor &= ~leastSigBit;
			}
		}
		// Find bitboards containing rook and queens
		long rooks = board.bitboards[CoreConstants.WHITE_BISHOP]
				| board.bitboards[CoreConstants.BLACK_BISHOP];
		long queens = board.bitboards[CoreConstants.WHITE_QUEEN]
				| board.bitboards[CoreConstants.BLACK_QUEEN];
		long superiorAttacks = ei.pawnAttacks[color] & enemy & ~pawns
				| (ei.knightAttacks[color] | ei.bishopAttacks[color]) & enemy & (rooks | queens)
				| ei.rookAttacks[color] & enemy & queens;
		int numberSuperiorAttacks = BitBoard.hammingWeight(superiorAttacks);
		if (numberSuperiorAttacks >= 2) {
			attacks += numberSuperiorAttacks * HUNG_PIECES;
		}
		long pinnedNotPawn = ei.pinnedPieces & ~pawns & enemy;
		if (pinnedNotPawn != 0) {
			attacks += PINNED_PIECE & BitBoard.hammingWeight(pinnedNotPawn);
		}
		return attacks;
	}

	// Return least significant bit
	long lsb(long squares) {
		return squares & (-squares);
	}

	// Returning opening game value
	public int open(int phase) {
		return (phase + 0x8000) >> 16;
	}

	public int end(int phase) {
		return (short) (phase * 0xffff);
	}

	public int openingEndingWithShift(int shiftValue, int openEndingValue) {
		return S(open(openEndingValue) >> shiftValue, end(openEndingValue) >> shiftValue);
	}

	// Get the moves availble to a rook
	long getRookMoves(BitBoard board, int index, int side) {
		long rookBlockers = (board.bitboards[CoreConstants.WHITE]
				| board.bitboards[CoreConstants.BLACK]) & CoreConstants.occupancyMaskRook[index];
		int lookupIndex = (int) ((rookBlockers
				* CoreConstants.magicNumbersRook[index]) >>> CoreConstants.magicShiftRook[index]);
		long moveSquares = CoreConstants.magicMovesRook[index][lookupIndex]
				& ~board.bitboards[side];
		return moveSquares;
	}
}
