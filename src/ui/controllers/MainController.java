package ui.controllers;

import engine.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;


public class MainController {
    // Variables loaded from the fxml file
    // Must be global so that they can be loaded from the fxml file
    public StackPane stackPane;
    public Canvas chessPane;
    public BorderPane borderPane;

    private ArrayList<Position> blueSquares = new ArrayList<>();
    private Position oldPos;

    public void initialize() {
        playGame();
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
            g.drawImage(Piece.getImage(piece.getPieceType(), piece.getColor()), (piece.getPos().getCol()) * cellSize,
                    (7 - piece.getPos().getRow()) * cellSize, cellSize, cellSize);
        }

        return cellSize;
    }

    private void resizingCanvas(ArrayList<Piece> pieceList) {
        chessPane.widthProperty().bind(stackPane.widthProperty());
        chessPane.heightProperty().bind(stackPane.heightProperty());
        chessPane.widthProperty().addListener(observable -> paintChessBoard(pieceList));
        chessPane.heightProperty().addListener(observable -> paintChessBoard(pieceList));
    }

    private void playGame() {
        ArrayList<Piece> pieceList = Piece.getInitialPieceList();
        double cellSize = paintChessBoard(pieceList);
        chessPane.setOnMouseClicked(evt -> clickListenerChessPane(pieceList, evt, cellSize));
    }

    private void clearCanvas() {
        GraphicsContext g = chessPane.getGraphicsContext2D();
        g.clearRect(0, 0, chessPane.getWidth(), chessPane.getHeight());
    }

    private void clickListenerChessPane(ArrayList<Piece> pieceList, MouseEvent evt, double cellSize) {
        // Get the position clicked in terms of the board
        int column = (int) Math.floor(evt.getX() / cellSize);
        int row = 7 - (int) Math.floor(evt.getY() / cellSize);
        boolean pieceMoved = false;
        // Get the piece that was clicked
        Piece piece = Board.getPiece(pieceList, new Position(row, column));

        for (Position square : blueSquares) {
            if (square.getRow() == row && square.getCol() == column) {
                move(pieceList, oldPos, new Position(row, column), true);
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
            g.setFill(Color.BLUE);
            for (Move move : moves) {
                g.fillOval((move.getPosition().getCol()) * cellSize, (7 - move.getPosition().getRow()) * cellSize,
                        cellSize, cellSize);
                blueSquares.add(move.getPosition());
            }

        }
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

    private Piece move(ArrayList<Piece> pieceList, Position oldPosition, Position newPosition, boolean repaint) {
        Piece pieceCaptured = null;
        try {
            Piece piece = Board.getPiece(pieceList, new Position(oldPosition.getRow(), oldPosition.getCol()));
            Move move = getMove(piece.getMovesList(), newPosition);
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
            }

            if (move.isCapture()) {
                pieceCaptured = Board.getPiece(pieceList, move.getPosition());
            }
            if (move.isEnPassant()) {
                pieceCaptured = Board.getPiece(pieceList, new Position(
                        move.getPosition().getRow() - piece.getColor().getColorFactor(), move.getPosition().getCol()));
            }

            //removedPieces.add(pieceCaptured);
            pieceList.remove(pieceCaptured);
            piece.setNumberOfMoves(piece.getNumberOfMoves() + 1);
            piece.setPosition(newPosition);

        } catch (NullPointerException e) {
        }

        if (repaint) {
            // Clear the canvas and then repaint it
            clearCanvas();
            paintChessBoard(pieceList);
            updateMoveList(pieceList, true);
        }
        return pieceCaptured;
    }

    private void undoMove(Piece piece, ArrayList<Piece> pieceList, Move move, Position oldPos, Piece pieceCaptured) {
        piece.setPosition(oldPos);
        piece.setNumberOfMoves(piece.getNumberOfMoves() - 1);
        if (pieceCaptured != null) {
            //pieceList.add(index, pieceCaptured);
        }
    }

    private void updateMoveList(ArrayList<Piece> pieceList, boolean removeCheck) {
        for (Piece piece : pieceList) {
            piece.setMovesList(getMoves(piece, pieceList, removeCheck));
        }
    }

    private void revertMoveList(ArrayList<ArrayList<Move>> move, ArrayList<Piece> pieceList) {
        for (int i = 0; i < move.size(); i++) {
            pieceList.get(i).setMovesList(move.get(i));
        }
    }

    private ArrayList<Move> getMoves(Piece piece, ArrayList<Piece> pieceList, boolean removeCheck) {
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
        ArrayList<Move> captureRecognised = recogniseCaptureMoves(piece, pieceList, legalMoves);
        return removeCheck ? removeIllegalMoves(piece, pieceList, piece.getColor(), captureRecognised)
                : removePositionsOffBoard(captureRecognised);
    }

    private ArrayList<Move> removeIllegalMoves(Piece piece, ArrayList<Piece> pieceList, engine.PieceColor color,
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

    private ArrayList<Move> removeCheckMoves(Piece piece, ArrayList<Piece> pieceList, engine.PieceColor color,
                                             ArrayList<Move> possibleMoves) {
        Piece king = null;

        // Iterator has to be used to avoid concurrent modification exception
        // i.e. so that we can remove from the arraylist as we loop through it
        Iterator<Move> iter = possibleMoves.iterator();
        Position oldPosition = piece.getPos();
        ArrayList<ArrayList<Move>> moves = new ArrayList<ArrayList<Move>>();

        // Extracting the King from the array of pieces
        for (Piece pieceLoop : pieceList) {
            if (pieceLoop.getPieceType() == PieceType.KING && pieceLoop.getColor() == color) {
                king = pieceLoop;
            }
            moves.add(pieceLoop.getMovesList());
        }

        // Loop through moves available to a piece
        // If any of the moves result in check remove them
        while (iter.hasNext()) {
            Move move = iter.next();
            //Piece pieceCaptured = move(pieceListTemp, piece.getPos(), move.getPosition(), false);
            Piece capPiece = null;
            ArrayList<Piece> pieceListTemp = clonePieceList(pieceList);
            Piece pieceTemp = Board.getPiece(pieceListTemp, piece.getPos());
            if (move.isCapture()) {
                capPiece = Board.getPiece(pieceListTemp, move.getPosition());
                pieceListTemp.remove(capPiece);
            }
            pieceTemp.setPosition(move.getPosition());
            pieceTemp.setNumberOfMoves(piece.getNumberOfMoves() + 1);
            updateMoveList(pieceListTemp, false);
            if (((King) king).check(pieceListTemp, king.getPos())) {
                iter.remove();
            }
            //undoMove(piece, pieceListTemp, move, oldPosition, pieceCaptured);

            pieceTemp.setPosition(oldPosition);
            pieceTemp.setNumberOfMoves(piece.getNumberOfMoves() - 1);
/*            if (move.isCapture()) {
                pieceListTemp.add(index, capPiece);
            }
            revertMoveList(moves, pieceListTemp);*/
        }

        return possibleMoves;

    }

    private ArrayList<Piece> clonePieceList(ArrayList<Piece> pieceList) {
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
        return clonedList;
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


}
