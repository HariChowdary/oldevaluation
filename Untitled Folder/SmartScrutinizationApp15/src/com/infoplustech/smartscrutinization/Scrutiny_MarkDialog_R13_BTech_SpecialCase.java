package com.infoplustech.smartscrutinization;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_MarkDialog_R13_BTech_SpecialCase extends Activity implements
OnLongClickListener, OnClickListener , TextWatcher{

	String userId, subjectCode, ansBookBarcode, bundleNo, bundle_serial_no;
	String row_total_12 = null;
	String row_total_34 = null;
	String row_total_56 = null;
	String row_total_78 = null;
	String row_total_910 = null;
	String grand_totally = null;  
	
	//String A1Limit="4";
	//String A1TotalLimit="20";  
	String RowTotalLimit="15";
	String GrandTotalLimit="75";
	int countButton=0;
	Boolean R13BTech=false;
	EditText tv_mark1a, tv_mark1b;

	EditText tv_mark2a, tv_mark2b;

	EditText tv_mark3a, tv_mark3b;  

	EditText tv_mark4a, tv_mark4b;

	EditText tv_mark5a, tv_mark5b;

	EditText tv_mark6a, tv_mark6b;

	EditText tv_mark7a, tv_mark7b;

	EditText tv_mark8a, tv_mark8b;

	EditText tv_mark9a, tv_mark9b;

	EditText tv_mark10a, tv_mark10b;


//	TextView tv_mark1_total, tv_mark_2_3_total, tv_mark_4_5_total,
//			tv_mark_6_7_total, tv_mark_8_9_total, tv_mark_10_11_total;
//
//	EditText tv_grand_toal;
	protected boolean isBool = false, timer_status;
	protected int timelimit = 0;
	private ProgressDialog progressDialog;

	int timeInterval = 0;
	Date date_temp = null;
	boolean navigationFromGrandTotalSummary;
	Utility instanceUtitlity;
	private PowerManager.WakeLock wl;
	HashMap<Integer, String> RemarksArray;
	BroadcastReceiver batteryLevelReceiver;
	Boolean clickFind = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.scrutiny_mark_dialog_r13_btech);
		setContentView(R.layout.scrutiny_mark_dialog_r13_btech_specialcase);
		// if (!Utility.isNetworkAvailable(this)) {  
		// alertMessageForChargeAutoUpdateApk(getString(R.string.alert_network_avail));
		// return;
		// }
		instanceUtitlity = new Utility();
		RemarksArray = new HashMap<Integer, String>();
		findViewById(R.id.tv_sub_code).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_h_sub_code).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_h_user_id).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_user_id).setVisibility(View.VISIBLE );
		R13BTech=instanceUtitlity
				.isRegulation_R13_Btech(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
		if (R13BTech) {
			// A1Limit="3";
			 //A1TotalLimit="25";
			 RowTotalLimit="15";
			 GrandTotalLimit="75";
		}
		//Log.w(A1TotalLimit, RowTotalLimit+" "+GrandTotalLimit);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"Scrutiny_MarkDialog_R13_Mtech");

		// hide keyboard
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		// get data from previous activity/screen
		Intent intent_extras = getIntent();
		navigationFromGrandTotalSummary = intent_extras
				.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO);
		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);  
		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		((TextView) findViewById(R.id.tv_user_id)).setText(userId);  
		((TextView) findViewById(R.id.tv_h_user_id))
				.setText("Scrutinizer Id : ");

		if (intent_extras.hasExtra(SSConstants.ANS_BOOK_BARCODE)) {
			ansBookBarcode = getIntent().getStringExtra(
					SSConstants.ANS_BOOK_BARCODE);  
		}
		bundle_serial_no = intent_extras
				.getStringExtra(SSConstants.BUNDLE_SERIAL_NO);

		String date = intent_extras.getStringExtra("noteDate");
		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE MMM d HH:mm:ss zzz yyyy");

		try {
			date_temp = formatter.parse(date);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		CountDownTimer myCountdownTimer = new CountDownTimer(30000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				timelimit = (int) (millisUntilFinished / 1000);
			}
			@Override
			public void onFinish() {
				isBool = true;
			}
		}.start();
		if (!navigationFromGrandTotalSummary)
			myCountdownTimer.start();
		else
			isBool = true;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor cursor = _database.executeSQLQuery(
				"select time_interval as Value from table_date_configuration",
				null);
		// ansBookBarcode="11273960455";
		// bundleNo="2D070220001";
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				timeInterval = cursor.getInt(cursor.getColumnIndex("Value"));

			}
			cursor.close();
		}
		// (findViewById(R.id.btn_submit1)).setOnClickListener(this);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				showProgress();
			}
		});

		showItems();
		showMarks();
		showRemarksWithGreenColor();
		// final TextView tvTimer = (TextView)
		// findViewById(R.id.tv_timeinterval);
		// if (!navigationFromGrandTotalSummary) {
		//
		// final String dateTimeInterval = Utility.getDateDifference(
		// date_temp, timeInterval, this);
		//
		// if (!TextUtils.isEmpty(dateTimeInterval)) {
		// if (Integer.valueOf(dateTimeInterval) > 0
		// && Integer.valueOf(dateTimeInterval) < timeInterval + 1) {
		// // showing timer
		// new CountDownTimer((Math.abs((timeInterval - Integer
		// .valueOf(dateTimeInterval))) * 1000), 1000) {
		//
		// public void onTick(long millisUntilFinished) {
		// if (millisUntilFinished / 1000 > 0
		// && millisUntilFinished / 1000 < timeInterval + 1) {
		// tvTimer.setVisibility(View.VISIBLE);
		// tvTimer.setText("Scrutiny Time : "
		// + millisUntilFinished / 1000);
		// }
		// }
		//
		// public void onFinish() {
		// tvTimer.setText("");
		// }
		// }.start();
		// } else {
		// tvTimer.setVisibility(View.GONE);
		// }
		// } else {
		// tvTimer.setVisibility(View.GONE);
		// }
		//
		// } else {
		// tvTimer.setVisibility(View.GONE);
		// }
		// tvTimer.setVisibility(View.GONE);
		hideProgress();
		Button btnSubmit1 = (Button) findViewById(R.id.btn_submit1);
		btnSubmit1.setVisibility(View.GONE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		LinearLayout ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
		ll_submit.addView(addNumbersView());
	}

	private View addNumbersView() {
		View numbersView = LayoutInflater.from(this).inflate(
				R.layout.scrutiny_layout_numbers, null);
		numbersView.findViewById(R.id.button1).setOnClickListener(this);
		numbersView.findViewById(R.id.button2).setOnClickListener(this);
		numbersView.findViewById(R.id.button3).setOnClickListener(this);
		numbersView.findViewById(R.id.button4).setOnClickListener(this);

		numbersView.findViewById(R.id.button5).setOnClickListener(this);
		numbersView.findViewById(R.id.button6).setOnClickListener(this);
		numbersView.findViewById(R.id.button7).setOnClickListener(this);
		numbersView.findViewById(R.id.button8).setOnClickListener(this);

		numbersView.findViewById(R.id.button9).setOnClickListener(this);
		numbersView.findViewById(R.id.button0).setOnClickListener(this);
		numbersView.findViewById(R.id.btn_dot).setOnClickListener(this);
		numbersView.findViewById(R.id.btn_clear).setOnClickListener(this);
		numbersView.findViewById(R.id.btn_delete).setOnClickListener(this);
		numbersView.findViewById(R.id.btn_submit).setOnClickListener(this);
		return numbersView;
	}

	// set Marks to EditText
	private void setMarkToCellFromDB(String pMark, EditText pTextView) {
		if (!TextUtils.isEmpty(pMark)) {
			pTextView.setTag(pMark);
		} else {
			pTextView.setTag("");
		}
	}

	// set Marks to TextView
	private void setMarkToCellFromDB(String pMark, TextView pTextView) {
		if (!TextUtils.isEmpty(pMark)) {
			pTextView.setText(pMark);
		} else {
			pTextView.setText("");
		}
	}

	// set Marks to EditText
	private void setMarkToCellScrutiny(String pMark, EditText pTextView) {
		if (!TextUtils.isEmpty(pMark)) {
			pTextView.setText(pMark);
		} else {
			pTextView.setText("");
		}
	}

	// set Marks to TextView
	private void setMarkToCellScrutiny(String pMark, TextView pTextView) {
		if (!TextUtils.isEmpty(pMark)) {
			pTextView.setText(pMark);
		} else {
			pTextView.setText("");
		}
	}

	// show and set values to views
	private void showItems() {
		tv_mark1a = ((EditText) findViewById(R.id.q1_a));
		tv_mark1b = ((EditText) findViewById(R.id.q1_b));

		tv_mark2a = ((EditText) findViewById(R.id.q2_a));
		tv_mark2b = ((EditText) findViewById(R.id.q2_b));

		tv_mark3a = ((EditText) findViewById(R.id.q3_a));
		tv_mark3b = ((EditText) findViewById(R.id.q3_b));

		tv_mark4a = ((EditText) findViewById(R.id.q4_a));
		tv_mark4b = ((EditText) findViewById(R.id.q4_b));

		tv_mark5a = ((EditText) findViewById(R.id.q5_a));
		tv_mark5b = ((EditText) findViewById(R.id.q5_b));

		tv_mark6a = ((EditText) findViewById(R.id.q6_a));
		tv_mark6b = ((EditText) findViewById(R.id.q6_b));

		tv_mark7a = ((EditText) findViewById(R.id.q7_a));
		tv_mark7b = ((EditText) findViewById(R.id.q7_b));

		tv_mark8a = ((EditText) findViewById(R.id.q8_a));
		tv_mark8b = ((EditText) findViewById(R.id.q8_b));

		tv_mark9a = ((EditText) findViewById(R.id.q9_a));
		tv_mark9b = ((EditText) findViewById(R.id.q9_b));

		tv_mark10a = ((EditText) findViewById(R.id.q10_a));
		tv_mark10b = ((EditText) findViewById(R.id.q10_b));

		/*tv_mark1_total = ((TextView) findViewById(R.id.q1_total));
		tv_mark_2_3_total = ((TextView) findViewById(R.id.q2_3_total));
		tv_mark_4_5_total = ((TextView) findViewById(R.id.q4_5_total));
		tv_mark_6_7_total = ((TextView) findViewById(R.id.q6_7_total));
		tv_mark_8_9_total = ((TextView) findViewById(R.id.q8_9_total));
		tv_mark_10_11_total = ((TextView) findViewById(R.id.q10_11_total));

		tv_grand_toal = ((EditText) findViewById(R.id.grand_total));
		tv_grand_toal.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(s.toString())) {
					try {

						if (Float.parseFloat(s.toString()) > Float
								.parseFloat(GrandTotalLimit)) {
							alertForInvalidMark(tv_grand_toal, true,
									" Total Exceeds "+GrandTotalLimit);
						}

					} catch (Exception e) {

					}
				}
			}
		});*/
		Cursor cursor = null;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		if (!getIntent().hasExtra(
				SSConstants.FROM_CLASS_MISS_MATCH_SCRIPT_WITH_DB)) {
			cursor = _database.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
					SSConstants.ANS_BOOK_BARCODE + " = '" + ansBookBarcode
							+ "' AND " + SSConstants.BUNDLE_NO + " = '"
							+ bundleNo + "'", null);
		} else {
			cursor = _database.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
					SSConstants.BUNDLE_SERIAL_NO + " = '" + bundle_serial_no
							+ "' AND " + SSConstants.BUNDLE_NO + " = '"
							+ bundleNo + "'", null);
		}

		if (cursor.getCount() > 0 && cursor != null) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {

				subjectCode = cursor.getString(cursor
						.getColumnIndex(SSConstants.SUBJECT_CODE));

				setMarkToCellFromDB(subjectCode,
						((TextView) findViewById(R.id.tv_sub_code)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)),
						((TextView) findViewById(R.id.tv_ans_book)));

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.USER_ID)),
				// ((TextView) findViewById(R.id.tv_user_id)));

				subjectCode = cursor.getString(cursor
						.getColumnIndex(SSConstants.SUBJECT_CODE));

				setMarkToCellFromDB(subjectCode,
						((TextView) findViewById(R.id.tv_sub_code)));

				bundle_serial_no = cursor.getString(cursor
						.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO));
				setMarkToCellFromDB(bundle_serial_no,
						((TextView) findViewById(R.id.tv_ans_book)));

				// String userId = cursor.getString(cursor
				// .getColumnIndex(SSConstants.USER_ID));
				// setMarkToCellFromDB(userId,
				// ((TextView) findViewById(R.id.tv_user_id)));

				// Marks1
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1A)), tv_mark1a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1B)), tv_mark1b);

				// Marks2
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2A)), tv_mark2a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2B)), tv_mark2b);
				
				// Marks3
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3A)), tv_mark3a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3B)), tv_mark3b);
				
				// Marks4
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4A)), tv_mark4a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4B)), tv_mark4b);
				
				// Marks5
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5A)), tv_mark5a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5B)), tv_mark5b);
				

				// Marks6
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6A)), tv_mark6a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6B)), tv_mark6b);
				
				// Marks7
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7A)), tv_mark7a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7B)), tv_mark7b);

				// Marks8
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8A)), tv_mark8a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8B)), tv_mark8b);
				
				// Marks9
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK9A)), tv_mark9a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK9B)), tv_mark9b);

				// Marks10
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK10A)), tv_mark10a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK10B)), tv_mark10b);

			}
		} else {
			showAlert(getString(R.string.alert_barcode_not_exists_in_db),
					getString(R.string.alert_dialog_ok), "", false,
					SSConstants.FAILURE);
		}
		cursor.close();
		/*
		 * tv_mark1a.setOnClickListener(this);
		 * tv_mark1b.setOnClickListener(this);
		 * tv_mark1c.setOnClickListener(this);
		 * tv_mark1d.setOnClickListener(this);
		 * tv_mark1e.setOnClickListener(this); tv_mark1f.setFocusable(false);
		 * tv_mark1g.setFocusable(false); tv_mark1h.setFocusable(false);
		 * tv_mark1i.setFocusable(false); tv_mark1j.setFocusable(false);
		 * 
		 * tv_mark2a.setOnClickListener(this);
		 * tv_mark2b.setOnClickListener(this);
		 * tv_mark2c.setOnClickListener(this);
		 * 
		 * tv_mark3a.setOnClickListener(this);
		 * tv_mark3b.setOnClickListener(this);
		 * tv_mark3c.setOnClickListener(this);
		 * 
		 * tv_mark4a.setOnClickListener(this);
		 * tv_mark4b.setOnClickListener(this);
		 * tv_mark4c.setOnClickListener(this);
		 * 
		 * tv_mark5a.setOnClickListener(this);
		 * tv_mark5b.setOnClickListener(this);
		 * tv_mark5c.setOnClickListener(this);
		 * 
		 * tv_mark6a.setOnClickListener(this);
		 * tv_mark6b.setOnClickListener(this);
		 * tv_mark6c.setOnClickListener(this);
		 * 
		 * tv_mark7a.setOnClickListener(this);
		 * tv_mark7b.setOnClickListener(this);
		 * tv_mark7c.setOnClickListener(this);
		 * 
		 * tv_mark8a.setOnClickListener(this);
		 * tv_mark8b.setOnClickListener(this);
		 * tv_mark8c.setOnClickListener(this);
		 * 
		 * tv_mark9a.setOnClickListener(this);
		 * tv_mark9b.setOnClickListener(this);
		 * tv_mark9c.setOnClickListener(this);
		 * 
		 * tv_mark10a.setOnClickListener(this);
		 * tv_mark10b.setOnClickListener(this);
		 * tv_mark10c.setOnClickListener(this);
		 * 
		 * tv_mark11a.setOnClickListener(this);
		 * tv_mark11b.setOnClickListener(this);
		 * tv_mark11c.setOnClickListener(this);
		 */
		tv_mark1a.setOnLongClickListener(this);
		tv_mark1b.setOnLongClickListener(this);
		
		tv_mark2a.setOnLongClickListener(this);
		tv_mark2b.setOnLongClickListener(this);

		tv_mark3a.setOnLongClickListener(this);
		tv_mark3b.setOnLongClickListener(this);

		tv_mark4a.setOnLongClickListener(this);
		tv_mark4b.setOnLongClickListener(this);

		tv_mark5a.setOnLongClickListener(this);
		tv_mark5b.setOnLongClickListener(this);

		tv_mark6a.setOnLongClickListener(this);
		tv_mark6b.setOnLongClickListener(this);

		tv_mark7a.setOnLongClickListener(this);
		tv_mark7b.setOnLongClickListener(this);

		tv_mark8a.setOnLongClickListener(this);
		tv_mark8b.setOnLongClickListener(this);

		tv_mark9a.setOnLongClickListener(this);
		tv_mark9b.setOnLongClickListener(this);

		tv_mark10a.setOnLongClickListener(this);
		tv_mark10b.setOnLongClickListener(this);

		tv_mark1a.setFocusable(true);
		tv_mark1b.setFocusable(true);
		
		tv_mark2a.setFocusable(true);
		tv_mark2b.setFocusable(true);

		tv_mark3a.setFocusable(true);
		tv_mark3b.setFocusable(true);

		tv_mark4a.setFocusable(true);
		tv_mark4b.setFocusable(true);

		tv_mark5a.setFocusable(true);
		tv_mark5b.setFocusable(true);

		tv_mark6a.setFocusable(true);
		tv_mark6b.setFocusable(true);

		tv_mark7a.setFocusable(true);
		tv_mark7b.setFocusable(true);

		tv_mark8a.setFocusable(true);
		tv_mark8b.setFocusable(true);

		tv_mark9a.setFocusable(true);
		tv_mark9b.setFocusable(true);

		tv_mark10a.setFocusable(true);
		tv_mark10b.setFocusable(true);

		tv_mark1a.setFocusableInTouchMode(true);
		tv_mark1b.setFocusableInTouchMode(true);
		
		tv_mark2a.setFocusableInTouchMode(true);
		tv_mark2b.setFocusableInTouchMode(true);

		tv_mark3a.setFocusableInTouchMode(true);
		tv_mark3b.setFocusableInTouchMode(true);

		tv_mark4a.setFocusableInTouchMode(true);
		tv_mark4b.setFocusableInTouchMode(true);

		tv_mark5a.setFocusableInTouchMode(true);
		tv_mark5b.setFocusableInTouchMode(true);

		tv_mark6a.setFocusableInTouchMode(true);
		tv_mark6b.setFocusableInTouchMode(true);

		tv_mark7a.setFocusableInTouchMode(true);
		tv_mark7b.setFocusableInTouchMode(true);

		tv_mark8a.setFocusableInTouchMode(true);
		tv_mark8b.setFocusableInTouchMode(true);

		tv_mark9a.setFocusableInTouchMode(true);
		tv_mark9b.setFocusableInTouchMode(true);

		tv_mark10a.setFocusableInTouchMode(true);
		tv_mark10b.setFocusableInTouchMode(true);

	}

	// harinath
	void showMarks() {

		Cursor cursor = null;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		if (!getIntent().hasExtra(
				SSConstants.FROM_CLASS_MISS_MATCH_SCRIPT_WITH_DB)) {
			cursor = _database.passedQuery(SSConstants.TABLE_EVALUATION_SAVE,
					SSConstants.ANS_BOOK_BARCODE + " = '" + ansBookBarcode
							+ "' AND " + SSConstants.BUNDLE_NO + " = '"
							+ bundleNo + "'", null);
		} else {
			cursor = _database.passedQuery(SSConstants.TABLE_EVALUATION_SAVE,
					SSConstants.BUNDLE_SERIAL_NO + " = '" + bundle_serial_no
							+ "' AND " + SSConstants.BUNDLE_NO + " = '"
							+ bundleNo + "'", null);
		}

		if (cursor.getCount() > 0 && cursor != null) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {

				// Marks1
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1A)), tv_mark1a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1B)), tv_mark1b);

