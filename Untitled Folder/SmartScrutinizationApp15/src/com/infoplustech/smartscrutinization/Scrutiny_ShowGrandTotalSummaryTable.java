
package com.infoplustech.smartscrutinization;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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
import android.database.SQLException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.callback.Scrutiny_NetworkCallback;
import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.FileLog;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Scrutiny_DataBaseUtility;
import com.infoplustech.smartscrutinization.utils.Scrutiny_SoapServiceManager;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_ShowGrandTotalSummaryTable extends Activity implements
		OnClickListener {

	String userId;
	String bundleNo;
	String subjectCode, SeatNo;
	View menuView;
	String bundeId;
	int maxAnswerBook;
	int currentAnsBook;
	boolean scrutinySelection;
	private PowerManager.WakeLock wl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		entry();    
	}   

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.scrutiny_activity_main, menu);
	 * menu.getItem(0).setVisible(false); return true; }
	 * 
	 * @Override public boolean onMenuItemSelected(int featureId, MenuItem item)
	 * { if (item.getItemId() == R.id.menu_back) { finish(); } return
	 * super.onMenuItemSelected(featureId, item); }
	 */

	public void entry() {
		setContentView(R.layout.scrutiny_show_grand_total_summary_table);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");

		Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO);

		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);
		((TextView) findViewById(R.id.tv_eval_id)).setText(userId);
		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);

		findViewById(R.id.btn_ok).setOnClickListener(this);

		SharedPreferences _sharedPreferences;
		_sharedPreferences = getSharedPreferences(
				SSConstants.SCRUTINY_SELECTED, Context.MODE_PRIVATE);
		scrutinySelection = _sharedPreferences.getBoolean(
				SSConstants.SCRUTINY_SELECTED, false);

		Button btnAddScript = (Button) findViewById(R.id.btn_add_script);
		btnAddScript.setOnClickListener(this);
		Cursor cursor_grand_totals = null;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		
		menuView = LayoutInflater.from(this).inflate(
				R.layout.layout_menu_totalscript, null);

		((TextView) menuView.findViewById(R.id.seatno))
		.setVisibility(View.VISIBLE);
		
		((TextView) menuView.findViewById(R.id.tv_seatno))
		.setVisibility(View.VISIBLE);  
		((TextView) menuView.findViewById(R.id.tv_seatno))
		.setText(SeatNo);
		
		if(scrutinySelection){
			cursor_grand_totals = _database.passedQuery(
					SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO + " = '"
							+ bundleNo + "'", SSConstants.BUNDLE_SERIAL_NO);
			}else{
		cursor_grand_totals = _database.passedQuery(
				SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO + " = '"
						+ bundleNo + "'", SSConstants.BUNDLE_SERIAL_NO);
		}
		int _total_scripts = cursor_grand_totals.getCount();
		if (_total_scripts > 0 && cursor_grand_totals != null) {

			subjectCode = cursor_grand_totals.getString(cursor_grand_totals
					.getColumnIndex(SSConstants.SUBJECT_CODE));
			while (!cursor_grand_totals.isAfterLast()) {

				// call method setGrandTotal for setting data
				setGrandTotalData(
						cursor_grand_totals.getString(cursor_grand_totals
								.getColumnIndex(SSConstants.GRAND_TOTAL_MARK)),
						cursor_grand_totals.getString(cursor_grand_totals
								.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)),
						cursor_grand_totals.getString(cursor_grand_totals
								.getColumnIndex(SSConstants.ANS_BOOK_BARCODE)),
						cursor_grand_totals.getInt(cursor_grand_totals
								.getColumnIndex(SSConstants.SCRUTINIZE_STATUS)),
						cursor_grand_totals.getInt(cursor_grand_totals
								.getColumnIndex(SSConstants.IS_CORRECTED)),
						cursor_grand_totals.getInt(cursor_grand_totals
								.getColumnIndex(SSConstants.IS_SCRUTINIZED)));
				cursor_grand_totals.moveToNext();
			}
		}
		if (cursor_grand_totals != null) {
			cursor_grand_totals.close();
		}
		((TextView) findViewById(R.id.tv_sub_code)).setText(subjectCode);

		if (scrutinySelection) {
			btnAddScript.setVisibility(View.GONE);
			// if (_total_scripts > 39) {
			if (_total_scripts > 49) {
				btnAddScript.setVisibility(View.GONE);
			}
			addDeleteButton();
		} else {
			btnAddScript.setVisibility(View.GONE);
		}
	}

	// enable delete button
	private void addDeleteButton() {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);

		Cursor _cursor = _database.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
				SSConstants.BUNDLE_NO + " = '" + bundleNo + "' AND "
						+ SSConstants.SCRUTINIZE_STATUS + " = '"
						+ SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED + "'",
				null);

		if (_cursor != null && _cursor.getCount() > 0) {
			// delete button enable for observation
			((LinearLayout) findViewById(R.id.ll_del_script))
					.setVisibility(View.VISIBLE);
			((Button) findViewById(R.id.btn_del_script))
					.setOnClickListener(this);

		}
		if (_cursor != null) {
			_cursor.close();
		}
	}

	// change textview background or cell color to green
	private void changeCellBGToGreen(Button tv) {
		// TextView tv = (TextView) view;
		tv.setBackgroundResource(R.drawable.green_with_border);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(Typeface.DEFAULT_BOLD);
		// tv.setClickable(false);
	}

	// change textview background or cell color to red
	private void changeCellBGToRed(Button tv) {
		tv.setBackgroundResource(R.drawable.red_with_border);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(Typeface.DEFAULT_BOLD);
	}

	private void setGrandTotalData2(String grandTotalMarks,
			String bundleSerialNo, int scrutinize_status, int is_completed,
			int is_scrutinized, Button pTexView) {
		pTexView.setVisibility(View.VISIBLE);
		pTexView.setTag(bundleSerialNo);
		pTexView.setOnClickListener(this);//harinath
		// 1 0r 2 means red less than 1 def gre 0 green
		if (scrutinize_status > 0) {
			if ((scrutinize_status == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED
					|| scrutinize_status == SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH || scrutinize_status == SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION)) {
				if (is_completed == 0) {
					pTexView.setClickable(true);
					changeCellBGToRed(pTexView);
				} else if (is_scrutinized == 1) {
					pTexView.setClickable(true);
					changeCellBGToRed(pTexView);
				} else if (is_completed == 1) {
					pTexView.setClickable(false);
				}
			} else if ((scrutinize_status == SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED)) {
				changeCellBGToGreen(pTexView);
			} else if (scrutinize_status == SSConstants.SCRUTINY_STATUS_7_SCRIPT_MISMATCH_WITH_DB) {
				if (scrutinySelection) {
					changeCellBGToOrange(pTexView);
					pTexView.setClickable(false);
				} else {
					if (is_completed == 0) {
						changeCellBGToOrange(pTexView);
						pTexView.setClickable(true);
					} else if (is_completed == 1) {
						pTexView.setClickable(false);
					}
				}
			}

			else {
				if (!scrutinySelection)
					pTexView.setClickable(false);
			}

		}

	}

	// change textview background or cell color to orange
	private void changeCellBGToOrange(Button tv) {
		// by def remarked ones are green
		tv.setBackgroundResource(R.drawable.orange_with_border);
		tv.setTextColor(getResources().getColor(R.color.white));
		tv.setTypeface(Typeface.DEFAULT_BOLD);
	}

	// method for setting grand total using bundle serial number
	private void setGrandTotalData(String grandTotalMarks,
			String bundleSerialNo, String barcode, int scrutinize_status,
			int is_completed, int is_scrutinized) {

		switch (Integer.valueOf(bundleSerialNo)) {
		case 1:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_1));
			break;

		case 2:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_2));
			break;

		case 3:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_3));
			break;

		case 4:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_4));
			break;

		case 5:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_5));
			break;

		case 6:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_6));
			break;

		case 7:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_7));
			break;

		case 8:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_8));
			break;

		case 9:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_9));
			break;

		case 10:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_10));
			break;

		case 11:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_11));
			break;

		case 12:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_12));
			break;

		case 13:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_13));
			break;

		case 14:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_14));
			break;

		case 15:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_15));
			break;

		case 16:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_16));
			break;

		case 17:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_17));
			break;

		case 18:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_18));
			break;

		case 19:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_19));
			break;

		case 20:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_20));
			break;

		case 21:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_21));
			break;

		case 22:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_22));
			break;

		case 23:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_23));
			break;

		case 24:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_24));
			break;

		case 25:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_25));
			break;

		case 26:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_26));
			break;

		case 27:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_27));
			break;

		case 28:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_28));
			break;

		case 29:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_29));
			break;

		case 30:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_30));
			break;

		case 31:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_31));
			break;

		case 32:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_32));
			break;

		case 33:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_33));
			break;

		case 34:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_34));
			break;

		case 35:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_35));
			break;

		case 36:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_36));
			break;

		case 37:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_37));
			break;

		case 38:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_38));
			break;

		case 39:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_39));
			break;

		case 40:
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized,
					(Button) findViewById(R.id.gt_40));
			break;

		case 41:
			ScrollView scrollView = (ScrollView) findViewById(R.id.scroll);
			scrollView.setVerticalScrollBarEnabled(true);
			scrollView.setScrollbarFadingEnabled(false);
			Button gt_41 = (Button) findViewById(R.id.gt_41);
			gt_41.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_41);

			break;

		case 42:
			Button gt_42 = (Button) findViewById(R.id.gt_42);
			gt_42.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_42);
			break;

		case 43:
			Button gt_43 = (Button) findViewById(R.id.gt_43);
			gt_43.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_43);
			break;

		case 44:
			Button gt_44 = (Button) findViewById(R.id.gt_44);
			gt_44.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_44);
			break;

		case 45:
			Button gt_45 = (Button) findViewById(R.id.gt_45);
			gt_45.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_45);
			break;

		case 46:
			Button gt_46 = (Button) findViewById(R.id.gt_46);
			gt_46.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_46);
			break;

		case 47:
			Button gt_47 = (Button) findViewById(R.id.gt_47);
			gt_47.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_47);
			break;

		case 48:
			Button gt_48 = (Button) findViewById(R.id.gt_48);
			gt_48.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_48);
			break;

		case 49:
			Button gt_49 = (Button) findViewById(R.id.gt_49);
			gt_49.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_49);
			break;

		case 50:
			Button gt_50 = (Button) findViewById(R.id.gt_50);
			gt_50.setVisibility(View.VISIBLE);
			setGrandTotalData2(grandTotalMarks, bundleSerialNo,
					scrutinize_status, is_completed, is_scrutinized, gt_50);
			break;
		default:
			break;
		}
	}

	private String getSumofTotal() {
		String getGrandTotal = "0";
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor cursor_bundle = null;
		try {

			String selectQuery = "select sum(grand_total) as grand_total from "
					+ "(select bundle_serial_no,total_mark as grand_total from table_marks_scrutinize where UPPER(bundle_no) = UPPER('"
					+ bundleNo + "') group by bundle_serial_no)";
			cursor_bundle = _database.executeSQLQuery(selectQuery, null);

			if (cursor_bundle != null) {
				while (!cursor_bundle.isAfterLast()) {

					getGrandTotal = cursor_bundle.getString(cursor_bundle
							.getColumnIndex("grand_total"));

					cursor_bundle.moveToNext();
				}
			} else {
				FileLog.logInfo(
						"Cursor Null ---> ShowGrandTotalSummaryTable: getSumofTotal() ",
						0);
			}

		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> ShowGrandTotalSummaryTable: getSumofTotal() "
							+ ex.toString(), 0);
		} finally {
			cursor_bundle.close();

		}

		return getGrandTotal;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_ok) {
			// clicks OK

			if (scrutinySelection) {
				switchToScrutinyOptionSelectionActivity();
			} else {
				switchToScrutinyOptionSelectionActivity();
			}

		} else if (v.getId() == R.id.btn_add_script) {
			// when clicks on add script
			showAlertForAddScript(getString(R.string.alert_add_new_script),
					getString(R.string.alert_yes),
					getString(R.string.alert_dialog_cancel));

		} else if (v.getId() == R.id.btn_del_script) {
			delLastScriptAlert();
		} else {

			String _slNo = (String) v.getTag();
			SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
			Cursor _cursor = _database.passedQuery(
					SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO
							+ " = '" + bundleNo + "' AND "
							+ SSConstants.BUNDLE_SERIAL_NO + " = '" + _slNo
							+ "' AND (" + SSConstants.IS_CORRECTED
							+ " = '1' OR " + SSConstants.IS_SCRUTINIZED
							+ " = '1')", null);
			//
			Intent _intent;  
			if (scrutinySelection) { // harinath
				/*if (_cursor != null && _cursor.getCount() > 0) {
					int _scrutinyStatus = _cursor.getInt(_cursor
							.getColumnIndex(SSConstants.SCRUTINIZE_STATUS));
					if (!(_scrutinyStatus == SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED)) {

						_intent = new Intent(this,
								Scrutiny_SeriallyScanAnswerSheet.class);
						_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
						_intent.putExtra(SSConstants.USER_ID, userId);
						_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
								String.valueOf(_slNo));
						_intent.putExtra(SSConstants.SUBJECT_CODE, subjectCode);
						_intent.putExtra("SeatNo",
								SSConstants.SeatNo);
						_intent.putExtra(
								SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
								SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
						_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(_intent);
						finish();
					}
				}*/
			} else {
				if (_cursor.getCount() == 0) {
					_intent = new Intent(this,
							Scrutiny_ScanAnswerBookActivity.class);
					_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
					_intent.putExtra(SSConstants.USER_ID, userId);
					_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
							String.valueOf(_slNo));
					_intent.putExtra(SSConstants.SUBJECT_CODE, subjectCode);
					_intent.putExtra("SeatNo",
							SSConstants.SeatNo);
					// _intent.putExtra(SSConstants.TOTAL_SCRIPTS,
					// totalScripts);
					_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(_intent);
				}
			}
			if (_cursor != null) {
				_cursor.close();
			}
		}
	}

	private void delLastScriptAlert() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage("Are you Sure of deleting the last Script ? ");
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(
				getResources().getString(R.string.alert_yes),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						lastScriptDeletion();

					}

				});

		myAlertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	private void lastScriptDeletion() {
		int del_last_script_count = checkbundleSerialNo();
		if (checkbundleSerialNo() == 0) {
			showAlertForZeroScripts("There is nothing to delete", "Ok");
		} else {
			showAlertForLastScriptDeletion(
					"WARNING!  You are about to delete the AnswerBook Serial No."
							+ del_last_script_count
							+ ". This cannot be undone. ", "Ok", "Cancel",
					del_last_script_count);
		}
	}

	private void showAlertForLastScriptDeletion(String msg, String positiveStr,
			String negativeStr, final int serialNo) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(positiveStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						SScrutinyDatabase _database = SScrutinyDatabase
								.getInstance(Scrutiny_ShowGrandTotalSummaryTable.this);
						_database.deleteRow(SSConstants.TABLE_SCRUTINY_SAVE,
								SSConstants.BUNDLE_NO + " = '" + bundleNo
										+ "' AND "
										+ SSConstants.BUNDLE_SERIAL_NO + " = '"
										+ serialNo + "'");
						_database.deleteRow(SSConstants.TABLE_EVALUATION_SAVE,
								SSConstants.BUNDLE_NO + " = '" + bundleNo
										+ "' AND "
										+ SSConstants.BUNDLE_SERIAL_NO + " = '"
										+ serialNo + "'");
						Intent intent = new Intent(
								Scrutiny_ShowGrandTotalSummaryTable.this,
								Scrutiny_ShowGrandTotalSummaryTable.class);
						intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
						intent.putExtra(SSConstants.USER_ID, userId);
						intent.putExtra("SeatNo",
								SSConstants.SeatNo);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});

		myAlertDialog.setNegativeButton(negativeStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	private void showAlertForZeroScripts(String msg, String positiveStr) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(positiveStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
					}
				});
		myAlertDialog.show();
	}

	// bundle serial no
	private int checkbundleSerialNo() {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor = _database.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
				SSConstants.BUNDLE_NO + " = '" + bundleNo + "'", null);
		int _count = _cursor.getCount();
		_cursor.close();
		return _count;
	}

	// show alert
	private void showAlertForAddScript(String msg, String positiveStr,
			String negativeStr) {
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
						// add script case
						SScrutinyDatabase _database = SScrutinyDatabase
								.getInstance(Scrutiny_ShowGrandTotalSummaryTable.this);
						Cursor _cursor = _database.executeSQLQuery(
								"Select count(distinct bundle_serial_no) as Value from "
										+ SSConstants.TABLE_SCRUTINY_SAVE
										+ " where bundle_no= '" + bundleNo
										+ "'", null);

						if (_cursor != null && _cursor.getCount() > 0) {
							int _count = _cursor.getInt(_cursor
									.getColumnIndex("Value"));
							Intent _intent = new Intent(
									Scrutiny_ShowGrandTotalSummaryTable.this,
									Scrutiny_SeriallyScanAnswerSheet.class);
							_intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
									SSConstants.ADD_SCRIPT_CASE);
							_intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									String.valueOf(_count + 1));
							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(_intent);
						}
						if (_cursor != null) {
							_cursor.close();
						}
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

	private void switchToScrutinyOptionSelectionActivity() {

		// before navigation set status to 1 for all ans sheet barcode
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor;
		int count;
		if (scrutinySelection) {
			_cursor = _database.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
					SSConstants.IS_SCRUTINIZED + " = 1 AND "
							+ SSConstants.BUNDLE_NO + " = '" + bundleNo.toUpperCase() + "'",
					null);
			count = _cursor.getCount();
			showAlert("You have Observed : " + count + " Scripts", "Ok",
					"Cancel");
		} else {
			int scripts_count;
			_cursor = _database
					.passedQuery(
							SSConstants.TABLE_SCRUTINY_SAVE,
							SSConstants.BUNDLE_NO
									+ "= '"
									+ bundleNo
									+ "' AND ( "
									+ SSConstants.SCRUTINIZE_STATUS
									+ "='"
									+ SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED
									+ "' OR "
									+ SSConstants.SCRUTINIZE_STATUS
									+ "='"
									+ SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH
									+ "' OR "
									+ SSConstants.SCRUTINIZE_STATUS
									+ "='"
									+ SSConstants.SCRUTINY_STATUS_3_CORRECTION_REQUIRED
									+ "' )", null);
			scripts_count = _cursor.getCount();
			if (scripts_count == 0) {
				showAlert(getString(R.string.alert_corr_completed),
						getString(R.string.alert_dialog_ok),
						getString(R.string.alert_dialog_cancel));
			} else {
				alertbox("Scrutiny Correction", "You Have Not Corrected "
						+ scripts_count
						+ " Answer Sheet(s) of this Bundle Yet!");
			}

		}
		_cursor.close();

	}

	// alert dialog display
	private void alertbox(String title, String mymessage) {
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				R.style.alert_text_style))
				.setMessage(mymessage)
				.setTitle(title)
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						}).show();
	}

	private void showAlert(String msg, String positiveStr, String negativeStr) {
		final ContentValues contentValues = new ContentValues();
		final SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
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
						// int ScrutinyStatus = 0;
						showProgress();
						contentValues.put(SSConstants.IS_UPDATED_SERVER, 1);
						if (scrutinySelection) {

							String query1 = "update "+SSConstants.TABLE_SCRUTINY_SAVE+" set scrutinize_status = 4 where scrutinize_status in (0) and bundle_no ='"
									+ bundleNo + "'";
							String query2 = "update "+SSConstants.TABLE_EVALUATION_SAVE+" set scrutinize_status = 4 where scrutinize_status in (0) and bundle_no ='"
									+ bundleNo + "'";
							_database.executeSQLQuery(query1, null).close();
							_database.executeSQLQuery(query2, null).close();
							String query3 = "update "+SSConstants.TABLE_SCRUTINY_SAVE+" set is_updated_server = 1, "
									+ SSConstants.SCRUTINIZED_BY
									+ "= '"
									+ userId
									+ "',"
									+ SSConstants.SCRUTINIZED_ON
									+ "= '"     
									+ getPresentTime()      
									+ "',"
									+ SSConstants.IS_SCRUTINIZED
									+ "=1"
									+ " where bundle_no ='" + bundleNo + "'";
							String query4 = "update "+SSConstants.TABLE_EVALUATION_SAVE+" set is_updated_server = 1, "
									+ SSConstants.SCRUTINIZED_BY
									+ "= '"
									+ userId
									+ "',"
									+ SSConstants.SCRUTINIZED_ON
									+ "= '"     
									+ getPresentTime()      
									+ "',"
									+ SSConstants.IS_SCRUTINIZED  
									+ "=1"
									+ " where bundle_no ='" + bundleNo + "'";
							_database.executeSQLQuery(query3, null).close();
							_database.executeSQLQuery(query4, null).close();
							String query5 ="update table_user set active_status=11 where user_id='"+userId+"'";
							Log.v("query submit", query5);
							_database.executeSQLQuery(query5, null).close();
							
						}
						// if scrutiny correction
						else {
							String strQuery = "select scrutinize_status from table_marks_scrutinize where scrutinize_status in (1,2,3,4) and bundle_no='"
									+ bundleNo + "'";
							Cursor _cursor = _database.executeSQLQuery(
									strQuery, null);
							if (_cursor.getCount() > 0) {
								String query1 = "update table_marks_scrutinize set scrutinize_status = 5 where scrutinize_status in (1,2) and bundle_no ='"
										+ bundleNo + "'";
								String query2 = "update table_marks_scrutinize set scrutinize_status = 6 where scrutinize_status in (3,4) and bundle_no ='"
										+ bundleNo + "'";
								_database.executeSQLQuery(query1, null).close();
								_database.executeSQLQuery(query2, null).close();
								
								String querye1 = "update "+SSConstants.TABLE_EVALUATION_SAVE+" set scrutinize_status = 5 where scrutinize_status in (1,2) and bundle_no ='"
										+ bundleNo + "'";
								String querye2 = "update "+SSConstants.TABLE_EVALUATION_SAVE+" set scrutinize_status = 6 where scrutinize_status in (3,4) and bundle_no ='"
										+ bundleNo + "'";
								_database.executeSQLQuery(querye1, null).close();
								_database.executeSQLQuery(querye2, null).close();
							}
							if (_cursor != null) {
								_cursor.close();
							}
							contentValues.put(SSConstants.IS_CORRECTED, 1);
							contentValues.put(SSConstants.IS_UPDATED_SERVER, 1);
							_database.updateRow(
									SSConstants.TABLE_SCRUTINY_SAVE,
									contentValues, SSConstants.BUNDLE_NO + "='"
											+ bundleNo + "'");
							String _query = "update "
									+ SSConstants.TABLE_SCRUTINY_SAVE + " SET "
									+ SSConstants.CORRECTED_ON + "= '"
									+ getPresentTime() + "' where "
									+ SSConstants.BUNDLE_NO + "='" + bundleNo
									+ "'";
							_database.updateRow(SSConstants.TABLE_EVALUATION_SAVE,
									contentValues, SSConstants.BUNDLE_NO + "='"
											+ bundleNo + "'");
							String _queryE = "update "
									+ SSConstants.TABLE_EVALUATION_SAVE + " SET "
									+ SSConstants.CORRECTED_ON + "= '"
									+ getPresentTime() + "' where "
									+ SSConstants.BUNDLE_NO + "='" + bundleNo
									+ "'";
							_database.executeSQLQuery(_query, null).close();
							_database.executeSQLQuery(_queryE, null).close();
							String queryF ="update table_user set active_status=22 where user_id='"+userId+"'";
							_database.executeSQLQuery(queryF, null).close();
						}
						dialog.dismiss();
						 navigateToStartingScreen();
						// Navigate to Starting screen
//						 Utility instanceUtitlity = new Utility();
//						if (instanceUtitlity.isNetworkAvailable(Scrutiny_ShowGrandTotalSummaryTable.this)) {
//						callIntentService();
//					}else{
//						Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
//								"Please Check your Network Connection", Toast.LENGTH_LONG)
//								.show();
//					}
						// navigateToStartingScreen();
						// Intent _intent = new Intent(
						// ShowGrandTotalSummaryTable.this,
						// ScrutinyOptionSelectionActivity.class);
						// _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						// startActivity(_intent);

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

	private String getPresentTime() {
		// set the format here
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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

	@Override
	protected void onResume() {
		super.onResume();
		batteryLevel();
		if (wl != null) {
			wl.acquire();
		}
	}

	BroadcastReceiver batteryLevelReceiver;

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

	// Navigate to Starting screen
	private void navigateToStartingScreen() {
		hideProgress();
		Intent _intent = new Intent(Scrutiny_ShowGrandTotalSummaryTable.this,
				ShowBundleCompletedMessage.class);
		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(_intent);
		finish();
	}

	private void callIntentService() {
		String mode;
		if (scrutinySelection) {
			mode = SSConstants.SCRUTINY;
			getLoginIdbyAppLoad();
		} else {
			mode = SSConstants.SCRUTINY_CORRECTION;
		
//	AsyncCallWS servercall=new AsyncCallWS();
//	servercall.execute();
		// register broadcast receiver before calling Intent service
//		registerReceiver(receiver, new IntentFilter(SSConstants.NOTIFICATION));
//		// call Intentservice
//		Intent intent = new Intent(Scrutiny_ShowGrandTotalSummaryTable.this,
//				Scrutiny_OneTimeService.class);
//		intent.putExtra(SSConstants.MODE, mode);
//		startService(intent);
		
//		Scrutiny_SoapServiceManager manager = Scrutiny_SoapServiceManager
//				.getInstance(Scrutiny_ShowGrandTotalSummaryTable.this);
//		String ss=manager.checkServerForEvaluationUpdation();
//		Log.v("gahr", ""+ss);
		}
	}
	// show alert for charge of a tablet
		private void alertMessageForChargeDummy(String msg) {
			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.alert_text_style));
			myAlertDialog.setTitle(getString(R.string.app_name));
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

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String _mode = bundle.getString(SSConstants.MODE);
				if (_mode.equals(SSConstants.MODE_SCRUTINY)) {
					Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
							"Scrutiny marks Posted", Toast.LENGTH_LONG).show();
					navigateToStartingScreen();
				}  
				else if (_mode.equals(SSConstants.MODE_ERROR)) {
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Error posting marks...!", Toast.LENGTH_LONG).show();
				//navigateToStartingScreen();
				}
				else if (_mode.equals(SSConstants.MODE_CORRECTION)) {
					Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
							"Scrutiny Correction marks Posted", Toast.LENGTH_LONG).show();
					navigateToStartingScreen();
				}
				
					/*
				}      
					
					SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
					
					Cursor _cursor = _database.passedQuery(
							SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO
									+ " = '" + bundleNo 
									+ "' AND (" 
									+ SSConstants.IS_SCRUTINIZED
									+ " = '1')", null);
				

						if (_cursor != null && _cursor.getCount() > 0) {
							
							int _is_updated_server = _cursor.getInt(_cursor.
									getColumnIndex(SSConstants.IS_UPDATED_SERVER));
							if (_is_updated_server == SSConstants.IS_UPDATED_SERVER_STATUS) {
						
								Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
										"Scrutiny marks Posted", Toast.LENGTH_LONG).show();
								navigateToStartingScreen();
						}
							else{
								Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
										"Please submit again...!", Toast.LENGTH_LONG).show();
							}
						
					}
					
					  
				*/

				else if (_mode.equals(SSConstants.SCRUTINY_CORRECTION)) {
					// setStatusOfTextView(_mode, false);
					//checkScrutinyCorrectionTable();
				}

				else if (_mode.equals(SSConstants.MODE_NETWORK_FAILS)) {
					Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
							"Network fails data not posted. Please post again", Toast.LENGTH_LONG)
							.show();
				}
			}   
			unregisterReceiver(receiver);
			hideProgress();
		}
	};

	private ProgressDialog progressDialog;

	public void showProgress() {
		progressDialog = ProgressDialog.show(this, "",
				"Submitting data. Please wait...");
		progressDialog.setCancelable(false);
	}

	protected void checkScrutinyCorrectionTable() {
		// TODO Auto-generated method stub
		
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
	
	Cursor _cursor = _database.passedQuery(
			SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO
					+ " = '" + bundleNo 
					+ "' AND (" 
					+ SSConstants.IS_CORRECTED   
					+ " = '1')", null);


		if (_cursor != null && _cursor.getCount() > 0) {
			
			int _is_updated_server = _cursor.getInt(_cursor.
					getColumnIndex(SSConstants.IS_UPDATED_SERVER));
			if (_is_updated_server == SSConstants.IS_UPDATED_SERVER_STATUS) {
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Scrutiny Correction marks Posted",
						Toast.LENGTH_LONG).show();
				navigateToStartingScreen();
		}
			else{
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Please submit again...!", Toast.LENGTH_LONG).show();
			}
		  
	}
		
	}

	public void hideProgress() {
		Log.v("ass", "dog");
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (progressDialog != null)
			if (progressDialog.isShowing()) {
				progressDialog.cancel();   
			}

	}
	private void getLoginIdbyAppLoad() {
	//	showProgress();
		// calling webservices for auto update of apk
		Scrutiny_SoapServiceManager manager = Scrutiny_SoapServiceManager
				.getInstance(this);
		manager.sendScrutinyDataToServer(callback);
	}
	
	Scrutiny_NetworkCallback<Object> callback = new Scrutiny_NetworkCallback<Object>() {

		@Override
		public void onSuccess(Object object) {
			String _mode = object.toString();
			hideProgress();
			Log.v("sdfadg", "finish1 "+_mode);
		//	alertMessageForChargeDummy(_mode);

			if (_mode.equals(SSConstants.MODE_SCRUTINY)) {
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Scrutiny marks Posted", Toast.LENGTH_LONG).show();
				navigateToStartingScreen();
			}  
			else if (_mode.equals(SSConstants.MODE_ERROR)) {
			Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
					"Error posting marks...!", Toast.LENGTH_LONG).show();
			}
			else if (_mode.equals(SSConstants.MODE_CORRECTION)) {
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Scrutiny Correction marks Posted", Toast.LENGTH_LONG).show();
				navigateToStartingScreen();
			}
			else if (_mode.equals(SSConstants.MODE_NETWORK_FAILS)) {
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Network fails data not posted. Please post again", Toast.LENGTH_LONG)
						.show();
			}else{
				alertMessageForChargeDummy(_mode);
			}
		
			
		}

		@Override
		public void onFailure(String _mode) {
			hideProgress();
			Log.v("sdfadg", "finish");
			//alertMessageForChargeDummy(_mode);
			if (_mode.equals(SSConstants.MODE_SCRUTINY)) {
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Scrutiny marks Posted", Toast.LENGTH_LONG).show();
				navigateToStartingScreen();
			}  
			else if (_mode.equals(SSConstants.MODE_ERROR)) {
			Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
					"Error posting marks...!", Toast.LENGTH_LONG).show();
			}
			else if (_mode.equals(SSConstants.MODE_CORRECTION)) {
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Scrutiny Correction marks Posted", Toast.LENGTH_LONG).show();
				navigateToStartingScreen();
			}
			else if (_mode.equals(SSConstants.MODE_NETWORK_FAILS)) {
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Network fails data not posted. Please post again", Toast.LENGTH_LONG)
						.show();
			}else{
				alertMessageForChargeDummy(_mode);
			}
			}
	};
	
	
	private class AsyncCallWS extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String s=checkServerForEvaluationUpdation();
			return s;
		}

		@Override
		protected void onPostExecute(String result) {
			hideProgress();
			if(result.equals(SSConstants.MODE_SCRUTINY)){
				Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
						"Scrutiny marks Posted", Toast.LENGTH_LONG).show();
				broadCastResultToActivity(SSConstants.MODE_SCRUTINY);
			}else if (result.equals(SSConstants.MODE_NETWORK_FAILS)) {
					Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
							"Network fails data not posted. Please post again", Toast.LENGTH_LONG)
							.show();
				
			}else if (result.equals(SSConstants.MODE_ERROR)) {
			Toast.makeText(Scrutiny_ShowGrandTotalSummaryTable.this,
					"Error posting marks...! Please post again", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onPreExecute() {
			showProgress();
		}

	}
	public String checkServerForEvaluationUpdation() {

		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(this);
		String sqlStatement = "select mark_scrutinize_id,barcode, bundle_serial_no, bundle_id, question_setid, "
				+ "mark1a, mark1b, mark1c, mark1d, mark1e, "
				+ "mark1f, mark1g, mark1h, mark1i, mark1j, "
				+ "mark2a, mark2b, mark2c, mark2d, mark2e, "
				+ "mark3a, mark3b, mark3c, mark3d, mark3e, "
				+ "mark4a, mark4b, mark4c, mark4d, mark4e, "
				+ "mark5a, mark5b, mark5c, mark5d, mark5e, "
				+ "mark6a, mark6b, mark6c, mark6d, mark6e, "
				+ "mark7a, mark7b, mark7c, mark7d, mark7e, "
				+ "mark8a, mark8b, mark8c, mark8d, mark8e, "
				+ "mark9a, mark9b, mark9c, mark9d, mark9e, "
				+ "mark10a, mark10b, mark10c, mark10d, mark10e, "
				+ "mark11a, mark11b, mark11c, mark11d, mark11e, "
				+ "total_mark, "
				+ "bundle_no, subject_code, question_setcode, user_id, "
				+ "r1_total, r2_total, r3_total, r4_total, r5_total, "
				+ "r6_total, r7_total, r8_total, r9_total, r10_total, r11_total, "
				+ "enter_on, is_updated_server, updated_on, tablet_IMEI, "
				+ "barcode_status, edit_userid, transferred_on, spot_centre_code, "
				+ "remark_1a, remark_1b, remark_1c, remark_1d, remark_1e, "
				+ "remark_1f, remark_1g, remark_1h, remark_1i, remark_1j, "
				+ "remark_2a, remark_2b, remark_2c, remark_2d, remark_2e, "
				+ "remark_3a, remark_3b, remark_3c, remark_3d, remark_3e, "
				+ "remark_4a, remark_4b, remark_4c, remark_4d, remark_4e, "
				+ "remark_5a, remark_5b, remark_5c, remark_5d, remark_5e, "
				+ "remark_6a, remark_6b, remark_6c, remark_6d, remark_6e, "
				+ "remark_7a, remark_7b, remark_7c, remark_7d, remark_7e, "
				+ "remark_8a, remark_8b, remark_8c, remark_8d, remark_8e, "
				+ "remark_9a, remark_9b, remark_9c, remark_9d, remark_9e, "
				+ "remark_10a, remark_10b, remark_10c, remark_10d, remark_10e, "
				+ "remark_11a, remark_11b, remark_11c, remark_11d, remark_11e, "
				+ "remark_r1_total,remark_r2_total,remark_r3_total,remark_r4_total, "
				+ "remark_r5_total,remark_r6_total,remark_r7_total,remark_r8_total, "
				+ "remark_r9_total,remark_r10_total,remark_r11_total,remark_grant_total, "
				+ "scrutinize_status,is_scrutinized,scrutinized_by, scrutinized_on, "
				+ "is_corrected, corrected_on, max_total from  "+SSConstants.TABLE_SCRUTINY_SAVE+" WHERE is_updated_server = 1 AND "
				+ " bundle_no in (select distinct bundle_no from "+SSConstants.TABLE_SCRUTINY_SAVE+" "
				+ " where is_updated_server = 1 AND is_scrutinized = 1 limit 1)";

		try {
			StringBuffer strBuf = new StringBuffer();
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			if (cursor != null) {
				if (cursor.getCount() != 0) {
					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {
						String delimiter;

						cursor.getColumnCount();
						for (int i = 1; i <= cursor.getColumnCount() - 1; i++) {

							try {
								
								if (i == cursor.getColumnCount() - 1) {
									delimiter = "),(";
								}

								else {
									delimiter = ",";
								}
								strBuf.append("'"
										+ (String.valueOf(cursor.getString(cursor
												.getColumnIndex(cursor
														.getColumnName(i)))))
										+ "'" + delimiter);
								Log.v(i+""+cursor.getColumnName(i), i+" "+String.valueOf(cursor.getString(cursor
										.getColumnIndex(cursor.getColumnName(i)))));
								// strBuf.append((String.valueOf(cursor
								// .getString(cursor.getColumnIndex(cursor
								// .getColumnName(i)))))
								// + delimiter);
							} catch (Exception e) {
								e.printStackTrace();
								FileLog.logInfo(
										"Exception with retrieving values from cursor - "
												+ e.toString(), 0);
							}

						}
					}
				}

				Scrutiny_DataBaseUtility.closeCursor(cursor);

				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				String retrieveString = null;
				try {
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString = webServiceForEntryObservation("("
								+ replacechar.substring(0,
										replacechar.length() - 2), checkServerForOservationString());
					} 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLog.logInfo(
							"Exception with service call - " + e.toString(), 0);
				}

				if (retrieveString != null) {
					if (retrieveString.equalsIgnoreCase("error")) {
					//	broadCastResultToActivity(SSConstants.MODE_ERROR);
						showAlertForZeroScripts("SSConstants.MODE_ERROR", "Ok");
						return SSConstants.MODE_ERROR;
					} else {
						try {
							if(retrieveString.contains(";")){
							String tableDatas[] = retrieveString.split(";");
							for(int i=0;i<tableDatas.length;i++){
								try{
							String deleteSql = tableDatas[i];
							Cursor cursorRecordsForDeletion = helper
									.deleteRecords(deleteSql);
							cursorRecordsForDeletion.close();
								}catch(Exception e){
									e.printStackTrace();
									return SSConstants.MODE_ERROR;
								}
							}
							}
							checkServerForOservationUpdation();  
							return SSConstants.MODE_SCRUTINY;
						//	broadCastResultToActivity(SSConstants.MODE_SCRUTINY);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							FileLog.logInfo(
									"OservationUpdation =====> Exception in TableMarksHistory- "
											+ e.toString(), 0);
						}

					}

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLog.logInfo(
					"OservationUpdation =====> SQLException in TableMarksHistory- "
							+ e.toString(), 0);
			return SSConstants.MODE_ERROR;
		}
		return SSConstants.MODE_ERROR;
	}
	public void checkServerForOservationUpdation() {

		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(this);
		String sqlStatement = "select mark_scrutinize_id,barcode, bundle_serial_no, bundle_id, question_setid, "
				+ "mark1a, mark1b, mark1c, mark1d, mark1e, "
				+ "mark1f, mark1g, mark1h, mark1i, mark1j, "
				+ "mark2a, mark2b, mark2c, mark2d, mark2e, "
				+ "mark3a, mark3b, mark3c, mark3d, mark3e, "
				+ "mark4a, mark4b, mark4c, mark4d, mark4e, "
				+ "mark5a, mark5b, mark5c, mark5d, mark5e, "
				+ "mark6a, mark6b, mark6c, mark6d, mark6e, "
				+ "mark7a, mark7b, mark7c, mark7d, mark7e, "
				+ "mark8a, mark8b, mark8c, mark8d, mark8e, "
				+ "mark9a, mark9b, mark9c, mark9d, mark9e, "
				+ "mark10a, mark10b, mark10c, mark10d, mark10e, "
				+ "mark11a, mark11b, mark11c, mark11d, mark11e, "
				+ "total_mark, "
				+ "bundle_no, subject_code, question_setcode, user_id, "
				+ "r1_total, r2_total, r3_total, r4_total, r5_total, "
				+ "r6_total, r7_total, r8_total, r9_total, r10_total, r11_total, "
				+ "enter_on, is_updated_server, updated_on, tablet_IMEI, "
				+ "barcode_status, edit_userid, transferred_on, spot_centre_code, "
				+ "remark_1a, remark_1b, remark_1c, remark_1d, remark_1e, "
				+ "remark_1f, remark_1g, remark_1h, remark_1i, remark_1j, "
				+ "remark_2a, remark_2b, remark_2c, remark_2d, remark_2e, "
				+ "remark_3a, remark_3b, remark_3c, remark_3d, remark_3e, "
				+ "remark_4a, remark_4b, remark_4c, remark_4d, remark_4e, "
				+ "remark_5a, remark_5b, remark_5c, remark_5d, remark_5e, "
				+ "remark_6a, remark_6b, remark_6c, remark_6d, remark_6e, "
				+ "remark_7a, remark_7b, remark_7c, remark_7d, remark_7e, "
				+ "remark_8a, remark_8b, remark_8c, remark_8d, remark_8e, "
				+ "remark_9a, remark_9b, remark_9c, remark_9d, remark_9e, "
				+ "remark_10a, remark_10b, remark_10c, remark_10d, remark_10e, "
				+ "remark_11a, remark_11b, remark_11c, remark_11d, remark_11e, "
				+ "remark_r1_total,remark_r2_total,remark_r3_total,remark_r4_total, "
				+ "remark_r5_total,remark_r6_total,remark_r7_total,remark_r8_total, "
				+ "remark_r9_total,remark_r10_total,remark_r11_total,remark_grant_total, "
				+ "scrutinize_status,is_scrutinized,scrutinized_by, scrutinized_on, "
				+ "is_corrected, corrected_on, max_total from  "+SSConstants.TABLE_EVALUATION_SAVE+" WHERE is_updated_server = 1 AND "
				+ " bundle_no in (select distinct bundle_no from "+SSConstants.TABLE_EVALUATION_SAVE+" "
				+ " where is_updated_server = 1 AND is_scrutinized = 1 limit 1)";

		try {
			StringBuffer strBuf = new StringBuffer();
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			if (cursor != null) {
				if (cursor.getCount() != 0) {
					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {
						String delimiter;

						cursor.getColumnCount();
						for (int i = 1; i <= cursor.getColumnCount() - 1; i++) {

							try {
								cursor.getColumnName(i);
								if (i == cursor.getColumnCount() - 1) {
									delimiter = "),(";
								}

								else {
									delimiter = ",";
								}
								strBuf.append("'"
										+ (String.valueOf(cursor.getString(cursor
												.getColumnIndex(cursor
														.getColumnName(i)))))
										+ "'" + delimiter);
								// strBuf.append((String.valueOf(cursor
								// .getString(cursor.getColumnIndex(cursor
								// .getColumnName(i)))))
								// + delimiter);
							} catch (Exception e) {
								FileLog.logInfo(
										"Exception with retrieving values from cursor - "
												+ e.toString(), 0);
							}

						}
					}
				}

				Scrutiny_DataBaseUtility.closeCursor(cursor);

				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				String retrieveString = null;
				try {
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString = webServiceForObservation("("
								+ replacechar.substring(0,
										replacechar.length() - 2));
					} 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLog.logInfo(
							"Exception with service call - " + e.toString(), 0);
				}

				if (retrieveString != null) {
					if (retrieveString.equalsIgnoreCase("error")) {
					//	broadCastResultToActivity(SSConstants.MODE_ERROR);
					//	showAlertForZeroScripts("SSConstants.MODE_ERROR", "Ok");
						
					} else {
						try {
						//	checkServerForEvaluationUpdation();
							if(retrieveString.contains(";")){
							String tableDatas[] = retrieveString.split(";");
							for(int i=0;i<tableDatas.length;i++){
								try{
							String deleteSql = tableDatas[i];
							Cursor cursorRecordsForDeletion = helper
									.deleteRecords(deleteSql);
							cursorRecordsForDeletion.close();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							}
							//broadCastResultToActivity(SSConstants.MODE_SCRUTINY);
						//	showAlertForZeroScripts("SSConstants.MODE_SCRUTINY", "Ok");
							//checkServerForEvaluationUpdation();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							FileLog.logInfo("OservationUpdation =====> Exception in TableMarksHistory- "+ e.toString(), 0);
							e.printStackTrace();
						}

					}

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLog.logInfo(
					"OservationUpdation =====> SQLException in TableMarksHistory- "
							+ e.toString(), 0);
		}
	}
	
	private String webServiceForEntryObservation(final String pXmlString) {
		String strcheck=null;
		final String METHOD_NAME = "UpdateObservationDetailsFromTablet_NR";
		final String SOAP_ACTION = "ScrutinizingService/" + METHOD_NAME;

		final String NAMESPACE = "ScrutinizingService";
		final Utility instanceUtility = new Utility();
		final String URL = instanceUtility.getIPConfiguration()
				+ "/ScrutinizingService/ScrutinizingService.asmx";

		try {
			final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLScrutinize", pXmlString);
			Log.v("hari", "UpdateObservationDetailsFromTablet_NR "+pXmlString);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);

			final HttpTransportSE ht = new HttpTransportSE(URL);
		//	ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			// ht.debug = true;
			ht.call(SOAP_ACTION, envelope);

			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			String str = response.toString();
			strcheck = str;
			Log.v("strcheck", "strcheck "+strcheck);
		}

		catch (Exception e) {
			e.printStackTrace();
			FileLog.logInfo("Exception with webServiceForObservation call - "
					+ e.toString(), 0);
		}

		return strcheck;
	}
	private String webServiceForObservation(final String pXmlString) {
		String strcheck=null;
		final String METHOD_NAME = "UpdateObservationentryDetailsFromTablet";
		final String SOAP_ACTION = "ScrutinizingService/" + METHOD_NAME;

		final String NAMESPACE = "ScrutinizingService";
		final Utility instanceUtility = new Utility();
		final String URL = instanceUtility.getIPConfiguration()
				+ "/ScrutinizingService/ScrutinizingService.asmx";

		try {
			final	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			final	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLScrutinize", pXmlString);
			Log.v("hari", "UpdateObservationentryDetailsFromTablet "+pXmlString);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);
			
			final HttpTransportSE ht = new HttpTransportSE(URL);
//			ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
//			 ht.debug = true;
			ht.call(SOAP_ACTION, envelope);

			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			String str = response.toString();
			strcheck = str;
		}

		catch (Exception e) {

			e.printStackTrace();
			FileLog.logInfo("Exception with webServiceForObservation call - "
					+ e.toString(), 0);
		}

		return strcheck;
	}
	private void broadCastResultToActivity(String mode) {
		Intent _intent = new Intent(Scrutiny_ShowGrandTotalSummaryTable.this,
				ShowBundleCompletedMessage.class);
		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(_intent);
	}
	
	public String checkServerForOservationString() {
		String retrieveString = null;
		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(this);
		String sqlStatement = "select mark_scrutinize_id,barcode, bundle_serial_no, bundle_id, question_setid, "
				+ "mark1a, mark1b, mark1c, mark1d, mark1e, "
				+ "mark1f, mark1g, mark1h, mark1i, mark1j, "
				+ "mark2a, mark2b, mark2c, mark2d, mark2e, "
				+ "mark3a, mark3b, mark3c, mark3d, mark3e, "
				+ "mark4a, mark4b, mark4c, mark4d, mark4e, "
				+ "mark5a, mark5b, mark5c, mark5d, mark5e, "
				+ "mark6a, mark6b, mark6c, mark6d, mark6e, "
				+ "mark7a, mark7b, mark7c, mark7d, mark7e, "
				+ "mark8a, mark8b, mark8c, mark8d, mark8e, "
				+ "mark9a, mark9b, mark9c, mark9d, mark9e, "
				+ "mark10a, mark10b, mark10c, mark10d, mark10e, "
				+ "mark11a, mark11b, mark11c, mark11d, mark11e, "
				+ "total_mark, "
				+ "bundle_no, subject_code, question_setcode, user_id, "
				+ "r1_total, r2_total, r3_total, r4_total, r5_total, "
				+ "r6_total, r7_total, r8_total, r9_total, r10_total, r11_total, "
				+ "enter_on, is_updated_server, updated_on, tablet_IMEI, "
				+ "barcode_status, edit_userid, transferred_on, spot_centre_code, "
				+ "remark_1a, remark_1b, remark_1c, remark_1d, remark_1e, "
				+ "remark_1f, remark_1g, remark_1h, remark_1i, remark_1j, "
				+ "remark_2a, remark_2b, remark_2c, remark_2d, remark_2e, "
				+ "remark_3a, remark_3b, remark_3c, remark_3d, remark_3e, "
				+ "remark_4a, remark_4b, remark_4c, remark_4d, remark_4e, "
				+ "remark_5a, remark_5b, remark_5c, remark_5d, remark_5e, "
				+ "remark_6a, remark_6b, remark_6c, remark_6d, remark_6e, "
				+ "remark_7a, remark_7b, remark_7c, remark_7d, remark_7e, "
				+ "remark_8a, remark_8b, remark_8c, remark_8d, remark_8e, "
				+ "remark_9a, remark_9b, remark_9c, remark_9d, remark_9e, "
				+ "remark_10a, remark_10b, remark_10c, remark_10d, remark_10e, "
				+ "remark_11a, remark_11b, remark_11c, remark_11d, remark_11e, "
				+ "remark_r1_total,remark_r2_total,remark_r3_total,remark_r4_total, "
				+ "remark_r5_total,remark_r6_total,remark_r7_total,remark_r8_total, "
				+ "remark_r9_total,remark_r10_total,remark_r11_total,remark_grant_total, "
				+ "scrutinize_status,is_scrutinized,scrutinized_by, scrutinized_on, "
				+ "is_corrected, corrected_on, max_total from  "+SSConstants.TABLE_EVALUATION_SAVE+" WHERE is_updated_server = 1 AND "
				+ " bundle_no in (select distinct bundle_no from "+SSConstants.TABLE_EVALUATION_SAVE+" "
				+ " where is_updated_server = 1 AND is_scrutinized = 1 limit 1)";

		
		StringBuffer strBuf = new StringBuffer();
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			if (cursor != null) {
				try {
				if (cursor.getCount() != 0) {
					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {
						String delimiter;

						cursor.getColumnCount();
						for (int i = 1; i <= cursor.getColumnCount() - 1; i++) {

							try {
								cursor.getColumnName(i);
								if (i == cursor.getColumnCount() - 1) {
									delimiter = "),(";
								}

								else {
									delimiter = ",";
								}
								strBuf.append("'"
										+ (String.valueOf(cursor.getString(cursor
												.getColumnIndex(cursor
														.getColumnName(i)))))
										+ "'" + delimiter);
								// strBuf.append((String.valueOf(cursor
								// .getString(cursor.getColumnIndex(cursor
								// .getColumnName(i)))))
								// + delimiter);
							} catch (Exception e) {
								FileLog.logInfo(
										"Exception with retrieving values from cursor - "
												+ e.toString(), 0);
							}

						}
					}
				}

				Scrutiny_DataBaseUtility.closeCursor(cursor);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				FileLog.logInfo(
						"OservationUpdation =====> SQLException in TableMarksHistory- "
								+ e.toString(), 0);
			}
				
				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString="("+ replacechar.substring(0, replacechar.length() - 2);
					//	retrieveString = webServiceForObservation();
					} 

			}

			return retrieveString;
	}
	
	private String webServiceForEntryObservation(final String pXmlString, final String pXmlString2) {
		String strcheck=null;
		final String METHOD_NAME = "UpdateObservationDetailsFromTablet_NR";
		final String SOAP_ACTION = "ScrutinizingService/" + METHOD_NAME;

		final String NAMESPACE = "ScrutinizingService";
		final Utility instanceUtility = new Utility();
		final String URL = instanceUtility.getIPConfiguration()
				+ "/ScrutinizingService/ScrutinizingService.asmx";

		try {
			final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLScrutinize", pXmlString);
			request.addProperty("XMLScrutinizeEntry", pXmlString2);
			Log.v("hari", "UpdateObservationDetailsFromTablet_NR "+pXmlString);
			Log.v("hari", "UpdateObservationDetailsFromTablet_NR2 "+pXmlString2);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);

			final HttpTransportSE ht = new HttpTransportSE(URL);
		//	ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			// ht.debug = true;
			try{
			ht.call(SOAP_ACTION, envelope);
			}catch(Exception e){
				e.printStackTrace();
			}
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			String str = response.toString();
			Log.v("response", "response  "+str);
			strcheck = str;
			try {
				if(ht.getConnection()!=null)
                ht.getConnection().disconnect();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}

		catch (Exception e) {
			e.printStackTrace();
			FileLog.logInfo("Exception with webServiceForObservation call - "
					+ e.toString(), 0);
		}

		return strcheck;
	}
	
}
