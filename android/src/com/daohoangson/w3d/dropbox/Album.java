package com.daohoangson.w3d.dropbox;

import android.database.Cursor;

public class Album {
	public long id;
	public String path;
	public String hash;
	public long updatedTime;

	static String[] columns = new String[] { SqliteHelper.ALBUM_ID,
			SqliteHelper.ALBUM_PATH, SqliteHelper.ALBUM_HASH, SqliteHelper.ALBUM_UPDATED_TIME };
	
	Album(Cursor cursor) {
		id = cursor.getLong(0);
		path = cursor.getString(1);
		hash = cursor.getString(2);
		updatedTime = cursor.getLong(3);
	}
}
