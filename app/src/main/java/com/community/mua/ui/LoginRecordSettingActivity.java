package com.community.mua.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.Empty;
import com.community.mua.bean.LoginRecordBean;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivityLoginRecordBinding;
import com.community.mua.databinding.ActivityLoginRecordSettingBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SharedPreferUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginRecordSettingActivity extends BaseActivity<ActivityLoginRecordSettingBinding> {

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoginRecordSettingActivity.class));
    }

    private boolean mRecordEnabled;
    @Override
    protected ActivityLoginRecordSettingBinding getViewBinding() {
        return ActivityLoginRecordSettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("登录记录设置");
    }

    @Override
    protected void initData() {
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        Map<String, String> params = new HashMap<>();
        params.put("userId", userBean.getUserid());
        App.getApi().getUser(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<UserBean>(mContext) {
                    @Override
                    public void next(UserBean bean) {
                        mRecordEnabled = bean.isRecordPrivate();
                        mBinding.swRecordDetail.setChecked(mRecordEnabled);
                    }
                });
    }

    @Override
    protected void initListener() {
        mBinding.swRecordDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
                Map<String, Object> params = new HashMap<>();
                params.put("userId", userBean.getUserid());
                params.put("isPrivate", !mRecordEnabled);
                App.getApi().setIsPrivate(params).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new QObserver<Empty>(mContext) {
                            @Override
                            public void next(Empty bean) {
                                mBinding.swRecordDetail.setChecked(!mRecordEnabled);
                            }
                        });
            }
        });
    }
}