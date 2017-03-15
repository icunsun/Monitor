package com.icunsun.monitor.model;

/**
 * Created by Administrator on 2017/3/3 0003.
 */

public class Video {
    private String mSdcardPath;
    private boolean mIsSeleted = false;

    public Video(String sdcardPath) {
        mSdcardPath = sdcardPath;
    }

    public String getSdcardPath() {
        return mSdcardPath;
    }

    public void setSdcardPath(String sdcardPath) {
        mSdcardPath = sdcardPath;
    }

    public boolean isSeleted() {
        return mIsSeleted;
    }

    public void setSeleted(boolean seleted) {
        mIsSeleted = seleted;
    }
}
