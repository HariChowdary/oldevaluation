package com.infoplustech.smartscrutinization;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.SSConstants;

public class Scrutiny_AddScriptMarksSummary extends Activity implements
		OnClickListener {

	String userId, subjectCode, ansBookBarcode, bundleNo, bundle_serial_no, SeatNo;
	private PowerManager.WakeLock wl;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.scrutiny_mark_dialog);  

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");

		((TextView) findViewById(R.id.tv_timeinterval))
				.setVisibility(View.GONE);

		Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO);
		ansBookBarcode = intent_extras
				.getStringExtra(SSConstants.ANS_BOOK_BARCODE);
		bundle_serial_no = intent_extras
				.getStringExtra(SSConstants.BUNDLE_SERIAL_NO);
		subjectCode = intent_extras.getStringExtra(SSConstants.SUBJECT_CODE);
		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);

		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);

		findViewById(R.id.btn_submit1).setOnClickListener(this);
		View viewButton = LayoutInflater.from(this).inflate(
				R.layout.scrutiny_layout_add_script_summary, null);
		LinearLayout ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
		viewButton.findViewById(R.id.btn_back_edit).setOnClickListener(this);

		ll_submit.addView(viewButton);
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
		if (!TextUtils.isEmpty(pMark) && !pMark.equals("null")) {
			pTextView.setText(pMark);
		}
	}

	private void setMarkToCellFromDB(String pMark, TextView pTextView) {
		if (!TextUtils.isEmpty(pMark) && !pMark.equals("null")) {
			pTextView.setText(pMark);
		}
	}

	// read and show marks
	private void showItems() {

		Cursor cursor;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		cursor = _database.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
				SSConstants.ANS_BOOK_BARCODE + " = '" + ansBookBarcode
						+ "' AND " + SSConstants.BUNDLE_NO + " = '" + bundleNo
						+ "'", null);

		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
					.moveToNext()) {

				subjectCode = cursor.getString(cursor
						.getColumnIndex(SSConstants.SUBJECT_CODE));

				setMarkToCellFromDB(subjectCode,
						((TextView) findViewById(R.id.tv_sub_code)));

				bundle_serial_no = cursor.getString(cursor
						.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO));
				setMarkToCellFromDB(bundle_serial_no,
						((TextView) findViewById(R.id.tv_ans_book)));

				userId = cursor.getString(cursor
						.getColumnIndex(SSConstants.USER_ID));
				setMarkToCellFromDB(userId,
						((TextView) findViewById(R.id.tv_user_id)));

				// Marks1
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1A)),
						((EditText) findViewById(R.id.q1_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1B)),
						((EditText) findViewById(R.id.q1_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1C)),
						((EditText) findViewById(R.id.q1_c)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1D)),
						((EditText) findViewById(R.id.q1_d)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK1E)),
						((EditText) findViewById(R.id.q1_e)));

//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R1_TOTAL)),
//						((TextView) findViewById(R.id.q1_total)));

				// Marks2
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2A)),
						((EditText) findViewById(R.id.q2_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2B)),
						((EditText) findViewById(R.id.q2_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2C)),
						((EditText) findViewById(R.id.q2_c)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2D)),
						((EditText) findViewById(R.id.q2_d)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK2E)),
						((EditText) findViewById(R.id.q2_e)));

//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R2_TOTAL)),
//						((TextView) findViewById(R.id.q2_total)));

				// Marks3
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3A)),
						((EditText) findViewById(R.id.q3_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3B)),
						((EditText) findViewById(R.id.q3_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3C)),
						((EditText) findViewById(R.id.q3_c)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3D)),
						((EditText) findViewById(R.id.q3_d)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK3E)),
						((EditText) findViewById(R.id.q3_e)));

//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R3_TOTAL)),
//						((TextView) findViewById(R.id.q3_total)));

				// Marks4
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4A)),
						((EditText) findViewById(R.id.q4_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4B)),
						((EditText) findViewById(R.id.q4_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4C)),
						((EditText) findViewById(R.id.q4_c)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4D)),
						((EditText) findViewById(R.id.q4_d)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK4E)),
						((EditText) findViewById(R.id.q4_e)));

//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R4_TOTAL)),
//						((TextView) findViewById(R.id.q4_total)));

				// Marks5
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5A)),
						((EditText) findViewById(R.id.q5_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5B)),
						((EditText) findViewById(R.id.q5_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5C)),
						((EditText) findViewById(R.id.q5_c)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5D)),
						((EditText) findViewById(R.id.q5_d)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK5E)),
						((EditText) findViewById(R.id.q5_e)));

//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R5_TOTAL)),
//						((TextView) findViewById(R.id.q5_total)));

				// Marks6
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6A)),
						((EditText) findViewById(R.id.q6_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6B)),
						((EditText) findViewById(R.id.q6_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6C)),
						((EditText) findViewById(R.id.q6_c)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6D)),
						((EditText) findViewById(R.id.q6_d)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK6E)),
						((EditText) findViewById(R.id.q6_e)));

//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R6_TOTAL)),
//						((TextView) findViewById(R.id.q6_total)));

				// Marks7
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7A)),
						((EditText) findViewById(R.id.q7_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7B)),
						((EditText) findViewById(R.id.q7_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7C)),
						((EditText) findViewById(R.id.q7_c)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7D)),
						((EditText) findViewById(R.id.q7_d)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK7E)),
						((EditText) findViewById(R.id.q7_e)));

//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R7_TOTAL)),
//						((TextView) findViewById(R.id.q7_total)));

				// Marks8
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8A)),
						((EditText) findViewById(R.id.q8_a)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8B)),
						((EditText) findViewById(R.id.q8_b)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8C)),
						((EditText) findViewById(R.id.q8_c)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8D)),
						((EditText) findViewById(R.id.q8_d)));
				setMarkToCellFromDB(cursor.getString(cursor
						.getColumnIndex(SSConstants.MARK8E)),
						((EditText) findViewById(R.id.q8_e)));

//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.R8_TOTAL)),
//						((TextView) findViewById(R.id.q8_total)));

				// grand total
//				setMarkToCellFromDB(cursor.getString(cursor
//						.getColumnIndex(SSConstants.GRAND_TOTAL_MARK)),
//						((TextView) findViewById(R.id.grand_total)));
			}
		}

		cursor.close();
	}

	public void switchToShowGrandTotalSummaryTableActivity() {
		Intent intent = new Intent(this,
				Scrutiny_ShowGrandTotalSummaryTable.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
		intent.putExtra(SSConstants.USER_ID, userId);
		intent.putExtra("SeatNo", SSConstants.SeatNo);
		startActivity(intent);
	}

	private void showAlertForSubmission() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(getString(R.string.alert_submit_corr_marks));
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						switchToShowGrandTotalSummaryTableActivity();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back_edit:
			finish();
			break;

		case R.id.btn_submit1:
			showAlertForSubmission();
			break;

		default:
			break;
		}
	}
}
