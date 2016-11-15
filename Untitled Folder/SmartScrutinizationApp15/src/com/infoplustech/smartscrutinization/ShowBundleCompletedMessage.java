package com.infoplustech.smartscrutinization;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class ShowBundleCompletedMessage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_bundle_completed_message);
		
		
		// String _mode = getIntent().getStringExtra(SEConstants.MODE);
		//
		// TextView tvMsg = (TextView) findViewById(R.id.tv_msg);
		// if(_mode.equals(SEConstants.EVALUATION)) {
		// tvMsg.setText("Evaluation Completed");
		// } else if(_mode.equals(SEConstants.SCRUTINY)) {
		// tvMsg.setText("Scrutiny Observation Completed");
		// } else if(_mode.equals(SEConstants.SCRUTINY_CORRECTION)) {
		// tvMsg.setText("Scrutiny Correction Completed");
		// }
		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//navigateToTabletHomeScreen();
			//	finish();
				Toast.makeText(ShowBundleCompletedMessage.this, "Please Release the Tab...!", 1000).show();
			}
		});
	}     

	protected void navigateToTabletHomeScreen() {  
		// TODO Auto-generated method stub
		Intent in = new Intent(ShowBundleCompletedMessage.this,
				ShowBundleCompletedMessage.class);
		in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		in.addCategory(Intent.CATEGORY_HOME);
		startActivity(in);
			/*Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);*/
	}

	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Toast.makeText(this,
					getResources().getString(R.string.alert_press_home),
					Toast.LENGTH_LONG).show();
			return false;
		}
		return false;
	}
@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
	Toast.makeText(this,
			getResources().getString(R.string.alert_press_home),
			Toast.LENGTH_LONG).show();
}
	// navigate to Starting screen
	private void navigateToStartingScreen() {
	
	}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
}
@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
	Intent intent = new Intent(this, Scrutiny_OptionSelectionActivity.class);
	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	startActivity(intent);
}
}
