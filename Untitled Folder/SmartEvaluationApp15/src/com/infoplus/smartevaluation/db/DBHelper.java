package com.infoplus.smartevaluation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.infoplus.smartevaluation.SEConstants;
import com.infoplus.smartevaluation.log.FileLog;

public class DBHelper extends SQLiteOpenHelper {

	private static DBHelper dbHelper;

	public DBHelper(Context context) {
		super(context, DataBaseUtility.EVALUATION_DATABASE_NAME, null,
				DataBaseUtility.DATABASE_VERSION);
	}

	public static DBHelper getInstance(Context context) {
		return dbHelper == null ? new DBHelper(context) : dbHelper;
	}

	// method for updating the values with rawQuery from evaluation table
	public synchronized Cursor updateRecordsUsingRawQuery(String pSqlStatement) {
		Cursor _cursor = null;
		try {

			SQLiteDatabase _database = DataBaseUtility
					.getWritableDBForEvaluation();
			try {
				_cursor = _database.rawQuery(pSqlStatement, null);

				if (_cursor != null) {
					_cursor.moveToFirst();
				} else {
					FileLog.logInfo(
							"Empty Cursor - updateRecordsUsingRawQuery() - ", 0);
				}
			} catch (SQLiteException sqle) {
				FileLog.logInfo("Exception - updateRecordsUsingRawQuery() - "
						+ sqle.toString(), 0);
			} finally {
				if (_database != null) {
					_database.close();
				}
			}
		} catch (Exception e) {
			FileLog.logInfo(
					"Exception - updateRecordsUsingRawQuery() - "
							+ e.toString(), 0);
		}
		return _cursor;
	}

	// method for retrieving the values with rawQuery from evaluation table
	public synchronized Cursor getRecordsUsingRawQuery(String pSqlStatement) {
		Cursor _cursor = null;
		try {

			SQLiteDatabase _database = DataBaseUtility
					.getReadableDBForEvaluation();
			try {
				_cursor = _database.rawQuery(pSqlStatement, null);

				if (_cursor != null) {
					_cursor.moveToFirst();
				} else {
					FileLog.logInfo(
							"Cursor is empty - getRecordsUsingRawQuery()", 0);
				}

			} catch (SQLiteException sqle) {
				FileLog.logInfo(
						"Exception - getRecordsUsingRawQuery()"
								+ sqle.toString(), 0);
				;
			} finally {
				// Close the db Object
				if (_database != null) {
					_database.close();
				}
			}

		} catch (Exception e) {
			FileLog.logInfo(
					"Exception - getRecordsUsingRawQuery()" + e.toString(), 0);
		}
		return _cursor;
	}

