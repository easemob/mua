package com.community.mua.imkit.modules.conversation.delegate;


import com.community.mua.imkit.adapter.EaseAdapterDelegate;
import com.community.mua.imkit.adapter.EaseBaseRecyclerViewAdapter;
import com.community.mua.imkit.modules.conversation.model.EaseConversationSetStyle;

public abstract class EaseBaseConversationDelegate<T, VH extends EaseBaseRecyclerViewAdapter.ViewHolder<T>> extends EaseAdapterDelegate<T, VH> {
    public EaseConversationSetStyle setModel;

    public void setSetModel(EaseConversationSetStyle setModel) {
        this.setModel = setModel;
    }

    public EaseBaseConversationDelegate(EaseConversationSetStyle setModel) {
        this.setModel = setModel;
    }
}

