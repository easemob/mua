package com.community.mua.callkit;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.community.mua.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.community.mua.callkit.base.EaseCallFloatWindow;
import com.community.mua.callkit.event.ConfirmRingEvent;
import com.community.mua.callkit.ui.EaseBaseCallActivity;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.EasyUtils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.community.mua.callkit.base.EaseCallKitConfig;
import com.community.mua.callkit.event.AlertEvent;
import com.community.mua.callkit.event.AnswerEvent;
import com.community.mua.callkit.event.BaseEvent;
import com.community.mua.callkit.event.CallCancelEvent;
import com.community.mua.callkit.event.ConfirmCallEvent;
import com.community.mua.callkit.base.EaseCallInfo;
import com.community.mua.callkit.event.InviteEvent;
import com.community.mua.callkit.livedatas.EaseLiveDataBus;
import com.community.mua.callkit.ui.EaseMultipleVideoActivity;
import com.community.mua.callkit.ui.EaseVideoCallActivity;
import com.community.mua.callkit.utils.EaseCallAction;
import com.community.mua.callkit.base.EaseCallKitListener;
import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.callkit.utils.EaseCallKitNotifier;
import com.community.mua.callkit.utils.EaseCallState;
import com.community.mua.callkit.utils.EaseMsgUtils;
import com.community.mua.callkit.utils.EaseCallKitUtils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.community.mua.callkit.utils.EaseCallKitUtils.isAppRunningForeground;
import static com.community.mua.callkit.utils.EaseMsgUtils.CALL_INVITE_EXT;


/**
 * The kit is a help class to help developers use CallKit, it provides methods to launch audio and video
 */
public class EaseCallKit {
    private static final String TAG = EaseCallKit.class.getSimpleName();
    private static EaseCallKit instance = null;
    private boolean callKitInit = false;
    private Context appContext = null;
    private EMMessageListener messageListener = null;
    private EaseCallType callType = EaseCallType.SINGLE_VIDEO_CALL;
    private EaseCallState callState = EaseCallState.CALL_IDLE;
    private String channelName;
    private String fromUserId; //?????????????????????
    public static String deviceId = "android_";
    public  String clallee_devId;
    private String callID = null;
    private JSONObject inviteExt = null;
    private EaseCallInfo callInfo = new EaseCallInfo();
    private TimeHandler timeHandler;
    private Map<String,EaseCallInfo> callInfoMap = new HashMap<>();
    private EaseCallKitListener callListener;
    private static boolean isComingCall = true;
    private ArrayList<String> inviteeUsers = new ArrayList<>();
    private EaseCallKitConfig  callKitConfig;
    private EaseCallKitNotifier notifier;
    private Class<? extends EaseBaseCallActivity> curCallCls;
    private Handler handler;
    /**
     * If use the default class, you should register it to AndroidManifest
     */
    private Class<? extends EaseVideoCallActivity> defaultVideoCallCls = EaseVideoCallActivity.class;

    /**
     * If use the default class, you should register it to AndroidManifest
     */
    private Class<? extends EaseMultipleVideoActivity> defaultMultiVideoCls = EaseMultipleVideoActivity.class;

    private EaseCallKit() {}

    public static EaseCallKit getInstance() {
        if(instance == null) {
            synchronized (EaseCallKit.class) {
                if(instance == null) {
                    instance = new EaseCallKit();
                }
            }
        }
        return instance;
    }

    /**
     * init ?????????
     * @param context
     * @return
     */
    public synchronized boolean init(Context context, EaseCallKitConfig config) {
        if(callKitInit) {
            return true;
        }
        removeMessageListener();
        appContext = context;
        if (!isMainProcess(appContext)) {
            Log.e(TAG, "enter the service process!");
            return false;
        }

        //?????????????????????
        deviceId += EaseCallKitUtils.getPhoneSign();
        timeHandler = new TimeHandler();

        //??????callkit?????????
        callKitConfig = new EaseCallKitConfig();
        callKitConfig.setAgoraAppId(config.getAgoraAppId());
        callKitConfig.setUserInfoMap(config.getUserInfoMap());
        callKitConfig.setDefaultHeadImage(config.getDefaultHeadImage());
        callKitConfig.setCallTimeOut(config.getCallTimeOut());
        callKitConfig.setRingFile(config.getRingFile());
        callKitConfig.setEnableRTCToken(config.isEnableRTCToken());

        //init notifier
        initNotifier();

        //????????????????????????
        addMessageListener();
        callKitInit = true;

        handler = new Handler(appContext.getMainLooper());
        return true;
    }

