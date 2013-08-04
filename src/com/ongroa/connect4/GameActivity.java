package com.ongroa.connect4;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class GameActivity extends Activity {
	DrawView drawView;
	final int EMPTY = 0;
	final int HUMAN = 1;
	final int COMPUTER = 2;
	final int BORDER = 3;
	int width;
	int height;
	int size;
	int lastMove;
	int level;
	HumanPlayer humanPlayer;
	ComputerPlayer computerPlayer;
	Table table;
	Thread threadHumanMove;
	Thread threadComputerMove;
	private boolean isHumanTurn;
	private boolean isHumanStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("O's Connect 4");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_game);
		Display display = getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		size = width / 7;
		level = getIntent().getIntExtra("LEVEL", 3);
		isHumanStart = getIntent().getBooleanExtra("IS_HUMAN_START", true);
		humanPlayer = new HumanPlayer();
		computerPlayer = new ComputerPlayer();
		drawView = new DrawView(this);
		drawView.setBackgroundColor(Color.WHITE);
		setContentView(drawView);
		initTable();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_game, menu);
		return true;
	}

	private void initTable() {
		table = new Table();
		table.setOrder();
		resetGame();
	}

	public void resetGame() {
		table.clearTable();
		table.clearMoveList();
		drawView.invalidate();
		if (isHumanStart) {
			isHumanTurn = true;
		}
		else {
			isHumanTurn = false;
			computerMove();
		}
	}

	public void humanMove(int color, final int move) {
		threadHumanMove = new Thread() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (isHumanTurn) {
							isHumanTurn = false;
							if (threadComputerMove != null && 
									threadComputerMove.isAlive())
								try {
									threadComputerMove.join();
								} catch (InterruptedException e) {
								}
							Log.d(INPUT_SERVICE, "" + move);
							table.addMoveToList(move);
							animateMove(humanPlayer.getColor(), move);
							drawView.invalidate();
							if (table.isGameOver() != -1) {
								gameOver();
							}
							else {
								computerMove();
							}
						}
					}
				});
			}
		};
		threadHumanMove.start();
	}

	public void computerMove() {
		threadComputerMove = new Thread() {
			@Override
			public void run() {
				try {
					if (threadHumanMove != null && 
							threadHumanMove.isAlive())
						threadHumanMove.join();
					//					drawView.invalidate();
				} catch (InterruptedException e) {
				}
				lastMove = computerPlayer.getMove(table, level);
				table.addMoveToList(lastMove);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						animateMove(computerPlayer.getColor(), lastMove);
						drawView.invalidate();
						if (table.isGameOver() != -1) {
							gameOver();
						}
						else {
							isHumanTurn = true;
						}
					}
				});
			}
		};
		threadComputerMove.start();
	}

	private void animateMove(int color, int move) {
		int i = 1;
		while (table.getField(move, i) == table.EMPTY) {
			i++;
		}
		int y = i - 1;
		for (i = 1; i < y; i++) {
			table.setField(move, i, color);
			drawView.invalidate();
//			waitMillis(200);
			table.setField(move, i, table.EMPTY);
		}
		table.makeMove(color, move);
		drawView.invalidate();
	}

	private void gameOver() {
		isHumanTurn = false;
		showResultDialog(getResult(table));
	}

	public String getResult(Table table) {
		String returnString = "VÉGE\n";
		if (table.isWon(table.HUMAN)) returnString += "Nyertél!\n";
		else if (table.isWon(table.COMPUTER)) returnString += "Vesztettél!\n";
		else returnString += "Döntetlen!\n";
		return returnString;
	}

	private void showResultDialog(String result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(result);
		builder.setTitle("Game over.");
		builder.setPositiveButton("OK", 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
			}
		});
		builder.setCancelable(false);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void waitMillis(long x) {
//		Log.d("AA", "wait " + x);
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
		}
	}

	class DrawView extends View {
		Paint paint = new Paint();
		Context context;

		public DrawView(Context context) {
			super(context);
			this.context = context;
		}

		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			int margin = 5;
			int offset = size + size;
			int color;
			int[][] t = table.getTable();
			paint.setColor(Color.GRAY); 
			paint.setStyle(Style.FILL); 
			canvas.drawPaint(paint); 
			paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			if (isHumanTurn) 
				paint.setColor(Color.RED);
			else 
				paint.setColor(Color.BLUE);
			canvas.drawCircle(size / 2 + margin, 
					size / 2 + margin, 
					(size - margin) / 2, 
					paint);
			paint.setColor(Color.WHITE);
			paint.setTextSize(size / 4);
			canvas.drawText("BACK", size / 5 + margin, 3 * size / 5 + margin, paint);
			if (table.getMoveListSize() > 0)  {
				paint.setColor(Color.BLACK);
				int move = table.getMoveAtIdx(table.getMoveListSize() - 1);
				canvas.drawCircle((move-1) * size + size / 2 + margin,  
						size + size / 2,  
						(size) / 8, paint);
			}
			for (int y = 1; y < 7; y++) {
				for (int x = 1; x < 8; x++) {
					switch (t[x][y]) {
					case EMPTY:
						color = Color.WHITE;
						paint.setColor(color);
						break;
					case HUMAN:
						color = Color.RED;
						paint.setColor(color);
						break;
					case COMPUTER:
						color = Color.BLUE;
						paint.setColor(color);
						break;
					}
					canvas.drawCircle((x-1) * size + size / 2 + margin, 
							offset + (y-1) * size + size / 2 + margin, 
							(size - margin) / 2, 
							paint);
				}
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				int x = (int)event.getX() / size + 1;
				int y = (int)event.getY() / size + 1;
				if (x <= 2 && y == 1) moveBack();
				else
					if (table.isLegalMove(x)) {
						humanMove(humanPlayer.getColor(), x);
					}
			}
			return false;
		}

		private void moveBack() {
			if (! isHumanStart && table.getMoveListSize() == 1) {
			}
			else {
				table.removeMoveFromList();
				if (isHumanStart && 
						table.getMoveListSize() % 2 == 1)
					table.removeMoveFromList();
				if (! isHumanStart && 
						table.getMoveListSize() % 2 == 0)
					table.removeMoveFromList();
				drawView.invalidate();
				isHumanTurn = true;
			}
		}
	}
}
