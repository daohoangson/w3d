package com.daohoangson.w3d;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Account;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

public class DropboxAuth extends Activity implements OnClickListener {

	private static final String TAG = "DropboxAuth";

	private Button mBtnAuth;
	private Button mBtnTest;

	private DropboxAPI<AndroidAuthSession> mDropboxApi;
	
	private TaskTest mTaskTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropbox_auth);

		mBtnAuth = (Button) findViewById(R.id.btn_dropbox_auth_auth);
		mBtnAuth.setOnClickListener(this);
		mBtnTest = (Button) findViewById(R.id.btn_dropbox_auth_test);
		mBtnTest.setOnClickListener(this);

		// initializes Dropbox API
		AppKeyPair appKeys = new AppKeyPair(Secret.DROPBOX_APP_KEY,
				Secret.DROPBOX_APP_SECRET);
		AndroidAuthSession session = new AndroidAuthSession(appKeys,
				Secret.DROPBOX_ACCESS_TYPE);
		mDropboxApi = new DropboxAPI<AndroidAuthSession>(session);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mDropboxApi.getSession().authenticationSuccessful()) {
			try {
				mDropboxApi.getSession().finishAuthentication();
				AccessTokenPair tokens = mDropboxApi.getSession()
						.getAccessTokenPair();

				// this activity should never be activated to users
				// so output token pair to console is not very worrying
				Log.i(TAG, String.format("key=%s", tokens.key));
				Log.i(TAG, String.format("secret=%s", tokens.secret));
			} catch (IllegalStateException e) {
				Log.e(TAG, "Error authenticating", e);
			}
		}
		
		mTaskTest = null;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (mTaskTest != null) {
			mTaskTest.cancel(true);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_dropbox_auth_auth:
			mDropboxApi.getSession().startAuthentication(this);
			break;
		case R.id.btn_dropbox_auth_test:
			AccessTokenPair tokens = new AccessTokenPair(
					Secret.DROPBOX_ACCESS_TOKEN_KEY,
					Secret.DROPBOX_ACCESS_TOKEN_SECRET);
			mDropboxApi.getSession().setAccessTokenPair(tokens);

			if (mTaskTest != null) {
				mTaskTest.cancel(true);	
			}
			
			mTaskTest = new TaskTest();
			mTaskTest.execute();
			
			break;
		}
	}

	private class TaskTest extends AsyncTask<Void, Void, Account> {
		
		@Override
		protected void onPreExecute() {
			mBtnTest.setEnabled(false);
			mBtnAuth.setEnabled(false);
		}

		@Override
		protected Account doInBackground(Void... params) {
			Account account = null;
			
			try {
				account = mDropboxApi.accountInfo();
			} catch (DropboxException e) {
				Log.e(TAG, "Error getting account info", e);
			}

			return account;
		}
		
		@Override
		protected void onPostExecute(Account account) {
			mBtnTest.setEnabled(true);
			
			// account found!
			mBtnAuth.setEnabled(false);
			mBtnAuth.setText(account.displayName);
		}

	};
}
