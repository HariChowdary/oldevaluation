package com.infoplus.smartevaluation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;
import com.infoplus.smartevaluation.R;

public class GetScriptCountFromBundle extends Activity implements
		OnTouchListener, OnClickListener {

	String ScanedAnswerBookNumber, UserId, editUserId, is_updated_server,
			SubjectId, editSubjectId, BundleNo, SubjectCode, editBundleNo,
			edtSubjectCode, programName;
	int CurrentAnswerBook, programId, maxBook;

	Button submit, clear;
	TextView batteryLevel;
	EditText editText;
	RelativeLayout getRelativeLayout;

	SharedPreferences preferences, getProgramPrefs;
	SharedPreferences.Editor editor_prg;

	DBHelper sEvalDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scriptcountforbundle);
		sEvalDatabase = DBHelper.getInstance(this);
		preferences = this.getSharedPreferences("ScriptCount",
				MODE_WORLD_READABLE);
		editor_prg = preferences.edit();
		getProgramPrefs = this.getSharedPreferences("program_details",
				MODE_WORLD_READABLE);

		programId = getProgramPrefs.getInt(SEConstants.SHARED_PREF_PROGRAM_ID,
				-1);
		programName = getProgramPrefs.getString(
				SEConstants.SHARED_PREF_PROGRAM_NAME, "");
		((TextView) findViewById(R.id.txt_programName)).setText(programName);
		Bundle b = getIntent().getExtras();

		UserId = b.getString("UserId");

		((TextView) findViewById(R.id.txt_eval_id)).setText("ID:  " + UserId);

		BundleNo = b.getString("BundleNo");
		SubjectId = b.getString("SubjectId");
		SubjectCode = b.getString("SubjectCode");
		CurrentAnswerBook = b.getInt("CurrentAnswerBook");
		((TextView) findViewById(R.id.tv_sub_code)).setText(SubjectCode);
		((TextView) findViewById(R.id.tv_bun_code)).setText(BundleNo);

		getRelativeLayout = (RelativeLayout) this.findViewById(R.id.Container);
		getRelativeLayout.setOnTouchListener(this);

		editText = (EditText) this
				.findViewById(R.id.numberOfBooksPerBundleEditText);

		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(2);
		editText.setFilters(FilterArray);

		submit = (Button) this.findViewById(R.id.nextButton);
		submit.setOnClickListener(this);
		clear = (Button) this.findViewById(R.id.clearButton);
		// clear.setTag("clear");
		clear.setOnClickListener(this);

		batteryLevel = (TextView) this.findViewById(R.id.txt_batteryLevel);
		batteryLevel();

	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		if (v == getRelativeLayout) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

			return true;
		}
		return false;
	}

	private void batteryLevel() {
		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,
						-1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				int level = -1;
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}
				batteryLevel.setText("Battery Level Remaining: " + level + "%");
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.clearButton:
			editText.setText("");
			editText.setFocusable(true);
			editText.requestFocus();
			editText.setFocusableInTouchMode(true);

			break;

		case R.id.nextButton:

			if (editText != null) {
				Editable editable = editText.getText();
				if (editable != null) {
					String value = editable.toString().trim();

					if (value.equalsIgnoreCase("0")
							|| value.equalsIgnoreCase("")
							|| Integer.parseInt(value) > SEConstants.MAX_ANSWER_BOOK) {
						alertMsg("Invalid Answer Book Count");
					} else {
						if (value.length() > 0 && value.length() < 3) {

							String scripts_number = null;
							String sql_query_bundle_del = "select count(distinct bundle_serial_no) as Value from table_marks where UPPER(bundle_no)=UPPER('"
									+ BundleNo + "')";
							Cursor cursor_bundle_del = null;
							try {
								cursor_bundle_del = sEvalDatabase
										.executeSelectSQLQuery(sql_query_bundle_del);

								if ((cursor_bundle_del != null)
										&& (cursor_bundle_del.getCount() > 0)) {
									while (!cursor_bundle_del.isAfterLast()) {

										scripts_number = cursor_bundle_del
												.getString(cursor_bundle_del
														.getColumnIndex("Value"));

										cursor_bundle_del.moveToNext();
									}
									if (Integer.parseInt(scripts_number) > Integer
											.parseInt(value)) {

										String isFromSummary = this.getIntent()
												.getStringExtra("IsModified");
										if (isFromSummary
												.equalsIgnoreCase("IsModified")) {

											alertMsg("Answer Books' Count should not be Less than the Scanned Answer Sheets' Count");

										}

									}

									else if (Integer.parseInt(scripts_number) == Integer
											.parseInt(value)) {
										editor_prg.putInt(
												SEConstants.SCRIPT_COUNT,
												(Integer.parseInt(value)));

										editor_prg.commit();
										switchToSummaryActivity(value);
									}

									else {

										editor_prg.putInt(
												SEConstants.SCRIPT_COUNT,
												(Integer.parseInt(value)));

										editor_prg.commit();
										switchToScanActivity(Integer
												.parseInt(value));
									}
								} else {
									FileLog.logInfo(
											"Cursor Null ---> GetScriptCountFromBundle: nextButton():",
											0);
								}

							} catch (Exception ex) {
								FileLog.logInfo(
										"Exception ---> GetScriptCountFromBundle: nextButton():"
												+ ex.toString(), 0);
							} finally {
								DataBaseUtility.closeCursor(cursor_bundle_del);
							}

						}

						else {
							Toast.makeText(this,
									"Please enter the valid script count",
									Toast.LENGTH_LONG).show();
						}
					}

				}
			}

		default:
			break;

		}
	}

	private void alertMsg(final String getMsg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				GetScriptCountFromBundle.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(getMsg);

		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						Dialog.dismiss();
						if (getMsg.equalsIgnoreCase("Invalid Count")) {
							editText.setText("");
							editText.setFocusable(true);
							editText.requestFocus();
							editText.setFocusableInTouchMode(true);
						} else {
							editText.setText("");
						}

					}
				});

		myAlertDialog.show();
	}

	private void switchToScanActivity(int value) {
		// TODO Auto-generated method stub
		String scripts_number = getScriptCount();
		maxBook = value;
		Intent intent = new Intent(this, ScanActivity.class);
		intent.putExtra("UserId", UserId);
		intent.putExtra("BundleNo", BundleNo);
		intent.putExtra("SubjectId", SubjectId);
		intent.putExtra("SubjectCode", SubjectCode); 
		intent.putExtra("SeatNo", SEConstants.seatNo);
		intent.putExtra("CurrentAnswerBook",
				Integer.parseInt(scripts_number) + 1);
		intent.putExtra("MaxAnswerBook", value);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

	}

	private void switchToSummaryActivity(String value) {

		maxBook = Integer.parseInt(value);
		
		Intent intent_eval_entry = new Intent(GetScriptCountFromBundle.this,
				GrandTotalSummaryTable.class);
		intent_eval_entry.putExtra("UserId", UserId);
		intent_eval_entry.putExtra("BundleNo", BundleNo);
		intent_eval_entry.putExtra("SubjectCode", SubjectCode);
		//++++++++++++++++
		//intent.putExtra("SeatNo", SEConstants.seatNo);
		intent_eval_entry.putExtra("ScriptCount", maxBook);
		intent_eval_entry.putExtra(
				"MaxAnswerBook",Integer.parseInt(value));
		intent_eval_entry.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent_eval_entry);
		
