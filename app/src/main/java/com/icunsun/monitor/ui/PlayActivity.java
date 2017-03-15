package com.icunsun.monitor.ui;

import android.content.Intent;
import android.media.JetPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.icunsun.monitor.R;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.common.Urls;
import com.lzy.ninegrid.ImageInfo;

import butterknife.BindView;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class PlayActivity extends BaseActivity {

    @Override
    public int getContentViewResId() {
        return R.layout.activity_play;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            String video = intent.getStringExtra(Urls.VIDEO_URL);
            JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.custom_videoplayer_standard);
            jcVideoPlayerStandard.setUp(video, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "村尚i拍");
            jcVideoPlayerStandard.prepareVideo();
        }
    }

    @OnClick(R.id.layout_play)
    public void exit() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayerStandard.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
