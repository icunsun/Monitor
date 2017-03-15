package com.icunsun.monitor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/11/9.
 */

public class MyGridView extends GridView {
    public boolean isOnMeasure;

    public MyGridView(Context context) {
        super(context);
    }

    public MyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isOnMeasure = true;
        int reqHeight = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 4, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, reqHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        isOnMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }
}
