package com.daohoangson.w3d;

import android.app.Application;

public class App extends Application {

	private static App sInstance;
	
	public App() {
		sInstance = this;
	}
	
	public static App getInstance() {
		return sInstance;
	}
	
}
