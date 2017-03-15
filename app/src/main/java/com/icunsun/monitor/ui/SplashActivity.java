package com.icunsun.monitor.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.icunsun.monitor.R;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class SplashActivity extends BaseActivity implements Handler.Callback {
    private static final long DELAY_TIME = 3000;
    private static final int TO_LOGIN = 10;
    private Handler mHandler;

    @BindView(R.id.btn_login)
    Button mLoginBtn;
    @BindView(R.id.btn_register)
    Button mRegister;

    @BindView(R.id.layout)
    View mView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#097ee1"));
        }
        BmobUser user = BmobUser.getCurrentUser(BmobUser.class);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.activity_in);
        mView.startAnimation(animation);
        if (user == null) {
            mLoginBtn.setVisibility(View.VISIBLE);
            mRegister.setVisibility(View.VISIBLE);
            mLoginBtn.startAnimation(animation);
            mRegister.startAnimation(animation);
        } else {
            mHandler = new Handler(this);
            mHandler.sendEmptyMessageDelayed(TO_LOGIN, DELAY_TIME);
        }
    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                toLogin();
                break;
            case R.id.btn_register:
                toRegister();
                break;
        }
    }

    /**
     * 跳转到注册页面
     */
    private void toRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到登录页面
     */
    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case TO_LOGIN:
                toLogin();
                break;
        }
        return true;
    }
}
