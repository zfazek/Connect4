package com.ongroa.connect4;

import java.util.ArrayList;
import java.util.HashMap;

public class Table {

	private boolean useHash = false;

	public Hash hash;
	private HashMap<Long, Integer> hashmap;

	private int[][] field = new int[9][8];
	private int[] order = new int[7];
	private ArrayList<Integer> moves;

	final static int INVALID = -1;
	final static int EMPTY = 0;
	final static int HUMAN = 1;
	final static int COMPUTER = 2;
	final static int BORDER = 3;

	final static int POINT_FOUR = 1000;
	final static int POINT_THREE_OPEN = 250;
	final static int POINT_THREE_CLOSED = 25;
	final static int POINT_TWO_OPEN = 25;
	final static int POINT_TWO_CLOSED = 10;

	final static int MAX_POINT = 10000;
	final static int MIN_POINT = -10000;
	final static int DRAW = 0;

	public int[][] getTable() { 
		return field; 
	}

	public void setTable(int[][] field) {
		this.field = field;
	}

	public Table() {
		moves = new ArrayList<Integer>();
		hash = new Hash();
		hashmap = new HashMap<Long, Integer>();
		setOrder();
		clearTable();
		clearMoveList();
		initHash();
	}

	public void clearMoveList() {
		moves.clear();
	}

	public int getMoveListSize() {
		return moves.size();
	}

	public int getMoveAtIdx(int idx) {
		return moves.get(idx);
	}

	public int order(int x) {
		return order[x - 1];
	}

	public void setOrder() {
		order[0] = 4;
		order[1] = 5;
		order[2] = 3;
		order[3] = 6;
		order[4] = 2;
		order[5] = 7;
		order[6] = 1;
	}

	public int getField(int x, int y) {
		return field[x][y];
	}

	public void setField(int x, int y, int color) {
		field[x][y] = color;
	}

	public int isGameOver() {
		if (isWon(HUMAN)) return HUMAN;
		if (isWon(COMPUTER)) return COMPUTER;

		//Check for draw: check each upper field empty or not
		for(int x = 1; x < 8; x++) 
			if (getField(x, 1) == EMPTY) return INVALID;
		return DRAW;
	}

	public boolean isWon(int color) {
		for(int x = 1; x < 8; x++)
			for(int y = 1; y < 7; y++) {
				if (getField(x, y) == color && isFoundFour(x, y, color)) {
					return true;
				}
			}
		return false;
	}

	private boolean isFoundFour(int x, int y, int color) {
		if (isFoundFourInOneDir(x, y, color,  0, 1)) return true;
		if (isFoundFourInOneDir(x, y, color,  1, 1)) return true;
		if (isFoundFourInOneDir(x, y, color,  1, 0)) return true;
		if (isFoundFourInOneDir(x, y, color, -1, 1)) return true;
		return false;
	}

	private boolean isFoundFourInOneDir(int x, int y, int color, int dx, 
			int dy) {
		int row = 1;
		while(x + dx > 0 && x + dx < 8 && y + dy > 0 && y + dy < 7 && 
				getField(x + dx, y + dy) == color && row < 4) {
			row++;
			x = x + dx;
			y = y + dy;
		}
		if (row > 3) return true;
		return false;
	}

	public void clearTable() {
		for(int x = 1; x < 8; x++)
			for(int y = 1; y < 7; y++) {
				setField(x, y, EMPTY);
			}
		for(int x = 0; x < 9; x++) {
			setField(x, 0, BORDER);
			setField(x, 7, BORDER);
		}
		for(int y = 0; y < 8; y++) {
			setField(0, y, BORDER);
			setField(8, y, BORDER);
		}
		// setInitalTable();
	}

	public void setInitalTable() {
		setField(2, 4, HUMAN);
		setField(2, 5, COMPUTER);
		setField(4, 2, HUMAN);
		setField(4, 3, HUMAN);
		setField(4, 5, HUMAN);
		setField(4, 6, HUMAN);
		setField(5, 5, HUMAN);
		setField(6, 3, HUMAN);
		setField(2, 6, COMPUTER);
		setField(4, 1, COMPUTER);
		setField(4, 4, COMPUTER);
		setField(5, 6, COMPUTER);
		setField(6, 2, COMPUTER);
		setField(6, 4, COMPUTER);
		setField(6, 5, COMPUTER);
		setField(6, 6, COMPUTER);
	}

