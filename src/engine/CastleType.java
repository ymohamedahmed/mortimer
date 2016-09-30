package engine;

public enum CastleType {
	QUEENSIDE(-1), KINGSIDE(1);
	private int castleFactor;
	CastleType(int castleFactor){
		this.castleFactor = castleFactor;
	}
	public int getCastleFactor(){
		return castleFactor;
	}
}
