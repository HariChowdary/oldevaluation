package com.infoplus.smartevaluation;

import android.app.IntentService;
import android.content.Intent;

import com.infoplus.smartevaluation.log.FileLog;
import com.infoplus.smartevaluation.webservice.Eval_WebServiceMethods;

public class Settings_IntentService extends IntentService {

	boolean mode2;

	public Settings_IntentService() {
		super("Settings_IntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String _mode = intent.getStringExtra(SEConstants.MODE);

		Eval_WebServiceMethods webServiceMethods = Eval_WebServiceMethods
				.getInstance();
		// if he clicks on evaluation button
		if (_mode.equals(SEConstants.EVALUATION)) {
			Utility instanceUtility = new Utility();
			if (instanceUtility.isNetworkAvailable(this)) {
				// Checking the Bundle History Update
				webServiceMethods.checkServerBundleHistoryUpdation(this);
				// Checking the Bundle Update
				webServiceMethods.checkServerBundleUpdation(this);
				// Checking Mark Update History
				webServiceMethods.checkServerMarkHistoryUpdation(this);
				// Checking NewMark Update
				webServiceMethods.checkServerNewMarkUpdation(this);

				broadCastResultToActivity(_mode);

			} else {
				FileLog.logInfo("Network connection fails ", 0);
			}

		}    

	} 

	// broadcast result
	private void broadCastResultToActivity(String mode) {
		Intent intent = null;
		intent = new Intent(SEConstants.EVALUATION_NOTIFICATION);

		intent.putExtra(SEConstants.MODE, mode);
		sendBroadcast(intent);
	}
}