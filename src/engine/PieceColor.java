package engine;

public enum PieceColor {
    WHITE(1), BLACK(-1);
	private int colorFactor;

    PieceColor(int colorFactor) {
        this.colorFactor = colorFactor;
	}

    public static PieceColor getColorByFactor(int factor) {
        return (factor == 1) ? PieceColor.WHITE : PieceColor.BLACK;
    }

	public int getColorFactor(){
		return colorFactor;
	}
}