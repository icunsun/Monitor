package com.icunsun.monitor.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2017/3/3 0003.
 */

public class VideoItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public VideoItemDecoration(int space) {
        mSpace = space;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        Log.d("Decoration", "getItemOffsets: " + view.getHeight());
        Log.d("Decoration", "getItemOffsets: " + view.getWidth());
        // 如果是第一个
        outRect.right = mSpace;
        outRect.top = mSpace;
        if (position % 3 == 0) {
            outRect.left = mSpace;
        } else {
            outRect.left = 0;
        }
    }
}
