package com.icunsun.monitor.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2016/12/1.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        int position = parent.getChildLayoutPosition(view);
        if (position != 0)
            outRect.right = space;
        outRect.bottom = space;
        //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
        if ((position - 1) % 3 == 0) {
            outRect.left = space;
        }
    }
}
