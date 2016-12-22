package ui.controllers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import core.BitBoard;
import core.Constants;
import core.Move;
import core.MoveGen;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MainController {
	private final int PLAYER_COLOR = Constants.BLACK;
	private final int AI_COLOR = Constants.WHITE;
	// Variables loaded from the fxml file
	// Must be global so that they can be loaded from the fxml file
	public StackPane stackPane;
	public Canvas chessPane;
	public BorderPane borderPane;
	private ArrayList<Integer> blueSquares = new ArrayList<>();
	private int oldPos;
	private ArrayList<Move> moveList = new ArrayList<>();
	private Hashtable<Integer, TranspositionEntry> hashtable = new Hashtable<>();
	private MoveGen moveGen;

	public void initialize() {
		playGame();
	}

	private double paintChessBoard(BitBoard board) {
		GraphicsContext g = chessPane.getGraphicsContext2D();
		double width = chessPane.getWidth();
		double height = chessPane.getHeight();
		double cellSize = Math.ceil(Math.min(width / 8.0, height / 8.0));
		int squareNo = 0;
		while (squareNo <= 63) {
			int x = squareNo % 8;
			int y = squareNo / 8;
			if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
				g.setFill(CellColor.COLOR1.getColor());
			} else {
				g.setFill(CellColor.COLOR2.getColor());
			}
			g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
			squareNo++;
		}

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				byte piece = board.board[(row * 8) + col];
				if (piece != Constants.EMPTY) {
					Image image = new Image(MainController.class
							.getResource("/images/" + Constants.FILE_NAMES[piece] + ".png").toExternalForm());
					g.drawImage(image, col * cellSize, (7 - row) * cellSize, cellSize, cellSize);
				}
			}

		}
		return cellSize;
	}

	private void playGame() {
		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		moveGen = new MoveGen();
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();
		moveGen.initialisePawnLookupTable();
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);
		moveList = getMoves(board, false);
		double cellSize = paintChessBoard(board);
		chessPane.setOnMouseClicked(evt -> clickListenerChessPane(board, evt, cellSize));
	}

	private void clearCanvas() {
		GraphicsContext g = chessPane.getGraphicsContext2D();
		g.clearRect(0, 0, chessPane.getWidth(), chessPane.getHeight());
	}

	private void clickListenerChessPane(BitBoard board, MouseEvent evt, double cellSize) {
		// Get the position clicked in terms of the board
		int column = (int) Math.floor(evt.getX() / cellSize);
		int row = 7 - (int) Math.floor(evt.getY() / cellSize);
		boolean pieceMoved = false;
		// Get the piece that was clicked
		int index = (8 * row) + column;
		byte piece = board.board[index];

		for (int square : blueSquares) {
			if (square == index) {
				move(board, new Move(piece, oldPos, index), true);
				blueSquares.clear();
				pieceMoved = true;
				break;
			}
		}

		// Clicks square with piece in it
		if (piece != Constants.EMPTY && !pieceMoved) {
			// Get its available moves
			ArrayList<Move> moves = getMovesPiece(index, moveList);
			oldPos = index;
			// Clear the canvas and then repaint it
			clearCanvas();
			paintChessBoard(board);
			GraphicsContext g = chessPane.getGraphicsContext2D();
			// Show available moves by painting a blue circle in the cells
			blueSquares.clear();
			for (Move move : moves) {
				int rowMove = Math.floorDiv(move.getFinalPos(), 8);
				int colMove = move.getFinalPos() % 8;
				if (board.board[move.getFinalPos()] == Constants.EMPTY) {
					g.setFill(Color.BLUE);
					g.fillOval(colMove * cellSize, (7 - rowMove) * cellSize, cellSize, cellSize);
				} else {
					g.setFill(Color.RED);
					g.fillOval(colMove * cellSize, (7 - rowMove) * cellSize, cellSize / 5, cellSize / 5);
				}
				blueSquares.add(move.getFinalPos());
			}

		}
	}

	private ArrayList<Move> getMovesPiece(int oldPos, ArrayList<Move> moveList) {
		ArrayList<Move> result = new ArrayList<>();
		for (Move move : moveList) {
			if (move.getOldPos() == oldPos) {
				result.add(move);
			}
		}
		return result;
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

	public void move(BitBoard board, Move move, boolean repaint) {
		board.move(move);
		if (repaint) {
			// Clear the canvas and then repaint it
			clearCanvas();
			paintChessBoard(board);
			moveList = getMoves(board, true);
		}
	}

	private ArrayList<Move> getMoves(BitBoard board, boolean removeCheck) {
		return moveGen.generateMoves(board, removeCheck);
	}

	private void displayPawnPromotionDialog(int pawnOldPos, int side, BitBoard board) {
		String choice = new String();
		List<String> choices = new ArrayList<>();
		choices.add("Queen");
		choices.add("Rook");
		choices.add("Bishop");
		choices.add("Knight");

		ChoiceDialog<String> dialog = new ChoiceDialog<>("Queen", choices);
		dialog.setTitle("Pawn Promotion");
		dialog.setHeaderText("Choose the piece to switch your pawn to");
		dialog.setContentText("Choose piece:");
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			choice = result.get();
		} else {
			displayPawnPromotionDialog(pawnOldPos, side, board);
		}

		int colorFactor = (side == 0) ? 1 : -1;
		int newPos = pawnOldPos + (colorFactor * 8);
		switch (choice) {
		case "Queen":
			board.addPiece((side == 0) ? Constants.WHITE_QUEEN : Constants.BLACK_QUEEN, newPos);
			break;
		case "Rook":
			board.addPiece((side == 0) ? Constants.WHITE_ROOK : Constants.BLACK_ROOK, newPos);
			break;
		case "Bishop":
			board.addPiece((side == 0) ? Constants.WHITE_BISHOP : Constants.BLACK_BISHOP, newPos);
			break;
		case "Knight":
			board.addPiece((side == 0) ? Constants.WHITE_KNIGHT : Constants.BLACK_KNIGHT, newPos);
			break;
		}
		board.removePiece(pawnOldPos);
	}

	private enum TranspositionFlag {
		EXACT, LOWERBOUND, UPPERBOUND
	}

	private enum CellColor {
		COLOR1(Color.rgb(140, 82, 66)), COLOR2(Color.rgb(255, 255, 206));
		private Color color;

		CellColor(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}
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