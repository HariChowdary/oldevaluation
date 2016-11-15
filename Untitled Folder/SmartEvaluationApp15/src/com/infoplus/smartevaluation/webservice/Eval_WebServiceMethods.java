package com.infoplus.smartevaluation.webservice;


import java.net.SocketTimeoutException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.infoplus.smartevaluation.Utility;
import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;

public class Eval_WebServiceMethods {

	StringBuffer strBuf;
	String scrutniDatas;
	String HostString = Utility.getIPConfiguration();
	String strcheck;

	private static Eval_WebServiceMethods webServiceMethods;

	public static Eval_WebServiceMethods getInstance() {
		return webServiceMethods == null ? new Eval_WebServiceMethods()
				: webServiceMethods;
	}

	public void checkServerBundleHistoryUpdation(Context context) {

		DBHelper helper = DBHelper.getInstance(context);

		// You have the table names in dbHelper?

		// Table names and field names are available in contants --> Push to
		// contant file
		String sqlStatement = "select tb.*  from table_bundle_history tb "
				+ "WHERE is_updated_server = 0";
		try {
			Cursor cursorRecords = helper.getRecordsUsingRawQuery(sqlStatement);
			if (cursorRecords != null) {
				if (cursorRecords.getCount() != 0) {
					for (cursorRecords.moveToFirst(); !(cursorRecords
							.isAfterLast()); cursorRecords.moveToNext()) {

						String bundle_no = cursorRecords
								.getString(cursorRecords
										.getColumnIndex("bundle_no"));
						String subject_code = cursorRecords
								.getString(cursorRecords
										.getColumnIndex("subject_code"));
						String status = cursorRecords.getString(cursorRecords
								.getColumnIndex("status"));
						String enter_by = cursorRecords.getString(cursorRecords
								.getColumnIndex("enter_by"));
						String enter_on = cursorRecords.getString(cursorRecords
								.getColumnIndex("enter_on"));
						String updated_on = cursorRecords
								.getString(cursorRecords
										.getColumnIndex("updated_on"));
						String tablet_IMEI = cursorRecords
								.getString(cursorRecords
										.getColumnIndex("tablet_IMEI"));

						strBuf = new StringBuffer(
								"<?xml version=\"1.0\" encoding=\"utf-8\"?>");

						strBuf.append("<BundleStatusHistory>");

						strBuf.append("<bundle_no>");
						strBuf.append(bundle_no);
						strBuf.append("</bundle_no>");

						strBuf.append("<subject_code>");
						strBuf.append(subject_code);
						strBuf.append("</subject_code>");

						strBuf.append("<status>");
						strBuf.append(status);
						strBuf.append("</status>");

						strBuf.append("<enter_by>");
						strBuf.append(enter_by);
						strBuf.append("</enter_by>");

						strBuf.append("<enter_on>");
						strBuf.append(enter_on);
						strBuf.append("</enter_on>");

						strBuf.append("<updated_on>");
						strBuf.append(updated_on);
						strBuf.append("</updated_on>");

						strBuf.append("<TabletIMEI>");
						strBuf.append(tablet_IMEI);
						strBuf.append("</TabletIMEI>");

						strBuf.append("<apk_version>");
						strBuf.append(cursorRecords.getString(cursorRecords
								.getColumnIndex("apk_version")));
						strBuf.append("</apk_version>");

						strBuf.append("<is_unreadable>");
						strBuf.append(cursorRecords.getString(cursorRecords
								.getColumnIndex("is_unreadable")));
						strBuf.append("</is_unreadable>");

						strBuf.append("<program_name>");
						strBuf.append(cursorRecords.getString(cursorRecords
								.getColumnIndex("program_name")));
						strBuf.append("</program_name>");

						strBuf.append("<is_deleted>");
						strBuf.append(cursorRecords.getString(cursorRecords
								.getColumnIndex("is_deleted")));
						strBuf.append("</is_deleted>");

						strBuf.append("</BundleStatusHistory>");

						String retrieveString = null;
						try {

							retrieveString = webServiceForBundleHistory(strBuf
									.toString());

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							//
							if (!FileLog.logInfo(
									"Exception with service call - "
											+ e.toString(), 0))
								;
						}

						if (retrieveString != null) {

							// You should also trim both sides of the string
							if (!retrieveString.equalsIgnoreCase("error")) {
								try {
									String updateSql = retrieveString;
									Cursor cursorRecordsForUpdation = helper
											.updateRecordsUsingRawQuery(updateSql);

									if (cursorRecordsForUpdation == null) {
										if (!FileLog
												.logInfo(
														"DBUpdate Failed - TableBundleHistory",
														0));
									}

									DataBaseUtility
											.closeCursor(cursorRecordsForUpdation);

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//
									if (!FileLog.logInfo(
											"Exception - TableBundleHistory---"
													+ e.toString(), 0))
										;
								}
							} else {
								// Write to log that you received an error for
								// the webservice
								//
								if (!FileLog.logInfo(
										"WSError - TableBundleHistory --- "
												+ retrieveString.toString(), 0))
									;
							}

						}

					}
				}

				DataBaseUtility.closeCursor(cursorRecords);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//
			if (!FileLog.logInfo(
					"SQLException - TableBundleHistory---" + e.toString(), 0));
		}

	}

	/*
	 * public void checkServerMarkHistoryUpdation(Context context) {
	 * 
	 * DBHelper helper = DBHelper.getInstance(context); String sqlStatement =
	 * "select * from  table_marks_history " +
	 * "WHERE is_updated_server in (0,2)";
	 * 
	 * try { Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement); if
	 * (cursor != null) { if (cursor.getCount() != 0) { for
	 * (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor .moveToNext()) {
	 * 
	 * strBuf = new StringBuffer(
	 * "<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
	 * 
	 * strBuf.append("<EvalMarkHistory>"); strBuf.append("<EvalCode>" +
	 * cursor.getString(cursor .getColumnIndex("user_id")) + "</EvalCode>");
	 * 
	 * strBuf.append("<SubjectCode>" + cursor.getString(cursor
	 * .getColumnIndex("subject_code")) + "</SubjectCode>");
	 * 
	 * strBuf.append("<BundleNo>" + cursor.getString(cursor
	 * .getColumnIndex("bundle_no")) + "</BundleNo>"); strBuf.append("<Barcode>"
	 * + cursor.getString(cursor .getColumnIndex("barcode")) + "</Barcode>");
	 * strBuf.append("<BundleSerialNo>" + cursor.getString(cursor
	 * .getColumnIndex("bundle_serial_no")) + "</BundleSerialNo>");
	 * 
	 * strBuf.append("<QuestionSetcode>" + cursor.getString(cursor
	 * .getColumnIndex("question_setcode")) + "</QuestionSetcode>");
	 * 
	 * // Loop and add the marks for 1a, 1b, etc.. for (int rownum = 1; rownum
	 * <= 8; rownum++) { for (int colnum = 1; colnum <= 5; colnum++) { String
	 * colstr = ""; switch (colnum) { case 1: colstr = "a"; break; case 2:
	 * colstr = "b"; break; case 3: colstr = "c"; break; case 4: colstr = "d";
	 * break; case 5: colstr = "e"; break; } strBuf.append("<Mark" + rownum +
	 * colstr.toUpperCase() + ">" + cursor.getString(cursor
	 * .getColumnIndex("mark" + rownum + colstr.toLowerCase())) + "</Mark" +
	 * rownum + colstr.toUpperCase() + ">"); } strBuf.append("<Mark" + rownum +
	 * "Total>" + cursor.getString(cursor .getColumnIndex("r" + rownum +
	 * "_total")) + "</Mark" + rownum + "Total>"); }
	 * 
	 * strBuf.append("<TotalMark>" + cursor.getString(cursor
	 * .getColumnIndex("total_mark")) + "</TotalMark>");
	 * 
	 * strBuf.append("<EnterOn>" + cursor.getString(cursor
	 * .getColumnIndex("enter_on")) + "</EnterOn>");
	 * 
	 * strBuf.append("<TabletIMEI>" + cursor.getString(cursor
	 * .getColumnIndex("tablet_IMEI")) + "</TabletIMEI>");
	 * 
	 * strBuf.append("<BarcodeStatus>" + cursor.getString(cursor
	 * .getColumnIndex("barcode_status")) + "</BarcodeStatus>");
	 * strBuf.append("<EditUserId>" + cursor.getString(cursor
	 * .getColumnIndex("edit_userid")) + "</EditUserId>");
	 * 
	 * strBuf.append("<QuestionSetid>" + cursor.getString(cursor
	 * .getColumnIndex("question_setid")) + "</QuestionSetid>");
	 * 
	 * strBuf.append("</EvalMarkHistory>");
	 * 
	 * String retrieveString = null; try { // Toast.makeText(this,
	 * strBuf.toString(), // Toast.LENGTH_SHORT).show();
	 * 
	 * retrieveString = webServiceForMarkHistory(strBuf .toString());
	 * 
	 * // Toast.makeText(this, retrieveString, // Toast.LENGTH_SHORT) //
	 * .show(); } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); // if (!FileLog.logInfo(
	 * "Exception with service call - " + e.toString(), 0)) ; }
	 * 
	 * if (retrieveString != null) { if
	 * (!retrieveString.equalsIgnoreCase("error")) { try {
	 * 
	 * String updateSql = retrieveString; Cursor cursorRecordsForUpdation =
	 * helper .updateRecordsUsingRawQuery(updateSql); if
	 * (cursorRecordsForUpdation == null) { // if (!FileLog .logInfo(
	 * "Updation of record failed in TableMarksHistory - ", 0)) ; }
	 * 
	 * DataBaseUtility .closeCursor(cursorRecordsForUpdation);
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); // if (!FileLog.logInfo(
	 * "Exception in TableMarksHistory- " + e.toString(), 0)) ; } } else { // if
	 * (!FileLog.logInfo( "WSError - TableMarksHistory - ", 0)) ; }
	 * 
	 * }
	 * 
	 * } }
	 * 
	 * DataBaseUtility.closeCursor(cursor); }
	 * 
	 * } catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); // if (!FileLog.logInfo(
	 * "SQLException in TableMarksHistory---- " + e.toString(), 0)) ; } }
	 */

	public void checkServerBundleUpdation(Context context) {

		DBHelper helper = DBHelper.getInstance(context);

		String sqlStatement = "select bundle_no,subject_code,status,enter_by,enter_on,updated_on,tablet_IMEI,apk_version,is_unreadable,program_name, (select count(distinct bundle_serial_no) from table_marks tm where tm.bundle_no=tb.bundle_no) as script_count "
				+ " from table_bundle tb where is_updated_server = 0 and status IN (1,2)";

		try {

			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			if (cursor != null) {
				if (cursor.getCount() != 0) {
					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {

						String bundle_no = cursor.getString(cursor
								.getColumnIndex("bundle_no"));
						String subject_code = cursor.getString(cursor
								.getColumnIndex("subject_code"));
						String status = cursor.getString(cursor
								.getColumnIndex("status"));
						String enter_by = cursor.getString(cursor
								.getColumnIndex("enter_by"));
						String enter_on = cursor.getString(cursor
								.getColumnIndex("enter_on"));
						String updated_on = cursor.getString(cursor
								.getColumnIndex("updated_on"));
						String tablet_IMEI = cursor.getString(cursor
								.getColumnIndex("tablet_IMEI"));

						strBuf = new StringBuffer(
								"<?xml version=\"1.0\" encoding=\"utf-8\"?>");

						strBuf.append("<BundleStatus>");

						strBuf.append("<bundle_no>");
						strBuf.append(bundle_no);
						strBuf.append("</bundle_no>");

						strBuf.append("<subject_code>");
						strBuf.append(subject_code);
						strBuf.append("</subject_code>");

						strBuf.append("<status>");
						strBuf.append(status);
						strBuf.append("</status>");

						strBuf.append("<enter_by>");
						strBuf.append(enter_by);
						strBuf.append("</enter_by>");

						strBuf.append("<enter_on>");
						strBuf.append(enter_on);
						strBuf.append("</enter_on>");

						strBuf.append("<updated_on>");
						strBuf.append(updated_on);
						strBuf.append("</updated_on>");

						strBuf.append("<TabletIMEI>");
						strBuf.append(tablet_IMEI);
						strBuf.append("</TabletIMEI>");

						strBuf.append("<apk_version>");
						strBuf.append(cursor.getString(cursor
								.getColumnIndex("apk_version")));
						strBuf.append("</apk_version>");

						strBuf.append("<is_unreadable>");
						strBuf.append(cursor.getString(cursor
								.getColumnIndex("is_unreadable")));
						strBuf.append("</is_unreadable>");

						strBuf.append("<program_name>");
						strBuf.append(cursor.getString(cursor
								.getColumnIndex("program_name")));
						strBuf.append("</program_name>");

						strBuf.append("<script_count>");
						strBuf.append(cursor.getString(cursor
								.getColumnIndex("script_count")));
						strBuf.append("</script_count>");

						strBuf.append("</BundleStatus>");

						String retrieveString = null;
						try {

							retrieveString = webServiceForBundle(strBuf
									.toString());

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  

						if (retrieveString != null) {
							Log.v("retrieveS2222tring", "retrieveString "+retrieveString);
							if (!retrieveString.equalsIgnoreCase("error")) {
								try {
									String updateSql = retrieveString;
									Cursor cursorRecordsForUpdation = helper
											.updateRecordsUsingRawQuery(updateSql);
									if (cursorRecordsForUpdation == null) {
										//
										if (!FileLog
												.logInfo(
														"Updation of record failed - TableBundle ",
														0))
											;
									}
    
									DataBaseUtility
											.closeCursor(cursorRecordsForUpdation);

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									//
									if (!FileLog.logInfo(
											"Exception - TableBundle "
													+ e.toString(), 0));
								}
							} else {
								//
								if (!FileLog.logInfo(
										"WSError - TableBundle --- "
												+ retrieveString.toString(), 0))
									;
							}

						}

					}
				}

				DataBaseUtility.closeCursor(cursor);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			if (!FileLog.logInfo(
					"SQLException - TableBundle ---- " + e.toString(), 0))
				;
		}

	}

	public void checkServerMarkHistoryUpdation(Context context) {
		DBHelper helper = DBHelper.getInstance(context);
		try {

			// Normal Evaluation
			// String sqlStatement = "select user_id, subject_code, bundle_no,"
			// + "barcode, bundle_serial_no,question_setcode,"
			// + "mark1a, mark1b, mark1c, mark1d, mark1e,"
			// + "mark2a, mark2b, mark2c, mark2d, mark2e,"
			// + "mark3a, mark3b, mark3c, mark3d, mark3e,"
			// + "mark4a, mark4b, mark4c, mark4d, mark4e,"
			// + "mark5a, mark5b, mark5c, mark5d, mark5e,"
			// + "mark6a, mark6b, mark6c, mark6d, mark6e,"
			// + "mark7a, mark7b, mark7c, mark7d, mark7e,"
			// + "mark8a, mark8b, mark8c, mark8d, mark8e,"
			// + "total_mark, r1_total, r2_total, r3_total,"
			// + "r4_total, r5_total, r6_total, r7_total, r8_total,"
			// + "enter_on, tablet_IMEI, barcode_status,"
			// + "edit_userid,question_setid from table_marks_history tm WHERE "
			// + "tm.is_updated_server IN (0,2) AND tm.bundle_no "
			// + "IN(select distinct tb.bundle_no from table_bundle "
			// +
			// "tb join table_marks_history ems on tb.bundle_no = ems.bundle_no "
			// + "where ems.is_updated_server IN (0,2) AND tb.status in (1,2) "
			// + "AND tb.is_updated_server=1 limit 1)";

			// R_13 B.tech

			String sqlStatement = "select user_id, subject_code, bundle_no, barcode, bundle_serial_no, "
					+ "question_setcode,"
					+ "mark1a, mark1b, mark1c, mark1d, mark1e, mark1f, mark1g, mark1h, mark1i, mark1j, "
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
					+ "total_mark, r1_total, r2_total, r3_total, "
					+ "r4_total, r5_total, r6_total, r7_total, "
					+ "r8_total, r9_total, r10_total, r11_total, "
					+ "enter_on, tablet_IMEI, "
					+ "barcode_status, edit_userid, question_setid from table_marks_history tm WHERE  "
					+ " tm.is_updated_server "
					+ " IN (0,2) AND tm.bundle_no IN (select distinct tb.bundle_no from table_bundle tb join "
					+ " table_marks_history ems on tb.bundle_no = ems.bundle_no where ems.is_updated_server IN (0,2) "
					+ " AND tb.status in (1,2) AND tb.is_updated_server=1 limit 1) ";

			// Above query limits this function to send only 1 bundle in each
			// call
			// Instead get all the bundles
			// First Get all the bundles whose status is 1 or 2
			// Loop through the bundles for each bundle
			// Get the marks from table_marks
			// Call the webservice
			// Update table_marks
			// Update bundle table
			// End Loop
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			strBuf = new StringBuffer();
			if (cursor != null) {
				if (cursor.getCount() != 0) {
					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {

						String delimiter;

						for (int i = 0; i <= cursor.getColumnCount() - 1; i++) {

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
								// int a = 0;
								e.printStackTrace();
							}

						}
					}
				}

				DataBaseUtility.closeCursor(cursor);

				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				String retrieveString = null;
				try {
					// Toast.makeText(this, strBuf.toString(),
					// Toast.LENGTH_SHORT).show();
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString = webServiceForMarkHistory("("
								+ replacechar.substring(0,
										replacechar.length() - 2));
					}

					// Toast.makeText(this, retrieveString,
					// Toast.LENGTH_SHORT)
					// .show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					if (!FileLog.logInfo(
							"Exception with service call --- " + e.toString(),
							0))
						;
				}

				if (retrieveString != null) {
					Log.v("retrieveString", "retrie11veString "+retrieveString);
					if (!retrieveString.equalsIgnoreCase("error")) {
						try {

							String updateSql = retrieveString;
							Cursor cursorRecordsForUpdation = helper
									.updateRecordsUsingRawQuery(updateSql);
							if (cursorRecordsForUpdation == null) {

								if (!FileLog
										.logInfo(
												"Updation of record failed in TableMarkHistory - ",
												0))
									;
							}

							DataBaseUtility
									.closeCursor(cursorRecordsForUpdation);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if (!FileLog.logInfo("Exception in TableMarks - "
									+ e.toString(), 0));
						}
					} else {

						if (!FileLog.logInfo(
								"WSError - checkServerMarkHistoryUpdation --- "
										+ retrieveString.toString(), 0))
							;
					}

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			if (!FileLog.logInfo("SQLException in TableMarkMarkHistory ---- "
					+ e.toString(), 0))
				;
		}

	}

	// currently using service bulk insertionofRecords for markhistory
	public void checkServerNewMarkUpdation(Context context) {
		DBHelper helper = DBHelper.getInstance(context);
		try {

			// Normal Evaluation
			// String sqlStatement =
			// "select user_id, subject_code, bundle_no, barcode, bundle_serial_no, "
			// + "question_setcode,"
			// + "mark1a, mark1b, mark1c, mark1d, mark1e, "
			// + "mark2a, mark2b, mark2c, mark2d, mark2e, "
			// + "mark3a, mark3b, mark3c, mark3d, mark3e, "
			// + "mark4a, mark4b, mark4c, mark4d, mark4e, "
			// + "mark5a, mark5b, mark5c, mark5d, mark5e, "
			// + "mark6a, mark6b, mark6c, mark6d, mark6e, "
			// + "mark7a, mark7b, mark7c, mark7d, mark7e, "
			// + "mark8a, mark8b, mark8c, mark8d, mark8e, "
			// + "total_mark, r1_total, r2_total, r3_total, "
			// + "r4_total, r5_total, r6_total, r7_total, "
			// + "r8_total, enter_on,tablet_IMEI,updated_on,"
			// + "barcode_status,edit_userid from table_marks tm WHERE  "
			// + "tm.bundle_no  AND tm.is_updated_server"
			// +
			// " IN (0,2) AND tm.bundle_no IN (select distinct tb.bundle_no from table_bundle tb join"
			// +
			// " table_marks ems on tb.bundle_no = ems.bundle_no where ems.is_updated_server IN (0,2)"
			// + " AND tb.status in (1,2) AND tb.is_updated_server=1 limit 1) ";

			// R_13 B.tech

			String sqlStatement = "select user_id, subject_code, bundle_no, barcode, bundle_serial_no, "
					+ "question_setcode,"
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
					+ "total_mark, r1_total, r2_total, r3_total, "
					+ "r4_total, r5_total, r6_total, r7_total, "
					+ "r8_total, r9_total,r10_total,r11_total, "
					+ "enter_on,tablet_IMEI,updated_on, "
					+ "barcode_status,edit_userid from table_marks tm WHERE  "
					+ "tm.bundle_no  AND tm.is_updated_server "
					+ " IN (0,2) AND tm.bundle_no IN (select distinct tb.bundle_no from table_bundle tb join "
					+ " table_marks ems on tb.bundle_no = ems.bundle_no where ems.is_updated_server IN (0,2) "
					+ " AND tb.status in (1,2) AND tb.is_updated_server=1 limit 1) ";

			// Above query limits this function to send only 1 bundle in each
			// call
			// Instead get all the bundles
			// First Get all the bundles whose status is 1 or 2
			// Loop through the bundles for each bundle
			// Get the marks from table_marks
			// Call the webservice
			// Update table_marks
			// Update bundle table
			// End Loop
			Cursor cursor = helper.getRecordsUsingRawQuery(sqlStatement);
			strBuf = new StringBuffer();
			if (cursor != null) {
				if (cursor.getCount() != 0) {
					for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor
							.moveToNext()) {

						String delimiter;

						for (int i = 0; i <= cursor.getColumnCount() - 1; i++) {

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
								// int a = 0;
							}

						}
					}
				}

				DataBaseUtility.closeCursor(cursor);

				String sample = strBuf.toString();
				String replacechar = sample.replace("'null'", "NULL");

				String retrieveString = null;
				try {
					// Toast.makeText(this, strBuf.toString(),
					// Toast.LENGTH_SHORT).show();
					if (!replacechar.equalsIgnoreCase("")) {
						retrieveString = webServiceForMark("("
								+ replacechar.substring(0,
										replacechar.length() - 2));
					}

					// Toast.makeText(this, retrieveString,
					// Toast.LENGTH_SHORT)
					// .show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					if (!FileLog.logInfo(
							"Exception with service call --- " + e.toString(),
							0))
						;
				}

