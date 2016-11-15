package com.infoplus.smartevaluation;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
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

public class MarkEntryScreen_R13_BTech_SpecialCase extends Activity implements OnClickListener,
TextWatcher {
   
	EditText tv_mark1a, tv_mark1b ;

	EditText tv_mark2a, tv_mark2b ;

	EditText tv_mark3a, tv_mark3b ;

	EditText tv_mark4a, tv_mark4b ;

	EditText tv_mark5a, tv_mark5b ;  

	EditText tv_mark6a, tv_mark6b ;       
    
	EditText tv_mark7a, tv_mark7b ;

	EditText tv_mark8a, tv_mark8b ;

	EditText tv_mark9a, tv_mark9b ;

	EditText tv_mark10a, tv_mark10b ;

	Boolean clickFind = false;
//	TextView tv_mark1_total, tv_mark_2_3_total, tv_mark_4_5_total,
//			tv_mark_6_7_total, tv_mark_8_9_total, tv_mark_10_11_total;
	Boolean R13BTech= true;
	int A1Limit= 15;
	int RowTotalLimit= 15;  
	int GrandTotalLimit= 75;
	String _mark_1_2_total=null, _mark_3_4_total=null, _mark_5_6_total=null,
	_mark_7_8_total=null, _mark_9_10_total=null,
	 _grand_toal=null;
	
	//boolean isRegulation_R13_BTech = true; 

	String userId, subjectCode, ansBookBarcode, bundleNo, seatNo;
	int bundle_serial_no, scrutinizedStatusInObsMode, max_mark, old_bundle_serial_no;
	boolean isAddScript, timer_status;
	SharedPreferences sharedPreference;
	SharedPreferences.Editor editor;
	boolean is_subject_code_special_case = false;
	
	protected boolean isBool = false;;
	protected int timelimit=0;

	private PowerManager.WakeLock wl;
	DBHelper database;
	//String date = new Date().toString();  
	Utility instanceUtility;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mark_dialog_r13_btech_specialcase);
		startTime=Utility.getPresentTime();
		database = DBHelper.getInstance(this);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"MarkEntryScreen_R13_Mtech"); 
		
		// get data from previous activity/screen
		// isRegulation_R13_MTech = Utility.isRegulation_R13_Mtech(this);
		is_subject_code_special_case = true;
		R13BTech = true;
		
		 instanceUtility = new Utility();
		R13BTech=instanceUtility.isRegulation_R13_Btech(this);
		
	 if(R13BTech && is_subject_code_special_case){  
			A1Limit=15;
			 RowTotalLimit=15;
			 GrandTotalLimit=75;
		}
		/*else
		{
			 A1Limit=4;
			 A1TotalLimit=20;
			 RowTotalLimit=8;
			 GrandTotalLimit=60;
		}*/
		Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SEConstants.bundle);

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
		((TextView) findViewById(R.id.tv_seat_no)).setText(seatNo);
		// Making views invisible
		((TextView) findViewById(R.id.tv_h_user_id)).setVisibility(View.VISIBLE);  
		((TextView) findViewById(R.id.tv_h_sub_code)).setVisibility(View.VISIBLE);  
		((TextView) findViewById(R.id.tv_h_bundle_no)).setVisibility(View.VISIBLE);  
		((TextView) findViewById(R.id.tv_h_serial_no)).setVisibility(View.VISIBLE);
		
		((TextView) findViewById(R.id.tv_user_id)).setText(userId);
		((TextView) findViewById(R.id.tv_sub_code)).setText(subjectCode);
  
		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
		((TextView) findViewById(R.id.tv_ans_book)).setText(String.valueOf(bundle_serial_no)); 
				
				Button btnSubmit1 = (Button) findViewById(R.id.btn_submit1);
				btnSubmit1.setVisibility(View.GONE);
				
				Button btnBack=(Button)findViewById(R.id.btn_back);
				btnBack.setVisibility(View.GONE);
		showItems();  
		
		
		CountDownTimer myCountdownTimer =    new CountDownTimer(SEConstants.time_limit, 1000) {

			 public void onTick(long millisUntilFinished) {  
   				 timelimit = (int) (millisUntilFinished / 1000);
			 }    
     
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
					
					_mark_1_2_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R1_2TOTAL));

					// Marks2
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK2A)), tv_mark2a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK2B)), tv_mark2b);
					
					// Marks3
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK3A)), tv_mark3a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK3B)), tv_mark3b);
					  
    
//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R2_3TOTAL)),
//							tv_mark_2_3_total);
					_mark_3_4_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R3_4TOTAL));
					// Marks4
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK4A)), tv_mark4a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK4B)), tv_mark4b);
					
					// Marks5
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK5A)), tv_mark5a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK5B)), tv_mark5b);
					

