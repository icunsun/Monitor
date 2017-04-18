package com.icunsun.monitor.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.MonitorUser;
import com.icunsun.monitor.ui.LoginActivity;
import com.icunsun.monitor.util.ProgressDialogUtils;
import com.icunsun.monitor.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MonitorFragment extends BaseFragment {

    public static final String TAG = MonitorFragment.class.getSimpleName();

    @BindView(R.id.et_name)
    EditText mNameEt;
    @BindView(R.id.et_phone)
    EditText mPhoneEt;
    @BindView(R.id.et_password)
    EditText mPwdEt;
    @BindView(R.id.et_verify)
    EditText mVerifyEt;
    @BindView(R.id.btn_register)
    Button mRegisterBtn;
    @BindView(R.id.tv_login)
    TextView mLoginTv;
    @BindView(R.id.chb_pwd)
    CheckBox mCheckBox;

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_monitor;
    }

    @OnClick({R.id.tv_login, R.id.btn_register, R.id.tv_get_verify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                toLogin();
                break;
            case R.id.tv_get_verify:
                requestCode();
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }

    @OnCheckedChanged(R.id.chb_pwd)
    public void changePwdStyle() {
        if (mCheckBox.isChecked()) {
            ToastUtils.toast(getContext(), "密码可见");
            mPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            ToastUtils.toast(getContext(), "密码不可见");
            mPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

    }

    /**
     * 注册
     */
    private void register() {
        final String phone = mPhoneEt.getText().toString().trim();
        final String name = mNameEt.getText().toString().trim();
        final String pwd = mPwdEt.getText().toString().trim();
        String verify = mVerifyEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            ToastUtils.toast(getContext(), "姓名不能为空");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toast(getContext(), "手机号不能为空");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.toast(getContext(), "密码不能为空");
            return;
        }
        if (TextUtils.isEmpty(verify)) {
            ToastUtils.toast(getContext(), "验证码不能为空");
            return;
        }

        ProgressDialogUtils.showDialog(getContext(), "正在注册,请稍候...");
        BmobSMS.verifySmsCode(phone, verify, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                ProgressDialogUtils.closeDialog();
                if (e == null) {
                    MonitorUser user = new MonitorUser();
                    user.setUsername(phone);
                    user.setPassword(pwd);
                    user.setName(name);
                    // 设置注册用户的类型
                    user.setUserType(TypeConstants.MONITOR);
                    user.setPwd(pwd);
                    user.setMobilePhoneNumber(phone);
                    // 将电话号码设置成激活状态
                    user.setMobilePhoneNumberVerified(true);
                    user.signUp(new SaveListener<MonitorUser>() {
                        @Override
                        public void done(MonitorUser monitorUser, BmobException e) {
                            if (e == null) {
                                ToastUtils.toast(getContext(), "注册成功");
                                toLogin();
                            } else {
                                ToastUtils.toast(getContext(), "注册失败: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    ToastUtils.toast(getContext(), "验证码有误");
                }
            }
        });


    }

    /**
     * 请求验证码
     */
    private void requestCode() {
        String phone = mPhoneEt.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.toast(getContext(), "手机号不能为空");
            return;
        }
        ProgressDialogUtils.showDialog(getContext(), "正在发送中...");
        BmobSMS.requestSMSCode(phone, "i村尚短信验证", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                ProgressDialogUtils.closeDialog();
                if (e == null) {
                    ToastUtils.toast(getContext(), "验证码发送成功");
                } else {
                    ToastUtils.toast(getContext(), "验证码发送失败： " + e.getMessage());
                }
            }
        });

    }

    /**
     * 跳转到login界面
     */
    private void toLogin() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
