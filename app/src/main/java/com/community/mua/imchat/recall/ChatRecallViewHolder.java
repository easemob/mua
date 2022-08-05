package com.community.mua.imchat.recall;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.community.mua.imkit.interfaces.MessageListItemClickListener;
import com.community.mua.imkit.viewholder.EaseChatRowViewHolder;

public class ChatRecallViewHolder extends EaseChatRowViewHolder {

    public ChatRecallViewHolder(@NonNull View itemView, MessageListItemClickListener itemClickListener) {
        super(itemView, itemClickListener);
    }

    public static ChatRecallViewHolder create(ViewGroup parent, boolean isSender,
                                              MessageListItemClickListener listener) {
        return new ChatRecallViewHolder(new ChatRowRecall(parent.getContext(), isSender), listener);
    }


}
