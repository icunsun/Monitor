package com.icunsun.monitor.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.icunsun.monitor.R;
import com.icunsun.monitor.model.ImageItem;

import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class ImageAdapter extends SingleBaseAdapter<ImageItem> {

    private final Context mContext;

    public ImageAdapter(Context context, List<ImageItem> data, int layoutId) {
        super(context, data, layoutId);
        mContext = context;
    }

    @Override
    protected void bindData(ViewHolder holder, ImageItem item) {
        ImageView imageView = (ImageView) holder.getView(R.id.item_image);
        View view = holder.getView(R.id.view_video);
        Glide.with(mContext)
                .load(item.getUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
        if (item.isVideo()) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}
