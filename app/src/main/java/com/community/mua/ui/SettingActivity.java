package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivitySettingBinding;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.window.FloatWindow;
import com.hyphenate.chat.EMClient;

public class SettingActivity extends BaseActivity<ActivitySettingBinding> {

    private UserBean mTaBean;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Override
    protected ActivitySettingBinding getViewBinding() {
        return ActivitySettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("设置");
    }

    @Override
    protected void initData() {
        mTaBean = SharedPreferUtil.getInstance().getTaBean();
        initBtnSound();
        initChatFlow();
    }

    private void initChatFlow() {
        mBinding.swChatFlow.setChecked(SharedPreferUtil.getInstance().getChatFlowEnabled());
    }

    private void initBtnSound() {
        boolean b = SharedPreferUtil.getInstance().getBtnSoundEnabled();
        mBinding.swBtnSound.setChecked(b);
    }

    @Override
    protected void initListener() {

    }

    public void onEnterBgm(View v) {
        SoundUtil.getInstance().playBtnSound();
        BgmActivity.start(mContext);
    }

    public void onUnpair(View v) {
        SoundUtil.getInstance().playBtnSound();
        UnPairActivity.start(mContext);
    }

    public void onSwitchChatFlow(View v) {
        SoundUtil.getInstance().playBtnSound();

        boolean checked = mBinding.swChatFlow.isChecked();
        SharedPreferUtil.getInstance().setChatFlowEnabled(checked);

        if (checked) {
            FloatWindow.get().show();
            View floatView = FloatWindow.get().getView();
            FrameLayout flAvatar = floatView.findViewById(R.id.fl_avatar);
            ImageView ivAvatar = floatView.findViewById(R.id.iv_avatar);
            UserUtils.setUserInfo(mContext, null, flAvatar, ivAvatar, mTaBean);
        } else {
            FloatWindow.get().hide();
        }
    }
    public void onSwitchBtnSoundEnabled(View v) {
        SoundUtil.getInstance().playBtnSound();

        boolean checked = mBinding.swBtnSound.isChecked();
        SharedPreferUtil.getInstance().setBtnSoundEnabled(checked);
        SoundUtil.getInstance().switchSoundEnabled(checked);
    }
}
