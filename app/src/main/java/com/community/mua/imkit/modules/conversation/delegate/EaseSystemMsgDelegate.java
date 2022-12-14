package com.community.mua.imkit.modules.conversation.delegate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.community.mua.imkit.EaseIM;
import com.community.mua.imkit.constants.EaseConstant;
import com.community.mua.imkit.manager.EaseSystemMsgManager;
import com.community.mua.imkit.modules.conversation.model.EaseConversationInfo;
import com.community.mua.imkit.modules.conversation.model.EaseConversationSetStyle;
import com.community.mua.imkit.provider.EaseConversationInfoProvider;
import com.community.mua.imkit.utils.EaseCommonUtils;
import com.community.mua.imkit.utils.EaseDateUtils;
import com.community.mua.imkit.utils.EaseSmileUtils;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import com.community.mua.R;


import java.util.Date;

public class EaseSystemMsgDelegate extends EaseDefaultConversationDelegate {

    public EaseSystemMsgDelegate(EaseConversationSetStyle setModel) {
        super(setModel);
    }

    @Override
    public boolean isForViewType(EaseConversationInfo item, int position) {
        return item != null && item.getInfo() instanceof EMConversation
                && EaseSystemMsgManager.getInstance().isSystemConversation((EMConversation) item.getInfo());
    }

    @Override
    protected void onBindConViewHolder(ViewHolder holder, int position, EaseConversationInfo bean) {
        EMConversation item = (EMConversation) bean.getInfo();
        Context context = holder.itemView.getContext();
        String username = item.conversationId();
        holder.listIteaseLayout.setBackground(!TextUtils.isEmpty(item.getExtField())
                ? ContextCompat.getDrawable(context, R.drawable.ease_conversation_top_bg)
                : null);
        holder.mentioned.setVisibility(View.GONE);
        EaseConversationInfoProvider infoProvider = EaseIM.getInstance().getConversationInfoProvider();
        holder.avatar.setImageResource(R.drawable.em_system_nofinication);
        holder.name.setText(holder.mContext.getString(R.string.ease_conversation_system_message));
        if(infoProvider != null) {
            Drawable avatar = infoProvider.getDefaultTypeAvatar(EaseConstant.DEFAULT_SYSTEM_MESSAGE_TYPE);
            if(avatar != null) {
                Glide.with(holder.mContext).load(avatar).error(R.drawable.em_system_nofinication).into(holder.avatar);
            }
        }

        if(!setModel.isHideUnreadDot()) {
            showUnreadNum(holder, item.getUnreadMsgCount());
        }

        if(item.getAllMsgCount() != 0) {
            EMMessage lastMessage = item.getLastMessage();
            holder.message.setText(EaseSmileUtils.getSmiledText(context, EaseCommonUtils.getMessageDigest(lastMessage, context)));
            holder.time.setText(EaseDateUtils.getTimestampString(holder.itemView.getContext(), new Date(lastMessage.getMsgTime())));
        }
    }
}

