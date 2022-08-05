package com.community.mua.imkit.modules.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.community.mua.callkit.EaseCallKit;
import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.common.Constants;
import com.community.mua.imkit.constants.EaseConstant;
import com.community.mua.imkit.manager.EaseDingMessageHelper;
import com.community.mua.imkit.modules.chat.interfaces.OnAddMsgAttrsBeforeSendEvent;
import com.community.mua.imkit.modules.chat.interfaces.OnChatLayoutListener;
import com.community.mua.imkit.modules.chat.interfaces.OnChatRecordTouchListener;
import com.community.mua.imkit.modules.chat.interfaces.OnMenuChangeListener;
import com.community.mua.imkit.modules.chat.interfaces.OnTranslateMessageListener;
import com.community.mua.imkit.modules.menu.EasePopupWindowHelper;
import com.community.mua.imkit.modules.menu.MenuItemBean;
import com.community.mua.imkit.ui.base.EaseBaseFragment;
import com.community.mua.imkit.utils.EaseCommonUtils;
import com.community.mua.imkit.utils.EaseCompat;
import com.community.mua.imkit.utils.EaseFileUtils;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.ui.DistanceActivity;
import com.community.mua.ui.WatchMovieActivity;
import com.community.mua.ui.LoveListActivity;
import com.community.mua.ui.call.VideoCallActivity;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.community.mua.R;

