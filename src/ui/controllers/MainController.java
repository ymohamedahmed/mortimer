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
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController {
	// Variables loaded from the fxml file
	// Must be global so that they can be loaded from the fxml file
	public Canvas chessPane;
	public TextArea pgnTextField;
	public Slider moveSpeedSlider;

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
	private BoardColour boardColour = BoardColour.CLASSIC;

	// Called initially
	public void initialize() {
		// Intialise all the various lookup tables used by the AI
		moveGen.initialiseKnightLookupTable();
		// This allows the user to change how long it takes for the AI to select
		// moves
		moveSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue,
					Number newValue) {
				double value = moveSpeedSlider.getValue();
				EvalConstants.THINKING_TIME = EvalConstants.MAX_THINKING_TIME - (value / 100
						* (EvalConstants.MAX_THINKING_TIME - EvalConstants.MIN_THINKING_TIME));
			}
		});
		moveGen.initialiseKingLookupTable();
		moveGen.initialisePawnLookupTable();
		moveGen.initialiseBishopAndRookEvalLookupTable();
		// Lookup tables for rooks (true) and bishops (false)
		moveGen.generateMoveDatabase(true);
		moveGen.generateMoveDatabase(false);
		// Initialise the hash function
		BitBoard.initialiseZobrist();
	}

	private double paintChessBoard(BitBoard board) {
		GraphicsContext g = chessPane.getGraphicsContext2D();
		double width = chessPane.getWidth();
		double height = chessPane.getHeight();
		double cellSize = Math.ceil(Math.min(width / 8.0, height / 8.0));
		int squareNo = 0;
		// Considers each square
		while (squareNo <= 63) {
			int col = squareNo % 8;
			int row = squareNo / 8;
			// Selects the colour of the square based on the row and column
			if ((col % 2 == 0 && row % 2 == 0) || (col % 2 == 1 && row % 2 == 1)) {
				g.setFill(boardColour.getColourPrimary());
			} else {
				g.setFill(boardColour.getColourSecondary());
			}
			// Paints the square
			g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
			squareNo++;
		}

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				byte piece = board.board[(row * 8) + col];
				// If the square is not empty it draws the image based on the
				// type of piece
				if (piece != CoreConstants.EMPTY) {
					Image image = new Image(MainController.class
							.getResource("/images/" + CoreConstants.FILE_NAMES[piece] + ".png")
							.toExternalForm());
					g.drawImage(image, col * cellSize, (7 - row) * cellSize, cellSize, cellSize);
				}
			}

		}
		return cellSize;
	}

	public void playGame() {
		// If the ai is the first to move
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
		// Make sure there is an active action listener
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
						g.fillOval(colMove * cellSize, (7 - rowMove) * cellSize, cellSize,
								cellSize);
					} else {
						// If the move is a capture move draw a red circle in
						// the corner
						g.setFill(Color.RED);
						g.fillOval(colMove * cellSize, (7 - rowMove) * cellSize, cellSize / 5,
								cellSize / 5);
					}
					blueSquares.add(move.getFinalPos());
				}

			}
		}
	}

	// Returns the move based on the piece, where it came from and where it's
	// going
	public Move getMove(ArrayList<Move> moves, int piece, int oldIndex, int finalIndex) {
		for (Move move : moves) {
			if (move.getPieceType() == piece && move.getOldPos() == oldIndex
					&& move.getFinalPos() == finalIndex) {
				return move;
			}
		}
		return null;
	}

	// Gets all the moves available to a particular piece
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
		// Even piece id means white piece, odd mean black piece
		int side = move.getPieceType() % 2;
		// If the a pawn is moved onto the final row, then display the pawn
		// promotion dialog getting player which piece to convert the pawn to
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
			// Get the new moves
			moveList = getMoves(board, true);

		}
		boolean aiLost = board.checkmate(aiColor);
		boolean playerLost = board.checkmate(playerColour);
		boolean stalemate = board.stalemate(board.toMove);
		// If it is the AI's turn
		if (side == playerColour && playingAI && !aiLost) {
			moveAI(board);
		}
		// When the game is over, display the game over dialog
		if (aiLost || playerLost || stalemate) {
			String result = "";
			String sPlayerColour = (playerColour == 0) ? "WHITE" : "BLACK";
			if (aiLost) {
				result = "Congratulations you, playing with " + sPlayerColour + ", have won!";
			} else if (playerLost) {
				result = "Commiserations, you have lost.";
			} else if (stalemate) {
				result = "It is a stalemate!";
			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Game Over");
			alert.setHeaderText(null);
			alert.setContentText(result);
			alert.showAndWait();
		}

	}

	// Return all the moves available to the next player to move
	private ArrayList<Move> getMoves(BitBoard board, boolean removeCheck) {
		return moveGen.generateMoves(board, removeCheck);
	}

	// Displays a dialog giving the player the choice of which piece to convert
	// their pawn to
	private void pawnPromotion(int pawnOldPos, int newPos, int side, BitBoard board,
			boolean display) {
		board.removePiece(pawnOldPos);
		if (display) {
			// Display choices
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
				// If no valid answer, re-display the dialog
				pawnPromotion(pawnOldPos, newPos, side, board, display);
			}
			switch (choice) {
			// Based on choice add the appropriate piece
			// Note: side == 0 is equivalent to is the side white
			case "Queen":
				board.addPiece((side == 0) ? CoreConstants.WHITE_QUEEN : CoreConstants.BLACK_QUEEN,
						newPos);
				break;
			case "Rook":
				board.addPiece((side == 0) ? CoreConstants.WHITE_ROOK : CoreConstants.BLACK_ROOK,
						newPos);
				break;
			case "Bishop":
				board.addPiece(
						(side == 0) ? CoreConstants.WHITE_BISHOP : CoreConstants.BLACK_BISHOP,
						newPos);
				break;
			case "Knight":
				board.addPiece(
						(side == 0) ? CoreConstants.WHITE_KNIGHT : CoreConstants.BLACK_KNIGHT,
						newPos);
				break;
			}
		} else {
			// If it is the AI, immediately select the queen option
			board.addPiece((side == 0) ? CoreConstants.WHITE_QUEEN : CoreConstants.BLACK_QUEEN,
					newPos);
		}
	}

	private void moveAI(BitBoard board) {
		// Use the search class to select the best move
		int colorFactor = (aiColor == 0) ? EvalConstants.WHITE : EvalConstants.BLACK;
		Move move = search.rootNegamax(moveGen, board, colorFactor);
		move(board, move, true);
	}

	// PGN is the notation used to represent the moves played so far in the
	// chess game
	private void updatePGNTextField(BitBoard board, Move move, boolean capture) {
		String result = "";
		if (board.getMoveNumber() % 2 == 0) {
			result = " ";
		}
		pgnTextField.setWrapText(true);
		int side = move.getPieceType() % 2;
		int enemy = (side == 0) ? 1 : 0;
		// If white is moving start with the number of the move
		if (side == 0) {
			result += String.valueOf((board.getMoveNumber() / 2) + 1) + ". ";
		}
		// Add the letter of the piece being moved
		result += CoreConstants.pieceToLetterCapital[move.getPieceType()];
		// Capture moves get an 'x'
		if (capture) {
			result += "x";
		}
		// Convert square to algebraic notation e.g. 1 square is A1 etc.
		result += CoreConstants.indexToAlgebraic[move.getFinalPos()];

		// Castling has special notation
		if (move.getCastlingFlag() != 0) {
			if (move.getCastlingFlag() == CoreConstants.wQSide
					|| move.getCastlingFlag() == CoreConstants.bQSide) {
				result = String.valueOf((board.getMoveNumber() / 2) + 1) + "." + " O-O";
			} else {
				result = String.valueOf((board.getMoveNumber() / 2) + 1) + "." + " O-O-O";
			}
		}
		if (board.checkmate(enemy)) {
			result += "#";
		} else if (board.check(enemy)) {
			result += "+";
		}
		// New line after every two moves
		if (board.getMoveNumber() % 2 == 0 && board.getMoveNumber() != 0) {
			result += "\n";
		}
		pgnTextField
				.setText((pgnTextField.getText() == null ? "" : pgnTextField.getText()) + result);
		// Store history of the field so that undos work
		pgnHistory[board.getMoveNumber()] = pgnTextField.getText();
	}

	// The next two methods are used for loading files and saving files
	// They are called when specific buttons are pressed
	// FILE FORMAT:
	// Save who is next to move
	// Save the number of moves made so far
	// Then save the board array by index order
	// Then save all the bitboards for each piece by piece id order
	// Then save all the history arrays in order from the first move to the
	// latest move
	// Save the state of castling
	// Save the current pgn
	// Save the colour of the player
	// Save the colour of the AI

	// NOTE: loading is done in the same order
	@FXML
	private void handleLoadFileAction(ActionEvent event) {
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

				for (int i = 0; i < noOfMoves - 1; i++) {
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
				alert.setContentText(
						"Ooops, there was an error whilst loading the save game file!");
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

	// File format is outlined above, text file is build up and then written to
	// a file
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
		Stage stage = Main.primaryStage;
		File file = fileChooser.showSaveDialog(stage);

		BufferedWriter writer = null;
		if (file != null) {
			try {
				writer = new BufferedWriter(
						new FileWriter(file + (!file.getName().endsWith(".txt") ? ".txt" : "")));
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

	// Method is called when the user presses the restart game button
	@FXML
	private void restartGame(ActionEvent event) {
		setupGame();
		playGame();
	}

	// Simply undo the game twice to get back to the player's move
	// Also undo the PGN notation
	// Then re-generate the moves available
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

	// Load FEN notation so that users can easily change the board
	// Some users may wish to import boards from other programs
	// This makes it easy
	@FXML
	private void loadFenMenuItem(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Input FEN Notation");
		dialog.setContentText("Please enter FEN of board to be loaded");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			try {
				// Change the board
				board.loadFen(result.get());
				// Regenerate moves
				moveList = moveGen.generateMoves(board, true);
				pgnTextField.setText("");
				clearCanvas();
				paintChessBoard(board);
			} catch (Exception e) {
				// If there is an error, notify the user
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setContentText("Ooops, there was an error whilst loading the FEN notation. "
						+ "Are you sure the notation is valid?");
				alert.showAndWait();
				e.printStackTrace();
			}
		}

	}

	// Export the FEN of the current board
	// Executed when a user clicks a button in the menu bar
	@FXML
	private void exportFenMenuItem(ActionEvent event) {
		String fen = board.exportFen();
		TextInputDialog alert = new TextInputDialog(fen);
		alert.setTitle("Export FEN");
		alert.setHeaderText("The FEN notation for the current board");
		alert.getEditor().setEditable(false);
		alert.showAndWait();
	}

	// Executed when the user chooses to change the theme of the board
	@FXML
	private void boardColourMenuItem(ActionEvent event) {
		// The user chooses from three options
		List<String> choices = new ArrayList<>();
		choices.add("Classic");
		choices.add("Moss Green");
		choices.add("Grey");

		ChoiceDialog<String> dialog = new ChoiceDialog<>(boardColour.getColourName(), choices);
		dialog.setHeaderText("Choose a Colour Theme");
		dialog.setTitle("Choose Board Colour");

		Optional<String> result = dialog.showAndWait();
		// Based on the response change the enum value
		if (result.isPresent()) {
			switch (result.get()) {
			case "Classic":
				boardColour = BoardColour.CLASSIC;
				break;
			case "Moss Green":
				boardColour = BoardColour.MOSS_GREEN;
				break;
			case "Grey":
				boardColour = BoardColour.GREY;
				break;
			}
		}
		clearCanvas();
		paintChessBoard(board);
	}

	// Used by the Start Menu Controller to change the settings of the game
	public void setPlayingAI(boolean playingAI) {
		this.playingAI = playingAI;
	}

	public void setPlayerColour(int color) {
		playerColour = color;
	}

	public void setAIColour(int color) {
		aiColor = color;
	}

	// Enumeration for the board colour
	// Stores the two colours used in theme
	// Classic theme is default
	private enum BoardColour {
		CLASSIC, MOSS_GREEN, GREY;
		public String getColourName() {
			if (this == BoardColour.CLASSIC) {
				return "Classic";
			} else if (this == BoardColour.MOSS_GREEN) {
				return "Moss Green";
			} else if (this == BoardColour.GREY) {
				return "Grey";
			} else {
				return "";
			}
		}

		// Colour for the half the squares
		public Color getColourPrimary() {
			if (this == BoardColour.CLASSIC) {
				return Color.rgb(140, 82, 66);
			} else if (this == BoardColour.MOSS_GREEN) {
				return Color.rgb(175, 212, 144);
			} else if (this == BoardColour.GREY) {
				return Color.rgb(167, 171, 164);
			} else {
				return BoardColour.CLASSIC.getColourPrimary();
			}
		}

		// Colour for the other half of the squares
		public Color getColourSecondary() {
			if (this == BoardColour.CLASSIC) {
				return Color.rgb(255, 255, 206);
			} else if (this == BoardColour.MOSS_GREEN) {
				return Color.rgb(255, 255, 255);
			} else if (this == BoardColour.GREY) {
				return Color.rgb(255, 255, 255);
			} else {
				return BoardColour.CLASSIC.getColourSecondary();
			}
		}

	}

}