package com.infoplus.smartevaluation.db;

import com.infoplus.smartevaluation.SEConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TempDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "MarksDialog.db";
	private static final int DATABASE_VERSION = 1;

	private static final String grand_total = "grand_total";

	public TempDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// SQLiteDatabase db = SQLiteDatabase.
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "CREATE TABLE " + SEConstants.TEMP_TABLE_NAME + " ("
				+ SEConstants.bundle_SNo + " text, " + SEConstants.subject
				+ "text, " + SEConstants.bundle + " text, "
				+ SEConstants.mark1a + "text, " + SEConstants.mark1b + "text, "
				+ SEConstants.mark1c + "text, " + SEConstants.mark1d + "text, "
				+ SEConstants.mark1e + "text, " + SEConstants.mark2a + "text, "
				+ SEConstants.mark2b + "text, " + SEConstants.mark2c + "text, "
				+ SEConstants.mark2d + "text, " + SEConstants.mark2e + "text, "
				+ SEConstants.mark3a + "text, " + SEConstants.mark3b + "text, "
				+ SEConstants.mark3c + "text, " + SEConstants.mark3d + "text, "
				+ SEConstants.mark3e + "text, " + SEConstants.mark4a + "text, "
				+ SEConstants.mark4b + "text, " + SEConstants.mark4c + "text, "
				+ SEConstants.mark4d + "text, " + SEConstants.mark4e + "text, "
				+ SEConstants.mark5a + "text, " + SEConstants.mark5b + "text, "
				+ SEConstants.mark5c + "text, " + SEConstants.mark5d + "text, "
				+ SEConstants.mark5e + "text, " + SEConstants.mark6a + "text, "
				+ SEConstants.mark6b + "text, " + SEConstants.mark6c + "text, "
				+ SEConstants.mark6d + "text, " + SEConstants.mark6e + "text, "
				+ SEConstants.mark7a + "text, " + SEConstants.mark7b + "text, "
				+ SEConstants.mark7c + "text, " + SEConstants.mark7d + "text, "
				+ SEConstants.mark7e + "text, " + SEConstants.mark8a + "text, "
				+ SEConstants.mark8b + "text, " + SEConstants.mark8c + "text, "
				+ SEConstants.mark8d + "text, " + SEConstants.mark8e + "text, "
				+ SEConstants.r1_total + "text, " + SEConstants.r2_total
				+ "text, " + SEConstants.r3_total + "text, "
				+ SEConstants.r4_total + "text, " + SEConstants.r5_total
				+ "text, " + SEConstants.r6_total + "text, "
				+ SEConstants.r7_total + "text, " + SEConstants.r8_total
				+ "text, " + grand_total + "text " + ");";
		db.execSQL(sql);

	}

	/*
	 * @Override public synchronized void close() {
	 * 
	 * super.close(); }
	 */

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}