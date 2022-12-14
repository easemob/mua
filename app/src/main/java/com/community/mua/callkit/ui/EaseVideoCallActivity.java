package com.community.mua.callkit.ui;


import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.community.mua.R;
import com.community.mua.callkit.EaseCallKit;

import com.community.mua.callkit.base.EaseCallFloatWindow;
import com.community.mua.callkit.base.EaseCallKitConfig;
import com.community.mua.callkit.base.EaseCallKitTokenCallback;
import com.community.mua.callkit.base.EaseCallUserInfo;
import com.community.mua.callkit.base.EaseGetUserAccountCallback;
import com.community.mua.callkit.base.EaseUserAccount;
import com.community.mua.callkit.event.*;
import com.community.mua.callkit.event.BaseEvent;
import com.community.mua.callkit.livedatas.EaseLiveDataBus;
import com.community.mua.callkit.utils.EaseCallAction;
import com.community.mua.callkit.base.EaseCallEndReason;
import com.community.mua.callkit.base.EaseCallKitListener;
import com.community.mua.callkit.base.EaseCallType;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.community.mua.imkit.utils.EaseUserUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.community.mua.callkit.utils.EaseCallState;
import com.community.mua.callkit.utils.EaseMsgUtils;
import com.community.mua.callkit.utils.EaseCallKitUtils;
import com.community.mua.callkit.widget.EaseImageView;
import com.community.mua.callkit.widget.MyChronometer;


import static com.community.mua.callkit.utils.EaseMsgUtils.CALL_INVITE_EXT;

import static io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
import static io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED;
import static io.agora.rtc.Constants.REMOTE_VIDEO_STATE_STOPPED;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.UserInfo;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;


/**
 * author lijian
 * email: Allenlee@easemob.com
 * date: 01/11/2021
 */
public class EaseVideoCallActivity extends EaseBaseCallActivity implements View.OnClickListener{

    private static final String TAG = EaseVideoCallActivity.class.getSimpleName();

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private View rootView;
    private Group comingBtnContainer;
    private ImageButton refuseBtn;
    private ImageButton answerBtn;
    private ImageButton hangupBtn;

    private Group groupHangUp;
    private Group groupUseInfo;
    private Group groupOngoingSettings;
    private TextView nickTextView;
    private boolean isMuteState = false;
    private boolean isHandsfreeState;
    private ImageView muteImage;
    private ImageView handsFreeImage;
    private ImageButton switchCameraBtn;
    private MyChronometer chronometer;
    private boolean surfaceStateChange = false;
    private EaseImageView avatarView;
    private TextView call_stateView;

    private Group videoCallingGroup;
    private Group voiceCallingGroup;
    private TextView tv_nick_voice;

    private Group videoCalledGroup;
    private Group voiceCalledGroup;

    private RelativeLayout video_transe_layout;
    private RelativeLayout video_transe_comming_layout;
    private ImageButton btn_voice_trans;
    private TextView tv_call_state_voice;
    private EaseImageView iv_avatar_voice;
    private ImageButton float_btn;

    //?????????????????????????????????
    protected boolean isInComingCall;
    // Judge whether is ongoing call
    protected boolean isOngoingCall;
    protected String username;
    protected String channelName;

    protected AudioManager audioManager;
    protected Ringtone ringtone;

    private boolean mMuted = false;
    private boolean mCallEnd = false;
    volatile private boolean mConfirm_ring = false;
    private String tokenUrl;
    private int remoteUId = 0;
    private boolean changeFlag = true;
    boolean transVoice = false;
    private String headUrl = null;
    private Bitmap headBitMap;
    private String ringFile;
    private MediaPlayer mediaPlayer;


    // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    protected RelativeLayout localSurface_layout;
    protected RelativeLayout oppositeSurface_layout;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;
    protected EaseCallType callType;
    private View Voice_View;
    private TimeHandler timehandler;

    private RtcEngine mRtcEngine;
    private boolean isMuteVideo = false;
    private String agoraAppId = null;
    // Camera direction: front or back
    private boolean isCameraFront;

    //?????????????????????????????????????????????
    private boolean requestOverlayPermission;

