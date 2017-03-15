package com.icunsun.monitor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.model.MonitorInfo;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/9.
 */

public class EnterpriseAdapter extends RecyclerView.Adapter<EnterpriseAdapter.ViewHolder> implements View.OnClickListener {

    private List<MonitorInfo> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private RecyclerView mRecyclerView;
    private AddressAdapter.OnItemClickListener mListener;

    private int mLastPosition = -1;

    public void setListener(AddressAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public EnterpriseAdapter(List<MonitorInfo> data, Context context) {
        if (data == null) {
            mData = new ArrayList<>();
        } else {
            mData = data;
        }
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_enterprise, parent, false);
        itemView.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    public MonitorInfo getItem(int pos) {
        return mData.get(pos);
    }


    /**
     * 清空所以数据
     */
    public void  clearAll(){
        mData.clear();
        notifyDataSetChanged();
    }

    private void setAnimation(View view, int position) {
        if (position > mLastPosition && position > 0) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.item_bottom_in);
            view.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        MonitorInfo item = mData.get(position);
        List<ImageInfo> imageInfos = new ArrayList<>();
        List<String> pictures = item.getPictures();

        for (String s : pictures) {
            ImageInfo info = new ImageInfo();
            info.setBigImageUrl(s);
            info.setThumbnailUrl(s);
            imageInfos.add(info);
        }

        NineGridViewClickAdapter adapter = new NineGridViewClickAdapter(mContext, imageInfos);
        holder.gridView.setAdapter(adapter);
        holder.name.setText(item.getName());
        holder.time.setText(item.getTime());
        holder.content.setText(item.getContent());
        holder.location.setText(item.getAddress());
        holder.video.setOnClickListener(this);

        setAnimation((View) holder.video.getParent(), position);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        ((View) holder.video.getParent()).clearAnimation();
    }

    @Override
    public int getItemCount() {
        return (mData == null) ? 0 : mData.size();
    }

    public void updateData(List<MonitorInfo> list) {
        if (list != null) {
            mData.clear();
            mData.addAll(list);
            this.notifyDataSetChanged();
        }
    }

    public void addData(List<MonitorInfo> list) {
        if (list != null) {
            mData.addAll(list);
            this.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_video:
                int position = mRecyclerView.getChildAdapterPosition((View) v.getParent());
                if (position >= 0 && position < mData.size()) {
                    if (mListener != null) {
                        mListener.onItemClick(v, position);
                    }
                }
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView name;
        TextView content;
        TextView location;
        ImageView video;
        NineGridView gridView;

        public ViewHolder(View itemView) {
            super(itemView);
            gridView = (NineGridView) itemView.findViewById(R.id.gv_pic);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            location = (TextView) itemView.findViewById(R.id.tv_location);
            video = (ImageView) itemView.findViewById(R.id.iv_video);
        }
    }
}
