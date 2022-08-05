package com.community.mua.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.RecordBean;
import com.community.mua.bean.SettingGridBean;

import java.util.List;

public class RecordGridAdapter extends RecyclerView.Adapter<RecordGridAdapter.Holder> {
    private List<RecordBean> mList;
    private RecordGridAdapter.OnItemClickListener mOnItemClickListener;

    public RecordGridAdapter(List<RecordBean> list, RecordGridAdapter.OnItemClickListener onItemClickListener) {
        mList = list;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecordGridAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordGridAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_day_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordGridAdapter.Holder holder, int position) {
        RecordBean bean = mList.get(position);

        holder.mTvWeek.setText(bean.getWork());
        holder.mTvDay.setText(bean.getDay());

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(holder.getAbsoluteAdapterPosition(),bean);
            }
        });

        if(bean.isSelect()){
            holder.mCardView.setCardBackgroundColor(Color.parseColor("#B1A2FF"));
            holder.mTvWeek.setTextColor(Color.WHITE);
            holder.mTvDay.setTextColor(Color.WHITE);
        } else {
            holder.mCardView.setCardBackgroundColor(Color.parseColor("#FFEEF8"));
            holder.mTvWeek.setTextColor(Color.parseColor("#A594FE"));
            holder.mTvDay.setTextColor(Color.parseColor("#A594FE"));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        private LinearLayout mLinearLayout;
        private CardView mCardView;
        protected TextView mTvWeek;
        protected TextView mTvDay;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mLinearLayout = itemView.findViewById(R.id.ll_record);
            mCardView = itemView.findViewById(R.id.card_view);
            mTvWeek = itemView.findViewById(R.id.tv_week);
            mTvDay = itemView.findViewById(R.id.tv_day);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, RecordBean bean);
    }
}
