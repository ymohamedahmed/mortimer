package eval;

import java.util.ArrayList;
import java.util.Hashtable;

import core.BitBoard;
import core.Constants;
import core.Move;
import core.MoveGen;

public class Search {
	private Hashtable<Integer, TranspositionEntry> hashtable = new Hashtable<>();
	private Evaluation eval = new Evaluation();
	private MoveGen moveGen = new MoveGen();

	private Move rootNegamax(BitBoard board, int color) {
		double maxScore = Double.NEGATIVE_INFINITY;
		Move optimal = null;
		ArrayList<Move> moves = moveGen.generateMoves(board, true);
		for (Move move : moves) {
			board.move(move);
			long startTime = System.currentTimeMillis();
			double firstGuess = 0;
			for (int d = 1; d <= Constants.MAX_DEPTH; d++) {
				firstGuess = mtdf(board, firstGuess, d, color);
				if (System.currentTimeMillis() - startTime >= Constants.MAX_TIME) {
					break;
				}
			}
			board.undo();
			if (firstGuess > maxScore) {
				maxScore = firstGuess;
				optimal = move;
			}
		}

		return optimal;
	}

	private double mtdf(BitBoard board, double f, int d, int color) {
		double g = f;
		double upperBound = Double.POSITIVE_INFINITY;
		double lowerBound = Double.NEGATIVE_INFINITY;
		while (lowerBound < upperBound) {
			double beta = Math.max(g, lowerBound + 1);
			g = negamax(beta - 1, beta, board, d, color);
			if (g < beta) {
				upperBound = g;
			} else {
				lowerBound = g;
			}
		}
		return g;
	}

	// Color Factor: 1 for white, -1 for black
	private double negamax(double alpha, double beta, BitBoard board, int depth, int colorFactor) {
		double alphaOrig = alpha;
		TranspositionEntry tEntry = new TranspositionEntry();
		tEntry = hashtable.get(board.hash());
		if (tEntry != null) {
			if (tEntry.getDepth() >= depth) {
				if (tEntry.getFlag() == TranspositionFlag.EXACT) {
					return tEntry.getScore();
				} else if (tEntry.getFlag() == TranspositionFlag.LOWERBOUND) {
					alpha = Math.max(alpha, tEntry.getScore());
				} else if (tEntry.getFlag() == TranspositionFlag.UPPERBOUND) {
					beta = Math.min(beta, tEntry.getScore());
				}
			}
		}
		if (depth == 0) {
			return colorFactor * (eval.evaluate(board, colorFactor));
		}
		double bestValue = Double.NEGATIVE_INFINITY;
		ArrayList<Move> moves = sortMoves(board, moveGen.generateMoves(board, false));
		for (Move move : moves) {
			board.move(move);
			double v = -negamax(-beta, -alpha, board, depth - 1, -1 * colorFactor);
			bestValue = Math.max(bestValue, v);
			alpha = Math.max(alpha, v);
			if (alpha >= beta) {
				break;
			}
		}
		TranspositionEntry tEntryFinal = new TranspositionEntry();
		tEntryFinal.setScore(bestValue);
		if (bestValue <= alphaOrig) {
			tEntryFinal.setFlag(TranspositionFlag.UPPERBOUND);
		} else if (bestValue >= beta) {
			tEntryFinal.setFlag(TranspositionFlag.LOWERBOUND);
		} else {
			tEntryFinal.setFlag(TranspositionFlag.EXACT);
		}
		tEntryFinal.setDepth(depth);
		tEntryFinal.setValid(true);
		hashtable.put(board.hash(), tEntryFinal);

		return 0;
	}

	private ArrayList<MoveScore> quickSort(ArrayList<MoveScore> moves) {
		if (!moves.isEmpty()) {
			MoveScore pivot = moves.get(0);
			ArrayList<MoveScore> less = new ArrayList<>();
			ArrayList<MoveScore> pivotList = new ArrayList<>();
			ArrayList<MoveScore> more = new ArrayList<>();

			for (MoveScore move : moves) {
				if (move.getScore() < pivot.getScore()) {
					less.add(move);
				} else if (move.getScore() > pivot.getScore()) {
					more.add(move);
				} else {
					pivotList.add(move);
				}
			}
			less = quickSort(less);
			more = quickSort(more);

			less.addAll(pivotList);
			less.addAll(more);
			return less;
		}
		return moves;
	}

	private ArrayList<Move> sortMoves(BitBoard board, ArrayList<Move> possibleMoves) {
		// Calculating score for each move
		ArrayList<Move> sortedMoves = new ArrayList<>();
		ArrayList<MoveScore> movesScore = new ArrayList<>();
		for (Move move : possibleMoves) {
			board.move(move);
			MoveScore moveScore = new MoveScore(move, eval.evaluate(board, (move.getPieceType() % 2 == 0) ? 1 : -1));
			movesScore.add(moveScore);
		}
		ArrayList<MoveScore> sorted = quickSort(movesScore);
		sorted.forEach(e -> sortedMoves.add(e.getMove()));
		return sortedMoves;
	}

	private enum TranspositionFlag {
		EXACT, LOWERBOUND, UPPERBOUND
	}

	private class TranspositionEntry {
		private double score;
		private TranspositionFlag flag;
		private int depth;
		private boolean valid = false;

		public TranspositionEntry() {
		}

		public double getScore() {
			return score;
		}

		public void setScore(double score) {
			this.score = score;
		}

		public TranspositionFlag getFlag() {
			return flag;
		}

		public void setFlag(TranspositionFlag flag) {
			this.flag = flag;
		}

		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

		public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}
	}

	class MoveScore {
		private Move move;
		private double score;

		public MoveScore(Move move, double score) {
			this.move = move;
			this.score = score;
		}

		public Move getMove() {
			return move;
		}

		public double getScore() {
			return score;
		}
	}
}
