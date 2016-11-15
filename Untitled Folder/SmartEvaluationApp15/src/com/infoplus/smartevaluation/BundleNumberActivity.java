package com.infoplus.smartevaluation;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;
import com.infoplus.smartevaluation.webservice.WebServiceUtility;

public class BundleNumberActivity extends Activity implements OnClickListener,
		OnTouchListener {

	int Bundle_no = 0, CurrentAnswerBook, programId, progressBarStatus = 0;;
	String UserId, SubjectId, SubjectCode, BundleNo, IMEI, responseFromService,
			programName, getRegulation, SeatNo;

	TextView batteryLevel;
	RelativeLayout getRelativeLayout;
	Button btnChangeSubCode, Scanbutton;
	EditText ScaneditText;
	View menuView;

	SharedPreferences preferences, getProgramPrefs;
	SharedPreferences.Editor max_total_edit;
	SharedPreferences.Editor editor_prg;
	ProgressDialog progressBar;

	PowerManager pm;
	WakeLock wl;
	KeyguardManager km;
	BroadcastReceiver batteryLevelReceiver;

	DBHelper sEvalDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = telephonyManager.getDeviceId();

		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "INFO");
		wl.acquire();

		setContentView(R.layout.bundle_entry);
		sEvalDatabase = DBHelper.getInstance(this);
		ScaneditText = (EditText) this
				.findViewById(R.id.editTextScanBundelNumber);
		getActionBar().hide();
		Bundle b = getIntent().getExtras();
		UserId = b.getString("UserId");
		preferences = getSharedPreferences(SEConstants.SHARED_PREF_MAX_TOTAL, 0);
		max_total_edit = preferences.edit();
		
		SharedPreferences pref = getSharedPreferences(SEConstants.SHARED_PREF_TIME_LIMIT, 0); 
		    Editor editor = pref.edit();
		editor.putInt(SEConstants.SHARED_PREF_TIME_LIMIT, 120000);   
		editor.commit();
		Log.v("time", "120000");
		
		((TextView) findViewById(R.id.txt_eval_id)).setText("ID:  " + UserId);

		getProgramPrefs = this.getSharedPreferences("program_details",
				MODE_WORLD_READABLE);
		programId = getProgramPrefs.getInt(SEConstants.SHARED_PREF_PROGRAM_ID,
				-1);  
		programName = getProgramPrefs.getString(
				SEConstants.SHARED_PREF_PROGRAM_NAME, "");  
		((TextView) findViewById(R.id.txt_programName)).setText(programName);
		Log.i("program id", "" + programId);
		SubjectId = b.getString("SubjectId");
		SubjectCode = b.getString("SubjectCode");
		SeatNo = b.getString("SeatNo");

		((TextView) findViewById(R.id.seatno)).setText(SeatNo);
		((TextView) findViewById(R.id.tv_sub_code)).setText(SubjectCode);
		btnChangeSubCode = (Button) findViewById(R.id.btn_change_sub_code);
		btnChangeSubCode.setVisibility(View.INVISIBLE);

		menuView = LayoutInflater.from(this).inflate(
				R.layout.layout_menu_bundle_entry, null);

		((TextView) menuView.findViewById(R.id.tv_unreadable_bundle))
				.setOnClickListener(this);
		((TextView) menuView.findViewById(R.id.tv_exit))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.rel_lay_menu_bundle_entry))
				.addView(menuView);

		batteryLevel = (TextView) this.findViewById(R.id.txt_batteryLevel);

		getRelativeLayout = (RelativeLayout) this.findViewById(R.id.Container);
		getRelativeLayout.setOnTouchListener(this);

		Button clearbutton = (Button) findViewById(R.id.Bundle_clear);
		clearbutton.setOnClickListener(this);

