package com.community.mua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.MusicBean;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
    private List<MusicBean> mList;
    private OnItemClickListener mOnItemClickListener;
    private int mCheckPos;

    public MusicAdapter(List<MusicBean> list, OnItemClickListener onItemClickListener) {
        mList = list;
        mOnItemClickListener = onItemClickListener;
    }

    public void setCheckPos(int pos) {
        mCheckPos = pos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MusicAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.Holder holder, int position) {
        MusicBean bean = mList.get(position);

        holder.mIvIcon.setImageResource(bean.getIcon());
        holder.mTvName.setText(bean.getName());

        holder.mFlMusic.setBackgroundResource(position == mCheckPos ? R.drawable.sp_music : R.color.transparent);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckPos(holder.getAbsoluteAdapterPosition());
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(mCheckPos,bean);
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
        protected FrameLayout mFlMusic;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mTvName = itemView.findViewById(R.id.tv_name);
            mFlMusic = itemView.findViewById(R.id.fl_music);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, MusicBean bean);
    }
}
