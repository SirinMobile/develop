package com.uiu.camera.orangeil.friendly.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class PreviewLayout extends LinearLayout
{
    private Context myContext;

    public PreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec+((int)myContext.getResources().getDimension(R.dimen.quickplay_offset)));
    }
}