package com.infoplus.smartevaluation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infoplus.smartevaluation.db.DBHelper;

public class MarkDialogScreen_R13_Btech_SpecialCase extends Activity implements
		OnClickListener {

	String userId, subjectCode, ansBookBarcode, bundleNo;
	int bundle_serial_no;
	private PowerManager.WakeLock wl;
	private int MaxAnswerBook;
	DBHelper database;   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mark_dialog_r13_btech_specialcase);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		database = DBHelper.getInstance(this);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");

		// ((TextView) findViewById(R.id.tv_timeinterval))
		// .setVisibility(View.GONE);

		Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SEConstants.BUNDLE_NO);
		ansBookBarcode = intent_extras
				.getStringExtra(SEConstants.ANS_BOOK_BARCODE);
		bundle_serial_no = intent_extras.getIntExtra(
				SEConstants.BUNDLE_SERIAL_NO, -1);
		// subjectCode = "10000";
		subjectCode = intent_extras.getStringExtra(SEConstants.SUBJECT_CODE);
		userId = intent_extras.getStringExtra(SEConstants.USER_ID);

		SharedPreferences getScriptCountPrefs = this.getSharedPreferences(
				"ScriptCount", MODE_WORLD_READABLE);

		MaxAnswerBook = getScriptCountPrefs.getInt(SEConstants.SCRIPT_COUNT,
				SEConstants.MAX_ANSWER_BOOK);

		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);

		findViewById(R.id.btn_submit1).setOnClickListener(this);
		View viewButton = LayoutInflater.from(this).inflate(
				R.layout.scrutiny_layout_add_script_summary, null);
		LinearLayout ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
		//viewButton.findViewById(R.id.btn_back_edit).setOnClickListener(this);
		
		Button btnBack=(Button)findViewById(R.id.btn_back);
		btnBack.setOnClickListener(this);

		//ll_submit.addView(viewButton);
		showItems();
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

	private void setMarkToCellFromDB(String pMark, EditText pTextView) {
		pTextView.setFocusable(false);
		pTextView.setFocusableInTouchMode(false);
		if (!TextUtils.isEmpty(pMark) && !pMark.equalsIgnoreCase("null")) {
			pTextView.setText(pMark);
		}
	}

	private void setMarkToCellFromDB(String pMark, TextView pTextView) {
		pTextView.setFocusable(false);
		pTextView.setFocusableInTouchMode(false);
		if (!TextUtils.isEmpty(pMark) && !pMark.equalsIgnoreCase("null")) {
			pTextView.setText(pMark);
		}
	}

	// read and show marks
	private void showItems() {

		Cursor cursor;
		DBHelper _database = DBHelper.getInstance(this);
		cursor = _database.getRow(SEConstants.TABLE_MARKS,
				SEConstants.ANS_BOOK_BARCODE + " = '" + ansBookBarcode
						+ "' AND " + SEConstants.BUNDLE_NO + " = '" + bundleNo   
						+ "'", null);

		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {

				subjectCode = cursor.getString(cursor
						.getColumnIndex(SEConstants.SUBJECT_CODE));  

				setMarkToCellFromDB(subjectCode,
						((TextView) findViewById(R.id.tv_sub_code)));

				bundle_serial_no = cursor.getInt(cursor
						.getColumnIndex(SEConstants.BUNDLE_SERIAL_NO));
				setMarkToCellFromDB(String.valueOf(bundle_serial_no),
						((TextView) findViewById(R.id.tv_ans_book)));

				userId = cursor.getString(cursor
						.getColumnIndex(SEConstants.USER_ID));
				setMarkToCellFromDB(userId,
						((TextView) findViewById(R.id.tv_user_id)));

				// Marks1
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK1A)),
						((EditText) findViewById(R.id.q1_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK1B)),
						((EditText) findViewById(R.id.q1_b)));

				// Marks2
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK2A)),
						((EditText) findViewById(R.id.q2_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK2B)),
						((EditText) findViewById(R.id.q2_b)));

				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.R1_2TOTAL)),
						((TextView) findViewById(R.id.q1_2_total)));

				// Marks3
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK3A)),
						((EditText) findViewById(R.id.q3_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK3B)),
						((EditText) findViewById(R.id.q3_b)));

				// Marks4
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK4A)),
						((EditText) findViewById(R.id.q4_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK4B)),
						((EditText) findViewById(R.id.q4_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.R3_4TOTAL)),
						((TextView) findViewById(R.id.q3_4_total)));

				// Marks5
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK5A)),
						((EditText) findViewById(R.id.q5_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK5B)),
						((EditText) findViewById(R.id.q5_b)));

				// Marks6
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK6A)),
						((EditText) findViewById(R.id.q6_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK6B)),
						((EditText) findViewById(R.id.q6_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.R5_6TOTAL)),
						((TextView) findViewById(R.id.q5_6_total)));

				// Marks7
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK7A)),
						((EditText) findViewById(R.id.q7_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK7B)),
						((EditText) findViewById(R.id.q7_b)));

				// Marks8
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK8A)),
						((EditText) findViewById(R.id.q8_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK8B)),
						((EditText) findViewById(R.id.q8_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.R7_8TOTAL)),
						((TextView) findViewById(R.id.q7_8_total)));

				// Marks9
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK9A)),
						((EditText) findViewById(R.id.q9_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK9B)),
						((EditText) findViewById(R.id.q9_b)));

				// Marks10
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK10A)),
						((EditText) findViewById(R.id.q10_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.MARK10B)),
						((EditText) findViewById(R.id.q10_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.R9_10TOTAL)),
						((TextView) findViewById(R.id.q9_10_total)));

				// grand total
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SEConstants.GRAND_TOTAL_MARK)),
						((TextView) findViewById(R.id.grand_total)));
			}
		}

		cursor.close();
	}

	private void showAlertForSubmission() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder((this));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage("Do you want to submit the marks ?");
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						// switchToShowGrandTotalSummaryTableActivity();
						switchingActivity();
						dialog.dismiss();
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

	public void switchingActivity() {
		// if bundle number less than MAX bundle number
		if (getIntent().hasExtra(
				SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE)) {
			bundleTotal();
		} else {
			if (bundle_serial_no < MaxAnswerBook) {

				// points scanActivity
				switchToScanActivity();

			} else if (bundle_serial_no == MaxAnswerBook
					|| bundle_serial_no == Integer
							.parseInt(SEConstants.EXTRA_Max_BOOK)) {

				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
						this);
				myAlertDialog.setTitle("Smart Evaluation");
				myAlertDialog.setCancelable(false);
				myAlertDialog
						.setMessage("You have Finished the Bundle Successfully! ");

				myAlertDialog.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface Dialog, int arg1) {
								bundleTotal();
							}
						});   

				myAlertDialog.show();

			} else if (bundle_serial_no > MaxAnswerBook
					&& bundle_serial_no < Integer
							.parseInt(SEConstants.EXTRA_Max_BOOK)) {
				switchToScanActivity();
			}
		}

	}

	private void bundleTotal() {

		Intent intent_eval_entry = new Intent(this,
				GrandTotalSummaryTable.class);
		intent_eval_entry.putExtra("UserId", userId);
		intent_eval_entry.putExtra("BundleNo", bundleNo);
		intent_eval_entry.putExtra("SubjectCode", subjectCode);
		intent_eval_entry.putExtra("ScriptCount", MaxAnswerBook);
		intent_eval_entry.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent_eval_entry);
	}

	// switch activity
	public void switchToScanActivity() {

		Intent intent = new Intent(this, ScanActivity.class);

		intent.putExtra("MaxAnswerBook", MaxAnswerBook);
		intent.putExtra("CurrentAnswerBook", (bundle_serial_no + 1));
		intent.putExtra("UserId", userId);
		intent.putExtra("BundleNo", bundleNo);
		// intent.putExtra("SubjectId", SubjectId);
		intent.putExtra("SubjectCode", subjectCode);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back_edit:
			finish();
			break;

		case R.id.btn_submit1:
			showAlertForSubmission();
			break;
			
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
	}
}
