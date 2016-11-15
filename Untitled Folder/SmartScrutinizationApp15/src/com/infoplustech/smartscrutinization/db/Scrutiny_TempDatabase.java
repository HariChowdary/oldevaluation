package com.infoplustech.smartscrutinization.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Utility;

public class Scrutiny_TempDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "remarksdialog.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "table_remarks_dialog";
	public static final String _SNo = "_id";
	private static final String bundle = "bundle_no";
	private static final String subject = "subject_code";
	private static final String grand_total = "grand_total";
	Utility instanceUtility;

	public Scrutiny_TempDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "CREATE TABLE if not exists " + TABLE_NAME + " (" + _SNo
				+ "   text, " + subject + "  text, " + bundle + "   text, "
				+ SSConstants.M1A_REMARK + "  text, " + SSConstants.M1B_REMARK
				+ "  text, " + SSConstants.M1C_REMARK + "  text, "
				+ SSConstants.M1D_REMARK + "  text, " + SSConstants.M1E_REMARK
				+ "  text, " + SSConstants.M1F_REMARK + "  text, "
				+ SSConstants.M1G_REMARK + "  text, " + SSConstants.M1H_REMARK
				+ "  text, " + SSConstants.M1I_REMARK + "  text, "
				+ SSConstants.M1J_REMARK + "  text, " + SSConstants.M2A_REMARK

				+ "  text, " + SSConstants.M2B_REMARK + "  text, "
				+ SSConstants.M2C_REMARK + "  text, " + SSConstants.M2D_REMARK
				+ "  text, " + SSConstants.M2E_REMARK + "  text, "

				+ SSConstants.M3A_REMARK + "  text, " + SSConstants.M3B_REMARK
				+ "  text, " + SSConstants.M3C_REMARK + "  text, "
				+ SSConstants.M3D_REMARK + "  text, " + SSConstants.M3E_REMARK

				+ "  text, " + SSConstants.M4A_REMARK + "  text, "
				+ SSConstants.M4B_REMARK + "  text, " + SSConstants.M4C_REMARK
				+ "  text, " + SSConstants.M4D_REMARK + "  text, "
				+ SSConstants.M4E_REMARK + "  text, " + SSConstants.M5A_REMARK

				+ "  text, " + SSConstants.M5B_REMARK + "  text, "
				+ SSConstants.M5C_REMARK + "  text, " + SSConstants.M5D_REMARK
				+ "  text, " + SSConstants.M5E_REMARK + "  text, "

				+ SSConstants.M6A_REMARK + "  text, " + SSConstants.M6B_REMARK
				+ "  text, " + SSConstants.M6C_REMARK + "  text, "
				+ SSConstants.M6D_REMARK + "  text, " + SSConstants.M6E_REMARK

				+ "  text, " + SSConstants.M7A_REMARK + "  text, "
				+ SSConstants.M7B_REMARK + "  text, " + SSConstants.M7C_REMARK
				+ "  text, " + SSConstants.M7D_REMARK + "  text, "

				+ SSConstants.M7E_REMARK + "  text, " + SSConstants.M8A_REMARK
				+ "  text, " + SSConstants.M8B_REMARK + "  text, "
				+ SSConstants.M8C_REMARK + "  text, " + SSConstants.M8D_REMARK

				+ "  text, " + SSConstants.M8E_REMARK + "  text, "
				+ SSConstants.M9A_REMARK + "  text, " + SSConstants.M9B_REMARK
				+ "  text, " + SSConstants.M9C_REMARK + "  text, "

				+ SSConstants.M10A_REMARK + "  text, "
				+ SSConstants.M10B_REMARK + "  text, "
				+ SSConstants.M10C_REMARK + "  text, "
				+ SSConstants.M11A_REMARK + "  text, "
				+ SSConstants.M11B_REMARK + "  text, "
				+ SSConstants.M11C_REMARK + "  text, "

				+ SSConstants.R1_REMARK + "  text, " + SSConstants.R2_REMARK
				+ "  text, " + SSConstants.R3_REMARK + "  text, "
				+ SSConstants.R4_REMARK + "  text, " + SSConstants.R5_REMARK
				+ "  text, " + SSConstants.R6_REMARK + "  text, "
				+ SSConstants.R7_REMARK + "  text, " + SSConstants.R8_REMARK
				+ "  text, " + SSConstants.R9_REMARK + "  text, "
				+ SSConstants.R10_REMARK + "  text, " + SSConstants.R11_REMARK
				+ "  text, " + grand_total + "  text " + ");";
		db.execSQL(sql);

	}

	@Override
	public synchronized void close() {
		super.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public long insertRow(ContentValues values) {
		long insert;
		SQLiteDatabase _tempDatabase = this.getWritableDatabase();
		insert = _tempDatabase.insert(TABLE_NAME, null, values);
		_tempDatabase.close();
		return insert;
	}

	public Cursor getRow(String where) {
		Cursor _cursor = null;
		SQLiteDatabase _tempDatabase = this.getWritableDatabase();
		_cursor = _tempDatabase.query(TABLE_NAME, null, where, null, null,
				null, null);
		_cursor.moveToFirst();
		_tempDatabase.close();
		return _cursor;
	}

	public void updateRow(ContentValues values) {
		try {
			SQLiteDatabase _tempDatabase = this.getWritableDatabase();
			_tempDatabase.update(TABLE_NAME, values, _SNo + " = '1'", null);
			_tempDatabase.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public void deleteRow() {
		try {
			SQLiteDatabase _tempDatabase = this.getWritableDatabase();
			_tempDatabase.delete(TABLE_NAME, _SNo + " = '1'", null);
			_tempDatabase.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

}