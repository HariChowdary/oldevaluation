package com.infoplus.smartevaluation.webservice;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.os.Environment;
import android.util.Log;

import com.infoplus.smartevaluation.log.FileLog;

public class WebServiceUtility {

	String FOLDER = "SmartEvaluation";
	String CONFIG_XML = "SmartConfig.xml";
	String HEADER_TAG = "SmartConfig";
	String INNER_TAG = "IPConfig";   

	// ======================================
	// Evaluator entry screen method names
	// ======================================
	// Apk update method name
	String APK_UPDATE_METHOD_NAME = "CheckAPKUpdate";
	// ScriptLimit method name
	String SCRIPT_TIMELIMIT_METHOD_NAME = "GetScriptTimeLimit";
	// RestoreData method name(Evaluator & Subject)
	String RESTORE_DATA_METHOD_NAME = "InsertSubjectuserMasterDetails";
	// Date comparison method name
	String DATE_COMPARISON_METHOD_NAME = "GetCurrentServerDateTime";

	// ======================================
	// Bundle entry screen method name
	// ======================================
	String BUNDLE_VALIDATION_METHOD_NAME = "ValidateUnreadableBundle";

	String NAMESPACE = "SmartEvalService";

	// Soap action for APK_UPDATE
	String SOAP_ACTION_APK_UPDATE = NAMESPACE + "/" + APK_UPDATE_METHOD_NAME;

	// Soap action for SCRIPT_TIMELIMIT
	String SOAP_ACTION_TIME_LIMIT = NAMESPACE + "/"
			+ SCRIPT_TIMELIMIT_METHOD_NAME;

	// Soap action for RESTORE_DATA
	String SOAP_ACTION_RESTORE_DATA = NAMESPACE + "/"
			+ RESTORE_DATA_METHOD_NAME;

	// Soap action for DATE_COMPARISON
	String SOAP_ACTION_DATE_COMPARISON = NAMESPACE + "/"
			+ DATE_COMPARISON_METHOD_NAME;

	// Soap action for BUNDLE_VALIDATION
	String SOAP_ACTION_BUNDLE_VALIDATION = NAMESPACE + "/"
			+ BUNDLE_VALIDATION_METHOD_NAME;

	// ============================================================================================
	// Get ip address from the path sdcard/smartevaluation/SmartConfig.xml
	// ============================================================================================
	public String getIPConfiguration() {
		String IPString = null;
		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(Environment
					.getExternalStoragePublicDirectory(FOLDER), CONFIG_XML));

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList smartConfigNodeList = doc.getElementsByTagName(HEADER_TAG);

