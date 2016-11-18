package ui.controllers;

import core.Piece;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by yousuf on 11/18/16.
 */
public class MainControllerTest {
    @Test
    public void testingHashValues() {
        assertEquals(Piece.getInitialPieceList().hashCode(), Piece.getInitialPieceList().hashCode());
    }

}