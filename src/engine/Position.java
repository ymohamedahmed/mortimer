package engine;

public class Position {
	int row;
	int column;
	public Position(int row, int column){
		this.row = row;
		this.column = column;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return column;
	}
	public void setCol(int column) {
		this.column = column;
	}
}
