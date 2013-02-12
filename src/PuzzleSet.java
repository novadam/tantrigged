import java.awt.Color;


public enum PuzzleSet {
	SET1(
		new int[][]	{
			{0,2,3,1,5,2},{0,1,3,1,5,3},{0,1,4,1,2,1},{0,2,3,2,1,3},{0,3,1,1,4,1},
			{0,2,1,3,3,2},{0,2,5,2,3,1},{0,2,1,2,4,1},{0,3,2,2,5,2},{0,2,4,1,1,2}},
		new Color[] { Color.red, Color.white, Color.green }),
	SET2(
		new int[][] {
			{0,2,1,2,4,1,-1,-1},{0,3,1,1,4,1,-1,-1},{0,1,4,1,2,1,-1,-1},{0,3,5,2,2,2,-1,-1},{0,1,2,3,3,1,-1,-1},
			{0,2,3,2,1,3,-1,-1},{0,1,3,2,2,2,-1,-1},{0,1,-1,-1,3,2,2,2},{0,2,-1,-1,3,2,1,3},{0,2,-1,-1,3,1,5,2}},
		new Color[] { Color.red, Color.green, Color.blue, Color.white });
	
	PuzzleSet(int[][] elem, Color[] colors) {
		this.elem = elem;
		this.colors = colors;
	}
	
	int[][] elem;
	Color[] colors;
}

