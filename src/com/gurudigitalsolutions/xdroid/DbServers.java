package com.gurudigitalsolutions.xdroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbServers extends SQLiteOpenHelper
{

	public static final String TABLE_SERVERS = "servers";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_IP = "ip";
	public static final String COLUMN_PORT = "port";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_ISDEFAULT = "isdefault";
	/*
	 * Servers Database:
	 * 
	 * id
	 * ip
	 * port
	 * username
	 * password
	 * isdefault
	 */

	private static final String DATABASE_NAME = "servers.db";
	private static final int DATABASE_VERSION = 5;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SERVERS + "( " + 
			COLUMN_ID + " integer primary key autoincrement, " + 
			COLUMN_IP + " text not null, " + 
			COLUMN_PORT + " integer not null, " +
			COLUMN_USERNAME + " text not null, " +
			COLUMN_PASSWORD + " text not null, " +
			COLUMN_ISDEFAULT + " integer not null);";
	
	public DbServers(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		ContentValues cvs = new ContentValues();

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DbServers.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);
		onCreate(db);
	}
}
