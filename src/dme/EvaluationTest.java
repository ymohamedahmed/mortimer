package dme;

import core.*;
import org.junit.Assert;
import org.junit.Test;
import ui.controllers.MainController;

import java.util.ArrayList;

public class EvaluationTest {
    @Test
    public void totalEvaluation() {
        // Test cases with hand calculated evaluation values
        ArrayList<Piece> pieceList = Piece.getInitialPieceList();
        double expectedEvalValue = 0;
        Assert.assertEquals(expectedEvalValue, new Evaluation().totalEvaluation(pieceList, PieceColor.WHITE), 0);

        Piece pawn = Board.getPiece(pieceList, new Position(1, 1));

        MainController mainController = new MainController();
        mainController.move(pieceList, new Move(pawn, new Position(3, 1), false), false);
        mainController.updateMoveList(pieceList, true);
        Assert.assertEquals(420, new Evaluation().totalEvaluation(pieceList, PieceColor.BLACK), 0);
    }

}