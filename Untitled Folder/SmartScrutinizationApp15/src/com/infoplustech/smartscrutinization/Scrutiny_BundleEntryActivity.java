
package com.infoplustech.smartscrutinization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

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
import android.database.sqlite.SQLiteException;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.infoplustech.smartscrutinization.callback.Scrutiny_NetworkCallback;
import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.BundleMappingHandler;
import com.infoplustech.smartscrutinization.utils.BundleModel;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Scrutiny_SoapServiceManager;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_BundleEntryActivity extends Activity implements
		OnClickListener {

	EditText etBundleNo;
	String IMEI;  
	String userId, bundleNo, SeatNo;
	private ProgressDialog progressDialog;
	boolean scrutinySelection;
	private PowerManager.WakeLock wl;
	SScrutinyDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scrutiny_layout_bundle_no_entry);
//		Utility instanceUtility=new Utility();
//		if (!instanceUtility.isNetworkAvailable(this)) {
//			alertMessageForChargeAutoUpdateApk(getString(R.string.alert_network_avail));
//			return;
//		}
		database = SScrutinyDatabase.getInstance(this);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");

		TextView tvMode = (TextView) findViewById(R.id.tv_mode);
		TextView tvUserId = (TextView) findViewById(R.id.tv_h_user_id);
		SharedPreferences _sharedPreferences;
		_sharedPreferences = getSharedPreferences(
				SSConstants.SCRUTINY_SELECTED, Context.MODE_PRIVATE);
		scrutinySelection = _sharedPreferences.getBoolean(
				SSConstants.SCRUTINY_SELECTED, false);
		if (scrutinySelection) {
			tvMode.setText(getString(R.string.scrutiny));
			tvUserId.setText("Scrutinizer Id : ");
		} else {
			tvUserId.setText("Evaluator Id : ");
			tvMode.setText(getString(R.string.scrutiny_corr));
		}
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
//				WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		IMEI = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
				.getDeviceId();
		userId = getIntent().getStringExtra(SSConstants.USER_ID);
		((TextView) findViewById(R.id.tv_user_id)).setText(userId);
		
		SeatNo = getIntent().getStringExtra("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);
		  
		etBundleNo = (EditText) findViewById(R.id.et_bundle_no);
		etBundleNo.setFocusable(true);
		etBundleNo.setFocusableInTouchMode(true);
		etBundleNo.setOnKeyListener(new OnKeyListener() {  
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// if keydown and "enter" is pressed
				if ((keyCode == KeyEvent.KEYCODE_ENTER)
						&& (event.getAction() == KeyEvent.ACTION_DOWN)) {
					submit();
					return true;
				}
				return false;
			}
		});
		findViewById(R.id.btn_submit).setOnClickListener(this);
	}

	private void alertMessageForChargeAutoUpdateApk(String msg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);
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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_submit) {
			// here call webservice and submit to DB
			submit();
		}
	}

	// call this method when clicked on submit button
	private void submit() {
		bundleNo = ((EditText) findViewById(R.id.et_bundle_no)).getText()
				.toString().trim();

		// bundle number should not be empty and greater than 10 and less than
		// 12 if not temporary bundle
		if (!TextUtils.isEmpty(bundleNo)
				&& (bundleNo.length() == 11  || (bundleNo
						.length() == 12))) {
			String strQuery;
			if (scrutinySelection) {
			 strQuery = "select COUNT(*) as Value from table_user where TRIM(UPPER(login)) =TRIM(UPPER('"
					+ userId + "')) and active_status=1";
			}else{
			 strQuery = "select COUNT(*) as Value from table_user where TRIM(UPPER(login)) =TRIM(UPPER('"
					+ userId + "')) and active_status=2";
			}
			Cursor cursorq = database.executeSQLQuery(strQuery, null);
			if (cursorq != null && cursorq.getCount() > 0) {
				int _userCount = Integer.parseInt(cursorq.getString(0));
				if (_userCount > 0) {
				
			String _strQuery;
			
				_strQuery = "select count(*) as Value from table_marks_scrutinize "
						+ "where TRIM(UPPER(bundle_no)) = TRIM(UPPER('"
						+ bundleNo
						+ "'))";

			Cursor _cursor = database.executeSQLQuery(_strQuery, null);
			if (_cursor != null) {
				// check for pending ans scripts in local db
				if (_cursor.getCount() > 0) {
					int count = _cursor.getInt(0);
					if (count > 0) {

						int scriptsCount;

						if (scrutinySelection) {
							Cursor _cur_pending_scrutiny = database
									.passedQuery(
											SSConstants.TABLE_SCRUTINY_SAVE,
											SSConstants.BUNDLE_NO
													+ " = '"
													+ bundleNo.toUpperCase()
													+ "' AND "
													+ SSConstants.IS_SCRUTINIZED
													+ " = '0' AND "
													+ SSConstants.SCRUTINIZE_STATUS
													+ " <> '"
													+ SSConstants.SCRUTINY_STATUS_6_SCRIPT_COMPLETED
													+ "'",
											SSConstants.BUNDLE_SERIAL_NO);
							int serial_no_count_for_scrutiny = _cur_pending_scrutiny
									.getCount();

							if (serial_no_count_for_scrutiny >= 0) {

								Intent _intent;
								Cursor _cursor_scripts_count = database
										.passedQuery(
												SSConstants.TABLE_SCRUTINY_SAVE,
												SSConstants.BUNDLE_NO + " = '"
														+ bundleNo.toUpperCase() + "'", null);
								scriptsCount = _cursor_scripts_count.getCount();
								_cursor_scripts_count.close();
								if (0 == serial_no_count_for_scrutiny) {
									_intent = new Intent(this,
											Scrutiny_ShowGrandTotalSummaryTable.class);
									_intent.putExtra(SSConstants.BUNDLE_NO,
											bundleNo.toUpperCase());
									_intent.putExtra(SSConstants.USER_ID,
											userId);
									_intent.putExtra("SeatNo",
											SSConstants.SeatNo);
									String subjectC=null;
									if(bundleNo.length()==12)
										subjectC=bundleNo.substring(1, 7);
									else
										subjectC=bundleNo.substring(1, 6);
									
									_intent.putExtra(SSConstants.SUBJECT_CODE,
											subjectC);
									_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(_intent);
								} else if (scriptsCount >= serial_no_count_for_scrutiny) {
									_intent = new Intent(
											this,
											Scrutiny_SeriallyScanAnswerSheet.class);
									_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									_intent.putExtra(SSConstants.BUNDLE_NO,
											bundleNo.toUpperCase());
									_intent.putExtra("SeatNo",
											SSConstants.SeatNo);
									String subjectC=null;
									if(bundleNo.length()==12)
										subjectC=bundleNo.substring(1, 7);
									else
										subjectC=bundleNo.substring(1, 6);
									
									_intent.putExtra(SSConstants.SUBJECT_CODE,
											subjectC);
									_intent.putExtra(
											SSConstants.BUNDLE_SERIAL_NO,
											_cur_pending_scrutiny
													.getString(_cur_pending_scrutiny
															.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)));
									_intent.putExtra(SSConstants.USER_ID,
											userId);
									startActivity(_intent);
								}
							} else {

								showAlert(
										getString(R.string.alert_bun_already_scrutinized),
										getString(R.string.alert_dialog_ok), "");
							}
							_cur_pending_scrutiny.close();
						} else {
							// here check condition that scrutiny data is erased
							// while doing scrutiny correction of the same
							// bundle
							Cursor _cursor_erase_scrutiny_data = database
									.passedQuery(
											SSConstants.TABLE_SCRUTINY_SAVE,
											SSConstants.BUNDLE_NO + "='"
													+ bundleNo.toUpperCase() + "'", null);
							if (_cursor_erase_scrutiny_data != null
									&& _cursor_erase_scrutiny_data.getCount() > 0) {
								switchToShowGrandTotalSummaryTableActivity(
										bundleNo.toUpperCase(), true);
							} else {
								if (scrutinySelection) {
									showAlert(getString(R.string.enter_valid_map1_s),
											getString(R.string.alert_dialog_ok), "");
									}else{
										showAlert(getString(R.string.enter_valid_map1_c),
												getString(R.string.alert_dialog_ok), "");
									}
								// if bundle not exists in local DB then call
								// webservices
//								showProgress();
//								callWebServiceServices(bundleNo.toUpperCase());
							}
							_cursor_erase_scrutiny_data.close();

						}

					} else {
						if (scrutinySelection) {
							showAlert(getString(R.string.enter_valid_map1_s),
									getString(R.string.alert_dialog_ok), "");
							}else{
								showAlert(getString(R.string.enter_valid_map1_c),
										getString(R.string.alert_dialog_ok), "");
							}
						// if bundle not exists in local DB then call
						// webservices
						
//						showProgress();
//						callWebServiceServices(bundleNo.toUpperCase());
					}

				}
				_cursor.close();
			}
				} else {
					if (scrutinySelection) {
					showAlert(getString(R.string.enter_valid_map1_s),
							getString(R.string.alert_dialog_ok), "");
					}else{
						showAlert(getString(R.string.enter_valid_map1_c),
								getString(R.string.alert_dialog_ok), "");
					}
				}
			} else {
				if (scrutinySelection) {
				showAlert(getString(R.string.enter_valid_map1_s),
						getString(R.string.alert_dialog_ok), "");
				}else{
					showAlert(getString(R.string.enter_valid_map1_c),
							getString(R.string.alert_dialog_ok), "");
				}
			}
			if (cursorq != null) {
				cursorq.close();
			}
		} else {
			// show alert for entering invalid bundle
			showAlert(getString(R.string.alert_enter_valid_bundle),
					getString(R.string.alert_dialog_ok), "");
		}
	}

	private void switchToShowGrandTotalSummaryTableActivity(String bundleNo,
			boolean navFromLocalDB) {
		Intent _intent;
		if (scrutinySelection) {
			_intent = new Intent(this, Scrutiny_SeriallyScanAnswerSheet.class);
			_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo.toUpperCase());
			_intent.putExtra(SSConstants.USER_ID, userId);
			_intent.putExtra("SeatNo",
					SSConstants.SeatNo);

			// check whether any SCRUTINIZE_STATUS is 5 bec it may be next obs
			Cursor cursor_serial_no = database.passedQuery(
					SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.BUNDLE_NO
							+ " = '" + bundleNo + "' AND "
							+ SSConstants.SCRUTINIZE_STATUS + " = '"
							+ SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION
							+ "'", null);
			if (cursor_serial_no.getCount() > 0) {
				Cursor cursor_serial_no_from_db = database
						.passedQuery(
								SSConstants.TABLE_SCRUTINY_SAVE,
								SSConstants.BUNDLE_NO
										+ " = '"
										+ bundleNo
										+ "' AND "
										+ SSConstants.SCRUTINIZE_STATUS
										+ " = '"
										+ SSConstants.SCRUTINY_STATUS_5_NEXT_OBSERVATION
										+ "'", SSConstants.BUNDLE_SERIAL_NO);
				if (cursor_serial_no_from_db.getCount() > 0) {
					cursor_serial_no_from_db.moveToFirst();
					_intent.putExtra(
							SSConstants.BUNDLE_SERIAL_NO,
							cursor_serial_no_from_db.getString(cursor_serial_no_from_db
									.getColumnIndex(SSConstants.BUNDLE_SERIAL_NO)));
				}
				cursor_serial_no_from_db.close();
			} else {
				_intent.putExtra(SSConstants.BUNDLE_SERIAL_NO,
						String.valueOf(1));
			}

			cursor_serial_no.close();
			String subjectC=null;
			if(bundleNo.length()==12)
				subjectC=bundleNo.substring(1, 7);
			else
				subjectC=bundleNo.substring(1, 6);
			
			_intent.putExtra(SSConstants.SUBJECT_CODE,
					subjectC);
			_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(_intent);
		} else {
			// if navigation from local DB no need to set Revaluated ones to
			// empty barcode
			if (!navFromLocalDB) {
				setVauesToReValuatedOnes();
			}
			_intent = new Intent(this,
					Scrutiny_ShowGrandTotalSummaryTable.class);
			_intent.putExtra(SSConstants.BUNDLE_NO, bundleNo);
			_intent.putExtra(SSConstants.USER_ID, userId);  
			_intent.putExtra("SeatNo",
					SSConstants.SeatNo);
			_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
			startActivity(_intent);
		}
	}

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
						etBundleNo.setText("");
						etBundleNo.setFocusable(true);
						etBundleNo.setFocusableInTouchMode(true);
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

	// method for calling webservices
	private void callWebServiceServices(String bundleNo) {
		Scrutiny_SoapServiceManager manager = Scrutiny_SoapServiceManager
				.getInstance(Scrutiny_BundleEntryActivity.this);
		manager.getMarkDetailsForObservation(bundleNo.toUpperCase(), userId, IMEI, callback,
				scrutinySelection);
	}

	Scrutiny_NetworkCallback<Object> callback = new Scrutiny_NetworkCallback<Object>() {
		@Override
		public void onSuccess(Object object) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = null;
			try {
				saxParser = factory.newSAXParser();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BundleMappingHandler handler = new BundleMappingHandler(
					Scrutiny_BundleEntryActivity.this);
			try {
				saxParser.parse(new ByteArrayInputStream(object.toString()
						.getBytes()), handler);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<BundleModel> list = handler.getBundleMappingList();

			// updating regulation here
			if (list.size() > 0) {
				String _response_query = list.get(0).getBundleMappingResponse();
				String _regulation = list.get(0).getRegulation();
				ContentValues _values = new ContentValues();
				_values.put(SSConstants.REGULATION, _regulation);
				database.updateRow2(SSConstants.TABLE_DATE_CONFIGURATION,
						_values, "id=1");

				if (scrutinySelection) {
					// scrutiny observation
					responseFromServiceForObservation(_response_query);

					hideProgress();
				} else {
					// scrutiny correction
					responseFromServiceForCorrection(_response_query);
					hideProgress();
				}
			} else {
				hideProgress();
				showAlert("Failed to Retrieve list data from Server \n\n"
						+ "Server message : \n  " + object.toString(), "Ok", "");
			}
		}

		@Override
		public void onFailure(String errorMessge) {
			hideProgress();
			if (errorMessge.contains("101")) {
				alertMessageForChargeAutoUpdateApk("Failed to Retrieve Data from Server \n\n"
						+ "Server message : \n  "
						+ errorMessge
						+ "\n\n"
						+ getString(R.string.alert_network_avail));
			} else {
				showAlert("Failed to Retrieve Data from Server \n\n"
						+ "Server message : \n  " + errorMessge, "Ok", "");
			}
		}

	};

	public void responseFromServiceForObservation(Object object) {
		if ((object.toString().equalsIgnoreCase("Mapping does not exist"))
				|| (object.toString()
						.contains("No scripts available for observation"))
				|| (object.toString()
						.equalsIgnoreCase("Bundle already completed observation"))
				|| (object.toString()
						.equalsIgnoreCase("INSERT INTO table_marks_scrutinize  (bundlestatus)   SELECT 'Bundle already completed observation'"))) {
			etBundleNo.setText("");
			etBundleNo.setFocusable(true);
			etBundleNo.setFocusableInTouchMode(true);
			showAlert(object.toString(), "Ok", "");
		}

		else {
			String _strQuery = "select count(distinct bundle_serial_no) as Value from table_marks_scrutinize "
					+ "where TRIM(UPPER(bundle_no)) = TRIM(UPPER('"
					+ bundleNo
					+ "'))";
			Cursor _cursor = database.executeSQLQuery(_strQuery, null);
			if (_cursor.getCount() > 0) {
				int _userCount = Integer.parseInt(_cursor.getString(0));
				if (_userCount > 0) {

					try {
						showAlert("Bundle Already Exist in DataBase", "Ok",
								"");
						/*database.deleteBundle(SSConstants.TABLE_SCRUTINY_SAVE,
								bundleNo);
						database.deleteBundle(SSConstants.TABLE_EVALUATION_SAVE,
								bundleNo);
						database.executeSQLQuery(object.toString(), null)
								.close();
						Log.v("data", "server "+object.toString());
						database.executeSQLQuery(object.toString().replace(SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.TABLE_EVALUATION_SAVE), null)
						.close();
						removeMarksDB();
						switchToShowGrandTotalSummaryTableActivity(etBundleNo
								.getText().toString().trim().toUpperCase(), false);*/
					} catch (SQLiteException sqle) {
						showAlert(getString(R.string.unable_query_exec), "Ok",
								"");
					}
				}

				else {
					try {
						database.executeSQLQuery(object.toString(), null)
								.close();
						Log.v("data", "server "+object.toString());
					} catch (SQLiteException sqle) {
						showAlert(getString(R.string.unable_query_exec), "Ok",
								"");
					}
					
					try {
						database.executeSQLQuery(object.toString().replace(SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.TABLE_EVALUATION_SAVE), null)
						.close();
					} catch (SQLiteException sqle) {
						showAlert(getString(R.string.unable_query_exec), "Ok",
								"");
					}
					try {
						removeMarksDB();
						switchToShowGrandTotalSummaryTableActivity(etBundleNo
								.getText().toString().trim().toUpperCase(), false);
					} catch (SQLiteException sqle) {
						showAlert(getString(R.string.unable_query_exec), "Ok",
								"");
					}
					
				}
			}
			_cursor.close();
		}

	}
	private void removeMarksDB() {
		ContentValues setInContentValue = new ContentValues();
		// Marks1
		setInContentValue.putNull(SSConstants.MARK1A);
		setInContentValue.putNull(SSConstants.MARK1B);
		setInContentValue.putNull(SSConstants.MARK1C);
		setInContentValue.putNull(SSConstants.MARK1D);
		setInContentValue.putNull(SSConstants.MARK1E);
		setInContentValue.putNull(SSConstants.MARK1F);
		setInContentValue.putNull(SSConstants.MARK1G);
		setInContentValue.putNull(SSConstants.MARK1H);
		setInContentValue.putNull(SSConstants.MARK1I);
		setInContentValue.putNull(SSConstants.MARK1J);
		setInContentValue.putNull(SSConstants.R1_TOTAL);
// Marks2
		setInContentValue.putNull(SSConstants.MARK2A);
		setInContentValue.putNull(SSConstants.MARK2B);
		setInContentValue.putNull(SSConstants.MARK2C);
		setInContentValue.putNull(SSConstants.MARK2D);
		setInContentValue.putNull(SSConstants.MARK2E);
		setInContentValue.putNull(SSConstants.R2_TOTAL);
		
// Marks3
		setInContentValue.putNull(SSConstants.MARK3A);
		setInContentValue.putNull(SSConstants.MARK3B);
		setInContentValue.putNull(SSConstants.MARK3C);
		setInContentValue.putNull(SSConstants.MARK3D);
		setInContentValue.putNull(SSConstants.MARK3E);
		setInContentValue.putNull(SSConstants.R3_TOTAL);

// Marks4
		setInContentValue.putNull(SSConstants.MARK4A);
		setInContentValue.putNull(SSConstants.MARK4B);
		setInContentValue.putNull(SSConstants.MARK4C);
		setInContentValue.putNull(SSConstants.MARK4D);
		setInContentValue.putNull(SSConstants.MARK4E);
		setInContentValue.putNull(SSConstants.R4_TOTAL);

// Marks5
		setInContentValue.putNull(SSConstants.MARK5A);
		setInContentValue.putNull(SSConstants.MARK5B);
		setInContentValue.putNull(SSConstants.MARK5C);
		setInContentValue.putNull(SSConstants.MARK5D);
		setInContentValue.putNull(SSConstants.MARK5E);
		setInContentValue.putNull(SSConstants.R5_TOTAL);

// Marks6
		setInContentValue.putNull(SSConstants.MARK6A);
		setInContentValue.putNull(SSConstants.MARK6B);
		setInContentValue.putNull(SSConstants.MARK6C);
		setInContentValue.putNull(SSConstants.MARK6D);
		setInContentValue.putNull(SSConstants.MARK6E);
		setInContentValue.putNull(SSConstants.R6_TOTAL);

// Marks7
		setInContentValue.putNull(SSConstants.MARK7A);
		setInContentValue.putNull(SSConstants.MARK7B);
		setInContentValue.putNull(SSConstants.MARK7C);
		setInContentValue.putNull(SSConstants.MARK7D);
		setInContentValue.putNull(SSConstants.MARK7E);
		setInContentValue.putNull(SSConstants.R7_TOTAL);

// Marks8
		setInContentValue.putNull(SSConstants.MARK8A);
		setInContentValue.putNull(SSConstants.MARK8B);
		setInContentValue.putNull(SSConstants.MARK8C);
		setInContentValue.putNull(SSConstants.MARK8D);
		setInContentValue.putNull(SSConstants.MARK8E);
		setInContentValue.putNull(SSConstants.R8_TOTAL);

		// mark9
		setInContentValue.putNull(SSConstants.MARK9A);
		setInContentValue.putNull(SSConstants.MARK9B);
		setInContentValue.putNull(SSConstants.MARK9C);

		// mark10
		setInContentValue.putNull(SSConstants.MARK10A);
		setInContentValue.putNull(SSConstants.MARK10B);
		setInContentValue.putNull(SSConstants.MARK10C);

		// mark11
		setInContentValue.putNull(SSConstants.MARK11A);
		setInContentValue.putNull(SSConstants.MARK11B);
		setInContentValue.putNull(SSConstants.MARK11C);

		setInContentValue.putNull(SSConstants.R2_3TOTAL);
		setInContentValue.putNull(SSConstants.R4_5TOTAL);
		setInContentValue.putNull(SSConstants.R6_7TOTAL);
		setInContentValue.putNull(SSConstants.R8_9TOTAL);
		setInContentValue.putNull(SSConstants.R10_11TOTAL);
		setInContentValue.putNull(SSConstants.GRAND_TOTAL_MARK);

		insertToEvaluationDB(setInContentValue); 
		}
	private void insertToEvaluationDB(ContentValues contentValues) {
		database.updateRow(SSConstants.TABLE_EVALUATION_SAVE,
					contentValues, SSConstants.BUNDLE_NO + " = '" + bundleNo + "'");
	}
	public void responseFromServiceForCorrection(Object object) {
		try {
			if ((object.toString().equalsIgnoreCase("Mapping does not exist"))
					|| (object.toString()
							.equalsIgnoreCase("Bundle already completed correction"))
					|| object.toString().equalsIgnoreCase(
							"No scripts available for correction")) {
				etBundleNo.setText("");
				etBundleNo.setFocusable(true);
				etBundleNo.setFocusableInTouchMode(true);
				showAlert(object.toString(), "Ok", "");
			}

			else {

				String _strQueryForMarks = "select count(distinct bundle_serial_no) as Value from table_marks"
						+ " where TRIM(UPPER(bundle_no)) = TRIM(UPPER('"
						+ bundleNo + "'))";

				Cursor _cursorForMarks = database.executeSQLQuery(
						_strQueryForMarks, null);
				if (_cursorForMarks.getCount() > 0) {
					
//					database.deleteBundle(SSConstants.TABLE_SCRUTINY_REQUEST,
//							bundleNo);
				}
				_cursorForMarks.close();

				String _strQuery = "select count(distinct bundle_serial_no) as Value from table_marks_scrutinize "
						+ "where TRIM(UPPER(bundle_no)) = TRIM(UPPER('"
						+ bundleNo + "'))";

				Cursor _cursor = database.executeSQLQuery(_strQuery, null);
				if (_cursor.getCount() > 0) {
					int _userCount = Integer.parseInt(_cursor.getString(0));
					if (_userCount > 0) {
						// delete records related to scanned bundle
						showAlert("Bundle Already Exist in DataBase", "Ok",
								"");
					/*	database.deleteBundle(SSConstants.TABLE_SCRUTINY_SAVE,
								bundleNo);
						database.deleteBundle(SSConstants.TABLE_EVALUATION_SAVE,
								bundleNo);
						// insert records related to scanned bundle

						String tableDatas[] = object.toString().split(";");
						String table_marks_scrutinize = tableDatas[0];
						table_marks_scrutinize.replace("'null'", "NULL");
						String table_marks = tableDatas[1];
						table_marks.replace("'null'", "NULL");
// hari insert
						Log.v("data", "server "+table_marks_scrutinize);
						database.executeSQLQuery(table_marks_scrutinize, null)
								.close();
						database.executeSQLQuery(table_marks, null).close();
//						database.executeSQLQuery(table_marks_scrutinize.replace(SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.TABLE_EVALUATION_SAVE), null)
//						.close();
						switchToShowGrandTotalSummaryTableActivity(etBundleNo
								.getText().toString().trim().toUpperCase(), false);*/

					}

					else {
						String tableDatas[] = object.toString().split(";");
						String table_marks_scrutinize = tableDatas[0];
						table_marks_scrutinize.replace("'null'", "NULL");
						String table_marks = tableDatas[1];
						table_marks.replace("'null'", "NULL");
Log.v("data", "server "+table_marks_scrutinize);
						database.executeSQLQuery(table_marks_scrutinize, null)
								.close();
						database.executeSQLQuery(table_marks, null).close();
//						database.executeSQLQuery(table_marks_scrutinize.replace(SSConstants.TABLE_SCRUTINY_SAVE, SSConstants.TABLE_EVALUATION_SAVE), null)
//						.close();
						switchToShowGrandTotalSummaryTableActivity(etBundleNo
								.getText().toString().trim().toUpperCase(), false);
					}
				}
				_cursor.close();

			}
		} catch (Exception e) {
			showAlert(getString(R.string.unable_query_exec), "Ok", "");
		}

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

	public void showProgress() {
		progressDialog = ProgressDialog.show(this, "", "Verifying Bundle ...");
		progressDialog.setCancelable(false);
	}

	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	// setting values for revaluated ones
	private void setVauesToReValuatedOnes() {
		if (!scrutinySelection) {
			Cursor cursor;
			cursor = database
					.passedQuery(
							SSConstants.TABLE_SCRUTINY_SAVE,
							SSConstants.SCRUTINIZE_STATUS
									+ " = '"
									+ SSConstants.SCRUTINY_STATUS_2_BARCODE_AND_SLNO_MISMATCH
									+ "' AND " + SSConstants.BUNDLE_NO + " = '"
									+ bundleNo + "'", null);

			if (cursor.getCount() > 0) {
				int serialNumber;
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {

					serialNumber = Integer.parseInt(cursor.getString(cursor
							.getColumnIndex("bundle_serial_no")));
//harinath changes bundle_id,enter_on,is_updated_server,transferred_on
					String insertQuery = "INSERT INTO "+SSConstants.TABLE_SCRUTINY_SAVE+"(bundle_serial_no,bundle_no,subject_code,scrutinize_status,is_scrutinized,scrutinized_by,scrutinized_on,spot_centre_code,barcode_status,max_total,bundle_id,enter_on,is_updated_server,transferred_on)"
							+ "SELECT bundle_serial_no,bundle_no,subject_code,scrutinize_status, is_scrutinized, scrutinized_by, scrutinized_on,spot_centre_code,barcode_status,max_total,bundle_id,enter_on,is_updated_server,transferred_on FROM "+SSConstants.TABLE_SCRUTINY_SAVE+" WHERE bundle_no='"
							+ bundleNo
							+ "' AND bundle_serial_no='"
							+ serialNumber + "' AND scrutinize_status = 2";
					String insertQuery1 = "INSERT INTO "+SSConstants.TABLE_EVALUATION_SAVE+"(bundle_serial_no,bundle_no,subject_code,scrutinize_status,is_scrutinized,scrutinized_by,scrutinized_on,spot_centre_code,barcode_status,max_total,bundle_id,enter_on,is_updated_server,transferred_on)"
							+ "SELECT bundle_serial_no,bundle_no,subject_code,scrutinize_status, is_scrutinized, scrutinized_by, scrutinized_on,spot_centre_code,barcode_status,max_total,bundle_id,enter_on,is_updated_server,transferred_on FROM "+SSConstants.TABLE_EVALUATION_SAVE+" WHERE bundle_no='"
							+ bundleNo
							+ "' AND bundle_serial_no='"
							+ serialNumber + "' AND scrutinize_status = 2";
					database.executeSQLQuery(insertQuery, null).close();
					database.executeSQLQuery(insertQuery1, null).close();
					String deleteQuery = "DELETE FROM "+SSConstants.TABLE_SCRUTINY_SAVE+" WHERE bundle_no='"
							+ bundleNo
							+ "' AND bundle_serial_no='"
							+ serialNumber + "' AND barcode IS NOT NULL";
					String deleteQuery1 = "DELETE FROM "+SSConstants.TABLE_EVALUATION_SAVE+" WHERE bundle_no='"
							+ bundleNo
							+ "' AND bundle_serial_no='"
							+ serialNumber + "' AND barcode IS NOT NULL";
					database.executeSQLQuery(deleteQuery, null).close();
					database.executeSQLQuery(deleteQuery1, null).close();
					cursor.moveToNext();

					// serialNumber = Integer.parseInt(cursor.getString(cursor
					// .getColumnIndex("bundle_serial_no")));
					//
					// String insertQuery =
					// "INSERT INTO table_marks_scrutinize(bundle_serial_no,bundle_no,subject_code,scrutinize_status,is_scrutinized,scrutinized_by,scrutinized_on,spot_centre_code,barcode_status)"
					// +
					// "SELECT bundle_serial_no,bundle_no,subject_code,scrutinize_status, is_scrutinized, scrutinized_by, scrutinized_on,spot_centre_code,barcode_status FROM table_marks_scrutinize WHERE bundle_no='"
					// + bundleNo
					// + "' AND bundle_serial_no='"
					// + serialNumber + "' AND scrutinize_status = 2";
					// _database.executeSQLQuery(insertQuery, null).close();
					//
					// String deleteQuery =
					// "DELETE FROM table_marks_scrutinize WHERE bundle_no='"
					// + bundleNo
					// + "' AND bundle_serial_no='"
					// + serialNumber + "' AND barcode IS NOT NULL";
					// _database.executeSQLQuery(deleteQuery, null).close();
					// cursor.moveToNext();
				}
			}
			cursor.close();
			database.close();

		}

	}

	BroadcastReceiver batteryLevelReceiver;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		batteryLevel();
		if (wl != null) {
			wl.acquire();
		}
	}

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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (progressDialog != null)
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
	}
}
