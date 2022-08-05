package com.community.mua.ui;

import static io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.UserBean;
import com.community.mua.callkit.EaseCallKit;
import com.community.mua.callkit.base.EaseCallKitConfig;
import com.community.mua.callkit.base.EaseCallKitListener;
import com.community.mua.callkit.base.EaseCallKitTokenCallback;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityChatBinding;
import com.community.mua.imkit.EaseIM;
import com.community.mua.imkit.constants.EaseConstant;
import com.community.mua.imkit.model.EaseNotifier;
import com.community.mua.imkit.modules.chat.EaseChatFragment;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;
import com.hyphenate.chat.EMClient;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;

public class ChatActivity extends BaseActivity<ActivityChatBinding> {

    private String mChatId;
    private EaseChatFragment mFragment;
    private UserBean mTaBean;
    private UserBean mUserBean;
    private boolean isWatchMovie;
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private String agoraAppId = null;
    protected String channelName;
    EaseCallKitListener listener = EaseCallKit.getInstance().getCallListener();
    protected AudioManager audioManager;


    public static void start(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        if (context instanceof Application) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.CHAT_FROM, "app");
        }
        intent.putExtra("isWatchMovie",false);
        context.startActivity(intent);
    }


    public static void start(Context context,boolean isWatchMovie) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("isWatchMovie",isWatchMovie);
        context.startActivity(intent);
    }


    @Override
    protected ActivityChatBinding getViewBinding() {
        return ActivityChatBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.ivMore.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onMore() {
        ChatSettingActivity.start(mContext);
    }

    @Override
    protected void initData() {
        EaseIM.getInstance().getNotifier().reset();
        mChatId = SharedPreferUtil.getInstance().getTaBean().getChatId();
        isWatchMovie = getIntent().getBooleanExtra("isWatchMovie", false);

        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        channelName = SharedPreferUtil.getInstance().getPairBean().getMatchingCode();

        if (TextUtils.equals(getIntent().getStringExtra(Constants.CHAT_FROM), "app")) {
            mBinding.titleBar.ivBack.setRotation(270f);
            overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_static);
        }

        mUserBean = SharedPreferUtil.getInstance().getUserBean();
        mTaBean = SharedPreferUtil.getInstance().getTaBean();

        mBinding.titleBar.tvName.setText(mTaBean.getNickname());

        mBinding.ivMobile.setVisibility(View.GONE);
        if (isWatchMovie) {
            // 获取权限后，初始化 RtcEngine，并加入频道。
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
                initAgoraEngineAndJoinChannel();
            }
            LiveDataBus.get().with(Constants.WATCH_MOVIE_BLACK).postValue(new LiveEvent(true));
            UserUtils.sendTxtMsg("宝，想和你一起看电影，快快加入吧!");
            mBinding.getRoot().findViewById(R.id.title_bar).setVisibility(View.GONE);
            mBinding.rlWatchMovie.setVisibility(View.VISIBLE);
            mBinding.fcv.setBackgroundColor(Color.parseColor("#1D212D"));
        } else {
            mBinding.getRoot().findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
            mBinding.rlWatchMovie.setVisibility(View.GONE);
            mBinding.fcv.setBackgroundColor(Color.WHITE);
        }


        initChatFragment();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }
    private void initChatFragment() {
        mFragment = new EaseChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, mChatId);
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        bundle.putBoolean("isWatchMovie",isWatchMovie);
        mFragment.setArguments(bundle);
        replace(mFragment, "chat");
    }

    @Override
    protected void initListener() {
        mBinding.ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                exitWatchMovie();
            }
        });
        mBinding.ivMobile.setOnTouchListener(new ButtonLongClick());
        mBinding.ivMobile.setOnLongClickListener(new ButtonLongClick());
        LiveDataBus.get().with(Constants.WATCH_MOVIE, LiveEvent.class).observe(this, this::updateWatchMovie);
    }

    class ButtonLongClick implements View.OnLongClickListener, View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    setupAudioConfig(true);
                    return true;
                case MotionEvent.ACTION_UP:
                    setupAudioConfig(false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    private void updateWatchMovie(LiveEvent event){
        if (isWatchMovie) {
            return;
        }
        isWatchMovie = event.flag;
        // 获取权限后，初始化 RtcEngine，并加入频道。
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }
        LiveDataBus.get().with(Constants.WATCH_MOVIE_BLACK).postValue(new LiveEvent(true));
        UserUtils.sendTxtMsg("宝，想和你一起看电影，快快加入吧!");
        mBinding.ivMobile.setVisibility(View.GONE);
        if (isWatchMovie) {
            mBinding.getRoot().findViewById(R.id.title_bar).setVisibility(View.GONE);
            mBinding.rlWatchMovie.setVisibility(View.VISIBLE);
        } else {
            mBinding.getRoot().findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
            mBinding.rlWatchMovie.setVisibility(View.GONE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (TextUtils.equals(getIntent().getStringExtra(Constants.CHAT_FROM), "app")) {
            overridePendingTransition(R.anim.slide_static, R.anim.slide_out_from_top);
        }
    }

    private RtcEngine mRtcEngine;
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // 注册 onJoinChannelSuccess 回调。
        // 本地用户成功加入频道时，会触发该回调。
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("wyz", "Join channel success, uid: " + (uid & 0xFFFFFFFFL));
                }
            });
        }

        @Override
        // 注册 onUserOffline 回调。
        // 远端主播离开频道或掉线时，会触发该回调。
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("wyz", "User offline, uid: " + (uid & 0xFFFFFFFFL));
                    mBinding.ivMobile.setVisibility(View.GONE);
