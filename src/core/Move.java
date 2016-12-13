package core;

public class Move {
	private int oldPosition;
	private int finalPosition;

	public Move(int oldPosition, int finalPosition) {
		this.oldPosition = oldPosition;
		this.finalPosition = finalPosition;
	}

	public int getPosition() {
		return finalPosition;
	}

	public void setPosition(int position) {
		this.finalPosition = position;
	}

}