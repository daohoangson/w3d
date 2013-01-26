package com.daohoangson.w3d.dropbox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class SqliteDataSource {

	private SQLiteDatabase mDatabase;
	private SqliteHelper mHelper;

	SqliteDataSource() {
		mHelper = new SqliteHelper();
	}

	void open() {
		if (mDatabase == null) {
			mDatabase = mHelper.getWritableDatabase();
		}
	}

	void close() {
		mHelper.close();
	}

	long insertPhoto(Album album, String path, long bytes, String rev) {
		open();
		
		ContentValues values = new ContentValues();
		values.put(SqliteHelper.PHOTO_ALBUM_ID, album.id);
		values.put(SqliteHelper.PHOTO_PATH, path);
		values.put(SqliteHelper.PHOTO_BYTES, rev);
		values.put(SqliteHelper.PHOTO_REV, rev);

		long insertId = mDatabase
				.insert(SqliteHelper.PHOTO_TABLE, null, values);

		return insertId;
	}

	boolean deletePhoto(Photo photo) {
		if (photo == null) {
			return false;
		}

		open();
		
		return mDatabase.delete(SqliteHelper.PHOTO_TABLE, SqliteHelper.PHOTO_ID
				+ "=?", new String[] { String.valueOf(photo.id) }) > 0;
	}
	
	Photo getPhoto(String path) {
		Photo photo = null;

		open();
		
		Cursor cursor = mDatabase.query(SqliteHelper.PHOTO_TABLE,
				Photo.columns, SqliteHelper.PHOTO_PATH + "=?",
				new String[] { path }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			photo = new Photo(cursor);
			cursor.moveToNext();
		}
		cursor.close();

		return photo;
	}

	List<Photo> getPhotos(Album album) {
		List<Photo> photos = new ArrayList<Photo>();

		if (album == null) {
			return photos;
		}

		open();
		
		Cursor cursor = mDatabase.query(SqliteHelper.PHOTO_TABLE,
				Photo.columns, SqliteHelper.PHOTO_ALBUM_ID + "=?",
				new String[] { String.valueOf(album.id) }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			photos.add(new Photo(cursor));
			cursor.moveToNext();
		}
		cursor.close();

		return photos;
	}

	long insertAlbum(String path, String hash) {
		ContentValues values = new ContentValues();
		values.put(SqliteHelper.ALBUM_PATH, path);
		values.put(SqliteHelper.ALBUM_HASH, hash);
		values.put(SqliteHelper.ALBUM_UPDATED_TIME, new Date().getTime());

		open();
		
		long insertId = mDatabase
				.insert(SqliteHelper.ALBUM_TABLE, null, values);

		return insertId;
	}

	boolean updateAlbum(Album album, String hash) {
		if (album == null) {
			return false;
		}

		ContentValues values = new ContentValues();
		values.put(SqliteHelper.ALBUM_HASH, hash);
		values.put(SqliteHelper.ALBUM_UPDATED_TIME, new Date().getTime());

		open();
		
		return mDatabase.update(SqliteHelper.ALBUM_TABLE, values,
				SqliteHelper.ALBUM_ID + "=?",
				new String[] { String.valueOf(album.id) }) > 0;
	}

	boolean deleteAlbum(Album album) {
		if (album == null) {
			return false;
		}

		open();
		
		return mDatabase.delete(SqliteHelper.ALBUM_TABLE, SqliteHelper.ALBUM_ID
				+ "=?", new String[] { String.valueOf(album.id) }) > 0;
	}

	Album getAlbum(long albumId) {
		Album album = null;

		open();
		
		Cursor cursor = mDatabase.query(SqliteHelper.ALBUM_TABLE,
				Album.columns, SqliteHelper.ALBUM_ID + "=?",
				new String[] { String.valueOf(albumId) }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			album = new Album(cursor);
			cursor.moveToNext();
		}
		cursor.close();

		return album;
	}

	Album getAlbum(String path) {
		Album album = null;

		open();
		
		Cursor cursor = mDatabase.query(SqliteHelper.ALBUM_TABLE,
				Album.columns, SqliteHelper.ALBUM_PATH + "=?",
				new String[] { path }, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			album = new Album(cursor);
			cursor.moveToNext();
		}
		cursor.close();

		return album;
	}
}
