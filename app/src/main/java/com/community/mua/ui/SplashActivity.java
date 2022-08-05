package com.community.mua.ui;

import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.AppManager;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.CreateAccountBean;
import com.community.mua.bean.Empty;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.SplashSizeBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivitySplashBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.GsonUtils;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPresence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    @Override
    protected ActivitySplashBinding getViewBinding() {
        return ActivitySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initData() {

        getSplashSize();

        UserBean bean = SharedPreferUtil.getInstance().getUserBean();
        if (bean == null) {
            jump2Register();
            return;
        }
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        if (pairBean != null) {
            UserUtils.setSplashImage(mContext, mBinding.ivSplash, pairBean.getSplashUrl());
        }

        Map<String, String> params = new HashMap<>();
        params.put("userId", bean.getUserid());
        App.getApi().getSplash(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<PairBean>(mContext, true, false) {
                    @Override
                    public void next(PairBean pairBean) {
                        setPairData(pairBean);
                    }

                    @Override
                    public void error(String code, String msg) {
                        if (TextUtils.equals(code, "1004")) {
                            //未匹配
                            jump2Pair();
                        }
                    }
                });


        String lat = SharedPreferUtil.getInstance().getLat();
        String longitude = SharedPreferUtil.getInstance().getLongitude();
        String locationAdd = SharedPreferUtil.getInstance().getLocationAdd();

        if (TextUtils.isEmpty(locationAdd)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", bean.getUserid());
        map.put("loginAddress", locationAdd);
        map.put("latitude", lat);
        map.put("longitude", longitude);
        map.put("timeStamp", System.currentTimeMillis() + "");
        map.put("mobileInfo", SharedPreferUtil.getInstance().getBatteryManager());

        App.getApi().insertRecord(map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Empty>(mContext, true) {
                    @Override
                    public void next(Empty empty) {

                    }

                    @Override
                    public void error(String code, String msg) {

                    }
                });


    }

    private void getSplashSize() {
        SplashSizeBean sizeBean = SharedPreferUtil.getInstance().getSplashSize();
        if (sizeBean != null) {
            return;
        }
        int y = DisplayUtil.getHeight(mContext) - DisplayUtil.dp2px(mContext, 104) - DisplayUtil.getNavigationBarHeight(mContext);
        SharedPreferUtil.getInstance().setSplashSize(new SplashSizeBean(DisplayUtil.getWidth(mContext), y));
    }

    private void jump2Register() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppManager.getInstance().finishActivity(SplashActivity.class);
                RegisterActivity.start(mContext);
            }
        }, 2000);
    }

    private void jump2Pair() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppManager.getInstance().finishActivity(SplashActivity.class);
                PairActivity.start(mContext);
            }
        }, 2000);
    }

    private void setPairData(PairBean pairBean) {
        UserUtils.setSplashImage(mContext, mBinding.ivSplash, pairBean.getSplashUrl());

        List<UserBean> userRsp = pairBean.getUserRsp();
        UserBean meBean = null;
        UserBean taBean = null;
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        for (UserBean bean : userRsp) {
            if (TextUtils.equals(userBean.getUserid(), bean.getUserid())) {
                meBean = bean;
                continue;
            }
            taBean = bean;
        }
        if (meBean == null || taBean == null) {
            ToastUtil.show("无用户数据");
            return;
        }

        SharedPreferUtil.getInstance().setUserBean(meBean);
        SharedPreferUtil.getInstance().setTaBean(taBean);
        SharedPreferUtil.getInstance().setPairBean(pairBean);

        if (SharedPreferUtil.getInstance().getCoins() == 0) {
            jump2Welcome();
            return;
        }
        jump2Main(taBean);
    }

    private void jump2Welcome() {
        WelcomeActivity.start(mContext);
    }

    private void jump2Main(UserBean taBean) {
        List<String> list = new ArrayList<>();
        list.add(taBean.getChatId());
//        EMClient.getInstance().presenceManager().subscribePresences(list, 1 * 24 * 3600, new EMValueCallBack<List<EMPresence>>() {
//            @Override
//            public void onSuccess(List<EMPresence> presences) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppManager.getInstance().finishActivity(SplashActivity.class);
                MainActivity.start(mContext);
            }
        }, 2000);
//            }
//
//            @Override
//            public void onError(int errorCode, String errorMsg) {
//
//            }
//        });

    }

    @Override
    protected void initListener() {

    }
}
