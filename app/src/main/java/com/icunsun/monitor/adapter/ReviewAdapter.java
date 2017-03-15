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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> implements View.OnClickListener {

    private List<MonitorInfo> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private RecyclerView mRecyclerView;
    private AddressAdapter.OnItemClickListener mListener;

    private int mLastPosition = -1;

    public void setListener(AddressAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public ReviewAdapter(List<MonitorInfo> data, Context context) {
        if (data == null) {
            mData = new ArrayList<>();
        } else {
            mData = data;
        }
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_review, parent, false);
        itemView.setOnClickListener(this);
        ReviewViewHolder holder = new ReviewViewHolder(itemView);
        return holder;
    }

    public MonitorInfo getItem(int pos) {
        return mData.get(pos);
    }


    /**
     * 清空所以数据
     */
    public void clearAll() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void remove(int index) {
        if (index >= 0 && index < mData.size())
            mData.remove(index);
        notifyDataSetChanged();
    }

    private void setAnimation(View view, int position) {
        if (position > mLastPosition && position > 0) {
            Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.item_right_in);
            view.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

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
        holder.refuse.setOnClickListener(this);
        holder.pass.setOnClickListener(this);

        setAnimation((View) holder.video.getParent().getParent(), position);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public void onViewDetachedFromWindow(ReviewViewHolder holder) {
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
        int position;
        switch (v.getId()) {
            case R.id.iv_video:
                position = mRecyclerView.getChildAdapterPosition((View) v.getParent().getParent().getParent());
                break;
            case R.id.iv_pass:
            case R.id.iv_refuse:
                position = mRecyclerView.getChildAdapterPosition((View) v.getParent());
                break;
            default:
                position = mRecyclerView.getChildAdapterPosition(v);
                break;
        }
        if (position >= 0 && position < mData.size()) {
            if (mListener != null) {
                mListener.onItemClick(v, position);
            }
        }

    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView name;
        TextView content;
        TextView location;
        ImageView video;
        NineGridView gridView;
        View pass;
        View refuse;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            refuse = itemView.findViewById(R.id.iv_refuse);
            pass = itemView.findViewById(R.id.iv_pass);
            gridView = (NineGridView) itemView.findViewById(R.id.gv_pic);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            location = (TextView) itemView.findViewById(R.id.tv_location);
            video = (ImageView) itemView.findViewById(R.id.iv_video);
        }
    }
}
