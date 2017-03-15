package com.icunsun.monitor.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;

import static com.icunsun.monitor.ui.fragment.MonitorFragment.TAG;

/**
 * Created by Administrator on 2016/11/17.
 */

public class ImageUtils {

    /**
     * 压缩图片，并返回压缩后的图片路径
     *
     * @param dest
     * @return
     */
    public static String compressPic(File dest, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(dest.getAbsolutePath(), options);


        int width = options.outWidth;
        int height = options.outHeight;

        float reqHeight = 1920f;
        float reqWidth = 1080f;

        int scale = 1;

        if (width > height && width > reqWidth) {
            scale = (int) (width / reqWidth);
        } else if (width < height && height > reqHeight) {
            scale = (int) (height / reqHeight);
        }
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;

        options.inJustDecodeBounds = false;

        Bitmap reqBitmap = BitmapFactory.decodeFile(dest.getAbsolutePath(), options);

        String dir = FileUtils.getCacheDir(context).getAbsolutePath() + File.separator + "icunsun";
        File dirFile = new File(dir);
        if (!dirFile.exists())
            dirFile.mkdirs();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();
        String time = "IMG_" + format.format(date) + ".jpg";
        File target = new File(dir, File.separator + time);
        if (target.exists()) {
            target.delete();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileOutputStream:" + e.getMessage());
        }
        //对质量进行压缩
        reqBitmap.compress(Bitmap.CompressFormat.JPEG, 40, fos);

        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return target.getAbsolutePath();
    }

}
