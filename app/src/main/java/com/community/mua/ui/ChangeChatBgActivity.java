package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;

import com.community.mua.R;
import com.community.mua.adapter.ChangeChatBgAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.ChangeChatBgBean;
import com.community.mua.databinding.ActivityChangeChatBgBinding;
import com.community.mua.utils.SharedPreferUtil;

import java.util.ArrayList;
import java.util.List;

public class ChangeChatBgActivity extends BaseActivity<ActivityChangeChatBgBinding> {

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChangeChatBgActivity.class));
    }

    @Override
    protected ActivityChangeChatBgBinding getViewBinding() {
        return ActivityChangeChatBgBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("更换聊天背景");
    }

    @Override
    protected void initData() {
        initRv();
    }

    private void initRv() {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mBinding.rv.setLayoutManager(layoutManager);
        ChangeChatBgAdapter adapter = new ChangeChatBgAdapter(SharedPreferUtil.getBgBeans());

        mBinding.rv.setAdapter(adapter);
    }

    @Override
    protected void initListener() {

    }
}
