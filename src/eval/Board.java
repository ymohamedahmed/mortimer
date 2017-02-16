package eval;

public class Board {
	// Bitboards containing a set bit for each square on the board
	public static final long A1 = 1L;
	public static final long B1 = A1 << 1;
	public static final long C1 = A1 << 2;
	public static final long D1 = A1 << 3;
	public static final long E1 = A1 << 4;
	public static final long F1 = A1 << 5;
	public static final long G1 = A1 << 6;
	public static final long H1 = A1 << 7;

	public static final long A2 = A1 << 8;
	public static final long B2 = A1 << 9;
	public static final long C2 = A1 << 10;
	public static final long D2 = A1 << 11;
	public static final long E2 = A1 << 12;
	public static final long F2 = A1 << 13;
	public static final long G2 = A1 << 14;
	public static final long H2 = A1 << 15;

	public static final long A3 = A1 << 16;
	public static final long B3 = A1 << 17;
	public static final long C3 = A1 << 18;
	public static final long D3 = A1 << 19;
	public static final long E3 = A1 << 20;
	public static final long F3 = A1 << 21;
	public static final long G3 = A1 << 22;
	public static final long H3 = A1 << 23;

	public static final long A4 = A1 << 24;
	public static final long B4 = A1 << 25;
	public static final long C4 = A1 << 26;
	public static final long D4 = A1 << 27;
	public static final long E4 = A1 << 28;
	public static final long F4 = A1 << 29;
	public static final long G4 = A1 << 30;
	public static final long H4 = A1 << 31;

	public static final long A5 = A1 << 32;
	public static final long B5 = A1 << 33;
	public static final long C5 = A1 << 34;
	public static final long D5 = A1 << 35;
	public static final long E5 = A1 << 36;
	public static final long F5 = A1 << 37;
	public static final long G5 = A1 << 38;
	public static final long H5 = A1 << 39;

	public static final long A6 = A1 << 40;
	public static final long B6 = A1 << 41;
	public static final long C6 = A1 << 42;
	public static final long D6 = A1 << 43;
	public static final long E6 = A1 << 44;
	public static final long F6 = A1 << 45;
	public static final long G6 = A1 << 46;
	public static final long H6 = A1 << 47;

	public static final long A7 = A1 << 48;
	public static final long B7 = A1 << 49;
	public static final long C7 = A1 << 50;
	public static final long D7 = A1 << 51;
	public static final long E7 = A1 << 52;
	public static final long F7 = A1 << 53;
	public static final long G7 = A1 << 54;
	public static final long H7 = A1 << 55;

	public static final long A8 = A1 << 56;
	public static final long B8 = A1 << 57;
	public static final long C8 = A1 << 58;
	public static final long D8 = A1 << 59;
	public static final long E8 = A1 << 60;
	public static final long F8 = A1 << 61;
	public static final long G8 = A1 << 62;
	public static final long H8 = A1 << 63;

	// Used to flip the the column component of the index
	private static int[] flipIndex = { 7, 5, 3, 1, -1, -3, -5, -7 };

	// Mirror function is important since this program uses a different indexing
	// system to Carballo which is where some of the evaluation constants where
	// derived
	public static int mirrorIndex(int index) {
		return index + (flipIndex[index % 8]);
	}

	// Returns the distance between two pieces
	public static int distance(int a, int b) {
		return Math.max(Math.abs((a & 7) - (b & 7)), Math.abs((a >> 3) - (b >> 3)));
	}

	public static int flipHorizontalIndex(int index) {
		return (index & 0xF8) | (7 - (index & 7));
	}

}
