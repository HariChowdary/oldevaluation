package com.infoplus.smartevaluation;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;

public class GrandTotalSummaryTable extends Activity implements OnClickListener {

	String userId, bundleNo, subjectCode, seatNo;
	int maxAnswerBook, currentAnsBook, scriptCount;
	private final int TEMP_CONDITION2 = 101, TEMP_CONDITION3 = 102,
			TEMP_CONDITION4 = 103;
	SharedPreferences preferences, getScriptCountPrefs;
	SharedPreferences.Editor script_count_edit;
	Button Submit;
	View menuView;  
	// ProgressDialog progressBar;  
	DBHelper sEvalDatabase;
	Context context;

	private ProgressDialog progressDialog;

	public void showProgress(String msg) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(GrandTotalSummaryTable.this,
					"", msg);
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent_extras = getIntent();  
		userId = intent_extras.getStringExtra("UserId");
		bundleNo = intent_extras.getStringExtra("BundleNo");
		subjectCode = intent_extras.getStringExtra("SubjectCode");
		maxAnswerBook = GrandTotalSummaryTable.this.getIntent().getIntExtra(
				"MaxAnswerBook", SEConstants.MAX_ANSWER_BOOK);
		getScriptCountPrefs = this.getSharedPreferences("ScriptCount",
				MODE_WORLD_READABLE);
		seatNo = intent_extras.getStringExtra("SeatNo");
		//getActionBar().setTitle("SmartEvaluation" + " SEAT NO - " + seatNo);
		maxAnswerBook = this.getIntent().getIntExtra("MaxAnswerBook",
				SEConstants.MAX_ANSWER_BOOK);
		currentAnsBook = getIntent().getIntExtra(
				SEConstants.CURRENT_ANSWER_BOOK, 0);

