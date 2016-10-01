package dme;

import engine.Move;
import engine.Piece;

import java.util.ArrayList;

public class Tree<T> {
    private Node rootNode;

    public Tree(double rootValue) {
        rootNode = new Node(0);
        rootNode.evalValue = rootValue;
        rootNode.children = new ArrayList<Node>();
    }

    public static class Node<Double> {
        private double evalValue;
        private Move move;
        private ArrayList<Node> children;
        private boolean terminal = false;
        private ArrayList<Piece> pieceList;

        public Node(double evalValue) {
            this.evalValue = evalValue;
        }

        public ArrayList<Piece> getPieceList() {
            return pieceList;
        }

        public void setPieceList(ArrayList<Piece> pieceList) {
            this.pieceList = pieceList;
        }

        public Move getMove() {
            return move;
        }

        public void setMove(Move move) {
            this.move = move;
        }

        public double getEvalValue() {
            return evalValue;
        }

        public void setEvalValue(double evalValue) {
            this.evalValue = evalValue;
        }

        public ArrayList<Node> getChildren() {
            return children;
        }

        public void setChildren(ArrayList<Node> children) {
            this.children = children;
        }

        public boolean isTerminal() {
            return terminal;
        }

        public void setTerminal(boolean terminal) {
            this.terminal = terminal;
        }
    }
}