	public static void copyTable(Table table1, Table table2) {
		for(int x = 0; x < 9; x++)
			for(int y = 0; y < 8; y++) {
				table1.setField(x, y, table2.getField(x, y));
			}
	}

	public boolean isLegalMove(int column) {
		return getField(column, 1) == EMPTY;
	}

	public void makeMove(int color, int column) {
		if (isLegalMove(column)) 
			putDisc(color, column);
		else {
			// System.out.println(color + " " + column + " is not legal");
		}
	}

	public int putDisc(int player, int column) {
		int y;
		if (getField(column, 1) != EMPTY) {
			return 0; //not possible
		}
		for (y = 1; y < 7; y++) {
			if (getField(column, y) != EMPTY) break;
		}
		setField(column, y - 1, player);
		return y - 1;
	}

	public void removeDisc(int column) {
		for (int y = 1; y < 7; y++) {
			if (getField(column, y) != EMPTY) {
				setField(column, y, EMPTY);	
				break;
			}
		}
	}

	public void addMoveToList(int x) {
		moves.add(x);
	}

	public void removeMoveFromList() {
		if (moves.size() > 0) {
			int x = moves.get(moves.size() - 1);
			removeDisc(x);
			moves.remove(moves.size() - 1);
		}
	}

	public int evaluate(int color) {
		if (useHash) {
			long hash_key = hash.getHash(field, color);
			if (hashmap.containsKey(hash_key)) {
				return hashmap.get(hash_key);
			} else {
				int eval =  evaluatePosition(color);
				hashmap.put(hash_key, eval);
				return eval;
			}
		} else {
			return evaluatePosition(color);
		}
	}

	private int evaluatePosition(int color) {
		int point = 0;
		for(int x = 1; x < 8; x++)
			for(int y = 1; y < 7; y++) {
				if (getField(x, y) == color) {
					point += evaluateDir(x, y, color, 0, 1);
					point += evaluateDir(x, y, color, 0, -1);
					point += evaluateDir(x, y, color, 1, 1);
					point += evaluateDir(x, y, color, -1, -1);
					point += evaluateDir(x, y, color, 1, 0);
					point += evaluateDir(x, y, color, -1, 0);
					point += evaluateDir(x, y, color, -1, 1);
					point += evaluateDir(x, y, color, 1, -1);
				}
			}
		int point_tmp = point;
		if (color == HUMAN) {
			color = COMPUTER;
		} else {
			color = HUMAN;
		}
		point = 0;
		for(int x = 1; x < 8; x++)
			for(int y = 1; y < 7; y++) {
				if (getField(x, y) == color) {
					point -= 2 * evaluateDir(x, y, color, 0, 1);
					point -= 2 * evaluateDir(x, y, color, 0, -1);
					point -= 2 * evaluateDir(x, y, color, 1, 1);
					point -= 2 * evaluateDir(x, y, color, -1, -1);
					point -= 2 * evaluateDir(x, y, color, 1, 0);
					point -= 2 * evaluateDir(x, y, color, -1, 0);
					point -= 2 * evaluateDir(x, y, color, -1, 1);
					point -= 2 * evaluateDir(x, y, color, 1, -1);
				}
			}
		return point_tmp + point;
	}

	private int evaluateDir(int x, int y, int color, int dx, int dy) {
		int k = 1;
		int open = 2;

		// check in reverse dir
		//not the edge of line
		if (getField(x - dx, y - dy) == color) return 0;

		//not open in the back end
		if (getField(x - dx, y - dy) != EMPTY) { open -= 1; }
		while (getField(x + k * dx, y + k * dy) == color) {
			k += 1;
		}

		//not open in the end
		if (getField(x + k * dx, y + k * dy) != EMPTY) { open -= 1; }

		// check XX X like patterns
		if (k > 1 && getField(x + k * dx, y + k * dy) == EMPTY &&
				getField(x + k * dx + dx, y + k * dy + dy) == color) {
			k += 1;
			if (getField(x + k * dx + dx, y + k * dy + dy) != EMPTY) { 
				open -= 1; 
			}
		}
		if (k >= 4) return POINT_FOUR;
		if (k == 3 && open == 2) return POINT_THREE_OPEN;
		if (k == 3 && open == 1) return POINT_THREE_CLOSED;
		if (k == 2 && open == 2) return POINT_TWO_OPEN;
		if (k == 2 && open == 1) return POINT_TWO_CLOSED;
		return 0;
	}

	public void initHash() {
		hash.initHash();
	}

}