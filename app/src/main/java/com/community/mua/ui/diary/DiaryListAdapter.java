package com.community.mua.ui.diary;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.community.mua.R;
import com.community.mua.bean.DiaryBean;
import com.community.mua.bean.GvFeelBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.utils.BatteryUtils;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.UserUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder>{

    private Context mContext;
    private List<DiaryBean> mList ;

    public DiaryListAdapter(Context context, List<DiaryBean> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public DiaryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_diary_list, parent, false);
        return new DiaryListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryListAdapter.ViewHolder holder, int position) {
        DiaryBean diaryBean = mList.get(position);



        holder.content.setText(diaryBean.getContent());
        GvFeelBean feelBean = BatteryUtils.list.get(Integer.parseInt(diaryBean.getMood()));
        holder.ivMood.setImageResource(feelBean.getDesId());
        holder.tvMood.setText(feelBean.getName());

        long diaryTimeStamp = diaryBean.getDiaryTimeStamp();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM");//转成后的时间的格式
        String sd = sdf.format(new Date(diaryTimeStamp));   // 时间戳转换成时间

        holder.dayMonth.setText(sd);
        holder.week.setText(DateUtil.getWeek(diaryTimeStamp));

        if (!TextUtils.isEmpty(diaryBean.getPic())){
            Glide.with(mContext).load(Constants.HTTP_HOST + diaryBean.getPic()).into(holder.pic);
        }

        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        if (TextUtils.equals(userBean.getUserid(),diaryBean.getUserId())) {
            UserUtils.setAvatar(mContext, userBean, holder.avatar);
            holder.mFrameLayout.setBackgroundResource(userBean.getGender() == 0 ? R.drawable.sp_ff007a : R.drawable.sp_00ffe0);
            holder.trash.setVisibility(View.VISIBLE);
        } else {
            UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
            UserUtils.setAvatar(mContext, taBean, holder.avatar);
            holder.mFrameLayout.setBackgroundResource(taBean.getGender() == 0 ? R.drawable.sp_ff007a : R.drawable.sp_00ffe0);
            holder.trash.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mList == null ?0 : mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView dayMonth,week,tvMood,content;
        public FrameLayout mFrameLayout;
        public ImageView avatar,ivMood,pic,trash;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayMonth = itemView.findViewById(R.id.tv_day_month);
            week = itemView.findViewById(R.id.tv_week);
            tvMood = itemView.findViewById(R.id.tv_diary_moon);
            content = itemView.findViewById(R.id.et_content);
            mFrameLayout = itemView.findViewById(R.id.fl_avatar_mine);
            avatar = itemView.findViewById(R.id.iv_avatar_mine);
            ivMood = itemView.findViewById(R.id.iv_diary_moon);
            pic = itemView.findViewById(R.id.iv_diary_pic);
            trash = itemView.findViewById(R.id.iv_diary_trash);
        }
    }
}
