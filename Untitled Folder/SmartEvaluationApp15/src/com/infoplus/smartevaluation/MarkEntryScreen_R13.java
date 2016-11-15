package com.infoplus.smartevaluation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;

public class MarkEntryScreen_R13 extends Activity implements OnClickListener
                  , TextWatcher
{  

	EditText tv_mark1a, tv_mark1b, tv_mark1c, tv_mark1d, tv_mark1e, tv_mark1f,tv_mark1g, tv_mark1h, tv_mark1i, tv_mark1j;

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

//	TextView tv_mark1_total, tv_mark_2_3_total, tv_mark_4_5_total,
//			tv_mark_6_7_total, tv_mark_8_9_total, tv_mark_10_11_total;
	Boolean R13BTech=false;
	Boolean R15Mtech = false;
	int A1Limit=4;
	int A1TotalLimit=20;
	int RowTotalLimit=8;
	int GrandTotalLimit=60;
	Boolean clickFind = false;
	String _mark1_total=null, _mark_2_3_total=null, _mark_4_5_total=null,
	_mark_6_7_total=null, _mark_8_9_total=null,
	_mark_10_11_total=null, _grand_toal=null;
	
	boolean isRegulation_R13_Mtech = true;    

	String userId, subjectCode, ansBookBarcode, bundleNo, seatNo;
	int bundle_serial_no, scrutinizedStatusInObsMode, MaxAnswerBook, max_mark, old_bundle_serial_no;
	boolean isAddScript, timer_status;
	SharedPreferences sharedPreference;
	SharedPreferences.Editor editor;
	boolean is_subject_code_special_case;
	
	protected boolean isBool = false;;
	protected int timelimit=0;

	private PowerManager.WakeLock wl;
	DBHelper database;
	String date = new Date().toString();
	Utility instanceUtility;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mark_dialog_r13_btech_nototal);
		startTime=Utility.getPresentTime();
		database = DBHelper.getInstance(this);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"MarkEntryScreen_R13_Mtech");
		isRegulation_R13_Mtech = true;
		// get data from previous activity/screen
		// isRegulation_R13_MTech = Utility.isRegulation_R13_Mtech(this);
		 instanceUtility = new Utility();
		R13BTech=instanceUtility.isRegulation_R13_Btech(this);
		R15Mtech = instanceUtility.isRegulation_R15_Mtech(this);
		if (R13BTech) {
			 A1Limit=3;
			 A1TotalLimit=25;    
			 RowTotalLimit=10;  
			 GrandTotalLimit=75;         
		}else if (R15Mtech){
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
		bundleNo = intent_extras.getStringExtra(SEConstants.bundle);
		MaxAnswerBook = intent_extras.getIntExtra("MaxAnswerBook",
				SEConstants.MAX_ANSWER_BOOK);
		userId = intent_extras.getStringExtra(SEConstants.USER_ID);  
		ansBookBarcode = intent_extras
				.getStringExtra(SEConstants.ANS_BOOK_BARCODE);  
		bundle_serial_no = intent_extras.getIntExtra(  
				SEConstants.BUNDLE_SERIAL_NO, -1);
		timer_status=intent_extras.getBooleanExtra(SEConstants.BUNDLE_TIMER, true);
		subjectCode = intent_extras.getStringExtra(SEConstants.SUBJECT_CODE);
		old_bundle_serial_no=intent_extras.getIntExtra(  
				SEConstants.BUNDLE_SERIAL_OLD_NO, 000);
		seatNo = intent_extras.getStringExtra("SeatNo");
		// Making views invisible      
		((TextView) findViewById(R.id.tv_h_user_id)).setVisibility(View.VISIBLE);  
		((TextView) findViewById(R.id.tv_h_sub_code)).setVisibility(View.VISIBLE);    
		((TextView) findViewById(R.id.tv_h_bundle_no)).setVisibility(View.VISIBLE);    
		((TextView) findViewById(R.id.tv_h_serial_no)).setVisibility(View.VISIBLE);
		
		((TextView) findViewById(R.id.tv_user_id)).setText(userId);
		((TextView) findViewById(R.id.tv_sub_code)).setText(subjectCode);
		((TextView) findViewById(R.id.tv_seat_no)).setText(seatNo);
  
		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
		((TextView) findViewById(R.id.tv_ans_book)).setText(String.valueOf(bundle_serial_no)); 
				Button btnSubmit1 = (Button) findViewById(R.id.btn_submit1);
				btnSubmit1.setVisibility(View.GONE); 
		showItems();  
		
		
		CountDownTimer myCountdownTimer =    new CountDownTimer(SEConstants.time_limit/*getTimeLimit()*/, 1000) {

			 @Override
			public void onTick(long millisUntilFinished) {  
   				 timelimit = (int) (millisUntilFinished / 1000);
			 }        
        
			 @Override
			public void onFinish() {   
				 isBool = true;  
			 }   
			 };
			      
			 if(timer_status)
				 myCountdownTimer.start();
			 else
				 isBool = true;  
			 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		LinearLayout ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
		ll_submit.addView(addNumbersView());
		checkRowExistsAndInsertRow();
	}
	public int getTimeLimit() {
		int time = 120;
		Cursor cursor = null;
		try {
			cursor = database
					.executeSelectSQLQuery("select time_interval from table_date_configuration"
							+ " where "
							+ "id=1");

			if ((cursor != null) && (cursor.getCount() > 0)) {
				while (!(cursor.isAfterLast())) {
					try{
					time = cursor.getInt(cursor
									.getColumnIndex("time_interval"));
					cursor.moveToNext();
					if(time==0){
					}
					}catch(Exception e){
						time=120;
						e.printStackTrace();
					}
				}
			} else {
				FileLog.logInfo(
						"No User with bundle status 0,--->EvaluatorEntryActivity: getbundleNoByUserid(): ",
						0);
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception --->EvaluatorEntryActivity: getbundleNoByUserid(): "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor);
		}
Log.v("time_interval", ""+time);
		return time;
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
     
	private void checkRowExistsAndInsertRow() {
		// Cursor _cursor = database.getRow(ansBookBarcode, null);
		Cursor _cursor = database.getRow(SEConstants.TABLE_MARKS,
				SEConstants.ANS_BOOK_BARCODE + " = '" + ansBookBarcode
						+ "' AND " + SEConstants.BUNDLE_NO + " = '" + bundleNo
						+ "'", null);
		if (_cursor.getCount() == 0) {
			/*ContentValues _values = new ContentValues();
			_values.put(SEConstants.ANS_BOOK_BARCODE, ansBookBarcode);
			_values.put(SEConstants.USER_ID, userId);  
			_values.put(SEConstants.SUBJECT_CODE, subjectCode);  
			_values.put(SEConstants.BUNDLE_NO, bundleNo);
			_values.put(SEConstants.BUNDLE_SERIAL_NO, bundle_serial_no);
			_values.put(SEConstants.BARCODE_STATUS,
					getIntent().getIntExtra(SEConstants.BARCODE_STATUS, -1));
			database.insertReords(SEConstants.TABLE_MARKS, _values);*/
		} else {

			if (_cursor.getCount() > 0) {  
				for (_cursor.moveToFirst(); !(_cursor.isAfterLast()); _cursor
						.moveToNext()) {
  
					// Marks1
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1A)), tv_mark1a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1B)), tv_mark1b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1C)), tv_mark1c);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1D)), tv_mark1d);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1E)), tv_mark1e);
					if(R13BTech){
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1F)), tv_mark1f);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1G)), tv_mark1g);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1H)), tv_mark1h);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1I)), tv_mark1i);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK1J)), tv_mark1j);
					}
