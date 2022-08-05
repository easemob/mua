package com.community.mua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.community.mua.R;
import com.community.mua.bean.ChangeChatBgBean;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.SharedPreferUtil;

import java.util.List;

public class ChangeChatBgAdapter extends RecyclerView.Adapter<ChangeChatBgAdapter.Holder> {
    private List<ChangeChatBgBean> mList;

    public ChangeChatBgAdapter(List<ChangeChatBgBean> list) {
        mList = list;
    }

    @NonNull
    @Override
    public ChangeChatBgAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_chat_bg, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeChatBgAdapter.Holder holder, int position) {
        ChangeChatBgBean bean = mList.get(position);

        holder.mTvTitle.setText(bean.getTitle());
        holder.mIvContent.setImageResource(bean.getImg());

        boolean isSelect = SharedPreferUtil.getInstance().isChatBgSelected(bean.getTitle());
        holder.mIvChange.setImageResource(isSelect ? R.drawable.btn_hign_light : R.drawable.btn_change_gray);

        holder.mIvChange.setOnClickListener(isSelect ? null : new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                SharedPreferUtil.getInstance().setChatBg(bean.getTitle());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected ImageView mIvContent;
        protected TextView mTvTitle;
        protected ImageView mIvChange;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mIvContent = itemView.findViewById(R.id.iv_content);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mIvChange = itemView.findViewById(R.id.iv_change);
        }
    }
}
