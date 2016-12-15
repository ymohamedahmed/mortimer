package ui.controllers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import core.Bishop;
import core.BitBoard;
import core.Board;
import core.King;
import core.Knight;
import core.Move;
import core.Pawn;
import core.Piece;
import core.PieceColor;
import core.PieceType;
import core.Position;
import core.Queen;
import core.Rook;
import dme.Evaluation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class MainController {
	private final PieceColor PLAYER_COLOR = PieceColor.BLACK;
	// Variables loaded from the fxml file
	// Must be global so that they can be loaded from the fxml file
	public StackPane stackPane;
	public Canvas chessPane;
	public BorderPane borderPane;
	private ArrayList<Position> blueSquares = new ArrayList<>();
	private Position oldPos;
	private PieceColor AI_COLOR = PieceColor.WHITE;
	private Hashtable<Integer, TranspositionEntry> hashtable = new Hashtable<>();

	// DEBUGGING
	private int noOfMovesAnalyzed = 0;
	private double cloneTime = 0;
	private double updateTime = 0;
	private int iClone = 0;
	private int iUpdate = 0;
	private double[] updateTimeArr = new double[6];
	private int[] iUpdateArr = new int[6];

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
				Image image = new Image(MainController.class
						.getResource("/images/" + String.valueOf(board.board[(8*row)+col]) + ".png").toExternalForm());
				g.drawImage(image, col * cellSize,
                        (7 - row) * cellSize, cellSize, cellSize);
			}

		}
		return cellSize;
	}

	private void playGame() {
		BitBoard board = new BitBoard();
		board.resetToInitialSetup();
		double cellSize = paintChessBoard(board);
		chessPane.setOnMouseClicked(evt -> clickListenerChessPane(board, evt, cellSize));
		if (AI_COLOR == PieceColor.WHITE) {
			moveAI(board);
		}
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
		Piece piece = Board.getPiece(pieceList, new Position(row, column));

		for (Position square : blueSquares) {
			if (square.getRow() == row && square.getCol() == column) {
				move(pieceList, getMove(Board.getPiece(pieceList, oldPos).getMovesList(), new Position(row, column)),
						true);
				blueSquares.clear();
				pieceMoved = true;
				break;
			}
		}

		// Clicks square with piece in it
		if (piece != null && !pieceMoved) {
			// Get its available moves
			ArrayList<Move> moves = piece.getMovesList();
			oldPos = piece.getPos();
			// Clear the canvas and then repaint it
			clearCanvas();
			paintChessBoard(pieceList);
			GraphicsContext g = chessPane.getGraphicsContext2D();
			// Show available moves by painting a blue circle in the cells
			blueSquares.clear();
			for (Move move : moves) {
				if (!move.isCapture()) {
					g.setFill(Color.BLUE);
					g.fillOval((move.getPosition().getCol()) * cellSize, (7 - move.getPosition().getRow()) * cellSize,
							cellSize, cellSize);
				} else {
					g.setFill(Color.RED);
					g.fillOval((move.getPosition().getCol()) * cellSize, (7 - move.getPosition().getRow()) * cellSize,
							cellSize / 5, cellSize / 5);
				}
				blueSquares.add(move.getPosition());
			}

		}
	}

	private Move rootNegamax(ArrayList<Piece> pieceList, PieceColor color) {
		int depth = 2;
		double maxScore = Double.NEGATIVE_INFINITY;
		Move bestMove = null;
		ArrayList<Move> possibleMoves = getAllMovesColor(pieceList, color);
		double startTime = System.currentTimeMillis();
		noOfMovesAnalyzed = 0;
		noOfMovesAnalyzed += possibleMoves.size();
		for (Move move : possibleMoves) {
			ArrayList<Piece> pieceListTemp = clonePieceList(pieceList);
			pieceListTemp = move(pieceListTemp, move, false);
			updateMoveList(pieceListTemp, false);
			double score = negamax(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, pieceListTemp, depth,
					color.getColorFactor());
			System.out.println("SCORE: " + score);
			if (score > maxScore) {
				maxScore = score;
				bestMove = move;
			}
		}
		double endTime = System.currentTimeMillis();
		// DEBUGGING
		System.out.println("TIME TO SELECT: " + (endTime - startTime));
		// System.out.println("NUMBER OF MOVES ANALYZED " + noOfMovesAnalyzed);
		System.out.println("CLONE AVG: " + (cloneTime / iClone));
		System.out.println("UPDATE AVG: " + (updateTime / iUpdate));
		System.out.println("CLONE TIME: " + cloneTime);
		System.out.println("UPDATE TIME: " + updateTime);
		System.out.println("SIZE HASH TABLE : " + hashtable.size());
		for (int i = 0; i <= 5; i++) {
			System.out.println("AVG TIME " + i + " : " + (updateTimeArr[i] / iUpdateArr[i]));
			System.out.println("TOTAL TIME " + i + " : " + updateTimeArr[i]);
		}
		return bestMove;
	}

	private double negamax(double alpha, double beta, ArrayList<Piece> pieceList, int depth, int colorFactor) {
		double alphaOrig = alpha;
		TranspositionEntry transpositionEntry = new TranspositionEntry();
		// Checking the transposition table
		transpositionEntry = hashtable.get(Board.hash(pieceList));

		if (transpositionEntry != null) {
			if (transpositionEntry.getDepth() >= depth) {
				if (transpositionEntry.getFlag() == TranspositionFlag.EXACT) {
					return transpositionEntry.getScore();
				} else if (transpositionEntry.getFlag() == TranspositionFlag.LOWERBOUND) {
					alpha = Math.max(alpha, transpositionEntry.getScore());
				} else if (transpositionEntry.getFlag() == TranspositionFlag.UPPERBOUND) {
					beta = Math.min(beta, transpositionEntry.getScore());
				}
			}
		}

		if (depth == 0) {
			return colorFactor
					* (new Evaluation().totalEvaluation(pieceList, PieceColor.getColorByFactor(colorFactor)));
		}
		double bestValue = Double.NEGATIVE_INFINITY;
		ArrayList<Move> possibleMoves = getAllMovesColor(pieceList, PieceColor.getColorByFactor(colorFactor));
		possibleMoves = sortMoves(pieceList, possibleMoves);
		noOfMovesAnalyzed += possibleMoves.size();
		for (Move move : possibleMoves) {
			ArrayList<Piece> pieceListTemp = clonePieceList(pieceList);
			pieceListTemp = move(pieceListTemp, move, false);
			updateMoveList(pieceListTemp, false);
			double v = -negamax(-beta, -alpha, pieceListTemp, depth - 1, -1 * colorFactor);
			bestValue = Math.max(bestValue, v);
			alpha = Math.max(alpha, v);
			if (alpha >= beta) {
				break;
			}
		}

		TranspositionEntry transpositionEntryFinal = new TranspositionEntry();
		transpositionEntryFinal.setScore(bestValue);
		if (bestValue <= alphaOrig) {
			transpositionEntryFinal.setFlag(TranspositionFlag.UPPERBOUND);
		} else if (bestValue >= beta) {
			transpositionEntryFinal.setFlag(TranspositionFlag.LOWERBOUND);
		} else {
			transpositionEntryFinal.setFlag(TranspositionFlag.EXACT);
		}
		transpositionEntryFinal.setDepth(depth);
		transpositionEntryFinal.setValid(true);
		hashtable.put(Board.hash(pieceList), transpositionEntryFinal);

		return bestValue;
	}

	private ArrayList<Move> sortMoves(ArrayList<Piece> pieceList, ArrayList<Move> possibleMoves) {
		// Calculating score for each move
		ArrayList<Move> sortedMoves = new ArrayList<>();
		ArrayList<MoveScore> movesScore = new ArrayList<>();
		for (Move move : possibleMoves) {
			ArrayList<Piece> pieceListTemp = clonePieceList(pieceList);
			move(pieceListTemp, move, false);
			MoveScore moveScore = new MoveScore(move,
					new Evaluation().totalEvaluation(pieceListTemp, move.getPiece().getColor()));
			movesScore.add(moveScore);
		}
		ArrayList<MoveScore> sorted = quickSort(movesScore);
		sorted.forEach(e -> sortedMoves.add(e.getMove()));
		return sortedMoves;
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

	private ArrayList<Move> getAllMovesColor(ArrayList<Piece> pieceList, PieceColor color) {
		ArrayList<Move> possibleMoves = new ArrayList<>();
		for (Piece piece : pieceList) {
			if (piece.getColor() == color) {
				possibleMoves.addAll(piece.getMovesList());
			}
		}
		return possibleMoves;
	}

	public ArrayList<Piece> move(ArrayList<Piece> pieceList, Move move, boolean repaint) {
		Piece pieceCaptured = null;
		PieceColor colorMoved = null;
		try {
			Piece piece = Board.getPiece(pieceList,
					new Position(move.getPiece().getPos().getRow(), move.getPiece().getPos().getCol()));
			colorMoved = move.getPiece().getColor();
			// Checks if the move selected is a castling move
			// If so changes the position of the rook based on whether
			// it is Queenside or Kingside castling
			if (move.isCastling()) {
				Position rookNewPosition = null;
				Position rookOldPosition = null;
				if (move.getPosition().getCol() == 6) {
					rookNewPosition = new Position(move.getPosition().getRow(), 5);
					rookOldPosition = (piece.getColor() == PieceColor.WHITE) ? new Position(0, 7) : new Position(7, 7);
				} else if (move.getPosition().getCol() == 2) {
					rookNewPosition = new Position(move.getPosition().getRow(), 3);
					rookOldPosition = (piece.getColor() == PieceColor.WHITE) ? new Position(0, 0) : new Position(7, 0);
				}
				Piece rook = Board.getPiece(pieceList, rookOldPosition);
				rook.setPosition(rookNewPosition);

				((King) piece).setNoOfCastleMoves(((King) piece).getNoOfCastleMoves() + 1);
			}
			// If the move results in pawn promotion display the pawn promotion
			// dialog
			if (piece.getPieceType() == PieceType.PAWN) {
				if (((Pawn) piece).pawnPromotion(move.getPosition(), pieceList)) {
					if (piece.getColor() == AI_COLOR && repaint) {
						Position newPiecePosition = new Position(
								piece.getPos().getRow() + piece.getColor().getColorFactor(), piece.getPos().getCol());
						pieceList.add(new Queen(newPiecePosition, piece.getColor(), piece.getNumberOfMoves()));
						pieceList.remove(piece);
					} else if (piece.getColor() == PLAYER_COLOR && repaint) {
						displayPawnPromotionDialog(piece, pieceList);
					}
				}
			}
			// If it is a capture move, the piece to be captured is found
			if (move.isCapture()) {
				pieceCaptured = Board.getPiece(pieceList, move.getPosition());
			}
			// Finds the piece captured during en passant
			if (move.isEnPassant()) {
				pieceCaptured = Board.getPiece(pieceList, new Position(
						move.getPosition().getRow() - piece.getColor().getColorFactor(), move.getPosition().getCol()));
			}

			pieceList.remove(pieceCaptured);
			piece.setNumberOfMoves(piece.getNumberOfMoves() + 1);
			piece.setPosition(move.getPosition());

		} catch (NullPointerException e) {
		}
		if (repaint) {
			// Clear the canvas and then repaint it
			clearCanvas();
			paintChessBoard(pieceList);
			updateMoveList(pieceList, true);
		}
		if (colorMoved == PLAYER_COLOR && colorMoved != null && repaint) {
			moveAI(pieceList);
		}
		return pieceList;
	}

	private Move getMove(ArrayList<Move> moves, Position finalPosition) {
		Move moveFound = null;
		for (Move move : moves) {
			if (move.getPosition().getRow() == finalPosition.getRow()
					&& move.getPosition().getCol() == finalPosition.getCol()) {
				moveFound = move;
			}
		}
		return moveFound;
	}

	private void moveAI(BitBoard board) {
		updateMoveList(pieceList, true);
		Move moveSelected = rootNegamax(pieceList, AI_COLOR);
		System.out.println("COLOR AI MOVE: " + moveSelected.getPiece().getColor());
		System.out.println("AI MOVE : " + moveSelected.getPiece().getPieceType() + " "
				+ moveSelected.getPiece().getColor() + " to (" + moveSelected.getPosition().getRow() + " , "
				+ moveSelected.getPosition().getCol() + " )");
		move(pieceList, moveSelected, true);
	}

	public void updateMoveList(ArrayList<Piece> pieceList, boolean removeCheck) {
		double before = System.currentTimeMillis();

		for (Piece piece : pieceList) {
			piece.setMovesList(getMoves(piece, pieceList, removeCheck));
		}
		updateTime += (System.currentTimeMillis() - before);
		iUpdate++;
	}

	private ArrayList<Move> getMoves(Piece piece, ArrayList<Piece> pieceList, boolean removeCheck) {
		ArrayList<Move> legalMoves = new ArrayList<Move>();
		PieceType type = piece.getPieceType();
		double before = System.currentTimeMillis();
		if (type == PieceType.PAWN) {
			legalMoves = ((Pawn) piece).getLegalMoves(pieceList);
			updateTimeArr[0] += (System.currentTimeMillis() - before);
			iUpdateArr[0]++;
		} else if (type == PieceType.KNIGHT) {
			legalMoves = ((Knight) piece).getLegalMoves(pieceList);
			updateTimeArr[1] += (System.currentTimeMillis() - before);
			iUpdateArr[1]++;
		} else if (type == PieceType.BISHOP) {
			legalMoves = ((Bishop) piece).getLegalMoves(pieceList);
			updateTimeArr[2] += (System.currentTimeMillis() - before);
			iUpdateArr[2]++;
		} else if (type == PieceType.ROOK) {
			legalMoves = ((Rook) piece).getLegalMoves(pieceList);
			updateTimeArr[3] += (System.currentTimeMillis() - before);
			iUpdateArr[3]++;
		} else if (type == PieceType.QUEEN) {
			legalMoves = ((Queen) piece).getLegalMoves(pieceList);
			updateTimeArr[4] += (System.currentTimeMillis() - before);
			iUpdateArr[4]++;
		} else if (type == PieceType.KING) {
			legalMoves = ((King) piece).getLegalMoves(pieceList);
			updateTimeArr[5] += (System.currentTimeMillis() - before);
			iUpdateArr[5]++;
		}
		ArrayList<Move> captureRecognised = recogniseCaptureMoves(piece, pieceList, legalMoves);
		return removeCheck ? removeIllegalMoves(piece, pieceList, piece.getColor(), captureRecognised)
				: removePositionsOffBoard(captureRecognised);
	}

	private ArrayList<Move> removeIllegalMoves(Piece piece, ArrayList<Piece> pieceList, core.PieceColor color,
			ArrayList<Move> possibleMoves) {
		ArrayList<Move> intermediate = removeMovesToKing(pieceList, removePositionsOffBoard(possibleMoves));
		return removeCheckMoves(piece, pieceList, color, intermediate);
	}

	private ArrayList<Move> removePositionsOffBoard(ArrayList<Move> moves) {
		// Iterator has to be used to avoid concurrent modification exception
		// i.e. so that we can remove from the arraylist as we loop through it
		Iterator<Move> iter = moves.iterator();
		while (iter.hasNext()) {
			Move move = iter.next();
			if (move.getPosition().getRow() > 7 || move.getPosition().getRow() < 0 || move.getPosition().getCol() > 7
					|| move.getPosition().getCol() < 0) {
				iter.remove();
			}
		}
		return moves;
	}

	private ArrayList<Move> removeMovesToKing(ArrayList<Piece> pieceList, ArrayList<Move> moves) {
		// Iterator has to be used to avoid concurrent modification exception
		// i.e. so that we can remove from the arraylist as we loop through it
		Iterator<Move> iter = moves.iterator();
		while (iter.hasNext()) {
			Move move = iter.next();
			try {
				Piece piece = Board.getPiece(pieceList, move.getPosition());
				if (piece.getPieceType() == PieceType.KING) {
					iter.remove();
				}
			} catch (NullPointerException e) {
			}
		}
		return moves;
	}

	private ArrayList<Move> removeCheckMoves(Piece piece, ArrayList<Piece> pieceList, core.PieceColor color,
			ArrayList<Move> possibleMoves) {
		Piece king = null;

		// Iterator has to be used to avoid concurrent modification exception
		// i.e. so that we can remove from the arraylist as we loop through it
		Iterator<Move> moveIterator = possibleMoves.iterator();
		Position oldPosition = piece.getPos();

		// Extracting the King from the array of pieces
		for (Piece pieceLoop : pieceList) {
			if (pieceLoop.getPieceType() == PieceType.KING && pieceLoop.getColor() == color) {
				king = pieceLoop;
			}
		}
		// Consider the case when the piece variable is the king in check
		boolean kingInCheck = piece.getPieceType() == PieceType.KING && piece.getColor() == color;

		// Loop through moves available to a piece
		// If any of the moves result in check remove them
		while (moveIterator.hasNext()) {
			Move move = moveIterator.next();
			ArrayList<Piece> pieceListTemp = clonePieceList(pieceList);
			Piece pieceTemp = Board.getPiece(pieceListTemp, piece.getPos());
			if (move.isCapture()) {
				Piece capPiece = Board.getPiece(pieceListTemp, move.getPosition());
				pieceListTemp.remove(capPiece);
			}
			pieceTemp.setPosition(move.getPosition());
			pieceTemp.setNumberOfMoves(piece.getNumberOfMoves() + 1);
			updateMoveList(pieceListTemp, false);
			if (((King) king).check(pieceListTemp, (kingInCheck) ? move.getPosition() : king.getPos())) {
				moveIterator.remove();
			}
			pieceTemp.setPosition(oldPosition);
			pieceTemp.setNumberOfMoves(piece.getNumberOfMoves() - 1);
		}

		return possibleMoves;

	}

	private ArrayList<Piece> clonePieceList(ArrayList<Piece> pieceList) {
		double before = System.currentTimeMillis();
		ArrayList<Piece> clonedList = new ArrayList<>();
		for (Piece piece : pieceList) {
			PieceType type = piece.getPieceType();
			if (type == PieceType.PAWN) {
				clonedList.add(new Pawn(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
			} else if (type == PieceType.KNIGHT) {
				clonedList.add(new Knight(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
			} else if (type == PieceType.BISHOP) {
				clonedList.add(new Bishop(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
			} else if (type == PieceType.ROOK) {
				clonedList.add(new Rook(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
			} else if (type == PieceType.QUEEN) {
				clonedList.add(new Queen(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
			} else if (type == PieceType.KING) {
				clonedList.add(new King(piece.getPos(), piece.getColor(), piece.getNumberOfMoves()));
			}
		}
		cloneTime += (System.currentTimeMillis() - before);
		iClone++;
		return clonedList;
	}

	private void displayPawnPromotionDialog(Piece pawn, ArrayList<Piece> pieceList) {
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
			displayPawnPromotionDialog(pawn, pieceList);
		}
		Position newPosition = new Position(pawn.getPos().getRow() + pawn.getColor().getColorFactor(),
				pawn.getPos().getCol());
		switch (choice) {
		case "Queen":
			pieceList.add(new Queen(newPosition, pawn.getColor(), pawn.getNumberOfMoves()));
			break;
		case "Rook":
			pieceList.add(new Rook(newPosition, pawn.getColor(), pawn.getNumberOfMoves()));
			break;
		case "Bishop":
			pieceList.add(new Bishop(newPosition, pawn.getColor(), pawn.getNumberOfMoves()));
			break;
		case "Knight":
			pieceList.add(new Knight(newPosition, pawn.getColor(), pawn.getNumberOfMoves()));
			break;
		}
		pieceList.remove(pawn);
	}

	private ArrayList<Move> recogniseCaptureMoves(Piece piece, ArrayList<Piece> pieceList, ArrayList<Move> moves) {
		for (Move move : moves) {
			try {
				Piece capturePiece = Board.getPiece(pieceList, move.getPosition());
				if (capturePiece.getColor() != piece.getColor() && capturePiece.getPieceType() != PieceType.KING) {
					move.setCapture(true);
				} else {
					move.setCapture(false);
				}

			} catch (NullPointerException e) {
				move.setCapture(false);
			}

		}

		return moves;

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