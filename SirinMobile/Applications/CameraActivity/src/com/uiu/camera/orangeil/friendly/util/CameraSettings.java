package com.uiu.camera.orangeil.friendly.util;

public class CameraSettings
{
	final static int FLASH_AUTO = 0;
	final static int FLASH_ON = 1;
	final static int FLASH_OFF = 2;
	
	final static int WB_AUTO = 0;
	final static int WB_INCANDESCENT = 1;
	final static int WB_CLOUDY = 2;
	final static int WB_DAYLIGHT = 3;
	final static int WB_FLOURESCENT = 4;
	final static int WB_BW = 5;
	
	final static int CAMERA_REAR = 0;
	final static int CAMERA_FRONT = 1;
	
	int flash,wb,direction;
	
	CameraSettings mCameraSettings = null;
	
	private CameraSettings()
	{
		this.flash = FLASH_AUTO;
		this.wb = WB_AUTO;
		this.direction = CAMERA_REAR;
	}
	
	public CameraSettings getInstance()
	{
		if(mCameraSettings == null)
		{
			mCameraSettings = new CameraSettings();
		}
		
		return mCameraSettings;
	}


	public void setSettings(int flash, int wb, int direction)
	{
		this.flash = flash;
		this.wb = wb;
		this.direction = direction;
	}

	public int getFlash()
	{
		return flash;
	}

	public void setFlash(int flash)
	{
		this.flash = flash;
	}

	public int getWb()
	{
		return wb;
	}

	public void setWb(int wb)
	{
		this.wb = wb;
	}

	public int getDirection()
	{
		return direction;
	}

	public void setDirection(int direction)
	{
		this.direction = direction;
	}
	
	
}