	// insert
	public long insertReords(String table, ContentValues values) {
		long _rowId = 0;
		try {

			SQLiteDatabase _database = DataBaseUtility
					.getWritableDBForEvaluation();
			try {
				_rowId = _database.insert(table, null, values);

			}

			catch (SQLiteException sqle) {

				FileLog.logInfo(
						"SQLException ---> Class Name: dbHelper, Method Name : insertItem"
								+ "\n" + sqle.getMessage(), 0);
			} finally {
				_database.close();
			}

		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> Class Name: dbHelper, Method Name : insertItem"
							+ "\n" + ex.getMessage(), 0);
		}
		return _rowId;
	}

	// update row
	public int updateRow(String table, ContentValues values, String where) {
		int count = 0;
		try {
			SQLiteDatabase _database = DataBaseUtility
					.getWritableDBForEvaluation();
			try {
				count = _database.update(table, values, where, null);
			}

			catch (SQLiteException sqle) {
				FileLog.logInfo(
						"SQLException ---> Class Name: dbHelper, Method Name : updateBundleStatusByIdInDB"
								+ "\n" + sqle.getMessage(), 0);
			} finally {
				_database.close();
			}
		} catch (Exception ex) {
			FileLog.logInfo(
					"Exception ---> Class Name: dbHelper, Method Name : updateBundleStatusByIdInDB"
							+ "\n" + ex.getMessage(), 0);
		}
		return count;
	}

	// method for retrieving row from table
	public Cursor getRow(String answerSheetBarcode, String[] selectedCols) {
		Cursor _cursor = null;
		try {

			SQLiteDatabase _database = DataBaseUtility
					.getReadableDBForEvaluation();
			try {
				_cursor = _database.query(SEConstants.TABLE_MARKS,
						selectedCols, SEConstants.barcode + " = '"
								+ answerSheetBarcode + "'", null, null, null,
						null);
				if (_cursor != null) {
					_cursor.moveToFirst();
				} else {
					FileLog.logInfo("cursor null getRow method", 0);
				}
			} catch (SQLiteException sqle) {
				FileLog.logInfo(
						"SQLException ---> Class Name: dbHelper, Method Name : getRow"
								+ "\n" + sqle.getMessage(), 0);
			} finally {
				_database.close();
			}

		} catch (Exception e) {
			FileLog.logInfo(
					"Exception ---> Class Name: dbHelper, Method Name : getRow"
							+ "\n" + e.getMessage(), 0);
		}
		return _cursor;
	}

	// method for retrieving row from table
	public Cursor getRow(String table, String where, String[] selectedCols) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = DataBaseUtility
					.getReadableDBForEvaluation();
			try {
				_cursor = _database.query(table, selectedCols, where, null,
						null, null, null);
				if (_cursor != null) {
					_cursor.moveToFirst();
				} else {
					FileLog.logInfo("cursor null getRow method", 0);
				}
			} catch (SQLiteException sqle) {
				FileLog.logInfo(
						"SQLException ---> Class Name: dbHelper, Method Name : getRow"
								+ "\n" + sqle.getMessage(), 0);
			} finally {
				_database.close();
			}

		} catch (SQLiteException sqle) {
			FileLog.logInfo(
					"Exception ---> Class Name: dbHelper, Method Name : getRow"
							+ "\n" + sqle.getMessage(), 0);
		}
		return _cursor;
	}

	// method for deleting a row from a table
	public int deleteRow(String table, String where) {
		int del = 0;
		try {
			SQLiteDatabase _database = DataBaseUtility
					.getWritableDBForEvaluation();
			try {
				del = _database.delete(table, where, null);
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
	public int deleteMore(String sql) {
		int del = 0;
		try {
			SQLiteDatabase _database = DataBaseUtility
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
	public Cursor executeSelectSQLQuery(String query) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = DataBaseUtility
					.getReadableDBForEvaluation();
			try {
				_cursor = _database.rawQuery(query, null);
				if (_cursor != null) {
					_cursor.moveToFirst();
				} else {
					FileLog.logInfo("cursor null", 0);
				}
			} catch (SQLiteException sqle) {
				FileLog.logInfo(
						"SQLiteException ---> Class Name: dbHelper, Method Name : executeSelectSQLQuery"
								+ "\n" + sqle.getMessage(), 0);
			} finally {
				_database.close();
			}

		} catch (SQLiteException sqle) {
			FileLog.logInfo(
					"Exception ---> Class Name: dbHelper, Method Name : executeSelectSQLQuery"
							+ "\n" + sqle.getMessage(), 0);
		}

		return _cursor;
	}

	// execute query
	public Cursor executeSQLQuery(String query) {
		Cursor _cursor = null;
		try {
			SQLiteDatabase _database = DataBaseUtility
					.getWritableDBForEvaluation();  
			try {
				_cursor = _database.rawQuery(query, null);
				if (_cursor != null) {
					_cursor.moveToFirst();
				} else {
					FileLog.logInfo("cursor null", 0);
				}
			} catch (SQLiteException sqle) {
				FileLog.logInfo(
						"SQLiteException ---> Class Name: dbHelper, Method Name : executeQuery"
								+ "\n" + sqle.getMessage(), 0);
			} finally {
				_database.close();
			}

		} catch (SQLiteException sqle) {
			FileLog.logInfo(
					"Exception ---> Class Name: dbHelper, Method Name : executeQuery"
							+ "\n" + sqle.getMessage(), 0);
		}

		return _cursor;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
	}

}
