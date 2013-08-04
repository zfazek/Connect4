package com.ongroa.connect4;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

public class MainActivity extends Activity {

	private Spinner spinnerLevel;
	private CheckBox checkBoxHumanStart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinnerLevel = (Spinner) findViewById(R.id.spinnerLevels);
		spinnerLevel.setSelection(5);
		checkBoxHumanStart = (CheckBox) findViewById(R.id.checkBoxHumanStart);
		checkBoxHumanStart.setChecked(true);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public void startGameActivity(View view) {
		int level = Integer.parseInt(spinnerLevel.getSelectedItem().toString());
		boolean isHumanStart = checkBoxHumanStart.isChecked();
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("LEVEL", level);
		if (isHumanStart) {
			intent.putExtra("IS_HUMAN_START", true);
		}
		else {
			intent.putExtra("IS_HUMAN_START", false);
		}
		startActivity(intent);
	}

}
