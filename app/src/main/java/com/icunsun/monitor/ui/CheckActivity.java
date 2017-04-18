package com.icunsun.monitor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.icunsun.monitor.R;
import com.icunsun.monitor.adapter.AddressAdapter;
import com.icunsun.monitor.adapter.ReviewAdapter;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.common.Urls;
import com.icunsun.monitor.model.MonitorInfo;
import com.icunsun.monitor.model.State;
import com.icunsun.monitor.util.ProgressDialogUtils;
import com.icunsun.monitor.util.ToastUtils;
import com.icunsun.monitor.widget.CustomLinearLayoutManager;

import java.util.List;

import butterknife.BindView;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;
import cn.appsdream.nestrefresh.normalstyle.NestRefreshLayout;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class CheckActivity extends BaseActivity implements AddressAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.empty_view)
    View mEmptyView;
    @BindView(R.id.rv_review)
    RecyclerView mReviewRv;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    private ReviewAdapter mAdapter;

    private CustomLinearLayoutManager mManager;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_check;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mManager = new CustomLinearLayoutManager(this, CustomLinearLayoutManager.HORIZONTAL, false);


        mReviewRv.setLayoutManager(mManager);
        mAdapter = new ReviewAdapter(null, this);
        mAdapter.setListener(this);
        mReviewRv.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    private void queryData() {
        BmobQuery<MonitorInfo> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(20);
        query.addWhereEqualTo("mState", TypeConstants.STATE_REVIEW);
        query.findObjects(new FindListener<MonitorInfo>() {
            @Override
            public void done(List<MonitorInfo> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    mAdapter.updateData(list);
                    mEmptyView.setVisibility(View.GONE);
                } else {
                    ToastUtils.toast(CheckActivity.this, "暂无数据");
                }
                if (mRefreshLayout.isRefreshing())
                    mRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(View view, final int position) {
        MonitorInfo info = new MonitorInfo();
        switch (view.getId()) {
            case R.id.iv_pass:
                info.setState(TypeConstants.STATE_PASS);
                refreshState(info, position);
                break;
            case R.id.iv_refuse:
                info.setState(TypeConstants.STATE_FAIL);
                refreshState(info, position);
                break;
            case R.id.iv_video:
                Intent intent = new Intent(this, PlayActivity.class);
                intent.putExtra(Urls.VIDEO_URL, mAdapter.getItem(position).getVideo());
                startActivity(intent);
                break;
        }
    }

    private void refreshState(MonitorInfo info, final int position) {

        ProgressDialogUtils.showDialog(this, "正在处理中...");
        info.update(mAdapter.getItem(position).getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                ProgressDialogUtils.closeDialog();
                if (e == null) {
                    ToastUtils.toast(CheckActivity.this, "成功");
                    if (mAdapter.getItemCount() - 1 > position)
                        mManager.scrollToPosition(position + 1);
                    mAdapter.remove(position);
                    int count = mAdapter.getItemCount();
                    if (count == 0) {
                        mEmptyView.setVisibility(View.VISIBLE);
                        mRefreshLayout.setRefreshing(true);
                        onRefresh();
                    }
                } else {
                    ToastUtils.toast(CheckActivity.this, "操作失败，请重试");
                }

            }
        });

    }

    @Override
    public void onRefresh() {
        queryData();
    }
}