			for (int s = 0; s < smartConfigNodeList.getLength(); s++) {

				Node firstNode = smartConfigNodeList.item(s);
				if (firstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element firstElement = (Element) firstNode;

					// -------
					NodeList firstNameList = firstElement
							.getElementsByTagName(INNER_TAG);
					Element firstNameElement = (Element) firstNameList.item(0);

					NodeList textFNList = firstNameElement.getChildNodes();

					IPString = textFNList.item(0).getNodeValue().trim()
							.toString();

				}// end of if clause

			}// end of for loop with s var

		} catch (SAXParseException err) {

		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace(); // IPString="error";

		} catch (Throwable t) {
			t.printStackTrace();
			// IPString="error";
		}
		return IPString;
	}

	// =============================================================================================
	// Functions used in EVALUATOR ENTRY_ACTIVTY//
	// =============================================================================================

	// ============================================================================================
	// Checking whether the APK is upto date or not
	// ============================================================================================
	public String webServiceForAPKUpdate(int pApkType, String pApkVersionName,
			String pDeviceIMEI) {
		String apkResponse = null;
		String SOAP_ADDRESS = getIPConfiguration()
				+ "/EvalWebService/EvalService.asmx";
		try {  
			SoapObject request = new SoapObject(NAMESPACE,
					APK_UPDATE_METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(  
					SoapEnvelope.VER11);
			request.addProperty("APKType", pApkType);
			request.addProperty("APKVersion", pApkVersionName);
			request.addProperty("TabletIMEINo", pDeviceIMEI);
			Log.v("apk ", pApkType+" "+pApkVersionName+" "+pDeviceIMEI);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.setOutputSoapObject(request);      
			envelope.setAddAdornments(false);

			HttpTransportSE ht = new HttpTransportSE(SOAP_ADDRESS);
			ht.debug = true;
			ht.call(SOAP_ACTION_APK_UPDATE, envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();
			apkResponse = response.toString();
		}

		catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> WS_ERROR ---> WebServiceUtility: webServiceForAPKUpdate() - "
							+ ex.toString(), 0);
		}
		return apkResponse;

	}

	// ============================================================================================
	// Updating the script time limit in date_config table
	// ============================================================================================
	public String webServiceForScriptTimeLimit() {
		String scriptTimeLimitResponse = null;
		String SOAP_ADDRESS = getIPConfiguration()
				+ "/EvalWebService/EvalService.asmx";
		try {     
			SoapObject request = new SoapObject(NAMESPACE,
					SCRIPT_TIMELIMIT_METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);
			HttpTransportSE ht = new HttpTransportSE(SOAP_ADDRESS);
			ht.debug = true;
			ht.call(SOAP_ACTION_TIME_LIMIT, envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();
			scriptTimeLimitResponse = response.toString();

		}

		catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> WS_ERROR ---> WebServiceUtility: webServiceForScriptTimeLimit() - "
							+ ex.toString(), 0);
		}
		return scriptTimeLimitResponse;
	}

	// ============================================================================================
	// loading the DB with Evaluator list and Subject list
	// ============================================================================================
	public String webServiceForRestoreDatas(String pXmlString) {
		String restoreDataResponse = null;   
		String SOAP_ADDRESS = getIPConfiguration()  
				+ "/EvalWebService/EvalService.asmx";  
		try {  
			SoapObject request = new SoapObject(NAMESPACE,
					RESTORE_DATA_METHOD_NAME);
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
			HttpTransportSE ht = new HttpTransportSE(SOAP_ADDRESS);  
			ht.debug = true;   
			ht.call(SOAP_ACTION_RESTORE_DATA, envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope   
					.getResponse();
			restoreDataResponse = response.toString();
		}

		catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> WS_ERROR ---> WebServiceUtility: webServiceForRestoreDatas() - "
							+ ex.toString(), 0);

		}
		return restoreDataResponse;
	}

	// ============================================================================================
	// Check tablet date and server date
	// ============================================================================================
	public String webServiceForDateComparison() {
		String DateResponse = null;
		String SOAP_ADDRESS = getIPConfiguration()
				+ "/EvalWebService/EvalService.asmx";
		try {
			SoapObject request = new SoapObject(NAMESPACE,
					DATE_COMPARISON_METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);
			HttpTransportSE ht = new HttpTransportSE(SOAP_ADDRESS);
			ht.debug = true;   
			ht.call(SOAP_ACTION_DATE_COMPARISON, envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();
			DateResponse = response.toString();

		} catch (Exception ex) {              
			FileLog.logInfo(
					"Exception ---> WS_ERROR ---> WebServiceUtility: webServiceForDateComparison() - "
							+ ex.toString(), 0);

		}
		return DateResponse;
	}

	// =============================================================================================
	// Functions used in BUNDLE NUMBER_ACTIVTY//
	// =============================================================================================

	// =============================================================================================
	// Check the Bundle is exists in spot center //
	// =============================================================================================

	public String webServiceForBundle(String pXmlString) {
		String bundleResponse = null;
		String SOAP_ADDRESS = getIPConfiguration()
				+ "/EvalWebService/EvalService.asmx";
		try {
			SoapObject request = new SoapObject(NAMESPACE,
					BUNDLE_VALIDATION_METHOD_NAME);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			request.addProperty("XMLUnreadableBundleNo", pXmlString);
			envelope.dotNet = true;
			envelope.implicitTypes = true;
			envelope.enc = SoapEnvelope.ENC2003;
			envelope.xsd = SoapEnvelope.XSD;
			envelope.xsi = SoapEnvelope.XSI;  
			envelope.setOutputSoapObject(request);
			envelope.setAddAdornments(false);
			HttpTransportSE ht = new HttpTransportSE(SOAP_ADDRESS);
			ht.debug = true;
			ht.call(SOAP_ACTION_BUNDLE_VALIDATION, envelope);
			final SoapPrimitive response = (SoapPrimitive) envelope
					.getResponse();

			bundleResponse = response.toString();
		}      
		catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> WS_ERROR ---> WebServiceUtility: webServiceForBundle() - "
							+ ex.toString(), 0);
		}
		return bundleResponse;
	}
}
