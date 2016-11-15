
package com.infoplus.smartevaluation;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;

public class ScanActivity extends Activity implements OnClickListener,
		OnTouchListener {

	String strcheck;
	int edt_currentbook, CurrentAnswerBook, MaxAnswerBook, BarcodeStatus,
			programId, startTime, time, count, barcode = 0;
	String ScanedAnswerBookNumber, UserId, editUserId, is_updated_server,
			SubjectId, editSubjectId, BundleNo, SubjectCode, editBundleNo,
			edtSubjectCode, programName, regulation_, seatNo;

	EditText editText, bookSno;  
	Button nxtbutton;
	TextView batteryLevel;        
	Button btnCompleted, btnChange;      
	RelativeLayout getRelativeLayout;
  
	private ProgressDialog progressDialog;
	SharedPreferences preferences, getScriptCountPrefs, getProgramPrefs;  
	SharedPreferences.Editor script_count_edit;
	View menuView;
	Date interestingDate;  
	BroadcastReceiver batteryLevelReceiver;
	DBHelper sEvalDatabase;
	Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().hide();
		setContentView(R.layout.scan);
		
		sEvalDatabase = DBHelper.getInstance(this);

		getProgramPrefs = this.getSharedPreferences("program_details",
				MODE_WORLD_READABLE);

		programId = getProgramPrefs.getInt(SEConstants.SHARED_PREF_PROGRAM_ID,
				-1);
		programName = getProgramPrefs.getString(
				SEConstants.SHARED_PREF_PROGRAM_NAME, "");

		// regulation = getProgramPrefs.getString(
		// SEConstants.SHARED_PREF_REGULATION_NAME, "");

		((TextView) findViewById(R.id.txt_programName)).setText(programName);

		menuView = LayoutInflater.from(this).inflate(R.layout.layout_menu_scan,
				null);
		((TextView) menuView.findViewById(R.id.tv_save_n_exit))
				.setOnClickListener(this);
		btnCompleted = (Button) findViewById(R.id.btn_bundle_completed);
		btnCompleted.setOnClickListener(this);  
                       
		btnChange = (Button) findViewById(R.id.btn_change_bundle);
		btnChange.setOnClickListener(this);

		((TextView) menuView.findViewById(R.id.tv_unread_barcode))   
				.setOnClickListener(this);       
		((RelativeLayout) findViewById(R.id.rel_lay_menu_scan))
				.addView(menuView);

//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
//				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    
		Bundle b = getIntent().getExtras();
		// bundleNumber = intent.getIntExtra("CurrentBundleNumber", -1);
		CurrentAnswerBook = b.getInt("CurrentAnswerBook");

		if (CurrentAnswerBook == 1) {

			btnChange.setEnabled(true);
			btnChange.setVisibility(View.INVISIBLE);

			btnCompleted.setEnabled(false);
			btnCompleted.setVisibility(View.GONE);
		} else {  
			btnChange.setEnabled(false);
			btnChange.setVisibility(View.GONE);

			btnCompleted.setEnabled(true);
			btnCompleted.setVisibility(View.VISIBLE);  
		}

		UserId = b.getString("UserId");

		((TextView) findViewById(R.id.txt_eval_id)).setText("ID:  " + UserId);

		BundleNo = b.getString("BundleNo");
		SubjectId = b.getString("SubjectId");
		SubjectCode = b.getString("SubjectCode");
		seatNo = b.getString("SeatNo");
		((TextView) findViewById(R.id.tv_sub_code)).setText(SubjectCode);
		((TextView) findViewById(R.id.tv_bun_code)).setText(BundleNo);
		((TextView) findViewById(R.id.tv_seatno)).setText(seatNo);
		if (CurrentAnswerBook == 1) {
			btnCompleted.setVisibility(View.GONE);
		} else {
			btnCompleted.setVisibility(View.VISIBLE);
		}
		getRelativeLayout = (RelativeLayout) this.findViewById(R.id.Container);
		getRelativeLayout.setOnTouchListener(this);

		batteryLevel = (TextView) this.findViewById(R.id.txt_batteryLevel);
		// batteryLevel();

		Button button = (Button) findViewById(R.id.scan_answer_book_button);
		button.setOnClickListener(this);

		Button clearbutton = (Button) findViewById(R.id.barcode_clear);
		clearbutton.setOnClickListener(this);

		bookSno  = (EditText) findViewById(R.id.WriteSerialNumberTextView1);
//		textView.setText(this.getString(R.string.write_serial_number) + " "
//				+ CurrentAnswerBook);

		editText = (EditText) this.findViewById(R.id.editTextScanAnswerBook);
		editText.setTransformationMethod(new HiddenPassTransformationMethod());
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
				}
			}
		});
		editText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

					scanBarcode();
					// nxtbutton.setFocusableInTouchMode(true);
					// nxtbutton.requestFocus();

					return true;

				}
				return false;
			}

		});

		SharedPreferences settings = getSharedPreferences("SAVE_EXIT", 0);
		boolean isSaveExit = settings.getBoolean("isSaveExit", false);

		if (isSaveExit) {

			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("isSaveExit", false);

			// Commit the edits!
			editor.commit();

			this.showView(this.getIntent().getStringExtra("AnswerBookNumber"),
					BarcodeStatus);
		}

		 MaxAnswerBook = this.getIntent().getIntExtra("MaxAnswerBook", 50);
		/*getScriptCountPrefs = this.getSharedPreferences("ScriptCount",
				MODE_WORLD_READABLE);

		MaxAnswerBook = getScriptCountPrefs.getInt(SEConstants.SCRIPT_COUNT,
				SEConstants.MAX_ANSWER_BOOK);*/

		((TextView) menuView.findViewById(R.id.tv_smart_eval)).setText(this
				.getString(R.string.app_name)
				+ " "
				+ CurrentAnswerBook
				+ " of " + MaxAnswerBook);
	}

	/*@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}*/

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Toast.makeText(this, "Press Home Button to pause Evaluation",
					Toast.LENGTH_LONG).show();
			return false;
		}
		return false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.scan_answer_book_button:
			this.scanBarcode();

			break;

		case R.id.barcode_clear:
			editText.setText("");
			bookSno.setText("");
			break;

		case R.id.nextButton:
			showProgress();
			navigatingScreen();
			break;

		case R.id.tv_save_n_exit:

			saveNExit();
			break;

		case R.id.btn_change_bundle:
			changeBundle();
			break;

		case R.id.btn_bundle_completed:
			completed();
			break;

		case R.id.tv_unread_barcode:
		//	unReadableBarCode();

			break;

		default:
			break;
		}

	}

	void insertDefaultBarcode() {
		Cursor cur = null;
		try {
			cur = sEvalDatabase
					.executeSelectSQLQuery("select count(*) as Value from table_marks where TRIM(UPPER(bundle_no)) = TRIM(UPPER('"
							+ BundleNo
							+ "')) and TRIM(UPPER(user_id))=TRIM(UPPER('"
							+ UserId + "')) and barcode_status=2");

			if ((cur != null) && (cur.getCount() > 0)) {
				int _barcode = 0;
				while (!cur.isAfterLast()) {
					String barcodecount = cur.getString(cur
							.getColumnIndex("Value"));
					int count = Integer.parseInt(barcodecount);

					_barcode = 1000 + count;

					cur.moveToNext();

				}
				showView(BundleNo + String.valueOf(_barcode), 2);
			} else {
				FileLog.logInfo(
						"Cursor Null ---> ScanActivity: insertDefaultBarcode() ",
						0);
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> ScanActivity: insertDefaultBarcode() "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cur);
		}

	}
private	String getCurrentAnswerBookNo(String sno) {
	String barcode="";
			Cursor cursor = null;
			try {
				String qry="select tm.barcode "
						+ "from table_bundle as tb inner join table_marks"
						+ " as tm on tb.bundle_no = tm.bundle_no "
						+ "where tb.bundle_no = '"
						+ BundleNo
						+ "' and tb.subject_code =  '"
						+ SubjectCode
						+ "' and tb.enter_by  = '" + UserId + "' and tm.bundle_serial_no='"+sno+"'";
				Log.v("tag", "qry "+qry);
				cursor = sEvalDatabase
						.executeSelectSQLQuery(qry);

				if ((cursor != null) && (cursor.getCount() > 0)) {
					while (!(cursor.isAfterLast())) {
						barcode=cursor.getString(cursor.getColumnIndex("barcode"));
						Log.v("barcode", "barcode "+barcode);
						cursor.moveToNext();
					}
				} else {
					FileLog.logInfo(
							"Cursor Null--->EvaluatorEntryActivity: switchingActivity(): ",
							0);
				}

			} catch (Exception ex) {
				FileLog.logInfo(
						"Exception --->EvaluatorEntryActivity: switchingActivity(): "
								+ ex.toString(), 0);
			} finally {
				DataBaseUtility.closeCursor(cursor);
			}
			return barcode;
	}
	
	
	private Boolean bookSnoValid(){
		Boolean val=false;
		String sno=bookSno.getText().toString().trim();
			if (!TextUtils.isEmpty(sno)) {
				if(isInteger(sno)){
					Log.v("CurrentAnswerBook", CurrentAnswerBook+"  "+sno);
					if(Integer.parseInt(sno)<51){
				if(CurrentAnswerBook==Integer.parseInt(sno)){
					val=true;
				}else{
					alertmsg("Enter Valid Serial Number");
					val=false;
					/*
					String barcode=getCurrentAnswerBookNo(sno);
					if(TextUtils.isEmpty(barcode)){
						bookSno.setText("");
					alertmsg("Wrong Entry. Book Serial Number is "+CurrentAnswerBook);
					}else{
						if(barcode.equals(editText.getText().toString().trim())){
							edt_currentbook=Integer.parseInt(sno);
						//	bookEditOption(barcode);
						}else{
							alertmsg("Serial number Already exists." +
									"");
						}
					}
					
				*/}
					}else{
						alertmsg("Enter Valid Serial Number");
					}
				}else{
					bookSno.setText("");
					alertmsg("Enter only number");
					val=false;
				}
			}else{
				alertmsg("Enter Book serial number");
				val=false;
			}
		return val;
	}
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	public void scanBarcode() {
		if(bookSnoValid()){
		if (editText != null) {
			Editable editable = editText.getText();
			if (editable != null) {
				String value = editable.toString().trim();
				if (comparesAnswerSheetCodeWithBundleNo(value)) {
					if (value.matches("[0-9]+")) {
						if ((value.length() == 10) || (value.length() == 11)) {
							showView(value.trim(), 1);
						} else {
							alertmsg("Invalid Barcode ");
						}
					}      
					else {  
						alertmsg("Invalid Barcode ");
					}
				} else {
					alertmsg("Both Bundle Number and Answer Sheet Barcode are the Same. Please Enter Valid Barcodes");
				}

			}
		}
		}
	}

	private boolean comparesAnswerSheetCodeWithBundleNo(String scanCodeNo) {

		boolean istrue = false;
		Cursor _cursor = null;
		try {
			_cursor = sEvalDatabase.getRow("table_bundle", "bundle_no" + " = '"
					+ scanCodeNo + "'", null);
			if (_cursor != null) {
				if (_cursor.getCount() > 0) {

					istrue = false;
				} else {
					istrue = true;
				}
			} else {
				FileLog.logInfo(
						"Cursor Null ---> ScanActivity: comparesAnswerSheetCodeWithBundleNo(): ",
						0);

			} 
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> ScanActivity: comparesAnswerSheetCodeWithBundleNo(): "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(_cursor);
		}
		return istrue;

	}

	private void alertmsg(String msg) {

		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setMessage(msg);

		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						bookSno.setText("");
						editText.setText("");
						Dialog.dismiss();

					}
				});

		myAlertDialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				// The Intents Fairy has delivered us some data!
				String contents = intent.getStringExtra("SCAN_RESULT");
				// String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan

				if ((contents != null)
						&& ((contents.length() == 10) && (contents.length() == 11))) {

					showView(contents.trim(), 1);
				}

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Toast.makeText(this,
						"Unable to scan the barcode, please try again!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (resolveInfo.size() > 0) {
			return true;
		}
		return false;
	}

	private void showView(final String barcode, int status) {

		if (status == 2) {
			showAnswerBookNo(barcode, status);
		}

		else if (status == 1) {
			// final String editingValue = value;
			String edtbundle_no = null;
			Cursor cursor = null;
			BarcodeStatus = status;
			try {
				cursor = sEvalDatabase
						.executeSelectSQLQuery("select bundle_serial_no,bundle_no,subject_code,"
								+ "is_updated_server from table_marks where TRIM(UPPER(barcode)) = TRIM(UPPER('"
								+ barcode.trim() + "'))");

				if (cursor != null && cursor.getCount() > 0) {/*
					while (!(cursor.isAfterLast())) {
						edt_currentbook = Integer.parseInt(cursor
								.getString(cursor
										.getColumnIndex("bundle_serial_no")));
						is_updated_server = cursor.getString(cursor
								.getColumnIndex("is_updated_server"));
						edtSubjectCode = cursor.getString(cursor
								.getColumnIndex("subject_code"));
						edtbundle_no = cursor.getString(cursor
								.getColumnIndex("bundle_no"));
						cursor.moveToNext();
					}
					
					 String sno=bookSno.getText().toString().trim();
						if(edt_currentbook==Integer.parseInt(sno)){
						bookEditOption(barcode);
						}else{
							alertmsg("Answer Book Already Exists..!");
						}
							
					
				*/
					alertmsg("Answer Book Already Exists..!");
					}

				else {
					FileLog.logInfo(
							"Cursor Null ---> ScanActivity: showView() ", 0);

					showAnswerBookNo(barcode, status);
					// getSubjectSetBySubjectId();
				}
			} catch (Exception ex) {
				FileLog.logInfo("Exception ---> ScanActivity: showView() ", 0);
			} finally {
				DataBaseUtility.closeCursor(cursor);
			}

		}
	}
	void bookEditOption(final String barcode){
		if ((edt_currentbook != 0)) {
			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
					this);
			myAlertDialog.setTitle("Smart Evaluation");
			myAlertDialog.setCancelable(false);
			myAlertDialog
					.setMessage("Answer Book Already Exists. Do You Want to Edit it? ");

			myAlertDialog.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								final DialogInterface Dialog,
								int arg1) {

							// check for regulation and navigate

							Utility instanceUtility = new Utility();
							if (instanceUtility
									.isRegulation_R13_Mtech(context)) {
								Intent intent = new Intent(
										ScanActivity.this,
										MarkEntryScreen_R13.class);
								intent.putExtra("MaxAnswerBook",
										MaxAnswerBook);   
								intent.putExtra(
										SEConstants.BUNDLE_NO,
										BundleNo);
								intent.putExtra(
										SEConstants.ANS_BOOK_BARCODE,
										barcode);
								intent.putExtra(     
										SEConstants.SUBJECT_CODE,
										SubjectCode);
								intent.putExtra("SeatNo", SEConstants.seatNo);
								intent.putExtra(
										SEConstants.USER_ID,
										UserId);
								intent.putExtra(
										SEConstants.BUNDLE_SERIAL_NO,
										edt_currentbook);  
								intent.putExtra(SEConstants.BUNDLE_TIMER,
										false);
								intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
										CurrentAnswerBook);
								intent.putExtra(SEConstants.BARCODE_STATUS, BarcodeStatus);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
							else
							if (instanceUtility
									.isRegulation_R13_Btech(context) && 
									SubjectCode.equalsIgnoreCase("111AG")
									|| SubjectCode.equalsIgnoreCase("111AH")	
									|| SubjectCode.equalsIgnoreCase("111AJ")
									|| SubjectCode.equalsIgnoreCase("111AK")) {
								
								Intent intent = new Intent(
										ScanActivity.this,
										MarkEntryScreen_R13_BTech_SpecialCase.class);
								intent.putExtra(
										SEConstants.BUNDLE_NO,
										BundleNo);
								intent.putExtra("MaxAnswerBook",
										MaxAnswerBook);
								intent.putExtra(
										SEConstants.ANS_BOOK_BARCODE,
										barcode);
								intent.putExtra(
										SEConstants.SUBJECT_CODE,
										SubjectCode);
								intent.putExtra("SeatNo", SEConstants.seatNo);
								intent.putExtra(
										SEConstants.USER_ID,
										UserId);
								intent.putExtra(
										SEConstants.BUNDLE_SERIAL_NO,
										edt_currentbook);
								intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
										CurrentAnswerBook);
								intent.putExtra(SEConstants.BARCODE_STATUS, BarcodeStatus);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

							}
							else
								if (instanceUtility
										.isRegulation_R13_Btech(context) && 
										SubjectCode.equalsIgnoreCase(SEConstants.SUBJ_114DA_ENGG_DRAWING)
										|| SubjectCode.equalsIgnoreCase(SEConstants.SUBJ_114DB_ENGG_DRAWING)) {
									
									Intent intent = new Intent(
											ScanActivity.this,
											MarkEntryScreen_R13_BTech_SpecialCase_New.class);
									intent.putExtra(
											SEConstants.BUNDLE_NO,
											BundleNo);
									intent.putExtra("MaxAnswerBook",
											MaxAnswerBook);
									intent.putExtra(
											SEConstants.ANS_BOOK_BARCODE,
											barcode);
									intent.putExtra(
											SEConstants.SUBJECT_CODE,
											SubjectCode);
									intent.putExtra("SeatNo", SEConstants.seatNo);
									intent.putExtra(
											SEConstants.USER_ID,
											UserId);
									intent.putExtra(
											SEConstants.BUNDLE_SERIAL_NO,
											edt_currentbook);
									intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
											CurrentAnswerBook);
									intent.putExtra(SEConstants.BARCODE_STATUS, BarcodeStatus);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);

								}

							else if (instanceUtility
									.isRegulation_R13_Btech(context)) {
								Intent intent = new Intent(
										ScanActivity.this,
										MarkEntryScreen_R13.class);
								intent.putExtra(
										SEConstants.BUNDLE_NO,
										BundleNo);
								intent.putExtra("MaxAnswerBook",
										MaxAnswerBook);
								intent.putExtra(
										SEConstants.ANS_BOOK_BARCODE,
										barcode);
								intent.putExtra(         
										SEConstants.SUBJECT_CODE,
										SubjectCode);
								intent.putExtra("SeatNo", SEConstants.seatNo);
								intent.putExtra(
										SEConstants.USER_ID,
										UserId);
								intent.putExtra(
										SEConstants.BUNDLE_SERIAL_NO,
										edt_currentbook);  
								intent.putExtra(SEConstants.BUNDLE_TIMER,
										false);
								intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
										CurrentAnswerBook);
								intent.putExtra(SEConstants.BARCODE_STATUS, BarcodeStatus);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

							}
							
							
							else if (instanceUtility
									.isRegulation_R09_Course(context)
									|| instanceUtility.
									isRegulation_R09_MTech_Course(context)
									) {
								Intent intent = new Intent(
										ScanActivity.this,
										MarkEntryScreen_R09.class);
								intent.putExtra(
										SEConstants.BUNDLE_NO,
										BundleNo);
								intent.putExtra("MaxAnswerBook",
										MaxAnswerBook);
								intent.putExtra(
										SEConstants.ANS_BOOK_BARCODE,  
										barcode);
								intent.putExtra(     
										SEConstants.SUBJECT_CODE,
										SubjectCode);
								intent.putExtra("SeatNo", SEConstants.seatNo);
								intent.putExtra(
										SEConstants.USER_ID,
										UserId);
								intent.putExtra(
										SEConstants.BUNDLE_SERIAL_NO,
										edt_currentbook);  
								intent.putExtra(SEConstants.BUNDLE_TIMER,
										false);
								intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
										CurrentAnswerBook);
								intent.putExtra(SEConstants.BARCODE_STATUS, BarcodeStatus);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
							else {
								alertmsg("Regulation with Degree is Mis-Matched");
							}

							Dialog.dismiss();
						}
					});

			myAlertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface Dialog, int arg1) {

							editText.setText("");
							bookSno.setText("");
							Dialog.dismiss();
						}
					});

			myAlertDialog.show();
		}
	
	}
	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
