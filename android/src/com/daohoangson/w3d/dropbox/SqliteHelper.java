package com.daohoangson.w3d.dropbox;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.daohoangson.w3d.App;

class SqliteHelper extends SQLiteOpenHelper {

	static final String PHOTO_TABLE = "photos";
	static final String PHOTO_ID = "_id";
	static final String PHOTO_ALBUM_ID = "album_id";
	static final String PHOTO_PATH = "path";
	static final String PHOTO_BYTES = "bytes";
	static final String PHOTO_REV = "rev";

	static final String ALBUM_TABLE = "albums";
	static final String ALBUM_ID = "_id";
	static final String ALBUM_PATH = "path";
	static final String ALBUM_HASH = "hash";
	static final String ALBUM_UPDATED_TIME = "updated_time";

	private static final String TAG = "SqliteHelper";

	private static final String DATABASE_NAME = "dropbox.db";
	private static final int DATABASE_VERSION = 3;

	SqliteHelper() {
		super(App.getInstance(), DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table " + PHOTO_TABLE + " (" + PHOTO_ID
				+ " integer primary key autoincrement," + PHOTO_ALBUM_ID
				+ " integer not null," + PHOTO_PATH + " text not null,"
				+ PHOTO_BYTES + " integer not null," + PHOTO_REV
				+ " text not null);");

		db.execSQL("create table " + ALBUM_TABLE + " (" + ALBUM_ID
				+ " integer primary key autoincrement," + ALBUM_PATH
				+ " text not null," + ALBUM_HASH + " text not null,"
				+ ALBUM_UPDATED_TIME + " integer not null);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + PHOTO_TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + ALBUM_TABLE);

		onCreate(db);
	}

}
