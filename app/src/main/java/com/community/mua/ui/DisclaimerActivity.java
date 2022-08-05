package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.community.mua.base.BaseActivity;
import com.community.mua.databinding.ActivityDisclaimerBinding;
import com.community.mua.utils.SoundUtil;

public class DisclaimerActivity extends BaseActivity<ActivityDisclaimerBinding> {
    public static void start(Context context) {
        context.startActivity(new Intent(context, DisclaimerActivity.class));
    }
    @Override
    protected ActivityDisclaimerBinding getViewBinding() {
        return ActivityDisclaimerBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                finish();
            }
        });
    }
}