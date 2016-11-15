package com.infoplustech.smartscrutinization;

import java.io.File;
import java.io.FileWriter;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Scrutiny_OneTimeService;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_CallOneTimeService extends Activity implements
		OnClickListener {

	EditText etIPAdress;
	private static final Pattern IP_ADDRESS = Pattern
			.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
					+ "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
					+ "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
					+ "|[1-9][0-9]|[0-9]))");

	private PowerManager.WakeLock wl;
	private TextView tvSyncStatus, tvIMEI;

	@Override
	protected void onResume() {
		super.onResume();
		if (wl != null) {
			wl.acquire();
		}  
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (wl != null) {
			wl.release();
		}
	}// End of onPause

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scrutiny_call_one_time_service);
		etIPAdress = (EditText) findViewById(R.id.et_ip);
		etIPAdress.setVisibility(View.INVISIBLE);
		tvSyncStatus = (TextView) findViewById(R.id.tv_sync_status);
		tvIMEI = (TextView) findViewById(R.id.tv_imei_status);
		findViewById(R.id.btn_update_ip_address).setOnClickListener(this);
		findViewById(R.id.btn_scrutiny).setVisibility(View.INVISIBLE);
		findViewById(R.id.btn_scrutiny_correction).setVisibility(View.INVISIBLE);
		findViewById(R.id.btn_showimei).setOnClickListener(this);

		 Utility instanceUtility = new Utility();
		 String ip = instanceUtility.getIPConfiguration();
		 if (!TextUtils.isEmpty(ip) && ip.contains("http://")) {
		 ip = ip.substring(7, ip.length());
		 }
		 etIPAdress.setText(ip);
		 etIPAdress.setSelection(ip.length());

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_scrutiny:
			callIntentService(true);
			break;

		case R.id.btn_scrutiny_correction:
			callIntentService(false);
			break;

		case R.id.btn_update_ip_address:

