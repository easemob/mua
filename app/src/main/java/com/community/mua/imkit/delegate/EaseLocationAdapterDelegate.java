package com.community.mua.imkit.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.community.mua.imkit.interfaces.MessageListItemClickListener;
import com.community.mua.imkit.model.styles.EaseMessageListItemStyle;
import com.community.mua.imkit.viewholder.EaseChatRowViewHolder;
import com.community.mua.imkit.viewholder.EaseLocationViewHolder;
import com.community.mua.imkit.widget.chatrow.EaseChatRow;
import com.community.mua.imkit.widget.chatrow.EaseChatRowLocation;
import com.hyphenate.chat.EMMessage;


/**
 * 定位代理类
 */
public class EaseLocationAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseLocationAdapterDelegate() {
    }

    public EaseLocationAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.LOCATION;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowLocation(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseLocationViewHolder(view, itemClickListener);
    }
}
