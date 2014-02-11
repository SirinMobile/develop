package com.uiu.camera.orangeil.friendly.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CameraProfile;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.uiu.camera.orangeil.friendly.CameraActivity;
import com.uiu.camera.orangeil.friendly.R;
import com.uiu.camera.orangeil.friendly.ui.CustomSurfaceView;
import com.uiu.stats.StatsConstants;
import com.uiu.stats.StatsManager;

public class CameraFunctions
{
	private static final String TAG = CameraActivity.class.getSimpleName();
	public static final String ACTION_ACTIVATE_FILE_MANAGER_SYNC = "com.activate.filemanager.sync";
	public final static long SIZE_KB = 1024L;
	public final static long SIZE_MB = SIZE_KB * SIZE_KB;
	public final static String [] flashSettings = {"off","auto","on"};
	public final static String [] brightnessSettings = {"auto", "incandescent", "fluorescent", "daylight", "cloudy-daylight"};
	public final static String [] cameraDirection = {"back", "front"};
	public Camera mCamera;
	public Parameters frontCameraParams;
	public Parameters backCameraParams;
	public boolean mPreviewRunning = false;
	public MediaRecorder mMediaRecoder;
	public boolean mIsRecording = false;
	List<Camera.Size>[] lists;
	public Camera.Size currentSize;
	private int colorMode = 0;
	Context mContext;
	public boolean bwImage = false;
	public boolean isFront = false;
	public boolean torchIsOn = false;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera.Size frontPictureSizes, rearPictureSizes,frontPreviewSizes, rearPreviewSizes;
	private int colorEffect;
	private Size previewSize;

