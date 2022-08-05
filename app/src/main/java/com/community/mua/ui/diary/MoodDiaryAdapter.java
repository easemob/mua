package com.community.mua.ui.diary;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.DayBean;
import com.community.mua.bean.DiaryBean;
import com.community.mua.utils.BatteryUtils;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.ToastUtil;

import java.util.Calendar;
import java.util.List;

public class MoodDiaryAdapter extends RecyclerView.Adapter<MoodDiaryAdapter.ViewHolder>  {
    private List<DayBean> mDayBeans;
    private Context mContext;
    private int mRecyclerViewHeight;
    OnItemClickListener listener;

    public MoodDiaryAdapter(List<DayBean> dayBeans, Context context) {
        mDayBeans = dayBeans;
        mContext = context;
    }


    public void setOnItemClickListener(OnItemClickListener listenser) {
        this.listener = listenser;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.month_day_view, parent, false);
        return new  ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DayBean dayBean = mDayBeans.get(position);
        if (TextUtils.equals("今天",dayBean.getDay())) {
            holder.t1.setBackgroundColor(Color.parseColor("#B1A2FF"));
        } else{
            holder.t1.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.t1.setText(dayBean.getDay());

        List<DiaryBean> diaryBeans = dayBean.getDiaryBeans();
        Log.e("wyz", "diaryBeans  : " + (diaryBeans == null ? 0:diaryBeans.size()));
        if (diaryBeans != null &&  diaryBeans.size() > 0){
            holder.one.setImageResource(BatteryUtils.list.get(Integer.parseInt(diaryBeans.get(0).getMood())).getDesId());
        } else {
            holder.one.setBackgroundColor(Color.TRANSPARENT);
        }

        if (diaryBeans != null &&  diaryBeans.size() > 1){
            holder.two.setImageResource(BatteryUtils.list.get(Integer.parseInt(diaryBeans.get(0).getMood())).getDesId());
        } else {
            holder.two.setBackgroundColor(Color.TRANSPARENT);
        }

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
            holder.one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SoundUtil.getInstance().playBtnSound();
                    if ((!dayBean.isEmpty() && (!TextUtils.equals("今天",dayBean.getDay()) && Integer.parseInt(dayBean.getDay()) < day)) ||  TextUtils.equals("今天",dayBean.getDay())) {
                        if (dayBean.getDiaryBeans() == null || dayBean.getDiaryBeans().size() < 1) {
                            AddDiaryActivity.start(mContext,dayBean.getDate());
                        } else {
                            //日记本界面
                            DiaryListActivity.start(mContext);
//                            WriteDiaryActivity.start(mContext,dayBean.getDiaryBeans().get(0),true);
                        }
                    } else {
                        ToastUtil.show("不能写未来日记哦!");
                    }
                }
            });

//            holder.two.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if ((!dayBean.isEmpty() && (!TextUtils.equals("今天",dayBean.getDay()) && Integer.parseInt(dayBean.getDay()) < day)) ||  TextUtils.equals("今天",dayBean.getDay())) {
//
//                        if (dayBean.getDiaryBeans() == null || dayBean.getDiaryBeans().size() < 2) {
//                            AddDiaryActivity.start(mContext,dayBean.getDate());
//                        } else {
//                            WriteDiaryActivity.start(mContext,dayBean.getDiaryBeans().get(1),true);
//                        }
//
//                    } else {
//                        ToastUtils.show("不能写未来日记哦！");
//                    }
//
//
//                }
//            });

    }

    @Override
    public int getItemCount() {
        return mDayBeans.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{
       public TextView t1;
       public ImageView one,two;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.t1);
            one = itemView.findViewById(R.id.iv_diary_one);
            two = itemView.findViewById(R.id.iv_diary_two);
        }
    }
}
