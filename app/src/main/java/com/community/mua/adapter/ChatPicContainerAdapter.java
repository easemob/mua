package com.community.mua.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.community.mua.R;
import com.community.mua.bean.ChatPicContainerBean;

import java.util.List;

public class ChatPicContainerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_NORMAL = 1;
    private List<ChatPicContainerBean> mList;
    private ChatPicHistoryAdapter mAdapter;
    private Context mContext;

    public ChatPicContainerAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ChatPicContainerBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mList == null || mList.isEmpty()) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            return new EmptyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty, parent, false));
        }
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_pic_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_EMPTY) {
            return;
        }

        viewHolder.setIsRecyclable(false);

        ChatPicContainerBean containerBean = mList.get(position);

        Holder holder = (Holder) viewHolder;
        mAdapter = (ChatPicHistoryAdapter) holder.mRv.getAdapter();
        if (mAdapter == null) {
            mAdapter = new ChatPicHistoryAdapter(mContext,containerBean.getMessages());
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
            holder.mRv.setLayoutManager(manager);
            manager.setAutoMeasureEnabled(true);
            holder.mRv.setAdapter(mAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null || mList.isEmpty() ? 1 : mList.size();
    }

    protected class Holder extends RecyclerView.ViewHolder {

        protected TextView mTvTime;
        protected RecyclerView mRv;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mTvTime = itemView.findViewById(R.id.tv_time);
            mRv = itemView.findViewById(R.id.rv);
        }
    }

    protected class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