    //????????????Uid Map
    private Map<Integer, EaseUserAccount> uIdMap = new HashMap<>();
    EaseCallKitListener listener = EaseCallKit.getInstance().getCallListener();

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onError(int err) {
            super.onError(err);
            EMLog.d(TAG,"IRtcEngineEventHandler onError:" + err);
            if(listener != null){
                listener.onCallError(EaseCallKit.EaseCallError.RTC_ERROR,err,"rtc error");
            }
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            EMLog.d(TAG,"onJoinChannelSuccess channel:"+ channel + " uid" +uid);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isHandsfreeState = true;
                    openSpeakerOn();
                    if(EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VOICE_CALL){
                        handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                    }
                    if(!isInComingCall){
                        //??????????????????
                        if(EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL){
                            handler.sendEmptyMessage(EaseMsgUtils.MSG_MAKE_SIGNAL_VIDEO);
                        }else{
                            handler.sendEmptyMessage(EaseMsgUtils.MSG_MAKE_SIGNAL_VOICE);
                        }
                        //???????????????
                        timehandler.startTime();
                    }
                }
            });
        }

        @Override
        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onRejoinChannelSuccess(channel, uid, elapsed);
        }


        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
        }

        @Override
        public void onClientRoleChanged(int oldRole, int newRole) {
            super.onClientRoleChanged(oldRole, newRole);
        }

        @Override
        public void onLocalUserRegistered(int uid, String userAccount) {
            super.onLocalUserRegistered(uid, userAccount);
        }

        @Override
        public void onUserInfoUpdated(int uid, UserInfo userInfo) {
            super.onUserInfoUpdated(uid, userInfo);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //?????????????????????
                    makeOngoingStatus();

                    String userName = null;
                    if(uIdMap != null){
                        EaseUserAccount account = uIdMap.get(uid);
                        if(account != null){
                            userName = uIdMap.get(uid).getUserName();
                        }
                    }
                    setUserJoinChannelInfo(null,uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //????????????????????? ????????????
                    exitChannel();
                    if(uIdMap != null){
                        uIdMap.remove(uid);
                    }
                    if(listener != null){
                        //????????????
                        long time = getChronometerSeconds(chronometer);
                        listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonHangup,time * 1000);
                    }
                }
            });
        }


        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    remoteUId = uid;
                    if(callType == EaseCallType.SINGLE_VIDEO_CALL){
                        setupRemoteVideo(uid);
                    }
                }
            });
        }

        /** @deprecated */
        @Deprecated
        public void onFirstRemoteAudioFrame(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        remoteUId = uid;
                        startCount();
                        if(EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VOICE_CALL){
                            voiceCalledGroup.setVisibility(View.VISIBLE);
                            handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                        }
                }
            });
        }

        @Override
        public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //??????????????????
                    if(uid == remoteUId){
                        //????????????????????????
                        if(state == REMOTE_VIDEO_STATE_STOPPED || state == REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED){
                            callType = EaseCallType.SINGLE_VOICE_CALL;
                            EaseCallKit.getInstance().setCallType(EaseCallType.SINGLE_VOICE_CALL);
                            EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
                            isHandsfreeState = true;
                            openSpeakerOn();
                            handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                            changeVideoVoiceState();
                            if(mRtcEngine != null){
                                mRtcEngine.muteLocalVideoStream(true);
                                mRtcEngine.enableVideo();
                            }
                        }
                    }
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ease_video_call);

        //?????????
        if(savedInstanceState == null){
            initParams(getIntent().getExtras());
        }else{
            initParams(savedInstanceState);
        }

        //Init View
        initView();
        checkFloatIntent(getIntent());
        //??????LiveData??????
        addLiveDataObserver();

        //??????????????????
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
        }

        timehandler = new TimeHandler();

        EaseCallKit.getInstance().getNotifier().reset();
    }

    private void initParams(Bundle bundle){
        if(bundle != null) {
            isInComingCall = bundle.getBoolean("isComingCall", false);
            username = bundle.getString("username");
            channelName = bundle.getString("channelName");
            int uId = bundle.getInt("uId",-1);
            callType = EaseCallKit.getInstance().getCallType();
            if(uId == -1) {
                EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
            }else {
                isOngoingCall = true;
            }
        }else{
            isInComingCall = EaseCallKit.getInstance().getIsComingCall();
            username = EaseCallKit.getInstance().getFromUserId();
            channelName = EaseCallKit.getInstance().getChannelName();
            EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
        }
    }

    public void initView(){
        refuseBtn = findViewById(R.id.btn_refuse_call);
        answerBtn = findViewById(R.id.btn_answer_call);
        hangupBtn = findViewById(R.id.btn_hangup_call);
        comingBtnContainer = findViewById(R.id.ll_coming_call);
        avatarView = findViewById(R.id.iv_avatar);
        iv_avatar_voice = findViewById(R.id.iv_avatar_voice);

        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        switchCameraBtn = (ImageButton) findViewById(R.id.btn_switch_camera);

        //???????????????
        videoCallingGroup = findViewById(R.id.ll_video_calling);
        voiceCallingGroup = findViewById(R.id.ll_voice_calling);

        video_transe_layout = findViewById(R.id.bnt_video_transe);
        video_transe_comming_layout = findViewById(R.id.bnt_video_transe_comming);
        tv_nick_voice = findViewById(R.id.tv_nick_voice);
        tv_call_state_voice = findViewById(R.id.tv_call_state_voice);

        headUrl = EaseCallKitUtils.getUserHeadImage(username);
        ringFile = EaseCallKitUtils.getRingFile();

        //??????????????????
//        loadHeadImage1();
        if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
            EaseCallKitUtils.setUserAvatar(this,username,avatarView);
        } else {
            EaseCallKitUtils.setUserAvatar(this,username,iv_avatar_voice);
        }


        if(callType == EaseCallType.SINGLE_VIDEO_CALL){
            videoCallingGroup.setVisibility(View.VISIBLE);
            voiceCallingGroup.setVisibility(View.GONE);
            if(isInComingCall){
                video_transe_layout.setVisibility(View.GONE);
                video_transe_comming_layout.setVisibility(View.VISIBLE);
            }else{
                video_transe_layout.setVisibility(View.VISIBLE);
                video_transe_comming_layout.setVisibility(View.GONE);
            }
        }else{
            videoCallingGroup.setVisibility(View.GONE);
            video_transe_layout.setVisibility(View.GONE);
            video_transe_comming_layout.setVisibility(View.GONE);
            voiceCallingGroup.setVisibility(View.VISIBLE);
            hangupBtn.setVisibility(View.GONE);
            tv_nick_voice.setText(EaseCallKitUtils.getUserNick(username));
        }

        video_transe_layout.setOnClickListener(this);
        video_transe_comming_layout.setOnClickListener(this);

        //???????????????
        videoCalledGroup = findViewById(R.id.ll_video_called);
        voiceCalledGroup =findViewById(R.id.ll_voice_control);
        voiceCalledGroup.setVisibility(View.INVISIBLE);

        btn_voice_trans = findViewById(R.id.btn_voice_trans);
        btn_voice_trans.setOnClickListener(this);

        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);

        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);
        switchCameraBtn.setOnClickListener(this);

        // local surfaceview
        localSurface_layout = (RelativeLayout) findViewById(R.id.local_surface_layout);
        // remote surfaceview
        oppositeSurface_layout = (RelativeLayout) findViewById(R.id.opposite_surface_layout);
        groupHangUp = findViewById(R.id.group_hang_up);
        groupUseInfo = findViewById(R.id.group_use_info);
        groupOngoingSettings = findViewById(R.id.group_ongoing_settings);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        chronometer = (MyChronometer) findViewById(R.id.chronometer);
        call_stateView = (TextView)findViewById(R.id.tv_call_state) ;

        nickTextView.setText(EaseCallKitUtils.getUserNick(username));
        localSurface_layout.setOnClickListener(this);

        Voice_View = findViewById(R.id.view_ring);

        rootView = ((ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);

        float_btn = findViewById(R.id.btn_call_float);
        float_btn.setOnClickListener(this);

        if(isInComingCall){
            call_stateView.setText(getApplicationContext().getString(R.string.invite_you_for_audio_and_video_call));
            tv_call_state_voice.setText(getApplicationContext().getString(R.string.invite_you_for_audio_and_video_call));
        }else{
            call_stateView.setText(getApplicationContext().getString(R.string.waiting_for_accept));
            tv_call_state_voice.setText(getApplicationContext().getString(R.string.waiting_for_accept));
        }

        //?????????????????????
        if(callType == EaseCallType.SINGLE_VOICE_CALL){
            rootView.setBackground(getResources().getDrawable(R.drawable.call_bg_voice));
            //sufaceview?????????
            localSurface_layout.setVisibility(View.GONE);
            oppositeSurface_layout.setVisibility(View.GONE);
            answerBtn.setImageResource(R.drawable.answer);
            //????????????UI??????
            Voice_View.setVisibility(View.VISIBLE);
            avatarView.setVisibility(View.VISIBLE);
        }else{
            answerBtn.setImageResource(R.drawable.call_answer);
            avatarView.setVisibility(View.GONE);
        }

        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        if(!isInComingCall){
            //??????????????????
            makeCallStatus();

            //??????????????????
            initEngineAndJoinChannel();
        }else{
            //???????????????
            makeComingStatus();

            //????????????
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            if(ringUri != null){
                ringtone = RingtoneManager.getRingtone(this, ringUri);
            }
            AudioManager am = (AudioManager)this.getApplication().getSystemService(Context.AUDIO_SERVICE);
            int ringerMode = am.getRingerMode();
            if(ringerMode == AudioManager.RINGER_MODE_NORMAL){
                EMLog.e(TAG,"playRing start");
                playRing();
            }
        }

        if(isOngoingCall) {
            makeOngoingStatus();
        }
    }


    /**
     * ??????????????????
     */
    private void makeComingStatus() {
        comingBtnContainer.setVisibility(View.VISIBLE);
        groupUseInfo.setVisibility(View.VISIBLE);
        if(callType == EaseCallType.SINGLE_VIDEO_CALL){
            groupOngoingSettings.setVisibility(View.INVISIBLE);
            localSurface_layout.setVisibility(View.INVISIBLE);
        }else{
            avatarView.setVisibility(View.VISIBLE);
            nickTextView.setVisibility(View.VISIBLE);
        }
        groupHangUp.setVisibility(View.INVISIBLE);
        groupRequestLayout();
    }


    /**
     * ??????????????????
     */
    private void makeOngoingStatus() {
        isOngoingCall = true;
        comingBtnContainer.setVisibility(View.INVISIBLE);
        groupUseInfo.setVisibility(View.INVISIBLE);
        groupHangUp.setVisibility(View.VISIBLE);
        callType = EaseCallKit.getInstance().getCallType();
        EaseCallFloatWindow.getInstance().setCallType(callType);
        if(callType == EaseCallType.SINGLE_VIDEO_CALL){
            groupOngoingSettings.setVisibility(View.VISIBLE);
            localSurface_layout.setVisibility(View.VISIBLE);
            videoCalledGroup.setVisibility(View.VISIBLE);
            voiceCalledGroup.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            videoCallingGroup.setVisibility(View.GONE);
            voiceCallingGroup.setVisibility(View.GONE);
        }else{
            groupOngoingSettings.setVisibility(View.VISIBLE);
            avatarView.setVisibility(View.VISIBLE);
            localSurface_layout.setVisibility(View.GONE);
            oppositeSurface_layout.setVisibility(View.GONE);
            nickTextView.setVisibility(View.VISIBLE);
            videoCalledGroup.setVisibility(View.INVISIBLE);
            voiceCalledGroup.setVisibility(View.VISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);

            videoCallingGroup.setVisibility(View.GONE);
            voiceCallingGroup.setVisibility(View.VISIBLE);
            tv_nick_voice.setText(EaseCallKitUtils.getUserNick(username));
            tv_call_state_voice.setText(getApplicationContext().getString(R.string.in_the_call));
        }

        video_transe_layout.setVisibility(View.GONE);
        video_transe_comming_layout.setVisibility(View.GONE);
        groupRequestLayout();
    }

    /**
     * ?????????????????????
     */
    public void makeCallStatus() {
        if(!isInComingCall && callType == EaseCallType.SINGLE_VOICE_CALL){
            voiceCalledGroup.setVisibility(View.INVISIBLE);
        }else{
            voiceCalledGroup.setVisibility(View.INVISIBLE);
           //oppositeSurface_layout.setVisibility(View.INVISIBLE);
        }
        comingBtnContainer.setVisibility(View.INVISIBLE);
        groupUseInfo.setVisibility(View.VISIBLE);
        groupOngoingSettings.setVisibility(View.INVISIBLE);
        localSurface_layout.setVisibility(View.INVISIBLE);
        groupHangUp.setVisibility(View.VISIBLE);
        groupRequestLayout();
    }

    public void groupRequestLayout() {
        comingBtnContainer.requestLayout();
        //voiceCalledGroup.requestLayout();
        groupHangUp.requestLayout();
        groupUseInfo.requestLayout();
        groupOngoingSettings.requestLayout();
    }


    private void initEngineAndJoinChannel() {
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();
    }

    private void initializeEngine() {
        try {
            EaseCallKitConfig config =  EaseCallKit.getInstance().getCallKitConfig();
            if(config != null){
                agoraAppId = config.getAgoraAppId();
            }
            mRtcEngine = RtcEngine.create(getBaseContext(), agoraAppId, mRtcEventHandler);
            //?????????????????? ????????????????????? ?????????????????????
            mRtcEngine.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.setClientRole(CLIENT_ROLE_BROADCASTER);

            EaseCallFloatWindow.getInstance().setRtcEngine(getApplicationContext(), mRtcEngine);
        } catch (Exception e) {
            EMLog.e(TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoConfig() {
        if(EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL){
            mRtcEngine.enableVideo();
            mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_1280x720,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
            isCameraFront = true;
        }else{
            mRtcEngine.disableVideo();
        }
    }

    private void setupLocalVideo() {
        if(isFloatWindowShowing()) {
            return;
        }
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        //view.setZOrderMediaOverlay(true);
//        localSurface_layout.addView(view);
        oppositeSurface_layout.addView(view);
        mLocalVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);
    }


    private void setupRemoteVideo(int uid) {
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        oppositeSurface_layout.removeAllViews();
        oppositeSurface_layout.addView(view);
        mRemoteVideo = new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN,uid);
        mRtcEngine.setupRemoteVideo(mRemoteVideo);

        SurfaceView localView = RtcEngine.CreateRendererView(getBaseContext());
        localSurface_layout.removeAllViews();
        localView.setZOrderMediaOverlay(true);
        localSurface_layout.addView(localView);
        mLocalVideo = new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(mLocalVideo);
    }

    /**
     * ????????????
     */
    private void joinChannel() {
        EaseCallKitConfig callKitConfig = EaseCallKit.getInstance().getCallKitConfig();
        if(listener != null && callKitConfig != null && callKitConfig.isEnableRTCToken()){
            listener.onGenerateToken(EMClient.getInstance().getCurrentUser(),channelName,  EMClient.getInstance().getOptions().getAppKey(), new EaseCallKitTokenCallback(){
                @Override
                public void onSetToken(String token,int uId) {
                    EMLog.d(TAG,"onSetToken token:" + token + " uid: " +uId);
                    //?????????Token uid????????????
                    mRtcEngine.joinChannel(token, channelName,null,uId);
                    //??????????????????uIdMap
                    uIdMap.put(uId,new EaseUserAccount(uId,EMClient.getInstance().getCurrentUser()));
                }

                @Override
                public void onGetTokenError(int error, String errorMsg) {
                    EMLog.e(TAG,"onGenerateToken error :" + error + " errorMsg:" + errorMsg);
                    //??????Token??????,????????????
                    exitChannel();
                }
            });
        }
    }

    private void changeCameraDirection(boolean isFront) {
        if(isCameraFront != isFront) {
            if(mRtcEngine != null){
                mRtcEngine.switchCamera();
            }
            isCameraFront = isFront;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_refuse_call) {
            stopPlayRing();
            if(isInComingCall){
                stopCount();

                //??????????????????
                AnswerEvent event = new AnswerEvent();
                event.result = EaseMsgUtils.CALL_ANSWER_REFUSE;
                event.callId = EaseCallKit.getInstance().getCallID();
                event.callerDevId = EaseCallKit.getInstance().getClallee_devId();
                event.calleeDevId = EaseCallKit.deviceId;
                sendCmdMsg(event,username);
            }

        } else if (id == R.id.btn_answer_call) {
            if(isInComingCall){
                stopPlayRing();
                //??????????????????
                AnswerEvent event = new AnswerEvent();
                event.result = EaseMsgUtils.CALL_ANSWER_ACCEPT;
                event.callId = EaseCallKit.getInstance().getCallID();
                event.callerDevId = EaseCallKit.getInstance().getClallee_devId();
                event.calleeDevId = EaseCallKit.deviceId;
                if (TextUtils.isEmpty(username)){
                    username = EaseCallKit.getInstance().getFromUserId();
                }
                if (TextUtils.isEmpty(channelName)){
                    channelName = EaseCallKit.getInstance().getChannelName();
                }
                sendCmdMsg(event,username);
            }
        } else if (id == R.id.btn_hangup_call) {
            stopCount();
            if(remoteUId == 0){
                CallCancelEvent cancelEvent = new CallCancelEvent();
                sendCmdMsg(cancelEvent,username);
            }else{
                exitChannel();
                if(listener != null){
                    //????????????????????????
                    long time = getChronometerSeconds(chronometer);
                    listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonHangup,time *1000);
                }
            }
        } else if(id == R.id.local_surface_layout){
            changeSurface();
        } else if(id == R.id.btn_call_float){
            showFloatWindow();
        } else if (id == R.id.iv_mute) { // mute
            if (isMuteState) {
                // resume voice transfer
                muteImage.setImageResource(R.drawable.call_mute_normal);
                mRtcEngine.muteLocalAudioStream(false);
                isMuteState = false;
            } else {
                // pause voice transfer
                muteImage.setImageResource(R.drawable.call_mute_on);
                mRtcEngine.muteLocalAudioStream(true);
                isMuteState = true;
            }
        } else if (id == R.id.iv_handsfree) { // handsfree
            if (isHandsfreeState) {
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_normal);
                closeSpeakerOn();
                isHandsfreeState = false;
            } else {
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                openSpeakerOn();
                isHandsfreeState = true;
            }
        }else if(id == R.id.btn_switch_camera){
            changeCameraDirection(!isCameraFront);
        }else if(id == R.id.btn_voice_trans){
            if(callType == EaseCallType.SINGLE_VOICE_CALL){
                callType = EaseCallType.SINGLE_VIDEO_CALL;
                EaseCallKit.getInstance().setCallType(EaseCallType.SINGLE_VIDEO_CALL);
                EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
                changeVideoVoiceState();
                if(mRtcEngine != null){
                    mRtcEngine.muteLocalVideoStream(false);
                }
            }else{
                callType = EaseCallType.SINGLE_VOICE_CALL;
                EaseCallKit.getInstance().setCallType(EaseCallType.SINGLE_VOICE_CALL);
                EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
                isHandsfreeState = true;
                openSpeakerOn();
                handsFreeImage.setImageResource(R.drawable.em_icon_speaker_on);
                changeVideoVoiceState();
                if(mRtcEngine != null){
                    mRtcEngine.muteLocalVideoStream(true);
                }
            }
        }else if(id == R.id.bnt_video_transe_comming || id == R.id.bnt_video_transe){
            //???????????????????????????
            callType = EaseCallType.SINGLE_VOICE_CALL;
            EaseCallKit.getInstance().setCallType(EaseCallType.SINGLE_VOICE_CALL);
            EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
            if(mRtcEngine != null){
                mRtcEngine.disableVideo();
                mRtcEngine.muteLocalVideoStream(true);
            }
            localSurface_layout.setVisibility(View.GONE);
            oppositeSurface_layout.setVisibility(View.GONE);
            rootView.setBackground(getResources().getDrawable(R.drawable.call_bg_voice));

//            loadHeadImage();
            if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
                EaseCallKitUtils.setUserAvatar(this,username,avatarView);
            } else {
                EaseCallKitUtils.setUserAvatar(this,username,iv_avatar_voice);
            }

            videoCallingGroup.setVisibility(View.GONE);
            video_transe_layout.setVisibility(View.GONE);
            video_transe_comming_layout.setVisibility(View.GONE);
            voiceCallingGroup.setVisibility(View.VISIBLE);
            tv_nick_voice.setText(EaseCallKitUtils.getUserNick(username));