//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R4_5TOTAL)),
//							tv_mark_4_5_total);

					_mark_5_6_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R5_6TOTAL));
							
					// Marks6
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK6A)), tv_mark6a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK6B)), tv_mark6b);
					
					// Marks7
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK7A)), tv_mark7a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK7B)), tv_mark7b);
					

//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R6_7TOTAL)),
//							tv_mark_6_7_total);
					_mark_7_8_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R7_8TOTAL));

					// Marks8
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK8A)), tv_mark8a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK8B)), tv_mark8b);
					
					// Marks9
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK9A)), tv_mark9a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK9B)), tv_mark9b);
					

//					setMarkToCellFromDB(_cursor.getString(_cursor
//							.getColumnIndex(SEConstants.R8_9TOTAL)),
//							tv_mark_8_9_total);
					_mark_9_10_total=_cursor.getString(_cursor
							.getColumnIndex(SEConstants.R9_10TOTAL));
					// Marks10
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK10A)), tv_mark10a);
					setMarkToCellFromDB(_cursor.getString(_cursor
							.getColumnIndex(SEConstants.MARK10B)), tv_mark10b);
					
//					
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
		
		tv_mark2a.addTextChangedListener(this);
		tv_mark2b.addTextChangedListener(this);

		tv_mark3a.addTextChangedListener(this);
		tv_mark3b.addTextChangedListener(this);

		tv_mark6a.addTextChangedListener(this);
		tv_mark6b.addTextChangedListener(this);

		tv_mark4a.addTextChangedListener(this);
		tv_mark4b.addTextChangedListener(this);

		tv_mark5a.addTextChangedListener(this);
		tv_mark5b.addTextChangedListener(this);

		tv_mark7a.addTextChangedListener(this);
		tv_mark7b.addTextChangedListener(this);

		tv_mark8a.addTextChangedListener(this);
		tv_mark8b.addTextChangedListener(this);

		tv_mark9a.addTextChangedListener(this);
		tv_mark9b.addTextChangedListener(this);

		tv_mark10a.addTextChangedListener(this);
		tv_mark10b.addTextChangedListener(this);


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
			}
			break;

		case R.id.btn_delete:
			View focusedView2 = getCurrentFocus();
			if (focusedView2 != null && focusedView2 instanceof EditText) {
				deleteCharAndSetSelection((EditText) focusedView2);
				calculateTotal();
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
			
			
		}
		  
	}
		  

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

	public void switchToShowGrandTotalSummaryTableActivity(View view) {

		new AsyncTask<Void, Void, Void>() {

			protected void onPreExecute() {
				showProgress("Submitting Marks");
			};

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				setContentValuesOnFinalSubmission();
				return null;
			}

			protected void onPostExecute(Void result) {
				hideProgress();  

				// check here where to navigate
				Intent intent = new Intent(MarkEntryScreen_R13_BTech_SpecialCase.this,
						MarkDialogScreen_R13_Btech_SpecialCase.class);
				if (getIntent().hasExtra( 
						SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE)) {
					intent.putExtra(
							(SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE),
							getIntent()  
									.hasExtra(
											SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE));
				}
  
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
		_contentValues.put(
				SEConstants.MARK1A,
				((TextUtils.isEmpty(mark = tv_mark1a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK1B,
				((TextUtils.isEmpty(mark = tv_mark1b.getText().toString()
						.trim()))) ? "null" : mark);
		
		
		_contentValues.put(
				SEConstants.MARK2A,
				((TextUtils.isEmpty(mark = tv_mark2a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK2B,
				((TextUtils.isEmpty(mark = tv_mark2b.getText().toString()
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
				SEConstants.MARK4A,
				((TextUtils.isEmpty(mark = tv_mark4a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK4B,
				((TextUtils.isEmpty(mark = tv_mark4b.getText().toString()
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
				SEConstants.MARK6A,
				((TextUtils.isEmpty(mark = tv_mark6a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK6B,
				((TextUtils.isEmpty(mark = tv_mark6b.getText().toString()
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
				SEConstants.MARK8A,
				((TextUtils.isEmpty(mark = tv_mark8a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK8B,
				((TextUtils.isEmpty(mark = tv_mark8b.getText().toString()
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
				SEConstants.MARK10A,
				((TextUtils.isEmpty(mark = tv_mark10a.getText().toString()
						.trim()))) ? "null" : mark);
		_contentValues.put(
				SEConstants.MARK10B,
				((TextUtils.isEmpty(mark = tv_mark10b.getText().toString()
						.trim()))) ? "null" : mark);
		
		

		// total marks
		_contentValues.put(SEConstants.R1_2TOTAL, _mark_1_2_total);
		_contentValues.put(
				SEConstants.R3_4TOTAL, _mark_3_4_total);
		_contentValues.put(
				SEConstants.R5_6TOTAL, _mark_5_6_total);
		_contentValues.put(
				SEConstants.R7_8TOTAL, _mark_7_8_total);
		_contentValues.put(  
				SEConstants.R9_10TOTAL, _mark_9_10_total);
  
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
					SEConstants.MARK2A,
					((TextUtils.isEmpty(mark = tv_mark2a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK2B,
					((TextUtils.isEmpty(mark = tv_mark2b.getText().toString()
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
					SEConstants.MARK4A,
					((TextUtils.isEmpty(mark = tv_mark4a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK4B,
					((TextUtils.isEmpty(mark = tv_mark4b.getText().toString()
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
					SEConstants.MARK6A,
					((TextUtils.isEmpty(mark = tv_mark6a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK6B,
					((TextUtils.isEmpty(mark = tv_mark6b.getText().toString()
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
					SEConstants.MARK8A,
					((TextUtils.isEmpty(mark = tv_mark8a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK8B,
					((TextUtils.isEmpty(mark = tv_mark8b.getText().toString()
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
					SEConstants.MARK10A,
					((TextUtils.isEmpty(mark = tv_mark10a.getText().toString()
							.trim()))) ? "null" : mark);
			_contentValues.put(
					SEConstants.MARK10B,
					((TextUtils.isEmpty(mark = tv_mark10b.getText().toString()
							.trim()))) ? "null" : mark);
			

						// total marks
			_contentValues.put(SEConstants.R1_2TOTAL, _mark_1_2_total);
			_contentValues.put(
					SEConstants.R3_4TOTAL, _mark_3_4_total);
			_contentValues.put(
					SEConstants.R5_6TOTAL, _mark_5_6_total);
			_contentValues.put(
					SEConstants.R7_8TOTAL, _mark_7_8_total);
			_contentValues.put(  
					SEConstants.R9_10TOTAL, _mark_9_10_total);
			
			if(TextUtils.isEmpty(_grand_toal))
			{
				_grand_toal = "0";  
			}    
			_contentValues.put(
					SEConstants.GRAND_TOTAL_MARK, _grand_toal);
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
					MarkEntryScreen_R13_BTech_SpecialCase.this, "", msg);
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
		
		// Q2
		case R.id.q2_a:
			setRemarkInContentValue3(SEConstants.M2A_REMARK,
					SEConstants.MARK2A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q2_b:
			setRemarkInContentValue3(SEConstants.M2B_REMARK,
					SEConstants.MARK2B, remarkOrMark, setRemark, insertToTempDB);
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
		
		// Q4
		case R.id.q4_a:
			setRemarkInContentValue3(SEConstants.M4A_REMARK,
					SEConstants.MARK4A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q4_b:
			setRemarkInContentValue3(SEConstants.M4B_REMARK,
					SEConstants.MARK4B, remarkOrMark, setRemark, insertToTempDB);
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
		
		// Q6
		case R.id.q6_a:
			setRemarkInContentValue3(SEConstants.M6A_REMARK,
					SEConstants.MARK6A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q6_b:
			setRemarkInContentValue3(SEConstants.M6B_REMARK,
					SEConstants.MARK6B, remarkOrMark, setRemark, insertToTempDB);
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
		
		// Q8
		case R.id.q8_a:
			setRemarkInContentValue3(SEConstants.M8A_REMARK,
					SEConstants.MARK8A, remarkOrMark, setRemark, insertToTempDB);
			break;
		case R.id.q8_b:
			setRemarkInContentValue3(SEConstants.M8B_REMARK,
					SEConstants.MARK8B, remarkOrMark, setRemark, insertToTempDB);
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
		

		case R.id.q1_2_total:
			setRemarkInContentValue3(SEConstants.R1_2_REMARK,
					SEConstants.R1_2TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q3_4_total:
			setRemarkInContentValue3(SEConstants.R3_4_REMARK,
					SEConstants.R3_4TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q5_6_total:
			setRemarkInContentValue3(SEConstants.R5_6_REMARK,
					SEConstants.R5_6TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q7_8_total:
			setRemarkInContentValue3(SEConstants.R7_8_REMARK,
					SEConstants.R7_8TOTAL, remarkOrMark, setRemark,
					insertToTempDB);

			break;

		case R.id.q9_10_total:
			setRemarkInContentValue3(SEConstants.R9_10_REMARK,
					SEConstants.R9_10TOTAL, remarkOrMark, setRemark,
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
			 if (R13BTech) {
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
					|| etQuesNo == tv_mark2a || etQuesNo == tv_mark2b
					|| etQuesNo == tv_mark3a || etQuesNo == tv_mark3b
					|| etQuesNo == tv_mark4a || etQuesNo == tv_mark4b
					|| etQuesNo == tv_mark5a || etQuesNo == tv_mark5b
					|| etQuesNo == tv_mark6a || etQuesNo == tv_mark6b
					|| etQuesNo == tv_mark7a || etQuesNo == tv_mark7b
					|| etQuesNo == tv_mark8a || etQuesNo == tv_mark8b
					|| etQuesNo == tv_mark9a || etQuesNo == tv_mark9b
					|| etQuesNo == tv_mark10a || etQuesNo == tv_mark10b
					) {
				_max_value = A1Limit;
			}


		return _max_value;
	}

	private float checkRowTotalExceedsMaxValue(View view, String pMarks) {
		if (TextUtils.isEmpty(pMarks)) {
			pMarks = "0";
		}
			if (view.getId() == R.id.q1_a || view.getId() == R.id.q1_b
					|| view.getId() == R.id.q2_a || view.getId() == R.id.q2_b
					|| view.getId() == R.id.q3_a || view.getId() == R.id.q3_b
					|| view.getId() == R.id.q4_a || view.getId() == R.id.q4_b
					|| view.getId() == R.id.q5_a || view.getId() == R.id.q5_b
					|| view.getId() == R.id.q6_a || view.getId() == R.id.q6_b
					|| view.getId() == R.id.q7_a || view.getId() == R.id.q7_b
					|| view.getId() == R.id.q8_a || view.getId() == R.id.q8_b
					|| view.getId() == R.id.q9_a || view.getId() == R.id.q9_b
					|| view.getId() == R.id.q10_a || view.getId() == R.id.q10_b){
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
				et_focusedView.removeTextChangedListener(MarkEntryScreen_R13_BTech_SpecialCase.this);
				et_focusedView.setText("");
				et_focusedView.addTextChangedListener(MarkEntryScreen_R13_BTech_SpecialCase.this);
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
		if (R13BTech && is_subject_code_special_case) {
			String mark;
			ArrayList<Float> listTotalMarks = new ArrayList<Float>();

			if (!TextUtils.isEmpty(_mark_1_2_total)) {
				listTotalMarks.add(Float.valueOf(_mark_1_2_total));
			//	_contentValues.put(SEConstants.R1_TOTAL, _mark1_total);
			}

			if (!TextUtils.isEmpty(_mark_3_4_total)) {
				listTotalMarks.add(Float.valueOf(_mark_3_4_total));
			//	_contentValues.put(SEConstants.R2_3TOTAL, _mark_2_3_total);
			}

			if (!TextUtils.isEmpty(_mark_5_6_total)) {
				listTotalMarks.add(Float.valueOf(_mark_5_6_total));
			//	_contentValues.put(SEConstants.R4_5TOTAL, _mark_4_5_total);
			}

			if (!TextUtils.isEmpty(_mark_7_8_total)) {
				listTotalMarks.add(Float.valueOf(_mark_7_8_total));
			//	_contentValues.put(SEConstants.R6_7TOTAL, _mark_6_7_total);
			}

			if (!TextUtils.isEmpty(_mark_9_10_total)) {
				listTotalMarks.add(Float.valueOf(_mark_9_10_total));
			//	_contentValues.put(SEConstants.R8_9TOTAL, _mark_8_9_total);
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

//			if (roundOffGrandTotal != null) {
//				_contentValues.put(SEConstants.GRAND_TOTAL_MARK,
//						String.valueOf(roundOffGrandTotal));
//			} else {
//				_contentValues.put(SEConstants.GRAND_TOTAL_MARK, "0");
//			}
//			if (_contentValues.size() > 0) {
//				insertToDB(_contentValues);
//			}

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
		row_1_2Total_();
		row_3_4_Total_();
		row_5_6_Total_();
		row_7_8_Total_();
		row_9_10_Total_();

	}


	private String row_1_2Total_() {
		String mark = null;
	String mark1a = tv_mark1a.getText().toString().trim();
	if (TextUtils.isEmpty(mark1a)) {
		mark1a = null;
	}
	String mark1b = tv_mark1b.getText().toString().trim();
	if (TextUtils.isEmpty(mark1b)) {
		mark1b = null;
	}
	
	String mark2a = tv_mark2a.getText().toString().trim();
	if (TextUtils.isEmpty(mark2a)) {
		mark2a = null;
	}
	String mark2b = tv_mark2b.getText().toString().trim();
	if (TextUtils.isEmpty(mark2b)) {
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
			String _value=String.valueOf(_marks1 > _marks2 ? _marks1 : _marks2);
			if (!TextUtils.isEmpty(_value)) {
				if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
					Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
							.show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {

						alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
					}
				} else {
					_mark_1_2_total=_value;
				}
			} else {
				_mark_1_2_total=null;
			}
			  
			return _value;
		}  

	_mark_1_2_total=mark;
	return mark;
	}

		private String row_3_4_Total_() {
	
		
		String mark = null;
		String mark3a = tv_mark3a.getText().toString().trim();
		if (TextUtils.isEmpty(mark3a)) {
			mark3a = null;
		}
		String mark3b = tv_mark3b.getText().toString().trim();
		if (TextUtils.isEmpty(mark3b)) {
			mark3b = null;
		}
		
		String mark4a = tv_mark4a.getText().toString().trim();
		if (TextUtils.isEmpty(mark4a)) {
			mark4a = null;
		}
		String mark4b = tv_mark4b.getText().toString().trim();
		if (TextUtils.isEmpty(mark4b)) {
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
				String _value=String.valueOf(_marks3 > _marks4 ? _marks3 : _marks4);
				if (!TextUtils.isEmpty(_value)) {
					if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
						Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
								.show();
						View focusedView = getCurrentFocus();
						if (focusedView != null && focusedView instanceof EditText) {

							alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
						}
					} else {
						_mark_3_4_total=_value;
					}
				} else {
					_mark_3_4_total=null;
				}
				
				return _value;
			}
		
	
		_mark_3_4_total=mark;
		return mark;
	
	}

	private String row_5_6_Total_() {
		
		String mark = null;
		String mark5a = tv_mark5a.getText().toString().trim();
		if (TextUtils.isEmpty(mark5a)) {
			mark5a = null;
		}
		String mark5b = tv_mark5b.getText().toString().trim();
		if (TextUtils.isEmpty(mark5b)) {
			mark5b = null;
		}
		
		String mark6a = tv_mark6a.getText().toString().trim();
		if (TextUtils.isEmpty(mark6a)) {
			mark6a = null;
		}
		String mark6b = tv_mark6b.getText().toString().trim();
		if (TextUtils.isEmpty(mark6b)) {
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
				String _value=String.valueOf(_marks5 > _marks6 ? _marks5 : _marks6);
				if (!TextUtils.isEmpty(_value)) {
					if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
						Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
								.show();
						View focusedView = getCurrentFocus();
						if (focusedView != null && focusedView instanceof EditText) {

							alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
						}
					} else {
						_mark_5_6_total=_value;
					}
				} else {
					_mark_5_6_total=null;
				}
				
				return _value;
			}
		
	
		_mark_5_6_total=mark;
		return mark;
		}

	private String row_7_8_Total_() {
		
		String mark = null;
	String mark7a = tv_mark7a.getText().toString().trim();
	if (TextUtils.isEmpty(mark7a)) {
		mark7a = null;
	}
	String mark7b = tv_mark7b.getText().toString().trim();
	if (TextUtils.isEmpty(mark7b)) {
		mark7b = null;
	}
	
	String mark8a = tv_mark8a.getText().toString().trim();
	if (TextUtils.isEmpty(mark8a)) {
		mark8a = null;
	}
	String mark8b = tv_mark8b.getText().toString().trim();
	if (TextUtils.isEmpty(mark8b)) {
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
			String _value=String.valueOf(_marks7 > _marks8 ? _marks7 : _marks8);
			if (!TextUtils.isEmpty(_value)) {
				if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
					Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
							.show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {

						alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
					}
				} else {
					_mark_7_8_total=_value;
				}
			} else {
				_mark_7_8_total=null;
			}
			
			return _value;
		}
	

	_mark_7_8_total=mark;
	return mark;
	}
		

	private String row_9_10_Total_() {
		String mark = null;
	String mark9a = tv_mark9a.getText().toString().trim();
	if (TextUtils.isEmpty(mark9a)) {
		mark9a = null;
	}
	String mark9b = tv_mark9b.getText().toString().trim();
	if (TextUtils.isEmpty(mark9b)) {
		mark9b = null;
	}
	
	String mark10a = tv_mark10a.getText().toString().trim();
	if (TextUtils.isEmpty(mark10a)) {
		mark10a = null;
	}
	String mark10b = tv_mark10b.getText().toString().trim();
	if (TextUtils.isEmpty(mark10b)) {
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
			String _value=String.valueOf(_marks9 > _marks10 ? _marks9 : _marks10);
			if (!TextUtils.isEmpty(_value)) {
				if (Float.parseFloat(_value) > Float.parseFloat(""+RowTotalLimit)) {
					Toast.makeText(this, "Total Exceeds "+RowTotalLimit, Toast.LENGTH_SHORT)
							.show();
					View focusedView = getCurrentFocus();
					if (focusedView != null && focusedView instanceof EditText) {

						alertForInvalidMark(focusedView, true, ""+RowTotalLimit);
					}
				} else {
					_mark_9_10_total=_value;
				}
			} else {
				_mark_9_10_total=null;
			}
			
			return _value;
		}
	

	_mark_9_10_total=mark;
	return mark;
	}
			

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v("onDestroy", "MarkEntry");
	}

}
