package com.infoplus.smartevaluation;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;
import com.infoplus.smartevaluation.webservice.WebServiceUtility;

public class EvaluatorEntryActivity extends Activity implements

		OnClickListener, OnTouchListener, OnItemSelectedListener {

	String UserId, responseForUpdate, tableDate, userCount, subjectCount,
			db_Version = "0", formattedDate, retrieveDate, versionName,
			TabletIMEINo, SubjectId = null, SubjectCode, BundleNo, strcheck,
			SeatNo, 
			spinSelectedString;
	int UserCount, CurrentAnswerBook, MaxAnswerBook, percentageStatus, i = 0,
			progressBarStatus = 0;
	int maxx;
	boolean isException = false; 

	RelativeLayout getRelativeLayout;  
	Button button;
	EditText editText;
	TextView batteryLevel;
   
	SharedPreferences preferences;
	SharedPreferences.Editor editor_prg;  
	View v;
	private PowerManager.WakeLock wl;
	DBHelper sEvalDatabase;
	private ProgressDialog progressDialog;

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.scrutiny_activity_main, menu);
		menu.findItem(R.id.menu_back).setVisible(false);
		return true;
	}*/

	/*@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.menu_settings) {
			navigateTo_Settings_OneTime_Service();
		} else if (item.getItemId() == R.id.update_settings) {
			navigateToUpdateSettings();
		}
		return super.onMenuItemSelected(featureId, item);
	}*/

	private void navigateTo_Settings_OneTime_Service() {
		Intent _intent = new Intent(this, Settings_OneTimeServiceActivity.class);
		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(_intent);
	}

	private void navigateToUpdateSettings() {
		/*Intent _intent = new Intent(this, Settings_UpdateBundle.class);
		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(_intent);*/
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"EvaluatorEntryActivity");
		sEvalDatabase = DBHelper.getInstance(EvaluatorEntryActivity.this);
		// ipAddress = getIPConfiguration();
		SharedPreferences pref = getSharedPreferences(SEConstants.SHARED_PREF_TIME_LIMIT, 0); 
		if(pref.contains(SEConstants.SHARED_PREF_TIME_LIMIT)){
			SEConstants.time_limit=120000;
			Log.v("time", "120000");
		}else{
			Log.v("time", "0");
			SEConstants.time_limit=120000;//0; FOR TIME LESS
		}
		
		String userCount1="1";
		Cursor cur1 = null;
		try {
			cur1 = sEvalDatabase
					.executeSelectSQLQuery("select ifnull(is_first,1) as userCount from table_user");
			if (cur1 != null) {
						while (!(cur1.isAfterLast())) {
							userCount1 = cur1.getString(cur1
									.getColumnIndex("userCount"));
							cur1.moveToNext();
						}
			} else {
				FileLog.logInfo(
						"Cur1 null --->EvaluatorEntryActivity : RunInBackground()_doInBackground :",
						0);
			}
		} catch (Exception e) {
			FileLog.logInfo(
					"1. Exception: --->EvaluatorEntryActivity : RunInBackground()_doInBackground :"
							+ e.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cur1);
		}
		if(userCount1.equals("3"))
			SEConstants.time_limit=SEConstants.time_limit_rc; //30000
		
		//deleteMoreData();
		checkDbVersion();
