package com.community.mua.imkit.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.community.mua.imkit.interfaces.MessageListItemClickListener;
import com.community.mua.imkit.model.styles.EaseMessageListItemStyle;
import com.community.mua.imkit.viewholder.EaseChatRowViewHolder;
import com.community.mua.imkit.viewholder.EaseTextViewHolder;
import com.community.mua.imkit.widget.chatrow.EaseChatRow;
import com.community.mua.imkit.widget.chatrow.EaseChatRowText;
import com.hyphenate.chat.EMMessage;


/**
 * 文本代理类
 */
public class EaseTextAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseTextAdapterDelegate() {
    }

    public EaseTextAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.TXT;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowText(parent.getContext(), isSender);
    }

    @Override
    public EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseTextViewHolder(view, itemClickListener);
    }

}