	public CameraFunctions(Context context, SurfaceView mSurfaceView, SurfaceHolder mSurfaceHolder)
	{
		this.mContext = context;
		this.mSurfaceView = mSurfaceView;
		this.mSurfaceHolder = mSurfaceHolder;
		frontPictureSizes = rearPictureSizes = frontPreviewSizes = rearPreviewSizes = null;;
	}

	
	/**
	 * Start recording of videos
	 */
	public void startMediaRecording()
	{

		backCameraParams = mCamera.getParameters();
		frontCameraParams = mCamera.getParameters();

		// Release the camera before recording
		stopPreview();

		if (!prepareMediaRecorder()){
		
			//Toast.makeText(mContext, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
			Toast.makeText(mContext, R.string.sd_low_space_text, Toast.LENGTH_LONG).show();
			((Activity) mContext).finish();
		
		}else{
		
			mMediaRecoder.start();
			mIsRecording = true;
		}
	}


	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info =
			new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
		.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0: degrees = 0; break;
		case Surface.ROTATION_90: degrees = 90; break;
		case Surface.ROTATION_180: degrees = 180; break;
		case Surface.ROTATION_270: degrees = 270; break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;  // compensate the mirror
		} else {  // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	/**
	 * Preparing the media recording
	 * 
	 * @return
	 */
	private boolean prepareMediaRecorder()
	{
		if (!sdHasEnoughSpace())
		{
			Toast.makeText(mContext, R.string.sd_low_space_text, Short.SIZE).show();
			return false;
		}
		if(!isFront)
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

		else
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		mCamera.setDisplayOrientation(90);

		CustomSurfaceView.setCamera(mCamera);
		//setCameraDisplayOrientation((Activity)mContext,0,mCamera);

		if(bwImage)
		{
			backCameraParams.setColorEffect("mono");
			frontCameraParams.setColorEffect("mono");
		}
		try
		{
			//this might not work on certain devices
			backCameraParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		}
		catch(Exception e){}
		
		//TODO: check if the torch is on
		Log.i(TAG, "backCameraParams.getFlashMode() " + backCameraParams.getFlashMode());
		Log.i(TAG, "frontCameraParams.getFlashMode() " + frontCameraParams.getFlashMode());
		
		if(!isFront)
			mCamera.setParameters( backCameraParams );
		else 
			mCamera.setParameters( frontCameraParams );
		
		mCamera.unlock();

		mMediaRecoder = new MediaRecorder();

		mMediaRecoder.setCamera(mCamera);

		int orientation = 0;

		switch(CameraActivity.mOrientation)
		{
			case CameraActivity.ORIENTATION_LANDSCAPE_NORMAL:
				Log.i(TAG, "CameraActivity.ORIENTATION_LANDSCAPE_NORMAL");
				if (isFront)
					orientation = 0;
					//orientation = 180;
				else
					orientation = 0;
					//orientation = 0;
				break;
				
			case CameraActivity.ORIENTATION_PORTRAIT_NORMAL:
				Log.i(TAG, "CameraActivity.ORIENTATION_PORTRAIT_NORMAL");
				if (isFront)
					orientation = 270;
					//orientation = 270;
				else
					orientation = 90;
					//orientation = 90;
				break;
				
			case CameraActivity.ORIENTATION_PORTRAIT_INVERTED:
				Log.i(TAG, "CameraActivity.ORIENTATION_PORTRAIT_INVERTED");
				if (isFront)
					orientation = 90;
					//orientation = 0;
				else
					orientation = 270;
					//orientation = 180;
				break;
				
			case CameraActivity.ORIENTATION_LANDSCAPE_INVERTED:
				Log.i(TAG, "CameraActivity.ORIENTATION_LANDSCAPE_INVERTED");
				if (isFront)
					orientation = 180;
					//orientation = 90;
				else
					orientation = 180;
					//orientation = 270;
				break;
		}
		
		Log.i(TAG, "CameraActivity.mOrientation " + CameraActivity.mOrientation);
		Log.i(TAG, "orientation " + orientation);

		mMediaRecoder.setOrientationHint(orientation);
/*		mMediaRecoder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
			@Override
			public void onError(MediaRecorder mr, int what, int extra)
			{
				Log.i(TAG, "Error");
			}
		});*/

		Method[] methods = mMediaRecoder.getClass().getMethods();
		mMediaRecoder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecoder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		mMediaRecoder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		
		List<Integer> frameRates = backCameraParams.getSupportedPreviewFrameRates();

		mMediaRecoder.setVideoFrameRate(getHighestFrameRate(frameRates));

		Size cameraSize = backCameraParams.getPreferredPreviewSizeForVideo();
		if(cameraSize != null )
			mMediaRecoder.setVideoSize(cameraSize.width, cameraSize.height);

		for (Method method: methods){
			try{
				if (method.getName().equals("setAudioChannels")){
					method.invoke(mMediaRecoder, String.format("audio-param-number-of-channels=%d", 1));
				} 
				else if(method.getName().equals("setAudioEncodingBitRate")){
					method.invoke(mMediaRecoder,12200);
				}
				else if(method.getName().equals("setVideoEncodingBitRate")){
					method.invoke(mMediaRecoder, 3000000);
				}
				else if(method.getName().equals("setAudioSamplingRate")){
					method.invoke(mMediaRecoder,8000);
				}
				else if(method.getName().equals("setVideoFrameRate")){
					//This makes start recording fail
					//method.invoke(mMediaRecoder,24);
				}
			}catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			}
			
		}
		mMediaRecoder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mMediaRecoder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		mMediaRecoder.setOutputFile(Environment.getExternalStorageDirectory().getPath() +"/UIUSuit/UIUGallery/UIUCamera/Videos/uiu_" + System.currentTimeMillis()/1000 + ".3gp");

		// Set max duration 10min
		//mMediaRecoder.setMaxDuration(600000000);

		// Set max file size 10MB
		//mMediaRecoder.setMaxFileSize(1000000000);

		mMediaRecoder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

		try
		{
			mMediaRecoder.prepare();
		}

		catch (IllegalStateException e)
		{
			releaseMediaRecorder();
			Log.v(TAG, "Crashed");
			return false;
		}

		catch (IOException e)
		{
			releaseMediaRecorder();
			Log.v(TAG, "Crashed");
			return false;
		}

		return true;

	}
	
	public boolean sdHasEnoughSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		int sdAvailSize = (int) ((int)stat.getAvailableBlocks()
				* (double)stat.getBlockSize());
		int mbAvailable = (int) (sdAvailSize / SIZE_MB);
		
		Log.i(TAG, "mbAvailable " + mbAvailable);
		
		if(mbAvailable > 5) // minimum required size for a picture
			return true;
		else
			return false;
	}

	public int getHighestFrameRate(List <Integer> list)
	{
		if (list == null)
			return 0;

		int temp1 = list.get(0);
		int temp2 = list.get(list.size()-1);

		return (temp1 > temp2)?temp1:temp2;



	}

	public void releaseMediaRecorder()
	{
		if (mMediaRecoder != null)
		{
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;

			// clear recorder configuration
			mMediaRecoder.reset();

			// release the recorder object
			mMediaRecoder.release();
			mMediaRecoder = null;


			mIsRecording = false;

			Log.d(TAG, "Media recording released!");
		}
	}

	public void destroySurface()
	{
		if (mPreviewRunning && mCamera != null)
		{
			if (mCamera != null)
			{
				stopPreview();
			}
			mPreviewRunning = false;
		}
	}

	public boolean startPreview()
	{
		if (!mPreviewRunning && mCamera != null)
		{
			mCamera.startPreview();
			mPreviewRunning = true;
			Log.d(TAG, "Preview Started");
			return true;
		}
		Log.d(TAG, "Preview failed to start");
		return false;
	}

	public void stopPreview()
	{
		if (mPreviewRunning && mCamera != null)
		{
			mCamera.stopPreview();
			mPreviewRunning = false;
			mCamera.release();
			mCamera = null;
		}
	}

	public void setCameraParams()
	{

		if(isFront)
		{

			frontCameraParams = mCamera.getParameters();
			//ArrayList<String> whiteBalance = (ArrayList<String>) frontCameraParams.getSupportedWhiteBalance();
			frontCameraParams.setPreviewSize(frontPreviewSizes.width, frontPreviewSizes.height);
			frontCameraParams.setPictureSize(frontPictureSizes.width, frontPictureSizes.height);
			frontCameraParams.setJpegQuality(100);
			frontCameraParams.set("rotation", 90);
			frontCameraParams.setPictureFormat(ImageFormat.JPEG);
			frontCameraParams.set("flash-mode", "auto");
			mCamera.setDisplayOrientation(90);
			try{
				frontCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			catch(Exception e){}
			mCamera.setParameters(frontCameraParams);
		}
		else
		{

			backCameraParams.setPreviewSize(previewSize.width, previewSize.height);
			backCameraParams.setPictureSize(rearPictureSizes.width, rearPictureSizes.height);

			try{
				backCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}
			catch(Exception e){}
			
			backCameraParams.setJpegQuality(100);
			backCameraParams.set("rotation", 90);
			backCameraParams.setPictureFormat(ImageFormat.JPEG);
			//backCameraParams.set("flash-mode", "auto");
			//				mCameraParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			mCamera.setDisplayOrientation(90);
			mCamera.setParameters(backCameraParams);
		}
	}

	public void changeSurface(SurfaceHolder holder,int width, int height)
	{

		try
		{
			if(mCamera == null)
			{
				mCamera = Camera.open();

				CustomSurfaceView.setCamera(mCamera);
				mCamera.setErrorCallback(errorListener);
				frontCameraParams = mCamera.getParameters();
				backCameraParams = mCamera.getParameters();

				setPictureSizeArrays();

				if(frontPictureSizes == null)
				{
					Size size = frontCameraParams.getPictureSize();
					//frontPictureSizes = getBestPictureSize(width, height,true);
					frontPictureSizes = getOptimalPreviewSize(frontCameraParams.getSupportedPictureSizes(), (double) size.width / size.height);
					//frontPreviewSizes = getBestPreviewSize(width, height);
					frontPreviewSizes = frontPictureSizes; 
				}
				if(rearPictureSizes == null)
				{
					//rearPictureSizes = getBestPictureSize(width, height,false);
					Size size = backCameraParams.getPictureSize();
					
					//previewSize = getOptimalPreviewSize(backCameraParams.getSupportedPreviewSizes(), point.x, point.y);
				
					previewSize = getOptimalPreviewSize(backCameraParams.getSupportedPictureSizes(), (double) size.width / size.height);
					rearPictureSizes = previewSize;
					//rearPreviewSizes = rearPictureSizes;
				}

				// new
				// mBackCamera.setPreviewCallback(mPreviewCallback);
				
				frontCameraParams.setJpegQuality(CameraProfile.QUALITY_HIGH);
				backCameraParams.setJpegQuality(CameraProfile.QUALITY_HIGH);
				setCameraParams();
				mCamera.setPreviewDisplay(holder);
				startPreview();
			}
		}

		catch (Exception e)
		{
			Log.d(TAG, "Cannot start preview", e);
			((Activity) mContext).finish();
		}

		mPreviewRunning = true;
	}
	

    public Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.001;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of preview surface. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size.
        Point point = getDefaultDisplaySize();
        int targetHeight = Math.min(point.x, point.y);
        // Try to find an size match aspect ratio and size
        
        optimalSize = sizes.get(sizes.size()-1);
/*        for (Size size : sizes) {

            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) 
            	continue;
            
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }*/
        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSize == null) {
            Log.w(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
	
    private Point getDefaultDisplaySize() {
    	Point size = new Point();
        Display d = ((Activity)mContext).getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            d.getSize(size);
        } else {
            size.set(d.getWidth(), d.getHeight());
        }
        return size;
    }
	
	
	private Camera.Size getBestPreviewSize(Camera.Parameters parameters, int width, int height) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width<=width && size.height<=height) {
				if (result==null) {
					result=size;
				}
				else {
					int resultArea=result.width*result.height;
					int newArea=size.width*size.height;

					if (newArea>resultArea) {
						result=size;
					}
				}
			}
		}

		return(result);
	}
	
	
	
	Camera.ErrorCallback errorListener = new Camera.ErrorCallback() {

		@Override
		public void onError(int error, Camera camera)
		{
			Log.e(TAG, "Camera ERROR: " + error);
			if (mCamera != null)
			{
				stopPreview();
				mCamera.release();

			}

		}
	};

	// calculates cameras resolution capabilities and builds array of them
	@SuppressWarnings("unchecked")
	private void setPictureSizeArrays()
	{
		List<Camera.Size> pictureSizes = backCameraParams.getSupportedPictureSizes();

		int length = 0;
		int index = -1;
		double prevMegapixels = 0;
		for (Size size : pictureSizes)
		{
			double megaPixels = Math.ceil((double) size.width * size.height / (double) 1000000);
			if (megaPixels != prevMegapixels)
			{
				length++;
				prevMegapixels = megaPixels;
			}
		}
		prevMegapixels = 0;
		// Log.d("length", length + "");

		lists = (ArrayList<Camera.Size>[]) new ArrayList[length];

		for (Size size : pictureSizes)
		{
			int gcd = BigInteger.valueOf(size.width).gcd(BigInteger.valueOf(size.height)).intValue();
			double megaPixels = Math.ceil((double) size.width * size.height / (double) 1000000);
			if (prevMegapixels != megaPixels && index < length)
			{
				index++;
				lists[index] = new ArrayList<Camera.Size>();
				lists[index].add(size);
				prevMegapixels = megaPixels;
			}
			else if (prevMegapixels == megaPixels && index < length)
			{
				lists[index].add(size);
			}

			Log.i("@@@@@@@@@@@@@", "PICTURES SIZES " + Math.ceil((double) size.width * size.height / (double) 1000000) + " ASPECT RATIO "
					+ size.width / gcd + " | " + size.height / gcd + " PIXELS " + size.width + " | " + size.height);
		}
		currentSize = lists[0].get(0);
		for (int i = 0; i < lists.length; i++)
		{
			try
			{
				System.out.println(i);
				for (Camera.Size size : lists[i])
				{
					System.out.println(size.width + "," + size.height);
				}
			}
			catch (Exception e)
			{
				System.out.println("NULL");
			}
		}
	}

	// after taking picture - returns to preview mode
	public void retake()
	{
		mCamera.startPreview();
		//	setCameraParams();

	}

	public void stopFilming() 
	{
		Log.i(TAG, "stopFilming");
		stopPreview();
		//mCamera.release();
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		if(isFront)
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		else
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

		CustomSurfaceView.setCamera(mCamera);
		//	mCamera.setParameters(mCameraParams);

	//	backCameraParams.set("rotation", 90);
	//	frontCameraParams.set("rotation", 90);

	//	mCamera.setDisplayOrientation(90);

		setCameraParams();
		try
		{
			mCamera.setPreviewDisplay(mSurfaceHolder);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		startPreview();

		mPreviewRunning = true;
	}

	private Camera.Size getBestPreviewSize(int height, int width)
	{
		Camera.Size result = null;
		Camera.Parameters p = mCamera.getParameters();
		for (Camera.Size size : p.getSupportedPreviewSizes())
		{
			if ((size.width <= width && size.height <= height) || (size.width <= height && size.height <= width))
			{
				if (result == null)
				{
					result = size;
				}
				else
				{
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea)
					{
						result = size;
					}
				}
			}
		}
		return result;
	}

	private Camera.Size getBestPictureSize(int height, int width, boolean front)
	{
		Camera.Size result = null;

		Camera.Parameters p = mCamera.getParameters();

		List<Camera.Size> l = p.getSupportedPictureSizes();
		if(!front)
		{
			if (l.size()/2 != 0)
				return l.get((l.size()/2)-1);
			return l.get(l.size()/2);
		}
		else
		{
			for (Camera.Size size : p.getSupportedPictureSizes())
			{

				if ((size.width <= width && size.height <= height) || (size.width <= height && size.height <= width))
				{
					if (result == null)
					{
						result = size;
					}
					else
					{
						int resultArea = result.width * result.height;
						int newArea = size.width * size.height;

						if (newArea > resultArea)
						{
							Log.d(TAG, "Best Picture Size is width: " + size.width + " height: " + size.height );
							result = size;
						}
					}
				}
			}
			return result;
		}

	}

	// store image on sdcard
	public boolean StoreByteImage(Context mContext, byte[] imageData, int rotation)
	{
		File folder = new File(Environment.getExternalStorageDirectory() + "/UIUSuit/UIUGallery/UIUCamera/Pictures");
		if (!folder.exists())
		{
			folder.mkdir();
		}

		long lDateTime = new Date().getTime();
		File photo = new File(folder, lDateTime + "photo.jpg");

		try
		{

			FileOutputStream fos = new FileOutputStream(photo.getPath());
			fos.write(imageData);
			ExifInterface exif = new ExifInterface(photo.getPath());
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(rotation));
			exif.saveAttributes();
			fos.close();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			Log.e(TAG, "Exception in photoCallback", e);
		}

		galleryAddPic(photo);
		((Activity) mContext).setResult(Activity.RESULT_OK);

		return true;
	}

	// saves pic to gallery
	private void galleryAddPic(File f)
	{
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		mContext.sendBroadcast(mediaScanIntent);
		
		//send broadcast to the file manager - when taking picture
		Intent fileManagerSync = new Intent();
		fileManagerSync.setAction(ACTION_ACTIVATE_FILE_MANAGER_SYNC);
		mContext.sendBroadcast(fileManagerSync);
	}



	public boolean toggleFlash(int position)
	{
		try
		{
			Log.i(TAG, "toggleFlash " + position);
			Parameters p = mCamera.getParameters();
			if(position == 0 && torchIsOn)
			{
				StatsManager.addToStatFile(StatsConstants.CAMERA_SETTINGS_CHANGE, "Torch off");
				p.setFlashMode(Parameters.FLASH_MODE_AUTO);
				torchIsOn = false;
			}
			else if (position == 1 && !torchIsOn)
			{
				StatsManager.addToStatFile(StatsConstants.CAMERA_SETTINGS_CHANGE, "Torch on");
				p.setFlashMode(Parameters.FLASH_MODE_TORCH);
				torchIsOn = true;
			}
			mCamera.setParameters(p);
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// switch camera (0 - front ,1 - back)
	public int switchCamera(int direction)
	{
		if ((isFront && direction == 1) || (!isFront && direction == 0))
			return 0;

		stopPreview();
		try
		{
			if (isFront == false)
			{
				isFront = true;
				mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
				StatsManager.addToStatFile(StatsConstants.CAMERA_SETTINGS_CHANGE, "front camera");
			}

			else
			{
				isFront = false;
				mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
				backCameraParams = mCamera.getParameters();
				StatsManager.addToStatFile(StatsConstants.CAMERA_SETTINGS_CHANGE, "back camera");
			}

			CustomSurfaceView.setCamera(mCamera);
			setCameraParams();
			mCamera.setPreviewDisplay(mSurfaceHolder);
			startPreview();

			/*if(colorEffect != 5 && whiteBalance != null)	// SGS 2 has no effects in front camera mode 
				frontCameraParams.setWhiteBalance(whiteBalance.get(colorEffect));
			else
				changeWhiteBalanceMode(5, true);*/
			if(!changeWhiteBalanceMode(colorEffect, false))
			{
				colorEffect = 0;
				return 2;
			}

			mPreviewRunning = true;
			return 1;
		}

		catch (Exception e)
		{
			e.printStackTrace();
			((Activity) mContext).finish();
		}
		return 0; //why should we get here?
	}

	public boolean changeColorEffect(int colorMode, boolean showToast)
	{
		ArrayList<String> colorEffects = (ArrayList<String>) backCameraParams.getSupportedColorEffects();

		if (colorEffects.size() > 0)
		{
			frontCameraParams.setColorEffect(colorEffects.get(colorMode));
			backCameraParams.setColorEffect(colorEffects.get(colorMode));

			if( isFront)
				mCamera.setParameters(frontCameraParams);
			else
				mCamera.setParameters(backCameraParams);
			if(showToast)
				Toast.makeText(mContext, "Color effect: " + colorEffects.get(colorMode), Toast.LENGTH_SHORT).show();
			return true;
		}

		else
		{
			Toast.makeText(mContext, "there is no color support", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	// private int focusMode = 0;

	/*public void changeFocusMode(int focusMode, boolean showToast)
	{
		ArrayList<String> focusModes = (ArrayList<String>) mCameraParams.getSupportedFocusModes();

		if (focusModes.size() > 0)
		{
			mCameraParams.setFocusMode(focusModes.get(focusMode));

			mCamera.setParameters(mCameraParams);
			if(showToast)
				Toast.makeText(mContext, "Focus mode: " + focusModes.get(focusMode), Toast.LENGTH_SHORT).show();
		}

		else
		{
			Toast.makeText(mContext, "there is no focus support", Toast.LENGTH_SHORT).show();
		}
	}*/

	// Change color effects (sepia, black-white, negative, etc...)
	// private int whiteBalanceMode = 0;

	public boolean changeWhiteBalanceMode(int wbMode, boolean showToast)
	{
		//front and back cameras have different capabilities on different phones
		ArrayList<String> whiteBalance = null;
		if(!isFront){
			whiteBalance = (ArrayList<String>) backCameraParams.getSupportedWhiteBalance();
		}else{
			whiteBalance = (ArrayList<String>) frontCameraParams.getSupportedWhiteBalance();
		}
		
		//ArrayList<String> whiteBalance =(!isFront)?(ArrayList<String>) backCameraParams.getSupportedWhiteBalance():(ArrayList<String>) frontCameraParams.getSupportedWhiteBalance();

		if(whiteBalance == null)
			return false;

		if (wbMode == 5)
		{
			StatsManager.addToStatFile(StatsConstants.CAMERA_SETTINGS_CHANGE, "black & white image");
			bwImage = true;
			backCameraParams.setColorEffect("mono");
			frontCameraParams.setColorEffect("mono");
			if (isFront)
				mCamera.setParameters(frontCameraParams);
			else
				mCamera.setParameters(backCameraParams);
			if(showToast)
				Toast.makeText(mContext, "WB mode: Black & White", Toast.LENGTH_SHORT).show();

		}
		else if (whiteBalance.size() > 0)
		{
			StatsManager.addToStatFile(StatsConstants.CAMERA_SETTINGS_CHANGE, whiteBalance.get(wbMode) +" image");

			if(bwImage)
			{
				bwImage = false;
				backCameraParams.setColorEffect("none");
				frontCameraParams.setColorEffect("none");
			}

			if(wbMode > brightnessSettings.length)
				wbMode = 0;
			
			String modeType = brightnessSettings[wbMode];

			int modeTypeIndex = 0;
			
			for(int i=0; i < whiteBalance.size();i++){
				if(whiteBalance.get(i).equals(modeType))
					modeTypeIndex = i;
			}
			
			frontCameraParams.setWhiteBalance(whiteBalance.get(modeTypeIndex));
			backCameraParams.setWhiteBalance(whiteBalance.get(modeTypeIndex));

			if (isFront)
				mCamera.setParameters(frontCameraParams);
			else
				mCamera.setParameters(backCameraParams);
			if(showToast)
				Toast.makeText(mContext, "WB mode: " + brightnessSettings[wbMode], Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, "there is no color support", Toast.LENGTH_SHORT).show();
			return false;
		}
		colorEffect = wbMode;
		return true;
	}


	public boolean cameraHasFlash()
	{
		if (mCamera == null)
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

		ArrayList<String> flashModes = (ArrayList<String>) mCamera.getParameters().getSupportedFlashModes();

		if (flashModes == null || flashModes.size() == 0)
			return false;
		return true;
	}


	// Change flash mode (on, off etc..)
	// private int flashMode = 0;

	public boolean changeFlashMode(int flashMode, boolean showToast)
	{
		if (isFront)
		{
			Toast.makeText(mContext, "there is no flash in the front camera", Short.SIZE).show();
			return false;
		}

		ArrayList<String> flashModes = (ArrayList<String>) backCameraParams.getSupportedFlashModes();

		if (flashModes != null && flashModes.size() > 0)
		{
//			backCameraParams.setFlashMode(flashModes.get(flashMode));
			backCameraParams.setFlashMode(flashSettings[flashMode]);

			mCamera.setParameters(backCameraParams);
			StatsManager.addToStatFile(StatsConstants.CAMERA_SETTINGS_CHANGE,"Flash mode: " + flashModes.get(flashMode));

			if(showToast)
				Toast.makeText(mContext, "Flash mode: " + flashSettings[flashMode], Toast.LENGTH_SHORT).show();
		}

		else
		{
			Toast.makeText(mContext, "there is no flash support", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}



	/*
	 private int previewSize = 0;
	 private void changePreviewSize()
	{
		ArrayList<Camera.Size> previewSizes = (ArrayList<Camera.Size>) mCameraParams.getSupportedPreviewSizes();

		if (previewSizes.size() > 0)
		{
			if (previewSize == previewSizes.size() - 1)
			{
				previewSize = 0;
			}

			else
			{
				previewSize++;
			}

			mCameraParams.setPreviewSize(previewSizes.get(previewSize).width, previewSizes.get(previewSize).height);

			mCamera.setParameters(mCameraParams);

			Toast.makeText(mContext, "Preview size: " + previewSizes.get(previewSize).width + "X" + previewSizes.get(previewSize).height,
					Toast.LENGTH_SHORT).show();
		}

		else
		{
			Toast.makeText(mContext, "there is no preview sizes support", Toast.LENGTH_SHORT).show();
		}
	}*/

	public void resetCamera()
	{
		isFront = false;
		//setCameraParams();
		changeFlashMode(1,false);
		changeWhiteBalanceMode(0,false);
		changeColorEffect(0,false);
		toggleFlash(0);

	}
}