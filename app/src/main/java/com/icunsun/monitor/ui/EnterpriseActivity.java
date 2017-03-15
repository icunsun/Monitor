package com.icunsun.monitor.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.adapter.AddressAdapter;
import com.icunsun.monitor.adapter.EnterpriseAdapter;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.common.Urls;
import com.icunsun.monitor.model.MonitorInfo;
import com.icunsun.monitor.model.MonitorUser;
import com.icunsun.monitor.model.State;
import com.icunsun.monitor.util.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;
import cn.appsdream.nestrefresh.normalstyle.NestRefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class EnterpriseActivity extends BaseActivity implements OnPullListener, AddressAdapter.OnItemClickListener {

    @BindView(R.id.rv_monitor_info)
    RecyclerView mMonitorInfoRv;
    @BindView(R.id.tv_name)
    TextView mNameTv;
    @BindView(R.id.empty_view)
    View mEmptyView;
    @BindView(R.id.iv_location)
    ImageView mLocationIv;

    private int mLimit = 20;
    private int mCurPage = 0;

    // 最新的省份编码
    private int mLastCode = -1;
    private int mCode = TypeConstants.ALL;

    private EnterpriseAdapter mAdapter;
    private NestRefreshLayout mLoader;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_enterprise;
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(SelectPlaceActivity.SELECT_ADDRESS, Context.MODE_PRIVATE);
        mCode = preferences.getInt(SelectPlaceActivity.SELECT_ADDRESS, TypeConstants.ALL);
        if (mLastCode != mCode) {
            queryData(mCurPage, State.REFRESH, mCode);
            mLastCode = mCode;
        }
    }

    @Override
    public void init(Bundle savedInstanceState) {
        MonitorUser currentUser = MonitorUser.getCurrentUser(MonitorUser.class);
        String name = currentUser.getName();
        mNameTv.setText(name);

        mEmptyView.setVisibility(View.GONE);

        mLoader = new NestRefreshLayout(mMonitorInfoRv);
        mLoader.setPullRefreshEnable(true);
        mLoader.setPullLoadEnable(true);
        mLoader.setOnLoadingListener(this);

        mAdapter = new EnterpriseAdapter(null, this);
        mAdapter.setListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mMonitorInfoRv.setLayoutManager(manager);
        mMonitorInfoRv.setAdapter(mAdapter);
    }

    @OnClick({R.id.btn_feedback, R.id.iv_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_feedback:
                Intent feedback = new Intent(this, FeedbackActivity.class);
                startActivity(feedback);
                break;
            case R.id.iv_location:
                Intent selectPlace = new Intent(this, SelectPlaceActivity.class);
                startActivity(selectPlace);
                break;
        }
    }

    /**
     * 分页查询
     *
     * @param page
     * @param state
     */
    private void queryData(int page, final State state, int code) {
        BmobQuery<MonitorInfo> query = new BmobQuery<>();
        // 如何code为全部，则不加查询条件
        if (code != TypeConstants.ALL) {
            query.addWhereEqualTo("mProvinceCode", code);
        }
        query.order("-createdAt");
        switch (state) {
            case REFRESH:
                page = 0;
                break;
            case LOADING:
                page++;
                break;
        }
        // 只讲审核通过的显示出来
        query.addWhereEqualTo("mState", TypeConstants.STATE_PASS);
        query.setSkip(page * mLimit);
        query.setLimit(mLimit);
        query.findObjects(new FindListener<MonitorInfo>() {
                              @Override
                              public void done(List<MonitorInfo> list, BmobException e) {
                                  if (mLoader != null) {
                                      mLoader.onLoadFinished();
                                  }
                                  if (e == null) {
                                      if (list != null && list.size() > 0) {
                                          switch (state) {
                                              case REFRESH:
                                                  mEmptyView.setVisibility(View.GONE);
                                                  mCurPage = 0;
                                                  mAdapter.updateData(list);
                                                  break;
                                              case LOADING:
                                                  mCurPage++;
                                                  mAdapter.addData(list);
                                                  break;
                                          }
                                      } else {
                                          switch (state) {
                                              case REFRESH:
                                                  ToastUtils.toast(EnterpriseActivity.this, "没有数据!");
                                                  mAdapter.clearAll();
                                                  mEmptyView.setVisibility(View.VISIBLE);
                                                  break;
                                              case LOADING:
                                                  ToastUtils.toast(EnterpriseActivity.this, "没有更多数据了!");
                                                  break;
                                          }
                                      }
                                  } else {
                                      ToastUtils.toast(EnterpriseActivity.this, "刷新失败: " + e.getMessage());
                                  }
                              }
                          }
        );
    }

    @Override
    public void onRefresh(AbsRefreshLayout listLoader) {
        queryData(mCurPage, State.REFRESH, mCode);
    }

    @Override
    public void onLoading(AbsRefreshLayout listLoader) {
        queryData(mCurPage, State.LOADING, mCode);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()) {
            case R.id.iv_video:
                Intent intent = new Intent(this, PlayActivity.class);
                intent.putExtra(Urls.VIDEO_URL, mAdapter.getItem(position).getVideo());
                startActivity(intent);
                break;
        }
    }

}
