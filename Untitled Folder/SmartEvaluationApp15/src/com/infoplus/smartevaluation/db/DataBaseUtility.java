package com.infoplus.smartevaluation.db;

import java.io.File;

import com.infoplus.smartevaluation.log.FileLog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class DataBaseUtility {

	// Database Related Constants
	public static int DATABASE_VERSION = 1;
	public static String DATABASE_FOLDER = "SmartEvaluation";
	public static String EVALUATION_DATABASE_NAME = "Sevaluation.db";
	public static String LOG_FILE="evalLog.log";
	// DataBasePath
	public static File EVALUATION_DATABASE_FILE_PATH = new File(
			Environment.getExternalStoragePublicDirectory(DATABASE_FOLDER),
			EVALUATION_DATABASE_NAME);

	// Writable DB Object
	public static SQLiteDatabase getWritableDBForEvaluation() {
		SQLiteDatabase database = null;
		try {
			database = SQLiteDatabase.openDatabase(
					DataBaseUtility.EVALUATION_DATABASE_FILE_PATH.toString(),
					null, SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {

			// Write to Log file
			 FileLog.logInfo("Exception ---> DataBaseUtility: getWritableDBForEvaluation() " + e.toString(), 0);
				
		}
		return database;
	}

	// Read Only DB Object
	public static SQLiteDatabase getReadableDBForEvaluation() {
		SQLiteDatabase database = null;
		try {
			database = SQLiteDatabase.openDatabase(
					DataBaseUtility.EVALUATION_DATABASE_FILE_PATH.toString(),
					null, SQLiteDatabase.OPEN_READONLY);
		} catch (Exception e) {
			// Write to Log file
			 FileLog.logInfo("Exception ---> DataBaseUtility: getReadableDBForEvaluation() " + e.toString(), 0);
		}
		return database;
	}

	public static void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}
}
