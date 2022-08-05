package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.mua.R;
import com.community.mua.adapter.ChatSettingAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivityChatSettingBinding;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.List;

public class ChatSettingActivity extends BaseActivity<ActivityChatSettingBinding> implements ChatSettingAdapter.OnItemClickListener {

    private UserBean mTaBean;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChatSettingActivity.class));
    }

    @Override
    protected ActivityChatSettingBinding getViewBinding() {
        return ActivityChatSettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("设置");
    }

    @Override
    protected void initData() {
        mTaBean = SharedPreferUtil.getInstance().getTaBean();
        initRv();
    }

    private void initRv() {
        List<String> list = new ArrayList<>();
        list.add("更换聊天背景");
        list.add("消息设置");
        list.add("查找聊天记录");
        list.add("删除本地聊天记录");

        mBinding.rv.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.rv.setAdapter(new ChatSettingAdapter(list, this));
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onClick(int pos) {
        SoundUtil.getInstance().playBtnSound();
        if (pos == 1) {
            MsgSettingActivity.start(mContext);
        } else if (pos == 0) {
            ChangeChatBgActivity.start(mContext);
        } else if (pos == 2) {
            ChatHistoryActivity.start(mContext);
        } else if (pos == 3) {
            showDeleteDialog();
        }
    }

    private void showDeleteDialog() {
        NiceDialog.init().setLayoutId(R.layout.dialog_delete_conversation)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                deleteConversation();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setMargin(DisplayUtil.dp2px(mContext, 10))
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    private void deleteConversation() {
        //删除和某个user会话，如果需要保留聊天记录，传false
        EMClient.getInstance().chatManager().deleteConversation(mTaBean.getChatId(), true);
    }
}
