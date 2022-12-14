package com.community.mua.imkit.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.community.mua.imkit.interfaces.MessageListItemClickListener;
import com.community.mua.imkit.viewholder.EaseChatRowViewHolder;
import com.community.mua.imkit.viewholder.EaseCustomViewHolder;
import com.community.mua.imkit.widget.chatrow.EaseChatRow;
import com.community.mua.imkit.widget.chatrow.EaseChatRowCustom;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;


public class EaseCustomAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.CUSTOM;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowCustom(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseCustomViewHolder(view, itemClickListener);
    }
}
