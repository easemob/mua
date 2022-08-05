package com.community.mua.imchat.recall;

import static com.hyphenate.chat.EMMessage.Type.TXT;

import android.view.View;
import android.view.ViewGroup;

import com.community.mua.imkit.constants.EaseConstant;
import com.community.mua.imkit.delegate.EaseMessageAdapterDelegate;
import com.community.mua.imkit.interfaces.MessageListItemClickListener;
import com.community.mua.imkit.viewholder.EaseChatRowViewHolder;
import com.community.mua.imkit.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;


public class ChatRecallAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == TXT && item.getBooleanAttribute(EaseConstant.MESSAGE_TYPE_RECALL, false);
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowRecall(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatRecallViewHolder(view, itemClickListener);
    }
}
