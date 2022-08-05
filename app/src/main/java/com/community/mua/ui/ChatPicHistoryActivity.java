package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.mua.adapter.ChatPicContainerAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.ChatPicContainerBean;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivityChatPicHistoryBinding;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVideoMessageBody;

import java.util.ArrayList;
import java.util.List;

public class ChatPicHistoryActivity extends BaseActivity<ActivityChatPicHistoryBinding> {

    private ChatPicContainerAdapter mContainerAdapter;
    private UserBean mTaBean;
    private EMConversation mConversation;

    private List<ChatPicContainerBean> mList = new ArrayList<>();

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChatPicHistoryActivity.class));
    }

    @Override
    protected ActivityChatPicHistoryBinding getViewBinding() {
        return ActivityChatPicHistoryBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("图片与视频");
    }

    @Override
    protected void initData() {
        initRv();

        getConversation();
    }

    private void getConversation() {
        mTaBean = SharedPreferUtil.getInstance().getTaBean();
        mConversation = EMClient.getInstance().chatManager().getConversation(mTaBean.getChatId(), EMConversation.EMConversationType.Chat, true);

        List<EMMessage> allMessages = mConversation.getAllMessages();

        for (EMMessage message : allMessages) {
            if (!(message.getBody() instanceof EMImageMessageBody) && !(message.getBody() instanceof EMVideoMessageBody)) {
                continue;
            }
            if (mList.isEmpty()) {
                List<EMMessage> list = new ArrayList<>();
                list.add(message);
                mList.add(new ChatPicContainerBean(DateUtil.stampToMonth(message.getMsgTime()), list));
                continue;
            }
            for (ChatPicContainerBean containerBean : mList) {
                List<EMMessage> messages = containerBean.getMessages();
                if (!TextUtils.equals(containerBean.getTime(), DateUtil.stampToMonth(message.getMsgTime()))) {
                    containerBean.setTime(DateUtil.stampToMonth(message.getMsgTime()));
                }
                messages.add(message);
            }
        }

        mContainerAdapter.setData(mList);
    }

    private boolean containsDate(EMMessage message) {
        if (mList.isEmpty()) {
            return false;
        }
        for (ChatPicContainerBean containerBean : mList) {
            if (TextUtils.equals(containerBean.getTime(), DateUtil.stampToMonth(message.getMsgTime()))) {
                return true;
            }
        }
        return false;
    }

    private void initRv() {
        mBinding.rv.setLayoutManager(new LinearLayoutManager(mContext));
        mContainerAdapter = new ChatPicContainerAdapter(mContext);
        mBinding.rv.setAdapter(mContainerAdapter);
    }

    @Override
    protected void initListener() {

    }
}