//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
//				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		ScaneditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
  
				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

					int flag = 1;
					if (ScaneditText != null) {
						Editable editable = ScaneditText.getText();
						scanBarcode(flag, v, editable);
					}

					return true;

				}

				return false;
			}

		});

		Scanbutton = (Button) findViewById(R.id.scanBundleNumberButton);
		Scanbutton.setOnClickListener(this);

	}

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			// Toast.makeText(this,"Home Button Clicked",Toast.LENGTH_LONG).show();
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

		int flag;

		switch (v.getId()) {

		case R.id.tv_exit:
			exit();

			break;

		case R.id.btn_change_sub_code:
			changeSubCode();
			break;

		case R.id.tv_unreadable_bundle:

			flag = 0;
			unreadableBundle(flag, v);
			// this.scanBarcode(flag, v);
			break;

		case R.id.scanBundleNumberButton:

			flag = 1;
			if (ScaneditText != null) {
				Editable editable = ScaneditText.getText();
				this.scanBarcode(flag, v, editable);
			}
			break;

		case R.id.Bundle_clear:
			ScaneditText.setText("");
			ScaneditText.setFocusable(true);
			ScaneditText.requestFocus();
			ScaneditText.setFocusableInTouchMode(true);
			break;

		case R.id.nextButton:
			boolean isEntered = true;
			Intent intent = new Intent(this, ScanActivity.class);

			EditText editText = (EditText) findViewById(R.id.numberOfBooksPerBundleEditText);

			if (editText != null) {
				Editable editable = editText.getText();
				if (editable != null) {
					String value = editable.toString().trim();

					if (value.length() > 0) {

						intent.putExtra("UserId", UserId);
						intent.putExtra("BundleNo", BundleNo);
						intent.putExtra("SubjectId", SubjectId);
						intent.putExtra("SubjectCode", SubjectCode);
						intent.putExtra("SeatNo", SEConstants.seatNo);
						intent.putExtra("CurrentAnswerBook", CurrentAnswerBook);
						intent.putExtra(
								"MaxAnswerBook",
								this.getIntent().getIntExtra("MaxAnswerBook",
										SEConstants.MAX_ANSWER_BOOK));

					} else {
						isEntered = false;
					}

				} else {
					isEntered = false;
				}
			} else {
				isEntered = false;
			}

			if (isEntered) {
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				// this.finish();
			} else {
				Toast.makeText(this, this.getString(R.string.enter_all),
						Toast.LENGTH_LONG).show();
			}

			break;

		default:
			break;
		}

	}

	public void scanBarcode(int flag, View v, Editable editable) {

		if (editable != null) {
			String value = editable.toString().trim();
			if((value.length() == 11) || (value.length() == 12)){
			if (value.matches("^[a-zA-Z0-9]*$")) {
				String tempBundleNo = value.toLowerCase().trim();
				String tempSubCode = SubjectCode.toLowerCase().trim();

				if (tempBundleNo.equalsIgnoreCase(tempSubCode)
						|| tempBundleNo.matches("[a-z]+")) {

					alertMsgBundle("The Bundle Number Entered is Incorrect! ",
							1);

				} else {
					tempBundleNo = tempBundleNo.substring(1,
							tempSubCode.length() + 1);
					if (tempBundleNo.equalsIgnoreCase(tempSubCode)) {
						// tempBundleNo.charAt(6);
						String sss = checkBundle(UserId);
						if (value.equalsIgnoreCase(sss)) {
							Intent intent = new Intent(this, ScanActivity.class);
							intent.putExtra("UserId", UserId);
							intent.putExtra("SubjectId", SubjectId);
							intent.putExtra("SubjectCode", SubjectCode);
							intent.putExtra("BundleNo", sss);
							intent.putExtra("SeatNo", SEConstants.seatNo);
							intent.putExtra(
									"CurrentAnswerBook",
									BundleNumberActivity.this
											.getIntent()
											.getIntExtra("CurrentAnswerBook", 1));
							intent.putExtra(
									"MaxAnswerBook",
									BundleNumberActivity.this
											.getIntent()
											.getIntExtra("MaxAnswerBook",
													SEConstants.MAX_ANSWER_BOOK));
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						} else {
							alertMsgBundle("Bundle is not Assigned", 1);
						}
					} else {
						alertMsgBundle("Bundle is not Assigned", 1);
					}
				}
			} else {
				alertMsgBundle("Invalid Bundle ", 3);

			}
			} else {
				alertMsgBundle("Invalid Bundle ", 3);

			}

		}

	}

	String checkBundle(String value) {
		String pname = "";
		String a[] = { "bundle_no" };
		Cursor _cursor = sEvalDatabase.getRow(SEConstants.TABLE_BUNDLE,
				"enter_by ='" + value + "'", a);
		if (_cursor != null) {
			if (_cursor.getCount() > 0) {
				pname = _cursor.getString(_cursor.getColumnIndex("bundle_no"));
			}
		}
		DataBaseUtility.closeCursor(_cursor);
		if (TextUtils.isEmpty(pname)) {
			pname = "";
		}
		return pname;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				// The Intents Fairy has delivered us some data!
				String contents = intent.getStringExtra("SCAN_RESULT");
				// String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan

				if (contents != null && contents.length() > 0) {
					// showView(contents.trim());
				}
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Toast.makeText(
						this,
						"Unable to scan the barcode, please try again or try manual entry",
						Toast.LENGTH_LONG).show();
				// findViewById(R.id.manual_entry).setEnabled(true);
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
  
	private void showView(String value, int flag) {
		Cursor cursor = null;
		try {

			cursor = sEvalDatabase
					.executeSelectSQLQuery("select COUNT(*) as Value from table_bundle where TRIM(UPPER(bundle_no)) =TRIM(UPPER('"
							+ value.trim() + "'))");

			if (cursor != null && cursor.getCount() > 0) {
				while (!(cursor.isAfterLast())) {
					Bundle_no = cursor.getInt(cursor.getColumnIndex("Value"));
					cursor.moveToNext();
				}

				if (Bundle_no == 0) {
					// values to be inserted in table_bundle_history
					try {
						ContentValues _contentValues = contentValues(value,
								flag, "table_bundle_history");
						// insert values into db
						long checkValue = sEvalDatabase.insertReords(
								"table_bundle_history", _contentValues);
						// clear content values
						if (checkValue != -1) {
							_contentValues.clear();
						} else {
							FileLog.logInfo(
									"BundleInsertion Failed in table_bundle_history",
									0);
						}
					} catch (Exception ex) {
						FileLog.logInfo(
								" Exception--->table_bundle_history : "
										+ "InsertingRecords --- BundleNumberActivity: showView():"
										+ ex.getMessage(), 0);
					}

					// values to be inserted in table_bundle
					try {
						ContentValues _contentValues = contentValues(value,
								flag, "table_bundle");
						long checkValue = sEvalDatabase.insertReords(
								"table_bundle", _contentValues);
						// clear content values
						if (checkValue != -1) {
							_contentValues.clear();

							Cursor _cursor = null;
							// get the next answersheet with bundle_serial_no+1
							try {
								_cursor = sEvalDatabase
										.executeSelectSQLQuery("select ifnull(max(tm.bundle_serial_no),0) as anser_serial_no "
												+ "from table_bundle as tb inner join table_marks"
												+ " as tm on tb.bundle_no = tm.bundle_no "
												+ "where TRIM(UPPER(tb.bundle_no)) =TRIM(UPPER('"
												+ value.trim()
												+ "'))"
												+ " and tb.subject_id =  "
												+ SubjectId
												+ " and tb.enter_by  = '"
												+ UserId + "'");
								if ((_cursor != null)
										&& (_cursor.getCount() > 0)) {
									while (_cursor.moveToNext()) {
										CurrentAnswerBook = Integer
												.parseInt(_cursor.getString(_cursor
														.getColumnIndex("anser_serial_no")));
										CurrentAnswerBook = CurrentAnswerBook + 1;
									}
									Intent intent = new Intent(this,
											GetScriptCountFromBundle.class);
									intent.putExtra("UserId", UserId);
									intent.putExtra("BundleNo", value.trim());
									intent.putExtra("SubjectId", SubjectId);
									intent.putExtra("SubjectCode", SubjectCode);
									intent.putExtra("SeatNo", SEConstants.seatNo);
									intent.putExtra("CurrentAnswerBook",
											CurrentAnswerBook);
									intent.putExtra(
											"MaxAnswerBook",
											this.getIntent()
													.getIntExtra(
															"MaxAnswerBook",
															SEConstants.MAX_ANSWER_BOOK));
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
								} else {
									FileLog.logInfo(
											"BundleNumberActivity: Cursor Null ---> while getting next AnswerBook ",
											0);
								}
							} catch (Exception ex) {
								FileLog.logInfo(
										"3. Exception--->Next AnswerBook"
												+ " --- BundleNumberActivity: showView():"
												+ ex.getMessage(), 0);
							} finally {
								DataBaseUtility.closeCursor(_cursor);
							}

						} else {
							FileLog.logInfo(
									"BundleInsertion Failed in table_bundle", 0);
						}
					} catch (Exception ex) {
						FileLog.logInfo(
								"2. Exception--->table_bundle :"
										+ " InsertingRecords --- BundleNumberActivity: showView():"
										+ ex.getMessage(), 0);
					}

				} else {
					alertMsgBundle("This Bundle Number Already Exists! ", 4);
				}

			}

			else {
				FileLog.logInfo(
						"Cursor Null while getting count for Bundle from table_bundle",
						0);
			}

		} catch (Exception e) {

			FileLog.logInfo(
					"1. Exception ---> BundleNumberActivity --- showView(): "
							+ e.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		if (v == getRelativeLayout) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(ScaneditText.getWindowToken(), 0);

			return true;
		}
		return false;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		wl.release(); // when the activiy pauses, we should realse the wakelock
		if (batteryLevelReceiver != null) {
			try {
				unregisterReceiver(batteryLevelReceiver);
				batteryLevelReceiver = null;
			} catch (IllegalArgumentException ILAE) {

			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		wl.acquire();// must call this!
		batteryLevel();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	private void changeSubCode() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				BundleNumberActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage("Do You Want to Change the Subject ?  ");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						Dialog.dismiss();
						Intent intent = new Intent(BundleNumberActivity.this,
								SubjectCodeActivity.class);
						intent.putExtra("UserId", UserId);
						intent.putExtra(
								"MaxAnswerBook",
								BundleNumberActivity.this.getIntent()
										.getIntExtra("MaxAnswerBook",
												SEConstants.MAX_ANSWER_BOOK));
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);

					}
				});

		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
		myAlertDialog.show();
	}

	private void exit() {
		AlertDialog.Builder exitAlertDialog = new AlertDialog.Builder(
				BundleNumberActivity.this);
		exitAlertDialog.setTitle("Smart Evaluation");
		exitAlertDialog.setCancelable(false);
		exitAlertDialog.setMessage("Do You Want to Quit the Evaluation ? ");
		exitAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		exitAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						ContentValues _contentValues = new ContentValues();
						try {

							_contentValues.put("logged_in_status", "0");
							int _count = sEvalDatabase
									.updateRow(SEConstants.TABLE_USER,
											_contentValues, SEConstants.USER_ID
													+ " = '" + UserId + "'");

							if (_count > 0) {

								Intent intent_screen1 = new Intent(
										BundleNumberActivity.this,
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
												+ " setting satus --->BundleNumberAvctivity: Exiting app ",
										0);
							}
						} catch (Exception ex) {
							FileLog.logInfo(
									"Exception ---> BundleNumberAvctivity: Exiting app "
											+ ex.toString(), 0);
						} finally {
							_contentValues.clear();
						}
						Dialog.dismiss();

					}
				});

		exitAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
		exitAlertDialog.show();
	}

	private void unreadableBundle(final int flag, final View v) {

		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage("Is Your Bundle Unreadable ? ");

		myAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface Dialog, int arg1) {

						LayoutInflater factory = LayoutInflater
								.from(BundleNumberActivity.this);
						final View textEntryView = factory.inflate(
								R.layout.alert_dialog_text_entry, null);

						new AlertDialog.Builder(BundleNumberActivity.this)
								.setIconAttribute(
										android.R.attr.alertDialogIcon)
								.setCancelable(false)
								.setTitle("Enter the Unreadable Bundle Code ")
								.setView(textEntryView)
								.setPositiveButton(R.string.alert_dialog_ok,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int whichButton) {

												if (flag == 0) {

												}

												EditText editText_bundle = (EditText) textEntryView
														.findViewById(R.id.tv_bundle);

												if (editText_bundle != null) {
													Editable editable = editText_bundle
															.getText();
													scanBarcode(flag, v,
															editable);

												}

											}

										})
								.setNegativeButton(
										R.string.alert_dialog_cancel,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int whichButton) {

											}
										}).create().show();

					}
				});

		myAlertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface Dialog, int arg1) {

					}
				});
		myAlertDialog.show();
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

	public void alertMessage(final String errorMsg,
			final String unreadableBundle, final int flag) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(errorMsg);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						// unreadableBundle
						if (flag == 0) {
							if (errorMsg.equalsIgnoreCase("Bundle verified")) {
								setMaxToatlForUnreadableBundle(
										unreadableBundle, flag);
							}

							else {

								ScaneditText.setText("");
								ScaneditText.setFocusable(true);
								ScaneditText.setFocusableInTouchMode(true);
								ScaneditText.requestFocus();
							}
						}
						// scannedBundle
						else if (flag == 1) {
							if (errorMsg.equalsIgnoreCase("Bundle verified")) {
								setMaxToatlForReadableBundle(unreadableBundle,
										flag);
							}

							else {

								ScaneditText.setText("");
								ScaneditText.setFocusable(true);
								ScaneditText.setFocusableInTouchMode(true);
								ScaneditText.requestFocus();
							}
						}

						else {
						}

						// do something when the OK button is clicked
						Dialog.dismiss();

					}
				});

		myAlertDialog.show();
	}

	public void aletMessageToVerify(final String unreadableBundle,
			final int flag) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				BundleNumberActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog
				.setMessage("Network Range is too low. Please select the alternative network");
		myAlertDialog.setCancelable(false);
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// if (flag == 0) {
						// setMaxToatlForUnreadableBundle(unreadableBundle,
						// flag);
						// } else if (flag == 1) {
						// setMaxToatlForReadableBundle(unreadableBundle, flag);
						// } else {
						//
						// }
						Dialog.dismiss();
					}
				});

		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface Dialog, int arg1) {
						Dialog.dismiss();
					}
				});
		myAlertDialog.show();
	}

	public void aletMessageToVerifyRegulation(final String getMsg,
			final int getStatus, final String unreadableBundle, final int flag) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				BundleNumberActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(getMsg);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						Dialog.dismiss();
						if (getStatus == 1) {
							editor_prg = getProgramPrefs.edit();
							editor_prg.putString(
									SEConstants.SHARED_PREF_REGULATION_NAME,
									getRegulation.toString().trim());
							editor_prg.commit();
							ContentValues _values = new ContentValues();
							_values.put(SEConstants.REGULATION, getRegulation);
							try {
								sEvalDatabase.updateRow(
										SEConstants.TABLE_DATE_CONFIGURATION,
										_values, "id=1");
							} catch (Exception ex) {
								FileLog.logInfo(
										"BundleNumberActivity-->RunInBackground()"
												+ ex.toString(), 0);
							}
							alertMessage("Bundle verified", unreadableBundle,
									flag);
						}
					}
				});

		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface Dialog, int arg1) {
						Dialog.dismiss();
					}
				});
		myAlertDialog.show();
	}

	public String webServiceforUnreadableBundle(final String pXmlString) {
		// super.onStart();
		// create a new thread

		try {

			progressBarStatus = 75;
			progressBar.setProgress(progressBarStatus);
			WebServiceUtility instanceWebServiceUtility = new WebServiceUtility();
			String response = instanceWebServiceUtility.webServiceForBundle(
					pXmlString).toString();
			if (response != null) {
				responseFromService = response;
			}

		}

		catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> WS_ERROR ---> BundleNumberActivity: webServiceforUnreadableBundle() - "
							+ ex.toString(), 0);
			responseFromService = "failedtoconnect";

		}

		return responseFromService;
	}

	public void setMaxToatlForUnreadableBundle(String unreadableBundle, int flag) {
		int max = 100;
		Cursor cursor = null;
		try {
			cursor = sEvalDatabase
					.executeSelectSQLQuery("select max(max_total) as m_total from table_regulation tr,"
							+ "table_program tp where tr.program_id = "
							+ programId + " and tp.program_id = " + programId);

			if ((cursor != null) && (cursor.getCount() > 0)) {
				while (!cursor.isAfterLast()) {
					max = cursor.getInt(cursor.getColumnIndex("m_total"));
					cursor.moveToNext();
				}
			} else {
				FileLog.logInfo(
						"Cursor Null ---> setMaxToatlForUnreadableBundle()", 0);
			}

		} catch (Exception ex) {
			FileLog.logInfo("Exception----> setMaxToatlForUnreadableBundle()"
					+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor);
		}

		max_total_edit.putInt(SEConstants.SHARED_PREF_MAX_TOTAL, max / 5);
		max_total_edit.commit();
		showView(unreadableBundle, flag);
	}

	public void setMaxToatlForReadableBundle(String value, int flag) {
		Cursor cursor = null;
		try {
			cursor = sEvalDatabase
					.executeSelectSQLQuery("select max_total from table_regulation where program_id = '"
							+ programId
							+ "' and bundle_indicator = '"
							+ value.charAt(6) + " '");
			if (cursor != null && cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					max_total_edit.putInt(SEConstants.SHARED_PREF_MAX_TOTAL,
							((Integer.parseInt(cursor.getString(cursor
									.getColumnIndex("max_total")))) / 5));
					max_total_edit.commit();
					cursor.moveToNext();
				}
			} else {
				FileLog.logInfo(
						"Failed to set MaxTotal with bundle_indicator ---> setMaxToatlForReadableBundle()",
						0);
				int max = 100;
				Cursor cursor_faultBundle = null;
				try {
					cursor_faultBundle = sEvalDatabase
							.executeSelectSQLQuery("select max(max_total) as m_total from table_regulation tr,"
									+ "table_program tp where tr.program_id = "
									+ programId
									+ " and tp.program_id = "
									+ programId);
					if (cursor_faultBundle != null
							&& cursor_faultBundle.getCount() > 0) {
						while (!cursor_faultBundle.isAfterLast()) {
							max = cursor_faultBundle.getInt(cursor_faultBundle
									.getColumnIndex("m_total"));
							cursor_faultBundle.moveToNext();

						}
						max_total_edit.putInt(
								SEConstants.SHARED_PREF_MAX_TOTAL, max / 5);
						max_total_edit.commit();
					} else {
						FileLog.logInfo(
								"Failed to set Default MaxTotal ---> setMaxToatlForReadableBundle()",
								0);
					}

				} catch (Exception ex) {
					FileLog.logInfo(
							"Exception -- Default MaxTotal---> setMaxToatlForReadableBundle()"
									+ ex.toString(), 0);
				} finally {
					DataBaseUtility.closeCursor(cursor_faultBundle);
				}
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception -- With BundleIndicator MaxTotal ---> setMaxToatlForReadableBundle()"
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor);
		}

		showView(value, flag);
	}

	private void RunInBackground(final String unreadableBundle, final int flag,
			final View v) {

		new AsyncTask<Void, Void, Void>() {

			String retrieveString;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				Log.i("in pre ", "entered");
				showProgress(v);
			}

			@Override
			protected Void doInBackground(Void... params) {
				Log.i("in do in ", "entered");

				// connectToService(unreadableBundle,flag);

				progressBarStatus = 25;
				progressBar.setProgress(progressBarStatus);

				StringBuffer strBuf;
				strBuf = new StringBuffer(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?>");

				strBuf.append("<UnreadableBundle>");

				strBuf.append("<UserId>");
				strBuf.append(UserId);
				strBuf.append("</UserId>");

				strBuf.append("<BundleNo>");
				strBuf.append(unreadableBundle);
				strBuf.append("</BundleNo>");

				strBuf.append("<IMEI>");
				strBuf.append(IMEI);
				strBuf.append("</IMEI>");

				strBuf.append("</UnreadableBundle>");

				progressBarStatus = 50;
				progressBar.setProgress(progressBarStatus);

				try {

					retrieveString = webServiceforUnreadableBundle(strBuf
							.toString());

				} catch (Exception e) {
					FileLog.logInfo(
							"WSError---> BundleNumberActivity" + e.toString(),
							0);
					retrieveString = "failedtoconnect";
					return null;
				}
				progressBarStatus = 100;
				progressBar.setProgress(progressBarStatus);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				Log.i("in on ", "entered");
				hideProgress();

				if (retrieveString.contains("4")) {
					getRegulation = retrieveString.replace("4-", "");

					// String segments[] = getRegulation;
					// Grab the last segment
					// getRegulation = segments[0];
					// String programName = segments[segments.length - 1];
					String regulationCheck = getRegulation.toString().trim()
							.substring(getRegulation.lastIndexOf("-") + 1);

					if (regulationCheck.length() != 0
							&& regulationCheck.toString().trim()
									.equalsIgnoreCase(programName)) {
						aletMessageToVerifyRegulation(
								"You are about to evaluate "
										+ getRegulation.toString().trim(), 1,
								unreadableBundle, flag);
					}

					else {
						aletMessageToVerifyRegulation(
								"Regulation Mismatches with Degree", 2,
								unreadableBundle, flag);
					}

				}

				else if (retrieveString.equalsIgnoreCase("failedtoconnect")) {
					aletMessageToVerify(unreadableBundle, flag);

				}

				else {

					alertMessage(retrieveString, unreadableBundle, flag);
					// return;
				}
			}

		}.execute();
	}

	public void checkAvailabilityOfNetwork(String unreadableBundle, int flag,
			View v) {

		// alertMessage("Bundle verified", unreadableBundle,
		// flag);
		// hh
		// Utility instanceUtility = new Utility();
		// if (instanceUtility.isNetworkAvailable(BundleNumberActivity.this) ==
		// true) {
		// Log.i("in if ", "entered");
		// RunInBackground(unreadableBundle, flag, v);
		//
		// } else {
		//
		// aletMessageToVerify(unreadableBundle, flag);
		// }

	}

	public ContentValues contentValues(String value, int flag, String table) {
		String apkVersion = "Version";
		ContentValues _contentValues = new ContentValues();
		if (table.equalsIgnoreCase("table_bundle")) {
			_contentValues.put("is_unreadable", flag);
			_contentValues.put("program_name", programName);
			_contentValues.put("bundle_no", value.trim());
			_contentValues.put("subject_id", SubjectId);
			_contentValues.put("subject_code", SubjectCode);
			_contentValues.put("status", 0);
			_contentValues.put("enter_by", UserId);
			try {
				_contentValues.put("enter_on", Utility.getPresentTime());
				apkVersion = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionName;
				_contentValues.put("apk_version", apkVersion);
			} catch (Exception e) {
				FileLog.logInfo("Error while fetching current time in Tablet "
						+ e.getMessage(), 0);
			}
			_contentValues.put("is_updated_server", "0");
			_contentValues.put("tablet_IMEI", IMEI);

		} else {

			_contentValues.put("is_deleted", 0);
			_contentValues.put("is_unreadable", flag);
			_contentValues.put("program_name", programName);
			_contentValues.put("bundle_no", value.trim());
			_contentValues.put("subject_id", SubjectId);
			_contentValues.put("subject_code", SubjectCode);
			_contentValues.put("status", 0);
			_contentValues.put("enter_by", UserId);
			try {
				_contentValues.put("enter_on", Utility.getPresentTime());
				apkVersion = getPackageManager().getPackageInfo(
						getPackageName(), 0).versionName;
				_contentValues.put("apk_version", apkVersion);
			} catch (Exception e) {
				FileLog.logInfo(e.getMessage(), 0);
			}
			_contentValues.put("is_updated_server", "0");
			_contentValues.put("tablet_IMEI", IMEI);

		}
		return _contentValues;

	}

	public void alertMsgBundle(String pMsgtype, final int ptype) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(pMsgtype);

		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						ScaneditText.setText("");
						ScaneditText.setFocusable(true);
						ScaneditText.requestFocus();
						ScaneditText.setFocusableInTouchMode(true);
						if (ptype == 2) {
							changeSubCode();
						}
						Dialog.dismiss();

					}
				});

		myAlertDialog.show();
	}

	public void showProgress(View v) {

		progressBar = new ProgressDialog(v.getContext());
		progressBar.setCancelable(false);
		progressBar.setMessage("Verifying Bundle ...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressBar.setProgress(0);
		progressBar.setMax(100);
		progressBar.show();

		progressBarStatus = 0;

	}

	public void hideProgress() {

		progressBar.dismiss();
	}

}