//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R1_TOTAL)),
//							tv_mark1_total);
					_mark1_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R1_TOTAL));

					// Marks2
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK2A)), tv_mark2a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK2B)), tv_mark2b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK2C)), tv_mark2c);
					// Marks3
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK3A)), tv_mark3a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK3B)), tv_mark3b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK3C)), tv_mark3c);

//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R2_3TOTAL)),
//							tv_mark_2_3_total);
					_mark_2_3_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R2_3TOTAL));
					// Marks4
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK4A)), tv_mark4a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK4B)), tv_mark4b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK4C)), tv_mark4c);
					// Marks5
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK5A)), tv_mark5a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK5B)), tv_mark5b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK5C)), tv_mark5c);

//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R4_5TOTAL)),
//							tv_mark_4_5_total);

					_mark_4_5_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R4_5TOTAL));
							
					// Marks6
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK6A)), tv_mark6a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK6B)), tv_mark6b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK6C)), tv_mark6c);
					// Marks7
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK7A)), tv_mark7a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK7B)), tv_mark7b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK7C)), tv_mark7c);

//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R6_7TOTAL)),
//							tv_mark_6_7_total);
					_mark_6_7_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R6_7TOTAL));

					// Marks8
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK8A)), tv_mark8a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK8B)), tv_mark8b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK8C)), tv_mark8c);
					// Marks9
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK9A)), tv_mark9a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK9B)), tv_mark9b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK9C)), tv_mark9c);

//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R8_9TOTAL)),
//							tv_mark_8_9_total);
					_mark_8_9_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R8_9TOTAL));
					// Marks10
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK10A)), tv_mark10a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK10B)), tv_mark10b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK10C)), tv_mark10c);
					// Marks11
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK11A)), tv_mark11a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK11B)), tv_mark11b);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK11C)), tv_mark11c);
//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R10_11TOTAL)),
//							tv_mark_10_11_total);
					_mark_10_11_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R10_11TOTAL));
							
