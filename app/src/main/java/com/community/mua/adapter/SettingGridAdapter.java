package com.community.mua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.SettingGridBean;

import java.util.List;

public class SettingGridAdapter extends RecyclerView.Adapter<SettingGridAdapter.Holder> {
    private List<SettingGridBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public SettingGridAdapter(List<SettingGridBean> list, OnItemClickListener onItemClickListener) {
        mList = list;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SettingGridAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SettingGridAdapter.Holder holder, int position) {
        SettingGridBean bean = mList.get(position);

        holder.mIvIcon.setImageResource(bean.getIcon());
        holder.mTvName.setText(bean.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(holder.getAbsoluteAdapterPosition(),bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected TextView mTvName;
        protected ImageView mIvIcon;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, SettingGridBean bean);
    }
}
