package com.infoplustech.smartscrutinization.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.infoplustech.smartscrutinization.utils.FileLog;
import com.infoplustech.smartscrutinization.utils.SSConstants;
import com.infoplustech.smartscrutinization.utils.Scrutiny_DataBaseUtility;

public class SScrutinyDatabase extends SQLiteOpenHelper {

	static Context mContext;


	private static SScrutinyDatabase scrutinyDatabase;
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Sscrutinization";

	public SScrutinyDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	public static SScrutinyDatabase getInstance(Context context) {
		mContext = context;
		return scrutinyDatabase == null ? new SScrutinyDatabase(context)
				: scrutinyDatabase;

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	// method for saving values to DB
	public Long saveDataToDB(String table, ContentValues values) {
		long _rowId = 0;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			if(table.equals(SSConstants.TABLE_EVALUATION_SAVE)){
			_rowId = _database.insert(SSConstants.TABLE_EVALUATION_SAVE, null,
					values);
			}else{
				_rowId = _database.insert(SSConstants.TABLE_SCRUTINY_SAVE, null,
						values);
			}
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Save to DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return _rowId;
	}

	// method for updating row in a table
	public int updateRow(String table, ContentValues values, String where) {
		int count = 0;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			if(table.equals(SSConstants.TABLE_EVALUATION_SAVE)){
				count = _database.update(SSConstants.TABLE_EVALUATION_SAVE, values,
						where, null);
			}else{
			count = _database.update(SSConstants.TABLE_SCRUTINY_SAVE, values,
					where, null);
			}
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Update to DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return count;
	}

	// method for updating row in a table
	public int updateRow2(String table, ContentValues values, String where) {
		int count = 0;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			count = _database.update(table, values, where, null);
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Update to DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return count;
	}

	// method for retrieving row from table
	public Cursor getRow(String answerSheetBarcode, String[] selectedCols) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			_cursor = _database.query(SSConstants.TABLE_SCRUTINY_SAVE,
					selectedCols, SSConstants.ANS_BOOK_BARCODE + " = '"
							+ answerSheetBarcode + "'", null, null, null, null);
			_cursor.moveToFirst();
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Retrieve Data from DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return _cursor;
	}

