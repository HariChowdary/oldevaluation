package com.infoplustech.smartscrutinization;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.db.Scrutiny_TempDatabase;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_MarkDialogCorrection_R13 extends Activity implements
		OnClickListener, TextWatcher, OnFocusChangeListener {

	EditText tv_mark1a, tv_mark1b, tv_mark1c, tv_mark1d, tv_mark1e,
	tv_mark1f, tv_mark1g, tv_mark1h, tv_mark1i, tv_mark1j;
	EditText tv_mark2a, tv_mark2b, tv_mark2c;

	EditText tv_mark3a, tv_mark3b, tv_mark3c;

	EditText tv_mark4a, tv_mark4b, tv_mark4c;

	EditText tv_mark5a, tv_mark5b, tv_mark5c;    

	EditText tv_mark6a, tv_mark6b, tv_mark6c;

	EditText tv_mark7a, tv_mark7b, tv_mark7c; 

	EditText tv_mark8a, tv_mark8b, tv_mark8c;

	EditText tv_mark9a, tv_mark9b, tv_mark9c;

	EditText tv_mark10a, tv_mark10b, tv_mark10c;

	EditText tv_mark11a, tv_mark11b, tv_mark11c;
	
	Boolean clickFind = false;

//	EditText tv_mark1_total, tv_mark_2_3_total, tv_mark_4_5_total,
//			tv_mark_6_7_total, tv_mark_8_9_total, tv_mark_10_11_total;
//
//	EditText tv_grand_toal;
	String _mark1_total=null, _mark_2_3_total=null, _mark_4_5_total=null,
			_mark_6_7_total=null, _mark_8_9_total=null,
			_mark_10_11_total=null, _grand_toal=null;
	Boolean R13BTech=false;
	Boolean R15MTech = false;
	int A1Limit=4;
	int A1TotalLimit=20;
	int RowTotalLimit=8;
	int GrandTotalLimit=60;
	Utility instanceUtitlity;
	HashMap<Integer, String> RemarksArray;
	HashMap<Integer, String> marksArray;
	BigDecimal final_Total = null;

	String userId, subjectCode, ansBookBarcode, bundleNo, bundle_serial_no, SeatNo;
	int scrutinizedStatusInObsMode;
	boolean isAddScript, clickable = false;;
	int max_mark;

	SharedPreferences sharedPreference;
	SharedPreferences.Editor editor;

	private PowerManager.WakeLock wl;
  
		@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scrutiny_r13_nototal);

		// if (!Utility.isNetworkAvailable(this)) {
		// alertMessageForChargeAutoUpdateApk(getString(R.string.alert_network_avail));
		// return;
		// }
		instanceUtitlity = new Utility();
		RemarksArray = new HashMap<Integer, String>();
		marksArray = new HashMap<Integer, String>();

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");
		// get data from previous activity/screen
		R13BTech=instanceUtitlity
				.isRegulation_R13_Btech(Scrutiny_MarkDialogCorrection_R13.this);
		R15MTech = instanceUtitlity.isRegulation_R15_Mtech(Scrutiny_MarkDialogCorrection_R13.this);
		if (R13BTech) {
			 A1Limit=3;  
			 A1TotalLimit=25;
			 RowTotalLimit=10;        
			 GrandTotalLimit=75;    
		}     
		else if(R15MTech){
			 A1Limit=5;
			 A1TotalLimit=25;
			 RowTotalLimit=10;  
			 GrandTotalLimit=75;
		}
		else{       
			 A1Limit=4;  
			 A1TotalLimit=20;
			 RowTotalLimit=8;
			 GrandTotalLimit=60;
		}
		
		Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO);

		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		
		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		ansBookBarcode = intent_extras
				.getStringExtra(SSConstants.ANS_BOOK_BARCODE);  
		bundle_serial_no = intent_extras
				.getStringExtra(SSConstants.BUNDLE_SERIAL_NO);
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);  

		if (intent_extras.hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
			isAddScript = true;
		} else {
			isAddScript = false;

		}
	
		showItems();

		Button btnSubmit1 = (Button) findViewById(R.id.btn_submit1);
		if (scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED
				|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH
				|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED)  {
			
//			((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
//			((TextView) findViewById(R.id.tv_ans_book)).setText(bundle_serial_no);
//			((TextView) findViewById(R.id.tv_bundle_no)).setVisibility(View.GONE);
//			((TextView) findViewById(R.id.tv_ans_book)).setVisibility(View.GONE);

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
//			((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
//			((TextView) findViewById(R.id.tv_ans_book)).setText(bundle_serial_no);
//			((TextView) findViewById(R.id.tv_bundle_no)).setVisibility(View.GONE);
//			((TextView) findViewById(R.id.tv_ans_book)).setVisibility(View.GONE);
			btnSubmit1.setOnClickListener(this);
		}

		((TextView) findViewById(R.id.tv_user_id)).setText(userId);
		((TextView) findViewById(R.id.tv_sub_code)).setText(""+subjectCode);
		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
		((TextView) findViewById(R.id.tv_ans_book)).setText(bundle_serial_no);
	}

	// set marks
	private void setMarkToCellFromDB(String pMark, EditText pTextView) {
		if (!TextUtils.isEmpty(pMark)) {
			pTextView.setText(pMark);
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
tv_mark1f = ((EditText) findViewById(R.id.q1_f));
tv_mark1g = ((EditText) findViewById(R.id.q1_g));
tv_mark1h = ((EditText) findViewById(R.id.q1_h));
tv_mark1i = ((EditText) findViewById(R.id.q1_i));
tv_mark1j = ((EditText) findViewById(R.id.q1_j));

tv_mark2a = ((EditText) findViewById(R.id.q2_a));
tv_mark2b = ((EditText) findViewById(R.id.q2_b));
tv_mark2c = ((EditText) findViewById(R.id.q2_c));

tv_mark3a = ((EditText) findViewById(R.id.q3_a));
tv_mark3b = ((EditText) findViewById(R.id.q3_b));
tv_mark3c = ((EditText) findViewById(R.id.q3_c));

tv_mark4a = ((EditText) findViewById(R.id.q4_a));
tv_mark4b = ((EditText) findViewById(R.id.q4_b));
tv_mark4c = ((EditText) findViewById(R.id.q4_c));

tv_mark5a = ((EditText) findViewById(R.id.q5_a));
tv_mark5b = ((EditText) findViewById(R.id.q5_b));
tv_mark5c = ((EditText) findViewById(R.id.q5_c));

tv_mark6a = ((EditText) findViewById(R.id.q6_a));
tv_mark6b = ((EditText) findViewById(R.id.q6_b));
tv_mark6c = ((EditText) findViewById(R.id.q6_c));

tv_mark7a = ((EditText) findViewById(R.id.q7_a));
tv_mark7b = ((EditText) findViewById(R.id.q7_b));
tv_mark7c = ((EditText) findViewById(R.id.q7_c));

tv_mark8a = ((EditText) findViewById(R.id.q8_a));
tv_mark8b = ((EditText) findViewById(R.id.q8_b));
tv_mark8c = ((EditText) findViewById(R.id.q8_c));

tv_mark9a = ((EditText) findViewById(R.id.q9_a));
tv_mark9b = ((EditText) findViewById(R.id.q9_b));
tv_mark9c = ((EditText) findViewById(R.id.q9_c));

tv_mark10a = ((EditText) findViewById(R.id.q10_a));
tv_mark10b = ((EditText) findViewById(R.id.q10_b));
tv_mark10c = ((EditText) findViewById(R.id.q10_c));

tv_mark11a = ((EditText) findViewById(R.id.q11_a));
tv_mark11b = ((EditText) findViewById(R.id.q11_b));
tv_mark11c = ((EditText) findViewById(R.id.q11_c));

//tv_mark1_total = ((EditText) findViewById(R.id.q1_total));
//tv_mark_2_3_total = ((EditText) findViewById(R.id.q2_3_total));
//tv_mark_4_5_total = ((EditText) findViewById(R.id.q4_5_total));
//tv_mark_6_7_total = ((EditText) findViewById(R.id.q6_7_total));
//tv_mark_8_9_total = ((EditText) findViewById(R.id.q8_9_total));
//tv_mark_10_11_total = ((EditText) findViewById(R.id.q10_11_total));
//tv_grand_toal = ((EditText) findViewById(R.id.grand_total));
//
//tv_mark1_total.setOnFocusChangeListener(this);
//tv_mark_2_3_total.setOnFocusChangeListener(this);  
//tv_mark_4_5_total.setOnFocusChangeListener(this);
//tv_mark_6_7_total.setOnFocusChangeListener(this);
//tv_mark_8_9_total.setOnFocusChangeListener(this);
//tv_mark_10_11_total.setOnFocusChangeListener(this); 


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

		tv_mark1a.addTextChangedListener(this);
		tv_mark1b.addTextChangedListener(this);
		tv_mark1c.addTextChangedListener(this);
		tv_mark1d.addTextChangedListener(this);
		tv_mark1e.addTextChangedListener(this);
		

		tv_mark2a.addTextChangedListener(this);
		tv_mark2b.addTextChangedListener(this);
		tv_mark2c.addTextChangedListener(this);

		tv_mark3a.addTextChangedListener(this);
		tv_mark3b.addTextChangedListener(this);
		tv_mark3c.addTextChangedListener(this);

		tv_mark4a.addTextChangedListener(this);
		tv_mark4b.addTextChangedListener(this);
		tv_mark4c.addTextChangedListener(this);

		tv_mark5a.addTextChangedListener(this);
		tv_mark5b.addTextChangedListener(this);
		tv_mark5c.addTextChangedListener(this);

		tv_mark6a.addTextChangedListener(this);
		tv_mark6b.addTextChangedListener(this);
		tv_mark6c.addTextChangedListener(this);

		tv_mark7a.addTextChangedListener(this);
		tv_mark7b.addTextChangedListener(this);
		tv_mark7c.addTextChangedListener(this);

		tv_mark8a.addTextChangedListener(this);
		tv_mark8b.addTextChangedListener(this);
		tv_mark8c.addTextChangedListener(this);

		tv_mark9a.addTextChangedListener(this);
		tv_mark9b.addTextChangedListener(this);
		tv_mark9c.addTextChangedListener(this);

		tv_mark10a.addTextChangedListener(this);
		tv_mark10b.addTextChangedListener(this);
		tv_mark10c.addTextChangedListener(this);

		tv_mark11a.addTextChangedListener(this);
		tv_mark11b.addTextChangedListener(this);
		tv_mark11c.addTextChangedListener(this); 

		tv_mark1a.setOnClickListener(this);
		tv_mark1b.setOnClickListener(this);
		tv_mark1c.setOnClickListener(this);
		tv_mark1d.setOnClickListener(this);
		tv_mark1e.setOnClickListener(this);
		

		if (R13BTech) {
			tv_mark1f.setOnClickListener(this);
			tv_mark1g.setOnClickListener(this);
			tv_mark1h.setOnClickListener(this);
			tv_mark1i.setOnClickListener(this);
			tv_mark1j.setOnClickListener(this);
			tv_mark1f.addTextChangedListener(this);
			tv_mark1g.addTextChangedListener(this);
			tv_mark1h.addTextChangedListener(this); 
			tv_mark1i.addTextChangedListener(this);
			tv_mark1j.addTextChangedListener(this);
		}
		
		if(!R13BTech){
			
			tv_mark1f.setLongClickable(false);
			tv_mark1g.setLongClickable(false);  
			tv_mark1h.setLongClickable(false);
			tv_mark1i.setLongClickable(false);   
			tv_mark1j.setLongClickable(false);
			}
		  
		tv_mark2a.setOnClickListener(this);
		tv_mark2b.setOnClickListener(this);
		tv_mark2c.setOnClickListener(this);

		tv_mark3a.setOnClickListener(this);
		tv_mark3b.setOnClickListener(this);
		tv_mark3c.setOnClickListener(this);

		tv_mark6a.setOnClickListener(this);
		tv_mark6b.setOnClickListener(this);
		tv_mark6c.setOnClickListener(this);

		tv_mark4a.setOnClickListener(this);
		tv_mark4b.setOnClickListener(this);
		tv_mark4c.setOnClickListener(this);

		tv_mark5a.setOnClickListener(this);
		tv_mark5b.setOnClickListener(this);
		tv_mark5c.setOnClickListener(this);

		tv_mark7a.setOnClickListener(this);
		tv_mark7b.setOnClickListener(this);
		tv_mark7c.setOnClickListener(this);

		tv_mark8a.setOnClickListener(this);
		tv_mark8b.setOnClickListener(this);
		tv_mark8c.setOnClickListener(this);

		tv_mark9a.setOnClickListener(this);
		tv_mark9b.setOnClickListener(this);
		tv_mark9c.setOnClickListener(this);

		tv_mark10a.setOnClickListener(this);
		tv_mark10b.setOnClickListener(this);
		tv_mark10c.setOnClickListener(this);

		tv_mark11a.setOnClickListener(this);
		tv_mark11b.setOnClickListener(this);
		tv_mark11c.setOnClickListener(this);
		
		//Swapna
		tv_mark1a.setLongClickable(false);
		tv_mark1b.setLongClickable(false);
		tv_mark1c.setLongClickable(false);
		tv_mark1d.setLongClickable(false);
		tv_mark1e.setLongClickable(false);
		
		tv_mark1f.setLongClickable(false);
		tv_mark1g.setLongClickable(false);  
		tv_mark1h.setLongClickable(false);
		tv_mark1i.setLongClickable(false);   
		tv_mark1j.setLongClickable(false);
		  
		tv_mark2a.setLongClickable(false);
		tv_mark2b.setLongClickable(false);
		tv_mark2c.setLongClickable(false);

		tv_mark3a.setLongClickable(false);
		tv_mark3b.setLongClickable(false);
		tv_mark3c.setLongClickable(false);

		tv_mark6a.setLongClickable(false);
		tv_mark6b.setLongClickable(false);
		tv_mark6c.setLongClickable(false);

		tv_mark4a.setLongClickable(false);
		tv_mark4b.setLongClickable(false);
		tv_mark4c.setLongClickable(false);

		tv_mark5a.setLongClickable(false);
		tv_mark5b.setLongClickable(false);
		tv_mark5c.setLongClickable(false);

		tv_mark7a.setLongClickable(false);
		tv_mark7b.setLongClickable(false);
		tv_mark7c.setLongClickable(false);

		tv_mark8a.setLongClickable(false);
		tv_mark8b.setLongClickable(false);
		tv_mark8c.setLongClickable(false);

		tv_mark9a.setLongClickable(false);
		tv_mark9b.setLongClickable(false);
		tv_mark9c.setLongClickable(false);

		tv_mark10a.setLongClickable(false);
		tv_mark10b.setLongClickable(false);
		tv_mark10c.setLongClickable(false);

		tv_mark11a.setLongClickable(false);
		tv_mark11b.setLongClickable(false);
		tv_mark11c.setLongClickable(false);
		
		

//		TextView tvSubjectCode = (TextView) findViewById(R.id.tv_sub_code);
//		tvSubjectCode.setVisibility(View.GONE);
//		TextView tvBundelSerialNo = (TextView) findViewById(R.id.tv_ans_book);
//		tvBundelSerialNo.setVisibility(View.GONE);

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
		if (R13BTech) {  
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK1F)), tv_mark1f);  
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK1G)), tv_mark1g);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK1H)), tv_mark1h);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK1I)), tv_mark1i);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK1J)), tv_mark1j);
		}
