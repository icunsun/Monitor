package com.icunsun.monitor.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.icunsun.monitor.R;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.widget.MyGridView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class UploadImageAdapter extends BaseAdapter {
    private final Context mContext;
    private List<String> mData;
    private LayoutInflater mInflater;

    public UploadImageAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_upload_image, parent, false);
            holder.image = (ImageView) convertView.findViewById(R.id.item_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (((MyGridView) parent).isOnMeasure) {
            return convertView;
        }
        String url = mData.get(position);

        if (url.equals(TypeConstants.ADD)) {
            holder.image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.btn_add_bg));
        } else {
            Glide.with(mContext)
                    .load(url)
                    .placeholder(R.mipmap.icon_default)
                    .into(holder.image);
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
    }
}
