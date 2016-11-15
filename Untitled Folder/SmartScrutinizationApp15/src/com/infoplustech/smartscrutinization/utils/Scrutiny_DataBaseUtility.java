package com.infoplustech.smartscrutinization.utils;

import java.io.File;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class Scrutiny_DataBaseUtility {

	// Database Related Constants
	public static int DATABASE_VERSION = 1;
	public static String DATABASE_FOLDER = "SmartEvaluation";
	public static String EVALUATION_DATABASE_NAME = "Sevaluation.db";
	public static String SCRUTINY_DATABASE_NAME = "Sscrutinization.db";
	public static String bundle = "table_bundle";
	public static String bundle_history = "table_bundle_history";
	public static String marks = "table_marks";
	public static String marks_history = "table_marks_history";

	public static String updated_server = "is_updated_server";

	// DataBasePath
	public static File EVALUATION_DATABASE_FILE_PATH = new File(
			Environment.getExternalStoragePublicDirectory(DATABASE_FOLDER),
			EVALUATION_DATABASE_NAME);

	public static File SCRUTINY_DATABASE_FILE_PATH = new File(
			Environment.getExternalStoragePublicDirectory(DATABASE_FOLDER),
			SCRUTINY_DATABASE_NAME);

	// Writable DB Object
	public static SQLiteDatabase getWritableDBForEvaluation() {
		SQLiteDatabase database = null;
		try {
			database = SQLiteDatabase.openDatabase(
					Scrutiny_DataBaseUtility.EVALUATION_DATABASE_FILE_PATH
							.toString(), null, SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {

			// Write to Log file
			if (!FileLog.logInfo("Exception - " + e.toString(), 0))
				;
		}
		return database;
	}

	// Read Only DB Object
	public static SQLiteDatabase getReadableDBForEvaluation() {
		SQLiteDatabase database = null;
		try {
			database = SQLiteDatabase.openDatabase(
					Scrutiny_DataBaseUtility.EVALUATION_DATABASE_FILE_PATH
							.toString(), null, SQLiteDatabase.OPEN_READONLY);
		} catch (Exception e) {

			// Write to Log file
			if (!FileLog.logInfo("Exception - " + e.toString(), 0))
				;
		}
		return database;
	}

	// Writable DB Object
	public static SQLiteDatabase getWritableDBForScrutini() {
		SQLiteDatabase database = null;
		try {
			database = SQLiteDatabase.openDatabase(
					Scrutiny_DataBaseUtility.SCRUTINY_DATABASE_FILE_PATH
							.toString(), null, SQLiteDatabase.OPEN_READWRITE);
		} catch (Exception e) {

			// Write to Log file
			if (!FileLog.logInfo("Exception - " + e.toString(), 0))
				;
		}
		return database;
	}

	// Read Only DB Object
	public static SQLiteDatabase getReadableDBForScrutini() {
		SQLiteDatabase database = null;
		try {
			database = SQLiteDatabase.openDatabase(
					Scrutiny_DataBaseUtility.SCRUTINY_DATABASE_FILE_PATH
							.toString(), null, SQLiteDatabase.OPEN_READONLY);
		} catch (Exception e) {

			// Write to Log file
			if (!FileLog.logInfo("Exception - " + e.toString(), 0))
				;
		}
		return database;
	}

	public static void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

	// public static void closeDataBase(SQLiteDatabase database) {
	// if (database != null) {
	// database.close();
	// }
	// }
}
