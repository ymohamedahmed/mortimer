package core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MoveGenTest {
	@Test
	public void testHammingWeight() {
		
		assertEquals(4, new MoveGen(null,null).hammingWeight(0b0001010101));
		assertEquals(2, new MoveGen(null,null).hammingWeight(0b11000));
		assertEquals(5, new MoveGen(null,null).hammingWeight(0b00010101011));
		assertEquals(0, new MoveGen(null,null).hammingWeight(0b0000000000));
	}
}
