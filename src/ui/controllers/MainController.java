package ui.controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import core.BitBoard;
import core.CoreConstants;
import core.Main;
import core.Move;
import core.MoveGen;
import eval.EvalConstants;
import eval.Search;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
	// Variables loaded from the fxml file
	// Must be global so that they can be loaded from the fxml file
	public Canvas chessPane;
	public BorderPane borderPane;
	public TextArea pgnTextField;
	@FXML
	public Slider moveSpeedSlider;
	public Slider moveStrengthSlider;

	private int playerColour = CoreConstants.WHITE;
	private int aiColor = CoreConstants.BLACK;
	private ArrayList<Integer> blueSquares = new ArrayList<>();
	private int oldPos;
	private ArrayList<Move> moveList = new ArrayList<>();
	private Search search = new Search();
	private BitBoard board;
	private MoveGen moveGen = new MoveGen();
	private String[] pgnHistory = new String[CoreConstants.MAX_MOVES];
	private boolean playingAI = true;

	public void initialize() {
		System.out.println("INITIALIZE");
		moveGen.initialiseKnightLookupTable();
		moveSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double value = moveSpeedSlider.getValue();
				EvalConstants.THINKING_TIME = EvalConstants.MAX_THINKING_TIME
						- (value / 100 * (EvalConstants.MAX_THINKING_TIME - EvalConstants.MIN_THINKING_TIME));
			}
		});
		moveGen.initialiseKingLookupTable();
		moveGen.initialisePawnLookupTable();
		moveGen.initialiseBishopAndRookEvalLookupTable();
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);
		BitBoard.initialiseZobrist();
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

	public void playGame() {
		if (aiColor == CoreConstants.WHITE) {
			moveAI(board);
		}
	}

	public void setupGame() {
		board = new BitBoard();
		board.resetToInitialSetup();
		pgnTextField.setText("");
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
		double x = evt.getX();
		double y = evt.getY();
		int column = (int) Math.floor(x / cellSize);
		int row = 7 - (int) Math.floor(y / cellSize);
		boolean pieceMoved = false;
		// Get the piece that was clicked
		int index = (8 * row) + column;
		if (index < 64 && x <= (8 * cellSize) && y <= (8 * cellSize)) {
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
		boolean capture = board.board[move.getFinalPos()] != CoreConstants.EMPTY;
		board.move(move);
		int side = move.getPieceType() % 2;
		if (move.isPromotion() && side == playerColour) {
			pawnPromotion(move.getOldPos(), move.getFinalPos(), side, board, true);
		} else if (move.isPromotion() && side == aiColor) {
			pawnPromotion(move.getOldPos(), move.getFinalPos(), side, board, false);
		}
		updatePGNTextField(board, move, capture);
		if (repaint) {
			// Clear the canvas and then repaint it
			clearCanvas();
			paintChessBoard(board);
			moveList = getMoves(board, true);

		}
		if (side == playerColour && playingAI) {
			moveAI(board);
		}
		if (board.checkmate(0) || board.checkmate(1)) {

		}

	}

	private ArrayList<Move> getMoves(BitBoard board, boolean removeCheck) {
		return moveGen.generateMoves(board, removeCheck);
	}

	private void pawnPromotion(int pawnOldPos, int newPos, int side, BitBoard board, boolean display) {
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
			board.addPiece((side == 0) ? CoreConstants.WHITE_QUEEN : CoreConstants.BLACK_QUEEN, newPos);
		}
	}

	private void moveAI(BitBoard board) {
		int colorFactor = (aiColor == 0) ? EvalConstants.WHITE : EvalConstants.BLACK;
		Move move = search.rootNegamax(moveGen, board, colorFactor);
		move(board, move, true);
	}

	private void updatePGNTextField(BitBoard board, Move move, boolean capture) {
		String result = " ";
		if (board.getMoveNumber() == 1) {
			result = "";
		}
		pgnTextField.setWrapText(true);
		int side = move.getPieceType() % 2;
		int enemy = (side == 0) ? 1 : 0;
		if (side == 0) {
			result += String.valueOf((board.getMoveNumber() / 2) + 1) + ". ";
		}
		result += CoreConstants.pieceToLetter[move.getPieceType()];
		if (capture) {
			result += "x";
		}
		result += CoreConstants.indexToAlgebraic[move.getFinalPos()];

		if (move.getCastlingFlag() != 0) {
			if (move.getCastlingFlag() == CoreConstants.wQSide || move.getCastlingFlag() == CoreConstants.bQSide) {
				result = " O-O";
			} else {
				result = " O-O-O";
			}
		}
		if (board.checkmate(enemy)) {
			result += "#";
		} else if (board.check(enemy)) {
			result += "+";
		}
		pgnTextField.setText((pgnTextField.getText() == null ? "" : pgnTextField.getText()) + result);
		pgnHistory[board.getMoveNumber()] = pgnTextField.getText();
	}
	// File Format
	// Current Bitboards in order
	// History sorted by move number

	@FXML
	public void handleLoadFileAction(ActionEvent event) {
		System.out.println("LOADING GAME");
		Stage stage = Main.primaryStage;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Game");
		File file = fileChooser.showOpenDialog(stage);
		BufferedReader reader = null;
		if (file != null) {
			try {
				reader = new BufferedReader(new FileReader(file));
				board.toMove = Integer.valueOf(reader.readLine());
				int noOfMoves = Integer.valueOf(reader.readLine());
				board.setMoveNumber(noOfMoves);
				for (int i = 0; i <= 63; i++) {
					board.board[i] = (byte) ((int) Integer.valueOf(reader.readLine()));
				}
				for (int i = 0; i <= 13; i++) {
					board.bitboards[i] = Long.valueOf(reader.readLine());
				}

				for (int i = 0; i < noOfMoves; i++) {
					board.moveHistory[i] = Long.valueOf(reader.readLine());
					board.whiteHistory[i] = Long.valueOf(reader.readLine());
					board.blackHistory[i] = Long.valueOf(reader.readLine());
					board.pawnHistory[0][i] = Long.valueOf(reader.readLine());
					board.pawnHistory[1][i] = Long.valueOf(reader.readLine());
					board.rookHistory[0][i] = Long.valueOf(reader.readLine());
					board.rookHistory[1][i] = Long.valueOf(reader.readLine());
					board.queenHistory[0][i] = Long.valueOf(reader.readLine());
					board.queenHistory[1][i] = Long.valueOf(reader.readLine());
					board.bishopHistory[0][i] = Long.valueOf(reader.readLine());
					board.bishopHistory[1][i] = Long.valueOf(reader.readLine());
					board.knightHistory[0][i] = Long.valueOf(reader.readLine());
					board.knightHistory[1][i] = Long.valueOf(reader.readLine());
					board.kingHistory[0][i] = Long.valueOf(reader.readLine());
					board.kingHistory[1][i] = Long.valueOf(reader.readLine());
					board.boardHistory[0][i] = Byte.valueOf(reader.readLine());
					board.boardHistory[1][i] = Byte.valueOf(reader.readLine());
					board.castlingHistory[0][i] = Long.valueOf(reader.readLine());
					board.castlingHistory[1][i] = Long.valueOf(reader.readLine());
					board.epHistory[0][i] = Long.valueOf(reader.readLine());
					board.epHistory[1][i] = Long.valueOf(reader.readLine());
				}
				board.castling[0] = Integer.valueOf(reader.readLine());
				board.castling[1] = Integer.valueOf(reader.readLine());
				pgnTextField.setText(reader.readLine());
				playerColour = Integer.valueOf(reader.readLine());
				aiColor = Integer.valueOf(reader.readLine());
				clearCanvas();
				paintChessBoard(board);
				moveList = moveGen.generateMoves(board, true);
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setContentText("Ooops, there was an error whilst loading the save game file!");
				alert.showAndWait();
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	@FXML
	private void handleSaveFileAction(ActionEvent event) {
		String result = "";
		int noOfMoves = board.getMoveNumber();
		result += board.toMove + "\n";
		result += String.valueOf(noOfMoves) + "\n";
		for (int i = 0; i <= 63; i++) {
			result += board.board[i] + "\n";
		}
		for (int i = 0; i <= 13; i++) {
			result += String.valueOf(board.bitboards[i]) + "\n";
		}
		for (int i = 0; i < noOfMoves; i++) {
			result += String.valueOf(board.moveHistory[i]) + "\n";
			result += String.valueOf(board.whiteHistory[i]) + "\n";
			result += String.valueOf(board.blackHistory[i]) + "\n";
			result += String.valueOf(board.pawnHistory[0][i]) + "\n";
			result += String.valueOf(board.pawnHistory[1][i]) + "\n";
			result += String.valueOf(board.rookHistory[0][i]) + "\n";
			result += String.valueOf(board.rookHistory[1][i]) + "\n";
			result += String.valueOf(board.queenHistory[0][i]) + "\n";
			result += String.valueOf(board.queenHistory[1][i]) + "\n";
			result += String.valueOf(board.bishopHistory[0][i]) + "\n";
			result += String.valueOf(board.bishopHistory[1][i]) + "\n";
			result += String.valueOf(board.knightHistory[0][i]) + "\n";
			result += String.valueOf(board.knightHistory[1][i]) + "\n";
			result += String.valueOf(board.kingHistory[0][i]) + "\n";
			result += String.valueOf(board.kingHistory[1][i]) + "\n";
			result += String.valueOf(board.boardHistory[0][i]) + "\n";
			result += String.valueOf(board.boardHistory[1][i]) + "\n";
			result += String.valueOf(board.castlingHistory[0][i]) + "\n";
			result += String.valueOf(board.castlingHistory[1][i]) + "\n";
			result += String.valueOf(board.epHistory[0][i]) + "\n";
			result += String.valueOf(board.epHistory[1][i]) + "\n";
		}
		result += board.castling[0] + "\n";
		result += board.castling[1] + "\n";
		result += pgnTextField.getText() + "\n";
		result += String.valueOf(playerColour) + "\n";
		result += String.valueOf(aiColor);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Game");
		Stage stage = (Stage) borderPane.getScene().getWindow();
		File file = fileChooser.showSaveDialog(stage);

		BufferedWriter writer = null;
		if (file != null) {
			try {
				writer = new BufferedWriter(new FileWriter(file + (!file.getName().endsWith(".txt") ? ".txt" : "")));
				writer.write(result);
			} catch (IOException ie) {
				ie.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@FXML
	private void restartGame(ActionEvent event) {
		setupGame();
		playGame();
	}

	@FXML
	private void undoAction(ActionEvent event) {
		if (board.getMoveNumber() >= 2) {
			board.undo();
			board.undo();
			pgnTextField.setText(pgnHistory[board.getMoveNumber()]);
			clearCanvas();
			paintChessBoard(board);
			moveList = moveGen.generateMoves(board, true);
		}
	}

	public void setPlayingAI(boolean playingAI) {
		this.playingAI = playingAI;
	}

	public void setPlayerColour(int color) {
		playerColour = color;
	}

	public void setAIColour(int color) {
		aiColor = color;
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