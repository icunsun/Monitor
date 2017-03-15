package com.icunsun.monitor.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.icunsun.monitor.R;
import com.icunsun.monitor.util.ToastUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016/11/4.
 */

public abstract class BaseActivity extends AppCompatActivity {


    private Unbinder mUnbinder;
    private long mTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResId());
        mUnbinder = ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#0f99e4"));
        }
        init(savedInstanceState);
    }

    public abstract int getContentViewResId();

    public abstract void init(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mTime > 3000) {
            ToastUtils.toast(this, "再按一次退出");
            mTime = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }
}