	// method for retrieving row from table
	public Cursor getRowBarcodeStatusBySerialNo(String bundleSerialNo,
			String bundleNo, String[] columns) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			_cursor = _database.query(SSConstants.TABLE_SCRUTINY_SAVE, columns,
					SSConstants.BUNDLE_SERIAL_NO + " = '" + bundleSerialNo
							+ "' AND " + SSConstants.BUNDLE_NO + " = '"
							+ bundleNo + "' AND " + SSConstants.BARCODE_STATUS
							+ " = '2'", null, null, null, null);
			_cursor.moveToFirst();
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Retrieve Data from DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return _cursor;
	}

	// method for retrieving row from table
	public Cursor getRowFromTable_Marks(String answerSheetBarcode,
			String[] selectedCols) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			_cursor = _database.query(SSConstants.TABLE_SCRUTINY_REQUEST,
					selectedCols, SSConstants.ANS_BOOK_BARCODE + " = '"
							+ answerSheetBarcode + "'", null, null, null, null);
			_cursor.moveToFirst();
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Retrieve Data from DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return _cursor;
	}

	// method for deleting a row from a table
	public int deleteRow(String table, String where) {
		int del = 0;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			del = _database.delete(table, where, null);
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Delete Data from DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return del;
	}

	// delete bundle
	public int deleteBundle(String table, String bundle) {
		int del = 0;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			del = _database.delete(table, SSConstants.BUNDLE_NO + " = '"
					+ bundle + "'", null);
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Delete Data from DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return del;
	}

	// get bundle
	public Cursor getBundle(String bundleNo) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			_cursor = _database.query(SSConstants.TABLE_SCRUTINY_REQUEST, null,
					SSConstants.BUNDLE_NO + " = '" + bundleNo + "'", null,
					null, null, null);
			_cursor.moveToFirst();
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Retrieve Data from DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return _cursor;
	}

	public Cursor passedQuery(String table, String where, String orderBy) {
		Cursor _cursor = null;
		try {
  			   SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			_cursor = _database.query(table, null, where, null, null, null,
					orderBy);
			_cursor.moveToFirst();
			_database.close();
		} catch (SQLiteException sqle) {
			Toast.makeText(mContext,
					"Cannot Execute Data from DB \n " + sqle.getMessage(),
					Toast.LENGTH_LONG).show();
		}
		return _cursor;    
	}   

	public Cursor executeSQLQuery(String sqlQuery, String[] selectionArgs) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = SQLiteDatabase.openDatabase(
					SSConstants.SSCRUTINY_DB_PATH, null,
					SQLiteDatabase.OPEN_READWRITE);
			_cursor = _database.rawQuery(sqlQuery, selectionArgs);
			_cursor.moveToFirst();
			_database.close();
		} catch (SQLiteException sqle) {

		}

		return _cursor;
	}

	// /========================================
	// DB methods for onetime service

	public Cursor deleteRecords(String pSqlStatement) {
		Cursor _cursor = null;
		try {

			SQLiteDatabase _database = Scrutiny_DataBaseUtility
					.getWritableDBForScrutini();

			try {
				_cursor = _database.rawQuery(pSqlStatement, null);
				if (_cursor != null) {
					_cursor.moveToFirst();
				} else {
					FileLog fl = new FileLog();
					if (!FileLog.logInfo(
							"Cursor is empty - deleteRecords() in DBHelperScrutiny",
							0))
						;
				}
			} catch (Exception e) {
				FileLog fl = new FileLog();
				if (!FileLog.logInfo("Exception - deleteRecords" + e.toString(), 0))
					;
			} finally {
				_database.close();
			}
		} catch (SQLiteException sqle) {
			FileLog fl = new FileLog();
			if (!FileLog.logInfo("Exception - deleteRecords() in DBHelperScrutiny"
					+ sqle.toString(), 0))
				;
		}

		return _cursor;
	}

	// method for retrieving the values with rawQuery from Scrutinization
	public Cursor getRecordsUsingRawQuery(String pSqlStatement) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = Scrutiny_DataBaseUtility
					.getReadableDBForScrutini();
			try {
				_cursor = _database.rawQuery(pSqlStatement, null);
				if (_cursor != null) {
					_cursor.moveToFirst();
				} else {
					FileLog fl = new FileLog();
					if (!FileLog.logInfo(
							"Cursor is empty - getRecordsUsingRawQuery() in DBHelperScrutiny",
							0))
						;
				}
			} catch (Exception e) {
				FileLog fl = new FileLog();
				if (!FileLog.logInfo(
						"Exception - getRecordsUsingRawQuery - " + e.toString(),
						0))
					;
			} finally {
				_database.close();
			}

		} catch (SQLiteException sqle) {
			FileLog fl = new FileLog();
			if (!FileLog.logInfo(
					"Exception - getRecordsUsingRawQuery() in DBHelperScrutiny"
							+ sqle.toString(), 0))
				;
		}

		return _cursor;
	}
	public int deleteMore(String sql) {
		int del = 0;
		try {
			SQLiteDatabase _database = Scrutiny_DataBaseUtility
					.getWritableDBForEvaluation();
			try {
				 _database.execSQL(sql);
			} catch (SQLiteException sqle) {
				FileLog.logInfo(
						"SQLiteException ---> Class Name: dbHelper, Method Name : deleteRow"
								+ "\n" + sqle.getMessage(), 0);
			} finally {
				_database.close();
			}

		} catch (Exception e) {
			FileLog.logInfo(
					"Exception ---> Class Name: dbHelper, Method Name : deleteRow"
							+ "\n" + e.getMessage(), 0);
		}
		return del;
	}
	// ===================================================

	@Override
	public void onCreate(SQLiteDatabase arg0) {
	}


}