//		if (v == getRelativeLayout) {
//			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//
//			return true;
//		}
		return false;
	}

	private void unReadableBarCode() {
		if(bookSnoValid()){
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setCancelable(false);
		myAlertDialog
				.setMessage("Unreadable Barcodes will be Replaced with ##******##");

		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked

						Dialog.dismiss();
						insertDefaultBarcode();

					}
				});

		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
					}
				});
		myAlertDialog.show();
	}
	}

	private void changeBundle() {

		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				ScanActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage("Do You Want to Change the Bundle? ");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						// do something when the OK button is clicked

						Dialog.dismiss();
						Cursor cursor = null;
						try {
							cursor = sEvalDatabase
									.executeSelectSQLQuery("select count(*) as value from table_marks where "
											+ "TRIM(UPPER(bundle_no)) = TRIM(UPPER('"
											+ BundleNo.trim()
											+ "')) and TRIM(UPPER(user_id))=TRIM(UPPER('"
											+ UserId + "'))");
							if (cursor != null) {
								while (cursor.moveToNext()) {
									count = Integer.parseInt(cursor
											.getString(cursor
													.getColumnIndex("value")));

								}

								if (count == 0) {
									try {
										int cnt = sEvalDatabase.deleteRow(
												"table_bundle", "bundle_no = '"
														+ BundleNo.trim()
														+ "' and enter_by = '"
														+ UserId + "'");
										if (cnt == 0) {
											FileLog.logInfo(
													"Bundle not deleted ---> ScanActivity: changeBundle(): ",
													0);
										}
										Intent intent = new Intent(
												ScanActivity.this,
												BundleNumberActivity.class);

										intent.putExtra("UserId", UserId);
										intent.putExtra("SubjectId", SubjectId);

										intent.putExtra("SubjectCode",
												SubjectCode);
										intent.putExtra("SeatNo", SEConstants.seatNo);
										intent.putExtra("MaxAnswerBook",
												MaxAnswerBook);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
									} catch (Exception ex) {
										FileLog.logInfo(
												"Exception ---> ScanActivity: changeBundle(): ",
												0);
									}

								}
							} else {
								FileLog.logInfo(
										"Cursor Null ---> ScanActivity: changeBundle(): ",
										0);
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> ScanActivity: changeBundle(): ",
									0);
						} finally {
							DataBaseUtility.closeCursor(cursor);
						}

					}
				});

		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
					}
				});
		myAlertDialog.show();

	}

	private void completed() {

		int Serial_no = CurrentAnswerBook - 1;
		AlertDialog.Builder compAlertDialog = new AlertDialog.Builder(
				ScanActivity.this);
		compAlertDialog.setCancelable(false);
		compAlertDialog.setTitle("Smart Evaluation");
		compAlertDialog
				.setMessage("This Bundle Contains " + Serial_no + " Scripts. "
						+ "Are You Sure of Submitting this Bundle Now? ");
		compAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		compAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						// do something when the OK button is clicked

						Dialog.dismiss();

						Intent intent_eval_entry = new Intent(
								ScanActivity.this,
								GrandTotalSummaryTable.class);
						intent_eval_entry.putExtra("UserId", UserId);
						intent_eval_entry.putExtra("BundleNo", BundleNo);
						intent_eval_entry.putExtra("SubjectCode", SubjectCode);
						intent_eval_entry.putExtra("SeatNo", SEConstants.seatNo);
						intent_eval_entry
								.putExtra("MaxAnswerBook", MaxAnswerBook);
						intent_eval_entry
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent_eval_entry);

					}
				});

		compAlertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
					}
				});
		compAlertDialog.show();
	}

	private void saveNExit() {
		AlertDialog.Builder saveAlertDialog = new AlertDialog.Builder(
				ScanActivity.this);
		saveAlertDialog.setTitle("Smart Evaluation");
		saveAlertDialog.setCancelable(false);
		saveAlertDialog.setMessage("Save and Exit ! ");
		saveAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		saveAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						// do something when the OK button is clicked

						Dialog.dismiss();
						Intent intent1 = new Intent(Intent.ACTION_MAIN);
						intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent1.addCategory(Intent.CATEGORY_HOME);
						startActivity(intent1);

					}
				});

		saveAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override  
					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
					}
				});
		saveAlertDialog.show();
	}

	public void navigatingScreen() {
		Utility instanceUtility = new Utility();
		 if (instanceUtility.isRegulation_R13_Btech(context)
				|| instanceUtility.isRegulation_R13_Mtech(context)
				|| instanceUtility.isRegulation_R15_Mtech(context)
				) {
			confirmationAlertForRegulation(1);
		}
		
		else if ( instanceUtility.isRegulation_R09_Course(context)
				|| instanceUtility.isRegulation_R09_MTech_Course(context)
				) {
			confirmationAlertForRegulation(3);
		}
		
		else {
			alertmsg("Regulation with Degree is Mis-Matched");
		}

	}

	public void confirmationAlertForRegulation(final int getStatus) {

		/*if (getStatus == 1) {
			Intent intent = new Intent(ScanActivity.this,
					MarkEntryScreen_R13.class);  
			intent.putExtra("Evaluator_Id", ScanActivity.this.getIntent()
					.getStringExtra("Evaluator_Id"));
			intent.putExtra("MaxAnswerBook", MaxAnswerBook);
			intent.putExtra("StartTime", startTime);
			intent.putExtra(SEConstants.USER_ID, UserId);
			intent.putExtra(SEConstants.BUNDLE_NO, BundleNo);
			intent.putExtra("SubjectId", SubjectId);
			intent.putExtra(SEConstants.SUBJECT_CODE, SubjectCode);
			intent.putExtra("SeatNo", SEConstants.seatNo);
			intent.putExtra("Time", time);  
			intent.putExtra(SEConstants.BUNDLE_TIMER,
					true);
			intent.putExtra(SEConstants.BARCODE_STATUS, BarcodeStatus);
			intent.putExtra(SEConstants.BUNDLE_SERIAL_NO, CurrentAnswerBook);
			intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
					000);
			Log.e("ScanActivity", "barcode = " + ScanedAnswerBookNumber);
			intent.putExtra(SEConstants.ANS_BOOK_BARCODE,
					ScanedAnswerBookNumber);
			interestingDate = new Date();
			intent.putExtra("noteDate", interestingDate.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			}
		*/  if (getStatus == 1) {
			if( SubjectCode.equalsIgnoreCase("111AG")   
					|| SubjectCode.equalsIgnoreCase("111AH")	
					|| SubjectCode.equalsIgnoreCase("111AJ")
					|| SubjectCode.equalsIgnoreCase("111AK")  
						){	  
					    Intent intent = new Intent(ScanActivity.this,
					    		MarkEntryScreen_R13_BTech_SpecialCase.class);
						intent.putExtra("Evaluator_Id", ScanActivity.this
								.getIntent().getStringExtra("Evaluator_Id"));
						intent.putExtra("MaxAnswerBook", MaxAnswerBook);
						intent.putExtra("StartTime", startTime);
						intent.putExtra(SEConstants.USER_ID, UserId);
						intent.putExtra(SEConstants.BUNDLE_NO, BundleNo);
						intent.putExtra("SubjectId", SubjectId);
						intent.putExtra(SEConstants.SUBJECT_CODE,
								SubjectCode);
						intent.putExtra("SeatNo", SEConstants.seatNo);
						intent.putExtra("Time", time);
						intent.putExtra(SEConstants.BUNDLE_SERIAL_NO,
								CurrentAnswerBook);
						intent.putExtra(SEConstants.BARCODE_STATUS,
								BarcodeStatus);
						Log.e("ScanActivity", "barcode = "
								+ ScanedAnswerBookNumber);
						intent.putExtra(SEConstants.ANS_BOOK_BARCODE,
								ScanedAnswerBookNumber);
						interestingDate = new Date();
						intent.putExtra("noteDate",
								interestingDate.toString());
						intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
								000);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);  
				}
			else if(SubjectCode.equalsIgnoreCase(SEConstants.SUBJ_114DA_ENGG_DRAWING)
					|| SubjectCode.equalsIgnoreCase(SEConstants.SUBJ_114DB_ENGG_DRAWING)){	
					    Intent intent = new Intent(ScanActivity.this,
					    		MarkEntryScreen_R13_BTech_SpecialCase_New.class);
						intent.putExtra("Evaluator_Id", ScanActivity.this
								.getIntent().getStringExtra("Evaluator_Id"));
						intent.putExtra("MaxAnswerBook", MaxAnswerBook);
						intent.putExtra("StartTime", startTime);
						intent.putExtra(SEConstants.USER_ID, UserId);
						intent.putExtra(SEConstants.BUNDLE_NO, BundleNo);
						intent.putExtra("SubjectId", SubjectId);
						intent.putExtra(SEConstants.SUBJECT_CODE,
								SubjectCode);
						intent.putExtra("SeatNo", SEConstants.seatNo);
						intent.putExtra("Time", time);
						intent.putExtra(SEConstants.BUNDLE_SERIAL_NO,
								CurrentAnswerBook);
						intent.putExtra(SEConstants.BARCODE_STATUS,
								BarcodeStatus);
						Log.e("ScanActivity", "barcode = "
								+ ScanedAnswerBookNumber);
						intent.putExtra(SEConstants.ANS_BOOK_BARCODE,
								ScanedAnswerBookNumber);
						interestingDate = new Date();
						intent.putExtra("noteDate",
								interestingDate.toString());
						intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
								000);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);  
				}
		else {           
			Intent intent = new Intent(ScanActivity.this,      
					MarkEntryScreen_R13.class);
			intent.putExtra("Evaluator_Id", ScanActivity.this.getIntent()
					.getStringExtra("Evaluator_Id"));
			intent.putExtra("MaxAnswerBook", MaxAnswerBook);
			intent.putExtra("StartTime", startTime);
			intent.putExtra(SEConstants.USER_ID, UserId);
			intent.putExtra(SEConstants.BUNDLE_NO, BundleNo);
			intent.putExtra("SubjectId", SubjectId);
			intent.putExtra(SEConstants.SUBJECT_CODE, SubjectCode);  
			intent.putExtra("SeatNo", SEConstants.seatNo);
			intent.putExtra("Time", time);
			intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
					000);
			intent.putExtra(SEConstants.BUNDLE_SERIAL_NO, CurrentAnswerBook);
			intent.putExtra(SEConstants.BARCODE_STATUS, BarcodeStatus);
			Log.e("ScanActivity", "barcode = " + ScanedAnswerBookNumber);
			intent.putExtra(SEConstants.ANS_BOOK_BARCODE,
					ScanedAnswerBookNumber);
			interestingDate = new Date();
			intent.putExtra("noteDate", interestingDate.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		 }  
		} else if (getStatus == 3) {
			Intent intent = new Intent(ScanActivity.this,
					MarkEntryScreen_R09.class);  
			intent.putExtra("Evaluator_Id", ScanActivity.this.getIntent()  
					.getStringExtra("Evaluator_Id"));
			intent.putExtra("MaxAnswerBook", MaxAnswerBook);
			intent.putExtra("StartTime", startTime);  
			intent.putExtra(SEConstants.USER_ID, UserId);  
			intent.putExtra(SEConstants.BUNDLE_NO, BundleNo);
			intent.putExtra("SubjectId", SubjectId);
			intent.putExtra(SEConstants.SUBJECT_CODE, SubjectCode);
			intent.putExtra("SeatNo", SEConstants.seatNo);
			intent.putExtra("Time", time);    
			intent.putExtra(SEConstants.BUNDLE_TIMER,    
					true);    
			intent.putExtra(SEConstants.BARCODE_STATUS, BarcodeStatus);
			intent.putExtra(SEConstants.BUNDLE_SERIAL_NO, CurrentAnswerBook);
			intent.putExtra(SEConstants.BUNDLE_SERIAL_OLD_NO,
					000);
			Log.e("ScanActivity", "barcode = " + ScanedAnswerBookNumber);
			intent.putExtra(SEConstants.ANS_BOOK_BARCODE,
					ScanedAnswerBookNumber);
			interestingDate = new Date();
			intent.putExtra("noteDate", interestingDate.toString());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} 

	}

	private void showAnswerBookNo(String value, int status) {
		ScanedAnswerBookNumber = value;
		BarcodeStatus = status;
		navigatingScreen();
	}

	public void showProgress() {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(ScanActivity.this,
					"SmartEvalUation", "Loading...");
			progressDialog.setIcon(getResources().getDrawable(
					R.drawable.ic_launcher));
			progressDialog.setCancelable(false);

		}
	}

	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		batteryLevel();
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
					batteryLevel.setText("Battery Level : " + level);
				}

				if (level < SEConstants.TABLET_CHARGE) {
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

	}// End of onPause

	// password hidden class
	private class HiddenPassTransformationMethod implements
			TransformationMethod {

		private char DOT = '\u2022';

		@Override
		public CharSequence getTransformation(final CharSequence charSequence,
				final View view) {
			return new PassCharSequence(charSequence);
		}

		@Override
		public void onFocusChanged(final View view,
				final CharSequence charSequence, final boolean b, final int i,
				final Rect rect) {
			// nothing to do here
		}

		private class PassCharSequence implements CharSequence {

			private final CharSequence charSequence;

			public PassCharSequence(final CharSequence charSequence) {
				this.charSequence = charSequence;
			}

			@Override
			public char charAt(final int index) {
				return DOT;
			}

			@Override
			public int length() {
				return charSequence.length();
			}

			@Override
			public CharSequence subSequence(final int start, final int end) {
				return new PassCharSequence(
						charSequence.subSequence(start, end));
			}
		}
	}

}