//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.GRAND_TOTAL_MARK)),tv_grand_toal);
							_grand_toal=_cursor.getString(_cursor
									.getColumnIndex(SEConstants.GRAND_TOTAL_MARK));

				}
			}
		}
		DataBaseUtility.closeCursor(_cursor);
	}

	// set marks
	private void setMarkToCellFromDB(String pMark, EditText pTextView) {
		if (!TextUtils.isEmpty(pMark) && !pMark.equalsIgnoreCase("null")) {
			pTextView.setText(pMark);
		}
	}

	// set marks
	private void setMarkToCellFromDB(String pMark, TextView pTextView) {
		if (!TextUtils.isEmpty(pMark) && !pMark.equalsIgnoreCase("null")) {
			pTextView.setText(pMark);
		}
	}

	private void showItems() {

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
		if(!R13BTech){
		tv_mark1f.setFocusable(false);
		tv_mark1g.setFocusable(false);
		tv_mark1h.setFocusable(false);
		tv_mark1i.setFocusable(false);   
		tv_mark1j.setFocusable(false);
		
		tv_mark1f.setLongClickable(false);
		tv_mark1g.setLongClickable(false);
		tv_mark1h.setLongClickable(false);
		tv_mark1i.setLongClickable(false);   
		tv_mark1j.setLongClickable(false);
		}

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

//		tv_mark1_total = ((TextView) findViewById(R.id.q1_total));
//		tv_mark_2_3_total = ((TextView) findViewById(R.id.q2_3_total));
//		tv_mark_4_5_total = ((TextView) findViewById(R.id.q4_5_total));
//		tv_mark_6_7_total = ((TextView) findViewById(R.id.q6_7_total));
//		tv_mark_8_9_total = ((TextView) findViewById(R.id.q8_9_total));
//		tv_mark_10_11_total = ((TextView) findViewById(R.id.q10_11_total));
//
//		tv_grand_toal = ((TextView) findViewById(R.id.grand_total));

		tv_mark1a.addTextChangedListener(this);
		tv_mark1b.addTextChangedListener(this);
		tv_mark1c.addTextChangedListener(this);
		tv_mark1d.addTextChangedListener(this);
		tv_mark1e.addTextChangedListener(this);
		if(R13BTech){
		tv_mark1f.addTextChangedListener(this);
		tv_mark1g.addTextChangedListener(this);
		tv_mark1h.addTextChangedListener(this);
		tv_mark1i.addTextChangedListener(this);
		tv_mark1j.addTextChangedListener(this);
		}
		tv_mark2a.addTextChangedListener(this);
		tv_mark2b.addTextChangedListener(this);
		tv_mark2c.addTextChangedListener(this);

		tv_mark3a.addTextChangedListener(this);
		tv_mark3b.addTextChangedListener(this);
		tv_mark3c.addTextChangedListener(this);

		tv_mark6a.addTextChangedListener(this);
		tv_mark6b.addTextChangedListener(this);
		tv_mark6c.addTextChangedListener(this);

		tv_mark4a.addTextChangedListener(this);
		tv_mark4b.addTextChangedListener(this);
		tv_mark4c.addTextChangedListener(this);

		tv_mark5a.addTextChangedListener(this);
		tv_mark5b.addTextChangedListener(this);
		tv_mark5c.addTextChangedListener(this);

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

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

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
				setRemarkInContentValue2(focusedView, "", false, false);
				calculateTotal();
				calculateGrandTotal();
			}
			break;

		case R.id.btn_delete:
			View focusedView2 = getCurrentFocus();
			if (focusedView2 != null && focusedView2 instanceof EditText) {
				deleteCharAndSetSelection((EditText) focusedView2);
				calculateTotal();
				calculateGrandTotal();
			}
			break;
   
		case R.id.btn_submit:
			// set true since number layout is inflated/attached
			submit(v);
			break;
		case R.id.btn_submit1:
			// set false since number layout is not inflated/attached
			// submit(v);
			break;

		default:
			break;
		}

	}
   
	// call this method when clicked on Submit button
	private void submit(final View view) {
		
		if(isBool){
   
			  //   mTextField.setText("done!");
			     showAlert("Do you want to Submit Marks? ",
							getString(R.string.alert_dialog_ok),
							getString(R.string.alert_dialog_cancel), true, view);
			     // the 30 seconds is up now so do make any checks you need here.
			     
		}
		else{    
			
			alertMsgForSecondsRemaining("Evaluation Time for Each Script is Set to a Minimum of 120 " +
					"Seconds.\nPlease, Continue Evaluation for the Next "
					+ timelimit + " Seconds...! ");   
			
			/*showAlert("Evaluation Time for Each Script is Set to a Minimum of 120 " +
					"Seconds.\nPlease, Continue Evaluation for the Next "
					+ timelimit + " Seconds...! ",
								getString(R.string.alert_dialog_ok), getString(R.string.alert_dialog_cancel), true, view); */
		}
		  
	}
		  
		/*  

		// if ((correctedQuesCount() > 4 || is_subject_code_special_case)) {
		// new TempDatabase(this).deleteRow();

		SimpleDateFormat formatter = new SimpleDateFormat(
				"EEE MMM d HH:mm:ss zzz yyyy");
		Date date_temp = null;
		try {
			date_temp = formatter.parse(date);
		} catch (ParseException ex) {

		}
		Cursor cursor = null;
		int timeInterval = 0;
		try {
			cursor = DBHelper
					.getInstance(this)
					.executeSelectSQLQuery(
							"select time_interval as Value from table_date_configuration");
			if ((cursor != null) && (cursor.getCount() > 0)) {
				while (!(cursor.isAfterLast())) {
					timeInterval = cursor
							.getInt(cursor.getColumnIndex("Value"));
					cursor.moveToNext();
				}
			} else {
				FileLog.logInfo(
						"Cursor Null ---> MarkEntryScreen_R13: onCreate() ", 0);
			}

		} catch (Exception e) {
			FileLog.logInfo("Exception ---> MarkEntryScreen_R13: onCreate() "
					+ e.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor);
		}
		String dateTimeInterval = Utility.getDateDifference(date_temp,
				timeInterval, this);

		int remainingTime = (timeInterval - Integer.parseInt(dateTimeInterval));

		if (Integer.parseInt(dateTimeInterval) >= timeInterval
				|| getIntent().hasExtra(
						SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE)) {
			showAlert("Do you want to Submit Marks? ",
					getString(R.string.alert_dialog_ok),
					getString(R.string.alert_dialog_cancel), true, view);
		} else {
			alertMsgForSecondsRemaining("Evaluation Time for Each Script is Set to a Minimum of "
					+ timeInterval
					+ " Seconds.\nPlease, Continue Evaluation for the Next  "
					+ remainingTime + " Seconds...! ");
		}

		// } else {
		// showAlertForLessthan5();
		// }
	*/

	// show alert message
	private void showAlert(String msg, String positiveStr, String negativeStr,
			final boolean navigation, final View view) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder((this));
		myAlertDialog.setTitle("Smart Evaluation");
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
							switchToShowGrandTotalSummaryTableActivity(view);
