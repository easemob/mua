package com.community.mua.imchat.av;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.community.mua.callkit.EaseCallKit;
import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.imkit.interfaces.MessageListItemClickListener;
import com.community.mua.imkit.viewholder.EaseChatRowViewHolder;
import com.community.mua.ui.call.VideoCallActivity;
import com.hyphenate.chat.EMMessage;


public class ChatVoiceCallViewHolder extends EaseChatRowViewHolder {

    public ChatVoiceCallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatVoiceCallViewHolder create(ViewGroup parent, boolean isSender,
                                                        MessageListItemClickListener itemClickListener) {
        return new ChatVoiceCallViewHolder(new ChatRowVoiceCall(parent.getContext(), isSender), itemClickListener);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        if(message.direct() == EMMessage.Direct.SEND) {
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL,message.getTo(),null, VideoCallActivity.class);
        }else {
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL,message.getFrom(),null, VideoCallActivity.class);
        }
    }
}
