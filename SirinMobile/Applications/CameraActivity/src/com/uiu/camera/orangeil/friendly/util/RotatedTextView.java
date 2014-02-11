package com.uiu.camera.orangeil.friendly.util;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class RotatedTextView extends TextView
{
	public RotatedTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.rotate(-90);

		super.onDraw(canvas);
	}

}
