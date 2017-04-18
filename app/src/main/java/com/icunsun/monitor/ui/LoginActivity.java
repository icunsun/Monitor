package com.icunsun.monitor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.MonitorUser;
import com.icunsun.monitor.util.ProgressDialogUtils;
import com.icunsun.monitor.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.et_username)
    EditText mUsernameEt;
    @BindView(R.id.et_password)
    EditText mPasswordEt;
    @BindView(R.id.btn_login)
    Button mLoginBtn;
    @BindView(R.id.tv_register)
    TextView mRegisterTv;
    @BindView(R.id.tv_forget_pwd)
    TextView mForgetPwdTv;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        judgeLogin();
    }

    @OnClick({R.id.btn_login, R.id.tv_register, R.id.tv_forget_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_register:
                register();
                break;
            case R.id.tv_forget_pwd:
                forgetPwd();
                break;
        }
    }


    /**
     * 忘记密码功能
     */
    private void forgetPwd() {
        Intent intent = new Intent(this, LoginByPhoneActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        judgeLogin();
    }

    /**
     * 判断当前用户是否已经注册或者登录
     * 如果当前用户已经登录过，则直接将其显示在et上
     */
    private void judgeLogin() {
        MonitorUser user = BmobUser.getCurrentUser(MonitorUser.class);
        if (user != null) {
            String username = user.getUsername();
            String pwd = user.getPwd();
            mUsernameEt.setText(username);
            mPasswordEt.setText(pwd);
        }
    }

    /**
     * 注册功能
     */
    private void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * 登录
     */
    private void login() {
        String username = mUsernameEt.getText().toString().trim();
        String password = mPasswordEt.getText().toString().trim();

        Log.d(TAG, "login:" + username);
        if (TextUtils.isEmpty(username)) {
            ToastUtils.toast(this, "用户名不能为空!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toast(this, "密码不能为空!");
            return;
        }

        final MonitorUser u = new MonitorUser(username, password);
        ProgressDialogUtils.showDialog(this, "正在登录，请稍候...");
        u.login(new SaveListener<MonitorUser>() {
            @Override
            public void done(MonitorUser monitorUser, BmobException e) {
                ProgressDialogUtils.closeDialog();
                if (e == null) {
                    Integer type = monitorUser.getUserType();
                    switch (type) {
                        case TypeConstants.MONITOR:
                            toMonitor();
                            break;
                        case TypeConstants.ENTERPRISE:
                            toEnterprise();
                            break;
                        case TypeConstants.CHECK:
                            toCheck();
                            break;
                    }
                } else {
                    ToastUtils.toast(LoginActivity.this, "登录失败：" + e.getMessage());
                }
            }

        });


    }

    /**
     * 跳转至审核页面
     */
    private void toCheck() {
        Intent intent = new Intent(this, CheckActivity.class);
        startActivity(intent);
    }


    /**
     * 跳转到监播界面
     */
    private void toMonitor() {
        Intent intent = new Intent(this, MonitorActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 跳转到企业界面
     */
    private void toEnterprise() {
        Intent intent = new Intent(this, EnterpriseActivity.class);
        startActivity(intent);
        finish();
    }

}
