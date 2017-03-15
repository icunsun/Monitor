package com.icunsun.monitor.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.adapter.AddressAdapter;
import com.icunsun.monitor.adapter.SpaceItemDecoration;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.Province;
import com.icunsun.monitor.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

public class SelectPlaceActivity extends BaseActivity implements AddressAdapter.OnItemClickListener {
    public static final String SELECT_ADDRESS = "SELECT_ADDRESS";
    @BindView(R.id.rv_address)
    RecyclerView mAddressRv;
    @BindArray(R.array.provinces_name)
    String[] mProvincesName;

    @BindArray(R.array.provinces_code)
    int[] mProvincesCode;
    private List<Province> mData;
    private AddressAdapter mAdapter;
    private SharedPreferences mPreferences;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_select_place;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mData = new ArrayList<>();

        // 添加头布局
        Province header = new Province(null, TypeConstants.HEADER);
        mData.add(header);

        // 第一次默认选择的是全部
        Province province = new Province("全部", TypeConstants.ALL);
        mData.add(province);
        int length = mProvincesName.length;
        for (int i = 0; i < length; i++) {
            Province p = new Province();
            p.setProvinceName(mProvincesName[i]);
            p.setProvinceCode(mProvincesCode[i]);
            mData.add(p);
        }

        mPreferences = getSharedPreferences(SELECT_ADDRESS, Context.MODE_PRIVATE);

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int span;
                if (mData.get(position).getProvinceCode() == TypeConstants.HEADER) {
                    span = 3;
                } else {
                    span = 1;
                }
                return span;
            }
        });
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float scale = metrics.density;
        int space = (int) (scale * 17 + 0.5f);
        SpaceItemDecoration decor = new SpaceItemDecoration(space);
        mAddressRv.addItemDecoration(decor);
        mAddressRv.setLayoutManager(manager);
        mAdapter = new AddressAdapter(mData, this);
        mAddressRv.setAdapter(mAdapter);
        mAdapter.setListener(this);

    }

    @OnClick(R.id.iv_close)
    public void close() {
        finish();
    }

    @Override
    public void onItemClick(View view, int position) {
        Province province = mData.get(position);
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt(SELECT_ADDRESS, province.getProvinceCode());
        edit.commit();
        finish();
    }
}
