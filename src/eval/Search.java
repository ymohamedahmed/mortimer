package eval;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import core.BitBoard;
import core.Move;
import core.MoveGen;

public class Search {
	// Transposition table $\label{code:hashtable}$
	private Hashtable<Integer, TranspositionEntry> hashtable = new Hashtable<>();

	// This is the method that is accessed from the main controller class, and
	// returns what the program deems to be the best available move to a
	// particular colour. $\label{code:rootNegamax}$
	public Move rootNegamax(BitBoard board, int color) {
		long overallStartTime = System.currentTimeMillis();
		double maxScore = Double.NEGATIVE_INFINITY;
		double minScore = Double.POSITIVE_INFINITY;
		Move optimal = null;
		// Find all the possible moves
		List<Move> moves = MoveGen.generateMoves(board, true);
		int noOfMoves = moves.size();
		// Calculates a rough estimate of how much time to spend evaluating each
		// move
		double timePerMove = EvalConstants.THINKING_TIME / noOfMoves;
		for (Move move : moves) {
			// Make the move then judge the resulting board
			long startTime = System.currentTimeMillis();
			double firstGuess = 0;
			// If there is more time, keep increasing the depth of the search
			// (i.e. the number of moves looked ahead)
			for (int depth = 0; depth <= EvalConstants.MAX_DEPTH; depth++) {
				// Use the mtdf algorithm to generate an value for the board at
				// a particular depth
				board.move(move);
				firstGuess = mtdf(board, firstGuess, depth, color);
				board.undo();
				// If too much time has been spent evaluating break from the
				// loop
				if (System.currentTimeMillis() - startTime >= timePerMove
						&& depth >= EvalConstants.MIN_DEPTH) {
					break;
				}

			}
			// Return board to original state
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
		System.out.println("TIME TO MAKE MOVE: " + (System.currentTimeMillis() - overallStartTime));
		return optimal;
	}

	// Search algorithm used with negamax (minimax variant), supposed to be more
	// efficient and produce the same result $\label{code:mtdf}$
	private double mtdf(BitBoard board, double firstGuess, int depth, int color) {
		double g = firstGuess;
		double upperBound = Double.POSITIVE_INFINITY;
		double lowerBound = Double.NEGATIVE_INFINITY;
		while (lowerBound < upperBound) {
			double beta = Math.max(g, lowerBound + 1);
			g = negamax(beta - 1, beta, board, depth, color);
			if (g < beta) {
				upperBound = g;
			} else {
				lowerBound = g;
			}
		}
		return g;
	}

	// Color Factor: 1 for white, -1 for black $\label{code:negamax}$
	private double negamax(double alpha, double beta, BitBoard board, int depth, int colorFactor) {
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
			return colorFactor * Evaluation.evaluate(board, colorFactor);
		}
		double bestValue = Double.NEGATIVE_INFINITY;
		// Pseudo-legal moves are generated to speed up the algorithm
		// By sorting them by evaluation value, cutoffs are more likely to occur
		List<Move> moves = mergeSort(board, MoveGen.generateMoves(board, false), colorFactor);
		//List<Move> moves = MoveGen.generateMoves(board, false);
		// Analyses each move
		for (Move move : moves) {
			board.move(move);
			double v = -negamax(-beta, -alpha, board, depth - 1, -1 * colorFactor);
			board.undo();
			bestValue = Math.max(bestValue, v);
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

	// Merge sort algorithm to order the moves available by the evaluation value
	// of the resulting board $\label{code:mergesort}$
	public List<Move> mergeSort(BitBoard board, List<Move> moves, int colorFactor) {
		int size = moves.size();
		if (size <= 1) {
			return moves;
		}
		// Find the middle of the list
		int middleIndex = size / 2;
		// Split the list into left and right $\label{code:sublist}$
		List<Move> leftList = moves.subList(0, middleIndex);
		List<Move> rightList = moves.subList(middleIndex, size);
		// Recursively apply the sort to left and right list
		rightList = mergeSort(board, rightList, colorFactor);
		leftList = mergeSort(board, leftList, colorFactor);
		// Merge the two lists
		List<Move> result = merge(board, leftList, rightList, colorFactor);
		return result;
	}

	// Algorithm to merge two lists together into a single list
	public List<Move> merge(BitBoard board, List<Move> left, List<Move> right, int colorFactor) {
		List<Move> result = new LinkedList<>();
		// Generate iterators for each half of the list
		Iterator<Move> leftIter = left.iterator();
		Iterator<Move> rightIter = right.iterator();
		// Work out eval values for the first item in each list
		// Note: move must be undone to return the board to its original state
		Move leftMove = leftIter.next();
		board.move(leftMove);
		double leftEval = Evaluation.fastEval(board, colorFactor);
		board.undo();
		Move rightMove = rightIter.next();
		board.move(rightMove);
		double rightEval = Evaluation.fastEval(board, colorFactor);
		board.undo();
		// The while loops is broken as and when is appropriate
		while (true) {
			// White player is maximiser, so board values should be sorted in
			// ascending order
			if (colorFactor == 1) {
				if (leftEval <= rightEval) {
					// Add the move to the resulting list, sort
					result.add(leftMove);
					if (leftIter.hasNext()) {
						// Update evaluation values
						leftMove = leftIter.next();
						board.move(leftMove);
						leftEval = Evaluation.fastEval(board, colorFactor);
						board.undo();
					} else {
						// If the left list is empty, add the rest of the right
						// list
						result.add(rightMove);
						while (rightIter.hasNext()) {
							result.add(rightIter.next());
						}
						break;
					}
				} else {
					// Add the smaller of the two to the list
					result.add(rightMove);
					if (rightIter.hasNext()) {
						rightMove = rightIter.next();
						board.move(rightMove);
						rightEval = Evaluation.fastEval(board, colorFactor);
						board.undo();
					} else {
						result.add(leftMove);
						while (leftIter.hasNext()) {
							result.add(leftIter.next());
						}
						break;
					}
				}
			} else {
				// Black player is minimiser, so board values should be sorted
				// in descending order to maximise cutoffs
				// Equivalent to the code above, the initial condition is merely
				// reversed
				if (leftEval >= rightEval) {
					result.add(leftMove);
					if (leftIter.hasNext()) {
						leftMove = leftIter.next();
						board.move(leftMove);
						leftEval = Evaluation.fastEval(board, colorFactor);
						board.undo();
					} else {
						result.add(rightMove);
						while (rightIter.hasNext()) {
							result.add(rightIter.next());
						}
						break;
					}
				} else {
					result.add(rightMove);
					if (rightIter.hasNext()) {
						rightMove = rightIter.next();
						board.move(rightMove);
						rightEval = Evaluation.fastEval(board, colorFactor);
						board.undo();
					} else {
						result.add(leftMove);
						while (leftIter.hasNext()) {
							result.add(leftIter.next());
						}
						break;
					}
				}
			}
		}
		return result;
	}

	// Indicates the nature of the value stored in the hash table
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
