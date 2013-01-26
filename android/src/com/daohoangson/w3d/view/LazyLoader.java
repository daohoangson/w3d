package com.daohoangson.w3d.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.daohoangson.w3d.R;
import com.daohoangson.w3d.dropbox.DropboxManager;

public class LazyLoader {

	private final static String TAG = "LazyLoader";

	private File cacheRoot;

	private ExecutorService mExecutor;
	private Handler mHandler = new Handler();

	public LazyLoader(Context context) {
		mExecutor = Executors.newFixedThreadPool(2);

		cacheRoot = context.getExternalCacheDir();
		if (cacheRoot == null) {
			// this will happen if the device doesn't have external storage
			// switch to use internal storage
			cacheRoot = context.getCacheDir();
		}
	}

	public void load(ImageView imageView, String photoPath) {
		imageView.setImageResource(R.drawable.stub);
		RealLoader rl = new RealLoader(imageView, photoPath);
		mExecutor.submit(rl);
	}

	public void cancel(ImageView imageView) {
		// TODO
	}

	private File getOutputFile(RealLoader rl) {
		return new File(cacheRoot, rl.photoPath);
	}

	private class RealLoader implements Runnable {
		ImageView imageView;
		String photoPath;
		File out;

		RealLoader(ImageView _imageView, String _photoPath) {
			imageView = _imageView;
			photoPath = _photoPath;

			out = getOutputFile(this);
		}

		@Override
		public void run() {
			if (!out.exists()) {
				DropboxManager dm = DropboxManager.getInstance();

				File dir = out.getParentFile();
				dir.mkdirs();

				if (dir.isDirectory() && !dm.thumbnail(photoPath, out)) {
					Log.e(TAG, String.format(
							"Error requesting thumbnail %s->%s", photoPath,
							out.getAbsolutePath()));
				}
			}

			if (out.exists()) {
				FileInputStream stream;
				Bitmap bitmap = null;
				try {
					stream = new FileInputStream(out);
					bitmap = BitmapFactory.decodeStream(stream, null, null);
					stream.close();

					Log.v(TAG,
							String.format("Loaded bitmap from %s",
									out.getAbsolutePath()));
				} catch (IOException e) {
					Log.e(TAG,
							String.format("Error reading bitmap from %s",
									out.getAbsolutePath()), e);
				}

				mHandler.post(new Displayer(imageView, bitmap));
			}
		}
	}

	private class Displayer implements Runnable {

		ImageView imageView;
		Bitmap bitmap;

		Displayer(ImageView _imageView, Bitmap _bitmap) {
			imageView = _imageView;
			bitmap = _bitmap;
		}

		@Override
		public void run() {
			imageView.setImageBitmap(bitmap);
		}

	}

}
