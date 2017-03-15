package com.icunsun.monitor.model;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/11/4.
 */

public class MonitorUser extends BmobUser {
    private String mName;
    private Integer mUserType;
    private String mPwd;


    public MonitorUser() {

    }

    public MonitorUser(String username, String pwd) {
        setUsername(username);
        setPassword(pwd);
    }

    public Integer getUserType() {
        return mUserType;
    }

    public void setUserType(Integer userType) {
        mUserType = userType;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPwd() {
        return mPwd;
    }

    public void setPwd(String pwd) {
        mPwd = pwd;
    }
}
