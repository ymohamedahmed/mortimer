package ui;

import java.util.ArrayList;

import engine.Piece;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class GamePanel extends Canvas {
	private ArrayList<Piece> pieceList;

	public GamePanel(ArrayList<Piece> pieceList) {
		this.pieceList = pieceList;
	}

	private void setupCells(ArrayList<Piece> pieceList) {
		ArrayList<ArrayList<Cell>> cells = new ArrayList<>();

		double cellSize = Math.ceil(Math.min(this.getWidth() / 8, this.getHeight() / 8));

		for (int x = 0; x <= this.getWidth(); x += cellSize) {
			ArrayList<Cell> temp = new ArrayList<Cell>();
			for (int y = 0; y <= this.getHeight(); y += cellSize) {
				Rectangle rect = new Rectangle(x, y, cellSize, cellSize);
				if(y%2 == 0){
					rect.setFill(CellColor.COLOR1.getColor());
				}else if(y%2 == 1){
					rect.setFill(CellColor.COLOR2.getColor());
				}
				Cell cell = new Cell();
				cell.setEmpty(false);
				cell.setRect(rect);
				temp.add(cell);
			}
			cells.add(temp);
		}

		for (Piece piece : pieceList) {
			Cell cell = cells.get(piece.getPos().getRow()).get(piece.getPos().getCol());
			cell.setImage(Piece.getImage(piece.getPieceType(), piece.getColor()));
			cell.getImage().setOpacity(0);
			cell.getImage().setFont(new Font("Tahoma", 12));
		}
		
	}

	private StackPane getGamePane(ArrayList<ArrayList<Cell>> cells){
		ArrayList<StackPane> stackPanes = new ArrayList<StackPane>();
		for(ArrayList<Cell> cellRow : cells){
			for(Cell cell : cellRow){
				stackPanes.add(new StackPane(cell.getRect(), cell.getImage()));
			}
		}
		StackPane rootPane = new StackPane();
		rootPane.getChildren().addAll(stackPanes);
		return rootPane;
	}
	
	enum CellColor{
		COLOR1(new Color(0,0,0,1.0)), COLOR2(new Color(255,255,255,1.0));
		private Color color;
		CellColor(Color color){
			this.color = color;
		}
		public Color getColor(){
			return color;
		}
	}

	private class Cell {
		private Rectangle rect;
		private Label image;
		private boolean empty;
		private CellColor color;
		public Rectangle getRect() {
			return rect;
		}
		public void setRect(Rectangle rect) {
			this.rect = rect;
		}
		public Label getImage() {
			return image;
		}
		public void setImage(Label image) {
			this.image = image;
		}
		public boolean isEmpty() {
			return empty;
		}
		public void setEmpty(boolean empty) {
			this.empty = empty;
		}
		public CellColor getColor() {
			return color;
		}
		public void CellColor(CellColor color) {
			this.color = color;
		}
	}

}