//		Utility instanceUtility = new Utility();
//		if (instanceUtility.isNetworkAvailable(EvaluatorEntryActivity.this) == false) {
//			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
//					EvaluatorEntryActivity.this);
//			myAlertDialog.setTitle("Smart Evaluation");
//			myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
//			myAlertDialog
//					.setMessage("Couldnot Connect to Network. \nPlease Contact Spotcenter Co-Ordinator");
//
//			myAlertDialog.setCancelable(false);
//
//			myAlertDialog.setPositiveButton("OK",
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface Dialog, int arg1) {
//
//							finish();
//							Dialog.dismiss();
//
//						}
//					});
//
//			myAlertDialog.show();
//
//		}
//		else {
//			backGroundService();
//			// backGroundServiceForDate();
//		}  

		loadScreen();
		/*ContentValues _values = new ContentValues();
		_values.put("time_interval", 7);
		try {
			sEvalDatabase.updateRow(SEConstants.TABLE_DATE_CONFIGURATION,
					_values, "id=1");
		} catch (Exception ex) {
			FileLog.logInfo(
					"BundleNumberActivity-->RunInBackground()"
							+ ex.toString(), 0);
		}*/
	}

	public void alertMessageToVerify(final String errormsg,
			final boolean pException) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				EvaluatorEntryActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(errormsg);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						if (errormsg
								.equalsIgnoreCase("You Should Update the APK Immediately")) {

							Intent LaunchIntent = getPackageManager()
									.getLaunchIntentForPackage(
											"com.infoplus.smartupdate");
							startActivity(LaunchIntent);
							finish();
							Dialog.dismiss();
						}

						else if (pException) {
							finish();
							Dialog.dismiss();
						}

						else if (!pException) {
							Dialog.dismiss();
							if (checkDateBeforeLogin()) {
								progressBarStatus = 0;
						//		RunInBackground(progressBarStatus);
							} else {
								alertMsgForDateMismatch();
							}
						}

					}
				});

		myAlertDialog.show();
	}

	public void alertMessageToRestoreData(final String errormsg,
			final boolean pException) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				EvaluatorEntryActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage(errormsg);
		myAlertDialog.setCancelable(false);
		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						finish();
						Dialog.dismiss();

					}
				});

		myAlertDialog.show();
	}

	private void RunInBackground(final int progressBarStatus) {
		percentageStatus = progressBarStatus;

		new AsyncTask<Void, Void, Void>() {

			String retrieveString;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgress("Loading, Please wait...");

			}

			@Override
			protected Void doInBackground(Void... params) {
				Cursor cur1 = null, cur2 = null;
				try {
					// Gives user count in the table_user
					cur1 = sEvalDatabase
							.executeSelectSQLQuery("select ifnull(max(login_id),0) as userCount from table_user");
					if (cur1 != null) {
						while (!(cur1.isAfterLast())) {
							userCount = cur1.getString(cur1
									.getColumnIndex("userCount"));
							Log.w("userCount", "" + userCount);
							cur1.moveToNext();
						}

					} else {
						FileLog.logInfo(
								"Cur1 null --->EvaluatorEntryActivity : RunInBackground()_doInBackground :",
								0);
					}
				} catch (Exception e) {
					FileLog.logInfo(
							"1. Exception: --->EvaluatorEntryActivity : RunInBackground()_doInBackground :"
									+ e.toString(), 0);
				} finally {
					DataBaseUtility.closeCursor(cur1);
				}

				try {
					// Gives subject count
					cur2 = sEvalDatabase
							.executeSelectSQLQuery("select ifnull(max(subject_id),0) as subjectCount from  table_subject");
					if (cur2 != null) {
						while (!(cur2.isAfterLast())) {
							subjectCount = cur2.getString(cur2
									.getColumnIndex("subjectCount"));
							Log.w("subjectCount", "" + subjectCount);
							cur2.moveToNext();
						}

					} else {
						FileLog.logInfo(
								"Cur2 null --->EvaluatorEntryActivity : RunInBackground()_doInBackground :",
								0);
					}

				}

				catch (Exception e) {
					FileLog.logInfo(
							"1. Exception: --->EvaluatorEntryActivity : RunInBackground()_doInBackground :"
									+ e.toString(), 0);
				} finally {
					DataBaseUtility.closeCursor(cur2);
				}

				StringBuffer strBuf = new StringBuffer(
						"<?xml version=\"1.0\" encoding=\"utf-8\"?>");

				strBuf.append("<CountStatus>");

				strBuf.append("<usercount>");
				strBuf.append(userCount);
				strBuf.append("</usercount>");

				strBuf.append("<subjectcount>");
				strBuf.append(subjectCount);
				strBuf.append("</subjectcount>");

				strBuf.append("</CountStatus>");

				try {
					String ss=strBuf
							.toString();
					Log.e("request data", "request "+ss);
					retrieveString = webServiceForRestoreDatas(strBuf.toString());
				} catch (Exception e) {
					FileLog.logInfo(
							"WS Error ---> EvaluatorEntryActivity: RunInBackground() "
									+ e.toString(), 0);
					retrieveString = "Error Code 101: Failed to Connect with Server";
					isException = true;
					return null;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if (isException) {
					alertMessageToRestoreData(retrieveString, isException);
				} else {
					if (retrieveString.equalsIgnoreCase("0")) {
						hideProgress();
					}

					else {
						// hideProgress();
						Cursor cur1 = null;
						String userCountProgress = null;
						try {
							cur1 = sEvalDatabase
									.executeSelectSQLQuery("select ifnull(Count(*),0) as userCount from  table_user");
							if (cur1 != null) {
								while (!(cur1.isAfterLast())) {
									userCountProgress = cur1.getString(cur1
											.getColumnIndex("userCount"));
									Log.w("userCountProgress", ""
											+ userCountProgress);
									cur1.moveToNext();
								}

							} else {
								FileLog.logInfo(
										"Cur1 null --->EvaluatorEntryActivity : RunInBackground()_postexecute() :",
										0);
							}
						} catch (Exception e) {
							FileLog.logInfo(
									"Exception: --->EvaluatorEntryActivity : RunInBackground()_postexecute() :"
											+ e.toString(), 0);

						} finally {
							DataBaseUtility.closeCursor(cur1);
						}
						double progressBarStatusRoutine = Integer
								.parseInt(userCountProgress);
						int fixedStatus = 100;
						int i = (int) ((progressBarStatusRoutine / fixedStatus));
						if (i < 300) {
							percentageStatus = 10;
						} else {
							percentageStatus = percentageStatus + 10;
						}
					//	RunInBackground(percentageStatus);
					}
				}
			}
		}.execute();
	}

	public String webServiceForRestoreDatas(final String pXmlString) {

		String responseFromService = null;

		try {

			WebServiceUtility instanceWebServiceUtility = new WebServiceUtility();

			responseFromService = instanceWebServiceUtility
					.webServiceForRestoreDatas(pXmlString).toString();

			if (responseFromService != null) {
				if (responseFromService.equalsIgnoreCase("0")) {
					hideProgress();
				}

				else {  
   
					Cursor cursor = null;        
					try {   
						cursor = sEvalDatabase
								.executeSQLQuery(responseFromService);
						if ((cursor == null)) {
           
							FileLog.logInfo(
									"Cursor Null ---> EvaluatorEntryActivity: webServiceForRestoreDatas():",
									0);
						}
                     
					} catch (SQLiteException ex) {
						FileLog.logInfo(
								"SQLiteException ---> EvaluatorEntryActivity: webServiceForRestoreDatas():"
										+ ex.toString(), 0);

						responseFromService = "Error Code 101: Failed to Connect with Server";
						isException = true;
					} finally {
						DataBaseUtility.closeCursor(cursor);
					}
				}
			}

		}

		catch (Exception ex) {
			FileLog.logInfo(
					"1. Exception ---> EvaluatorEntryActivity: webServiceForRestoreDatas():"
							+ ex.toString(), 0);
			responseFromService = "Error Code 101: Failed to Connect with Server";

			hideProgress();
			isException = true;
		}

		return responseFromService;
	}

	public String webServiceForDateConfig() {
		String date = null;

		try {
			WebServiceUtility instanceWebServiceUtility = new WebServiceUtility();
			date = instanceWebServiceUtility.webServiceForDateComparison()
					.toString();

			if (date != null) {
				if (date.equalsIgnoreCase("NOVALUE")) {
					FileLog.logInfo(
							"EvaluatorEntryActivity: webServiceForDateConfig()"
									+ date, 0);
					date = "failedtoconnect";
				}
			}

		}
		catch (Exception ex) {
			FileLog.logInfo(
					"1. Exception ---> EvaluatorEntryActivity: webServiceForDateConfig() "
							+ ex.toString(), 0);
			date = "failedtoconnect";
		}  
		return date;
	}

	public void loadScreen() {
		
		setContentView(R.layout.evaluator);
		editText = (EditText) findViewById(R.id.evaluatorIDEditText);

		preferences = this.getSharedPreferences("program_details",
				MODE_WORLD_READABLE);
		editor_prg = preferences.edit();
		spinSelectedString = "B.Tech";
		SEConstants.seatNo=getSeatNo();  
		getActionBar().setTitle("SmartEvaluation" +"AppUpdate"); // + "--" +
																	// db_Version
		((TextView) findViewById(R.id.tv_seat_no)).setText(SEConstants.seatNo);
		getRelativeLayout = (RelativeLayout) this.findViewById(R.id.Container);
		getRelativeLayout.setOnTouchListener(this);

		batteryLevel = (TextView) this.findViewById(R.id.txt_batteryLevel);
		// batteryLevel();

		Spinner spinCourse = (Spinner) findViewById(R.id.spin_course);
		spinCourse.setOnItemSelectedListener(this);

		/*
		 * Spinner spinReg = (Spinner) findViewById(R.id.spin_reg);
		 * spinReg.setOnItemSelectedListener(this);
		 */

		Cursor cur_Course = null;
		try {
			cur_Course = sEvalDatabase
					.executeSelectSQLQuery("select program_name,program_id from table_program");
			// cur_Course = sEvalDatabase
			// .executeSelectSQLQuery("SELECT tp.program_id, tr.regulation_name||'_'||tp.program_name as reg_prg  "
			// + "from table_program as tp inner join "
			// + "table_regulation as tr ON tr.program_id=tp.program_id");

			ArrayList<String> program = new ArrayList<String>();
			if ((cur_Course != null) && (cur_Course.getCount() > 0)) {

				while (!cur_Course.isAfterLast()) {
					program.add(cur_Course.getString(cur_Course
							.getColumnIndex("program_name")));
					cur_Course.moveToNext();
				}
				if (!program.isEmpty()) {
					spinCourse.setAdapter(new ArrayAdapter<String>(this,
							R.layout.layout_textview, program));
				}

			} else {
				FileLog.logInfo(
						"Cursor Null ---> EvaluatorEntryActivity: loadScreen() ",
						0);
			}

		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> EvaluatorEntryActivity: loadScreen() "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cur_Course);
		}

		button = (Button) findViewById(R.id.evaluatorButton);
		button.setOnClickListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		if (v == getRelativeLayout) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			return true;
		}
		return false;
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
			// finish();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.evaluatorButton) {
			stop();
		}
	}

	private void stop() {
		if (editText != null) {
			Editable editable = editText.getText();
			if (editable != null) {
				String value = editable.toString().trim();
				if(!TextUtils.isEmpty(value)){
				Cursor cur = null;
				try {
					cur = sEvalDatabase
							.executeSelectSQLQuery("select COUNT(*) as Value from table_user where TRIM(UPPER(login)) =TRIM(UPPER('"
									+ value + "')) and active_status=0");

					if ((cur != null) && (cur.getCount() > 0)) {

						while (!(cur.isAfterLast())) {
							UserCount = Integer.parseInt(cur.getString(0));
							cur.moveToNext();
						}

						if (UserCount > 0) {
							Cursor cur1 = null;
							try {
								cur1 = sEvalDatabase
										.executeSelectSQLQuery("select user_id from table_user where TRIM(UPPER(login)) =TRIM(UPPER('"
												+ value + "')) and active_status=0");

								if ((cur1 != null) && (cur1.getCount() > 0)) {
									while (!(cur1.isAfterLast())) {
										UserId = cur1.getString(cur1
												.getColumnIndex("user_id"));
										cur1.moveToNext();
									}
									// update table table_user
									ContentValues _contentValues = new ContentValues();
									_contentValues.put("logged_in_status", "1");
									try {
										int getCount = sEvalDatabase.updateRow(
												"table_user", _contentValues,
												"user_id='" + UserId + "'");
										if (getCount > 0) {
											String ssss=getProgramName(UserId);
											if(spinSelectedString.equalsIgnoreCase(ssss)){
											String BundleNo = getbundleNoByUserid(UserId);
											switchingActivity(BundleNo, UserId);
											}else{
												alertMsg(getString(R.string.enter_valid_course), false);	
											}
										} else {
											FileLog.logInfo(
													"EvaluatorEntryActivity: stop():"
															+ String.valueOf(getCount),
													0);
										}

									} catch (Exception ex) {

									} finally {
										_contentValues.clear();
									}

								} else {
									FileLog.logInfo(
											"cur1 Null ---> EvaluatorEntryActivity: stop():",
											0);
								}
							} catch (Exception ex) {
								FileLog.logInfo(
										"2. Exception ---> EvaluatorEntryActivity: stop():"
												+ ex.toString(), 0);
							} finally {
								DataBaseUtility.closeCursor(cur1);
							}

						}
						else {
							alertMsg(getString(R.string.enter_valid_map), true);
							return;
						}
					}
					else {
						alertMsg(getString(R.string.enter_valid_map), true);
						FileLog.logInfo(
								"cur Null ---> EvaluatorEntryActivity: stop():",
								0);
					}
				} catch (Exception ex) {
					FileLog.logInfo(
							"1. Exception ---> EvaluatorEntryActivity: stop():"
									+ ex.toString(), 0);
				} finally {
					DataBaseUtility.closeCursor(cur);
				}
				}
				else {
					alertMsg(getString(R.string.enter_valid_id),true);
					FileLog.logInfo(
							"cur Null ---> EvaluatorEntryActivity: stop():",
							0);
				}
			}
		}
	}
	String getProgramName(String val){
		String pname="";
		String a[]={"program_name"};
		Cursor _cursor = sEvalDatabase.getRow(SEConstants.TABLE_BUNDLE,
				"enter_by ='" + val + "'", a);
		if (_cursor != null) {
			if (_cursor.getCount() > 0) {
				pname = _cursor
						.getString(_cursor
								.getColumnIndex("program_name"));
			}
		}
		DataBaseUtility.closeCursor(_cursor);
		if(TextUtils.isEmpty(pname)){
			pname="";
		}
		return pname;
	}
	public String getbundleNoByUserid(String Userid) {
		String Bundle = null;
		Cursor cursor = null;
		try {
			cursor = sEvalDatabase
					.executeSelectSQLQuery("select tb.subject_id,tb.bundle_no,tb.subject_code, tb.total_scripts from table_bundle tb, table_user tu "
							+ "where "
							+ "tb.enter_by=tu.user_id and tb.enter_by='"
							+ Userid + "' and tb.status=0");

			if ((cursor != null) && (cursor.getCount() > 0)) {
				while (!(cursor.isAfterLast())) {
					SubjectId = cursor.getString(cursor
							.getColumnIndex("subject_id"));
					Bundle = cursor.getString(cursor
							.getColumnIndex("bundle_no"));
					SubjectCode = cursor.getString(cursor
							.getColumnIndex("subject_code"));
					MaxAnswerBook = cursor.getInt(cursor
									.getColumnIndex("total_scripts"));
					cursor.moveToNext();
				}
			} else {
				FileLog.logInfo(
						"No User with bundle status 0,--->EvaluatorEntryActivity: getbundleNoByUserid(): ",
						0);
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception --->EvaluatorEntryActivity: getbundleNoByUserid(): "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor);
		}

		return Bundle;
	}

	public void getLoginIdbyAppLoad() {
		String EvalCode = null;
		Cursor cursor = null;
		try {
			cursor = sEvalDatabase
					.executeSelectSQLQuery("select tu.user_id from table_user tu "
							+ "where  tu.logged_in_status=1");

			if ((cursor != null) && (cursor.getCount() > 0)) {
				while (!(cursor.isAfterLast())) {
					EvalCode = cursor.getString(cursor
							.getColumnIndex("user_id"));
					cursor.moveToNext();
				}
				if (EvalCode != null) {
					UserId = EvalCode;
					switchingActivity(getbundleNoByUserid(EvalCode), EvalCode);
				}
			} else {
				FileLog.logInfo(
						"No User with logged in status 1--->EvaluatorEntryActivity: getLoginIdbyAppLoad(): ",
						0);
			}

		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception --->EvaluatorEntryActivity: getLoginIdbyAppLoad(): "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor);
		}

	}

	void switchingActivity(String BundleNo, String EvalCode) {
		if (BundleNo != null) {
			Cursor cursor = null;
			try {
				cursor = sEvalDatabase
						.executeSelectSQLQuery("select ifnull(max(tm.bundle_serial_no),0) as anser_serial_no "
								+ "from table_bundle as tb inner join table_marks"
								+ " as tm on tb.bundle_no = tm.bundle_no "
								+ "where tb.bundle_no = '"
								+ BundleNo
								+ "' and tb.subject_id =  "
								+ SubjectId
								+ " and tb.enter_by  = '" + UserId + "'");

				if ((cursor != null) && (cursor.getCount() > 0)) {
					while (!(cursor.isAfterLast())) {
						CurrentAnswerBook = Integer.parseInt(cursor
								.getString(cursor
										.getColumnIndex("anser_serial_no")));
						CurrentAnswerBook = CurrentAnswerBook + 1;
						cursor.moveToNext();
					}
					setMaxTotalInBetweenScripts(BundleNo, UserId);
					if(CurrentAnswerBook<=MaxAnswerBook){
					Intent intent = new Intent(this, BundleNumberActivity.class);
					intent.putExtra("UserId", UserId);
					intent.putExtra("SubjectId", SubjectId);  
					intent.putExtra("BundleNo", BundleNo);
					intent.putExtra("SeatNo", SEConstants.seatNo);
					intent.putExtra("SubjectCode", SubjectCode);
					intent.putExtra("MaxAnswerBook",MaxAnswerBook);
					intent.putExtra("CurrentAnswerBook", CurrentAnswerBook);
					intent.putExtra(SEConstants.SHARED_PREF_MAX_TOTAL,maxx);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					}else{    
						Intent intent_eval_entry = new Intent(EvaluatorEntryActivity.this,
								GrandTotalSummaryTable.class);
						intent_eval_entry.putExtra("UserId", UserId);
						intent_eval_entry.putExtra("BundleNo", BundleNo);
						intent_eval_entry.putExtra("SubjectCode", SubjectCode);
						intent_eval_entry.putExtra("SeatNo", SEConstants.seatNo);
						intent_eval_entry
								.putExtra("ScriptCount", MaxAnswerBook);
						intent_eval_entry
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent_eval_entry);
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
				DataBaseUtility.closeCursor(cursor);
			}

		}
		else {
			SharedPreferences settings = getSharedPreferences("SAVE_EXIT", 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.commit();
			submit(EvalCode);
		}

	}

	public void submit(String EvalCode) {
		if (editText != null) {
			if (spinSelectedString != null
					&& !TextUtils.isEmpty(editText.getText().toString().trim())
					&& editText.getText().toString().trim()
							.equalsIgnoreCase(EvalCode)) {
				AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
						this);
				myAlertDialog.setTitle("Smart Evaluation");
				myAlertDialog.setMessage("You have selected : "
						+ spinSelectedString + " Course ");

				myAlertDialog.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();

								Intent intent = new Intent(
										EvaluatorEntryActivity.this,
										SubjectCodeActivity.class);
								intent.putExtra("UserId", UserId);
								intent.putExtra("MaxAnswerBook", MaxAnswerBook);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

							}
						});
				myAlertDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				myAlertDialog.show();
			}
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		if (arg1 != null) {
			TextView tv = (TextView) arg1.findViewById(R.id.tv_spinner);
			tv.setTypeface(Typeface.DEFAULT_BOLD);
			spinSelectedString = tv.getText().toString();
			Cursor cursor = null;
			try {
				//
				// cursor = sEvalDatabase
				// .executeSelectSQLQuery("SELECT tp.program_id, tr.regulation_name||'_'||tp.program_name as reg_prg  "
				// + "from table_program as tp inner join "
				// +
				// "table_regulation as tr ON tr.program_id=tp.program_id where program_name = '"
				// +tv.getText().toString().trim()+"'");

				cursor = sEvalDatabase
						.executeSelectSQLQuery("select program_id,program_name from table_program where program_name = '"
								+ tv.getText().toString() + "'");
				if (cursor != null && cursor.getCount() > 0) {
					while (!cursor.isAfterLast()) {
						editor_prg.putInt(SEConstants.SHARED_PREF_PROGRAM_ID,
								cursor.getInt(cursor
										.getColumnIndex("program_id")));
						editor_prg.putString(
								SEConstants.SHARED_PREF_PROGRAM_NAME,
								cursor.getString(cursor
										.getColumnIndex("program_name")));

						editor_prg.commit();

						cursor.moveToNext();
					}
				} else {
					FileLog.logInfo(
							"Cursor Null--->EvaluatorEntryActivity: onItemSelected(): ",
							0);
					DataBaseUtility.closeCursor(cursor);
					return;
				}
			} catch (Exception ex) {
				FileLog.logInfo(
						"Exception --->EvaluatorEntryActivity: onItemSelected(): "
								+ ex.toString(), 0);
			} finally {
				DataBaseUtility.closeCursor(cursor);
			}

		}
	}

	boolean checkDateBeforeLogin() {
		boolean isTrue = false;

//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//		String currentDateSDF = sdf.format(new Date());
//
//		Date tabletDate = null;
//		try {
//			tabletDate = sdf.parse(currentDateSDF);
//		} catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		Date serverDate = null;
//		// String str_date="11-June-07";
//		DateFormat formatter;
//		formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		//if (!runInBackgroundDate().equalsIgnoreCase("failedtoconnect")) {
			try {
//
//				serverDate = formatter.parse(retrieveDate);
//
//				String strServerDate = serverDate.toString();
//				String strTabletDate = tabletDate.toString();
//
//				String dateMonthServer = strServerDate.substring(4, 11);
//				String dateMonthTablet = strTabletDate.substring(4, 11);
//				String serverYear = strServerDate.substring(strServerDate
//						.length() - 4);
//				String tabletYear = strTabletDate.substring(strTabletDate
//						.length() - 4);
//
//				if ((dateMonthServer.concat(serverYear))
//						.equalsIgnoreCase((dateMonthTablet.concat(tabletYear)))) {
//					if ((serverDate.getTime() >= tabletDate.getTime() - 300000)
//							&& (serverDate.getTime() <= tabletDate.getTime() + 300000)) {
						getLoginIdbyAppLoad();
						isTrue = true;
//					} else {
//
//						// alertMsgForDateMismatch();
//						isTrue = false;
//					}
//				}
//
//				else {
//					// alertMsgForDateMismatch();
//					isTrue = false;
//				}
			} catch (Exception ex) {
				Log.i("parsererror", ex.toString());
			}
//		} else {
//			isTrue = true;
//			// alertAndAllowForDateMismatch();
//		}

		return isTrue;

	}

	private String runInBackgroundDate() {

		retrieveDate = webServiceForDateConfig();

		return retrieveDate;

	}

	public void alertAndAllowForDateMismatch() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				EvaluatorEntryActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog
				.setMessage("Kindly Check for the Date&Time.\n(Go to Settings->Date&Time)");

		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {
						progressBarStatus = 0;
					//	RunInBackground(progressBarStatus);
						Dialog.dismiss();

					}
				});

		myAlertDialog.show();
	}

	public void alertMsgForDateMismatch() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				EvaluatorEntryActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
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

	public void checkDbVersion() {
		Cursor dbVersion = null;
		try {
			dbVersion = sEvalDatabase
					.executeSelectSQLQuery("select db_version, config_date from table_date_configuration");

			if (dbVersion != null && dbVersion.getCount() > 0) {

				while (!dbVersion.isAfterLast()) {
					db_Version = dbVersion.getString(dbVersion
							.getColumnIndex("db_version"));
					dbVersion.moveToNext();
				}
			}

			else {
				FileLog.logInfo(
						"Cursor Null ---> EvaluatorEntryActivity: checkDbVersion()",
						0);
				alertForDbVersion();
			}

		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> EvaluatorEntryActivity: checkDbVersion()"
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(dbVersion);
		}
	}

	public void alertForDbVersion() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				EvaluatorEntryActivity.this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		myAlertDialog.setMessage("DB mismatched");

		myAlertDialog.setCancelable(false);

		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						Dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_MAIN);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.addCategory(Intent.CATEGORY_HOME);
						startActivity(intent);

					}
				});

		myAlertDialog.show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		hideProgress();
	}

	BroadcastReceiver batteryLevelReceiver;

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

				if (level < SEConstants.TABLET_CHARGE) {
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
	protected void onResume() {
		super.onResume();
		batteryLevel();
		if (wl != null) {
			wl.acquire();
		}
	}

	public void backGroundService() {

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showProgress("Loading. Please wait...");
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					versionName = getPackageManager().getPackageInfo(
							getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLog.logInfo(e.getMessage(), 0);
				}
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				TabletIMEINo = telephonyManager.getDeviceId();

				try {
					WebServiceUtility instanceWebServiceUtility = new WebServiceUtility();
					String response = instanceWebServiceUtility
							.webServiceForAPKUpdate(1, versionName,
									TabletIMEINo);

					responseForUpdate =""+ response;
					if (responseForUpdate != null) {

						if (responseForUpdate.equalsIgnoreCase("0")) {
							responseForUpdate = "Your Current APK is Up To Date";
						}
						else if (responseForUpdate.equalsIgnoreCase("1")) {
							responseForUpdate = "You Should Update the APK Immediately";
						} else if (responseForUpdate.equalsIgnoreCase("2")) {
							responseForUpdate = "Your Version is More Advanced than the Server Version. Contact Your Spot Center Coordinator for Resolution.";
						} else if (responseForUpdate.equalsIgnoreCase("3")) {
							responseForUpdate = "Errors Found. Please, Reopen Your Application.";
						}

						else {
							FileLog.logInfo(
									"Response to valid from WS ---> EvaluatorEntryActivity: backGroundService()"
											+ responseForUpdate, 0);
							responseForUpdate = "Failed to Connect with Server";
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					FileLog.logInfo(
							"WS Error ---> EvaluatorEntryActivity: backGroundService()"
									+ e.toString(), 0);
					responseForUpdate = "Error Code 101: Failed to Connect with Server";
					isException = true;
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				hideProgress();
				alertMessageToVerify(responseForUpdate, isException);
			}

		}.execute();
	}

	public void setMaxTotalInBetweenScripts(String bundleNo, String userId) {

		String maxQuery = "select max(max_total) as maxTotal from table_regulation where bundle_indicator = '"
				+ bundleNo.charAt(6)
				+ "'"
				+ "AND program_id = (select program_id from table_program where program_name IN"
				+ "(select program_name from table_bundle where bundle_no='"
				+ bundleNo + "' AND enter_by='" + userId + "'))";
		
//		select max(max_total) as maxTotal from table_regulation where bundle_indicator = '' AND
//				program_id = (select program_id from table_program where program_name IN (select program_name
//				from table_bundle where bundle_no='' AND enter_by=''))
		Cursor cursor = null;
		try {
			cursor = sEvalDatabase.executeSelectSQLQuery(maxQuery);
			int maxMarks = 0;
			if ((cursor != null) && (cursor.getCount() > 0)) {
				while (!(cursor.isAfterLast())) {
					maxMarks = Integer.parseInt(cursor.getString(cursor
							.getColumnIndex("maxTotal")));
					cursor.moveToNext();
				}
				SharedPreferences preferences = getSharedPreferences(
						SEConstants.SHARED_PREF_MAX_TOTAL, 0);
				SharedPreferences.Editor max_total_edit = preferences.edit();
				 maxx=(maxMarks / 5);
				max_total_edit.putInt(SEConstants.SHARED_PREF_MAX_TOTAL,
						maxx);
				max_total_edit.commit();
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					" Exception ---> EvaluatorEntryActivity: setMaxTotalInBetweenScripts() - "
							+ ex.toString(), 0);
		} finally {
			DataBaseUtility.closeCursor(cursor);
		}

	}

	public void alertMsg(String msg, final Boolean val) {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Smart Evaluation");
		myAlertDialog.setMessage(msg);

		myAlertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface Dialog, int arg1) {

						Dialog.dismiss();
						if(val){
						editText.setText("");
						editText.setFocusableInTouchMode(true);
						editText.setFocusable(true);
						}
					}
				});

		myAlertDialog.show();
	}

	public void showProgress(String msg) {
		if (progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(EvaluatorEntryActivity.this,
					"", msg);
			progressDialog.setCancelable(false);
		}
	}

	public void deleteMoreData() {
		int tableMarksCount = 0;
		Cursor curs1 = null, curs2 = null;
		String sqlStatement = "select barcode from table_marks";
		try {
			curs1 = sEvalDatabase.getRecordsUsingRawQuery(sqlStatement);
			if (curs1 != null) {
				tableMarksCount = curs1.getCount();
				Log.v("fffff", ""+tableMarksCount);
				if (tableMarksCount > 10) {
					String selectQuery = "DELETE FROM table_marks WHERE mark_id NOT IN ("
							+ "SELECT mark_id FROM (SELECT mark_id FROM "
							+ "table_marks ORDER BY mark_id DESC LIMIT 10));";
					
					sEvalDatabase.deleteMore(selectQuery);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DataBaseUtility.closeCursor(curs1);
		}
		int tableMarksHistoryCount = 0;
		sqlStatement = "select barcode from table_marks_history";
		try {
			curs2 = sEvalDatabase.getRecordsUsingRawQuery(sqlStatement);
			if (curs2 != null) {
				tableMarksHistoryCount = curs2.getCount();
				if (tableMarksHistoryCount > 10) {
					String selectQuery = "DELETE FROM table_marks_history WHERE mark_history_id NOT IN ("
							+ "SELECT mark_history_id FROM (SELECT mark_history_id FROM "
							+ "table_marks_history ORDER BY mark_history_id DESC LIMIT 10));";
					sEvalDatabase.deleteMore(selectQuery);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DataBaseUtility.closeCursor(curs2);
		}
	}
private Boolean checkEvaluatorId(String s){
	Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
	Matcher matcher = pattern.matcher(s);
	// Check if pattern matches 
	if (matcher.matches()) {
	  return true;
	}else{
		return false;
	}
}
public String getSeatNo() {
	String time = "";
	Cursor cursor = null;
	try {
		cursor = sEvalDatabase
				.executeSelectSQLQuery(
						"select seat_no from table_date_configuration"
						+ " where "
						+ "id=1");

		if ((cursor != null) && (cursor.getCount() > 0)) {
			while (!(cursor.isAfterLast())) {
				try{
				time = cursor.getString(cursor
								.getColumnIndex("seat_no"));
				cursor.moveToNext();
				if(TextUtils.isEmpty(time)){
				}
				}catch(Exception e){
					time="";
					e.printStackTrace();
				}
			}
		} else {
			FileLog.logInfo(
					"No User with bundle status 0,--->EvaluatorEntryActivity: getbundleNoByUserid(): ",
					0);
		}
	} catch (Exception ex) {
		FileLog.logInfo(
				"Exception --->EvaluatorEntryActivity: getbundleNoByUserid(): "
						+ ex.toString(), 0);
	} finally {
		DataBaseUtility.closeCursor(cursor);
	}
Log.v("time_interval", ""+time);
	return time;
}
	public void hideProgress() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}
