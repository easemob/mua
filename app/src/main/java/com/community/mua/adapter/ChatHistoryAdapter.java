package com.community.mua.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.community.mua.R;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.imkit.utils.EaseCommonUtils;
import com.community.mua.imkit.utils.EaseDateUtils;
import com.community.mua.imkit.utils.EaseEditTextUtils;
import com.community.mua.imkit.utils.EaseSmileUtils;
import com.community.mua.utils.UserUtils;
import com.hyphenate.chat.EMMessage;

import java.util.Date;
import java.util.List;

public class ChatHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_NORMAL = 1;
    private List<EMMessage> mList;
    private String mKey;
    private Context mContext;

    public ChatHistoryAdapter(Context context) {
        mContext = context;
    }

    public ChatHistoryAdapter(List<EMMessage> list) {
        mList = list;
    }

    public void setData(List<EMMessage> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void clear() {
        if (mList == null || mList.isEmpty()) {
            return;
        }
        mList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new EmptyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false));
        }
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_EMPTY) {
            return;
        }

        EMMessage message = mList.get(position);
        String content = EaseSmileUtils.getSmiledText(mContext, EaseCommonUtils.getMessageDigest(message, mContext)).toString();

        UserBean userBean = UserUtils.getUserBean(message.getFrom());
        if (userBean == null){
            return;
        }
        Holder holder = (Holder) viewHolder;

        holder.mTvName.setText(userBean.getNickname());

        String url = Constants.HTTP_HOST + userBean.getAvatar();
        RequestOptions options = new RequestOptions()
                .placeholder(userBean.getGender() == 0?R.drawable.male_avatar:R.mipmap.female_avatar)
                .error(userBean.getGender() == 0?R.drawable.male_avatar:R.mipmap.female_avatar);

        Glide.with(mContext).applyDefaultRequestOptions(options).load(url).into(holder.mIvAvatar);

        holder.mTvTime.setText(EaseDateUtils.getTimestampString(mContext, new Date(message.getMsgTime())));

        holder.mTvMsg.post(() -> {
            String subContent = EaseEditTextUtils.ellipsizeString(holder.mTvMsg, content, mKey, holder.mTvMsg.getWidth());
            SpannableStringBuilder builder = EaseEditTextUtils.highLightKeyword(mContext, subContent, mKey);
            if (builder != null) {
                holder.mTvMsg.setText(builder);
            } else {
                holder.mTvMsg.setText(content);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (mList == null || mList.isEmpty()) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return mList == null || mList.isEmpty() ? 1 : mList.size();
    }

    public void setKeyword(String key) {
        mKey = key;
        notifyDataSetChanged();
    }

    protected class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected ImageView mIvAvatar;
        protected TextView mTvName;
        protected TextView mTvMsg;
        protected TextView mTvTime;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mIvAvatar = itemView.findViewById(R.id.iv_avatar);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvMsg = itemView.findViewById(R.id.tv_msg);
            mTvTime = itemView.findViewById(R.id.tv_time);
        }
    }
} 
