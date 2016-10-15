package ui.controllers;

import engine.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;

enum Mode {
    SelectMove, MakeMove
}

enum CellColor {
    COLOR1(Color.rgb(140, 82, 66)), COLOR2(Color.rgb(255, 255, 206));
    private Color color;

    CellColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}

public class MainController {
    // Variables loaded from the fxml file
    // Must be global so that they can be loaded from the fxml file
    public StackPane stackPane;
    public Canvas chessPane;
    public BorderPane borderPane;

    private Stage primaryStage;
    private ArrayList<Position> blueSquares = new ArrayList<>();
    private Position oldPos;

    public void initialize() {
        playGame();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private double paintChessBoard(ArrayList<Piece> pieceList) {
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

        for (Piece piece : pieceList) {
            g.drawImage(Piece.getImage(piece.getPieceType(), piece.getColor()),
                    (piece.getPos().getCol()) * cellSize, (7 - piece.getPos().getRow()) * cellSize, cellSize,
                    cellSize);
        }

        return cellSize;
    }

    private ArrayList<Move> removePositionsOffBoard(ArrayList<Move> moves) {
        // Iterator has to be used to avoid concurrent modification exception
        // i.e. so that we can remove from the arraylist as we loop through it
        Iterator<Move> iter = moves.iterator();
        while (iter.hasNext()) {
            Move move = iter.next();
            if (move.getPosition().getRow() > 7 || move.getPosition().getRow() < 0 || move.getPosition().getCol() > 7
                    || move.getPosition().getRow() < 0) {
                iter.remove();
            }
        }
        return moves;
    }

    private void resizingCanvas(ArrayList<Piece> pieceList) {
        chessPane.widthProperty().bind(stackPane.widthProperty());
        chessPane.heightProperty().bind(stackPane.heightProperty());
        chessPane.widthProperty().addListener(observable -> paintChessBoard(pieceList));
        chessPane.heightProperty().addListener(observable -> paintChessBoard(pieceList));
    }

    private void playGame() {
        ArrayList<Piece> pieceList = Piece.getInitialPieceList();
        Mode mode = Mode.SelectMove;
        double cellSize = paintChessBoard(pieceList);
        chessPane.setOnMouseClicked(evt -> clickListenerChessPane(pieceList, evt, cellSize));
    }

    private void clearCanvas() {
        GraphicsContext g = chessPane.getGraphicsContext2D();
        g.clearRect(0, 0, chessPane.getWidth(), chessPane.getHeight());
    }

    private void clickListenerChessPane(ArrayList<Piece> pieceList, MouseEvent evt, double cellSize) {
        //System.out.println("CLICK");
        //Get the position clicked in terms of the board
        int column = (int) Math.floor(evt.getX() / cellSize);
        int row = 7 - (int) Math.floor(evt.getY() / cellSize);

        //Get the piece that was clicked
        Piece piece = Board.getPiece(pieceList, new Position(row, column));


        //System.out.println("Position clicked: " + row + ", " + column + " ,BLUE SQUARES SIZE: " + blueSquares.size());

        for (Position square : blueSquares) {
            if (square.getRow() == row && square.getCol() == column) {
                move(pieceList, oldPos, new Position(row, column));
                blueSquares.clear();
                break;
            }
        }


        //Clicks square with piece in it
        if (piece != null) {
            //Get its available moves
            ArrayList<Move> moves = piece.getMovesList();
            oldPos = piece.getPos();
            //Clear the canvas and then repaint it
            clearCanvas();
            paintChessBoard(pieceList);
            GraphicsContext g = chessPane.getGraphicsContext2D();

            //Show available moves by painting a blue circle in the cells
            blueSquares.clear();
            g.setFill(Color.BLUE);
            for (Move move : moves) {
                g.fillOval((move.getPosition().getCol()) * cellSize, (7 - move.getPosition().getRow()) * cellSize, cellSize,
                        cellSize);
                blueSquares.add(move.getPosition());
            }

        }
        if (piece == null) {
            System.out.println("CELL SHOULD BE EMPTY");
        }

    }

    private void move(ArrayList<Piece> pieceList, Position oldPosition, Position newPosition) {
        try {
            Piece piece = Board.getPiece(pieceList, new Position(oldPosition.getRow(), oldPosition.getCol()));
            piece.setNumberOfMoves(piece.getNumberOfMoves() + 1);
            piece.setPosition(newPosition);
        } catch (NullPointerException e) {

        }
        //Clear the canvas and then repaint it
        clearCanvas();
        paintChessBoard(pieceList);
        updateMoveList(pieceList);


    }

    private void updateMoveList(ArrayList<Piece> pieceList) {
        ArrayList<Piece> pieceListTemp = pieceList;
        for (Piece piece : pieceListTemp) {
            piece.setMovesList(getMoves(piece, pieceListTemp));
        }


    }

    public ArrayList<Move> getMoves(Piece piece, ArrayList<Piece> pieceList) {
        ArrayList<Move> legalMoves = new ArrayList<Move>();
        PieceType type = piece.getPieceType();
        if (type == PieceType.PAWN) {
            legalMoves = ((Pawn) piece).getLegalMoves(pieceList);
        } else if (type == PieceType.KNIGHT) {
            legalMoves = ((Knight) piece).getLegalMoves(pieceList);
        } else if (type == PieceType.BISHOP) {
            legalMoves = ((Bishop) piece).getLegalMoves(pieceList);
        } else if (type == PieceType.ROOK) {
            legalMoves = ((Rook) piece).getLegalMoves(pieceList);
        } else if (type == PieceType.QUEEN) {
            legalMoves = ((Queen) piece).getLegalMoves(pieceList);
        } else if (type == PieceType.KING) {
            legalMoves = ((King) piece).getLegalMoves(pieceList);
        }
        //return legalMoves;
        return removeIllegalMoves(pieceList, piece.getColor(), legalMoves);
    }

    private ArrayList<Move> removeIllegalMoves(ArrayList<Piece> pieceList, engine.Color color, ArrayList<Move> possibleMoves) {
        return removeCheckMoves(pieceList, color, removePositionsOffBoard(possibleMoves));
    }

    private ArrayList<Move> removeCheckMoves(ArrayList<Piece> pieceList, engine.Color color, ArrayList<Move> possibleMoves) {
        Piece king = null;
        // Extracting the King from the array of pieces
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == PieceType.KING && piece.getColor() == color) {
                king = piece;
            }
        }
        // Iterator has to be used to avoid concurrent modification exception
        // i.e. so that we can remove from the arraylist as we loop through it
        Iterator<Move> iter = possibleMoves.iterator();

        //Loop through moves available to a piece
        //If any of the moves result in check remove them
        while (iter.hasNext()) {
            Move move = iter.next();
            Position oldPosition = move.getPiece().getPos();
            move.getPiece().setPosition(move.getPosition());
            if (((King) king).check(pieceList, king.getPos())) {
                iter.remove();
            }
            move.getPiece().setPosition(oldPosition);
        }
        return possibleMoves;

    }
}