    /**
     * Register the activity which you want to display video call or audio call and you have registered in AndroidManifest.xml
     * @param videoCallClass
     */
    public void registerVideoCallClass(Class<? extends EaseVideoCallActivity> videoCallClass) {
        defaultVideoCallCls = videoCallClass;
    }

    /**
     * Register the activity which you want to display multiple video call and you have registered in AndroidManifest.xml
     * @param multipleVideoClass
     */
    public void registerMultipleVideoClass(Class<? extends EaseMultipleVideoActivity> multipleVideoClass) {
        defaultMultiVideoCls = multipleVideoClass;
    }

    /**
     * ????????????callKitConfig
     *
     */
     public EaseCallKitConfig getCallKitConfig(){
         return  callKitConfig;
     }


    private void initNotifier(){
        notifier = new EaseCallKitNotifier(appContext);
    }


    /**
     * ??????????????????
     *
    */
    public enum EaseCallError{
         PROCESS_ERROR, //??????????????????
         RTC_ERROR, //???????????????
         IM_ERROR  //IM??????
    }

    public enum CALL_PROCESS_ERROR {
        CALL_STATE_ERROR(0),
        CALL_TYPE_ERROR(1),
        CALL_PARAM_ERROR(2),
        CALL_RECEIVE_ERROR(3);

        public int code;
        CALL_PROCESS_ERROR(int code) {
            this.code = code;
        }
    }


    /**
     * ??????1v1??????
     * ??????????????????activity?????????????????????{@link #releaseCall()}???????????????????????????
     * @param type ????????????(?????????SINGLE_VOICE_CALL???SINGLE_VIDEO_CALL?????????
     * @param user ????????????ID(???????????????ID)
     * @param ext  ????????????(??????????????????)
     */
    public void startSingleCall(final EaseCallType type, final String user,final  Map<String, Object> ext){
        startSingleCall(type, user, ext, defaultVideoCallCls);
    }