//							Intent in = new Intent(MarkEntryScreen_R13_Mtech.this, MarkEntryScreen_Totals_R13_Mtech.class);
//							in.putExtra(SEConstants.BUNDLE_NO, bundleNo);
//							in.putExtra(SEConstants.ANS_BOOK_BARCODE, ansBookBarcode);
//							in.putExtra(SEConstants.BUNDLE_SERIAL_NO, bundle_serial_no);
//							in.putExtra(SEConstants.SUBJECT_CODE, subjectCode);
//							in.putExtra(SEConstants.USER_ID, userId);
//							in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//							startActivity(in);
							/*     
							//dialog.dismiss();
							// ContentValues _contentValues = new   
							// ContentValues();       

							// insertToDB(_contentValues);
							if(tv_grand_toal.getText() != null ||   
								   !tv_grand_toal.equals(".")){   
								if(Double.parseDouble(tv_grand_toal.getText().toString()) == Double.parseDouble(grand_totally.toString())){
									// if(tv_grand_toal.getText().equals(grand_totally)){
										  
								  }
								  else{
										Toast.makeText(MarkEntryScreen_R13_Mtech.this, "Grand total doesnt match, Please ReCheck...! ",
												Toast.LENGTH_LONG).show();
								  }  
										
							}   
							else if(tv_grand_toal.getText() == null ||
								  tv_grand_toal.equals(".")){
									  Toast.makeText(MarkEntryScreen_R13_Mtech.this, "Please enter valid Grand Total ",
												Toast.LENGTH_LONG).show();
				  				  
							}   
							    
							
						*/} else {
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

	public void switchToShowGrandTotalSummaryTableActivity(View view) {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				showProgress("Submitting Marks");
			};

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				setContentValuesOnFinalSubmission();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				hideProgress();

				// check here where to navigate
				Intent intent = new Intent(MarkEntryScreen_R13.this,
						MarkDialogScreen_R13.class);  
				if (getIntent().hasExtra(
						SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE)) {
					intent.putExtra(
							(SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE),
							getIntent()
									.hasExtra(
											SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE));
				}
				intent
				.putExtra("MaxAnswerBook", MaxAnswerBook);
				intent.putExtra(SEConstants.BUNDLE_NO, bundleNo);
				intent.putExtra(SEConstants.ANS_BOOK_BARCODE, ansBookBarcode);
				intent.putExtra(SEConstants.BUNDLE_SERIAL_NO, bundle_serial_no);
				intent.putExtra(SEConstants.SUBJECT_CODE, subjectCode);
				intent.putExtra("SeatNo", SEConstants.seatNo);
				intent.putExtra(SEConstants.USER_ID, userId);
				intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO, old_bundle_serial_no);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			};
		}.execute();

	}
