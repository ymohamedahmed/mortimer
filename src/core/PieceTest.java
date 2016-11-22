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

        assertEquals(50, Piece.getIndex(testPiece1));
        assertEquals(59, Piece.getIndex(testPiece2));
        assertEquals(10, Piece.getPieceTableValue(testPiece1, null));
        assertEquals(-5, Piece.getPieceTableValue(testPiece2, null));
        assertEquals(7, Piece.getPosition(0).getRow());
        assertEquals(0, Piece.getPosition(0).getCol());
        assertEquals(0, Piece.getPosition(56).getRow());
        assertEquals(0, Piece.getPosition(56).getCol());
    }

}