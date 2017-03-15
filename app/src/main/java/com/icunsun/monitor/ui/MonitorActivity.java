package com.icunsun.monitor.ui;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.adapter.MonitorInfoAdapter;
import com.icunsun.monitor.adapter.MonitorInfoStateAdapter;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.MonitorInfo;
import com.icunsun.monitor.model.MonitorUser;
import com.icunsun.monitor.ui.fragment.PassFragment;
import com.icunsun.monitor.ui.fragment.ReviewFragment;
import com.icunsun.monitor.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;
import cn.appsdream.nestrefresh.normalstyle.NestRefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MonitorActivity extends BaseActivity {

    @BindView(R.id.vp_monitorinfo)
    ViewPager mMonitorInfoVp;
    @BindView(R.id.tab_state)
    TabLayout mStateTab;
    @BindView(R.id.tv_name)
    TextView mNameTv;
    private MonitorInfoStateAdapter mAdapter;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_monitor;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void init(Bundle savedInstanceState) {
        MonitorUser currentUser = MonitorUser.getCurrentUser(MonitorUser.class);
        String name = currentUser.getName();
        mNameTv.setText(name);

        mAdapter = new MonitorInfoStateAdapter(getSupportFragmentManager(), getData(), this);
        mMonitorInfoVp.setAdapter(mAdapter);
        mStateTab.setupWithViewPager(mMonitorInfoVp);
    }

    private List<Fragment> getData() {
        PassFragment passFragment = new PassFragment();
        ReviewFragment reviewFragment = new ReviewFragment();
        List<Fragment> data = new ArrayList<>();
        data.add(passFragment);
        data.add(reviewFragment);
        return data;
    }


    @OnClick(R.id.btn_upload)
    public void toUpload(View view) {
        Intent intent = new Intent(this, UploadActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_out, R.anim.activity_in);
    }

}
