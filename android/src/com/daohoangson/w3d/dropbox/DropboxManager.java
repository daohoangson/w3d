package com.daohoangson.w3d.dropbox;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.daohoangson.w3d.Secret;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class DropboxManager {

	private static final String TAG = "DropboxManager";
	private static DropboxManager sInstance;

	private DropboxAPI<AndroidAuthSession> mDropboxApi;
	private SqliteDataSource mSqliteDataSource;

	private DropboxManager() {
		AppKeyPair appKeys = new AppKeyPair(Secret.DROPBOX_APP_KEY,
				Secret.DROPBOX_APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys,
				Secret.DROPBOX_ACCESS_TYPE);

		AccessTokenPair tokens = new AccessTokenPair(
				Secret.DROPBOX_ACCESS_TOKEN_KEY,
				Secret.DROPBOX_ACCESS_TOKEN_SECRET);
		session.setAccessTokenPair(tokens);

		mDropboxApi = new DropboxAPI<AndroidAuthSession>(session);

		mSqliteDataSource = new SqliteDataSource();
	}

	public Account accountInfo() {
		Account info = null;

		try {
			info = mDropboxApi.accountInfo();
		} catch (DropboxException e) {
			Log.e(TAG, "Error getting account info", e);
		}

		return info;
	}

	public List<Photo> photos(String path) {
		int fileLimit = 100; // max is 25000
		String hash = null; // known hash
		boolean list = true; // get files
		String rev = null; // ignore for directory anyway
		Entry entryDir = null;

		Album album = mSqliteDataSource.getAlbum(path);
		if (album != null) {
			Log.d(TAG, String.format("Existing hash found for %s", path));
			hash = album.hash;
		} else {
			Log.d(TAG, String.format("First time requesting %s", path));
		}

		try {
			if (album == null
					|| album.updatedTime + 60000 < new Date().getTime()) {
				// only query metadata once a minute at most
				entryDir = mDropboxApi.metadata(path, fileLimit, hash, list,
						rev);
			}
		} catch (DropboxServerException se) {
			Log.w(TAG, String.format(
					"Problem getting metadata for %s (%d, %s)", path, se.error,
					se.reason));
		} catch (DropboxException e) {
			Log.e(TAG, String.format("Error getting metadata for %s", path), e);
		}

		if (entryDir != null) {
			if (album != null) {
				// update existing album
				mSqliteDataSource.updateAlbum(album, entryDir.hash);
			} else {
				// insert new album
				long albumId = mSqliteDataSource.insertAlbum(path,
						entryDir.hash);
				album = mSqliteDataSource.getAlbum(albumId);
			}

			for (Entry entryFile : entryDir.contents) {
				if (entryFile.thumbExists) {
					Photo existingPhoto = mSqliteDataSource
							.getPhoto(entryFile.path);
					if (existingPhoto == null) {
						// this is a new photo
						mSqliteDataSource.insertPhoto(album, entryFile.path,
								entryFile.bytes, entryFile.rev);
						Log.d(TAG,
								String.format("%s new photo", entryFile.path));
					}
				} else {
					Log.d(TAG, String.format("%s no thumbnail", entryFile.path));
				}
			}
		}

		return mSqliteDataSource.getPhotos(album);
	}

	public boolean thumbnail(String path, File out) {
		try {
			mDropboxApi.getThumbnail(path, new FileOutputStream(out),
					DropboxAPI.ThumbSize.ICON_128x128,
					DropboxAPI.ThumbFormat.JPEG, null);

			return out.exists();
		} catch (Exception e) {
			Log.e(TAG, String.format("Error downloading thumbnail %s->%s",
					path, out), e);

			out.delete();

			return false;
		}
	}

	public static DropboxManager getInstance() {
		if (sInstance == null) {
			sInstance = new DropboxManager();
		}

		return sInstance;
	}

}
