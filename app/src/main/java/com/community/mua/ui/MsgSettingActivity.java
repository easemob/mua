package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.community.mua.base.BaseActivity;
import com.community.mua.databinding.ActivityMsgSettingBinding;
import com.community.mua.imchat.ImHelper;
import com.community.mua.imkit.manager.EaseThreadManager;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMPushConfigs;
import com.hyphenate.chat.EMPushManager;
import com.hyphenate.exceptions.HyphenateException;

public class MsgSettingActivity extends BaseActivity<ActivityMsgSettingBinding> {

    private boolean mSoundEnabled;
    private boolean mVibrateEnabled;
    private boolean mMsgDetailEnabled;

    public static void start(Context context) {
        context.startActivity(new Intent(context, MsgSettingActivity.class));
    }

    @Override
    protected ActivityMsgSettingBinding getViewBinding() {
        return ActivityMsgSettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("消息设置");
    }

    @Override
    protected void initData() {
        initOptions();
    }

    private void initOptions() {
        mSoundEnabled = SharedPreferUtil.getInstance().getNotifySoundEnabled();
        mBinding.swSoundNotify.setChecked(mSoundEnabled);
        mVibrateEnabled = SharedPreferUtil.getInstance().getNotifyVibrateEnabled();
        mBinding.swVibrate.setChecked(mVibrateEnabled);

        getPushConfigsFromServer();
    }

    public void onChangeSoundEnable(View view) {
        SoundUtil.getInstance().playBtnSound();
        mSoundEnabled = !mSoundEnabled;
        mBinding.swSoundNotify.setChecked(mSoundEnabled);
        SharedPreferUtil.getInstance().setNotifySoundEnabled(mSoundEnabled);
    }

    public void onChangeVibrateEnable(View view) {
        SoundUtil.getInstance().playBtnSound();
        mVibrateEnabled = !mVibrateEnabled;
        mBinding.swVibrate.setChecked(mVibrateEnabled);
        SharedPreferUtil.getInstance().setNotifyVibrateEnabled(mVibrateEnabled);
    }
    public void onChangeMsgDetailEnable(View view) {
        SoundUtil.getInstance().playBtnSound();
        mMsgDetailEnabled = !mMsgDetailEnabled;
        EMPushManager.DisplayStyle[] values = EMPushManager.DisplayStyle.values();
        EMPushManager.DisplayStyle style =  mMsgDetailEnabled ? values[1]:values[0];
        ImHelper.getInstance().getEMClient().pushManager().asyncUpdatePushDisplayStyle(style, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.swMsgDetail.setChecked(mMsgDetailEnabled);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }


    private void getPushConfigsFromServer(){
        EaseThreadManager.getInstance().runOnIOThread(()-> {
            try {
                EMPushConfigs configs = ImHelper.getInstance().getEMClient().pushManager().getPushConfigsFromServer();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMsgDetailEnabled = configs.getDisplayStyle() == EMPushManager.DisplayStyle.MessageSummary;
                        mBinding.swMsgDetail.setChecked(mMsgDetailEnabled);
                    }
                });
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        });
    }


    @Override
    protected void initListener() {

    }
}
