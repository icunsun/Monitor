package com.icunsun.monitor.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.icunsun.monitor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */

public class MonitorInfoStateAdapter extends FragmentPagerAdapter {

    private String[] mTitles;
    private List<Fragment> mData;

    public MonitorInfoStateAdapter(FragmentManager fm, List<Fragment> data, Context context) {
        super(fm);
        if (data == null) {
            mData = new ArrayList<>();
        } else {
            mData = data;
        }
        mTitles = context.getResources().getStringArray(R.array.state_name);
    }

    @Override
    public Fragment getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