				if (retrieveString != null) {
					Log.v("retrieveString", "retrieveString "+retrieveString);
					if (!retrieveString.equalsIgnoreCase("error")) {
						try {

							String updateSql = retrieveString;
							Cursor cursorRecordsForUpdation = helper
									.updateRecordsUsingRawQuery(updateSql);
							if (cursorRecordsForUpdation == null) {

								if (!FileLog
										.logInfo(
												"Updation of record failed in TableMarks - ",
												0))
									;
							}

							DataBaseUtility
									.closeCursor(cursorRecordsForUpdation);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

							if (!FileLog.logInfo("Exception in TableMarks - "
									+ e.toString(), 0))
								;
						}
					} else {

						if (!FileLog.logInfo(
								"WSError - checkServerNewMarkUpdation --- "
										+ retrieveString.toString(), 0))
							;
					}

				}

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			if (!FileLog.logInfo(
					"SQLException in TableMarks ---- " + e.toString(), 0))
				;
		}

	}

	public String webServiceForBundleHistory(final String pXmlString) {

		final String SOAP_ACTION = "SmartEvalService/InsertBundleHistoryStatusFromXML";
		final String METHOD_NAME = "InsertBundleHistoryStatusFromXML";
		final String NAMESPACE = "SmartEvalService";
		final String URL = HostString + "/EvalWebService/EvalService.asmx";

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLBundleStatus", pXmlString);
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
			Log.v("retrieveString", "retrieveStringstr "+str);
		}catch (SocketTimeoutException e) {
			Log.v("SocketTimeoutException", "SocketTimeoutException");
			e.printStackTrace();
			if (!FileLog.logInfo(
					"Exception with webServiceForBundleHistory call - "
							+ e.toString(), 0))
				;
		}
		catch (Exception e) {
			Log.v("Exception", "Exception");
			e.printStackTrace();

			if (!FileLog.logInfo(
					"Exception with webServiceForBundleHistory call - "
							+ e.toString(), 0))
				;
		}

		return strcheck;
	}

	public String webServiceForMarkHistory(final String pXmlString) {

		final String SOAP_ACTION = "SmartEvalService/InsertMarksHistoryFromXML_NR";
		// final String METHOD_NAME = "InsertMarksHistoryFromXML";
		final String METHOD_NAME = "InsertMarksHistoryFromXML_NR";
		final String NAMESPACE = "SmartEvalService";
		final String URL = HostString + "/EvalWebService/EvalService.asmx";

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLMarks", pXmlString);
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
			Log.v("retrieveString", "retrieveStringssss "+str);
		}

		catch (Exception e) {

			e.printStackTrace();

			if (!FileLog.logInfo(
					"Exception with webServiceForMarkHistory call - "
							+ e.toString(), 0))
				;
		}

		return strcheck;
	}

	public String webServiceForBundle(final String pXmlString) {

		final String SOAP_ACTION = "SmartEvalService/InsertBundleStatusFromXML";
		final String METHOD_NAME = "InsertBundleStatusFromXML";
		final String NAMESPACE = "SmartEvalService";
		final String URL = HostString + "/EvalWebService/EvalService.asmx";

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLBundleStatus", pXmlString);
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

			if (!FileLog.logInfo("Exception with webServiceForBundle call - "
					+ e.toString(), 0))
				;
		}

		return strcheck;
	}

	public String webServiceForMark(final String pXmlString) {

		final String SOAP_ACTION = "SmartEvalService/InsertMarksFromXML_NR";
		// final String METHOD_NAME = "InsertMarksFromXML";
		final String METHOD_NAME = "InsertMarksFromXML_NR";
		final String NAMESPACE = "SmartEvalService";
		final String URL = HostString + "/EvalWebService/EvalService.asmx";

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLMarks", pXmlString);
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
			Log.v("retrieveString", "retrieveStringssttrr "+str);
		}catch (SocketTimeoutException e) {
			Log.v("SocketTimeoutException", "SocketTimeoutException");
			e.printStackTrace();
			if (!FileLog.logInfo(
					"Exception with webServiceForBundleHistory call - "
							+ e.toString(), 0))
				;
		}
		catch (Exception e) {
			e.printStackTrace();
			if (!FileLog.logInfo(
					"Exception with webServiceForMark call - " + e.toString(),
					0));
		}

		return strcheck;
	}
}
