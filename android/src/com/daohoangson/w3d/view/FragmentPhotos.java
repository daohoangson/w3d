package com.daohoangson.w3d.view;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockFragment;
import com.daohoangson.w3d.R;
import com.daohoangson.w3d.dropbox.DropboxManager;
import com.daohoangson.w3d.dropbox.Photo;

public class FragmentPhotos extends SherlockFragment {

	private GridView mGrid;
	private PhotosAdapter mAdapter;
	private TaskLoad mTaskLoad;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater
				.inflate(R.layout.fragment_photos, container, false);

		mGrid = (GridView) view.findViewById(R.id.grid_photos);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAdapter = new PhotosAdapter(getActivity());
		mGrid.setAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mTaskLoad == null) {
			mTaskLoad = new TaskLoad();
			mTaskLoad.execute();
		}
	}

	private class TaskLoad extends AsyncTask<Void, Void, List<Photo>> {

		@Override
		protected List<Photo> doInBackground(Void... arg0) {
			DropboxManager dm = DropboxManager.getInstance();
			List<Photo> photos = dm.photos("/");

			return photos;
		}

		@Override
		protected void onPostExecute(List<Photo> photos) {
			mAdapter.clear();
			mAdapter.addAll(photos);
			mAdapter.notifyDataSetChanged();
			
			mTaskLoad = null;
		}

	}

	private class PhotosAdapter extends ArrayAdapter<Photo> {

		private LazyLoader mLazyLoader;
		private float mPhotoSize;

		public PhotosAdapter(Context context) {
			super(context, 0);

			mLazyLoader = new LazyLoader(context);

			mPhotoSize = context.getResources()
					.getDimension(R.dimen.photo_size);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView view = (ImageView) convertView;

			if (view == null) {
				view = new ImageView(getContext());
				view.setLayoutParams(new GridView.LayoutParams(
						(int) mPhotoSize, (int) mPhotoSize));
				view.setScaleType(ImageView.ScaleType.CENTER_CROP);
				view.setPadding(8, 8, 8, 8);
			}

			Photo photo = getItem(position);
			mLazyLoader.load(view, photo.path);

			return view;
		}

	}
}