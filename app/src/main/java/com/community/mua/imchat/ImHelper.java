package com.community.mua.imchat;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.community.mua.R;
import com.community.mua.callkit.EaseCallKit;
import com.community.mua.callkit.base.EaseCallEndReason;
import com.community.mua.callkit.base.EaseCallKitConfig;
import com.community.mua.callkit.base.EaseCallKitListener;
import com.community.mua.callkit.base.EaseCallKitTokenCallback;
import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.callkit.base.EaseCallUserInfo;
import com.community.mua.callkit.base.EaseGetUserAccountCallback;
import com.community.mua.callkit.base.EaseUserAccount;
import com.community.mua.imchat.av.ChatVideoCallAdapterDelegate;
import com.community.mua.imchat.av.ChatVoiceCallAdapterDelegate;
import com.community.mua.imchat.recall.ChatRecallAdapterDelegate;
import com.community.mua.imkit.EaseIM;
import com.community.mua.imkit.delegate.EaseCustomAdapterDelegate;
import com.community.mua.imkit.delegate.EaseExpressionAdapterDelegate;
import com.community.mua.imkit.delegate.EaseFileAdapterDelegate;
import com.community.mua.imkit.delegate.EaseImageAdapterDelegate;
import com.community.mua.imkit.delegate.EaseLocationAdapterDelegate;
import com.community.mua.imkit.delegate.EaseTextAdapterDelegate;
import com.community.mua.imkit.delegate.EaseVideoAdapterDelegate;
import com.community.mua.imkit.delegate.EaseVoiceAdapterDelegate;
import com.community.mua.imkit.domain.EaseAvatarOptions;
import com.community.mua.imkit.domain.EaseEmojicon;
import com.community.mua.imkit.domain.EaseEmojiconGroupEntity;
import com.community.mua.imkit.domain.EaseUser;
import com.community.mua.imkit.manager.EaseMessageTypeSetManager;
import com.community.mua.imkit.model.EaseEvent;
import com.community.mua.imkit.provider.EaseEmojiconInfoProvider;
import com.community.mua.imkit.provider.EaseSettingsProvider;
import com.community.mua.imkit.provider.EaseUserProfileProvider;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.ui.call.VideoCallActivity;
import com.community.mua.utils.SharedPreferUtil;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.cloud.EMHttpClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.push.EMPushConfig;
import com.hyphenate.util.EMLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ImHelper {
    private static ImHelper mInstance;
    private EaseCallKitListener callKitListener;
    private Context mainContext;
    private String tokenUrl = "http://a1.easemob.com/token/rtcToken/v1";
    private String uIdUrl = "http://a1.easemob.com/channel/mapper";

    private ImHelper() {
    }

    public boolean isSDKInit;//SDK是否初始化

    public static ImHelper getInstance() {
        if (mInstance == null) {
            synchronized (ImHelper.class) {
                if (mInstance == null) {
                    mInstance = new ImHelper();
                }
            }
        }
        return mInstance;
    }

    /**
     * 判断是否之前登录过
     * @return
     */
    public boolean isLoggedIn() {
        return getEMClient().isLoggedInBefore();
    }

    /**
     * 获取IM SDK的入口类
     * @return
     */
    public EMClient getEMClient() {
        return EMClient.getInstance();
    }

    public void init(Context context) {
//        demoModel = new DemoModel(context);
        //初始化IM SDK
        if (initSDK(context)) {
            // debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            // set Call options
//            setCallOptions(context);
            //初始化推送
//            initPush(context);
            //注册call Receiver
            //initReceiver(context);
            //初始化ease ui相关
            initEaseUI(context);
            //注册对话类型
            registerConversationType();

            //callKit初始化
            InitCallKit(context);

            //启动获取用户信息线程
//            fetchUserInfoList = FetchUserInfoList.getInstance();
//            fetchUserRunnable = new FetchUserRunnable();
//            fetchUserTread = new Thread(fetchUserRunnable);
//            fetchUserTread.start();
        }

    }

    /**
     * 初始化SDK
     * @param context
     * @return
     */
    private boolean initSDK(Context context) {
        // 根据项目需求对SDK进行配置
        EMOptions options = initChatOptions(context);
        // 初始化SDK
        isSDKInit = EaseIM.getInstance().init(context, options);
//        //设置删除用户属性数据超时时间
//        demoModel.setUserInfoTimeOut(30 * 60 * 1000);
//        //更新过期用户属性列表
//        updateTimeoutUsers();
        mainContext = context;
        return isSDKInit;
    }

    /**
     * callKit初始化
     *
     * @param context
     */
    private void InitCallKit(Context context){
        EaseCallKitConfig callKitConfig = new EaseCallKitConfig();
        //设置呼叫超时时间
        callKitConfig.setCallTimeOut(30 * 1000);
        //设置声网AgoraAppId
        callKitConfig.setAgoraAppId("声网AgoraAppId");
        callKitConfig.setEnableRTCToken(true);
        EaseCallKit.getInstance().init(context,callKitConfig);
        // Register the activities which you have registered in manifest
        EaseCallKit.getInstance().registerVideoCallClass(VideoCallActivity.class);
//        EaseCallKit.getInstance().registerMultipleVideoClass(MultipleVideoActivity.class);
        addCallkitListener();
    }
    /**
     * 增加EaseCallkit监听
     *
     */
    public void addCallkitListener(){
        callKitListener = new EaseCallKitListener() {
            @Override
            public void onInviteUsers(Context context, String userId[], JSONObject ext) {

            }

            @Override
            public void onEndCallWithReason(EaseCallType callType, String channelName, EaseCallEndReason reason, long callTime) {
//                EMLog.d(TAG,"onEndCallWithReason" + (callType != null ? callType.name() : " callType is null ") + " reason:" + reason + " time:"+ callTime);
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String callString = mainContext.getString(R.string.call_duration);
                callString += formatter.format(callTime);

                Toast.makeText(mainContext,callString,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGenerateToken(String userId, String channelName, String appKey, EaseCallKitTokenCallback callback){
                EMLog.d("wyz","onGenerateToken userId:" + userId + " channelName:" + channelName + " appKey:"+ appKey);
                String url = tokenUrl;
                url += "?";
                url += "userAccount=";
                url += userId;
                url += "&channelName=";
                url += channelName;
                url += "&appkey=";
                url +=  appKey;

                //获取声网Token
                getRtcToken(url, callback);
            }

            @Override
            public void onReceivedCall(EaseCallType callType, String fromUserId,JSONObject ext) {
                //收到接听电话
//                EMLog.d(TAG,"onRecivedCall" + callType.name() + " fromUserId:" + fromUserId);
            }
            @Override
            public  void onCallError(EaseCallKit.EaseCallError type, int errorCode, String description){

            }

            @Override
            public void onInViteCallMessageSent(){
//                LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
            }

            @Override
            public void onRemoteUserJoinChannel(String channelName, String userName, int uid, EaseGetUserAccountCallback callback){
                if(userName == null || userName == ""){
                    String url = uIdUrl;
                    url += "?";
                    url += "channelName=";
                    url += channelName;
                    url += "&userAccount=";
                    url += EMClient.getInstance().getCurrentUser();
                    url += "&appkey=";
                    url +=  EMClient.getInstance().getOptions().getAppKey();
                    getUserIdAgoraUid(uid,url,callback);
                }else{
                    //设置用户昵称 头像
                    setEaseCallKitUserInfo(userName);
                    EaseUserAccount account = new EaseUserAccount(uid,userName);
                    List<EaseUserAccount> accounts = new ArrayList<>();
                    accounts.add(account);
                    callback.onUserAccount(accounts);
                }
            }
        };
        EaseCallKit.getInstance().setCallKitListener(callKitListener);
    }
    /**
     * 设置callKit 用户头像昵称
     * @param userName
     */
    private void setEaseCallKitUserInfo(String userName){
//        EaseUser user = getUserInfo(userName);
//        EaseCallUserInfo userInfo = new EaseCallUserInfo();
//        if(user != null){
//            userInfo.setNickName(user.getNickname());
//            userInfo.setHeadImage(user.getAvatar());
//        }
//        EaseCallKit.getInstance().getCallKitConfig().setUserInfo(userName,userInfo);
    }

    /**
     * 获取声网Token
     *
     */
    private void getRtcToken(String tokenUrl,EaseCallKitTokenCallback callback){
        new AsyncTask<String, Void, Pair<Integer, String>>(){
            @Override
            protected Pair<Integer, String> doInBackground(String... str) {
                try {
                    Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(tokenUrl, null,EMHttpClient.GET);
                    return response;
                }catch (HyphenateException exception) {
                    exception.printStackTrace();
                }
                return  null;
            }
            @Override
            protected void onPostExecute(Pair<Integer, String> response) {
                if(response != null) {
                    try {
                        int resCode = response.first;
                        if(resCode == 200){
                            String responseInfo = response.second;
                            if(responseInfo != null && responseInfo.length() > 0){
                                try {
                                    JSONObject object = new JSONObject(responseInfo);
                                    String token = object.getString("accessToken");
                                    int uId = object.getInt("agoraUserId");

                                    //设置自己头像昵称
                                    setEaseCallKitUserInfo(EMClient.getInstance().getCurrentUser());
                                    callback.onSetToken(token,uId);
                                }catch (Exception e){
                                    e.getStackTrace();
                                }
                            }else{
                                callback.onGetTokenError(response.first,response.second);
                            }
                        }else{
                            callback.onGetTokenError(response.first,response.second);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    callback.onSetToken(null,0);
                }
            }
        }.execute(tokenUrl);
    }

    /**
     * 根据channelName和声网uId获取频道内所有人的UserId
     * @param uId
     * @param url
     * @param callback
     */
    private void getUserIdAgoraUid(int uId, String url, EaseGetUserAccountCallback callback){
        new AsyncTask<String, Void, Pair<Integer, String>>(){
            @Override
            protected Pair<Integer, String> doInBackground(String... str) {
                try {
                    Pair<Integer, String> response = EMHttpClient.getInstance().sendRequestWithToken(url, null,EMHttpClient.GET);
                    return response;
                }catch (HyphenateException exception) {
                    exception.printStackTrace();
                }
                return  null;
            }
            @Override
            protected void onPostExecute(Pair<Integer, String> response) {
                if(response != null) {
                    try {
                        int resCode = response.first;
                        if(resCode == 200){
                            String responseInfo = response.second;
                            List<EaseUserAccount> userAccounts = new ArrayList<>();
                            if(responseInfo != null && responseInfo.length() > 0){
                                try {
                                    JSONObject object = new JSONObject(responseInfo);
                                    JSONObject resToken = object.getJSONObject("result");
                                    Iterator it = resToken.keys();
                                    while(it.hasNext()) {
                                        String uIdStr = it.next().toString();
                                        int uid = 0;
                                        uid = Integer.valueOf(uIdStr).intValue();
                                        String username = resToken.optString(uIdStr);
                                        if(uid == uId){
                                            //获取到当前用户的userName 设置头像昵称等信息
                                            setEaseCallKitUserInfo(username);
                                        }
                                        userAccounts.add(new EaseUserAccount(uid, username));
                                    }
                                    callback.onUserAccount(userAccounts);
                                }catch (Exception e){
                                    e.getStackTrace();
                                }
                            }else{
                                callback.onSetUserAccountError(response.first,response.second);
                            }
                        }else{
                            callback.onSetUserAccountError(response.first,response.second);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    callback.onSetUserAccountError(100,"response is null");
                }
            }
        }.execute(url);
    }

    /**
     *注册对话类型
     */
    private void registerConversationType() {
        EaseMessageTypeSetManager.getInstance()
                .addMessageType(EaseExpressionAdapterDelegate.class)       //自定义表情
                .addMessageType(EaseFileAdapterDelegate.class)             //文件
                .addMessageType(EaseImageAdapterDelegate.class)            //图片
                .addMessageType(EaseLocationAdapterDelegate.class)         //定位
                .addMessageType(EaseVideoAdapterDelegate.class)            //视频
                .addMessageType(EaseVoiceAdapterDelegate.class)            //声音
//                .addMessageType(ChatConferenceInviteAdapterDelegate.class) //语音邀请
                .addMessageType(ChatRecallAdapterDelegate.class)           //消息撤回
                .addMessageType(ChatVideoCallAdapterDelegate.class)        //视频通话
                .addMessageType(ChatVoiceCallAdapterDelegate.class)        //语音通话
//                .addMessageType(ChatUserCardAdapterDelegate.class)         //名片消息
                .addMessageType(EaseCustomAdapterDelegate.class)           //自定义消息
//                .addMessageType(ChatNotificationAdapterDelegate.class)     //入群等通知消息
                .setDefaultMessageType(EaseTextAdapterDelegate.class);       //文本
    }

    /**
     * ChatPresenter中添加了网络连接状态监听，多端登录监听，群组监听，联系人监听，聊天室监听
     * @param context
     */
    private void initEaseUI(Context context) {
        //添加ChatPresenter,ChatPresenter中添加了网络连接状态监听，
        EaseIM.getInstance().addChatPresenter(ChatPresenter.getInstance());
        EaseIM.getInstance()
                .setSettingsProvider(new EaseSettingsProvider() {
                    @Override
                    public boolean isMsgNotifyAllowed(EMMessage message) {
                        return true;
                    }

                    @Override
                    public boolean isMsgSoundAllowed(EMMessage message) {
                        return SharedPreferUtil.getInstance().getNotifySoundEnabled();
                    }

                    @Override
                    public boolean isMsgVibrateAllowed(EMMessage message) {
                        return SharedPreferUtil.getInstance().getNotifyVibrateEnabled();
                    }

                    @Override
                    public boolean isSpeakerOpened() {
//                        return demoModel.getSettingMsgSpeaker();
                        return false;
                    }
                })
                .setEmojiconInfoProvider(new EaseEmojiconInfoProvider() {
                    @Override
                    public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
//                        EaseEmojiconGroupEntity data = EmojiconExampleGroupData.getData();
//                        for(EaseEmojicon emojicon : data.getEmojiconList()){
//                            if(emojicon.getIdentityCode().equals(emojiconIdentityCode)){
//                                return emojicon;
//                            }
//                        }
                        return null;
                    }

                    @Override
                    public Map<String, Object> getTextEmojiconMapping() {
                        return null;
                    }
                })
                .setAvatarOptions(getAvatarOptions())
                /*.setUserProvider(new EaseUserProfileProvider() {
                    @Override
                    public EaseUser getUser(String username) {
                        return getUserInfo(username);
                    }

                })*/;
    }

    /**
     * 统一配置头像
     * @return
     */
    private EaseAvatarOptions getAvatarOptions() {
        EaseAvatarOptions avatarOptions = new EaseAvatarOptions();
        avatarOptions.setAvatarShape(1);
        return avatarOptions;
    }

    /**
     * 根据自己的需要进行配置
     * @param context
     * @return
     */
    private EMOptions initChatOptions(Context context) {
        EMOptions options = new EMOptions();
        // 设置是否自动接受加好友邀请,默认是true
        options.setAcceptInvitationAlways(true);
        // 设置是否需要接受方已读确认
        options.setRequireAck(true);
        // 设置是否需要接受方送达确认,默认false
        options.setRequireDeliveryAck(false);
        //设置fpa开关，默认false
        options.setFpaEnable(true);

//        /**
//         * NOTE:你需要设置自己申请的账号来使用三方推送功能，详见集成文档
//         */
//        EMPushConfig.Builder builder = new EMPushConfig.Builder(context);
//
//        builder.enableVivoPush() // 需要在AndroidManifest.xml中配置appId和appKey
//                .enableMeiZuPush("134952", "f00e7e8499a549e09731a60a4da399e3")
//                .enableMiPush("2882303761517426801", "5381742660801")
//                .enableOppoPush("0bb597c5e9234f3ab9f821adbeceecdb",
//                        "cd93056d03e1418eaa6c3faf10fd7537")
//                .enableHWPush() // 需要在AndroidManifest.xml中配置appId
//                .enableFCM("782795210914");
//        options.setPushConfig(builder.build());

        //set custom servers, commonly used in private deployment
//        if(demoModel.isCustomSetEnable()) {
//            if(demoModel.isCustomServerEnable() && demoModel.getRestServer() != null && demoModel.getIMServer() != null) {
//                // 设置rest server地址
//                options.setRestServer(demoModel.getRestServer());
//                // 设置im server地址
//                options.setIMServer(demoModel.getIMServer());
//                //如果im server地址中包含端口号
//                if(demoModel.getIMServer().contains(":")) {
//                    options.setIMServer(demoModel.getIMServer().split(":")[0]);
//                    // 设置im server 端口号，默认443
//                    options.setImPort(Integer.valueOf(demoModel.getIMServer().split(":")[1]));
//                }else {
//                    //如果不包含端口号
//                    if(demoModel.getIMServerPort() != 0) {
//                        options.setImPort(demoModel.getIMServerPort());
//                    }
//                }
//            }
//        }
//        if (demoModel.isCustomAppkeyEnabled() && !TextUtils.isEmpty(demoModel.getCutomAppkey())) {
//            // 设置appkey
//            options.setAppKey(demoModel.getCutomAppkey());
//        }
//
//        String imServer = options.getImServer();
//        String restServer = options.getRestServer();
//
//        // 设置是否允许聊天室owner离开并删除会话记录，意味着owner再不会受到任何消息
//        options.allowChatroomOwnerLeave(demoModel.isChatroomOwnerLeaveAllowed());
//        // 设置退出(主动和被动退出)群组时是否删除聊天消息
//        options.setDeleteMessagesAsExitGroup(demoModel.isDeleteMessagesAsExitGroup());
//        // 设置是否自动接受加群邀请
//        options.setAutoAcceptGroupInvitation(demoModel.isAutoAcceptGroupInvitation());
//        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载
//        options.setAutoTransferMessageAttachments(demoModel.isSetTransferFileByUser());
//        // 是否自动下载缩略图，默认是true为自动下载
//        options.setAutoDownloadThumbnail(demoModel.isSetAutodownloadThumbnail());
        return options;
    }
}