//				setMarkToCellScrutiny(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R1_TOTAL)), tv_mark1_total);

				// Marks2
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2A)), tv_mark2a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2B)), tv_mark2b);
				
				// Marks3
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3A)), tv_mark3a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3B)), tv_mark3b);
				

//				setMarkToCellScrutiny(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R2_3TOTAL)),
//						tv_mark_2_3_total);
				// Marks4
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4A)), tv_mark4a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4B)), tv_mark4b);
				
				// Marks5
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5A)), tv_mark5a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5B)), tv_mark5b);

//				setMarkToCellScrutiny(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R4_5TOTAL)),
//						tv_mark_4_5_total);
				// Marks6
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6A)), tv_mark6a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6B)), tv_mark6b);
				
				// Marks7
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7A)), tv_mark7a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7B)), tv_mark7b);
				

//				setMarkToCellScrutiny(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R6_7TOTAL)),
//						tv_mark_6_7_total);
				// Marks8
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8A)), tv_mark8a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8B)), tv_mark8b);
				
				// Marks9
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK9A)), tv_mark9a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK9B)), tv_mark9b);
				

//				setMarkToCellScrutiny(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R8_9TOTAL)),
//						tv_mark_8_9_total);
				// Marks10
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK10A)), tv_mark10a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK10B)), tv_mark10b);
				
