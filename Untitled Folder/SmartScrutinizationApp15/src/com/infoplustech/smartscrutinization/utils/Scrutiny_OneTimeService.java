package com.infoplustech.smartscrutinization.utils;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;

public class Scrutiny_OneTimeService extends IntentService {

	// Add comments for what you are using these variables for
	StringBuffer strBuf;
	String scrutniDatas;

	String strcheck;

	public Scrutiny_OneTimeService() {
		super("ScrutinyOneTimeService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Utility instanceUtitlity = new Utility();

		// TODO Auto-generated method stub
		String _mode = intent.getStringExtra(SSConstants.MODE);
		// if he clicks on scrutiny button
		if (_mode.equals(SSConstants.SCRUTINY)) {

			if (instanceUtitlity.isNetworkAvailable(this)) {
				// ..OBSERVATION..
//				String s=checkServerForEvaluationUpdation();
//				if(s.equals(SSConstants.MODE_SCRUTINY)){
//				//checkServerForOservationUpdation();  
//					broadCastResultToActivity(SSConstants.MODE_SCRUTINY);
//				}else{
//				broadCastResultToActivity(SSConstants.MODE_ERROR);
//				}
//			//	broadCastResultToActivity(_mode);
				
				Scrutiny_SoapServiceManager manager = Scrutiny_SoapServiceManager
				.getInstance(this);
		String ss=manager.checkServerForEvaluationUpdation();
		broadCastResultToActivity(ss);
		Log.v("harii", "hari  "+ss);
			} else {
				FileLog.logInfo("Network connection fails ", 0);
				broadCastResultToActivity(SSConstants.MODE_NETWORK_FAILS);
			}
		}

		// if he clicks on scrutiny correction button
		else if (_mode.equals(SSConstants.SCRUTINY_CORRECTION)) {
			if (instanceUtitlity.isNetworkAvailable(this)) {
				// ..CORRECTION..
				checkServerForCorrectionUpdation();
				broadCastResultToActivity(_mode);
			} else {
				FileLog.logInfo("Network connection fails ", 0);
				broadCastResultToActivity(SSConstants.MODE_NETWORK_FAILS);
			}
		}

	}

	public void checkServerForCorrectionUpdation() {

		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(this);
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
				+ "barcode_status, edit_userid, transferred_on,  spot_centre_code, "
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
				+ "remark_r9_total,remark_r10_total,remark_r11_total, "
				+ "remark_grant_total, "
				+ "scrutinize_status,is_scrutinized,scrutinized_by, scrutinized_on, "
				+ "is_corrected, corrected_on,max_total from "
				+ "table_marks_scrutinize WHERE is_updated_server = 1 AND "
				+ " bundle_no in (select distinct bundle_no from table_marks_scrutinize "
				+ " where is_updated_server = 1 AND is_corrected = 1 limit 1) ";
 
		try {
			strBuf = new StringBuffer();
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);

			if (cursor != null) {
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
										"Exception with retrieving values with cursor - "
												+ e.toString(), 0);
							}

						}
					}
				}

				Scrutiny_DataBaseUtility.closeCursor(cursor);

				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				String retrieveString = null;
				try {
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString = webServiceForCorrection("("
								+ replacechar.substring(0,
										replacechar.length() - 2));
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLog.logInfo(
							"Exception with service call - " + e.toString(), 0);
				}

				if (retrieveString != null) {
					if (retrieveString.contains("error")) {
						Log.v("error response","retrieveString "+retrieveString);
						broadCastResultToActivity(SSConstants.MODE_ERROR);
					} else {
						Log.v("response","retrieveString "+retrieveString);
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
								broadCastResultToActivity(SSConstants.MODE_CORRECTION);
								}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							FileLog.logInfo(
									"CorrectionUpdation =====> Exception in TableMarksScrutini- "
											+ e.toString(), 0);
						}

					}

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLog.logInfo(
					"CorrectionUpdation =====> SQLException in TableMarksScrutini- "
							+ e.toString(), 0);
		}
	} 

	public void checkServerForOservationUpdation() {

		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(this);
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

		try {
			strBuf = new StringBuffer();
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			if (cursor != null) {
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

				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				String retrieveString = null;
				try {
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString = webServiceForObservation("("
								+ replacechar.substring(0,
										replacechar.length() - 2));
					} 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLog.logInfo(
							"Exception with service call - " + e.toString(), 0);
				}

				if (retrieveString != null) {
					if (retrieveString.equalsIgnoreCase("error")) {
						broadCastResultToActivity(SSConstants.MODE_ERROR);
						
					} else {
						try {
						//	checkServerForEvaluationUpdation();
							if(retrieveString.contains(";")){
							String tableDatas[] = retrieveString.split(";");
							for(int i=0;i<tableDatas.length;i++){
								try{
							String deleteSql = tableDatas[i];
							Cursor cursorRecordsForDeletion = helper
									.deleteRecords(deleteSql);
							cursorRecordsForDeletion.close();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							}
							broadCastResultToActivity(SSConstants.MODE_SCRUTINY);
							//checkServerForEvaluationUpdation();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							FileLog.logInfo(
									"OservationUpdation =====> Exception in TableMarksHistory- "
											+ e.toString(), 0);
							e.printStackTrace();
						}

					}

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLog.logInfo(
					"OservationUpdation =====> SQLException in TableMarksHistory- "
							+ e.toString(), 0);
		}
	}
	
	public String checkServerForEvaluationUpdation() {

		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(this);
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
					if (retrieveString.equalsIgnoreCase("error")) {
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
	/*public void checkServerForEvaluationUpdation() {

		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(this);
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
			strBuf = new StringBuffer();
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
								}

								else {
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

				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				String retrieveString = null;
				try {
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString = webServiceForEntryObservation("("
								+ replacechar.substring(0,
										replacechar.length() - 2));
					} 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					FileLog.logInfo(
							"Exception with service call - " + e.toString(), 0);
				}

				if (retrieveString != null&& !retrieveString.equals("1")) {
					if (retrieveString.equalsIgnoreCase("error")) {
						broadCastResultToActivity(SSConstants.MODE_ERROR);
						
					} else {
						try {
							if(retrieveString.contains(";")){
							String tableDatas[] = retrieveString.split(";");
							for(int i=0;i<tableDatas.length;i++){
								try{
							String deleteSql = tableDatas[i];
							Cursor cursorRecordsForDeletion = helper
									.deleteRecords(deleteSql);
							cursorRecordsForDeletion.close();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							}
						//	broadCastResultToActivity(SSConstants.MODE_SCRUTINY);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							FileLog.logInfo(
									"OservationUpdation =====> Exception in TableMarksHistory- "
											+ e.toString(), 0);
						}

					}

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			FileLog.logInfo(
					"OservationUpdation =====> SQLException in TableMarksHistory- "
							+ e.toString(), 0);
		}
	}*/

	// ============================================================================
	// Implementation of all methods which talks with .net webservice
	// ============================================================================
	private String webServiceForObservation(final String pXmlString) {
		final String METHOD_NAME = "UpdateObservationentryDetailsFromTablet";
		final String SOAP_ACTION = "ScrutinizingService/" + METHOD_NAME;

		final String NAMESPACE = "ScrutinizingService";
		final Utility instanceUtility = new Utility();
		final String URL = instanceUtility.getIPConfiguration()
				+ "/ScrutinizingService/ScrutinizingService.asmx";

		try {
			final	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			final	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLScrutinize", pXmlString);
			Log.v("hari", "UpdateObservationentryDetailsFromTablet "+pXmlString);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);
			
			final HttpTransportSE ht = new HttpTransportSE(URL);
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
				if(ht.getConnection()!=null)
                ht.getConnection().disconnect();
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
	private String webServiceForEntryObservation(final String pXmlString, final String pXmlString2) {
		final String METHOD_NAME = "UpdateObservationDetailsFromTablet_NR";
		final String SOAP_ACTION = "ScrutinizingService/" + METHOD_NAME;

		final String NAMESPACE = "ScrutinizingService";
		final Utility instanceUtility = new Utility();
		final String URL = instanceUtility.getIPConfiguration()
				+ "/ScrutinizingService/ScrutinizingService.asmx";

		try {
			final SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLScrutinize", pXmlString);
			request.addProperty("XMLScrutinizeEntry", pXmlString2);
			Log.v("hari", "UpdateObservationDetailsFromTablet_NR "+pXmlString);
			Log.v("hari", "UpdateObservationDetailsFromTablet_NR2 "+pXmlString2);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);

			final HttpTransportSE ht = new HttpTransportSE(URL);
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
				if(ht.getConnection()!=null)
                ht.getConnection().disconnect();
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
	private String webServiceForCorrection(final String pXmlString) {
		final String METHOD_NAME = "UpdateCorrectionDetailsFromTablet_NR";
		final String SOAP_ACTION = "ScrutinizingService/" + METHOD_NAME;

		final String NAMESPACE = "ScrutinizingService";
		Utility instanceUtility = new Utility();
		final String URL = instanceUtility.getIPConfiguration()
				+ "/ScrutinizingService/ScrutinizingService.asmx";

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLScrutinize", pXmlString);
			Log.v("XMLScrutinize", pXmlString);
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

			String str = response.toString();
			strcheck = str;
		}

		catch (Exception e) {
			e.printStackTrace();
			FileLog.logInfo("Exception with webServiceForCorrection call - "
					+ e.toString(), 0);
		} 
		return strcheck;
	}

	// broadcast result
	private void broadCastResultToActivity(String mode) {
		Intent intent = new Intent(SSConstants.NOTIFICATION);
	intent.putExtra(SSConstants.MODE, mode);
	Log.i("mode in broadcast", mode);
	sendBroadcast(intent);
	
//		Intent _intent = new Intent(Scrutiny_OneTimeService.this,
//				ShowBundleCompletedMessage.class);
//		_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(_intent);
	}
	public String checkServerForOservationString() {
		String retrieveString = null;
		SScrutinyDatabase helper = SScrutinyDatabase.getInstance(this);
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

		
			strBuf = new StringBuffer();
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
}
