package engine;

public enum Color {
	WHITE(1), BLACK(-1);
	private int colorFactor;
	Color(int colorFactor){
		this.colorFactor = colorFactor;
	}
	public int getColorFactor(){
		return colorFactor;
	}
}