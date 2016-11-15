package com.infoplustech.smartscrutinization;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.callback.Scrutiny_NetworkCallback;
import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.utils.Base64;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Scrutiny_DataBaseUtility;
import com.infoplustech.smartscrutinization.utils.Scrutiny_SoapServiceManager;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_OptionSelectionActivity extends Activity implements
		OnClickListener {

	BroadcastReceiver batteryLevelReceiver;
	private ProgressDialog progressDialog;
	private PowerManager.WakeLock wl;
	Utility instanceUtitlity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
      
		setContentView(R.layout.scrutiny_option_selection);  
		SSConstants.SeatNo=getSeatNo();
		getActionBar().setTitle(  
				getString(R.string.title_activity_main)
						+ "App15");
		
		((TextView) findViewById(R.id.tv_seat_no)).setText(SSConstants.SeatNo);
		findViewById(R.id.btn_scrutiny).setOnClickListener(this);
		findViewById(R.id.btn_scrut_correction).setOnClickListener(this);
	//	deleteMoreData();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");

//		// check network availability
//		runOnUiThread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				instanceUtitlity=new Utility();
//				if (!instanceUtitlity
//						.isNetworkAvailable(Scrutiny_OptionSelectionActivity.this)) {
//					alertMessageForChargeAutoUpdateApk(
//							getString(R.string.alert_network_avail), true);
//					return;
//				} else {
//					showProgress("Checking Date.  Please wait...");
//					// checkDateBeforeLogin();
//					webServiceForDateConfig();
//				}
//			}
//		});
		
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		batteryLevel();
		if (wl != null) {
			wl.acquire();
		}
	}

	public void showProgress(String msg) {
		progressDialog = ProgressDialog.show(this, "", msg);
		progressDialog.setCancelable(false);
	}

	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	private void getLoginIdbyAppLoad() {
		showProgress("Checking for APK Updates. Please wait...");
		// calling webservices for auto update of apk
		Scrutiny_SoapServiceManager manager = Scrutiny_SoapServiceManager
				.getInstance(this);
		manager.soapWebServiceForAutoUpdate(callback);
	}
	public String getSeatNo() {
		String seatNo="";
		try{
		SScrutinyDatabase database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor = database.passedQuery(
				SSConstants.TABLE_DATE_CONFIGURATION, "id = 1", null);
		seatNo = _cursor.getString(_cursor
				.getColumnIndex("seat_no"));
		Scrutiny_DataBaseUtility.closeCursor(_cursor);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ""+seatNo;
	}
	Scrutiny_NetworkCallback<Object> callback = new Scrutiny_NetworkCallback<Object>() {

		@Override
		public void onSuccess(Object object) {
			String responseForUpdate = object.toString();
			hideProgress();
			if (responseForUpdate.equalsIgnoreCase("0")) {
				responseForUpdate = "Current APK is up to date";
				// check for database
				// checkDBUpdateWithServer();
				// showAlert(responseForUpdate, "OK", "");
			} else if (responseForUpdate.equalsIgnoreCase("1")) {
				responseForUpdate = "You should update the APK immediately";
				alertMessageForChargeAutoUpdateApk(responseForUpdate, false);
			} else if (responseForUpdate.equalsIgnoreCase("2")) {
				responseForUpdate = "Current version is greater than server version, Contact your Spot Centre Coordinator";
				showAlert(responseForUpdate, "OK", "");
			} else if (responseForUpdate.equalsIgnoreCase("3")) {   
				responseForUpdate = "Error while receiving the APK update";
				showAlert(responseForUpdate, "OK", "");
			} else {
				responseForUpdate = "failedtoconnect \n" + responseForUpdate;
				showAlert(responseForUpdate, "OK", "");
			}
		}

		@Override
		public void onFailure(String errorMessge) {
			hideProgress();
			if (errorMessge.contains("101")) {
				if (!isFinishing()) {
					alertMessageForChargeAutoUpdateApk(
							getString(R.string.alert_network_avail) + "\n\n\n"
									+ errorMessge, true);
				}

			} else {
				showAlert("Failed to Retrieve Data from Server \n\n"
						+ "Server message : \n  " + errorMessge, "Ok", "");
			}
		}
	};

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_scrutiny:
			switchToEvaluatorLoginActivity(true);
			break;
		case R.id.btn_scrut_correction:
			switchToEvaluatorLoginActivity(false);
			break;
		default:
			break;
		}
	}

	// check battery level
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
				((TextView) findViewById(R.id.txt_batteryLevel))
						.setText("Battery Level Remaining: " + level + "%");
				if (level < SSConstants.TABLET_CHARGE) {
					alertMessageForChargeAutoUpdateApk(
							getString(R.string.alert_charge), true);
				}
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);
	}

	// show alert
	private void alertMessageForChargeAutoUpdateApk(String msg,
			final boolean chargeTab) {
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
						if (chargeTab) {
							navigateToTabletHomeScreen();
							Dialog.dismiss();
						} else {
							// Intent LaunchIntent = getPackageManager()
							// .getLaunchIntentForPackage(
							// "com.infoplus.smartupdate");
							startActivity(getPackageManager()
									.getLaunchIntentForPackage(
											"com.infoplus.smartupdate"));
							finish();
							Dialog.dismiss();
						}

					}
				});
		myAlertDialog.show();
	}

	// navigate to Evaluator login activity
	private void switchToEvaluatorLoginActivity(boolean flag) {
		SharedPreferences sharedPreference = getSharedPreferences(
				SSConstants.SCRUTINY_SELECTED, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putBoolean(SSConstants.SCRUTINY_SELECTED, flag);
		editor.commit();
		Intent intent = new Intent(this, Scrutiny_EvaluatorLogin.class);
		intent.putExtra("SeatNo", SSConstants.SeatNo);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scrutiny_activity_main, menu);
		menu.findItem(R.id.menu_back).setVisible(false);
		return true;
	}*/

