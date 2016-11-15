package com.infoplustech.smartscrutinization;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_EvaluatorLogin extends Activity implements OnClickListener {
	EditText etUserId;
	int alertShown = 1;
	String tableDate, SeatNo;
	SScrutinyDatabase _database;
	private PowerManager.WakeLock wl;
	BroadcastReceiver batteryLevelReceiver;  
	private ProgressDialog progressDialog;
	private boolean scrutinySelection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");
		setContentView(R.layout.scrutiny_eval_login);  
		// check for internet connection
//		Utility instanceUtitlity = new Utility();
//		if (!instanceUtitlity.isNetworkAvailable(this)) {
//			alertboxNWAvail("", getString(R.string.alert_network_avail));
//			return;
//		} else {
//			showProgress();
//			RunInBackground(true);
//		}
		Bundle b = getIntent().getExtras();
		SeatNo = b.getString("SeatNo");
		((TextView) findViewById(R.id.tv_seat_no)).setText(SeatNo);
		
		TextView tvMode = (TextView) findViewById(R.id.tv_mode);
		SharedPreferences _sharedPreferences;
		_sharedPreferences = getSharedPreferences(
				SSConstants.SCRUTINY_SELECTED, Context.MODE_PRIVATE);
		scrutinySelection = _sharedPreferences.getBoolean(
				SSConstants.SCRUTINY_SELECTED, true);
		if (scrutinySelection) {
			tvMode.setText(getString(R.string.scrutiny));
		} else {
			tvMode.setText(getString(R.string.scrutiny_corr));
		}
		etUserId = (EditText) findViewById(R.id.et_eval_id);
		etUserId.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				// if keydown and "enter" is pressed
				if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
					if (alertShown == 1) {
						switchToBundleEntryActivity();
					}
					alertShown++;
					return true;
				}
				return false;
			}
		});
		findViewById(R.id.btn_submit).setOnClickListener(this);
	}

	// get IP Address from smartConfig.xml file from Tablet
	private void RunInBackground(final boolean fromOnCreate) {

		new AsyncTask<Void, Void, Void>() {
			String retrieveString;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				
				// set time limit here
				
//				if(fromOnCreate && scrutinySelection) {
//					webServiceForTimeLimit();
//				}
				
				Cursor cur1 = null;
				_database = new SScrutinyDatabase(Scrutiny_EvaluatorLogin.this);

				String userCount = null;
				try {
					cur1 = _database
							.executeSQLQuery(
									"select ifnull(max(login_id),0) as userCount from  table_user",
									null);
					for (cur1.moveToFirst(); !(cur1.isAfterLast()); cur1
							.moveToNext()) {
						userCount = cur1.getString(cur1
								.getColumnIndex("userCount"));
					}
				} catch (Exception ex) {
				} finally {
					if (cur1 != null) {
						cur1.close();
					}
				}

				StringBuffer strBuf;
				strBuf = new StringBuffer(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?>");
				strBuf.append("<CountStatus>");
				strBuf.append("<usercount>");
				strBuf.append(userCount);
				strBuf.append("</usercount>");
				strBuf.append("</CountStatus>");
				try {
					retrieveString = webServiceforUnreadableBundle(strBuf
							.toString());
				} catch (Exception e) {
					retrieveString = "failedtoconnect" + e.getMessage();
				}

				if (!retrieveString.equalsIgnoreCase("0")) {
					SScrutinyDatabase db = new SScrutinyDatabase(
							Scrutiny_EvaluatorLogin.this);
					// execute query from service
					try {
						db.executeSQLQuery(retrieveString, null).close();
					} catch (SQLiteException ex) {
						retrieveString = "failedtoconnect" + ex.getMessage();
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (retrieveString.equals("0")) {
					hideProgress();
				}
				if (retrieveString == null) {
					hideProgress();
					AlertShowForUserUpdate("Null Pointer Exception");
				} else if (retrieveString.contains("failedtoconnect")) {
					hideProgress();
					AlertShowForUserUpdate(retrieveString);
				} else if (!retrieveString.equals("0")) {
					RunInBackground(false);
				}
			}
		}.execute();
	}

	private void AlertShowForUserUpdate(String msg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(Scrutiny_EvaluatorLogin.this,
						R.style.alert_text_style));
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog.setMessage(msg);
		myAlertDialog.setPositiveButton(getString(R.string.alert_dialog_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is
						// clicked
						Dialog.dismiss();
					}
				});
		myAlertDialog.show();
	}

	// alert dialog display
	private void alertboxNWAvail(String title, String mymessage) {
		new AlertDialog.Builder(new ContextThemeWrapper(this,
				R.style.alert_text_style))
				.setMessage(mymessage)
				.setTitle(title)
				.setCancelable(false)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
							}
						}).show();
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
	Boolean scrutinyAvailable(int val){
		
		
		return null;
	}
	public void switchToBundleEntryActivity() {
		String _userId = etUserId.getText().toString().trim();
		if (!TextUtils.isEmpty(_userId)) {
			SScrutinyDatabase _database = SScrutinyDatabase.getInstance(this);
			String _strQuery;
			if (scrutinySelection) {
			 _strQuery = "select COUNT(*) as Value from table_user where TRIM(UPPER(login)) =TRIM(UPPER('"
					+ _userId + "')) and active_status=1";
			}else{
			 _strQuery = "select COUNT(*) as Value from table_user where TRIM(UPPER(login)) =TRIM(UPPER('"
					+ _userId + "')) and active_status=2";
			}
			Cursor _cursor = _database.executeSQLQuery(_strQuery, null);
			if (_cursor != null && _cursor.getCount() > 0) {
				int _userCount = Integer.parseInt(_cursor.getString(0));
				if (_userCount > 0) {
					Intent intent_eval_login = new Intent(this,
							Scrutiny_BundleEntryActivity.class);
					intent_eval_login.putExtra("SeatNo", SSConstants.SeatNo);
					intent_eval_login.putExtra(SSConstants.USER_ID, _userId);
					intent_eval_login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent_eval_login);
				} else {
					if (scrutinySelection) {
						alertMessage(getString(R.string.enter_valid_map_s));
						}else{
							alertMessage(getString(R.string.enter_valid_map_c));
						}
				}
			} else {
				if (scrutinySelection) {
					alertMessage(getString(R.string.enter_valid_map_s));
					}else{
						alertMessage(getString(R.string.enter_valid_map_c));
					}
			}
			if (_cursor != null) {
				_cursor.close();
			}
		} else {
			alertMessage(getString(R.string.enter_valid_id));
		}
	}

	private void alertMessage(String msg) {
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
						etUserId.setText("");
						etUserId.setFocusableInTouchMode(true);
						etUserId.setFocusable(true);
						alertShown = 1;
					}
				});
		myAlertDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_submit:
			switchToBundleEntryActivity();
			break;
		default:
			break;
		}
	}

	private String webServiceforUnreadableBundle(final String pXmlString) {
		// create a new thread
		String str_response = null;
		final String SOAP_ACTION = "ScrutinizingService/InsertSubjectuserMasterDetails";
		final String METHOD_NAME = "InsertSubjectuserMasterDetails";
		final String NAMESPACE = "ScrutinizingService";
		Utility instanceUtility = new Utility();
		final String URL = instanceUtility.getIPConfiguration()
				+ "/ScrutinizingService/ScrutinizingService.asmx";
		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLMasterDetails", pXmlString);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);

			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			ht.debug = true;

			ht.call(SOAP_ACTION, envelope);

			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();
			str_response = response.toString();
		} catch (Exception e) {
			str_response = "failedtoconnect" + e.getMessage();
		}

		return str_response;
	}

	public void showProgress() {
		progressDialog = ProgressDialog.show(this, "",
				"Loading. Please wait...");
		progressDialog.setCancelable(false);
	}

	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
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

