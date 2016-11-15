package com.infoplustech.smartscrutinization.utils;

import java.io.File;
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
import android.util.Log;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.R;
import com.infoplustech.smartscrutinization.db.SScrutinyDatabase;

public class Utility {   

	// Condition for R13_Mtech,MBA,M.Pharm
	public boolean isRegulation_R13_Mtech(Context context) {
		boolean flag;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(context);
		Cursor _cursor = _database.passedQuery(
				SSConstants.TABLE_DATE_CONFIGURATION, "id=1", null);
		String _reg = _cursor.getString(_cursor
				.getColumnIndex(SSConstants.REGULATION));
		Scrutiny_DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("R13-M.Tech")
				|| _reg.equalsIgnoreCase("R13-M.Pharm")
				|| _reg.equalsIgnoreCase("R13-MBA")
				|| _reg.equalsIgnoreCase("R13-MCA")) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

	// Condition for R15_Mtech,MBA,M.Pharm
		public boolean isRegulation_R15_Mtech(Context context) {
			boolean flag;
			SScrutinyDatabase _database = SScrutinyDatabase.getInstance(context);
			Cursor _cursor = _database.passedQuery(
					SSConstants.TABLE_DATE_CONFIGURATION, "id=1", null);
			String _reg = _cursor.getString(_cursor
					.getColumnIndex(SSConstants.REGULATION));
			Scrutiny_DataBaseUtility.closeCursor(_cursor);
			if (_reg.equalsIgnoreCase("R15-M.Tech")
					|| _reg.equalsIgnoreCase("R15-M.Pharm")
					|| _reg.equalsIgnoreCase("R15-MBA")
					|| _reg.equalsIgnoreCase("R15-MCA")) {
				flag = true;
			} else {
				flag = false;
			}
			return flag;
		}
	
	// Condition for R13_Btech
	public boolean isRegulation_R13_Btech(Context context) {
		// TODO Auto-generated method stub
		boolean _flag;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(context);
		Cursor _cursor = _database.passedQuery(
				SSConstants.TABLE_DATE_CONFIGURATION, "id=1", null);
		String _reg = null;

		_reg = _cursor
				.getString(_cursor.getColumnIndex(SSConstants.REGULATION));
		Scrutiny_DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("R13-B.Tech")
				|| _reg.equalsIgnoreCase("R13-B.Pharm")) {
			_flag = true;
		} else {  
			_flag = false;
		}

