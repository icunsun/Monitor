package com.icunsun.monitor.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/11/8.
 */

public class MonitorInfo extends BmobObject {
    private String mUserId;
    private String mName;
    private String mContent;
    private String mAddress;
    private String mTime;
    private List<String> mPictures;
    private String mVideo;
    private Integer mProvinceCode;
    private Integer mState;

    public void setProvinceCode(Integer provinceCode) {
        mProvinceCode = provinceCode;
    }

    public Integer getState() {
        return mState;
    }

    public void setState(Integer state) {
        mState = state;
    }

    public int getProvinceCode() {
        return mProvinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        mProvinceCode = provinceCode;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public List<String> getPictures() {
        return mPictures;
    }

    public void setPictures(List<String> pictures) {
        mPictures = pictures;
    }

    public String getVideo() {
        return mVideo;
    }

    public void setVideo(String video) {
        mVideo = video;
    }
}
