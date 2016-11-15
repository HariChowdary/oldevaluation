package com.infoplus.smartevaluation;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;
import com.infoplus.smartevaluation.R;

public class SubjectCodeActivity extends Activity implements OnClickListener,
		OnTouchListener {

	int SubjectCount, programId;
	String SubjectId, programName, SubjectName, SubjectCode, UserId, subcode;
	MenuItem item1;
	Spinner spinReg;
	TextView batteryLevel;
	LinearLayout getLinearLayout;
	View view;
	Button Sub_Go;
	SharedPreferences getProgramPrefs;
	AutoCompleteTextView acTextView;
	EditText examIdText;
	BroadcastReceiver batteryLevelReceiver;

	DBHelper sEvalDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setTheme(android.R.style.Theme_Light);
		setContentView(R.layout.subjectcode);
		getActionBar().hide();
		sEvalDatabase = DBHelper.getInstance(this);
		getProgramPrefs = this.getSharedPreferences("program_details",
				MODE_WORLD_READABLE);

		programId = getProgramPrefs.getInt(SEConstants.SHARED_PREF_PROGRAM_ID,
				-1);
		programName = getProgramPrefs.getString(
				SEConstants.SHARED_PREF_PROGRAM_NAME, "");
		((TextView) findViewById(R.id.txt_programName)).setText(programName);
		((TextView) findViewById(R.id.tv_exit)).setOnClickListener(this);
		 spinReg = (Spinner) findViewById(R.id.reg_sipn);
		 spinReg.setAdapter(new ArrayAdapter<String>(this,
				R.layout.layout_spin_textview, getResources().getStringArray(R.array.regulation_array)));
	
		Bundle b = getIntent().getExtras();
		UserId = b.getString("UserId");

		((TextView) findViewById(R.id.txt_eval_id)).setText("ID:  " + UserId);

		getLinearLayout = (LinearLayout) this.findViewById(R.id.sublayout);
		getLinearLayout.setOnTouchListener(this);

		batteryLevel = (TextView) this.findViewById(R.id.txt_batteryLevel);

		Sub_Go = (Button) this.findViewById(R.id.subSubmit_button);
		Sub_Go.setOnClickListener(this);

		ArrayList<String> Subject = new ArrayList<String>();
		Cursor curtest = null;
		try {
			curtest = sEvalDatabase
					.executeSelectSQLQuery("select subject_code,subject_name from table_subject");
			if (curtest != null) {
				while (!(curtest.isAfterLast())) {
					Subject.add((curtest.getString(curtest
							.getColumnIndex("subject_code"))));
					curtest.moveToNext();
				}

			} else {
				FileLog.logInfo(
						"Cursor Null ---> SubjectCodeActivity: oncreate() ", 0);
			}
		} catch (Exception ex) {
			FileLog.logInfo("Exception ---> SubjectCodeActivity: oncreate() - "
					+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(curtest);
		}

		TextView txt = new TextView(this);
		txt.setTextColor(Color.BLACK);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, Subject);
		acTextView = (AutoCompleteTextView) findViewById(R.id.subcode_autotext);
		acTextView.setThreshold(1);
		acTextView.setAdapter(adapter);
		acTextView.setTextColor(Color.BLACK);

	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			Toast.makeText(this, "Home Button Clicked", Toast.LENGTH_LONG)
					.show();
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
		case R.id.subSubmit_button:
			enterSubjectCode();
			break;

		case R.id.tv_exit:
			exit();
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		if (v == getLinearLayout) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(acTextView.getWindowToken(), 0);

			return true;
		}
		return false;
	}

	void CheckforSubjectCode(String reg) {
		ContentValues _values = new ContentValues();
		_values.put(SEConstants.REGULATION, reg);
		try {
			sEvalDatabase.updateRow(SEConstants.TABLE_DATE_CONFIGURATION,
					_values, "id=1");
		} catch (Exception ex) {
			FileLog.logInfo(
					"BundleNumberActivity-->RunInBackground()"
							+ ex.toString(), 0);
		}
		Cursor cur1 = null;
		try {
			cur1 = sEvalDatabase
					.executeSelectSQLQuery("select subject_id,subject_name,subject_code from table_subject where TRIM(UPPER(subject_code)) =TRIM(UPPER('"
							+ subcode + "'))");
			if (cur1 != null) {
				while (!(cur1.isAfterLast())) {
					SubjectId = cur1.getString(cur1
							.getColumnIndex("subject_id"));
					SubjectName = cur1.getString(cur1
							.getColumnIndex("subject_name"));
					SubjectCode = cur1.getString(cur1
							.getColumnIndex("subject_code"));
					cur1.moveToNext();
				}

				Intent intent = new Intent(this, BundleNumberActivity.class);

				intent.putExtra("UserId", UserId);
				intent.putExtra("SubjectId", SubjectId);
				intent.putExtra("SubjectName", SubjectName);
				intent.putExtra("SubjectCode", SubjectCode);
				intent.putExtra("MaxAnswerBook",
						this.getIntent().getIntExtra("MaxAnswerBook", SEConstants.MAX_ANSWER_BOOK));
				intent.putExtra(SEConstants.SHARED_PREF_MAX_TOTAL, getIntent()
						.getStringExtra(SEConstants.SHARED_PREF_MAX_TOTAL));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			} else {
				FileLog.logInfo(
						"Cursor Null ---> SubjectCodeActivity: CheckforSubjectCode() ",
						0);
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> SubjectCodeActivity: CheckforSubjectCode() - "
							+ ex.toString(), 0);

		} finally {
			DataBaseUtility.closeCursor(cur1);
		}

	}

	private void enterSubjectCode() {
		subcode = acTextView.getText().toString().trim();
		final String reg=spinReg.getSelectedItem().toString();
		
		if (subcode.length() == 5) {
			if(!reg.equals("--Select--")){
			Cursor cur = null;
			try {
				cur = sEvalDatabase
						.executeSelectSQLQuery("select COUNT(*) as Value from table_subject where TRIM(UPPER(subject_code)) =TRIM(UPPER('"
								+ subcode + "'))");

				if (cur != null && cur.getCount() > 0) {
					while (!(cur.isAfterLast())) {
						SubjectCount = Integer.parseInt(cur.getString(cur
								.getColumnIndex("Value")));
						cur.moveToNext();
					}

					if (SubjectCount != 0) {
						CheckforSubjectCode(reg);

					} else {

						AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
								this);
						myAlertDialog.setTitle("Smart Evaluation");
						myAlertDialog.setCancelable(false);
						myAlertDialog
								.setMessage("This Subject Code is Not Available. Do You Want to Add? ");

						myAlertDialog.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface Dialog,
											int arg1) {

										TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
										String IMEI = telephonyManager
												.getDeviceId();
										ContentValues _contentValues = new ContentValues();
										_contentValues.put("subject_name",subcode);
										_contentValues.put("subject_code",subcode);
										_contentValues.put("user_id", UserId);
										_contentValues.put("tablet_IMEI", IMEI);
										try {
											long count = sEvalDatabase
													.insertReords(
															"table_subject",
															_contentValues);
											if (count > 0) {
												CheckforSubjectCode(reg);
											} else {
												FileLog.logInfo(
														"2. Cursor Null"
																+ " SubjectCodeActivity: enterSubjectCode()",
														0);
											}
										} catch (Exception ex) {
											FileLog.logInfo(
													"2. Exception --->"
															+ " SubjectCodeActivity: enterSubjectCode()"
															+ ex.toString(), 0);
										} finally {
											_contentValues.clear();
										}

										Dialog.dismiss();

									}
								});
						myAlertDialog.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										acTextView.setText("");
										acTextView.setFocusable(true);
										acTextView.requestFocus();
										acTextView
												.setFocusableInTouchMode(true);
									}
								});
						myAlertDialog.show();

					}
				}

				else {
					FileLog.logInfo(
							"1. Cursor Null ---> SubjectCodeActivity: enterSubjectCode() - ",
							0);
				}
			} catch (Exception ex) {
				FileLog.logInfo(
						"1. Exception ---> SubjectCodeActivity: enterSubjectCode() - ",
						0);
			} finally {
				DataBaseUtility.closeCursor(cur);
			}
			}
			else {

				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
				myAlertDialog.setTitle("Smart Evaluation");
				myAlertDialog.setCancelable(false);
				myAlertDialog.setMessage("Select Valid Regulation");
				
				myAlertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface Dialog, int arg1) {

								Dialog.dismiss();
								acTextView.setText("");
								acTextView.setFocusable(true);
								acTextView.requestFocus();
								acTextView.setFocusableInTouchMode(true);
							}
						});
				myAlertDialog.show();

			}
		}

		else {

			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
			myAlertDialog.setTitle("Smart Evaluation");
			myAlertDialog.setCancelable(false);
			myAlertDialog.setMessage("This Subject Code is Invalid! ");

			myAlertDialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface Dialog, int arg1) {

							Dialog.dismiss();
							acTextView.setText("");
							acTextView.setFocusable(true);
							acTextView.requestFocus();
							acTextView.setFocusableInTouchMode(true);
						}
					});
			myAlertDialog.show();

		}

	}

	private void exit() {
		AlertDialog.Builder saveAlertDialog = new AlertDialog.Builder(
				SubjectCodeActivity.this);
		saveAlertDialog.setTitle("Smart Evaluation");
		saveAlertDialog.setCancelable(false);
		saveAlertDialog.setMessage("Do You Want to Quit from the Evaluation? ");
		saveAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		saveAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						// do something when the OK button is clicked

						ContentValues _contentValues = new ContentValues();
						try {

							_contentValues.put("logged_in_status", "0");
							int _count = sEvalDatabase
									.updateRow(SEConstants.TABLE_USER,
											_contentValues, SEConstants.USER_ID
													+ " = '" + UserId + "'");

							if (_count > 0) {

								Intent intent_screen1 = new Intent(
										SubjectCodeActivity.this,
										EvaluatorEntryActivity.class);
								intent_screen1
										.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent_screen1);

								Intent intent1 = new Intent(Intent.ACTION_MAIN);
								intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent1.addCategory(Intent.CATEGORY_HOME);
								startActivity(intent1);
							} else {
								FileLog.logInfo(
										"updation failed with table_user while"
												+ " setting satus --->SubjectCodeActivity: exit() ",
										0);
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> SubjectCodeActivity: exit() "
											+ ex.toString(), 0);
						} finally {
							_contentValues.clear();
						}
						Dialog.dismiss();

					}
				});

		saveAlertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
					}
				});
		saveAlertDialog.show();
	}

	private void batteryLevel() {
		batteryLevelReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				context.unregisterReceiver(this);
				int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,
						-1);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				int level = -1;
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}
				batteryLevel.setText("Battery Level : " + level + "%");
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		batteryLevel();
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

}