//			showAlertForAdminRights("Do you want to update the IP address",
//					"Yes", "No");
			// etIPAdress.setVisibility(View.INVISIBLE);
			
			String ipAddress = etIPAdress.getText().toString().trim();
			if (!TextUtils.isEmpty(ipAddress)
					&& IP_ADDRESS.matcher(ipAddress).matches()) {
				showAlertForIpUpdate(ipAddress);
			} else {
				etIPAdress.setText("");
				showAlert("Please enter Valid IP Address", "OK", "");
			}

			break;
		case R.id.btn_showimei:
			TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String TabletIMEINo = telephonyManager.getDeviceId();
			tvIMEI.setTextColor(Color.BLACK);
			tvIMEI.setText(TabletIMEINo);
			break;
		default:
			break;
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

	private void callIntentService(boolean scrutinySelection) {
		String mode;
		if (scrutinySelection) {
			mode = SSConstants.SCRUTINY;
		} else {
			mode = SSConstants.SCRUTINY_CORRECTION;
		}

		setStatusOfTextView(mode, true);

		// register broadcast receiver before calling Intent service
		registerReceiver(receiver, new IntentFilter(SSConstants.NOTIFICATION));
		// call Intentservice
		Intent intent = new Intent(Scrutiny_CallOneTimeService.this,
				Scrutiny_OneTimeService.class);
		intent.putExtra(SSConstants.MODE, mode);
		startService(intent);
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				String _mode = bundle.getString(SSConstants.MODE);
				Log.v("_mode", "_mode "+_mode);
				if (_mode.equals(SSConstants.SCRUTINY)) {
					setStatusOfTextView(_mode, false);
					Toast.makeText(Scrutiny_CallOneTimeService.this,
							"Scrutiny marks Posted", Toast.LENGTH_LONG).show();
					navigateToStartingScreen();
				}
				else if (_mode.equals(SSConstants.SCRUTINY_CORRECTION)) {
					setStatusOfTextView(_mode, false);
					Toast.makeText(Scrutiny_CallOneTimeService.this,
							"Scrutiny Correction marks Posted",
							Toast.LENGTH_LONG).show();
					navigateToStartingScreen();
				} else if (_mode.equals("MODE_NETWORK_FAILS")) {
					Toast.makeText(Scrutiny_CallOneTimeService.this,
							"Network fails data not posted", Toast.LENGTH_LONG)
							.show();
					tvSyncStatus.setText("Network fails data not posted");
				}else if(_mode.equals(SSConstants.MODE_ERROR)){
					Toast.makeText(Scrutiny_CallOneTimeService.this,
							"Error posting marks...!", Toast.LENGTH_LONG).show();
					tvSyncStatus.setText("Error posting marks...!");
					navigateToStartingScreen();
				}else{
					setStatusOfTextView(_mode, false);
					Toast.makeText(Scrutiny_CallOneTimeService.this,
							"marks Posted", Toast.LENGTH_LONG).show();
					navigateToStartingScreen();
				}
			}
			unregisterReceiver(receiver);
			hideProgress();
		}
	};

	// Navigate to Starting screen
	private void navigateToStartingScreen() {
		Intent _intent = new Intent(Scrutiny_CallOneTimeService.this,
				Scrutiny_OptionSelectionActivity.class);
		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(_intent);
	}

	private ProgressDialog progressDialog;

	public void showProgress() {
		progressDialog = ProgressDialog.show(this, "",
				"Submitting data. Please wait...");
		progressDialog.setCancelable(false);
	}

	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (progressDialog != null)
			if (progressDialog.isShowing()) {
				progressDialog.cancel();
			}

	}

	// show alert for IpAddress update
	private void showAlertForIpUpdate(final String ipAddress) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.alert_text_style));
		myAlertDialog.setTitle(getResources().getString(R.string.app_name));
		myAlertDialog.setMessage("Do you want to update IP Address");
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
					}
				});

		myAlertDialog.show();
	}
	
	


	// show alert
	private void showAlertForAdminRights(final String msg, String positiveStr,
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

						LayoutInflater factory = LayoutInflater
								.from(Scrutiny_CallOneTimeService.this);
						final View textEntryView = factory.inflate(
								R.layout.scrutiny_ipupdate, null);

						new AlertDialog.Builder(
								Scrutiny_CallOneTimeService.this)
								.setIconAttribute(
										android.R.attr.alertDialogIcon)
								.setCancelable(false)
								.setTitle("Enter the admin password ")
								.setView(textEntryView)
								.setPositiveButton(R.string.alert_dialog_ok,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int whichButton) {

												EditText editText_admin = (EditText) textEntryView
														.findViewById(R.id.tv_admin);

												if (editText_admin != null) {
													Editable editable = editText_admin
															.getText();

													if (!TextUtils
															.isEmpty(editable)) {

														if (editable
																.toString()
																.trim()
																.equalsIgnoreCase(
																		"INFOADMINi234")) {

															LayoutInflater factory = LayoutInflater
																	.from(Scrutiny_CallOneTimeService.this);
															final View textEntryView = factory
																	.inflate(
																			R.layout.ipaddress,
																			null);

															new AlertDialog.Builder(
																	Scrutiny_CallOneTimeService.this)
																	.setIconAttribute(
																			android.R.attr.alertDialogIcon)
																	.setCancelable(
																			false)
																	.setTitle(
																			"Please update ip address now ")
																	.setView(
																			textEntryView)
																	.setPositiveButton(
																			R.string.alert_dialog_ok,
																			new DialogInterface.OnClickListener() {
																				@Override
																				public void onClick(
																						DialogInterface dialog,
																						int whichButton) {
																					dialog.dismiss();
																					EditText etIPAdress = (EditText) textEntryView
																							.findViewById(R.id.edt_ipaddress);

																					Utility instanceUtility = new Utility();
																					String ip = instanceUtility
																							.getIPConfiguration();
																					if (!TextUtils
																							.isEmpty(ip)
																							&& ip.contains("http://")) {
																						ip = ip.substring(
																								7,
																								ip.length());
																					}
																					etIPAdress
																							.setText(ip);
																					etIPAdress
																							.setSelection(ip
																									.length());

																					String ipAddress = etIPAdress
																							.getText()
																							.toString()
																							.trim();
																					if (!TextUtils
																							.isEmpty(ipAddress)
																							&& IP_ADDRESS
																									.matcher(
																											ipAddress)
																									.matches()) {
																						File file = new File(
																								Environment
																										.getExternalStoragePublicDirectory("SmartEvaluation"),
																								"SmartConfig.xml");
																						if (file.exists()) {
																							FileWriter fWriter;
																							try {
																								fWriter = new FileWriter(
																										file);
																								fWriter.write("<SmartConfig><IPConfig>http://"
																										+ ipAddress
																										+ "</IPConfig></SmartConfig>");
																								fWriter.flush();
																								fWriter.close();
																								showAlert(
																										"SmartConfig.xml Updated Successfully",
																										"OK",
																										"");

																							} catch (Exception e) {
																								e.printStackTrace();
																								showAlert(
																										"Unable to Update SmartConfig.xml file"
																												+ "\n\n"
																												+ e.getMessage(),
																										"OK",
																										"");
																							}
																						} else {
																							showAlert(
																									"SmartConfig.xml doesnot exists",
																									"OK",
																									"");
																						}
																					}
																				}
																			})
																	.create()
																	.show();

														}

														else {
															showAlert(
																	"Please enter valid admin password",
																	"OK", "");
														}

													}

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

						dialog.dismiss();
					}
				});
		myAlertDialog.setNegativeButton(negativeStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
		myAlertDialog.show();

	}

	// set the textView
	private void setStatusOfTextView(String mode, boolean calledFromActivity) {
		if (mode.equals(SSConstants.SCRUTINY)) {
			if (calledFromActivity) {
				tvSyncStatus.setText("Scrutiny data transferring...");
			} else {
				tvSyncStatus.setText("Scrutiny data Transferred Completed...");
			}
		}

		else if (mode.equals(SSConstants.SCRUTINY_CORRECTION)) {
			if (calledFromActivity) {
				tvSyncStatus
						.setText("Scrutiny Correction data transferring...");
			} else {
				tvSyncStatus
						.setText("Scrutiny CorrectionF data Transferred Completed...");
			}
		}
	}
}