//            if(!isInComingCall){
//                voiceCalledGroup.setVisibility(View.VISIBLE);
//            }
            if(isInComingCall){
                stopPlayRing();
                //??????????????????
                AnswerEvent event = new AnswerEvent();
                event.result = EaseMsgUtils.CALL_ANSWER_ACCEPT;
                event.callId = EaseCallKit.getInstance().getCallID();
                event.callerDevId = EaseCallKit.getInstance().getClallee_devId();
                event.calleeDevId = EaseCallKit.deviceId;
                event.transVoice = true;
                sendCmdMsg(event,username);
            }else{
                //?????????????????????
                VideoToVoiceeEvent event = new VideoToVoiceeEvent();
                sendCmdMsg(event,username);
            }
        }
    }

    private void changeSurface(){
        if(changeFlag){
            SurfaceView remoteview = RtcEngine.CreateRendererView(getBaseContext());
            localSurface_layout.removeAllViews();
            localSurface_layout.addView(remoteview);
            remoteview.setZOrderMediaOverlay(true);
            mRemoteVideo = new VideoCanvas(remoteview, VideoCanvas.RENDER_MODE_HIDDEN,remoteUId);
            mRtcEngine.setupRemoteVideo(mRemoteVideo);


            SurfaceView localview = RtcEngine.CreateRendererView(getBaseContext());
            oppositeSurface_layout.removeAllViews();
            oppositeSurface_layout.addView(localview);
            mLocalVideo = new VideoCanvas(localview, VideoCanvas.RENDER_MODE_HIDDEN, 0);
            mRtcEngine.setupLocalVideo(mLocalVideo);

            changeFlag = !changeFlag;

        }else{
            SurfaceView localview = RtcEngine.CreateRendererView(getBaseContext());
            localview.setZOrderMediaOverlay(true);
            localSurface_layout.removeAllViews();
            localSurface_layout.addView(localview);
            mLocalVideo = new VideoCanvas(localview, VideoCanvas.RENDER_MODE_HIDDEN, 0);
            mRtcEngine.setupLocalVideo(mLocalVideo);

            SurfaceView remoteview = RtcEngine.CreateRendererView(getBaseContext());
            oppositeSurface_layout.removeAllViews();
            oppositeSurface_layout.addView(remoteview);
            mRemoteVideo = new VideoCanvas(remoteview, VideoCanvas.RENDER_MODE_HIDDEN,remoteUId);
            mRtcEngine.setupRemoteVideo(mRemoteVideo);
            changeFlag = !changeFlag;
        }
    }


    /**
     * ????????????
     */
    private void leaveChannel() {
        // ?????????????????????
        if(mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }


    void changeVideoVoiceState(){
        if(callType == EaseCallType.SINGLE_VIDEO_CALL){//?????????????????????UI
            //????????????UI??????
            Voice_View.setVisibility(View.GONE);
            avatarView.setVisibility(View.GONE);

            //sufaceview?????????
            localSurface_layout.setVisibility(View.VISIBLE);
            oppositeSurface_layout.setVisibility(View.VISIBLE);

            makeOngoingStatus();
        }else{ // ?????????????????????UI
            localSurface_layout.setVisibility(View.GONE);
            oppositeSurface_layout.setVisibility(View.GONE);
            rootView.setBackground(getResources().getDrawable(R.drawable.call_bg_voice));

            //??????????????????
            if(EaseCallKit.getInstance().getCallState() == EaseCallState.CALL_ANSWERED){
                //????????????UI??????
                Voice_View.setVisibility(View.VISIBLE);
                avatarView.setVisibility(View.VISIBLE);
                tv_call_state_voice.setText(getApplicationContext().getString(R.string.in_the_call));
                makeOngoingStatus();
            }else{
                localSurface_layout.setVisibility(View.GONE);
                oppositeSurface_layout.setVisibility(View.GONE);
                rootView.setBackground(getResources().getDrawable(R.drawable.call_bg_voice));

                if(isInComingCall){
                    tv_call_state_voice.setText(getApplicationContext().getString(R.string.invite_you_for_audio_and_video_call));
                }else{
                    tv_call_state_voice.setText(getApplicationContext().getString(R.string.waiting_for_accept));
//                    if(!isInComingCall){
//                        voiceCalledGroup.setVisibility(View.VISIBLE);
//                    }
                }
                videoCallingGroup.setVisibility(View.GONE);
                video_transe_layout.setVisibility(View.GONE);
                video_transe_comming_layout.setVisibility(View.GONE);
                voiceCallingGroup.setVisibility(View.VISIBLE);
                tv_nick_voice.setText(EaseCallKitUtils.getUserNick(username));
            }
            if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
                EaseCallKitUtils.setUserAvatar(this,username,avatarView);
            } else {
                EaseCallKitUtils.setUserAvatar(this,username,iv_avatar_voice);
            }
//            loadHeadImage();
        }
    }



    /**
     * ??????LiveData??????
     */
    protected void addLiveDataObserver(){
        EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString(), BaseEvent.class).observe(this, event -> {
            if(event != null) {
                switch (event.callAction){
                    case CALL_ALERT:
                         AlertEvent alertEvent = (AlertEvent)event;
                         //????????????????????????
                         ConfirmRingEvent ringEvent = new ConfirmRingEvent();
                         if(TextUtils.equals(alertEvent.callId, EaseCallKit.getInstance().getCallID())
                                 && EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_ANSWERED) {
                             //????????????????????????
                             ringEvent.calleeDevId = alertEvent.calleeDevId;
                             ringEvent.callId = alertEvent.callId;
                             ringEvent.valid = true;
                             sendCmdMsg(ringEvent,username);
                         }else{
                             //????????????????????????
                             ringEvent.calleeDevId = alertEvent.calleeDevId;
                             ringEvent.callId = alertEvent.callId;
                             ringEvent.valid = false;
                             sendCmdMsg(ringEvent, username);
                         }
                         //?????????????????????????????????
                         mConfirm_ring = true;
                         break;
                    case CALL_CANCEL:
                         if(!isInComingCall){
                             //?????????????????????
                             timehandler.stopTime();
                         }
                         //????????????
                         exitChannel();
                        if(listener != null){
                            //????????????
                            listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonRemoteCancel,0);
                        }
                        break;
                    case CALL_ANSWER:
                        AnswerEvent answerEvent = (AnswerEvent)event;
                        ConfirmCallEvent callEvent = new ConfirmCallEvent();
                        boolean transVoice = answerEvent.transVoice;
                        callEvent.calleeDevId = answerEvent.calleeDevId;
                        callEvent.callerDevId = answerEvent.callerDevId;
                        callEvent.result = answerEvent.result;
                        callEvent.callId = answerEvent.callId;
                        if(TextUtils.equals(answerEvent.result, EaseMsgUtils.CALL_ANSWER_BUSY)) {
                             if(!mConfirm_ring){
                                 //????????????
                                timehandler.stopTime();
                                runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         //???????????????????????????
                                         String info = getString(R.string.The_other_is_busy);
                                         Toast.makeText(getApplicationContext(),info , Toast.LENGTH_SHORT).show();
                                         //????????????
                                         exitChannel();

                                         if(listener != null){
                                             //?????????????????????
                                             listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonBusy,0); }
                                         }
                                 });
                             }else{
                                 timehandler.stopTime();
                                 sendCmdMsg(callEvent,username);
                             }
                         }else if(TextUtils.equals(answerEvent.result, EaseMsgUtils.CALL_ANSWER_ACCEPT)){
                             //???????????????
                             EaseCallKit.getInstance().setCallState(EaseCallState.CALL_ANSWERED);
                             timehandler.stopTime();
                             sendCmdMsg(callEvent,username);
                             if(transVoice){
                                 runOnUiThread(new Runnable() {
                                     @Override
                                     public void run() {
                                         callType = EaseCallType.SINGLE_VOICE_CALL;
                                         EaseCallKit.getInstance().setCallType(EaseCallType.SINGLE_VOICE_CALL);
                                         EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
                                         changeVideoVoiceState();
                                     }

                                 });
                             }
                         }else if(TextUtils.equals(answerEvent.result, EaseMsgUtils.CALL_ANSWER_REFUSE)){
                             timehandler.stopTime();
                             sendCmdMsg(callEvent,username);
                         }
                         break;
                    case CALL_INVITE:
                         //?????????????????????
                         InviteEvent inviteEvent = (InviteEvent)event;
                         if(inviteEvent.type == EaseCallType.SINGLE_VOICE_CALL){
                             callType = EaseCallType.SINGLE_VOICE_CALL;
                             EaseCallKit.getInstance().setCallType(EaseCallType.SINGLE_VOICE_CALL);
                             EaseCallFloatWindow.getInstance(getApplicationContext()).setCallType(callType);
                             if(mRtcEngine != null){
                                 mRtcEngine.disableVideo();
                             }
                             changeVideoVoiceState();
                         }
                         break;
                    case CALL_CONFIRM_RING:
                         break;
                    case CALL_CONFIRM_CALLEE:
                         ConfirmCallEvent confirmEvent = (ConfirmCallEvent)event;
                         String deviceId = confirmEvent.calleeDevId;
                         String result = confirmEvent.result;
                         timehandler.stopTime();
                         //??????????????????????????????
                         if(TextUtils.equals(deviceId, EaseCallKit.deviceId)){

                             //????????????????????????
                             if(TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                                 EaseCallKit.getInstance().setCallState(EaseCallState.CALL_ANSWERED);
                                 //????????????
                                 initEngineAndJoinChannel();
                                 makeOngoingStatus();

                             }else if(TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_REFUSE)){
                                 //????????????
                                 exitChannel();
                             }
                         }else{
                             runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     //??????????????????????????????
                                     String info = null;
                                     if(TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                                         //???????????????????????????
                                         info = getString(R.string.The_other_is_recived);

                                     }else if(TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_REFUSE)){
                                         //???????????????????????????
                                         info = getString(R.string.The_other_is_refused);
                                     }
                                     Toast.makeText(getApplicationContext(),info , Toast.LENGTH_SHORT).show();
                                     //????????????
                                     exitChannel();

                                     if(listener != null){
                                         //???????????????????????????
                                         listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonHandleOnOtherDevice,0);
                                     }
                                 }
                             });
                         }

                         break;
                }
            }
        });

        EaseLiveDataBus.get().with(EaseCallKitUtils.UPDATE_USERINFO, EaseCallUserInfo.class).observe(this, userInfo -> {
            if (userInfo != null) {
                if(TextUtils.equals(userInfo.getUserId(), username)){
                    //????????????????????????
                    EaseCallKit.getInstance().getCallKitConfig().setUserInfo(username,userInfo);
                    updateUserInfo();
                }
            }
        });
    }

    /**
     * ??????????????????
     */
    HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");
    { callHandlerThread.start(); }
    protected Handler handler = new Handler(callHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 100: // 1V1????????????
                    sendInviteeMsg(username, EaseCallType.SINGLE_VOICE_CALL);
                    break;
                case 101: // 1V1????????????
                    sendInviteeMsg(username, EaseCallType.SINGLE_VIDEO_CALL);
                    break;
                case 301: //????????????????????????
                    //??????????????????
                    handler.removeMessages(100);
                    handler.removeMessages(101);
                    handler.removeMessages(102);
                    callHandlerThread.quit();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ????????????????????????
     * @param username
     * @param callType
     */
    private void sendInviteeMsg(String username, EaseCallType callType){
        //???????????? ??????
        setUserJoinChannelInfo(username,0);

        mConfirm_ring = false;
        final EMMessage message;
        if(callType == EaseCallType.SINGLE_VIDEO_CALL){
            message = EMMessage.createTxtSendMessage( getApplicationContext().getString(R.string.invite_you_for_video_call), username);
        }else{
            message = EMMessage.createTxtSendMessage( getApplicationContext().getString(R.string.invite_you_for_audio_call), username);
        }
        message.setAttribute(EaseMsgUtils.CALL_ACTION, EaseCallAction.CALL_INVITE.state);
        message.setAttribute(EaseMsgUtils.CALL_CHANNELNAME, channelName);
        message.setAttribute(EaseMsgUtils.CALL_TYPE,callType.code);
        message.setAttribute(EaseMsgUtils.CALL_DEVICE_ID, EaseCallKit.deviceId);
        JSONObject object = EaseCallKit.getInstance().getInviteExt();
        if(object != null){
            message.setAttribute(CALL_INVITE_EXT, object);
        }else{
            try {
                JSONObject obj = new JSONObject();
                message.setAttribute(CALL_INVITE_EXT, obj);
            }catch (Exception e){
                e.getStackTrace();
            }
        }

        //??????????????????
        JSONObject extObject = new JSONObject();
        try {
            EaseCallType type = EaseCallKit.getInstance().getCallType();
            if(type == EaseCallType.SINGLE_VOICE_CALL){
                String info = getApplication().getString(R.string.alert_request_voice, EMClient.getInstance().getCurrentUser());
                extObject.putOpt("em_push_title",info);
                extObject.putOpt("em_push_content",info);
            }else{
                String info = getApplication().getString(R.string.alert_request_video, EMClient.getInstance().getCurrentUser());
                extObject.putOpt("em_push_title",info);
                extObject.putOpt("em_push_content",info);
            }
            extObject.putOpt("isRtcCall",true);
            extObject.putOpt("callType",type.code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setAttribute("em_apns_ext", extObject);

        if(EaseCallKit.getInstance().getCallID() == null){
            EaseCallKit.getInstance().setCallID(EaseCallKitUtils.getRandomString(10));
        }
        message.setAttribute(EaseMsgUtils.CLL_ID, EaseCallKit.getInstance().getCallID());

        message.setAttribute(EaseMsgUtils.CLL_TIMESTRAMEP, System.currentTimeMillis());
        message.setAttribute(EaseMsgUtils.CALL_MSG_TYPE, EaseMsgUtils.CALL_MSG_INFO);

        final EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username, EMConversation.EMConversationType.Chat, true);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                EMLog.d(TAG, "Invite call success");
                if(listener != null){
                    listener.onInViteCallMessageSent();
                }
            }

            @Override
            public void onError(int code, String error) {
                EMLog.e(TAG, "Invite call error " + code + ", " + error);
                if(listener != null){
                    listener.onCallError(EaseCallKit.EaseCallError.IM_ERROR,code,error);
                    listener.onInViteCallMessageSent();
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }


    /**
     * ??????CMD????????????
     * @param username
     */
    private void  sendCmdMsg(BaseEvent event,String username){
        EaseCallKit.getInstance().sendCmdMsg(event, username, new EMCallBack() {
            @Override
            public void onSuccess() {
                if(event.callAction == EaseCallAction.CALL_CANCEL){
                    //????????????
                    resetState();

                    boolean cancel = ((CallCancelEvent)event).cancel;
                    if(cancel){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(listener != null){
                                    //????????????
                                    listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonCancel,0);
                                }
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(listener != null){
                                    //???????????????
                                    listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonRemoteNoResponse,0);
                                }
                            }
                        });
                    }
                }else if(event.callAction == EaseCallAction.CALL_CONFIRM_CALLEE){
                    //?????????????????? ????????????
                    if(!TextUtils.equals(((ConfirmCallEvent)event).result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                        resetState();
                        String result = ((ConfirmCallEvent)event).result;

                        //??????????????????
                        if(TextUtils.equals(result, EaseMsgUtils.CALL_ANSWER_REFUSE)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(listener != null){
                                        listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonRefuse,0);
                                    }
                                }
                            });
                        }
                    }
                }else if(event.callAction == EaseCallAction.CALL_ANSWER){
                    //????????????????????????????????????????????????
                    timehandler.startTime();
                }
            }

            @Override
            public void onError(int code, String error) {
                EMLog.e(TAG, "Invite call error " + code + ", " + error);
                if(listener != null){
                    listener.onCallError(EaseCallKit.EaseCallError.IM_ERROR,code,error);
                }
                if(event.callAction == EaseCallAction.CALL_CANCEL){
                    //????????????
                    resetState();
                }else if(event.callAction == EaseCallAction.CALL_CONFIRM_CALLEE){
                    //?????????????????? ????????????
                    if(!TextUtils.equals(((ConfirmCallEvent)event).result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                        resetState();
                    }
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private class TimeHandler extends Handler {
        private final int MSG_TIMER = 0;
        private DateFormat dateFormat = null;
        private int timePassed = 0;

        public TimeHandler() {
            dateFormat = new SimpleDateFormat("HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        public void startTime() {
            timePassed = 0;
            sendEmptyMessageDelayed(MSG_TIMER, 1000);
        }

        public void stopTime() {
            removeMessages(MSG_TIMER);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TIMER) {
                // TODO: update calling time.
                timePassed++;
                Log.e("TAG", "TimeHandler timePassed: "+timePassed);
                String time = dateFormat.format(timePassed * 1000);

                long intervalTime;
                EaseCallKitConfig callKitConfig = EaseCallKit.getInstance().getCallKitConfig();
                if(callKitConfig != null){
                    intervalTime = callKitConfig.getCallTimeOut();
                }else{
                    intervalTime = EaseMsgUtils.CALL_INVITE_INTERVAL;
                }
                if(timePassed *1000 == intervalTime){

                    //????????????
                    timehandler.stopTime();
                    if(!isInComingCall){
                        CallCancelEvent cancelEvent = new CallCancelEvent();
                        cancelEvent.cancel  = false;
                        cancelEvent.remoteTimeout =true;

                        //?????????????????????,????????????
                        sendCmdMsg(cancelEvent,username);
                    }else{
                        //??????????????????????????????
                        exitChannel();
                        if(listener != null){
                            //??????????????????
                            listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonRemoteNoResponse,0);
                        }
                    }
                }
                sendEmptyMessageDelayed(MSG_TIMER, 1000);
                return;
            }
            super.handleMessage(msg);
        }
    }

    public long getChronometerSeconds(MyChronometer cmt) {
        if(cmt == null) {
            EMLog.e(TAG, "MyChronometer is null, can not get the cost seconds!");
            return 0;
        }
        return cmt.getCostSeconds();
    }


    /**
     * ????????????????????????
     * @return
     */
    private void loadHeadImage1() {
        if(headUrl != null) {
            if (headUrl.startsWith("http://") || headUrl.startsWith("https://")) {
                new AsyncTask<String, Void, Bitmap>() {
                    //?????????????????????????????????????????????????????????????????????UI???UI??????????????????
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        Bitmap bitmap = null;
                        try {
                            String url = params[0];
                            URL HttpURL = new URL(url);
                            HttpURLConnection conn = (HttpURLConnection) HttpURL.openConnection();
                            conn.setDoInput(true);
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return bitmap;
                    }

                    //???doInBackground ??????????????????onPostExecute ????????????UI ???????????????
                    // ????????????????????????????????????????????????UI??????????????????????????????????????????.
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        if (bitmap != null) {
                            if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
                                avatarView.setImageBitmap(bitmap);
                            } else {
                                iv_avatar_voice.setImageBitmap(bitmap);
                            }
                        }
                    }
                }.execute(headUrl);
            } else {
                if(headBitMap == null){
                    //????????????????????????????????????????????????????????????????????????????????????Bitmap??????
                    headBitMap = BitmapFactory.decodeFile(headUrl);
                }
                if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
                        avatarView.setImageBitmap(headBitMap);
                    } else {
                    iv_avatar_voice.setImageBitmap(headBitMap);
                }
            }
        }
    }

    /**
     * ????????????????????????
     * @param userName
     * @param uId
     */
    private void setUserJoinChannelInfo(String userName,int uId){
        if (listener != null) {
            listener.onRemoteUserJoinChannel(channelName, userName, uId, new EaseGetUserAccountCallback() {
                @Override
                public void onUserAccount(List<EaseUserAccount> userAccounts) {
                    if (userAccounts != null && userAccounts.size() > 0) {
                        for (EaseUserAccount account : userAccounts) {
                            uIdMap.put(account.getUid(), account);
                        }
                    }
                    updateUserInfo();
                }

                @Override
                public void onSetUserAccountError(int error, String errorMsg) {
                    EMLog.e(TAG,"onRemoteUserJoinChannel error:" + error + "  errorMsg:" + errorMsg);
                }
            });
        }
    }

    /**
     * ????????????????????????
     */
    private void updateUserInfo(){
        //????????????????????????
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //??????
                headUrl = EaseCallKitUtils.getUserHeadImage(username);
//                loadHeadImage();
                if (EaseCallKit.getInstance().getCallType() == EaseCallType.SINGLE_VIDEO_CALL) {
                    EaseCallKitUtils.setUserAvatar(EaseVideoCallActivity.this,username,avatarView);
                } else {
                    EaseCallKitUtils.setUserAvatar(EaseVideoCallActivity.this,username,iv_avatar_voice);
                }
                //??????
                tv_nick_voice.setText(EaseCallKitUtils.getUserNick(username));
            }
        });
    }



    private void playRing(){
      if(ringFile != null){
           mediaPlayer = new MediaPlayer();
           try {
              mediaPlayer.setDataSource(ringFile);
              if (!mediaPlayer.isPlaying()){
                  mediaPlayer.prepare();
                  mediaPlayer.start();
                  Log.e(TAG,"playRing play file");
              }
          } catch (IOException e) {
               mediaPlayer = null;
          }
      }else{
          EMLog.d(TAG,"playRing start play");
          if(ringtone != null){
              ringtone.play();
              Log.e(TAG,"playRing play ringtone");
          }
          EMLog.d(TAG,"playRing start play end");
      }
    }

    private void stopPlayRing(){
        if(ringFile != null){
            if(mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer = null;
            }
        }else{
            if(ringtone != null){
                ringtone.stop();
            }
        }
    }

    /**
     * ???????????????
     */
    @Override
    public void doShowFloatWindow() {
        super.doShowFloatWindow();
        if(chronometer != null) {
            EaseCallFloatWindow.getInstance().setCostSeconds(chronometer.getCostSeconds());
        }
        EaseCallFloatWindow.getInstance().show();
        boolean surface = true;
        if(isInComingCall && EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_ANSWERED){
            surface = false;
        }
        EaseCallFloatWindow.getInstance().update(!changeFlag,0, remoteUId,surface);
        EaseCallFloatWindow.getInstance().setCameraDirection(isCameraFront, changeFlag);
        moveTaskToBack(false);
    }



    /**
     * ???????????????
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
     * ???????????????
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

    private void resetState(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isInComingCall){
                    stopPlayRing();
                }
                isOngoingCall = false;

                finish();
            }
        });
    }

    /**
     * ????????????
     */
    void exitChannel(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EMLog.i(TAG, "exit channel channelName: " + channelName);
                if(isFloatWindowShowing()){
                    EaseCallFloatWindow.getInstance(getApplicationContext()).dismiss();
                }

                //????????????
                EaseCallKit.getInstance().setCallState(EaseCallState.CALL_IDLE);
                EaseCallKit.getInstance().setCallID(null);
                resetState();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkFloatIntent(intent);
    }

    private void checkFloatIntent(Intent intent) {
        // ??????activity????????????start???????????????window?????????
        if(isFloatWindowShowing()) {
            EaseCallFloatWindow.SingleCallInfo callInfo = EaseCallFloatWindow.getInstance().getSingleCallInfo();
            if(callInfo != null) {
                remoteUId = callInfo.remoteUid;
                changeFlag = callInfo.changeFlag;
                isCameraFront = callInfo.isCameraFront;
                if(EaseCallKit.getInstance().getCallState()==EaseCallState.CALL_ANSWERED){
                    if(changeFlag && remoteUId != 0) {
                        SurfaceView remoteView = RtcEngine.CreateRendererView(getBaseContext());
                        oppositeSurface_layout.removeAllViews();
                        oppositeSurface_layout.addView(remoteView);
                        mRemoteVideo = new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUId);
                        mRtcEngine.setupRemoteVideo(mRemoteVideo);

                        SurfaceView localView = RtcEngine.CreateRendererView(getBaseContext());
                        localSurface_layout.removeAllViews();
                        localSurface_layout.addView(localView);
                        mLocalVideo = new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
                        mRtcEngine.setupLocalVideo(mLocalVideo);
                    }else {
                        SurfaceView localview = RtcEngine.CreateRendererView(getBaseContext());
                        oppositeSurface_layout.removeAllViews();
                        oppositeSurface_layout.addView(localview);
                        mLocalVideo = new VideoCanvas(localview, VideoCanvas.RENDER_MODE_HIDDEN, 0);
                        mRtcEngine.setupLocalVideo(mLocalVideo);

                        SurfaceView remoteview = RtcEngine.CreateRendererView(getBaseContext());
                        localSurface_layout.removeAllViews();
                        localSurface_layout.addView(remoteview);
                        mRemoteVideo = new VideoCanvas(remoteview, VideoCanvas.RENDER_MODE_HIDDEN,remoteUId);
                        mRtcEngine.setupRemoteVideo(mRemoteVideo);
                    }
                }else{
                    if(!isInComingCall){
                        SurfaceView localview = RtcEngine.CreateRendererView(getBaseContext());
                        oppositeSurface_layout.removeAllViews();
                        oppositeSurface_layout.addView(localview);
                        mLocalVideo = new VideoCanvas(localview, VideoCanvas.RENDER_MODE_HIDDEN, 0);
                        mRtcEngine.setupLocalVideo(mLocalVideo);
                    }
                }
                changeCameraDirection(isCameraFront);
            }
            long totalCostSeconds = EaseCallFloatWindow.getInstance().getTotalCostSeconds();
            chronometer.setBase(SystemClock.elapsedRealtime() - totalCostSeconds * 1000);
            chronometer.start();
        }
        EaseCallFloatWindow.getInstance().dismiss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EMLog.i(TAG, "onActivityResult: " + requestCode + ", result code: " + resultCode);
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestOverlayPermission = false;
            // Result of window permission request, resultCode = RESULT_CANCELED
            if (Settings.canDrawOverlays(this)) {
                doShowFloatWindow();
            } else {
                Toast.makeText(this, getString(R.string.alert_window_permission_denied), Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    private void startCount() {
        if(chronometer != null) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        }
    }

    private void stopCount() {
        if(chronometer != null) {
            chronometer.stop();
        }
    }

    /**
     * ??????????????????
     */
    protected void releaseHandler() {
        handler.sendEmptyMessage(EaseMsgUtils.MSG_RELEASE_HANDLER);
    }

    @Override
    protected void onDestroy() {
        EMLog.d(TAG,"onDestroy");
        super.onDestroy();
        releaseHandler();
        if(timehandler != null){
            timehandler.stopTime();
        }
        if(headBitMap != null){
            headBitMap.recycle();
        }
        if(uIdMap != null){
            uIdMap.clear();
        }
        if(!isFloatWindowShowing()) {
            EaseCallKit.getInstance().releaseCall();

            leaveChannel();
            RtcEngine.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // ?????????????????????back???
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }else{
            // ????????????back???????????????
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFloatWindowShowing()) {
            exitChannelDisplay();
        }
    }


    /**
     * ?????????????????????????????????
     */
    public void exitChannelDisplay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EaseVideoCallActivity.this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(EaseVideoCallActivity.this, R.layout.activity_exit_channel, null);
        dialog.setView(dialogView);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER | Gravity.CENTER;
        dialog.show();

        final Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        final Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                EMLog.e(TAG, "exitChannelDisplay  exit channel:");
                stopCount();
                if(remoteUId == 0){
                    CallCancelEvent cancelEvent = new CallCancelEvent();
                    sendCmdMsg(cancelEvent,username);
                }else{
                    exitChannel();
                    if(listener != null){
                        //????????????????????????
                        long time = getChronometerSeconds(chronometer);
                        listener.onEndCallWithReason(callType,channelName, EaseCallEndReason.EaseCallEndReasonHangup,time *1000);
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                EMLog.e(TAG, "exitChannelDisplay not exit channel");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_IDLE){
            showFloatWindow();
        }
    }
}