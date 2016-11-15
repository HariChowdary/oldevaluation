package com.infoplustech.smartscrutinization.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.infoplustech.smartscrutinization.callback.Scrutiny_NetworkCallback;
import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;
import com.infoplustech.smartscrutinization.handler.Scrutiny_Handler;

public class Scrutiny_SoapServiceManager {

	// http://192.168.1.6/ScrutinizingService/ScrutinizingService.asmx

	String METHOD_NAME_OBSERVATION = "GetMarkDetailsForObservation_NR";
	String SOAP_ACTION_OBSERVATION = "ScrutinizingService/"
			+ METHOD_NAME_OBSERVATION;
	String METHOD_NAME_CORRECTION = "GetObservationDetailsForCorrection_NR";
	String SOAP_ACTION_CORRECTION = "ScrutinizingService/"
			+ METHOD_NAME_CORRECTION;
	// private static String SCRIPT_TIMELIMIT_METHOD_NAME =
	// "GetScriptTimeLimit";
	// private static String NAME_SPACE_TIME_LIMIT = "SmartEvalService";
	// Soap action for SCRIPT_TIMELIMIT
	// private static String SOAP_ACTION_TIME_LIMIT = NAME_SPACE_TIME_LIMIT +
	// "/"
	// + SCRIPT_TIMELIMIT_METHOD_NAME;

	String METHOD_NAME_APK_UPDATE = "CheckAPKUpdate";
	String SOAP_ACTION_APK_UPDATE = "SmartEvalService/"
			+ METHOD_NAME_APK_UPDATE;

	String NAMESPACE = "ScrutinizingService";
	private static Scrutiny_SoapServiceManager serviceManager;
	private static ExecutorService executorService;
	Context mContext;

	public static Scrutiny_SoapServiceManager getInstance(Context context) {
		return serviceManager == null ? serviceManager = new Scrutiny_SoapServiceManager(
				context) : serviceManager;
	}

	public Scrutiny_SoapServiceManager(Context context) {
		mContext = context;
		executorService = Executors.newSingleThreadExecutor();
	}

