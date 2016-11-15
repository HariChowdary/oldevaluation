//package com.infoplus.smartevaluation;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.regex.Pattern;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.database.Cursor;
//import android.graphics.Typeface;
//import android.os.BatteryManager;
//import android.os.Bundle;
//import android.os.PowerManager;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.view.ContextThemeWrapper;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.infoplus.smartevaluation.SEConstants;
//import com.infoplus.smartevaluation.db.DBHelper;
//import com.infoplus.smartevaluation.Utility;
//
//public class RC_MarksShowActivity extends Activity implements OnClickListener,
//		TextWatcher, OnTouchListener {
//
//	private static int max_mark = 16;
//	EditText et_mark1a;
//	EditText et_mark1b;
//	EditText et_mark1c;
//	EditText et_mark1d;
//
//	EditText et_mark2a;
//	EditText et_mark2b;
//	EditText et_mark2c;
//	EditText et_mark2d;
//
//	EditText et_mark3a;
//	EditText et_mark3b;
//	EditText et_mark3c;
//	EditText et_mark3d;
//
//	EditText et_mark4a;
//	EditText et_mark4b;
//	EditText et_mark4c;
//	EditText et_mark4d;
//
//	EditText et_mark5a;
//	EditText et_mark5b;
//	EditText et_mark5c;
//	EditText et_mark5d;
//
//	EditText et_mark6a;
//	EditText et_mark6b;
//	EditText et_mark6c;
//	EditText et_mark6d;
//
//	EditText et_mark7a;
//	EditText et_mark7b;
//	EditText et_mark7c;
//	EditText et_mark7d;
//
//	EditText et_mark8a;
//	EditText et_mark8b;
//	EditText et_mark8c;
//	EditText et_mark8d;
//
//	TextView et_mark1_total;
//	TextView et_mark2_total;
//	TextView et_mark3_total;
//	TextView et_mark4_total;
//	TextView et_mark5_total;
//	TextView et_mark6_total;
//	TextView et_mark7_total;
//	TextView et_mark8_total;
//
//	TextView et_q1;
//	TextView et_q2;
//	TextView et_q3;
//	TextView et_q4;
//	TextView et_q5;
//	TextView et_q6;
//	TextView et_q7;
//	TextView et_q8;
//
//	TextView et_grand_toal;
//	private String subjectCode;
//	private String userId;
//	private String bundleNo;
//	private String bundleSerialNo;
//
//	DBHelper database;
//	private ProgressDialog progressDialog;
//
//	BroadcastReceiver batteryLevelReceiver;
//	private PowerManager.WakeLock wl;
//
////	@Override
////	public boolean onCreateOptionsMenu(Menu menu) {
////		getMenuInflater().inflate(R.menu.rc_menu, menu);
////		menu.findItem(R.id.menu_settings).setVisible(false);
////		return true;
////	}
////
////	@Override
////	public boolean onMenuItemSelected(int featureId, MenuItem item) {
////		if (item.getItemId() == R.id.menu_back) {
////			finish();
////		}
////		return super.onMenuItemSelected(featureId, item);
////	}
//
//	// check battery level
//	private void batteryLevel() {
//		batteryLevelReceiver = new BroadcastReceiver() {
//			public void onReceive(Context context, Intent intent) {
//				int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,
//						-1);
//				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//				int level = -1;
//				if (rawlevel >= 0 && scale > 0) {
//					level = (rawlevel * 100) / scale;
//				}
//				// ((TextView) findViewById(R.id.txt_batteryLevel))
//				// .setText("Battery Level Remaining: " + level + "%");
//				if (level < SEConstants.TABLET_CHARGE) {
//					alertMessageForCharge();
//				}
//			}
//		};
//		IntentFilter batteryLevelFilter = new IntentFilter(
//				Intent.ACTION_BATTERY_CHANGED);
//		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		batteryLevel();
//		if (wl != null) {
//			wl.acquire();
//		}
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		if (batteryLevelReceiver != null) {
//			try {
//				unregisterReceiver(batteryLevelReceiver);
//				batteryLevelReceiver = null;
//			} catch (IllegalArgumentException ILAE) {
//
//			}
//		}
//
//		if (wl != null) {
//			wl.release();
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		if (progressDialog != null)
//			if (progressDialog.isShowing()) {
//				progressDialog.cancel();
//			}
//	}
//
//	private void alertMessageForCharge() {
//		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
//		myAlertDialog.setTitle(getString(R.string.app_name));
//		myAlertDialog.setMessage(getString(R.string.alert_charge));
//
//		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
//				new DialogInterface.OnClickListener() {
//
//					public void onClick(DialogInterface Dialog, int arg1) {
//						// do something when the OK button is clicked
//						navigateToTabletHomeScreen();
//						Dialog.dismiss();
//					}
//				});
//
//		myAlertDialog.show();
//	}
//
//	private void navigateToTabletHomeScreen() {
//		Intent intent = new Intent(Intent.ACTION_MAIN);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.addCategory(Intent.CATEGORY_HOME);
//		startActivity(intent);
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.marks_show_activity);
//		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
//				"EvaluatorEntryActivity");
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
//				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//		database = DBHelper.getInstance(this);
//		Intent _intent_extras = getIntent();
//		subjectCode = _intent_extras.getStringExtra(SEConstants.subject);
//		userId = _intent_extras.getStringExtra(SEConstants.USER_ID);
//		bundleNo = _intent_extras.getStringExtra(SEConstants.bundle);
//		bundleSerialNo = _intent_extras
//				.getStringExtra(SEConstants.bundle_SNo);
//		LinearLayout ll_submit = (LinearLayout) findViewById(R.id.ll_submit);
//		ll_submit.addView(addNumbersView());
//
//		// get max marks from shared preference
//		SharedPreferences preferences = getSharedPreferences(
//				SEConstants.SHARED_PREF_MAX_TOTAL, 0);
//		max_mark = preferences.getInt(SEConstants.SHARED_PREF_MAX_TOTAL, 16);
//
//		(findViewById(R.id.button1)).setOnClickListener(this);
//		(findViewById(R.id.button2)).setOnClickListener(this);
//		(findViewById(R.id.button3)).setOnClickListener(this);
//		(findViewById(R.id.button4)).setOnClickListener(this);
//
//		(findViewById(R.id.button5)).setOnClickListener(this);
//		(findViewById(R.id.button6)).setOnClickListener(this);
//		(findViewById(R.id.button7)).setOnClickListener(this);
//		(findViewById(R.id.button8)).setOnClickListener(this);
//
//		(findViewById(R.id.button9)).setOnClickListener(this);
//		(findViewById(R.id.button0)).setOnClickListener(this);
//		(findViewById(R.id.btn_dot)).setOnClickListener(this);
//		(findViewById(R.id.btn_clear)).setOnClickListener(this);
//		(findViewById(R.id.btn_delete)).setOnClickListener(this);
//		findViewById(R.id.btn_submit).setOnClickListener(this);
//
//		et_mark1a = (EditText) findViewById(R.id.q1_a);
//		et_mark1b = ((EditText) findViewById(R.id.q1_b));
//		et_mark1c = ((EditText) findViewById(R.id.q1_c));
//		et_mark1d = ((EditText) findViewById(R.id.q1_d));
//
//		et_mark2a = ((EditText) findViewById(R.id.q2_a));
//		et_mark2b = ((EditText) findViewById(R.id.q2_b));
//		et_mark2c = ((EditText) findViewById(R.id.q2_c));
//		et_mark2d = ((EditText) findViewById(R.id.q2_d));
//
//		et_mark3a = ((EditText) findViewById(R.id.q3_a));
//		et_mark3b = ((EditText) findViewById(R.id.q3_b));
//		et_mark3c = ((EditText) findViewById(R.id.q3_c));
//		et_mark3d = ((EditText) findViewById(R.id.q3_d));
//
//		et_mark4a = ((EditText) findViewById(R.id.q4_a));
//		et_mark4b = ((EditText) findViewById(R.id.q4_b));
//		et_mark4c = ((EditText) findViewById(R.id.q4_c));
//		et_mark4d = ((EditText) findViewById(R.id.q4_d));
//
//		et_mark5a = ((EditText) findViewById(R.id.q5_a));
//		et_mark5b = ((EditText) findViewById(R.id.q5_b));
//		et_mark5c = ((EditText) findViewById(R.id.q5_c));
//		et_mark5d = ((EditText) findViewById(R.id.q5_d));
//
//		et_mark6a = ((EditText) findViewById(R.id.q6_a));
//		et_mark6b = ((EditText) findViewById(R.id.q6_b));
//		et_mark6c = ((EditText) findViewById(R.id.q6_c));
//		et_mark6d = ((EditText) findViewById(R.id.q6_d));
//
//		et_mark7a = ((EditText) findViewById(R.id.q7_a));
//		et_mark7b = ((EditText) findViewById(R.id.q7_b));
//		et_mark7c = ((EditText) findViewById(R.id.q7_c));
//		et_mark7d = ((EditText) findViewById(R.id.q7_d));
//
//		et_mark8a = ((EditText) findViewById(R.id.q8_a));
//		et_mark8b = ((EditText) findViewById(R.id.q8_b));
//		et_mark8c = ((EditText) findViewById(R.id.q8_c));
//		et_mark8d = ((EditText) findViewById(R.id.q8_d));
//
//		et_mark1_total = ((TextView) findViewById(R.id.q1_total));
//		et_mark2_total = ((TextView) findViewById(R.id.q2_total));
//		et_mark3_total = ((TextView) findViewById(R.id.q3_total));
//		et_mark4_total = ((TextView) findViewById(R.id.q4_total));
//		et_mark5_total = ((TextView) findViewById(R.id.q5_total));
//		et_mark6_total = ((TextView) findViewById(R.id.q6_total));
//		et_mark7_total = ((TextView) findViewById(R.id.q7_total));
//		et_mark8_total = ((TextView) findViewById(R.id.q8_total));
//
//		et_q1 = ((TextView) findViewById(R.id.q1));
//		et_q2 = ((TextView) findViewById(R.id.q2));
//		et_q3 = ((TextView) findViewById(R.id.q3));
//		et_q4 = ((TextView) findViewById(R.id.q4));
//		et_q5 = ((TextView) findViewById(R.id.q5));
//		et_q6 = ((TextView) findViewById(R.id.q6));
//		et_q7 = ((TextView) findViewById(R.id.q7));
//		et_q8 = ((TextView) findViewById(R.id.q8));
//
//		et_grand_toal = ((TextView) findViewById(R.id.grand_total));
//
//		et_mark1a.addTextChangedListener(this);
//		et_mark1b.addTextChangedListener(this);
//		et_mark1c.addTextChangedListener(this);
//		et_mark1d.addTextChangedListener(this);
//
//		et_mark2a.addTextChangedListener(this);
//		et_mark2b.addTextChangedListener(this);
//		et_mark2c.addTextChangedListener(this);
//		et_mark2d.addTextChangedListener(this);
//
//		et_mark3a.addTextChangedListener(this);
//		et_mark3b.addTextChangedListener(this);
//		et_mark3c.addTextChangedListener(this);
//		et_mark3d.addTextChangedListener(this);
//
//		et_mark4a.addTextChangedListener(this);
//		et_mark4b.addTextChangedListener(this);
//		et_mark4c.addTextChangedListener(this);
//		et_mark4d.addTextChangedListener(this);
//
//		et_mark5a.addTextChangedListener(this);
//		et_mark5b.addTextChangedListener(this);
//		et_mark5c.addTextChangedListener(this);
//		et_mark5d.addTextChangedListener(this);
//
//		et_mark6a.addTextChangedListener(this);
//		et_mark6b.addTextChangedListener(this);
//		et_mark6c.addTextChangedListener(this);
//		et_mark6d.addTextChangedListener(this);
//
//		et_mark7a.addTextChangedListener(this);
//		et_mark7b.addTextChangedListener(this);
//		et_mark7c.addTextChangedListener(this);
//		et_mark7d.addTextChangedListener(this);
//
//		et_mark8a.addTextChangedListener(this);
//		et_mark8b.addTextChangedListener(this);
//		et_mark8c.addTextChangedListener(this);
//		et_mark8d.addTextChangedListener(this);
//
//		et_mark1a.setOnTouchListener(this);
//		et_mark1b.setOnTouchListener(this);
//		et_mark1c.setOnTouchListener(this);
//		et_mark1d.setOnTouchListener(this);
//		//
//		et_mark2a.setOnTouchListener(this);
//		et_mark2b.setOnTouchListener(this);
//		et_mark2c.setOnTouchListener(this);
//		et_mark2d.setOnTouchListener(this);
//		//
//		et_mark3a.setOnTouchListener(this);
//		et_mark3b.setOnTouchListener(this);
//		et_mark3c.setOnTouchListener(this);
//		et_mark3d.setOnTouchListener(this);
//
//		et_mark4a.setOnTouchListener(this);
//		et_mark4b.setOnTouchListener(this);
//		et_mark4c.setOnTouchListener(this);
//		et_mark4d.setOnTouchListener(this);
//
//		et_mark5a.setOnTouchListener(this);
//		et_mark5b.setOnTouchListener(this);
//		et_mark5c.setOnTouchListener(this);
//		et_mark5d.setOnTouchListener(this);
//
//		et_mark6a.setOnTouchListener(this);
//		et_mark6b.setOnTouchListener(this);
//		et_mark6c.setOnTouchListener(this);
//		et_mark6d.setOnTouchListener(this);
//
//		et_mark7a.setOnTouchListener(this);
//		et_mark7b.setOnTouchListener(this);
//		et_mark7c.setOnTouchListener(this);
//		et_mark7d.setOnTouchListener(this);
//
//		et_mark8a.setOnTouchListener(this);
//		et_mark8b.setOnTouchListener(this);
//		et_mark8c.setOnTouchListener(this);
//		et_mark8d.setOnTouchListener(this);
//
//		et_q1.setOnTouchListener(this);
//		et_q2.setOnTouchListener(this);
//		et_q3.setOnTouchListener(this);
//		et_q4.setOnTouchListener(this);
//		et_q5.setOnTouchListener(this);
//		et_q6.setOnTouchListener(this);
//		et_q7.setOnTouchListener(this);
//		et_q8.setOnTouchListener(this);
//
//		et_mark1_total.setOnTouchListener(this);
//		et_mark2_total.setOnTouchListener(this);
//		et_mark3_total.setOnTouchListener(this);
//		et_mark4_total.setOnTouchListener(this);
//		et_mark5_total.setOnTouchListener(this);
//		et_mark6_total.setOnTouchListener(this);
//		et_mark7_total.setOnTouchListener(this);
//		et_mark8_total.setOnTouchListener(this);
//
//		runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				showProgress("Loading data ...");
//				showItems();
//
//				hideProgress();
//			}
//		});
//
//	}
//
//	 
//	 
//
//	// show alert
//	private void showAlert(String msg, String positiveStr, String negativeStr) {
//		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
//				new ContextThemeWrapper(this, R.style.alert_text_style));
//		myAlertDialog.setTitle(getString(R.string.recounting));
//		myAlertDialog.setMessage(msg);
//		myAlertDialog.setCancelable(false);
//
//		myAlertDialog.setPositiveButton(positiveStr,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int arg1) {
//						// do something when the OK button is
//						// clicked
//						dialog.dismiss();
//						finish();
//
//					}
//				});
//
//		myAlertDialog.setNegativeButton(negativeStr,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int arg1) {
//						// do something when the OK button is
//						// clicked
//						dialog.dismiss();
//					}
//				});
//
//		myAlertDialog.show();
//	}
//
//	// set Marks to EditText
//	private void setMarkToCellFromDB(String pMark, EditText pTextView) {
//		if (!TextUtils.isEmpty(pMark) && !pMark.equals("null")) {
//			pTextView.setText(pMark);
//		}
//	}
//
//	// set Marks to TextView
//	private void setMarkToCellFromDB(String pMark, TextView pTextView) {
//		if (!TextUtils.isEmpty(pMark) && !pMark.equals("null")) {
//			pTextView.setText(pMark);
//		}
//	}
//
//	private View addNumbersView() {
//		View numbersView = LayoutInflater.from(this).inflate(
//				R.layout.layout_numbers, null); 
//		// Button btn = (Button) numbersView.findViewById(R.id.btn_submit);
//		(numbersView.findViewById(R.id.btn_submit)).setOnClickListener(this);
//		return numbersView;
//	}
//
//	// @Override
//	// public boolean onCreateOptionsMenu(Menu menu) {
//	// getMenuInflater().inflate(R.menu.activity_main, menu);
//	// return true;
//	// }
//	//
//	// @Override
//	// public boolean onMenuItemSelected(int featureId, MenuItem item) {
//	// if (item.getItemId() == R.id.menu_back) {
//	// finish();
//	// }
//	// return super.onMenuItemSelected(featureId, item);
//	// }
//
//	private void submit(View view) {
//		// insert to DB and Navigate to ScanAnswerSheetBarcode or Summary screen
//		showAlertForScriptFinalSubmission("Want to Submit the Answer Sheet ?",
//				"OK", "Cancel", view);
//
//	}
//
//	// show alert
//	private void showAlertForScriptFinalSubmission(String msg,
//			String positiveStr, String negativeStr, final View view) {
//		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
//				new ContextThemeWrapper(this, R.style.alert_text_style));
//		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
//		myAlertDialog.setMessage(msg);
//		myAlertDialog.setCancelable(false);
//
//		myAlertDialog.setPositiveButton(positiveStr,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int arg1) {
//						// do something when the OK button is
//						// clicked
//						dialog.dismiss();
//						setContentValuesOnFinalSubmission(view);
//					}
//				});
//
//		myAlertDialog.setNegativeButton(negativeStr,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int arg1) {
//						// do something when the OK button is
//						// clicked
//						dialog.dismiss();
//
//					}
//				});
//
//		myAlertDialog.show();
//	}
//
//	private void navigateToScanActivtyScreen() {
//		if (getIntent().hasExtra(
//				SEConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)
//				|| bundleSerialNo.equalsIgnoreCase("40")
//				|| bundleSerialNo.equalsIgnoreCase(Utility
//						.getTotalScriptsFrom_SharedPref(this))) {
//
//			Intent _intent = new Intent(this, RC_SumOfTotalsEntryScreen.class);
//			_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			_intent.putExtra(SEConstants.USER_ID, userId);
//			_intent.putExtra(SEConstants.SUBJECT_CODE, subjectCode);
//			_intent.putExtra(SEConstants.BUNDLE_NO, bundleNo);
//			startActivity(_intent);
//		} else {
//			Intent _intent = new Intent(this, RC_ScanAnswerSheetBarcode.class);
//			_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			_intent.putExtra(SEConstants.USER_ID, userId);
//			_intent.putExtra(SEConstants.SUBJECT_CODE, subjectCode);
//			_intent.putExtra(SEConstants.BUNDLE_NO, bundleNo);
//			startActivity(_intent);
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//
//		switch (v.getId()) {
//		case R.id.btn_submit:
//			submit(v);
//			break;
//		case R.id.button1:
//			setTextToFocusedView("1");
//			break;
//
//		case R.id.button2:
//			setTextToFocusedView("2");
//			break;
//
//		case R.id.button3:
//			setTextToFocusedView("3");
//			break;
//
//		case R.id.button4:
//			setTextToFocusedView("4");
//			break;
//
//		case R.id.button5:
//			setTextToFocusedView("5");
//			break;
//
//		case R.id.button6:
//			setTextToFocusedView("6");
//			break;
//
//		case R.id.button7:
//			setTextToFocusedView("7");
//			break;
//
//		case R.id.button8:
//			setTextToFocusedView("8");
//			break;
//
//		case R.id.button9:
//			setTextToFocusedView("9");
//			break;
//
//		case R.id.button0:
//			setTextToFocusedView("0");
//			break;
//
//		case R.id.btn_dot:
//			View dotFocusedView = getCurrentFocus();
//			if (dotFocusedView != null && dotFocusedView instanceof EditText) {
//				String data = ((EditText) dotFocusedView).getText().toString()
//						.trim();
//				if (!(data.contains(".")) && !TextUtils.isEmpty(data)) {
//					setTextToFocusedView(".");
//				}
//			}
//			break;
//
//		case R.id.btn_clear:
//			View focusedView = getCurrentFocus();
//			if (focusedView != null && focusedView instanceof EditText) {
//				((EditText) focusedView).setText("");
//			}
//			break;
//
//		case R.id.btn_delete:
//			View focusedView2 = getCurrentFocus();
//			if (focusedView2 != null && focusedView2 instanceof EditText) {
//				deleteCharAndSetSelection((EditText) focusedView2);
//			}
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	// set text to focused view on click
//	private void setTextToFocusedView(String text) {
//		View _focusedView = getCurrentFocus();
//		if (_focusedView != null && _focusedView instanceof EditText) {
//			EditText _et_focusedView = ((EditText) _focusedView);
//			String prevText = _et_focusedView.getText().toString().trim();
//			if (TextUtils.isEmpty(prevText)) {
//				// check whether marks are valid or not
//				if (Float.parseFloat(text) > max_mark
//						|| !FloatOrIntegerOnlyAllow(text)) {
//					if (!text.equals("."))
//						alertForInvalidMark(_focusedView, true, text);
//				} else {
//					if (checkRowTotalExceedsMaxValue(_et_focusedView, text) > max_mark) {
//						showAlertTotalExceedsMaxMark(
//								getString(R.string.alert_total_exceeds)
//										+ " "
//										+ +max_mark
//										+ " / Sub Marks Exceeds  the Maximum Marks",
//								getString(R.string.alert_dialog_ok),
//								_et_focusedView, "");
//					} else {
//						setRemarkInContentValue2(_et_focusedView, text, false,
//								false);
//						_et_focusedView.setText(text);
//						_et_focusedView.setSelection(_et_focusedView.getText()
//								.length());
//					}
//				}
//			} else {
//				if (text.equals(".")) {
//					_et_focusedView.setText(prevText + text);
//					_et_focusedView.setSelection(_et_focusedView.getText()
//							.length());
//					return;
//				}
//
//				if (Float.parseFloat(prevText + text) > max_mark
//						|| !FloatOrIntegerOnlyAllow(prevText + text)) {
//					alertForInvalidMark(_focusedView, true, prevText + text);
//				} else {
//					if (checkRowTotalExceedsMaxValue(_et_focusedView, prevText
//							+ text) > max_mark) {
//						showAlertTotalExceedsMaxMark(
//								getString(R.string.alert_total_exceeds)
//										+ " "
//										+ +max_mark
//										+ " / Sub Marks Exceeds  the Maximum Marks",
//								getString(R.string.alert_dialog_ok),
//								_et_focusedView, "");
//					} else {
//						setRemarkInContentValue2(_et_focusedView, prevText
//								+ text, false, false);
//						_et_focusedView.setText(prevText + text);
//						_et_focusedView.setSelection(_et_focusedView.getText()
//								.length());
//					}
//				}
//			}
//			_et_focusedView.setSelection(_et_focusedView.getText().length());
//		}
//	}
//
//	private void setRemarkInContentValue3(String CONS_REMARK, String CONS_MARK,
//			final String remarkOrMark, boolean setRemark, boolean insertToTempDB) {
//		ContentValues _contentValues = new ContentValues();
//		if (setRemark) {
//			_contentValues.put(CONS_REMARK, remarkOrMark);
//		} else {
//			_contentValues.put(CONS_MARK, remarkOrMark);
//		}
//
//		database.updateRow(
//				SEConstants.TABLE_RC_MARKS,
//				_contentValues,
//				SEConstants.ANS_BOOK_BARCODE
//						+ " = '"
//						+ getIntent().getStringExtra(
//								SEConstants.ANS_BOOK_BARCODE) + "'");
//
//		// if (insertToTempDB) {
//		// new Scrutiny_TempDatabase(this).updateRow(_contentValues);
//		//
//		// } else {
//		// insertToDB(_contentValues);
//		// }
//	}
//
//	// set mark or remark
//	private void setRemarkInContentValue2(View view, final String remarkOrMark,
//			boolean setRemark, boolean insertToTempDB) {
//		switch (view.getId()) {
//		case R.id.q1_a:
//			setRemarkInContentValue3(SEConstants.M1A_REMARK,
//					SEConstants.MARK1A, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q1_b:
//			setRemarkInContentValue3(SEConstants.M1B_REMARK,
//					SEConstants.MARK1B, remarkOrMark, setRemark,
//					insertToTempDB);
//			break;
//		case R.id.q1_c:
//			setRemarkInContentValue3(SEConstants.M1C_REMARK,
//					SEConstants.MARK1C, remarkOrMark, setRemark,
//					insertToTempDB);
//			break;
//		case R.id.q1_d:
//			setRemarkInContentValue3(SEConstants.M1D_REMARK,
//					SEConstants.MARK1D, remarkOrMark, setRemark,
//					insertToTempDB);
//			break;
//
//		case R.id.q2_a:
//			setRemarkInContentValue3(SEConstants.M2A_REMARK,
//					SEConstants.MARK2A, remarkOrMark, setRemark,
//					insertToTempDB);
//			break;
//		case R.id.q2_b:
//			setRemarkInContentValue3(SEConstants.M2B_REMARK,
//					SEConstants.MARK2B, remarkOrMark, setRemark,
//					insertToTempDB);
//			break;
//		case R.id.q2_c:
//			setRemarkInContentValue3(SEConstants.M2C_REMARK,
//					SEConstants.MARK2C, remarkOrMark, setRemark,
//					insertToTempDB);
//			break;
//		case R.id.q2_d:
//			setRemarkInContentValue3(SEConstants.M2D_REMARK,
//					SEConstants.MARK2D, remarkOrMark, setRemark,
//					insertToTempDB);
//			break;
//
//		case R.id.q3_a:
//			setRemarkInContentValue3(SEConstants.M3A_REMARK,
//					SEConstants.MARK3A, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q3_b:
//			setRemarkInContentValue3(SEConstants.M3B_REMARK,
//					SEConstants.MARK3B, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q3_c:
//			setRemarkInContentValue3(SEConstants.M3C_REMARK,
//					SEConstants.MARK3C, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q3_d:
//			setRemarkInContentValue3(SEConstants.M3D_REMARK,
//					SEConstants.MARK3D, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q4_a:
//			setRemarkInContentValue3(SEConstants.M4A_REMARK,
//					SEConstants.MARK4A, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q4_b:
//			setRemarkInContentValue3(SEConstants.M4B_REMARK,
//					SEConstants.MARK4B, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q4_c:
//			setRemarkInContentValue3(SEConstants.M4C_REMARK,
//					SEConstants.MARK4C, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q4_d:
//			setRemarkInContentValue3(SEConstants.M4D_REMARK,
//					SEConstants.MARK4D, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q5_a:
//			setRemarkInContentValue3(SEConstants.M5A_REMARK,
//					SEConstants.MARK5A, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q5_b:
//			setRemarkInContentValue3(SEConstants.M5B_REMARK,
//					SEConstants.MARK5B, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q5_c:
//			setRemarkInContentValue3(SEConstants.M5C_REMARK,
//					SEConstants.MARK5C, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q5_d:
//			setRemarkInContentValue3(SEConstants.M5D_REMARK,
//					SEConstants.MARK5D, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q6_a:
//			setRemarkInContentValue3(SEConstants.M6A_REMARK,
//					SEConstants.MARK6A, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q6_b:
//			setRemarkInContentValue3(SEConstants.M6B_REMARK,
//					SEConstants.MARK6B, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q6_c:
//			setRemarkInContentValue3(SEConstants.M6C_REMARK,
//					SEConstants.MARK6C, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q6_d:
//			setRemarkInContentValue3(SEConstants.M6D_REMARK,
//					SEConstants.MARK6D, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q7_a:
//			setRemarkInContentValue3(SEConstants.M7A_REMARK,
//					SEConstants.MARK7A, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q7_b:
//			setRemarkInContentValue3(SEConstants.M7B_REMARK,
//					SEConstants.MARK7B, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q7_c:
//			setRemarkInContentValue3(SEConstants.M7C_REMARK,
//					SEConstants.MARK7C, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q7_d:
//			setRemarkInContentValue3(SEConstants.M7D_REMARK,
//					SEConstants.MARK7D, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q8_a:
//			setRemarkInContentValue3(SEConstants.M8A_REMARK,
//					SEConstants.MARK8A, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q8_b:
//			setRemarkInContentValue3(SEConstants.M8B_REMARK,
//					SEConstants.MARK8B, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q8_c:
//			setRemarkInContentValue3(SEConstants.M8C_REMARK,
//					SEConstants.MARK8C, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//		case R.id.q8_d:
//			setRemarkInContentValue3(SEConstants.M8D_REMARK,
//					SEConstants.MARK8D, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q1_total:
//			setRemarkInContentValue3(SEConstants.R1_REMARK,
//					SEConstants.R1_TOTAL, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q2_total:
//			setRemarkInContentValue3(SEConstants.R2_REMARK,
//					SEConstants.R2_TOTAL, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q3_total:
//			setRemarkInContentValue3(SEConstants.R3_REMARK,
//					SEConstants.R3_TOTAL, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q4_total:
//			setRemarkInContentValue3(SEConstants.R4_REMARK,
//					SEConstants.R4_TOTAL, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q5_total:
//			setRemarkInContentValue3(SEConstants.R5_REMARK,
//					SEConstants.R5_TOTAL, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q6_total:
//			setRemarkInContentValue3(SEConstants.R6_REMARK,
//					SEConstants.R6_TOTAL, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q7_total:
//			setRemarkInContentValue3(SEConstants.R7_REMARK,
//					SEConstants.R7_TOTAL, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.q8_total:
//			setRemarkInContentValue3(SEConstants.R8_REMARK,
//					SEConstants.R8_TOTAL, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		case R.id.grand_total:
//			setRemarkInContentValue3(SEConstants.GRAND_TOTAL_REMARK,
//					SEConstants.GRAND_TOTAL_MARK, remarkOrMark, setRemark,
//					insertToTempDB);
//
//			break;
//
//		default:
//			break;
//		}
//
//	}
//
//	private void showAlertTotalExceedsMaxMark(String msg, String positiveStr,
//			final EditText editText, final String prevMarks) {
//		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
//				new ContextThemeWrapper(this, R.style.alert_text_style));
//		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
//		myAlertDialog.setMessage(msg);
//		myAlertDialog.setCancelable(false);
//
//		myAlertDialog.setPositiveButton(positiveStr,
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int arg1) {
//						// do something when the OK button is
//						// clicked
//						// editText.setFocusableInTouchMode(true);
//						// editText.setFocusable(true);
//						if (TextUtils.isEmpty(prevMarks)) {
//							editText.setText("");
//						} else {
//							editText.setText(prevMarks);
//						}
//						dialog.dismiss();
//
//					}
//				});
//
//		// myAlertDialog.setNegativeButton(negativeStr,
//		// new DialogInterface.OnClickListener() {
//		// public void onClick(DialogInterface dialog, int arg1) {
//		// // do something when the OK button is
//		// // clicked
//		// dialog.dismiss();
//		// }
//		// });
//
//		myAlertDialog.show();
//	}
//
//	private void alertForInvalidMark(final View view2,
//			final boolean fromNumbersLayout, final String mark) {
//		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
//				new ContextThemeWrapper(this, R.style.alert_text_style));
//		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
//		myAlertDialog.setMessage(getResources().getString(
//				R.string.alert_valid_marks));
//		myAlertDialog.setCancelable(false);
//
//		myAlertDialog.setPositiveButton("Ok",
//				new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int arg1) {
//						// do something when the OK button is
//						// clicked
//						dialog.dismiss();
//						// if (!fromNumbersLayout) {
//						// editMarksEnterDialog(view2);
//						// } else {
//						((EditText) view2).setText("");
//						// }
//
//					}
//				});
//
//		myAlertDialog.show();
//	}
//
//	private float checkRowTotalExceedsMaxValue(View view, String pMarks) {
//		String mark;
//		float _fl_total_row_mark;
//		if (TextUtils.isEmpty(pMarks)) {
//			pMarks = "0";
//		}
//		switch (view.getId()) {
//
//		case R.id.q1_a:
//
//			_fl_total_row_mark = Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1d
//							.getText().toString().trim()))) ? "0" : mark);
//
//			return _fl_total_row_mark;
//
//		case R.id.q1_b:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark1a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q1_c:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark1a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//		case R.id.q1_d:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark1a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark1c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks);
//			return _fl_total_row_mark;
//
//		case R.id.q2_a:
//			_fl_total_row_mark = Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q2_b:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark2a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q2_c:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark2a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2d
//							.getText().toString().trim()))) ? "0" : mark);
//
//			return _fl_total_row_mark;
//		case R.id.q2_d:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark2a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark2c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks);
//
//			return _fl_total_row_mark;
//
//		case R.id.q3_a:
//			_fl_total_row_mark = Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q3_b:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark3a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q3_c:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark3a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q3_d:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark3a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark3c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks);
//			return _fl_total_row_mark;
//
//		case R.id.q4_a:
//			_fl_total_row_mark = Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q4_b:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark4a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q4_c:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark4a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q4_d:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark4a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark4c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks);
//			return _fl_total_row_mark;
//
//		case R.id.q5_a:
//			_fl_total_row_mark = Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q5_b:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark5a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q5_c:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark5a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q5_d:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark5a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark5c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks);
//			return _fl_total_row_mark;
//
//		case R.id.q6_a:
//			_fl_total_row_mark = Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q6_b:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark6a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q6_c:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark6a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q6_d:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark6a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark6c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks);
//			return _fl_total_row_mark;
//
//		case R.id.q7_a:
//			_fl_total_row_mark = Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q7_b:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark7a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q7_c:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark7a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q7_d:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark7a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark7c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks);
//			return _fl_total_row_mark;
//
//		case R.id.q8_a:
//			_fl_total_row_mark = Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q8_b:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark8a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q8_c:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark8a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8d
//							.getText().toString().trim()))) ? "0" : mark);
//			return _fl_total_row_mark;
//
//		case R.id.q8_d:
//			_fl_total_row_mark = Float
//					.parseFloat(((TextUtils.isEmpty(mark = et_mark8a.getText()
//							.toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8b
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(((TextUtils.isEmpty(mark = et_mark8c
//							.getText().toString().trim()))) ? "0" : mark)
//					+ Float.parseFloat(pMarks);
//			return _fl_total_row_mark;
//
//		default:
//			return 0;
//		}
//	}
//
//	// when clicked on delete button call this method
//	private void deleteCharAndSetSelection(EditText edittext) {
//		if (!TextUtils.isEmpty(edittext.getText().toString())) {
//			edittext.setText(edittext.getText().toString()
//					.substring(0, (edittext.getText().toString().length() - 1)));
//			edittext.setSelection(edittext.getText().toString().length());
//		}
//	}
//
//	// calculate row 1 total
//	private String row1Total_() {
//		String mark = null;
//		String mark1a = et_mark1a.getText().toString().trim();
//		if (mark1a.equals("null")) {
//			mark1a = null;
//		}
//		String mark1b = et_mark1b.getText().toString().trim();
//		if (mark1b.equals("null")) {
//			mark1b = null;
//		}
//		String mark1c = et_mark1c.getText().toString().trim();
//		if (mark1c.equals("null")) {
//			mark1c = null;
//		}
//		String mark1d = et_mark1d.getText().toString().trim();
//		if (mark1d.equals("null")) {
//			mark1d = null;
//		}
//
//		if ((!TextUtils.isEmpty(mark1a) || !TextUtils.isEmpty(mark1b)
//				|| !TextUtils.isEmpty(mark1c) || !TextUtils.isEmpty(mark1d))) {
//			mark = String
//					.valueOf(Float.parseFloat(((TextUtils
//							.isEmpty(mark = mark1a))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark1b))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark1c))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark1d))) ? "0" : mark));
//		}
//
//		return mark;
//	}
//
//	// calculate row 2 total
//	private String row2Total_() {
//		String mark = null;
//		String mark2a = et_mark2a.getText().toString().trim();
//		if (mark2a.equals("null")) {
//			mark2a = null;
//		}
//		String mark2b = et_mark2b.getText().toString().trim();
//		if (mark2b.equals("null")) {
//			mark2b = null;
//		}
//		String mark2c = et_mark2c.getText().toString().trim();
//		if (mark2c.equals("null")) {
//			mark2c = null;
//		}
//		String mark2d = et_mark2d.getText().toString().trim();
//		if (mark2d.equals("null")) {
//			mark2d = null;
//		}
//
//		if (!TextUtils.isEmpty(mark2a) || !TextUtils.isEmpty(mark2b)
//				|| !TextUtils.isEmpty(mark2c) || !TextUtils.isEmpty(mark2d)) {
//			mark = String
//					.valueOf(Float.parseFloat(((TextUtils
//							.isEmpty(mark = mark2a))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark2b))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark2c))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark2d))) ? "0" : mark));
//		}
//		return mark;
//	}
//
//	// calculate row 3 total
//	private String row3Total_() {
//		String mark = null;
//		String mark3a = et_mark3a.getText().toString().trim();
//		if (mark3a.equals("null")) {
//			mark3a = null;
//		}
//		String mark3b = et_mark3b.getText().toString().trim();
//		if (mark3b.equals("null")) {
//			mark3b = null;
//		}
//		String mark3c = et_mark3c.getText().toString().trim();
//		if (mark3c.equals("null")) {
//			mark3c = null;
//		}
//		String mark3d = et_mark3d.getText().toString().trim();
//		if (mark3d.equals("null")) {
//			mark3d = null;
//		}
//
//		if (!TextUtils.isEmpty(mark3a) || !TextUtils.isEmpty(mark3b)
//				|| !TextUtils.isEmpty(mark3c) || !TextUtils.isEmpty(mark3d)) {
//			mark = String
//					.valueOf(Float.parseFloat(((TextUtils
//							.isEmpty(mark = mark3a))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark3b))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark3c))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark3d))) ? "0" : mark));
//		}
//		return mark;
//	}
//
//	// calculate row 4 total
//	private String row4Total_() {
//		String mark = null;
//		String mark4a = et_mark4a.getText().toString().trim();
//		if (mark4a.equals("null")) {
//			mark4a = null;
//		}
//		String mark4b = et_mark4b.getText().toString().trim();
//		if (mark4b.equals("null")) {
//			mark4b = null;
//		}
//		String mark4c = et_mark4c.getText().toString().trim();
//		if (mark4c.equals("null")) {
//			mark4c = null;
//		}
//		String mark4d = et_mark4d.getText().toString().trim();
//		if (mark4d.equals("null")) {
//			mark4d = null;
//		}
//
//		if (!TextUtils.isEmpty(mark4a) || !TextUtils.isEmpty(mark4b)
//				|| !TextUtils.isEmpty(mark4c) || !TextUtils.isEmpty(mark4d)) {
//			mark = String
//					.valueOf(Float.parseFloat(((TextUtils
//							.isEmpty(mark = mark4a))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark4b))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark4c))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark4d))) ? "0" : mark));
//		}
//		return mark;
//	}
//
//	// calculate row 5 total
//	private String row5Total_() {
//		String mark = null;
//		String mark5a = et_mark5a.getText().toString().trim();
//		if (mark5a.equals("null")) {
//			mark5a = null;
//		}
//		String mark5b = et_mark5b.getText().toString().trim();
//		if (mark5b.equals("null")) {
//			mark5b = null;
//		}
//		String mark5c = et_mark5c.getText().toString().trim();
//		if (mark5c.equals("null")) {
//			mark5c = null;
//		}
//		String mark5d = et_mark5d.getText().toString().trim();
//		if (mark5d.equals("null")) {
//			mark5d = null;
//		}
//
//		if (!TextUtils.isEmpty(mark5a) || !TextUtils.isEmpty(mark5b)
//				|| !TextUtils.isEmpty(mark5c) || !TextUtils.isEmpty(mark5d)) {
//			mark = String
//					.valueOf(Float.parseFloat(((TextUtils
//							.isEmpty(mark = mark5a))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark5b))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark5c))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark5d))) ? "0" : mark));
//		}
//		return mark;
//	}
//
//	// calculate row 6 total
//	private String row6Total_() {
//		String mark = null;
//		String mark6a = et_mark6a.getText().toString().trim();
//		if (mark6a.equals("null")) {
//			mark6a = null;
//		}
//		String mark6b = et_mark6b.getText().toString().trim();
//		if (mark6b.equals("null")) {
//			mark6b = null;
//		}
//		String mark6c = et_mark6c.getText().toString().trim();
//		if (mark6c.equals("null")) {
//			mark6c = null;
//		}
//		String mark6d = et_mark6d.getText().toString().trim();
//		if (mark6d.equals("null")) {
//			mark6d = null;
//		}
//
//		if (!TextUtils.isEmpty(mark6a) || !TextUtils.isEmpty(mark6b)
//				|| !TextUtils.isEmpty(mark6c) || !TextUtils.isEmpty(mark6d)) {
//			mark = String
//					.valueOf(Float.parseFloat(((TextUtils
//							.isEmpty(mark = mark6a))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark6b))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark6c))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark6d))) ? "0" : mark));
//		}
//		return mark;
//	}
//
//	// calculate row 7 total
//	private String row7Total_() {
//		String mark = null;
//		String mark7a = et_mark7a.getText().toString().trim();
//		if (mark7a.equals("null")) {
//			mark7a = null;
//		}
//		String mark7b = et_mark7b.getText().toString().trim();
//		if (mark7b.equals("null")) {
//			mark7b = null;
//		}
//		String mark7c = et_mark7c.getText().toString().trim();
//		if (mark7c.equals("null")) {
//			mark7c = null;
//		}
//		String mark7d = et_mark7d.getText().toString().trim();
//		if (mark7d.equals("null")) {
//			mark7d = null;
//		}
//
//		if (!TextUtils.isEmpty(mark7a) || !TextUtils.isEmpty(mark7b)
//				|| !TextUtils.isEmpty(mark7c) || !TextUtils.isEmpty(mark7d)) {
//			mark = String
//					.valueOf(Float.parseFloat(((TextUtils
//							.isEmpty(mark = mark7a))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark7b))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark7c))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark7d))) ? "0" : mark));
//		}
//		return mark;
//	}
//
//	// calculate row 8 total
//	private String row8Total_() {
//		String mark = null;
//		String mark8a = et_mark8a.getText().toString().trim();
//		if (mark8a.equals("null")) {
//			mark8a = null;
//		}
//		String mark8b = et_mark8b.getText().toString().trim();
//		if (mark8b.equals("null")) {
//			mark8b = null;
//		}
//		String mark8c = et_mark8c.getText().toString().trim();
//		if (mark8c.equals("null")) {
//			mark8c = null;
//		}
//		String mark8d = et_mark8d.getText().toString().trim();
//		if (mark8d.equals("null")) {
//			mark8d = null;
//		}
//
//		if (!TextUtils.isEmpty(mark8a) || !TextUtils.isEmpty(mark8b)
//				|| !TextUtils.isEmpty(mark8c) || !TextUtils.isEmpty(mark8d)) {
//			mark = String
//					.valueOf(Float.parseFloat(((TextUtils
//							.isEmpty(mark = mark8a))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark8b))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark8c))) ? "0" : mark)
//							+ Float.parseFloat(((TextUtils
//									.isEmpty(mark = mark8d))) ? "0" : mark));
//		}
//		return mark;
//	}
//
//	private void calculateTotal() {
//		String total;
//
//		total = row1Total_();
//		et_mark1_total.setText(total);
//
//		total = row2Total_();
//		et_mark2_total.setText(total);
//
//		total = row3Total_();
//		et_mark3_total.setText(total);
//
//		total = row4Total_();
//		et_mark4_total.setText(total);
//
//		total = row5Total_();
//		et_mark5_total.setText(total);
//
//		total = row6Total_();
//		et_mark6_total.setText(total);
//
//		total = row7Total_();
//		et_mark7_total.setText(total);
//
//		total = row8Total_();
//		et_mark8_total.setText(total);
//	}
//
//	private void calculateGrandTotal() {
//		String mark;
//		ArrayList<Float> listTotalMarks = new ArrayList<Float>();
//
//		mark = et_mark1_total.getText().toString().trim();
//		if (!TextUtils.isEmpty(mark)) {
//			listTotalMarks.add(Float.valueOf(mark));
//		}
//
//		mark = et_mark2_total.getText().toString().trim();
//		if (!TextUtils.isEmpty(mark)) {
//			listTotalMarks.add(Float.valueOf(mark));
//		}
//
//		mark = et_mark3_total.getText().toString().trim();
//		if (!TextUtils.isEmpty(mark)) {
//			listTotalMarks.add(Float.valueOf(mark));
//		}
//
//		mark = et_mark4_total.getText().toString().trim();
//		if (!TextUtils.isEmpty(mark)) {
//			listTotalMarks.add(Float.valueOf(mark));
//		}
//
//		mark = et_mark5_total.getText().toString().trim();
//		if (!TextUtils.isEmpty(mark)) {
//			listTotalMarks.add(Float.valueOf(mark));
//		}
//
//		mark = et_mark6_total.getText().toString().trim();
//		if (!TextUtils.isEmpty(mark)) {
//			listTotalMarks.add(Float.valueOf(mark));
//		}
//
//		mark = et_mark7_total.getText().toString().trim();
//		if (!TextUtils.isEmpty(mark)) {
//			listTotalMarks.add(Float.valueOf(mark));
//		}
//
//		mark = et_mark8_total.getText().toString().trim();
//		if (!TextUtils.isEmpty(mark)) {
//			listTotalMarks.add(Float.valueOf(mark));
//		}
//
//		BigDecimal roundOffGrandTotal = null;
//		if (!listTotalMarks.isEmpty()) {
//			float GrandTotal = 0;
//			Collections.sort(listTotalMarks, Collections.reverseOrder());
//			if (listTotalMarks.size() < 6) {
//				for (int i = 0; i < listTotalMarks.size(); i++) {
//					GrandTotal += listTotalMarks.get(i);
//				}
//				roundOffGrandTotal = new BigDecimal(Double.toString(GrandTotal));
//				roundOffGrandTotal = roundOffGrandTotal.setScale(0,
//						BigDecimal.ROUND_HALF_UP);
//				et_grand_toal.setText(String.valueOf(roundOffGrandTotal));
//			} else {
//				for (int i = 0; i < 5; i++) {
//					GrandTotal += listTotalMarks.get(i);
//				}
//				roundOffGrandTotal = new BigDecimal(Double.toString(GrandTotal));
//				roundOffGrandTotal = roundOffGrandTotal.setScale(0,
//						BigDecimal.ROUND_HALF_UP);
//				et_grand_toal.setText(String.valueOf(roundOffGrandTotal));
//			}
//		} else {
//			et_grand_toal.setText("0");
//		}
//	}
//
//	@Override
//	public void afterTextChanged(Editable s) {
//		// String _enteredChar = s.toString();
//		// if (!TextUtils.isEmpty(_enteredChar) && !(_enteredChar.length() > 1))
//		// {
//		// if (".".equals(_enteredChar.substring(_enteredChar.length() - 1))) {
//		// return;
//		// }
//		// }
//
//		// if (!TextUtils.isEmpty(_enteredChar) && _enteredChar.equals(".")) {
//		// return;
//		// }
//		calculateTotal();
//		calculateGrandTotal();
//	}
//
//	@Override
//	public void beforeTextChanged(CharSequence s, int start, int count,
//			int after) {
//	}
//
//	@Override
//	public void onTextChanged(CharSequence s, int start, int before, int count) {
//	}
//
//	private boolean FloatOrIntegerOnlyAllow(String inputvalue) {
//
//		switch (12) {
//		case 12:
//			return Pattern
//					.matches(
//							"[0-1][0-1](\\.(0|5|50|00))|[0-1][0-2]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
//							inputvalue);
//
//		case 14:
//			return Pattern
//					.matches(
//							"[0-1][0-3](\\.(0|5|50|00))|[0-1][0-4]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
//							inputvalue);
//		case 15:
//			return Pattern
//					.matches(
//							"[0-1][0-4](\\.(0|5|50|00))|[0-1][0-5]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
//							inputvalue);
//
//		case 16:
//			return Pattern
//					.matches(
//							"[0-1][0-5](\\.(0|5|50|00))|[0-1][0-6]|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
//							inputvalue);
//
//		case 45:
//			return Pattern
//					.matches(
//							"[0-4][0-4](\\.(0|5|50|00))|[0-4][0-9]|[0-4][0-9](\\.(0|5|50|00))|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
//							inputvalue);
//
//		case 48:
//			return Pattern
//					.matches(
//							"[0-4][0-7](\\.(0|5|50|00))|[0-4][0-9]|[0-4][0-9](\\.(0|5|50|00))|[0-9](\\.(0|5|50|00))|[0-9]|(.(5|50))",
//							inputvalue);
//
//		default:
//			return true;
//		}
//	}
//
//	// change row layout to black
//	private void changeRowToBlack(EditText et1, EditText et2, EditText et3,
//			EditText et4, TextView tv, TextView et_q, int con) {
//		et1.setBackgroundResource(R.drawable.black_with_border);
//		et1.setTextColor(getResources().getColor(R.color.white));
//		et1.setTypeface(Typeface.DEFAULT_BOLD);
//
//		et2.setBackgroundResource(R.drawable.black_with_border);
//		et2.setTextColor(getResources().getColor(R.color.white));
//		et2.setTypeface(Typeface.DEFAULT_BOLD);
//
//		et3.setBackgroundResource(R.drawable.black_with_border);
//		et3.setTextColor(getResources().getColor(R.color.white));
//		et3.setTypeface(Typeface.DEFAULT_BOLD);
//
//		et4.setBackgroundResource(R.drawable.black_with_border);
//		et4.setTextColor(getResources().getColor(R.color.white));
//		et4.setTypeface(Typeface.DEFAULT_BOLD);
//
//		tv.setBackgroundResource(R.drawable.black_with_border);
//		tv.setTextColor(getResources().getColor(R.color.white));
//		tv.setTypeface(Typeface.DEFAULT_BOLD);
//
//		et_q.setBackgroundResource(R.drawable.black_with_border);
//		et_q.setTextColor(getResources().getColor(R.color.white));
//		et_q.setTypeface(Typeface.DEFAULT_BOLD);
//
//		if (con != 1) {
//			changeRowToWhite(et_mark1a, et_mark1b, et_mark1c, et_mark1d,
//					et_mark1_total, et_q1);
//		}
//
//		if (con != 2) {
//			changeRowToWhite(et_mark2a, et_mark2b, et_mark2c, et_mark2d,
//					et_mark2_total, et_q2);
//		}
//
//		if (con != 3) {
//			changeRowToWhite(et_mark3a, et_mark3b, et_mark3c, et_mark3d,
//					et_mark3_total, et_q3);
//		}
//
//		if (con != 4) {
//			changeRowToWhite(et_mark4a, et_mark4b, et_mark4c, et_mark4d,
//					et_mark4_total, et_q4);
//		}
//
//		if (con != 5) {
//			changeRowToWhite(et_mark5a, et_mark5b, et_mark5c, et_mark5d,
//					et_mark5_total, et_q5);
//		}
//
//		if (con != 6) {
//			changeRowToWhite(et_mark6a, et_mark6b, et_mark6c, et_mark6d,
//					et_mark6_total, et_q6);
//		}
//
//		if (con != 7) {
//			changeRowToWhite(et_mark7a, et_mark7b, et_mark7c, et_mark7d,
//					et_mark7_total, et_q7);
//		}
//
//		if (con != 8) {
//			changeRowToWhite(et_mark8a, et_mark8b, et_mark8c, et_mark8d,
//					et_mark8_total, et_q8);
//		}
//	}
//
//	// change row layout to white
//	private void changeRowToWhite(EditText et1, EditText et2, EditText et3,
//			EditText et4, TextView tv, TextView et_q) {
//		et1.setBackgroundResource(R.drawable.selector_marks_summary);
//		et1.setTextColor(getResources().getColor(R.color.black));
//		et1.setTypeface(Typeface.DEFAULT_BOLD);
//
//		et2.setBackgroundResource(R.drawable.selector_marks_summary);
//		et2.setTextColor(getResources().getColor(R.color.black));
//		et2.setTypeface(Typeface.DEFAULT_BOLD);
//
//		et3.setBackgroundResource(R.drawable.selector_marks_summary);
//		et3.setTextColor(getResources().getColor(R.color.black));
//		et3.setTypeface(Typeface.DEFAULT_BOLD);
//
//		et4.setBackgroundResource(R.drawable.selector_marks_summary);
//		et4.setTextColor(getResources().getColor(R.color.black));
//		et4.setTypeface(Typeface.DEFAULT_BOLD);
//
//		tv.setBackgroundResource(R.drawable.selector_marks_summary);
//		tv.setTextColor(getResources().getColor(R.color.black));
//		tv.setTypeface(Typeface.DEFAULT_BOLD);
//
//		et_q.setBackgroundResource(R.drawable.selector_marks_summary);
//		et_q.setTextColor(getResources().getColor(R.color.black));
//		et_q.setTypeface(Typeface.DEFAULT_BOLD);
//	}
//
//	private void setRequestFocus(EditText et) {
//		et.requestFocus();
//	}
//
//	@Override
//	public boolean onTouch(View arg0, MotionEvent arg1) {
//		// TODO Auto-generated method stub
//		switch (arg0.getId()) {
//		case R.id.q1_a:
//			setClickables(1);
//			changeRowToBlack(et_mark1a, et_mark1b, et_mark1c, et_mark1d,
//					et_mark1_total, et_q1, 1);
//			break;
//
//		case R.id.q1_b:
//			setClickables(1);
//			changeRowToBlack(et_mark1a, et_mark1b, et_mark1c, et_mark1d,
//					et_mark1_total, et_q1, 1);
//			break;
//
//		case R.id.q1_c:
//			setClickables(1);
//			changeRowToBlack(et_mark1a, et_mark1b, et_mark1c, et_mark1d,
//					et_mark1_total, et_q1, 1);
//			break;
//
//		case R.id.q1_d:
//			setClickables(1);
//			changeRowToBlack(et_mark1a, et_mark1b, et_mark1c, et_mark1d,
//					et_mark1_total, et_q1, 1);
//			break;
//
//		case R.id.q1:
//			setClickables(1);
//			setRequestFocus(et_mark1a);
//			changeRowToBlack(et_mark1a, et_mark1b, et_mark1c, et_mark1d,
//					et_mark1_total, et_q1, 1);
//			break;
//
//		case R.id.q1_total:
//			setClickables(1);
//			setRequestFocus(et_mark1a);
//			changeRowToBlack(et_mark1a, et_mark1b, et_mark1c, et_mark1d,
//					et_mark1_total, et_q1, 1);
//			break;
//
//		case R.id.q2_a:
//			setClickables(2);
//			changeRowToBlack(et_mark2a, et_mark2b, et_mark2c, et_mark2d,
//					et_mark2_total, et_q2, 2);
//			break;
//
//		case R.id.q2_b:
//			setClickables(2);
//			changeRowToBlack(et_mark2a, et_mark2b, et_mark2c, et_mark2d,
//					et_mark2_total, et_q2, 2);
//			break;
//
//		case R.id.q2_c:
//			setClickables(2);
//			changeRowToBlack(et_mark2a, et_mark2b, et_mark2c, et_mark2d,
//					et_mark2_total, et_q2, 2);
//			break;
//
//		case R.id.q2_d:
//			setClickables(2);
//			changeRowToBlack(et_mark2a, et_mark2b, et_mark2c, et_mark2d,
//					et_mark2_total, et_q2, 2);
//			break;
//
//		case R.id.q2:
//			setClickables(2);
//			setRequestFocus(et_mark2a);
//			changeRowToBlack(et_mark2a, et_mark2b, et_mark2c, et_mark2d,
//					et_mark2_total, et_q2, 2);
//			break;
//
//		case R.id.q2_total:
//			setClickables(2);
//			setRequestFocus(et_mark2a);
//			changeRowToBlack(et_mark2a, et_mark2b, et_mark2c, et_mark2d,
//					et_mark2_total, et_q2, 2);
//			break;
//
//		case R.id.q3_a:
//			setClickables(3);
//			changeRowToBlack(et_mark3a, et_mark3b, et_mark3c, et_mark3d,
//					et_mark3_total, et_q3, 3);
//			break;
//
//		case R.id.q3_b:
//			setClickables(3);
//			changeRowToBlack(et_mark3a, et_mark3b, et_mark3c, et_mark3d,
//					et_mark3_total, et_q3, 3);
//			break;
//
//		case R.id.q3_c:
//			setClickables(3);
//			changeRowToBlack(et_mark3a, et_mark3b, et_mark3c, et_mark3d,
//					et_mark3_total, et_q3, 3);
//			break;
//
//		case R.id.q3_d:
//			setClickables(3);
//			changeRowToBlack(et_mark3a, et_mark3b, et_mark3c, et_mark3d,
//					et_mark3_total, et_q3, 3);
//			break;
//
//		case R.id.q3:
//			setClickables(3);
//			setRequestFocus(et_mark3a);
//			changeRowToBlack(et_mark3a, et_mark3b, et_mark3c, et_mark3d,
//					et_mark3_total, et_q3, 3);
//			break;
//
//		case R.id.q3_total:
//			setClickables(3);
//			setRequestFocus(et_mark3a);
//			changeRowToBlack(et_mark3a, et_mark3b, et_mark3c, et_mark3d,
//					et_mark3_total, et_q3, 3);
//			break;
//
//		case R.id.q4_a:
//			setClickables(4);
//			changeRowToBlack(et_mark4a, et_mark4b, et_mark4c, et_mark4d,
//					et_mark4_total, et_q4, 4);
//			break;
//
//		case R.id.q4_b:
//			setClickables(4);
//			changeRowToBlack(et_mark4a, et_mark4b, et_mark4c, et_mark4d,
//					et_mark4_total, et_q4, 4);
//			break;
//
//		case R.id.q4_c:
//			setClickables(4);
//			changeRowToBlack(et_mark4a, et_mark4b, et_mark4c, et_mark4d,
//					et_mark4_total, et_q4, 4);
//			break;
//
//		case R.id.q4_d:
//			setClickables(4);
//			changeRowToBlack(et_mark4a, et_mark4b, et_mark4c, et_mark4d,
//					et_mark4_total, et_q4, 4);
//			break;
//
//		case R.id.q4:
//			setClickables(4);
//			setRequestFocus(et_mark4a);
//			changeRowToBlack(et_mark4a, et_mark4b, et_mark4c, et_mark4d,
//					et_mark4_total, et_q4, 4);
//			break;
//
//		case R.id.q4_total:
//			setClickables(4);
//			setRequestFocus(et_mark4a);
//			changeRowToBlack(et_mark4a, et_mark4b, et_mark4c, et_mark4d,
//					et_mark4_total, et_q4, 4);
//			break;
//
//		case R.id.q5_a:
//			setClickables(5);
//			changeRowToBlack(et_mark5a, et_mark5b, et_mark5c, et_mark5d,
//					et_mark5_total, et_q5, 5);
//			break;
//
//		case R.id.q5_b:
//			setClickables(5);
//			changeRowToBlack(et_mark5a, et_mark5b, et_mark5c, et_mark5d,
//					et_mark5_total, et_q5, 5);
//			break;
//
//		case R.id.q5_c:
//			setClickables(5);
//			changeRowToBlack(et_mark5a, et_mark5b, et_mark5c, et_mark5d,
//					et_mark5_total, et_q5, 5);
//			break;
//
//		case R.id.q5_d:
//			setClickables(5);
//			changeRowToBlack(et_mark5a, et_mark5b, et_mark5c, et_mark5d,
//					et_mark5_total, et_q5, 5);
//			break;
//
//		case R.id.q5:
//			setClickables(5);
//			setRequestFocus(et_mark5a);
//			changeRowToBlack(et_mark5a, et_mark5b, et_mark5c, et_mark5d,
//					et_mark5_total, et_q5, 5);
//			break;
//
//		case R.id.q5_total:
//			setClickables(5);
//			setRequestFocus(et_mark5a);
//			changeRowToBlack(et_mark5a, et_mark5b, et_mark5c, et_mark5d,
//					et_mark5_total, et_q5, 5);
//			break;
//
//		case R.id.q6_a:
//			setClickables(6);
//			changeRowToBlack(et_mark6a, et_mark6b, et_mark6c, et_mark6d,
//					et_mark6_total, et_q6, 6);
//			break;
//
//		case R.id.q6_b:
//			setClickables(6);
//			changeRowToBlack(et_mark6a, et_mark6b, et_mark6c, et_mark6d,
//					et_mark6_total, et_q6, 6);
//			break;
//
//		case R.id.q6_c:
//			setClickables(6);
//			changeRowToBlack(et_mark6a, et_mark6b, et_mark6c, et_mark6d,
//					et_mark6_total, et_q6, 6);
//			break;
//
//		case R.id.q6_d:
//			setClickables(6);
//			changeRowToBlack(et_mark6a, et_mark6b, et_mark6c, et_mark6d,
//					et_mark6_total, et_q6, 6);
//			break;
//
//		case R.id.q6:
//			setClickables(6);
//			setRequestFocus(et_mark6a);
//			changeRowToBlack(et_mark6a, et_mark6b, et_mark6c, et_mark6d,
//					et_mark6_total, et_q6, 6);
//			break;
//
//		case R.id.q6_total:
//			setClickables(6);
//			setRequestFocus(et_mark6a);
//			changeRowToBlack(et_mark6a, et_mark6b, et_mark6c, et_mark6d,
//					et_mark6_total, et_q6, 6);
//			break;
//
//		case R.id.q7_a:
//			setClickables(7);
//			changeRowToBlack(et_mark7a, et_mark7b, et_mark7c, et_mark7d,
//					et_mark7_total, et_q7, 7);
//			break;
//
//		case R.id.q7_b:
//			setClickables(7);
//			changeRowToBlack(et_mark7a, et_mark7b, et_mark7c, et_mark7d,
//					et_mark7_total, et_q7, 7);
//			break;
//
//		case R.id.q7_c:
//			setClickables(7);
//			changeRowToBlack(et_mark7a, et_mark7b, et_mark7c, et_mark7d,
//					et_mark7_total, et_q7, 7);
//			break;
//
//		case R.id.q7_d:
//			setClickables(7);
//			changeRowToBlack(et_mark7a, et_mark7b, et_mark7c, et_mark7d,
//					et_mark7_total, et_q7, 7);
//			break;
//
//		case R.id.q7:
//			setClickables(7);
//			setRequestFocus(et_mark7a);
//			changeRowToBlack(et_mark7a, et_mark7b, et_mark7c, et_mark7d,
//					et_mark7_total, et_q7, 7);
//			break;
//
//		case R.id.q7_total:
//			setClickables(7);
//			setRequestFocus(et_mark7a);
//			changeRowToBlack(et_mark7a, et_mark7b, et_mark7c, et_mark7d,
//					et_mark7_total, et_q7, 7);
//			break;
//
//		case R.id.q8_a:
//			setClickables(8);
//			changeRowToBlack(et_mark8a, et_mark8b, et_mark8c, et_mark8d,
//					et_mark8_total, et_q8, 8);
//			break;
//
//		case R.id.q8_b:
//			setClickables(8);
//			changeRowToBlack(et_mark8a, et_mark8b, et_mark8c, et_mark8d,
//					et_mark8_total, et_q8, 8);
//			break;
//
//		case R.id.q8_c:
//			setClickables(8);
//			changeRowToBlack(et_mark8a, et_mark8b, et_mark8c, et_mark8d,
//					et_mark8_total, et_q8, 8);
//			break;
//
//		case R.id.q8_d:
//			setClickables(8);
//			changeRowToBlack(et_mark8a, et_mark8b, et_mark8c, et_mark8d,
//					et_mark8_total, et_q8, 8);
//			break;
//
//		case R.id.q8:
//			setClickables(8);
//			setRequestFocus(et_mark8a);
//			changeRowToBlack(et_mark8a, et_mark8b, et_mark8c, et_mark8d,
//					et_mark8_total, et_q8, 8);
//			break;
//
//		case R.id.q8_total:
//			setClickables(8);
//			setRequestFocus(et_mark8a);
//			changeRowToBlack(et_mark8a, et_mark8b, et_mark8c, et_mark8d,
//					et_mark8_total, et_q8, 8);
//			break;
//
//		default:
//			break;
//		}
//		return false;
//	}
//
//	private void setClickables(int condition) {
//
//		if (condition == 1) {
//			et_mark1a.setFocusable(true);
//			et_mark1b.setFocusable(true);
//			et_mark1c.setFocusable(true);
//			et_mark1d.setFocusable(true);
//
//			et_mark1a.setFocusableInTouchMode(true);
//			et_mark1b.setFocusableInTouchMode(true);
//			et_mark1c.setFocusableInTouchMode(true);
//			et_mark1d.setFocusableInTouchMode(true);
//
//		} else {
//			et_mark1a.setFocusable(false);
//			et_mark1b.setFocusable(false);
//			et_mark1c.setFocusable(false);
//			et_mark1d.setFocusable(false);
//
//			// et_mark1a.setFocusableInTouchMode(false);
//			// et_mark1b.setFocusableInTouchMode(false);
//			// et_mark1c.setFocusableInTouchMode(false);
//			// et_mark1d.setFocusableInTouchMode(false);
//		}
//
//		if (condition == 2) {
//			et_mark2a.setFocusable(true);
//			et_mark2b.setFocusable(true);
//			et_mark2c.setFocusable(true);
//			et_mark2d.setFocusable(true);
//
//			et_mark2a.setFocusableInTouchMode(true);
//			et_mark2b.setFocusableInTouchMode(true);
//			et_mark2c.setFocusableInTouchMode(true);
//			et_mark2d.setFocusableInTouchMode(true);
//		} else {
//			et_mark2a.setFocusable(false);
//			et_mark2b.setFocusable(false);
//			et_mark2c.setFocusable(false);
//			et_mark2d.setFocusable(false);
//		}
//
//		if (condition == 3) {
//			et_mark3a.setFocusable(true);
//			et_mark3b.setFocusable(true);
//			et_mark3c.setFocusable(true);
//			et_mark3d.setFocusable(true);
//
//			et_mark3a.setFocusableInTouchMode(true);
//			et_mark3b.setFocusableInTouchMode(true);
//			et_mark3c.setFocusableInTouchMode(true);
//			et_mark3d.setFocusableInTouchMode(true);
//
//		} else {
//			et_mark3a.setFocusable(false);
//			et_mark3b.setFocusable(false);
//			et_mark3c.setFocusable(false);
//			et_mark3d.setFocusable(false);
//		}
//
//		if (condition == 4) {
//			et_mark4a.setFocusable(true);
//			et_mark4b.setFocusable(true);
//			et_mark4c.setFocusable(true);
//			et_mark4d.setFocusable(true);
//
//			et_mark4a.setFocusableInTouchMode(true);
//			et_mark4b.setFocusableInTouchMode(true);
//			et_mark4c.setFocusableInTouchMode(true);
//			et_mark4d.setFocusableInTouchMode(true);
//		} else {
//			et_mark4a.setFocusable(false);
//			et_mark4b.setFocusable(false);
//			et_mark4c.setFocusable(false);
//			et_mark4d.setFocusable(false);
//		}
//
//		if (condition == 5) {
//			et_mark5a.setFocusable(true);
//			et_mark5b.setFocusable(true);
//			et_mark5c.setFocusable(true);
//			et_mark5d.setFocusable(true);
//
//			et_mark5a.setFocusableInTouchMode(true);
//			et_mark5b.setFocusableInTouchMode(true);
//			et_mark5c.setFocusableInTouchMode(true);
//			et_mark5d.setFocusableInTouchMode(true);
//		} else {
//			et_mark5a.setFocusable(false);
//			et_mark5b.setFocusable(false);
//			et_mark5c.setFocusable(false);
//			et_mark5d.setFocusable(false);
//		}
//
//		if (condition == 6) {
//			et_mark6a.setFocusable(true);
//			et_mark6b.setFocusable(true);
//			et_mark6c.setFocusable(true);
//			et_mark6d.setFocusable(true);
//
//			et_mark6a.setFocusableInTouchMode(true);
//			et_mark6b.setFocusableInTouchMode(true);
//			et_mark6c.setFocusableInTouchMode(true);
//			et_mark6d.setFocusableInTouchMode(true);
//		} else {
//			et_mark6a.setFocusable(false);
//			et_mark6b.setFocusable(false);
//			et_mark6c.setFocusable(false);
//			et_mark6d.setFocusable(false);
//		}
//
//		if (condition == 7) {
//			et_mark7a.setFocusable(true);
//			et_mark7b.setFocusable(true);
//			et_mark7c.setFocusable(true);
//			et_mark7d.setFocusable(true);
//
//			et_mark7a.setFocusableInTouchMode(true);
//			et_mark7b.setFocusableInTouchMode(true);
//			et_mark7c.setFocusableInTouchMode(true);
//			et_mark7d.setFocusableInTouchMode(true);
//		} else {
//			et_mark7a.setFocusable(false);
//			et_mark7b.setFocusable(false);
//			et_mark7c.setFocusable(false);
//			et_mark7d.setFocusable(false);
//		}
//
//		if (condition == 8) {
//			et_mark8a.setFocusable(true);
//			et_mark8b.setFocusable(true);
//			et_mark8c.setFocusable(true);
//			et_mark8d.setFocusable(true);
//
//			et_mark8a.setFocusableInTouchMode(true);
//			et_mark8b.setFocusableInTouchMode(true);
//			et_mark8c.setFocusableInTouchMode(true);
//			et_mark8d.setFocusableInTouchMode(true);
//
//		} else {
//			et_mark8a.setFocusable(false);
//			et_mark8b.setFocusable(false);
//			et_mark8c.setFocusable(false);
//			et_mark8d.setFocusable(false);
//		}
//	}
//
//	public void showProgress(String msg) {
//		progressDialog = ProgressDialog.show(this, "", msg);
//		progressDialog.setCancelable(false);
//	}
//
//	public void hideProgress() {
//		if (progressDialog != null && progressDialog.isShowing()) {
//			progressDialog.dismiss();
//		}
//	}
//
//	private void setContentValuesOnFinalSubmission(View v) {
//		showProgress("Saving data ...");
//		v.post(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//
//				String mark;
//				ContentValues _contentValues = new ContentValues();
//				try {
//					// check whether text is empty if so set to null
//					_contentValues.put(
//							SEConstants.MARK1A,
//							((TextUtils.isEmpty(mark = et_mark1a.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK1B,
//							((TextUtils.isEmpty(mark = et_mark1b.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK1C,
//							((TextUtils.isEmpty(mark = et_mark1c.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK1D,
//							((TextUtils.isEmpty(mark = et_mark1d.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.MARK2A,
//							((TextUtils.isEmpty(mark = et_mark2a.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK2B,
//							((TextUtils.isEmpty(mark = et_mark2b.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK2C,
//							((TextUtils.isEmpty(mark = et_mark2c.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK2D,
//							((TextUtils.isEmpty(mark = et_mark2d.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.MARK3A,
//							((TextUtils.isEmpty(mark = et_mark3a.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK3B,
//							((TextUtils.isEmpty(mark = et_mark3b.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK3C,
//							((TextUtils.isEmpty(mark = et_mark3c.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK3D,
//							((TextUtils.isEmpty(mark = et_mark3d.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.MARK4A,
//							((TextUtils.isEmpty(mark = et_mark4a.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK4B,
//							((TextUtils.isEmpty(mark = et_mark4b.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK4C,
//							((TextUtils.isEmpty(mark = et_mark4c.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK4D,
//							((TextUtils.isEmpty(mark = et_mark4d.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.MARK5A,
//							((TextUtils.isEmpty(mark = et_mark5a.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK5B,
//							((TextUtils.isEmpty(mark = et_mark5b.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK5C,
//							((TextUtils.isEmpty(mark = et_mark5c.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK5D,
//							((TextUtils.isEmpty(mark = et_mark5d.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.MARK6A,
//							((TextUtils.isEmpty(mark = et_mark6a.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK6B,
//							((TextUtils.isEmpty(mark = et_mark6b.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK6C,
//							((TextUtils.isEmpty(mark = et_mark6c.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK6D,
//							((TextUtils.isEmpty(mark = et_mark6d.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.MARK7A,
//							((TextUtils.isEmpty(mark = et_mark7a.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK7B,
//							((TextUtils.isEmpty(mark = et_mark7b.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK7C,
//							((TextUtils.isEmpty(mark = et_mark7c.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK7D,
//							((TextUtils.isEmpty(mark = et_mark7d.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.MARK8A,
//							((TextUtils.isEmpty(mark = et_mark8a.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK8B,
//							((TextUtils.isEmpty(mark = et_mark8b.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK8C,
//							((TextUtils.isEmpty(mark = et_mark8c.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.MARK8D,
//							((TextUtils.isEmpty(mark = et_mark8d.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.R1_TOTAL,
//							((TextUtils.isEmpty(mark = et_mark1_total.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.R2_TOTAL,
//							((TextUtils.isEmpty(mark = et_mark2_total.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.R3_TOTAL,
//							((TextUtils.isEmpty(mark = et_mark3_total.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.R4_TOTAL,
//							((TextUtils.isEmpty(mark = et_mark4_total.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.R5_TOTAL,
//							((TextUtils.isEmpty(mark = et_mark5_total.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.R6_TOTAL,
//							((TextUtils.isEmpty(mark = et_mark6_total.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.R7_TOTAL,
//							((TextUtils.isEmpty(mark = et_mark7_total.getText()
//									.toString().trim()))) ? "null" : mark);
//					_contentValues.put(
//							SEConstants.R8_TOTAL,
//							((TextUtils.isEmpty(mark = et_mark8_total.getText()
//									.toString().trim()))) ? "null" : mark);
//
//					_contentValues.put(
//							SEConstants.GRAND_TOTAL_MARK,
//							((TextUtils.isEmpty(mark = et_grand_toal.getText()
//									.toString().trim()))) ? "0" : mark);
//
//					_contentValues.put(SEConstants.UPDATED_ON,
//							Utility.getPresentTime());
//
//					_contentValues.put(SEConstants.TABLET_IMEI,
//							Utility.getTabletIMEI(RC_MarksShowActivity.this));
//
//					// bundle serial number
//					_contentValues.put(SEConstants.BUNDLE_SERIAL_NO,
//							bundleSerialNo);
//
//					// bundle id
//					_contentValues.put("bundle_id", "0");
//
//					// question set id
//					_contentValues.put("question_setid", "0");
//
//					// bundle numbaer
//					_contentValues.put(SEConstants.BUNDLE_NO, bundleNo);
//
//					// subject code
//					_contentValues.put(SEConstants.SUBJECT_CODE, subjectCode);
//
//					// question set code
//					_contentValues.put("question_setcode", "null");
//
//					// user id
//					_contentValues.put(SEConstants.USER_ID, userId);
//
//					// enter on
//					try {
//						_contentValues.put(SEConstants.UPDATED_ON,
//								Utility.getPresentTime());
//					} catch (Exception e) {
//						_contentValues.put(SEConstants.UPDATED_ON, "");
//					}
//
//					// is updated server
//					_contentValues.put(SEConstants.IS_UPDATED_SERVER, "0");
//
//					// tablet imei
//					_contentValues.put(SEConstants.TABLET_IMEI,
//							Utility.getTabletIMEI(RC_MarksShowActivity.this));
//
//					// barcode status
//					_contentValues.put(SEConstants.BARCODE_STATUS, 1);
//
//					if (_contentValues.size() > 0) {
//						// update DB here
//						if (database.updateRow(
//								SEConstants.TABLE_RC_MARKS,
//								_contentValues,
//								SEConstants.ANS_BOOK_BARCODE
//										+ " = '"
//										+ getIntent().getStringExtra(
//												SEConstants.ANS_BOOK_BARCODE)
//										+ "'") > 0) {
//							navigateToScanActivtyScreen();
//						} else {
//							showAlert("Marks Not Update", "OK", null);
//						}
//					}
//
//					hideProgress();
//
//				} catch (Exception e) {
//					hideProgress();
//				}
//			}
//		});
//	}
//}
