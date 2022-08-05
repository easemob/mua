package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.mua.adapter.ChatHistoryAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivityChatHistoryBinding;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatHistoryActivity extends BaseActivity<ActivityChatHistoryBinding> {

    private ChatHistoryAdapter mAdapter;
    private UserBean mTaBean;
    private EMConversation mConversation;
    private String mKey = "我";

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChatHistoryActivity.class));
    }

    @Override
    protected ActivityChatHistoryBinding getViewBinding() {
        return ActivityChatHistoryBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("查找聊天记录");
    }

    @Override
    protected void initData() {
        mTaBean = SharedPreferUtil.getInstance().getTaBean();

        initRv();

        getConversation();
    }

    private void getConversation() {
        mConversation = EMClient.getInstance().chatManager().getConversation(mTaBean.getChatId(), EMConversation.EMConversationType.Chat, true);
    }

    private void initRv() {
        mBinding.rv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new ChatHistoryAdapter(mContext);
        mBinding.rv.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        mBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String key = mBinding.etSearch.getText().toString().trim();
                if (TextUtils.isEmpty(key)) {
                    switchViews(true);
                    mAdapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = mBinding.etSearch.getText().toString().trim();
                    if (!TextUtils.isEmpty(key)) {
                        doSearch(key);
                    }
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

    }

    private void switchViews(boolean b) {
        mBinding.ivSearch.setVisibility(b ? View.VISIBLE : View.GONE);
        mBinding.ivClear.setVisibility(b ? View.GONE : View.VISIBLE);
        mBinding.tvOthers.setVisibility(b ? View.VISIBLE : View.GONE);
        mBinding.rv.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    private void doSearch(String key) {
        switchViews(false);

        List<EMMessage> messageList = new ArrayList<>();
        List<EMMessage> mData = mConversation.searchMsgFromDB(key, System.currentTimeMillis(), 200, null, EMConversation.EMSearchDirection.UP);
        mAdapter.setKeyword(key);
        for (EMMessage emMessage : mData) {
            String msgTxt = ((EMTextMessageBody) emMessage.getBody()).getMessage();
            if (!TextUtils.isEmpty(msgTxt) && msgTxt.contains(key) && !TextUtils.equals(emMessage.getFrom(), "admin")) {
                messageList.add(emMessage);
            }
        }
        Collections.reverse(messageList);
        mAdapter.setData(messageList);
    }

    public void onClear(View v) {
        SoundUtil.getInstance().playBtnSound();
        mBinding.etSearch.setText("");
        switchViews(true);
        mAdapter.clear();
    }

    public void onSearchClick(View v) {
        SoundUtil.getInstance().playBtnSound();
        String key = mBinding.etSearch.getText().toString().trim();
        doSearch(key);
        hideKeyboard();
    }

    public void onPicHistory(View v) {
        SoundUtil.getInstance().playBtnSound();
        ChatPicHistoryActivity.start(mContext);
    }
}
