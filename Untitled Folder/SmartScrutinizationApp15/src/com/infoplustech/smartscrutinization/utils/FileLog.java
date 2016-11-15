package com.infoplustech.smartscrutinization.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class FileLog {

	static String folderName="SmartEvaluation";
	static String serviceLog = "scrutinyLog.log";
	
	public static boolean logInfo (String msg, int type){

		File myFile = new File(
				Environment.getExternalStoragePublicDirectory(folderName),
				serviceLog);
		try {
				if (!myFile.exists()){
					myFile.createNewFile();
				}
				
				FileOutputStream fout = new FileOutputStream(myFile, true);
				OutputStreamWriter osw = new OutputStreamWriter(fout);
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
				osw.write(sdf.format(new Date())+ " : " + msg+"\n");
				
				osw.flush();
				osw.close();
				return true;
				
		} catch (Exception e) {
			//Show a message for not able to write to log
			return false;
		} 
	}
}   