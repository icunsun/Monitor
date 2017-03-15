package com.icunsun.monitor.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/7.
 */

public class ToastUtils {
    public static Toast sToast;

    public static void toast(Context context, CharSequence text) {
        if (sToast == null) {
            sToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        sToast.setText(text);
        sToast.show();
    }
}
