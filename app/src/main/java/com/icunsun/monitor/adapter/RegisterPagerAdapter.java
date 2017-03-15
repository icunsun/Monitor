package com.icunsun.monitor.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2016/11/6.
 */

public class RegisterPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mData;
    private String[] mTitles;

    public RegisterPagerAdapter(FragmentManager fm, List<Fragment> data, String[] titles) {
        super(fm);
        if (data == null) {
            mData = new ArrayList<>();
        } else {
            mData = data;
        }
        mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

}