//		setMarkToCellFromDB1(cursor.getString(cursor
//				.getColumnIndex(SSConstants.R1_TOTAL)), tv_mark1_total);

		// Marks2  
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK2A)), tv_mark2a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK2B)), tv_mark2b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK2C)), tv_mark2c);
		// Marks3
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK3A)), tv_mark3a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK3B)), tv_mark3b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK3C)), tv_mark3c);
//		setMarkToCellFromDB1(cursor.getString(cursor
//				.getColumnIndex(SSConstants.R2_3TOTAL)),
//				tv_mark_2_3_total);

		// Marks4
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK4A)), tv_mark4a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK4B)), tv_mark4b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK4C)), tv_mark4c);
		// Marks5
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK5A)), tv_mark5a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK5B)), tv_mark5b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK5C)), tv_mark5c);
//		setMarkToCellFromDB1(cursor.getString(cursor
//				.getColumnIndex(SSConstants.R4_5TOTAL)),
//				tv_mark_4_5_total);

		// Marks6
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK6A)), tv_mark6a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK6B)), tv_mark6b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK6C)), tv_mark6c);
		// Marks7
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK7A)), tv_mark7a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK7B)), tv_mark7b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK7C)), tv_mark7c);

//		setMarkToCellFromDB1(cursor.getString(cursor
//				.getColumnIndex(SSConstants.R6_7TOTAL)),
//				tv_mark_6_7_total);

		// Marks8
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK8A)), tv_mark8a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK8B)), tv_mark8b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK8C)), tv_mark8c);
		// Marks9
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK9A)), tv_mark9a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK9B)), tv_mark9b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK9C)), tv_mark9c);

//		setMarkToCellFromDB1(cursor.getString(cursor
//				.getColumnIndex(SSConstants.R8_9TOTAL)),
//				tv_mark_8_9_total);

		// Marks10
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK10A)), tv_mark10a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK10B)), tv_mark10b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK10C)), tv_mark10c);
		// Marks11
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK11A)), tv_mark11a);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK11B)), tv_mark11b);
		setMarkToCellFromDB(cursor.getString(cursor
				.getColumnIndex(SSConstants.MARK11C)), tv_mark11c);
//		setMarkToCellFromDB1(cursor.getString(cursor
//				.getColumnIndex(SSConstants.R10_11TOTAL)),
//				tv_mark_10_11_total);
//
//		setMarkToCellFromDB1(cursor.getString(cursor
//				.getColumnIndex(SSConstants.GRAND_TOTAL_MARK)),
//				tv_grand_toal);

		if (!(scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED || 
				scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH)) {

			/*tvSubjectCode.setVisibility(View.VISIBLE);
			tvSubjectCode.setText(subjectCode);

			tvBundelSerialNo.setVisibility(View.VISIBLE);
			tvBundelSerialNo.setText(cursor.getString(cursor
					.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)));*/
		
			
//			((TextView) findViewById(R.id.tv_bundle_no)).setVisibility(View.GONE);
//			((TextView) findViewById(R.id.tv_ans_book)).setVisibility(View.GONE);
			showRemarksWithOrangeColor();
			
			disableEditText();
			
		} else {
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
			setFocusables();
		}

	}
} else {

	showAlert(getString(R.string.alert_barcode_not_exists_in_db),
			getString(R.string.alert_dialog_ok), "", false);
}
cursor.close();
}

	private void disableEditText() {
		// TODO Auto-generated method stub
		
		
	}

	private void setFocusables() {  
		// TODO Auto-generated method stub

		//R13
		tv_mark1a.setFocusable(true);
		tv_mark1b.setFocusable(true);
		tv_mark1c.setFocusable(true);
		tv_mark1d.setFocusable(true);
		tv_mark1e.setFocusable(true);

		if (R13BTech) {
			
				tv_mark1f.setFocusableInTouchMode(true);
				tv_mark1g.setFocusableInTouchMode(true);
				tv_mark1h.setFocusableInTouchMode(true);
				tv_mark1i.setFocusableInTouchMode(true);
				tv_mark1j.setFocusableInTouchMode(true);
				
				tv_mark1f.setFocusable(true);
				tv_mark1g.setFocusable(true);
				tv_mark1h.setFocusable(true);
				tv_mark1i.setFocusable(true);
				tv_mark1j.setFocusable(true);
				
		}
		
		tv_mark2a.setFocusable(true);
		tv_mark2b.setFocusable(true);
		tv_mark2c.setFocusable(true);

		tv_mark3a.setFocusable(true);
		tv_mark3b.setFocusable(true);
		tv_mark3c.setFocusable(true);

		tv_mark4a.setFocusable(true);
		tv_mark4b.setFocusable(true);
		tv_mark4c.setFocusable(true);

		tv_mark5a.setFocusable(true);
		tv_mark5b.setFocusable(true);
		tv_mark5c.setFocusable(true);

		tv_mark6a.setFocusable(true);
		tv_mark6b.setFocusable(true);
		tv_mark6c.setFocusable(true);

		tv_mark7a.setFocusable(true);
		tv_mark7b.setFocusable(true);
		tv_mark7c.setFocusable(true);

		tv_mark8a.setFocusable(true);
		tv_mark8b.setFocusable(true);
		tv_mark8c.setFocusable(true);
		
		tv_mark1a.setFocusableInTouchMode(true);
		tv_mark1b.setFocusableInTouchMode(true);
		tv_mark1c.setFocusableInTouchMode(true);
		tv_mark1d.setFocusableInTouchMode(true);
		tv_mark1e.setFocusableInTouchMode(true);

		tv_mark2a.setFocusableInTouchMode(true);
		tv_mark2b.setFocusableInTouchMode(true);
		tv_mark2c.setFocusableInTouchMode(true);
		
		tv_mark3a.setFocusableInTouchMode(true);
		tv_mark3b.setFocusableInTouchMode(true);
		tv_mark3c.setFocusableInTouchMode(true);

		tv_mark4a.setFocusableInTouchMode(true);
		tv_mark4b.setFocusableInTouchMode(true);
		tv_mark4c.setFocusableInTouchMode(true);

		tv_mark5a.setFocusableInTouchMode(true);
		tv_mark5b.setFocusableInTouchMode(true);
		tv_mark5c.setFocusableInTouchMode(true);

		tv_mark6a.setFocusableInTouchMode(true);
		tv_mark6b.setFocusableInTouchMode(true);
		tv_mark6c.setFocusableInTouchMode(true);

		tv_mark7a.setFocusableInTouchMode(true);
		tv_mark7b.setFocusableInTouchMode(true);
		tv_mark7c.setFocusableInTouchMode(true);

		tv_mark8a.setFocusableInTouchMode(true);
		tv_mark8b.setFocusableInTouchMode(true);
		tv_mark8c.setFocusableInTouchMode(true);

        tv_mark9a.setFocusable(true);
		tv_mark9b.setFocusable(true);
		tv_mark9c.setFocusable(true);
		
		tv_mark9a.setFocusableInTouchMode(true);
		tv_mark9b.setFocusableInTouchMode(true);
		tv_mark9c.setFocusableInTouchMode(true);

        tv_mark10a.setFocusable(true);
		tv_mark10b.setFocusable(true);
		tv_mark10c.setFocusable(true);
		
		tv_mark10a.setFocusableInTouchMode(true);
		tv_mark10b.setFocusableInTouchMode(true);
		tv_mark10c.setFocusableInTouchMode(true);

        tv_mark11a.setFocusable(true);
		tv_mark11b.setFocusable(true);
		tv_mark11c.setFocusable(true);
		
		tv_mark11a.setFocusableInTouchMode(true);
		tv_mark11b.setFocusableInTouchMode(true);
		tv_mark11c.setFocusableInTouchMode(true);

		
//		tv_mark1_total.setFocusable(true);
//		tv_mark1_total.setFocusableInTouchMode(true);
//		
//		tv_mark_2_3_total.setFocusable(true);
//		tv_mark_2_3_total.setFocusableInTouchMode(true);
//		
//		tv_mark_4_5_total.setFocusable(true);
//		tv_mark_4_5_total.setFocusableInTouchMode(true);
//		
//		tv_mark_6_7_total.setFocusable(true);
//		tv_mark_6_7_total.setFocusableInTouchMode(true);
//		
//		tv_mark_8_9_total.setFocusable(true);
//		tv_mark_8_9_total.setFocusableInTouchMode(true);
//		
//		tv_mark_10_11_total.setFocusable(true);
//		tv_mark_10_11_total.setFocusableInTouchMode(true);
//		
//			
//		tv_grand_toal.setFocusable(true);
//		tv_grand_toal.setFocusableInTouchMode(true);
		
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
			if (R13BTech) {
			  setRemarkWithColor(cursor.getString(cursor
			  .getColumnIndex(SSConstants.M1F_REMARK)), tv_mark1f);
			  setRemarkWithColor(cursor.getString(cursor
			  .getColumnIndex(SSConstants.M1G_REMARK)), tv_mark1g);
			  setRemarkWithColor(cursor.getString(cursor
			  .getColumnIndex(SSConstants.M1H_REMARK)), tv_mark1h);
			  setRemarkWithColor(cursor.getString(cursor
			  .getColumnIndex(SSConstants.M1I_REMARK)), tv_mark1i);
			  setRemarkWithColor(cursor.getString(cursor
			  .getColumnIndex(SSConstants.M1J_REMARK)), tv_mark1j);
			}

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2A_REMARK)), tv_mark2a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2B_REMARK)), tv_mark2b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2C_REMARK)), tv_mark2c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3A_REMARK)), tv_mark3a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3B_REMARK)), tv_mark3b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3C_REMARK)), tv_mark3c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4A_REMARK)), tv_mark4a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4B_REMARK)), tv_mark4b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4C_REMARK)), tv_mark4c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5A_REMARK)), tv_mark5a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5B_REMARK)), tv_mark5b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5C_REMARK)), tv_mark5c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6A_REMARK)), tv_mark6a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6B_REMARK)), tv_mark6b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6C_REMARK)), tv_mark6c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7A_REMARK)), tv_mark7a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7B_REMARK)), tv_mark7b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7C_REMARK)), tv_mark7c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8A_REMARK)), tv_mark8a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8B_REMARK)), tv_mark8b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8C_REMARK)), tv_mark8c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M9A_REMARK)), tv_mark9a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M9B_REMARK)), tv_mark9b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M9C_REMARK)), tv_mark9c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M10A_REMARK)), tv_mark10a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M10B_REMARK)), tv_mark10b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M10C_REMARK)), tv_mark10c);

			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M11A_REMARK)), tv_mark11a);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M11B_REMARK)), tv_mark11b);
			setRemarkWithColor(cursor.getString(cursor
					.getColumnIndex(SSConstants.M11C_REMARK)), tv_mark11c);