//		String scripts_number = getScriptCount();
//		Intent intent_showGrandTotalSummaryTable = new Intent(this,
//				ShowGrandTotalSummaryTable.class);
//
//		intent_showGrandTotalSummaryTable.putExtra("SubjectCode", SubjectCode);
//		intent_showGrandTotalSummaryTable.putExtra("BundleNo", BundleNo);
//
//		intent_showGrandTotalSummaryTable.putExtra("UserId", UserId);
//		intent_showGrandTotalSummaryTable.putExtra("MaxAnswerBook",
//				Integer.parseInt(value));
//		intent_showGrandTotalSummaryTable.putExtra(
//				SEConstants.CURRENT_ANSWER_BOOK,
//				Integer.parseInt(scripts_number) + 1);
//
//		intent_showGrandTotalSummaryTable
//				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent_showGrandTotalSummaryTable); 
	}

	public String getScriptCount() {
		String scripts_number = null;
		String sql_query_bundle_del = "select count(distinct bundle_serial_no) as Value"
				+ " from table_marks where UPPER(bundle_no)=UPPER('"
				+ BundleNo
				+ "')";
		Cursor cursor_bundle_del = null;
		try {
			cursor_bundle_del = sEvalDatabase
					.executeSelectSQLQuery(sql_query_bundle_del);

			if ((cursor_bundle_del != null)
					&& (cursor_bundle_del.getCount() > 0)) {
				while (!cursor_bundle_del.isAfterLast()) {

					scripts_number = cursor_bundle_del
							.getString(cursor_bundle_del
									.getColumnIndex("Value"));

					cursor_bundle_del.moveToNext();
				}
			} else {
				FileLog.logInfo(
						"Cursor null ---> GetScriptCountFromBundle: getScriptCount(): ",
						0);
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> GetScriptCountFromBundle: getScriptCount(): "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor_bundle_del);
		}

		return scripts_number;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(this,"Home Button Clicked",Toast.LENGTH_LONG).show();
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Toast.makeText(this, "Press Home Button to pause Evaluation",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return false;
	}

}