String startTime;
	private void setContentValuesOnFinalSubmission() {
		String mark=null;  
		
		
		
		Cursor _cursor = database.getRow(SEConstants.TABLE_MARKS,
				SEConstants.ANS_BOOK_BARCODE + " = '" + ansBookBarcode
						+ "' AND " + SEConstants.BUNDLE_NO + " = '" + bundleNo
						+ "'", null);
		if (_cursor.getCount() == 0) {
		ContentValues _contentValues = new ContentValues();

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		_contentValues.put(SEConstants.ANS_BOOK_BARCODE, ansBookBarcode);
		_contentValues.put(SEConstants.USER_ID, userId);
		_contentValues.put(SEConstants.SUBJECT_CODE, subjectCode); 
		_contentValues.put(SEConstants.BUNDLE_NO, bundleNo);
		_contentValues.put(SEConstants.BUNDLE_SERIAL_NO, bundle_serial_no);
		_contentValues.put(SEConstants.BARCODE_STATUS,
				getIntent().getIntExtra(SEConstants.BARCODE_STATUS, -1));
		_contentValues.put(SEConstants.TABLET_IMEI, imei);
		_contentValues.put(SEConstants.IS_UPDATED_SERVER, "0");
		if(startTime.isEmpty())
			startTime=Utility.getPresentTime();
		_contentValues.put(SEConstants.ENTER_ON, startTime);
		_contentValues.put(SEConstants.UPDATED_ON, Utility.getPresentTime());		
		// check whether text is empty if so set to null
		// marks1
		_contentValues.put(
				SEConstants.MARK1A,
				((TextUtils.isEmpty(mark = tv_mark1a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1B,
				((TextUtils.isEmpty(mark = tv_mark1b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1C,
				((TextUtils.isEmpty(mark = tv_mark1c.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1D,
				((TextUtils.isEmpty(mark = tv_mark1d.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1E,
				((TextUtils.isEmpty(mark = tv_mark1e.getText().toString()
						.trim()))) ? "null" : mark);
		if(R13BTech){
		_contentValues.put(
				SEConstants.MARK1F,
				((TextUtils.isEmpty(mark = tv_mark1f.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1G,
				((TextUtils.isEmpty(mark = tv_mark1g.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1H,
				((TextUtils.isEmpty(mark = tv_mark1h.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1I,
				((TextUtils.isEmpty(mark = tv_mark1i.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1J,
				((TextUtils.isEmpty(mark = tv_mark1j.getText().toString()
						.trim()))) ? "null" : mark);
		}
		// marks_2_3
		_contentValues.put(
				SEConstants.MARK2A,
				((TextUtils.isEmpty(mark = tv_mark2a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK2B,
				((TextUtils.isEmpty(mark = tv_mark2b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK2C,
				((TextUtils.isEmpty(mark = tv_mark2c.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SEConstants.MARK3A,
				((TextUtils.isEmpty(mark = tv_mark3a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK3B,
				((TextUtils.isEmpty(mark = tv_mark3b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK3C,
				((TextUtils.isEmpty(mark = tv_mark3c.getText().toString()
						.trim()))) ? "null" : mark);

		// marks_4_5  
		_contentValues.put(
				SEConstants.MARK4A,
				((TextUtils.isEmpty(mark = tv_mark4a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK4B,
				((TextUtils.isEmpty(mark = tv_mark4b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK4C,
				((TextUtils.isEmpty(mark = tv_mark4c.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SEConstants.MARK5A,
				((TextUtils.isEmpty(mark = tv_mark5a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK5B,
				((TextUtils.isEmpty(mark = tv_mark5b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK5C,
				((TextUtils.isEmpty(mark = tv_mark5c.getText().toString()
						.trim()))) ? "null" : mark);

		// marks_6_7
		_contentValues.put(
				SEConstants.MARK6A,
				((TextUtils.isEmpty(mark = tv_mark6a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK6B,
				((TextUtils.isEmpty(mark = tv_mark6b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK6C,
				((TextUtils.isEmpty(mark = tv_mark6c.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SEConstants.MARK7A,
				((TextUtils.isEmpty(mark = tv_mark7a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK7B,
				((TextUtils.isEmpty(mark = tv_mark7b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK7C,
				((TextUtils.isEmpty(mark = tv_mark7c.getText().toString()
						.trim()))) ? "null" : mark);

		// marks_8_9
		_contentValues.put(
				SEConstants.MARK8A,
				((TextUtils.isEmpty(mark = tv_mark8a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK8B,
				((TextUtils.isEmpty(mark = tv_mark8b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK8C,
				((TextUtils.isEmpty(mark = tv_mark8c.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SEConstants.MARK9A,
				((TextUtils.isEmpty(mark = tv_mark9a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK9B,
				((TextUtils.isEmpty(mark = tv_mark9b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK9C,
				((TextUtils.isEmpty(mark = tv_mark9c.getText().toString()
						.trim()))) ? "null" : mark);
		// marks_10_11
		_contentValues.put(
				SEConstants.MARK10A,
				((TextUtils.isEmpty(mark = tv_mark10a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK10B,
				((TextUtils.isEmpty(mark = tv_mark10b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK10C,
				((TextUtils.isEmpty(mark = tv_mark10c.getText().toString()
						.trim()))) ? "null" : mark);

		_contentValues.put(
				SEConstants.MARK11A,
				((TextUtils.isEmpty(mark = tv_mark11a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK11B,
				((TextUtils.isEmpty(mark = tv_mark11b.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK11C,
				((TextUtils.isEmpty(mark = tv_mark11c.getText().toString()
						.trim()))) ? "null" : mark);

		// total marks
		_contentValues.put(SEConstants.R1_TOTAL, _mark1_total);
		_contentValues.put(
				SEConstants.R2_3TOTAL, _mark_2_3_total);
		_contentValues.put(
				SEConstants.R4_5TOTAL, _mark_4_5_total);
		_contentValues.put(
				SEConstants.R6_7TOTAL, _mark_6_7_total);
		_contentValues.put(  
				SEConstants.R8_9TOTAL, _mark_8_9_total);
		_contentValues.put(
				SEConstants.R10_11TOTAL, _mark_10_11_total);
		if(TextUtils.isEmpty(_grand_toal))
		{
			_grand_toal = "0";  
		}
		_contentValues.put(
				SEConstants.GRAND_TOTAL_MARK, _grand_toal);
		database.insertReords(SEConstants.TABLE_MARKS, _contentValues);
		_contentValues.remove(SEConstants.UPDATED_ON);
		database.insertReords(SEConstants.TABLE_MARKS_HISTORY, _contentValues);
		} else {

			ContentValues _contentValues = new ContentValues();
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			_contentValues.put(SEConstants.BARCODE_STATUS,
					getIntent().getIntExtra(SEConstants.BARCODE_STATUS, -1));
			_contentValues.put(SEConstants.TABLET_IMEI, imei);
			_contentValues.put(SEConstants.IS_UPDATED_SERVER, "0");
			if(startTime.isEmpty())
				startTime=Utility.getPresentTime();
			_contentValues.put(SEConstants.ENTER_ON, startTime);
			_contentValues.put(SEConstants.UPDATED_ON, Utility.getPresentTime());
			// check whether text is empty if so set to null
			// marks1
			_contentValues.put(
					SEConstants.MARK1A,
					((TextUtils.isEmpty(mark = tv_mark1a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK1B,
					((TextUtils.isEmpty(mark = tv_mark1b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK1C,
					((TextUtils.isEmpty(mark = tv_mark1c.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK1D,
					((TextUtils.isEmpty(mark = tv_mark1d.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK1E,
					((TextUtils.isEmpty(mark = tv_mark1e.getText().toString()
							.trim()))) ? "null" : mark);
			if(R13BTech){
			_contentValues.put(
					SEConstants.MARK1F,
					((TextUtils.isEmpty(mark = tv_mark1f.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK1G,
					((TextUtils.isEmpty(mark = tv_mark1g.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK1H,
					((TextUtils.isEmpty(mark = tv_mark1h.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK1I,
					((TextUtils.isEmpty(mark = tv_mark1i.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK1J,
					((TextUtils.isEmpty(mark = tv_mark1j.getText().toString()
							.trim()))) ? "null" : mark);
			}
			// marks_2_3
			_contentValues.put(
					SEConstants.MARK2A,
					((TextUtils.isEmpty(mark = tv_mark2a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK2B,
					((TextUtils.isEmpty(mark = tv_mark2b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK2C,
					((TextUtils.isEmpty(mark = tv_mark2c.getText().toString()
							.trim()))) ? "null" : mark);

			_contentValues.put(
					SEConstants.MARK3A,
					((TextUtils.isEmpty(mark = tv_mark3a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK3B,
					((TextUtils.isEmpty(mark = tv_mark3b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK3C,
					((TextUtils.isEmpty(mark = tv_mark3c.getText().toString()
							.trim()))) ? "null" : mark);

			// marks_4_5
			_contentValues.put(
					SEConstants.MARK4A,
					((TextUtils.isEmpty(mark = tv_mark4a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK4B,
					((TextUtils.isEmpty(mark = tv_mark4b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK4C,
					((TextUtils.isEmpty(mark = tv_mark4c.getText().toString()
							.trim()))) ? "null" : mark);

			_contentValues.put(
					SEConstants.MARK5A,
					((TextUtils.isEmpty(mark = tv_mark5a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK5B,
					((TextUtils.isEmpty(mark = tv_mark5b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK5C,
					((TextUtils.isEmpty(mark = tv_mark5c.getText().toString()
							.trim()))) ? "null" : mark);

			// marks_6_7
			_contentValues.put(
					SEConstants.MARK6A,
					((TextUtils.isEmpty(mark = tv_mark6a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK6B,
					((TextUtils.isEmpty(mark = tv_mark6b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK6C,
					((TextUtils.isEmpty(mark = tv_mark6c.getText().toString()
							.trim()))) ? "null" : mark);

			_contentValues.put(
					SEConstants.MARK7A,
					((TextUtils.isEmpty(mark = tv_mark7a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK7B,
					((TextUtils.isEmpty(mark = tv_mark7b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK7C,
					((TextUtils.isEmpty(mark = tv_mark7c.getText().toString()
							.trim()))) ? "null" : mark);

			// marks_8_9
			_contentValues.put(
					SEConstants.MARK8A,
					((TextUtils.isEmpty(mark = tv_mark8a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK8B,
					((TextUtils.isEmpty(mark = tv_mark8b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK8C,
					((TextUtils.isEmpty(mark = tv_mark8c.getText().toString()
							.trim()))) ? "null" : mark);

			_contentValues.put(
					SEConstants.MARK9A,
					((TextUtils.isEmpty(mark = tv_mark9a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK9B,
					((TextUtils.isEmpty(mark = tv_mark9b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK9C,
					((TextUtils.isEmpty(mark = tv_mark9c.getText().toString()
							.trim()))) ? "null" : mark);
			// marks_10_11
			_contentValues.put(
					SEConstants.MARK10A,
					((TextUtils.isEmpty(mark = tv_mark10a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK10B,
					((TextUtils.isEmpty(mark = tv_mark10b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK10C,
					((TextUtils.isEmpty(mark = tv_mark10c.getText().toString()
							.trim()))) ? "null" : mark);

			_contentValues.put(
					SEConstants.MARK11A,
					((TextUtils.isEmpty(mark = tv_mark11a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK11B,
					((TextUtils.isEmpty(mark = tv_mark11b.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK11C,
					((TextUtils.isEmpty(mark = tv_mark11c.getText().toString()
							.trim()))) ? "null" : mark);

			// total marks
			_contentValues.put(SEConstants.R1_TOTAL, _mark1_total);
			_contentValues.put(
					SEConstants.R2_3TOTAL, _mark_2_3_total);
			_contentValues.put(
					SEConstants.R4_5TOTAL, _mark_4_5_total);
			_contentValues.put(
					SEConstants.R6_7TOTAL, _mark_6_7_total);
			_contentValues.put(  
					SEConstants.R8_9TOTAL, _mark_8_9_total);
			_contentValues.put(
					SEConstants.R10_11TOTAL, _mark_10_11_total);
			if(TextUtils.isEmpty(_grand_toal))
			{
				_grand_toal = "0";  
			}
			_contentValues.put(SEConstants.GRAND_TOTAL_MARK, _grand_toal);
			insertToDB(_contentValues);
			_contentValues.remove(SEConstants.UPDATED_ON);
			updateToMarksHistoryTable(_contentValues);
		}
	}

	// insert valuesto DB  
	private void insertToDB(ContentValues pContentValues) {
		try {
			// DBHelper _db_for_scrutiny = DBHelper.getInstance(this);
			database.updateRow(SEConstants.TABLE_MARKS, pContentValues,
					SEConstants.USER_ID + "='" + userId + "' AND "
							+ SEConstants.SUBJECT_CODE + "='" + subjectCode
							+ "' AND " + SEConstants.BUNDLE_SERIAL_NO + " = '"
							+ bundle_serial_no + "' AND "
							+ SEConstants.BUNDLE_NO + " = '" + bundleNo
							+ "' AND " + SEConstants.ANS_BOOK_BARCODE + "= '"
							+ ansBookBarcode + "'");
		} catch (Exception ex) {
			FileLog.logInfo(ex.toString(), 0);
		}

	}
	private void updateToMarksHistoryTable(ContentValues pContentValues) {
		try {
			// DBHelper _db_for_scrutiny = DBHelper.getInstance(this);
			database.updateRow(SEConstants.TABLE_MARKS_HISTORY, pContentValues,
					SEConstants.USER_ID + "='" + userId + "' AND "
							+ SEConstants.SUBJECT_CODE + "='" + subjectCode
							+ "' AND " + SEConstants.BUNDLE_SERIAL_NO + " = '"
							+ bundle_serial_no + "' AND "
							+ SEConstants.BUNDLE_NO + " = '" + bundleNo
							+ "' AND " + SEConstants.ANS_BOOK_BARCODE + "= '"
							+ ansBookBarcode + "'");
		} catch (Exception ex) {
			FileLog.logInfo(ex.toString(), 0);
		}

	}

	private ProgressDialog progressDialog;

	public void showProgress(String msg) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(
					MarkEntryScreen_R13.this, "", msg);
			progressDialog.setCancelable(false);
		}
	}

	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private void alertMsgForSecondsRemaining(String pMsg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setTitle("Smart Evaluation");

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

	// when clicked on delete button call this method
	private void deleteCharAndSetSelection(EditText edittext) {
		if (!TextUtils.isEmpty(edittext.getText().toString())) {
			edittext.setText(edittext.getText().toString()
					.substring(0, (edittext.getText().toString().length() - 1)));
			edittext.setSelection(edittext.getText().toString().length());
			setRemarkInContentValue2(edittext, edittext.getText().toString()
					.trim(), false, false);
		}
	}

	// set mark or remark
	private void setRemarkInContentValue2(View view, final String remarkOrMark,
			boolean setRemark, boolean insertToTempDB) {
		switch (view.getId()) {
		// Q1
		case R.id.q1_a:
			setRemarkInContentValue3(SEConstants.M1A_REMARK,
					SEConstants.MARK1A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_b:
			setRemarkInContentValue3(SEConstants.M1B_REMARK,
					SEConstants.MARK1B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_c:
			setRemarkInContentValue3(SEConstants.M1C_REMARK,
					SEConstants.MARK1C, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_d:
			setRemarkInContentValue3(SEConstants.M1D_REMARK,
					SEConstants.MARK1D, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_e:
			setRemarkInContentValue3(SEConstants.M1E_REMARK,
					SEConstants.MARK1E, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_f:
			setRemarkInContentValue3(SEConstants.M1F_REMARK,
					SEConstants.MARK1F, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_g:
			setRemarkInContentValue3(SEConstants.M1G_REMARK,
					SEConstants.MARK1G, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_h:
			setRemarkInContentValue3(SEConstants.M1H_REMARK,
					SEConstants.MARK1H, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_i:
			setRemarkInContentValue3(SEConstants.M1I_REMARK,
					SEConstants.MARK1I, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q1_j:
			setRemarkInContentValue3(SEConstants.M1J_REMARK,
					SEConstants.MARK1J, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q2
		case R.id.q2_a:
			setRemarkInContentValue3(SEConstants.M2A_REMARK,
					SEConstants.MARK2A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q2_b:
			setRemarkInContentValue3(SEConstants.M2B_REMARK,
					SEConstants.MARK2B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q2_c:
			setRemarkInContentValue3(SEConstants.M2C_REMARK,
					SEConstants.MARK2C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q3
		case R.id.q3_a:
			setRemarkInContentValue3(SEConstants.M3A_REMARK,
					SEConstants.MARK3A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q3_b:
			setRemarkInContentValue3(SEConstants.M3B_REMARK,
					SEConstants.MARK3B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q3_c:
			setRemarkInContentValue3(SEConstants.M3C_REMARK,
					SEConstants.MARK3C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q4
		case R.id.q4_a:
			setRemarkInContentValue3(SEConstants.M4A_REMARK,
					SEConstants.MARK4A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q4_b:
			setRemarkInContentValue3(SEConstants.M4B_REMARK,
					SEConstants.MARK4B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q4_c:
			setRemarkInContentValue3(SEConstants.M4C_REMARK,
					SEConstants.MARK4C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q5
		case R.id.q5_a:
			setRemarkInContentValue3(SEConstants.M5A_REMARK,
					SEConstants.MARK5A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q5_b:
			setRemarkInContentValue3(SEConstants.M5B_REMARK,
					SEConstants.MARK5B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q5_c:
			setRemarkInContentValue3(SEConstants.M5C_REMARK,
					SEConstants.MARK5C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q6
		case R.id.q6_a:
			setRemarkInContentValue3(SEConstants.M6A_REMARK,
					SEConstants.MARK6A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q6_b:
			setRemarkInContentValue3(SEConstants.M6B_REMARK,
					SEConstants.MARK6B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q6_c:
			setRemarkInContentValue3(SEConstants.M6C_REMARK,
					SEConstants.MARK6C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q7
		case R.id.q7_a:
			setRemarkInContentValue3(SEConstants.M7A_REMARK,
					SEConstants.MARK7A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q7_b:
			setRemarkInContentValue3(SEConstants.M7B_REMARK,
					SEConstants.MARK7B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q7_c:
			setRemarkInContentValue3(SEConstants.M7C_REMARK,
					SEConstants.MARK7C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q8
		case R.id.q8_a:
			setRemarkInContentValue3(SEConstants.M8A_REMARK,
					SEConstants.MARK8A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q8_b:
			setRemarkInContentValue3(SEConstants.M8B_REMARK,
					SEConstants.MARK8B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q8_c:
			setRemarkInContentValue3(SEConstants.M8C_REMARK,
					SEConstants.MARK8C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q9
		case R.id.q9_a:
			setRemarkInContentValue3(SEConstants.M9A_REMARK,
					SEConstants.MARK9A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q9_b:
			setRemarkInContentValue3(SEConstants.M9B_REMARK,
					SEConstants.MARK9B, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q9_c:
			setRemarkInContentValue3(SEConstants.M9C_REMARK,
					SEConstants.MARK9C, remarkOrMark, setRemark, insertToTempDB);
			break;
		// Q10
		case R.id.q10_a:
			setRemarkInContentValue3(SEConstants.M10A_REMARK,
					SEConstants.MARK10A, remarkOrMark, setRemark,
					insertToTempDB);
			break;
		case R.id.q10_b:
			setRemarkInContentValue3(SEConstants.M10B_REMARK,
					SEConstants.MARK10B, remarkOrMark, setRemark,
					insertToTempDB);
			break;
		case R.id.q10_c:
			setRemarkInContentValue3(SEConstants.M10C_REMARK,
					SEConstants.MARK10C, remarkOrMark, setRemark,
					insertToTempDB);
			break;
		// Q11
		case R.id.q11_a:
			setRemarkInContentValue3(SEConstants.M11A_REMARK,
					SEConstants.MARK11A, remarkOrMark, setRemark,
					insertToTempDB);
			break;
		case R.id.q11_b:
			setRemarkInContentValue3(SEConstants.M11B_REMARK,
					SEConstants.MARK11B, remarkOrMark, setRemark,
					insertToTempDB);
			break;
		case R.id.q11_c:
			setRemarkInContentValue3(SEConstants.M11C_REMARK,
					SEConstants.MARK11C, remarkOrMark, setRemark,
					insertToTempDB);
			break;

		case R.id.q1_total:
			setRemarkInContentValue3(SEConstants.R1_REMARK,
					SEConstants.R1_TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q2_3_total:
			setRemarkInContentValue3(SEConstants.R2_3_REMARK,
					SEConstants.R2_3TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q4_5_total:
			setRemarkInContentValue3(SEConstants.R4_5_REMARK,
					SEConstants.R4_5TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q6_7_total:
			setRemarkInContentValue3(SEConstants.R6_7_REMARK,
					SEConstants.R6_7TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q8_9_total:
			setRemarkInContentValue3(SEConstants.R8_9_REMARK,
					SEConstants.R8_9TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q10_11_total:
			setRemarkInContentValue3(SEConstants.R10_11_REMARK,
					SEConstants.R10_11TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.grand_total:
			setRemarkInContentValue3(SEConstants.GRAND_TOTAL_REMARK,
					SEConstants.GRAND_TOTAL_MARK, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		default:
			break;
		}

	}

	private void setRemarkInContentValue3(String CONS_REMARK, String CONS_MARK,
			final String remarkOrMark, boolean setRemark, boolean insertToTempDB) {
		ContentValues _contentValues = new ContentValues();
		_contentValues.put(CONS_MARK, remarkOrMark);

		insertToDB(_contentValues);
	}
  
	// set text to focused view on click
	private void setTextToFocusedView(String text) {
		View focusedView = getCurrentFocus();
		if (focusedView != null && focusedView instanceof EditText) {
			EditText et_focusedView = ((EditText) focusedView);
			// check whether subject code is special case
			// if (is_subject_code_special_case || isRegulation_R13_BTech)
			if (isRegulation_R13_Mtech) {
				max_mark = setMaxValueForSubjCodeSpecialCase(et_focusedView);
			}
			// max_mark=16;
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
					if ((_row_total > max_mark)) {
						showAlertTotalExceedsMaxMark(
								getString(R.string.alert_total_exceeds)
										+ " "
										+ +max_mark
										+ " / Sub Marks Exceeds  the Maximum Marks",
								getString(R.string.alert_dialog_ok),
								et_focusedView, "");
					} else {
						setRemarkInContentValue2(et_focusedView, text, false,
								false);
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
						setRemarkInContentValue2(et_focusedView, prevText
								+ text, false, false);
						et_focusedView.setText(prevText + text);
						et_focusedView.setSelection(et_focusedView.getText()
								.length());
					}
				}
			}
		}
	}

	private void showAlertTotalExceedsMaxMark(String msg, String positiveStr,
			final EditText editText, final String prevMarks) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder((this));
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
	}

	// setting max value for special case subject codes
	private int setMaxValueForSubjCodeSpecialCase(EditText etQuesNo) {
		int _max_value = 75;

			if (etQuesNo == tv_mark1a || etQuesNo == tv_mark1b
					|| etQuesNo == tv_mark1c || etQuesNo == tv_mark1d
					|| etQuesNo == tv_mark1e
				|| etQuesNo == tv_mark1f
					|| etQuesNo == tv_mark1g || etQuesNo == tv_mark1h
					|| etQuesNo == tv_mark1i || etQuesNo == tv_mark1j) {
				_max_value = A1Limit;
			} else {
				_max_value = RowTotalLimit;
			}


		return _max_value;
	}

	private float checkRowTotalExceedsMaxValue(View view, String pMarks) {
		if (TextUtils.isEmpty(pMarks)) {
			pMarks = "0";
		}
			if (view.getId() == R.id.q1_a || view.getId() == R.id.q1_b
					|| view.getId() == R.id.q1_c || view.getId() == R.id.q1_d
					|| view.getId() == R.id.q1_e 
					|| view.getId() == R.id.q1_f
					|| view.getId() == R.id.q1_g || view.getId() == R.id.q1_h
					|| view.getId() == R.id.q1_i || view.getId() == R.id.q1_j){
				return A1Limit;
			} else {
				return RowTotalLimit;
			}
	}

	private void alertForInvalidMark(final View view2,
			final boolean fromNumbersLayout, final String mark) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder((this));
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

						((EditText) view2).setText("");

					}
				});

		myAlertDialog.show();
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
			flag = true;
			break;
		default:
			break;
		}
		return flag;
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		try{
			if(!clickFind){  
		//	setTextToFocusedView(arg0.toString());
			View focusedView = getCurrentFocus();
			if (focusedView != null && focusedView instanceof EditText) {
				EditText et_focusedView = ((EditText) focusedView);
				et_focusedView.removeTextChangedListener(MarkEntryScreen_R13.this);
				et_focusedView.setText("");
				et_focusedView.addTextChangedListener(MarkEntryScreen_R13.this);
			}
			}
			}catch(Exception e){
				e.printStackTrace();  
			}
			clickFind=false;  
		
		calculateTotal();
		calculateGrandTotal();
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
			if(TextUtils.isEmpty(_grand_toal))
			{
				_grand_toal = "0";  
			}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	private void calculateTotal() {
		row1Total_();
		row_2_3_Total_();
		row_4_5_Total_();
		row_6_7_Total_();
		row_8_9_Total_();
		row_10_11_Total_();

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
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub  
		super.onDestroy();
		Log.v("onDestroy", "MarkEntry");
	}
}