    /**
     * ??????1v1??????
     * ??????????????????activity?????????????????????{@link #releaseCall()}???????????????????????????
     * @param type ????????????(?????????SINGLE_VOICE_CALL???SINGLE_VIDEO_CALL?????????
     * @param user ????????????ID(???????????????ID)
     * @param ext  ????????????(??????????????????)
     * @param cls  ?????????{@link EaseVideoCallActivity}???activity
     */
    public void startSingleCall(final EaseCallType type, final String user,final  Map<String, Object> ext, Class<? extends EaseVideoCallActivity> cls){
        if(callState != EaseCallState.CALL_IDLE){
            if(callListener != null){
                callListener.onCallError(EaseCallError.PROCESS_ERROR,CALL_PROCESS_ERROR.CALL_STATE_ERROR.code,"current state is busy");
            }
            return;
        }
        if(type == EaseCallType.CONFERENCE_CALL){
            if(callListener != null){
                callListener.onCallError(EaseCallError.PROCESS_ERROR,CALL_PROCESS_ERROR.CALL_TYPE_ERROR.code,"call type is error");
            }
            return;
        }
        if(user != null && user.length() == 0){
            if(callListener != null){
                callListener.onCallError(EaseCallError.PROCESS_ERROR,CALL_PROCESS_ERROR.CALL_PARAM_ERROR.code,"user is null");
            }
            return;
        }
        callType = type;
        //????????????????????????
        callState = EaseCallState.CALL_OUTGOING;
        fromUserId = user;
        if(ext != null){
            inviteExt = EaseCallKitUtils.convertMapToJSONObject(ext);
        }
        curCallCls = cls;
        //??????1V1??????
        Intent intent = new Intent(appContext, curCallCls).addFlags(FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        isComingCall = false;
        bundle.putBoolean("isComingCall", false);
        bundle.putString("username", user);
        channelName = EaseCallKitUtils.getRandomString(10);
        bundle.putString("channelName", channelName);
        intent.putExtras(bundle);
        appContext.startActivity(intent);
    }


    /**
     * ????????????????????????
     * ??????????????????activity?????????????????????{@link #releaseCall()}???????????????????????????
     * @param users ??????ID??????(??????ID??????)
     * @param ext  ????????????(??????????????????)
     */
    public void startInviteMultipleCall(final String[] users,final Map<String, Object> ext){
        startInviteMultipleCall(users, ext, defaultMultiVideoCls);
    }

    /**
     * ????????????????????????
     * ??????????????????activity?????????????????????{@link #releaseCall()}???????????????????????????
     * @param users ??????ID??????(??????ID??????)
     * @param ext  ????????????(??????????????????)
     * @param cls   ?????????{@link EaseMultipleVideoActivity}???activity
     */
    public void startInviteMultipleCall(final String[] users,final Map<String, Object> ext, Class<? extends EaseMultipleVideoActivity> cls){
        if(callState != EaseCallState.CALL_IDLE && callType != EaseCallType.CONFERENCE_CALL){
            if(callListener != null){
                callListener.onCallError(EaseCallError.PROCESS_ERROR,CALL_PROCESS_ERROR.CALL_STATE_ERROR.code,"current state is busy");
            }
            return;
        }
        if(users == null || users.length  == 0) {
            if(curCallCls != null){
                inviteeUsers.clear();
                Intent intent = new Intent(appContext, curCallCls)
                        .addFlags(FLAG_ACTIVITY_NEW_TASK);
                appContext.startActivity(intent);
            }else{
                if(callListener != null){
                    callListener.onCallError(EaseCallError.PROCESS_ERROR,CALL_PROCESS_ERROR.CALL_PARAM_ERROR.code,"users is null");
                }
            }
        }else{
            callType = EaseCallType.CONFERENCE_CALL;
            inviteeUsers.clear();
            for(String user:users){
                inviteeUsers.add(user);
            }
            //????????????????????? ????????????
            if(curCallCls == null){
                if(users != null && users.length > 0){
                    //????????????????????????
                    if(ext != null){
                        inviteExt = EaseCallKitUtils.convertMapToJSONObject(ext);
                    }
                    callState = EaseCallState.CALL_OUTGOING;
                    curCallCls = cls;
                    Intent intent = new Intent(appContext, curCallCls);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    isComingCall = false;
                    bundle.putBoolean("isComingCall", false);
                    channelName = EaseCallKitUtils.getRandomString(10);
                    bundle.putString("channelName", channelName);
                    intent.putExtras(bundle);
                    appContext.startActivity(intent);
                }
            }else{
                //??????????????????
                Intent intent = new Intent(appContext, curCallCls).addFlags(FLAG_ACTIVITY_NEW_TASK);
                appContext.startActivity(intent);
            }
        }
    }

    /**
     * The method is used for {@link com.community.mua.callkit.base.EaseCallFloatWindow}, other methods are not recommended
     * @return Current call activity's class, maybe is null.
     */
    public Class<? extends EaseBaseCallActivity> getCurrentCallClass() {
        return curCallCls;
    }

    /**
     * If you call {@link #startSingleCall(EaseCallType, String, Map)}, {@link #startSingleCall(EaseCallType, String, Map, Class)}
     * or {@link #startInviteMultipleCall(String[], Map)}, you should call the method of {@link #releaseCall()} when the {@link #curCallCls} is finishing.
     */
    public void releaseCall() {
        if(curCallCls != null) {
            curCallCls = null;
        }
    }


    /**
     * ??????????????????
     */
    private void addMessageListener() {
        this.messageListener = new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for(EMMessage message: messages){
                    String messageType = message.getStringAttribute(EaseMsgUtils.CALL_MSG_TYPE, "");
                    EMLog.d(TAG,"Receive msg:" + message.getMsgId() + " from:" + message.getFrom()+ "  messageType:"+ messageType);
                    //????????????????????????
                    if(TextUtils.equals(messageType, EaseMsgUtils.CALL_MSG_INFO)
                            && !TextUtils.equals(message.getFrom(), EMClient.getInstance().getCurrentUser())) {
                        String action = message.getStringAttribute(EaseMsgUtils.CALL_ACTION, "");
                        String callerDevId = message.getStringAttribute(EaseMsgUtils.CALL_DEVICE_ID, "");
                        String fromCallId = message.getStringAttribute(EaseMsgUtils.CLL_ID, "");
                        String fromUser = message.getFrom();
                        String channel = message.getStringAttribute(EaseMsgUtils.CALL_CHANNELNAME, "");
                        JSONObject ext = null;
                        try {
                            ext = message.getJSONObjectAttribute(CALL_INVITE_EXT);
                        } catch (HyphenateException exception) {
                            exception.printStackTrace();
                        }

                        if(action == null || callerDevId == null || fromCallId == null || fromUser ==null || channel == null){
                            if(callListener != null){
                                callListener.onCallError(EaseCallError.PROCESS_ERROR,CALL_PROCESS_ERROR.CALL_RECEIVE_ERROR.code,"receive message error");
                            }
                            continue;
                        }
                        EaseCallAction callAction = EaseCallAction.getfrom(action);
                        switch (callAction) {
                            case CALL_INVITE: //??????????????????
                                int calltype = message.getIntAttribute
                                        (EaseMsgUtils.CALL_TYPE, 0);
                                EaseCallType callkitType =
                                        EaseCallType.getfrom(calltype);
                                if (callState != EaseCallState.CALL_IDLE) {
                                    if(TextUtils.equals(fromCallId, callID) && TextUtils.equals(fromUser, fromUserId)
                                            && callkitType == EaseCallType.SINGLE_VOICE_CALL && callType == EaseCallType.SINGLE_VIDEO_CALL) {
                                        InviteEvent inviteEvent = new InviteEvent();
                                        inviteEvent.callId = fromCallId;
                                        inviteEvent.type = callkitType;

                                        //????????????
                                        EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString()).postValue(inviteEvent);
                                    } else {
                                        //??????????????????
                                        AnswerEvent callEvent = new AnswerEvent();
                                        callEvent.result = EaseMsgUtils.CALL_ANSWER_BUSY;
                                        callEvent.callerDevId = callerDevId;
                                        callEvent.callId = fromCallId;
                                        sendCmdMsg(callEvent, fromUser);
                                    }
                                } else {
                                    callInfo.setCallerDevId(callerDevId);
                                    callInfo.setCallId(fromCallId);
                                    callInfo.setCallKitType(callkitType);
                                    callInfo.setChannelName(channel);
                                    callInfo.setComming(true);
                                    callInfo.setFromUser(fromUser);
                                    callInfo.setExt(ext);

                                    //???????????????????????????
                                    callInfoMap.put(fromCallId, callInfo);

                                    //??????alert??????
                                    AlertEvent callEvent = new AlertEvent();
                                    callEvent.callerDevId = callerDevId;
                                    callEvent.callId = fromCallId;
                                    sendCmdMsg(callEvent, fromUser);

                                    //???????????????
                                    timeHandler.startTime();
                                }
                                break;
                            default:
                                break;
                        }

                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                for(EMMessage message: messages){
                    String messageType = message.getStringAttribute(EaseMsgUtils.CALL_MSG_TYPE, "");
                    EMLog.d(TAG,"Receive cmdmsg:" + message.getMsgId() + " from:" + message.getFrom()  + "  messageType:"+ messageType);
                    //????????????????????????
                    if(TextUtils.equals(messageType, EaseMsgUtils.CALL_MSG_INFO)
                            && !TextUtils.equals(message.getFrom(), EMClient.getInstance().getCurrentUser())) {
                        String action = message.getStringAttribute(EaseMsgUtils.CALL_ACTION, "");
                        String callerDevId = message.getStringAttribute(EaseMsgUtils.CALL_DEVICE_ID, "");
                        String fromCallId = message.getStringAttribute(EaseMsgUtils.CLL_ID, "");
                        String fromUser = message.getFrom();
                        String channel = message.getStringAttribute(EaseMsgUtils.CALL_CHANNELNAME, "");
                        EaseCallAction callAction = EaseCallAction.getfrom(action);
                        switch (callAction){
                            case CALL_CANCEL: //????????????
                                if(callState == EaseCallState.CALL_IDLE){
                                    timeHandler.stopTime();
                                    //????????????
                                    callInfoMap.remove(fromCallId);
                                }else{
                                    CallCancelEvent event = new CallCancelEvent();
                                    event.callerDevId = callerDevId;
                                    event.callId = fromCallId;
                                    event.userId = fromUser;
                                    if(TextUtils.equals(callID, fromCallId)) {
                                        callState = EaseCallState.CALL_IDLE;
                                    }
                                    notifier.reset();
                                    //????????????
                                    EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString()).postValue(event);
                                }
                                break;
                            case CALL_ALERT:
                                String calleedDeviceId = message.getStringAttribute(EaseMsgUtils.CALLED_DEVICE_ID, "");
                                AlertEvent alertEvent = new AlertEvent();
                                alertEvent.callId = fromCallId;
                                alertEvent.calleeDevId = calleedDeviceId;
                                alertEvent.userId = fromUser;
                                EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString()).postValue(alertEvent);
                                break;
                            case CALL_CONFIRM_RING: //??????callId ????????????
                                String calledDvId = message.getStringAttribute(EaseMsgUtils.CALLED_DEVICE_ID, "");
                                boolean vaild = message.getBooleanAttribute(EaseMsgUtils.CALL_STATUS, false);
                                //????????????????????????????????????DrviceId,
                                // ????????????????????????Id???CALL_CONFIRM_RING
                                if(TextUtils.equals(calledDvId, deviceId)) {
                                    timeHandler.stopTime();
                                    if(!vaild){
                                        //????????????
                                        callInfoMap.remove(fromCallId);
                                    }else{
                                        //??????callId ??????
                                        if(callState == EaseCallState.CALL_IDLE){
                                            callState = EaseCallState.CALL_ALERTING;
                                            //???????????????????????????
                                            clallee_devId = callerDevId;
                                            callID = fromCallId;
                                            EaseCallInfo info = callInfoMap.get(fromCallId);
                                            if(info != null){
                                                channelName = info.getChannelName();
                                                callType = info.getCallKitType();
                                                fromUserId = info.getFromUser();
                                                inviteExt =info.getExt();
                                            }
                                            //?????????????????????map????????????
                                            callInfoMap.clear();
                                            timeHandler.startSendEvent();
                                        }else{
                                            //????????????
                                            callInfoMap.remove(fromCallId);
                                            timeHandler.stopTime();
                                        }
                                    }
                                }

                                break;
                            case CALL_CONFIRM_CALLEE:  //??????????????????
                                String result = message.getStringAttribute(EaseMsgUtils.CALL_RESULT, "");
                                String calledDevId = message.getStringAttribute(EaseMsgUtils.CALLED_DEVICE_ID, "");
                                ConfirmCallEvent event = new ConfirmCallEvent();
                                event.calleeDevId = calledDevId;
                                event.result = result;
                                event.callerDevId = callerDevId;
                                event.callId = fromCallId;
                                event.userId = fromUser;
                                //????????????
                                EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString()).postValue(event);
                                break;
                            case CALL_ANSWER:  //???????????????????????????
                                String result1 = message.getStringAttribute(EaseMsgUtils.CALL_RESULT, "");
                                String calledDevId1 = message.getStringAttribute(EaseMsgUtils.CALLED_DEVICE_ID, "");
                                boolean transVoice = message.getBooleanAttribute(EaseMsgUtils.CALLED_TRANSE_VOICE, false);
                                //???????????????????????????????????????????????????
                                //????????????????????????
                                if(callType != EaseCallType.CONFERENCE_CALL){
                                    if(!isComingCall || TextUtils.equals(calledDevId1, deviceId)) {
                                        AnswerEvent answerEvent = new AnswerEvent();
                                        answerEvent.result = result1;
                                        answerEvent.calleeDevId = calledDevId1;
                                        answerEvent.callerDevId = callerDevId;
                                        answerEvent.callId = fromCallId;
                                        answerEvent.userId = fromUser;
                                        answerEvent.transVoice = transVoice;

                                        //????????????
                                        EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString()).postValue(answerEvent);
                                    }
                                }else{
                                        if(!TextUtils.equals(fromUser, EMClient.getInstance().getCurrentUser())) {
                                            AnswerEvent answerEvent = new AnswerEvent();
                                            answerEvent.result = result1;
                                            answerEvent.calleeDevId = calledDevId1;
                                            answerEvent.callerDevId = callerDevId;
                                            answerEvent.callId = fromCallId;
                                            answerEvent.userId = fromUser;
                                            answerEvent.transVoice = transVoice;

                                            //????????????
                                            EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString()).postValue(answerEvent);

                                        }
                                }
                                break;
                            case CALL_VIDEO_TO_VOICE:
                                if (callState != EaseCallState.CALL_IDLE) {
                                    if(TextUtils.equals(fromCallId, callID)
                                            && TextUtils.equals(fromUser, fromUserId)) {
                                        InviteEvent inviteEvent = new InviteEvent();
                                        inviteEvent.callId = fromCallId;
                                        inviteEvent.type = EaseCallType.SINGLE_VOICE_CALL;
                                        //????????????
                                        EaseLiveDataBus.get().with(EaseCallType.SINGLE_VIDEO_CALL.toString()).postValue(inviteEvent);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };
        //??????????????????
        EMClient.getInstance().chatManager().addMessageListener(this.messageListener);

    }

    /***
     * ??????call kit??????
     * @param listener
     * @return
     */
    public void setCallKitListener(EaseCallKitListener listener){
        this.callListener = listener;
    }


    /***
     * ??????call kit??????
     * @param listener
     * @return
     */
    public void removeCallKitListener(EaseCallKitListener listener){
        this.callListener = null;
    }



    /**
     * ??????????????????
     */
    private void removeMessageListener() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        messageListener = null;
    }

