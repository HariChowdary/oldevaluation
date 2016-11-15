package com.infoplus.smartevaluation.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

public class FileLog {

//	String folderName="SmartEvaluation";
//	String evalLog = "evalLog.log";
	
	public static boolean logInfo (String msg, int type){

		File myFile = new File(
				Environment.getExternalStoragePublicDirectory("SmartEvaluation"),
				"evalLog.log");
		try {
				if (!myFile.exists()){
					myFile.createNewFile();
				}
				
				FileOutputStream fout = new FileOutputStream(myFile, true);
				OutputStreamWriter osw = new OutputStreamWriter(fout);
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
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