//				setMarkToCellScrutiny(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R10_11TOTAL)),
//						tv_mark_10_11_total);
//				setMarkToCellScrutiny(cursor.getString(cursor
//						.getColumnIndex(SSConstants.GRAND_TOTAL_MARK)),
//						tv_grand_toal);

			}
		} else {
			showAlert(getString(R.string.alert_barcode_not_exists_in_db),
					getString(R.string.alert_dialog_ok), "", false,
					SSConstants.FAILURE);
		}
		cursor.close();
	}

	// show alert
	private void showAlert(String msg, String positiveStr, String negativeStr,
			final boolean navigation, final int scrutinyStatus) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle("Smart Scrutinization");
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(positiveStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
						if (navigation) {
							updateDBProcess(scrutinyStatus);
							
						} else {
							finish();
						}

					}
				});

		myAlertDialog.setNegativeButton(negativeStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	private void showAlertSubmit() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle("Smart Scrutinization");
		myAlertDialog.setMessage(getString(R.string.alert_submit_scrutiny_summary));
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						getRemarkComplete();
						row_total_12 = row_1_2_Total_();
						row_total_34 = row_3_4_Total_();
						row_total_56 = row_5_6_Total_();
						row_total_78 = row_7_8_Total_();
						row_total_910 = row_9_10_Total_();
						grand_totally = calculateGrandTotal();
					if (RemarksArray.size() == 0) {
					updateDBProcess(SSConstants.SCRUTINY_STATUS_4_NO_CORRECTION);
					}else{
					updateDBProcess(SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED);
					}
					dialog.dismiss();
					}
				});

		myAlertDialog.setNegativeButton(getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}
	
	private void alertMsgForSecondsRemaining(String pMsg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setTitle("Smart Scrutinization");

		myAlertDialog.setMessage(pMsg);
		myAlertDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						dialog.dismiss();

					}

				});

		myAlertDialog.show();
	}

	private String getPresentTime() {
		try {
			// set the format here
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
		} catch (TimeFormatException tfe) {
			return null;
		}
	}

	// change EditText or TextView background to Green
	private void changeEditTextCellBGToGreen(View view) {
		if (!checkView(view)) {
			EditText tv = (EditText) view;
			tv.setBackgroundResource(R.drawable.green_with_border);
			tv.setTextColor(getResources().getColor(R.color.white));
			// tv.setTypeface(Typeface.DEFAULT_BOLD);

		} else {
			TextView tv = (TextView) view;
			tv.setBackgroundResource(R.drawable.green_with_border);
			tv.setTextColor(getResources().getColor(R.color.white));
			// tv.setTypeface(Typeface.DEFAULT_BOLD);
		}

	}

	private void changeEditTextCellBGToGreen(View view, String ss) {
		if (!checkView(view)) {
			EditText tv = (EditText) view;
			tv.setBackgroundResource(R.drawable.green_with_border);
			tv.setTextColor(getResources().getColor(R.color.white));
			// tv.setTypeface(Typeface.DEFAULT_BOLD);
			RemarksArray.put(view.getId(), ss);
		} else {
			TextView tv = (TextView) view;
			tv.setBackgroundResource(R.drawable.green_with_border);
			tv.setTextColor(getResources().getColor(R.color.white));
			// tv.setTypeface(Typeface.DEFAULT_BOLD);
		}

	}

	// change TextView background to Green
	private void changeTextViewCellBGToGreen(View view) {
		TextView tv = (TextView) view;
		tv.setBackgroundResource(R.drawable.green_with_border);
		tv.setTextColor(getResources().getColor(R.color.white));
		// tv.setTypeface(Typeface.DEFAULT_BOLD);
	}

	// navigate to ShowGrandTotalSummaryTable Activity
	private void navigateToShowGrandTotalSummaryTableActivity() {
		Intent _intent = new Intent(this,
				Scrutiny_ShowGrandTotalSummaryTable.class);
		_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
		_intent.putExtra(SSConstants.USER_ID, userId);
		_intent.putExtra(SSConstants.SUBJECT_CODE, subjectCode);
		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(_intent);
		finish();
	}

	// navigate to ShowGrandTotalSummaryTable Activity or ScanActivity
	private void switchToShowGrandTotalSummaryTableOrScanActivity() {
		Intent _intent;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor_scripts_count = _database.passedQuery(
				SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO + " = '"
						+ bundleNo + "'", null);
		int scriptsCount = _cursor_scripts_count.getCount();  
		_cursor_scripts_count.close();
		if (scriptsCount == Integer.valueOf(bundle_serial_no)   
				|| navigationFromGrandTotalSummary) {
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

			// if count greater than 0 then it is next/2nd observation
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
			_intent.putExtra(SSConstants.SUBJECT_CODE, subjectCode);
			_intent.putExtra(SSConstants.USER_ID, userId);
			startActivity(_intent);
			finish();
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
			if (_cursor.getInt(_cursor
					.getColumnIndex(SSConstants.SCRUTINIZE_STATUS)) == SSConstants.SCRUTINY_STATUS_6_SCRIPT_COMPLETED) {
				flag = true;
			} else {
				flag = false;
			}
			// flag = _cursor.getInt(
			// _cursor.getColumnIndex(SSConstants.SCRUTINIZE_STATUS))
			// .equals(SSConstants.SCRUTINY_STATUS_6_SCRIPT_COMPLETED);
		} else {
			flag = false;
		}
		_cursor.close();
		return flag;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Toast.makeText(this, getString(R.string.alert_press_home),
					Toast.LENGTH_LONG).show();
			return false;
		}
		return false;
	}

	// set view color
	private void showRemarksInETWithGreenColor(String C_REMARK,
			EditText PeditText) {
		if (!TextUtils.isEmpty(C_REMARK)) {
			PeditText.setFocusableInTouchMode(false);
			PeditText.setOnClickListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
			changeEditTextCellBGToGreen(PeditText, C_REMARK);
		}
	}

	// set view color
	private void showRemarksInTVWithGreenColor(String C_REMARK,
			TextView PeditText) {
		if (!TextUtils.isEmpty(C_REMARK)) {
			changeTextViewCellBGToGreen(PeditText);
		}
	}

	// showing remarks with color
	private void showRemarksWithGreenColor() {
		Cursor _cursor;
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		_cursor = _db_for_scrutiny
				.passedQuery(SSConstants.TABLE_EVALUATION_SAVE,
						SSConstants.ANS_BOOK_BARCODE + " = '" + ansBookBarcode
								+ "' AND " + SSConstants.BUNDLE_NO + " = '"
								+ bundleNo + "'", null);
		if (_cursor.getCount() > 0 && _cursor != null) {

			// mark1
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M1A_REMARK)), tv_mark1a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M1B_REMARK)), tv_mark1b);
			
			// mark2
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M2A_REMARK)), tv_mark2a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M2B_REMARK)), tv_mark2b);

			// mark3
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M3A_REMARK)), tv_mark3a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M3B_REMARK)), tv_mark3b);

			// mark4
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M4A_REMARK)), tv_mark4a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M4B_REMARK)), tv_mark4b);

			// mark5
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M5A_REMARK)), tv_mark5a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M5B_REMARK)), tv_mark5b);

			// mark6
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M6A_REMARK)), tv_mark6a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M6B_REMARK)), tv_mark6b);

			// mark7
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M7A_REMARK)), tv_mark7a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M7B_REMARK)), tv_mark7b);

			// mark8
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M8A_REMARK)), tv_mark8a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M8B_REMARK)), tv_mark8b);

			// mark9
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M9A_REMARK)), tv_mark9a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M9B_REMARK)), tv_mark9b);

			// mark10
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M10A_REMARK)), tv_mark10a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M10B_REMARK)), tv_mark10b);

			/*showRemarksInTVWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.R1_REMARK)), tv_mark1_total);

			showRemarksInTVWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.R2_REMARK)), tv_mark_2_3_total);

			showRemarksInTVWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.R4_REMARK)), tv_mark_4_5_total);

			showRemarksInTVWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.R6_REMARK)), tv_mark_6_7_total);

			showRemarksInTVWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.R8_REMARK)), tv_mark_8_9_total);

			showRemarksInTVWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.R10_REMARK)),
					tv_mark_10_11_total);*/

		}
		_cursor.close();
	}

	// check remark count +++++++++++++++++++++=
	private void checkRemarkCountForShowingAlert() {
		if (RemarksArray.size() == 0) {
			showAlert(getString(R.string.alert_submit_scrutiny_summary),
					getString(R.string.alert_dialog_ok),
					getString(R.string.alert_dialog_cancel), true,
					SSConstants.SCRUTINY_STATUS_4_NO_CORRECTION);
		} else {
			showAlert(getString(R.string.alert_submit_scrutiny_summary),
					getString(R.string.alert_dialog_ok),
					getString(R.string.alert_dialog_cancel), true,
					SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED);
		}
}

	private void showAlertForManualMarksWrongEntry(String msg,
			String positiveStr, String negativeStr) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setPositiveButton(positiveStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
					}
				});
		myAlertDialog.setNegativeButton(negativeStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
					}
				});
		myAlertDialog.show();
	}

	// onClick widget
	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		// case R.id.grand_total:
		// onTotalCellClick(SEConstants.GRAND_TOTAL_MARK,
		// SEConstants.GRAND_TOTAL_REMARK, v);
		// break;

		case R.id.button1:
			clickFind=true;
			setTextToFocusedView("1");
			break;

		case R.id.button2:
			clickFind=true;
			setTextToFocusedView("2");
			break;

		case R.id.button3:
			clickFind=true;
			setTextToFocusedView("3");
			break;

		case R.id.button4:
			clickFind=true;
			setTextToFocusedView("4");
			break;

		case R.id.button5:
			clickFind=true;
			setTextToFocusedView("5");
			break;

		case R.id.button6:
			clickFind=true;
			setTextToFocusedView("6");
			break;

		case R.id.button7:
			clickFind=true;
			setTextToFocusedView("7");
			break;

		case R.id.button8:
			clickFind=true;
			setTextToFocusedView("8");
			break;

		case R.id.button9:
			clickFind=true;
			setTextToFocusedView("9");
			break;

		case R.id.button0:
			clickFind=true;
			setTextToFocusedView("0");
			break;

		case R.id.btn_dot:
			View dotFocusedView = getCurrentFocus();
			if (dotFocusedView != null && dotFocusedView instanceof EditText) {
				String data = ((EditText) dotFocusedView).getText().toString()
						.trim();
				if (!(data.contains("."))) {
					clickFind=true;
					setTextToFocusedView(".");
				}
			}
			break;

		case R.id.btn_clear:
			View focusedView = getCurrentFocus();
			if (focusedView != null && focusedView instanceof EditText) {
				((EditText) focusedView).setText("");
				// setRemarkInContentValue2(focusedView, "", false, false);
				// calculateTotal();
				setTotaltoTextView((EditText) focusedView);
			}
			break;

		case R.id.btn_delete:
			View focusedView2 = getCurrentFocus();
			if (focusedView2 != null && focusedView2 instanceof EditText) {
				deleteCharAndSetSelection((EditText) focusedView2);
				setTotaltoTextView((EditText) focusedView2);
				// calculateTotal();
			}
			break;

		case R.id.btn_submit:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromInputMethod(
					tv_mark2b.getWindowToken(), 0);
			
			if (isBool) { 
				countButton=countButton+1;
			//	showAlertSubmit();
				
			if (!getRemarkComplete()) {
				row_total_12 = row_1_2_Total_();
				row_total_34 = row_3_4_Total_();
				row_total_56 = row_5_6_Total_();
				row_total_78 = row_7_8_Total_();
				row_total_910 = row_9_10_Total_();
				grand_totally = calculateGrandTotal();
				if (checkGroundTotal(grand_totally)) {
					if(countButton<2){
					checkRemarkCountForShowingAlert();
					}else{
						if (RemarksArray.size() == 0) {
							showAlert(getString(R.string.alert_submit_scrutiny_summary),
									getString(R.string.alert_dialog_ok),
									getString(R.string.alert_dialog_cancel), true,
									SSConstants.SCRUTINY_STATUS_4_NO_CORRECTION);
						}else{
						alertMessageSubmit(getString(R.string.app_name),
								"Your limit is over");
						//	updateDBProcess(SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED);
							}
					}
			}  
			} else {
				alertMessageForCharge(getString(R.string.app_name),
						getString(R.string.alert_remarks), false);
			}
			} else {
				alertMsgForSecondsRemaining("Scrutinization Time for Each Script is Set to a Minimum of 30 "
						+ "Seconds.\nPlease, Continue Scrutinization for the Next "
						+ timelimit + " Seconds...! ");
			}
			break;
		case R.id.q1_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q1_b:
			showEditSelectionDialog(view);
			break;
			
		case R.id.q2_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q2_b:
			showEditSelectionDialog(view);
			break;

		case R.id.q3_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q3_b:
			showEditSelectionDialog(view);
			break;

		case R.id.q4_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q4_b:
			showEditSelectionDialog(view);
			break;

		case R.id.q5_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q5_b:
			showEditSelectionDialog(view);
			break;

		case R.id.q6_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q6_b:
			showEditSelectionDialog(view);
			break;

		case R.id.q7_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q7_b:
			showEditSelectionDialog(view);
			break;

		case R.id.q8_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q8_b:
			showEditSelectionDialog(view);
			break;

		case R.id.q9_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q9_b:
			showEditSelectionDialog(view);
			break;

		case R.id.q10_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q10_b:
			showEditSelectionDialog(view);
			break;
		
		default:
			break;
		}

	}

	private void alertMessageSubmit(String title, String msg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(title);
		myAlertDialog.setMessage(msg);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						/*if (RemarksArray.size() == 0) {
							updateDBProcess(SSConstants.SCRUTINY_STATUS_4_NO_CORRECTION);
							}else{*/
							updateDBProcess(SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED);
							//}
						Dialog.dismiss();
					}
				});
		myAlertDialog.setCancelable(false);
		myAlertDialog.show();
	}

	private int storeRemarksDB() {
		for (Map.Entry<Integer, String> e : RemarksArray.entrySet()) {
			String remarkOrMark = RemarksArray.get(e.getKey());
			int setRemark = 1;
			Log.v("key" + e.getKey(), "value" + remarkOrMark);
			switch (e.getKey()) {
			case R.id.q1_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M1A_REMARK);
				break;
			case R.id.q1_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M1B_REMARK);
				break;

				// Q2
			case R.id.q2_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M2A_REMARK);
				break;
			case R.id.q2_b:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M2B_REMARK);
				break;

			// Q3
			case R.id.q3_a:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M3A_REMARK);
				break;
			case R.id.q3_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M3B_REMARK);
				break;
				
			// Q4
			case R.id.q4_a:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M4A_REMARK);
				break;
			case R.id.q4_b:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M4B_REMARK);
				break;

			// Q5
			case R.id.q5_a:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M5A_REMARK);
				break;
			case R.id.q5_b:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M5B_REMARK);
				break;
			// Q6
			case R.id.q6_a:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M6A_REMARK);
				break;
			case R.id.q6_b:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M6B_REMARK);
				break;
			// Q7
			case R.id.q7_a:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M7A_REMARK);
				break;
			case R.id.q7_b:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M7B_REMARK);
				break;

			// Q8
			case R.id.q8_a:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M8A_REMARK);
				break;
			case R.id.q8_b:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M8B_REMARK);
				break;

			// Q9
			case R.id.q9_a:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M9A_REMARK);
				break;
			case R.id.q9_b:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M9B_REMARK);
				break;

			// Q10
			case R.id.q10_a:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M10A_REMARK);
				break;
			case R.id.q10_b:

				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M10B_REMARK);
				break;

			/*case R.id.q1_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R1_2_REMARK);
				break;

			case R.id.q2_3_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R3_4_REMARK);
				break;

			case R.id.q4_5_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R5_6_REMARK);
				break;

			case R.id.q6_7_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R7_8_REMARK);
				break;

			case R.id.q8_9_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R9_10_REMARK);
				break;

			case R.id.q10_11_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R10_REMARK);
				break;*/

			default:
				break;
			}
		}
		return 0;
	}

	private boolean getRemarkComplete() {
		// TODO Auto-generated method stub
		Boolean checkRemark = false;
		if (setSampleRemark(tv_mark1a))
			checkRemark = true;
		if (setSampleRemark(tv_mark1b))
			checkRemark = true;
		
		if (setSampleRemark(tv_mark2a))
			checkRemark = true;
		if (setSampleRemark(tv_mark2b))
			checkRemark = true;
		
		if (setSampleRemark(tv_mark3a))
			checkRemark = true;
		if (setSampleRemark(tv_mark3b))
			checkRemark = true;

		if (setSampleRemark(tv_mark4a))
			checkRemark = true;
		if (setSampleRemark(tv_mark4b))
			checkRemark = true;

		if (setSampleRemark(tv_mark5a))
			checkRemark = true;
		if (setSampleRemark(tv_mark5b))
			checkRemark = true;

		if (setSampleRemark(tv_mark6a))
			checkRemark = true;
		if (setSampleRemark(tv_mark6b))
			checkRemark = true;

		if (setSampleRemark(tv_mark7a))
			checkRemark = true;
		if (setSampleRemark(tv_mark7b))
			checkRemark = true;

		if (setSampleRemark(tv_mark8a))
			checkRemark = true;
		if (setSampleRemark(tv_mark8b))
			checkRemark = true;

		if (setSampleRemark(tv_mark9a))
			checkRemark = true;
		if (setSampleRemark(tv_mark9b))
			checkRemark = true;

		if (setSampleRemark(tv_mark10a))
			checkRemark = true;
		if (setSampleRemark(tv_mark10b))
			checkRemark = true;

		return checkRemark;
	}

	// showing remark selection dialog
	private void showRemarkSelectionDialog(final View view) {
		// int selection = setRemarkInContentValue(view, "", 0);
		RemarksArray.remove(view.getId());
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		builder.setTitle("Select Remark");
		builder.setCancelable(false);
		final CharSequence[] remarksList = getRemarksBasedOnSelection(view);
		builder.setSingleChoiceItems(remarksList, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// setRemarkInContentValue(view,
						// remarksList[which].toString(), 1);
						RemarksArray.put(view.getId(),
								remarksList[which].toString());
Log.v("remark", remarksList[which].toString());
						changeEditTextCellBGToGreen(view);

						EditText et = (EditText) view;
						// et.setFocusableInTouchMode(true);
						// et.requestFocus();
						// et.setOnClickListener(Scrutiny_MarkDialog_R13_Mtech.this);
						et.setError(null);
						et.setFocusableInTouchMode(false);
						et.setOnClickListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
						et.clearFocus();
						dialog.dismiss();

					}
				});
		builder.setPositiveButton("Clear Remark",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						EditText tv = (EditText) view;
						tv.setBackgroundResource(R.drawable.cell_shape_in_grey);
						tv.setTextColor(getResources().getColor(R.color.black));
						// tv.setTypeface(Typeface.DEFAULT_BOLD);
						// setRemarkInContentValue(view, "", 1);
						RemarksArray.remove(view.getId());
						tv.setFocusableInTouchMode(true);
						tv.requestFocus();
						tv.setError(null);
						tv.setOnClickListener(null);
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromInputMethod(getCurrentFocus()
								.getWindowToken(), 0);
					}
				});
		builder.setNegativeButton(
				getResources().getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	// get and show remark for clicked cell
	private CharSequence[] getRemarksBasedOnSelection(View view) {
		TextView tv = (TextView) view;
		// final CharSequence[] remarksList ;
		// "Marks posted in Answer Script but not in Tablet",
		// "Not evaluated in Answer Script but posted in Tablet",
		// "Mismatch of marks", "Question is not evaluated" };
		if (checkView(view)) {
			CharSequence[] remarksList = { "Total Mismatch" };
			return remarksList;
		} else if (!TextUtils.isEmpty(tv.getText().toString())) {
			CharSequence[] remarksList = {
					"Marks posted in Answer Book but not in Tablet",
					"Marks posted in Tablet but not in Answer Book",
					"Mismatch of marks",
					"Not evaluated in Answer Book but posted in Tablet",
					"Question is not evaluated"
					};
			return remarksList;
		}
		else {
			CharSequence[] remarksList = {
					"Marks posted in Answer Book but not in Tablet",
					"Marks posted in Tablet but not in Answer Book",
					"Mismatch of marks",
					"Not evaluated in Answer Book but posted in Tablet",
					"Question is not evaluated"
					};
			return remarksList;
		}

	}

	// check view whether totals clicked
	private boolean checkView(View view) {
		switch (view.getId()) {
		case R.id.q1_total:
			return true;
		case R.id.q2_3_total:
			return true;
		case R.id.q4_5_total:
			return true;
		case R.id.q6_7_total:
			return true;
		case R.id.q8_9_total:
			return true;
		case R.id.q10_11_total:
			return true;

		default:
			return false;
		}
	}

	// get remark
	private int getRemark(String remark) {
		if (remark.equals("Marks posted in Answer Script but not in Tablet")) {
			return 0;
		} else if (remark.equals("Question is not evaluated")) {
			return 1;
		} else if (remark
				.equals("Not evaluated in Answer Script but posted in Tablet")) {
			return 0;
		} else if (remark.equals("Mismatch of marks")) {
			return 1;
		} else if (remark.equals("Total Mismatch")) {
			return 0;
		} else {
			return -1;
		}
	}

	// insert remark into database or get remark if already set
	private int setRemarkInContentValue2(String remarkOrMark, int setRemark,
			String C_REMARK) {
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		ContentValues _contentValues = new ContentValues();
		Cursor _cursor;
		String _remark = null;
		if (setRemark == 1) {
			_contentValues.put(C_REMARK, remarkOrMark);
		} else {
			_cursor = _db_for_scrutiny.getRow(ansBookBarcode,
					new String[] { C_REMARK });
			if (_cursor.getCount() > 0)
				_remark = _cursor.getString(_cursor.getColumnIndex(C_REMARK));
			_cursor.close();

			if (!TextUtils.isEmpty(_remark)) {
				return getRemark(_remark);
			} else {
				return -1;
			}
		}
		insertToDB(_contentValues);
		insertToEvaluationDB(_contentValues);
		return 4;
	}

	// show progress
	public void showProgress() {
		progressDialog = ProgressDialog.show(this, "", "Loading ...");
		progressDialog.setCancelable(false);
	}

	// hide progress
	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	// insert values to DB
	private void insertToDB(ContentValues contentValues) {
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		Cursor _cursor = _db_for_scrutiny.getRow(ansBookBarcode, null);
		int count = _cursor.getCount();
		if (count > 0) {
			_db_for_scrutiny.updateRow(SSConstants.TABLE_SCRUTINY_SAVE,
					contentValues, SSConstants.BUNDLE_SERIAL_NO + " = '"
							+ bundle_serial_no + "' AND "
							+ SSConstants.BUNDLE_NO + " = '" + bundleNo + "'");
		}
		_cursor.close();

	}

	private void insertToEvaluationDB(ContentValues contentValues) {
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		Cursor _cursor = _db_for_scrutiny.getRow(ansBookBarcode, null);
		int count = _cursor.getCount();
		if (count > 0) {
			_db_for_scrutiny.updateRow(SSConstants.TABLE_EVALUATION_SAVE,
					contentValues, SSConstants.BUNDLE_SERIAL_NO + " = '"
							+ bundle_serial_no + "' AND "
							+ SSConstants.BUNDLE_NO + " = '" + bundleNo + "'");
		}
		_cursor.close();

	}

	@Override
	protected void onResume() {
		super.onResume();
		batteryLevel();
		R13BTech=instanceUtitlity
				.isRegulation_R13_Btech(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
		if (wl != null) {
			wl.acquire();
		}
	}

	// checking battery level through broadcast reciever
	private void batteryLevel() {
		batteryLevelReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,
						-1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				int level = -1;
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}

				if (level < SSConstants.TABLET_CHARGE) {
					alertMessageForCharge(getString(R.string.app_name),
							getString(R.string.alert_charge), true);
				}
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	// show alert for charge of a tablet
	private void alertMessageForCharge(String title, String msg,
			final Boolean status) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(title);
		myAlertDialog.setMessage(msg);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						if (status)
							navigateToTabletHomeScreen();
						Dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	// navigate to Tablet Home screen
	private void navigateToTabletHomeScreen() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		// finish();
	}

	protected Boolean setSampleRemark(EditText mEditText) {
		Boolean check = false;
		String text = "" + mEditText.getText().toString().trim();
		String tag = "" + mEditText.getTag().toString().trim();
		Log.v("text " + text, "tag " + tag);
		if (text.equals("") && tag.equals("")) {
		} else if (text.equals("") && !tag.equals("")) {
			if (mEditText.getCurrentTextColor() == getResources().getColor(
					R.color.black)) {
				if(countButton<2){
					mEditText.setError("");
					mEditText.setFocusableInTouchMode(false);
					mEditText.setOnClickListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
					check = true;
					}else{
						RemarksArray.put(mEditText.getId(),
								"Mismatch of marks");
						changeEditTextCellBGToGreen(mEditText);
						mEditText.setError(null);
						mEditText.setFocusableInTouchMode(false);
						mEditText.setOnClickListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
						mEditText.clearFocus();
					}
		}
		} else if (!text.equals("") && tag.equals("")) {
			if (mEditText.getCurrentTextColor() == getResources().getColor(
					R.color.black)) {
				if(countButton<2){
					mEditText.setError("");
					mEditText.setFocusableInTouchMode(false);
					mEditText.setOnClickListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
					check = true;
					}else{
						RemarksArray.put(mEditText.getId(),
								"Mismatch of marks");
						changeEditTextCellBGToGreen(mEditText);
						mEditText.setError(null);
						mEditText.setFocusableInTouchMode(false);
						mEditText.setOnClickListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
						mEditText.clearFocus();
					}
			}
		} else if (!text.equals("") && !tag.equals("")) {
			if (!text.equals(tag)) {
				if (mEditText.getCurrentTextColor() == getResources().getColor(
						R.color.black)) {
					if(countButton<2){
						mEditText.setError("");
						mEditText.setFocusableInTouchMode(false);
						mEditText.setOnClickListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
						check = true;
						}else{
							RemarksArray.put(mEditText.getId(),
									"Mismatch of marks");
							changeEditTextCellBGToGreen(mEditText);
							mEditText.setError(null);
							mEditText.setFocusableInTouchMode(false);
							mEditText.setOnClickListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
							mEditText.clearFocus();
						}
				}
			}
		}
		mEditText.clearFocus();
		return check;

	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregister receiver
		if (batteryLevelReceiver != null) {
			try {
				unregisterReceiver(batteryLevelReceiver);
				batteryLevelReceiver = null;
			} catch (IllegalArgumentException ILAE) {

			}
		}

		if (wl != null) {
			wl.release();
		}

	}// End of onPause

	private void setTextToFocusedView(String text) {
		View focusedView = getCurrentFocus();
		if (focusedView != null && focusedView instanceof EditText) {
			EditText et_focusedView = ((EditText) focusedView);
			String sss = et_focusedView.getText().toString().trim();
			et_focusedView.setText(sss + "" + text);
			sss = et_focusedView.getText().toString().trim();
			Log.v("enter " + text, "new " + sss);
			if (!TextUtils.isEmpty(sss)) {
				/*if (focusedView.getId() == R.id.q1_a
						|| focusedView.getId() == R.id.q1_b
						|| focusedView.getId() == R.id.q1_c
						|| focusedView.getId() == R.id.q1_d
						|| focusedView.getId() == R.id.q1_e
						||focusedView.getId() == R.id.q1_f
						|| focusedView.getId() == R.id.q1_g
						|| focusedView.getId() == R.id.q1_h
						|| focusedView.getId() == R.id.q1_i
						|| focusedView.getId() == R.id.q1_j) {*/
					if (Float.parseFloat(sss) > Float.parseFloat(RowTotalLimit)) {
						alertForInvalidMark(et_focusedView, true, "");
					} else {
						setTotaltoTextView(et_focusedView);
					}
				/*} else {
					setTotaltoTextView(et_focusedView);
				}*/
			}

		}
	}

	private void deleteCharAndSetSelection(EditText edittext) {
		if (!TextUtils.isEmpty(edittext.getText().toString())) {
			edittext.setText(edittext.getText().toString()
					.substring(0, (edittext.getText().toString().length() - 1)));
			// edittext.setSelection(edittext.getText().toString().length());
			// setRemarkInContentValue2(edittext, edittext.getText().toString()
			// .trim(), false, false);
		}
	}

	CharSequence[] EditSelection = { "Edit", "Remark" };

	private void showEditSelectionDialog(final View view) {
		// int selection = setRemarkInContentValue(view, "", 0);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		builder.setTitle("Select");
		builder.setCancelable(false);
		builder.setSingleChoiceItems(EditSelection, -1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							EditText et = (EditText) view;
							et.setFocusableInTouchMode(true);
							// ((EditText) view.set
							et.requestFocus();
							et.setOnClickListener(null);
							et.setError(null);
							if (et.getCurrentTextColor() == getResources()
									.getColor(R.color.white)) {
								et.setBackgroundResource(R.drawable.cell_shape_in_grey);
								et.setTextColor(getResources().getColor(
										R.color.black));
								// et.setTypeface(Typeface.DEFAULT_BOLD);
								// setRemarkInContentValue(view, "", 1);
								RemarksArray.remove(view.getId());
							}
						} else {
							showRemarkSelectionDialog(view);
						}
						dialog.dismiss();

					}
				});
		builder.setPositiveButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	private void updateDBProcess(final int scrutinyStatus) {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				showProgress();
			};

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				removeRemarksDB();
				removeMarksDB();
				storeRemarksDB();
				updateAllMarksDB();
				ContentValues _contentValues = new ContentValues();
				_contentValues
						.put(SSConstants.SCRUTINIZED_ON, getPresentTime());
				_contentValues.put(SSConstants.SCRUTINIZED_BY, userId);
				_contentValues.put(SSConstants.IS_SCRUTINIZED, 1);

				_contentValues.put(SSConstants.SCRUTINIZE_STATUS,
						scrutinyStatus);

				_contentValues
						.put(SSConstants.ANS_BOOK_BARCODE, ansBookBarcode);
				_contentValues.put(SSConstants.SUBJECT_CODE, subjectCode);
				_contentValues.put(SSConstants.BUNDLE_NO, bundleNo);
				_contentValues.put(SSConstants.BUNDLE_SERIAL_NO,
						bundle_serial_no);
				insertToDB(_contentValues);
				insertToEvaluationDB(_contentValues);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				hideProgress();
				switchToShowGrandTotalSummaryTableOrScanActivity();
			};

		}.execute();
	}

	void updateAllMarksDB() {

		// if(TextUtils.isEmpty(row_total1)){
		// row_total1=null;
		// }
		ContentValues contentValuesmark = new ContentValues();
		// mark1
		contentValuesmark.put(SSConstants.MARK1A, getMarksfromEdit(tv_mark1a));
		contentValuesmark.put(SSConstants.MARK1B, getMarksfromEdit(tv_mark1b));
		
		// mark2
		contentValuesmark.put(SSConstants.MARK2A, getMarksfromEdit(tv_mark2a));
		contentValuesmark.put(SSConstants.MARK2B, getMarksfromEdit(tv_mark2b));

		// mark3
		contentValuesmark.put(SSConstants.MARK3A, getMarksfromEdit(tv_mark3a));
		contentValuesmark.put(SSConstants.MARK3B, getMarksfromEdit(tv_mark3b));

		// mark4
		contentValuesmark.put(SSConstants.MARK4A, getMarksfromEdit(tv_mark4a));
		contentValuesmark.put(SSConstants.MARK4B, getMarksfromEdit(tv_mark4b));

		// mark5
		contentValuesmark.put(SSConstants.MARK5A, getMarksfromEdit(tv_mark5a));
		contentValuesmark.put(SSConstants.MARK5B, getMarksfromEdit(tv_mark5b));

		// mark6
		contentValuesmark.put(SSConstants.MARK6A, getMarksfromEdit(tv_mark6a));
		contentValuesmark.put(SSConstants.MARK6B, getMarksfromEdit(tv_mark6b));

		// mark7
		contentValuesmark.put(SSConstants.MARK7A, getMarksfromEdit(tv_mark7a));
		contentValuesmark.put(SSConstants.MARK7B, getMarksfromEdit(tv_mark7b));

		// mark8
		contentValuesmark.put(SSConstants.MARK8A, getMarksfromEdit(tv_mark8a));
		contentValuesmark.put(SSConstants.MARK8B, getMarksfromEdit(tv_mark8b));

		// mark9
		contentValuesmark.put(SSConstants.MARK9A, getMarksfromEdit(tv_mark9a));
		contentValuesmark.put(SSConstants.MARK9B, getMarksfromEdit(tv_mark9b));

		// mark10
		contentValuesmark
				.put(SSConstants.MARK10A, getMarksfromEdit(tv_mark10a));
		contentValuesmark
				.put(SSConstants.MARK10B, getMarksfromEdit(tv_mark10b));

		contentValuesmark.put(SSConstants.R1_2TOTAL, row_total_12);
		contentValuesmark.put(SSConstants.R3_4TOTAL, row_total_34);
		contentValuesmark.put(SSConstants.R5_6TOTAL, row_total_56);
		contentValuesmark.put(SSConstants.R7_8TOTAL, row_total_78);
		contentValuesmark.put(SSConstants.R9_10TOTAL, row_total_910);
		contentValuesmark.put(SSConstants.GRAND_TOTAL_MARK, grand_totally);

		insertToEvaluationDB(contentValuesmark);

	}

	private String getMarksfromEdit(EditText ptext) {
		if (!TextUtils.isEmpty(ptext.getText().toString().trim())) {
			return ptext.getText().toString().trim();
		}
		return null;
	}

	private void removeRemarksDB() {
		String remarkOrMark = "";
		ContentValues setInContentValue = new ContentValues();
		setInContentValue.put(SSConstants.M1A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M1B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M2A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M2B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M3A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M3B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M4A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M4B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M5A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M5B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M6A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M6B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M7A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M7B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M8A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M8B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M9A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M9B_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M10A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M10B_REMARK, remarkOrMark);

		/*setInContentValue.put(SSConstants.R1_2_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.R3_4_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.R5_6_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.R7_8_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.R9_10_REMARK, remarkOrMark);*/
		insertToDB(setInContentValue);
		insertToEvaluationDB(setInContentValue);
	}

	private void removeMarksDB() {
		ContentValues setInContentValue = new ContentValues();

		setInContentValue.putNull(SSConstants.MARK1A);
		setInContentValue.putNull(SSConstants.MARK1B);  
		
		// mark2
		setInContentValue.putNull(SSConstants.MARK2A);
		setInContentValue.putNull(SSConstants.MARK2B);

		// mark3
		setInContentValue.putNull(SSConstants.MARK3A);
		setInContentValue.putNull(SSConstants.MARK3B);

		// mark4
		setInContentValue.putNull(SSConstants.MARK4A);
		setInContentValue.putNull(SSConstants.MARK4B);

		// mark5
		setInContentValue.putNull(SSConstants.MARK5A);
		setInContentValue.putNull(SSConstants.MARK5B);

		// mark6
		setInContentValue.putNull(SSConstants.MARK6A);
		setInContentValue.putNull(SSConstants.MARK6B);

		// mark7
		setInContentValue.putNull(SSConstants.MARK7A);
		setInContentValue.putNull(SSConstants.MARK7B);

		// mark8
		setInContentValue.putNull(SSConstants.MARK8A);
		setInContentValue.putNull(SSConstants.MARK8B);

		// mark9
		setInContentValue.putNull(SSConstants.MARK9A);
		setInContentValue.putNull(SSConstants.MARK9B);

		// mark10
		setInContentValue.putNull(SSConstants.MARK10A);
		setInContentValue.putNull(SSConstants.MARK10B);

		setInContentValue.putNull(SSConstants.R1_2TOTAL);
		setInContentValue.putNull(SSConstants.R3_4TOTAL);
		setInContentValue.putNull(SSConstants.R5_6TOTAL);
		setInContentValue.putNull(SSConstants.R7_8TOTAL);
		setInContentValue.putNull(SSConstants.R9_10TOTAL);
		setInContentValue.putNull(SSConstants.GRAND_TOTAL_MARK);

		insertToEvaluationDB(setInContentValue);
	}

	private String row_1_2_Total_() {
		String mark = null;
		

		String mark1a = tv_mark1a.getText().toString().trim();
		if (mark1a.equals("null")) {
			mark1a = null;
		}
		String mark1b = tv_mark1b.getText().toString().trim();
		if (mark1b.equals("null")) {
			mark1b = null;
		}
		
		String mark2a = tv_mark2a.getText().toString().trim();
		if (mark2a.equals("null")) {
			mark2a = null;
		}
		String mark2b = tv_mark2b.getText().toString().trim();
		if (mark2b.equals("null")) {
			mark2b = null;
		}
		if (!TextUtils.isEmpty(mark1a) || !TextUtils.isEmpty(mark1b)
				|| !TextUtils.isEmpty(mark2a)
				|| !TextUtils.isEmpty(mark2b) ) {
			
			float _mark1a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark1a))) ? "0"
							: mark)));
			float _mark1b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark1b))) ? "0"
							: mark)));
			float _mark2a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark2a))) ? "0"
							: mark)));
			float _mark2b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark2b))) ? "0"
							: mark)));
			

			
			float _marks1 = _mark1a + _mark1b ;
			float _marks2 = _mark2a + _mark2b ;

			mark = String.valueOf(_marks1 > _marks2 ? _marks1 : _marks2);
			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(RowTotalLimit)) {
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true,
								"Total Exceeds "+RowTotalLimit);
						mark = row_total_12;
					}
				} else {
					row_total_12 = mark;
				}
			} else {
				row_total_12 = null;
			}

			return mark;
		}

		row_total_12 = mark;
		return mark;
	}

	private String row_3_4_Total_() {
		String mark = null;
		

		String mark3a = tv_mark3a.getText().toString().trim();
		if (mark3a.equals("null")) {
			mark3a = null;
		}
		String mark3b = tv_mark3b.getText().toString().trim();
		if (mark3b.equals("null")) {
			mark3b = null;
		}
		
		String mark4a = tv_mark4a.getText().toString().trim();
		if (mark4a.equals("null")) {
			mark4a = null;
		}
		String mark4b = tv_mark4b.getText().toString().trim();
		if (mark4b.equals("null")) {
			mark4b = null;
		}
		if (!TextUtils.isEmpty(mark3a) || !TextUtils.isEmpty(mark3b)
				|| !TextUtils.isEmpty(mark4a)
				|| !TextUtils.isEmpty(mark4b) ) {
			
			float _mark3a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark3a))) ? "0"
							: mark)));
			float _mark3b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark3b))) ? "0"
							: mark)));
			float _mark4a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark4a))) ? "0"
							: mark)));
			float _mark4b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark4b))) ? "0"
							: mark)));
			

			
			float _marks3 = _mark3a + _mark3b ;
			float _marks4 = _mark4a + _mark4b ;

			mark = String.valueOf(_marks3 > _marks4 ? _marks3 : _marks4);
			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(RowTotalLimit)) {
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true,
								"Total Exceeds "+RowTotalLimit);
						mark = row_total_34;
					}
				} else {
					row_total_34 = mark;
				}
			} else {
				row_total_34 = null;
			}

			return mark;
		}

		row_total_34 = mark;
		return mark;
	}

	private String row_5_6_Total_() {
		String mark = null;
		

		String mark5a = tv_mark5a.getText().toString().trim();
		if (mark5a.equals("null")) {
			mark5a = null;
		}
		String mark5b = tv_mark5b.getText().toString().trim();
		if (mark5b.equals("null")) {
			mark5b = null;
		}
		
		String mark6a = tv_mark6a.getText().toString().trim();
		if (mark6a.equals("null")) {
			mark6a = null;
		}
		String mark6b = tv_mark6b.getText().toString().trim();
		if (mark6b.equals("null")) {
			mark6b = null;
		}
		if (!TextUtils.isEmpty(mark5a) || !TextUtils.isEmpty(mark5b)
				|| !TextUtils.isEmpty(mark6a)
				|| !TextUtils.isEmpty(mark6b) ) {
			
			float _mark5a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark5a))) ? "0"
							: mark)));
			float _mark5b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark5b))) ? "0"
							: mark)));
			float _mark6a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark6a))) ? "0"
							: mark)));
			float _mark6b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark6b))) ? "0"
							: mark)));
			

			
			float _marks5 = _mark5a + _mark5b ;
			float _marks6 = _mark6a + _mark6b ;

			mark = String.valueOf(_marks5 > _marks6 ? _marks5 : _marks6);
			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(RowTotalLimit)) {
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true,
								"Total Exceeds "+RowTotalLimit);
						mark = row_total_56;
					}
				} else {
					row_total_56 = mark;
				}
			} else {
				row_total_56 = null;
			}

			return mark;
		}

		row_total_56 = mark;
		return mark;
	}

	private String row_7_8_Total_() {
		String mark = null;
		

		String mark7a = tv_mark7a.getText().toString().trim();
		if (mark7a.equals("null")) {
			mark7a = null;
		}
		String mark7b = tv_mark7b.getText().toString().trim();
		if (mark7b.equals("null")) {
			mark7b = null;
		}
		
		String mark8a = tv_mark8a.getText().toString().trim();
		if (mark8a.equals("null")) {
			mark8a = null;
		}
		String mark8b = tv_mark8b.getText().toString().trim();
		if (mark8b.equals("null")) {
			mark8b = null;
		}
		if (!TextUtils.isEmpty(mark7a) || !TextUtils.isEmpty(mark7b)
				|| !TextUtils.isEmpty(mark8a)
				|| !TextUtils.isEmpty(mark8b) ) {
			
			float _mark7a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark7a))) ? "0"
							: mark)));
			float _mark7b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark7b))) ? "0"
							: mark)));
			float _mark8a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark8a))) ? "0"
							: mark)));
			float _mark8b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark8b))) ? "0"
							: mark)));
			

			
			float _marks7 = _mark7a + _mark7b ;
			float _marks8 = _mark8a + _mark8b ;

			mark = String.valueOf(_marks7 > _marks8 ? _marks7 : _marks8);
			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(RowTotalLimit)) {
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true,
								"Total Exceeds "+RowTotalLimit);
						mark = row_total_78;
					}
				} else {
					row_total_78 = mark;
				}
			} else {
				row_total_78 = null;
			}

			return mark;
		}

		row_total_78 = mark;
		return mark;
	}

	private String row_9_10_Total_() {
		String mark = null;
		

		String mark9a = tv_mark9a.getText().toString().trim();
		if (mark9a.equals("null")) {
			mark9a = null;
		}
		String mark9b = tv_mark9b.getText().toString().trim();
		if (mark9b.equals("null")) {
			mark9b = null;
		}
		
		String mark10a = tv_mark10a.getText().toString().trim();
		if (mark10a.equals("null")) {
			mark10a = null;
		}
		String mark10b = tv_mark10b.getText().toString().trim();
		if (mark10b.equals("null")) {
			mark10b = null;
		}
		if (!TextUtils.isEmpty(mark9a) || !TextUtils.isEmpty(mark9b)
				|| !TextUtils.isEmpty(mark10a)
				|| !TextUtils.isEmpty(mark10b) ) {
			
			float _mark9a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark9a))) ? "0"
							: mark)));
			float _mark9b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark9b))) ? "0"
							: mark)));
			float _mark10a = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark10a))) ? "0"
							: mark)));
			float _mark10b = Float.valueOf(String.valueOf(Float
					.parseFloat(((TextUtils.isEmpty(mark = mark10b))) ? "0"
							: mark)));
			

			
			float _marks9 = _mark9a + _mark9b ;
			float _marks10 = _mark10a + _mark10b ;

			mark = String.valueOf(_marks9 > _marks10 ? _marks9 : _marks10);
			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(RowTotalLimit)) {
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true,
								"Total Exceeds "+RowTotalLimit);
						mark = row_total_910;
					}
				} else {
					row_total_910 = mark;
				}
			} else {
				row_total_910 = null;
			}

			return mark;
		}

		row_total_910 = mark;
		return mark;
	}


	private String calculateGrandTotal() {

		BigDecimal roundOffGrandTotal = null;

		float grandTotal = 0;
		String mark;
		ArrayList<Float> listTotalMarks = new ArrayList<Float>();

		if (!TextUtils.isEmpty(row_total_12)) {
			mark = row_total_12.toString().trim();
			if (!TextUtils.isEmpty(mark)) {
				listTotalMarks.add(Float.valueOf(mark));

			}
		}

		if (!TextUtils.isEmpty(row_total_34)) {
			mark = row_total_34.toString().trim();
			if (!TextUtils.isEmpty(mark)) {
				listTotalMarks.add(Float.valueOf(mark));

			}
		}

		if (!TextUtils.isEmpty(row_total_56)) {
			mark = row_total_56.toString().trim();
			if (!TextUtils.isEmpty(mark)) {
				listTotalMarks.add(Float.valueOf(mark));

			}
		}

		if (!TextUtils.isEmpty(row_total_78)) {
			mark = row_total_78.toString().trim();
			if (!TextUtils.isEmpty(mark)) {
				listTotalMarks.add(Float.valueOf(mark));

			}
		}

		if (!TextUtils.isEmpty(row_total_910)) {
			mark = row_total_910.toString().trim();
			if (!TextUtils.isEmpty(mark)) {
				listTotalMarks.add(Float.valueOf(mark));

			}
		}

		if (!listTotalMarks.isEmpty()) {
			Collections.sort(listTotalMarks, Collections.reverseOrder());
			for (int i = 0; i < listTotalMarks.size(); i++) {
				grandTotal += listTotalMarks.get(i);
			}
			roundOffGrandTotal = new BigDecimal(Double.toString(grandTotal));
			roundOffGrandTotal = roundOffGrandTotal.setScale(0,
					BigDecimal.ROUND_HALF_UP);
			// tv_grand_toal.setText(String.valueOf(roundOffGrandTotal));
		} else {
			// tv_grand_toal.setText("0");
		}

		if (roundOffGrandTotal != null) {
			return "" + roundOffGrandTotal;
		} else {
			return "" + 0;
		}

	}

	private boolean checkGroundTotal(String grand) {
		// TODO Auto-generated method stub
	/*	String ss = tv_grand_toal.getText().toString().trim();
		if (!TextUtils.isEmpty(ss)) {
			float roundOffGrandTotal1 = Float.valueOf(ss);
			if (!TextUtils.isEmpty(grand)) {
				float roundOffGrandTotal = Float.valueOf(grand);
				Log.e("grand", roundOffGrandTotal + " " + roundOffGrandTotal1);
				if (roundOffGrandTotal1 == roundOffGrandTotal) {
					return true;
				} else {
					alertMessageForMarksMisMatch(ss);
					return false;
				}
			} else {
				alertMsgForSecondsRemaining("Please Enter Marks");
				return false;
			}
		} else {
			alertMsgForSecondsRemaining("Please Enter GrandTotal ");*/
			return true;
	//	}
	}

	private void alertForInvalidMark(final View view2,
			boolean fromNumbersLayout, final String mark) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder((this));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_valid_marks)
				+ " " + mark);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						((EditText) view2).setText("");
						setTotaltoTextView((EditText) view2);
					}
				});

		myAlertDialog.show();
	}

	private void alertMessageForMarksMisMatch(final String _total_marks) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog
				.setMessage("The Total Marks you entered :"
						+ _total_marks
						+ "\n is not matching the entered marks." );
		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						Dialog.dismiss();
					}
				});
		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						Dialog.dismiss();
					//	tv_grand_toal.setText("");
					}
				});
		myAlertDialog.show();
	}

	void setTotaltoTextView(EditText focusedView) {
		if (focusedView.getId() == R.id.q1_a
				|| focusedView.getId() == R.id.q1_b
				|| focusedView.getId() == R.id.q2_a
				|| focusedView.getId() == R.id.q2_b) {
			row_total_12 = row_1_2_Total_();
//			if (TextUtils.isEmpty(row_total1))
//				tv_mark1_total.setText("");
//			else
//				tv_mark1_total.setText("" + row_total1);
		} else if ( focusedView.getId() == R.id.q3_a
				|| focusedView.getId() == R.id.q3_b
				|| focusedView.getId() == R.id.q4_a
				|| focusedView.getId() == R.id.q4_b
				) {
			row_total_34 = row_3_4_Total_();
//			if (TextUtils.isEmpty(row_total_23))
//				tv_mark_2_3_total.setText("");
//			else
//				tv_mark_2_3_total.setText("" + row_total_23);
		} else if (
				 focusedView.getId() == R.id.q5_a
				|| focusedView.getId() == R.id.q5_b
				|| focusedView.getId() == R.id.q6_a
				|| focusedView.getId() == R.id.q6_b
				) {
			row_total_56 = row_5_6_Total_();
//			if (TextUtils.isEmpty(row_total_45))
//				tv_mark_4_5_total.setText("");
//			else
//				tv_mark_4_5_total.setText("" + row_total_45);
		} else if (
				 focusedView.getId() == R.id.q7_a
				|| focusedView.getId() == R.id.q7_b
				|| focusedView.getId() == R.id.q8_a
				|| focusedView.getId() == R.id.q8_b
				) {
			row_total_78 = row_7_8_Total_();
//			if (TextUtils.isEmpty(row_total_67))
//				tv_mark_6_7_total.setText("");
//			else
//				tv_mark_6_7_total.setText("" + row_total_67);
		} else if (
				 focusedView.getId() == R.id.q9_a
				|| focusedView.getId() == R.id.q9_b
				|| focusedView.getId() == R.id.q10_a
				|| focusedView.getId() == R.id.q10_b
				) {
			row_total_910 = row_9_10_Total_();
//			if (TextUtils.isEmpty(row_total_89))
//				tv_mark_8_9_total.setText("");
//			else
//				tv_mark_8_9_total.setText("" + row_total_89);
		} 
	}

	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.q1_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q1_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q2_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q2_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q3_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q3_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q4_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q4_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q5_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q5_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q6_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q6_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q7_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q7_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q8_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q8_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q9_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q9_b:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q10_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q10_b:
			showRemarkSelectionDialog(view);
			break;
		
		default:
			break;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		try{
			if(!clickFind){
		//	setTextToFocusedView(arg0.toString());
			View focusedView = getCurrentFocus();
			if (focusedView != null && focusedView instanceof EditText) {
				EditText et_focusedView = ((EditText) focusedView);
				et_focusedView.removeTextChangedListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
				et_focusedView.setText("");
				et_focusedView.addTextChangedListener(Scrutiny_MarkDialog_R13_BTech_SpecialCase.this);
			}
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			clickFind=false;    
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

}
