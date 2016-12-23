package eval;

import java.util.ArrayList;
import java.util.Hashtable;

import core.BitBoard;
import core.Move;
import core.MoveGen;

public class Search {
	private Hashtable<Integer, TranspositionEntry> hashtable = new Hashtable<>();

	private Move rootNegamax(MoveGen moveGen, BitBoard board, int color) {
		int depth = 1;
		double maxScore = Double.NEGATIVE_INFINITY;
		Move optimal = null;
		ArrayList<Move> moves = moveGen.generateMoves(board, true);
		for (Move move : moves) {
			board.move(move);
			double score = negamax(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, board, depth, color);
			board.undo();
			if (score > maxScore) {
				maxScore = score;
				optimal = move;
			}
		}
		return optimal;
	}
	//Color Factor: 1 for white, -1 for black
	private int negamax(double alpha, double beta, BitBoard board, int depth, int colorFactor) {
		
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
