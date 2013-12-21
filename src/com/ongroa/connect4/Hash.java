package com.ongroa.connect4;

import java.util.Random;

public class Hash {

	public long value;
	private long[][][] hash_field = new long[3][8][7];
	private long humanPlayerTurns;
	private long computerPlayerTurns;

	
	public Hash() {
		
	}
	
	public void initHash() {
		Random random = new Random();
		for (int color = 0; color < 3; ++color)
			for (int column = 1; column < 8; ++column)
				for (int row = 1; row < 7; ++row)
					hash_field[color][column][row] = random.nextLong();
		humanPlayerTurns = random.nextLong();
		computerPlayerTurns = random.nextLong();
	}

	public long getHash(int[][] field, int color) {
		long hash = 0;
		if (color == Table.HUMAN)
			hash ^= humanPlayerTurns;
		else
			hash ^= computerPlayerTurns;
		for (int column = 1; column < 8; ++column)
			for (int row = 1; row < 7; ++row)
				if (field[column][row] == Table.EMPTY)
					hash ^= hash_field[0][column][row];
				else if (field[column][row] == Table.HUMAN)
					hash ^= hash_field[1][column][row];
				else if (field[column][row] == Table.COMPUTER)
					hash ^= hash_field[2][column][row];
		return hash;
	}
	
	@Override
	public String toString() {
		String ret = "";
		for (int color = 0; color < 2; ++color)
			for (int column = 1; column < 8; ++column)
				for (int row = 1; row < 7; ++row)
					ret += String.format("%d %d %d %d\n",
							color,
							column,
							row,
							hash_field[color][column][row]);
		ret += String.format("first  player move: %d\n", humanPlayerTurns);
		ret += String.format("second player move: %d\n", computerPlayerTurns);
		return ret;
	}

}