//	private void webServiceForTimeLimit() {
//		FileLog fileLog = new FileLog();
//		SScrutinyDatabase ssDatabase = SScrutinyDatabase.getInstance(this);
//		try {
//
//			String date = Scrutiny_SoapServiceManager.webServiceForScriptTimeLimit()
//					.toString();
//
//			if (date != null) {
//				if (!date.equalsIgnoreCase("ERROR")) {
//
//					// open db and update the retrieved values in column
//					// time_interval of Table table_date_configuration
//					ContentValues _contentValues = new ContentValues();
//					_contentValues.put("time_interval", date);
//
//					try {
//
//						int _count = ssDatabase.updateRow2(
//								SSConstants.TABLE_DATE_CONFIGURATION,
//								_contentValues, null);
//
//						if (_count <= 0) {
//							fileLog.logInfo(
//									"updation failed ---> EvaluatorEntryActivity: webServiceForTimeLimit() - ",
//									0);
//						}
//
//					} catch (Exception ex) {
//						fileLog.logInfo(
//								"3. Exception ---> EvaluatorEntryActivity: webServiceForTimeLimit() - "
//										+ ex.toString(), 0);
//					} finally {
//						_contentValues.clear();
//					}
//				} else {
//					// write on log with date
//					fileLog.logInfo(
//							"Date values ---> EvaluatorEntryActivity: webServiceForTimeLimit() - "
//									+ date, 0);
//				}
//			}
//
//		}
//
//		catch (Exception ex) {
//
//			// write on log
//			fileLog.logInfo(
//					"1. Exception ---> EvaluatorEntryActivity: webServiceForTimeLimit() - "
//							+ ex.toString(), 0);
//		}
//
//	}

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
	public void onDestroy() {
		super.onDestroy();
		if (progressDialog != null)
			if (progressDialog.isShowing()) {
				progressDialog.cancel();
			}

	}
}
