package com.infoplus.smartevaluation;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.infoplus.smartevaluation.db.DBHelper;
import com.infoplus.smartevaluation.db.DataBaseUtility;
import com.infoplus.smartevaluation.log.FileLog;

public class Utility {

	// get Ip address
	public static String getIPConfiguration() {
		String IPString = null;
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

			// Folder name and file name must go into constants list
			Document doc = docBuilder.parse(new File(Environment
					.getExternalStoragePublicDirectory("SmartEvaluation"),
					"SmartConfig.xml"));

			// Document doc = docBuilder.parse(new
			// File("file:///android_asset/SmartConfig.xml"));

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList smartConfigNodeList = doc
					.getElementsByTagName("SmartConfig");
			// int smart = smartConfigNodeList.getLength();

			for (int s = 0; s < smartConfigNodeList.getLength(); s++) {

				Node firstNode = smartConfigNodeList.item(s);
				if (firstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element firstElement = (Element) firstNode;

					// -------
					NodeList firstNameList = firstElement
							.getElementsByTagName("IPConfig");
					Element firstNameElement = (Element) firstNameList.item(0);

					NodeList textFNList = firstNameElement.getChildNodes();

					IPString = textFNList.item(0).getNodeValue().trim()
							.toString();

				}// end of if clause

			}// end of for loop with s var

		} catch (SAXParseException err) {

			FileLog fl = new FileLog();
			FileLog.logInfo(
					"SAXParseException - ipconfig_xml " + err.toString(), 0);

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace(); // IPString="error";

			FileLog.logInfo("SAXException - ipconfig_xml " + e.toString(), 0);
			;
		} catch (Throwable t) {
			t.printStackTrace();
			// IPString="error";
		}
		return IPString;
	}

	// check network availabilty
	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context

		.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();

		// return connectivityManager.getActiveNetworkInfo().isConnected();

		return activeNetworkInfo != null;
	}

	public boolean pingTest(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			try {
				URL url = new URL(getIPConfiguration()); // server ip
				HttpURLConnection urlc = (HttpURLConnection) url
						.openConnection();
				urlc.setConnectTimeout(10 * 1000); // 10 s.
				urlc.connect();
				if (urlc.getResponseCode() == 200) { // 200 = "OK" code (http
														// connection is fine).
					FileLog.logInfo("Connection-Success", 0);
					return true;
				} else {
					FileLog.logInfo("Connection-failure", 0);
					return false;
				}
			} catch (MalformedURLException e1) {
				FileLog.logInfo("Connection-failure", 0);
				return false;
			} catch (IOException e) {
				FileLog.logInfo("Connection-failure", 0);
				return false;
			}
		}
		return false;
	}

	// public static boolean isRegulation_R13_Mtech(Context context) {
	//
	// DBHelper _database = DBHelper.getInstance(context);
	// Cursor _cursor = _database.getRow(
	// SEConstants.TABLE_DATE_CONFIGURATION, "id=1", null);
	// String _reg = _cursor.getString(_cursor
	// .getColumnIndex(SEConstants.REGULATION));
	// DataBaseUtility.closeCursor(_cursor);
	// return _reg.equalsIgnoreCase("R13") ? true : false;
	// }

	// saving total scripts count in preferences
	// public static void saveTotalScriptsIn_SharedPref(Context context,
	// String totalScripts) {
	// SharedPreferences preferences = context.getSharedPreferences(
	// RC_Constants.SHARED_PREF_TOTAL_SCRIPTS,
	// context.MODE_WORLD_READABLE);
	// Editor editor = preferences.edit();
	// editor.putString(RC_Constants.SHARED_PREF_TOTAL_SCRIPTS, totalScripts);
	// editor.commit();
	// }
	//
	// public static String getTotalScriptsFrom_SharedPref(Context context) {
	// SharedPreferences preferences = context.getSharedPreferences(
	// RC_Constants.SHARED_PREF_TOTAL_SCRIPTS,
	// context.MODE_WORLD_READABLE);
	// return preferences.getString(RC_Constants.SHARED_PREF_TOTAL_SCRIPTS,
	// "40");
	// }

	public static String getTabletIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	public static String getDateDifference(Date thenDate, int timeInterval,
			Context context) {

		try {
			Calendar now = Calendar.getInstance();
			Calendar then = Calendar.getInstance();
			now.setTime(new Date());
			then.setTime(thenDate);

			// Get the represented date in milliseconds
			long nowMs = now.getTimeInMillis();
			long thenMs = then.getTimeInMillis();

			// Calculate difference in milliseconds
			long diff = nowMs - thenMs;

			// Calculate difference in seconds
			long seconds = (int) ((diff / 1000));

			if (seconds >= timeInterval) {
				return String.valueOf(seconds);
			}

			else {
				return String.valueOf(seconds);
			}
		} catch (Exception ee) {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.alert_time_zone_wrong), Toast.LENGTH_LONG)
					.show();
		}
		return null;

	}

	public static boolean is_subject_code_special_case(String subjectCode) {
		if (subjectCode
				.equalsIgnoreCase(SEConstants.SUBJ_58002_DESIGN_AND_DRAWING_OF_IRRIGATION_STRUCTURES_1)
				|| subjectCode
						.equalsIgnoreCase(SEConstants.SUBJ_K0129_DESIGN_AND_DRAWING_OF_HYDRAULIC_STRUCTURES_2)
				|| subjectCode
						.equalsIgnoreCase(SEConstants.SUBJ_54017_MACHINE_DRAWING_3)
				|| subjectCode
						.equalsIgnoreCase(SEConstants.SUBJ_54065_MACHINE_DRAWING_AND_COMPUTER_AIDED_GRAPHICS_4)
				|| subjectCode
						.equalsIgnoreCase(SEConstants.SUBJ_T0121_BUILDING_PLANNING_AND_DRAWING_5)
				|| subjectCode
						.equalsIgnoreCase(SEConstants.SUBJ_X0305_MACHINE_DRAWING_6)
				|| subjectCode
						.equalsIgnoreCase(SEConstants.SUBJ_V0323_MACHINE_DRAWING_7)
				|| subjectCode
						.equalsIgnoreCase(SEConstants.SUBJ_Q0123_ENGINEERING_DRAWING)) {
			return true;
		} else {
			return false;
		}

	}

	// get present time
	public static String getPresentTime() {
		// set the date format here
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public boolean isRegulation_R13_Mtech(Context context) {
		// TODO Auto-generated method stub
		boolean _flag;
		DBHelper _database = DBHelper.getInstance(context);
		Cursor _cursor = _database.getRow(SEConstants.TABLE_DATE_CONFIGURATION,
				"id=1", null);
		String _reg = null;

		_reg = _cursor
				.getString(_cursor.getColumnIndex(SEConstants.REGULATION));
		DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("R13-M.Tech")
				|| _reg.equalsIgnoreCase("R13-M.Pharm")
				|| _reg.equalsIgnoreCase("R13-MBA")
				|| _reg.equalsIgnoreCase("R13-MCA")) {
			_flag = true;
		} else {
			_flag = false;
		}

		return _flag;
	}
	
	
	public boolean isRegulation_R15_Mtech(Context context) {
		// TODO Auto-generated method stub
		boolean _flag;
		DBHelper _database = DBHelper.getInstance(context);
		Cursor _cursor = _database.getRow(SEConstants.TABLE_DATE_CONFIGURATION,
				"id=1", null);
		String _reg = null;

		_reg = _cursor
				.getString(_cursor.getColumnIndex(SEConstants.REGULATION));
		DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("R15-M.Tech")
				|| _reg.equalsIgnoreCase("R15-M.Pharm")
				|| _reg.equalsIgnoreCase("R15-MBA")
				|| _reg.equalsIgnoreCase("R15-MCA")) {
			_flag = true;
		} else {
			_flag = false;
		}

		return _flag;
	}
	public boolean isRegulation_R13_Btech(Context context) {
		// TODO Auto-generated method stub
		boolean _flag;
		DBHelper _database = DBHelper.getInstance(context);
		Cursor _cursor = _database.getRow(SEConstants.TABLE_DATE_CONFIGURATION,
				"id=1", null);
		String _reg = null;

		_reg = _cursor
				.getString(_cursor.getColumnIndex(SEConstants.REGULATION));
		DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("R13-B.Tech")
				|| _reg.equalsIgnoreCase("R13-B.Pharm")
				|| _reg.equalsIgnoreCase("R15-B.Tech")
				|| _reg.equalsIgnoreCase("R15-B.Pharm")) {
			_flag = true;
		} else {
			_flag = false;
		}

		return _flag;
	}

	
	// Condition for R09_Btech
	public boolean isRegulation_R09_Course(Context context) {
		// TODO Auto-generated method stub
		boolean _flag;
		DBHelper _database = DBHelper.getInstance(context);
		Cursor _cursor = _database.getRow(SEConstants.TABLE_DATE_CONFIGURATION,
				"id=1", null);
		String _reg = null;

		_reg = _cursor
				.getString(_cursor.getColumnIndex(SEConstants.REGULATION));
		DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("R09-B.Tech")
				|| _reg.equalsIgnoreCase("R09-B.Pharm")

				|| _reg.equalsIgnoreCase("R07-B.Tech")
				|| _reg.equalsIgnoreCase("R07-B.Pharm")

				|| _reg.equalsIgnoreCase("R05-B.Tech")
				|| _reg.equalsIgnoreCase("R05-B.Pharm")

				|| _reg.equalsIgnoreCase("RR-B.Tech")
				|| _reg.equalsIgnoreCase("RR-B.Pharm")

				|| _reg.equalsIgnoreCase("NR-B.Tech")
				|| _reg.equalsIgnoreCase("NR-B.Pharm")
				|| _reg.equalsIgnoreCase("NR-B.Tech-CCC")

		) {
			_flag = true;
		} else {
			_flag = false;
		}

		return _flag;
	}

	// Condition for R09_Btech
	public boolean isRegulation_NR_Course(Context context) {
		// TODO Auto-generated method stub
		boolean _flag;
		DBHelper _database = DBHelper.getInstance(context);
		Cursor _cursor = _database.getRow(SEConstants.TABLE_DATE_CONFIGURATION,
				"id=1", null);
		String _reg = null;

		_reg = _cursor
				.getString(_cursor.getColumnIndex(SEConstants.REGULATION));
		DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("NR-B.Tech-CCC")) {
			_flag = true;
		} else {
			_flag = false;
		}

		return _flag;
	}

	// Condition for R09_Btech
	public boolean isRegulation_R09_MTech_Course(Context context) {
		// TODO Auto-generated method stub
		boolean _flag;
		DBHelper _database = DBHelper.getInstance(context);
		Cursor _cursor = _database.getRow(SEConstants.TABLE_DATE_CONFIGURATION,
				"id=1", null);
		String _reg = null;

		_reg = _cursor
				.getString(_cursor.getColumnIndex(SEConstants.REGULATION));
		DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("R09-M.Tech")
				|| _reg.equalsIgnoreCase("R09-M.Pharm")
				|| _reg.equalsIgnoreCase("R09-MBA")
				|| _reg.equalsIgnoreCase("R09-MCA")

				|| _reg.equalsIgnoreCase("R07-M.Tech")
				|| _reg.equalsIgnoreCase("R07-M.Pharm")
				|| _reg.equalsIgnoreCase("R07-MBA")
				|| _reg.equalsIgnoreCase("R07-MCA")

				|| _reg.equalsIgnoreCase("R05-M.Tech")
				|| _reg.equalsIgnoreCase("R05-M.Pharm")
				|| _reg.equalsIgnoreCase("R05-MBA")
				|| _reg.equalsIgnoreCase("R05-MCA")

				|| _reg.equalsIgnoreCase("RR-M.Tech")
				|| _reg.equalsIgnoreCase("RR-M.Pharm")
				|| _reg.equalsIgnoreCase("RR-MBA")
				|| _reg.equalsIgnoreCase("RR-MCA")

				|| _reg.equalsIgnoreCase("NR-M.Tech")
				|| _reg.equalsIgnoreCase("NR-M.Pharm")
				|| _reg.equalsIgnoreCase("NR-MBA")
				|| _reg.equalsIgnoreCase("NR-MCA")

		) {
			_flag = true;
		} else {
			_flag = false;
		}

		return _flag;
	}

}