	public void getMarkDetailsForObservation(final String bundleNo,
			final String userId, final String IMEI,
			final Scrutiny_NetworkCallback<Object> callback,
			final boolean scrutinySelection) {

		final Scrutiny_Handler srutinyHandler = new Scrutiny_Handler(callback);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(SSConstants.BUNDLE_NO, bundleNo);
				map.put(SSConstants.USER_ID, userId);
				map.put(SSConstants.TABLET_IMEI, IMEI);

				// scrutiny obs
				if (scrutinySelection) {
					soapWebService(
							soapRequestFromBundleNo(map,
									METHOD_NAME_OBSERVATION, "Observation"),
							srutinyHandler, SOAP_ACTION_OBSERVATION,
							METHOD_NAME_OBSERVATION, false);
				} else {
					// scrutiny corr
					soapWebService(
							soapRequestFromBundleNo(map,
									METHOD_NAME_CORRECTION, "Correction"),
							srutinyHandler, SOAP_ACTION_CORRECTION,
							METHOD_NAME_CORRECTION, false);

				}
			}
		});

	}

	public void soapWebServiceForAutoUpdate(
			final Scrutiny_NetworkCallback<Object> callback) {
		final Scrutiny_Handler srutinyHandler = new Scrutiny_Handler(callback);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				soapWebService("", srutinyHandler, SOAP_ACTION_APK_UPDATE,
						METHOD_NAME_APK_UPDATE, true);
			}
		});
	}

	public void soapWebServiceForAutoUpdateDB(final String db_version,
			final Scrutiny_NetworkCallback<Object> callback) {
		final Scrutiny_Handler srutinyHandler = new Scrutiny_Handler(callback);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				soapWebServiceForDBUpdate(srutinyHandler, db_version);
			}
		});
	}

	private final void soapWebService(final String pXmlString,
			Scrutiny_Handler srutinyHandler, String SOAP_ACTION,
			String METHOD_NAME, boolean is_auto_update) {

		try {
			SoapObject soapObject;
			if (is_auto_update) {
				soapObject = new SoapObject("SmartEvalService", METHOD_NAME);
			} else {
				soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
			}
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);

			if (is_auto_update) {
				TelephonyManager telephonyManager = (TelephonyManager) mContext
						.getSystemService(Context.TELEPHONY_SERVICE);
				String tabletIMEINo = telephonyManager.getDeviceId();
				String versionName = mContext.getPackageManager()
						.getPackageInfo(mContext.getPackageName(), 0).versionName;
				soapObject.addProperty("APKType", 2);
				soapObject.addProperty("APKVersion", versionName);
				soapObject.addProperty("TabletIMEINo", tabletIMEINo);

			} else {
				soapObject.addProperty("XMLScrutinize", pXmlString);
			}

			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(soapObject);
			envelope.setAddAdornments(false);
			HttpTransportSE ht;
			Utility instanceUtility = new Utility();
			String IPADDRESS = instanceUtility.getIPConfiguration();
			String URL = IPADDRESS
					+ "/ScrutinizingService/ScrutinizingService.asmx";
			if (is_auto_update) {

				ht = new HttpTransportSE(IPADDRESS
						+ "/EvalWebService/EvalService.asmx");
			} else {
				ht = new HttpTransportSE(URL);
			}
			ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			ht.debug = true;
			ht.call(SOAP_ACTION, envelope);

			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();
			String strResponse = response.toString();
			if (!TextUtils.isEmpty(strResponse)) {
				Message.obtain(srutinyHandler, SSConstants.SUCCESS, strResponse)
						.sendToTarget();
			} else {
				Message.obtain(srutinyHandler, SSConstants.FAILURE,
						SSConstants.FAILURE + "Null value returned from server")
						.sendToTarget();
			}
		} catch (Exception e) {
			Message.obtain(srutinyHandler, SSConstants.FAILURE,
					SSConstants.FAILURE + e.getMessage()).sendToTarget();
			e.printStackTrace();
		}

	}

	private final void soapWebServiceForDBUpdate(
			Scrutiny_Handler srutinyHandler, String db_version) {

		try {
			SoapObject soapObject;
			soapObject = new SoapObject("SmartEvalService",
					METHOD_NAME_APK_UPDATE);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);

			TelephonyManager telephonyManager = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String tabletIMEINo = telephonyManager.getDeviceId();
			// String versionName = mContext.getPackageManager().getPackageInfo(
			// mContext.getPackageName(), 0).versionName;
			soapObject.addProperty("APKType", 4);
			soapObject.addProperty("APKVersion", db_version);
			soapObject.addProperty("TabletIMEINo", tabletIMEINo);

			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(soapObject);
			envelope.setAddAdornments(false);
			HttpTransportSE ht;
			Utility instanceUtility = new Utility();
			String IPADDRESS = instanceUtility.getIPConfiguration();
			ht = new HttpTransportSE(IPADDRESS
					+ "/EvalWebService/EvalService.asmx");
			ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			ht.debug = true;
			ht.call(SOAP_ACTION_APK_UPDATE, envelope);

			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();
			String strResponse = response.toString();
			if (!TextUtils.isEmpty(strResponse)) {
				Message.obtain(srutinyHandler, SSConstants.SUCCESS, strResponse)
						.sendToTarget();
			} else {
				Message.obtain(srutinyHandler, SSConstants.FAILURE,
						SSConstants.FAILURE + "Null value returned from server")
						.sendToTarget();
			}
		} catch (Exception e) {
			Message.obtain(srutinyHandler, SSConstants.FAILURE,
					SSConstants.FAILURE + e.getMessage()).sendToTarget();
			e.printStackTrace();
		}

	}

	// ============================================================================================
	// Updating the script time limit in date_config table
	// ============================================================================================

	// public static String webServiceForScriptTimeLimit() {
	// String scriptTimeLimitResponse = null;
	// try {
	// SoapObject request = new SoapObject(NAME_SPACE_TIME_LIMIT,
	// SCRIPT_TIMELIMIT_METHOD_NAME);
	// SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	// SoapEnvelope.VER11);
	// envelope.dotNet = true;
	// envelope.implicitTypes = true;
	// envelope.enc = SoapSerializationEnvelope.ENC2003;
	// envelope.xsd = SoapEnvelope.XSD;
	// envelope.xsi = SoapEnvelope.XSI;
	// envelope.setOutputSoapObject(request);
	// envelope.setAddAdornments(false);
	// HttpTransportSE ht = new HttpTransportSE(SOAP_ACTION_TIME_LIMIT);
	// ht.debug = true;
	// ht.call(SOAP_ACTION_TIME_LIMIT, envelope);
	// final SoapPrimitive response = (SoapPrimitive) envelope
	// .getResponse();
	// scriptTimeLimitResponse = response.toString();
	//
	// }
	//
	// catch (Exception ex) {
	// FileLog.logInfo(
	// "webServiceForScriptTimeLimit() - "
	// + ex.toString(), 0);
	// }
	// return scriptTimeLimitResponse;
	// }

	// xml framing
	private final String soapRequestFromBundleNo(HashMap<String, String> map,
			String methodName, String mainTag) {
		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		builder.append("<" + mainTag + ">");
		Set<String> keys = map.keySet();
		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			String val = map.get(key);
			builder.append("<" + key + ">" + val + "</" + key + ">");
		}

		builder.append("</" + mainTag + ">");
		return builder.toString();
	}

	public String getRegulationFromServer() {
		String _NAMESPACE_FOR_REGULATION = "";
		String _METHOD_NAME_REGULATION = "";
		String SOAP_ACTION_FOR_REGULATION = "";

		String _regulation = null;
		try {
			SoapObject request = new SoapObject(_NAMESPACE_FOR_REGULATION,
					_METHOD_NAME_REGULATION);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			Utility instanceUtility = new Utility();
			String IPADDRESS = instanceUtility.getIPConfiguration();
			String URL = IPADDRESS
					+ "/ScrutinizingService/ScrutinizingService.asmx";
			HttpTransportSE ht = new HttpTransportSE(URL);
			ht.call(SOAP_ACTION_FOR_REGULATION, envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();
			_regulation = response.toString();

		}

		catch (Exception ex) {
			FileLog.logInfo(
					"webServiceForScriptTimeLimit() - " + ex.toString(), 0);
		}
		return _regulation;
	}
	
	public void sendScrutinyDataToServer(
			final Scrutiny_NetworkCallback<Object> callback) {
		final Scrutiny_Handler srutinyHandler = new Scrutiny_Handler(callback);
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try{
				String strResponse=checkServerForEvaluationUpdation();
				if (!TextUtils.isEmpty(strResponse)) {
					Message.obtain(srutinyHandler, SSConstants.SUCCESS1, strResponse)
							.sendToTarget();
				} else {
					Message.obtain(srutinyHandler, SSConstants.FAILURE1,
							SSConstants.MODE_ERROR)
							.sendToTarget();
				}
			} catch (Exception e) {
				Message.obtain(srutinyHandler, SSConstants.FAILURE1,
						SSConstants.MODE_ERROR).sendToTarget();
				e.printStackTrace();
			}
			}
		});
	}
	
	
	public String checkServerForEvaluationUpdation() {

		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(mContext);
		String sqlStatement = "select mark_scrutinize_id,barcode, bundle_serial_no, bundle_id, question_setid, "
				+ "mark1a, mark1b, mark1c, mark1d, mark1e, "
				+ "mark1f, mark1g, mark1h, mark1i, mark1j, "
				+ "mark2a, mark2b, mark2c, mark2d, mark2e, "
				+ "mark3a, mark3b, mark3c, mark3d, mark3e, "
				+ "mark4a, mark4b, mark4c, mark4d, mark4e, "
				+ "mark5a, mark5b, mark5c, mark5d, mark5e, "
				+ "mark6a, mark6b, mark6c, mark6d, mark6e, "
				+ "mark7a, mark7b, mark7c, mark7d, mark7e, "
				+ "mark8a, mark8b, mark8c, mark8d, mark8e, "
				+ "mark9a, mark9b, mark9c, mark9d, mark9e, "
				+ "mark10a, mark10b, mark10c, mark10d, mark10e, "
				+ "mark11a, mark11b, mark11c, mark11d, mark11e, "
				+ "total_mark, "
				+ "bundle_no, subject_code, question_setcode, user_id, "
				+ "r1_total, r2_total, r3_total, r4_total, r5_total, "
				+ "r6_total, r7_total, r8_total, r9_total, r10_total, r11_total, "
				+ "enter_on, is_updated_server, updated_on, tablet_IMEI, "
				+ "barcode_status, edit_userid, transferred_on, spot_centre_code, "
				+ "remark_1a, remark_1b, remark_1c, remark_1d, remark_1e, "
				+ "remark_1f, remark_1g, remark_1h, remark_1i, remark_1j, "
				+ "remark_2a, remark_2b, remark_2c, remark_2d, remark_2e, "
				+ "remark_3a, remark_3b, remark_3c, remark_3d, remark_3e, "
				+ "remark_4a, remark_4b, remark_4c, remark_4d, remark_4e, "
				+ "remark_5a, remark_5b, remark_5c, remark_5d, remark_5e, "
				+ "remark_6a, remark_6b, remark_6c, remark_6d, remark_6e, "
				+ "remark_7a, remark_7b, remark_7c, remark_7d, remark_7e, "
				+ "remark_8a, remark_8b, remark_8c, remark_8d, remark_8e, "
				+ "remark_9a, remark_9b, remark_9c, remark_9d, remark_9e, "
				+ "remark_10a, remark_10b, remark_10c, remark_10d, remark_10e, "
				+ "remark_11a, remark_11b, remark_11c, remark_11d, remark_11e, "
				+ "remark_r1_total,remark_r2_total,remark_r3_total,remark_r4_total, "
				+ "remark_r5_total,remark_r6_total,remark_r7_total,remark_r8_total, "
				+ "remark_r9_total,remark_r10_total,remark_r11_total,remark_grant_total, "
				+ "scrutinize_status,is_scrutinized,scrutinized_by, scrutinized_on, "
				+ "is_corrected, corrected_on, max_total from  "+SSConstants.TABLE_SCRUTINY_SAVE+" WHERE is_updated_server = 1 AND "
				+ " bundle_no in (select distinct bundle_no from "+SSConstants.TABLE_SCRUTINY_SAVE+" "
				+ " where is_updated_server = 1 AND is_scrutinized = 1 limit 1)";

		try {
			StringBuffer strBuf = new StringBuffer();
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			if (cursor != null) {
				if (cursor.getCount() != 0) {
					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {
						String delimiter;

						cursor.getColumnCount();
						for (int i = 1; i <= cursor.getColumnCount() - 1; i++) {
							try {
								if (i == cursor.getColumnCount() - 1) {
									delimiter = "),(";
								} else {
									delimiter = ",";
								}
								strBuf.append("'"
										+ (String.valueOf(cursor.getString(cursor
												.getColumnIndex(cursor
														.getColumnName(i)))))
										+ "'" + delimiter);
								Log.v(i+""+cursor.getColumnName(i), i+" "+String.valueOf(cursor.getString(cursor
										.getColumnIndex(cursor.getColumnName(i)))));
								// strBuf.append((String.valueOf(cursor
								// .getString(cursor.getColumnIndex(cursor
								// .getColumnName(i)))))
								// + delimiter);
							} catch (Exception e) {
								e.printStackTrace();
								FileLog.logInfo(
										"Exception with retrieving values from cursor - "
												+ e.toString(), 0);
							}

						}
					}
				}

				Scrutiny_DataBaseUtility.closeCursor(cursor);
				helper.close();
				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				String retrieveString = null;
				try {
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString = webServiceForEntryObservation("("
								+ replacechar.substring(0,
										replacechar.length() - 2), checkServerForOservationString());
					} 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLog.logInfo("Exception with service call - " + e.toString(), 0);
				}
			
				if (retrieveString != null) {
					if (retrieveString.contains("error")) {
					//	broadCastResultToActivity(SSConstants.MODE_ERROR);
						return SSConstants.MODE_ERROR;
					} else {
						try {
							if(retrieveString.contains(";")){
							String tableDatas[] = retrieveString.split(";");
							for(int i=0;i<tableDatas.length-1;i++){
								try{
							String deleteSql = tableDatas[i];
							Cursor cursorRecordsForDeletion = helper
									.deleteRecords(deleteSql);
							cursorRecordsForDeletion.close();
							Log.v("delete", deleteSql);
								}catch(Exception e){
									e.printStackTrace();
									//return SSConstants.MODE_ERROR;
								}
							}
							}
							//checkServerForOservationUpdation();  
							return SSConstants.MODE_SCRUTINY;
						//	broadCastResultToActivity(SSConstants.MODE_SCRUTINY);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							FileLog.logInfo("OservationUpdation =====> Exception in TableMarksHistory- "
											+ e.toString(), 0);
						}
					}

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLog.logInfo("OservationUpdation =====> SQLException in TableMarksHistory- " + e.toString(), 0);
			return SSConstants.MODE_ERROR;
		}
		
		return SSConstants.MODE_ERROR;
	}
	public String checkServerForOservationString() {
		String retrieveString = null;
		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(mContext);
		String sqlStatement = "select mark_scrutinize_id,barcode, bundle_serial_no, bundle_id, question_setid, "
				+ "mark1a, mark1b, mark1c, mark1d, mark1e, "
				+ "mark1f, mark1g, mark1h, mark1i, mark1j, "
				+ "mark2a, mark2b, mark2c, mark2d, mark2e, "
				+ "mark3a, mark3b, mark3c, mark3d, mark3e, "
				+ "mark4a, mark4b, mark4c, mark4d, mark4e, "
				+ "mark5a, mark5b, mark5c, mark5d, mark5e, "
				+ "mark6a, mark6b, mark6c, mark6d, mark6e, "
				+ "mark7a, mark7b, mark7c, mark7d, mark7e, "
				+ "mark8a, mark8b, mark8c, mark8d, mark8e, "
				+ "mark9a, mark9b, mark9c, mark9d, mark9e, "
				+ "mark10a, mark10b, mark10c, mark10d, mark10e, "
				+ "mark11a, mark11b, mark11c, mark11d, mark11e, "
				+ "total_mark, "
				+ "bundle_no, subject_code, question_setcode, user_id, "
				+ "r1_total, r2_total, r3_total, r4_total, r5_total, "
				+ "r6_total, r7_total, r8_total, r9_total, r10_total, r11_total, "
				+ "enter_on, is_updated_server, updated_on, tablet_IMEI, "
				+ "barcode_status, edit_userid, transferred_on, spot_centre_code, "
				+ "remark_1a, remark_1b, remark_1c, remark_1d, remark_1e, "
				+ "remark_1f, remark_1g, remark_1h, remark_1i, remark_1j, "
				+ "remark_2a, remark_2b, remark_2c, remark_2d, remark_2e, "
				+ "remark_3a, remark_3b, remark_3c, remark_3d, remark_3e, "
				+ "remark_4a, remark_4b, remark_4c, remark_4d, remark_4e, "
				+ "remark_5a, remark_5b, remark_5c, remark_5d, remark_5e, "
				+ "remark_6a, remark_6b, remark_6c, remark_6d, remark_6e, "
				+ "remark_7a, remark_7b, remark_7c, remark_7d, remark_7e, "
				+ "remark_8a, remark_8b, remark_8c, remark_8d, remark_8e, "
				+ "remark_9a, remark_9b, remark_9c, remark_9d, remark_9e, "
				+ "remark_10a, remark_10b, remark_10c, remark_10d, remark_10e, "
				+ "remark_11a, remark_11b, remark_11c, remark_11d, remark_11e, "
				+ "remark_r1_total,remark_r2_total,remark_r3_total,remark_r4_total, "
				+ "remark_r5_total,remark_r6_total,remark_r7_total,remark_r8_total, "
				+ "remark_r9_total,remark_r10_total,remark_r11_total,remark_grant_total, "
				+ "scrutinize_status,is_scrutinized,scrutinized_by, scrutinized_on, "
				+ "is_corrected, corrected_on, max_total from  "+SSConstants.TABLE_EVALUATION_SAVE+" WHERE is_updated_server = 1 AND "
				+ " bundle_no in (select distinct bundle_no from "+SSConstants.TABLE_EVALUATION_SAVE+" "
				+ " where is_updated_server = 1 AND is_scrutinized = 1 limit 1)";

		
		StringBuffer strBuf = new StringBuffer();
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			if (cursor != null) {
				try {
				if (cursor.getCount() != 0) {
					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {
						String delimiter;

						cursor.getColumnCount();
						for (int i = 1; i <= cursor.getColumnCount() - 1; i++) {

							try {
								cursor.getColumnName(i);
								if (i == cursor.getColumnCount() - 1) {
									delimiter = "),(";
								}

								else {
									delimiter = ",";
								}
								strBuf.append("'"
										+ (String.valueOf(cursor.getString(cursor
												.getColumnIndex(cursor
														.getColumnName(i)))))
										+ "'" + delimiter);
								// strBuf.append((String.valueOf(cursor
								// .getString(cursor.getColumnIndex(cursor
								// .getColumnName(i)))))
								// + delimiter);
							} catch (Exception e) {
								FileLog.logInfo(
										"Exception with retrieving values from cursor - "
												+ e.toString(), 0);
							}

						}
					}
				}

				Scrutiny_DataBaseUtility.closeCursor(cursor);
				helper.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				FileLog.logInfo(
						"OservationUpdation =====> SQLException in TableMarksHistory- "
								+ e.toString(), 0);
			}
				
				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString="("+ replacechar.substring(0, replacechar.length() - 2);
					//	retrieveString = webServiceForObservation();
					} 

			}
			
			return retrieveString;
	}
	
	private String webServiceForEntryObservation(final String pXmlString, final String pXmlString2) {
		String strcheck=null;
		final String METHOD_NAME = "UpdateObservationDetailsFromTablet_NR";
		final String SOAP_ACTION = "ScrutinizingService/" + METHOD_NAME;

		final String NAMESPACE = "ScrutinizingService";
	    Utility instanceUtility = new Utility();
		String URL = instanceUtility.getIPConfiguration()
				+ "/ScrutinizingService/ScrutinizingService.asmx";

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLScrutinize", pXmlString);
			request.addProperty("XMLScrutinizeEntry", pXmlString2);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);

			HttpTransportSE ht = new HttpTransportSE(URL, 20000);
		//	ht.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			// ht.debug = true;
			try{
			ht.call(SOAP_ACTION, envelope);
			}catch(Exception e){
				e.printStackTrace();
			}
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();
			String str = response.toString();
			Log.v("response", "response  "+str);
			 strcheck = str;
			
			try {
				envelope=null;
				instanceUtility=null;
				URL=null;
				 ht.reset();
				if(ht.getConnection()!=null){
                ht.getConnection().disconnect();
                Log.v("disconnect", "disconnect");
				}
				ht=null;
				request= null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
		}

		catch (Exception e) {
			e.printStackTrace();
			FileLog.logInfo("Exception with webServiceForObservation call - "
					+ e.toString(), 0);
		}

		return strcheck;
	}
}
