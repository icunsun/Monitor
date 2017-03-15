package com.icunsun.monitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.common.Urls;
import com.icunsun.monitor.model.MonitorInfo;
import com.icunsun.monitor.ui.PlayActivity;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class MonitorInfoAdapter extends SingleBaseAdapter<MonitorInfo> {

    private final Context mContext;

    public MonitorInfoAdapter(Context context, List<MonitorInfo> data, int layoutId) {
        super(context, data, layoutId);
        mContext = context;
    }

    @Override
    protected void bindData(ViewHolder holder, final MonitorInfo item) {
        NineGridView gridView = (NineGridView) holder.getView(R.id.gv_pic);
        View state = holder.getView(R.id.image_state);
        TextView time = (TextView) holder.getView(R.id.tv_time);
        TextView content = (TextView) holder.getView(R.id.tv_content);
        TextView location = (TextView) holder.getView(R.id.tv_location);
        View view = holder.getView(R.id.iv_video);
        if (item.getState() == TypeConstants.STATE_REVIEW) {
            state.setVisibility(View.VISIBLE);
        } else {
            state.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video = item.getVideo();
                Intent intent = new Intent(mContext, PlayActivity.class);
                intent.putExtra(Urls.VIDEO_URL, video);
                mContext.startActivity(intent);
            }
        });
        List<ImageInfo> imageInfos = new ArrayList<>();
        List<String> pictures = item.getPictures();

        for (String s : pictures) {
            ImageInfo info = new ImageInfo();
            info.setBigImageUrl(s);
            info.setThumbnailUrl(s);
            imageInfos.add(info);
        }
        NineGridViewClickAdapter clickAdapter = new NineGridViewClickAdapter(mContext, imageInfos);
        gridView.setAdapter(clickAdapter);

        time.setText(item.getTime());
        content.setText(item.getContent());
        location.setText(item.getAddress());
    }

}