		return _flag;
	}
  
	// Condition for R15_Btech
		public boolean isRegulation_R15_Btech(Context context) {
			// TODO Auto-generated method stub
			boolean _flag;
			SScrutinyDatabase _database = SScrutinyDatabase.getInstance(context);
			Cursor _cursor = _database.passedQuery(
					SSConstants.TABLE_DATE_CONFIGURATION, "id=1", null);
			String _reg = null;

			_reg = _cursor
					.getString(_cursor.getColumnIndex(SSConstants.REGULATION));
			Scrutiny_DataBaseUtility.closeCursor(_cursor);
			if (_reg.equalsIgnoreCase("R15-B.Tech")
					|| _reg.equalsIgnoreCase("R15-B.Pharm")) {
				_flag = true;
			} else {  
				_flag = false;
			}

			return _flag;
		}
	
	
	// Condition for R09,R07,R05,RR_Btech
	public boolean isRegulation_R09_Course(Context context) {
		// TODO Auto-generated method stub
		boolean _flag;
		SScrutinyDatabase _database = SScrutinyDatabase.getInstance(context);
		Cursor _cursor = _database.passedQuery(
				SSConstants.TABLE_DATE_CONFIGURATION, "id=1", null);
		String _reg = null;

		_reg = _cursor
				.getString(_cursor.getColumnIndex(SSConstants.REGULATION));
		Scrutiny_DataBaseUtility.closeCursor(_cursor);
		if (_reg.equalsIgnoreCase("R09-M.Tech")
				|| _reg.equalsIgnoreCase("R09-M.Pharm")
				|| _reg.equalsIgnoreCase("R09-MBA")
				|| _reg.equalsIgnoreCase("R09-MCA")
				|| _reg.equalsIgnoreCase("R09-B.Tech")
				|| _reg.equalsIgnoreCase("R09-B.Pharm")

				|| _reg.equalsIgnoreCase("R07-M.Tech")
				|| _reg.equalsIgnoreCase("R07-M.Pharm")
				|| _reg.equalsIgnoreCase("R07-MBA")
				|| _reg.equalsIgnoreCase("R07-MCA")
				|| _reg.equalsIgnoreCase("R07-B.Tech")
				|| _reg.equalsIgnoreCase("R07-B.Pharm")

				|| _reg.equalsIgnoreCase("R05-M.Tech")
				|| _reg.equalsIgnoreCase("R05-M.Pharm")   
				|| _reg.equalsIgnoreCase("R05-MBA")
				|| _reg.equalsIgnoreCase("R05-MCA")
				|| _reg.equalsIgnoreCase("R05-B.Tech")
				|| _reg.equalsIgnoreCase("R05-B.Pharm")
  
				|| _reg.equalsIgnoreCase("RR-M.Tech")
				|| _reg.equalsIgnoreCase("RR-M.Pharm")
				|| _reg.equalsIgnoreCase("RR-MBA")     
				|| _reg.equalsIgnoreCase("RR-MCA")
				|| _reg.equalsIgnoreCase("RR-B.Tech")
				|| _reg.equalsIgnoreCase("RR-B.Pharm")
				
				|| _reg.equalsIgnoreCase("NR-M.Tech")
				|| _reg.equalsIgnoreCase("NR-M.Pharm")
				|| _reg.equalsIgnoreCase("NR-MBA")   
				|| _reg.equalsIgnoreCase("NR-MCA")
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
	
	// Condition for R09,R07,R05,RR_Btech
		public boolean isRegulation_R09_BTech_Course(Context context) {
			// TODO Auto-generated method stub
			boolean _flag;
			SScrutinyDatabase _database = SScrutinyDatabase.getInstance(context);
			Cursor _cursor = _database.passedQuery(
					SSConstants.TABLE_DATE_CONFIGURATION, "id=1", null);
			String _reg = null;

			_reg = _cursor
					.getString(_cursor.getColumnIndex(SSConstants.REGULATION));
			Scrutiny_DataBaseUtility.closeCursor(_cursor);
			
					if(_reg.equalsIgnoreCase("R09-B.Tech")
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
		public boolean isRegulation_NR_BTech_Course(Context context) {
			// TODO Auto-generated method stub
			boolean _flag;
			SScrutinyDatabase _database = SScrutinyDatabase.getInstance(context);
			Cursor _cursor = _database.passedQuery(
					SSConstants.TABLE_DATE_CONFIGURATION, "id=1", null);
			String _reg = null;

			_reg = _cursor
					.getString(_cursor.getColumnIndex(SSConstants.REGULATION));
			Scrutiny_DataBaseUtility.closeCursor(_cursor);
					if( _reg.equalsIgnoreCase("NR-B.Tech-CCC")
					) {
				_flag = true;
			} else {
				_flag = false;   
			}

			return _flag;
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

	// get IP address from Config file
	public String getIPConfiguration() {
		String IPString = null;
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(Environment
					.getExternalStoragePublicDirectory("SmartEvaluation"),
					"SmartConfig.xml"));

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList smartConfigNodeList = doc
					.getElementsByTagName("SmartConfig");

			for (int s = 0; s < smartConfigNodeList.getLength(); s++) {

				Node firstNode = smartConfigNodeList.item(s);
				if (firstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element firstElement = (Element) firstNode;

					// -------
					NodeList firstNameList = firstElement
							.getElementsByTagName("IPConfig");
					Element firstNameElement = (Element) firstNameList.item(0);

					NodeList textFNList = firstNameElement.getChildNodes();

					Log.i("ipConfig Button", textFNList.item(0)
							.getNodeValue().trim().toString());
					IPString = textFNList.item(0).getNodeValue()
							.trim().toString();

				}// end of if clause

			}// end of for loop with s var
		} catch (SAXParseException err) {
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace(); // IPString="error";
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return IPString;
	}

	// get date and time difference
	public final String getDateDifference(Date thenDate, int timeInterval,
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
			} else {
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

	// check special case subject codes
	public static final boolean is_subject_code_special_case(String subjectCode) {
		if (subjectCode
				.equalsIgnoreCase(SSConstants.SUBJ_58002_DESIGN_AND_DRAWING_OF_IRRIGATION_STRUCTURES_1)
				|| subjectCode
						.equalsIgnoreCase(SSConstants.SUBJ_K0129_DESIGN_AND_DRAWING_OF_HYDRAULIC_STRUCTURES_2)
				|| subjectCode
						.equalsIgnoreCase(SSConstants.SUBJ_54017_MACHINE_DRAWING_3)
				|| subjectCode
						.equalsIgnoreCase(SSConstants.SUBJ_54065_MACHINE_DRAWING_AND_COMPUTER_AIDED_GRAPHICS_4)
				|| subjectCode
						.equalsIgnoreCase(SSConstants.SUBJ_T0121_BUILDING_PLANNING_AND_DRAWING_5)
				|| subjectCode
						.equalsIgnoreCase(SSConstants.SUBJ_X0305_MACHINE_DRAWING_6)
				|| subjectCode
						.equalsIgnoreCase(SSConstants.SUBJ_V0323_MACHINE_DRAWING_7)) {
			return true;
		} else {
			return false;
		}

	}

}