package engine;

import dme.Evaluation;
import dme.Tree;

import java.util.ArrayList;

public class Board {
    public static ArrayList<Tree.Node> getAlLMoves(ArrayList<Piece> pieceList, Color color) {
        ArrayList<Tree.Node> childNodes = new ArrayList<Tree.Node>();
        for (Piece piece : pieceList) {
            if (piece.getColor() == color) {
                for (Move move : Piece.getLegalMove(piece, pieceList)) {
                    ArrayList<Piece> pieceListUpdated = updatePieceList(move, pieceList);
                    double evalValue = Evaluation.totalEvaluation(pieceListUpdated, color);
                    Tree.Node node = new Tree.Node(evalValue);
                    node.setMove(move);
                    node.setPieceList(pieceListUpdated);
                    childNodes.add(node);
                }
            }
        }
        return childNodes;
    }

    public static ArrayList<Piece> updatePieceList(Move move, ArrayList<Piece> pieceList) {
        for (Piece piece : pieceList) {
            if (piece.getColor() == move.getPiece().getColor() && piece.getPieceType() == move.getPiece().getPieceType()) {
                piece.setPosition(move.getPosition());
            }
        }
        return pieceList;
    }

    public static boolean isSquareEmpty(ArrayList<Piece> pieceList,
                                        Position position) {
        for (Piece piece : pieceList) {
            if (piece.getPos().getRow() == position.getRow()
                    && piece.getPos().getCol() == position.getCol()) {
                return false;
            }
        }
        return true;
    }

    public static Piece getPiece(ArrayList<Piece> pieceList, Position pos) {
        for (Piece piece : pieceList) {
            if (piece.getPos() == pos) {
                return piece;
            }
        }
        return null;

    }

    public static boolean offGrid(Position position) {
        return position.getRow() > 7 || position.getCol() > 7
                || position.getRow() < 0 || position.getCol() < 0;
    }

    public static int noOfPieces(ArrayList<Piece> pieceList, PieceType type, Color color) {
        int number = 0;
        for (Piece piece : pieceList) {
            if (piece.getPieceType() == type && piece.getColor() == color) {
                number++;
            }
        }
        return number;
    }

    public static int noOfPiecesColor(ArrayList<Piece> pieceList, Color color) {
        int answer = 0;
        for (Piece piece : pieceList) {
            if (color == piece.getColor()) {
                answer++;
            }
        }
        return answer;
    }
}
