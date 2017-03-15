package com.icunsun.monitor.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.icunsun.monitor.R;
import com.icunsun.monitor.adapter.RegisterPagerAdapter;
import com.icunsun.monitor.ui.fragment.EnterpriseFragment;
import com.icunsun.monitor.ui.fragment.MonitorFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class RegisterActivity extends BaseActivity {


    @BindView(R.id.vp_register)
    ViewPager mRegisterVp;
    @BindView(R.id.tab_type)
    TabLayout mTypeTab;



    @Override
    public int getContentViewResId() {
        return R.layout.activity_register;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        RegisterPagerAdapter adapter = new RegisterPagerAdapter(getSupportFragmentManager(), getData(), getTitles());
        mRegisterVp.setAdapter(adapter);
        mTypeTab.setupWithViewPager(mRegisterVp);
    }



    private List<Fragment> getData() {
        List<Fragment> data = new ArrayList<>();
        MonitorFragment monitorFragment = new MonitorFragment();
        EnterpriseFragment enterpriseFragment = new EnterpriseFragment();
        data.add(monitorFragment);
        data.add(enterpriseFragment);
        return data;

    }

    private String[] getTitles() {
        String[] titles = {"监播用户", "企业用户"};
        return titles;
    }
}
