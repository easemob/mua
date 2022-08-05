package com.community.mua;

import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.community.mua.common.Constants;
import com.community.mua.imchat.ImHelper;
import com.community.mua.imchat.UserActivityLifecycleCallbacks;
import com.community.mua.services.ApiService;
import com.community.mua.ui.ChatActivity;
import com.community.mua.ui.SplashActivity;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.LocationService;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.window.DkFloatingView;
import com.community.mua.views.window.FloatWindow;
import com.hjq.toast.ToastUtils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {
    private static ApiService sApi;
    private static App sContext;
    public LocationService locationService;
    private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        registerActivityLifecycleCallbacks();
        LocationClient.setAgreePrivacy(true);
        // 是否同意隐私政策，默认为false
        SDKInitializer.setAgreePrivacy(this, true);
        locationService = new LocationService(this);
        try {
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            SDKInitializer.initialize(this);
            SDKInitializer.setCoordType(CoordType.BD09LL);
        } catch (BaiduMapSDKException e) {
            e.printStackTrace();
        }
        initHttp();

        initToast();

        ImHelper.getInstance().init(sContext);

        initBtnSound();

        initChatFlow();
        Bugly.init(getApplicationContext(), "腾讯Bugly", true);
    }

    public static App getInstance() {
        return sContext;
    }
    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }


    public UserActivityLifecycleCallbacks getLifecycleCallbacks() {
        return mLifecycleCallbacks;
    }

    private void initChatFlow() {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = DisplayUtil.getWidth(this) - DisplayUtil.dp2px(this, 56);
        params.topMargin = DisplayUtil.getHeight(this) * 3 / 4;

        FloatWindow.with(this)//application上下文
                .setLayoutId(R.layout.float_chat)//悬浮布局
                .setFilter(SplashActivity.class, ChatActivity.class)//过滤activity
                .setLayoutParam(params)//设置悬浮布局layoutParam
                .build();

        FloatWindow.get()//悬浮窗实例
                .setOnClickListener(new DkFloatingView.ViewClickListener() {
                    @Override
                    public void onClick(int viewId) {//viewId
                        SoundUtil.getInstance().playBtnSound();
                        TextView tvUnread = FloatWindow.get().getView().findViewById(R.id.tv_unread);
                        UserUtils.setShowNum(tvUnread, 0);
                        ChatActivity.start(sContext);
                    }

                    @Override
                    public void onMove2Edge(boolean isLeft) {
                        View root = FloatWindow.get().getView().findViewById(R.id.root);
                        root.setBackgroundResource(isLeft?R.drawable.sp_white_right_28:R.drawable.sp_white_left_28);
                    }
                });
    }

    private void initBtnSound() {
        SoundUtil.getInstance().initSound(sContext);
        SoundUtil.getInstance().switchSoundEnabled(SharedPreferUtil.getInstance().getBtnSoundEnabled());
    }


    private void initToast() {
        ToastUtils.init(this);
        ToastUtils.getToast().setGravity(Gravity.BOTTOM, 0, DisplayUtil.dp2px(sContext, 93));
    }

    private void initHttp() {
        OkHttpClient client = new OkHttpClient
                .Builder()
                //断线重连
                .retryOnConnectionFailure(true)
                //链接超时时间
                .connectTimeout(Constants.NET_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                //写超时时间
                .writeTimeout(Constants.NET_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                //读超时时间
                .readTimeout(Constants.NET_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.HTTP_HOST)
                .addConverterFactory(GsonConverterFactory.create()) //增加返回值为Gson的支持(以实体类返回)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //增加返回值为Oservable<T>的支持
                .client(client)//配置OkHttpClient
                .build();

        sApi = retrofit.create(ApiService.class);

    }

    public static ApiService getApi() {
        return sApi;
    }

    public static Context getContext() {
        return sContext;
    }
}
