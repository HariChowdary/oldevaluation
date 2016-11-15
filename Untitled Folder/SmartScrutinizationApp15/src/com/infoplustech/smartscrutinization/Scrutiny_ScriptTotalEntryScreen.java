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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Scrutiny_DataBaseUtility;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_ScriptTotalEntryScreen extends Activity implements
		OnClickListener {

	String userId;
	String subjectCode;
	String bundleNo;
	String bundleSerialNo;
	private EditText etTotalMarks;
	Intent _intent_extras;

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_total_entry_screen);
		instanceUtitlity = new Utility();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");

		etTotalMarks = (EditText) findViewById(R.id.et_total_marks);
		_intent_extras = getIntent();
		subjectCode = _intent_extras.getStringExtra(SSConstants.SUBJECT_CODE);
		userId = _intent_extras.getStringExtra(SSConstants.USER_ID);
		bundleNo = _intent_extras.getStringExtra(SSConstants.BUNDLE_NO);
		bundleSerialNo = _intent_extras
				.getStringExtra(SSConstants.BUNDLE_SERIAL_NO);

		((TextView) findViewById(R.id.tv_sub_code)).setText(subjectCode);
		((TextView) findViewById(R.id.tv_user_id)).setText(userId);
		((TextView) findViewById(R.id.tv_bundle_no)).setText(bundleNo);
		((TextView) findViewById(R.id.tv_bundle_serial_no))
				.setText(bundleSerialNo);

		findViewById(R.id.btn_total_marks_submit).setOnClickListener(this);
		etTotalMarks.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					submit();
					return true;
				}
				return false;
			}
		});
	}

	// submit the data
	private void submit() {
		int _total_from_db=0;
		String _barcode = _intent_extras
				.getStringExtra(SSConstants.ANS_BOOK_BARCODE);
		Cursor cursor = null;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
		String _total_marks = etTotalMarks.getText().toString().trim();
		cursor = _database
				.passedQuery(SSConstants.TABLE_SCRUTINY_SAVE,
						SSConstants.ANS_BOOK_BARCODE + " = '" + _barcode
								+ "' AND " + SSConstants.BUNDLE_NO + " = '"
								+ bundleNo + "'", null);
			if (cursor.getCount() > 0 && cursor != null) {
		_total_from_db = cursor.getInt(cursor
				.getColumnIndex(SSConstants.GRAND_TOTAL_MARK));
			}
		Scrutiny_DataBaseUtility.closeCursor(cursor);
		if (!TextUtils.isEmpty(_total_marks)) {
			if (_total_from_db == Integer.valueOf(_total_marks)) {
				navigateToMarkDialogScreen(_barcode, _total_marks);
			} else {
				alertMessageForMarksMisMatch(_barcode, _total_marks);
			} 
		} else {
			alertMessage("Please Enter Valid Marks");
		}
	}

	// navigate to MarkDialog screen
	private void navigateToMarkDialogScreen(String barcode, String _total_marks) {

		if (instanceUtitlity
				.isRegulation_R13_Mtech(Scrutiny_ScriptTotalEntryScreen.this)) {
			confirmationAlertForRegulation(barcode, _total_marks,
					"You are about to scrutiny R13-M.Tech | M.Pharm | MBA |MCA ");
		} else if ((instanceUtitlity
				.isRegulation_R09_Course(Scrutiny_ScriptTotalEntryScreen.this))) {
			confirmationAlertForRegulation(barcode, _total_marks,
					"You are about to scrutiny R09-B.Tech | M.Tech | B.Pharm | MBA |MCA ");
		} else if (instanceUtitlity
				.isRegulation_R13_Btech(Scrutiny_ScriptTotalEntryScreen.this)) {
			confirmationAlertForRegulation(barcode, _total_marks,
					"You are about to scrutiny R13-B.Tech | B.Pharm ");
		}   

	}   

	public void confirmationAlertForRegulation(final String pBarcode,
			final String pTotalMarks, final String getMsg) {   
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				Scrutiny_ScriptTotalEntryScreen.this);
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
										.isRegulation_R09_Course(Scrutiny_ScriptTotalEntryScreen.this)) {
							Intent _intent = new Intent(
									Scrutiny_ScriptTotalEntryScreen.this,  
									Scrutiny_MarkDialog.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundleSerialNo);

							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (_intent_extras
									.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							if (_intent_extras.hasExtra(SSConstants.NOTE_DATE)) {
								_intent.putExtra(
										SSConstants.NOTE_DATE,
										_intent_extras
												.getStringExtra(SSConstants.NOTE_DATE));
							}

							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
						} 
						else if(instanceUtitlity
								.isRegulation_R13_Mtech(Scrutiny_ScriptTotalEntryScreen.this))
						{
							Intent _intent = new Intent(
									Scrutiny_ScriptTotalEntryScreen.this,
									Scrutiny_MarkDialog_R13.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundleSerialNo);

							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (_intent_extras
									.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							if (_intent_extras.hasExtra(SSConstants.NOTE_DATE)) {
								_intent.putExtra(
										SSConstants.NOTE_DATE,
										_intent_extras
												.getStringExtra(SSConstants.NOTE_DATE));
							}

							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
						}
						else if (instanceUtitlity
								.isRegulation_R13_Btech(Scrutiny_ScriptTotalEntryScreen.this)) {
							if( subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AG_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AH_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AJ_ENGG_DRAWING) ||
									   subjectCode.equalsIgnoreCase(SSConstants.SUBJ_111AK_ENGG_DRAWING) ){
							Intent _intent = new Intent(
									Scrutiny_ScriptTotalEntryScreen.this,
									Scrutiny_MarkDialog_R13_BTech_SpecialCase.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundleSerialNo);

							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (_intent_extras
									.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							if (_intent_extras.hasExtra(SSConstants.NOTE_DATE)) {
								_intent.putExtra(
										SSConstants.NOTE_DATE,
										_intent_extras
												.getStringExtra(SSConstants.NOTE_DATE));
							}

							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
							}else if(subjectCode.equalsIgnoreCase(SSConstants.SUBJ_114DA_ENGG_DRAWING)
									|| subjectCode.equalsIgnoreCase(SSConstants.SUBJ_114DB_ENGG_DRAWING)){

								Intent _intent = new Intent(
										Scrutiny_ScriptTotalEntryScreen.this,
										Scrutiny_MarkDialog_R13_BTech_SpecialCase_New.class);
								_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

								_intent.putExtra(SSConstants.SUBJECT_CODE,
										subjectCode);
								_intent.putExtra(SSConstants.USER_ID, userId);
								_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
								_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
										bundleSerialNo);

								_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
										pBarcode);
								if (_intent_extras
										.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

									_intent.putExtra(
											SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
											SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
								}

								if (_intent_extras.hasExtra(SSConstants.NOTE_DATE)) {
									_intent.putExtra(
											SSConstants.NOTE_DATE,
											_intent_extras
													.getStringExtra(SSConstants.NOTE_DATE));
								}

								_intent.putExtra(
										SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
										pTotalMarks);
								startActivity(_intent);
								
							}
								
								else{

								Intent _intent = new Intent(
										Scrutiny_ScriptTotalEntryScreen.this,
										Scrutiny_MarkDialog_R13.class);
								_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

								_intent.putExtra(SSConstants.SUBJECT_CODE,
										subjectCode);
								_intent.putExtra(SSConstants.USER_ID, userId);
								_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
								_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
										bundleSerialNo);

								_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
										pBarcode);
								if (_intent_extras
										.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

									_intent.putExtra(
											SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
											SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
								}

								if (_intent_extras.hasExtra(SSConstants.NOTE_DATE)) {
									_intent.putExtra(
											SSConstants.NOTE_DATE,
											_intent_extras
													.getStringExtra(SSConstants.NOTE_DATE));
								}

								_intent.putExtra(
										SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
										pTotalMarks);
								startActivity(_intent);
								
							}
						}else if(instanceUtitlity
								.isRegulation_R15_Btech(Scrutiny_ScriptTotalEntryScreen.this))
						{
							Intent _intent = new Intent(
									Scrutiny_ScriptTotalEntryScreen.this,
									Scrutiny_MarkDialog_R15.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundleSerialNo);

							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (_intent_extras
									.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							if (_intent_extras.hasExtra(SSConstants.NOTE_DATE)) {
								_intent.putExtra(
										SSConstants.NOTE_DATE,
										_intent_extras
												.getStringExtra(SSConstants.NOTE_DATE));
							}

							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
						}else if(instanceUtitlity
								.isRegulation_R15_Mtech(Scrutiny_ScriptTotalEntryScreen.this))
						{
							Intent _intent = new Intent(
									Scrutiny_ScriptTotalEntryScreen.this,
									Scrutiny_MarkDialog_R15.class);
							_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							_intent.putExtra(SSConstants.SUBJECT_CODE,
									subjectCode);
							_intent.putExtra(SSConstants.USER_ID, userId);
							_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
							_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
									bundleSerialNo);

							_intent.putExtra(SSConstants.ANS_BOOK_BARCODE,
									pBarcode);
							if (_intent_extras
									.hasExtra(SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY)) {

								_intent.putExtra(
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY,
										SSConstants.NAVIGATION_FROM_SHOW_GRAND_TOTAL_ACTIVITY);
							}

							if (_intent_extras.hasExtra(SSConstants.NOTE_DATE)) {
								_intent.putExtra(
										SSConstants.NOTE_DATE,
										_intent_extras
												.getStringExtra(SSConstants.NOTE_DATE));
							}

							_intent.putExtra(
									SSConstants.SCRUTINY_TOTAL_MARKS_FOR_SCRIPT,
									pTotalMarks);
							startActivity(_intent);
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

	// alert for showing mismatch marks
	private void alertMessageForMarksMisMatch(final String barcode,
			final String _total_marks) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog
				.setMessage("The Total Marks you entered :"
						+ _total_marks
						+ "\n is not matching with Tablet marks.\n Please put the Remarks");
		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						Dialog.dismiss();
						navigateToMarkDialogScreen(barcode, _total_marks);
					}
				});
		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						Dialog.dismiss();
						etTotalMarks.setText("");
					}
				});
		myAlertDialog.show();
	}

	// alert msg
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
@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
//	super.onBackPressed();
}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_total_marks_submit) {
			submit();
		}
	}
}
