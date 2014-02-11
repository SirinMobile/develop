package com.uiu.camera.orangeil.friendly;


import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import com.uiu.stats.StatsConstants;
import com.uiu.stats.StatsManager;
import com.uiu.util.SoundManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

@ReportsCrashes(formKey = "dEg4QzlOb2xyeTZCNnRocXkzcWswSmc6MQ")
public class CameraApplication extends Application
{
	public static Typeface	segoeui;
	public static Typeface	seguisb;
	private static Context	context;
	
	@Override
	public void onCreate()
	{
		ACRA.init(this);
		super.onCreate();
		StatsManager.setStatsIdentification(StatsConstants.CAMERA_MODULE, this);
		SoundManager.getInstance();
		SoundManager.initSounds(getApplicationContext());
		SoundManager.loadSounds();
		
		segoeui = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
		seguisb = Typeface.createFromAsset(getAssets(), "fonts/seguisb.ttf");
		CameraApplication.context = getApplicationContext();
	}

	public static Context getAppContext()
	{
		return CameraApplication.context;
	}

	
	@Override
	public void onTerminate()
	{
		super.onTerminate();
		SoundManager.cleanup();
	}
}
