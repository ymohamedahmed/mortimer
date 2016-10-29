package dme;

import engine.Board;
import engine.Move;
import engine.Piece;
import engine.PieceColor;

import java.util.ArrayList;

public class Search {
    public Move rootNegamax(ArrayList<Piece> pieceList, PieceColor color) {
        int depth = 5;
        Tree tree = new Tree(0);
        Node rootNode = tree.getRootNode();
        int colorFactor = color.getColorFactor();
        ArrayList<Node> children = Board.getAllMoves(pieceList, color);
        rootNode.setChildren(children);
        int i = 0;
        while (i <= depth) {
            ArrayList<Node> grandChildren = new ArrayList<>();
            for (Node node : children) {
                ArrayList<Node> grandChild = Board.getAllMoves(pieceList, PieceColor.getColorByFactor(colorFactor));
                node.setChildren(grandChild);
                grandChild.forEach(child -> grandChildren.add(child));
            }
            children = grandChildren;
            i++;
            colorFactor = colorFactor * -1;
            System.out.println(i);
        }
        Move move = null;
        System.out.println("REACHED NEGAMAX");
        double bestMove = negamax(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, rootNode, depth, color.getColorFactor());
        for (Node node : children) {
            if (node.getEvalValue() == bestMove) {
                // move = node.getMove();
            }
        }
        return move;
    }

    private double negamax(double alpha, double beta, Node node, int depth, int colorFactor) {
        if (depth == 0 || node.isTerminal()) {
            return colorFactor * node.getEvalValue();
        }
        //TODO add sorting algorithm here
        ArrayList<Node> children = node.getChildren();
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Node childNode : children) {
            double v = -negamax(-beta, -alpha, childNode, depth - 1, -colorFactor);
            bestValue = Math.max(bestValue, v);
            alpha = Math.max(alpha, v);
            if (alpha >= beta) {
                break;
            }
        }
        return bestValue;
    }

}