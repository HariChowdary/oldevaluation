package com.infoplustech.smartscrutinization;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.SSConstants;

public class Scrutiny_MissMatchScriptWithDB extends Activity implements OnClickListener {

	String userId;
	String bundleNo;
	String bundle_serial_no, SeatNo;
	EditText _et_scan1;
	EditText _et_scan2;
	EditText _et_scan3;
	ProgressDialog progressDialog;

	private PowerManager.WakeLock wl;
  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scrutiny_layout_mismatch_script_with_db);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");

		// keyboard hide
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		// get data from previous activity/screen
		Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO);
		bundle_serial_no = intent_extras
				.getStringExtra(SSConstants.BUNDLE_SERIAL_NO);
		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);
		findViewById(R.id.btn_scanbook_clear).setOnClickListener(this);

		findViewById(R.id.btn_scanbook_submit).setOnClickListener(this);
		_et_scan1 = (EditText) findViewById(R.id.et_scanbook1);
		_et_scan2 = (EditText) findViewById(R.id.et_scanbook2);
		_et_scan3 = (EditText) findViewById(R.id.et_scanbook3);

		_et_scan3.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// if keydown and "enter" is pressed
				if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
					// SwitchToMarkDialogActivity();
					submit();
					return true;
				}
				return false;
			}

		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (wl != null) {
			wl.acquire();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (wl != null) {
			wl.release();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scrutiny_activity_main, menu);
		menu.findItem(R.id.menu_settings).setVisible(false);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.menu_back) {
			finish();
		}
		return super.onMenuItemSelected(featureId, item);
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

	// private void SwitchToMarkDialogActivity() {
	// Intent _intent = new Intent(this, MarkDialog.class);
	// _intent.putExtra(SSConstants.BUNDLE_SERIAL_NO, bundle_serial_no);
	// _intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
	// _intent.putExtra(SSConstants.USER_ID, userId);
	// _intent.putExtra(SSConstants.FROM_CLASS_MISS_MATCH_SCRIPT_WITH_DB,
	// SSConstants.FROM_CLASS_MISS_MATCH_SCRIPT_WITH_DB);
	// _intent.putExtra(SSConstants.NOTE_DATE,
	// getIntent().getStringExtra("noteDate"));
	// if (getIntent().hasExtra(
	// SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {
	//
	// _intent.putExtra(
	// SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
	// SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
	// }
	// _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// startActivity(_intent);
	// }

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_scanbook_submit:
			submit();
			break;

		case R.id.btn_scanbook_clear:
			clear();
			break;

		default:
			break;
		}

	}

	private void clear() {
		_et_scan1.setText("");
		_et_scan2.setText("");
		_et_scan3.setText("");
		_et_scan1.setFocusable(true);
		_et_scan1.setFocusableInTouchMode(true);
	}

	private void submit() {
		String _scan1 = _et_scan1.getText().toString().trim();
		String _scan2 = _et_scan2.getText().toString().trim();
		String _scan3 = _et_scan3.getText().toString().trim();

		if (!TextUtils.isEmpty(_scan1) && !TextUtils.isEmpty(_scan2)
				&& !TextUtils.isEmpty(_scan3)) {

			if ((_scan1.length() == 10 || _scan1.length() == 11)
					&& (_scan2.length() == 10 || _scan2.length() == 11)
					&& (_scan3.length() == 10 || _scan3.length() == 11)) {
				if (_scan1.equals(_scan2) && _scan2.equals(_scan3)) {
					new AsyncUpdateDB().execute(_scan3);
				} else {
					clear();
					showAlert(getString(R.string.alert_enter_single_barcode));
				}
			} else {
				clear();
				showAlert(getString(R.string.alert_enter_valid_barcodes));
			}
		} else {
			showAlert(getString(R.string.alert_scan_3_barcodes));
		}
	}

	private void showAlert(String msg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
					}
				});

		// myAlertDialog.setNegativeButton(
		// getString(R.string.alert_dialog_cancel),
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int arg1) {
		// // do something when the OK button is
		// // clicked
		// dialog.dismiss();
		// }
		// });

		myAlertDialog.show();
	}

	private class AsyncUpdateDB extends AsyncTask<String, Void, Void> {
		int dbUpdate;
		int barcodeExists;
		SScrutinyDatabase _database = SScrutinyDatabase
				.getInstance(Scrutiny_MissMatchScriptWithDB.this);
		Cursor _cursor = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress();
		}

		@Override
		protected Void doInBackground(String... params) {

			if (params.length > 0) {
				try {
					// check whether barcode exists
					_cursor = _database.passedQuery(
							SSConstants.TABLE_SCRUTINY_SAVE,
							SSConstants.ANS_BOOK_BARCODE + " = '" + params[0]
									+ "'", null);
					if (_cursor != null) {
						barcodeExists = _cursor.getCount();
						if (barcodeExists == 0) {
							// update
							ContentValues _values = new ContentValues();
							_values.put(
									SSConstants.SCRUTINIZE_STATUS,
									SSConstants.SCRUTINY_STATUS_7_SCRIPT_MISMATCH_WITH_DB);
							_values.put(SSConstants.SCRUTINIZED_BY, userId);
							_values.put(SSConstants.IS_SCRUTINIZED, 1);

							dbUpdate = _database.updateRow(
									SSConstants.TABLE_SCRUTINY_SAVE, _values,
									SSConstants.BUNDLE_NO + " ='" + bundleNo
											+ "' AND "
											+ SSConstants.BUNDLE_SERIAL_NO
											+ " IN (" + bundle_serial_no + ","
											+ bundle_serial_no + ")");
							_database.updateRow(
									SSConstants.TABLE_EVALUATION_SAVE, _values,
									SSConstants.BUNDLE_NO + " ='" + bundleNo
											+ "' AND "
											+ SSConstants.BUNDLE_SERIAL_NO
											+ " IN (" + bundle_serial_no + ","
											+ bundle_serial_no + ")");
						}
					}
				} catch (Exception e) {
					hideProgress();
					showAlert(e.getMessage());
				} finally {
					if (_cursor != null) {
						_cursor.close();
					}
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (dbUpdate > 0) {
				Scrutiny_MissMatchScriptWithDB.this
						.switchToShowGrandTotalSummaryTableOrScanActivity();
			} else {
				if (barcodeExists > 0) {
					showAlert(getString(R.string.alert_invalid_barcode));
				} else {
					showAlert("Database not updated");
				}
			}

			hideProgress();
		}

	}

	public void showProgress() {
		progressDialog = ProgressDialog.show(this, "", "Updating Bundle ...");
		progressDialog.setCancelable(false);
	}

	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private void switchToShowGrandTotalSummaryTableOrScanActivity() {
		Intent _intent;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor_scripts_count = _database.passedQuery(
				SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO + " = '"
						+ bundleNo + "'", null);
		int scriptsCount = _cursor_scripts_count.getCount();
		_cursor_scripts_count.close();
		if (scriptsCount == Integer.valueOf(bundle_serial_no)
				|| getIntent().hasExtra(
						SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)
				|| (checkAllScriptsStatusIs6() == 0)) {
			navigateToShowGrandTotalSummaryTableActivity();

		} else if (scriptsCount > Integer.valueOf(bundle_serial_no)) {

			_intent = new Intent(this, Scrutiny_SeriallyScanAnswerSheet.class);

			// check whether any SCRUTINIZE_STATUS is 5 bec it may be next obs
			_database = SScrutinyDatabase.getInstance(this);
			Cursor cursor_serial_no = _database.passedQuery(
					SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO
							+ " = '" + bundleNo + "' AND "
							+ SSConstants.SCRUTINIZE_STATUS + " = '"
							+ SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION
							+ "'", null);

			// if count greater than 0 then it is next/2 observation
			if (cursor_serial_no.getCount() > 0) {
				Cursor cursor_serial_no_from_db = _database
						.passedQuery(
								SSConstants.TABLE_SCRUTINY_SAVE,
								SSConstants.BUNDLE_NO
										+ " = '"
										+ bundleNo
										+ "' AND "
										+ SSConstants.SCRUTINIZE_STATUS
										+ " = '"
										+ SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION
										+ "'", SSConstants.BUNDLE_SERIAL_NO);
				if (cursor_serial_no_from_db.getCount() > 0) {
					cursor_serial_no_from_db.moveToFirst();
					_intent.putExtra(
							SSConstants.BUNDLE_SERIAL_NO,
							cursor_serial_no_from_db.getString(cursor_serial_no_from_db
									.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)));
				}
				cursor_serial_no_from_db.close();
			} else {
				// check status is otherthan 6
				if (checkScriptStatusIs6(Integer.parseInt(bundle_serial_no) + 1)) {
					cursor_serial_no.close();
					navigateToShowGrandTotalSummaryTableActivity();
					return;
				} else {
					// else it is first observation
					_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO, String
							.valueOf((Integer.parseInt(bundle_serial_no) + 1)));
				}
			}
			cursor_serial_no.close();
			_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
			String subjectC=null;
			if(bundleNo.length()==12)
				subjectC=bundleNo.substring(1, 7);
			else
				subjectC=bundleNo.substring(1, 6);
			
			_intent.putExtra(SSConstants.SUBJECT_CODE,
					subjectC);
			_intent.putExtra(SSConstants.USER_ID, userId);
			_intent.putExtra("SeatNo",
					SSConstants.SeatNo);
			startActivity(_intent);
		}

	}

	// check with bundle serial no whether status is 6
	private boolean checkScriptStatusIs6(int bundle_serial_no)
			throws SQLiteException {
		boolean flag = false;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor = _database.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
				SSConstants.BUNDLE_NO + " = '" + bundleNo + "' AND "
						+ SSConstants.BUNDLE_SERIAL_NO + " = '"
						+ bundle_serial_no + "'", null);
		if (_cursor != null && _cursor.getCount() > 0) {
			_cursor.moveToFirst();
			flag = _cursor.getString(
					_cursor.getColumnIndex(SSConstants.SCRUTINIZE_STATUS))
					.equals(SSConstants.SCRUTINY_STATUS_6_SCRIPT_COMPLETED);
		} else {
			flag = false;
		}
		_cursor.close();
		return flag;
	}

	// check scripts if other than status 6
	private int checkAllScriptsStatusIs6() {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor cur_count = _database.passedQuery(
				SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO + " = '"
						+ bundleNo + "' AND " + SSConstants.SCRUTINIZE_STATUS
						+ " <> "
						+ SSConstants.SCRUTINY_STATUS_6_SCRIPT_COMPLETED, null);
		int count = cur_count.getCount();
		cur_count.close();
		return count;
	}

	private void navigateToShowGrandTotalSummaryTableActivity() {
		Intent _intent = new Intent(this, Scrutiny_ShowGrandTotalSummaryTable.class);
		_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
		_intent.putExtra(SSConstants.USER_ID, userId);
		_intent.putExtra("SeatNo",
				SSConstants.SeatNo);
		String subjectC=null;
		if(bundleNo.length()==12)
			subjectC=bundleNo.substring(1, 7);
		else
			subjectC=bundleNo.substring(1, 6);
		_intent.putExtra(SSConstants.SUBJECT_CODE, subjectC);
		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(_intent);
	}
}
