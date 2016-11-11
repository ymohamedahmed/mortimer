package core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created by yousuf on 11/11/16.
 */
public class PieceTest {
    @Test
    public void testIndex() {
        Position testPosition1 = new Position(1, 2);
        Position testPosition2 = new Position(7, 3);

        Pawn testPiece1 = new Pawn(testPosition1, PieceColor.WHITE, 0);
        Queen testPiece2 = new Queen(testPosition2, PieceColor.BLACK, 0);

        assertEquals(10, Piece.getIndex(testPiece1));
        assertEquals(3, Piece.getIndex(testPiece2));
    }

}