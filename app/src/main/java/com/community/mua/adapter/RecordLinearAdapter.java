package com.community.mua.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.LoginRecordBean;
import com.community.mua.bean.UserBean;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.UserUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecordLinearAdapter extends RecyclerView.Adapter<RecordLinearAdapter.Holder>{

    private List<LoginRecordBean> mList;
    private RecordLinearAdapter.OnItemClickListener mOnItemClickListener;
    private Context mContext;

    public RecordLinearAdapter(List<LoginRecordBean> list, Context context) {
        mList = list;
        mContext = context;
    }

    public RecordLinearAdapter(List<LoginRecordBean> list, RecordLinearAdapter.OnItemClickListener onItemClickListener, Context context) {
        mList = list;
        mOnItemClickListener = onItemClickListener;
        mContext = context;
    }

    @NonNull
    @Override
    public RecordLinearAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordLinearAdapter.Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_day_linear, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordLinearAdapter.Holder holder, int position) {
        LoginRecordBean bean = mList.get(position);


        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
        if (TextUtils.equals(userBean.getUserid(), bean.getUserId())) {
            UserUtils.setAvatar(mContext, userBean, holder.mIvAvatar);
            holder.mFrameLayout.setBackgroundResource(userBean.getGender() == 0 ? R.drawable.sp_ff007a : R.drawable.sp_00ffe0);
        } else {
            holder.mFrameLayout.setBackgroundResource(taBean.getGender() == 0 ? R.drawable.sp_ff007a : R.drawable.sp_00ffe0);
            UserUtils.setAvatar(mContext, taBean, holder.mIvAvatar);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        holder.mTvTime.setText("上线时间 "+simpleDateFormat.format(Long.parseLong(bean.getTimeStampLogin())));
        holder.mTvLocation.setText(bean.getLoginAddress());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        private LinearLayout mLinearLayout;
        private CardView mCardView;
        private FrameLayout mFrameLayout;
        protected ImageView mIvAvatar;
        protected TextView mTvTime;
        protected TextView mTvLocation;

        public Holder(@NonNull View itemView) {
            super(itemView);
            mFrameLayout = itemView.findViewById(R.id.fl_avatar_mine);
            mIvAvatar = itemView.findViewById(R.id.iv_avatar);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvLocation = itemView.findViewById(R.id.tv_location);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, LoginRecordBean bean);
    }
}
