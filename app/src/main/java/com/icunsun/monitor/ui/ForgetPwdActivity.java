package com.icunsun.monitor.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.model.MonitorUser;
import com.icunsun.monitor.util.ProgressDialogUtils;
import com.icunsun.monitor.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ForgetPwdActivity extends BaseActivity {
    @BindView(R.id.et_phone)
    EditText mPhoneEt;
    @BindView(R.id.et_verify)
    EditText mVerifyEt;
    @BindView(R.id.et_new_pwd)
    EditText mNewPwdEt;
    @BindView(R.id.et_confirm_pwd)
    EditText mConfirmPwdEt;
    @BindView(R.id.btn_confirm)
    Button mConfirmBtn;
    @BindView(R.id.tv_login)
    TextView mLoginTv;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    public void init(Bundle savedInstanceState) {


    }

    @OnClick({R.id.tv_login, R.id.btn_confirm, R.id.tv_get_verify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_get_verify:
                requestCode();
                break;
            case R.id.tv_login:
                toLogin();
                break;
            case R.id.btn_confirm:
                resetPwd();
                break;
        }
    }

    /**
     * 重新设置密码
     */
    private void resetPwd() {

        final String phone = mPhoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toast(this, "手机号不能为空!");
            return;
        }
        String verify = mVerifyEt.getText().toString().trim();
        if (TextUtils.isEmpty(verify)) {
            ToastUtils.toast(this, "验证码不能为空!");
            return;
        }
        final String pwd = mNewPwdEt.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.toast(this, "密码不能为空!");
            return;
        }
        String confirmPwd = mConfirmPwdEt.getText().toString().trim();
        if (TextUtils.isEmpty(confirmPwd)) {
            ToastUtils.toast(this, "两次密码输入不一致!");
            return;
        }
        if (!TextUtils.equals(confirmPwd, pwd)) {
            ToastUtils.toast(this, "两次密码输入不一致!");
            return;
        }

        ProgressDialogUtils.showDialog(this, "正在修改，请稍候...");
        MonitorUser.resetPasswordBySMSCode(verify, pwd, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                ProgressDialogUtils.closeDialog();
                if (e == null) {
                    MonitorUser user = MonitorUser.getCurrentUser(MonitorUser.class);
                    if (user.getMobilePhoneNumber().equals(phone)) {
                        user.updateCurrentUserPassword(user.getPwd(), pwd, null);
                        user.setPwd(pwd);
                    }
                    ToastUtils.toast(ForgetPwdActivity.this, "密码修改成功，请使用新密码登录");
                    toLogin();
                } else {
                    ToastUtils.toast(ForgetPwdActivity.this, "修改失败: " + e.getMessage());
                }
            }
        });
    }

    /**
     * 跳转到login界面
     */
    private void toLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 请求验证码
     */
    private void requestCode() {
        String phone = mPhoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toast(ForgetPwdActivity.this, "手机号不能为空");
            return;
        }
        ProgressDialogUtils.showDialog(ForgetPwdActivity.this, "正在发送中...");
        BmobSMS.requestSMSCode(phone, "i村尚短信验证", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                ProgressDialogUtils.closeDialog();
                if (e == null) {
                    ToastUtils.toast(ForgetPwdActivity.this, "验证码发送成功");
                } else {
                    ToastUtils.toast(ForgetPwdActivity.this, "验证码发送失败： " + e.getMessage());
                }
            }
        });

    }

}
