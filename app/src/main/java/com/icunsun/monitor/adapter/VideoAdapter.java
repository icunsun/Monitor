package com.icunsun.monitor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.icunsun.monitor.R;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.Video;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Administrator on 2017/3/3 0003.
 */

public class VideoAdapter extends CommonAdapter<Video> {

    public VideoAdapter(Context context, List<Video> data) {
        super(context, data);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_video;
    }

    @Override
    protected void onBindDataToView(CommonViewHolder holder, Video video, int position) {
        ImageView preview = (ImageView) holder.getView(R.id.item_video_preview);
        ImageView isVideo = (ImageView) holder.getView(R.id.image_video);
        View selected = holder.getView(R.id.item_video_selected);

        if (position == 0) {
            preview.setVisibility(View.GONE);
            isVideo.setImageResource(R.mipmap.video);
            preview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            preview.setVisibility(View.VISIBLE);
            preview.setAdjustViewBounds(true);
            preview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(getContent())
                    .load(video.getSdcardPath())
                    .into(preview);

        }
        if (video.isSeleted()) {
            selected.setVisibility(View.VISIBLE);
        } else {
            selected.setVisibility(View.GONE);
        }
    }
}