//                    onRemoteUserLeft();
                }
            });
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //检测到对方进来
                    ToastUtil.show("请说话，对方会实时听见!");
                    mBinding.ivMobile.setVisibility(View.VISIBLE);
                }
            });
        }

    };

    private void initAgoraEngineAndJoinChannel() {
        initializeEngine();
        joinChannel();
    }

    // 初始化 RtcEngine 对象。
    private void initializeEngine() {
        try {
            EaseCallKitConfig config =  EaseCallKit.getInstance().getCallKitConfig();
            if(config != null){
                agoraAppId = config.getAgoraAppId();
            }
            mRtcEngine = RtcEngine.create(getBaseContext(), agoraAppId, mRtcEventHandler);
            setupAudioConfig(false);
            //因为有小程序 设置为直播模式 角色设置为主播
            mRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setClientRole(CLIENT_ROLE_BROADCASTER);
            openSpeakerOn();
            mRtcEngine.adjustPlaybackSignalVolume(200);
            mRtcEngine.adjustRecordingSignalVolume(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinChannel(){
        EaseCallKitConfig callKitConfig = EaseCallKit.getInstance().getCallKitConfig();
        if(listener != null && callKitConfig != null && callKitConfig.isEnableRTCToken()){
            listener.onGenerateToken(EMClient.getInstance().getCurrentUser(),channelName,  EMClient.getInstance().getOptions().getAppKey(), new EaseCallKitTokenCallback(){
                @Override
                public void onSetToken(String token,int uId) {
                    Log.i("wyz","onSetToken token:" + token + " uid: " +uId);
                    //获取到Token uid加入频道
                    mRtcEngine.joinChannel(token, channelName,null,uId);
                }

                @Override
                public void onGetTokenError(int error, String errorMsg) {
                    Log.i("wyz","onGenerateToken error :" + error + " errorMsg:" + errorMsg);
                    //获取Token失败,退出呼叫
//                    exitChannel();
                }
            });
        }
    }

    /**
     * 开启扬声器
     */
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭扬声器
     */
    protected void closeSpeakerOn() {
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 长按按钮时传true，其它场景下传false
     * @param enabled
     */
    private void setupAudioConfig(boolean enabled) {
        /**
         * true:（默认）重新开启本地语音，即开启本地语音采集。
         * false: 关闭本地语音，即停止本地语音采集。
         */
        mRtcEngine.enableLocalAudio(enabled);
        /**
         * 是否取消发布本地音频流。
         * true：取消发布。
         * false：发布。
         */
        mRtcEngine.muteLocalAudioStream(!enabled);
    }



    public void exitWatchMovie() {
        NiceDialog.init().setLayoutId(R.layout.dialog_exit_movie)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                leaveChannel();
                                finish();
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
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

    /**
     * 离开频道
     */
    private void leaveChannel() {
        closeSpeakerOn();
        // 离开当前频道。
        if(mRtcEngine != null) {
            EaseCallKit.getInstance().releaseCall();
            mRtcEngine.leaveChannel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 是否触发按键为back键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isWatchMovie) {
                exitWatchMovie();
            } else {
                finish();
            }
            return true;
        }else{
            // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isWatchMovie) {
            exitWatchMovie();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveChannel();
        RtcEngine.destroy();
    }
}
