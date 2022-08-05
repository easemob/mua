package com.community.mua.ui;

import static io.agora.rtc.Constants.AUDIO_PROFILE_DEFAULT;
import static io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.callkit.EaseCallKit;
import com.community.mua.callkit.base.EaseCallKitConfig;
import com.community.mua.callkit.base.EaseCallKitListener;
import com.community.mua.callkit.base.EaseCallKitTokenCallback;
import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.callkit.base.EaseUserAccount;
import com.community.mua.databinding.ActivityWatchMovieBinding;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.SoundUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EMLog;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class WatchMovieActivity extends BaseActivity<ActivityWatchMovieBinding> {

    public static void start(Context context) {
        context.startActivity(new Intent(context, WatchMovieActivity.class));
    }


    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private String agoraAppId = null;
    protected String channelName;
    EaseCallKitListener listener = EaseCallKit.getInstance().getCallListener();
    protected AudioManager audioManager;

    @Override
    protected ActivityWatchMovieBinding getViewBinding() {
        return ActivityWatchMovieBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.ivMobile.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        channelName = SharedPreferUtil.getInstance().getPairBean().getMatchingCode();
        // 获取权限后，初始化 RtcEngine，并加入频道。
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initAgoraEngineAndJoinChannel();
        }
    }

    @Override
    protected void initListener() {

        mBinding.ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                leaveChannel();
                finish();
            }
        });
        mBinding.ivMobile.setOnTouchListener(new ButtonLongClick());
        mBinding.ivMobile.setOnLongClickListener(new ButtonLongClick());
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


    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
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
                    mBinding.ivMobile.setVisibility(View.VISIBLE);
                }
            });
        }

    };

    private void initAgoraEngineAndJoinChannel() {
        initializeEngine();
        setupAudioConfig(false);
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
            onBackPressed();
            return true;
        }else{
            // 如果不是back键正常响应
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveChannel();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveChannel();
        RtcEngine.destroy();
    }
}