//			setRemarkWithColor(cursor.getString(cursor
//					.getColumnIndex(SSConstants.R1_REMARK)), tv_mark1_total);
//
//			setRemarkWithColor(cursor.getString(cursor
//					.getColumnIndex(SSConstants.R2_REMARK)), tv_mark_2_3_total);
//
//			setRemarkWithColor(cursor.getString(cursor
//					.getColumnIndex(SSConstants.R4_REMARK)), tv_mark_4_5_total);
//
//			setRemarkWithColor(cursor.getString(cursor
//					.getColumnIndex(SSConstants.R6_REMARK)), tv_mark_6_7_total);
//
//			setRemarkWithColor(cursor.getString(cursor
//					.getColumnIndex(SSConstants.R8_REMARK)), tv_mark_8_9_total);
//
//			setRemarkWithColor(cursor.getString(cursor
//					.getColumnIndex(SSConstants.R10_REMARK)),
//					tv_mark_10_11_total);

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
		// tv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
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
						// clicked
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

	private void updateDBProcess() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				showProgress();
			};

			@Override
			protected Void doInBackground(Void... params) {

				updateAllMarksDB();
				ContentValues _contentValues = new ContentValues();
				_contentValues
						.put(SSConstants.TABLET_IMEI,
								((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
										.getDeviceId());
				_contentValues.put(SSConstants.CORRECTED_ON, getPresentTime());

				// if (isAddScript) {
				// contentValues.put(
				// SSConstants.SCRUTINIZE_STATUS, 5);
				// }

				if ((scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED
						|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH 
						|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION)
						|| scrutinizedStatusInObsMode == SSConstants.SCRUTINY_STATUS_7_SCRIPT_MISMATCH_WITH_DB) {
					_contentValues.put(SSConstants.SCRUTINIZE_STATUS,
							SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION);
				} else {
					_contentValues.put(SSConstants.SCRUTINIZE_STATUS,
							SSConstants.SCRUTINY_STATUS_6_SCRIPT_COMPLETED);
				}

				_contentValues.put(SSConstants.IS_CORRECTED, 1);
				_contentValues.put(SSConstants.USER_ID, userId);
				_contentValues.put(SSConstants.GRAND_TOTAL_MARK, _grand_toal
					);
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
		Boolean setRemark=true;
		Boolean insertToTempDB=true;
		switch (e.getKey()) {

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

		case R.id.q1_e:
			setRemarkInContentValue3(SSConstants.M1E_REMARK,
					SSConstants.MARK1E, remarkOrMark, setRemark, insertToTempDB);
			break;
			
		 case R.id.q1_f: setRemarkInContentValue3(SSConstants.M1F_REMARK,
				  SSConstants.MARK1F, remarkOrMark, setRemark, insertToTempDB); break;
				  
				  case R.id.q1_g: setRemarkInContentValue3(SSConstants.M1G_REMARK,
				  SSConstants.MARK1G, remarkOrMark, setRemark, insertToTempDB); break;
				  
				  case R.id.q1_h: setRemarkInContentValue3(SSConstants.M1H_REMARK,
				  SSConstants.MARK1H, remarkOrMark, setRemark, insertToTempDB); break;
				  
				  case R.id.q1_i: setRemarkInContentValue3(SSConstants.M1I_REMARK,
				  SSConstants.MARK1I, remarkOrMark, setRemark, insertToTempDB); break;
				  
				  case R.id.q1_j: setRemarkInContentValue3(SSConstants.M1J_REMARK,
				  SSConstants.MARK1J, remarkOrMark, setRemark, insertToTempDB); break;
				 
			
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
		case R.id.q9_a:
			setRemarkInContentValue3(SSConstants.M9A_REMARK,
					SSConstants.MARK9A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q9_b:
			setRemarkInContentValue3(SSConstants.M9B_REMARK,
					SSConstants.MARK9B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q9_c:
			setRemarkInContentValue3(SSConstants.M9C_REMARK,
					SSConstants.MARK9C, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q10_a:
			setRemarkInContentValue3(SSConstants.M10A_REMARK,
					SSConstants.MARK10A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q10_b:
			setRemarkInContentValue3(SSConstants.M10B_REMARK,
					SSConstants.MARK10B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q10_c:
			setRemarkInContentValue3(SSConstants.M10C_REMARK,
					SSConstants.MARK10C, remarkOrMark, setRemark, insertToTempDB);

			break;
			
		case R.id.q11_a:
			setRemarkInContentValue3(SSConstants.M11A_REMARK,
					SSConstants.MARK11A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q11_b:
			setRemarkInContentValue3(SSConstants.M11B_REMARK,
					SSConstants.MARK11B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q11_c:
			setRemarkInContentValue3(SSConstants.M11C_REMARK,
					SSConstants.MARK11C, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q1_total:
			setRemarkInContentValue3(SSConstants.R1_REMARK,
					SSConstants.R1_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q2_3_total:
			setRemarkInContentValue3(SSConstants.R2_REMARK,
					SSConstants.R2_3TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q4_5_total:
			setRemarkInContentValue3(SSConstants.R4_REMARK,
					SSConstants.R4_5TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q6_7_total:
			setRemarkInContentValue3(SSConstants.R6_REMARK,
					SSConstants.R6_7TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q8_9_total:
			setRemarkInContentValue3(SSConstants.R8_REMARK,
					SSConstants.R8_9TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q10_11_total:
			setRemarkInContentValue3(SSConstants.R10_REMARK,
					SSConstants.R10_11TOTAL, remarkOrMark, setRemark,
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

	void updateAllMarksDB() {

		ContentValues contentValuesmark = new ContentValues();

		// Marks1
		contentValuesmark.put(SSConstants.MARK1A, getMarksfromEdit(tv_mark1a));
		contentValuesmark.put(SSConstants.MARK1B, getMarksfromEdit(tv_mark1b));
		contentValuesmark.put(SSConstants.MARK1C, getMarksfromEdit(tv_mark1c));
		contentValuesmark.put(SSConstants.MARK1D, getMarksfromEdit(tv_mark1d));
		contentValuesmark.put(SSConstants.MARK1E, getMarksfromEdit(tv_mark1e));
		if (R13BTech) {
			contentValuesmark.put(SSConstants.MARK1F,
					getMarksfromEdit(tv_mark1f));
			contentValuesmark.put(SSConstants.MARK1G,
					getMarksfromEdit(tv_mark1g));
			contentValuesmark.put(SSConstants.MARK1H,
					getMarksfromEdit(tv_mark1h));
			contentValuesmark.put(SSConstants.MARK1I,
					getMarksfromEdit(tv_mark1i));
			contentValuesmark.put(SSConstants.MARK1J,
					getMarksfromEdit(tv_mark1j));
		}
		// Marks2
		contentValuesmark.put(SSConstants.MARK2A, getMarksfromEdit(tv_mark2a));
		contentValuesmark.put(SSConstants.MARK2B, getMarksfromEdit(tv_mark2b));
		contentValuesmark.put(SSConstants.MARK2C, getMarksfromEdit(tv_mark2c));

		// Marks3
		contentValuesmark.put(SSConstants.MARK3A, getMarksfromEdit(tv_mark3a));
		contentValuesmark.put(SSConstants.MARK3B, getMarksfromEdit(tv_mark3b));
		contentValuesmark.put(SSConstants.MARK3C, getMarksfromEdit(tv_mark3c));

		// Marks4
		contentValuesmark.put(SSConstants.MARK4A, getMarksfromEdit(tv_mark4a));
		contentValuesmark.put(SSConstants.MARK4B, getMarksfromEdit(tv_mark4b));
		contentValuesmark.put(SSConstants.MARK4C, getMarksfromEdit(tv_mark4c));

		// Marks5
		contentValuesmark.put(SSConstants.MARK5A, getMarksfromEdit(tv_mark5a));
		contentValuesmark.put(SSConstants.MARK5B, getMarksfromEdit(tv_mark5b));
		contentValuesmark.put(SSConstants.MARK5C, getMarksfromEdit(tv_mark5c));

		// Marks6
		contentValuesmark.put(SSConstants.MARK6A, getMarksfromEdit(tv_mark6a));
		contentValuesmark.put(SSConstants.MARK6B, getMarksfromEdit(tv_mark6b));
		contentValuesmark.put(SSConstants.MARK6C, getMarksfromEdit(tv_mark6c));

		// Marks7
		contentValuesmark.put(SSConstants.MARK7A, getMarksfromEdit(tv_mark7a));
		contentValuesmark.put(SSConstants.MARK7B, getMarksfromEdit(tv_mark7b));
		contentValuesmark.put(SSConstants.MARK7C, getMarksfromEdit(tv_mark7c));

		// Marks8
		contentValuesmark.put(SSConstants.MARK8A, getMarksfromEdit(tv_mark8a));
		contentValuesmark.put(SSConstants.MARK8B, getMarksfromEdit(tv_mark8b));
		contentValuesmark.put(SSConstants.MARK8C, getMarksfromEdit(tv_mark8c));

		row1Total_();
		row_2_3_Total_();
		row_4_5_Total_();
		row_6_7_Total_();
		row_8_9_Total_();
		row_10_11_Total_();

		contentValuesmark.put(SSConstants.R1_TOTAL,
				_mark1_total);
		
		contentValuesmark.put(SSConstants.R2_3TOTAL,
				_mark_2_3_total);
		
		contentValuesmark.put(SSConstants.R4_5TOTAL,
				_mark_4_5_total);
		
		contentValuesmark.put(SSConstants.R6_7TOTAL,
				_mark_6_7_total);
		
		contentValuesmark.put(SSConstants.R8_9TOTAL,
				_mark_8_9_total);
		
		contentValuesmark.put(SSConstants.R10_11TOTAL,
				_mark_10_11_total);
		
		contentValuesmark.put(SSConstants.GRAND_TOTAL_MARK,
				_grand_toal);
		insertToDB(contentValuesmark);
  
	}
	
	private String getMarksfromEdit(EditText ptext){
		if(!TextUtils.isEmpty(ptext.getText().toString().trim())){
			return ptext.getText().toString().trim();
		}
			return null;
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
			intent = new Intent(this, Scrutiny_AddScriptSummary_R13_Btech.class);
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

	// check count of corrected questions
//	private int correctedQuesCount() {
//		int count = 0;
//
//		if (!TextUtils.isEmpty(tv_mark1_total.getText().toString().trim())) {
//			count++;
//		}
//
//		if (!TextUtils.isEmpty(tv_mark_2_3_total.getText().toString().trim())) {
//			count++;
//		}
//
//		if (!TextUtils.isEmpty(tv_mark_4_5_total.getText().toString().trim())) {
//			count++;
//		}
//
//		if (!TextUtils.isEmpty(tv_mark_6_7_total.getText().toString().trim())) {
//			count++;
//		}
//
//		if (!TextUtils.isEmpty(tv_mark_8_9_total.getText().toString().trim())) {
//			count++;
//		}
//		if (!TextUtils.isEmpty(tv_mark_10_11_total.getText().toString().trim())) {
//			count++;
//		}
//
//		return count;
//
//	}

	// set text to focused view on click
	private void setTextToFocusedView(String text) {
		View focusedView = getCurrentFocus();
		if (focusedView != null && focusedView instanceof EditText) {
			EditText et_focusedView = ((EditText) focusedView);
			// check whether subject code is special case
			max_mark = setMaxValueForSubjCodeSpecialCase(et_focusedView);

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
					if (_row_total > rowTotalLimit(et_focusedView, text)) {
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
							+ text) > rowTotalLimit(et_focusedView, text)) {
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
		Log.v("" + Float.parseFloat(sss), "" + Float.parseFloat(""+GrandTotalLimit));
		if (Float.parseFloat(sss) > Float.parseFloat(""+GrandTotalLimit)) {
			View focusedView = getCurrentFocus();
			if (focusedView != null && focusedView instanceof EditText) {
				alertForInvalidMark(focusedView, true, "Total Exceeds "+""+GrandTotalLimit);
			}
		} else {
//			tv_grand_toal.setText(sss);
//			// setRemarkInContentValue2(tv_grand_toal, sss, false, false);
//			marksArray.put(tv_grand_toal.getId(), sss);
//			tv_grand_toal.setSelection(sss.length());
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
//			marksArray.put(tv_grand_toal.getId(), edittext.getText().toString()
//					.trim());
		}
	}

	// call this method when clicked on Submit button
	private void submit(final View view) {
		storeRemarksDB();
		int orangeColCount = getOrangeRemarkCount();
		Log.v("orangeColCount", ""+orangeColCount);
		if (orangeColCount > 0) {  
			showAlertForPendingCorrections(getString(R.string.alert_pend_corr),
					getString(R.string.alert_dialog_ok));
		} else {
			if (/*(correctedQuesCount() > 4)
					||*/ (!(scrutinizedStatusInObsMode == 1 || scrutinizedStatusInObsMode == 2) && !isAddScript)) {
				// new TempDatabase(this).deleteRow();
				showAlert(getString(R.string.alert_submit_corr_marks),
						getString(R.string.alert_dialog_ok),  
						getString(R.string.alert_dialog_cancel), true);
			} else {
				showAlertForLessthan5();
			}
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
		case R.id.q1_e:
			onCellClick(SSConstants.MARK1E, SSConstants.M1E_REMARK, v);
			break;
		
		  case R.id.q1_f: 
			  onCellClick(SSConstants.MARK1F,
		  SSConstants.M1F_REMARK, v);
			  break;
			  case R.id.q1_g:
		  onCellClick(SSConstants.MARK1G, SSConstants.M1G_REMARK, v);
		  break;
		  case R.id.q1_h:
			  onCellClick(SSConstants.MARK1H,
		  SSConstants.M1H_REMARK, v); 
			  break;
			  case R.id.q1_i:
		  onCellClick(SSConstants.MARK1I, SSConstants.M1I_REMARK, v); 
		  break;
		  case R.id.q1_j:
			  onCellClick(SSConstants.MARK1J,
		  SSConstants.M1J_REMARK, v);
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

		case R.id.q3_a:
			onCellClick(SSConstants.MARK3A, SSConstants.M3A_REMARK, v);
			break;
		case R.id.q3_b:
			onCellClick(SSConstants.MARK3B, SSConstants.M3B_REMARK, v);
			break;
		case R.id.q3_c:
			onCellClick(SSConstants.MARK3C, SSConstants.M3C_REMARK, v);
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

		case R.id.q5_a:
			onCellClick(SSConstants.MARK5A, SSConstants.M5A_REMARK, v);
			break;
		case R.id.q5_b:
			onCellClick(SSConstants.MARK5B, SSConstants.M5B_REMARK, v);
			break;
		case R.id.q5_c:
			onCellClick(SSConstants.MARK5C, SSConstants.M5C_REMARK, v);
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

		case R.id.q7_a:
			onCellClick(SSConstants.MARK7A, SSConstants.M7A_REMARK, v);
			break;
		case R.id.q7_b:
			onCellClick(SSConstants.MARK7B, SSConstants.M7B_REMARK, v);
			break;
		case R.id.q7_c:
			onCellClick(SSConstants.MARK7C, SSConstants.M7C_REMARK, v);
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

		case R.id.q9_a:
			onCellClick(SSConstants.MARK9A, SSConstants.M9A_REMARK, v);
			break;
		case R.id.q9_b:
			onCellClick(SSConstants.MARK9B, SSConstants.M9B_REMARK, v);
			break;
		case R.id.q9_c:
			onCellClick(SSConstants.MARK9C, SSConstants.M9C_REMARK, v);
			break;

		case R.id.q10_a:
			onCellClick(SSConstants.MARK10A, SSConstants.M10A_REMARK, v);
			break;
		case R.id.q10_b:
			onCellClick(SSConstants.MARK10B, SSConstants.M10B_REMARK, v);
			break;
		case R.id.q10_c:
			onCellClick(SSConstants.MARK10C, SSConstants.M10C_REMARK, v);
			break;

		case R.id.q11_a:
			onCellClick(SSConstants.MARK11A, SSConstants.M11A_REMARK, v);
			break;
		case R.id.q11_b:
			onCellClick(SSConstants.MARK11B, SSConstants.M11B_REMARK, v);
			break;
		case R.id.q11_c:
			onCellClick(SSConstants.MARK11C, SSConstants.M11C_REMARK, v);
			break;

		case R.id.q1_total:
			onTotalCellClick(SSConstants.R1_TOTAL, SSConstants.R1_REMARK, v);
			break;

		case R.id.q2_3_total:
			onTotalCellClick(SSConstants.R2_3TOTAL, SSConstants.R2_REMARK, v);
			break;

		case R.id.q4_5_total:
			onTotalCellClick(SSConstants.R4_5TOTAL, SSConstants.R4_REMARK, v);
			break;

		case R.id.q6_7_total:
			onTotalCellClick(SSConstants.R6_7TOTAL, SSConstants.R6_REMARK, v);
			break;

		case R.id.q8_9_total:
			onTotalCellClick(SSConstants.R8_9TOTAL, SSConstants.R8_REMARK, v);
			break;
		case R.id.q10_11_total:
			onTotalCellClick(SSConstants.R10_11TOTAL, SSConstants.R10_REMARK, v);
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
		//	if (!getRemarkComplete()) {
				calculateGrandTotal();
		//		if (!setSampleRemark1(tv_grand_toal)) {
					submit(v);
//				} else {
//					/*Toast.makeText(Scrutiny_MarkDialogCorrection_R13_Mtech.this,
//							"Please calculate Best of 5 before Submit...!",
//							Toast.LENGTH_LONG).show();*/
//					alertMessageForAnyChange(getString(R.string.app_name),
//							getString(R.string.grand_mis_match), false);
//				}
//			} else {
//				alertMessageForAnyChange(getString(R.string.app_name),
//						getString(R.string.mis_match), false);
//			}
			break;
		case R.id.btn_submit1:
			// set false since number layout is not inflated/attached
//			if (!getRemarkComplete()) {
//				if (!setSampleRemark1(tv_grand_toal)) {
					submit(v);
//				} else {
//					/*Toast.makeText(Scrutiny_MarkDialogCorrection_R13_Mtech.this,
//							"Please calculate Best of 5 before Submit...!",
//							Toast.LENGTH_LONG).show();*/ 
//					alertMessageForAnyChange(getString(R.string.app_name),
//							getString(R.string.grand_mis_match), false);
//				}
//			} else {
//				alertMessageForAnyChange(getString(R.string.app_name),
//						getString(R.string.mis_match), false);
//			}
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

//	private boolean getRemarkComplete() {
//		// TODO Auto-generated method
//
//		Boolean checkRemark = false;
//		if (setSampleRemark(tv_mark1_total))
//			checkRemark = true;
//		if (setSampleRemark(tv_mark_2_3_total))
//			checkRemark = true;
//		if (setSampleRemark(tv_mark_4_5_total))
//			checkRemark = true;
//		if (setSampleRemark(tv_mark_6_7_total))      
//			checkRemark = true;
//		if (setSampleRemark(tv_mark_8_9_total))
//			checkRemark = true;
//		if (setSampleRemark(tv_mark_10_11_total))
//			checkRemark = true;
//		
//
//		return checkRemark;
//	}

	protected Boolean setSampleRemark(EditText mEditText) {
		Boolean check = false;
		if (mEditText.isFocusable()) {
			String text = "" + mEditText.getText().toString().trim();
			String tag = "" + mEditText.getTag().toString().trim();

			if (!TextUtils.isEmpty(text))
				text = "" + Float.parseFloat(text);
			if (!TextUtils.isEmpty(tag))
				tag = "" + Float.parseFloat(tag);
		//	tv_grand_toal.requestFocus();
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
		max_mark = setMaxValueForSubjCodeSpecialCase(((EditText) view));
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
							if (val > rowTotalLimit(view, editMark)) {

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

		case R.id.q1_e:
			setRemarkInContentValue3(SSConstants.M1E_REMARK,
					SSConstants.MARK1E, remarkOrMark, setRemark, insertToTempDB);
			break;

		
		 case R.id.q1_f: setRemarkInContentValue3(SSConstants.M1F_REMARK,
		  SSConstants.MARK1F, remarkOrMark, setRemark, insertToTempDB); break;
		  
		  case R.id.q1_g: setRemarkInContentValue3(SSConstants.M1G_REMARK,
		  SSConstants.MARK1G, remarkOrMark, setRemark, insertToTempDB); break;
		  
		  case R.id.q1_h: setRemarkInContentValue3(SSConstants.M1H_REMARK,
		  SSConstants.MARK1H, remarkOrMark, setRemark, insertToTempDB); break;
		  
		  case R.id.q1_i: setRemarkInContentValue3(SSConstants.M1I_REMARK,
		  SSConstants.MARK1I, remarkOrMark, setRemark, insertToTempDB); break;
		  
		  case R.id.q1_j: setRemarkInContentValue3(SSConstants.M1J_REMARK,
		  SSConstants.MARK1J, remarkOrMark, setRemark, insertToTempDB); break;
		 
		// Q2
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
		// Q3
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
		// Q4
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
		// Q5 n  
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
		// Q6
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
		// Q7
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
		// Q8
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
		// Q9
		case R.id.q9_a:
			setRemarkInContentValue3(SSConstants.M9A_REMARK,
					SSConstants.MARK9A, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q9_b:
			setRemarkInContentValue3(SSConstants.M9B_REMARK,
					SSConstants.MARK9B, remarkOrMark, setRemark, insertToTempDB);

			break;
		case R.id.q9_c:
			setRemarkInContentValue3(SSConstants.M9C_REMARK,
					SSConstants.MARK9C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q10
		case R.id.q10_a:
			setRemarkInContentValue3(SSConstants.M10A_REMARK,
					SSConstants.MARK10A, remarkOrMark, setRemark,
					insertToTempDB);

			break;
		case R.id.q10_b:
			setRemarkInContentValue3(SSConstants.M10B_REMARK,
					SSConstants.MARK10B, remarkOrMark, setRemark,
					insertToTempDB);

			break;
		case R.id.q10_c:
			setRemarkInContentValue3(SSConstants.M10C_REMARK,
					SSConstants.MARK10C, remarkOrMark, setRemark,
					insertToTempDB);
			break;
		// Q11
		case R.id.q11_a:
			setRemarkInContentValue3(SSConstants.M11A_REMARK,
					SSConstants.MARK11A, remarkOrMark, setRemark,
					insertToTempDB);

			break;
		case R.id.q11_b:
			setRemarkInContentValue3(SSConstants.M11B_REMARK,
					SSConstants.MARK11B, remarkOrMark, setRemark,
					insertToTempDB);

			break;
		case R.id.q11_c:
			setRemarkInContentValue3(SSConstants.M11C_REMARK,
					SSConstants.MARK11C, remarkOrMark, setRemark,
					insertToTempDB);
			break;

		case R.id.q1_total:
			setRemarkInContentValue3(SSConstants.R1_REMARK,
					SSConstants.R1_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q2_3_total:
			setRemarkInContentValue3(SSConstants.R2_REMARK,
					SSConstants.R2_3TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q4_5_total:
			setRemarkInContentValue3(SSConstants.R4_REMARK,
					SSConstants.R4_5TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q6_7_total:
			setRemarkInContentValue3(SSConstants.R6_REMARK,
					SSConstants.R6_7TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q8_9_total:
			setRemarkInContentValue3(SSConstants.R8_REMARK,
					SSConstants.R8_9TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q10_11_total:
			setRemarkInContentValue3(SSConstants.R10_REMARK,
					SSConstants.R10_11TOTAL, remarkOrMark, setRemark,
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
		max_mark = setMaxValueForSubjCodeSpecialCase(((EditText) view2));
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
							// check for sub ques exceeds max marks for special
							// cases
							if (TextUtils.isEmpty(editMarks)
									|| Float.parseFloat(editMarks) > max_mark
									|| !FloatOrIntegerOnlyAllow(editMarks)) {
								alertForInvalidMark(view2, false, "");
							} else {
								// harinath
								float val = checkRowTotalExceedsMaxValue(view2,
										editMarks);
								// // check whether sub total exceeds max marks
								// if (val ==
								// SUB_TOTAL_EXCEEDS_MAX_MARKS_FOR_SPECIAL_CASE_SUBJ_CODES)
								// {
								// alertForInvalidMark(view2, false, "");
								// }
								// check whether row total exceeds max marks
								if (val > rowTotalLimit(view2, editMarks)) {
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
									RemarksArray.put(view2.getId(), SSConstants.GREEN_COLOR);
									
									marksArray.put(tv.getId(), editMarks);
									tv.setText(editMarks);
									
								//	getTotalValue(tv.getId());
									
								}

							}
						} else {
							TextView tv = (TextView) view2;
							if (!(scrutinizedStatusInObsMode == 1 || scrutinizedStatusInObsMode == 2)) {
								changeCellBGToGreen(view2);
							}

							RemarksArray.put(view2.getId(),
									SSConstants.GREEN_COLOR);
							// setRemarkInContentValue2(tv, editMarks, false,
							// false);
							marksArray.put(tv.getId(), editMarks);
							tv.setText(editMarks);

						//	getTotalValue(tv.getId());
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

//	protected void getTotalValue(int id) {
//		// TODO Auto-generated method stub
//		tv_grand_toal.setFocusable(true);
//		tv_grand_toal.setFocusableInTouchMode(true);
//
//		if (id == R.id.q1_a || id == R.id.q1_b || id == R.id.q1_c
//				|| id == R.id.q1_d || id == R.id.q1_e||
//				id == R.id.q1_f || id == R.id.q1_g || id == R.id.q1_h
//				|| id == R.id.q1_i || id == R.id.q1_j) {
//			tv_mark1_total.setText("");
//			tv_mark1_total.setFocusable(true);
//			tv_mark1_total.setFocusableInTouchMode(true);
//			tv_mark1_total.requestFocus();
//			// alertRowEntry(); please enter 1nd row total
//
//		}
//
//		else if (id == R.id.q2_a || id == R.id.q2_b || id == R.id.q2_c
//				|| id == R.id.q3_a || id == R.id.q3_b || id == R.id.q3_c ) {
//			tv_mark_2_3_total.setText("");
//			tv_mark_2_3_total.setFocusable(true);
//			tv_mark_2_3_total.setFocusableInTouchMode(true);
//			tv_mark_2_3_total.requestFocus();
//
//		} else if (id == R.id.q4_a || id == R.id.q4_b || id == R.id.q4_c
//				|| id == R.id.q5_a || id == R.id.q5_b || id == R.id.q5_c ) {
//			tv_mark_4_5_total.setText("");
//			tv_mark_4_5_total.setFocusable(true);
//			tv_mark_4_5_total.setFocusableInTouchMode(true);
//			tv_mark_4_5_total.requestFocus();
//
//		} else if (id == R.id.q6_a || id == R.id.q6_b || id == R.id.q6_c
//				|| id == R.id.q7_a || id == R.id.q7_b || id == R.id.q7_c ) {
//			tv_mark_6_7_total.setText("");
//			tv_mark_6_7_total.setFocusable(true);
//			tv_mark_6_7_total.setFocusableInTouchMode(true);
//			tv_mark_6_7_total.requestFocus();
//
//		}
//
//		else if (id == R.id.q8_a || id == R.id.q8_b || id == R.id.q8_c
//				|| id == R.id.q9_a || id == R.id.q9_b || id == R.id.q9_c ) {
//
//			tv_mark_8_9_total.setText("");
//			tv_mark_8_9_total.setFocusable(true);
//			tv_mark_8_9_total.setFocusableInTouchMode(true);
//			tv_mark_8_9_total.requestFocus();
//			// alertRowEntry(); please enter 2nd row total
//
//		}
//
//		else if (id == R.id.q10_a || id == R.id.q10_b || id == R.id.q10_c
//				|| id == R.id.q11_a || id == R.id.q11_b || id == R.id.q11_c ) {
//			tv_mark_10_11_total.setText("");
//			tv_mark_10_11_total.setFocusable(true);
//			tv_mark_10_11_total.setFocusableInTouchMode(true);
//			tv_mark_10_11_total.requestFocus();
//			// alertRowEntry(); please enter 6nd row total
//
//		}
//
//	}

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

		try{
			if(!clickFind){  
		//	setTextToFocusedView(arg0.toString());
			View focusedView = getCurrentFocus();
			if (focusedView != null && focusedView instanceof EditText) {
				EditText et_focusedView = ((EditText) focusedView);
				et_focusedView.removeTextChangedListener(Scrutiny_MarkDialogCorrection_R13.this);
				et_focusedView.setText("");
				et_focusedView.addTextChangedListener(Scrutiny_MarkDialogCorrection_R13.this);
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
		if(R13BTech){
			String mark1f = tv_mark1f.getText().toString().trim();
			if (TextUtils.isEmpty(mark1f)) {
				mark1f = null;
			}
			String mark1g = tv_mark1g.getText().toString().trim();
			if (TextUtils.isEmpty(mark1g)) {
				mark1g = null;
			}
			String mark1h = tv_mark1h.getText().toString().trim();
			if (TextUtils.isEmpty(mark1h)) {
				mark1h = null;
			}
			String mark1i = tv_mark1i.getText().toString().trim();
			if (TextUtils.isEmpty(mark1i)) {
				mark1i = null;
			}
			String mark1j = tv_mark1j.getText().toString().trim();
			if (TextUtils.isEmpty(mark1j)) {
				mark1j = null;
			}

			if (!TextUtils.isEmpty(mark1a) || !TextUtils.isEmpty(mark1b)
					|| !TextUtils.isEmpty(mark1c) || !TextUtils.isEmpty(mark1d)
					|| !TextUtils.isEmpty(mark1e)
					|| !TextUtils.isEmpty(mark1f)
					|| !TextUtils.isEmpty(mark1g) || !TextUtils.isEmpty(mark1h)
					|| !TextUtils.isEmpty(mark1i) || !TextUtils.isEmpty(mark1j)) {

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
										.isEmpty(mark = mark1e))) ? "0" : mark)
								+ Float.parseFloat(((TextUtils
										.isEmpty(mark = mark1f))) ? "0" : mark)
								+ Float.parseFloat(((TextUtils
										.isEmpty(mark = mark1g))) ? "0" : mark)
								+ Float.parseFloat(((TextUtils
										.isEmpty(mark = mark1h))) ? "0" : mark)
								+ Float.parseFloat(((TextUtils
										.isEmpty(mark = mark1i))) ? "0" : mark)
								+ Float.parseFloat(((TextUtils
										.isEmpty(mark = mark1j))) ? "0" : mark));

			}
			}else{
			
			if (!TextUtils.isEmpty(mark1a) || !TextUtils.isEmpty(mark1b)
					|| !TextUtils.isEmpty(mark1c) || !TextUtils.isEmpty(mark1d)
					|| !TextUtils.isEmpty(mark1e)){

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
			}
		if (!TextUtils.isEmpty(mark)) {
			if (Float.parseFloat(mark) > Float.parseFloat(""+A1TotalLimit)) {
				Toast.makeText(this, "Total Exceeds "+A1TotalLimit, Toast.LENGTH_SHORT)
						.show();
				View focusedView = getCurrentFocus();
				if (focusedView != null && focusedView instanceof EditText) {
					alertForInvalidMark(focusedView, true, ""+A1TotalLimit);
				}
			} else {
				_mark1_total=mark;
			}
		} else {
			_mark1_total=null;
		}
		return mark;
	}

	private String row_2_3_Total_() {
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

			if (!TextUtils.isEmpty(mark2a) || !TextUtils.isEmpty(mark2b)
					|| !TextUtils.isEmpty(mark2c) || !TextUtils.isEmpty(mark3a)
					|| !TextUtils.isEmpty(mark3b) || !TextUtils.isEmpty(mark3c)) {
				float _mark2a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark2a))) ? "0"
								: mark)));
				float _mark2b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark2b))) ? "0"
								: mark)));
				float _mark2c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark2c))) ? "0"
								: mark)));

				float _mark3a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark3a))) ? "0"
								: mark)));
				float _mark3b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark3b))) ? "0"
								: mark)));
				float _mark3c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark3c))) ? "0"
								: mark)));

				float _marks2 = _mark2a + _mark2b + _mark2c;
				float _marks3 = _mark3a + _mark3b + _mark3c;
				String _value=String.valueOf(_marks2 > _marks3 ? _marks2 : _marks3);
				if (!TextUtils.isEmpty(_value)) {
					if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
						Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
								.show();
						View focusedView = getCurrentFocus();
						if (focusedView != null && focusedView instanceof EditText) {

							alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
						}
					} else {
						_mark_2_3_total=_value;
					}
				} else {
					_mark_2_3_total=null;
				}
				
				return _value;
			}
		
	
		_mark_2_3_total=mark;
		return mark;
	}

	private String row_4_5_Total_() {
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

			if (!TextUtils.isEmpty(mark4a) || !TextUtils.isEmpty(mark4b)
					|| !TextUtils.isEmpty(mark4c) || !TextUtils.isEmpty(mark5a)
					|| !TextUtils.isEmpty(mark5b) || !TextUtils.isEmpty(mark5c)) {
				float _mark4a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark4a))) ? "0"
								: mark)));
				float _mark4b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark4b))) ? "0"
								: mark)));
				float _mark4c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark4c))) ? "0"
								: mark)));

				float _mark5a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark5a))) ? "0"
								: mark)));
				float _mark5b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark5b))) ? "0"
								: mark)));
				float _mark5c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark5c))) ? "0"
								: mark)));

				float _marks4 = _mark4a + _mark4b + _mark4c;
				float _marks5 = _mark5a + _mark5b + _mark5c;
				
				
				String _value=String.valueOf(_marks4 > _marks5 ? _marks4 : _marks5);
				if (!TextUtils.isEmpty(_value)) {
					if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
						Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
								.show();
						View focusedView = getCurrentFocus();
						if (focusedView != null && focusedView instanceof EditText) {

							alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
						}
					} else {
						_mark_4_5_total=_value;
					}
				} else {
					_mark_4_5_total=null;
				}
				
				return _value;
			}
		
		
		_mark_4_5_total=mark;
				
		return mark;
	}

	private String row_6_7_Total_() {
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

			if (!TextUtils.isEmpty(mark6a) || !TextUtils.isEmpty(mark6b)
					|| !TextUtils.isEmpty(mark6c) || !TextUtils.isEmpty(mark7a)
					|| !TextUtils.isEmpty(mark7b) || !TextUtils.isEmpty(mark7c)) {
				float _mark6a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark6a))) ? "0"
								: mark)));
				float _mark6b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark6b))) ? "0"
								: mark)));
				float _mark6c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark6c))) ? "0"
								: mark)));

				float _mark7a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark7a))) ? "0"
								: mark)));
				float _mark7b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark7b))) ? "0"
								: mark)));
				float _mark7c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark7c))) ? "0"
								: mark)));

				float _marks6 = _mark6a + _mark6b + _mark6c;
				float _marks7 = _mark7a + _mark7b + _mark7c;
				String _value=String.valueOf(_marks6 > _marks7 ? _marks6 : _marks7);
				if (!TextUtils.isEmpty(_value)) {
					if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
						Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
								.show();
						View focusedView = getCurrentFocus();
						if (focusedView != null && focusedView instanceof EditText) {

							alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
						}
					} else {
						_mark_6_7_total=_value;
					}
				} else {
					_mark_6_7_total=null;
				}
				
				return _value;
			}
		
		_mark_6_7_total=mark;
		return mark;
	}
		

	private String row_8_9_Total_() {
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

		String mark9a = tv_mark9a.getText().toString().trim();
		if (TextUtils.isEmpty(mark9a)) {
			mark9a = null;
		}
		String mark9b = tv_mark9b.getText().toString().trim();
		if (TextUtils.isEmpty(mark9b)) {
			mark9b = null;
		}
		String mark9c = tv_mark9c.getText().toString().trim();
		if (TextUtils.isEmpty(mark9c)) {
			mark9c = null;
		}

		// if Regulation R13 BTech
			if (!TextUtils.isEmpty(mark8a) || !TextUtils.isEmpty(mark8b)
					|| !TextUtils.isEmpty(mark8c) || !TextUtils.isEmpty(mark9a)
					|| !TextUtils.isEmpty(mark9b) || !TextUtils.isEmpty(mark9c)) {
				float _mark8a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark8a))) ? "0"
								: mark)));
				float _mark8b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark8b))) ? "0"
								: mark)));
				float _mark8c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark8c))) ? "0"
								: mark)));

				float _mark9a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark9a))) ? "0"
								: mark)));
				float _mark9b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark9b))) ? "0"
								: mark)));
				float _mark9c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark9c))) ? "0"
								: mark)));

				float _marks8 = _mark8a + _mark8b + _mark8c;
				float _marks9 = _mark9a + _mark9b + _mark9c;
				String _value=String.valueOf(_marks8 > _marks9 ? _marks8 : _marks9);
				if (!TextUtils.isEmpty(_value)) {
					if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
						Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
								.show();
						View focusedView = getCurrentFocus();
						if (focusedView != null && focusedView instanceof EditText) {

							alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
						}
					} else {
						_mark_8_9_total=_value;
					}
				} else {
					_mark_8_9_total=null;
				}
				
				return _value;
			}
		_mark_8_9_total=mark;
		return mark;
	}
			

	private String row_10_11_Total_() {
		String mark = null;
		String mark10a = tv_mark10a.getText().toString().trim();
		if (TextUtils.isEmpty(mark10a)) {
			mark10a = null;
		}
		String mark10b = tv_mark10b.getText().toString().trim();
		if (TextUtils.isEmpty(mark10b)) {
			mark10b = null;
		}
		String mark10c = tv_mark10c.getText().toString().trim();
		if (TextUtils.isEmpty(mark10c)) {
			mark10c = null;
		}

		String mark11a = tv_mark11a.getText().toString().trim();
		if (TextUtils.isEmpty(mark11a)) {
			mark11a = null;
		}
		String mark11b = tv_mark11b.getText().toString().trim();
		if (TextUtils.isEmpty(mark11b)) {
			mark11b = null;
		}
		String mark11c = tv_mark11c.getText().toString().trim();
		if (TextUtils.isEmpty(mark11c)) {
			mark11c = null;
		}

			if (!TextUtils.isEmpty(mark10a) || !TextUtils.isEmpty(mark10b)
					|| !TextUtils.isEmpty(mark10c)
					|| !TextUtils.isEmpty(mark11a)
					|| !TextUtils.isEmpty(mark11b)
					|| !TextUtils.isEmpty(mark11c)) {
				float _mark10a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark10a))) ? "0"
								: mark)));
				float _mark10b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark10b))) ? "0"
								: mark)));
				float _mark10c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark10c))) ? "0"
								: mark)));

				float _mark11a = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark11a))) ? "0"
								: mark)));
				float _mark11b = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark11b))) ? "0"
								: mark)));
				float _mark11c = Float.valueOf(String.valueOf(Float
						.parseFloat(((TextUtils.isEmpty(mark = mark11c))) ? "0"
								: mark)));

				float _marks10 = _mark10a + _mark10b + _mark10c;
				float _marks11 = _mark11a + _mark11b + _mark11c;
				String _value= String
						.valueOf(_marks10 > _marks11 ? _marks10 : _marks11);
				if (!TextUtils.isEmpty(_value)) {
					if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
						Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
								.show();
						View focusedView = getCurrentFocus();
						if (focusedView != null && focusedView instanceof EditText) {

							alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
						}
					} else {
						_mark_10_11_total=_value;
					}
				} else {
					_mark_10_11_total=null;
				}
				  
				return _value;
			}
		        
		_mark_10_11_total=mark;
		return mark;
	}

	/*
	 * private void setTotal(String total, TextView pTextView) { // if
	 * (!TextUtils.isEmpty(total)) { // pTextView.setText(total); // } else {
	 * pTextView.setText(total); // } }
	 */
