package com.icunsun.monitor;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.ninegrid.NineGridView;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Created by Administrator on 2016/11/4.
 */

public class MonitorApp extends Application {
    private static final String APP_ID = "8c96c81fc3338551148f9a738c18a704";

    @Override
    public void onCreate() {
        super.onCreate();
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId(APP_ID)
                .setConnectTimeout(30)
                .setUploadBlockSize(2 * 1024 * 1024)
                .build();
        Bmob.initialize(config);
        GlideImageLoader imageLoader = new GlideImageLoader();
        NineGridView.setImageLoader(imageLoader);
    }

    private class GlideImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.mipmap.icon_default)
                    .error(R.mipmap.ic_launcher)
                    .into(imageView);
        }


        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }
}
