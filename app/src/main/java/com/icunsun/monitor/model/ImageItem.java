package com.icunsun.monitor.model;

/**
 * Created by Administrator on 2016/11/9.
 */

public class ImageItem {

    private String mUrl;
    private boolean mIsVideo;

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public boolean isVideo() {
        return mIsVideo;
    }

    public void setVideo(boolean video) {
        mIsVideo = video;
    }
}
