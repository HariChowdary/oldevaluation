package com.infoplustech.smartscrutinization;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.HiddenPassTransformationMethod;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_SeriallyScanAnswerSheet extends Activity implements
		OnClickListener {

	EditText scannedBook, bookSno;
	SharedPreferences preferences;
	SharedPreferences.Editor max_total_edit;
	String subjectCode;
	boolean scrutinySelection;  
	String bundleNo, userId;
	int reevaluateCount;
	String bundle_serial_no, SeatNo;
	private final String CONDITION1_FOR_DELETE = "Delete_Barcode&SL_No_Marks";
	boolean fromAddScript;
	Utility instanceUtitlity;
	private PowerManager.WakeLock wl;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scrutiny_scan_answerbook);
		getActionBar().hide();
		Log.v("activity", "Scrutiny_SeriallyScanAnswerSheet");
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");
		instanceUtitlity = new Utility();  
		((TextView) findViewById(R.id.tv_back)).setVisibility(View.INVISIBLE);
		((TextView) findViewById(R.id.tv_back)).setOnClickListener(this);
		Button btnSubmit = (Button) findViewById(R.id.btn_scanbook_submit);
		btnSubmit.setOnClickListener(this);
		scannedBook = (EditText) findViewById(R.id.et_scanbook);
		bookSno = (EditText) findViewById(R.id.WriteSerialNumberTextView1);
		reevaluateCount = 0;
		scannedBook.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(scannedBook.getWindowToken(), 0);
				}  
			}
		});
		final Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO)
				.toUpperCase();
		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		bundle_serial_no = intent_extras
				.getStringExtra(SSConstants.BUNDLE_SERIAL_NO);
		subjectCode = intent_extras.getStringExtra(SSConstants.SUBJECT_CODE);
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);

		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor_scripts_count = _database.passedQuery(
				SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO + " = '"
						+ bundleNo + "'", null);
		int scriptsCount = _cursor_scripts_count.getCount();
		TextView tv_unreadable = (TextView) findViewById(R.id.tv_unread_barcode);
		tv_unreadable.setOnClickListener(this);
		_cursor_scripts_count.close();
		if (intent_extras.hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
			fromAddScript = true;
		} else { 
			fromAddScript = false;
		}

		if (!fromAddScript) {
			Cursor cursor_unreadable = _database.getRowBarcodeStatusBySerialNo(
					bundle_serial_no, bundleNo,
					new String[] { SSConstants.BARCODE_STATUS });
			if (cursor_unreadable != null && cursor_unreadable.getCount() > 0) {
				tv_unreadable.setVisibility(View.VISIBLE);
				tv_unreadable.setOnClickListener(this);
				scannedBook.setEnabled(false);
				btnSubmit.setEnabled(false);
			} else {
				// getWindow().setFlags(
				// WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
				// WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

				tv_unreadable.setVisibility(View.GONE);
			}
			if (cursor_unreadable != null) {
				cursor_unreadable.close();
			}
		}

		if (scriptsCount >= Integer.valueOf(bundle_serial_no)) {
			((TextView) findViewById(R.id.tv_smart_scrutiny))
					.setText(getString(R.string.app_name) + " "
							+ bundle_serial_no + " of " + scriptsCount);
		} else {
			((TextView) findViewById(R.id.tv_smart_scrutiny))
					.setText(getString(R.string.app_name) + " "
							+ bundle_serial_no + " of " + bundle_serial_no);
		}

		SharedPreferences sharedPreferences = getSharedPreferences(
				SSConstants.SCRUTINY_SELECTED, Context.MODE_PRIVATE);
		scrutinySelection = sharedPreferences.getBoolean(
				SSConstants.SCRUTINY_SELECTED, true);

		preferences = getSharedPreferences(SSConstants.SHARED_PREF_MAX_TOTAL, 0);
		max_total_edit = preferences.edit();
		scannedBook
				.setTransformationMethod(new HiddenPassTransformationMethod());
		scannedBook.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (intent_extras.hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
						addScriptCase(scannedBook.getText().toString().trim(),
								false);
					} else {
						checkBarcodeInDB();
					}
					return true;
				}
				return false;
			}
		});  
	}  

	// navigate to MarkDialog screen
	private void navigateToMarkDialogScreen(String barcode, String _total_marks) {

		if (instanceUtitlity
				.isRegulation_R13_Mtech(Scrutiny_SeriallyScanAnswerSheet.this)) {
			if (scrutinySelection)
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to scrutiny R13-M.Tech | M.Pharm | MBA |MCA ");
			else
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to evaluate R13-M.Tech | M.Pharm | MBA |MCA ");
		} else if ((instanceUtitlity
				.isRegulation_R09_Course(Scrutiny_SeriallyScanAnswerSheet.this))) {
			if (scrutinySelection)
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to scrutiny R09-B.Tech | M.Tech | B.Pharm | MBA |MCA |B.Tech-CCC ");
			else
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to evaluate R09-B.Tech | M.Tech | B.Pharm | MBA |MCA |B.Tech-CCC ");

		} else if (instanceUtitlity
				.isRegulation_R13_Btech(Scrutiny_SeriallyScanAnswerSheet.this)) {
			if (scrutinySelection)
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to scrutiny R13-B.Tech | B.Pharm ");
			else
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to evaluate R13-B.Tech | B.Pharm ");
		} else if (instanceUtitlity
				.isRegulation_R15_Btech(Scrutiny_SeriallyScanAnswerSheet.this)) {
			if (scrutinySelection)
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to scrutiny R15-B.Tech | B.Pharm ");
			else
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to evaluate R15-B.Tech | B.Pharm ");
		} else if (instanceUtitlity
				.isRegulation_R15_Mtech(Scrutiny_SeriallyScanAnswerSheet.this)) {
			if (scrutinySelection)
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to scrutiny R15-M.Tech | M.Pharm | MBA |MCA ");
			else
				confirmationAlertForRegulation(barcode, _total_marks,
						"You are about to evaluate R15-M.Tech | M.Pharm | MBA |MCA ");
		} 

	}

	private void insertDefaultBarcode(int serialNo) {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor cur = _database.executeSQLQuery("select * from "
				+ SSConstants.TABLE_SCRUTINY_SAVE
				+ " where TRIM(UPPER(bundle_no)) = TRIM(UPPER('" + bundleNo
				+ "')) and bundle_serial_no=" + serialNo
				+ " and barcode_status=2", null);
		if (cur != null) {
			if (cur.getCount() > 0) {
				String barcode = cur.getString((cur
						.getColumnIndex(SSConstants.ANS_BOOK_BARCODE)));
				/*
				 * Intent intent = new Intent(this,
				 * Scrutiny_ScriptTotalEntryScreen.class);
				 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				 * intent.putExtra(SSConstants.SUBJECT_CODE, getIntent()
				 * .getStringExtra(SSConstants.SUBJECT_CODE));
				 * intent.putExtra(SSConstants.ANS_BOOK_BARCODE, barcode);
				 * intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
				 * String.valueOf(serialNo));
				 * intent.putExtra(SSConstants.BUNDLE_NO, bundleNo); if
				 * (getIntent().hasExtra(
				 * SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {
				 * 
				 * intent.putExtra(
				 * SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
				 * SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY); }
				 * intent.putExtra("noteDate", new Date().toString());
				 * intent.putExtra(SSConstants.USER_ID, userId);
				 * startActivity(intent); finish();
				 */
				navigateToMarkDialogScreen(barcode, "0");

			} else {
				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
						new ContextThemeWrapper(this, R.style.alert_text_style));
				myAlertDialog.setTitle(getResources().getString(
						R.string.app_name));
				myAlertDialog.setCancelable(false);
				myAlertDialog
						.setMessage("AnswerBook doesn't exists with this Serial No");

				myAlertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface Dialog, int arg1) {

								Dialog.dismiss();

							}
						});

				myAlertDialog.show();
			}
		}

		if (cur != null) {
			cur.close();
		}

	}

	private void unReadableBarCode() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_unreadable));

		myAlertDialog.setPositiveButton(
				getResources().getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						Dialog.dismiss();
						insertDefaultBarcode();
					}
				});

		myAlertDialog.setNegativeButton(
				getResources().getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
						arg0.dismiss();
					}
				});
		myAlertDialog.show();

	}

	private void insertDefaultBarcode() {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);

		Cursor cur = _database.executeSQLQuery(
				"select count(distinct bundle_serial_no) as Value from "
						+ SSConstants.TABLE_SCRUTINY_SAVE
						+ " where TRIM(UPPER(bundle_no)) = TRIM(UPPER('"
						+ bundleNo + "')) and barcode_status=2", null);
		if (cur.getCount() > 0) {
			// int barcode = 1000 + cur.getInt(cur.getColumnIndex("Value"));
			if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
				addScriptCase(
						bundleNo
								+ String.valueOf(1000 + cur.getInt(cur
										.getColumnIndex("Value"))), true);
			}
		}

		cur.close();
	}

	private void unreadableBarcode(View v) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_barcode_unreadable));

		myAlertDialog.setPositiveButton(
				getResources().getString(R.string.alert_yes),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface Dialog, int arg1) {

						LayoutInflater factory = LayoutInflater
								.from(Scrutiny_SeriallyScanAnswerSheet.this);
						final View textEntryView = factory
								.inflate(
										R.layout.scrutiny_alert_dialog_text_entry,
										null);

						new AlertDialog.Builder(new ContextThemeWrapper(
								Scrutiny_SeriallyScanAnswerSheet.this,
								R.style.alert_text_style))
								.setIconAttribute(
										android.R.attr.alertDialogIcon)
								.setCancelable(false)
								.setTitle("Enter The AnswerBook Serial No.")
								.setView(textEntryView)
								.setPositiveButton(R.string.alert_dialog_ok,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int whichButton) {

												EditText editText_bundle = (EditText) textEntryView
														.findViewById(R.id.tv_bundle);

												final String unreadableBundleSno = editText_bundle
														.getText().toString();

												if (!unreadableBundleSno
														.equalsIgnoreCase("")) {
													if (!unreadableBundleSno
															.equalsIgnoreCase(getIntent()
																	.getStringExtra(
																			SSConstants.BUNDLE_SERIAL_NO))) {
														showAlertForSerialNoWrongEntry(
																getString(R.string.alert_diff_serial_no),
																getString(R.string.alert_dialog_ok),
																getString(R.string.alert_dialog_cancel));
													} else {
														insertDefaultBarcode(Integer
																.parseInt(unreadableBundleSno));
													}

												}

												else {
													AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
															new ContextThemeWrapper(
																	Scrutiny_SeriallyScanAnswerSheet.this,
																	R.style.alert_text_style));
													myAlertDialog
															.setTitle(getResources()
																	.getString(
																			R.string.app_name));
													myAlertDialog
															.setCancelable(false);
													myAlertDialog
															.setMessage(getResources()
																	.getString(
																			R.string.alert_serial_no_empty));

													myAlertDialog
															.setPositiveButton(
																	getResources()
																			.getString(
																					R.string.alert_dialog_ok),
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface Dialog,
																				int arg1) {

																			Dialog.dismiss();

																		}
																	});

													myAlertDialog.show();

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

	private void addScriptCase(String _scannedAnsBookBarcode, boolean unreadable) {
		String _serialNo = bundle_serial_no;
		if (!TextUtils.isEmpty(_scannedAnsBookBarcode)
				&& !TextUtils.isEmpty(_serialNo)
				&& _scannedAnsBookBarcode.length() > 9) {
			if (!_serialNo.equalsIgnoreCase(getIntent().getStringExtra(
					SSConstants.BUNDLE_SERIAL_NO))) {
				showAlertForSerialNoWrongEntry(
						getString(R.string.alert_diff_serial_no),
						getString(R.string.alert_dialog_ok),
						getString(R.string.alert_dialog_cancel));

			} else {
				if (unreadable) {
					mainProcess(_scannedAnsBookBarcode, unreadable);
				} else {
					if (_scannedAnsBookBarcode.matches("[0-9]+")
							&& (_scannedAnsBookBarcode.length() == 10)
							|| (_scannedAnsBookBarcode.length() == 11)) {

						mainProcess(_scannedAnsBookBarcode, unreadable);
					} else {
						showAlertForSerialNoWrongEntry(
								getString(R.string.alert_invalid_barcode),
								getString(R.string.alert_dialog_ok), "");
					}
				}

			}
		}
	}

	private void mainProcess(String _scannedAnsBookBarcode, boolean unreadable) {
		if (scrutinySelection) {

			SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
			Cursor _cursor = _database.passedQuery(
					SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO
							+ " ='" + bundleNo + "' AND "
							+ SSConstants.ANS_BOOK_BARCODE + " ='"
							+ _scannedAnsBookBarcode + "'", null);

			if (_cursor.getCount() > 0) {
				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
						new ContextThemeWrapper(this, R.style.alert_text_style));
				myAlertDialog.setTitle(getResources().getString(
						R.string.app_name));
				myAlertDialog.setMessage(getResources().getString(
						R.string.alert_incorr_ans_book_scanned));
				myAlertDialog.setCancelable(false);

				myAlertDialog.setPositiveButton(
						getResources().getString(R.string.alert_dialog_ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								// do something when the OK button
								// is
								// clicked
								dialog.dismiss();
								scannedBook.setText("");
								bookSno.setText("");
							}
						});

				myAlertDialog.show();
			} else {
				ContentValues _values = new ContentValues();
				_values.put(SSConstants.ANS_BOOK_BARCODE,
						_scannedAnsBookBarcode);
				_values.put(SSConstants.BUNDLE_NO, bundleNo);
				_values.put(SSConstants.SUBJECT_CODE, subjectCode);
				_values.put(SSConstants.BUNDLE_SERIAL_NO, getIntent()
						.getStringExtra(SSConstants.BUNDLE_SERIAL_NO));
				_values.put(SSConstants.SCRUTINIZE_STATUS,
						SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED);
				_values.put(SSConstants.IS_SCRUTINIZED, 1);
				_values.put(SSConstants.SCRUTINIZED_BY, userId);
				if (unreadable) {
					_values.put(SSConstants.BARCODE_STATUS, 2);
				} else {
					_values.put(SSConstants.BARCODE_STATUS, 1);
				}
				_values.put(SSConstants.GRAND_TOTAL_MARK, "null");
				_values.put(SSConstants.SCRUTINIZED_ON, getPresentTime());
				_values.put(SSConstants.ENTER_ON, getPresentTime());
				_database
						.saveDataToDB(SSConstants.TABLE_SCRUTINY_SAVE, _values);
				_database.saveDataToDB(SSConstants.TABLE_EVALUATION_SAVE,
						_values);
				Intent intent = new Intent(this,
						Scrutiny_ShowGrandTotalSummaryTable.class);
				intent.putExtra("SeatNo", SSConstants.SeatNo);
				intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
				intent.putExtra(SSConstants.USER_ID, userId);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
			_cursor.close();

		} else {
			switchToMarkSummaryActivity(false);
		}
	}

	// get present time
	private String getPresentTime() {
		// set the format here
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	// show alert for serial no wrong entry
	private void showAlertForSerialNoWrongEntry(String msg, String positiveStr,
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
						bookSno.setText("");
						scannedBook.setText("");

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

	private void showAlertForRevaluated() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_scan_next_sheet));
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						navigateToGrandTotalSummaryOrthis();
					}
				});

		myAlertDialog.show();
	}

	private int checkStatusIs2() {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor = _database
				.passedQuery(
						SSConstants.TABLE_SCRUTINY_SAVE,
						SSConstants.BUNDLE_NO
								+ " = '"
								+ bundleNo
								+ "' AND "
								+ SSConstants.BUNDLE_SERIAL_NO
								+ " = '"
								+ bundle_serial_no
								+ "' AND "
								+ SSConstants.SCRUTINIZE_STATUS
								+ " = '"
								+ SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH
								+ "'", null);
		int _count = _cursor.getCount();
		_cursor.close();
		return _count;
	}

	// show alert
	private void showAlertForWrongserialNo2(String msg, String positiveStr,
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
						dialog.dismiss();
						editSerialNoEnterDialog2();
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

	// marks enter dialog
	private void editSerialNoEnterDialog2() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(getResources().getString(
				R.string.alert_serial_no));
		View view = LayoutInflater.from(this).inflate(
				R.layout.scrutiny_layout_alert_dialog_remarks_show, null);

		final EditText _etSerial = (EditText) view
				.findViewById(R.id.editText_remarks);
		// allows only number
		_etSerial.setInputType(2);
		// max characters two
		_etSerial
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
		myAlertDialog.setView(view);

		myAlertDialog.setPositiveButton(
				getResources().getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						String editMarks = _etSerial.getText().toString()
								.trim();
						dialog.dismiss();
						if (!TextUtils.isEmpty(editMarks)
								&& editMarks.equals(bundle_serial_no)) {
							SwitchToMissMatchScriptWithDB2(editMarks);
						} else {
							showAlertForWrongserialNo2(
									getString(R.string.alert_corr_serial_no),
									getString(R.string.alert_dialog_ok),
									getString(R.string.alert_dialog_cancel));
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

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	private Boolean bookSnoValid() {
		Boolean val = false;
		String sno = bookSno.getText().toString().trim();
		if (!TextUtils.isEmpty(sno)) {
			if (isInteger(sno)) {
				Log.v("CurrentAnswerBook", bundle_serial_no + "  " + sno);
				if (bundle_serial_no.equals(sno)) {
					val = true;
				} else {
					// String barcode=getCurrentAnswerBookNo(sno);
					// if(TextUtils.isEmpty(barcode)){
					alertMessage("Wrong Entry. Book Serial Number is "
							+ bundle_serial_no);
					/*
					 * }else{
					 * if(barcode.equals(scannedBook.getText().toString().
					 * trim())){ edt_currentbook=Integer.parseInt(sno);
					 * bookEditOption(barcode);
					 * 
					 * }else{ alertMessage("Serial number Already exists. \n" +
					 * "Do you want to edit. \nPlease scan correct barcode"); }
					 * }
					 */
					val = false;
				}
			} else {
				alertMessage("Enter only number");
				val = false;
			}
		} else {
			alertMessage("Enter Book serial number");
			val = false;
		}
		return val;
	}

	private void checkBarcodeInDB() {
		if (bookSnoValid()) {
			String _scannedAnsBookBarcode = scannedBook.getText().toString()
					.trim();
			String _serialNo = bundle_serial_no;
			if (!TextUtils.isEmpty(_scannedAnsBookBarcode)
					&& !TextUtils.isEmpty(_serialNo)
					&& _scannedAnsBookBarcode.length() > 9) { 

				// check serial no matching
				if (!_serialNo.equalsIgnoreCase(getIntent().getStringExtra(
						SSConstants.BUNDLE_SERIAL_NO))) {
					// showAlertForSerialNoWrongEntry(
					// "Selected serial no is not matching with entered serial no.",
					// "Ok", "Cancel");
					showAlertForSerialNoWrongEntry(
							getString(R.string.alert_diff_serial_no),
							getString(R.string.alert_dialog_ok),
							getString(R.string.alert_dialog_cancel));
					// }
					// // check status whether it is 2
					// else if (checkStatusIs2() == 1) {
					// showAlertForRevaluated();
				} else {
					if (_scannedAnsBookBarcode.matches("[0-9]+")
							&& (_scannedAnsBookBarcode.length() == 10)
							|| (_scannedAnsBookBarcode.length() == 11)) {
						SScrutinyDatabase _database = SScrutinyDatabase
								.getInstance(this);
						Cursor _cursor = _database.passedQuery(
								SSConstants.TABLE_SCRUTINY_SAVE,
								SSConstants.BUNDLE_SERIAL_NO
										+ "='"
										+ getIntent().getStringExtra(
												SSConstants.BUNDLE_SERIAL_NO)
										+ "' AND " + SSConstants.BUNDLE_NO
										+ "='" + bundleNo + "'", null);
						if (_cursor.getCount() > 0) {
							_cursor.moveToFirst();
							if (_scannedAnsBookBarcode
									.equalsIgnoreCase(_cursor.getString(_cursor
											.getColumnIndex(SSConstants.ANS_BOOK_BARCODE)))
									&& _serialNo
											.equalsIgnoreCase(_cursor.getString(_cursor
													.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)))) {
								switchToMarkSummaryActivity(false);
							} else {
								// show alert and delete ans barcode since sl no
								// not
								// matching
								SScrutinyDatabase _database2 = SScrutinyDatabase
										.getInstance(this);
								Cursor cur = _database2.passedQuery(
										SSConstants.TABLE_SCRUTINY_SAVE,
										SSConstants.BUNDLE_NO + " = '"
												+ bundleNo + "' AND "
												+ SSConstants.ANS_BOOK_BARCODE
												+ " = '"
												+ _scannedAnsBookBarcode + "'",
										null);
								if (cur.getCount() > 0) {
									cur.moveToFirst();
									final int _slNo = cur
											.getInt(cur
													.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO));
									cur.close();

									showAlertForDeletingBarcode(
											getString(R.string.alert_incorr_ans_book_scanned),
											"Revaluate", "Cancel",
											CONDITION1_FOR_DELETE, _slNo);

								} else {
									cur.close();
									SScrutinyDatabase _database3 = SScrutinyDatabase
											.getInstance(this);
									Cursor cur3 = _database3
											.passedQuery(
													SSConstants.TABLE_SCRUTINY_REQUEST,
													SSConstants.BUNDLE_NO
															+ " = '"
															+ bundleNo
															+ "' AND "
															+ SSConstants.ANS_BOOK_BARCODE
															+ " = '"
															+ _scannedAnsBookBarcode
															+ "'", null);
									if (cur3.getCount() > 0) {
										cur3.moveToFirst();
										cur3.close();
										ContentValues _values = new ContentValues();
										_values.put(
												SSConstants.ANS_BOOK_BARCODE,
												scannedBook.getText()
														.toString().trim());

										_database3
												.updateRow(
														SSConstants.TABLE_SCRUTINY_SAVE,
														_values,
														SSConstants.BUNDLE_SERIAL_NO
																+ "= '"
																+ bundle_serial_no
																+ "' AND "
																+ SSConstants.SCRUTINIZE_STATUS
																+ "= '"
																+ SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH
																+ "' AND "
																+ SSConstants.BUNDLE_NO
																+ "='"
																+ bundleNo
																+ "'");
										_database3
												.updateRow(
														SSConstants.TABLE_EVALUATION_SAVE,
														_values,
														SSConstants.BUNDLE_SERIAL_NO
																+ "= '"
																+ bundle_serial_no
																+ "' AND "
																+ SSConstants.SCRUTINIZE_STATUS
																+ "= '"
																+ SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH
																+ "' AND "
																+ SSConstants.BUNDLE_NO
																+ "='"
																+ bundleNo
																+ "'");
										/*
										 * Intent intent = new Intent( this,
										 * Scrutiny_ScriptTotalEntryScreen
										 * .class); intent.addFlags(Intent.
										 * FLAG_ACTIVITY_CLEAR_TOP);
										 * intent.putExtra(
										 * SSConstants.SUBJECT_CODE,
										 * getIntent().getStringExtra(
										 * SSConstants.SUBJECT_CODE));
										 * intent.putExtra(
										 * SSConstants.ANS_BOOK_BARCODE,
										 * scannedBook
										 * .getText().toString().trim());
										 * intent.putExtra(
										 * SSConstants.BUNDLE_SERIAL_NO,
										 * bundle_serial_no);
										 * intent.putExtra(SSConstants
										 * .BUNDLE_NO, bundleNo);
										 * intent.putExtra(SSConstants.USER_ID,
										 * userId);
										 * 
										 * startActivity(intent); finish();
										 */
										navigateToMarkDialogScreen(scannedBook
												.getText().toString().trim(),
												"0");

									} else {
										cur3.close();
										// when barcode doesn't exist in
										// database
										showAlertForMissMatchScriptWithDB(
												getString(R.string.alert_barcode_not_exists_in_db),
												getString(R.string.alert_dialog_ok),
												"");
									}
								}
							}
						} else {
							// when barcode doesn't exist in database
							showAlertForMissMatchScriptWithDB(
									getString(R.string.alert_barcode_not_exists_in_db),
									getString(R.string.alert_dialog_ok), "");
						}
						_cursor.close();
					} else {
						showAlert(getString(R.string.alert_invalid_barcode),
								getString(R.string.alert_dialog_ok), "");
					}
				}
			} else {
				showAlert(getString(R.string.alert_invalid_barcode),
						getString(R.string.alert_dialog_ok), "");
			}
		}
	}

	private void SwitchToMissMatchScriptWithDB2(String serialNo) {
		Intent _intent = new Intent(this, Scrutiny_MissMatchScriptWithDB.class);
		_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO, serialNo);
		_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
		_intent.putExtra(SSConstants.USER_ID, userId);
		_intent.putExtra("SeatNo",
				SSConstants.SeatNo);
		_intent.putExtra(SSConstants.NOTE_DATE, new Date().toString());
		if (getIntent().hasExtra(
				SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

			_intent.putExtra(
					SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
					SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
		}
		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(_intent);
		finish();
	}

	private void switchToMarkSummaryActivity(boolean is_unreadable) {
		/*
		 * Intent intent = new Intent(this,
		 * Scrutiny_ScriptTotalEntryScreen.class); if (getIntent().hasExtra(
		 * SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {
		 * 
		 * intent.putExtra(
		 * SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
		 * SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY); }
		 * 
		 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * intent.putExtra(SSConstants.SUBJECT_CODE,
		 * getIntent().getStringExtra(SSConstants.SUBJECT_CODE)); if
		 * (is_unreadable) { SScrutinyDatabase _database =
		 * SScrutinyDatabase.getInstance(this); Cursor _cursor =
		 * _database.passedQuery( SSConstants.TABLE_SCRUTINY_SAVE,
		 * SSConstants.BUNDLE_SERIAL_NO + "='" + bundle_serial_no + "' AND " +
		 * SSConstants.BUNDLE_NO + "='" + bundleNo + "'", null); if
		 * (_cursor.getCount() > 0) {
		 * intent.putExtra(SSConstants.ANS_BOOK_BARCODE, _cursor
		 * .getString(_cursor .getColumnIndex(SSConstants.ANS_BOOK_BARCODE))); }
		 * _cursor.close(); intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
		 * bundle_serial_no); } else {
		 * intent.putExtra(SSConstants.ANS_BOOK_BARCODE, scannedBook.getText()
		 * .toString()); intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
		 * bundle_serial_no); } if
		 * (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
		 * intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
		 * SSConstants.ADD_SCRIPT_CASE); } intent.putExtra("noteDate", new
		 * Date().toString()); intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
		 * intent.putExtra(SSConstants.USER_ID, userId); startActivity(intent);
		 * finish();
		 */
		if (is_unreadable) {
			SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
			Cursor _cursor = _database.passedQuery(
					SSConstants.TABLE_SCRUTINY_SAVE,
					SSConstants.BUNDLE_SERIAL_NO + "='" + bundle_serial_no
							+ "' AND " + SSConstants.BUNDLE_NO + "='"
							+ bundleNo + "'", null);
			if (_cursor.getCount() > 0) {
				navigateToMarkDialogScreen(_cursor.getString(_cursor
						.getColumnIndex(SSConstants.ANS_BOOK_BARCODE)), "");
			}
			_cursor.close();
		} else {
			navigateToMarkDialogScreen(scannedBook.getText().toString().trim(),
					"");
		}
	}

	// show alert
	private void showAlertForMissMatchScriptWithDB(String msg,
			String positiveStr, String negativeStr) {
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
						dialog.dismiss();
						scannedBook.setText("");
						((TextView) findViewById(R.id.tv_back))
								.setVisibility(View.VISIBLE);
						((TextView) findViewById(R.id.tv_back))
						.setText("Update");    
					}
				});
		myAlertDialog.setNegativeButton(negativeStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
						scannedBook.setText("");
					}
				});
		myAlertDialog.show();
	}

	// show alert
	private void showAlert(String msg, String positiveStr, String negativeStr) {
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
						dialog.dismiss();
						scannedBook.setText("");
						bookSno.setText("");
					}
				});
		myAlertDialog.setNegativeButton(negativeStr,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
						bookSno.setText("");
						scannedBook.setText("");
					}
				});
		myAlertDialog.show();
	}

	// show alert for deleting barcode
	private void showAlertForDeletingBarcode(String msg, String positiveStr,
			String negativeStr, final String conditonForDeleteScript,
			final int existSerialNo) {
		final SScrutinyDatabase _database = SScrutinyDatabase.getInstance(Scrutiny_SeriallyScanAnswerSheet.this);
		final ContentValues _values = new ContentValues();
		// final Cursor _cursor;
		View _view = LayoutInflater.from(Scrutiny_SeriallyScanAnswerSheet.this).inflate(
				R.layout.scrutiny_layout_alert_revaluate, null);
		final Dialog myAlertDialog = new Dialog(Scrutiny_SeriallyScanAnswerSheet.this);
		// AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
		// new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		// myAlertDialog.setMessage(msg);
		// myAlertDialog.setCancelable(true);
		myAlertDialog.setContentView(_view);

		_view.findViewById(R.id.btn_alert_revaluate).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (conditonForDeleteScript
								.equalsIgnoreCase(CONDITION1_FOR_DELETE)) {
							Calendar c = Calendar.getInstance();
							SimpleDateFormat forma = new SimpleDateFormat(
									"dd/MM/yyyy HH:mm");
							String dateStart = forma.format(c.getTime());
							_values.put(
									SSConstants.SCRUTINIZE_STATUS,
									SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH);
							_values.put(SSConstants.SCRUTINIZED_BY, userId);
							_values.put(SSConstants.SCRUTINIZED_ON, dateStart);
							_values.put(SSConstants.IS_SCRUTINIZED, 1);

							_database.updateRow(
									SSConstants.TABLE_SCRUTINY_SAVE, _values,
									SSConstants.BUNDLE_NO + " ='" + bundleNo
											+ "' AND "
											+ SSConstants.BUNDLE_SERIAL_NO
											+ " IN (" + bundle_serial_no + ","
											+ existSerialNo + ")");

							_database.updateRow(
									SSConstants.TABLE_EVALUATION_SAVE, _values,
									SSConstants.BUNDLE_NO + " ='" + bundleNo
											+ "' AND "
											+ SSConstants.BUNDLE_SERIAL_NO
											+ " IN (" + bundle_serial_no + ","
											+ existSerialNo + ")");
						}
						// ((DialogInterface) myAlertDialog).dismiss();
						navigateToGrandTotalSummaryOrthis();

					}
				});

		_view.findViewById(R.id.btn_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						scannedBook.setText("");
						bookSno.setText("");
						myAlertDialog.dismiss();
					}
				});
		/*
		 * _view.findViewById(R.id.btn_alert_rescan).setOnClickListener( new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // ((DialogInterface)
		 * myAlertDialog).dismiss(); myAlertDialog.dismiss();
		 * scannedBook.setText(""); scannedBook.setFocusable(true);
		 * scannedBook.setFocusableInTouchMode(true); // finish(); } });
		 */

		myAlertDialog.show();
	}

	private void navigateToGrandTotalSummaryOrthis() {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Intent _intent;
		Cursor _cursor_scripts_count = _database.passedQuery(
				SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO + " = '"
						+ bundleNo + "'", null);
		int scriptsCount = _cursor_scripts_count.getCount();
		_cursor_scripts_count.close();
		if (scriptsCount == Integer.valueOf(bundle_serial_no)
				|| getIntent().hasExtra(
						SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {
			_intent = new Intent(Scrutiny_SeriallyScanAnswerSheet.this,
					Scrutiny_ShowGrandTotalSummaryTable.class);
			_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
			_intent.putExtra(SSConstants.USER_ID, userId);
			_intent.putExtra("SeatNo", SSConstants.SeatNo);
			_intent.putExtra(SSConstants.SUBJECT_CODE, subjectCode);
			_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(_intent);
			finish();
		} else if (scriptsCount > Integer.valueOf(bundle_serial_no)) {
			_intent = new Intent(Scrutiny_SeriallyScanAnswerSheet.this,
					Scrutiny_SeriallyScanAnswerSheet.class);
			_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
			_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
					String.valueOf((Integer.parseInt(bundle_serial_no) + 1)));
			_intent.putExtra("SeatNo", SSConstants.SeatNo);
			_intent.putExtra(SSConstants.USER_ID, userId);
			startActivity(_intent);
			finish();
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scrutiny_activity_main, menu);
		menu.findItem(R.id.menu_settings).setVisible(false);
		menu.setGroupVisible(0, false);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.menu_back) {
			finish();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override 
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scanbook_submit:
			if (fromAddScript) {
				addScriptCase(scannedBook.getText().toString().trim(), false);
			} else {
				checkBarcodeInDB();
			}    
			break;    
		case R.id.tv_back:
			updateBarcode();  
			break;

		case R.id.tv_unread_barcode:
			if (fromAddScript) {  
				unReadableBarCode();
			} else {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				unreadableBarcode(v);
			}
			break;

		default:
			break;
		}
	}

	public void confirmationAlertForRegulation(final String pBarcode,
			final String pTotalMarks, final String getMsg) {   
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				Scrutiny_SeriallyScanAnswerSheet.this);
		myAlertDialog.setTitle("Smart Scrutinization");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(getMsg);

		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
  
						Dialog.dismiss();
						if (instanceUtitlity
										.isRegulation_R09_Course(Scrutiny_SeriallyScanAnswerSheet.this)) {
							Intent _intent = new Intent(
									Scrutiny_SeriallyScanAnswerSheet.this,  
									Scrutiny_MarkDialog.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundle_serial_no);
							_intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (getIntent().hasExtra(
									SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}
							if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
								_intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
										SSConstants.ADD_SCRIPT_CASE);
							}
							_intent.putExtra(SSConstants.NOTE_DATE, new Date().toString());
							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
							finish();
						} 
						else if(instanceUtitlity
								.isRegulation_R13_Mtech(Scrutiny_SeriallyScanAnswerSheet.this))
						{
							Intent _intent = new Intent(
									Scrutiny_SeriallyScanAnswerSheet.this,
									Scrutiny_MarkDialog_R13.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundleNo);
							_intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
								_intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
										SSConstants.ADD_SCRIPT_CASE);
							}
							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (getIntent().hasExtra(
									SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							_intent.putExtra(SSConstants.NOTE_DATE, new Date().toString());

							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
							finish();
						}
						else if (instanceUtitlity
								.isRegulation_R13_Btech(Scrutiny_SeriallyScanAnswerSheet.this)) {
							if( subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AG_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AH_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AJ_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AK_ENGG_DRAWING) ){
							Intent _intent = new Intent(
									Scrutiny_SeriallyScanAnswerSheet.this,
									Scrutiny_MarkDialog_R13_BTech_SpecialCase.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundle_serial_no);
							_intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
								_intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
										SSConstants.ADD_SCRIPT_CASE);
							}
							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (getIntent().hasExtra(
									SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							_intent.putExtra(SSConstants.NOTE_DATE, new Date().toString());
							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
							finish();
						}else if(subjectCode.equalsIgnoreCase(SSConstants.SUBJ_114DA_ENGG_DRAWING)
								|| subjectCode.equalsIgnoreCase(SSConstants.SUBJ_114DB_ENGG_DRAWING)){

							Intent _intent = new Intent(
									Scrutiny_SeriallyScanAnswerSheet.this,
									Scrutiny_MarkDialog_R13_BTech_SpecialCase_New.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundle_serial_no);
							_intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
								_intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
										SSConstants.ADD_SCRIPT_CASE);
							}
							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (getIntent().hasExtra(
									SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							_intent.putExtra(SSConstants.NOTE_DATE, new Date().toString());
							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
							finish();
							
						}
							else{

								Intent _intent = new Intent(
										Scrutiny_SeriallyScanAnswerSheet.this,
										Scrutiny_MarkDialog_R13.class);
								_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

								_intent.putExtra(SSConstants.SUBJECT_CODE,
										subjectCode);
								_intent.putExtra(SSConstants.USER_ID, userId);
								_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
								_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
										bundle_serial_no);
								_intent.putExtra("SeatNo",
										SSConstants.SeatNo);
								if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
									_intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
											SSConstants.ADD_SCRIPT_CASE);
								}
								_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
										pBarcode);
								if (getIntent().hasExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

									_intent.putExtra(
											SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
											SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
								}

								_intent.putExtra(SSConstants.NOTE_DATE, new Date().toString());
								_intent.putExtra(
										SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
										pTotalMarks);
								startActivity(_intent);
								finish();
							
							}
						}else if(instanceUtitlity
								.isRegulation_R15_Btech(Scrutiny_SeriallyScanAnswerSheet.this))
						{
							Intent _intent = new Intent(
									Scrutiny_SeriallyScanAnswerSheet.this,
									Scrutiny_MarkDialog_R15.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundleNo);
							_intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
								_intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
										SSConstants.ADD_SCRIPT_CASE);
							}
							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (getIntent().hasExtra(
									SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							_intent.putExtra(SSConstants.NOTE_DATE, new Date().toString());

							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
							finish();
						}else if(instanceUtitlity
								.isRegulation_R15_Mtech(Scrutiny_SeriallyScanAnswerSheet.this))
						{
							Intent _intent = new Intent(
									Scrutiny_SeriallyScanAnswerSheet.this,
									Scrutiny_MarkDialog_R15.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundleNo);
							_intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
								_intent.putExtra(SSConstants.ADD_SCRIPT_CASE,
										SSConstants.ADD_SCRIPT_CASE);
							}
							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (getIntent().hasExtra(
									SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							_intent.putExtra(SSConstants.NOTE_DATE, new Date().toString());

							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
							finish();
						}

						else {
							alertMessage("Kindly check with spot-center coordinator for regulaiton problem! ");
						}

					}
				});

		myAlertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// TODO Auto-generated method stub
						Dialog.dismiss();
					}
				});

		myAlertDialog.show();

	}

	private void alertMessage(String pMsg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog.setMessage(pMsg);
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

	private Boolean bookSnoCheck() {
		Boolean val = false;
		String sno = bookSno.getText().toString().trim();
		if (!TextUtils.isEmpty(sno)) {
			if (isInteger(sno)) {
				if (Integer.parseInt(sno) > 0 && Integer.parseInt(sno) < 51) {
					val = true;
				} else {
					alertMessage("Enter Valid Book serial number");
					val = false;
				}
			} else {
				alertMessage("Enter only number");
				val = false;
			}
		} else {
			alertMessage("Enter Book serial number");
			val = false;
		}
		return val;
	}

	void updateBarcode() {
		if (bookSnoCheck()) {
			String _scannedAnsBookBarcode = scannedBook.getText().toString()
					.trim();
			String _serialNo = bookSno.getText().toString().trim();
			if (!TextUtils.isEmpty(_scannedAnsBookBarcode)
					&& !TextUtils.isEmpty(_serialNo)
					&& _scannedAnsBookBarcode.length() > 9) {

				if (_scannedAnsBookBarcode.matches("[0-9]+")
						&& (_scannedAnsBookBarcode.length() == 10)
						|| (_scannedAnsBookBarcode.length() == 11)) {

					alertMessageUpdate("Do You want Update Barcode?",
							_scannedAnsBookBarcode, _serialNo);

				} else {
					showAlert("This Barcode is Invalid!", "OK", "");
				}
			} else {
				showAlert("This Barcode is Invalid!", "OK", "");
			}
		}
	}

	private void alertMessageUpdate(String pMsg,
			final String _scannedAnsBookBarcode, final String _serialNo) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog.setMessage(pMsg);
		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						try {  
							String _query = "update "
									+ SSConstants.TABLE_SCRUTINY_SAVE + " SET "
									+ SSConstants.ANS_BOOK_BARCODE + "= '"
									+ _scannedAnsBookBarcode +  "'"+
									" where "           
									+ SSConstants.BUNDLE_SERIAL_NO + "="
									+ _serialNo + "";
							//update table_marks_scrutinize SET barcode = '12345678900' , barcode_status = '5'
							//where bundle_serial_no=1
							String _queryE = "update "
									+ SSConstants.TABLE_EVALUATION_SAVE
									+ " SET " + SSConstants.ANS_BOOK_BARCODE
									+ "= '" + _scannedAnsBookBarcode  
									+ "' , "+ SSConstants.BARCODE_STATUS + "= '5' where " 
									+ SSConstants.BUNDLE_SERIAL_NO
									+ "=" + _serialNo + "";
							SScrutinyDatabase _database = SScrutinyDatabase
									.getInstance(Scrutiny_SeriallyScanAnswerSheet.this);

							_database.executeSQLQuery(_query, null).close();
							_database.executeSQLQuery(_queryE, null).close();
							((TextView) findViewById(R.id.tv_back))
									.setVisibility(View.INVISIBLE);
							scannedBook.setText("");
							bookSno.setText("");
						} catch (Exception e) {
							alertMessage("Updation Failed");
							e.printStackTrace();
						}
						Dialog.dismiss();
					}
				});
		myAlertDialog.setNegativeButton(
				getResources().getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// do something when the Cancel button is clicked
						arg0.dismiss();
					}
				});
		myAlertDialog.show();
	}
}
