package dme;

import core.Piece;
import core.PieceColor;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class EvaluationTest {
    @Test
    public void totalEvaluation() {
        //Test cases with hand calculated evaluation values
        ArrayList<Piece> pieceList = Piece.getInitialPieceList();
        double expectedEvalValue = 0;
        System.out.println(new Evaluation().totalEvaluation(pieceList, PieceColor.WHITE));
        Assert.assertEquals(expectedEvalValue, new Evaluation().totalEvaluation(pieceList, PieceColor.WHITE), 0);
    }

}