/*	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(this, Scrutiny_CallOneTimeService.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case R.id.menu_back:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}*/

	private void checkDateBeforeLogin(String retrieveDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		String currentDateSDF = sdf.format(new Date());

		Date tabletDate = null;
		try {
			tabletDate = sdf.parse(currentDateSDF);
		} catch (ParseException e1) {
			e1.printStackTrace();  
		}
		Date serverDate = null;
		DateFormat formatter;
		formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		// String retrieveDate = dateFromServer;
		if (retrieveDate == null) {
			retrieveDate = "failedtoconnect";
		}
		if (!retrieveDate.equalsIgnoreCase("failedtoconnect")) {
			try {
				serverDate = formatter.parse(retrieveDate);
				String strServerDate = serverDate.toString();
				String strTabletDate = tabletDate.toString();
				String dateMonthServer = strServerDate.substring(4, 11);
				String dateMonthTablet = strTabletDate.substring(4, 11);
				String serverYear = strServerDate.substring(strServerDate
						.length() - 4);
				String tabletYear = strTabletDate.substring(strTabletDate
						.length() - 4);

				if ((dateMonthServer.concat(serverYear))
						.equalsIgnoreCase((dateMonthTablet.concat(tabletYear)))) {
					if ((serverDate.getTime() >= tabletDate.getTime() - 300000)
							&& (serverDate.getTime() <= tabletDate.getTime() + 300000)) {
						hideProgress();
						getLoginIdbyAppLoad();
					} else {
						hideProgress();
						alertMsgForDateMismatch();
					}
				} else {
					hideProgress();
					alertMsgForDateMismatch();
				}
			} catch (Exception ex) {

				formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				try {
					serverDate = formatter.parse(retrieveDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String strServerDate = serverDate.toString();
				String strTabletDate = tabletDate.toString();
				String dateMonthServer = strServerDate.substring(4, 11);
				String dateMonthTablet = strTabletDate.substring(4, 11);
				String serverYear = strServerDate.substring(strServerDate
						.length() - 4);
				String tabletYear = strTabletDate.substring(strTabletDate
						.length() - 4);

				if ((dateMonthServer.concat(serverYear))
						.equalsIgnoreCase((dateMonthTablet.concat(tabletYear)))) {
					if ((serverDate.getTime() >= tabletDate.getTime() - 300000)
							&& (serverDate.getTime() <= tabletDate.getTime() + 300000)) {
						hideProgress();
						getLoginIdbyAppLoad();
					} else {
						hideProgress();
						alertMsgForDateMismatch();
					}
				} else {
					hideProgress();
					alertMsgForDateMismatch();
				}
			}
		} else {
			hideProgress();
			getLoginIdbyAppLoad();
			alertMessageForChargeAutoUpdateApk(
					getString(R.string.alert_network_avail), true);
			// showAlert("Failed to connect while checking Date", "OK", "");
		}
	}

	private void alertMsgForDateMismatch() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				Scrutiny_OptionSelectionActivity.this);
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog
				.setMessage("Your Tablet Date&Time is Invalid. Kindly Change the Date.\n(Go to Settings->Date&Time)");

		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						Intent intent = new Intent(
								Settings.ACTION_DATE_SETTINGS);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						finish();
						Dialog.dismiss();

					}
				});
		myAlertDialog.show();
	}

	private void webServiceForDateConfig() {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub
				final String SOAP_ACTION = "SmartEvalService/GetCurrentServerDateTime";
				final String METHOD_NAME = "GetCurrentServerDateTime";
				final String NAMESPACE = "SmartEvalService";
				Utility instanceUtility = new Utility();
				final String URL = instanceUtility.getIPConfiguration()
						+ "/EvalWebService/EvalService.asmx";
				String date;   
				try {     
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
							SoapEnvelope.VER11);
					// request.addProperty("XMLMasterDetails", pXmlString);
					envelope.dotNet = true;
					envelope.implicitTypes = true;
					envelope.enc = SoapEnvelope.ENC2003;
					envelope.xsd = SoapEnvelope.XSD;
					envelope.xsi = SoapEnvelope.XSI;
					envelope.setOutputSoapObject(request);         
					envelope.setAddAdornments(false);   

					HttpTransportSE ht = new HttpTransportSE(URL);    
					// ht.debug = true;

					try {
						ht.call(SOAP_ACTION, envelope);
					} catch (Exception ex) {
						date = "failedtoconnect";
					}

					try {
						final SoapPrimitive response = (SoapPrimitive) envelope
								.getResponse();
						date = response.toString();
						if (date.equalsIgnoreCase("NOVALUE")) {
							date = "failedtoconnect";
						}
					} catch (Exception e) {
						date = "failedtoconnect";
					}
				} catch (Exception e) {
					date = "failedtoconnect";
				}

				return date;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				// send result to method to check date
				Log.v("server time", result);
				checkDateBeforeLogin(result);
			}
		}.execute();

	}

	private void navigateToTabletHomeScreen() {
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// intent.addCategory(Intent.CATEGORY_HOME);
		// startActivity(intent);
		finish();
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
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (progressDialog != null)
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
	}

	private String checkDBVersionForDownload() {
		String _db_version;
		SScrutinyDatabase database = SScrutinyDatabase.getInstance(this);
		Cursor _cursor = database.passedQuery(
				SSConstants.TABLE_DATE_CONFIGURATION, "id = 1", null);
		_db_version = _cursor.getString(_cursor
				.getColumnIndex(SSConstants.DB_VERSION));
		Scrutiny_DataBaseUtility.closeCursor(_cursor);
		return _db_version;
	}

	// checking database update with server
	private void checkDBUpdateWithServer() {
		showProgress("Checking Database update");
		Scrutiny_SoapServiceManager manager = Scrutiny_SoapServiceManager
				.getInstance(this);
		manager.soapWebServiceForAutoUpdateDB(checkDBVersionForDownload(),
				callbackForDBUpdate);
	}

	Scrutiny_NetworkCallback<Object> callbackForDBUpdate = new Scrutiny_NetworkCallback<Object>() {

		@Override
		public void onSuccess(Object object) {
			// TODO Auto-generated method stub
			String responseForUpdate = object.toString();
			hideProgress();

			DBFileResponse(responseForUpdate);
			if (responseForUpdate.equalsIgnoreCase("0")) {
				responseForUpdate = "Current APK is up to date";
				// check for database

				showAlert(responseForUpdate, "OK", "");
			} else if (responseForUpdate.equalsIgnoreCase("1")) {
				responseForUpdate = "You should update the APK immediately";
				alertMessageForChargeAutoUpdateApk(responseForUpdate, false);
			} else if (responseForUpdate.equalsIgnoreCase("2")) {
				responseForUpdate = "Current version is greater than server version, Contact your Spot Centre Coordinator";
				// showAlert(responseForUpdate, "OK", "");
			} else if (responseForUpdate.equalsIgnoreCase("3")) {
				responseForUpdate = "Error while receiving the APK update";
				// showAlert(responseForUpdate, "OK", "");
			} else {
				responseForUpdate = "failedtoconnect \n" + responseForUpdate;
				// showAlert(responseForUpdate, "OK", "");
			}
		}

		@Override
		public void onFailure(String errorMessge) {
			// TODO Auto-generated method stub
			hideProgress();
		}
	};

	private void DBFileResponse(String response) {
		byte[] bResponse = null;
		try {
			bResponse = Base64.decodeFast((response.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Various Response from server
		// Application is uptodate ===== 0------> MA==
		// Error while receiving the APK update ===== 3------> Mw==
		if (response.toString().equalsIgnoreCase("MA==")
				|| response.toString().equalsIgnoreCase("Mw==")) {
			try {
				response = new String(bResponse, "UTF-8");
				dbFileSaveToSDCard(response);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (response.equalsIgnoreCase("0")) {
				alertMessageForAutoUpdateDB("Apk is upto date");
			} else if (response.equalsIgnoreCase("3")) {
			}
		}
	}

	private void dbFileSaveToSDCard(String response) {
		if (SSConstants.DATABASE_FILE_PATH_TO_SSCRUTINY.exists()) {
			if (SSConstants.DATABASE_FILE_PATH_TO_SSCRUTINY.delete()) {
				try {
					if (SSConstants.DATABASE_FILE_PATH_TO_SSCRUTINY
							.createNewFile()) {
						copyContentToFile(response);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			try {
				if (SSConstants.DATABASE_FILE_PATH_TO_SSCRUTINY.createNewFile()) {
					copyContentToFile(response);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void copyContentToFile(String response) {

		byte[] bResponse = null;
		try {
			bResponse = Base64.decodeFast((response.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(
					SSConstants.DATABASE_FILE_PATH_TO_SSCRUTINY);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// FileOutputStream fileOuputStream = new FileOutputStream(
		// outputFile);
		try {
			fOut.write(bResponse);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// OutputStreamWriter myOutWriter =
		// new OutputStreamWriter(fOut);
		// // fOut.write(response);
		// try {
		// myOutWriter.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// try {
		// fOut.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		Toast.makeText(this, "Updates has done", Toast.LENGTH_LONG).show();
	}

	// show alert
	private void alertMessageForAutoUpdateDB(String msg) {
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

						Dialog.dismiss();

						// Need to download DB here
					}
				});
		myAlertDialog.show();
	}
	public void deleteMoreData() {
		int tableMarksCount = 0;
		Cursor curs1 = null, curs2 = null;
		String sqlStatement = "select barcode from table_marks_scrutinize";
		SScrutinyDatabase database = SScrutinyDatabase.getInstance(this);
		try {
			curs1 = database.getRecordsUsingRawQuery(sqlStatement);
			if (curs1 != null) {
				tableMarksCount = curs1.getCount();
				Log.v("fffff", ""+tableMarksCount);
				if (tableMarksCount > 10) {
					String selectQuery = "DELETE FROM table_marks_scrutinize WHERE mark_scrutinize_id NOT IN ("
							+ "SELECT mark_scrutinize_id FROM (SELECT mark_scrutinize_id FROM "
							+ "table_marks_scrutinize ORDER BY mark_scrutinize_id DESC LIMIT 50));";
					
					database.deleteMore(selectQuery);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			Scrutiny_DataBaseUtility.closeCursor(curs1);
		}
		int tableMarksHistoryCount = 0;
		sqlStatement = "select barcode from table_marks_scrutinize_entry";
		try {
			curs2 = database.getRecordsUsingRawQuery(sqlStatement);
			if (curs2 != null) {
				tableMarksHistoryCount = curs2.getCount();
				if (tableMarksHistoryCount > 10) {
					String selectQuery = "DELETE FROM table_marks_scrutinize_entry WHERE mark_scrutinize_id NOT IN ("
							+ "SELECT mark_scrutinize_id FROM (SELECT mark_scrutinize_id FROM "
							+ "table_marks_scrutinize_entry ORDER BY mark_scrutinize_id DESC LIMIT 50));";
					database.deleteMore(selectQuery);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			Scrutiny_DataBaseUtility.closeCursor(curs2);
		}
	}
}
