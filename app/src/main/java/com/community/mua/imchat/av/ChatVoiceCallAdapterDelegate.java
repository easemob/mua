package com.community.mua.imchat.av;

import static com.hyphenate.chat.EMMessage.Type.TXT;

import android.view.View;
import android.view.ViewGroup;

import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.callkit.utils.EaseMsgUtils;
import com.community.mua.imkit.delegate.EaseMessageAdapterDelegate;
import com.community.mua.imkit.interfaces.MessageListItemClickListener;
import com.community.mua.imkit.viewholder.EaseChatRowViewHolder;
import com.community.mua.imkit.widget.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;


public class ChatVoiceCallAdapterDelegate extends EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder> {
    @Override
    public boolean isForViewType(EMMessage item, int position) {
        boolean isRtcCall =item.getStringAttribute(EaseMsgUtils.CALL_MSG_TYPE,"").equals(EaseMsgUtils.CALL_MSG_INFO)?true:false;
        boolean isVoiceCall = item.getIntAttribute(EaseMsgUtils.CALL_TYPE,0) == EaseCallType.SINGLE_VOICE_CALL.code?true:false;
        return item.getType() == TXT && isRtcCall && isVoiceCall;
    }

    @Override
    protected EaseChatRow getEaseChatRow(ViewGroup parent, boolean isSender) {
        return new ChatRowVoiceCall(parent.getContext(), isSender);
    }

    @Override
    protected EaseChatRowViewHolder createViewHolder(View view, MessageListItemClickListener itemClickListener) {
        return new ChatVoiceCallViewHolder(view, itemClickListener);
    }
}