import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.PathUtil;
import com.hyphenate.util.VersionUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class EaseChatFragment extends EaseBaseFragment implements OnChatLayoutListener, OnMenuChangeListener, OnAddMsgAttrsBeforeSendEvent, OnChatRecordTouchListener, OnTranslateMessageListener, EasyPermissions.PermissionCallbacks {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_DING_MSG = 4;
    protected static final int REQUEST_CODE_SELECT_VIDEO = 11;
    protected static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final String TAG = EaseChatFragment.class.getSimpleName();
    private static final int PERMISSION_CODE_RECORD = 1001;
    private static final int PERMISSION_CODE_CAMERA = 1002;
    public EaseChatLayout chatLayout;
    public String conversationId;
    public int chatType;
    public String historyMsgId;
    public boolean isRoam;
    public boolean isMessageInit;
    private OnChatLayoutListener listener;

    protected File cameraFile;
    private boolean isWatchMovie;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initArguments();
        return inflater.inflate(getLayoutId(), null);
    }

    private int getLayoutId() {
        return R.layout.ease_fragment_chat_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            conversationId = bundle.getString(EaseConstant.EXTRA_CONVERSATION_ID);
            chatType = bundle.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            historyMsgId = bundle.getString(EaseConstant.HISTORY_MSG_ID);
            isRoam = bundle.getBoolean(EaseConstant.EXTRA_IS_ROAM, false);
            isWatchMovie = bundle.getBoolean("isWatchMovie", false);
        }
    }

    public void initView() {
        chatLayout = findViewById(R.id.layout_chat);
        chatLayout.getChatMessageListLayout().setItemShowType(EaseChatMessageListLayout.ShowType.NORMAL);
        if (isWatchMovie) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.movie_black_bg);
            chatLayout.getChatMessageListLayout().setChatBgColor(drawable);
            chatLayout.getChatInputMenu().getPrimaryMenu().setMenuBackground(drawable);
            chatLayout.getChatInputMenu().getEmojiconMenu().setMenuBackground(drawable);
            chatLayout.getChatInputMenu().getChatExtendMenu().setMenuBackground(drawable);
        } else {
            chatLayout.getChatMessageListLayout().setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
            chatLayout.getChatInputMenu().setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
        }
    }

    public void initListener() {
        chatLayout.setOnChatLayoutListener(this);
        chatLayout.setOnPopupWindowItemClickListener(this);
        chatLayout.setOnAddMsgAttrsBeforeSendEvent(this);
        chatLayout.setOnChatRecordTouchListener(this);
        chatLayout.setOnTranslateListener(this);
        LiveDataBus.get().with(Constants.WATCH_MOVIE_BLACK, LiveEvent.class).observe(getActivity(), this::updateWatchMovie);
    }

    private void updateWatchMovie(LiveEvent event){
        if (event.flag) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.movie_black_bg);
            chatLayout.getChatMessageListLayout().setChatBgColor(drawable);
            chatLayout.getChatInputMenu().getPrimaryMenu().setMenuBackground(drawable);
            chatLayout.getChatInputMenu().getEmojiconMenu().setMenuBackground(drawable);
            chatLayout.getChatInputMenu().getChatExtendMenu().setMenuBackground(drawable);
        } else {
            chatLayout.getChatMessageListLayout().setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
            chatLayout.getChatInputMenu().setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
        }
    }

    public void initData() {
        if (!TextUtils.isEmpty(historyMsgId)) {
            chatLayout.init(EaseChatMessageListLayout.LoadDataType.HISTORY, conversationId, chatType);
            chatLayout.loadData(historyMsgId);
        } else {
            if (isRoam) {
                chatLayout.init(EaseChatMessageListLayout.LoadDataType.ROAM, conversationId, chatType);
            } else {
                chatLayout.init(conversationId, chatType);
            }
            chatLayout.loadDefaultData();
        }
        isMessageInit = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onResume() {
        super.onResume();
        if (isMessageInit) {
            chatLayout.getChatMessageListLayout().refreshMessages();
        }
        chatLayout.getChatMessageListLayout().setChatBg(SharedPreferUtil.getInstance().getChatBg());
    }

    public void setOnChatLayoutListener(OnChatLayoutListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onBubbleClick(EMMessage message) {
        EMMessageBody body = message.getBody();
        if (body instanceof EMCustomMessageBody) {
            LoveListActivity.start(mContext);
        }

        if (listener != null) {
            return listener.onBubbleClick(message);
        }
        return false;
    }

    @Override
    public boolean onBubbleLongClick(View v, EMMessage message) {
        if (listener != null) {
            return listener.onBubbleLongClick(v, message);
        }
        return false;
    }

    @Override
    public void onUserAvatarClick(String username) {
        if (listener != null) {
            listener.onUserAvatarClick(username);
        }
    }

    @Override
    public void onUserAvatarLongClick(String username) {
        if (listener != null) {
            listener.onUserAvatarLongClick(username);
        }
    }

    @Override
    public void onChatExtendMenuItemClick(View view, int itemId) {
        SoundUtil.getInstance().playBtnSound();
        if (itemId == R.id.extend_item_take_picture) {
            if (EasyPermissions.hasPermissions(mContext, Manifest.permission.CAMERA)) {
                selectPicFromCamera();
            } else {
                EasyPermissions.requestPermissions(mContext, "需要拍照权限", PERMISSION_CODE_CAMERA, Manifest.permission.CAMERA);
            }
        } else if (itemId == R.id.extend_item_picture) {
            selectPicFromLocal();
        } else if (itemId == R.id.extend_item_location) {
            startMapLocation(REQUEST_CODE_MAP);
        } else if (itemId == R.id.extend_item_video) {
            //音视频
            showRtcDialog();
        } else if (itemId == R.id.extend_item_file) {
            selectFileFromLocal();
        } else if (itemId == R.id.extend_item_movie) {
            //看电影
            LiveDataBus.get().with(Constants.WATCH_MOVIE).postValue(new LiveEvent(true));
//            WatchMovieActivity.start(mContext);
        }
    }

    private void showRtcDialog() {
        NiceDialog.init().setLayoutId(R.layout.dialog_rtc)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.getView(R.id.tv_voice).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
//                                ToastUtil.show("期待上线中");
                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL, conversationId, null, VideoCallActivity.class);
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.tv_video).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
//                                ToastUtil.show("期待上线中");
                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL, conversationId, null, VideoCallActivity.class);
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setAnimStyle(R.style.BottomAnimation)
                .setMargin(DisplayUtil.dp2px(mContext, 5))
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onChatSuccess(EMMessage message) {
        // you can do something after sending a successful message
    }

    @Override
    public void onChatError(int code, String errorMsg) {
        if (listener != null) {
            listener.onChatError(code, errorMsg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            chatLayout.getChatInputMenu().hideExtendContainer();
            if (requestCode == REQUEST_CODE_CAMERA) { // capture new image
                onActivityResultForCamera(data);
            } else if (requestCode == REQUEST_CODE_LOCAL) { // send local image
                onActivityResultForLocalPhotos(data);
            } else if (requestCode == REQUEST_CODE_MAP) { // location
                onActivityResultForMapLocation(data);
            } else if (requestCode == REQUEST_CODE_DING_MSG) { // To send the ding-type msg.
                onActivityResultForDingMsg(data);
            } else if (requestCode == REQUEST_CODE_SELECT_FILE) {
                onActivityResultForLocalFiles(data);
            } else if (requestCode == REQUEST_CODE_SELECT_VIDEO) {
                onActivityResultForLocalVideos(data);
            }
        }
    }

    private void onActivityResultForLocalVideos(Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mContext, uri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int duration = mediaPlayer.getDuration();
            EMLog.d(TAG, "path = " + uri.getPath() + ",duration=" + duration);
            chatLayout.sendVideoMessage(uri, duration);
        }
    }

    /**
     * select picture from camera
     */
    protected void selectPicFromCamera() {
        if (!checkSdCardExist()) {
            return;
        }
        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(getContext(), cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {
        EaseCompat.openImage(this, REQUEST_CODE_LOCAL);
    }

    /**
     * 启动定位
     * @param requestCode
     */
    protected void startMapLocation(int requestCode) {
        DistanceActivity.start(this, requestCode);
    }

    /**
     * select local video
     */
    protected void selectVideoFromLocal() {
        Intent intent = new Intent();
        if (VersionUtils.isTargetQ(getActivity())) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");

        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);

    }

    /**
     * select local file
     */
    protected void selectFileFromLocal() {
        Intent intent = new Intent();
        if (VersionUtils.isTargetQ(getActivity())) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
            } else {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 相机返回处理结果
     * @param data
     */
    protected void onActivityResultForCamera(Intent data) {
        if (cameraFile != null && cameraFile.exists()) {
            chatLayout.sendImageMessage(Uri.parse(cameraFile.getAbsolutePath()));
        }
    }

    /**
     * 选择本地图片处理结果
     * @param data
     */
    protected void onActivityResultForLocalPhotos(@Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, selectedImage);
                if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    chatLayout.sendImageMessage(Uri.parse(filePath));
                } else {
                    EaseFileUtils.saveUriPermission(mContext, selectedImage, data);
                    chatLayout.sendImageMessage(selectedImage);
                }
            }
        }
    }

    /**
     * 地图定位结果处理
     * @param data
     */
    protected void onActivityResultForMapLocation(@Nullable Intent data) {
        if (data != null) {
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);
            String locationAddress = data.getStringExtra("address");
            String buildingName = data.getStringExtra("buildingName");
            if (locationAddress != null && !locationAddress.equals("")) {
                chatLayout.sendLocationMessage(latitude, longitude, locationAddress, buildingName);
            } else {
                if (listener != null) {
                    listener.onChatError(-1, getResources().getString(R.string.unable_to_get_loaction));
                }
            }
        }
    }

    protected void onActivityResultForDingMsg(@Nullable Intent data) {
        if (data != null) {
            String msgContent = data.getStringExtra("msg");
            EMLog.i(TAG, "To send the ding-type msg, content: " + msgContent);
            // Send the ding-type msg.
            EMMessage dingMsg = EaseDingMessageHelper.get().createDingMessage(conversationId, msgContent);
            chatLayout.sendMessage(dingMsg);
        }
    }

    /**
     * 本地文件选择结果处理
     * @param data
     */
    protected void onActivityResultForLocalFiles(@Nullable Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String filePath = EaseFileUtils.getFilePath(mContext, uri);
                if (!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
                    chatLayout.sendFileMessage(Uri.parse(filePath));
                } else {
                    EaseFileUtils.saveUriPermission(mContext, uri, data);
                    chatLayout.sendFileMessage(uri);
                }
            }
        }
    }

    /**
     * 检查sd卡是否挂载
     * @return
     */
    protected boolean checkSdCardExist() {
        return EaseCommonUtils.isSdcardExist();
    }

    @Override
    public void onPreMenu(EasePopupWindowHelper helper, EMMessage message, View v) {

    }

    @Override
    public boolean onMenuItemClick(MenuItemBean item, EMMessage message) {
        return false;
    }

    @Override
    public void addMsgAttrsBeforeSend(EMMessage message) {

    }

    /**
     * Set whether can touch voice button
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onRecordTouch(View v, MotionEvent event) {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.RECORD_AUDIO)) {
            return true;
        } else {
            EasyPermissions.requestPermissions(mContext, "需要录音权限", PERMISSION_CODE_RECORD, Manifest.permission.RECORD_AUDIO);
            return false;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == PERMISSION_CODE_CAMERA) {
            selectPicFromCamera();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        ToastUtil.show("需要权限");
    }

    @Override
    public void translateMessageSuccess(EMMessage message) {

    }

    @Override
    public void translateMessageFail(EMMessage message, int code, String error) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}

