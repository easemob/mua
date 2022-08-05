package com.community.mua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.PetMarketBean;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.UserUtils;

import java.util.List;

public class PetMarketAdapter extends RecyclerView.Adapter<PetMarketAdapter.Holder> {
    private List<PetMarketBean> mList;
    private OnCostChangeListener mOnCostChangeListener;

    private int mCost = 0;

    public PetMarketAdapter(List<PetMarketBean> list, OnCostChangeListener onCostChangeListener) {
        mList = list;
        mOnCostChangeListener = onCostChangeListener;
    }

    @NonNull
    @Override
    public PetMarketAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet_market, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PetMarketAdapter.Holder holder, int position) {
        PetMarketBean bean = mList.get(position);

        holder.mIvIcon.setImageResource(UserUtils.getPetFoodIconRes(position));
        holder.mIvTitle.setImageResource(UserUtils.getPetFoodTitleRes(position));
        holder.mTvDes.setText(bean.getDes());
        holder.mTvPrice.setText(String.valueOf(bean.getPrice()));
        holder.mTvCount.setText(String.valueOf(bean.getCount()));

        holder.mIvPlus.setOnClickListener(new OnCtrlListener(true, holder, bean));

        holder.mIvReduce.setOnClickListener(new OnCtrlListener(false, holder, bean));
    }

    private class OnCtrlListener implements View.OnClickListener {

        private boolean mIsPlus;
        private Holder mHolder;
        private PetMarketBean mBean;


        public OnCtrlListener(boolean isPlus, Holder holder, PetMarketBean bean) {

            mIsPlus = isPlus;
            mHolder = holder;
            mBean = bean;
        }

        @Override
        public void onClick(View view) {
            SoundUtil.getInstance().playBtnSound();
            int count = mBean.getCount();
            if (!mIsPlus && count == 0) {
                return;
            }

            if (mIsPlus) {
                mHolder.mTvCount.setText(String.valueOf(++count));
                mCost += mBean.getPrice();
            } else {
                mHolder.mTvCount.setText(String.valueOf(--count));
                mCost -= mBean.getPrice();
            }

            mBean.setCount(count);

            if (mOnCostChangeListener != null)
                mOnCostChangeListener.onCostChange(mCost);

        }
    }

    public void confirmPay() {
        for (PetMarketBean bean : mList) {
            bean.setInventory(bean.getCount());
            bean.setCount(0);
        }
        mCost = 0;
        notifyDataSetChanged();
    }

    public interface OnCostChangeListener {
        void onCostChange(int cost);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected TextView mTvCount;
        protected TextView mTvDes;
        protected ImageView mIvTitle;
        protected ImageView mIvIcon;
        protected TextView mTvPrice;
        protected ImageView mIvPlus;
        protected ImageView mIvReduce;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mIvTitle = itemView.findViewById(R.id.iv_title);
            mTvDes = itemView.findViewById(R.id.tv_des);
            mTvPrice = itemView.findViewById(R.id.tv_price);
            mTvCount = itemView.findViewById(R.id.tv_count);

            mIvReduce = itemView.findViewById(R.id.iv_reduce);
            mIvPlus = itemView.findViewById(R.id.iv_plus);
        }
    }
}
