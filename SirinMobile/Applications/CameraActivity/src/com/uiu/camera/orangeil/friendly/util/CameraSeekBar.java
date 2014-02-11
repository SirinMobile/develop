package com.uiu.camera.orangeil.friendly.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.SeekBar;

public class CameraSeekBar extends SeekBar
{
	public CameraSeekBar(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CameraSeekBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public CameraSeekBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l)
	{
		// TODO Auto-generated method stub
		super.setOnSeekBarChangeListener(l);
	}

	@Override
	public void setThumb(Drawable thumb)
	{
		// TODO Auto-generated method stub
		super.setThumb(thumb);
	}

	@Override
	public int getThumbOffset()
	{
		
		// TODO Auto-generated method stub
		return super.getThumbOffset();
	}

	@Override
	public void setThumbOffset(int thumbOffset)
	{
		// TODO Auto-generated method stub
		super.setThumbOffset(thumbOffset);
	}

	@Override
	public void setKeyProgressIncrement(int increment)
	{
		// TODO Auto-generated method stub
		super.setKeyProgressIncrement(increment);
	}

	@Override
	public int getKeyProgressIncrement()
	{
		// TODO Auto-generated method stub
		return super.getKeyProgressIncrement();
	}

	@Override
	public synchronized void setMax(int max)
	{
		// TODO Auto-generated method stub
		super.setMax(max);
	}

	@Override
	protected boolean verifyDrawable(Drawable who)
	{
		// TODO Auto-generated method stub
		return super.verifyDrawable(who);
	}

	@Override
	protected void drawableStateChanged()
	{
		// TODO Auto-generated method stub
		super.drawableStateChanged();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected synchronized void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec)
	{
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public synchronized boolean isIndeterminate()
	{
		// TODO Auto-generated method stub
		return super.isIndeterminate();
	}

	@Override
	public synchronized void setIndeterminate(boolean indeterminate)
	{
		// TODO Auto-generated method stub
		super.setIndeterminate(indeterminate);
	}

	@Override
	public Drawable getIndeterminateDrawable()
	{
		// TODO Auto-generated method stub
		return super.getIndeterminateDrawable();
	}

	@Override
	public void setIndeterminateDrawable(Drawable d)
	{
		// TODO Auto-generated method stub
		super.setIndeterminateDrawable(d);
	}

	@Override
	public Drawable getProgressDrawable()
	{
		// TODO Auto-generated method stub
		return super.getProgressDrawable();
	}

	@Override
	public void setProgressDrawable(Drawable d)
	{
		// TODO Auto-generated method stub
		super.setProgressDrawable(d);
	}

	@Override
	public void postInvalidate()
	{
		// TODO Auto-generated method stub
		super.postInvalidate();
	}

	@Override
	public synchronized void setProgress(int progress)
	{
		// TODO Auto-generated method stub
		super.setProgress(progress);
	}

	@Override
	public synchronized void setSecondaryProgress(int secondaryProgress)
	{
		// TODO Auto-generated method stub
		super.setSecondaryProgress(secondaryProgress);
	}

	@Override
	public synchronized int getProgress()
	{
		// TODO Auto-generated method stub
		return super.getProgress();
	}

	@Override
	public synchronized int getSecondaryProgress()
	{
		// TODO Auto-generated method stub
		return super.getSecondaryProgress();
	}

	@Override
	public synchronized int getMax()
	{
		// TODO Auto-generated method stub
		return super.getMax();
	}

	@Override
	public void setInterpolator(Context context, int resID)
	{
		// TODO Auto-generated method stub
		super.setInterpolator(context, resID);
	}

	@Override
	public void setInterpolator(Interpolator interpolator)
	{
		// TODO Auto-generated method stub
		super.setInterpolator(interpolator);
	}

	@Override
	public Interpolator getInterpolator()
	{
		// TODO Auto-generated method stub
		return super.getInterpolator();
	}

	@Override
	public void setVisibility(int v)
	{
		// TODO Auto-generated method stub
		super.setVisibility(v);
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility)
	{
		// TODO Auto-generated method stub
		super.onVisibilityChanged(changedView, visibility);
	}

	@Override
	public void invalidateDrawable(Drawable dr)
	{
		// TODO Auto-generated method stub
		super.invalidateDrawable(dr);
	}

	@Override
	public Parcelable onSaveInstanceState()
	{
		// TODO Auto-generated method stub
		return super.onSaveInstanceState();
	}

	@Override
	public void onRestoreInstanceState(Parcelable state)
	{
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
	}

	@Override
	protected void onAttachedToWindow()
	{
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow()
	{
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
	}

	

}
