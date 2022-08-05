package com.community.mua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.AnniversaryBean;
import com.community.mua.utils.DateUtil;

import java.util.List;

public class AnniversaryAdapter extends RecyclerView.Adapter<AnniversaryAdapter.Holder> {
    private List<AnniversaryBean> mList;
    private OnLongClickListener mOnLongClickListener;

    public AnniversaryAdapter(List<AnniversaryBean> list, OnLongClickListener onLongClickListener) {
        mList = list;
        mOnLongClickListener = onLongClickListener;
    }

    public AnniversaryAdapter(OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setData(List<AnniversaryBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnniversaryAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anniversary, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnniversaryAdapter.Holder holder, int position) {
        AnniversaryBean bean = mList.get(position);
        long time = bean.getAnniversaryTime();

        String txt = "";
        long l;
        if (bean.getRepeat()) {
            l = DateUtil.getDurationDaysRepeat(time);
            if (l != 0) {
                txt = "还有";
            }
        } else {
            l = DateUtil.getDuringDay(time);
            if (l > 0) {
                txt = "已经";
            } else if (l < 0) {
                txt = "还有";
            }
        }

        holder.mTvName.setText(String.format("%s%s", bean.getAnniversaryName(), txt));
        holder.mTvTime.setText(DateUtil.stampToDate(bean.getRepeat(), time));


        holder.mTvExtra.setVisibility(l == 0 ? View.GONE : View.VISIBLE);
        holder.mTvCount.setText(l == 0 ? "今天" : String.valueOf(Math.abs(l)));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int pos = holder.getAbsoluteAdapterPosition();
                if (pos == 0) {
                    return true;
                }
                mOnLongClickListener.onItemLongClick(holder.itemView, bean, pos);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected TextView mTvCount;
        protected TextView mTvTime;
        protected TextView mTvName;
        protected TextView mTvExtra;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mTvName = itemView.findViewById(R.id.tv_name);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvCount = itemView.findViewById(R.id.tv_count);
            mTvExtra = itemView.findViewById(R.id.tv_extra);
        }
    }

    public interface OnLongClickListener {
        void onItemLongClick(View itemView, AnniversaryBean bean, int pos);
    }
}
