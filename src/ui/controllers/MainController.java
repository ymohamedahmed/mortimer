package ui.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import core.BitBoard;
import core.CoreConstants;
import core.Move;
import core.MoveGen;
import eval.EvalConstants;
import eval.Search;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MainController {
	private int PLAYER_COLOR = CoreConstants.BLACK;
	private int AI_COLOR = CoreConstants.WHITE;
	// Variables loaded from the fxml file
	// Must be global so that they can be loaded from the fxml file
	public StackPane stackPane;
	public Canvas chessPane;
	public BorderPane borderPane;
	private ArrayList<Integer> blueSquares = new ArrayList<>();
	private int oldPos;
	private ArrayList<Move> moveList = new ArrayList<>();
	private Search search = new Search();

	private MoveGen moveGen = new MoveGen();

	public void initialize() {
		moveGen.initialiseKnightLookupTable();
		moveGen.initialiseKingLookupTable();
		moveGen.initialisePawnLookupTable();
		moveGen.initialiseBishopAndRookEvalLookupTable();
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);
		BitBoard.initialiseZobrist();
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
				if (piece != CoreConstants.EMPTY) {
					Image image = new Image(MainController.class
							.getResource("/images/" + CoreConstants.FILE_NAMES[piece] + ".png").toExternalForm());
					g.drawImage(image, col * cellSize, (7 - row) * cellSize, cellSize, cellSize);
				}
			}

		}
		return cellSize;
	}

	private void playGame() {
		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		moveList = getMoves(board, false);
		if (AI_COLOR == CoreConstants.WHITE) {
			moveAI(board);
		}

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
				move(board, getMove(moveList, board.board[oldPos], oldPos, index), true);
				blueSquares.clear();
				pieceMoved = true;
				break;
			}
		}

		// Clicks square with piece in it
		if (piece != CoreConstants.EMPTY && !pieceMoved) {
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
				if (board.board[move.getFinalPos()] == CoreConstants.EMPTY) {
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

	public Move getMove(ArrayList<Move> moves, int piece, int oldIndex, int finalIndex) {
		for (Move move : moves) {
			if (move.getPieceType() == piece && move.getOldPos() == oldIndex && move.getFinalPos() == finalIndex) {
				return move;
			}
		}
		return null;
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

	public void move(BitBoard board, Move move, boolean repaint) {
		board.move(move);
		int side = move.getPieceType() % 2;
		int colorFactor = side == 0 ? 1 : -1;
		if (move.isPromotion() && side == PLAYER_COLOR) {
			pawnPromotion(move.getOldPos(),move.getFinalPos(), colorFactor, board, true);
		} else if (move.isPromotion() && side == AI_COLOR) {
			pawnPromotion(move.getOldPos(),move.getFinalPos(), colorFactor, board, false);
		}
		if (repaint) {
			// Clear the canvas and then repaint it
			clearCanvas();
			paintChessBoard(board);
			moveList = getMoves(board, true);

		}
		if (side == PLAYER_COLOR) {
			moveAI(board);
		}

	}

	private ArrayList<Move> getMoves(BitBoard board, boolean removeCheck) {
		return moveGen.generateMoves(board, removeCheck);
	}

	private void pawnPromotion(int pawnOldPos,int newPos,  int side, BitBoard board, boolean display) {
		board.removePiece(pawnOldPos);
		if (display) {
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
				pawnPromotion(pawnOldPos, newPos, side, board, display);
			}
			switch (choice) {
			case "Queen":
				board.addPiece((side == 0) ? CoreConstants.WHITE_QUEEN : CoreConstants.BLACK_QUEEN, newPos);
				break;
			case "Rook":
				board.addPiece((side == 0) ? CoreConstants.WHITE_ROOK : CoreConstants.BLACK_ROOK, newPos);
				break;
			case "Bishop":
				board.addPiece((side == 0) ? CoreConstants.WHITE_BISHOP : CoreConstants.BLACK_BISHOP, newPos);
				break;
			case "Knight":
				board.addPiece((side == 0) ? CoreConstants.WHITE_KNIGHT : CoreConstants.BLACK_KNIGHT, newPos);
				break;
			}
		} else {
			board.addPiece(CoreConstants.QUEEN, newPos);
		}
	}

	private void moveAI(BitBoard board) {
		int colorFactor = (AI_COLOR == 0) ? EvalConstants.WHITE : EvalConstants.BLACK;
		Move move = search.rootNegamax(moveGen, board, colorFactor);
		move(board, move, true);
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

}