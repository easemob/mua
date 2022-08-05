package com.community.mua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.LoveListBean;
import com.community.mua.utils.SharedPreferUtil;

import java.util.List;

public class LoveListAdapter extends RecyclerView.Adapter<LoveListAdapter.Holder> {
    private OnEnterClickListener mOnEnterClickListener;

    public LoveListAdapter(OnEnterClickListener onEnterClickListener) {
        mOnEnterClickListener = onEnterClickListener;
    }

    @NonNull
    @Override
    public LoveListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_love_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LoveListAdapter.Holder holder, int position) {
//        LoveListBean bean = mList.get(position);

        LoveListBean bean = SharedPreferUtil.getInstance().getLoveListBean(position);

        holder.mIvCover.setImageResource(getCover(position));
        holder.mTvTitle.setText(bean.getTitle());
        holder.mTvSub.setText(bean.getSubTitle());

        holder.mIvFinished.setVisibility(bean.isFinish() ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEnterClickListener != null)
                    mOnEnterClickListener.onEnter(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    private int getCover(int pos){
        if (pos == 0){
            return R.drawable.watch_film;
        }
        if (pos == 1){
            return R.drawable.cooking;
        }
        return R.drawable.loveletter;
    }

    @Override
    public int getItemCount() {
        return SharedPreferUtil.getInstance().getLoveList().size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected ImageView mIvCover;
        protected TextView mTvTitle;
        protected TextView mTvSub;
        protected ImageView mIvFinished;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mIvCover = itemView.findViewById(R.id.iv_cover);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvSub = itemView.findViewById(R.id.tv_sub);
            mIvFinished = itemView.findViewById(R.id.iv_finished);
        }
    }

    public interface OnEnterClickListener {
        void onEnter(int pos);
    }
}
