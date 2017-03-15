package com.icunsun.monitor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecyclerView的万能适配器
 * Created by Administrator on 2017/1/11.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonAdapter.CommonViewHolder> implements View.OnClickListener {
    protected List<T> mData;
    private LayoutInflater mInflater;
    protected RecyclerView mRecyclerView;
    private OnItemClickListener mListener;
    private Context mContext;

    public CommonAdapter(Context context, List<T> data) {
        mContext = context;
        if (data == null) {
            mData = new ArrayList<>();
        } else {
            mData = data;
        }
        mInflater = LayoutInflater.from(context);
    }


    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public Context getContent() {
        return mContext;
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * data的更新方法
     *
     * @param data
     */
    public void update(List<T> data) {
        if (data != null) {
            mData = data;
            this.notifyDataSetChanged();
        }
    }

    /**
     * data的增加方法
     *
     * @param data
     */
    public void addAll(List<T> data) {
        if (data != null) {
            mData.addAll(data);
            this.notifyDataSetChanged();
        }
    }

    public void add(T data) {
        if (data != null) {
            mData.add(data);
            notifyDataSetChanged();
        }
    }

    public T get(int i) {
        return mData.get(i);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(getItemLayoutId(viewType), parent, false);
        itemView.setOnClickListener(this);
        CommonViewHolder holder = new CommonViewHolder(itemView);
        return holder;

    }

    /**
     * 通过viewType，获取layoutId
     *
     * @param viewType
     * @return
     */
    protected abstract int getItemLayoutId(int viewType);

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        onBindDataToView(holder, mData.get(position), position);
    }

    /**
     * 将数据绑定到控件
     *
     * @param holder
     * @param t
     * @param position
     */
    protected abstract void onBindDataToView(CommonViewHolder holder, T t, int position);


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View view) {
        int position = mRecyclerView.getChildAdapterPosition(view);
        if (position >= 0 && position < mData.size()) {
            if (mListener != null) {
                mListener.onItemClick(view, position);
            }
        }
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder {

        private View mItemView;
        private Map<Integer, View> mViewsCache;

        public CommonViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mViewsCache = new HashMap<>();
        }

        public View getView(int viewId) {
            View view;
            if (mViewsCache.containsKey(viewId)) {
                view = mViewsCache.get(viewId);
            } else {
                view = mItemView.findViewById(viewId);
                mViewsCache.put(viewId, view);
            }
            return view;
        }
    }

    /**
     * RecyclerView的Item监听接口
     */
    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
