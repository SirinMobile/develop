package com.uiu.camera.orangeil.friendly;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.uiu.camera.orangeil.friendly.R;


public class AnimationList
{
	
	 Animation rotateAnim;
     
	//Seekbar small icons animations
	Animation seekbarIconsPortToLand;
	Animation seekbarIconsPortToLandInv;
	Animation seekbarIconsLandToPort;
	Animation seekbarIconsLandInvToPort;

	//preview Border animations
	Animation previewBorderPortToLand;
	Animation previewBorderPortToLandInv;
	Animation previewBorderLandToPort;
	Animation previewBorderLandInvToPort;

	//image preview animations
	Animation imagePreviewPortToLand;
	Animation imagePreviewPortToLandInv;
	Animation imagePreviewLandToPort;
	Animation imagePreviewLandInvToPort;

	//close button animation
	Animation closeButtonPortToLand;
	Animation closeButtonPortToLandInv;
	Animation closeButtonLandToPort;
	Animation closeButtonLandInvToPort;

	//settings button animation
	Animation settingsButtonPortToLand;
	Animation settingsButtonPortToLandInv;
	Animation settingsButtonLandToPort;
	Animation settingsButtonLandInvToPort;

	//flash Button animation
	Animation flashButtonPortToLand;
	Animation flashButtonPortToLandInv;
	Animation flashButtonLandToPort;
	Animation flashButtonLandInvToPort;

	//flash Button animation
	Animation lampButtonPortToLand;
	Animation lampButtonPortToLandInv;
	Animation lampButtonLandToPort;
	Animation lampButtonLandInvToPort;

	//wb Button animation
	Animation wbButtonPortToLand;
	Animation wbButtonPortToLandInv;
	Animation wbButtonLandToPort;
	Animation wbButtonLandInvToPort;

	//switch camera Button animation
	Animation switchCameraButtonPortToLand;
	Animation switchCameraButtonPortToLandInv;
	Animation switchCameraButtonLandToPort;
	Animation switchCameraButtonLandInvToPort;

	Animation listPortToLand;
	Animation listPortToLandInv;
	Animation listLandToPort;
	Animation listLandInvToPort;

	Animation actionButtonPortToLand;
	Animation actionButtonPortToLandInv;
	Animation actionButtonLandToPort;
	Animation actionButtonLandInvToPort;

	Animation recordCircleAnimationFadeOut;
	Animation cameraShutterAnimation;

	Animation shutterRightAnimation;
	Animation shutterLeftAnimation;
	Animation shutterRightCloseAnimation;
	Animation shutterLeftCloseAnimation;

	public AnimationList(Context context){

		rotateAnim = AnimationUtils.loadAnimation(context, R.anim.rotation);
		
		shutterRightAnimation = AnimationUtils.loadAnimation(context, R.anim.shutter_right_animation);
		shutterLeftAnimation = AnimationUtils.loadAnimation(context, R.anim.shutter_left_animation);
		shutterRightCloseAnimation = AnimationUtils.loadAnimation(context, R.anim.shutter_right_close);
		shutterLeftCloseAnimation = AnimationUtils.loadAnimation(context, R.anim.shutter_left_close);

		seekbarIconsPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		seekbarIconsPortToLandInv = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);
		seekbarIconsLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		seekbarIconsLandInvToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		previewBorderPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		previewBorderPortToLandInv  = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);
		previewBorderLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		previewBorderLandInvToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		imagePreviewPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		imagePreviewPortToLandInv  = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);
		imagePreviewLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		imagePreviewLandInvToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		closeButtonPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		closeButtonPortToLandInv = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);;
		closeButtonLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		closeButtonLandInvToPort =  AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		settingsButtonPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		settingsButtonPortToLandInv = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);;
		settingsButtonLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		settingsButtonLandInvToPort =  AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		flashButtonPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		flashButtonPortToLandInv = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);;
		flashButtonLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		flashButtonLandInvToPort =  AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		lampButtonPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		lampButtonPortToLandInv = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);;
		lampButtonLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		lampButtonLandInvToPort =  AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);
		
		wbButtonPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		wbButtonPortToLandInv = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);;
		wbButtonLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		wbButtonLandInvToPort =  AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		switchCameraButtonPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		switchCameraButtonPortToLandInv = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);;
		switchCameraButtonLandToPort = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		switchCameraButtonLandInvToPort  =  AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		listPortToLand = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		listPortToLandInv  = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);
		listLandToPort  = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		listLandInvToPort =  AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		actionButtonPortToLand =  AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
		actionButtonPortToLandInv  = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape_inv);;
		actionButtonLandToPort  = AnimationUtils.loadAnimation(context, R.anim.landscape_to_portrait);
		actionButtonLandInvToPort=  AnimationUtils.loadAnimation(context, R.anim.landscape_inv_to_portrait);

		recordCircleAnimationFadeOut = AnimationUtils.loadAnimation(context, R.anim.record_circle_fadeout);
		cameraShutterAnimation = AnimationUtils.loadAnimation(context, R.anim.portrait_to_landscape);
	}
}
