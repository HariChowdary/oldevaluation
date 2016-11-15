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

public class Scrutiny_MarkDialog extends Activity implements OnClickListener,
		OnLongClickListener , TextWatcher{

	String userId;
	String subjectCode, SeatNo;
	String ansBookBarcode;
	String bundleNo;
	String bundle_serial_no;  
	HashMap<Integer, String> RemarksArray;
	protected boolean isBool = false, btimer_status;
	protected int timelimit = 0;
	int countButton=0;
	String maxMark = "15";  
	String maxTotalMark = "75"; 
	String maxTotalMark_4 = "45"; 
	String row_total1 = null;
	String row_total2 = null;
	String row_total3 = null;  
	String row_total4 = null;  
	String row_total5 = null;
	String row_total6 = null;
	String row_total7 = null;
	String row_total8 = null;
	String grand_totally = null;
	
	Boolean clickFind = false;

	EditText tv_mark1a, tv_mark1b, tv_mark1c, tv_mark1d, tv_mark1e;

	EditText tv_mark2a, tv_mark2b, tv_mark2c, tv_mark2d, tv_mark2e;

	EditText tv_mark3a, tv_mark3b, tv_mark3c, tv_mark3d, tv_mark3e;

	EditText tv_mark4a, tv_mark4b, tv_mark4c, tv_mark4d, tv_mark4e;

	EditText tv_mark5a, tv_mark5b, tv_mark5c, tv_mark5d, tv_mark5e;

	EditText tv_mark6a, tv_mark6b, tv_mark6c, tv_mark6d, tv_mark6e;

	EditText tv_mark7a, tv_mark7b, tv_mark7c, tv_mark7d, tv_mark7e;

	EditText tv_mark8a, tv_mark8b, tv_mark8c, tv_mark8d, tv_mark8e;

	// TextView tv_mark1_total, tv_mark2_total, tv_mark3_total, tv_mark4_total,
	// tv_mark5_total, tv_mark6_total, tv_mark7_total, tv_mark8_total;
	//
	// EditText tv_grand_toal;

	private ProgressDialog progressDialog;

	int timeInterval = 0;
	Date date_temp = null;
	boolean navigationFromGrandTotalSummary;

	private PowerManager.WakeLock wl;

	BroadcastReceiver batteryLevelReceiver;
	// private boolean isRegulation_R13_MTech;
	Utility instanceUtitlity;
	Boolean R09BTech = false;
	boolean is_subject_code_special_case = false; 

	CharSequence[] EditSelection = { "Edit", "Remark" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scrutiny_r09_nototals);
		instanceUtitlity = new Utility();
		// if (!Utility.isNetworkAvailable(this)) {
		// alertMessageForChargeAutoUpdateApk(getString(R.string.alert_network_avail));
		// return;
		// }

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");
		RemarksArray = new HashMap<Integer, String>();
		// hide keyboard
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		R09BTech = instanceUtitlity
				.isRegulation_R09_BTech_Course(Scrutiny_MarkDialog.this);

		// isRegulation_R13_MTech =  
		// instanceUtitlity.isRegulation_R13_Mtech(this);
		// get data from previous activity/screen
		Intent intent_extras = getIntent();
		navigationFromGrandTotalSummary = intent_extras
				.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO);
		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		
		subjectCode = intent_extras.getStringExtra(SSConstants.SUBJECT_CODE);  
		
		if (Utility.is_subject_code_special_case(subjectCode)) {//Swapna
			is_subject_code_special_case = true;    
		} else {    
			is_subject_code_special_case = false;  
		}  
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);
		
		
		if (R09BTech && !is_subject_code_special_case) {    
			maxMark = "15";
			maxTotalMark = "75";
			Boolean NRBTech = instanceUtitlity
					.isRegulation_NR_BTech_Course(Scrutiny_MarkDialog.this);
			if(NRBTech){
				maxMark = "20";
				maxTotalMark = "100";
			}
			if(R09BTech && subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_Z1221_ENGINEERING_DRAWING)
					|| subjectCode.equalsIgnoreCase(SSConstants.SUBJ_Z0223_ENGINEERING_DRAWING)
					|| subjectCode.equalsIgnoreCase(SSConstants.SUBJ_Z0423_ENGINEERING_DRAWING)
					|| subjectCode.equalsIgnoreCase(SSConstants.SUBJ_Z0522_ENGINEERING_DRAWING)
					|| subjectCode.equalsIgnoreCase(SSConstants.SUBJ_X0221_ENGINEERING_DRAWING)
					){
				
				maxMark = "16";
				maxTotalMark = "80";
			}
			
		} else if (R09BTech && is_subject_code_special_case) {
			if (subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_54017_MACHINE_DRAWING_3) ||
					subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_54065_MACHINE_DRAWING_AND_COMPUTER_AIDED_GRAPHICS_4) 
					) {
				maxMark = "15";
				maxTotalMark = "75";
				maxTotalMark_4 = "45";

			} else if (subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_V0323_MACHINE_DRAWING_7)) {
				maxMark = "16";
				maxTotalMark = "80";
				maxTotalMark_4 = "48";
			} 
		} else {  
			maxMark = "12";
			maxTotalMark = "60";
		}
		
		CountDownTimer myCountdownTimer = new CountDownTimer(SSConstants.TimeLimit, 1000) {

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
		// ((TextView) findViewById(R.id.tv_user_id)).setText(userId);
		// ((TextView) findViewById(R.id.tv_h_user_id))
		// .setText("Scrutinizer Id : ");

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

		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor cursor = _database.executeSQLQuery(
				"select time_interval as Value from table_date_configuration",
				null);

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
		hideProgress();
		Button btnSubmit1 = (Button) findViewById(R.id.btn_submit1);
		btnSubmit1.setVisibility(View.GONE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		LinearLayout ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
		ll_submit.addView(addNumbersView());
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
		tv_mark1c = ((EditText) findViewById(R.id.q1_c));
		tv_mark1d = ((EditText) findViewById(R.id.q1_d));
		tv_mark1e = ((EditText) findViewById(R.id.q1_e));

		tv_mark2a = ((EditText) findViewById(R.id.q2_a));
		tv_mark2b = ((EditText) findViewById(R.id.q2_b));
		tv_mark2c = ((EditText) findViewById(R.id.q2_c));
		tv_mark2d = ((EditText) findViewById(R.id.q2_d));
		tv_mark2e = ((EditText) findViewById(R.id.q2_e));

		tv_mark3a = ((EditText) findViewById(R.id.q3_a));
		tv_mark3b = ((EditText) findViewById(R.id.q3_b));
		tv_mark3c = ((EditText) findViewById(R.id.q3_c));
		tv_mark3d = ((EditText) findViewById(R.id.q3_d));
		tv_mark3e = ((EditText) findViewById(R.id.q3_e));

		tv_mark4a = ((EditText) findViewById(R.id.q4_a));
		tv_mark4b = ((EditText) findViewById(R.id.q4_b));
		tv_mark4c = ((EditText) findViewById(R.id.q4_c));
		tv_mark4d = ((EditText) findViewById(R.id.q4_d));
		tv_mark4e = ((EditText) findViewById(R.id.q4_e));

		tv_mark5a = ((EditText) findViewById(R.id.q5_a));
		tv_mark5b = ((EditText) findViewById(R.id.q5_b));
		tv_mark5c = ((EditText) findViewById(R.id.q5_c));
		tv_mark5d = ((EditText) findViewById(R.id.q5_d));
		tv_mark5e = ((EditText) findViewById(R.id.q5_e));

		tv_mark6a = ((EditText) findViewById(R.id.q6_a));
		tv_mark6b = ((EditText) findViewById(R.id.q6_b));
		tv_mark6c = ((EditText) findViewById(R.id.q6_c));
		tv_mark6d = ((EditText) findViewById(R.id.q6_d));
		tv_mark6e = ((EditText) findViewById(R.id.q6_e));

		tv_mark7a = ((EditText) findViewById(R.id.q7_a));
		tv_mark7b = ((EditText) findViewById(R.id.q7_b));
		tv_mark7c = ((EditText) findViewById(R.id.q7_c));
		tv_mark7d = ((EditText) findViewById(R.id.q7_d));
		tv_mark7e = ((EditText) findViewById(R.id.q7_e));

		tv_mark8a = ((EditText) findViewById(R.id.q8_a));
		tv_mark8b = ((EditText) findViewById(R.id.q8_b));
		tv_mark8c = ((EditText) findViewById(R.id.q8_c));
		tv_mark8d = ((EditText) findViewById(R.id.q8_d));
		tv_mark8e = ((EditText) findViewById(R.id.q8_e));
		
		tv_mark1a.addTextChangedListener(this);  
		tv_mark1b.addTextChangedListener(this);
		tv_mark1c.addTextChangedListener(this);
		tv_mark1d.addTextChangedListener(this);
		/*
		 * tv_mark1f.addTextChangedListener(this);
		 * tv_mark1g.addTextChangedListener(this);
		 * tv_mark1h.addTextChangedListener(this);
		 * tv_mark1i.addTextChangedListener(this);
		 * tv_mark1j.addTextChangedListener(this);
		 */

		tv_mark2a.addTextChangedListener(this);
		tv_mark2b.addTextChangedListener(this);
		tv_mark2c.addTextChangedListener(this);
		tv_mark2d.addTextChangedListener(this);

		tv_mark3a.addTextChangedListener(this);
		tv_mark3b.addTextChangedListener(this);
		tv_mark3c.addTextChangedListener(this);
		tv_mark3d.addTextChangedListener(this);

		tv_mark6a.addTextChangedListener(this);
		tv_mark6b.addTextChangedListener(this);
		tv_mark6c.addTextChangedListener(this);
		tv_mark6d.addTextChangedListener(this);

		tv_mark4a.addTextChangedListener(this);
		tv_mark4b.addTextChangedListener(this);
		tv_mark4c.addTextChangedListener(this);
		tv_mark4d.addTextChangedListener(this);

		tv_mark5a.addTextChangedListener(this);
		tv_mark5b.addTextChangedListener(this);
		tv_mark5c.addTextChangedListener(this);
		tv_mark5d.addTextChangedListener(this);

		tv_mark7a.addTextChangedListener(this);
		tv_mark7b.addTextChangedListener(this);
		tv_mark7c.addTextChangedListener(this);
		tv_mark7d.addTextChangedListener(this);

		tv_mark8a.addTextChangedListener(this);
		tv_mark8b.addTextChangedListener(this);
		tv_mark8c.addTextChangedListener(this);
		tv_mark8d.addTextChangedListener(this);

			tv_mark1e.addTextChangedListener(this);
			tv_mark2e.addTextChangedListener(this);
			tv_mark3e.addTextChangedListener(this);
			tv_mark4e.addTextChangedListener(this);
			tv_mark5e.addTextChangedListener(this);
			tv_mark6e.addTextChangedListener(this);
			tv_mark7e.addTextChangedListener(this);
			tv_mark8e.addTextChangedListener(this);
		
		tv_mark1a.setOnLongClickListener(this);
		tv_mark1b.setOnLongClickListener(this);
		tv_mark1c.setOnLongClickListener(this);
		tv_mark1d.setOnLongClickListener(this);

		tv_mark2a.setOnLongClickListener(this);
		tv_mark2b.setOnLongClickListener(this);
		tv_mark2c.setOnLongClickListener(this);
		tv_mark2d.setOnLongClickListener(this);

		tv_mark3a.setOnLongClickListener(this);
		tv_mark3b.setOnLongClickListener(this);
		tv_mark3c.setOnLongClickListener(this);
		tv_mark3d.setOnLongClickListener(this);
		
		tv_mark1a.setFocusable(true);
		tv_mark1b.setFocusable(true);
		tv_mark1c.setFocusable(true);  
		tv_mark1d.setFocusable(true);

		tv_mark2a.setFocusable(true);
		tv_mark2b.setFocusable(true);
		tv_mark2c.setFocusable(true);
		tv_mark2d.setFocusable(true);

		tv_mark3a.setFocusable(true);
		tv_mark3b.setFocusable(true);
		tv_mark3c.setFocusable(true);
		tv_mark3d.setFocusable(true);

		tv_mark1a.setFocusableInTouchMode(true);
		tv_mark1b.setFocusableInTouchMode(true);
		tv_mark1c.setFocusableInTouchMode(true);
		tv_mark1d.setFocusableInTouchMode(true);

		tv_mark2a.setFocusableInTouchMode(true);
		tv_mark2b.setFocusableInTouchMode(true);
		tv_mark2c.setFocusableInTouchMode(true);
		tv_mark2d.setFocusableInTouchMode(true);

		tv_mark3a.setFocusableInTouchMode(true);
		tv_mark3b.setFocusableInTouchMode(true);
		tv_mark3c.setFocusableInTouchMode(true);
		tv_mark3d.setFocusableInTouchMode(true);
		/*
		 * tv_mark1_total = ((TextView) findViewById(R.id.q1_total));
		 * tv_mark2_total = ((TextView) findViewById(R.id.q2_total));
		 * tv_mark3_total = ((TextView) findViewById(R.id.q3_total));
		 * tv_mark4_total = ((TextView) findViewById(R.id.q4_total));
		 * tv_mark5_total = ((TextView) findViewById(R.id.q5_total));
		 * tv_mark6_total = ((TextView) findViewById(R.id.q6_total));
		 * tv_mark7_total = ((TextView) findViewById(R.id.q7_total));
		 * tv_mark8_total = ((TextView) findViewById(R.id.q8_total));
		 * tv_grand_toal = ((EditText) findViewById(R.id.grand_total));
		 * tv_grand_toal.addTextChangedListener(new TextWatcher() {
		 * 
		 * @Override public void onTextChanged(CharSequence s, int start, int
		 * before, int count) { // TODO Auto-generated method stub }
		 * 
		 * @Override public void beforeTextChanged(CharSequence s, int start,
		 * int count, int after) { // TODO Auto-generated method stub }
		 * 
		 * @Override public void afterTextChanged(Editable s) { // TODO
		 * Auto-generated method stub if(!TextUtils.isEmpty(s.toString())){ try{
		 * 
		 * if (Float.parseFloat(s.toString()) > Float.parseFloat(maxTotalMark))
		 * { alertForInvalidMark(tv_grand_toal, true,
		 * " Total Exceeds "+maxTotalMark); }
		 * 
		 * }catch(Exception e){
		 *   
		 * } } } });
		 */
		if(!is_subject_code_special_case){

		tv_mark4a.setOnLongClickListener(this);
		tv_mark4b.setOnLongClickListener(this);
		tv_mark4c.setOnLongClickListener(this);
		tv_mark4d.setOnLongClickListener(this);

		tv_mark5a.setOnLongClickListener(this);
		tv_mark5b.setOnLongClickListener(this);
		tv_mark5c.setOnLongClickListener(this);
		tv_mark5d.setOnLongClickListener(this);

		tv_mark6a.setOnLongClickListener(this);
		tv_mark6b.setOnLongClickListener(this);
		tv_mark6c.setOnLongClickListener(this);
		tv_mark6d.setOnLongClickListener(this);

		tv_mark7a.setOnLongClickListener(this);
		tv_mark7b.setOnLongClickListener(this);
		tv_mark7c.setOnLongClickListener(this);
		tv_mark7d.setOnLongClickListener(this);

		tv_mark8a.setOnLongClickListener(this);
		tv_mark8b.setOnLongClickListener(this);
		tv_mark8c.setOnLongClickListener(this);
		tv_mark8d.setOnLongClickListener(this);

		tv_mark1e.setOnLongClickListener(this);
		tv_mark2e.setOnLongClickListener(this);
		tv_mark3e.setOnLongClickListener(this);
		tv_mark4e.setOnLongClickListener(this);
		tv_mark5e.setOnLongClickListener(this);
		tv_mark6e.setOnLongClickListener(this);
		tv_mark7e.setOnLongClickListener(this);
		tv_mark8e.setOnLongClickListener(this);
		
		tv_mark1e.setFocusableInTouchMode(true);
		tv_mark2e.setFocusableInTouchMode(true);
		tv_mark3e.setFocusableInTouchMode(true);
		tv_mark4e.setFocusableInTouchMode(true);
		tv_mark5e.setFocusableInTouchMode(true);
		tv_mark6e.setFocusableInTouchMode(true);
		tv_mark7e.setFocusableInTouchMode(true);
		tv_mark8e.setFocusableInTouchMode(true);
		tv_mark1e.setFocusable(true);
		tv_mark2e.setFocusable(true);
		tv_mark3e.setFocusable(true);
		tv_mark4e.setFocusable(true);
		tv_mark5e.setFocusable(true);
		tv_mark6e.setFocusable(true);
		tv_mark7e.setFocusable(true);
		tv_mark8e.setFocusable(true);
		
		tv_mark4a.setFocusable(true);
		tv_mark4b.setFocusable(true);
		tv_mark4c.setFocusable(true);
		tv_mark4d.setFocusable(true);

		tv_mark5a.setFocusable(true);
		tv_mark5b.setFocusable(true);
		tv_mark5c.setFocusable(true);
		tv_mark5d.setFocusable(true);

		tv_mark6a.setFocusable(true);
		tv_mark6b.setFocusable(true);
		tv_mark6c.setFocusable(true);
		tv_mark6d.setFocusable(true);

		tv_mark7a.setFocusable(true);
		tv_mark7b.setFocusable(true);
		tv_mark7c.setFocusable(true);
		tv_mark7d.setFocusable(true);

		tv_mark8a.setFocusable(true);
		tv_mark8b.setFocusable(true);
		tv_mark8c.setFocusable(true);
		tv_mark8d.setFocusable(true);

		tv_mark4a.setFocusableInTouchMode(true);
		tv_mark4b.setFocusableInTouchMode(true);
		tv_mark4c.setFocusableInTouchMode(true);
		tv_mark4d.setFocusableInTouchMode(true);

		tv_mark5a.setFocusableInTouchMode(true);
		tv_mark5b.setFocusableInTouchMode(true);
		tv_mark5c.setFocusableInTouchMode(true);
		tv_mark5d.setFocusableInTouchMode(true);

		tv_mark6a.setFocusableInTouchMode(true);
		tv_mark6b.setFocusableInTouchMode(true);
		tv_mark6c.setFocusableInTouchMode(true);
		tv_mark6d.setFocusableInTouchMode(true);

		tv_mark7a.setFocusableInTouchMode(true);
		tv_mark7b.setFocusableInTouchMode(true);
		tv_mark7c.setFocusableInTouchMode(true);
		tv_mark7d.setFocusableInTouchMode(true);

		tv_mark8a.setFocusableInTouchMode(true);
		tv_mark8b.setFocusableInTouchMode(true);
		tv_mark8c.setFocusableInTouchMode(true);
		tv_mark8d.setFocusableInTouchMode(true);
		
		}
		
		else if(R09BTech && is_subject_code_special_case)
		{//Swapna
			if(subjectCode.equalsIgnoreCase(SSConstants.SUBJ_54017_MACHINE_DRAWING_3)
					|| subjectCode.equalsIgnoreCase(
							SSConstants.SUBJ_54065_MACHINE_DRAWING_AND_COMPUTER_AIDED_GRAPHICS_4) ||
							subjectCode.equalsIgnoreCase(SSConstants.SUBJ_V0323_MACHINE_DRAWING_7)){
					// PART-A --> BEST OF 2 FROM SUB QUES 1 (1a,1b,1c and 1d) [15X2=30M]; PART-B
					// --> 2 QUES IS COMPULSORY [1X45=45M] GRAND_TOTAL = 75M
				
				tv_mark4a.setOnLongClickListener(this);
				
				 tv_mark4a.setFocusable(true);
				 
				 tv_mark4a.setFocusableInTouchMode(true);
				
				((EditText) findViewById(R.id.q1_e)).setEnabled(false);
				((EditText) findViewById(R.id.q2_e)).setEnabled(false);  
				((EditText) findViewById(R.id.q3_e)).setEnabled(false);
				((EditText) findViewById(R.id.q4_b)).setEnabled(false);
				((EditText) findViewById(R.id.q4_c)).setEnabled(false);
				((EditText) findViewById(R.id.q4_d)).setEnabled(false);
				((EditText) findViewById(R.id.q4_e)).setEnabled(false);

				((EditText) findViewById(R.id.q5_a)).setEnabled(false);
				((EditText) findViewById(R.id.q5_b)).setEnabled(false);
				((EditText) findViewById(R.id.q5_c)).setEnabled(false);
				((EditText) findViewById(R.id.q5_d)).setEnabled(false);
				((EditText) findViewById(R.id.q5_e)).setEnabled(false);

				((EditText) findViewById(R.id.q6_a)).setEnabled(false);
				((EditText) findViewById(R.id.q6_b)).setEnabled(false);
				((EditText) findViewById(R.id.q6_c)).setEnabled(false);
				((EditText) findViewById(R.id.q6_d)).setEnabled(false);
				((EditText) findViewById(R.id.q6_e)).setEnabled(false);

				((EditText) findViewById(R.id.q7_a)).setEnabled(false);
				((EditText) findViewById(R.id.q7_b)).setEnabled(false);
				((EditText) findViewById(R.id.q7_c)).setEnabled(false);
				((EditText) findViewById(R.id.q7_d)).setEnabled(false);
				((EditText) findViewById(R.id.q7_e)).setEnabled(false);

				((EditText) findViewById(R.id.q8_a)).setEnabled(false);
				((EditText) findViewById(R.id.q8_b)).setEnabled(false);
				((EditText) findViewById(R.id.q8_c)).setEnabled(false);
				((EditText) findViewById(R.id.q8_d)).setEnabled(false);
				((EditText) findViewById(R.id.q8_e)).setEnabled(false);    
     
				 
			}
			
		}
		
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

				// subjectCode = cursor.getString(cursor
				// .getColumnIndex(SSConstants.SUBJECT_CODE));
				//
				// setMarkToCellFromDB(subjectCode,
				// ((TextView) findViewById(R.id.tv_sub_code)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)),
						((TextView) findViewById(R.id.tv_ans_book)));

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.USER_ID)),
				// ((TextView) findViewById(R.id.tv_user_id)));

				

				// setMarkToCellFromDB(subjectCode,
				// ((TextView) findViewById(R.id.tv_sub_code)));

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
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1C)), tv_mark1c);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1D)), tv_mark1d);
					setMarkToCellFromDB(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK1E)), tv_mark1e);
					setMarkToCellFromDB(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK2E)), tv_mark2e);
					setMarkToCellFromDB(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK3E)), tv_mark3e);
					setMarkToCellFromDB(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK4E)), tv_mark4e);
					setMarkToCellFromDB(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK5E)), tv_mark5e);
					setMarkToCellFromDB(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK6E)), tv_mark6e);
					setMarkToCellFromDB(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK7E)), tv_mark7e);
					setMarkToCellFromDB(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK8E)), tv_mark8e);

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R1_TOTAL)), tv_mark1_total);

				// Marks2
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2A)), tv_mark2a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2B)), tv_mark2b);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2C)), tv_mark2c);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2D)), tv_mark2d);

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R2_TOTAL)), tv_mark2_total);

				// Marks3
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3A)), tv_mark3a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3B)), tv_mark3b);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3C)), tv_mark3c);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3D)), tv_mark3d);

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R3_TOTAL)), tv_mark3_total);

				// Marks4
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4A)), tv_mark4a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4B)), tv_mark4b);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4C)), tv_mark4c);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4D)), tv_mark4d);

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R4_TOTAL)), tv_mark4_total);

				// Marks5
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5A)), tv_mark5a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5B)), tv_mark5b);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5C)), tv_mark5c);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5D)), tv_mark5d);

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R5_TOTAL)), tv_mark5_total);

				// Marks6
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6A)), tv_mark6a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6B)), tv_mark6b);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6C)), tv_mark6c);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6D)), tv_mark6d);

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R6_TOTAL)), tv_mark6_total);

				// Marks7
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7A)), tv_mark7a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7B)), tv_mark7b);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7C)), tv_mark7c);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7D)), tv_mark7d);

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R7_TOTAL)), tv_mark7_total);

				// Marks8
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8A)), tv_mark8a);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8B)), tv_mark8b);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8C)), tv_mark8c);
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8D)), tv_mark8d);

				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R8_TOTAL)), tv_mark8_total);
				//
				// setMarkToCellFromDB(cursor.getString(cursor
				// .getColumnIndex(SSConstants.GRAND_TOTAL_MARK)),
				// tv_grand_toal);

				/*
				 * tv_mark1a.setOnClickListener(this);
				 * tv_mark1b.setOnClickListener(this);
				 * tv_mark1c.setOnClickListener(this);
				 * tv_mark1d.setOnClickListener(this);
				 */

				
				/*
				 * tv_mark2a.setOnClickListener(this);
				 * tv_mark2b.setOnClickListener(this);
				 */
				/*if (!isRegulation_R13_MTech) {
											 * tv_mark2c.setOnClickListener(this)
											 * ;
											 * tv_mark2d.setOnClickListener(this
											 * ); //
											 * tv_mark2e.setOnClickListener
											 * (this);
											 
				}*/

				// tv_mark1_total.setOnClickListener(this);
				// tv_mark2_total.setOnClickListener(this);

				disableEditTextForSpecialCaseSubjCode();

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
							row_total1 = row1Total_();
							row_total2 = row2Total_();
							row_total3 = row3Total_();  
							row_total4 = row4Total_();
							row_total5 = row5Total_();
							row_total6 = row6Total_(); 
							row_total7 = row7Total_();
							row_total8 = row8Total_();
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
		_intent.putExtra("SeatNo", SSConstants.SeatNo);
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
			_intent.putExtra("SeatNo", SSConstants.SeatNo);
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
			PeditText.setOnClickListener(Scrutiny_MarkDialog.this);
			changeEditTextCellBGToGreen(PeditText, C_REMARK);
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
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M1C_REMARK)), tv_mark1c);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M1D_REMARK)), tv_mark1d);
				showRemarksInETWithGreenColor(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.M1E_REMARK)), tv_mark1e);
				showRemarksInETWithGreenColor(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.M2E_REMARK)), tv_mark2e);
				showRemarksInETWithGreenColor(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.M3E_REMARK)), tv_mark3e);
				showRemarksInETWithGreenColor(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.M4E_REMARK)), tv_mark4e);
				showRemarksInETWithGreenColor(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.M5E_REMARK)), tv_mark5e);
				showRemarksInETWithGreenColor(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.M6E_REMARK)), tv_mark6e);
				showRemarksInETWithGreenColor(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.M7E_REMARK)), tv_mark7e);
				showRemarksInETWithGreenColor(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.M8E_REMARK)), tv_mark8e);

			// mark2
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M2A_REMARK)), tv_mark2a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M2B_REMARK)), tv_mark2b);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M2C_REMARK)), tv_mark2c);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M2D_REMARK)), tv_mark2d);

			// mark3
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M3A_REMARK)), tv_mark3a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M3B_REMARK)), tv_mark3b);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M3C_REMARK)), tv_mark3c);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M3D_REMARK)), tv_mark3d);

			// mark4
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M4A_REMARK)), tv_mark4a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M4B_REMARK)), tv_mark4b);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M4C_REMARK)), tv_mark4c);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M4D_REMARK)), tv_mark4d);

			// mark5
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M5A_REMARK)), tv_mark5a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M5B_REMARK)), tv_mark5b);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M5C_REMARK)), tv_mark5c);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M5D_REMARK)), tv_mark5d);

			// mark6
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M6A_REMARK)), tv_mark6a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M6B_REMARK)), tv_mark6b);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M6C_REMARK)), tv_mark6c);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M6D_REMARK)), tv_mark6d);

			// mark7
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M7A_REMARK)), tv_mark7a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M7B_REMARK)), tv_mark7b);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M7C_REMARK)), tv_mark7c);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M7D_REMARK)), tv_mark7d);

			// mark8
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M8A_REMARK)), tv_mark8a);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M8B_REMARK)), tv_mark8b);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M8C_REMARK)), tv_mark8c);
			showRemarksInETWithGreenColor(_cursor.getString(_cursor
					.getColumnIndex(SSConstants.M8D_REMARK)), tv_mark8d);

			// showRemarksInTVWithGreenColor(_cursor.getString(_cursor
			// .getColumnIndex(SSConstants.R1_REMARK)), tv_mark1_total);
			//
			// showRemarksInTVWithGreenColor(_cursor.getString(_cursor
			// .getColumnIndex(SSConstants.R2_REMARK)), tv_mark2_total);
			//
			// showRemarksInTVWithGreenColor(_cursor.getString(_cursor
			// .getColumnIndex(SSConstants.R3_REMARK)), tv_mark3_total);
			//
			// showRemarksInTVWithGreenColor(_cursor.getString(_cursor
			// .getColumnIndex(SSConstants.R4_REMARK)), tv_mark4_total);
			//
			// showRemarksInTVWithGreenColor(_cursor.getString(_cursor
			// .getColumnIndex(SSConstants.R5_REMARK)), tv_mark5_total);
			//
			// showRemarksInTVWithGreenColor(_cursor.getString(_cursor
			// .getColumnIndex(SSConstants.R6_REMARK)), tv_mark6_total);
			//
			// showRemarksInTVWithGreenColor(_cursor.getString(_cursor
			// .getColumnIndex(SSConstants.R7_REMARK)), tv_mark7_total);
			//
			// showRemarksInTVWithGreenColor(_cursor.getString(_cursor
			// .getColumnIndex(SSConstants.R8_REMARK)), tv_mark8_total);

		}
		_cursor.close();
	}

	private boolean getRemarkComplete() {
		// TODO Auto-generated method stub
		Boolean checkRemark = false;
		if (setSampleRemark(tv_mark1a))
			checkRemark = true;
		if (setSampleRemark(tv_mark1b))
			checkRemark = true;
		if (setSampleRemark(tv_mark1c))
			checkRemark = true;
		if (setSampleRemark(tv_mark1d))
			checkRemark = true;
			if (setSampleRemark(tv_mark1e))
				checkRemark = true;
			if (setSampleRemark(tv_mark2e))
				checkRemark = true;
			if (setSampleRemark(tv_mark3e))
				checkRemark = true;
			if (setSampleRemark(tv_mark4e))
				checkRemark = true;
			if (setSampleRemark(tv_mark5e))
				checkRemark = true;
			if (setSampleRemark(tv_mark6e))
				checkRemark = true;
			if (setSampleRemark(tv_mark7e))
				checkRemark = true;
			if (setSampleRemark(tv_mark8e))
				checkRemark = true;

		if (setSampleRemark(tv_mark2a))
			checkRemark = true;
		if (setSampleRemark(tv_mark2b))
			checkRemark = true;
		if (setSampleRemark(tv_mark2c))
			checkRemark = true;
		if (setSampleRemark(tv_mark2d))
			checkRemark = true;

		if (setSampleRemark(tv_mark3a))
			checkRemark = true;
		if (setSampleRemark(tv_mark3b))
			checkRemark = true;
		if (setSampleRemark(tv_mark3c))
			checkRemark = true;
		if (setSampleRemark(tv_mark3d))
			checkRemark = true;

		if (setSampleRemark(tv_mark4a))
			checkRemark = true;
		if (setSampleRemark(tv_mark4b))
			checkRemark = true;
		if (setSampleRemark(tv_mark4c))
			checkRemark = true;
		if (setSampleRemark(tv_mark4d))
			checkRemark = true;

		if (setSampleRemark(tv_mark5a))
			checkRemark = true;
		if (setSampleRemark(tv_mark5b))
			checkRemark = true;
		if (setSampleRemark(tv_mark5c))
			checkRemark = true;
		if (setSampleRemark(tv_mark5d))
			checkRemark = true;

		if (setSampleRemark(tv_mark6a))
			checkRemark = true;
		if (setSampleRemark(tv_mark6b))
			checkRemark = true;
		if (setSampleRemark(tv_mark6c))
			checkRemark = true;
		if (setSampleRemark(tv_mark6d))
			checkRemark = true;

		if (setSampleRemark(tv_mark7a))
			checkRemark = true;
		if (setSampleRemark(tv_mark7b))
			checkRemark = true;
		if (setSampleRemark(tv_mark7c))
			checkRemark = true;
		if (setSampleRemark(tv_mark7d))
			checkRemark = true;

		if (setSampleRemark(tv_mark8a))
			checkRemark = true;
		if (setSampleRemark(tv_mark8b))
			checkRemark = true;
		if (setSampleRemark(tv_mark8c))
			checkRemark = true;
		if (setSampleRemark(tv_mark8d))
			checkRemark = true;

		return checkRemark;
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
				mEditText.setOnClickListener(Scrutiny_MarkDialog.this);
				check = true;
				}else{
					RemarksArray.put(mEditText.getId(),
							"Mismatch of marks");
					changeEditTextCellBGToGreen(mEditText);
					mEditText.setError(null);
					mEditText.setFocusableInTouchMode(false);
					mEditText.setOnClickListener(Scrutiny_MarkDialog.this);
					mEditText.clearFocus();
				}
			}
		} else if (!text.equals("") && tag.equals("")) {
			if (mEditText.getCurrentTextColor() == getResources().getColor(
					R.color.black)) {
				if(countButton<2){
				mEditText.setError("");
				mEditText.setFocusableInTouchMode(false);
				mEditText.setOnClickListener(Scrutiny_MarkDialog.this);
				check = true;
			}else{
				RemarksArray.put(mEditText.getId(),
						"Mismatch of marks");
				changeEditTextCellBGToGreen(mEditText);
				mEditText.setError(null);
				mEditText.setFocusableInTouchMode(false);
				mEditText.setOnClickListener(Scrutiny_MarkDialog.this);
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
					mEditText.setOnClickListener(Scrutiny_MarkDialog.this);
					check = true;
				}else{
					RemarksArray.put(mEditText.getId(),
							"Mismatch of marks");
					changeEditTextCellBGToGreen(mEditText);
					mEditText.setError(null);
					mEditText.setFocusableInTouchMode(false);
					mEditText.setOnClickListener(Scrutiny_MarkDialog.this);
					mEditText.clearFocus();
				}
				}
			}
		}
		mEditText.clearFocus();
		return check;

	}

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

	private void alertMessageForMarksMisMatch(final String _total_marks) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog
				.setMessage("The Total Marks you entered :"
						+ _total_marks
						+ "\n is not matching the entered marks.\n Please put the Remarks or "
						+ "\n Please Enter Calculated Best of 5 GrandTotal before Submit");
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
						// tv_grand_toal.setText("");
					}
				});
		myAlertDialog.show();
	}

	private void setTextToFocusedView(String text) {
		int max_Mark = 15;
		View focusedView = getCurrentFocus();
		if (focusedView != null && focusedView instanceof EditText) {
			EditText et_focusedView = ((EditText) focusedView);
			String sss = et_focusedView.getText().toString().trim();
			et_focusedView.setText(sss + "" + text);
			sss = et_focusedView.getText().toString().trim();
			
			max_Mark = setMaxValueForSubjCodeSpecialCase(et_focusedView);
			Log.v("enter " + text, "new " + sss);  
			
			
			if (!TextUtils.isEmpty(sss)) {
				
					if(Float.parseFloat(sss) > Float.parseFloat(""+max_Mark)){
					 alertForInvalidMark(et_focusedView, true, "");
					 }else{
						 setTotaltoTextView(et_focusedView);
					 }
				  
			}
		}
	}

	private int setMaxValueForSubjCodeSpecialCase(EditText etQuesNo) {
		int _max_value = 60;


	//Swapna
		
		if ( !is_subject_code_special_case) {

				   if (etQuesNo == tv_mark1a || etQuesNo == tv_mark1b
					|| etQuesNo == tv_mark1c || etQuesNo == tv_mark1d
					|| etQuesNo == tv_mark1e || etQuesNo == tv_mark2a
					|| etQuesNo == tv_mark2b || etQuesNo == tv_mark2c
					|| etQuesNo == tv_mark2d || etQuesNo == tv_mark2e
					|| etQuesNo == tv_mark3a || etQuesNo == tv_mark3b
					|| etQuesNo == tv_mark3c || etQuesNo == tv_mark3d
					|| etQuesNo == tv_mark3e || etQuesNo == tv_mark4a
					|| etQuesNo == tv_mark4b || etQuesNo == tv_mark4c
					|| etQuesNo == tv_mark4d || etQuesNo == tv_mark4e
					|| etQuesNo == tv_mark5a || etQuesNo == tv_mark5b
					|| etQuesNo == tv_mark5c || etQuesNo == tv_mark5d
					|| etQuesNo == tv_mark5e || etQuesNo == tv_mark6a
					|| etQuesNo == tv_mark6b || etQuesNo == tv_mark6c
					|| etQuesNo == tv_mark6d || etQuesNo == tv_mark6e
					|| etQuesNo == tv_mark7a || etQuesNo == tv_mark7b
					|| etQuesNo == tv_mark7c || etQuesNo == tv_mark7d
					|| etQuesNo == tv_mark7e || etQuesNo == tv_mark8a
					|| etQuesNo == tv_mark8b || etQuesNo == tv_mark8c
					|| etQuesNo == tv_mark8d || etQuesNo == tv_mark8e

			) {
				_max_value = Integer.parseInt(maxMark);
			}
			
		  }
		else if (R09BTech && is_subject_code_special_case) {

			
			if (subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_54065_MACHINE_DRAWING_AND_COMPUTER_AIDED_GRAPHICS_4) || 
					subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_54017_MACHINE_DRAWING_3) ||
					subjectCode.equalsIgnoreCase(SSConstants.SUBJ_V0323_MACHINE_DRAWING_7)) {
				if(etQuesNo == tv_mark1a || etQuesNo == tv_mark1b
						|| etQuesNo == tv_mark1c || etQuesNo == tv_mark1d
						 || etQuesNo == tv_mark2a
						|| etQuesNo == tv_mark2b || etQuesNo == tv_mark2c
						|| etQuesNo == tv_mark2d 
						|| etQuesNo == tv_mark3a || etQuesNo == tv_mark3b
						|| etQuesNo == tv_mark3c || etQuesNo == tv_mark3d){
					_max_value = Integer.parseInt(maxMark);
				}
				else if(etQuesNo == tv_mark4a){
					_max_value = Integer.parseInt(maxTotalMark_4);
				}
			 } 
		
		   }
	return _max_value;
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

	// onClick widget
	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {

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
				setTotaltoTextView((EditText) focusedView);
				// setRemarkInContentValue2(focusedView, "", false, false);
				// calculateTotal();
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
					tv_mark8b.getWindowToken(), 0);
			
			if (isBool) {
				//showAlertSubmit();
				countButton=countButton+1;
				
			if (!getRemarkComplete()) {  
				row_total1 = row1Total_();
				row_total2 = row2Total_();
				row_total3 = row3Total_();  
				row_total4 = row4Total_();
				row_total5 = row5Total_();
				row_total6 = row6Total_(); 
				row_total7 = row7Total_();
				row_total8 = row8Total_();
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
					//		updateDBProcess(SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED);
							}
					}
			}
			} else {
				alertMessageForCharge(getString(R.string.app_name),
						getString(R.string.alert_remarks), false);
			}
			} else {
				alertMessageForCharge(
						getString(R.string.app_name),
						"Scrutinization Time for Each Script is Set to a Minimum of 30 "
								+ "Seconds.\nPlease, Continue Scrutinization for the Next "
								+ timelimit + " Seconds...! ", false);
			}
			break;

		case R.id.q1_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q1_b:
			showEditSelectionDialog(view);
			break;
		case R.id.q1_c:
			showEditSelectionDialog(view);
			break;
		case R.id.q1_d:
			showEditSelectionDialog(view);
			break;
		case R.id.q1_e:
			showEditSelectionDialog(view);
			break;

		case R.id.q2_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q2_b:
			showEditSelectionDialog(view);
			break;
		case R.id.q2_c:
			showEditSelectionDialog(view);
			break;
		case R.id.q2_d:
			showEditSelectionDialog(view);
			break;
		case R.id.q2_e:
			showEditSelectionDialog(view);
			break;

		case R.id.q3_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q3_b:
			showEditSelectionDialog(view);
			break;
		case R.id.q3_c:
			showEditSelectionDialog(view);
			break;
		case R.id.q3_d:
			showEditSelectionDialog(view);
			break;
		case R.id.q3_e:
			showEditSelectionDialog(view);
			break;

		case R.id.q4_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q4_b:
			showEditSelectionDialog(view);
			break;
		case R.id.q4_c:
			showEditSelectionDialog(view);
			break;
		case R.id.q4_d:
			showEditSelectionDialog(view);
			break;
		case R.id.q4_e:
			showEditSelectionDialog(view);
			break;

		case R.id.q5_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q5_b:
			showEditSelectionDialog(view);
			break;
		case R.id.q5_c:
			showEditSelectionDialog(view);
			break;
		case R.id.q5_d:
			showEditSelectionDialog(view);
			break;
		case R.id.q5_e:
			showEditSelectionDialog(view);
			break;

		case R.id.q6_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q6_b:
			showEditSelectionDialog(view);
			break;
		case R.id.q6_c:
			showEditSelectionDialog(view);
			break;
		case R.id.q6_d:
			showEditSelectionDialog(view);
			break;
		case R.id.q6_e:
			showEditSelectionDialog(view);
			break;

		case R.id.q7_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q7_b:
			showEditSelectionDialog(view);
			break;
		case R.id.q7_c:
			showEditSelectionDialog(view);
			break;
		case R.id.q7_d:
			showEditSelectionDialog(view);
			break;
		case R.id.q7_e:
			showEditSelectionDialog(view);
			break;

		case R.id.q8_a:
			showEditSelectionDialog(view);
			break;
		case R.id.q8_b:
			showEditSelectionDialog(view);
			break;
		case R.id.q8_c:
			showEditSelectionDialog(view);
			break;
		case R.id.q8_d:
			showEditSelectionDialog(view);
			break;
		case R.id.q8_e:
			showEditSelectionDialog(view);
			break;
		default:
			break;
		}
	}


	private boolean checkGroundTotal(String grand) {
		
		return true;
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
					"Question is not evaluated" };
			return remarksList;
		}

		else {
			CharSequence[] remarksList = {
					"Marks posted in Answer Book but not in Tablet",
					"Marks posted in Tablet but not in Answer Book",
					"Mismatch of marks",
					"Not evaluated in Answer Book but posted in Tablet",
					"Question is not evaluated" };
			return remarksList;
		}

	}

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
	// check view whether totals clicked
	private boolean checkView(View view) {
		switch (view.getId()) {
		case R.id.q1_total:
			return true;
		case R.id.q2_total:
			return true;
		case R.id.q3_total:
			return true;
		case R.id.q4_total:
			return true;
		case R.id.q5_total:
			return true;
		case R.id.q6_total:
			return true;
		case R.id.q7_total:
			return true;
		case R.id.q8_total:
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

	private void removeRemarksDB() {
		String remarkOrMark = "";
		ContentValues setInContentValue = new ContentValues();
		setInContentValue.put(SSConstants.M1A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M1B_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M1C_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M1D_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M1E_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M2A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M2B_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M2C_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M2D_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M2E_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M3A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M3B_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M3C_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M3D_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M3E_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M4A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M4B_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M4C_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M4D_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M4E_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M5A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M5B_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M5C_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M5D_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M5E_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M6A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M6B_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M6C_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M6D_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M6E_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M7A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M7B_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M7C_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M7D_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M7E_REMARK, remarkOrMark);

		setInContentValue.put(SSConstants.M8A_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M8B_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M8C_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M8D_REMARK, remarkOrMark);
		setInContentValue.put(SSConstants.M8E_REMARK, remarkOrMark);
		insertToDB(setInContentValue);
		insertToEvaluationDB(setInContentValue);
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
			case R.id.q1_c:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M1C_REMARK);
				break;
			case R.id.q1_d:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M1D_REMARK);
				break;
			case R.id.q1_e:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M1E_REMARK);
				break;

			case R.id.q2_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M2A_REMARK);
				break;
			case R.id.q2_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M2B_REMARK);
				break;
			case R.id.q2_c:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M2C_REMARK);
				break;
			case R.id.q2_d:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M2D_REMARK);
				break;
			case R.id.q2_e:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M2E_REMARK);
				break;

			case R.id.q3_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M3A_REMARK);
				break;
			case R.id.q3_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M3B_REMARK);
				break;
			case R.id.q3_c:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M3C_REMARK);
				break;
			case R.id.q3_d:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M3D_REMARK);
				break;
			case R.id.q3_e:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M3E_REMARK);
				break;

			case R.id.q4_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M4A_REMARK);
				break;
			case R.id.q4_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M4B_REMARK);
				break;
			case R.id.q4_c:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M4C_REMARK);
				break;
			case R.id.q4_d:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M4D_REMARK);
				break;
			case R.id.q4_e:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M4E_REMARK);
				break;

			case R.id.q5_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M5A_REMARK);
				break;
			case R.id.q5_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M5B_REMARK);
				break;
			case R.id.q5_c:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M5C_REMARK);
				break;
			case R.id.q5_d:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M5D_REMARK);
				break;
			case R.id.q5_e:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M5E_REMARK);
				break;

			case R.id.q6_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M6A_REMARK);
				break;
			case R.id.q6_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M6B_REMARK);
				break;
			case R.id.q6_c:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M6C_REMARK);
				break;
			case R.id.q6_d:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M6D_REMARK);
				break;
			case R.id.q6_e:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M6E_REMARK);
				break;

			case R.id.q7_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M7A_REMARK);
				break;
			case R.id.q7_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M7B_REMARK);
				break;
			case R.id.q7_c:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M7C_REMARK);
				break;
			case R.id.q7_d:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M7D_REMARK);
				break;
			case R.id.q7_e:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M7E_REMARK);
				break;

			case R.id.q8_a:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M8A_REMARK);
				break;
			case R.id.q8_b:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M8B_REMARK);
				break;
			case R.id.q8_c:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M8C_REMARK);
				break;
			case R.id.q8_d:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M8D_REMARK);
				break;
			case R.id.q8_e:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.M8E_REMARK);
				break;

			case R.id.q1_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R1_REMARK);
				break;

			case R.id.q2_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R2_REMARK);
				break;

			case R.id.q3_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R3_REMARK);
				break;

			case R.id.q4_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R4_REMARK);
				break;

			case R.id.q5_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R5_REMARK);
				break;

			case R.id.q6_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R6_REMARK);
				break;

			case R.id.q7_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R7_REMARK);
				break;

			case R.id.q8_total:
				setRemarkInContentValue2(remarkOrMark, setRemark,
						SSConstants.R8_REMARK);
				break;

			default:
				break;

			}
		}
		return 0;
	}

	private void removeMarksDB() {
		ContentValues setInContentValue = new ContentValues();

		// Marks1
		setInContentValue.putNull(SSConstants.MARK1A);
		setInContentValue.putNull(SSConstants.MARK1B);
		setInContentValue.putNull(SSConstants.MARK1C);
		setInContentValue.putNull(SSConstants.MARK1D);
		setInContentValue.putNull(SSConstants.MARK1E);
		setInContentValue.putNull(SSConstants.R1_TOTAL);
		// Marks2
		setInContentValue.putNull(SSConstants.MARK2A);
		setInContentValue.putNull(SSConstants.MARK2B);
		setInContentValue.putNull(SSConstants.MARK2C);
		setInContentValue.putNull(SSConstants.MARK2D);
		setInContentValue.putNull(SSConstants.MARK2E);
		setInContentValue.putNull(SSConstants.R2_TOTAL);

		// Marks3
		setInContentValue.putNull(SSConstants.MARK3A);
		setInContentValue.putNull(SSConstants.MARK3B);
		setInContentValue.putNull(SSConstants.MARK3C);
		setInContentValue.putNull(SSConstants.MARK3D);
		setInContentValue.putNull(SSConstants.MARK3E);
		setInContentValue.putNull(SSConstants.R3_TOTAL);

		// Marks4
		setInContentValue.putNull(SSConstants.MARK4A);
		setInContentValue.putNull(SSConstants.MARK4B);
		setInContentValue.putNull(SSConstants.MARK4C);
		setInContentValue.putNull(SSConstants.MARK4D);
		setInContentValue.putNull(SSConstants.MARK4E);
		setInContentValue.putNull(SSConstants.R4_TOTAL);

		// Marks5
		setInContentValue.putNull(SSConstants.MARK5A);
		setInContentValue.putNull(SSConstants.MARK5B);
		setInContentValue.putNull(SSConstants.MARK5C);
		setInContentValue.putNull(SSConstants.MARK5D);
		setInContentValue.putNull(SSConstants.MARK5E);
		setInContentValue.putNull(SSConstants.R5_TOTAL);

		// Marks6
		setInContentValue.putNull(SSConstants.MARK6A);
		setInContentValue.putNull(SSConstants.MARK6B);
		setInContentValue.putNull(SSConstants.MARK6C);
		setInContentValue.putNull(SSConstants.MARK6D);
		setInContentValue.putNull(SSConstants.MARK6E);
		setInContentValue.putNull(SSConstants.R6_TOTAL);

		// Marks7
		setInContentValue.putNull(SSConstants.MARK7A);
		setInContentValue.putNull(SSConstants.MARK7B);
		setInContentValue.putNull(SSConstants.MARK7C);
		setInContentValue.putNull(SSConstants.MARK7D);
		setInContentValue.putNull(SSConstants.MARK7E);
		setInContentValue.putNull(SSConstants.R7_TOTAL);

		// Marks8
		setInContentValue.putNull(SSConstants.MARK8A);
		setInContentValue.putNull(SSConstants.MARK8B);
		setInContentValue.putNull(SSConstants.MARK8C);
		setInContentValue.putNull(SSConstants.MARK8D);
		setInContentValue.putNull(SSConstants.MARK8E);
		setInContentValue.putNull(SSConstants.R8_TOTAL);

		setInContentValue.putNull(SSConstants.GRAND_TOTAL_MARK);

		insertToEvaluationDB(setInContentValue);
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
		R09BTech = instanceUtitlity
				.isRegulation_R09_BTech_Course(Scrutiny_MarkDialog.this);
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
					alertMessageForCharge();
				}
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	// show alert for charge of a tablet
	private void alertMessageForCharge() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog.setMessage(getString(R.string.alert_charge));

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
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

	// disable EditText for special case subject codes
	private void disableEditTextForSpecialCaseSubjCode() {
		// check for special case subject codes
		if (Utility.is_subject_code_special_case(subjectCode)) {

			// 58002 or 54065 or V0323
			if (subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_58002_DESIGN_AND_DRAWING_OF_IRRIGATION_STRUCTURES_1)
					|| subjectCode
							.equalsIgnoreCase(SSConstants.SUBJ_54017_MACHINE_DRAWING_3)
					|| subjectCode
							.equalsIgnoreCase(SSConstants.SUBJ_54065_MACHINE_DRAWING_AND_COMPUTER_AIDED_GRAPHICS_4)
					|| subjectCode
							.equalsIgnoreCase(SSConstants.SUBJ_V0323_MACHINE_DRAWING_7)) {
				tv_mark3a.setOnClickListener(this);
				tv_mark3b.setOnClickListener(this);
				tv_mark3c.setOnClickListener(this);
				tv_mark3d.setOnClickListener(this);
				

				tv_mark4a.setOnClickListener(this);
				
			}

			// T0121
			
			else if (subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_T0121_BUILDING_PLANNING_AND_DRAWING_5)) {
				tv_mark3a.setOnClickListener(this);
				tv_mark3b.setOnClickListener(this);
				tv_mark3c.setOnClickListener(this);
				tv_mark3d.setOnClickListener(this);
				tv_mark3e.setOnClickListener(this);

				tv_mark4a.setOnClickListener(this);
				tv_mark4b.setOnClickListener(this);
				tv_mark4c.setOnClickListener(this);
				tv_mark4d.setOnClickListener(this);
				tv_mark4e.setOnClickListener(this);

				tv_mark5a.setOnClickListener(this);
				tv_mark5b.setOnClickListener(this);
				tv_mark5c.setOnClickListener(this);
				tv_mark5d.setOnClickListener(this);
				tv_mark5e.setOnClickListener(this);

				tv_mark6a.setOnClickListener(this);
				tv_mark6b.setOnClickListener(this);
				tv_mark6c.setOnClickListener(this);
				tv_mark6d.setOnClickListener(this);
				tv_mark6e.setOnClickListener(this);

				tv_mark7a.setOnClickListener(this);
				tv_mark7b.setOnClickListener(this);
				tv_mark7c.setOnClickListener(this);
				tv_mark7d.setOnClickListener(this);
				tv_mark7e.setOnClickListener(this);

			}

			// X0305
			else if (subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_X0305_MACHINE_DRAWING_6)) {
				tv_mark1d.setClickable(false);
				tv_mark1d.setEnabled(false);

				tv_mark1e.setClickable(false);
				tv_mark1e.setEnabled(false);
			}

		}  
  
		// if not special case
		else {/*

			tv_mark1e.setFocusable(false);
			tv_mark1e.setFocusableInTouchMode(false);

			tv_mark2e.setFocusable(false);
			tv_mark2e.setFocusableInTouchMode(false);

			tv_mark3e.setFocusable(false);
			tv_mark3e.setFocusableInTouchMode(false);
  
			tv_mark4e.setFocusable(false);
			tv_mark4e.setFocusableInTouchMode(false);

			tv_mark5e.setFocusable(false);
			tv_mark5e.setFocusableInTouchMode(false);

			tv_mark6e.setFocusable(false);
			tv_mark6e.setFocusableInTouchMode(false);

			tv_mark7e.setFocusable(false);
			tv_mark7e.setFocusableInTouchMode(false);

			tv_mark8e.setFocusable(false);
			tv_mark8e.setFocusableInTouchMode(false);
		*/}

			
		
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
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1C)), tv_mark1c);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1D)), tv_mark1d);

					setMarkToCellScrutiny(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK1E)), tv_mark1e);
					setMarkToCellScrutiny(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK2E)), tv_mark2e);
					setMarkToCellScrutiny(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK3E)), tv_mark3e);
					setMarkToCellScrutiny(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK4E)), tv_mark4e);
					setMarkToCellScrutiny(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK5E)), tv_mark5e);
					setMarkToCellScrutiny(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK6E)), tv_mark6e);
					setMarkToCellScrutiny(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK7E)), tv_mark7e);
					setMarkToCellScrutiny(cursor.getString(cursor
							.getColumnIndex(SSConstants.MARK8E)), tv_mark8e);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R1_TOTAL)), tv_mark1_total);

				// Marks2
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2A)), tv_mark2a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2B)), tv_mark2b);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2C)), tv_mark2c);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2D)), tv_mark2d);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R2_TOTAL)), tv_mark2_total);

				// Marks3
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3A)), tv_mark3a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3B)), tv_mark3b);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3C)), tv_mark3c);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3D)), tv_mark3d);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R3_TOTAL)), tv_mark3_total);

				// Marks4
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4A)), tv_mark4a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4B)), tv_mark4b);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4C)), tv_mark4c);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4D)), tv_mark4d);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R4_TOTAL)), tv_mark4_total);

				// Marks5
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5A)), tv_mark5a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5B)), tv_mark5b);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5C)), tv_mark5c);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5D)), tv_mark5d);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R5_TOTAL)), tv_mark5_total);

				// Marks6
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6A)), tv_mark6a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6B)), tv_mark6b);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6C)), tv_mark6c);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6D)), tv_mark6d);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R6_TOTAL)), tv_mark6_total);

				// Marks7
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7A)), tv_mark7a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7B)), tv_mark7b);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7C)), tv_mark7c);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7D)), tv_mark7d);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R7_TOTAL)), tv_mark7_total);

				// Marks8
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8A)), tv_mark8a);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8B)), tv_mark8b);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8C)), tv_mark8c);
				setMarkToCellScrutiny(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8D)), tv_mark8d);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R8_TOTAL)), tv_mark8_total);

				// setMarkToCellScrutiny(cursor.getString(cursor
				// .getColumnIndex(SSConstants.GRAND_TOTAL_MARK)),
				// tv_grand_toal);

			}
		} else {
			showAlert(getString(R.string.alert_barcode_not_exists_in_db),
					getString(R.string.alert_dialog_ok), "", false,
					SSConstants.FAILURE);
		}
		cursor.close();
	}

	private String getMarksfromEdit(EditText ptext) {
		if (!TextUtils.isEmpty(ptext.getText().toString().trim())) {
			return ptext.getText().toString().trim();
		}
		return null;
	}

	void updateAllMarksDB() {

		ContentValues contentValuesmark = new ContentValues();

		// Marks1
		contentValuesmark.put(SSConstants.MARK1A, getMarksfromEdit(tv_mark1a));
		contentValuesmark.put(SSConstants.MARK1B, getMarksfromEdit(tv_mark1b));
		contentValuesmark.put(SSConstants.MARK1C, getMarksfromEdit(tv_mark1c));
		contentValuesmark.put(SSConstants.MARK1D, getMarksfromEdit(tv_mark1d));
			contentValuesmark.put(SSConstants.MARK1E, getMarksfromEdit(tv_mark1e));
			contentValuesmark.put(SSConstants.MARK2E, getMarksfromEdit(tv_mark2e));
			contentValuesmark.put(SSConstants.MARK3E, getMarksfromEdit(tv_mark3e));
			contentValuesmark.put(SSConstants.MARK4E, getMarksfromEdit(tv_mark4e));
			contentValuesmark.put(SSConstants.MARK5E, getMarksfromEdit(tv_mark5e));
			contentValuesmark.put(SSConstants.MARK6E, getMarksfromEdit(tv_mark6e));
			contentValuesmark.put(SSConstants.MARK7E, getMarksfromEdit(tv_mark7e));
			contentValuesmark.put(SSConstants.MARK8E, getMarksfromEdit(tv_mark8e));
		

		// Marks2
		contentValuesmark.put(SSConstants.MARK2A, getMarksfromEdit(tv_mark2a));
		contentValuesmark.put(SSConstants.MARK2B, getMarksfromEdit(tv_mark2b));
		contentValuesmark.put(SSConstants.MARK2C, getMarksfromEdit(tv_mark2c));
		contentValuesmark.put(SSConstants.MARK2D, getMarksfromEdit(tv_mark2d));
		

		// Marks3
		contentValuesmark.put(SSConstants.MARK3A, getMarksfromEdit(tv_mark3a));
		contentValuesmark.put(SSConstants.MARK3B, getMarksfromEdit(tv_mark3b));
		contentValuesmark.put(SSConstants.MARK3C, getMarksfromEdit(tv_mark3c));
		contentValuesmark.put(SSConstants.MARK3D, getMarksfromEdit(tv_mark3d));
		

		// Marks4
		contentValuesmark.put(SSConstants.MARK4A, getMarksfromEdit(tv_mark4a));
		contentValuesmark.put(SSConstants.MARK4B, getMarksfromEdit(tv_mark4b));
		contentValuesmark.put(SSConstants.MARK4C, getMarksfromEdit(tv_mark4c));
		contentValuesmark.put(SSConstants.MARK4D, getMarksfromEdit(tv_mark4d));
		

		// Marks5
		contentValuesmark.put(SSConstants.MARK5A, getMarksfromEdit(tv_mark5a));
		contentValuesmark.put(SSConstants.MARK5B, getMarksfromEdit(tv_mark5b));
		contentValuesmark.put(SSConstants.MARK5C, getMarksfromEdit(tv_mark5c));
		contentValuesmark.put(SSConstants.MARK5D, getMarksfromEdit(tv_mark5d));
		

		// Marks6
		contentValuesmark.put(SSConstants.MARK6A, getMarksfromEdit(tv_mark6a));
		contentValuesmark.put(SSConstants.MARK6B, getMarksfromEdit(tv_mark6b));
		contentValuesmark.put(SSConstants.MARK6C, getMarksfromEdit(tv_mark6c));
		contentValuesmark.put(SSConstants.MARK6D, getMarksfromEdit(tv_mark6d));
		

		// Marks7
		contentValuesmark.put(SSConstants.MARK7A, getMarksfromEdit(tv_mark7a));
		contentValuesmark.put(SSConstants.MARK7B, getMarksfromEdit(tv_mark7b));
		contentValuesmark.put(SSConstants.MARK7C, getMarksfromEdit(tv_mark7c));
		contentValuesmark.put(SSConstants.MARK7D, getMarksfromEdit(tv_mark7d));
		

		// Marks8
		contentValuesmark.put(SSConstants.MARK8A, getMarksfromEdit(tv_mark8a));
		contentValuesmark.put(SSConstants.MARK8B, getMarksfromEdit(tv_mark8b));
		contentValuesmark.put(SSConstants.MARK8C, getMarksfromEdit(tv_mark8c));
		contentValuesmark.put(SSConstants.MARK8D, getMarksfromEdit(tv_mark8d));
		

		contentValuesmark.put(SSConstants.R1_TOTAL, row_total1);
		contentValuesmark.put(SSConstants.R2_TOTAL, row_total2);
		contentValuesmark.put(SSConstants.R3_TOTAL, row_total3);
		contentValuesmark.put(SSConstants.R4_TOTAL, row_total4);
		contentValuesmark.put(SSConstants.R5_TOTAL, row_total5);
		contentValuesmark.put(SSConstants.R6_TOTAL, row_total6);
		contentValuesmark.put(SSConstants.R7_TOTAL, row_total7);
		contentValuesmark.put(SSConstants.R8_TOTAL, row_total8);
		contentValuesmark.put(SSConstants.GRAND_TOTAL_MARK, grand_totally);
		insertToEvaluationDB(contentValuesmark);

	}

	private String row1Total_() {
		String mark = null;

		String mark1a = tv_mark1a.getText().toString().trim();
		if (TextUtils.isEmpty(mark1a)) {
			mark1a = null;
		}
		String mark1b = tv_mark1b.getText().toString().trim();
		if (TextUtils.isEmpty(mark1b)) {
			mark1b = null;
		}
		String mark1c = tv_mark1c.getText().toString().trim();
		if (TextUtils.isEmpty(mark1c)) {
			mark1c = null;
		}
		String mark1d = tv_mark1d.getText().toString().trim();
		if (TextUtils.isEmpty(mark1d)) {
			mark1d = null;
		}
		String mark1e = tv_mark1e.getText().toString().trim();
		if (TextUtils.isEmpty(mark1e)) {
			mark1e = null;
		}

		if (!TextUtils.isEmpty(mark1a) || !TextUtils.isEmpty(mark1b)
				|| !TextUtils.isEmpty(mark1c) || !TextUtils.isEmpty(mark1d)
				|| !TextUtils.isEmpty(mark1e)) {
			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark1a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark1b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark1c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark1d))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark1e))) ? "0" : mark));

		}

		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(maxMark)) {
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, "Total Exceeds "
							+ maxMark);
					mark = row_total1;
				}
			} else {
				row_total1 = mark;
			}
		} else {
			row_total1 = null;
		}
		Log.v("row_total1", "1  " + mark);
		return mark;
	}
  
	private String row2Total_() {
		String mark = null;

		String mark2a = tv_mark2a.getText().toString().trim();
		if (TextUtils.isEmpty(mark2a)) {
			mark2a = null;
		}
		String mark2b = tv_mark2b.getText().toString().trim();
		if (TextUtils.isEmpty(mark2b)) {
			mark2b = null;
		}
		String mark2c = tv_mark2c.getText().toString().trim();
		if (TextUtils.isEmpty(mark2c)) {
			mark2c = null;
		}
		String mark2d = tv_mark2d.getText().toString().trim();
		if (TextUtils.isEmpty(mark2d)) {
			mark2d = null;
		}
		String mark2e = tv_mark2e.getText().toString().trim();
		if (TextUtils.isEmpty(mark2e)) {
			mark2e = null;
		}

		if (!TextUtils.isEmpty(mark2a) || !TextUtils.isEmpty(mark2b)
				|| !TextUtils.isEmpty(mark2c) || !TextUtils.isEmpty(mark2d)
				|| !TextUtils.isEmpty(mark2e)) {
			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark2a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark2b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark2c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark2d))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark2e))) ? "0" : mark));

		}

		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(maxMark)) {
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, "Total Exceeds "
							+ maxMark);
					mark = row_total2;
				}
			} else {
				row_total2 = mark;
			}
		} else {
			row_total2 = null;
		}
		Log.v("row_total2", "2  " + mark);
		return mark;
	}

	private String row3Total_() {
		String mark = null;

		String mark3a = tv_mark3a.getText().toString().trim();
		if (TextUtils.isEmpty(mark3a)) {
			mark3a = null;
		}
		String mark3b = tv_mark3b.getText().toString().trim();
		if (TextUtils.isEmpty(mark3b)) {
			mark3b = null;
		}
		String mark3c = tv_mark3c.getText().toString().trim();
		if (TextUtils.isEmpty(mark3c)) {
			mark3c = null;
		}
		String mark3d = tv_mark3d.getText().toString().trim();
		if (TextUtils.isEmpty(mark3d)) {
			mark3d = null;
		}
		String mark3e = tv_mark3e.getText().toString().trim();
		if (TextUtils.isEmpty(mark3e)) {
			mark3e = null;
		}

		if (!TextUtils.isEmpty(mark3a) || !TextUtils.isEmpty(mark3b)
				|| !TextUtils.isEmpty(mark3c) || !TextUtils.isEmpty(mark3d)
				|| !TextUtils.isEmpty(mark3e)) {
			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark3a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark3b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark3c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark3d))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark3e))) ? "0" : mark));

		}

		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(maxMark)) {
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, "Total Exceeds "
							+ maxMark);
					mark = row_total3;
				}
			} else {
				row_total3 = mark;
			}
		} else {
			row_total3 = null;
		}
		Log.v("row_total3", "3  " + mark);
		return mark;
	}

	private String row4Total_() {
		String mark = null;
		if( !is_subject_code_special_case){
		String mark4a = tv_mark4a.getText().toString().trim();
		if (TextUtils.isEmpty(mark4a)) {
			mark4a = null;
		}
		String mark4b = tv_mark4b.getText().toString().trim();
		if (TextUtils.isEmpty(mark4b)) {
			mark4b = null;
		}
		String mark4c = tv_mark4c.getText().toString().trim();
		if (TextUtils.isEmpty(mark4c)) {
			mark4c = null;
		}
		String mark4d = tv_mark4d.getText().toString().trim();
		if (TextUtils.isEmpty(mark4d)) {
			mark4d = null;
		}
		String mark4e = tv_mark4e.getText().toString().trim();
		if (TextUtils.isEmpty(mark4e)) {
			mark4e = null;
		}

		if (!TextUtils.isEmpty(mark4a) || !TextUtils.isEmpty(mark4b)
				|| !TextUtils.isEmpty(mark4c) || !TextUtils.isEmpty(mark4d)
				|| !TextUtils.isEmpty(mark4e)) {
			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark4a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark4b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark4c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark4d))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark4e))) ? "0" : mark));

		}

		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(maxMark)) {
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, "Total Exceeds "
							+ maxMark);
					mark = row_total4;
				}
			} else {
				row_total4 = mark;
			}
		} else {
			row_total4 = null;
		}  
	}
		
		else if(R09BTech && is_subject_code_special_case){ //Swapna
			if(subjectCode.equalsIgnoreCase(SSConstants.SUBJ_54017_MACHINE_DRAWING_3) || 
					subjectCode.equalsIgnoreCase(
							SSConstants.SUBJ_54065_MACHINE_DRAWING_AND_COMPUTER_AIDED_GRAPHICS_4) ||
							subjectCode.equalsIgnoreCase(SSConstants.SUBJ_V0323_MACHINE_DRAWING_7)){

				String mark4a = tv_mark4a.getText().toString().trim();
				if (TextUtils.isEmpty(mark4a)) {
					mark4a = null;
				}
				
				if (!TextUtils.isEmpty(mark4a) ) {
					mark = String
							.valueOf(Float.parseFloat(((TextUtils
									.isEmpty(mark = mark4a))) ? "0" : mark));
				}

				if (!TextUtils.isEmpty(mark)) {
					if (Float.parseFloat(mark) > Float.parseFloat(""+ maxTotalMark_4)) {
						View focusedView = getCurrentFocus();
						if (focusedView != null && focusedView instanceof EditText) {
							alertForInvalidMark(focusedView, true, "Total Exceeds "
									+ maxTotalMark_4);
							mark = row_total4;
						}    
					} else {  
						row_total4 = mark;
					}
				} else {
					row_total4 = null;
				}
				Log.v("row_total4", "4  " + mark);
			
			}
		}
		return mark;
	}

	private String row5Total_() {
		String mark = null;

		String mark5a = tv_mark5a.getText().toString().trim();
		if (TextUtils.isEmpty(mark5a)) {
			mark5a = null;
		}
		String mark5b = tv_mark5b.getText().toString().trim();
		if (TextUtils.isEmpty(mark5b)) {
			mark5b = null;
		}
		String mark5c = tv_mark5c.getText().toString().trim();
		if (TextUtils.isEmpty(mark5c)) {
			mark5c = null;
		}
		String mark5d = tv_mark5d.getText().toString().trim();
		if (TextUtils.isEmpty(mark5d)) {
			mark5d = null;
		}
		String mark5e = tv_mark5e.getText().toString().trim();
		if (TextUtils.isEmpty(mark5e)) {
			mark5e = null;
		}

		if (!TextUtils.isEmpty(mark5a) || !TextUtils.isEmpty(mark5b)
				|| !TextUtils.isEmpty(mark5c) || !TextUtils.isEmpty(mark5d)
				|| !TextUtils.isEmpty(mark5e)) {
			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark5a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark5b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark5c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark5d))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark5e))) ? "0" : mark));

		}

		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(maxMark)) {
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, "Total Exceeds "
							+ maxMark);
					mark = row_total5;
				}
			} else {
				row_total5 = mark;
			}
		} else {
			row_total5 = null;
		}
		return mark;
	}

	private String row6Total_() {
		String mark = null;

		String mark6a = tv_mark6a.getText().toString().trim();
		if (TextUtils.isEmpty(mark6a)) {
			mark6a = null;
		}
		String mark6b = tv_mark6b.getText().toString().trim();
		if (TextUtils.isEmpty(mark6b)) {
			mark6b = null;
		}
		String mark6c = tv_mark6c.getText().toString().trim();
		if (TextUtils.isEmpty(mark6c)) {
			mark6c = null;
		}
		String mark6d = tv_mark6d.getText().toString().trim();
		if (TextUtils.isEmpty(mark6d)) {
			mark6d = null;
		}
		String mark6e = tv_mark6e.getText().toString().trim();
		if (TextUtils.isEmpty(mark6e)) {
			mark6e = null;
		}

		if (!TextUtils.isEmpty(mark6a) || !TextUtils.isEmpty(mark6b)
				|| !TextUtils.isEmpty(mark6c) || !TextUtils.isEmpty(mark6d)
				|| !TextUtils.isEmpty(mark6e)) {
			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark6a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark6b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark6c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark6d))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark6e))) ? "0" : mark));

		}

		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(maxMark)) {
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, "Total Exceeds "
							+ maxMark);
					mark = row_total6;
				}
			} else {
				row_total6 = mark;
			}
		} else {
			row_total6 = null;
		}
		return mark;
	}

	private String row7Total_() {
		String mark = null;

		String mark7a = tv_mark7a.getText().toString().trim();
		if (TextUtils.isEmpty(mark7a)) {
			mark7a = null;
		}
		String mark7b = tv_mark7b.getText().toString().trim();
		if (TextUtils.isEmpty(mark7b)) {
			mark7b = null;
		}
		String mark7c = tv_mark7c.getText().toString().trim();
		if (TextUtils.isEmpty(mark7c)) {
			mark7c = null;
		}
		String mark7d = tv_mark7d.getText().toString().trim();
		if (TextUtils.isEmpty(mark7d)) {
			mark7d = null;
		}
		String mark7e = tv_mark7e.getText().toString().trim();
		if (TextUtils.isEmpty(mark7e)) {
			mark7e = null;
		}

		if (!TextUtils.isEmpty(mark7a) || !TextUtils.isEmpty(mark7b)
				|| !TextUtils.isEmpty(mark7c) || !TextUtils.isEmpty(mark7d)
				|| !TextUtils.isEmpty(mark7e)) {
			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark7a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark7b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark7c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark7d))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark7e))) ? "0" : mark));

		}

		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(maxMark)) {
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, "Total Exceeds "
							+ maxMark);
					mark = row_total7;
				}
			} else {
				row_total7 = mark;
			}
		} else {
			row_total7 = null;
		}
		return mark;
	}

	private String row8Total_() {
		String mark = null;

		String mark8a = tv_mark8a.getText().toString().trim();
		if (TextUtils.isEmpty(mark8a)) {
			mark8a = null;
		}
		String mark8b = tv_mark8b.getText().toString().trim();
		if (TextUtils.isEmpty(mark8b)) {
			mark8b = null;
		}
		String mark8c = tv_mark8c.getText().toString().trim();
		if (TextUtils.isEmpty(mark8c)) {
			mark8c = null;
		}
		String mark8d = tv_mark8d.getText().toString().trim();
		if (TextUtils.isEmpty(mark8d)) {
			mark8d = null;
		}
		String mark8e = tv_mark8e.getText().toString().trim();
		if (TextUtils.isEmpty(mark8e)) {
			mark8e = null;
		}

		if (!TextUtils.isEmpty(mark8a) || !TextUtils.isEmpty(mark8b)
				|| !TextUtils.isEmpty(mark8c) || !TextUtils.isEmpty(mark8d)
				|| !TextUtils.isEmpty(mark8e)) {
			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark8a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark8b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark8c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark8d))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark8e))) ? "0" : mark));

		}

		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(maxMark)) {
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, "Total Exceeds "
							+ maxMark);
					mark = row_total8;
				}
			} else {
				row_total8 = mark;
			}
		} else {
			row_total8 = null;
		}
		return mark;
	}

	private String calculateGrandTotal() {
		BigDecimal roundOffGrandTotal = null;
		float grandTotal = 0;
		
		ArrayList<Float> listTotalMarks = new ArrayList<Float>();

		if (!TextUtils.isEmpty(row_total1)) {
			listTotalMarks.add(Float.valueOf(row_total1));
		} else {
			listTotalMarks.add(Float.valueOf("0"));
		}

		if (!TextUtils.isEmpty(row_total2)) {
			listTotalMarks.add(Float.valueOf(row_total2));
		} else {
			listTotalMarks.add(Float.valueOf("0"));
		}

		if (!TextUtils.isEmpty(row_total3)) {
			listTotalMarks.add(Float.valueOf(row_total3));
		} else {
			listTotalMarks.add(Float.valueOf("0"));
		}

		if ( !is_subject_code_special_case) {
		if (!TextUtils.isEmpty(row_total4)) {
			listTotalMarks.add(Float.valueOf(row_total4));
		} else {
			listTotalMarks.add(Float.valueOf("0"));
		}

		if (!TextUtils.isEmpty(row_total5)) {
			listTotalMarks.add(Float.valueOf(row_total5));
		} else {
			listTotalMarks.add(Float.valueOf("0"));
		}

		if (!TextUtils.isEmpty(row_total6)) {
			listTotalMarks.add(Float.valueOf(row_total6));
		} else {
			listTotalMarks.add(Float.valueOf("0"));
		}

		if (!TextUtils.isEmpty(row_total7)) {
			listTotalMarks.add(Float.valueOf(row_total7));
		} else {
			listTotalMarks.add(Float.valueOf("0"));
		}

		if (!TextUtils.isEmpty(row_total8)) {
			listTotalMarks.add(Float.valueOf(row_total8));
		} else {
			listTotalMarks.add(Float.valueOf("0"));
		}

		
		if (!listTotalMarks.isEmpty()) {
			Collections.sort(listTotalMarks);
			if (listTotalMarks.size() > 7) {
				for (int a = 7; a > 2; a--) {
					grandTotal += listTotalMarks.get(a);
					Log.v("" + listTotalMarks.size(),
							"vvv " + listTotalMarks.get(a));
				}
			}
		  }
		}
		else if(R09BTech && is_subject_code_special_case){
			if(subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_54065_MACHINE_DRAWING_AND_COMPUTER_AIDED_GRAPHICS_4)
					|| subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_54017_MACHINE_DRAWING_3) ||
					subjectCode.equalsIgnoreCase(SSConstants.SUBJ_V0323_MACHINE_DRAWING_7)
					){
				
				if (!listTotalMarks.isEmpty()) {
					Collections.sort(listTotalMarks, Collections.reverseOrder());
						// best of 2 from 3
						
						//for (int a = 7; a > 2; a--)     
					for (int a = 0; a < 2; a++)
						{   
						
							grandTotal += listTotalMarks.get(a);
							Log.v("" + listTotalMarks.size(), "vvv "
									+ listTotalMarks.get(a));
						}
					  
					if (!TextUtils.isEmpty(row_total4)) {
						listTotalMarks.add(Float.valueOf(row_total4));
					} else {  
						listTotalMarks.add(Float.valueOf("0"));
					}
   
					// 4 is compulsory
					float mark = Float.parseFloat(TextUtils.isEmpty(row_total4) ? "0" : row_total4);
					
					grandTotal += mark;
				//	grandTotal += listTotalMarks.get(3);
					
					/*roundOffGrandTotal = new BigDecimal(Float.toString(grandTotal));
					roundOffGrandTotal = roundOffGrandTotal.setScale(0,
							BigDecimal.ROUND_HALF_UP);
					_grand_total = String.valueOf(roundOffGrandTotal);
*/
					
				}
			}
		}

			roundOffGrandTotal = new BigDecimal(Float.toString(grandTotal));
			roundOffGrandTotal = roundOffGrandTotal.setScale(0,
					BigDecimal.ROUND_HALF_UP);
		
		if (roundOffGrandTotal != null) {
			return "" + roundOffGrandTotal;
		} else {
			return "" + 0;
		}  
	  
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

						changeEditTextCellBGToGreen(view);

						EditText et = (EditText) view;
						// et.setFocusableInTouchMode(true);
						// et.requestFocus();
						// et.setOnClickListener(Scrutiny_MarkDialog_R13_Mtech.this);
						et.setError(null);
						et.setFocusableInTouchMode(false);
						et.setOnClickListener(Scrutiny_MarkDialog.this);
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

	void setTotaltoTextView(EditText focusedView) {

		if (focusedView.getId() == R.id.q1_a
				|| focusedView.getId() == R.id.q1_b
				|| focusedView.getId() == R.id.q1_c
				|| focusedView.getId() == R.id.q1_d
				|| focusedView.getId() == R.id.q1_e) {
			row_total1 = row1Total_();
			/*
			 * if(TextUtils.isEmpty(row_total1)) tv_mark1_total.setText("");
			 * else tv_mark1_total.setText(""+row_total1);
			 */

		} else if (focusedView.getId() == R.id.q2_a
				|| focusedView.getId() == R.id.q2_b
				|| focusedView.getId() == R.id.q2_c
				|| focusedView.getId() == R.id.q2_d
				|| focusedView.getId() == R.id.q2_e) {
			row_total2 = row2Total_();
			/*
			 * if(TextUtils.isEmpty(row_total2)) tv_mark2_total.setText("");
			 * else tv_mark2_total.setText(""+row_total2);
			 */
		} else if (focusedView.getId() == R.id.q3_a
				|| focusedView.getId() == R.id.q3_b
				|| focusedView.getId() == R.id.q3_c
				|| focusedView.getId() == R.id.q3_d
				|| focusedView.getId() == R.id.q3_e) {
			row_total3 = row3Total_();
			/*
			 * if(TextUtils.isEmpty(row_total3)) tv_mark3_total.setText("");
			 * else tv_mark3_total.setText(""+row_total3);
			 */
		} else if (focusedView.getId() == R.id.q4_a
				|| focusedView.getId() == R.id.q4_b
				|| focusedView.getId() == R.id.q4_c
				|| focusedView.getId() == R.id.q4_d
				|| focusedView.getId() == R.id.q4_e) {
			row_total4 = row4Total_();
			/*
			 * if(TextUtils.isEmpty(row_total4)) tv_mark4_total.setText("");
			 * else tv_mark4_total.setText(""+row_total4);
			 */
		} else if (focusedView.getId() == R.id.q5_a
				|| focusedView.getId() == R.id.q5_b
				|| focusedView.getId() == R.id.q5_c
				|| focusedView.getId() == R.id.q5_d
				|| focusedView.getId() == R.id.q5_e) {
			row_total5 = row5Total_();
			/*
			 * if(TextUtils.isEmpty(row_total5)) tv_mark5_total.setText("");
			 * else tv_mark5_total.setText(""+row_total5);
			 */
		} else if (focusedView.getId() == R.id.q6_a
				|| focusedView.getId() == R.id.q6_b
				|| focusedView.getId() == R.id.q6_c
				|| focusedView.getId() == R.id.q6_d
				|| focusedView.getId() == R.id.q6_e) {
			row_total6 = row6Total_();
			/*
			 * if(TextUtils.isEmpty(row_total6)) tv_mark6_total.setText("");
			 * else tv_mark6_total.setText(""+row_total6);
			 */
		} else if (focusedView.getId() == R.id.q7_a
				|| focusedView.getId() == R.id.q7_b
				|| focusedView.getId() == R.id.q7_c
				|| focusedView.getId() == R.id.q7_d
				|| focusedView.getId() == R.id.q7_e) {
			row_total7 = row7Total_();
			/*
			 * if(TextUtils.isEmpty(row_total7)) tv_mark7_total.setText("");
			 * else tv_mark7_total.setText(""+row_total7);
			 */
		} else if (focusedView.getId() == R.id.q8_a
				|| focusedView.getId() == R.id.q8_b
				|| focusedView.getId() == R.id.q8_c
				|| focusedView.getId() == R.id.q8_d
				|| focusedView.getId() == R.id.q8_e) {
			row_total8 = row8Total_();
			/*
			 * if(TextUtils.isEmpty(row_total8)) tv_mark8_total.setText("");
			 * else tv_mark8_total.setText(""+row_total8);
			 */
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
		case R.id.q1_c:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q1_d:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q1_e:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q2_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q2_b:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q2_c:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q2_d:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q2_e:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q3_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q3_b:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q3_c:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q3_d:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q3_e:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q4_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q4_b:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q4_c:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q4_d:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q4_e:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q5_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q5_b:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q5_c:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q5_d:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q5_e:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q6_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q6_b:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q6_c:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q6_d:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q6_e:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q7_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q7_b:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q7_c:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q7_d:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q7_e:
			showRemarkSelectionDialog(view);
			break;

		case R.id.q8_a:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q8_b:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q8_c:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q8_d:
			showRemarkSelectionDialog(view);
			break;
		case R.id.q8_e:
			showRemarkSelectionDialog(view);
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
				et_focusedView.removeTextChangedListener(Scrutiny_MarkDialog.this);
				et_focusedView.setText("");
				et_focusedView.addTextChangedListener(Scrutiny_MarkDialog.this);
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