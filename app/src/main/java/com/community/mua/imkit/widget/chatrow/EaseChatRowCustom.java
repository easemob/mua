package com.community.mua.imkit.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.mua.imkit.manager.EaseDingMessageHelper;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.community.mua.R;


import java.util.List;

public class EaseChatRowCustom extends EaseChatRow {

    private TextView mContentView;
    private ImageView mIvType;

    public EaseChatRowCustom(Context context, boolean isSender) {
        super(context, isSender);
    }

    public EaseChatRowCustom(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(!showSenderType ?
                R.layout.ease_row_received_custom_message : R.layout.ease_row_sent_custom_message, this);
    }

    @Override
    protected void onFindViewById() {
        mContentView = (TextView) findViewById(R.id.tv_chatcontent);
        mIvType = findViewById(R.id.iv_type);
    }

    @Override
    public void onSetUpView() {
        EMCustomMessageBody txtBody = (EMCustomMessageBody) message.getBody();
        mContentView.setText(txtBody.event());
        String pos = txtBody.getParams().get("position");
        if (!TextUtils.isEmpty(pos)) {
            int position = Integer.parseInt(pos);
            setType(position);
        }
    }

    private void setType(int position) {
        if (position == 0) {
            mIvType.setImageResource(R.mipmap.watch_film_in_chat);
        } else if (position == 1) {
            mIvType.setImageResource(R.mipmap.cooking_in_chat);
        } else if (position == 2) {
            mIvType.setImageResource(R.mipmap.love_letter_in_chat);
        }
    }

    public void onAckUserUpdate(final int count) {
        if (ackedView != null && isSender()) {
            ackedView.post(new Runnable() {
                @Override
                public void run() {
                    ackedView.setVisibility(VISIBLE);
                    ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
                }
            });
        }
    }

    @Override
    protected void onMessageCreate() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onMessageSuccess() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }

        // Show "1 Read" if this msg is a ding-type msg.
        if (isSender() && EaseDingMessageHelper.get().isDingMessage(message) && ackedView != null) {
            ackedView.setVisibility(VISIBLE);
            int count = message.groupAckCount();
            ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
        }

        // Set ack-user list change listener.
        EaseDingMessageHelper.get().setUserUpdateListener(message, userUpdateListener);
    }

    @Override
    protected void onMessageError() {
        super.onMessageError();
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onMessageInProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
        }
    }

    private EaseDingMessageHelper.IAckUserUpdateListener userUpdateListener =
            new EaseDingMessageHelper.IAckUserUpdateListener() {
                @Override
                public void onUpdate(List<String> list) {
                    onAckUserUpdate(list.size());
                }
            };
}
