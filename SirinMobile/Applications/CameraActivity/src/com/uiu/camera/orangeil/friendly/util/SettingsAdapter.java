package com.uiu.camera.orangeil.friendly.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.uiu.camera.orangeil.friendly.CameraActivity;
import com.uiu.camera.orangeil.friendly.CameraApplication;
import com.uiu.camera.orangeil.friendly.R;
import com.uiu.camera.orangeil.friendly.RotateLayout;

public class SettingsAdapter extends ArrayAdapter<String>
{
	final static int FLASH_SETTINGS = 0;
	final static int WB_SETTINGS = 1;
	final static int SWITCH_CAMERA_SETTINGS = 2;

	public static final int[][] settingsDrawables = {
			{ R.drawable.flash_off_white, R.drawable.flash_auto_white, R.drawable.flash_on_white },
			{ R.drawable.wb_auto_white, R.drawable.wb_incandescent_white, R.drawable.wb_fluorescent_white, R.drawable.wb_daylight_white,
					R.drawable.wb_cloud_white, R.drawable.wb_bw_white }, { R.drawable.rear_white, R.drawable.front_white },
			{ R.drawable.lamp_off_white, R.drawable.lamp_on_white },
			{ R.drawable.wb_bw_white , R.drawable.wb_cloud_white ,R.drawable.wb_daylight_white ,R.drawable.wb_fluorescent_white ,
				R.drawable.wb_incandescent_white, R.drawable.wb_auto_white}
			};

	public static final int[][] settingsStrings = {
			{ R.string.camera_settings_flash_off, R.string.camera_settings_flash_auto, R.string.camera_settings_flash_on },
			{ R.string.camera_settings_wb_auto, R.string.camera_settings_wb_incandescent, R.string.camera_settings_wb_fluorescent,
					R.string.camera_settings_wb_daylight, R.string.camera_settings_wb_cloudy, R.string.camera_settings_wb_bw },
			{ R.string.camera_settings_cam_back, R.string.camera_settings_cam_front },
			{ R.string.camera_settings_lamp_off, R.string.camera_settings_lamp_on },
			{ R.string.camera_settings_wb_bw ,R.string.camera_settings_wb_cloudy,R.string.camera_settings_wb_daylight,
				 R.string.camera_settings_wb_fluorescent, R.string.camera_settings_wb_incandescent , R.string.camera_settings_wb_auto }};
	
	int settingsType;
	int selectedSetting;
	int orientation;
	Resources res;

	public SettingsAdapter(Context context, int resource, int type, int selectedSetting, int orientation)
	{
		super(context, resource);
		settingsType = type;
		this.selectedSetting = selectedSetting;
		this.orientation = orientation;
		res = context.getResources();
		// this.settingsType = settingType;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v =  vi.inflate(R.layout.settings_row_layout, null);
		}

		ImageView settingImage = (ImageView) v.findViewById(R.id.row_image);
		TextView settingsText = (TextView) v.findViewById(R.id.row_text);
		settingsText.setTypeface(CameraApplication.segoeui);
		ImageView selectedImage = (ImageView) v.findViewById(R.id.selected_image);
		FrameLayout itemBackground = (FrameLayout) v.findViewById(R.id.item_row);
		settingImage.setImageResource(settingsDrawables[settingsType][position]);
		ImageView divider = (ImageView) v.findViewById(R.id.divider);
		if ((position == settingsStrings[settingsType].length - 1 && orientation == CameraActivity.ORIENTATION_PORTRAIT_NORMAL))
		{
			divider.setVisibility(ImageView.GONE);
		}
		else
		{
			divider.setVisibility(ImageView.VISIBLE);
		}
		if (position == selectedSetting)
		{
			itemBackground.setBackgroundResource(android.R.color.black);
			itemBackground.getBackground().setAlpha(100);
			selectedImage.setVisibility(ImageView.VISIBLE);
		}
		else
		{
			itemBackground.setBackgroundResource(0);
			selectedImage.setVisibility(ImageView.INVISIBLE);
		}
		String settingsString = res.getString(settingsStrings[settingsType][position]);
		settingsText.setText(settingsString);

		MarginLayoutParams mlp = (MarginLayoutParams) settingImage.getLayoutParams();
		// 270 - landscape inverted
		// 90 - landscape normal
		switch (orientation)
		{
			case (CameraActivity.ORIENTATION_LANDSCAPE_INVERTED):
				mlp.setMargins(0, 0, 0, 0);
				settingImage.setLayoutParams(mlp);
				((RotateLayout) v).setOrientation(90);
				break;
			case (CameraActivity.ORIENTATION_LANDSCAPE_NORMAL):
				mlp.setMargins(40, 0, 0, 0);
				settingImage.setLayoutParams(mlp);
				((RotateLayout) v).setOrientation(270);
				break;

		}

		return v;
	}

	@Override
	public String getItem(int position)
	{
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

	@Override
	public int getCount()
	{
		return settingsStrings[settingsType].length;
	}

}
