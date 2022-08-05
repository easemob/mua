package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.community.mua.App;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.Empty;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivityMapSettingBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SharedPreferUtil;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MapSettingActivity extends BaseActivity<ActivityMapSettingBinding> {
    public static void start(Context context) {
        context.startActivity(new Intent(context, MapSettingActivity.class));
    }
    private boolean mLocationEnabled;
    @Override
    protected ActivityMapSettingBinding getViewBinding() {
        return ActivityMapSettingBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("定位设置");
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
                        mLocationEnabled = bean.isSeeLocation();
                        mBinding.swLocationDetail.setChecked(mLocationEnabled);
                    }
                });
    }

    @Override
    protected void initListener() {
        mBinding.swLocationDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
                Map<String, Object> params = new HashMap<>();
                params.put("userId", userBean.getUserid());
                params.put("isSeeLocation", !mLocationEnabled);
                App.getApi().setIsSeeLocation(params).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new QObserver<Empty>(mContext) {
                            @Override
                            public void next(Empty bean) {
                                mBinding.swLocationDetail.setChecked(!mLocationEnabled);
                            }
                        });
            }
        });
    }
}