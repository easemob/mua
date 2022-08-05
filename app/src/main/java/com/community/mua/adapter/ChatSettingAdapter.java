package com.community.mua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;

import java.util.List;

public class ChatSettingAdapter extends RecyclerView.Adapter<ChatSettingAdapter.Holder> {

    private List<String> mList;
    private OnItemClickListener mOnItemClickListener;

    public ChatSettingAdapter(List<String> list, OnItemClickListener onItemClickListener) {
        mList = list;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ChatSettingAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_setting, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatSettingAdapter.Holder holder, int position) {
        String name = mList.get(position);
        holder.mTvName.setText(name);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onClick(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected TextView mTvName;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnItemClickListener {
        void onClick(int pos);
    }
}
