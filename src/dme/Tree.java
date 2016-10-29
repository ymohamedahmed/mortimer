package dme;

import java.util.ArrayList;

public class Tree<T> {
    private Node rootNode;

    public Tree(double rootValue) {
        rootNode = new Node(0);
        rootNode.setEvalValue(rootValue);
        rootNode.setChildren(new ArrayList<Node>());
    }

    public Node getRootNode() {
        return rootNode;
    }

}