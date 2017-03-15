package com.icunsun.monitor.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.icunsun.monitor.R;

/**
 * Created by Administrator on 2016/11/7.
 */

public class ProgressDialogUtils {
    private static ProgressDialog sDialog;
    private static View sView = null;
    private static TextView mTitleTv;

    public static void showDialog(Context context, CharSequence title) {
        sDialog = new ProgressDialog(context);
        sView = LayoutInflater.from(context).inflate(R.layout.progress_layout, null);
        mTitleTv = (TextView) sView.findViewById(R.id.tv_title);
        mTitleTv.setText(title);
        sDialog.setCancelable(false);
        sDialog.show();
        sDialog.setContentView(sView);
    }

    public static void closeDialog() {
        if (sDialog != null) {
            sDialog.dismiss();
            sDialog = null;
            sView = null;
            mTitleTv = null;
        }
    }

}
