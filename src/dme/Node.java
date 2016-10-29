package dme;

import java.util.ArrayList;

public class Node<Double> {
    private double evalValue;
    private ArrayList<Node> children;
    private boolean terminal = false;


    public Node(double evalValue) {
        this.evalValue = evalValue;
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