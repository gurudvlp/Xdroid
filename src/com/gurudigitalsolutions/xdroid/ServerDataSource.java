package com.gurudigitalsolutions.xdroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ServerDataSource {
	// Database fields
		private SQLiteDatabase database;
		private DbServers dbHelper;
		private String[] allColumns = { DbServers.COLUMN_ID,
				DbServers.COLUMN_IP, DbServers.COLUMN_PORT,
				DbServers.COLUMN_USERNAME, DbServers.COLUMN_PASSWORD,
				DbServers.COLUMN_ISDEFAULT};

		public ServerDataSource(Context context) {
			dbHelper = new DbServers(context, null, null, 0);
		}

		public void open() throws SQLException {
			database = dbHelper.getWritableDatabase();
		}

		public void close() {
			dbHelper.close();
		}

		public void createServerEntry(String IP, int Port, String Username, String Password, Boolean IsDefault)
		{
			if(!database.isOpen()) { this.open(); }
			ContentValues values = new ContentValues();
			
			values.put(DbServers.COLUMN_IP, IP);
			values.put(DbServers.COLUMN_PORT, Port);
			values.put(DbServers.COLUMN_USERNAME, Username);
			values.put(DbServers.COLUMN_PASSWORD, Password);
			values.put(DbServers.COLUMN_ISDEFAULT, IsDefault);
			
			Server sinfo = ServerInfo(IP);
			if(sinfo == null)
			{
				//	This server is not in our database already, so let's create a new
				//	entry.
				long insertId = database.insert(DbServers.TABLE_SERVERS, null, values);
			}
			else
			{
				//	This server is already in our local database, so it's entry should
				//	just be updated.
				long insertId = database.update(DbServers.TABLE_SERVERS, values, "ip=?", new String[]{IP});
			}
			
			//database.close();
		}
		
		
		/*public Comment createComment(String comment) {
			ContentValues values = new ContentValues();
			values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
			long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
					values);
			// To show how to query
			Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
					allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
					null, null, null);
			cursor.moveToFirst();
			return cursorToComment(cursor);
		}*/
		/*
		public void deleteComment(Comment comment) {
			long id = comment.getId();
			System.out.println("Comment deleted with id: " + id);
			database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
					+ " = " + id, null);
		}

		public List<Comment> getAllComments() {
			List<Comment> comments = new ArrayList<Comment>();
			Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
					allColumns, null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Comment comment = cursorToComment(cursor);
				comments.add(comment);
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
			return comments;
		}*/

		public Server ServerInfo(String IP)
		{
			if(!database.isOpen()) { this.open(); }
			Server sinfo = new Server();
			
			Cursor cursor = database.rawQuery("SELECT * FROM " + DbServers.TABLE_SERVERS + " WHERE ip=?", new String[]{IP});
			int ccount = cursor.getCount();
			if(ccount <= 0)
			{
				cursor.close();
				return null;
			}
			
			if(cursor.isBeforeFirst()) { cursor.moveToFirst(); }
			sinfo.IP = cursor.getString(1);
			sinfo.Port = cursor.getInt(2);
			sinfo.Username = cursor.getString(3);
			sinfo.Password = cursor.getString(4);
			int isd = cursor.getInt(5);
			if(isd == 0) { sinfo.IsDefault = false; } else { sinfo.IsDefault = true; }
			
			cursor.close();
			//database.close();
			return sinfo;
		}
		
		public Server DefaultServerInfo()
		{
			if(!database.isOpen()) { this.open(); }
			Server sinfo = new Server();
			
			Cursor cursor = database.rawQuery("SELECT * FROM " + DbServers.TABLE_SERVERS + " WHERE isdefault=?", new String[]{"1"});
			int ccount = cursor.getCount();
			
			if(ccount <= 0)
			{
				cursor.close();
				//database.close();
				return null;
			}
			
			if(cursor.isBeforeFirst()) { cursor.moveToFirst(); }
			sinfo.IP = cursor.getString(1);
			sinfo.Port = cursor.getInt(2);
			sinfo.Username = cursor.getString(3);
			sinfo.Password = cursor.getString(4);
			int isd = cursor.getInt(5);
			if(isd == 0) { sinfo.IsDefault = false; } else { sinfo.IsDefault = true; }
			
			cursor.close();
			//database.close();
			return sinfo;
		}
		
		/*
		public String infoValue(String key)
		{
			//Cursor cursor = database.query(DbInfo.TABLE_USERINFO, allColumns, null, null, null, null, null);
			if(!database.isOpen()) { this.open(); }
			Cursor cursor = database.rawQuery("SELECT * FROM userinfo WHERE key=?", new String[]{key});
			int ccount = cursor.getCount();
			if(ccount <= 0) {
				cursor.close();
				//database.close();
				return null; 
			}
			
			if(cursor.isBeforeFirst()) { cursor.moveToFirst(); }
			String toret = cursor.getString(2);
			cursor.close();
			return toret;
		}*/
		/*
		private Comment cursorToComment(Cursor cursor) {
			Comment comment = new Comment();
			comment.setId(cursor.getLong(0));
			comment.setComment(cursor.getString(1));
			return comment;
		}*/
}
