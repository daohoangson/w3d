package com.daohoangson.w3d.dropbox;

import android.database.Cursor;

public class Photo {
	public long id;
	public long albumId;
	public String path;
	public long bytes;
	public String rev;

	static String[] columns = new String[] { SqliteHelper.PHOTO_ID,
			SqliteHelper.PHOTO_ALBUM_ID, SqliteHelper.PHOTO_PATH,
			SqliteHelper.PHOTO_BYTES, SqliteHelper.PHOTO_REV };

	Photo(Cursor cursor) {
		id = cursor.getLong(0);
		albumId = cursor.getLong(1);
		path = cursor.getString(2);
		bytes = cursor.getLong(3);
		rev = cursor.getString(4);
	}
}
