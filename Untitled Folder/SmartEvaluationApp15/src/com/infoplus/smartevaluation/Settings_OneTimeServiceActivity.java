package com.infoplus.smartevaluation;

import java.io.File;
import java.io.FileWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.log.FileLog;

public class Settings_OneTimeServiceActivity extends Activity implements
		OnClickListener {

	TextView tvSyncStatus, tvImei;
	EditText etIPAdress;
	DBHelper database;  
  
	@Override  
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_onetime_service);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		  
		database = DBHelper.getInstance(this);

		// check for network availabilty
//		Utility instanceUtility = new Utility();
//		if (!instanceUtility.isNetworkAvailable(this)) {
//			alertMessageForChargeAutoUpdateApk("No Network Available");
//			return;
//		}

		tvSyncStatus = (TextView) findViewById(R.id.tv_sync_status);
		tvImei = (TextView) findViewById(R.id.tv_imei_status);
		
		((Button) findViewById(R.id.btn_update_ip_address))
				.setOnClickListener(this);
		((Button) findViewById(R.id.btn_evaluation)).setVisibility(View.INVISIBLE);
		((Button) findViewById(R.id.btn_showimei)).setOnClickListener(this);
		
		etIPAdress = (EditText) findViewById(R.id.et_ip);
		String ip = Utility.getIPConfiguration();
		if (!TextUtils.isEmpty(ip) && ip.contains("http://")) {
			ip = ip.substring(7, ip.length());
		}
		etIPAdress.setText(ip);
		etIPAdress.setSelection(ip.length());
		
		

	}

	// show alert
	private void alertMessageForChargeAutoUpdateApk(String msg) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle(getString(R.string.app_name));
		myAlertDialog.setMessage(msg);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						// do something when the OK button is clicked
						Dialog.dismiss();
						finish();
					}
				});
		myAlertDialog.show();
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

	// show alert for IpAddress update
	private void showAlertForIpUpdate(final String ipAddress) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage("Want to Update IP Address");
		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
						File file = new File(
								Environment
										.getExternalStoragePublicDirectory("SmartEvaluation"),
								"SmartConfig.xml");
						if (file.exists()) {
							FileWriter fWriter;
							try {
								fWriter = new FileWriter(file);
								fWriter.write("<SmartConfig><IPConfig>http://"
										+ ipAddress
										+ "</IPConfig></SmartConfig>");
								fWriter.flush();
								fWriter.close();
								showAlert(
										"SmartConfig.xml Updated Successfully",
										"OK", "");

							} catch (Exception e) {
								e.printStackTrace();
								FileLog.logInfo("Exception - " + e.toString(),
										0);
								Toast.makeText(
										Settings_OneTimeServiceActivity.this,
										e.toString(), Toast.LENGTH_SHORT)
										.show();
								showAlert(
										"Unable to Update SmartConfig.xml file"
												+ "\n\n" + e.getMessage(),
										"OK", "");
							}
						} else {
							showAlert("SmartConfig.xml doesnot exists", "OK",
									"");
						}
					}
				});

		myAlertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// do something when the OK button is
						// clicked
						dialog.dismiss();
						etIPAdress.setText("");
					}
				});

		myAlertDialog.show();
	}

	private void callIntentService(String mode) {

		setStatusOfTextView(mode, true);
		// register broadcast receiver before calling Intent service
		registerReceiver(receiver, new IntentFilter(
				SEConstants.EVALUATION_NOTIFICATION));
		// call Intentservice
		Intent intent = new Intent(this, Settings_IntentService.class);
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
					setStatusOfTextView(_mode, false);
					Toast.makeText(Settings_OneTimeServiceActivity.this,
							"Evaluation Marks posted", Toast.LENGTH_LONG)
							.show();
				}
			}
			unregisterReceiver(receiver);   
			finish();
		}
	};        

	private void setStatusOfTextView(String mode, boolean calledFromActivity) {
		if (mode.equals(SEConstants.EVALUATION)) {
			if (calledFromActivity) {
				tvSyncStatus.setText("Evaluation data transferring...");
			} else {
				tvSyncStatus
						.setText("Evaluation data Transferred Completed...");
			}
		}
	}

	@Override
	public void onClick(View v) {  
		// TODO Auto-generated method stub
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		switch (v.getId()) {
		
		case R.id.btn_update_ip_address:
			String ipAddress = etIPAdress.getText().toString().trim();
			if (!TextUtils.isEmpty(ipAddress)
					&& SEConstants.IP_ADDRESS_PATTERN_MATCHER
							.matcher(ipAddress).matches()) {
				showAlertForIpUpdate(ipAddress);
			} else {
				etIPAdress.setText("");  
				showAlert("Please enter Valid IP Address", "OK", "");
			}
			break;
		
		

		case R.id.btn_evaluation:

			runInBackground(v);

			break;

		case R.id.btn_showimei:
			String TabletIMEINo = telephonyManager.getDeviceId();
			tvImei.setText(TabletIMEINo);
			break;
			
		default:
			break;
		}
	}

	public void runInBackground(final View v) {
		new AsyncTask<String, Void, Boolean>() {
@Override
protected void onPreExecute() {
	tvSyncStatus.setText("Network searching...");
};
			@Override
			protected Boolean doInBackground(String... params) {

				Utility instanceUtility = new Utility();
				return instanceUtility
						.pingTest(Settings_OneTimeServiceActivity.this);

			}
   
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result) {
					callIntentService(SEConstants.EVALUATION);
				} else {
					tvSyncStatus.setText("");
					showAlert(
							"Network Not Reachable. Please submit the bundle again",
							"OK", "");
				}

			}

		}.execute();
	}
} 
