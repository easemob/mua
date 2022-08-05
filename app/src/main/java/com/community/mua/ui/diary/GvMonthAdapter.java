package com.community.mua.ui.diary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.MonthBean;
import com.community.mua.utils.SoundUtil;

import java.util.List;

public class GvMonthAdapter extends RecyclerView.Adapter<GvMonthAdapter.ViewHolder> {

    private List<MonthBean> mMonthBeans;
    private Context mContext;
    private OnItemClickListener listener;

    public GvMonthAdapter(List<MonthBean> feelBeans, Context context) {
        mMonthBeans = feelBeans;
        mContext = context;
    }


    @NonNull
    @Override
    public GvMonthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_diary_month, parent, false);
        return new GvMonthAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GvMonthAdapter.ViewHolder holder, int position) {
        MonthBean monthBean = mMonthBeans.get(position);
        holder.name.setText(monthBean.getMonth() + " 月");

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                listener.onItemClick(position);
            }
        });
        if (monthBean.isSelect()) {
            holder.name.setBackgroundResource(R.drawable.month_highlight);
        } else {
            holder.name.setBackgroundColor(Color.TRANSPARENT);
        }
    }


    @Override
    public int getItemCount() {
        return mMonthBeans.size();
    }

    public void setOnItemClickListener(OnItemClickListener listenser) {
        this.listener = listenser;
    }
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_month);
        }
    }
}