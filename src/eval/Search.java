package eval;

import java.util.ArrayList;
import java.util.Hashtable;

import core.BitBoard;
import core.Move;
import core.MoveGen;

public class Search {
	private Hashtable<Integer, TranspositionEntry> hashtable = new Hashtable<>();

	// This is the method that is accessed from the main controller class, and
	// returns what the program deems to be the best available move to a
	// particular colour.

	public Move rootNegamax(MoveGen moveGen, BitBoard board, int color) {
		double maxScore = Double.NEGATIVE_INFINITY;
		double minScore = Double.POSITIVE_INFINITY;
		Move optimal = null;
		// Find all the possible moves
		ArrayList<Move> moves = moveGen.generateMoves(board, true);
		int noOfMoves = moves.size();
		// Calculates a rough estimate of how much time to spend evaluating each
		// move
		double timePerMove = EvalConstants.THINKING_TIME / noOfMoves;
		for (Move move : moves) {
			// Make the move then judge the resulting board
			board.move(move);
			long startTime = System.currentTimeMillis();
			double firstGuess = 0;
			// If there is more time, keep increasing the depth of the search
			// (i.e. the number of moves looked ahead)
			for (int d = 1; d <= EvalConstants.MAX_DEPTH; d += 2) {
				// Use the mtdf algorithm to generate an value for the board at
				// a particular depth
				firstGuess = mtdf(board, firstGuess, d, color, moveGen);
				// If too much time has been spent evaluating break from the
				// loop
				if (System.currentTimeMillis() - startTime >= timePerMove) {
					System.out.println("FINAL DEPTH: " + d);
					break;
				}
			}
			// Return board to original state
			board.undo();
			// The highest scoring move is optimal for white
			if (color == EvalConstants.WHITE) {
				if (firstGuess > maxScore) {
					maxScore = firstGuess;
					optimal = move;
				}
				// Lowest scoring for move is optimal for black
			} else {
				if (firstGuess < minScore) {
					minScore = firstGuess;
					optimal = move;
				}
			}
		}
		return optimal;
	}

	// Search algorithm used with negamax (minimax variant), supposed to be more
	// efficient and produce the same result
	private double mtdf(BitBoard board, double f, int d, int color, MoveGen moveGen) {
		double g = f;
		double upperBound = Double.POSITIVE_INFINITY;
		double lowerBound = Double.NEGATIVE_INFINITY;
		while (lowerBound < upperBound) {
			double beta = Math.max(g, lowerBound + 1);
			g = negamax(beta - 1, beta, board, d, color, moveGen);
			if (g < beta) {
				upperBound = g;
			} else {
				lowerBound = g;
			}
		}
		return g;
	}

	// Color Factor: 1 for white, -1 for black
	private double negamax(double alpha, double beta, BitBoard board, int depth, int colorFactor,
			MoveGen moveGen) {
		double alphaOrig = alpha;
		// Check if any of the values have already been computed, if so, return
		// them from the hash table
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
		// Return the value of the leaf node
		if (depth == 0) {
			return colorFactor * new Evaluation().evaluate(board, colorFactor);
		}
		double bestValue = Double.NEGATIVE_INFINITY;
		// Pseudo-legal moves are generated to speed up the algorithm
		ArrayList<Move> moves = moveGen.generateMoves(board, false);
		// Analyses each move
		for (Move move : moves) {
			board.move(move);
			double v = -negamax(-beta, -alpha, board, depth - 1, -1 * colorFactor, moveGen);
			board.undo();
			bestValue = (int) Math.max(bestValue, v);
			alpha = Math.max(alpha, v);
			if (alpha >= beta) {
				break;
			}
		}
		// Add values to the hash table to save them from being recomputed
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

	// Indicates the nature of the value stored
	private enum TranspositionFlag {
		EXACT, LOWERBOUND, UPPERBOUND
	}

	// User-defined type to store calculated values in the hash table
	private class TranspositionEntry {
		private double score;
		private TranspositionFlag flag;
		private int depth;

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
}
