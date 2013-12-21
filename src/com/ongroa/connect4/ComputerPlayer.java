package com.ongroa.connect4;

public class ComputerPlayer {

	final int COMPUTER = 2;
	private int color = COMPUTER;
	int bestmove = 4;
	int n = 0;
	int maxlevel = 0;
	int[] currentLine = new int[100];
	Table ownTable;
	
	ComputerPlayer() {
		this.color = COMPUTER;
	}

	public int getColor() {
		return color;
	}
	
	public int getMove(Table table, int level) {
		int point;
		ownTable = new Table();
		ownTable.setOrder();
		Table.copyTable(ownTable, table);
		n = 0;
		for (int l = 1; l <= level; l++) {
			maxlevel = l;
//			System.out.println("level " + l);
			point = alfabeta(l, ownTable, Table.COMPUTER, Table.MIN_POINT, 
					Table.MAX_POINT);
			if (point == Table.MAX_POINT) break;
		}
//		System.out.printf("\nnumber of nodes: %d\n", n);
		return bestmove;
	}

	private int alfabeta(int l, Table table, int player, int alfa, int beta) {
		int x, y, i, bestvalue, point;
		boolean draw = true;
		bestvalue = Table.MIN_POINT;
		for (i = 1; i < 8; i++) {
			x = table.order(i);
			y = table.putDisc(player, x);
			if (y == 0) continue;
			
			// there is at least one possible move
			draw = false;
			currentLine[l] = x;
			int go = table.isGameOver();
			if (go == Table.DRAW) {
				table.removeDisc(x);
				point = Table.DRAW;
			} else
				if (go == Table.HUMAN) {
					table.removeDisc(x);
					point = Table.MAX_POINT;
				} else
					if (go == Table.COMPUTER) {
						table.removeDisc(x);
						point = Table.MAX_POINT;
					} else {
						//if (l > 1) System.out.printf(
						//"l: %i, color: %i, x: %i\n", l, player, x);
						if (l == 1) {
							n++;
							point = table.evaluate(player);
							point += 10 * ((10 - Math.abs(4 - x)) + y);
//							point += (int)(random() * 20);
							//System.out.printf("l: %i, color: %i, evaluation: 
							//(%i): %i\n", l, player, x, point);
							table.removeDisc(x);
						} else {
							point = -alfabeta(l - 1, table, 3 - player, -beta, 
									-alfa);
							//System.out.printf("RETURN: point: %i\n", point);
							table.removeDisc(x);
						}
					}
			if (point > bestvalue) {
				bestvalue = point;
				//System.out.printf("bestvalue: %i\n", bestvalue);
				if (l == maxlevel) { 
					bestmove =  currentLine[maxlevel]; 
					//System.out.printf("bestmove: %i, bestvalue: %i\n", 
					//bestmove, bestvalue);
				}
			}
			if (bestvalue >= beta) return bestvalue;
			if (bestvalue > alfa) alfa = bestvalue;
		}
		
		// there was no possible move
		if (draw == true) {
			return	Table.DRAW;
		}
		return bestvalue;
	}

}