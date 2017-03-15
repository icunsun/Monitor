package com.icunsun.monitor.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;

import com.icunsun.monitor.R;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.Feedback;
import com.icunsun.monitor.model.MonitorUser;
import com.icunsun.monitor.util.ProgressDialogUtils;
import com.icunsun.monitor.util.ToastUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.et_feedback)
    EditText mFeedback;


    @OnClick({R.id.image_back, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    private void submit() {
        String content = mFeedback.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.toast(this, "请输入反馈内容");
            return;
        }
        MonitorUser user = MonitorUser.getCurrentUser(MonitorUser.class);
        if (user.getUserType() == TypeConstants.ENTERPRISE) {
            Feedback feedback = new Feedback();
            feedback.setContent(content);
            feedback.setUserName(user.getName());

            Date date = new Date();
            long time = date.getTime();
            String dateTime = DateUtils.formatDateTime(FeedbackActivity.this, time, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
            feedback.setTime(dateTime);
            ProgressDialogUtils.showDialog(this, "正在提交，请稍候...");
            feedback.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    ProgressDialogUtils.closeDialog();
                    if (e == null) {
                        ToastUtils.toast(FeedbackActivity.this, "提交成功！");
                    } else {
                        ToastUtils.toast(FeedbackActivity.this, "提交失败，" + e.getMessage());
                    }
                    finish();
                }
            });

        }
    }


    @Override
    public int getContentViewResId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }
}