    public EaseCallState getCallState() {
        return callState;
    }

    public EaseCallType getCallType() {
        return callType;
    }

    public void setCallType(EaseCallType callType) {
        this.callType = callType;
    }

    public void setCallState(EaseCallState callState) {
        this.callState = callState;
    }

    public String getCallID() { return callID; }

    public void setCallID(String callID) { this.callID = callID; }

    public String  getClallee_devId() {
        return clallee_devId;
    }

    public EaseCallKitListener getCallListener() {
        return callListener;
    }

    public String getChannelName(){
        return channelName;
    }

    public String getFromUserId(){
        return fromUserId;
    }

    public boolean getIsComingCall(){
        return isComingCall;
    }

    public EaseCallKitNotifier getNotifier(){
        return notifier;
    }

    private boolean isMainProcess(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return TextUtils.equals(context.getApplicationInfo().packageName, appProcess.processName);
            }
        }
        return false;
    }


    /**
     * ??????CMD????????????
     * @param username
     */
    private void sendCmdMsg(BaseEvent event,String username){
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setTo(username);
        String action="rtcCall";
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        message.addBody(cmdBody);

        message.setAttribute(EaseMsgUtils.CALL_ACTION, event.callAction.state);
        message.setAttribute(EaseMsgUtils.CALL_DEVICE_ID, event.callerDevId);
        message.setAttribute(EaseMsgUtils.CLL_ID,event.callId);
        message.setAttribute(EaseMsgUtils.CLL_TIMESTRAMEP, System.currentTimeMillis());
        message.setAttribute(EaseMsgUtils.CALL_MSG_TYPE, EaseMsgUtils.CALL_MSG_INFO);
        if(event.callAction == EaseCallAction.CALL_ANSWER){
            message.setAttribute(EaseMsgUtils.CALLED_DEVICE_ID, deviceId);
            message.setAttribute(EaseMsgUtils.CALL_RESULT,((AnswerEvent)event).result);
        }else if(event.callAction == EaseCallAction.CALL_ALERT){
            message.setAttribute(EaseMsgUtils.CALLED_DEVICE_ID, deviceId);
        }
        final EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username, EMConversation.EMConversationType.Chat, true);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                EMLog.d(TAG, "Invite call success");
                conversation.removeMessage(message.getMsgId());
            }

            @Override
            public void onError(int code, String error) {
                EMLog.e(TAG, "Invite call error " + code + ", " + error);
                conversation.removeMessage(message.getMsgId());
                if(callListener != null){
                    callListener.onCallError(EaseCallError.IM_ERROR,code,error);
                }
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }


    private class TimeHandler extends Handler {
        private final int MSG_TIMER = 0;
        private final int MSG_START_ACTIVITY = 1;
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

        public void startSendEvent() {
           sendEmptyMessage(MSG_START_ACTIVITY);
        }

        public void stopTime() {
            removeMessages(MSG_START_ACTIVITY);
            removeMessages(MSG_TIMER);
        }


        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TIMER) {
                // TODO: update calling time.
                timePassed++;
                String time = dateFormat.format(timePassed * 1000);
                if(timePassed *1000 == EaseMsgUtils.CALL_INVITED_INTERVAL){

                    //????????????
                    timeHandler.stopTime();
                    callState = EaseCallState.CALL_IDLE;
                }
                sendEmptyMessageDelayed(MSG_TIMER, 1000);
            }else if(msg.what == MSG_START_ACTIVITY){
                timeHandler.stopTime();
                String info = "";
                String userName = EaseCallKitUtils.getUserNick(fromUserId);
                if(callType != EaseCallType.CONFERENCE_CALL){

                    //??????activity
                    curCallCls = defaultVideoCallCls;
                    Intent intent = new Intent(appContext, curCallCls).addFlags(FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    isComingCall = true;
                    bundle.putBoolean("isComingCall", true);
                    bundle.putString("channelName", channelName);
                    bundle.putString("username", fromUserId);
                    intent.putExtras(bundle);
                    appContext.startActivity(intent);
                    if(Build.VERSION.SDK_INT >= 29 && !EasyUtils.isAppRunningForeground(appContext)) {
                        EMLog.e(TAG,"notifier.notify:" + info);
                        if(callType == EaseCallType.SINGLE_VIDEO_CALL){
                            info = appContext.getString(R.string.alert_request_video, userName);
                        }else{
                            info = appContext.getString(R.string.alert_request_voice, userName);
                        }
                        notifier.notify(intent, appContext.getString(R.string.app_name), info);
                    }
                }else {
                    //????????????????????????
                    curCallCls = defaultMultiVideoCls;
                    Intent intent = new Intent(appContext, curCallCls).addFlags(FLAG_ACTIVITY_NEW_TASK);
                    Bundle bundle = new Bundle();
                    isComingCall = true;
                    bundle.putBoolean("isComingCall", true);
                    bundle.putString("channelName", channelName);
                    bundle.putString("username", fromUserId);
                    intent.putExtras(bundle);
                    appContext.startActivity(intent);
                    if (Build.VERSION.SDK_INT >= 29 && isAppRunningForeground(appContext)) {
                        info = appContext.getString(R.string.alert_request_multiple_video, userName);
                        notifier.notify(intent, appContext.getString(R.string.easemob), info);
                    }
                }

                //??????????????????
                if(callListener != null){
                    callListener.onReceivedCall(callType,fromUserId,inviteExt);
                }
            }
            super.handleMessage(msg);
        }
    }

    /**
     * ??????Activity??????Destroy
     * @param mActivity
     * @return true:?????????
     */
    private boolean isDestroy(Activity mActivity) {
        if (mActivity == null ||
                mActivity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getInviteeUsers() {
        return inviteeUsers;
    }

    public void InitInviteeUsers() {
         inviteeUsers.clear();
    }

    public JSONObject getInviteExt() {
        return inviteExt;
    }

    public Context getAppContext() {
        return appContext;
    }

    public void sendCmdMsg(BaseEvent event,String username, EMCallBack callBack){
        final EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action="rtcCall";
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        message.setTo(username);
        message.addBody(cmdBody);
        if(event.callAction.equals(EaseCallAction.CALL_VIDEO_TO_VOICE) ||
                event.callAction.equals(EaseCallAction.CALL_CANCEL)){
            cmdBody.deliverOnlineOnly(false);
        }else{
            cmdBody.deliverOnlineOnly(true);
        }

        message.setAttribute(EaseMsgUtils.CALL_ACTION, event.callAction.state);
        message.setAttribute(EaseMsgUtils.CALL_DEVICE_ID, EaseCallKit.deviceId);
        message.setAttribute(EaseMsgUtils.CLL_ID, event.callId);
        message.setAttribute(EaseMsgUtils.CLL_TIMESTRAMEP, System.currentTimeMillis());
        message.setAttribute(EaseMsgUtils.CALL_MSG_TYPE, EaseMsgUtils.CALL_MSG_INFO);
        if(event.callAction == EaseCallAction.CALL_CONFIRM_RING){
            message.setAttribute(EaseMsgUtils.CALL_STATUS, ((ConfirmRingEvent)event).valid);
            message.setAttribute(EaseMsgUtils.CALLED_DEVICE_ID, ((ConfirmRingEvent)event).calleeDevId);
        }else if(event.callAction == EaseCallAction.CALL_CONFIRM_CALLEE){
            message.setAttribute(EaseMsgUtils.CALL_RESULT, ((ConfirmCallEvent)event).result);
            message.setAttribute(EaseMsgUtils.CALLED_DEVICE_ID, ((ConfirmCallEvent)event).calleeDevId);
        }else if(event.callAction == EaseCallAction.CALL_ANSWER){
            message.setAttribute(EaseMsgUtils.CALL_RESULT, ((AnswerEvent)event).result);
            message.setAttribute(EaseMsgUtils.CALLED_DEVICE_ID, ((AnswerEvent) event).calleeDevId);
            message.setAttribute(EaseMsgUtils.CALL_DEVICE_ID, ((AnswerEvent) event).callerDevId);
            message.setAttribute(EaseMsgUtils.CALLED_TRANSE_VOICE, ((AnswerEvent) event).transVoice);
        }
        final EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username, EMConversation.EMConversationType.Chat, true);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                EMLog.d(TAG, "Invite call success");
                conversation.removeMessage(message.getMsgId());
                if(event.callAction == EaseCallAction.CALL_CANCEL){
                    exitCall();
                }else if(event.callAction == EaseCallAction.CALL_CONFIRM_CALLEE){
                    //?????????????????? ????????????
                    if(!TextUtils.equals(((ConfirmCallEvent)event).result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                        exitCall();
                    }
                }
                callBack.onSuccess();
            }

            @Override
            public void onError(int code, String error) {
                EMLog.e(TAG, "Invite call error " + code + ", " + error);
                if(conversation != null){
                    conversation.removeMessage(message.getMsgId());
                }
                if(event.callAction == EaseCallAction.CALL_CANCEL){
                    //????????????
                    exitCall();
                }else if(event.callAction == EaseCallAction.CALL_CONFIRM_CALLEE){
                    //?????????????????? ????????????
                    if(!TextUtils.equals(((ConfirmCallEvent)event).result, EaseMsgUtils.CALL_ANSWER_ACCEPT)) {
                        exitCall();
                    }
                }
                callBack.onError(code, error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    private void exitCall(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(EaseCallFloatWindow.getInstance().isShowing()){
                    EaseCallFloatWindow.getInstance(appContext).dismiss();
                }
                //????????????
                setCallState(EaseCallState.CALL_IDLE);
                setCallID(null);
            }
        });
    }
}