		entry();

	}

	public void entry() {

		setContentView(R.layout.grand_total_summary_table);
		sEvalDatabase = DBHelper.getInstance(this);
		
		
		//setTitle("SmartEvaluation" + " SEAT NO - " + seatNo);
		 getActionBar().hide();  
		menuView = LayoutInflater.from(this).inflate(
				R.layout.layout_menu_totalscript, null);

		((TextView) menuView.findViewById(R.id.seatno))
		.setVisibility(View.VISIBLE);
		
		((TextView) menuView.findViewById(R.id.tv_seatno))
		.setVisibility(View.VISIBLE);  
		((TextView) menuView.findViewById(R.id.tv_seatno))
		.setText(seatNo);
		
		((TextView) menuView.findViewById(R.id.tv_back))
				.setVisibility(View.INVISIBLE);
		// .(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// finish();
		//
		// }
		// });

		((RelativeLayout) findViewById(R.id.rel_lay_menu_totalscript))
				.addView(menuView);
		((TextView) findViewById(R.id.tv_eval_id)).setText(userId);
		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);

		// ((TextView) findViewById(R.id.tv_sub_code)).setText(subjectCode);
		Submit = (Button) this.findViewById(R.id.btn_ok);
		Submit.setTag("submit");
		Submit.setOnClickListener(this);

		findViewById(R.id.btn_add_script).setOnClickListener(this);
		findViewById(R.id.btn_del_bundle).setOnClickListener(this);
		findViewById(R.id.btn_del_script).setOnClickListener(this);

		// pass bundleNo, userId and subjectCode into sql query string
		String sql_query_str = "select * from table_marks where UPPER(bundle_no)=UPPER('"
				+ bundleNo
				+ "') AND UPPER(user_id)=UPPER('"
				+ userId
				+ "') AND UPPER(subject_code)=UPPER('" + subjectCode + "')";

		Cursor cursor_grand_totals = null;
		try {
			cursor_grand_totals = sEvalDatabase
					.executeSelectSQLQuery(sql_query_str);

			if (cursor_grand_totals.getCount() > 0
					&& cursor_grand_totals != null) {

				while (!cursor_grand_totals.isAfterLast()) {

					// call method setGrandTotal for setting data
					setGrandTotalData(
							cursor_grand_totals.getString(cursor_grand_totals
									.getColumnIndex("total_mark")),
							cursor_grand_totals.getString(cursor_grand_totals
									.getColumnIndex("bundle_serial_no")));
					cursor_grand_totals.moveToNext();
				}
			} else {
				FileLog.logInfo(
						"Cursor Null ---> ShowGrandTotalSummaryTable: entry(): ",
						0);
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> ShowGrandTotalSummaryTable: entry(): "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor_grand_totals);
		}

		try {
			scriptCount = Integer.parseInt(checkbundleSerialNo());

			// if (scriptCount == SEConstants.MAX_ANSWER_BOOK) {
			// ((Button) findViewById(R.id.btn_add_script))
			// .setVisibility(View.GONE);
			// ((Button) findViewById(R.id.btn_del_bundle))
			// .setVisibility(View.GONE);
			// ((Button) findViewById(R.id.btn_del_script))
			// .setVisibility(View.GONE);
			// }
			if (scriptCount < 1) {
				((Button) findViewById(R.id.btn_del_script))
						.setVisibility(View.GONE);
			}

		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> Script count ---> ShowGrandTotalSummaryTable: entry(): "
							+ ex.toString(), 0);
		}

	}

	// marks enter dialog
	private void editscriptscountEnterDialog(final View view2) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(getResources()
				.getString(R.string.count_script));
		View view = LayoutInflater.from(this).inflate(R.layout.total_dialog,
				null);

		final EditText etMarks = (EditText) view
				.findViewById(R.id.editText_remarks);
		// allows only number
		// etMarks.setInputType(2);
		// max characters two
		myAlertDialog.setView(view);

		myAlertDialog.setPositiveButton(
				getResources().getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						String value = etMarks.getText().toString().trim();
						if (value.equalsIgnoreCase("0")
								|| value.equalsIgnoreCase("")
								|| Integer.parseInt(value) > SEConstants.MAX_ANSWER_BOOK) {
							Toast.makeText(GrandTotalSummaryTable.this,
									"Invalid Answer Book Count",
									Toast.LENGTH_LONG).show();
						} else {
							if (value.length() > 0 && value.length() < 3) {
								if (scriptCount == Integer.parseInt(value)) {
									ScriptSubmissionAlert(view2);
								} else if (scriptCount < Integer
										.parseInt(value)) {
									int ss = Integer.parseInt(value)
											- scriptCount;
									Toast.makeText(GrandTotalSummaryTable.this,
											"Add remaining " + ss + " Scripts",
											Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(GrandTotalSummaryTable.this,
											" Check the Number of Scripts",
											Toast.LENGTH_LONG).show();
								}
								dialog.dismiss();
							} else {
								Toast.makeText(GrandTotalSummaryTable.this,
										"Please enter the valid script count",
										Toast.LENGTH_LONG).show();
							}
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

	// method for setting grand total using bundle serial number
	private void setGrandTotalData(String grandTotalMarks, String bundleSerialNo) {

		switch (Integer.valueOf(bundleSerialNo)) {
		case 1:
			Button gt_1 = (Button) findViewById(R.id.gt_1);
			// gt_1.setText(grandTotalMarks);
			gt_1.setVisibility(View.VISIBLE);
		//	gt_1.(this);
			break;

		case 2:
			Button gt_2 = (Button) findViewById(R.id.gt_2);
			// gt_2.setText(grandTotalMarks);
			gt_2.setVisibility(View.VISIBLE);
		//	gt_2.(this);
			break;

		case 3:
			Button gt_3 = (Button) findViewById(R.id.gt_3);
			// gt_3.setText(grandTotalMarks);
			gt_3.setVisibility(View.VISIBLE);
		//	gt_3.(this);
			break;

		case 4:
			Button gt_4 = (Button) findViewById(R.id.gt_4);
			// gt_4.setText(grandTotalMarks);
			gt_4.setVisibility(View.VISIBLE);
		//	gt_4.(this);
			break;

		case 5:
			Button gt_5 = (Button) findViewById(R.id.gt_5);
			// gt_5.setText(grandTotalMarks);
			gt_5.setVisibility(View.VISIBLE);
		//	gt_5.(this);
			break;

		case 6:
			Button gt_6 = (Button) findViewById(R.id.gt_6);
			// gt_6.setText(grandTotalMarks);
			gt_6.setVisibility(View.VISIBLE);
		//	gt_6.(this);
			break;

		case 7:
			Button gt_7 = (Button) findViewById(R.id.gt_7);
			// gt_7.setText(grandTotalMarks);
			gt_7.setVisibility(View.VISIBLE);
		//	gt_7.(this);
			break;

		case 8:
			Button gt_8 = (Button) findViewById(R.id.gt_8);
			// gt_8.setText(grandTotalMarks);
			gt_8.setVisibility(View.VISIBLE);
		//	gt_8.(this);
			break;

		case 9:
			Button gt_9 = (Button) findViewById(R.id.gt_9);
			// gt_9.setText(grandTotalMarks);
			gt_9.setVisibility(View.VISIBLE);
		//	gt_9.(this);
			break;

		case 10:
			Button gt_10 = (Button) findViewById(R.id.gt_10); 
			// 9900913062
			// HARI 8147135581
			// gt_10.setText(grandTotalMarks);
			gt_10.setVisibility(View.VISIBLE);
		//	gt_10.(this);
			break;

		case 11:
			Button gt_11 = (Button) findViewById(R.id.gt_11);
			// gt_11.setText(grandTotalMarks);
			gt_11.setVisibility(View.VISIBLE);
		//	gt_11.(this);
			break;

		case 12:
			Button gt_12 = (Button) findViewById(R.id.gt_12);
			// gt_12.setText(grandTotalMarks);
			gt_12.setVisibility(View.VISIBLE);
		//	gt_12.(this);
			break;

		case 13:
			Button gt_13 = (Button) findViewById(R.id.gt_13);
			// gt_13.setText(grandTotalMarks);
			gt_13.setVisibility(View.VISIBLE);
		//	gt_13.(this);
			break;

		case 14:
			Button gt_14 = (Button) findViewById(R.id.gt_14);
			// gt_14.setText(grandTotalMarks);
			gt_14.setVisibility(View.VISIBLE);
		//	gt_14.(this);
			break;

		case 15:
			Button gt_15 = (Button) findViewById(R.id.gt_15);
			// gt_15.setText(grandTotalMarks);
			gt_15.setVisibility(View.VISIBLE);
		//	gt_15.(this);
			break;

		case 16:
			Button gt_16 = (Button) findViewById(R.id.gt_16);
			// gt_16.setText(grandTotalMarks);
			gt_16.setVisibility(View.VISIBLE);
		//	gt_16.(this);
			break;

		case 17:
			Button gt_17 = (Button) findViewById(R.id.gt_17);
			// gt_17.setText(grandTotalMarks);
			gt_17.setVisibility(View.VISIBLE);
		//	gt_17.(this);
			break;

		case 18:
			Button gt_18 = (Button) findViewById(R.id.gt_18);
			// gt_18.setText(grandTotalMarks);
			gt_18.setVisibility(View.VISIBLE);
		//	gt_18.(this);
			break;

		case 19:
			Button gt_19 = (Button) findViewById(R.id.gt_19);
			// gt_19.setText(grandTotalMarks);
			gt_19.setVisibility(View.VISIBLE);
		//	gt_19.(this);
			break;

		case 20:
			Button gt_20 = (Button) findViewById(R.id.gt_20);
			// gt_20.setText(grandTotalMarks);
			gt_20.setVisibility(View.VISIBLE);
		//	gt_20.(this);
			break;

		case 21:
			Button gt_21 = (Button) findViewById(R.id.gt_21);
			// gt_21.setText(grandTotalMarks);
			gt_21.setVisibility(View.VISIBLE);
		//	gt_21.(this);
			break;

		case 22:
			Button gt_22 = (Button) findViewById(R.id.gt_22);
			// gt_22.setText(grandTotalMarks);
			gt_22.setVisibility(View.VISIBLE);
		//	gt_22.(this);
			break;

		case 23:
			Button gt_23 = (Button) findViewById(R.id.gt_23);
			// gt_23.setText(grandTotalMarks);
			gt_23.setVisibility(View.VISIBLE);
		//	gt_23.(this);
			break;

		case 24:
			Button gt_24 = (Button) findViewById(R.id.gt_24);
			// gt_24.setText(grandTotalMarks);
			gt_24.setVisibility(View.VISIBLE);
		//	gt_24.(this);
			break;

		case 25:
			Button gt_25 = (Button) findViewById(R.id.gt_25);
			// gt_25.setText(grandTotalMarks);
			gt_25.setVisibility(View.VISIBLE);
		//	gt_25.(this);
			break;

		case 26:
			Button gt_26 = (Button) findViewById(R.id.gt_26);
			// gt_26.setText(grandTotalMarks);
			gt_26.setVisibility(View.VISIBLE);
		////	gt_26.(this);
			break;

		case 27:
			Button gt_27 = (Button) findViewById(R.id.gt_27);
			// gt_27.setText(grandTotalMarks);
			gt_27.setVisibility(View.VISIBLE);
		//	gt_27.(this);
			break;

		case 28:
			Button gt_28 = (Button) findViewById(R.id.gt_28);
			// gt_28.setText(grandTotalMarks);
			gt_28.setVisibility(View.VISIBLE);
		//	gt_28.(this);
			break;

		case 29:
			Button gt_29 = (Button) findViewById(R.id.gt_29);
			// gt_29.setText(grandTotalMarks);
			gt_29.setVisibility(View.VISIBLE);
		//	gt_29.(this);
			break;

		case 30:
			Button gt_30 = (Button) findViewById(R.id.gt_30);
			// gt_30.setText(grandTotalMarks);
			gt_30.setVisibility(View.VISIBLE);
		//	gt_30.(this);
			break;

		case 31:
			Button gt_31 = (Button) findViewById(R.id.gt_31);
			// gt_31.setText(grandTotalMarks);
			gt_31.setVisibility(View.VISIBLE);
		//	gt_31.(this);
			break;

		case 32:
			Button gt_32 = (Button) findViewById(R.id.gt_32);
			// gt_32.setText(grandTotalMarks);
			gt_32.setVisibility(View.VISIBLE);
		//	gt_32.(this);
			break;

		case 33:
			Button gt_33 = (Button) findViewById(R.id.gt_33);
			// gt_33.setText(grandTotalMarks);
			gt_33.setVisibility(View.VISIBLE);
		//	gt_33.(this);
			break;

		case 34:
			Button gt_34 = (Button) findViewById(R.id.gt_34);
			// gt_34.setText(grandTotalMarks);
			gt_34.setVisibility(View.VISIBLE);
		//	gt_34.(this);
			break;

		case 35:
			Button gt_35 = (Button) findViewById(R.id.gt_35);
			// gt_35.setText(grandTotalMarks);
			gt_35.setVisibility(View.VISIBLE);
		//	gt_35.(this);
			break;

		case 36:
			Button gt_36 = (Button) findViewById(R.id.gt_36);
			// gt_36.setText(grandTotalMarks);
			gt_36.setVisibility(View.VISIBLE);
		//	gt_36.(this);
			break;

		case 37:
			Button gt_37 = (Button) findViewById(R.id.gt_37);
			// gt_37.setText(grandTotalMarks);
			gt_37.setVisibility(View.VISIBLE);
		//	gt_37.(this);
			break;

		case 38:
			Button gt_38 = (Button) findViewById(R.id.gt_38);
			// gt_38.setText(grandTotalMarks);
			gt_38.setVisibility(View.VISIBLE);
		//	gt_38.(this);
			break;

		case 39:
			Button gt_39 = (Button) findViewById(R.id.gt_39);
			// gt_39.setText(grandTotalMarks);
			gt_39.setVisibility(View.VISIBLE);
		//	gt_39.(this);
			break;

		case 40:
			Button gt_40 = (Button) findViewById(R.id.gt_40);
			// gt_40.setText(grandTotalMarks);
			gt_40.setVisibility(View.VISIBLE);
		//	gt_40.(this);
			break;

		case 41:
			Button gt_41 = (Button) findViewById(R.id.gt_41);
			// gt_41.setText(grandTotalMarks);
			gt_41.setVisibility(View.VISIBLE);
		//	gt_41.(this);
			break;

		case 42:
			Button gt_42 = (Button) findViewById(R.id.gt_42);
			// gt_42.setText(grandTotalMarks);
			gt_42.setVisibility(View.VISIBLE);
		//	gt_42.(this);
			break;

		case 43:
			Button gt_43 = (Button) findViewById(R.id.gt_43);
			// gt_43.setText(grandTotalMarks);
			gt_43.setVisibility(View.VISIBLE);
		//	gt_43.(this);
			break;

		case 44:
			Button gt_44 = (Button) findViewById(R.id.gt_44);
			// gt_44.setText(grandTotalMarks);
			gt_44.setVisibility(View.VISIBLE);
		//	gt_44.(this);
			break;

		case 45:
			Button gt_45 = (Button) findViewById(R.id.gt_45);
			// gt_45.setText(grandTotalMarks);
			gt_45.setVisibility(View.VISIBLE);
		//	gt_45.(this);
			break;

		case 46:
			Button gt_46 = (Button) findViewById(R.id.gt_46);
			// gt_46.setText(grandTotalMarks);
			gt_46.setVisibility(View.VISIBLE);
		//	gt_46.(this);
			break;

		case 47:
			Button gt_47 = (Button) findViewById(R.id.gt_47);
			// gt_47.setText(grandTotalMarks);
			gt_47.setVisibility(View.VISIBLE);
		//	gt_47.(this);
			break;

		case 48:
			Button gt_48 = (Button) findViewById(R.id.gt_48);
			// gt_48.setText(grandTotalMarks);
			gt_48.setVisibility(View.VISIBLE);
		//	gt_48.(this);
			break;

		case 49:
			Button gt_49 = (Button) findViewById(R.id.gt_49);
			// gt_49.setText(grandTotalMarks);
			gt_49.setVisibility(View.VISIBLE);
		//	gt_49.(this);
			break;

		case 50:
			Button gt_50 = (Button) findViewById(R.id.gt_50);
			// gt_50.setText(grandTotalMarks);
			gt_50.setVisibility(View.VISIBLE);
		//	gt_50.(this);
			break;

		default:
			break;

		}
	}

	public void runInBackground(final View v) {
		new AsyncTask<String, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgress("Submitting the bundle...");
			}

			@Override
			protected Boolean doInBackground(String... params) {

				update_IMEI_EnterOnIfNotExits();

				return switchToEvaluatorEntryActivity();
				// if (isUpdated) {
				// Utility instanceUtility = new Utility();
				// return instanceUtility
				// .pingTest(GrandTotalSummaryTable.this);
				// } else {
				// return isUpdated;
				// }

			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				hideProgress();
				navigateToShowBundleCompletedMessageScreen();
				// if (result) {
				//
				// callIntentService(SEConstants.EVALUATION);
				// } else {
				// showAlert(
				// "Network Not Reachable. Please submit the bundle again",
				// "OK", "");
				// hideProgress();
				// }

			}

		}.execute();
	}

	// show alert
	private void showAlert(String msg, String positiveStr, String negativeStr) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
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

	private void update_IMEI_EnterOnIfNotExits() {
		ContentValues _contentValues = new ContentValues();
		String IMEI = getIMEINo();
		Log.v("IMEI", IMEI);
		_contentValues.put(SEConstants.TABLET_IMEI, IMEI);
		_contentValues.put(SEConstants.ENTER_ON, Utility.getPresentTime());
		sEvalDatabase.updateRow(SEConstants.TABLE_MARKS, _contentValues,
				SEConstants.BUNDLE_NO + "='" + bundleNo + "'");
		sEvalDatabase.updateRow(SEConstants.TABLE_MARKS_HISTORY,
				_contentValues, SEConstants.BUNDLE_NO + "='" + bundleNo + "'");

		 /*ContentValues _values = new ContentValues();
		 _values.put("time_interval", 7);
		 try {
		 sEvalDatabase.updateRow(SEConstants.TABLE_DATE_CONFIGURATION,
		 _values, "id=1");
		 } catch (Exception ex) {
		 FileLog.logInfo(
		 "BundleNumberActivity-->RunInBackground()"  
		 + ex.toString(), 0);
		 }*/

	}
  
	String getIMEINo() {
		String imei = "";
		String a[] = { SEConstants.TABLET_IMEI };
		Cursor _cursor = sEvalDatabase.getRow(SEConstants.TABLE_BUNDLE,
				SEConstants.BUNDLE_NO + "='" + bundleNo + "'", a);
		if (_cursor != null) {
			if (_cursor.getCount() > 0) {
				imei = _cursor.getString(_cursor
						.getColumnIndex(SEConstants.TABLET_IMEI));
			}
		}
		DataBaseUtility.closeCursor(_cursor);
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String IMEIa = telephonyManager.getDeviceId();
		if (TextUtils.isEmpty(imei)) {
			imei = IMEIa;
		}
		return imei;
	}

	private void callIntentService(String mode) {

		// register broadcast receiver before calling Intent service
		registerReceiver(receiver, new IntentFilter(
				SEConstants.EVALUATION_NOTIFICATION));
		// call Intentservice
		Intent intent = new Intent(GrandTotalSummaryTable.this,
				Settings_IntentService.class);
		intent.putExtra(SEConstants.MODE, mode);
		startService(intent);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String _mode = bundle.getString(SEConstants.MODE);
				if (_mode.equals(SEConstants.EVALUATION)) {

					Toast.makeText(GrandTotalSummaryTable.this,
							"Evaluation Marks posted", Toast.LENGTH_LONG)
							.show();
					hideProgress();
					// setStatusOfTextView(SEConstants.EVALUATION, false);
					// Navigate to Starting screen
					navigateToShowBundleCompletedMessageScreen();
				}
			}
			hideProgress();
			unregisterReceiver(receiver);
		}

	};

	/*
	 * 
	 * String is_updated_server_strMarks =
	 * "select is_updated_server from table_marks where UPPER(bundle_no)=UPPER('"
	 * + bundleNo + "') '" + "')";
	 * 
	 * String is_updated_server_strMarks =
	 * "select is_updated_server from table_marks where UPPER(bundle_no)=UPPER('"
	 * + bundleNo + "') '" + "')"; String is_updated_server_strBundle =
	 * "select is_updated_server from table_bundle where UPPER(bundle_no)=UPPER('"
	 * + bundleNo + "') ";
	 * 
	 * if(is_updated_server_strMarks.equalsIgnoreCase("1") &&
	 * is_updated_server_strBundle.equalsIgnoreCase("1")) {
	 * Toast.makeText(ShowGrandTotalSummaryTable.this,
	 * "Evaluation Marks posted", Toast.LENGTH_LONG) .show(); hideProgress(); //
	 * setStatusOfTextView(SEConstants.EVALUATION, false); // Navigate to
	 * Starting screen navigateToShowBundleCompletedMessageScreen(); } else{
	 * Toast.makeText(ShowGrandTotalSummaryTable.this,
	 * "Unable to Submit...Please Submit Again ", Toast.LENGTH_LONG).show(); }
	 */

	/*
	 * Bundle bundle = intent.getExtras(); if (bundle != null) { String _mode =
	 * bundle.getString(SEConstants.MODE); if
	 * (_mode.equals(SEConstants.EVALUATION)) {
	 * Toast.makeText(ShowGrandTotalSummaryTable.this,
	 * "Evaluation Marks posted", Toast.LENGTH_LONG) .show(); hideProgress(); //
	 * setStatusOfTextView(SEConstants.EVALUATION, false); // Navigate to
	 * Starting screen navigateToShowBundleCompletedMessageScreen(); } }
	 * hideProgress(); unregisterReceiver(receiver);
	 */

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		if (receiver != null) {
			try {
				unregisterReceiver(receiver);
				receiver = null;
			} catch (IllegalArgumentException ILAE) {

			}
		}
	}

	// Navigate to bundle completion screen
	private void navigateToShowBundleCompletedMessageScreen() {
		Intent intent_eval_entry = new Intent(this,
				Eval_ShowBundleCompletedMessage.class);
		intent_eval_entry.putExtra(SEConstants.MODE, SEConstants.EVALUATION);
		intent_eval_entry.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent_eval_entry);
	}

	private void ScriptSubmissionAlert(final View v) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog
				.setMessage("You have Completed the Bundle, and No More Edits are Possible if Submitted");
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						// do something when the OK button is
						// clicked
						runInBackground(v);
						dialog.dismiss();
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

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_add_script:
			// switch to ScanActivity when add script clicked
			try {
				int count = Integer.parseInt(checkbundleSerialNo());
				if (count >= maxAnswerBook) {
					ScriptModificationAlert(count);
				}

				else {

					showAlert("Do You Want to Add the Answer Script "
							+ (count + 1 + "? "), "Ok", "Cancel",
							TEMP_CONDITION4, v);

				}
			} catch (Exception ex) {
				FileLog.logInfo(
						"Exception --->  Add Script ---> ShowGrandTotalSummaryTable: onclick(): "
								+ ex.toString(), 0);
			}

			break;

		case R.id.btn_del_bundle:

			showAlertForBundleDeletion(
					"Are You Sure Of Deleting the Bundle? The Values Entered for it will be Lost if Deleted.",
					"Yes", "No");
			break;

		case R.id.btn_del_script:
			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
			myAlertDialog.setTitle("Smart Evaluation");
			myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
			myAlertDialog
					.setMessage("Are You Sure of Deleting the Last Script? ");
			myAlertDialog.setCancelable(false);

			myAlertDialog.setPositiveButton("Yes",
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

			break;

		case R.id.btn_ok:
			String tag = (String) v.getTag();
			if (tag.equalsIgnoreCase("submit")) {
				if (scriptCount < maxAnswerBook) {
					showAlert(
							"You have Evaluated Only "
									+ scriptCount
									+ " of "
									+ maxAnswerBook
									+ " Scripts for this Bundle."
									+ "\nDo You Want to Modify the Bundle Script Count? ",
							"Yes", "No", TEMP_CONDITION4, v);
				}

				else if (scriptCount == maxAnswerBook) {
					editscriptscountEnterDialog(v);
					// ScriptSubmissionAlert(v);

				} else {
					showAlert(
							"You have Evaluated "
									+ scriptCount
									+ " Scripts. But Enter total scripts count is "
									+ maxAnswerBook
									+ "."
									+ "\nDo You Want to Modify the Bundle Script Count? ",
							"Yes", "No", TEMP_CONDITION4, v);
				}
			}
			break;

		case R.id.gt_1:
			switchToEditTabMarkEntryActivty("1");
			break;

		case R.id.gt_2:
			switchToEditTabMarkEntryActivty("2");
			break;

		case R.id.gt_3:
			switchToEditTabMarkEntryActivty("3");
			break;

		case R.id.gt_4:
			switchToEditTabMarkEntryActivty("4");
			break;

		case R.id.gt_5:
			switchToEditTabMarkEntryActivty("5");
			break;

		case R.id.gt_6:
			switchToEditTabMarkEntryActivty("6");
			break;

		case R.id.gt_7:
			switchToEditTabMarkEntryActivty("7");
			break;

		case R.id.gt_8:
			switchToEditTabMarkEntryActivty("8");
			break;

		case R.id.gt_9:
			switchToEditTabMarkEntryActivty("9");
			break;
		case R.id.gt_10:
			switchToEditTabMarkEntryActivty("10");
			break;

		case R.id.gt_11:
			switchToEditTabMarkEntryActivty("11");
			break;

		case R.id.gt_12:
			switchToEditTabMarkEntryActivty("12");
			break;

		case R.id.gt_13:
			switchToEditTabMarkEntryActivty("13");
			break;

		case R.id.gt_14:
			switchToEditTabMarkEntryActivty("14");
			break;

		case R.id.gt_15:
			switchToEditTabMarkEntryActivty("15");
			break;
		case R.id.gt_16:
			switchToEditTabMarkEntryActivty("16");
			break;

		case R.id.gt_17:
			switchToEditTabMarkEntryActivty("17");
			break;

		case R.id.gt_18:
			switchToEditTabMarkEntryActivty("18");
			break;

		case R.id.gt_19:
			switchToEditTabMarkEntryActivty("19");
			break;
		case R.id.gt_20:
			switchToEditTabMarkEntryActivty("20");
			break;

		case R.id.gt_21:
			switchToEditTabMarkEntryActivty("21");
			break;

		case R.id.gt_22:
			switchToEditTabMarkEntryActivty("22");
			break;

		case R.id.gt_23:
			switchToEditTabMarkEntryActivty("23");
			break;

		case R.id.gt_24:
			switchToEditTabMarkEntryActivty("24");
			break;

		case R.id.gt_25:
			switchToEditTabMarkEntryActivty("25");
			break;
		case R.id.gt_26:
			switchToEditTabMarkEntryActivty("26");
			break;

		case R.id.gt_27:
			switchToEditTabMarkEntryActivty("27");
			break;

		case R.id.gt_28:
			switchToEditTabMarkEntryActivty("28");
			break;

		case R.id.gt_29:
			switchToEditTabMarkEntryActivty("29");
			break;
		case R.id.gt_30:
			switchToEditTabMarkEntryActivty("30");
			break;

		case R.id.gt_31:
			switchToEditTabMarkEntryActivty("31");
			break;

		case R.id.gt_32:
			switchToEditTabMarkEntryActivty("32");
			break;

		case R.id.gt_33:
			switchToEditTabMarkEntryActivty("33");
			break;

		case R.id.gt_34:
			switchToEditTabMarkEntryActivty("34");
			break;

		case R.id.gt_35:
			switchToEditTabMarkEntryActivty("35");
			break;
		case R.id.gt_36:
			switchToEditTabMarkEntryActivty("36");
			break;

		case R.id.gt_37:
			switchToEditTabMarkEntryActivty("37");
			break;

		case R.id.gt_38:
			switchToEditTabMarkEntryActivty("38");
			break;

		case R.id.gt_39:
			switchToEditTabMarkEntryActivty("39");
			break;
		case R.id.gt_40:
			switchToEditTabMarkEntryActivty("40");
			break;
		case R.id.gt_41:
			switchToEditTabMarkEntryActivty("41");
			break;

		case R.id.gt_42:
			switchToEditTabMarkEntryActivty("42");
			break;

		case R.id.gt_43:
			switchToEditTabMarkEntryActivty("43");
			break;

		case R.id.gt_44:
			switchToEditTabMarkEntryActivty("44");
			break;

		case R.id.gt_45:
			switchToEditTabMarkEntryActivty("45");
			break;
		case R.id.gt_46:
			switchToEditTabMarkEntryActivty("46");
			break;

		case R.id.gt_47:
			switchToEditTabMarkEntryActivty("47");
			break;

		case R.id.gt_48:
			switchToEditTabMarkEntryActivty("48");
			break;

		case R.id.gt_49:
			switchToEditTabMarkEntryActivty("49");
			break;
		case R.id.gt_50:
			switchToEditTabMarkEntryActivty("50");
			break;
		default:
			break;
		}
	}

	private String getSumofTotal() {
		String getGrandTotal = "0";

		Cursor cursor_bundle = null;
		try {

			String selectQuery = "select sum(grand_total) as grand_total from "
					+ "(select bundle_serial_no,total_mark as grand_total from table_marks where UPPER(bundle_no) = UPPER('"
					+ bundleNo + "') group by bundle_serial_no)";
			cursor_bundle = sEvalDatabase.executeSelectSQLQuery(selectQuery);

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
			DataBaseUtility.closeCursor(cursor_bundle);

		}

		return getGrandTotal;
	}

	private void switchToEditTabMarkEntryActivty(final String bundle_serial_no) {

		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setMessage(" Do You Want to Edit Answer Sheet "
				+ bundle_serial_no + " ? ");
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						dialog.dismiss();
						String str_query = "select * from table_marks where bundle_serial_no="
								+ bundle_serial_no
								+ " and bundle_no='"
								+ bundleNo
								+ "' and user_id='"
								+ userId
								+ "'and subject_code='" + subjectCode + "'";
						Cursor cursor_row = null;
						try {
							cursor_row = sEvalDatabase
									.executeSelectSQLQuery(str_query);

							if (cursor_row.getCount() == 1
									&& cursor_row != null) {

								int barCodeStatus = cursor_row.getInt(cursor_row
										.getColumnIndex("barcode_status"));

								String barCode = cursor_row
										.getString(cursor_row
												.getColumnIndex("barcode"));
								Utility instanceUtility = new Utility();
								if (instanceUtility
										.isRegulation_R13_Mtech(context)||
										instanceUtility.isRegulation_R15_Mtech(context)) {

									Intent intent = new Intent(
											GrandTotalSummaryTable.this,
											MarkEntryScreen_R13.class);
									intent.putExtra(SEConstants.BUNDLE_TIMER,
											false);
									intent.putExtra("MaxAnswerBook",
											maxAnswerBook);
									intent.putExtra(SEConstants.USER_ID, userId);
									intent.putExtra(SEConstants.BUNDLE_NO,
											bundleNo);
									intent.putExtra("SubjectId", "");
									intent.putExtra(SEConstants.SUBJECT_CODE,
											subjectCode);
									intent.putExtra("SeatNo",
											SEConstants.seatNo);
									intent.putExtra(
											SEConstants.BUNDLE_SERIAL_NO,
											Integer.valueOf(bundle_serial_no));
									intent.putExtra(SEConstants.BARCODE_STATUS,
											barCodeStatus);

									intent.putExtra(
											SEConstants.ANS_BOOK_BARCODE,
											barCode);

									intent.putExtra(
											SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE,
											SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

									startActivity(intent);
								}

								else if (instanceUtility
										.isRegulation_R13_Btech(context)) {

									if (subjectCode.equalsIgnoreCase("111AG")
											|| subjectCode
													.equalsIgnoreCase("111AH")
											|| subjectCode
													.equalsIgnoreCase("111AJ")
											|| subjectCode
													.equalsIgnoreCase("111AK")) {

										Intent intent = new Intent(
												GrandTotalSummaryTable.this,
												MarkEntryScreen_R13_BTech_SpecialCase.class);
										intent.putExtra(
												SEConstants.BUNDLE_TIMER, false);
										intent.putExtra("MaxAnswerBook",
												maxAnswerBook);
										intent.putExtra(SEConstants.USER_ID,
												userId);
										intent.putExtra(SEConstants.BUNDLE_NO,
												bundleNo);
										intent.putExtra("SubjectId", "");
										intent.putExtra("SeatNo",
												SEConstants.seatNo);
										intent.putExtra(
												SEConstants.SUBJECT_CODE,
												subjectCode);
										intent.putExtra(
												SEConstants.BUNDLE_SERIAL_NO,
												Integer.valueOf(bundle_serial_no));
										intent.putExtra(
												SEConstants.BARCODE_STATUS,
												barCodeStatus);

										intent.putExtra(
												SEConstants.ANS_BOOK_BARCODE,
												barCode);

										intent.putExtra(
												SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE,
												SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

										startActivity(intent);

									}
									else if (subjectCode.equalsIgnoreCase(SEConstants.SUBJ_114DA_ENGG_DRAWING)
											|| subjectCode.equalsIgnoreCase(SEConstants.SUBJ_114DB_ENGG_DRAWING)) {

								Intent intent = new Intent(
										GrandTotalSummaryTable.this,
										MarkEntryScreen_R13_BTech_SpecialCase_New.class);
								intent.putExtra(
										SEConstants.BUNDLE_TIMER, false);
								intent.putExtra("MaxAnswerBook",
										maxAnswerBook);
								intent.putExtra(SEConstants.USER_ID,
										userId);
								intent.putExtra(SEConstants.BUNDLE_NO,
										bundleNo);
								intent.putExtra("SubjectId", "");
								intent.putExtra("SeatNo",
										SEConstants.seatNo);
								intent.putExtra(
										SEConstants.SUBJECT_CODE,
										subjectCode);
								intent.putExtra(
										SEConstants.BUNDLE_SERIAL_NO,
										Integer.valueOf(bundle_serial_no));
								intent.putExtra(
										SEConstants.BARCODE_STATUS,
										barCodeStatus);

								intent.putExtra(
										SEConstants.ANS_BOOK_BARCODE,
										barCode);

								intent.putExtra(
										SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE,
										SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

								startActivity(intent);

							}
									else {

										Intent intent = new Intent(
												GrandTotalSummaryTable.this,
												MarkEntryScreen_R13.class);

										intent.putExtra("MaxAnswerBook",
												maxAnswerBook);
										intent.putExtra(SEConstants.USER_ID,
												userId);
										intent.putExtra(SEConstants.BUNDLE_NO,
												bundleNo);
										intent.putExtra("SubjectId", "");
										intent.putExtra(
												SEConstants.SUBJECT_CODE,
												subjectCode);
										intent.putExtra("SeatNo",
												SEConstants.seatNo);
										intent.putExtra(
												SEConstants.BUNDLE_SERIAL_NO,
												Integer.valueOf(bundle_serial_no));
										intent.putExtra(
												SEConstants.BARCODE_STATUS,
												barCodeStatus);
										intent.putExtra(
												SEConstants.BUNDLE_TIMER, false);
										intent.putExtra(
												SEConstants.ANS_BOOK_BARCODE,
												barCode);

										intent.putExtra(
												SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE,
												SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

										startActivity(intent);
									}
								} else if (instanceUtility
										.isRegulation_R09_Course(context)
										|| instanceUtility
												.isRegulation_R09_MTech_Course(context)) {

									Intent intent = new Intent(
											GrandTotalSummaryTable.this,
											MarkEntryScreen_R09.class);
									intent.putExtra(SEConstants.BUNDLE_TIMER,
											false);
									intent.putExtra("MaxAnswerBook",
											maxAnswerBook);
									intent.putExtra(SEConstants.USER_ID, userId);
									intent.putExtra(SEConstants.BUNDLE_NO,
											bundleNo);
									intent.putExtra("SubjectId", "");
									intent.putExtra("SeatNo",
											SEConstants.seatNo);
									intent.putExtra(SEConstants.SUBJECT_CODE,
											subjectCode);
									intent.putExtra(
											SEConstants.BUNDLE_SERIAL_NO,
											Integer.valueOf(bundle_serial_no));
									intent.putExtra(SEConstants.BARCODE_STATUS,
											barCodeStatus);

									intent.putExtra(
											SEConstants.ANS_BOOK_BARCODE,
											barCode);

									intent.putExtra(
											SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE,
											SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

									startActivity(intent);
								}

								else {
									showAlertForZeroScripts(
											"Regulation with Degree id is not matched",
											"Ok");
								}

							} else {
								FileLog.logInfo(
										"Cursor Null ---> ShowGrandTotalSummaryTable: switchToEditTabMarkEntryActivty(): ",
										0);
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception --->  ShowGrandTotalSummaryTable: switchToEditTabMarkEntryActivty(): "
											+ ex.toString(), 0);
						} finally {
							DataBaseUtility.closeCursor(cursor_row);
						}

					}
				});

		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myAlertDialog.show();

	}

	private boolean switchToEvaluatorEntryActivity() {

		boolean isUpdated = false;
		try {
			try {
				updateBundleStatusByBundleNo(bundleNo, 1);
			} catch (Exception ex) {
				FileLog.logInfo(
						"Exception ---> updating row table_bundle ---> "
								+ "ShowGrandTotalSummaryTable: switchToEvaluatorEntryActivity()"
								+ ex.toString(), 0);
			}

			ContentValues _contentValues = new ContentValues();
			_contentValues.put("logged_in_status", "0");
			try {
				int getCount = sEvalDatabase.updateRow("table_user",
						_contentValues, "user_id='" + userId + "'");
				if (getCount > 0) {

					isUpdated = true;

				} else {
					FileLog.logInfo(
							"updating row table_user ---> ShowGrandTotalSummaryTable: switchToEvaluatorEntryActivity(): "
									+ String.valueOf(getCount), 0);
					isUpdated = false;
				}

			} catch (Exception ex) {
				FileLog.logInfo(
						"Exception ---> updating row table_user --->"
								+ "ShowGrandTotalSummaryTable: switchToEvaluatorEntryActivity(): "
								+ ex.toString(), 0);

			} finally {
				_contentValues.clear();
			}

		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> "
							+ "ShowGrandTotalSummaryTable: switchToEvaluatorEntryActivity(): "
							+ ex.toString(), 0);
		}
		return isUpdated;

	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}

	private void showAlert(String msg, String positiveStr,
			String NegativeString, final int condition, final View v) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
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

						runInBackground(condition, v);

					}
				});

		myAlertDialog.setNegativeButton(NegativeString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	private void runInBackground(final int condition, final View v) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// showProgress(v);
				showProgress("Please wait....");
			}

			@Override
			protected Void doInBackground(Void... params) {

				if (condition == TEMP_CONDITION3) {
					// navigateToNextSummary();

				} else if (condition == TEMP_CONDITION4) {
					switchToScriptCountScreen();
				} else {
					switchToScanActivity();
				}
				// progressBar.setProgress(100);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hideProgress();
			}

		}.execute();
	}

	public void updateBundleStatusByBundleNo(String pBundleNo, int status) {

		String updateSql = "UPDATE table_bundle SET status="
				+ status
				+ ",updated_on=(datetime('now','localtime')),is_updated_server=0 WHERE bundle_no='"
				+ pBundleNo + "'";
		Cursor cursorRecordsForUpdation = null;
		try {
			cursorRecordsForUpdation = sEvalDatabase.executeSQLQuery(updateSql);
			if (cursorRecordsForUpdation == null) {
				FileLog.logInfo(
						"Cursor Null ---> updating TableBundle failed ---> "
								+ " ShowGrandTotalSummaryTable: updateBundleStatusByBundleNo()",
						0);
			}
			try {
				String query5 = "update table_user set active_status=10 where user_id='"
						+ userId + "'";
				DataBaseUtility.closeCursor(sEvalDatabase
						.executeSQLQuery(query5));
			} catch (Exception ex) {
				FileLog.logInfo(
						"Exception ---> inserting TableBundle_History failed ---> "
								+ " ShowGrandTotalSummaryTable: updateBundleStatusByBundleNo() "
								+ ex.toString(), 0);
			}
			if (status == 1 || status == 2) {
				String insertSqlForBundleHistory = "insert into table_bundle_history"
						+ " (is_deleted,is_unreadable,program_name,bundle_no,subject_id,subject_code,status,enter_by,"
						+ "enter_on,is_updated_server,tablet_IMEI,apk_version) select 0,is_unreadable,program_name,"
						+ "bundle_no,subject_id,subject_code,status,enter_by,enter_on,is_updated_server,"
						+ "tablet_IMEI,apk_version from table_bundle WHERE bundle_no='"
						+ pBundleNo + "'";

				Cursor cursorRecordsForHistoryUpdation = null;
				try {
					cursorRecordsForHistoryUpdation = sEvalDatabase
							.executeSQLQuery(insertSqlForBundleHistory);
					if (cursorRecordsForHistoryUpdation == null) {
						FileLog.logInfo(
								"Cursor Null ---> inserting TableBundle_History failed ---> "
										+ " ShowGrandTotalSummaryTable: updateBundleStatusByBundleNo()",
								0);
					}
				} catch (Exception ex) {
					FileLog.logInfo(
							"Exception ---> inserting TableBundle_History failed ---> "
									+ " ShowGrandTotalSummaryTable: updateBundleStatusByBundleNo() "
									+ ex.toString(), 0);
				} finally {
					DataBaseUtility
							.closeCursor(cursorRecordsForHistoryUpdation);
				}
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> updating TableBundle failed ---> "
							+ " ShowGrandTotalSummaryTable: updateBundleStatusByBundleNo() "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursorRecordsForUpdation);
		}

	}

	public void switchToScanActivity() {
		// progressBar.setProgress(50);
		Intent intent = new Intent(this, ScanActivity.class);

		Bundle b = getIntent().getExtras();
		String UserId = b.getString("UserId");
		String SubjectId = b.getString("SubjectId");
		String SubjectCode = b.getString("SubjectCode"); // SubjectCode
		String BundleNo = b.getString("BundleNo");
		String seatNo = b.getString("SeatNo");
		intent.putExtra("MaxAnswerBook", maxAnswerBook);

		if (getIntent().hasExtra(SEConstants.CURRENT_ANSWER_BOOK)) {
			intent.putExtra("CurrentAnswerBook",
					(Integer.parseInt(checkbundleSerialNo()) + 1));
		}

		intent.putExtra(SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE,
				SEConstants.FROM_CLASS_SHOW_GRAND_TOTAL_SUMMARY_TABLE);

		intent.putExtra("UserId", UserId);
		intent.putExtra("BundleNo", BundleNo);
		intent.putExtra("SubjectId", SubjectId);
		intent.putExtra("SubjectCode", SubjectCode);
		intent.putExtra("SeatNo", SEConstants.seatNo);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	// Deleting the Bundle
	private void showAlertForBundleDeletion(String msg, String positiveStr,
			String NegativeString) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Warning!  Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(positiveStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						String scripts_number = null;
						String sql_query_bundle_del = "select count(distinct bundle_serial_no) as Value from table_marks where UPPER(bundle_no)=UPPER('"
								+ bundleNo + "')";
						Cursor cursor_bundle_del = null;
						try {
							cursor_bundle_del = sEvalDatabase
									.executeSelectSQLQuery(sql_query_bundle_del);

							if (cursor_bundle_del != null) {
								while (!cursor_bundle_del.isAfterLast()) {

									scripts_number = cursor_bundle_del
											.getString(cursor_bundle_del
													.getColumnIndex("Value"));

									cursor_bundle_del.moveToNext();
								}
								showAlertForBundleDeletionWithTotalScripts(
										"Warning!  You are About to Delete the Entire Bundle Containing "
												+ scripts_number
												+ " AnswerScripts Marks. This Cannot Be Undone. ",
										"Ok", "Cancel");
							} else {
								FileLog.logInfo(
										"Cursor Null ---> "
												+ "ShowGrandTotalSummaryTable: showAlertForBundleDeletion() ",
										0);
							}

						} catch (Exception ex) {

							FileLog.logInfo(
									"Exception ---> "
											+ "ShowGrandTotalSummaryTable: showAlertForBundleDeletion() "
											+ ex.toString(), 0);

						} finally {
							DataBaseUtility.closeCursor(cursor_bundle_del);
						}

						dialog.dismiss();

					}
				});

		myAlertDialog.setNegativeButton(NegativeString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	private void showAlertForBundleDeletionWithTotalScripts(String msg,
			String positiveStr, String NegativeString) {

		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(positiveStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						String insertQueryForBundleHistory = "insert into table_bundle_history "
								+ "(is_deleted,bundle_no,subject_id,subject_code,status,enter_by,"
								+ "is_updated_server,tablet_IMEI,apk_version,is_unreadable,program_name,enter_on) "
								+ "select 1,bundle_no,subject_id,"
								+ "subject_code,status,enter_by,is_updated_server,tablet_IMEI,"
								+ "apk_version,is_unreadable,program_name,(datetime('now','localtime')) "
								+ "from table_bundle where UPPER(bundle_no)=UPPER('"
								+ bundleNo + "')";

						Cursor cursor = null;
						try {
							cursor = sEvalDatabase
									.executeSQLQuery(insertQueryForBundleHistory);
							if (cursor == null) {
								FileLog.logInfo(
										"Cursor Null ---> insert "
												+ "table_bundle_history failed ---> ShowGrandTotalSummaryTable: "
												+ "showAlertForBundleDeletionWithTotalScripts() ",
										0);
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> insert "
											+ "table_bundle_history failed ---> ShowGrandTotalSummaryTable: "
											+ "showAlertForBundleDeletionWithTotalScripts() "
											+ ex.toString(), 0);
						} finally {
							DataBaseUtility.closeCursor(cursor);
						}

						String updateSql = "Update table_marks_history set question_setid = 1000 where UPPER(bundle_no)=UPPER('"
								+ bundleNo + "')";
						Cursor cursorUpdate = null;
						try {
							cursorUpdate = sEvalDatabase
									.executeSQLQuery(updateSql);
							if (cursorUpdate == null) {
								FileLog.logInfo(
										"Cursor Null ---> update "
												+ "table_bundle_history failed ---> ShowGrandTotalSummaryTable: "
												+ "showAlertForBundleDeletionWithTotalScripts() ",
										0);
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> update "
											+ "table_bundle_history failed ---> ShowGrandTotalSummaryTable: "
											+ "showAlertForBundleDeletionWithTotalScripts() "
											+ ex.toString(), 0);
						} finally {
							DataBaseUtility.closeCursor(cursorUpdate);
						}

						String sql_query_marks_del = "Delete from table_marks where UPPER(bundle_no)=UPPER('"
								+ bundleNo + "')";

						Cursor cursorDeleteTableMarks = null;
						try {
							cursorDeleteTableMarks = sEvalDatabase
									.executeSQLQuery(sql_query_marks_del);
							if (cursorDeleteTableMarks == null) {
								FileLog.logInfo(
										"Cursor Null ---> Delete "
												+ "table_marks failed ---> ShowGrandTotalSummaryTable: "
												+ "showAlertForBundleDeletionWithTotalScripts() ",
										0);
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> Delete "
											+ "table_marks failed ---> ShowGrandTotalSummaryTable: "
											+ "showAlertForBundleDeletionWithTotalScripts() "
											+ ex.toString(), 0);
						} finally {
							DataBaseUtility.closeCursor(cursorDeleteTableMarks);
						}

						String sql_query_bundle_del = "Delete from table_bundle where UPPER(bundle_no)=UPPER('"
								+ bundleNo + "')";

						Cursor cursorDeleteTableBundle = null;
						try {
							cursorDeleteTableBundle = sEvalDatabase
									.executeSQLQuery(sql_query_bundle_del);
							if (cursorDeleteTableBundle == null) {
								FileLog.logInfo(
										"Cursor Null ---> Delete "
												+ "table_bundle failed ---> ShowGrandTotalSummaryTable: "
												+ "showAlertForBundleDeletionWithTotalScripts() ",
										0);
							} else {
								showAlertForSuccessfullDeletion(
										"The Bundle is Deleted Now! ", "Ok",
										"bundle");
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> Delete "
											+ "table_bundle failed ---> ShowGrandTotalSummaryTable: "
											+ "showAlertForBundleDeletionWithTotalScripts() "
											+ ex.toString(), 0);
						} finally {
							DataBaseUtility
									.closeCursor(cursorDeleteTableBundle);
						}

					}

				});

		myAlertDialog.setNegativeButton(NegativeString,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	private void showAlertForSuccessfullDeletion(String msg,
			String positiveStr, final String delfrom) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(positiveStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						if (delfrom.equalsIgnoreCase("bundle")) {
							if (switchToEvaluatorEntryActivity()) {
								Intent intent_eval_entry = new Intent(
										GrandTotalSummaryTable.this,
										EvaluatorEntryActivity.class);
								// +++++++++++++
								intent_eval_entry
										.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent_eval_entry);
							}

						} else if (delfrom.equalsIgnoreCase("script")) {

						}
					}
				});
		myAlertDialog.show();
	}

	private void lastScriptDeletion() {

		String del_last_script_count = checkbundleSerialNo();

		if (del_last_script_count.equalsIgnoreCase("0")) {
			showAlertForZeroScripts("There is nothing to delete", "Ok");

		} else {
			showAlertForLastScriptDeletion(
					"Warning!  You are About to Delete the AnswerBook Serial No."
							+ del_last_script_count
							+ ". This Cannot Be Undone. ", "Ok", "Cancel",
					del_last_script_count);
		}

	}

	private void showAlertForLastScriptDeletion(String msg, String positiveStr,
			String NegativeStr, final String del_last_script) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(positiveStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						String insertMarkHistoryForDeletion = "insert into table_marks_history "
								+ "(question_setid,barcode,bundle_serial_no,bundle_id, "
								+ "mark1a,mark1b,mark1c,mark1d,mark1e,r1_total, "
								+ "mark2a,mark2b,mark2c,mark2d,mark2e,r2_total, "
								+ "mark3a,mark3b,mark3c,mark3d,mark3e,r3_total, "
								+ "mark4a,mark4b,mark4c,mark4d,mark4e,r4_total, "
								+ "mark5a,mark5b,mark5c,mark5d,mark5e,r5_total, "
								+ "mark6a,mark6b,mark6c,mark6d,mark6e,r6_total, "
								+ "mark7a,mark7b,mark7c,mark7d,mark7e,r7_total, "
								+ "mark8a,mark8b,mark8c,mark8d,mark8e,r8_total, "
								+ "mark9a,mark9b,mark9c,mark9d,mark9e,r9_total, "
								+ "mark10a,mark10b,mark10c,mark10d,mark10e,r10_total, "
								+ "mark11a,mark11b,mark11c,mark11d,mark11e,r11_total, "
								+ "total_mark,bundle_no,subject_code,question_setcode, "
								+ "user_id,is_updated_server,tablet_IMEI,barcode_status, "
								+ "edit_userid,enter_on) "
								+ " select 2000,barcode,bundle_serial_no,bundle_id, "
								+ "mark1a,mark1b,mark1c,mark1d,mark1e,r1_total, "
								+ "mark2a,mark2b,mark2c,mark2d,mark2e,r2_total, "
								+ "mark3a,mark3b,mark3c,mark3d,mark3e,r3_total, "
								+ "mark4a,mark4b,mark4c,mark4d,mark4e,r4_total, "
								+ "mark5a,mark5b,mark5c,mark5d,mark5e,r5_total, "
								+ "mark6a,mark6b,mark6c,mark6d,mark6e,r6_total, "
								+ "mark7a,mark7b,mark7c,mark7d,mark7e,r7_total, "
								+ "mark8a,mark8b,mark8c,mark8d,mark8e,r8_total, "
								+ "mark9a,mark9b,mark9c,mark9d,mark9e,r9_total, "
								+ "mark10a,mark10b,mark10c,mark10d,mark10e,r10_total, "
								+ "mark11a,mark11b,mark11c,mark11d,mark11e,r11_total, "
								+ "total_mark,bundle_no,subject_code,question_setcode, "
								+ "user_id,is_updated_server,tablet_IMEI,barcode_status, "
								+ "edit_userid,(datetime('now','localtime')) "
								+ " from table_marks "
								+ " where bundle_serial_no="
								+ Integer.parseInt(del_last_script)
								+ " AND bundle_no='" + bundleNo + "'";

						Cursor cursorinsertMarkHistoryForDeletion = null;
						try {
							cursorinsertMarkHistoryForDeletion = sEvalDatabase
									.executeSQLQuery(insertMarkHistoryForDeletion);
							if (cursorinsertMarkHistoryForDeletion == null) {
								FileLog.logInfo(
										"Cursor Null ---> insert "
												+ "table_marks_history failed ---> ShowGrandTotalSummaryTable: "
												+ "showAlertForLastScriptDeletion() ",
										0);
							} else {

							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> insert "
											+ "table_marks_history failed ---> ShowGrandTotalSummaryTable: "
											+ "showAlertForLastScriptDeletion() "
											+ ex.toString(), 0);
						} finally {
							DataBaseUtility
									.closeCursor(cursorinsertMarkHistoryForDeletion);
						}

						String sql_query_lastmarks_del = "Delete from table_marks where bundle_serial_no="
								+ Integer.parseInt(del_last_script)
								+ " AND bundle_no='" + bundleNo + "'";

						Cursor cursorDeletionTableMarks = null;
						try {
							cursorDeletionTableMarks = sEvalDatabase
									.executeSQLQuery(sql_query_lastmarks_del);
							if (cursorDeletionTableMarks == null) {
								FileLog.logInfo(
										"Cursor Null ---> Delete "
												+ "table_marks failed ---> ShowGrandTotalSummaryTable: "
												+ "showAlertForLastScriptDeletion() ",
										0);
							} else {
								showAlertForSuccessfullDeletion(
										"You have Deleted the AnswerScript "
												+ del_last_script, "Ok",
										"script");
								entry();
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> Delete "
											+ "table_marks failed ---> ShowGrandTotalSummaryTable: "
											+ "showAlertForLastScriptDeletion() "
											+ ex.toString(), 0);
						} finally {
							DataBaseUtility
									.closeCursor(cursorDeletionTableMarks);
						}

					}

				});

		myAlertDialog.setNegativeButton(NegativeStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		myAlertDialog.show();
	}

	private void showAlertForZeroScripts(String msg, String positiveStr) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
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

	private String checkbundleSerialNo() {
		String del_last_script_count = "0";

		String del_last_script = "select count(distinct bundle_serial_no) as Value from table_marks where UPPER(bundle_no)=UPPER('"
				+ bundleNo + "')";
		Cursor cursor_bundle_del = null;
		try {
			cursor_bundle_del = sEvalDatabase
					.executeSelectSQLQuery(del_last_script);

			if (cursor_bundle_del != null) {
				while (!cursor_bundle_del.isAfterLast()) {

					del_last_script_count = cursor_bundle_del
							.getString(cursor_bundle_del
									.getColumnIndex("Value"));

					cursor_bundle_del.moveToNext();
				}
			} else {
				FileLog.logInfo(
						"Cursor Null ---> ShowGrandTotalSummaryTable: checkbundleSerialNo() ",
						0);
			}

		} catch (Exception e) {
			FileLog.logInfo(
					"Exception ---> ShowGrandTotalSummaryTable: checkbundleSerialNo() "
							+ e.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor_bundle_del);
		}

		return del_last_script_count;
	}

	private void ScriptModificationAlert(final int count) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog
				.setMessage("You have Already Evaluated "
						+ count
						+ " of "
						+ maxAnswerBook
						+ " Scripts For this Bundle.\nDo You Want to Modify the Bundle Script Count?");
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {

						dialog.dismiss();
						switchToScriptCountScreen();
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

	private void switchToScriptCountScreen() {
		// progressBar.setProgress(50);
		Intent intent = new Intent(this, GetScriptCountFromBundle.class);
		intent.putExtra("SeatNo", SEConstants.seatNo);
		intent.putExtra("UserId", userId);
		intent.putExtra("BundleNo", bundleNo);
		intent.putExtra("IsModified", "IsModified");
		intent.putExtra("SubjectCode", subjectCode);
		intent.putExtra("CurrentAnswerBook", currentAnsBook);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

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
}
