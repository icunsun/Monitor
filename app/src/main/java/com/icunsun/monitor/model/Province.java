package com.icunsun.monitor.model;

/**
 * Created by Administrator on 2016/11/29.
 */

public class Province {
    private String mProvinceName;
    private int mProvinceCode;

    public Province() {
    }

    public Province(String provinceName, int provinceCode) {
        mProvinceName = provinceName;
        mProvinceCode = provinceCode;
    }

    public String getProvinceName() {
        return mProvinceName;
    }

    public void setProvinceName(String provinceName) {
        mProvinceName = provinceName;
    }

    public int getProvinceCode() {
        return mProvinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        mProvinceCode = provinceCode;
    }
}
