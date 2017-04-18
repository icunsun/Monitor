package com.icunsun.monitor.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icunsun.monitor.R;
import com.icunsun.monitor.common.TypeConstants;
import com.icunsun.monitor.model.Province;
import com.icunsun.monitor.ui.SelectPlaceActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/30.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> implements View.OnClickListener {


    private List<Province> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private RecyclerView mRecyclerView;
    private OnItemClickListener mListener;

    public AddressAdapter(List<Province> data, Context context) {
        if (data == null) {
            mData = new ArrayList<>();
        } else {
            mData = data;
        }
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }


    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getProvinceCode();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AddressViewHolder holder;
        if (viewType == TypeConstants.HEADER) {
            View itemView = mInflater.inflate(R.layout.adress_header, parent, false);
            holder = new AddressViewHolder(itemView, viewType);
        } else {
            View itemView = mInflater.inflate(R.layout.item_address, parent, false);
            itemView.setOnClickListener(this);
            holder = new AddressViewHolder(itemView, viewType);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, int position) {
        if (getItemViewType(position) == TypeConstants.HEADER) {
            SharedPreferences preferences = mContext.getSharedPreferences(SelectPlaceActivity.SELECT_ADDRESS, Context.MODE_PRIVATE);
            int code = preferences.getInt(SelectPlaceActivity.SELECT_ADDRESS, TypeConstants.ALL);
            for (Province p : mData) {
                if (p.getProvinceCode() == code) {
                    holder.address.setText(p.getProvinceName());
                }
            }
        } else {
            holder.address.setText(mData.get(position).getProvinceName());
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public void onClick(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        if (position >= 0 && position < mData.size()) {
            if (mListener != null) {
                mListener.onItemClick(v, position);
            }
        }
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView address;

        public AddressViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == TypeConstants.HEADER)
                address = (TextView) itemView.findViewById(R.id.tv_cur_address);
            else
                address = (TextView) itemView.findViewById(R.id.item_address);
        }
    }


    /**
     * 定义接口回调响应RecyclerView的Item点击监听
     */
    public static interface OnItemClickListener {

        void onItemClick(View view, int position);
    }
}
