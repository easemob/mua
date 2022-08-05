package com.community.mua.adapter;

import static com.community.mua.utils.CatFeedUtil.canFeedCat;

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

public class PetFoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PetMarketBean> mList;
    private OnItemClickListener mOnItemClickListener;
    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_NORMAL = 1;

    public PetFoodsAdapter(List<PetMarketBean> list, OnItemClickListener onItemClickListener) {
        mList = list;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mList == null || mList.isEmpty()) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    public interface OnItemClickListener {
        void onFeed();

        void onEnterMarket();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new EmptyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_empty, parent, false));
        }
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_EMPTY) {
            EmptyHolder holder = (EmptyHolder) viewHolder;
            holder.mIvEnter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onEnterMarket();
                }
            });
            return;
        }

        Holder holder = (Holder) viewHolder;
        PetMarketBean bean = mList.get(position);

        holder.mIvIcon.setImageResource(UserUtils.getPetFoodIconRes(position));
        holder.mTvCount.setText(String.valueOf(bean.getInventory()));

        holder.itemView.setOnClickListener(bean.getInventory() > 0 ? new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                if (!canFeedCat()){
                    return;
                }
                int inventory = bean.getInventory();
                bean.setInventory(--inventory);
                mList.set(position, bean);
                notifyDataSetChanged();

                if (mOnItemClickListener != null)
                    mOnItemClickListener.onFeed();
            }
        } : null);
    }



    @Override
    public int getItemCount() {
        return mList == null || mList.isEmpty() ? 1 : mList.size();
    }

    protected class EmptyHolder extends RecyclerView.ViewHolder {

        protected ImageView mIvEnter;

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);

            mIvEnter = itemView.findViewById(R.id.iv_enter);
        }
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected TextView mTvCount;
        protected ImageView mIvIcon;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mTvCount = itemView.findViewById(R.id.tv_count);
        }
    }
}
