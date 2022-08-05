package com.community.mua.imchat;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.AppManager;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.imkit.constants.EaseConstant;
import com.community.mua.imkit.manager.EaseAtMessageHelper;
import com.community.mua.imkit.manager.EaseChatPresenter;
import com.community.mua.imkit.manager.EaseSystemMsgManager;
import com.community.mua.imkit.model.EaseEvent;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.ui.PairActivity;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.EMPresenceListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMPresence;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 主要用于chat过程中的全局监听，并对相应的事件进行处理
 * {@link #init()}方法建议在登录成功以后进行调用
 */
public class ChatPresenter extends EaseChatPresenter {
    private static final String TAG = ChatPresenter.class.getSimpleName();
    private static final int HANDLER_SHOW_TOAST = 0;
    private static ChatPresenter instance;
    private LiveDataBus messageChangeLiveData;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;
    private boolean isPushConfigsWithServer = false;
    private Context appContext;
    protected Handler handler;

    Queue<String> msgQueue = new ConcurrentLinkedQueue<>();

    private ChatPresenter() {
        appContext = App.getContext();
        initHandler(appContext.getMainLooper());
        messageChangeLiveData = LiveDataBus.get();
        //添加网络连接状态监听
        ImHelper.getInstance().getEMClient().addConnectionListener(new ChatConnectionListener());
        //添加多端登录监听
//        ImHelper.getInstance().getEMClient().addMultiDeviceListener(new ChatMultiDeviceListener());
        //添加群组监听
//        ImHelper.getInstance().getEMClient().groupManager().addGroupChangeListener(new ChatGroupListener());
        //添加联系人监听
//        ImHelper.getInstance().getEMClient().contactManager().setContactListener(new ChatContactListener());
        //添加聊天室监听
//        ImHelper.getInstance().getEMClient().chatroomManager().addChatRoomChangeListener(new ChatRoomListener());
        //添加对会话的监听（监听已读回执）
        ImHelper.getInstance().getEMClient().chatManager().addConversationListener(new ChatConversationListener());

        ImHelper.getInstance().getEMClient().presenceManager().addListener(new MyPresenceListener());
    }

    public static ChatPresenter getInstance() {
        if (instance == null) {
            synchronized (ChatPresenter.class) {
                if (instance == null) {
                    instance = new ChatPresenter();
                }
            }
        }
        return instance;
    }

    /**
     * 将需要登录成功进入MainActivity中初始化的逻辑，放到此处进行处理
     */
    public void init() {

    }

    public void initHandler(Looper looper) {
        handler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                Object obj = msg.obj;
                switch (msg.what) {
                    case HANDLER_SHOW_TOAST:
                        if (obj instanceof String) {
                            String str = (String) obj;
                            //ToastUtils.showToast(str);
                            Toast.makeText(appContext, str, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        while (!msgQueue.isEmpty()) {
            showToast(msgQueue.remove());
        }
    }

    void showToast(@StringRes int mesId) {
        showToast(context.getString(mesId));
    }

    void showToast(final String message) {
        Log.d(TAG, "receive invitation to join the group：" + message);
        if (handler != null) {
            Message msg = Message.obtain(handler, HANDLER_SHOW_TOAST, message);
            handler.sendMessage(msg);
        } else {
            msgQueue.add(message);
        }
    }

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        super.onMessageReceived(messages);
        LiveDataBus.get().with(Constants.RECEIVE_MSG, LiveEvent.class).postValue(new LiveEvent());

//        EaseEvent event = EaseEvent.create(EaseConstant.MESSAGE_CHANGE_RECEIVE, EaseEvent.TYPE.MESSAGE);
//        messageChangeLiveData.with(EaseConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
        for (EMMessage message : messages) {
            EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
            EMLog.d(TAG, "onMessageReceived: " + message.getType());
            // 如果设置群组离线消息免打扰，则不进行消息通知
//            List<String> disabledIds = DemoHelper.getInstance().getPushManager().getNoPushGroups();
//            if(disabledIds != null && disabledIds.contains(message.conversationId())) {
//                return;
//            }
            // in background, do not refresh UI, notify it in notification bar
            if(!App.getInstance().getLifecycleCallbacks().isFront()){
                getNotifier().notify(message);
            }
            //notify new message
            getNotifier().vibrateAndPlayTone(message);
        }
    }


    /**
     * 判断是否已经启动了MainActivity
     * @return
     */
//    private synchronized boolean isAppLaunchMain() {
//        List<Activity> activities = DemoApplication.getInstance().getLifecycleCallbacks().getActivityList();
//        if(activities != null && !activities.isEmpty()) {
//            for(int i = activities.size() - 1; i >= 0 ; i--) {
//                if(activities.get(i) instanceof MainActivity) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
        EaseEvent event = EaseEvent.create(EaseConstant.MESSAGE_CHANGE_CMD_RECEIVE, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.with(EaseConstant.MESSAGE_CHANGE_CHANGE).postValue(event);

        for (EMMessage message : messages) {
            String action = ((EMCmdMessageBody) message.getBody()).action();
            Map<String, Object> ext = message.ext();
            if (TextUtils.equals(action, Constants.PAIR_INFO)) {
                if (ext.containsKey(Constants.PAIR_INFO)) {
                    String s = (String) ext.get(Constants.PAIR_INFO);
                    LiveDataBus.get().with(Constants.PAIR_INFO).postValue(new LiveEvent(s));
                }
            } else if (TextUtils.equals(action, Constants.GALLERY_UPDATE)) {
                if (ext.containsKey(Constants.GALLERY_UPDATE)) {
                    String s = (String) ext.get(Constants.GALLERY_UPDATE);
                    LiveDataBus.get().with(Constants.GALLERY_UPDATE).postValue(new LiveEvent(s));
                }
            } else if (TextUtils.equals(action, Constants.UNPAIR)) {
                if (ext.containsKey(Constants.UNPAIR)) {
                    String s = (String) ext.get(Constants.UNPAIR);
                    ToastUtil.show(s + "已跟你解除匹配");

                    UserUtils.resetUser();

                    AppManager.getInstance().finishAllActivity();
                    PairActivity.start(appContext);
                }
            } else if (TextUtils.equals(action, Constants.NOTE_UPDATE)) {
                if (ext.containsKey(Constants.NOTE_UPDATE)) {
                    LiveDataBus.get().with(Constants.NOTE_UPDATE).postValue(new LiveEvent());
                }
            } else if (TextUtils.equals(action, Constants.TA_AVATAR_UPDATE)) {
                if (ext.containsKey(Constants.TA_AVATAR_UPDATE)) {
                    String avatar = (String) ext.get(Constants.TA_AVATAR_UPDATE);
                    UserUtils.saveTaAvatar(avatar);
                    LiveDataBus.get().with(Constants.TA_AVATAR_UPDATE).postValue(new LiveEvent());
                }
            } else if (TextUtils.equals(action, Constants.TA_NICKNAME_UPDATE)) {
                if (ext.containsKey(Constants.TA_NICKNAME_UPDATE)) {
                    String name = (String) ext.get(Constants.TA_NICKNAME_UPDATE);
                    UserUtils.saveTaNickName(name);
                    LiveDataBus.get().with(Constants.TA_NICKNAME_UPDATE).postValue(new LiveEvent());
                }
            } else if (TextUtils.equals(action, Constants.CAT_FEED)) {
                if (ext.containsKey(Constants.CAT_FEED)) {
                    String time = (String) ext.get(Constants.CAT_FEED);
                    SharedPreferUtil.getInstance().setLastFeedPetTime(Long.parseLong(time));
                    LiveDataBus.get().with(Constants.CAT_FEED).postValue(new LiveEvent());
                }
            }
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        super.onMessageRead(messages);
//        if(!(DemoApplication.getInstance().getLifecycleCallbacks().current() instanceof ChatActivity)) {
//            EaseEvent event = EaseEvent.create(EaseConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE);
//            messageChangeLiveData.with(EaseConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
//        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {

        for (EMMessage msg : messages) {
            if (msg.getChatType() == EMMessage.ChatType.GroupChat && EaseAtMessageHelper.get().isAtMeMsg(msg)) {
                EaseAtMessageHelper.get().removeAtMeGroup(msg.getTo());
            }
            EMMessage msgNotification = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            String text = null;
            String recaller = msg.getRecaller();
            String from = msg.getFrom();
            if ((!TextUtils.isEmpty(recaller)) && !TextUtils.equals(recaller, from)) {
                text = String.format(context.getString(R.string.msg_recall_by_another), recaller, from);
            } else {
                text = String.format(context.getString(R.string.msg_recall_by_user), from);
            }
            EMTextMessageBody txtBody = new EMTextMessageBody(text);
            msgNotification.addBody(txtBody);
            msgNotification.setDirection(msg.direct());
            msgNotification.setFrom(msg.getFrom());
            msgNotification.setTo(msg.getTo());
            msgNotification.setUnread(false);
            msgNotification.setMsgTime(msg.getMsgTime());
            msgNotification.setLocalTime(msg.getMsgTime());
            msgNotification.setChatType(msg.getChatType());
            msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALL, true);
            msgNotification.setAttribute(EaseConstant.MESSAGE_TYPE_RECALLER, recaller);
            msgNotification.setStatus(EMMessage.Status.SUCCESS);
            EMClient.getInstance().chatManager().saveMessage(msgNotification);
        }

        EaseEvent event = EaseEvent.create(EaseConstant.MESSAGE_CHANGE_RECALL, EaseEvent.TYPE.MESSAGE);
        messageChangeLiveData.with(EaseConstant.MESSAGE_CHANGE_CHANGE).postValue(event);
    }

    private class ChatConversationListener implements EMConversationListener {

        @Override
        public void onCoversationUpdate() {

        }

        @Override
        public void onConversationRead(String from, String to) {
            EaseEvent event = EaseEvent.create(EaseConstant.CONVERSATION_READ, EaseEvent.TYPE.MESSAGE);
            messageChangeLiveData.with(EaseConstant.CONVERSATION_READ).postValue(event);
        }
    }

    private class ChatConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            EMLog.i(TAG, "onConnected");
            if (!ImHelper.getInstance().isLoggedIn()) {
                return;
            }


        }

        /**
         * 用来监听账号异常
         * @param error
         */
        @Override
        public void onDisconnected(int error) {
            EMLog.i(TAG, "onDisconnected =" + error);
            String event = null;
            if (error == EMError.USER_REMOVED) {
                event = EaseConstant.ACCOUNT_REMOVED;
            } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE
                    || error == EMError.USER_BIND_ANOTHER_DEVICE
                    || error == EMError.USER_DEVICE_CHANGED
                    || error == EMError.USER_LOGIN_TOO_MANY_DEVICES) {
                event = EaseConstant.ACCOUNT_CONFLICT;
            } else if (error == EMError.SERVER_SERVICE_RESTRICTED) {
                event = EaseConstant.ACCOUNT_FORBIDDEN;
            } else if (error == EMError.USER_KICKED_BY_CHANGE_PASSWORD) {
                event = EaseConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD;
            } else if (error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                event = EaseConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE;
            }
            if (!TextUtils.isEmpty(event)) {
                LiveDataBus.get().with(EaseConstant.ACCOUNT_CHANGE).postValue(new EaseEvent(event, EaseEvent.TYPE.ACCOUNT));
                EMLog.i(TAG, event);
            }
        }
    }


    /**
     * 移除目标所有的消息记录，如果目标被删除
     * @param target
     */
    private void removeTargetSystemMessage(String target, String params) {
        EMConversation conversation = EaseSystemMsgManager.getInstance().getConversation();
        List<EMMessage> messages = conversation.getAllMessages();
        if (messages != null && !messages.isEmpty()) {
            for (EMMessage message : messages) {
                String from = null;
                try {
                    from = message.getStringAttribute(params);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                if (TextUtils.equals(from, target)) {
                    conversation.removeMessage(message.getMsgId());
                }
            }
        }
    }

    /**
     * 移除目标所有的消息记录，如果目标被删除
     * @param target1
     */
    private void removeTargetSystemMessage(String target1, String params1, String target2, String params2) {
        EMConversation conversation = EaseSystemMsgManager.getInstance().getConversation();
        List<EMMessage> messages = conversation.getAllMessages();
        if (messages != null && !messages.isEmpty()) {
            for (EMMessage message : messages) {
                String targetParams1 = null;
                String targetParams2 = null;
                try {
                    targetParams1 = message.getStringAttribute(params1);
                    targetParams2 = message.getStringAttribute(params2);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                if (TextUtils.equals(targetParams1, target1) && TextUtils.equals(targetParams2, target2)) {
                    conversation.removeMessage(message.getMsgId());
                }
            }
        }
    }


    private void notifyNewInviteMessage(EMMessage msg) {
        // notify there is new message
        getNotifier().vibrateAndPlayTone(null);
    }

    private class MyPresenceListener implements EMPresenceListener {
        @Override
        public void onPresenceUpdated(List<EMPresence> list) {
            UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
            for (EMPresence presence : list) {
                if (taBean == null || TextUtils.isEmpty(taBean.getChatId())) {
                    continue;
                }
                if (TextUtils.equals(taBean.getChatId(), presence.getPublisher())) {
                    Map<String, Integer> map = presence.getStatusList();
                    for (String key : map.keySet()) {
                        LiveDataBus.get().with(Constants.USER_ONLINE).postValue(new LiveEvent(Constants.USER_ONLINE, map.get(key) + ""));
                    }
                }
            }
        }
    }
}
