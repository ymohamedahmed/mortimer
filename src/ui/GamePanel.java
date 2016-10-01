package ui;

import engine.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel {
    private ArrayList<Piece> pieceList;

    public GamePanel(ArrayList<Piece> pieceList) {
        this.pieceList = pieceList;
    }

    @Override
    public void paint(Graphics g) {

    }

    private void setupCells(ArrayList<Piece> pieceList) {
        ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
        for (Piece piece : pieceList) {
            Cell cell = new Cell();
            cell.empty = false;
            cell.image = Piece.getImage(piece.getPieceType(), piece.getColor());
            cell.image.setOpaque(false);
            cell.image.setFont(new Font("Tahoma", Font.PLAIN, 12));
            cells.get(piece.getPos().getRow()).add(piece.getPos().getRow(), cell);
        }
    }

    enum cellColor {
        color1, color2
    }

    private class Cell {
        Rectangle rect;
        JLabel image;
        boolean empty;
    }


}
