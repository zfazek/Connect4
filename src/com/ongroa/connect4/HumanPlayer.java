package com.ongroa.connect4;

class HumanPlayer {
	final int HUMAN = 1;
	private int color = HUMAN;

	HumanPlayer() {
		this.color = HUMAN;
	}

	public int getColor() {
		return color;
	}

}