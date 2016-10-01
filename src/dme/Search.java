package dme;

import java.util.ArrayList;

public class Search {
    public double negamax(double alpha, double beta, Tree.Node node, int depth, int colorFactor) {
        if (depth == 0 || node.isTerminal() == true) {
            return colorFactor * node.getEvalValue();
        }
        ArrayList<Tree.Node> children = node.getChildren();
        double bestValue = Double.NEGATIVE_INFINITY;
        for (Tree.Node childNode : children) {
            double v = -negamax(-beta, -alpha, childNode, depth - 1, -colorFactor);

        }
        return alpha;
    }

}
