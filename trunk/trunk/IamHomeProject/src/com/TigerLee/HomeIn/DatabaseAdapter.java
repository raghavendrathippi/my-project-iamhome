package com.TigerLee.HomeIn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseAdapter {
	
	
	private DatabaseHelper mDatabaseHelper;
	
	private final Context mContext;
	public SQLiteDatabase mSQLiteDatabase;
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_SIDO = "name";
	public static final String KEY_GUGUN = "gugun";
	public static final String KEY_DONG = "dong";
	public static final String KEY_RI = "ri";
	public static final String KEY_BLDG = "bldg";
	public static final String KEY_BUNJI = "bunji";
	public static final String KEY_ZIPCODE = "zipcode";
	 
	public static final int FIND_BY_SIDO = 0;
	public static final int FIND_BY_GUGUN = 1;
	public static final int FIND_BY_DONG = 2;
	public static final int FIND_BY_RI = 3;
	public static final int FIND_BY_BLDG = 4;
	public static final int FIND_BY_BUNJI = 5;
	public static final int FIND_BY_ZIPCODE = 6;
	 

	private static String SQL_PRIMARIKEY = "(_id integer primary key autoincrement,"; 
	private static String SQL_TEXT_NOTNULL = " not null, ";
	private static String SQL_TEXT_NOTNULL_END = " not null);";
	
	private static final String DATABASE_NAME = "address.db";
	private static final String DATABASE_TABLE = "korea";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE =
		"create table " 
		+ DATABASE_TABLE 
		+ SQL_PRIMARIKEY 
		+ KEY_SIDO + SQL_TEXT_NOTNULL
		+ KEY_GUGUN + SQL_TEXT_NOTNULL
		+ KEY_DONG + SQL_TEXT_NOTNULL
		+ KEY_RI + SQL_TEXT_NOTNULL
		+ KEY_BLDG + SQL_TEXT_NOTNULL
		+ KEY_BUNJI + SQL_TEXT_NOTNULL
		+ KEY_ZIPCODE + SQL_TEXT_NOTNULL_END;
	
	private static final String TAG = "DbAdapter";
	
	private class DatabaseHelper extends SQLiteOpenHelper{
		
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE);
		}
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			Log.v(TAG, "Upgrading db from version" + oldVersion + " to" +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS data");
			onCreate(db);
		}
	}
	public DatabaseAdapter(Context context){
		this.mContext = context;
	}
	
	public DatabaseAdapter open() throws SQLException{
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
		return this;
	}
	public void close(){
		mDatabaseHelper.close();
	}
	public long insertAddress(String sido, String gugun, String dong, String ri, String bldg, String bunji, String zipcode){
		ContentValues initialValues = new ContentValues();
		
		initialValues.put(KEY_SIDO, sido);
		initialValues.put(KEY_GUGUN, gugun);
		initialValues.put(KEY_DONG, dong);
		initialValues.put(KEY_RI, ri);
		initialValues.put(KEY_BLDG, bldg);
		initialValues.put(KEY_BUNJI, bunji);
		initialValues.put(KEY_ZIPCODE, zipcode);
		
		return mSQLiteDatabase.insert(DATABASE_TABLE, null, initialValues);
	}
	public Cursor selectDistinctQuery(String[] columnName, String selectionCondition){
		return mSQLiteDatabase.query(
				true, DATABASE_TABLE, columnName, selectionCondition, null, null, null, null, null);
	}
	public Cursor rawQuery(String sql, String[] selectionArgs){
		return mSQLiteDatabase.rawQuery(sql, selectionArgs);
	}
	
	public boolean deleteAddress(long rowID){
		return mSQLiteDatabase.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowID, null) > 0;
	}
	
	
	/*
	public Cursor fetchAllAddress(){
		return mSQLiteDatabase.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_PHONE}, null, null, null, null, null);
	 
	}
	
	public Cursor fetchBook(long rowID) throws SQLException{
		Cursor mCursor = mSQLiteDatabase.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_PHONE}, KEY_ROWID + "=" + rowID, null, null, null, null, null);
		if(mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}
	public boolean updateBook(long rowID, String name, String phone){
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_PHONE, phone);
		return mSQLiteDatabase.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowID, null) > 0;
	}
	*/
}
