package com.community.mua.imkit.delegate;

import android.view.View;
import android.view.ViewGroup;

import com.community.mua.imkit.interfaces.MessageListItemClickListener;
import com.community.mua.imkit.model.styles.EaseMessageListItemStyle;
import com.community.mua.imkit.viewholder.EaseChatRowViewHolder;
import com.community.mua.imkit.viewholder.EaseVoiceViewHolder;
import com.community.mua.imkit.widget.chatrow.EaseChatRow;
import com.community.mua.imkit.widget.chatrow.EaseChatRowVoice;
import com.hyphenate.chat.EMMessage;


/**
 * 声音代理类
 */
public class EaseVoiceAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {

    public EaseVoiceAdapterDelegate() {
    }

    public EaseVoiceAdapterDelegate(MessageListItemClickListener itemClickListener, EaseMessageListItemStyle itemStyle) {
        super(itemClickListener, itemStyle);
    }

    @Override
    public boolean isForViewType(EMMessage item, int position) {
        return item.getType() == EMMessage.Type.VOICE;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new EaseChatRowVoice(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new EaseVoiceViewHolder(view, itemClickListener);
    }
}
