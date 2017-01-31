package eval;

import java.util.ArrayList;
import java.util.Hashtable;

import core.BitBoard;
import core.Move;
import core.MoveGen;

public class Search {
	private Hashtable<Integer, TranspositionEntry> hashtable = new Hashtable<>();
	private Evaluation eval = new Evaluation();
	private MoveGen moveGen;

	public Move rootNegamax(MoveGen moveGen, BitBoard board, int color) {
		this.moveGen = moveGen;
		double maxScore = Double.NEGATIVE_INFINITY;
		double minScore = Double.POSITIVE_INFINITY;

		Move optimal = null;
		ArrayList<Move> moves = moveGen.generateMoves(board, true);
		long overallStartTime = System.currentTimeMillis();
		int noOfMoves = moves.size();
		System.out.println("NO OF MOVES : " + noOfMoves);
		System.out.println("THINKING TIME : " + EvalConstants.THINKING_TIME);
		double timePerMove = EvalConstants.THINKING_TIME / noOfMoves;
		for (Move move : moves) {
			board.move(move);
			long startTime = System.currentTimeMillis();
			double firstGuess = 0;
			for (int d = 1; d <= EvalConstants.MAX_DEPTH; d += 2) {
				firstGuess = mtdf(board, firstGuess, d, color);
				if (System.currentTimeMillis() - startTime >= timePerMove) {
					System.out.println("FINAL DEPTH : " + d);
					break;
				}
				// System.out.println("SCORE: " + firstGuess);
			}
			board.undo();
			if (color == EvalConstants.WHITE) {
				if (firstGuess > maxScore) {
					maxScore = firstGuess;
					optimal = move;
				}
			} else {
				if (firstGuess < minScore) {
					minScore = firstGuess;
					optimal = move;
				}
			}
		}
		/*
		 * System.out.println("SCORE: " + ((color == EvalConstants.WHITE) ?
		 * maxScore : minScore)); System.out.println("TIME PER MOVE: " +
		 * timePerMove);
		 */
		System.out.println("TOTAL TIME: " + (System.currentTimeMillis() - overallStartTime));
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
			return colorFactor * eval.evaluate(board, colorFactor);
		}
		double bestValue = Double.NEGATIVE_INFINITY;
		ArrayList<Move> moves = moveGen.generateMoves(board, false);
		for (Move move : moves) {
			board.move(move);
			double v = -negamax(-beta, -alpha, board, depth - 1, -1 * colorFactor);
			board.undo();
			bestValue = (int) Math.max(bestValue, v);
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
		hashtable.put(board.hash(), tEntryFinal);

		return bestValue;
	}

	private enum TranspositionFlag {
		EXACT, LOWERBOUND, UPPERBOUND
	}

	private class TranspositionEntry {
		private double score;
		private TranspositionFlag flag;
		private int depth;

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
