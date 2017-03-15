package com.icunsun.monitor.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/11/18.
 */

public class FileUtils {

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = null;
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String getFileSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            size = getDirSize(file);
        } else if (file.isFile()) {
            size = calculateFileSize(file);
        }
        return formatFileSize(size);
    }

    private static long calculateFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return size;
    }


    private static long getDirSize(File file) {
        long size = 0;
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                getFileSize(f);
            } else {
                size += file.length();
            }
        }
        return size;
    }

    public static File getCacheDir(Context context) {
        File dir;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            dir = Environment.getExternalStorageDirectory();
        } else {
            dir = context.getCacheDir();
        }
        return dir;
    }
}
