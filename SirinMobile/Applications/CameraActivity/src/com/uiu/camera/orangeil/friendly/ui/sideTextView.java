package com.uiu.camera.orangeil.friendly.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class sideTextView extends TextView
{

	public sideTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.rotate(-90);
		super.onDraw(canvas);
	}
}
