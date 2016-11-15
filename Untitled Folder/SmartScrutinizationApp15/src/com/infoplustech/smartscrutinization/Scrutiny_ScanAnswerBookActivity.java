package com.infoplustech.smartscrutinization;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
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
import com.infoplustech.smartscrutinization.db.Scrutiny_TempDatabase;
import com.infoplustech.smartscrutinization.utils.FileLog;
import com.infoplustech.smartscrutinization.utils.HiddenPassTransformationMethod;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_ScanAnswerBookActivity extends Activity implements
		OnClickListener {

	EditText scannedBook, bookSno;
	SharedPreferences preferences;
	SharedPreferences.Editor max_total_edit;
	String subjectCode, SeatNo;  
	boolean scrutinySelection;
	String bundleNo, userId;
	int reevaluateCount;
	String bundle_serial_no;
	boolean fromAddScript;

	private PowerManager.WakeLock wl;
	Utility instanceUtitlity;

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
		instanceUtitlity = new Utility();
		Log.v("activity", "Scrutiny_SeriallyScanAnswerSheet");
		getActionBar().hide();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");
		((TextView) findViewById(R.id.tv_back)).setOnClickListener(this);
		Button btnSubmit = (Button) findViewById(R.id.btn_scanbook_submit);
		scannedBook = (EditText) findViewById(R.id.et_scanbook);
		bookSno  = (EditText) findViewById(R.id.WriteSerialNumberTextView1);
		reevaluateCount = 0; 
		final Intent intent_extras = getIntent();
		bundleNo = intent_extras.getStringExtra(SSConstants.BUNDLE_NO).toUpperCase();
		userId = intent_extras.getStringExtra(SSConstants.USER_ID);
		bundle_serial_no = intent_extras
				.getStringExtra(SSConstants.BUNDLE_SERIAL_NO);
		subjectCode = intent_extras.getStringExtra(SSConstants.SUBJECT_CODE);
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);
		if (intent_extras.hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
			fromAddScript = true;  
		} else {
			fromAddScript = false;
		}
		scannedBook.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(scannedBook.getWindowToken(), 0);
				}
			}  
		});
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor_scripts_count = _database.passedQuery(
				SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO + " = '"
						+ bundleNo + "'", SSConstants.BUNDLE_SERIAL_NO);
		((TextView) findViewById(R.id.tv_smart_scrutiny))
				.setText(getString(R.string.app_name) + " " + bundle_serial_no
						+ " of " + _cursor_scripts_count.getCount());
		_cursor_scripts_count.close();

		TextView tv_unreadable = (TextView) findViewById(R.id.tv_unread_barcode);
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
			btnSubmit.setOnClickListener(this);
		}
		if (cursor_unreadable != null) {
			cursor_unreadable.close();
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

					// checkBarcodeInDB();
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

	private void insertDefaultBarcode(int serialNo) {

		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor cur = _database.executeSQLQuery("select * from "
				+ SSConstants.TABLE_SCRUTINY_SAVE
				+ " where TRIM(UPPER(bundle_no)) = TRIM(UPPER('" + bundleNo
				+ "')) and bundle_serial_no=" + serialNo
				+ " and barcode_status=2", null);
		if (cur.getCount() > 0) {
			String barcode = cur.getString((cur
					.getColumnIndex(SSConstants.ANS_BOOK_BARCODE)));
			cur.close();
			if (instanceUtitlity
		.isRegulation_R13_Mtech(Scrutiny_ScanAnswerBookActivity.this)) {
				confirmationAlertForRegulation(barcode, serialNo,
						"You are about to evaluate R13-M.Tech | M.Pharm | MBA |MCA ");
			} else if ((instanceUtitlity.isRegulation_R09_Course(Scrutiny_ScanAnswerBookActivity.this))) {
				confirmationAlertForRegulation(barcode, serialNo,
						"You are about to evaluate R09-B.Tech | M.Tech | B.Pharm | MBA |MCA |B.Tech-CCC ");
			} else if (instanceUtitlity
					.isRegulation_R13_Btech(Scrutiny_ScanAnswerBookActivity.this)) {
				confirmationAlertForRegulation(barcode, serialNo,
						"You are about to evaluate R13-B.Tech | B.Pharm ");
			}

		}

		else {
			cur.close();

			alertMessage("AnswerBook doesn't exists with this Serial No");
		}

	}

	private void alertMessage(String pMsg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setCancelable(false);
		myAlertDialog.setMessage(pMsg);

		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						Dialog.dismiss();

					}
				});

		myAlertDialog.show();
	}

	private Boolean bookSnoValid(){
		Boolean val=false;
		String sno=bookSno.getText().toString().trim();
			if (!TextUtils.isEmpty(sno)) {
				if(isInteger(sno)){
					Log.v("CurrentAnswerBook", bundle_serial_no+"  "+sno);
				if(bundle_serial_no.equals(sno)){
					val=true;
				}else{
				//	String barcode=getCurrentAnswerBookNo(sno);
				//	if(TextUtils.isEmpty(barcode)){
						alertMessage("Wrong Entry. Book Serial Number is "+bundle_serial_no);
					/*}else{
						if(barcode.equals(scannedBook.getText().toString().trim())){
							edt_currentbook=Integer.parseInt(sno);
							bookEditOption(barcode);
							
						}else{
							alertMessage("Serial number Already exists. \n" +
									"Do you want to edit. \nPlease scan correct barcode");
						}
					}*/
					val=false;
				}
				}else{
					alertMessage("Enter only number");
					val=false;
				}
			}else{
				alertMessage("Enter Book serial number");
				val=false;
			}
		return val;
	}
	
	private	String getCurrentAnswerBookNo(String sno) {
		String barcode="";
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
				Cursor cursor = null;
				try {
					String qry="select barcode "
							+ "from table_marks_scrutinize "
							+ "where bundle_no = '"
							+ bundleNo
							+ "' and bundle_serial_no='"+sno+"'";
					Log.v("tag", "qry "+qry);
					cursor = _database
							.executeSQLQuery(qry, null);

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
					cursor.close();
					_database.close();
				}
				return barcode;
		}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
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

	public void confirmationAlertForRegulation(final String pBarcode,
			final int pSerialNo, final String getMsg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				Scrutiny_ScanAnswerBookActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(getMsg);

		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
   
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						Dialog.dismiss();
						if (instanceUtitlity
										.isRegulation_R09_Course(Scrutiny_ScanAnswerBookActivity.this)) {

							showRemarksWithOrangeColor(pBarcode);
							Intent intent = new Intent(
									Scrutiny_ScanAnswerBookActivity.this,
									Scrutiny_MarkDialogCorrection.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra(       
									SSConstants.SUBJECT_CODE,
									getIntent().getStringExtra(
											SSConstants.SUBJECT_CODE));
							intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									String.valueOf(pSerialNo));
							intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							intent.putExtra(SSConstants.USER_ID, userId);
							startActivity(intent);  

						} else if (instanceUtitlity
								.isRegulation_R13_Btech(Scrutiny_ScanAnswerBookActivity.this)) {
							if( subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AG_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AH_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AJ_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AK_ENGG_DRAWING) ){

							showRemarksWithOrangeColor_R13Btech(pBarcode);
							Intent intent = new Intent(
									Scrutiny_ScanAnswerBookActivity.this,
									Scrutiny_MarkDialogCorrection_R13_BTech_SpecialCase.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra(
									SSConstants.SUBJECT_CODE,
									getIntent().getStringExtra(
											SSConstants.SUBJECT_CODE));
							intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									String.valueOf(pSerialNo));
							intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							intent.putExtra(SSConstants.USER_ID, userId);
							startActivity(intent);
						}
							else if(subjectCode.equalsIgnoreCase(SSConstants.SUBJ_114DA_ENGG_DRAWING)
									|| subjectCode.equalsIgnoreCase(SSConstants.SUBJ_114DB_ENGG_DRAWING)){

								showRemarksWithOrangeColor_R13Btech(pBarcode);
								Intent intent = new Intent(
										Scrutiny_ScanAnswerBookActivity.this,
										Scrutiny_MarkDialogCorrection_R13_BTech_SpecialCase_New.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra(
										SSConstants.SUBJECT_CODE,
										getIntent().getStringExtra(
												SSConstants.SUBJECT_CODE));
								intent.putExtra("SeatNo",
										SSConstants.SeatNo);
								intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
										pBarcode);
								intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
										String.valueOf(pSerialNo));
								intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
								intent.putExtra(SSConstants.USER_ID, userId);
								startActivity(intent);
							
							}
							else{

							showRemarksWithOrangeColor_R13Btech(pBarcode);
							Intent intent = new Intent(
									Scrutiny_ScanAnswerBookActivity.this,
									Scrutiny_MarkDialogCorrection_R13.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra(
									SSConstants.SUBJECT_CODE,
									getIntent().getStringExtra(
											SSConstants.SUBJECT_CODE));
							intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									String.valueOf(pSerialNo));
							intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							intent.putExtra(SSConstants.USER_ID, userId);
							startActivity(intent);
						
						}
						}
						
					//swapna
						else if(instanceUtitlity.isRegulation_R15_Btech(Scrutiny_ScanAnswerBookActivity.this)){


							showRemarksWithOrangeColor_R13Btech(pBarcode);
							Intent intent = new Intent(
									Scrutiny_ScanAnswerBookActivity.this,
									Scrutiny_MarkDialogCorrection_R13.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							intent.putExtra(
									SSConstants.SUBJECT_CODE,
									getIntent().getStringExtra(
											SSConstants.SUBJECT_CODE));
							intent.putExtra("SeatNo",
									SSConstants.SeatNo);
							intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									String.valueOf(pSerialNo));
							intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							intent.putExtra(SSConstants.USER_ID, userId);
							startActivity(intent);
						
						
						}

						 else if (instanceUtitlity
									.isRegulation_R13_Mtech(Scrutiny_ScanAnswerBookActivity.this)
									//swapna
									|| instanceUtitlity
									.isRegulation_R15_Mtech(Scrutiny_ScanAnswerBookActivity.this)) {

								showRemarksWithOrangeColor_R13Btech(pBarcode);
								Intent intent = new Intent(
										Scrutiny_ScanAnswerBookActivity.this,
										Scrutiny_MarkDialogCorrection_R13.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								intent.putExtra(
										SSConstants.SUBJECT_CODE,
										getIntent().getStringExtra(
												SSConstants.SUBJECT_CODE));
								intent.putExtra("SeatNo",
										SSConstants.SeatNo);
								intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
										pBarcode);
								intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
										String.valueOf(pSerialNo));
								intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
								intent.putExtra(SSConstants.USER_ID, userId);
								startActivity(intent);
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

	private void insertDefaultBarcode() {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);

		Cursor cur = _database.executeSQLQuery(
				"select count(distinct bundle_serial_no) as Value from "
						+ SSConstants.TABLE_SCRUTINY_SAVE
						+ " where TRIM(UPPER(bundle_no)) = TRIM(UPPER('"
						+ bundleNo + "')) and barcode_status=2", null);
		if (cur.getCount() > 0) {
			int barcode = 1000 + cur.getInt(cur.getColumnIndex("Value"));
			if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
				addScriptCase(bundleNo + String.valueOf(barcode), true);
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
								.from(Scrutiny_ScanAnswerBookActivity.this);
						final View textEntryView = factory
								.inflate(
										R.layout.scrutiny_alert_dialog_text_entry,
										null);

						new AlertDialog.Builder(new ContextThemeWrapper(
								Scrutiny_ScanAnswerBookActivity.this,
								R.style.alert_text_style))
								.setIconAttribute(
										android.R.attr.alertDialogIcon)
								.setCancelable(false)
								.setTitle(
										getResources().getString(
												R.string.alert_serial_no))
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
																	Scrutiny_ScanAnswerBookActivity.this,
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
										getResources().getString(
												R.string.alert_dialog_cancel),
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
				&& !TextUtils.isEmpty(_serialNo)) {
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
				// myAlertDialog
				// .setMessage("Answer Sheet exists with serial no - "
				// + _cursor.getString(_cursor
				// .getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)));
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
								finish();

							}
						});

				myAlertDialog.show();
			} else {
				ContentValues _values = new ContentValues();
				_values.put(SSConstants.ANS_BOOK_BARCODE,
						_scannedAnsBookBarcode);
				_values.put(SSConstants.BUNDLE_NO, bundleNo);
				_values.put(SSConstants.BUNDLE_SERIAL_NO, getIntent()
						.getStringExtra(SSConstants.BUNDLE_SERIAL_NO));
				_values.put(SSConstants.SCRUTINIZE_STATUS,
						SSConstants.SCRUTINY_STATUS_1_NOT_EVALUATED);
				_values.put(SSConstants.IS_SCRUTINIZED, 1);
				if (unreadable) {
					_values.put(SSConstants.BARCODE_STATUS, 2);
				} else {
					_values.put(SSConstants.BARCODE_STATUS, 1);
				}
				_database.saveDataToDB(SSConstants.TABLE_EVALUATION_SAVE,_values);
				_database.saveDataToDB(SSConstants.TABLE_SCRUTINY_SAVE,_values);
				_database.close();

				Intent intent = new Intent(this,
						Scrutiny_ShowGrandTotalSummaryTable.class);
				intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
				intent.putExtra(SSConstants.USER_ID, userId);
				intent.putExtra("SeatNo",
						SSConstants.SeatNo);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			_cursor.close();

		} else {
			switchToMarkSummaryActivity(false);
		}
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

						scannedBook.setText("");
						// etBundleSerialNo.setText("");
						// etBundleSerialNo.setFocusable(true);
						// etBundleSerialNo.setFocusableInTouchMode(true);

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

	private int checkExistsInTableMarks(String answerSheetBarcode) {
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor = _database.getRowFromTable_Marks(answerSheetBarcode,
				new String[] { SSConstants.ANS_BOOK_BARCODE });
		int _count = _cursor.getCount();
		_cursor.close();

		if (_count == 0) {
			_cursor = _database.getRow(answerSheetBarcode,
					new String[] { SSConstants.ANS_BOOK_BARCODE });
			_count = _cursor.getCount();
		}
		_cursor.close();

		return _count;
	}

	private void checkBarcodeInDB() {
		if(bookSnoValid()){
		String _scannedAnsBookBarcode = scannedBook.getText().toString().trim();
		String _serialNo = bundle_serial_no;
		if (!TextUtils.isEmpty(_scannedAnsBookBarcode)
				&& !TextUtils.isEmpty(_serialNo)) {

			if (!_serialNo.equalsIgnoreCase(getIntent().getStringExtra(
					SSConstants.BUNDLE_SERIAL_NO))) {
				showAlertForSerialNoWrongEntry(
						getString(R.string.alert_diff_serial_no),
						getString(R.string.alert_dialog_ok),
						getString(R.string.alert_dialog_cancel));
			} else {
				// validate scanned barcode
				if (_scannedAnsBookBarcode.matches("[0-9]+")
						&& ((_scannedAnsBookBarcode.length() == 10) || (_scannedAnsBookBarcode
								.length() == 11))
						&& (checkExistsInTableMarks(_scannedAnsBookBarcode) > 0)) {
					SScrutinyDatabase _database = SScrutinyDatabase
							.getInstance(this);
					Cursor _cursor = _database.passedQuery(
							SSConstants.TABLE_SCRUTINY_SAVE,
							SSConstants.BUNDLE_SERIAL_NO
									+ "='"
									+ getIntent().getStringExtra(
											SSConstants.BUNDLE_SERIAL_NO)
									+ "' AND " + SSConstants.BUNDLE_NO + "='"
									+ bundleNo + "'", null);
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
							// show alert and delete ans barcode since sl no not
							// matching
							SScrutinyDatabase _database2 = SScrutinyDatabase
									.getInstance(this);
							Cursor cur = _database2.passedQuery(
									SSConstants.TABLE_SCRUTINY_SAVE,  
									SSConstants.BUNDLE_NO + " = '" + bundleNo
											+ "' AND "
											+ SSConstants.ANS_BOOK_BARCODE
											+ " = '" + _scannedAnsBookBarcode
											+ "'", null);
							if (cur.getCount() > 0) {
								cur.moveToFirst();
								cur.close();
								_database2.close();
								showAlert(
										getString(R.string.alert_incorr_ans_book_scanned),
										"Ok", "");
							} else {
								cur.close();
								_database2.close();
								SScrutinyDatabase _database3 = SScrutinyDatabase
										.getInstance(this);
								Cursor cur3 = _database3.passedQuery(
										SSConstants.TABLE_SCRUTINY_SAVE,
										SSConstants.BUNDLE_NO + " = '"
												+ bundleNo + "' AND "
												+ SSConstants.BUNDLE_SERIAL_NO
												+ " = '" + bundle_serial_no
												+ "'", null);
								if (cur3.getCount() == 1) {
									cur3.moveToFirst();
									final int _slNo = cur3
											.getInt(cur
													.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO));
									cur3.close();
									ContentValues _values = new ContentValues();
									_values.put(SSConstants.ANS_BOOK_BARCODE,
											scannedBook.getText().toString()
													.trim());

									_database3
											.updateRow(
													SSConstants.TABLE_SCRUTINY_SAVE,
													_values,
													SSConstants.BUNDLE_SERIAL_NO
															+ "= '"
															+ _slNo
															+ "' AND "
															+ SSConstants.SCRUTINIZE_STATUS
															+ "='"
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
													+ _slNo
													+ "' AND "
													+ SSConstants.SCRUTINIZE_STATUS
													+ "='"
													+ SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH
													+ "' AND "
													+ SSConstants.BUNDLE_NO
													+ "='"
													+ bundleNo
													+ "'");
									if (instanceUtitlity
											.isRegulation_R13_Mtech(Scrutiny_ScanAnswerBookActivity.this)) {
										confirmationAlertForRegulation(
												scannedBook.getText()
														.toString().trim(),
												Integer.parseInt(bundle_serial_no),
												"You are about to evaluate R13-M.Tech | M.Pharm | MBA |MCA ");
									} else if ((instanceUtitlity
											.isRegulation_R09_Course(Scrutiny_ScanAnswerBookActivity.this))) {
										confirmationAlertForRegulation(
												scannedBook.getText()
														.toString().trim(),
												Integer.parseInt(bundle_serial_no),
												"You are about to evaluate R09-B.Tech | M.Tech | B.Pharm | MBA |MCA |B.Tech-CCC ");
									} else if (instanceUtitlity
											.isRegulation_R13_Btech(Scrutiny_ScanAnswerBookActivity.this)) {
										confirmationAlertForRegulation(
												scannedBook.getText()
														.toString().trim(),
												Integer.parseInt(bundle_serial_no),
												"You are about to evaluate R13-B.Tech | B.Pharm ");
									}
								} else {  
									cur3.close();
									_database3.close();
									showAlert(
											getString(R.string.alert_barcode_not_exists_in_db),
											getString(R.string.alert_dialog_ok),
											"");
								}
							}
						}
					} else {
						showAlert(
								getString(R.string.alert_barcode_not_exists_in_db),
								getString(R.string.alert_dialog_ok), "");
					}
					_cursor.close();
					_database.close();
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

	private void switchToMarkSummaryActivity(boolean is_unreadable) {

		if (instanceUtitlity
				.isRegulation_R13_Mtech(Scrutiny_ScanAnswerBookActivity.this)) {
			confirmationAlertForRegulation(scannedBook.getText().toString()
					.trim(), Integer.parseInt(bundle_serial_no),
					"You are about to evaluate R13-M.Tech | M.Pharm | MBA |MCA ");
		} else if ((instanceUtitlity
				.isRegulation_R09_Course(Scrutiny_ScanAnswerBookActivity.this))) {
			confirmationAlertForRegulation(scannedBook.getText().toString()
					.trim(), Integer.parseInt(bundle_serial_no),
					"You are about to evaluate R09-B.Tech | M.Tech | B.Pharm | MBA |MCA |B.Tech-CCC ");
		} else if (instanceUtitlity
				.isRegulation_R13_Btech(Scrutiny_ScanAnswerBookActivity.this)) {
			confirmationAlertForRegulation(scannedBook.getText().toString()
					.trim(), Integer.parseInt(bundle_serial_no),
					"You are about to evaluate R13-B.Tech | B.Pharm ");  
		}else if (instanceUtitlity
				.isRegulation_R15_Btech(Scrutiny_ScanAnswerBookActivity.this)) { 
			confirmationAlertForRegulation(scannedBook.getText().toString()
					.trim(), Integer.parseInt(bundle_serial_no),
					"You are about to evaluate R15-B.Tech | B.Pharm ");
		} else if (instanceUtitlity
				.isRegulation_R15_Mtech(Scrutiny_ScanAnswerBookActivity.this)) {
			confirmationAlertForRegulation(scannedBook.getText().toString()
					.trim(), Integer.parseInt(bundle_serial_no),
					"You are about to evaluate R15-M.Tech | M.Pharm | MBA |MCA ");
		}
		

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
			if (getIntent().hasExtra(SSConstants.ADD_SCRIPT_CASE)) {
				addScriptCase(scannedBook.getText().toString().trim(), false);
			} else {
				checkBarcodeInDB();
			}
			break;
		case R.id.tv_back:
			finish();
			break;

		case R.id.tv_unread_barcode:

			if (fromAddScript) {
				unReadableBarCode();
			}

			else {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				unreadableBarcode(v);
			}
			break;

		default:
			break;
		}
	}

	private void showRemarksWithOrangeColor(String ansBookBarcode) {
		Cursor cursor;
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		Scrutiny_TempDatabase _tempDatabase = new Scrutiny_TempDatabase(this);
		cursor = _tempDatabase.getRow(Scrutiny_TempDatabase._SNo + " = '1'");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				_tempDatabase.deleteRow();
				cursor.moveToNext();
			}
		}
		cursor.close();

		ContentValues _contentValues = new ContentValues();
		cursor = _db_for_scrutiny.getRow(ansBookBarcode, null);
		if (cursor.getCount() > 0) {

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1E_REMARK)))) {
				_contentValues.put(SSConstants.M1E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2E_REMARK)))) {
				_contentValues.put(SSConstants.M2E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3E_REMARK)))) {
				_contentValues.put(SSConstants.M3E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4E_REMARK)))) {
				_contentValues.put(SSConstants.M4E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5E_REMARK)))) {
				_contentValues.put(SSConstants.M5E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6E_REMARK)))) {
				_contentValues.put(SSConstants.M6E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7E_REMARK)))) {
				_contentValues.put(SSConstants.M7E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8E_REMARK)))) {
				_contentValues.put(SSConstants.M8E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1A_REMARK)))) {
				_contentValues.put(SSConstants.M1A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1B_REMARK)))) {
				_contentValues.put(SSConstants.M1B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1C_REMARK)))) {
				_contentValues.put(SSConstants.M1C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1D_REMARK)))) {
				_contentValues.put(SSConstants.M1D_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2A_REMARK)))) {
				_contentValues.put(SSConstants.M2A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2B_REMARK)))) {
				_contentValues.put(SSConstants.M2B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2C_REMARK)))) {
				_contentValues.put(SSConstants.M2C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2D_REMARK)))) {
				_contentValues.put(SSConstants.M2D_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3A_REMARK)))) {
				_contentValues.put(SSConstants.M3A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3B_REMARK)))) {
				_contentValues.put(SSConstants.M3B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3C_REMARK)))) {
				_contentValues.put(SSConstants.M3C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3D_REMARK)))) {
				_contentValues.put(SSConstants.M3D_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4A_REMARK)))) {
				_contentValues.put(SSConstants.M4A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4B_REMARK)))) {
				_contentValues.put(SSConstants.M4B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4C_REMARK)))) {
				_contentValues.put(SSConstants.M4C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4D_REMARK)))) {
				_contentValues.put(SSConstants.M4D_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5A_REMARK)))) {
				_contentValues.put(SSConstants.M5A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5B_REMARK)))) {
				_contentValues.put(SSConstants.M5B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5C_REMARK)))) {
				_contentValues.put(SSConstants.M5C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5D_REMARK)))) {
				_contentValues.put(SSConstants.M5D_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6A_REMARK)))) {
				_contentValues.put(SSConstants.M6A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6B_REMARK)))) {
				_contentValues.put(SSConstants.M6B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6C_REMARK)))) {
				_contentValues.put(SSConstants.M6C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6D_REMARK)))) {
				_contentValues.put(SSConstants.M6D_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7A_REMARK)))) {
				_contentValues.put(SSConstants.M7A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7B_REMARK)))) {
				_contentValues.put(SSConstants.M7B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7C_REMARK)))) {
				_contentValues.put(SSConstants.M7C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7D_REMARK)))) {
				_contentValues.put(SSConstants.M7D_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8A_REMARK)))) {
				_contentValues.put(SSConstants.M8A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8B_REMARK)))) {
				_contentValues.put(SSConstants.M8B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8C_REMARK)))) {
				_contentValues.put(SSConstants.M8C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8D_REMARK)))) {
				_contentValues.put(SSConstants.M8D_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R1_REMARK)))) {
				_contentValues.put(SSConstants.R1_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R2_REMARK)))) {
				_contentValues.put(SSConstants.R2_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R3_REMARK)))) {
				_contentValues.put(SSConstants.R3_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R4_REMARK)))) {
				_contentValues.put(SSConstants.R4_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R5_REMARK)))) {
				_contentValues.put(SSConstants.R5_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R6_REMARK)))) {
				_contentValues.put(SSConstants.R6_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R7_REMARK)))) {
				_contentValues.put(SSConstants.R7_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R8_REMARK)))) {
				_contentValues.put(SSConstants.R8_REMARK,
						SSConstants.ORANGE_COLOR);
			}

		}
		cursor.close();
		_contentValues.put(Scrutiny_TempDatabase._SNo, "1");

		_tempDatabase.insertRow(_contentValues);
	}

	private void showRemarksWithOrangeColor_R13Btech(String ansBookBarcode) {
		Cursor cursor;
		SScrutinyDatabase _db_for_scrutiny = SScrutinyDatabase
				.getInstance(this);
		Scrutiny_TempDatabase _tempDatabase = new Scrutiny_TempDatabase(this);
		cursor = _tempDatabase.getRow(Scrutiny_TempDatabase._SNo + " = '1'");
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				_tempDatabase.deleteRow();
				cursor.moveToNext();
			}
		}
		cursor.close();

		ContentValues _contentValues = new ContentValues();
		cursor = _db_for_scrutiny.getRow(ansBookBarcode, null);
		if (cursor.getCount() > 0) {

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1A_REMARK)))) {
				_contentValues.put(SSConstants.M1A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1B_REMARK)))) {
				_contentValues.put(SSConstants.M1B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1C_REMARK)))) {
				_contentValues.put(SSConstants.M1C_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1D_REMARK)))) {
				_contentValues.put(SSConstants.M1D_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1E_REMARK)))) {
				_contentValues.put(SSConstants.M1E_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1F_REMARK)))) {
				_contentValues.put(SSConstants.M1F_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1G_REMARK)))) {
				_contentValues.put(SSConstants.M1G_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1H_REMARK)))) {
				_contentValues.put(SSConstants.M1H_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1I_REMARK)))) {
				_contentValues.put(SSConstants.M1I_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M1J_REMARK)))) {
				_contentValues.put(SSConstants.M1J_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2A_REMARK)))) {
				_contentValues.put(SSConstants.M2A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2B_REMARK)))) {
				_contentValues.put(SSConstants.M2B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M2C_REMARK)))) {
				_contentValues.put(SSConstants.M2C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3A_REMARK)))) {
				_contentValues.put(SSConstants.M3A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3B_REMARK)))) {
				_contentValues.put(SSConstants.M3B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M3C_REMARK)))) {
				_contentValues.put(SSConstants.M3C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4A_REMARK)))) {
				_contentValues.put(SSConstants.M4A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4B_REMARK)))) {
				_contentValues.put(SSConstants.M4B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M4C_REMARK)))) {
				_contentValues.put(SSConstants.M4C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5A_REMARK)))) {
				_contentValues.put(SSConstants.M5A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5B_REMARK)))) {
				_contentValues.put(SSConstants.M5B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M5C_REMARK)))) {
				_contentValues.put(SSConstants.M5C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6A_REMARK)))) {
				_contentValues.put(SSConstants.M6A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6B_REMARK)))) {
				_contentValues.put(SSConstants.M6B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M6C_REMARK)))) {
				_contentValues.put(SSConstants.M6C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7A_REMARK)))) {
				_contentValues.put(SSConstants.M7A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7B_REMARK)))) {
				_contentValues.put(SSConstants.M7B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M7C_REMARK)))) {
				_contentValues.put(SSConstants.M7C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8A_REMARK)))) {
				_contentValues.put(SSConstants.M8A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8B_REMARK)))) {
				_contentValues.put(SSConstants.M8B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M8C_REMARK)))) {
				_contentValues.put(SSConstants.M8C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M9A_REMARK)))) {
				_contentValues.put(SSConstants.M9A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M9B_REMARK)))) {
				_contentValues.put(SSConstants.M9B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M9C_REMARK)))) {
				_contentValues.put(SSConstants.M9C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M10A_REMARK)))) {
				_contentValues.put(SSConstants.M10A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M10B_REMARK)))) {
				_contentValues.put(SSConstants.M10B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M10C_REMARK)))) {
				_contentValues.put(SSConstants.M10C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M11A_REMARK)))) {
				_contentValues.put(SSConstants.M11A_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M11B_REMARK)))) {
				_contentValues.put(SSConstants.M11B_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.M11C_REMARK)))) {
				_contentValues.put(SSConstants.M11C_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R1_REMARK)))) {
				_contentValues.put(SSConstants.R1_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R2_REMARK)))) {
				_contentValues.put(SSConstants.R2_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R4_REMARK)))) {
				_contentValues.put(SSConstants.R4_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R6_REMARK)))) {
				_contentValues.put(SSConstants.R6_REMARK,
						SSConstants.ORANGE_COLOR);
			}

			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R8_REMARK)))) {
				_contentValues.put(SSConstants.R8_REMARK,
						SSConstants.ORANGE_COLOR);
			}
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(SSConstants.R10_REMARK)))) {
				_contentValues.put(SSConstants.R10_REMARK,
						SSConstants.ORANGE_COLOR);
			}

		}
		cursor.close();
		_contentValues.put(Scrutiny_TempDatabase._SNo, "1");

		_tempDatabase.insertRow(_contentValues);
	}

}
