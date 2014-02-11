package com.uiu.camera.orangeil.friendly.ui;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;

public class CustomSurfaceView extends SurfaceView {

	private static final String TAG = "CustomSurfaceView"; 
	private static final int INVALID_POINTER_ID = -1;

	static private int maxZoom;
	static private  Camera mCamera;
	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;

	public CustomSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Log.i(TAG, "CustomSurfaceView");

		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}
	
	static public void setCamera (Camera camera)
	{
		mCamera = camera;
		Camera.Parameters mCameraParams = mCamera.getParameters();
		maxZoom = mCameraParams.getMaxZoom();
	}

	// zoom camera (depends on if and how camera zooms)
	public void zoomView(int zoom)
	{
		Log.i(TAG, "zoomView");
		if(mCamera == null)
			return;
		
		Camera.Parameters mCameraParams = mCamera.getParameters();
		if (mCameraParams.isZoomSupported() && zoom <= maxZoom)
		{
			mCameraParams.setZoom(zoom);
			mCamera.setParameters(mCameraParams);
		}
		else
		{
			//zoom is not supported
		}
	}
	 
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);
		Log.i(TAG, "onTouchEvent");

		return true;
	}

	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			
			mScaleFactor *= detector.getScaleFactor();
			
			Log.i(TAG, "onScale - mScaleFactor " + mScaleFactor);
			
			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.0f, Math.min(mScaleFactor, maxZoom));
			
			int res = (int)Math.ceil(mScaleFactor);
			
			zoomView(res);
			return true;
		}

	}
}


