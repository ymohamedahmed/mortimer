package engine;

public enum PieceColor {
    WHITE(1), BLACK(-1);
	private int colorFactor;

    PieceColor(int colorFactor) {
        this.colorFactor = colorFactor;
	}
	public int getColorFactor(){
		return colorFactor;
	}
}