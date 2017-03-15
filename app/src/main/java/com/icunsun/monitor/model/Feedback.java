package com.icunsun.monitor.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/11/14.
 */

public class Feedback extends BmobObject {

    private String mUserName;
    private String mContent;
    private String mTime;

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }
}
