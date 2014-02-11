package com.uiu.camera.orangeil.friendly;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.uiu.camera.orangeil.friendly.R;
import com.uiu.camera.orangeil.friendly.ui.CustomSurfaceView;
import com.uiu.camera.orangeil.friendly.ui.ExpandCollapseAnimation;
import com.uiu.camera.orangeil.friendly.util.CameraFunctions;
import com.uiu.camera.orangeil.friendly.util.CameraSeekBar;
import com.uiu.camera.orangeil.friendly.util.MessageThreadObserver;
import com.uiu.camera.orangeil.friendly.util.SettingsAdapter;
import com.uiu.dispatcher.MessageDispatcher;
import com.uiu.stats.StatsConstants;
import com.uiu.stats.StatsManager;
import com.uiu.util.ImageOrientationCorrectionUtil;
import com.uiu.util.SoundManager;
import com.uiu.util.VibrationManager;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, OnClickListener
{

	private static final String TAG = CameraActivity.class.getSimpleName();
	public static final String VIDEO_LOCATION = Environment.getExternalStorageDirectory().getPath() + "/UIUSuit/UIUGallery/UIUCamera/Videos";
	private MessageThreadObserver mThreadObserver;
	boolean cameraPresnt;
	private ProgressDialog mDialogSavingImage;
	private Button mButtonResolution;
	private RelativeLayout mButtonExit, mButtonFlash, mButtonWhiteBlanace, mButtonFrontBackCamera, mButtonCloseSettings, mButtonOpenSettings,
	mButtonLamp;
	RelativeLayout recordingTimeBack, previewView, previewWindow;
	ImageView cameraButton, cameraImage, videoImage, previewPicView, flashImage, wbImage, frontBackImage, recordCircle, seekBarImage, shutterRight,
	shutterLeft, lampImage;
	private TextView exitText;
	private TextView settingsText;
	private ImageView settingsImage;
	private TextView recordingTime;
	private CameraSeekBar seekBar;
	private int padding;
	private Drawable recordThumb;
	private Drawable takePictureThumb;
	private Drawable stopRecordingThumb;
	private Drawable cameraButtonFeedBack;
	private boolean inSettingsMode, settingsMenuIsShowing, takingPicture;
	private static CustomSurfaceView mSurfaceView;
	private static SurfaceHolder mSurfaceHolder;
	public CameraFunctions funcs;
	public boolean cameraSettingsMenuVisible, cameraNeedsResetting;
	private HorizontalListView horizontalList;
	private ListView verticalList;
	private OrientationEventListener mOrientationEventListener;
	public static int mOrientation = -1;
	private AnimationList animations;
	private int SettingsType = -1;
	private int recordingSeconds = 0;
	private int recordingMinutes = 0;
	private final Handler mHandler = new Handler();
	private boolean mSurfaceCreated, inCameraMode;
	Vibrator vib;
	private final ThreadHandler handler = new ThreadHandler();
	private static int selectedFlash, selectedLamp, selectedWb, selectedCameraDir;
	private final int FLASH_SETTINGS = 0;
	private final int WB_SETTINGS = 1;
	private final int SWITCH_CAMERA_SETTINGS = 2;
	public static final int ORIENTATION_PORTRAIT_NORMAL = 1;
	public static final int ORIENTATION_PORTRAIT_INVERTED = 2;
	public static final int ORIENTATION_LANDSCAPE_NORMAL = 3;
	public static final int ORIENTATION_LANDSCAPE_INVERTED = 4;
	private static final Uri DBURI = Uri.parse("content://com.uiu.media.database.mediadatabase/media");
	private static final int[][] settingsDrawables = {
		{ R.drawable.flash_off, R.drawable.flash_autp, R.drawable.flash_on },
		{ R.drawable.wb_auto, R.drawable.wb_incandescent, R.drawable.wb_fluorescent, R.drawable.wb_daylight, R.drawable.wb_cloudy,
			R.drawable.wb_black_white }, { R.drawable.camera_rear, R.drawable.camera_front }, { R.drawable.lamp_off, R.drawable.lamp_on } };

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		// Activate StrictMode
		/*StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork() // or .detectAll() for all detectable problems
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog()
				.penaltyDeath().build());*/
		StatsManager.addToStatFile(StatsConstants.CAMERA_STARTED, "");
		doWindowOperation();
		setContentView(R.layout.camera);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR | ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		cameraPresnt = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		initComponents();
	}

	public void startGalleryIntent()
	{
		if(funcs.mIsRecording)
			startStopFilming();
		funcs.stopPreview();
		funcs.isFront = false;
		Intent intent = new Intent();
		StatsManager.addToStatFile(StatsConstants.CAMERA_STARTED_GALLERY, "");
		
		intent.setClassName(MessageDispatcher.GALLERY_PACKAGE, MessageDispatcher.GALLERY_PACKAGE + ".activities.PhotoGridActivity");
		intent.putExtra("fromCamera", true);
		startActivity(intent);
	}

	/**
	 * Set window flags
	 */
	private void doWindowOperation()
	{
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * Initialize components
	 */
	private void initComponents()
	{
		selectedFlash = 0;
		selectedLamp = 0;
		selectedWb = 0;
		selectedCameraDir = 0;

		if (mThreadObserver == null)
		{
			mThreadObserver = new MessageThreadObserver(getContentResolver(), getBaseContext(), DBURI, handler);
			mThreadObserver.startThreadMonitoring();
		}
		previewWindow = (RelativeLayout) findViewById(R.id.preview_window);
		inCameraMode = true;
		shutterRight = (ImageView) findViewById(R.id.shutter_right);
		shutterLeft = (ImageView) findViewById(R.id.shutter_left);
		animations = new AnimationList(getApplicationContext());
		vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		recordingTimeBack = (RelativeLayout) findViewById(R.id.recording_time_background);
		recordingTime = (TextView) findViewById(R.id.recording_time);
		recordCircle = (ImageView) findViewById(R.id.recording_circle);

		horizontalList = (HorizontalListView) findViewById(R.id.horizontal_settings_list);
		/* horizontalList.setPadding(40, 0, 40, 0); */
		horizontalList.setOnItemClickListener(settingsListListener);

		verticalList = (ListView) findViewById(R.id.vertical_settings_list);
		verticalList.setOnItemClickListener(settingsListListener);
		// verticalList.setPadding(40, 0, 40, 0);

		seekBarImage = (ImageView) findViewById(R.id.seekbar_image);
		exitText = (TextView) findViewById(R.id.exit_button_text);
		exitText.setTypeface(CameraApplication.seguisb);
		padding = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38, getResources().getDisplayMetrics()));
		stopRecordingThumb = getResources().getDrawable(R.drawable.stop_button_icon);
		stopRecordingThumb.setBounds(new Rect(0, stopRecordingThumb.getIntrinsicHeight(), stopRecordingThumb.getIntrinsicWidth(), 0));
		cameraButtonFeedBack = getResources().getDrawable(R.drawable.camera_button_feedback);
		cameraButtonFeedBack.setBounds(new Rect(0, cameraButtonFeedBack.getIntrinsicHeight(), cameraButtonFeedBack.getIntrinsicWidth(), 0));
		takePictureThumb = getResources().getDrawable(R.drawable.camera_button_icon);
		takePictureThumb.setBounds(new Rect(0, 0, takePictureThumb.getIntrinsicWidth(), takePictureThumb.getIntrinsicHeight()));
		recordThumb = getResources().getDrawable(R.drawable.red_camera_button);
		recordThumb.setBounds(new Rect(0, 0, recordThumb.getIntrinsicWidth(), recordThumb.getIntrinsicHeight()));
		seekBar = (CameraSeekBar) findViewById(R.id.seek_bar);
		mSurfaceView = (CustomSurfaceView) findViewById(R.id.camera_surface);
		// mButtonTakePicture = (Button) findViewById(R.id.shoot_photo_camera);
		mButtonFlash = (RelativeLayout) findViewById(R.id.flash_button);
		mButtonWhiteBlanace = (RelativeLayout) findViewById(R.id.brightness_button);
		mButtonFrontBackCamera = (RelativeLayout) findViewById(R.id.camera_direction_button);
		mButtonResolution = (Button) findViewById(R.id.camera_button_resolution);
		mButtonLamp = (RelativeLayout) findViewById(R.id.lamp_button);
		mButtonExit = (RelativeLayout) findViewById(R.id.exit_button);
		mButtonOpenSettings = (RelativeLayout) findViewById(R.id.open_settings_button);
		mButtonCloseSettings = (RelativeLayout) findViewById(R.id.close_settings_button);
		settingsText = (TextView) findViewById(R.id.settings_button_text);
		settingsText.setTypeface(CameraApplication.seguisb);
		settingsImage = (ImageView) findViewById(R.id.settings_button_image);
		cameraImage = (ImageView) findViewById(R.id.camera_image);
		lampImage = (ImageView) findViewById(R.id.lamp_button_image);
		videoImage = (ImageView) findViewById(R.id.video_image);
		mSurfaceCreated = false;
		takingPicture = false;
		previewView = (RelativeLayout) findViewById(R.id.preview_picture_layout);
		
		previewView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				VibrationManager.UIUVibrate(getApplicationContext());
				SoundManager.playSound(12);
				startGalleryIntent();
			}
		});
		previewPicView = (ImageView) findViewById(R.id.preview_picture);
		flashImage = (ImageView) findViewById(R.id.flash_button_image);
		wbImage = (ImageView) findViewById(R.id.brightness_button_image);
		frontBackImage = (ImageView) findViewById(R.id.camera_direction_image);

		mButtonFrontBackCamera.setOnClickListener(this);
		mButtonWhiteBlanace.setOnClickListener(this);
		mButtonFlash.setOnClickListener(this);
		mButtonFrontBackCamera.setOnClickListener(this);
		mButtonLamp.setOnClickListener(this);
		mButtonFlash.setOnClickListener(this);
		mButtonExit.setOnClickListener(this);
		mButtonOpenSettings.setOnClickListener(this);
		mButtonCloseSettings.setOnClickListener(this);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceView.setSystemUiVisibility(1);
		//mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		funcs = new CameraFunctions(CameraActivity.this, mSurfaceView, mSurfaceHolder);
		inSettingsMode = false;
		seekBar.setOnSeekBarChangeListener(mSeekBarListener);

		animations.shutterRightCloseAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation)
			{

			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				shutterRight.startAnimation(animations.shutterRightAnimation);

			}
		});
		animations.shutterLeftCloseAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation)
			{

			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{

			}

			@Override
			public void onAnimationEnd(Animation animation)
			{
				shutterLeft.startAnimation(animations.shutterLeftAnimation);

			}
		});

	}

	int seekBarlocation = 0;
	boolean stopFilming = true;
	OnSeekBarChangeListener mSeekBarListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar)
		{
			seekBarlocation = seekBar.getProgress();
			if (changedPosition)
			{
				if (seekBarlocation > seekBar.getMax() / 2)
				{
					if (!funcs.mIsRecording)
						seekBarImage.setImageDrawable(recordThumb);
					if(inSettingsMode && funcs.cameraHasFlash())
						mButtonLamp.setVisibility(RelativeLayout.VISIBLE);
					seekBar.setPadding(padding, 0, padding, 0);
					seekBar.setProgress(seekBar.getMax());

					inCameraMode = false;
				}
				else
				{
					if(inSettingsMode && funcs.cameraHasFlash())
					{
						mButtonLamp.setVisibility(RelativeLayout.INVISIBLE);
						mButtonFlash.setVisibility(RelativeLayout.VISIBLE);
					}
					seekBarImage.setImageDrawable(takePictureThumb);
					seekBar.setPadding(padding, 0, padding, 0);
					seekBar.setProgress(0);
					inCameraMode = true;
					if (funcs.torchIsOn)
					{
						funcs.toggleFlash(0);
						lampImage.setImageResource(R.drawable.lamp_off);
					}
				}

				changedPosition = false;
			}
			else
			{
				if (seekBarlocation <= 75)
				{
					VibrationManager.UIUVibrate(getApplicationContext());
					seekBar.setProgress(0);
					seekBarImage.setImageDrawable(cameraButtonFeedBack);
					takePicture();
					seekBar.setOnSeekBarChangeListener(null);

				}
				else if (seekBarlocation > 75)
				{
					seekBar.setProgress(seekBar.getMax());
					if (!funcs.mIsRecording)
					{
						seekBarImage.setImageDrawable(stopRecordingThumb);
					}
					else if (stopFilming)
					{
						seekBarImage.setImageDrawable(recordThumb);
					}
					// seekBar.setProgress(seekBar.getMax() - 1);
					if (stopFilming)
					{
						VibrationManager.UIUVibrate(getApplicationContext());
						startStopFilming();
					}
				}

			}
			Log.v(TAG, "stopped Tracking touch at: " + seekBar.getProgress());

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar)
		{

			Log.v(TAG, "started Tracking touch at: " + seekBar.getProgress());
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
		{
			if (funcs.mIsRecording && progress < 80)
			{
				seekBar.setProgress(seekBar.getMax());
				stopFilming = false;
				return;
			}
			else
			{
				stopFilming = true;
			}
			MarginLayoutParams params = (MarginLayoutParams) seekBarImage.getLayoutParams();

			if (Math.abs(seekBarlocation - progress) > 50)
			{
				changedPosition = true;
				params.setMargins(progress, 25, 0, 0);
			}
			else
			{
				if (seekBarlocation <= seekBar.getMax() / 2)
				{
					seekBarlocation = 0;
				}
				else
				{
					seekBarlocation = seekBar.getMax();
				}

				params.setMargins(seekBarlocation, 25, 0, 0);
			}
			seekBarImage.setLayoutParams(params);
			// Sets the margins of the imageview, making it move to a specific location on the screen

			Log.v(TAG, "progress changed at: " + progress + " from user: " + fromUser);

		}
	};

	@Override
	protected void onStart()
	{
		populatePreview(false);
		cameraSettingsMenuVisible = false;

		super.onStart();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run()
			{
				Animation animRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shutter_right_animation);
				shutterRight.startAnimation(animRight);
				Animation animLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shutter_left_animation);
				shutterLeft.startAnimation(animLeft);

			}
		}, 700);

	}

	static boolean changedPosition = false;

	@Override
	public void onResume()
	{
		super.onResume();

		cameraNeedsResetting = true;
		if (mOrientationEventListener == null)
		{
			mOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {

				@Override
				public void onOrientationChanged(int orientation)
				{

					// determine our orientation based on sensor response
					int lastOrientation = mOrientation;

					if ((orientation >= 315 || orientation < 45) && orientation != -1)
					{
						if (mOrientation != ORIENTATION_PORTRAIT_NORMAL)
						{

							mOrientation = ORIENTATION_PORTRAIT_NORMAL;
						}
					}
					else if (orientation < 315 && orientation >= 225)
					{
						if (mOrientation != ORIENTATION_LANDSCAPE_NORMAL)
						{
							if (mOrientation == -1)
								rotatePortraitToLandscape();
							mOrientation = ORIENTATION_LANDSCAPE_NORMAL;
						}
					}
					else if (orientation < 225 && orientation >= 135)
					{
						if (mOrientation != ORIENTATION_PORTRAIT_INVERTED)
						{
							 mOrientation = ORIENTATION_PORTRAIT_INVERTED;
						}
					}
					else
					{ // orientation <135 && orientation > 45
						if (mOrientation != ORIENTATION_LANDSCAPE_INVERTED)
						{
							if (mOrientation == -1)
								rotatePortraitToLandscapeInv();
							else if (orientation == -1)
								return;
							mOrientation = ORIENTATION_LANDSCAPE_INVERTED;
						}
					}

					if (lastOrientation == ORIENTATION_PORTRAIT_NORMAL && mOrientation == ORIENTATION_LANDSCAPE_INVERTED)
					{
						rotatePortraitToLandscapeInv();
					}

					if (lastOrientation == ORIENTATION_PORTRAIT_NORMAL && mOrientation == ORIENTATION_LANDSCAPE_NORMAL)
					{
						rotatePortraitToLandscape();
					}
					if (lastOrientation == ORIENTATION_LANDSCAPE_NORMAL && mOrientation == ORIENTATION_PORTRAIT_NORMAL)
					{
						rotateLandscapeToPortrait();
					}
					if (lastOrientation == ORIENTATION_LANDSCAPE_INVERTED && mOrientation == ORIENTATION_PORTRAIT_NORMAL)
					{
						rotateLandscapeInvToPortrait();
					}
				}
			};
		}
		if (mOrientationEventListener.canDetectOrientation())
		{
			mOrientationEventListener.enable();
		}

		Log.i(TAG, "onResume");
	}

	@Override
	public void onPause()
	{

		mOrientationEventListener.disable();
		/*
		 * if (funcs.mCamera != null) { funcs.stopPreview(); } surfaceDestroyed(mSurfaceHolder);
		 */
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onStop()
	{
		if (funcs.mIsRecording)
		{
			startStopFilming();
		}

		super.onStop();

		/*
		 * if (funcs.mCamera != null) { funcs.stopPreview(); }
		 */

		Log.i(TAG, "onStop");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (mThreadObserver != null)
			mThreadObserver.stopThreadMonitoring();
		StatsManager.addToStatFile(StatsConstants.CAMERA_EXIT, "");

		Log.i(TAG, "onDestroy");
	}

	public void setSettingsAdapter(int type)
	{
		SettingsType = type;
		SettingsAdapter s = null;
		RelativeLayout.LayoutParams lp = null;
		switch (type)
		{
		case 0:
			lp = new RelativeLayout.LayoutParams(450, 650);
			lp.addRule(RelativeLayout.BELOW, R.id.top_row);
			lp.setMargins(10, -15, 0, 0);
			s = new SettingsAdapter(this, R.layout.settings_row_layout, type, selectedFlash, mOrientation);
			break;
		case 1:
			lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 650);
			lp.addRule(RelativeLayout.BELOW, R.id.top_row);
			lp.setMargins(10, -15, 0, 0);
			if(mOrientation == ORIENTATION_LANDSCAPE_NORMAL)
				s = new SettingsAdapter(this, R.layout.settings_row_layout, 4, SettingsAdapter.settingsDrawables[1].length-1-selectedWb, mOrientation);
			else
				s = new SettingsAdapter(this, R.layout.settings_row_layout, type, selectedWb, mOrientation);
			break;
		case 2:
			lp = new RelativeLayout.LayoutParams(350, 650);
			lp.addRule(RelativeLayout.BELOW, R.id.top_row);
			lp.setMargins(0, -15, 120, 0);
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

			s = new SettingsAdapter(this, R.layout.settings_row_layout, type, selectedCameraDir,
					mOrientation);
			break;
		case 3:
			lp = new RelativeLayout.LayoutParams(450, 650);
			lp.addRule(RelativeLayout.BELOW, R.id.top_row);
			lp.setMargins(10, -15, 0, 0);
			s = new SettingsAdapter(this, R.layout.settings_row_layout, type, selectedLamp, mOrientation);
			break;
		}

		horizontalList.setLayoutParams(lp);
		verticalList.setAdapter(s);
		horizontalList.setAdapter(s);
		if(mOrientation == ORIENTATION_LANDSCAPE_NORMAL && selectedWb == 0)
			horizontalList.scrollTo(horizontalList.getWidth());

	}

	OnItemClickListener settingsListListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			SoundManager.playSound(12);

			VibrationManager.UIUVibrate(getApplicationContext());

			switch (SettingsType)
			{
			case 0:
				if (funcs.changeFlashMode(position, true))
				{
					flashImage.setImageResource(settingsDrawables[SettingsType][position]);
					selectedFlash = position;
				}
				break;
			case 1:
				if(mOrientation == ORIENTATION_LANDSCAPE_NORMAL)
					position = settingsDrawables[SettingsType].length - 1 - position;
				if (funcs.changeWhiteBalanceMode(position, true))
				{
					wbImage.setImageResource(settingsDrawables[SettingsType][position]);
					selectedWb = position;
				}
				break;
			case 2:
				int res = funcs.switchCamera(position);
				if ( res == 1 )	//switch and effect change was successful  
				{
					frontBackImage.setImageResource(settingsDrawables[SettingsType][position]);
					selectedCameraDir = position;
				}
				else if(res == 2 )	//only switch was successful
				{
					frontBackImage.setImageResource(settingsDrawables[SettingsType][position]);
					selectedCameraDir = position;
					selectedWb = 0;
					wbImage.setImageResource(settingsDrawables[1][0]);
				}
				break;
			case 3:
				if (funcs.toggleFlash(position))
				{
					lampImage.setImageResource(settingsDrawables[SettingsType][position]);
					selectedLamp = position;
				}
				break;
			}

			verticalList.setVisibility(ListView.INVISIBLE);
			horizontalList.setVisibility(ListView.INVISIBLE);
			cameraSettingsMenuVisible = false;
		}

	};

	public void populatePreview(boolean doAnimation)
	{

		String[] projection = { "path", "time", "source_created" };
		String[] selectionArgs = { "device" };
		final Cursor cursor = getContentResolver().query(DBURI, projection, "source_created = ?", selectionArgs, "time DESC LIMIT 2");

		if (cursor == null)
			return;

		if (cursor.moveToFirst())
		{

			final RelativeLayout image0RL = (RelativeLayout)findViewById(R.id.first_preview_picture_layout);
			final RelativeLayout image1RL = (RelativeLayout)findViewById(R.id.preview_picture_layout);
			final RelativeLayout image2RL = (RelativeLayout)findViewById(R.id.second_preview_picture_layout);
			final ImageView imageView2 = (ImageView) findViewById(R.id.second_preview_picture);
			final ImageView imageView = (ImageView) findViewById(R.id.preview_picture);
			final ImageView imageView0 = (ImageView) findViewById(R.id.first_preview_picture);
			Bitmap tempImage = null;

			String imageLocation = cursor.getString(0);
			File mediaFile = new File(imageLocation);
			final Bitmap newImage = setImage(mediaFile, imageView0, imageLocation);
			if (cursor.moveToNext())
			{
				imageLocation = cursor.getString(0);
				mediaFile = new File(imageLocation);
				tempImage = setImage(mediaFile, null, imageLocation);
			}

			final Bitmap oldImage = tempImage; 
			Animation r = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.preview_translate_animation);
			r.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					imageView2.setImageBitmap(oldImage);
					imageView.setImageBitmap(newImage);

				}
			});
			if(doAnimation)
			{
				image0RL.startAnimation(r);
				image1RL.startAnimation(r);
				image2RL.startAnimation(r);
			}
			else
			{
				imageView2.setImageBitmap(oldImage);
				imageView.setImageBitmap(newImage);
			}

		}

		cursor.close();
	}


	public Bitmap setImage(File mediaFile, ImageView imageView, String imageLocation)
	{
		Bitmap preview_bitmap = null;
		if (mediaFile.exists())
		{

			if (isImageFile(mediaFile))
			{
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 8;
				preview_bitmap = ImageOrientationCorrectionUtil.getBitmapRotatedToNormalOrientation(imageLocation);
				//preview_bitmap = BitmapFactory.decodeFile(imageLocation, options);

			}
			else if (isVideoFile(mediaFile))
			{
				preview_bitmap = ThumbnailUtils.createVideoThumbnail(mediaFile.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
			}

			if (preview_bitmap != null && imageView != null)
			{
				imageView.setImageBitmap(preview_bitmap);
			}
		}
		return preview_bitmap;
	}

	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) { //setContentView(R.layout.camera); super.onConfigurationChanged(newConfig);
	 * 
	 * };
	 */
	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(final byte[] imageData, Camera c)
		{

			final int orientation = mOrientation;
			int rotation = 0;
			if (Build.MANUFACTURER.contains("samsung") )
			{
				rotation = 6;

				if (orientation == ORIENTATION_LANDSCAPE_NORMAL)
					rotation = 1;
				else if (orientation == ORIENTATION_LANDSCAPE_INVERTED)
					rotation = 3;
				else if (orientation == ORIENTATION_PORTRAIT_INVERTED)
					rotation = 8;

			}
			else
			{
				rotation = 1;

				if (orientation == ORIENTATION_LANDSCAPE_NORMAL)
					rotation = 8;
				else if (orientation == ORIENTATION_LANDSCAPE_INVERTED)
					rotation = 6;
				else if (orientation == ORIENTATION_PORTRAIT_INVERTED)
					rotation = 3;
				else if (funcs.isFront && rotation == 3)
				{
					rotation = 1;
				}
				else if (funcs.isFront && rotation == 1)
				{
					rotation = 3;
				}
			}

			if (funcs.isFront && rotation == 8 && !Build.MANUFACTURER.contains("TCT"))
			{
				rotation = 6;
			}
			else if (funcs.isFront && rotation == 6 && !Build.MANUFACTURER.contains("TCT"))
			{
				rotation = 8;
			}

			if (Build.MANUFACTURER.contains("lge"))
			{
				if(funcs.isFront && rotation == 8 )
				{
					rotation = 6;
				}
				else if(funcs.isFront && rotation == 6)
				{
					rotation = 8;
				}

			}
			final int finalRotation = rotation;

			if (imageData != null)
			{


				Thread t = new Thread(new Runnable() {

					@Override
					public void run()
					{
						funcs.StoreByteImage(CameraActivity.this, imageData, finalRotation);

					}
				});
				t.start();

				funcs.retake();
				takingPicture = false;
				seekBar.setOnSeekBarChangeListener(mSeekBarListener);
			}

		}
	};

	/**
	 * Surface first created
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.v(TAG, "Surface Created");
		mSurfaceCreated = true;
	}

	/**
	 * Surface changed
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		Log.v(TAG, "Surface Changed");
		funcs.changeSurface(holder, width, height);
        mSurfaceView.requestLayout();
		if (cameraNeedsResetting && funcs.backCameraParams != null)
		{
			resetCamera();
			cameraNeedsResetting = false;
		}
        /*mCamera.setParameters(parameters);
        mCamera.startPreview();*/
		
	}

	/**
	 * Surface destroyed
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0)
	{
		Log.v(TAG, "Surface Destroyed");
		mSurfaceCreated = false;
		funcs.destroySurface();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		
        if (((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)) && inCameraMode){
        	takePicture();                   
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if (cameraSettingsMenuVisible)
    		{
    			verticalList.setVisibility(ListView.INVISIBLE);
    			horizontalList.setVisibility(ListView.INVISIBLE);
    			previewWindow.setVisibility(RelativeLayout.VISIBLE);
    			cameraSettingsMenuVisible = false;
    			return true;
    		}
    		if (funcs.mIsRecording)	//if we are filming we don't want to quit
    		{
    			return true;
    		}
	        finish();
	        return true;
	    }
       return true;
    }
	
	public void takePicture()
	{
		if (mSurfaceCreated )
		{
			Log.i(TAG, "takePicture");
			if (!funcs.sdHasEnoughSpace())
				Toast.makeText(getApplicationContext(), getString(R.string.sd_low_space_text), Toast.LENGTH_SHORT).show();
			//	funcs.backCameraParams = funcs.mCamera.getParameters();
			//	funcs.frontCameraParams = funcs.mCamera.getParameters();
			else if (!takingPicture)
			{
				takingPicture = true;
				funcs.mCamera.autoFocus(mAutoFocusCallback);
			}
		}
	}

	Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
		Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
			@Override
			public void onShutter()
			{
				handler.post(new Runnable() {
					@Override
					public void run() {
						shutterRight.startAnimation(animations.shutterRightCloseAnimation);
						shutterLeft.startAnimation(animations.shutterLeftCloseAnimation);
					}
				});
			}
		};

		@Override
		public void onAutoFocus(boolean success, Camera camera)
		{
			if (success || funcs.isFront)
			{
				StatsManager.addToStatFile(StatsConstants.CAMERA_PICTURE_TAKEN, "");
				camera.takePicture(shutterCallback, mPictureCallback, mPictureCallback);

			}
			else
			{
				Toast.makeText(getBaseContext(), getString(R.string.camera_could_not_focus), Toast.LENGTH_SHORT).show();
				seekBar.setOnSeekBarChangeListener(mSeekBarListener);
				takingPicture = false;
				/* takePicture(); */
			}
			seekBarImage.setImageDrawable(takePictureThumb);
		}
	};

	public void startStopFilming()
	{
		Log.i(TAG, "startStopFilming");
		
		if (funcs.mIsRecording)
		{
			StatsManager.addToStatFile(StatsConstants.CAMERA_VIDEO_FILMED, "");
			settingsImage.setAlpha(1f);
			mButtonOpenSettings.setOnClickListener(this);
			mHandler.removeCallbacks(updateRecordingTime);
			recordingTimeBack.setVisibility(RelativeLayout.INVISIBLE);
			recordCircle.clearAnimation();

			recordingMinutes = 0;
			recordingSeconds = 0;

			Log.v(TAG, "Stopping camera");
			try
			{
				// stop the recording
				// funcs.mMediaRecoder.release();
				funcs.mMediaRecoder.stop();
				Log.v(TAG, "STOPPED");
			}
			catch (Exception e)
			{
				e.printStackTrace();
				Log.e(TAG, "ERROR stopping Camera");
				funcs.releaseMediaRecorder();
			}

			Log.v(TAG, "Camera stopped");

			// release the MediaRecorder object
			funcs.releaseMediaRecorder();

			Toast.makeText(CameraActivity.this, getString(R.string.camera_video_saved_to) + VIDEO_LOCATION, Toast.LENGTH_LONG).show();
			

			funcs.stopFilming();
			
			//send broadcast to the file manager - when taking picture
			Intent fileManagerSync = new Intent();
			fileManagerSync.setAction(CameraFunctions.ACTION_ACTIVATE_FILE_MANAGER_SYNC);
			sendBroadcast(fileManagerSync);
		}

		else
		{
			settingsImage.setAlpha(100f);
			mButtonOpenSettings.setOnClickListener(null);
			recordCircle.startAnimation(animations.recordCircleAnimationFadeOut);
			recordingTimeBack.setVisibility(RelativeLayout.VISIBLE);
			mHandler.post(updateRecordingTime);
			funcs.startMediaRecording();
			// mButtonMediaRecording.setText("stop record");
		}
		seekBar.setOnSeekBarChangeListener(mSeekBarListener);
	}

	/**
	 * Buttons OnClick handling
	 */
	@Override
	public void onClick(View v)
	{
		SoundManager.playSound(12);
		VibrationManager.UIUVibrate(getApplicationContext());
		//	RelativeLayout bottomRow = (RelativeLayout) findViewById(R.id.bottom_row);
		switch (v.getId())
		{

		case R.id.exit_button:
			if (funcs.mIsRecording)
				startStopFilming();
			
			funcs.toggleFlash(0);
			
			finish();
			break;
		case R.id.open_settings_button:
			inSettingsMode = true;
			//settingsText.setBackgroundColor(Color.TRANSPARENT);
			mButtonOpenSettings.setVisibility(RelativeLayout.INVISIBLE);
			mButtonCloseSettings.setVisibility(RelativeLayout.VISIBLE);
			mButtonExit.setVisibility(RelativeLayout.INVISIBLE);
			settingsText.setGravity(RelativeLayout.CENTER_IN_PARENT);
			mButtonFrontBackCamera.setVisibility(ImageButton.VISIBLE);
			mButtonWhiteBlanace.setVisibility(ImageButton.VISIBLE);
			if(funcs.cameraHasFlash())
			{
				if (inCameraMode)
				{
					mButtonFlash.setVisibility(RelativeLayout.VISIBLE);
				}
				else	// video recorder specific settings
				{
					mButtonLamp.setVisibility(RelativeLayout.VISIBLE);
				}
			}
			break;
		case R.id.close_settings_button:
			inSettingsMode = false;
			mButtonCloseSettings.setVisibility(RelativeLayout.INVISIBLE);
			mButtonOpenSettings.setVisibility(RelativeLayout.VISIBLE);
			mButtonFlash.setVisibility(RelativeLayout.INVISIBLE);
			mButtonFrontBackCamera.setVisibility(ImageButton.INVISIBLE);
			mButtonWhiteBlanace.setVisibility(ImageButton.INVISIBLE);
			mButtonLamp.setVisibility(RelativeLayout.INVISIBLE);
			mButtonExit.setVisibility(RelativeLayout.VISIBLE);
			if (cameraSettingsMenuVisible)
			{
				horizontalList.setVisibility(ListView.INVISIBLE);
				verticalList.setVisibility(ListView.INVISIBLE);
				cameraSettingsMenuVisible = false;
			}
			//previewWindow.setVisibility(RelativeLayout.VISIBLE);
			//bottomRow.setVisibility(RelativeLayout.VISIBLE);
			break;
		case R.id.flash_button:
			horizontalList.setBackgroundResource(R.drawable.drop_down_flash_landscape);
			verticalList.setBackgroundResource(R.drawable.drop_down_flash);
			setSettingsAdapter(0);
			verticalList.setCacheColorHint(Color.TRANSPARENT);
			startListAnimation();
			cameraSettingsMenuVisible = true;
			break;
		case R.id.brightness_button:
			horizontalList.setBackgroundResource(R.drawable.drop_down_wb);
			verticalList.setBackgroundResource(R.drawable.drop_down_wb);
			setSettingsAdapter(1);
			verticalList.setCacheColorHint(Color.TRANSPARENT);
			startListAnimation();
			cameraSettingsMenuVisible = true;
			break;
		case R.id.camera_direction_button:
			horizontalList.setBackgroundResource(R.drawable.drop_down_switch_landscape);
			verticalList.setBackgroundResource(R.drawable.drop_down_switch);
			setSettingsAdapter(2);
			verticalList.setCacheColorHint(Color.TRANSPARENT);
			startListAnimation();
			cameraSettingsMenuVisible = true;
			break;

		case R.id.lamp_button:
			horizontalList.setBackgroundResource(R.drawable.drop_down_flash_landscape);
			verticalList.setBackgroundResource(R.drawable.drop_down_flash);
			setSettingsAdapter(3);
			verticalList.setCacheColorHint(Color.TRANSPARENT);
			startListAnimation();
			cameraSettingsMenuVisible = true;
			break;

		}
	}

	public void startListAnimation()
	{

		ExpandCollapseAnimation a;
		if(settingsMenuIsShowing)
		{
			a = new ExpandCollapseAnimation(verticalList,250, 1);
		}
		else
		{
			a = new ExpandCollapseAnimation(verticalList, 250, 0);

			if (mOrientation == ORIENTATION_PORTRAIT_INVERTED || mOrientation == ORIENTATION_PORTRAIT_NORMAL || mOrientation == -1)
			{
				verticalList.setVisibility(ListView.VISIBLE);
				verticalList.startAnimation(a);

			}
			else
			{
				horizontalList.setVisibility(ListView.VISIBLE);
			}
		}
	}

	Runnable updateRecordingTime = new Runnable() {

		@Override
		public void run()
		{
			if (recordingSeconds > 10 && recordingMinutes > 10)
				recordingTime.setText(recordingMinutes + ":" + recordingSeconds);
			else if (recordingSeconds > 10 && recordingMinutes < 10)
				recordingTime.setText("0" + recordingMinutes + ":" + recordingSeconds);
			else if (recordingSeconds < 10 && recordingMinutes > 10)
				recordingTime.setText(recordingMinutes + ":" + "0" + recordingSeconds);
			else if (recordingSeconds < 10 && recordingMinutes < 10)
				recordingTime.setText("0" + recordingMinutes + ":" + "0" + recordingSeconds);

			recordingSeconds++;
			if (recordingSeconds == 60)
			{
				recordingSeconds = 0;
				recordingMinutes++;
			}
			mHandler.postDelayed(this, 1000);
		}
	};

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		if (cameraSettingsMenuVisible)
		{
			RelativeLayout bottomRow = (RelativeLayout) findViewById(R.id.bottom_row);
			startListAnimation();
			//	verticalList.setVisibility(ListView.INVISIBLE);
			horizontalList.setVisibility(ListView.INVISIBLE);
			previewWindow.setVisibility(RelativeLayout.VISIBLE);
			// previewPicView.setVisibility(RelativeLayout.VISIBLE);
			bottomRow.setVisibility(RelativeLayout.VISIBLE);
			cameraSettingsMenuVisible = false;
			return;
		}
		if (funcs.mIsRecording)
		{
			return;
		}
		
	}

	
	public void resetCamera()
	{
		funcs.resetCamera();
		selectedCameraDir = 0;
		selectedFlash = 1;
		selectedLamp = 0;
		selectedWb = 0;
		lampImage.setImageResource(settingsDrawables[3][0]);
		flashImage.setImageResource(settingsDrawables[0][1]);
		wbImage.setImageResource(settingsDrawables[1][0]);
		frontBackImage.setImageResource(settingsDrawables[2][0]);
	}

	private boolean isVideoFile(File file)
	{
		String fileName = file.toString();
		fileName = fileName.toLowerCase();
		if (fileName.endsWith("mpeg") || fileName.endsWith("mpg") || fileName.endsWith("avi") || fileName.endsWith("mp4") || fileName.endsWith("3gp"))
		{
			return true;
		}
		return false;
	}

	private boolean isImageFile(File file)
	{
		String fileName = file.toString();
		fileName = fileName.toLowerCase();
		if (fileName.endsWith("jpeg") || fileName.endsWith("jpg"))
		{
			return true;
		}
		return false;
	}

	public void rotatePortraitToLandscape()
	{
		if (cameraSettingsMenuVisible)
		{
			verticalList.setVisibility(ListView.INVISIBLE);
			setSettingsAdapter(SettingsType);
			horizontalList.setVisibility(ListView.VISIBLE);
		}
		recordingTimeBack.setPivotX((recordingTimeBack.getHeight()/4)*3);
		recordingTimeBack.setPivotY((recordingTimeBack.getHeight()/4)*3);
		recordingTimeBack.animate().rotation(90);
		seekBarImage.animate().rotation(90);
		videoImage.setPivotX(17);
		videoImage.setPivotY(9);
		videoImage.animate().rotation(90);
		settingsImage.animate().rotation(90);
		frontBackImage.animate().rotation(90);
		wbImage.animate().rotation(90);
		flashImage.animate().rotation(90);
		lampImage.animate().rotation(90);
		exitText.animate().rotation(90);
		settingsText.animate().rotation(90);
		cameraImage.animate().rotation(90);
		previewPicView.animate().rotation(90);
	}

	public void rotatePortraitToLandscapeInv()
	{
		if (cameraSettingsMenuVisible)
		{
			verticalList.setVisibility(ListView.INVISIBLE);
			setSettingsAdapter(SettingsType);
			horizontalList.setVisibility(ListView.VISIBLE);
		}
		recordingTimeBack.setPivotX((recordingTimeBack.getWidth()/4)*3);
		recordingTimeBack.setPivotY((recordingTimeBack.getWidth()/4));
		recordingTimeBack.animate().rotation(-90);
		seekBarImage.animate().rotation(-90);
		videoImage.animate().rotation(-90);
		settingsImage.animate().rotation(-90);
		frontBackImage.animate().rotation(-90);
		wbImage.animate().rotation(-90);
		flashImage.animate().rotation(-90);
		lampImage.animate().rotation(-90);
		exitText.animate().rotation(-90);
		settingsText.animate().rotation(-90);
		cameraImage.animate().rotation(-90);
		previewPicView.animate().rotation(-90);
	}

	public void rotateLandscapeToPortrait()
	{
		if (cameraSettingsMenuVisible)
		{
			horizontalList.setVisibility(ListView.INVISIBLE);
			setSettingsAdapter(SettingsType);
			verticalList.setVisibility(ListView.VISIBLE);
		}
		recordingTimeBack.animate().rotation(0);
		seekBarImage.animate().rotation(0);
		videoImage.animate().rotation(0);
		settingsImage.animate().rotation(0);
		frontBackImage.animate().rotation(0);
		wbImage.animate().rotation(0);
		flashImage.animate().rotation(0);
		lampImage.animate().rotation(0);
		exitText.animate().rotation(0);
		settingsText.animate().rotation(0);
		cameraImage.animate().rotation(0);
		previewPicView.animate().rotation(0);
	}

	public void rotateLandscapeInvToPortrait()
	{
		if (cameraSettingsMenuVisible)
		{
			horizontalList.setVisibility(ListView.INVISIBLE);
			setSettingsAdapter(SettingsType);
			verticalList.setVisibility(ListView.VISIBLE);
		}
		recordingTimeBack.animate().rotation(0);
		seekBarImage.animate().rotation(0);
		videoImage.animate().rotation(0);
		settingsImage.animate().rotation(0);
		frontBackImage.animate().rotation(0);
		wbImage.animate().rotation(0);
		flashImage.animate().rotation(0);
		lampImage.animate().rotation(0);
		exitText.animate().rotation(0);
		settingsText.animate().rotation(0);
		cameraImage.animate().rotation(0);
		previewPicView.animate().rotation(0);
	}

	class ThreadHandler extends Handler
	{
		public void handleMessage(final Message msg)
		{
			Log.v(TAG, "ThreadMonitor :: GOT A NEW Picture");
			if(!funcs.mIsRecording)
				populatePreview(true);
			// here we need to populate the preview window

		}
	}

}