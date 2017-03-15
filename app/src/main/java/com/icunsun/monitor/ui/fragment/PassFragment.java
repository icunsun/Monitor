package com.icunsun.monitor.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.adapter.MonitorInfoAdapter;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.MonitorInfo;
import com.icunsun.monitor.model.MonitorUser;
import com.icunsun.monitor.model.State;
import com.icunsun.monitor.util.ToastUtils;

import java.util.List;

import butterknife.BindView;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;
import cn.appsdream.nestrefresh.normalstyle.NestRefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 已通过Fragment
 * A simple {@link Fragment} subclass.
 */
public class PassFragment extends BaseFragment implements OnPullListener {


    private int mLimit = 20;
    private int mCurPage = 0;
    private MonitorInfoAdapter mAdapter;
    @BindView(R.id.lv_pass)
    ListView mPassLv;
    private NestRefreshLayout mLoader;


    @Override
    public void init(Bundle savedInstanceState) {

        mLoader = new NestRefreshLayout(mPassLv);
        mLoader.setPullRefreshEnable(true);
        mLoader.setPullLoadEnable(true);
        mLoader.setOnLoadingListener(this);

        mAdapter = new MonitorInfoAdapter(getContext(), null, R.layout.item_monitorinfo);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.empty_monitor_layout, mPassLv, false);
        ViewGroup parent = (ViewGroup) mPassLv.getParent().getParent();
        parent.addView(view, 1);
        mPassLv.setEmptyView(view);
        mPassLv.setAdapter(mAdapter);
        // 查询通过的数据
    }

    @Override
    public void onResume() {
        super.onResume();
        queryData(TypeConstants.STATE_PASS, State.REFRESH);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_pass;
    }

    /**
     * 根据传入的状态查询数据
     *
     * @param type
     * @param state
     */
    private void queryData(int type, final State state) {
        int page = 0;
        switch (state) {
            case REFRESH:
                page = 0;
                break;
            case LOADING:
                page = mCurPage + 1;
                break;

        }
        MonitorUser currentUser = MonitorUser.getCurrentUser(MonitorUser.class);
        BmobQuery<MonitorInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("mUserId", currentUser.getObjectId());
        query.addWhereEqualTo("mState", type);
        query.setLimit(20);
        query.setSkip(page * mLimit);
        query.order("-createdAt");
        query.findObjects(new FindListener<MonitorInfo>() {
            @Override
            public void done(List<MonitorInfo> list, BmobException e) {
                if (mLoader != null) {
                    mLoader.onLoadFinished();
                }
                if (list != null && list.size() > 0) {
                    switch (state) {
                        case REFRESH:
                            // 刷新成功，将当前页置零
                            mCurPage = 0;
                            mAdapter.updateData(list);
                            break;
                        case LOADING:
                            // 成功之后才将当前页加一
                            mCurPage++;
                            mAdapter.addData(list);
                            break;
                    }
                } else {
                    if (state == State.LOADING) {
                        ToastUtils.toast(getContext(), "无更多内容!");
                    }
                }
            }
        });

    }

    /**
     * 下拉刷新监听
     *
     * @param listLoader
     */
    @Override
    public void onRefresh(AbsRefreshLayout listLoader) {
        queryData(TypeConstants.STATE_PASS, State.REFRESH);
    }

    /**
     * 上拉加载监听
     *
     * @param listLoader
     */
    @Override
    public void onLoading(AbsRefreshLayout listLoader) {
        queryData(TypeConstants.STATE_PASS, State.LOADING);
    }
}
