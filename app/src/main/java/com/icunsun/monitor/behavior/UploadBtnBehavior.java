package com.icunsun.monitor.behavior;

import android.support.design.widget.CoordinatorLayout;
import android.view.View;

/**
 * Created by Administrator on 2016/11/9.
 */

public class UploadBtnBehavior extends CoordinatorLayout.Behavior<View> {



    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return false;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }
}
