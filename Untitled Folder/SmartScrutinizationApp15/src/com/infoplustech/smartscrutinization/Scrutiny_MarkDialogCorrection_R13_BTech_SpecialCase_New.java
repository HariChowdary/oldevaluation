package com.infoplustech.smartscrutinization;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.db.Scrutiny_TempDatabase;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Scrutiny_MarkDialogCorrection_R13_BTech_SpecialCase_New extends Activity implements
OnClickListener, TextWatcher, OnFocusChangeListener{
	EditText tv_mark1a, tv_mark1b, tv_mark1c, tv_mark1d, tv_mark1e;

	EditText tv_mark2a, tv_mark2b, tv_mark2c, tv_mark2d, tv_mark2e;

	EditText tv_mark3a, tv_mark3b, tv_mark3c, tv_mark3d, tv_mark3e;

	EditText tv_mark4a, tv_mark4b, tv_mark4c, tv_mark4d, tv_mark4e;

	EditText tv_mark5a, tv_mark5b, tv_mark5c, tv_mark5d, tv_mark5e;

	EditText tv_mark6a, tv_mark6b, tv_mark6c, tv_mark6d, tv_mark6e;

	EditText tv_mark7a, tv_mark7b, tv_mark7c, tv_mark7d, tv_mark7e;

	EditText tv_mark8a, tv_mark8b, tv_mark8c, tv_mark8d, tv_mark8e;

	// EditText tv_mark1_total, tv_mark2_total, tv_mark3_total, tv_mark4_total,
	// tv_mark5_total, tv_mark6_total, tv_mark7_total, tv_mark8_total;
	//
	// EditText tv_grand_toal;

	String maxTotalMark = "75";
	String RowTotalLimit = "10";
	String maxTotalMark_5 = "45";
	HashMap<Integer, String> RemarksArray;
	HashMap<Integer, String> marksArray;
	
	// BigDecimal final_Total = null;

	String _mark1_total = null, _mark2_total = null, _mark3_total = null,
			_mark4_total = null, _mark5_total = null, _mark6_total = null,
			_mark7_total = null, _mark8_total = null, _grand_total = null;

	String userId, subjectCode, ansBookBarcode, bundleNo, bundle_serial_no, SeatNo;
	int scrutinizedStatusInObsMode;
	boolean isAddScript, clickable = false;
	int max_mark;
	
	Boolean clickFind = false;

	SharedPreferences sharedPreference;
	SharedPreferences.Editor editor;
	//boolean is_subject_code_special_case = false;
	private final int SUB_TOTAL_EXCEEDS_MAX_MARKS_FOR_SPECIAL_CASE_SUBJ_CODES = 200;

	private PowerManager.WakeLock wl;
	private boolean isRegulation;
	Utility instanceUtitlity;
	//Boolean R13BTech = false;

	private String row_total = null;

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.scrutiny_activity_main, menu);
	 * menu.findItem(R.id.menu_settings).setVisible(false); return true; }
	 * 
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { if (item.getItemId() == R.id.menu_back) { finish(); } return
	 * super.onMenuItemSelected(featureId, item); }
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scrutiny_r09_nototals);

		// if (!Utility.isNetworkAvailable(this)) {
		// alertMessageForChargeAutoUpdateApk(getString(R.string.alert_network_avail));
		// return;     
		// }   
		RemarksArray = new HashMap<Integer, String>();
		marksArray = new HashMap<Integer, String>();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");
		// get data from previous activity/screen
		instanceUtitlity = new Utility();
		isRegulation = instanceUtitlity.isRegulation_R13_Mtech(this);
		Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO);
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor = _database.executeSQLQuery(
				"Select max(max_total) as Value from "
						+ SSConstants.TABLE_SCRUTINY_SAVE
						+ " where bundle_no= '" + bundleNo + "'", null);

		if (_cursor.getCount() > 0 && _cursor != null) {
			_cursor.moveToFirst();
			max_mark = ((_cursor.getInt(_cursor.getColumnIndex("Value"))) / 5);
			if (max_mark == 0) {    
				max_mark = 16;   
			}
		}
		_cursor.close();

	
		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		ansBookBarcode = intent_extras
				.getStringExtra(SSConstants.ANS_BOOK_BARCODE);
		bundle_serial_no = intent_extras
				.getStringExtra(SSConstants.BUNDLE_SERIAL_NO);
		subjectCode = intent_extras.getStringExtra(SSConstants.SUBJECT_CODE);

		if (intent_extras.hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
			isAddScript = true;
		} else {
			isAddScript = false;
    
		}

		
		/*R09BTech = instanceUtitlity
				.isRegulation_R09_BTech_Course(Scrutiny_MarkDialogCorrection.this);*/
		
		
		
		/*if (Utility.is_subject_code_special_case(subjectCode)) {//Swapna
			is_subject_code_special_case = true;    
		} else {    
			is_subject_code_special_case = false;    
		}*/
		
		/*if (R09BTech && !is_subject_code_special_case) {
			RowTotalLimit = "15";
			maxTotalMark = "75";
		} 
		
		else {
			RowTotalLimit = "12";  
			String maxTotalMark = "60";
		}   
  */
		showItems();

		Button btnSubmit1 = (Button) findViewById(R.id.btn_submit1);
		if (scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED
				|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH
				|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED) {
			//((TextView) findViewById(R.id.tv_user_id)).setVisibility(View.GONE);
//			((TextView) findViewById(R.id.tv_h_user_id))
//					.setVisibility(View.GONE);
			((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
//			((TextView) findViewById(R.id.tv_bundle_no))
//					.setVisibility(View.GONE);
//			((TextView) findViewById(R.id.tv_h_bundle_no))  
//					.setVisibility(View.GONE);

//			((TextView) findViewById(R.id.tv_h_sub_code))
//					.setVisibility(View.GONE);
  
			((TextView) findViewById(R.id.tv_ans_book)).setText(bundle_serial_no);
				//	.setVisibility(View.GONE);
			
//			((TextView) findViewById(R.id.tv_sub_code))
//			.setVisibility(View.GONE);

//	((TextView) findViewById(R.id.tv_h_serial_no))
//			.setVisibility(View.GONE);
			btnSubmit1.setVisibility(View.GONE);
			LinearLayout ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
			ll_submit.addView(addNumbersView());

			Cursor _cursor_scripts_count = _database.passedQuery(
					SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO
							+ " = '" + bundleNo + "'",
					SSConstants.BUNDLE_SERIAL_NO);
			getActionBar().setTitle(
					getString(R.string.app_name) + " " + bundle_serial_no
							+ " of  " + _cursor_scripts_count.getCount());
			_cursor_scripts_count.close();
		} else {
			((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
			btnSubmit1.setOnClickListener(this);
		}

	}

	// set marks
	
	private void setMarkToCellFromDB1(String pMark, EditText pTextView) {

		Log.v("DbValue", "pMark " + pMark);
		if (!TextUtils.isEmpty(pMark)) {
			pTextView.setTag(pMark);
			pTextView.setText(pMark);
		} else {
			pTextView.setTag("");
			pTextView.setText(pMark);
		}

	}  

	// set marks
	private void setMarkToCellFromDB(String pMark, EditText pTextView) {
		if (!TextUtils.isEmpty(pMark)) {
			pTextView.setText(pMark);
		}
	}

	// set marks
	private void setMarkToCellFromDB(String pMark, TextView pTextView) {
		if (!TextUtils.isEmpty(pMark)) {
			pTextView.setText(pMark);
		}
	}

	// add layout_numbers view for add scripts
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

	// read from DB and show marks
	private void showItems() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
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

		tv_mark1a.setFocusable(false);
		tv_mark1b.setFocusable(false);
		tv_mark1c.setFocusable(false);
		tv_mark1d.setFocusable(false);
		tv_mark1e.setFocusable(false);

		tv_mark2a.setFocusable(false);
		tv_mark2b.setFocusable(false);
		tv_mark2c.setFocusable(false);
		tv_mark2d.setFocusable(false);
		tv_mark2e.setFocusable(false);

		tv_mark3a.setFocusable(false);
		tv_mark3b.setFocusable(false);
		tv_mark3c.setFocusable(false);
		tv_mark3d.setFocusable(false);
		tv_mark3e.setFocusable(false);

		tv_mark4a.setFocusable(false);
		tv_mark4b.setFocusable(false);
		tv_mark4c.setFocusable(false);
		tv_mark4d.setFocusable(false);
		tv_mark4e.setFocusable(false);

		tv_mark5a.setFocusable(false);
		tv_mark5b.setFocusable(false);
		tv_mark5c.setFocusable(false);
		tv_mark5d.setFocusable(false);
		tv_mark5e.setFocusable(false);

		tv_mark6a.setFocusable(false);
		tv_mark6b.setFocusable(false);
		tv_mark6c.setFocusable(false);
		tv_mark6d.setFocusable(false);
		tv_mark6e.setFocusable(false);

		tv_mark7a.setFocusable(false);
		tv_mark7b.setFocusable(false);
		tv_mark7c.setFocusable(false);
		tv_mark7d.setFocusable(false);
		tv_mark7e.setFocusable(false);

		tv_mark8a.setFocusable(false);
		tv_mark8b.setFocusable(false);
		tv_mark8c.setFocusable(false);
		tv_mark8d.setFocusable(false);
		tv_mark8e.setFocusable(false);

		tv_mark1a.setFocusableInTouchMode(false);
		tv_mark1b.setFocusableInTouchMode(false);
		tv_mark1c.setFocusableInTouchMode(false);
		tv_mark1d.setFocusableInTouchMode(false);
		tv_mark1e.setFocusableInTouchMode(false);

		tv_mark2a.setFocusableInTouchMode(false);
		tv_mark2b.setFocusableInTouchMode(false);
		tv_mark2c.setFocusableInTouchMode(false);
		tv_mark2d.setFocusableInTouchMode(false);
		tv_mark2e.setFocusableInTouchMode(false);

		tv_mark3a.setFocusableInTouchMode(false);
		tv_mark3b.setFocusableInTouchMode(false);
		tv_mark3c.setFocusableInTouchMode(false);
		tv_mark3d.setFocusableInTouchMode(false);
		tv_mark3e.setFocusableInTouchMode(false);

		tv_mark4a.setFocusableInTouchMode(false);
		tv_mark4b.setFocusableInTouchMode(false);
		tv_mark4c.setFocusableInTouchMode(false);
		tv_mark4d.setFocusableInTouchMode(false);
		tv_mark4e.setFocusableInTouchMode(false);

		tv_mark5a.setFocusableInTouchMode(false);
		tv_mark5b.setFocusableInTouchMode(false);
		tv_mark5c.setFocusableInTouchMode(false);
		tv_mark5d.setFocusableInTouchMode(false);
		tv_mark5e.setFocusableInTouchMode(false);

		tv_mark6a.setFocusableInTouchMode(false);
		tv_mark6b.setFocusableInTouchMode(false);
		tv_mark6c.setFocusableInTouchMode(false);
		tv_mark6d.setFocusableInTouchMode(false);
		tv_mark6e.setFocusableInTouchMode(false);

		tv_mark7a.setFocusableInTouchMode(false);
		tv_mark7b.setFocusableInTouchMode(false);
		tv_mark7c.setFocusableInTouchMode(false);
		tv_mark7d.setFocusableInTouchMode(false);
		tv_mark7e.setFocusableInTouchMode(false);

		tv_mark8a.setFocusableInTouchMode(false);
		tv_mark8b.setFocusableInTouchMode(false);
		tv_mark8c.setFocusableInTouchMode(false);
		tv_mark8d.setFocusableInTouchMode(false);
		tv_mark8e.setFocusableInTouchMode(false);
		
		//Swapna
		tv_mark1a.setLongClickable(false);
		tv_mark1b.setLongClickable(false);
		tv_mark1c.setLongClickable(false);
		tv_mark1d.setLongClickable(false);
		tv_mark1e.setLongClickable(false);

		tv_mark2a.setLongClickable(false);
		tv_mark2b.setLongClickable(false);
		tv_mark2c.setLongClickable(false);
		tv_mark2d.setLongClickable(false);
		tv_mark2e.setLongClickable(false);

		tv_mark3a.setLongClickable(false);
		tv_mark3b.setLongClickable(false);
		tv_mark3c.setLongClickable(false);
		tv_mark3d.setLongClickable(false);
		tv_mark3e.setLongClickable(false);

		tv_mark4a.setLongClickable(false);
		tv_mark4b.setLongClickable(false);
		tv_mark4c.setLongClickable(false);
		tv_mark4d.setLongClickable(false);
		tv_mark4e.setLongClickable(false);

		tv_mark5a.setLongClickable(false);
		tv_mark5b.setLongClickable(false);
		tv_mark5c.setLongClickable(false);
		tv_mark5d.setLongClickable(false);
		tv_mark5e.setLongClickable(false);

		tv_mark6a.setLongClickable(false);
		tv_mark6b.setLongClickable(false);
		tv_mark6c.setLongClickable(false);
		tv_mark6d.setLongClickable(false);
		tv_mark6e.setLongClickable(false);

		tv_mark7a.setLongClickable(false);
		tv_mark7b.setLongClickable(false);
		tv_mark7c.setLongClickable(false);
		tv_mark7d.setLongClickable(false);
		tv_mark7e.setLongClickable(false);

		tv_mark8a.setLongClickable(false);
		tv_mark8b.setLongClickable(false);
		tv_mark8c.setLongClickable(false);
		tv_mark8d.setLongClickable(false);  
		tv_mark8e.setLongClickable(false);

		// tv_mark1_total = ((EditText) findViewById(R.id.q1_total));
		// tv_mark2_total = ((EditText) findViewById(R.id.q2_total));
		// tv_mark3_total = ((EditText) findViewById(R.id.q3_total));
		// tv_mark4_total = ((EditText) findViewById(R.id.q4_total));
		// tv_mark5_total = ((EditText) findViewById(R.id.q5_total));
		// tv_mark6_total = ((EditText) findViewById(R.id.q6_total));
		// tv_mark7_total = ((EditText) findViewById(R.id.q7_total));
		// tv_mark8_total = ((EditText) findViewById(R.id.q8_total));
		// tv_grand_toal = ((EditText) findViewById(R.id.grand_total));
		//
		//
		// tv_mark1_total.setOnFocusChangeListener(this);
		// tv_mark2_total.setOnFocusChangeListener(this);
		// tv_mark3_total.setOnFocusChangeListener(this);
		// tv_mark4_total.setOnFocusChangeListener(this);
		// tv_mark5_total.setOnFocusChangeListener(this);
		// tv_mark6_total.setOnFocusChangeListener(this);
		// tv_mark7_total.setOnFocusChangeListener(this);
		// tv_mark8_total.setOnFocusChangeListener(this);

		Cursor cursor;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		cursor = _database.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
				SSConstants.ANS_BOOK_BARCODE + " = '" + ansBookBarcode
						+ "' AND " + SSConstants.BUNDLE_NO + " = '" + bundleNo
						+ "'", null);

		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {
				scrutinizedStatusInObsMode = cursor.getInt(cursor
						.getColumnIndex(SSConstants.SCRUTINIZE_STATUS));
				subjectCode = cursor.getString(cursor
						.getColumnIndex(SSConstants.SUBJECT_CODE));

				/*// check for special case subject codes
				if (Utility.is_subject_code_special_case(subjectCode)) {
					is_subject_code_special_case = true;
				} else {
					is_subject_code_special_case = false;
				}*/

				tv_mark1a.addTextChangedListener(this);
				tv_mark1b.addTextChangedListener(this);
				tv_mark1c.addTextChangedListener(this);
				tv_mark1d.addTextChangedListener(this);
				tv_mark1e.addTextChangedListener(this);

				tv_mark2a.addTextChangedListener(this);
				tv_mark2b.addTextChangedListener(this);
				tv_mark2c.addTextChangedListener(this);
				tv_mark2d.addTextChangedListener(this);
				tv_mark2e.addTextChangedListener(this);

				tv_mark3a.addTextChangedListener(this);
				tv_mark3b.addTextChangedListener(this);
				tv_mark3c.addTextChangedListener(this);
				tv_mark3d.addTextChangedListener(this);
				tv_mark3e.addTextChangedListener(this);

				tv_mark6a.addTextChangedListener(this);
				tv_mark6b.addTextChangedListener(this);
				tv_mark6c.addTextChangedListener(this);
				tv_mark6d.addTextChangedListener(this);
				tv_mark6e.addTextChangedListener(this);

				tv_mark4a.addTextChangedListener(this);
				tv_mark4b.addTextChangedListener(this);
				tv_mark4c.addTextChangedListener(this);
				tv_mark4d.addTextChangedListener(this);
				tv_mark4e.addTextChangedListener(this);

				tv_mark5a.addTextChangedListener(this);
				tv_mark5b.addTextChangedListener(this);
				tv_mark5c.addTextChangedListener(this);
				tv_mark5d.addTextChangedListener(this);
				tv_mark5e.addTextChangedListener(this);

				tv_mark7a.addTextChangedListener(this);
				tv_mark7b.addTextChangedListener(this);
				tv_mark7c.addTextChangedListener(this);
				tv_mark7d.addTextChangedListener(this);
				tv_mark7e.addTextChangedListener(this);

				tv_mark8a.addTextChangedListener(this);
				tv_mark8b.addTextChangedListener(this);
				tv_mark8c.addTextChangedListener(this);
				tv_mark8d.addTextChangedListener(this);
				tv_mark8e.addTextChangedListener(this);

				// TextView tvSubjectCode = (TextView)
				// findViewById(R.id.tv_sub_code);
				// tvSubjectCode.setVisibility(View.GONE);
				TextView tvBundelSerialNo = (TextView) findViewById(R.id.tv_ans_book);
				tvBundelSerialNo.setText(bundle_serial_no);

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

				// setMarkToCellFromDB1(cursor.getString(cursor
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
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2E)), tv_mark2e);

				// setMarkToCellFromDB1(cursor.getString(cursor
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
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3E)), tv_mark3e);

				// setMarkToCellFromDB1(cursor.getString(cursor
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
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4E)), tv_mark4e);

				// setMarkToCellFromDB1(cursor.getString(cursor
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
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5E)), tv_mark5e);

				// setMarkToCellFromDB1(cursor.getString(cursor
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
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6E)), tv_mark6e);

				// setMarkToCellFromDB1(cursor.getString(cursor
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
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7E)), tv_mark7e);

				// setMarkToCellFromDB1(cursor.getString(cursor
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
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8E)), tv_mark8e);

				// setMarkToCellFromDB1(cursor.getString(cursor
				// .getColumnIndex(SSConstants.R8_TOTAL)), tv_mark8_total);
				//
				// setMarkToCellFromDB1(cursor.getString(cursor
				// .getColumnIndex(SSConstants.GRAND_TOTAL_MARK)),
				// tv_grand_toal);

				if (!(scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED || scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH)) {

					// tvSubjectCode.setVisibility(View.VISIBLE);
					// tvSubjectCode.setText(subjectCode);

					tvBundelSerialNo.setVisibility(View.VISIBLE);
					tvBundelSerialNo.setText(cursor.getString(cursor
							.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)));
					// ((TextView)
					// findViewById(R.id.tv_user_id)).setText(userId);

					/*tv_mark1a.setOnClickListener(this);
					tv_mark1b.setOnClickListener(this);
					tv_mark1c.setOnClickListener(this);
					tv_mark1d.setOnClickListener(this);
					tv_mark1e.setOnClickListener(this);

					tv_mark2a.setOnClickListener(this);
					tv_mark2b.setOnClickListener(this);
					tv_mark2c.setOnClickListener(this);
					tv_mark2d.setOnClickListener(this);
					tv_mark2e.setOnClickListener(this);*/

					// tv_mark1_total.setOnClickListener(this);
					// tv_mark2_total.setOnClickListener(this);

					// disable edittext for special case subject codes
					disableEditTextForSpecialCaseSubjCode();

					showRemarksWithOrangeColor();

				} else {
					getWindow().setFlags(
							WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
							WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

					// set focusables edittext for special case subject codes
					setFocusablesForSpecialCaseSubjCodes();
				}

			}
		} else {

			showAlert(getString(R.string.alert_barcode_not_exists_in_db),
					getString(R.string.alert_dialog_ok), "", false);
		}
		cursor.close();
	}

	// show remark with color
	private void setRemarkWithColor(String strColor, TextView pTextView) {
		if (!TextUtils.isEmpty(strColor)) {
			if (strColor.equalsIgnoreCase(SSConstants.ORANGE_COLOR)) {
				changeCellBGToOrange(pTextView);
			} else if (strColor.equalsIgnoreCase(SSConstants.GREEN_COLOR)) {
				changeCellBGToGreen(pTextView);
			} else if (strColor.equalsIgnoreCase(SSConstants.RED_COLOR)) {
				changeCellBGToRed(pTextView);
			}
		}
	}

	// show remarked cell with orange color
	private void showRemarksWithOrangeColor() {
		Cursor cursor;
		Scrutiny_TempDatabase _tempDatabase = new Scrutiny_TempDatabase(this);
		cursor = _tempDatabase.getRow(Scrutiny_TempDatabase._SNo + " = '1'");
		if (cursor.getCount() > 0) {

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1A_REMARK)), tv_mark1a);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1B_REMARK)), tv_mark1b);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1C_REMARK)), tv_mark1c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1D_REMARK)), tv_mark1d);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1E_REMARK)), tv_mark1e);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2A_REMARK)), tv_mark2a);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2B_REMARK)), tv_mark2b);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2C_REMARK)), tv_mark2c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2D_REMARK)), tv_mark2d);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2E_REMARK)), tv_mark2e);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3A_REMARK)), tv_mark3a);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3B_REMARK)), tv_mark3b);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3C_REMARK)), tv_mark3c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3D_REMARK)), tv_mark3d);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3E_REMARK)), tv_mark3e);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4A_REMARK)), tv_mark4a);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4B_REMARK)), tv_mark4b);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4C_REMARK)), tv_mark4c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4D_REMARK)), tv_mark4d);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4E_REMARK)), tv_mark4e);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5A_REMARK)), tv_mark5a);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5B_REMARK)), tv_mark5b);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5C_REMARK)), tv_mark5c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5D_REMARK)), tv_mark5d);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5E_REMARK)), tv_mark5e);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6A_REMARK)), tv_mark6a);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6B_REMARK)), tv_mark6b);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6C_REMARK)), tv_mark6c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6D_REMARK)), tv_mark6d);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6E_REMARK)), tv_mark6e);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7A_REMARK)), tv_mark7a);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7B_REMARK)), tv_mark7b);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7C_REMARK)), tv_mark7c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7D_REMARK)), tv_mark7d);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7E_REMARK)), tv_mark7e);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8A_REMARK)), tv_mark8a);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8B_REMARK)), tv_mark8b);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8C_REMARK)), tv_mark8c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8D_REMARK)), tv_mark8d);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8E_REMARK)), tv_mark8e);

			// setRemarkWithColor(cursor.getString(cursor
			// .getColumnIndex(SSConstants.R1_REMARK)), tv_mark1_total);
			//
			// setRemarkWithColor(cursor.getString(cursor
			// .getColumnIndex(SSConstants.R2_REMARK)), tv_mark2_total);
			//
			// setRemarkWithColor(cursor.getString(cursor
			// .getColumnIndex(SSConstants.R3_REMARK)), tv_mark3_total);
			//
			// setRemarkWithColor(cursor.getString(cursor
			// .getColumnIndex(SSConstants.R4_REMARK)), tv_mark4_total);
			//
			// setRemarkWithColor(cursor.getString(cursor
			// .getColumnIndex(SSConstants.R5_REMARK)), tv_mark5_total);
			//
			// setRemarkWithColor(cursor.getString(cursor
			// .getColumnIndex(SSConstants.R6_REMARK)), tv_mark6_total);
			//
			// setRemarkWithColor(cursor.getString(cursor
			// .getColumnIndex(SSConstants.R7_REMARK)), tv_mark7_total);
			//
			// setRemarkWithColor(cursor.getString(cursor
			// .getColumnIndex(SSConstants.R8_REMARK)), tv_mark8_total);

		}

		cursor.close();

	}

	// change textview background or cell color to orange
	private void changeCellBGToOrange(View view) {
		// by def remarked ones are green
		TextView tv = (TextView) view;
		// tv.setBackgroundResource(R.drawable.green_with_border);
		tv.setText("");
		tv.setBackgroundResource(R.drawable.orange_with_border);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(Typeface.DEFAULT_BOLD);
	}

	// change textview background or cell color to green
	private void changeCellBGToGreen(View view) {
		// accepted changes to white
		TextView tv = (TextView) view;
		// tv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		tv.setBackgroundResource(R.drawable.green_with_border);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(Typeface.DEFAULT_BOLD);
	}

	// change textview background or cell color to red
	private void changeCellBGToRed(View view) {
		// accepted changes to white
		TextView tv = (TextView) view;
		tv.setBackgroundResource(R.drawable.red_with_border);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(Typeface.DEFAULT_BOLD);
	}

	// show alert message
	private void showAlert(String msg, String positiveStr, String negativeStr,
			final boolean navigation) {
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
						// clicked sub
						dialog.dismiss();
						if (navigation) {
							updateDBProcess();
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

	ProgressDialog progressDialog;

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

	private void updateDBProcess() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				showProgress();
			};

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				// removeRemarksDB();
				// removeMarksDB();

				updateAllMarksDB();
				ContentValues _contentValues = new ContentValues();
				_contentValues
						.put(SSConstants.TABLET_IMEI,
								((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
										.getDeviceId());
				_contentValues.put(SSConstants.CORRECTED_ON, getPresentTime());

				if ((scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED
						|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH || scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION)
						|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_7_SCRIPT_MISMATCH_WITH_DB) {
					_contentValues.put(SSConstants.SCRUTINIZE_STATUS,
							SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION);
				} else {
					_contentValues.put(SSConstants.SCRUTINIZE_STATUS,
							SSConstants.SCRUTINY_STATUS_6_SCRIPT_COMPLETED);
				}

				_contentValues.put(SSConstants.IS_CORRECTED, 1);
				_contentValues.put(SSConstants.USER_ID, userId);
				_contentValues.put(SSConstants.GRAND_TOTAL_MARK, _grand_total);
				insertToDB(_contentValues);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				hideProgress();
				switchToShowGrandTotalSummaryTableActivity();
			};

		}.execute();
	}

	private void storeRemarksDB() {
		// TODO Auto-generated method stub

		for (Map.Entry<Integer, String> e : RemarksArray.entrySet()) {
			String remarkOrMark = RemarksArray.get(e.getKey());
			Log.v("key" + e.getKey(), "value" + remarkOrMark);
			Boolean setRemark = true;
			Boolean insertToTempDB = true;
			switch (e.getKey()) {

			case R.id.q1_e:
				setRemarkInContentValue3(SSConstants.M1E_REMARK,
						SSConstants.MARK1E, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q2_e:
				setRemarkInContentValue3(SSConstants.M2E_REMARK,
						SSConstants.MARK2E, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q3_e:
				setRemarkInContentValue3(SSConstants.M3E_REMARK,
						SSConstants.MARK3E, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q4_e:
				setRemarkInContentValue3(SSConstants.M4E_REMARK,
						SSConstants.MARK4E, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q5_e:
				setRemarkInContentValue3(SSConstants.M5E_REMARK,
						SSConstants.MARK5E, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q6_e:
				setRemarkInContentValue3(SSConstants.M6E_REMARK,
						SSConstants.MARK6E, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q7_e:
				setRemarkInContentValue3(SSConstants.M7E_REMARK,
						SSConstants.MARK7E, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q8_e:
				setRemarkInContentValue3(SSConstants.M8E_REMARK,
						SSConstants.MARK8E, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q1_a:
				setRemarkInContentValue3(SSConstants.M1A_REMARK,
						SSConstants.MARK1A, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q1_b:
				setRemarkInContentValue3(SSConstants.M1B_REMARK,
						SSConstants.MARK1B, remarkOrMark, setRemark,
						insertToTempDB);
				break;
			case R.id.q1_c:
				setRemarkInContentValue3(SSConstants.M1C_REMARK,
						SSConstants.MARK1C, remarkOrMark, setRemark,
						insertToTempDB);
				break;
			case R.id.q1_d:
				setRemarkInContentValue3(SSConstants.M1D_REMARK,
						SSConstants.MARK1D, remarkOrMark, setRemark,
						insertToTempDB);
				break;

			case R.id.q2_a:
				setRemarkInContentValue3(SSConstants.M2A_REMARK,
						SSConstants.MARK2A, remarkOrMark, setRemark,
						insertToTempDB);
				break;
			case R.id.q2_b:
				setRemarkInContentValue3(SSConstants.M2B_REMARK,
						SSConstants.MARK2B, remarkOrMark, setRemark,
						insertToTempDB);
				break;
			case R.id.q2_c:
				setRemarkInContentValue3(SSConstants.M2C_REMARK,
						SSConstants.MARK2C, remarkOrMark, setRemark,
						insertToTempDB);
				break;
			case R.id.q2_d:
				setRemarkInContentValue3(SSConstants.M2D_REMARK,
						SSConstants.MARK2D, remarkOrMark, setRemark,
						insertToTempDB);
				break;

			case R.id.q3_a:
				setRemarkInContentValue3(SSConstants.M3A_REMARK,
						SSConstants.MARK3A, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q3_b:
				setRemarkInContentValue3(SSConstants.M3B_REMARK,
						SSConstants.MARK3B, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q3_c:
				setRemarkInContentValue3(SSConstants.M3C_REMARK,
						SSConstants.MARK3C, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q3_d:
				setRemarkInContentValue3(SSConstants.M3D_REMARK,
						SSConstants.MARK3D, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q4_a:
				setRemarkInContentValue3(SSConstants.M4A_REMARK,
						SSConstants.MARK4A, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q4_b:
				setRemarkInContentValue3(SSConstants.M4B_REMARK,
						SSConstants.MARK4B, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q4_c:
				setRemarkInContentValue3(SSConstants.M4C_REMARK,
						SSConstants.MARK4C, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q4_d:
				setRemarkInContentValue3(SSConstants.M4D_REMARK,
						SSConstants.MARK4D, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q5_a:
				setRemarkInContentValue3(SSConstants.M5A_REMARK,
						SSConstants.MARK5A, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q5_b:
				setRemarkInContentValue3(SSConstants.M5B_REMARK,
						SSConstants.MARK5B, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q5_c:
				setRemarkInContentValue3(SSConstants.M5C_REMARK,
						SSConstants.MARK5C, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q5_d:
				setRemarkInContentValue3(SSConstants.M5D_REMARK,
						SSConstants.MARK5D, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q6_a:
				setRemarkInContentValue3(SSConstants.M6A_REMARK,
						SSConstants.MARK6A, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q6_b:
				setRemarkInContentValue3(SSConstants.M6B_REMARK,
						SSConstants.MARK6B, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q6_c:
				setRemarkInContentValue3(SSConstants.M6C_REMARK,
						SSConstants.MARK6C, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q6_d:
				setRemarkInContentValue3(SSConstants.M6D_REMARK,
						SSConstants.MARK6D, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q7_a:
				setRemarkInContentValue3(SSConstants.M7A_REMARK,
						SSConstants.MARK7A, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q7_b:
				setRemarkInContentValue3(SSConstants.M7B_REMARK,
						SSConstants.MARK7B, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q7_c:
				setRemarkInContentValue3(SSConstants.M7C_REMARK,
						SSConstants.MARK7C, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q7_d:
				setRemarkInContentValue3(SSConstants.M7D_REMARK,
						SSConstants.MARK7D, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q8_a:
				setRemarkInContentValue3(SSConstants.M8A_REMARK,
						SSConstants.MARK8A, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q8_b:
				setRemarkInContentValue3(SSConstants.M8B_REMARK,
						SSConstants.MARK8B, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q8_c:
				setRemarkInContentValue3(SSConstants.M8C_REMARK,
						SSConstants.MARK8C, remarkOrMark, setRemark,
						insertToTempDB);

				break;
			case R.id.q8_d:
				setRemarkInContentValue3(SSConstants.M8D_REMARK,
						SSConstants.MARK8D, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q1_total:
				setRemarkInContentValue3(SSConstants.R1_REMARK,
						SSConstants.R1_TOTAL, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q2_total:
				setRemarkInContentValue3(SSConstants.R2_REMARK,
						SSConstants.R2_TOTAL, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q3_total:
				setRemarkInContentValue3(SSConstants.R3_REMARK,
						SSConstants.R3_TOTAL, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q4_total:
				setRemarkInContentValue3(SSConstants.R4_REMARK,
						SSConstants.R4_TOTAL, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q5_total:
				setRemarkInContentValue3(SSConstants.R5_REMARK,
						SSConstants.R5_TOTAL, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q6_total:
				setRemarkInContentValue3(SSConstants.R6_REMARK,
						SSConstants.R6_TOTAL, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q7_total:
				setRemarkInContentValue3(SSConstants.R7_REMARK,
						SSConstants.R7_TOTAL, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.q8_total:
				setRemarkInContentValue3(SSConstants.R8_REMARK,
						SSConstants.R8_TOTAL, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			case R.id.grand_total:
				setRemarkInContentValue3(SSConstants.GRAND_TOTAL_REMARK,
						SSConstants.GRAND_TOTAL_MARK, remarkOrMark, setRemark,
						insertToTempDB);

				break;

			default:
				break;
			}
		}

	}

	// get present time
	private String getPresentTime() {
		// set the date format here
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	// navigate to ShowGrandTotalSummaryTableActivity
	public void switchToShowGrandTotalSummaryTableActivity() {
		setContentValuesOnFinalSubmission();
		Intent intent;
		if (scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED
				|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH) {
			intent = new Intent(this, Scrutiny_AddScriptMarksSummary.class);
			// if (getIntent().hasExtra(SSConstants.TOTAL_SCRIPTS)) {
			// intent.putExtra(SSConstants.TOTAL_SCRIPTS, getIntent()
			// .getStringExtra(SSConstants.TOTAL_SCRIPTS));
			// }
			intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
			intent.putExtra(SSConstants.ANS_BOOK_BARCODE, ansBookBarcode);
			intent.putExtra(SSConstants.BUNDLE_SERIAL_NO, bundle_serial_no);
			intent.putExtra(SSConstants.SUBJECT_CODE, subjectCode);
			intent.putExtra("SeatNo", SSConstants.SeatNo);
			intent.putExtra(SSConstants.USER_ID, userId);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else {
			intent = new Intent(this, Scrutiny_ShowGrandTotalSummaryTable.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
			intent.putExtra(SSConstants.USER_ID, userId);
			intent.putExtra("SeatNo", SSConstants.SeatNo);
			startActivity(intent);
		}

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

	// show alert if he enters less than 5 ques
	private void showAlertForLessthan5() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(getString(R.string.alert_enter_less_than5));
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						// new
						// TempDatabase(MarkDialogCorrection.this).deleteRow();
						dialog.dismiss();
						showAlert(getString(R.string.alert_submit_corr_marks),
								getString(R.string.alert_dialog_ok),
								getString(R.string.alert_dialog_cancel), true);
					}
				});

		myAlertDialog.setNegativeButton(
				getString(R.string.alert_dialog_cancel),
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

	// // check count of corrected questions
	// private int correctedQuesCount() {
	// int count = 0;
	//
	// if (!TextUtils.isEmpty(tv_mark1_total.getText().toString().trim())) {
	// count++;
	// }
	//
	// if (!TextUtils.isEmpty(tv_mark2_total.getText().toString().trim())) {
	// count++;
	// }
	//
	// if (!TextUtils.isEmpty(tv_mark3_total.getText().toString().trim())) {
	// count++;
	// }
	//
	// if (!TextUtils.isEmpty(tv_mark4_total.getText().toString().trim())) {
	// count++;
	// }
	//
	// if (!TextUtils.isEmpty(tv_mark5_total.getText().toString().trim())) {
	// count++;
	// }
	// if (!TextUtils.isEmpty(tv_mark6_total.getText().toString().trim())) {
	// count++;
	// }
	// if (!TextUtils.isEmpty(tv_mark7_total.getText().toString().trim())) {
	// count++;
	// }
	// if (!TextUtils.isEmpty(tv_mark8_total.getText().toString().trim())) {
	// count++;
	// }
	//
	// return count;
	//  
	// }

	// set text to focused view on click
	private void setTextToFocusedView(String text) {  	
		View focusedView = getCurrentFocus();  
		if (focusedView != null && focusedView instanceof EditText) {
			EditText et_focusedView = ((EditText) focusedView);
			// check whether subject code is special case
			//if (is_subject_code_special_case || isRegulation) {
				max_mark = setMaxValueForSubjCodeSpecialCase(et_focusedView);
			//}  

			String prevText = et_focusedView.getText().toString().trim();
			if (!TextUtils.isEmpty(prevText)) {

				if (".".equals(prevText.charAt(prevText.length() - 1))) {  
					return;
				}
			}
			if (TextUtils.isEmpty(prevText)) {
				if (text.equals(".")) {
					et_focusedView.setText("");
					return;
				}

				if (Float.parseFloat(text) > max_mark
						|| !FloatOrIntegerOnlyAllow(text)) {
					if (!text.equals("."))
						alertForInvalidMark(focusedView, true, text);
				} else {

					float _row_total = checkRowTotalExceedsMaxValue(
							et_focusedView, text);
					if (_row_total > max_mark) {
						showAlertTotalExceedsMaxMark(
								getString(R.string.alert_total_exceeds)
										+ " "
										+ +max_mark
										+ " / Sub Marks Exceeds  the Maximum Marks",
								getString(R.string.alert_dialog_ok),
								et_focusedView, "");
					} else {
						// setRemarkInContentValue2(et_focusedView, text, false,
						// false);
						marksArray.put(et_focusedView.getId(), text);
						et_focusedView.setText(text);
						et_focusedView.setSelection(et_focusedView.getText()
								.length());
					}

				}

			} else {

				if (text.equals(".")) {
					et_focusedView.setText(prevText + text);
					et_focusedView.setSelection(et_focusedView.getText()
							.length());
					return;
				}
				if (et_focusedView.getId() == R.id.grand_total) {
					String sss = et_focusedView.getText().toString().trim();
					et_focusedView.setText(sss + "" + text);
					sss = et_focusedView.getText().toString().trim();
					Log.v("enter " + text, "new " + sss);
					if (!TextUtils.isEmpty(sss)) {
						setTotaltoTextView(sss);
					}
					return;
				}
				if (Float.parseFloat(prevText + text) > max_mark
						|| !FloatOrIntegerOnlyAllow(prevText + text)) {
					alertForInvalidMark(focusedView, true, prevText + text);
				} else {
					if (checkRowTotalExceedsMaxValue(et_focusedView, prevText
							+ text) > max_mark) {
						showAlertTotalExceedsMaxMark(
								getString(R.string.alert_total_exceeds)
										+ " "
										+ +max_mark
										+ " / Sub Marks Exceeds  the Maximum Marks",
								getString(R.string.alert_dialog_ok),
								et_focusedView, "");
					} else {
						// setRemarkInContentValue2(et_focusedView, prevText
						// + text, false, false);
						marksArray.put(et_focusedView.getId(), prevText + text);
						et_focusedView.setText(prevText + text);
						et_focusedView.setSelection(et_focusedView.getText()
								.length());
					}
				}
			}
		}
	}

	private void setTotaltoTextView(String sss) {
		// TODO Auto-generated method stub
		Log.v("" + Float.parseFloat(sss),
				"" + Float.parseFloat(maxTotalMark));
		if (Float.parseFloat(sss) > Float.parseFloat(maxTotalMark)) {
			View focusedView = getCurrentFocus();
			if (focusedView != null && focusedView instanceof EditText) {
				alertForInvalidMark(focusedView, true, "Total Exceeds "
						+ maxTotalMark);
			}
		} else {
			// tv_grand_toal.setText(sss);
			// // setRemarkInContentValue2(tv_grand_toal, sss, false, false);
			// marksArray.put(tv_grand_toal.getId(),sss);
			// tv_grand_toal.setSelection(sss.length());
		}

	}

	// when clicked on delete button call this method
	private void deleteCharAndSetSelection(EditText edittext) {
		if (!TextUtils.isEmpty(edittext.getText().toString())) {
			edittext.setText(edittext.getText().toString()
					.substring(0, (edittext.getText().toString().length() - 1)));
			edittext.setSelection(edittext.getText().toString().length());
			// setRemarkInContentValue2(edittext, edittext.getText().toString()
			// .trim(), false, false);
			// marksArray.put(tv_grand_toal.getId(),edittext.getText().toString()
			// .trim());
		}
	}

	// call this method when clicked on Submit button
	private void submit(final View view) {
		storeRemarksDB();
		int orangeColCount = getOrangeRemarkCount();
		if (orangeColCount > 0) {
			showAlertForPendingCorrections(getString(R.string.alert_pend_corr),
					getString(R.string.alert_dialog_ok));
		} else {
			// if ((/*correctedQuesCount() > 4 ||*/
			// is_subject_code_special_case)
			// || (!(scrutinizedStatusInObsMode == 1 ||
			// scrutinizedStatusInObsMode == 2) && !isAddScript)) {
			// new TempDatabase(this).deleteRow();
			showAlert(getString(R.string.alert_submit_corr_marks),
					getString(R.string.alert_dialog_ok),
					getString(R.string.alert_dialog_cancel), true);
			// } else {
			// showAlertForLessthan5();
			// }
		}
	}

	// call this method when cliked on marks cell
	private void onCellClick(String pMark, String pRemark, View pView) {
		Cursor _cursor;
		String _remark = null;
		String _editedMark = null;
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		if (!(scrutinizedStatusInObsMode == 1 || scrutinizedStatusInObsMode == 2)
				&& !isAddScript) {
			_cursor = _db_for_scrutiny.getRowFromTable_Marks(ansBookBarcode,
					new String[] { pMark });
			if (_cursor.getCount() > 0)
				_editedMark = _cursor.getString(_cursor.getColumnIndex(pMark));
			_cursor.close();

			_cursor = _db_for_scrutiny.getRow(ansBookBarcode, null);
			if (_cursor.getCount() > 0)
				_remark = _cursor.getString(_cursor.getColumnIndex(pRemark));

			_cursor.close();

			if (!TextUtils.isEmpty(_remark)) {
				showAcceptAlert(_remark, pView, _editedMark);
			}
		} else {
			editMarksEnterDialog(pView);
		}
	}

	// call this method when cliked on total marks cell
	private void onTotalCellClick(String pMark, String pRemark, View pView) {
		Cursor _cursor;
		String _remark = null;
		String _editedMark = null;
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		_cursor = _db_for_scrutiny.getRowFromTable_Marks(ansBookBarcode,
				new String[] { pMark });
		if (_cursor.getCount() > 0)
			_editedMark = _cursor.getString(_cursor.getColumnIndex(pMark));
		_cursor.close();

		_cursor = _db_for_scrutiny.getRow(ansBookBarcode,
				new String[] { pRemark });
		if (_cursor.getCount() > 0)
			_remark = _cursor.getString(_cursor.getColumnIndex(pRemark));

		_cursor.close();

		if (!TextUtils.isEmpty(_remark)) {
			showAcceptAlert(_remark, pView, _editedMark);
		}
	}

	// on click (widget)
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.q1_e:
			onCellClick(SSConstants.MARK1E, SSConstants.M1E_REMARK, v);
			break;
		case R.id.q2_e:
			onCellClick(SSConstants.MARK2E, SSConstants.M2E_REMARK, v);
			break;
		case R.id.q3_e:
			onCellClick(SSConstants.MARK3E, SSConstants.M3E_REMARK, v);
			break;
		case R.id.q4_e:
			onCellClick(SSConstants.MARK4E, SSConstants.M4E_REMARK, v);
			break;
		case R.id.q5_e:
			onCellClick(SSConstants.MARK5E, SSConstants.M5E_REMARK, v);
			break;
		case R.id.q6_e:
			onCellClick(SSConstants.MARK6E, SSConstants.M6E_REMARK, v);
			break;
		case R.id.q7_e:
			onCellClick(SSConstants.MARK7E, SSConstants.M7E_REMARK, v);
			break;
		case R.id.q8_e:
			onCellClick(SSConstants.MARK8E, SSConstants.M8E_REMARK, v);
			break;

		case R.id.q1_a:
			onCellClick(SSConstants.MARK1A, SSConstants.M1A_REMARK, v);
			break;
		case R.id.q1_b:
			onCellClick(SSConstants.MARK1B, SSConstants.M1B_REMARK, v);
			break;
		case R.id.q1_c:
			onCellClick(SSConstants.MARK1C, SSConstants.M1C_REMARK, v);
			break;
		case R.id.q1_d:
			onCellClick(SSConstants.MARK1D, SSConstants.M1D_REMARK, v);
			break;

		case R.id.q2_a:
			onCellClick(SSConstants.MARK2A, SSConstants.M2A_REMARK, v);
			break;
		case R.id.q2_b:
			onCellClick(SSConstants.MARK2B, SSConstants.M2B_REMARK, v);
			break;
		case R.id.q2_c:
			onCellClick(SSConstants.MARK2C, SSConstants.M2C_REMARK, v);
			break;
		case R.id.q2_d:
			onCellClick(SSConstants.MARK2D, SSConstants.M2D_REMARK, v);
			break;

		case R.id.q3_a:
			onCellClick(SSConstants.MARK3A, SSConstants.M3A_REMARK, v);
			break;
		case R.id.q3_b:
			onCellClick(SSConstants.MARK3B, SSConstants.M3B_REMARK, v);
			break;
		case R.id.q3_c:
			onCellClick(SSConstants.MARK3C, SSConstants.M3C_REMARK, v);
			break;
		case R.id.q3_d:
			onCellClick(SSConstants.MARK3D, SSConstants.M3D_REMARK, v);
			break;

		case R.id.q4_a:
			onCellClick(SSConstants.MARK4A, SSConstants.M4A_REMARK, v);
			break;  
		case R.id.q4_b:
			onCellClick(SSConstants.MARK4B, SSConstants.M4B_REMARK, v);
			break;
		case R.id.q4_c:
			onCellClick(SSConstants.MARK4C, SSConstants.M4C_REMARK, v);
			break;
		case R.id.q4_d:
			onCellClick(SSConstants.MARK4D, SSConstants.M4D_REMARK, v);
			break;

		case R.id.q5_a:
			onCellClick(SSConstants.MARK5A, SSConstants.M5A_REMARK, v);
			break;
		case R.id.q5_b:
			onCellClick(SSConstants.MARK5B, SSConstants.M5B_REMARK, v);
			break;
		case R.id.q5_c:
			onCellClick(SSConstants.MARK5C, SSConstants.M5C_REMARK, v);
			break;
		case R.id.q5_d:
			onCellClick(SSConstants.MARK5D, SSConstants.M5D_REMARK, v);
			break;

		case R.id.q6_a:
			onCellClick(SSConstants.MARK6A, SSConstants.M6A_REMARK, v);
			break;
		case R.id.q6_b:
			onCellClick(SSConstants.MARK6B, SSConstants.M6B_REMARK, v);
			break;
		case R.id.q6_c:
			onCellClick(SSConstants.MARK6C, SSConstants.M6C_REMARK, v);
			break;
		case R.id.q6_d:
			onCellClick(SSConstants.MARK6D, SSConstants.M6D_REMARK, v);
			break;

		case R.id.q7_a:
			onCellClick(SSConstants.MARK7A, SSConstants.M7A_REMARK, v);
			break;
		case R.id.q7_b:
			onCellClick(SSConstants.MARK7B, SSConstants.M7B_REMARK, v);
			break;
		case R.id.q7_c:
			onCellClick(SSConstants.MARK7C, SSConstants.M7C_REMARK, v);
			break;
		case R.id.q7_d:
			onCellClick(SSConstants.MARK7D, SSConstants.M7D_REMARK, v);
			break;

		case R.id.q8_a:
			onCellClick(SSConstants.MARK8A, SSConstants.M8A_REMARK, v);
			break;
		case R.id.q8_b:
			onCellClick(SSConstants.MARK8B, SSConstants.M8B_REMARK, v);
			break;
		case R.id.q8_c:
			onCellClick(SSConstants.MARK8C, SSConstants.M8C_REMARK, v);
			break;
		case R.id.q8_d:
			onCellClick(SSConstants.MARK8D, SSConstants.M8D_REMARK, v);
			break;

		case R.id.q1_total:
			onTotalCellClick(SSConstants.R1_TOTAL, SSConstants.R1_REMARK, v);
			break;

		case R.id.q2_total:
			onTotalCellClick(SSConstants.R2_TOTAL, SSConstants.R2_REMARK, v);
			break;

		case R.id.q3_total:
			onTotalCellClick(SSConstants.R3_TOTAL, SSConstants.R3_REMARK, v);
			break;

		case R.id.q4_total:
			onTotalCellClick(SSConstants.R4_TOTAL, SSConstants.R4_REMARK, v);
			break;

		case R.id.q5_total:
			onTotalCellClick(SSConstants.R5_TOTAL, SSConstants.R5_REMARK, v);
			break;

		case R.id.q6_total:
			onTotalCellClick(SSConstants.R6_TOTAL, SSConstants.R6_REMARK, v);
			break;

		case R.id.q7_total:
			onTotalCellClick(SSConstants.R7_TOTAL, SSConstants.R7_REMARK, v);
			break;

		case R.id.q8_total:
			onTotalCellClick(SSConstants.R8_TOTAL, SSConstants.R8_REMARK, v);
			break;

		// case R.id.grand_total:
		// onTotalCellClick(SSConstants.GRAND_TOTAL_MARK,
		// SSConstants.GRAND_TOTAL_REMARK, v);
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
				// setRemarkInContentValue2((EditText) focusedView, "", false,
				// false);
				marksArray.put(focusedView.getId(), "");
			}
			break;

		case R.id.btn_delete:
			View focusedView2 = getCurrentFocus();
			if (focusedView2 != null && focusedView2 instanceof EditText) {
				deleteCharAndSetSelection((EditText) focusedView2);
			}
			break;

		case R.id.btn_submit:
			// set true since number layout is inflated/attached
			// submit();
			// if(!getRemarkComplete()){
			calculateGrandTotal();
			// if (!setSampleRemark1(tv_grand_toal)){
			submit(v);
			// }else{
			// Toast.makeText(Scrutiny_MarkDialogCorrection.this,
			// "Please calculate Best of 5 before Submit...!",
			// Toast.LENGTH_LONG).show();
			// alertMessageForAnyChange(getString(R.string.app_name),getString(R.string.grand_mis_match),false);
			// }
			// }else{
			// alertMessageForAnyChange(getString(R.string.app_name),getString(R.string.mis_match),false);
			// }
			break;
		case R.id.btn_submit1:
			// set false since number layout is not inflated/attached
			// submit();
			// if(!getRemarkComplete()){
			// if (!setSampleRemark1(tv_grand_toal)){
			submit(v);
			// }else{
			// Toast.makeText(Scrutiny_MarkDialogCorrection.this,
			// "Please calculate Best of 5 before Submit...!",
			// Toast.LENGTH_LONG).show();
			// alertMessageForAnyChange(getString(R.string.app_name),getString(R.string.grand_mis_match),false);
			// }
			// }else{
			// alertMessageForAnyChange(getString(R.string.app_name),getString(R.string.mis_match),false);
			// }
			break;

		default:
			break;
		}
	}

	protected Boolean setSampleRemark1(EditText mEditText) {
		Boolean check = false;

		String text = "" + mEditText.getText().toString().trim();
		String tag = "" + mEditText.getTag().toString().trim();
		Log.v("text grand " + text, "tag " + tag);
		// mEditText.clearFocus();
		// tv_grand_toal.requestFocus();
		if (text.equals("") && tag.equals("")) {
		} else if (text.equals("") && !tag.equals("")) {
			mEditText.setText("");
			check = true;
		} else if (!text.equals("") && tag.equals("")) {
			mEditText.setText("");
			check = true;
		} else if (!text.equals("") && !tag.equals("")) {
			if (!text.equals(tag)) {
				mEditText.setText("");
				check = true;
			}
		}

		return check;

	}

	// private boolean getRemarkComplete() {
	// // TODO Auto-generated method
	//
	// Boolean checkRemark = false;
	// if (setSampleRemark(tv_mark1_total))
	// checkRemark = true;
	// if (setSampleRemark(tv_mark2_total))
	// checkRemark = true;
	// if (setSampleRemark(tv_mark3_total))
	// checkRemark = true;
	// if (setSampleRemark(tv_mark4_total))
	// checkRemark = true;
	// if (setSampleRemark(tv_mark5_total))
	// checkRemark = true;
	// if (setSampleRemark(tv_mark6_total))
	// checkRemark = true;
	// if (setSampleRemark(tv_mark7_total))
	// checkRemark = true;
	// if (setSampleRemark(tv_mark8_total))
	// checkRemark = true;
	//
	//
	// return checkRemark;
	// }

	protected Boolean setSampleRemark(EditText mEditText) {
		Boolean check = false;
		if (mEditText.isFocusable()) {
			String text = "" + mEditText.getText().toString().trim();
			String tag = "" + mEditText.getTag().toString().trim();

			if (!TextUtils.isEmpty(text))
				text = "" + Float.parseFloat(text);
			if (!TextUtils.isEmpty(tag))
				tag = "" + Float.parseFloat(tag);
			// tv_grand_toal.requestFocus();
			Log.v("text " + text, "tag " + tag);
			if (text.equals("") && tag.equals("")) {
			} else if (text.equals("") && !tag.equals("")) {
				mEditText.setError("");
				check = true;
			} else if (!text.equals("") && tag.equals("")) {
				mEditText.setError("");
				check = true;
			} else if (!text.equals("") && !tag.equals("")) {
				if (!text.equals(tag)) {
					mEditText.setError("");
					check = true;
				}
			}
			mEditText.clearFocus();
		} else {

		}
		return check;

	}

	// show accept or cancel alert
	private void showAcceptAlert(String msg, final View view,
			final String editMark) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(msg);

		myAlertDialog.setPositiveButton("Accept",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						acceptConfirmationAlert(view, editMark);

					}
				});

		myAlertDialog.setNegativeButton("Reject",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						acceptRejectAlert(view, editMark);
					}
				});
		myAlertDialog.show();
	}

	// show reject confirmation alert
	private void acceptRejectAlert(final View view, final String editMark) {
	//	***if (is_subject_code_special_case || isRegulation) {
			max_mark = setMaxValueForSubjCodeSpecialCase(((EditText) view));
	//	}
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_reject_change));

		myAlertDialog.setPositiveButton(
				getResources().getString(R.string.alert_yes),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						if (!TextUtils.isEmpty(editMark)) {
							float val = checkRowTotalExceedsMaxValue(view,
									editMark);
							// check whether sub total exceeds max marks
							// if (val ==
							// SUB_TOTAL_EXCEEDS_MAX_MARKS_FOR_SPECIAL_CASE_SUBJ_CODES)
							// {
							// alertForInvalidMark(view, false, "");
							// }
							// check whether row total exceeds max marks
							if (val > max_mark) {

								showAlertTotalExceedOnRejectClick(
										"Exceeds Total Marks.Please edit the row marks you entered previously and then Reject the marks",
										"Ok");
							}

							else {
								changeCellBGToRed(view);
								if (!checkView(view)) {
									((TextView) view).setText(editMark);
								}
								// setRemarkInContentValue2(view,
								// SSConstants.RED_COLOR, true, true);
								RemarksArray.put(view.getId(),
										SSConstants.RED_COLOR);
							}
						} else {
							changeCellBGToRed(view);
							if (!checkView(view)) {
								((TextView) view).setText(editMark);
							}
							// setRemarkInContentValue2(view,
							// SSConstants.RED_COLOR, true, true);
							RemarksArray.put(view.getId(),
									SSConstants.RED_COLOR);
						}

					}
				});

		myAlertDialog.setNegativeButton(
				getResources().getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		myAlertDialog.show();
	}

	// show accept confirmation alert
	private void acceptConfirmationAlert(final View view, final String editMark) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_accept_change));

		myAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();

						if (checkView(view)) {
							// setRemarkInContentValue2(view,
							// SSConstants.GREEN_COLOR, true, true);
							RemarksArray.put(view.getId(),
									SSConstants.GREEN_COLOR);
							changeCellBGToGreen(view);
						} else {
							editMarksEnterDialog(view);
						}

					}
				});

		myAlertDialog.setNegativeButton(
				getResources().getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
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

	private void setRemarkInContentValue3(String CONS_REMARK, String CONS_MARK,
			final String remarkOrMark, boolean setRemark, boolean insertToTempDB) {
		ContentValues _contentValues = new ContentValues();
		if (setRemark) {
			_contentValues.put(CONS_REMARK, remarkOrMark);
		} else {
			_contentValues.put(CONS_MARK, remarkOrMark);
		}

		if (insertToTempDB) {
			new Scrutiny_TempDatabase(this).updateRow(_contentValues);

		} else {
			insertToDB(_contentValues);
		}
	}

	// set mark or remark
	private void setRemarkInContentValue2(View view, final String remarkOrMark,
			boolean setRemark, boolean insertToTempDB) {
		switch (view.getId()) {

		case R.id.q1_e:
			setRemarkInContentValue3(SSConstants.M1E_REMARK,
					SSConstants.MARK1E, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q2_e:
			setRemarkInContentValue3(SSConstants.M2E_REMARK,
					SSConstants.MARK2E, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q3_e:
			setRemarkInContentValue3(SSConstants.M3E_REMARK,
					SSConstants.MARK3E, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q4_e:
			setRemarkInContentValue3(SSConstants.M4E_REMARK,
					SSConstants.MARK4E, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q5_e:
			setRemarkInContentValue3(SSConstants.M5E_REMARK,
					SSConstants.MARK5E, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q6_e:
			setRemarkInContentValue3(SSConstants.M6E_REMARK,
					SSConstants.MARK6E, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q7_e:
			setRemarkInContentValue3(SSConstants.M7E_REMARK,
					SSConstants.MARK7E, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q8_e:
			setRemarkInContentValue3(SSConstants.M8E_REMARK,
					SSConstants.MARK8E, remarkOrMark, setRemark, insertToTempDB);

			break;

		case R.id.q1_a:
			setRemarkInContentValue3(SSConstants.M1A_REMARK,
					SSConstants.MARK1A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q1_b:
			setRemarkInContentValue3(SSConstants.M1B_REMARK,
					SSConstants.MARK1B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_c:
			setRemarkInContentValue3(SSConstants.M1C_REMARK,
					SSConstants.MARK1C, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_d:
			setRemarkInContentValue3(SSConstants.M1D_REMARK,
					SSConstants.MARK1D, remarkOrMark, setRemark, insertToTempDB);
			break;

		case R.id.q2_a:
			setRemarkInContentValue3(SSConstants.M2A_REMARK,
					SSConstants.MARK2A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q2_b:
			setRemarkInContentValue3(SSConstants.M2B_REMARK,
					SSConstants.MARK2B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q2_c:
			setRemarkInContentValue3(SSConstants.M2C_REMARK,
					SSConstants.MARK2C, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q2_d:
			setRemarkInContentValue3(SSConstants.M2D_REMARK,
					SSConstants.MARK2D, remarkOrMark, setRemark, insertToTempDB);
			break;

		case R.id.q3_a:
			setRemarkInContentValue3(SSConstants.M3A_REMARK,
					SSConstants.MARK3A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q3_b:
			setRemarkInContentValue3(SSConstants.M3B_REMARK,
					SSConstants.MARK3B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q3_c:
			setRemarkInContentValue3(SSConstants.M3C_REMARK,
					SSConstants.MARK3C, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q3_d:
			setRemarkInContentValue3(SSConstants.M3D_REMARK,
					SSConstants.MARK3D, remarkOrMark, setRemark, insertToTempDB);

			break;

		case R.id.q4_a:
			setRemarkInContentValue3(SSConstants.M4A_REMARK,
					SSConstants.MARK4A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q4_b:
			setRemarkInContentValue3(SSConstants.M4B_REMARK,
					SSConstants.MARK4B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q4_c:
			setRemarkInContentValue3(SSConstants.M4C_REMARK,
					SSConstants.MARK4C, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q4_d:
			setRemarkInContentValue3(SSConstants.M4D_REMARK,
					SSConstants.MARK4D, remarkOrMark, setRemark, insertToTempDB);

			break;

		case R.id.q5_a:
			setRemarkInContentValue3(SSConstants.M5A_REMARK,
					SSConstants.MARK5A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q5_b:
			setRemarkInContentValue3(SSConstants.M5B_REMARK,
					SSConstants.MARK5B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q5_c:
			setRemarkInContentValue3(SSConstants.M5C_REMARK,
					SSConstants.MARK5C, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q5_d:
			setRemarkInContentValue3(SSConstants.M5D_REMARK,
					SSConstants.MARK5D, remarkOrMark, setRemark, insertToTempDB);

			break;

		case R.id.q6_a:
			setRemarkInContentValue3(SSConstants.M6A_REMARK,
					SSConstants.MARK6A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q6_b:
			setRemarkInContentValue3(SSConstants.M6B_REMARK,
					SSConstants.MARK6B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q6_c:
			setRemarkInContentValue3(SSConstants.M6C_REMARK,
					SSConstants.MARK6C, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q6_d:
			setRemarkInContentValue3(SSConstants.M6D_REMARK,
					SSConstants.MARK6D, remarkOrMark, setRemark, insertToTempDB);

			break;

		case R.id.q7_a:
			setRemarkInContentValue3(SSConstants.M7A_REMARK,
					SSConstants.MARK7A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q7_b:
			setRemarkInContentValue3(SSConstants.M7B_REMARK,
					SSConstants.MARK7B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q7_c:
			setRemarkInContentValue3(SSConstants.M7C_REMARK,
					SSConstants.MARK7C, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q7_d:
			setRemarkInContentValue3(SSConstants.M7D_REMARK,
					SSConstants.MARK7D, remarkOrMark, setRemark, insertToTempDB);

			break;

		case R.id.q8_a:
			setRemarkInContentValue3(SSConstants.M8A_REMARK,
					SSConstants.MARK8A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q8_b:
			setRemarkInContentValue3(SSConstants.M8B_REMARK,
					SSConstants.MARK8B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q8_c:
			setRemarkInContentValue3(SSConstants.M8C_REMARK,
					SSConstants.MARK8C, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q8_d:
			setRemarkInContentValue3(SSConstants.M8D_REMARK,
					SSConstants.MARK8D, remarkOrMark, setRemark, insertToTempDB);

			break;

		case R.id.q1_total:
			setRemarkInContentValue3(SSConstants.R1_REMARK,
					SSConstants.R1_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q2_total:
			setRemarkInContentValue3(SSConstants.R2_REMARK,
					SSConstants.R2_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q3_total:
			setRemarkInContentValue3(SSConstants.R3_REMARK,
					SSConstants.R3_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q4_total:
			setRemarkInContentValue3(SSConstants.R4_REMARK,
					SSConstants.R4_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q5_total:
			setRemarkInContentValue3(SSConstants.R5_REMARK,
					SSConstants.R5_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q6_total:
			setRemarkInContentValue3(SSConstants.R6_REMARK,
					SSConstants.R6_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q7_total:
			setRemarkInContentValue3(SSConstants.R7_REMARK,
					SSConstants.R7_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q8_total:
			setRemarkInContentValue3(SSConstants.R8_REMARK,
					SSConstants.R8_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.grand_total:
			setRemarkInContentValue3(SSConstants.GRAND_TOTAL_REMARK,
					SSConstants.GRAND_TOTAL_MARK, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		default:
			break;
		}

	}

	private void alertForInvalidMark(final View view2,
			final boolean fromNumbersLayout, final String mark) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_valid_marks));
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
						if (!fromNumbersLayout) {
							editMarksEnterDialog(view2);
						} else {
							((EditText) view2).setText("");
						}

					}
				});

		myAlertDialog.show();
	}

	// marks enter dialog
	private void editMarksEnterDialog(final View view2) {
		final String _prevMarks = ((EditText) view2).getText().toString()
				.trim();
		//***if (is_subject_code_special_case || isRegulation) {
			max_mark = setMaxValueForSubjCodeSpecialCase(((EditText) view2));
		//}
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_update_marks));
		View view = LayoutInflater.from(this).inflate(
				R.layout.scrutiny_layout_alert_dialog_remarks_show, null);

		final EditText etMarks = (EditText) view
				.findViewById(R.id.editText_remarks);
		// allows only number
		// etMarks.setInputType(2);
		// max characters two
		etMarks.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
		myAlertDialog.setView(view);

		myAlertDialog.setPositiveButton(
				getResources().getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						String editMarks = etMarks.getText().toString().trim();
						if (!TextUtils.isEmpty(editMarks)) {
							if (editMarks.equals(".")) {
								editMarks = "";
							}
							// --------------------------------------------------------------------------------
							// check for sub ques exceeds max marks for special
							// cases
							if (TextUtils.isEmpty(editMarks)
									|| Float.parseFloat(editMarks) > max_mark
									|| !FloatOrIntegerOnlyAllow(editMarks)) {
								alertForInvalidMark(view2, false, "");
							} else {

								float val = checkRowTotalExceedsMaxValue(view2,
										editMarks);
								// check whether sub total exceeds max marks
								if (val == SUB_TOTAL_EXCEEDS_MAX_MARKS_FOR_SPECIAL_CASE_SUBJ_CODES) {
									alertForInvalidMark(view2, false, "");
								}
								// check whether row total exceeds max marks
								else if (val > max_mark) {
									showAlertTotalExceedsMaxMark(
											getString(R.string.alert_total_exceeds)
													+ " " + max_mark,
											getString(R.string.alert_dialog_ok),
											(EditText) view2, _prevMarks);
								} else {
									TextView tv = (TextView) view2;
									if (!(scrutinizedStatusInObsMode == 1 || scrutinizedStatusInObsMode == 2)) {
										changeCellBGToGreen(view2);
									}
									// setRemarkInContentValue2(view2,
									// SSConstants.GREEN_COLOR, true, true);
									RemarksArray.put(view2.getId(),
											SSConstants.GREEN_COLOR);
									// setRemarkInContentValue2(tv, editMarks,
									// false,
									// false);
									marksArray.put(tv.getId(), editMarks);
									tv.setText(editMarks);

									// getTotalValue(tv.getId());
								}
							}
						} else {
							TextView tv = (TextView) view2;
							if (!(scrutinizedStatusInObsMode == 1 || scrutinizedStatusInObsMode == 2)) {
								changeCellBGToGreen(view2);
							}
							// setRemarkInContentValue2(view2,
							// SSConstants.GREEN_COLOR, true, true);
							RemarksArray.put(view2.getId(),
									SSConstants.GREEN_COLOR);
							// setRemarkInContentValue2(tv, editMarks, false,
							// false);
							marksArray.put(tv.getId(), editMarks);
							tv.setText(editMarks);
							// getTotalValue(tv.getId());
						}

						dialog.dismiss();

					}
				});

		myAlertDialog.setNegativeButton(
				getResources().getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		myAlertDialog.show();

	}

	/*
	 * protected void getTotalValue(int id) { // TODO Auto-generated method stub
	 * tv_grand_toal.setFocusable(true);
	 * tv_grand_toal.setFocusableInTouchMode(true);
	 * 
	 * if(id == R.id.q1_a || id == R.id.q1_b || id == R.id.q1_c || id ==
	 * R.id.q1_d || id == R.id.q1_e){ tv_mark1_total.setText("");
	 * tv_mark1_total.setFocusable(true);
	 * tv_mark1_total.setFocusableInTouchMode(true);
	 * tv_mark1_total.requestFocus(); // alertRowEntry(); please enter 1nd row
	 * total
	 * 
	 * }
	 * 
	 * else if(id == R.id.q2_a || id == R.id.q2_b || id == R.id.q2_c || id ==
	 * R.id.q2_d || id == R.id.q2_e){ tv_mark2_total.setText("");
	 * tv_mark2_total.setFocusable(true);
	 * tv_mark2_total.setFocusableInTouchMode(true);
	 * tv_mark2_total.requestFocus(); // alertRowEntry(); please enter 2nd row
	 * total
	 * 
	 * 
	 * } else if(id == R.id.q3_a || id == R.id.q3_b || id == R.id.q3_c || id ==
	 * R.id.q3_d || id == R.id.q3_e){ tv_mark3_total.setText("");
	 * tv_mark3_total.setFocusable(true);
	 * tv_mark3_total.setFocusableInTouchMode(true);
	 * tv_mark3_total.requestFocus(); // alertRowEntry(); please enter 3nd row
	 * total
	 * 
	 * 
	 * } else if(id == R.id.q4_a || id == R.id.q4_b || id == R.id.q4_c || id ==
	 * R.id.q4_d || id == R.id.q4_e){ tv_mark4_total.setText("");
	 * tv_mark4_total.setFocusable(true);
	 * tv_mark4_total.setFocusableInTouchMode(true);
	 * tv_mark4_total.requestFocus(); // alertRowEntry(); please enter 4nd row
	 * total
	 * 
	 * 
	 * }
	 * 
	 * else if(id == R.id.q5_a || id == R.id.q5_b || id == R.id.q5_c || id ==
	 * R.id.q5_d || id == R.id.q5_e){
	 * 
	 * tv_mark5_total.setText(""); tv_mark5_total.setFocusable(true);
	 * tv_mark5_total.setFocusableInTouchMode(true);
	 * tv_mark5_total.requestFocus(); // alertRowEntry(); please enter 2nd row
	 * total
	 * 
	 * }
	 * 
	 * else if(id == R.id.q6_a || id == R.id.q6_b || id == R.id.q6_c || id ==
	 * R.id.q6_d || id == R.id.q6_e){ tv_mark6_total.setText("");
	 * tv_mark6_total.setFocusable(true);
	 * tv_mark6_total.setFocusableInTouchMode(true);
	 * tv_mark6_total.requestFocus(); // alertRowEntry(); please enter 6nd row
	 * total
	 * 
	 * }
	 * 
	 * else if(id == R.id.q7_a || id == R.id.q7_b || id == R.id.q7_c || id ==
	 * R.id.q7_d || id == R.id.q7_e){ tv_mark7_total.setText("");
	 * tv_mark7_total.setFocusable(true);
	 * tv_mark7_total.setFocusableInTouchMode(true);
	 * tv_mark7_total.requestFocus(); // alertRowEntry(); please enter 7nd row
	 * total }
	 * 
	 * else if(id == R.id.q8_a || id == R.id.q8_b || id == R.id.q8_c || id ==
	 * R.id.q8_d || id == R.id.q8_e){ tv_mark8_total.setText("");
	 * tv_mark8_total.setFocusable(true);
	 * tv_mark8_total.setFocusableInTouchMode(true);
	 * tv_mark8_total.requestFocus(); // alertRowEntry(); please enter 8nd row
	 * total
	 * 
	 * } }
	 */

	private void alertMessageForAnyChange(String title, String msg,
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
						Dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	// insert valuesto DB
	private void insertToDB(ContentValues pContentValues) {
		pContentValues.put(SSConstants.SUBJECT_CODE, subjectCode);
		pContentValues.put(SSConstants.USER_ID, userId);
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		_db_for_scrutiny.updateRow(SSConstants.TABLE_SCRUTINY_SAVE,
				pContentValues, SSConstants.BUNDLE_SERIAL_NO + " = '"
						+ bundle_serial_no + "' AND " + SSConstants.BUNDLE_NO
						+ " = '" + bundleNo + "' AND "
						+ SSConstants.ANS_BOOK_BARCODE + "= '" + ansBookBarcode
						+ "'");
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
				et_focusedView.removeTextChangedListener(Scrutiny_MarkDialogCorrection_R13_BTech_SpecialCase_New.this);
				et_focusedView.setText("");
				et_focusedView.addTextChangedListener(Scrutiny_MarkDialogCorrection_R13_BTech_SpecialCase_New.this);
			}
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			clickFind=false;  
		
	  
		// calculate total and grand total
		calculateTotal();
		calculateGrandTotal();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	private void calculateTotal() {
		row1Total_();
		row_2_Total_();  
		row_3_Total_();
		row_4_Total_();
		row_5_Total_();
	//	row_6_Total_();
	//	row_7_Total_();
	//	row_8_Total_();

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

			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(""
						+ RowTotalLimit)) {
					Toast.makeText(this, "Row Total Exceeds " + RowTotalLimit,
							Toast.LENGTH_SHORT).show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true, " "
								+ RowTotalLimit);
					}
				} else {
					_mark1_total = mark;
				}
			} else {
				_mark1_total = null;
			}

			return mark;
		}

		_mark1_total = mark;
		return mark;
	}

	private String row_2_Total_() {
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

			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(""
						+ RowTotalLimit)) {
					Toast.makeText(this, "Row Total Exceeds " + RowTotalLimit,
							Toast.LENGTH_SHORT).show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true, " "
								+ RowTotalLimit);
					}
				} else {
					_mark2_total = mark;
				}
			} else {
				_mark2_total = null;
			}

			return mark;
		}

		_mark2_total = mark;
		return mark;
	}

	private String row_3_Total_() {
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

			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(""
						+ RowTotalLimit)) {
					Toast.makeText(this, "Row Total Exceeds " + RowTotalLimit,
							Toast.LENGTH_SHORT).show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true, " "
								+ RowTotalLimit);
					}
				} else {
					_mark3_total = mark;
				}
			} else {
				_mark3_total = null;
			}

			return mark;
		}

		_mark3_total = mark;
		return mark;
	}

	private String row_4_Total_() {
		String mark = null;
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

			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(""
						+ RowTotalLimit)) {
					Toast.makeText(this, "Row Total Exceeds " + RowTotalLimit,
							Toast.LENGTH_SHORT).show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true, " "
								+ RowTotalLimit);
					}
				} else {
					_mark4_total = mark;
				}
			} else {
				_mark4_total = null;
			}

			return mark;
		}

		_mark4_total = mark;
		return mark;
	}

	private String row_5_Total_() {
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

		

		if (!TextUtils.isEmpty(mark5a) || !TextUtils.isEmpty(mark5b)
				|| !TextUtils.isEmpty(mark5c) || !TextUtils.isEmpty(mark5d)
				) {

			mark = String
					.valueOf(Float.parseFloat(((TextUtils
							.isEmpty(mark = mark5a))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark5b))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark5c))) ? "0" : mark)
							+ Float.parseFloat(((TextUtils
									.isEmpty(mark = mark5d))) ? "0" : mark)
							);

			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(""
						+ maxTotalMark_5)) {
					Toast.makeText(this, "Row Total Exceeds " + maxTotalMark_5,
							Toast.LENGTH_SHORT).show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true, " "
								+ maxTotalMark_5);
					}
				} else {
					_mark5_total = mark;
				}
			} else {
				_mark5_total = null;
			}

			return mark;
		}

		_mark5_total = mark;
		return mark;
	}

	private String row_6_Total_() {
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

			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(""
						+ RowTotalLimit)) {
					Toast.makeText(this, "Row Total Exceeds " + RowTotalLimit,
							Toast.LENGTH_SHORT).show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true, " "
								+ RowTotalLimit);
					}
				} else {
					_mark6_total = mark;
				}
			} else {
				_mark6_total = null;
			}

			return mark;
		}

		_mark6_total = mark;
		return mark;
	}

	private String row_7_Total_() {
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

			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(""
						+ RowTotalLimit)) {
					Toast.makeText(this, "Row Total Exceeds " + RowTotalLimit,
							Toast.LENGTH_SHORT).show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true, " "
								+ RowTotalLimit);
					}
				} else {
					_mark7_total = mark;
				}
			} else {
				_mark7_total = null;
			}

			return mark;
		}

		_mark7_total = mark;
		return mark;
	}

	private String row_8_Total_() {
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

			if (!TextUtils.isEmpty(mark)) {
				if (Float.parseFloat(mark) > Float.parseFloat(""
						+ RowTotalLimit)) {
					Toast.makeText(this, "Row Total Exceeds " + RowTotalLimit,
							Toast.LENGTH_SHORT).show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {
						alertForInvalidMark(focusedView, true, " "
								+ RowTotalLimit);
					}
				} else {
					_mark8_total = mark;
				}
			} else {
				_mark8_total = null;
			}

			return mark;
		}

		_mark8_total = mark;
		return mark;
	}

	private void showAlertTotalExceedsMaxMark(String msg, String positiveStr,
			final EditText editText, final String prevMarks) {
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
						// editText.setFocusableInTouchMode(true);
						// editText.setFocusable(true);
						if (TextUtils.isEmpty(prevMarks)) {
							editText.setText("");
						} else {
							editText.setText(prevMarks);
						}
						dialog.dismiss();

					}
				});

		// myAlertDialog.setNegativeButton(negativeStr,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int arg1) {
		// // do something when the OK button is
		// // clicked
		// dialog.dismiss();
		// }
		// });

		myAlertDialog.show();
	}

	private void showAlertTotalExceedOnRejectClick(String msg,
			String positiveStr) {
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
						// editText.setFocusableInTouchMode(true);
						// editText.setFocusable(true);
						dialog.dismiss();

					}
				});

		// myAlertDialog.setNegativeButton(negativeStr,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int arg1) {
		// // do something when the OK button is
		// // clicked
		// dialog.dismiss();
		// }
		// });

		myAlertDialog.show();
	}

	private void showAlertForPendingCorrections(String msg, String positiveStr) {
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

		// myAlertDialog.setNegativeButton(negativeStr,
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int arg1) {
		// // do something when the OK button is
		// // clicked
		// dialog.dismiss();
		// }
		// });

		myAlertDialog.show();
	}

	private int getOrangeRemarkCount() {

		String where = Scrutiny_TempDatabase._SNo + "= '1' AND ("
				+ SSConstants.M1A_REMARK + "  = '1'  OR "
				+ SSConstants.M1B_REMARK + "  = '1'  OR "
				+ SSConstants.M1C_REMARK + "  = '1'  OR "
				+ SSConstants.M1D_REMARK + "  = '1'  OR "
				+ SSConstants.M1E_REMARK + "  = '1'  OR "
				+ SSConstants.M2E_REMARK + "  = '1'  OR "
				+ SSConstants.M3E_REMARK + "  = '1'  OR "
				+ SSConstants.M4E_REMARK + "  = '1'  OR "
				+ SSConstants.M5E_REMARK + "  = '1'  OR "
				+ SSConstants.M6E_REMARK + "  = '1'  OR "
				+ SSConstants.M7E_REMARK + "  = '1'  OR "
				+ SSConstants.M8E_REMARK + "  = '1'  OR " +

				SSConstants.R1_REMARK + "  = '1'  OR " + SSConstants.R2_REMARK
				+ "  = '1'  OR " + SSConstants.R3_REMARK + "  = '1'  OR "
				+ SSConstants.R4_REMARK + "  = '1'  OR " +

				SSConstants.R5_REMARK + "  = '1'  OR " + SSConstants.R6_REMARK
				+ "  = '1'  OR " + SSConstants.R7_REMARK + "  = '1'  OR "
				+ SSConstants.R8_REMARK + "  = '1'  OR " +

				SSConstants.M2A_REMARK + "  = '1'  OR "
				+ SSConstants.M2B_REMARK + "  = '1'  OR "
				+ SSConstants.M2C_REMARK + "  = '1'  OR "
				+ SSConstants.M2D_REMARK + "  = '1'  OR " +

				SSConstants.M3A_REMARK + "  = '1'  OR "
				+ SSConstants.M3B_REMARK + "  = '1'  OR "
				+ SSConstants.M3C_REMARK + "  = '1'  OR "
				+ SSConstants.M3D_REMARK + "  = '1'  OR " +

				SSConstants.M4A_REMARK + "  = '1'  OR "
				+ SSConstants.M4B_REMARK + "  = '1'  OR "
				+ SSConstants.M4C_REMARK + "  = '1'  OR "
				+ SSConstants.M4D_REMARK + "  = '1'  OR " +

				SSConstants.M5A_REMARK + "  = '1'  OR "
				+ SSConstants.M5B_REMARK + "  = '1'  OR "
				+ SSConstants.M5C_REMARK + "  = '1'  OR "
				+ SSConstants.M5D_REMARK
				+ "  = '1'  OR "
				+

				SSConstants.M6A_REMARK
				+ "  = '1'  OR "
				+ SSConstants.M6B_REMARK
				+ "  = '1'  OR "
				+ SSConstants.M6C_REMARK
				+ "  = '1'  OR "
				+ SSConstants.M6D_REMARK
				+ "  = '1'  OR "
				+

				SSConstants.M7A_REMARK
				+ "  = '1'  OR "
				+ SSConstants.M7B_REMARK
				+ "  = '1'  OR "
				+ SSConstants.M7C_REMARK
				+ "  = '1'  OR "
				+ SSConstants.M7D_REMARK
				+ "  = '1'  OR "
				+

				// SSConstants.R1_REMARK + "  = '1'  OR "
				// + SSConstants.M7B_REMARK + "  = '1'  OR "
				// + SSConstants.M7C_REMARK + "  = '1'  OR "
				// + SSConstants.M7D_REMARK + "  = '1'  OR " +

				SSConstants.M8A_REMARK + "  = '1'  OR "
				+ SSConstants.M8B_REMARK + "  = '1'  OR "
				+ SSConstants.M8C_REMARK + "  = '1'  OR "
				+ SSConstants.M8D_REMARK + "  = '1'  )";

		Cursor _cursor = new Scrutiny_TempDatabase(this).getRow(where);
		int count = _cursor.getCount();
		_cursor.close();
		return count;
	}

	private void setContentValuesOnFinalSubmission() {
		String mark;
		ContentValues _contentValues = new ContentValues();

		// check whether text is empty if so set to null
		_contentValues.put(
				SSConstants.MARK1E,
				((TextUtils.isEmpty(mark = tv_mark1e.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK2E,
				((TextUtils.isEmpty(mark = tv_mark2e.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK3E,
				((TextUtils.isEmpty(mark = tv_mark3e.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK4E,
				((TextUtils.isEmpty(mark = tv_mark4e.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK5E,
				((TextUtils.isEmpty(mark = tv_mark5e.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK6E,
				((TextUtils.isEmpty(mark = tv_mark6e.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK7E,
				((TextUtils.isEmpty(mark = tv_mark7e.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK8E,
				((TextUtils.isEmpty(mark = tv_mark8e.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK1A,
				((TextUtils.isEmpty(mark = tv_mark1a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK1B,
				((TextUtils.isEmpty(mark = tv_mark1b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK1C,
				((TextUtils.isEmpty(mark = tv_mark1c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK1D,
				((TextUtils.isEmpty(mark = tv_mark1d.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK2A,
				((TextUtils.isEmpty(mark = tv_mark2a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK2B,
				((TextUtils.isEmpty(mark = tv_mark2b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK2C,
				((TextUtils.isEmpty(mark = tv_mark2c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK2D,
				((TextUtils.isEmpty(mark = tv_mark2d.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK3A,
				((TextUtils.isEmpty(mark = tv_mark3a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK3B,
				((TextUtils.isEmpty(mark = tv_mark3b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK3C,
				((TextUtils.isEmpty(mark = tv_mark3c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK3D,
				((TextUtils.isEmpty(mark = tv_mark3d.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK4A,
				((TextUtils.isEmpty(mark = tv_mark4a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK4B,
				((TextUtils.isEmpty(mark = tv_mark4b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK4C,
				((TextUtils.isEmpty(mark = tv_mark4c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK4D,
				((TextUtils.isEmpty(mark = tv_mark4d.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK5A,
				((TextUtils.isEmpty(mark = tv_mark5a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK5B,
				((TextUtils.isEmpty(mark = tv_mark5b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK5C,
				((TextUtils.isEmpty(mark = tv_mark5c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK5D,
				((TextUtils.isEmpty(mark = tv_mark5d.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK6A,
				((TextUtils.isEmpty(mark = tv_mark6a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK6B,
				((TextUtils.isEmpty(mark = tv_mark6b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK6C,
				((TextUtils.isEmpty(mark = tv_mark6c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK6D,
				((TextUtils.isEmpty(mark = tv_mark6d.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK7A,
				((TextUtils.isEmpty(mark = tv_mark7a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK7B,
				((TextUtils.isEmpty(mark = tv_mark7b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK7C,
				((TextUtils.isEmpty(mark = tv_mark7c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK7D,
				((TextUtils.isEmpty(mark = tv_mark7d.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK8A,
				((TextUtils.isEmpty(mark = tv_mark8a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK8B,
				((TextUtils.isEmpty(mark = tv_mark8b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK8C,
				((TextUtils.isEmpty(mark = tv_mark8c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK8D,
				((TextUtils.isEmpty(mark = tv_mark8d.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(SSConstants.R1_TOTAL, _mark1_total);
		_contentValues.put(SSConstants.R2_TOTAL, _mark2_total);
		_contentValues.put(SSConstants.R3_TOTAL, _mark3_total);
		_contentValues.put(SSConstants.R4_TOTAL, _mark4_total);
		_contentValues.put(SSConstants.R5_TOTAL, _mark5_total);
		_contentValues.put(SSConstants.R6_TOTAL, _mark6_total);
		_contentValues.put(SSConstants.R7_TOTAL, _mark7_total);
		_contentValues.put(SSConstants.R8_TOTAL, _mark8_total);

		_contentValues.put(SSConstants.GRAND_TOTAL_MARK, _grand_total);

		// insertToDB(_contentValues);

	}

	private boolean FloatOrIntegerOnlyAllow(String inputvalue) {
		boolean flag = false;

		switch (max_mark) {

		case 4:
			flag = Pattern.matches("[0-3](\\.(0|5|50|00))|[0-4]", inputvalue);
			break;

		case 8:
			flag = Pattern.matches("[0-7](\\.(0|5|50|00))|[0-8]", inputvalue);
			break;
			
		case 10:
			flag = Pattern
					.matches(
							"[0-1][0-1](\\.(0|5|50|00))|[0-1][0-1]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
							inputvalue);
			break;

		case 12:
			flag = Pattern
					.matches(
							"[0-1][0-1](\\.(0|5|50|00))|[0-1][0-2]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
							inputvalue);
			break;

		case 14:
			flag = Pattern
					.matches(
							"[0-1][0-3](\\.(0|5|50|00))|[0-1][0-4]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
							inputvalue);
			break;
		case 15:
			flag = Pattern
					.matches(
							"[0-1][0-4](\\.(0|5|50|00))|[0-1][0-5]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
							inputvalue);
			break;

		case 16:
			flag = Pattern
					.matches(
							"[0-1][0-5](\\.(0|5|50|00))|[0-1][0-6]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
							inputvalue);   
			break;    

		case 20:
			flag = Pattern
					.matches(
							"[0-1][0-9]|[0-1][0-9](\\.(0|5|50|00))|[2][0]|[0-9]|(.(5|50))|[0-9](\\.(0|5|50|00))",
							inputvalue);

		case 30:
			flag = Pattern
					.matches(
							"[0-2][0-9]|[0-2][0-9](\\.(0|5|50|00))|[3][0]|[0-9]|(.(5|50))|[0-9](\\.(0|5|50|00))",
							inputvalue);
			break;

		case 32:
			flag = Pattern
					.matches(
							"[(.(5|50)]|[0-9]|[0-9](\\.(0|5|50|00))|[0-2][0-9]|[0-2][0-9](\\.(0|5|50|00))|[0-3][0-2]|[0-3][0-1](\\.(0|5|50|00))",
							inputvalue);
			break;

		case 45:
			flag = Pattern
					.matches(
							"[0-4][0-4]|[0-4][0-4](\\.(0|5|50|00))|[0-3][5-9]|[0-3][5-9](\\.(0|5|50|00))|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))|[4][5]",
							inputvalue);
			break;

		case 48:
			flag = Pattern
					.matches(
							"[(.(5|50)]|[0-9]|[0-9](\\.(0|5|50|00))|[0-4][0-8]|[0-3][0-9]|[0-3][0-9](\\.(0|5|50|00))",
							inputvalue);

			break;

		case 50:
			flag = Pattern
					.matches(
							"[0-4][0-9]|[0-4][0-9](\\.(0|5|50|00))|[5][0]|[0-9]|[(.(5|50)]|[0-9](\\.(0|5|50|00))",
							inputvalue);

			break;

		case 60:
			flag = Pattern
					.matches(
							"[0-5][0-9]|[0-5][0-9](\\.(0|5|50|00))|[6][0]|[0-9]|[(.(5|50)]|[0-9](\\.(0|5|50|00))",
							inputvalue);

			break;

		case 70:
			flag = Pattern
					.matches(
							"[0-6][0-9]|[0-6][0-9](\\.(0|5|50|00))|[7][0]|[0-9]|(.(5|50))|[0-9](\\.(0|5|50|00))",
							inputvalue);
			break;
		case 75:
			flag = true;
			break;
		default:
			break;
		}
		return flag;
	}

	BroadcastReceiver batteryLevelReceiver;

	@Override
	protected void onResume() {
		super.onResume();
		batteryLevel();

		if (wl != null) {
			wl.acquire();
		}
	}

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

	private void navigateToTabletHomeScreen() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
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

	private float selectBestof2() {
		float _row_total = SUB_TOTAL_EXCEEDS_MAX_MARKS_FOR_SPECIAL_CASE_SUBJ_CODES;
		ArrayList<Float> listSubTotalMarks = new ArrayList<Float>();
		String _marks = tv_mark1a.getText().toString().trim();
		if (!TextUtils.isEmpty(_marks)) {
			listSubTotalMarks.add(Float.valueOf(_marks));
		}

		_marks = tv_mark1b.getText().toString().trim();
		if (!TextUtils.isEmpty(_marks)) {
			listSubTotalMarks.add(Float.valueOf(_marks));
		}

		_marks = tv_mark1c.getText().toString().trim();
		if (!TextUtils.isEmpty(_marks)) {
			listSubTotalMarks.add(Float.valueOf(_marks));
		}

		_marks = tv_mark1d.getText().toString().trim();
		if (!TextUtils.isEmpty(_marks)) {
			listSubTotalMarks.add(Float.valueOf(_marks));
		}

		if (!listSubTotalMarks.isEmpty()) {
			Collections.sort(listSubTotalMarks, Collections.reverseOrder());
			_row_total = 0;
			if (listSubTotalMarks.size() > 2) {
				for (int i = 0; i < 2; i++) {
					_row_total += listSubTotalMarks.get(i);
				}
			} else {
				for (int i = 0; i < listSubTotalMarks.size(); i++) {
					_row_total += listSubTotalMarks.get(i);
				}
			}
		}
		return _row_total;
	}

	// ================================================================
	// special case subject code methods
	// ================================================================

	// setting max value for special case subject codes
	private int setMaxValueForSubjCodeSpecialCase(EditText etQuesNo) {

		int _max_value = 75;


				if(etQuesNo == tv_mark1a || etQuesNo == tv_mark1b
						|| etQuesNo == tv_mark1c || etQuesNo == tv_mark1d
						 || etQuesNo == tv_mark2a
						|| etQuesNo == tv_mark2b || etQuesNo == tv_mark2c
						|| etQuesNo == tv_mark2d 
						|| etQuesNo == tv_mark3a || etQuesNo == tv_mark3b
						|| etQuesNo == tv_mark3c || etQuesNo == tv_mark3d
						|| etQuesNo == tv_mark4a || etQuesNo == tv_mark4b
						|| etQuesNo == tv_mark4c || etQuesNo == tv_mark4d
						){
					_max_value = Integer.parseInt(RowTotalLimit);
				}
				else if(etQuesNo == tv_mark5a || etQuesNo == tv_mark5b
						|| etQuesNo == tv_mark5c || etQuesNo == tv_mark5d){
					_max_value = Integer.parseInt(maxTotalMark_5);
				}
		  
		return _max_value;
	}

	// check sub total for ques 1 for special case subject codes
	private boolean checkSubTotalforSpecialcaseSubjCode(String marks) {
		boolean flag = true;
		/*if (is_subject_code_special_case) {

			if (subjectCode
					.equalsIgnoreCase(SSConstants.SUBJ_X0305_MACHINE_DRAWING_6)) {
				if (!(Float.valueOf(marks) > SSConstants.SUBJ_X0305_MACHINE_DRAWING_6_PART_A_SUB_QUES_MAX_TOTAL)) {
					flag = true;
				} else {
					flag = false;
				}
			}
		} else {
			flag = false;
		}*/
		return flag;
	}

	private void setFocusablesForSpecialCaseSubjCodes() {
		
		tv_mark1a.setFocusable(true);
		tv_mark1b.setFocusable(true);
		tv_mark1c.setFocusable(true);
		tv_mark1d.setFocusable(true);
		 tv_mark1e.setFocusable(true);

		tv_mark2a.setFocusable(true);
		tv_mark2b.setFocusable(true);
		tv_mark2c.setFocusable(true);
		tv_mark2d.setFocusable(true);
		 tv_mark2e.setFocusable(true);

		tv_mark3a.setFocusable(true);
		tv_mark3b.setFocusable(true);
		tv_mark3c.setFocusable(true);
		tv_mark3d.setFocusable(true);
		
		tv_mark4a.setFocusable(true);
		tv_mark4b.setFocusable(true);
		tv_mark4c.setFocusable(true);
		tv_mark4d.setFocusable(true);
		
		tv_mark5a.setFocusable(true);
		tv_mark5b.setFocusable(true);
		tv_mark5c.setFocusable(true);
		tv_mark5d.setFocusable(true);
		
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
		
		tv_mark4a.setFocusableInTouchMode(true);
		tv_mark4b.setFocusableInTouchMode(true);
		tv_mark4c.setFocusableInTouchMode(true);
		tv_mark4d.setFocusableInTouchMode(true);
		
		tv_mark5a.setFocusableInTouchMode(true);
		tv_mark5b.setFocusableInTouchMode(true);
		tv_mark5c.setFocusableInTouchMode(true);
		tv_mark5d.setFocusableInTouchMode(true);



	}

	// disable EditText for special case subject codes
	private void disableEditTextForSpecialCaseSubjCode() {
		// check for special case subject codes
		
		
				tv_mark1a.setOnClickListener(this);
				tv_mark1b.setOnClickListener(this);
				tv_mark1c.setOnClickListener(this);
				tv_mark1d.setOnClickListener(this);
				
				tv_mark2a.setOnClickListener(this);
				tv_mark2b.setOnClickListener(this);
				tv_mark2c.setOnClickListener(this);
				tv_mark2d.setOnClickListener(this);
				
				tv_mark3a.setOnClickListener(this);
				tv_mark3b.setOnClickListener(this);
				tv_mark3c.setOnClickListener(this);
				tv_mark3d.setOnClickListener(this);
				
				tv_mark4a.setOnClickListener(this);
				tv_mark4b.setOnClickListener(this);
				tv_mark4c.setOnClickListener(this);
				tv_mark4d.setOnClickListener(this);
				
				tv_mark5a.setOnClickListener(this);
				tv_mark5b.setOnClickListener(this);
				tv_mark5c.setOnClickListener(this);
				tv_mark5d.setOnClickListener(this);
				
	}  

	private void calculateGrandTotal() { 

		ContentValues _contentValues = new ContentValues();
		BigDecimal roundOffGrandTotal = null;
		float grandTotal = 0;
		
					ArrayList<Float> listTotalMarks = new ArrayList<Float>();
					String etMark = _mark1_total;
					if (!TextUtils.isEmpty(etMark)) {
						_contentValues.put(SSConstants.R1_TOTAL, etMark);
					}
					float mark = Float.parseFloat(TextUtils.isEmpty(etMark) ? "0"
							: etMark);
					listTotalMarks.add(mark);

					etMark = _mark2_total;
					if (!TextUtils.isEmpty(etMark)) {  
						_contentValues.put(SSConstants.R2_TOTAL, etMark);
					}
					mark = Float.parseFloat(TextUtils.isEmpty(etMark) ? "0" : etMark);
					listTotalMarks.add(mark);

					etMark = _mark3_total;
					if (!TextUtils.isEmpty(etMark)) {
						_contentValues.put(SSConstants.R3_TOTAL, etMark);
					}
					mark = Float.parseFloat(TextUtils.isEmpty(etMark) ? "0" : etMark);
					listTotalMarks.add(mark);
					
					etMark = _mark4_total;
					if (!TextUtils.isEmpty(etMark)) {  
						_contentValues.put(SSConstants.R4_TOTAL, etMark);
					}
					mark = Float.parseFloat(TextUtils.isEmpty(etMark) ? "0" : etMark);
					listTotalMarks.add(mark);

					Collections.sort(listTotalMarks, Collections.reverseOrder());

					// best of 3 from 4
					
					for (int i = 0; i < 3; i++) {
						grandTotal += listTotalMarks.get(i);
					}

					etMark = _mark5_total;
					if (!TextUtils.isEmpty(etMark)) {
						_contentValues.put(SSConstants.R5_TOTAL, etMark);
					}
					
					mark = Float.parseFloat(TextUtils.isEmpty(etMark) ? "0" : etMark);

					// 5 is compulsory
					grandTotal += mark;

					roundOffGrandTotal = new BigDecimal(Float.toString(grandTotal));
					roundOffGrandTotal = roundOffGrandTotal.setScale(0,
							BigDecimal.ROUND_HALF_UP);

					_grand_total = String.valueOf(roundOffGrandTotal);

					if (roundOffGrandTotal != null) {
						_contentValues.put(SSConstants.GRAND_TOTAL_MARK,
								String.valueOf(roundOffGrandTotal));
					} else {
						_contentValues.put(SSConstants.GRAND_TOTAL_MARK, "0");
					}
					if (_contentValues.size() > 0) {
						// insertToDB(_contentValues);
					}


				

			}    
		
		
	private float checkRowTotalExceedsMaxValue(View view, String pMarks) {
		String mark;
		float _fl_total_row_mark = 0;
		if (TextUtils.isEmpty(pMarks)) {
			pMarks = "0";
		}
		if (isRegulation) {
			if (view.getId() == R.id.q1_a || view.getId() == R.id.q1_b
					|| view.getId() == R.id.q1_c || view.getId() == R.id.q1_d
					|| view.getId() == R.id.q1_e) {
				return _fl_total_row_mark = SSConstants.R13_MTECH_MAXSUBTOTAL_1;
			} else {
				return _fl_total_row_mark = SSConstants.R13_MTECH_MAXSUBTOTAL_2_TO_11;
			}
		}

		switch (view.getId()) {

		case R.id.q1_a:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q1_b:

			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q1_c:

			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;
		case R.id.q1_d:

			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q1_e:

			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q2_a:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q2_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark2a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q2_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark2a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2e
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;
		case R.id.q2_d:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark2a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2e
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q2_e:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark2a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2d
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q3_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q3_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark3a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q3_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark3a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q3_d:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark3a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q3_e:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark3a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3d
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q4_a:
				_fl_total_row_mark = Float.parseFloat(pMarks)
						+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4b
								.getText().toString().trim()))) ? "0" : mark)
						+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4c
								.getText().toString().trim()))) ? "0" : mark)
						+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4d
								.getText().toString().trim()))) ? "0" : mark)
						+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4e
								.getText().toString().trim()))) ? "0" : mark);
				return _fl_total_row_mark;  
				
		case R.id.q4_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark4a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q4_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark4a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q4_d:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark4a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q4_e:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark4a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4d
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q5_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q5_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark5a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q5_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark5a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q5_d:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark5a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q5_e:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark5a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5d
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q6_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q6_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark6a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q6_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark6a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q6_d:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark6a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q6_e:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark6a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6d
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q7_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q7_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark7a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q7_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark7a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q7_d:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark7a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q7_e:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark7a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7d
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q8_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q8_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark8a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q8_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark8a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q8_d:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark8a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8e
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q8_e:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark8a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8d
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		default:
			return 0;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
			EditText vvv = (EditText) v;
			vvv.setError(null);
			vvv.setText("");
		}
	}

	void updateAllMarksDB() {

		ContentValues contentValuesmark = new ContentValues();

		// Marks1
		contentValuesmark.put(SSConstants.MARK1A, getMarksfromEdit(tv_mark1a));
		contentValuesmark.put(SSConstants.MARK1B, getMarksfromEdit(tv_mark1b));
		contentValuesmark.put(SSConstants.MARK1C, getMarksfromEdit(tv_mark1c));
		contentValuesmark.put(SSConstants.MARK1D, getMarksfromEdit(tv_mark1d));
		contentValuesmark.put(SSConstants.MARK1E, getMarksfromEdit(tv_mark1e));

		// Marks2
		contentValuesmark.put(SSConstants.MARK2A, getMarksfromEdit(tv_mark2a));
		contentValuesmark.put(SSConstants.MARK2B, getMarksfromEdit(tv_mark2b));
		contentValuesmark.put(SSConstants.MARK2C, getMarksfromEdit(tv_mark2c));
		contentValuesmark.put(SSConstants.MARK2D, getMarksfromEdit(tv_mark2d));
		contentValuesmark.put(SSConstants.MARK2E, getMarksfromEdit(tv_mark2e));

		// Marks3
		contentValuesmark.put(SSConstants.MARK3A, getMarksfromEdit(tv_mark3a));
		contentValuesmark.put(SSConstants.MARK3B, getMarksfromEdit(tv_mark3b));
		contentValuesmark.put(SSConstants.MARK3C, getMarksfromEdit(tv_mark3c));
		contentValuesmark.put(SSConstants.MARK3D, getMarksfromEdit(tv_mark3d));
		contentValuesmark.put(SSConstants.MARK3E, getMarksfromEdit(tv_mark3e));

		// Marks4
		contentValuesmark.put(SSConstants.MARK4A, getMarksfromEdit(tv_mark4a));
		contentValuesmark.put(SSConstants.MARK4B, getMarksfromEdit(tv_mark4b));
		contentValuesmark.put(SSConstants.MARK4C, getMarksfromEdit(tv_mark4c));
		contentValuesmark.put(SSConstants.MARK4D, getMarksfromEdit(tv_mark4d));
		contentValuesmark.put(SSConstants.MARK4E, getMarksfromEdit(tv_mark4e));
  
		// Marks5
		contentValuesmark.put(SSConstants.MARK5A, getMarksfromEdit(tv_mark5a));
		contentValuesmark.put(SSConstants.MARK5B, getMarksfromEdit(tv_mark5b));
		contentValuesmark.put(SSConstants.MARK5C, getMarksfromEdit(tv_mark5c));
		contentValuesmark.put(SSConstants.MARK5D, getMarksfromEdit(tv_mark5d));
		contentValuesmark.put(SSConstants.MARK5E, getMarksfromEdit(tv_mark5e));

		// Marks6
		contentValuesmark.put(SSConstants.MARK6A, getMarksfromEdit(tv_mark6a));
		contentValuesmark.put(SSConstants.MARK6B, getMarksfromEdit(tv_mark6b));
		contentValuesmark.put(SSConstants.MARK6C, getMarksfromEdit(tv_mark6c));
		contentValuesmark.put(SSConstants.MARK6D, getMarksfromEdit(tv_mark6d));
		contentValuesmark.put(SSConstants.MARK6E, getMarksfromEdit(tv_mark6e));

		// Marks7
		contentValuesmark.put(SSConstants.MARK7A, getMarksfromEdit(tv_mark7a));
		contentValuesmark.put(SSConstants.MARK7B, getMarksfromEdit(tv_mark7b));
		contentValuesmark.put(SSConstants.MARK7C, getMarksfromEdit(tv_mark7c));
		contentValuesmark.put(SSConstants.MARK7D, getMarksfromEdit(tv_mark7d));
		contentValuesmark.put(SSConstants.MARK7E, getMarksfromEdit(tv_mark7e));

		// Marks8
		contentValuesmark.put(SSConstants.MARK8A, getMarksfromEdit(tv_mark8a));
		contentValuesmark.put(SSConstants.MARK8B, getMarksfromEdit(tv_mark8b));
		contentValuesmark.put(SSConstants.MARK8C, getMarksfromEdit(tv_mark8c));
		contentValuesmark.put(SSConstants.MARK8D, getMarksfromEdit(tv_mark8d));
		contentValuesmark.put(SSConstants.MARK8E, getMarksfromEdit(tv_mark8e));

		row1Total_();
		row_2_Total_();
		row_3_Total_();
		row_4_Total_();
		row_5_Total_();
		row_6_Total_();
		row_7_Total_();
		row_8_Total_();

		contentValuesmark.put(SSConstants.R1_TOTAL, _mark1_total);
		contentValuesmark.put(SSConstants.R2_TOTAL, _mark2_total);
		contentValuesmark.put(SSConstants.R3_TOTAL, _mark3_total);
		contentValuesmark.put(SSConstants.R4_TOTAL, _mark4_total);
		contentValuesmark.put(SSConstants.R5_TOTAL, _mark5_total);
		contentValuesmark.put(SSConstants.R6_TOTAL, _mark6_total);
		contentValuesmark.put(SSConstants.R7_TOTAL, _mark7_total);
		contentValuesmark.put(SSConstants.R8_TOTAL, _mark8_total);
		contentValuesmark.put(SSConstants.GRAND_TOTAL_MARK, _grand_total);
		insertToDB(contentValuesmark);
	}

	private String getMarksfromEdit(EditText ptext) {
		if (!TextUtils.isEmpty(ptext.getText().toString().trim())) {
			return ptext.getText().toString().trim();  
		}
		return null;
	}
}