// set total -for edit text set tag --needed change
	private void setTotal(String total, TextView pTextView) {
		
		// if (!TextUtils.isEmpty(total)) {
		// pTextView.setText(total);
		// } else {
		if (pTextView.getId() == R.id.q1_total) {
			if (!TextUtils.isEmpty(total)) {
				if (Float.parseFloat(total) > Float.parseFloat(""+A1TotalLimit)) {
					Toast.makeText(this, "Total Exceeds "+A1TotalLimit, Toast.LENGTH_SHORT)
							.show();

				} else {
					pTextView.setTag(total);
				}
			} else {
				pTextView.setTag("");
			}
		}

		else if ((pTextView.getId() == R.id.q2_3_total)
				|| (pTextView.getId() == R.id.q4_5_total)
				|| (pTextView.getId() == R.id.q6_7_total)
				|| (pTextView.getId() == R.id.q8_9_total)
				|| (pTextView.getId() == R.id.q10_11_total)) {
			if (!TextUtils.isEmpty(total)) {
				if (Float.parseFloat(total) > Float.parseFloat(""+RowTotalLimit)) {
					Toast.makeText(this, "Total Exceeds "+""+RowTotalLimit, Toast.LENGTH_SHORT)
							.show();

				} else {
					 pTextView.setTag(total);
				}
			} else {
				pTextView.setTag("");
			}  
		}
		// }
	}

	private void calculateTotal() {
		row1Total_();
		row_2_3_Total_();
		row_4_5_Total_();
		row_6_7_Total_();
		row_8_9_Total_();
		row_10_11_Total_();

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

		myAlertDialog.show();
	}

	private int getOrangeRemarkCount() {

		String where = Scrutiny_TempDatabase._SNo + "= '1' AND ("
				+ SSConstants.M1A_REMARK + "  = '1'  OR "
				+ SSConstants.M1B_REMARK + "  = '1'  OR "
				+ SSConstants.M1C_REMARK + "  = '1'  OR "
				+ SSConstants.M1D_REMARK + "  = '1'  OR "
				+ SSConstants.M1E_REMARK + "  = '1'  OR "
				+ SSConstants.M1F_REMARK + "  = '1'  OR "
				+ SSConstants.M1G_REMARK + "  = '1'  OR "
				+ SSConstants.M1H_REMARK + "  = '1'  OR "
				+ SSConstants.M1I_REMARK + "  = '1'  OR "
				+ SSConstants.M1J_REMARK + "  = '1'  OR "

				+ SSConstants.M2E_REMARK + "  = '1'  OR "
				+ SSConstants.M3E_REMARK + "  = '1'  OR "
				+ SSConstants.M4E_REMARK + "  = '1'  OR "
				+ SSConstants.M5E_REMARK + "  = '1'  OR "
				+ SSConstants.M6E_REMARK + "  = '1'  OR "
				+ SSConstants.M7E_REMARK + "  = '1'  OR "
				+ SSConstants.M8E_REMARK + "  = '1'  OR "

				+ SSConstants.R1_REMARK + "  = '1'  OR "
				+ SSConstants.R2_REMARK + "  = '1'  OR "
				+ SSConstants.R3_REMARK + "  = '1'  OR "
				+ SSConstants.R4_REMARK + "  = '1'  OR "
				+ SSConstants.R5_REMARK + "  = '1'  OR "
				+ SSConstants.R6_REMARK + "  = '1'  OR "
				+ SSConstants.R7_REMARK + "  = '1'  OR "
				+ SSConstants.R8_REMARK + "  = '1'  OR "
				+ SSConstants.R9_REMARK + "  = '1'  OR "
				+ SSConstants.R10_REMARK + "  = '1'  OR "
				+ SSConstants.R11_REMARK + "  = '1'  OR "

				+ SSConstants.M2A_REMARK + "  = '1'  OR "
				+ SSConstants.M2B_REMARK + "  = '1'  OR "
				+ SSConstants.M2C_REMARK + "  = '1'  OR "
				+ SSConstants.M2D_REMARK + "  = '1'  OR "

				+ SSConstants.M3A_REMARK + "  = '1'  OR "
				+ SSConstants.M3B_REMARK + "  = '1'  OR "
				+ SSConstants.M3C_REMARK + "  = '1'  OR "
				+ SSConstants.M3D_REMARK + "  = '1'  OR "

				+ SSConstants.M4A_REMARK + "  = '1'  OR "
				+ SSConstants.M4B_REMARK + "  = '1'  OR "
				+ SSConstants.M4C_REMARK + "  = '1'  OR "
				+ SSConstants.M4D_REMARK + "  = '1'  OR "

				+ SSConstants.M5A_REMARK + "  = '1'  OR "
				+ SSConstants.M5B_REMARK + "  = '1'  OR "
				+ SSConstants.M5C_REMARK + "  = '1'  OR "
				+ SSConstants.M5D_REMARK + "  = '1'  OR "

				+ SSConstants.M6A_REMARK + "  = '1'  OR "
				+ SSConstants.M6B_REMARK + "  = '1'  OR "
				+ SSConstants.M6C_REMARK + "  = '1'  OR "
				+ SSConstants.M6D_REMARK + "  = '1'  OR "

				+ SSConstants.M7A_REMARK + "  = '1'  OR "
				+ SSConstants.M7B_REMARK + "  = '1'  OR "
				+ SSConstants.M7C_REMARK + "  = '1'  OR "
				+ SSConstants.M7D_REMARK + "  = '1'  OR "

				+ SSConstants.M8A_REMARK + "  = '1'  OR "
				+ SSConstants.M8B_REMARK + "  = '1'  OR "
				+ SSConstants.M8C_REMARK + "  = '1'  OR "
				+ SSConstants.M8D_REMARK + "  = '1'  OR "

				+ SSConstants.M9A_REMARK + "  = '1'  OR "
				+ SSConstants.M9B_REMARK + "  = '1'  OR "
				+ SSConstants.M9C_REMARK + "  = '1'  OR "

				+ SSConstants.M10A_REMARK + "  = '1' OR "
				+ SSConstants.M10B_REMARK + "  = '1' OR "
				+ SSConstants.M10C_REMARK + "  = '1' OR "

				+ SSConstants.M11A_REMARK + "  = '1' OR "
				+ SSConstants.M11B_REMARK + "  = '1' OR "
				+ SSConstants.M11C_REMARK + "  = '1' )";

		Cursor _cursor = new Scrutiny_TempDatabase(this).getRow(where);
		int count = _cursor.getCount();
		_cursor.close();
		return count;
	}

	private void setContentValuesOnFinalSubmission() {
		String mark;
		ContentValues _contentValues = new ContentValues();
		// check whether text is empty if so set to null
		// marks1
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
				SSConstants.MARK1E,
				((TextUtils.isEmpty(mark = tv_mark1e.getText().toString()
						.trim()))) ? "null" : mark);
		if (R13BTech) {
		  _contentValues.put( SSConstants.MARK1F, ((TextUtils.isEmpty(mark =
		  tv_mark1f.getText().toString() .trim()))) ? "null" : mark);
		  _contentValues.put( SSConstants.MARK1G, ((TextUtils.isEmpty(mark =
		  tv_mark1g.getText().toString() .trim()))) ? "null" : mark);
		 _contentValues.put( SSConstants.MARK1H, ((TextUtils.isEmpty(mark =
		  tv_mark1h.getText().toString() .trim()))) ? "null" : mark);
		  _contentValues.put( SSConstants.MARK1I, ((TextUtils.isEmpty(mark =
		  tv_mark1i.getText().toString() .trim()))) ? "null" : mark);
		  _contentValues.put( SSConstants.MARK1J, ((TextUtils.isEmpty(mark =
		 tv_mark1j.getText().toString() .trim()))) ? "null" : mark);
		}
		// marks_2_3
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

		// marks_4_5
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

		// marks_6_7
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

		// marks_8_9
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
				SSConstants.MARK9A,
				((TextUtils.isEmpty(mark = tv_mark9a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK9B,
				((TextUtils.isEmpty(mark = tv_mark9b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK9C,
				((TextUtils.isEmpty(mark = tv_mark9c.getText().toString()
						.trim()))) ? "null" : mark);
		// marks_10_11
		_contentValues.put(
				SSConstants.MARK10A,
				((TextUtils.isEmpty(mark = tv_mark10a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK10B,
				((TextUtils.isEmpty(mark = tv_mark10b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK10C,
				((TextUtils.isEmpty(mark = tv_mark10c.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SSConstants.MARK11A,
				((TextUtils.isEmpty(mark = tv_mark11a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK11B,
				((TextUtils.isEmpty(mark = tv_mark11b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SSConstants.MARK11C,
				((TextUtils.isEmpty(mark = tv_mark11c.getText().toString()
						.trim()))) ? "null" : mark);

		// total marks
		_contentValues.put(
				SSConstants.R1_TOTAL,_mark1_total);
		_contentValues.put(
				SSConstants.R2_3TOTAL,_mark_2_3_total);
		_contentValues.put(
				SSConstants.R4_5TOTAL,_mark_4_5_total);
		_contentValues.put(
				SSConstants.R6_7TOTAL,_mark_6_7_total);
		_contentValues.put(
				SSConstants.R8_9TOTAL,_mark_8_9_total);
		_contentValues.put(
				SSConstants.R10_11TOTAL,
				_mark_10_11_total);

		_contentValues.put(
				SSConstants.GRAND_TOTAL_MARK,_grand_toal);

		insertToDB(_contentValues);

	}

	private boolean FloatOrIntegerOnlyAllow(String inputvalue) {
		boolean flag = false;
  
		switch (max_mark) {

		case 3:
			flag = Pattern.matches("[0-2](\\.(0|5|50|00))|[0-3]", inputvalue);
			break;

		case 4:
			flag = Pattern.matches("[0-3](\\.(0|5|50|00))|[0-4]", inputvalue);
			break;
		
		case 5:
			flag = Pattern.matches("[0-4](\\.(0|5|50|00))|[0-5]", inputvalue);
			break;  

		case 8:
			flag = Pattern.matches("[0-7](\\.(0|5|50|00))|[0-8]", inputvalue);
			break;

		case 10:
			flag = Pattern
					.matches(
							"[0-1][0-1](\\.(0|5|50|00))|[0-1][0-1]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
							inputvalue);
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
break;
		case 25:
			flag = Pattern
					.matches(
							"[0-1][0-9]|[0-1][0-9](\\.(0|5|50|00))|[0-2][0-5]|[0-9]|(.(5|50))|[0-9](\\.(0|5|50|00))",
							inputvalue);
			break;
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
			flag =true;
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
		R13BTech=instanceUtitlity
				.isRegulation_R13_Btech(Scrutiny_MarkDialogCorrection_R13.this);
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

	// ================================================================
	// special case subject code methods
	// ================================================================

	// setting max value for special case subject codes
	private int setMaxValueForSubjCodeSpecialCase(EditText etQuesNo) {
		int _max_value = 60;
		
		if (etQuesNo == tv_mark1a || etQuesNo == tv_mark1b
				|| etQuesNo == tv_mark1c || etQuesNo == tv_mark1d
				|| etQuesNo == tv_mark1e
			 || etQuesNo == tv_mark1f || etQuesNo == tv_mark1g || etQuesNo ==
			 tv_mark1h || etQuesNo == tv_mark1i || etQuesNo == tv_mark1j) {
			_max_value =A1Limit;
		}
//		else if (etQuesNo == tv_mark1_total){
//			_max_value = A1TotalLimit;
//		}
		else {
			_max_value = RowTotalLimit;
		}

		// // 58002
		// else if (subjectCode
		// .equalsIgnoreCase(SEConstants.SUBJ_58002_DESIGN_AND_DRAWING_OF_IRRIGATION_STRUCTURES_1))
		// {
		// if ((etQuesNo == tv_mark1a || etQuesNo == tv_mark1b
		// || etQuesNo == tv_mark1c || etQuesNo == tv_mark1d
		// || etQuesNo == tv_mark2a || etQuesNo == tv_mark2b
		// || etQuesNo == tv_mark2c || etQuesNo == tv_mark2d)) {
		// _max_value =
		// SEConstants.SUBJ_58002_DESIGN_AND_DRAWING_OF_IRRIGATION_STRUCTURES_1_MAX_SUBTOTAL_FOR_1_AND_2;
		// } else if (etQuesNo == tv_mark3a || etQuesNo == tv_mark3b
		// || etQuesNo == tv_mark3c || etQuesNo == tv_mark3d
		// || etQuesNo == tv_mark4a || etQuesNo == tv_mark4b
		// || etQuesNo == tv_mark4c || etQuesNo == tv_mark4d) {
		// _max_value =
		// SEConstants.SUBJ_58002_DESIGN_AND_DRAWING_OF_IRRIGATION_STRUCTURES_1_MAX_SUBTOTAL_FOR_3_AND_4;
		// }
		// }
		//

		return _max_value;
	}

	private void calculateGrandTotal() {

		//	ContentValues _contentValues = new ContentValues();
			BigDecimal roundOffGrandTotal = null;
			float grandTotal = 0;
				String mark;
				ArrayList<Float> listTotalMarks = new ArrayList<Float>();

				if (!TextUtils.isEmpty(_mark1_total)) {
					listTotalMarks.add(Float.valueOf(_mark1_total));
				//	_contentValues.put(SEConstants.R1_TOTAL, _mark1_total);
				}

				if (!TextUtils.isEmpty(_mark_2_3_total)) {
					listTotalMarks.add(Float.valueOf(_mark_2_3_total));
				//	_contentValues.put(SEConstants.R2_3TOTAL, _mark_2_3_total);
				}

				if (!TextUtils.isEmpty(_mark_4_5_total)) {
					listTotalMarks.add(Float.valueOf(_mark_4_5_total));
				//	_contentValues.put(SEConstants.R4_5TOTAL, _mark_4_5_total);
				}

				if (!TextUtils.isEmpty(_mark_6_7_total)) {
					listTotalMarks.add(Float.valueOf(_mark_6_7_total));
				//	_contentValues.put(SEConstants.R6_7TOTAL, _mark_6_7_total);
				}

				if (!TextUtils.isEmpty(_mark_8_9_total)) {
					listTotalMarks.add(Float.valueOf(_mark_8_9_total));
				//	_contentValues.put(SEConstants.R8_9TOTAL, _mark_8_9_total);
				}

				if (!TextUtils.isEmpty(_mark_10_11_total)) {
					listTotalMarks.add(Float.valueOf(_mark_10_11_total));
				//	_contentValues.put(SEConstants.R10_11TOTAL, _mark_10_11_total);
				}

				if (!listTotalMarks.isEmpty()) {
					Collections.sort(listTotalMarks, Collections.reverseOrder());
					if (listTotalMarks.size() < 7) {
						for (int i = 0; i < listTotalMarks.size(); i++) {
							grandTotal += listTotalMarks.get(i);
						}
						roundOffGrandTotal = new BigDecimal(
								Double.toString(grandTotal));
						roundOffGrandTotal = roundOffGrandTotal.setScale(0,
								BigDecimal.ROUND_HALF_UP);
						_grand_toal=String.valueOf(roundOffGrandTotal);
					} else {
						for (int i = 0; i < 6; i++) {
							grandTotal += listTotalMarks.get(i);
						}
						roundOffGrandTotal = new BigDecimal(
								Float.toString(grandTotal));
						roundOffGrandTotal = roundOffGrandTotal.setScale(0,
								BigDecimal.ROUND_HALF_UP);
						_grand_toal=String.valueOf(roundOffGrandTotal);
					}
				} else {
					_grand_toal="0";
				}

		}

	private float rowTotalLimit(View view, String pMarks) {
		float row_max_mark = 0;
			if (view.getId() == R.id.q1_a || view.getId() == R.id.q1_b
					|| view.getId() == R.id.q1_c || view.getId() == R.id.q1_d
					|| view.getId() == R.id.q1_e||view.getId() == R.id.q1_f ||
					view.getId() == R.id.q1_g || view.getId() == R.id.q1_h
					|| view.getId() == R.id.q1_i || view.getId() == R.id.q1_j) {
				return row_max_mark = A1TotalLimit;
			} else {
				return row_max_mark = RowTotalLimit;
			}
	}

	private float checkRowTotalExceedsMaxValue(View view, String pMarks) {
		String mark;
		float _fl_total_row_mark;
		if (TextUtils.isEmpty(pMarks)) {
			pMarks = "0";
		}
		/*
		 * boolean isRegulation_R13_BTech = true; if (isRegulation_R13_BTech) {
		 * if (view.getId() == R.id.q1_a || view.getId() == R.id.q1_b ||
		 * view.getId() == R.id.q1_c || view.getId() == R.id.q1_d ||
		 * view.getId() == R.id.q1_e || view.getId() == R.id.q1_f ||
		 * view.getId() == R.id.q1_g || view.getId() == R.id.q1_h ||
		 * view.getId() == R.id.q1_i || view.getId() == R.id.q1_j) { return
		 * _fl_total_row_mark = SSConstants.R13_BTECH_MAXSUBTOTAL_1; } else {
		 * return _fl_total_row_mark =
		 * SSConstants.R13_BTECH_MAXSUBTOTAL_2_TO_11; } }
		 */
		switch (view.getId()) {

		case R.id.q1_a:

			_fl_total_row_mark= Float.parseFloat(pMarks)
						+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
								.getText().toString().trim()))) ? "0" : mark)
						+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
								.getText().toString().trim()))) ? "0" : mark)
						+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
								.getText().toString().trim()))) ? "0" : mark)
						+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
								.getText().toString().trim()))) ? "0" : mark)
				  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
				  .getText().toString().trim()))) ? "0" : mark) +
				  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
				  .getText().toString().trim()))) ? "0" : mark) +
				  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
				  .getText().toString().trim()))) ? "0" : mark) +
				  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
				  .getText().toString().trim()))) ? "0" : mark) +
				  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
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
							.getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
			  .getText().toString().trim()))) ? "0" : mark);
			 
			return _fl_total_row_mark;

		case R.id.q1_c:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
			  .getText().toString().trim()))) ? "0" : mark);
			 
			return _fl_total_row_mark;
		case R.id.q1_d:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
			  .getText().toString().trim()))) ? "0" : mark) +
			 Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
			  .getText().toString().trim()))) ? "0" : mark);
			 
			return _fl_total_row_mark;

		case R.id.q1_e:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a
							.getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
			  .getText().toString().trim()))) ? "0" : mark);
			 			return _fl_total_row_mark;

		case R.id.q1_f:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a
							.getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
			 .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
			  .getText().toString().trim()))) ? "0" : mark);
			 
			return _fl_total_row_mark;

		case R.id.q1_g:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a
							.getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
			  .getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
			 .getText().toString().trim()))) ? "0" : mark);
			
			return _fl_total_row_mark;

		case R.id.q1_h:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a
							.getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
			  .getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
			  .getText().toString().trim()))) ? "0" : mark);
			 
			return _fl_total_row_mark;

		case R.id.q1_i:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a
							.getText().toString().trim()))) ? "0" : mark)
			  + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
			 .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1j
			  .getText().toString().trim()))) ? "0" : mark);
			 
			return _fl_total_row_mark;

		case R.id.q1_j:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1c
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1d
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1e
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1a
							.getText().toString().trim()))) ? "0" : mark)
			   + Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1f
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1g
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1h
			  .getText().toString().trim()))) ? "0" : mark) +
			  Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark1i
			  .getText().toString().trim()))) ? "0" : mark);
			 
			return _fl_total_row_mark;

		case R.id.q2_a:

			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q2_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark2a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q2_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark2a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark2b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);

			return _fl_total_row_mark;

		case R.id.q3_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q3_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark3a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q3_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark3a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark3b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);
			return _fl_total_row_mark;

		case R.id.q4_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q4_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark4a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q4_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark4a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark4b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);
			return _fl_total_row_mark;

		case R.id.q5_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q5_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark5a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q5_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark5a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark5b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);
			return _fl_total_row_mark;

		case R.id.q6_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q6_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark6a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q6_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark6a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark6b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);
			return _fl_total_row_mark;

		case R.id.q7_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q7_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark7a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7c
							.getText().toString().trim()))) ? "0" : mark);
			return _fl_total_row_mark;

		case R.id.q7_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark7a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark7b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);
			return _fl_total_row_mark;

		case R.id.q8_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8c
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q8_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark8a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8c
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q8_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark8a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark8b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);

			return _fl_total_row_mark;

		case R.id.q9_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark9b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark9c
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q9_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark9a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark9c
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q9_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark9a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark9b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);

			return _fl_total_row_mark;

		case R.id.q10_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark10b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark10c
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q10_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark10a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark10c
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q10_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark10a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark10b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);

			return _fl_total_row_mark;

		case R.id.q11_a:
			_fl_total_row_mark = Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark11b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark11c
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q11_b:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark11a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark11c
							.getText().toString().trim()))) ? "0" : mark);

			return _fl_total_row_mark;

		case R.id.q11_c:
			_fl_total_row_mark = Float
					.parseFloat(((TextUtils.isEmpty(mark = tv_mark11a.getText()
							.toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(((TextUtils.isEmpty(mark = tv_mark11b
							.getText().toString().trim()))) ? "0" : mark)
					+ Float.parseFloat(pMarks);

			return _fl_total_row_mark;

		default:
			return 0;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus){
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromInputMethod(v
					.getWindowToken(), 0);
			EditText vvv=(EditText) v;
			vvv.setError(null);
			vvv.setText("");
		}
	}
}
