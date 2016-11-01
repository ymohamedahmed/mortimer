package dme;

import engine.*;

import java.util.ArrayList;
import java.util.Iterator;

public class Search {
    public Move rootNegamax(ArrayList<Piece> pieceList, PieceColor color) {
        int depth = 8;
        double maxScore = 0;
        Move bestMove = null;
        for (Move move : getAllMovesColor(pieceList, color)) {
            double score = negamax(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, clonePieceList(pieceList), depth,
                    color.getColorFactor());
            System.out.println("Score  " + score);
            if (score > maxScore) {
                maxScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private double negamax(double alpha, double beta, ArrayList<Piece> pieceList, int depth, int colorFactor) {
        if (depth == 0) {
            return new Evaluation().totalEvaluation(pieceList, PieceColor.getColorByFactor(colorFactor));
        }
        // TODO add sorting algorithm here
        double bestValue = Double.NEGATIVE_INFINITY;

        // Move piece
        //ArrayList<Piece> pieceListTemp = clonePieceList(pieceList);
        for (Move move : getAllMovesColor(pieceList, PieceColor.getColorByFactor(colorFactor))) {
            move(pieceList, move.getPiece().getPos(), move.getPosition());
            //System.out.println("EVALUATION DIFFERENCE: " + (new Evaluation().totalEvaluation(pieceList, PieceColor.getColorByFactor(colorFactor)) - new Evaluation().totalEvaluation(pieceListTemp, PieceColor.getColorByFactor(colorFactor))));
            double v = -negamax(-beta, -alpha, pieceList, depth - 1, -colorFactor);
            bestValue = Math.max(bestValue, v);
            alpha = Math.max(alpha, v);
            if (alpha >= beta) {
                break;
            }
        }
        return bestValue;
    }

    private void move(ArrayList<Piece> pieceList, Position oldPosition, Position newPosition) {
        Piece pieceCaptured = null;
        try {
            Piece piece = Board.getPiece(pieceList, new Position(oldPosition.getRow(), oldPosition.getCol()));
            Move move = getMove(piece.getMovesList(), newPosition);
            //Checks if the move selected is a castling move
            //If so changes the position of the rook based on whether
            //it is Queenside or Kingside castling
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

            //If it is a capture move, the piece to be captured is found
            if (move.isCapture()) {
                pieceCaptured = Board.getPiece(pieceList, move.getPosition());
            }
            //Finds the piece captured during en passant
            if (move.isEnPassant()) {
                pieceCaptured = Board.getPiece(pieceList, new Position(
                        move.getPosition().getRow() - piece.getColor().getColorFactor(), move.getPosition().getCol()));
            }

            pieceList.remove(pieceCaptured);
            piece.setNumberOfMoves(piece.getNumberOfMoves() + 1);
            piece.setPosition(newPosition);


        } catch (NullPointerException e) {
        }
        updateMoveList(pieceList, true);
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

    private ArrayList<Move> getAllMovesColor(ArrayList<Piece> pieceList, PieceColor color) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (Piece piece : pieceList) {
            if (piece.getColor() == color) {
                for (Move move : piece.getMovesList()) {
                    possibleMoves.add(move);
                }
            }
        }
        return possibleMoves;
    }

    private void updateMoveList(ArrayList<Piece> pieceList, boolean removeCheck) {
        for (Piece piece : pieceList) {
            piece.setMovesList(getMoves(piece, pieceList, removeCheck));
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
        Iterator<Move> moveIterator = possibleMoves.iterator();
        Position oldPosition = piece.getPos();

        // Extracting the King from the array of pieces
        for (Piece pieceLoop : pieceList) {
            if (pieceLoop.getPieceType() == PieceType.KING && pieceLoop.getColor() == color) {
                king = pieceLoop;
            }
        }

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
            if (((King) king).check(pieceListTemp, king.getPos())) {
                moveIterator.remove();
            }
            pieceTemp.setPosition(oldPosition);
            pieceTemp.setNumberOfMoves(piece.getNumberOfMoves() - 1);
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
}