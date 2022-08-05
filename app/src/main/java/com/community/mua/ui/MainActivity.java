package com.community.mua.ui;

import static com.community.mua.utils.CatFeedUtil.canFeedCat;
import static com.community.mua.utils.CatFeedUtil.isEating;
import static com.community.mua.utils.CatFeedUtil.isFull;
import static com.community.mua.utils.CatFeedUtil.isHungry;
import static com.community.mua.utils.CatFeedUtil.isNormal;

import android.Manifest;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.adapter.MainTreasureAdapter;
import com.community.mua.adapter.PetFoodsAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.PetMarketBean;
import com.community.mua.bean.AlbumBean;
import com.community.mua.bean.Empty;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.TreasureBean;
import com.community.mua.bean.UserBean;
import com.community.mua.callkit.EaseCallKit;
import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityMainBinding;
import com.community.mua.imchat.ImHelper;
import com.community.mua.imkit.utils.EaseFileUtils;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.services.MusicService;
import com.community.mua.services.QObserver;
import com.community.mua.ui.call.VideoCallActivity;
import com.community.mua.ui.diary.MoodDiaryActivity;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.LocationService;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.TakeImageUtils;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.utils.Utils;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.DismisListener;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;
import com.community.mua.views.window.FloatWindow;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMPresence;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSION_CODE_CAMERA = 1001;
    private static final int PERMISSION_CODE_GALLERY = 1002;
    private static final int PERMISSION_CODE_SCREEN_SHOT = 1003;

    private static final int WHAT_PET_STATUS = 1004;
    private static final int WHAT_COIN_TIMER = 1005;

    private static final int COINS_PERIOD_ADD = 100;

    private UserBean mUserBean;
    private UserBean mTaBean;
    private Uri mPicUri;
    private PairBean mPairBean;
    private Intent mMusicIntent;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {

            if (msg.what == WHAT_PET_STATUS) {
                updatePetStatus(null);
                mHandler.sendEmptyMessageDelayed(WHAT_PET_STATUS, 20 * 1000);
            } else if (msg.what == WHAT_COIN_TIMER) {
                if (mTimerCount > 0) {
                    mBinding.tvTimer.setText(formatTime(mTimerCount--));
                    mHandler.sendEmptyMessageDelayed(WHAT_COIN_TIMER, 1000);
                } else {
                    mHandler.removeMessages(WHAT_COIN_TIMER);
                    switchCoinTimerView(STATUS_READY_2_COLLECT);
                }
            }
        }
    };


    private LocationService locationService;
    private File imageCropFile;

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        initScrollView();
    }

    private void initScrollView() {
        mBinding.sv.post(new Runnable() {
            @Override
            public void run() {
                mBinding.sv.scrollBy(DisplayUtil.dp2px(mContext, 360), 0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.ivUnreadChat.setVisibility(EMClient.getInstance().chatManager().getUnreadMessageCount() > 0 ? View.VISIBLE : View.GONE);
        initChatFlow();
    }


    @Override
    protected void onStart() {
        super.onStart();

        boolean hasPermissions = EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasPermissions) {
            locationService = ((App) getApplication()).locationService;
            //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
            locationService.registerListener(mLocationListener);
            boolean locationOption = locationService.setLocationOption(locationService.getDefaultLocationClientOption());


            locationService.start();// 定位SDK
        } else {
            //没有权限，向用户请求权限
            EasyPermissions.requestPermissions(this,
                    "权限申请原理对话框 : 描述申请权限的原理",
                    100, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }

        BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int currentLevel = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);


        SharedPreferUtil.getInstance().setBatteryManager(currentLevel);
    }

    private BDAbstractLocationListener mLocationListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String addrStr = location.getAddrStr();
                SharedPreferUtil.getInstance().setLat(latitude);
                SharedPreferUtil.getInstance().setLongitude(longitude);
                SharedPreferUtil.getInstance().setLocationAdd(addrStr);
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void initData() {

        initChatBg();

        setAvatars();

        setBelongTimes();

        setGalleryImage(SharedPreferUtil.getInstance().getPairBean().getAlbumUrl());

        publishOnline();

        startBgmService();

        setCoins();

        startPetStatusTimer();

        setCollectCoinsStatus();

    }

    private void setCollectCoinsStatus() {
        long collectCoinsTime = SharedPreferUtil.getInstance().getLastCollectCoinsTime();
        int collectCoinsCounts = SharedPreferUtil.getInstance().getTodayCollectCoinsCounts();
        int lastCoinTimerCount = SharedPreferUtil.getInstance().getLastCoinTimerCount();

        if (DateUtil.isYesterdayBefore(collectCoinsTime)) {
            //上次领是昨天
            collectCoinsCounts = 0;
            SharedPreferUtil.getInstance().setTodayCollectCoinsCounts(collectCoinsCounts);

            lastCoinTimerCount = -1;
            SharedPreferUtil.getInstance().setLastCollectCoinsTime(lastCoinTimerCount);
        }

        if (collectCoinsCounts == 10) {
            switchCoinTimerView(STATUS_COLLECTED);
            return;
        }

        if (lastCoinTimerCount != 0) {
            mTimerCount = lastCoinTimerCount > 0 ? lastCoinTimerCount : 5 * 60;
            startTimerCount();
        } else {
            switchCoinTimerView(STATUS_READY_2_COLLECT);
        }
    }

    private static final int STATUS_START_TIMER = 0;
    private static final int STATUS_READY_2_COLLECT = 1;
    private static final int STATUS_COLLECTED = 2;

    private void switchCoinTimerView(int status) {
        if (status == STATUS_START_TIMER) {
            mBinding.ivCoin.setVisibility(View.GONE);
            mBinding.tvTimer.setVisibility(View.VISIBLE);
        } else if (status == STATUS_READY_2_COLLECT) {
            mBinding.ivCoin.setVisibility(View.VISIBLE);
            mBinding.tvTimer.setVisibility(View.GONE);
            startShakeByPropertyAnim(mBinding.ivCoin, 0.9f, 1.5f, 10f, 500);
        } else if (status == STATUS_COLLECTED) {
            mBinding.ivCoin.setVisibility(View.GONE);
            mBinding.tvTimer.setVisibility(View.GONE);
        }
    }

    private void initChatFlow() {
        if (SharedPreferUtil.getInstance().getChatFlowEnabled()) {
            FloatWindow.get().show();

            View floatView = FloatWindow.get().getView();
            FrameLayout flAvatar = floatView.findViewById(R.id.fl_avatar);
            ImageView ivAvatar = floatView.findViewById(R.id.iv_avatar);

            UserUtils.setUserInfo(mContext, null, flAvatar, ivAvatar, mTaBean);

            updateFloatView();
        } else {
            FloatWindow.get().hide();
        }
    }

    private void startPetStatusTimer() {
        mHandler.sendEmptyMessage(WHAT_PET_STATUS);
    }

    private void updatePetStatus(LiveEvent event) {
        if (isEating()) {
            mBinding.ivPet.setImageResource(R.drawable.kitty_eatting);
            return;
        }
        if (isFull()) {
            mBinding.ivPet.setImageResource(R.drawable.kitty_full);
            mBinding.ivPetStatus.setImageResource(R.drawable.pet_status_normal);
            return;
        }
        if (isHungry()) {
            mBinding.ivPet.setImageResource(R.drawable.kitty_hungry);
            mBinding.ivPetStatus.setImageResource(R.drawable.pet_status_hunger);
            return;
        }
        if (isNormal()) {
            mBinding.ivPet.setImageResource(R.drawable.kitty_normal);
            mBinding.ivPetStatus.setImageResource(R.drawable.pet_status_normal);
        }
    }

    private void initChatBg() {
        SharedPreferUtil.initBgBeans();
    }

    private void setCoins() {
        int coins = SharedPreferUtil.getInstance().getCoins();
        mBinding.tvCoins.setText(String.valueOf(coins));
    }

    private void startBgmService() {
        mMusicIntent = new Intent(mContext, MusicService.class);
        //让该service前台运行，避免手机休眠时系统自动杀掉该服务
        //如果 id 为 0 ，那么状态栏的 notification 将不会显示。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MainActivity.this.startForegroundService(mMusicIntent);
        } else {
            startService(mMusicIntent);
        }

        String bgm = SharedPreferUtil.getInstance().getBgm();
        if (!TextUtils.isEmpty(bgm)) {
            LiveDataBus.get().with(Constants.BGM_CTRL).postValue(new LiveEvent(Constants.BGM_START, bgm));
        }
    }

    private void updateMusicView(LiveEvent event) {
        AnimationDrawable ad = (AnimationDrawable) mBinding.ivMusic.getDrawable();
        if (TextUtils.equals(Constants.BGM_START, event.event)) {
            ad.start();
        } else if (TextUtils.equals(Constants.BGM_STOP, event.event)) {
            ad.stop();
        }
    }

    public void onPetClick(View v) {
        if (isHungry()) {
            SoundUtil.getInstance().playCatHungrySound();
        } else if (isFull()) {
            SoundUtil.getInstance().playCatFullSound();
        }
        if (!canFeedCat()) {
            return;
        }
        onShowPetFoods(null);
    }

    private void publishOnline() {
        EMClient.getInstance().presenceManager().publishPresence("online", new EMCallBack() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(int code, String error) {
            }
        });

        EMClient.getInstance().presenceManager();
        UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
        List<String> list = new ArrayList<>();
        list.add(taBean.getChatId());
        ImHelper.getInstance().getEMClient().presenceManager().subscribePresences(list, 24 * 60 * 60 * 30, new EMValueCallBack<List<EMPresence>>() {
            @Override
            public void onSuccess(List<EMPresence> emPresences) {
                //FIXME 查询状态后刷新界面
                for (EMPresence p : emPresences) {
                    if (TextUtils.equals(taBean.getChatId(), p.getPublisher())) {
                        Map<String, Integer> map = p.getStatusList();
                        for (String key : map.keySet()) {
                            mBinding.ivTaState.setImageResource(map.get(key) == 0 ? R.drawable.sp_online_state : R.drawable.sp_online_state_on);
                            System.out.println("Key: " + key + ", Value: " + map.get(key));
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
            }
        });

    }

    private void setGalleryImage(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        url = Constants.HTTP_HOST + url;

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.sp_transparent)                //加载成功之前占位图
                .error(R.drawable.sp_transparent);

        Glide.with(mContext).applyDefaultRequestOptions(options).load(url).into(mBinding.ivGallery);
    }

    private void setBelongTimes() {
        if (mPairBean == null) return;
        long time = Long.parseLong(mPairBean.getMatchingTime());
        mBinding.tvTime.setText(DateUtil.getDuringDay(time) + " 天");
    }

    private void setAvatars() {
        mUserBean = SharedPreferUtil.getInstance().getUserBean();
        mTaBean = SharedPreferUtil.getInstance().getTaBean();
        mPairBean = SharedPreferUtil.getInstance().getPairBean();

        UserUtils.setUserInfo(mContext, null, mBinding.flMe, mBinding.ivMe, mUserBean);
        UserUtils.setUserInfo(mContext, null, mBinding.flTa, mBinding.ivTa, mTaBean);
    }

    @Override
    protected void initListener() {
        LiveDataBus.get().with(Constants.GALLERY_UPDATE, LiveEvent.class).observe(this, this::onGalleryUpdate);
        LiveDataBus.get().with(Constants.RECEIVE_MSG, LiveEvent.class).observe(this, this::onReceiveMsg);
        LiveDataBus.get().with(Constants.COINS_UPDATE, LiveEvent.class).observe(this, this::onCoinsUpdate);
        LiveDataBus.get().with(Constants.SHOW_PET_FOODS, LiveEvent.class).observe(this, this::onShowPetFoods);
        LiveDataBus.get().with(Constants.AVATAR_UPDATE, LiveEvent.class).observe(this, this::onAvatarUpdate);
        LiveDataBus.get().with(Constants.TA_AVATAR_UPDATE, LiveEvent.class).observe(this, this::onTaAvatarUpdate);
        LiveDataBus.get().with(Constants.USER_ONLINE, LiveEvent.class).observe(this, this::setTaOnlineState);
        LiveDataBus.get().with(Constants.BGM_CTRL, LiveEvent.class).observe(this, this::updateMusicView);
        LiveDataBus.get().with(Constants.CAT_FEED, LiveEvent.class).observe(this, this::updatePetStatus);
    }

    private void setTaOnlineState(LiveEvent event) {
        mBinding.ivTaState.setImageResource(Integer.valueOf(event.message) == 0 ? R.drawable.sp_online_state : R.drawable.sp_online_state_on);
    }

    private void onTaAvatarUpdate(LiveEvent event) {
        mTaBean = SharedPreferUtil.getInstance().getTaBean();
        UserUtils.setUserInfo(mContext, null, mBinding.flTa, mBinding.ivTa, mTaBean);
    }

    private boolean isFeedDialogShow;

    private void onShowPetFoods(LiveEvent event) {
        if (event != null) {
            mBinding.sv.post(new Runnable() {
                @Override
                public void run() {
                    mBinding.sv.scrollTo(DisplayUtil.dp2px(mContext, 80), 0);
                }
            });
        }

        if (isFeedDialogShow) {
            return;
        }

        NiceDialog.init().setLayoutId(R.layout.dialog_show_pet_foods)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        List<PetMarketBean> petFoods = SharedPreferUtil.getInstance().getPetFoods();
                        RecyclerView rv = holder.getView(R.id.rv);

                        rv.setLayoutManager(new GridLayoutManager(mContext, isFoodsEmpty(petFoods) ? 1 : 3));
                        rv.setAdapter(new PetFoodsAdapter(isFoodsEmpty(petFoods) ? null : petFoods, new PetFoodsAdapter.OnItemClickListener() {
                            @Override
                            public void onFeed() {
                                if (!canFeedCat()) {
                                    return;
                                }
                                uploadFeedTime(petFoods);

                            }

                            @Override
                            public void onEnterMarket() {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                PetMarketActivity.start(mContext);
                            }
                        }));

                        holder.getView(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setAnimStyle(R.style.BottomAnimation)
                .setOutCancel(true)
                .setShowBottom(true)
                .setDismissListener(new DismisListener() {
                    @Override
                    public void dismiss() {
                        isFeedDialogShow = false;
                    }
                })
                .show(getSupportFragmentManager());
        isFeedDialogShow = true;
    }

    private void uploadFeedTime(List<PetMarketBean> petFoods) {
        long currentTimeMillis = System.currentTimeMillis();

        Map<String, Object> map = new HashMap<>();
        map.put("time", String.valueOf(currentTimeMillis));
        map.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
        App.getApi().uploadCatFeedTime(map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Empty>(mContext) {
                    @Override
                    public void next(Empty empty) {

                        UserUtils.sendCmdMsg(Constants.CAT_FEED, String.valueOf(currentTimeMillis));

                        SharedPreferUtil.getInstance().setLastFeedPetTime(currentTimeMillis);

//      SharedPreferUtil.getInstance().setLastFeedPetTime(currentTimeMillis);
                        SharedPreferUtil.getInstance().setPetFoods(petFoods);
                        updatePetStatus(null);
                        ToastUtil.show("猪咪说：听我说谢谢你...");
                        SoundUtil.getInstance().playCatEatingSound();
                    }
                });
    }

    private boolean isFoodsEmpty(List<PetMarketBean> list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        for (PetMarketBean bean : list) {
            if (bean.getInventory() != 0) {
                return false;
            }
        }
        return true;
    }

    private void onCoinsUpdate(LiveEvent event) {
        setCoins();
    }

    private void onAvatarUpdate(LiveEvent event) {
        mUserBean = SharedPreferUtil.getInstance().getUserBean();
        UserUtils.setUserInfo(mContext, null, mBinding.flMe, mBinding.ivMe, mUserBean);
    }

    private void onReceiveMsg(LiveEvent event) {
        mBinding.ivUnreadChat.setVisibility(View.VISIBLE);
    }

    private void updateFloatView() {
        View floatView = FloatWindow.get().getView();
        TextView tvUnread = floatView.findViewById(R.id.tv_unread);
        UserUtils.setShowNum(tvUnread, EMClient.getInstance().chatManager().getUnreadMessageCount());
    }

    private void onGalleryUpdate(LiveEvent event) {
        String url = event.event;

        mPairBean.setAlbumUrl(url);
        SharedPreferUtil.getInstance().setPairBean(mPairBean);

        Log.e("tagqi", "onGalleryUpdate: " + url);
        setGalleryImage(url);
    }

    public void onGalleryChange(View v) {
        SoundUtil.getInstance().playBtnSound();
        NiceDialog.init().setLayoutId(R.layout.dialog_avatar_edit)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.getView(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                onPhotoClick();
                            }
                        });
                        holder.getView(R.id.tv_gallery).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                onGalleryClick();
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }

                })
                .setAnimStyle(R.style.BottomAnimation)
                .setMargin(DisplayUtil.dp2px(mContext, 5))
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void onPhotoClick() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doTakePhoto();
        } else {
            EasyPermissions.requestPermissions(mContext, "需要拍照、文件存储权限", PERMISSION_CODE_CAMERA, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void onGalleryClick() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doOpenGallery();
        } else {
            EasyPermissions.requestPermissions(mContext, "需要文件存储权限", PERMISSION_CODE_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void doTakePhoto() {
        mPicUri = TakeImageUtils.getInstance().doTakePhoto(mContext);
    }

    private void doOpenGallery() {
        TakeImageUtils.getInstance().doOpenGallery(mContext);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if (requestCode == PERMISSION_CODE_CAMERA) {
            doTakePhoto();
        } else if (requestCode == PERMISSION_CODE_GALLERY) {
            doOpenGallery();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        ToastUtil.show("需要权限");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TakeImageUtils.REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            imageCropFile = TakeImageUtils.getInstance().cropPhoto(mContext, mPicUri, 400, 300);
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_CAMERA && resultCode == RESULT_CANCELED) {
            System.out.println("拍照取消");
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                imageCropFile = TakeImageUtils.getInstance().cropPhoto(mContext, data.getData(), 400, 300);
            }
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_CROP && resultCode == RESULT_OK) {
            if (imageCropFile != null && imageCropFile.getAbsolutePath() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (EaseFileUtils.uri != null) {
                        // 通过存储的uri 查询File
                        imageCropFile = EaseFileUtils.getCropFile(this, EaseFileUtils.uri);
                        uploadPic();
                    }
                } else {
                    uploadPic();
                }
            }
        }
    }

    private void uploadPic() {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageCropFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("album", imageCropFile.getName(), requestFile);

        Map<String, RequestBody> map = new HashMap<>();
        map.put("matchCode", RequestBody.create(null, mPairBean.getMatchingCode()));

        App.getApi().uploadAlbum(part, map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<AlbumBean>(mContext) {
                    @Override
                    public void next(AlbumBean avatarBean) {
                        setGallery(avatarBean);
                    }
                });
    }

    private void setGallery(AlbumBean avatarBean) {
        String url = avatarBean.getAlbumUrl();

        LiveDataBus.get().with(Constants.GALLERY_UPDATE).postValue(new LiveEvent(url));

        UserUtils.sendCmdMsg(Constants.GALLERY_UPDATE, url);
    }

    public void onScreenShotNew(View v) {
        SoundUtil.getInstance().playBtnSound();
        showFlash();
    }

    public void showScreenShotDialog(Bitmap bitmap, Bitmap rotateBitmap) {
        NiceDialog.init().setLayoutId(R.layout.dialog_screen_shot)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                        ImageView ivContent = holder.getView(R.id.iv_content);
                        ivContent.setImageBitmap(rotateBitmap);

                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                saveScreenShot(bitmap);
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

    public void saveScreenShot(Bitmap bitmap) {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Utils.saveImageToGallery(mContext, bitmap);
        } else {
            EasyPermissions.requestPermissions(mContext, "需要文件存储权限", PERMISSION_CODE_SCREEN_SHOT, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void showFlash() {
        Bitmap bitmap = Utils.loadBitmapFromViewByCanvas(mBinding.llContent);
        Bitmap rotateBitmap = Utils.adjustPhotoRotation(bitmap, 90);

        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);//设置动画持续时间
        animation.setFillAfter(true);
        mBinding.ivFlash.setAnimation(animation);
        mBinding.ivFlash.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                SoundUtil.getInstance().playPhotoSound();
                mBinding.ivFlash.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mBinding.ivFlash.setVisibility(View.GONE);
                showScreenShotDialog(bitmap, rotateBitmap);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void onChatClick(View v) {
        SoundUtil.getInstance().playBtnSound();
        ChatActivity.start(mContext);
    }

    public void onPersonClick(View v) {
        SoundUtil.getInstance().playBtnSound();
        MyInfoActivity.start(mContext);
    }

    public void onDiaryClick(View v) {
        SoundUtil.getInstance().playBtnSound();
        MoodDiaryActivity.start(mContext);
    }

    public void onEnterAv(View v) {
        SoundUtil.getInstance().playBtnSound();
        NiceDialog.init().setLayoutId(R.layout.dialog_rtc)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.getView(R.id.tv_voice).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
//                                ToastUtil.show("期待上线中");
                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL, mTaBean.getChatId(), null, VideoCallActivity.class);
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.tv_video).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
//                                ToastUtil.show("期待上线中");
                                EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL, mTaBean.getChatId(), null, VideoCallActivity.class);
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setAnimStyle(R.style.BottomAnimation)
                .setMargin(DisplayUtil.dp2px(mContext, 5))
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    public void onEnterBgm(View v) {
        SoundUtil.getInstance().playBtnSound();
        BgmActivity.start(mContext);
    }

    public void onEnterGlobal(View v) {
        SoundUtil.getInstance().playBtnSound();
        MapLocationActivity.start(mContext);
    }

    public void onFridgeClick(View v) {
        SoundUtil.getInstance().playBtnSound();
        NotesActivity.start(mContext);
    }

    public void onTreasureClick(View v) {
        SoundUtil.getInstance().playBtnSound();

        NiceDialog.init().setLayoutId(R.layout.dialog_treasure)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                        RecyclerView rvFunction = holder.getView(R.id.rv_function);
                        RecyclerView rvGame = holder.getView(R.id.rv_game);

                        rvFunction.setLayoutManager(new GridLayoutManager(mContext, 4));
                        rvGame.setLayoutManager(new GridLayoutManager(mContext, 4));

                        List<TreasureBean> fucList = new ArrayList<>();
                        fucList.add(new TreasureBean(R.drawable.location, "查看距离"));
                        fucList.add(new TreasureBean(R.drawable.film, "看电影"));
                        fucList.add(new TreasureBean(R.drawable.notice, "便利贴"));
                        fucList.add(new TreasureBean(R.drawable.memorial_day, "纪念日"));
                        fucList.add(new TreasureBean(R.drawable.take_photo, "拍全家福"));
                        fucList.add(new TreasureBean(R.drawable.img, "相册"));
                        fucList.add(new TreasureBean(R.drawable.list, "恋爱清单"));
                        fucList.add(new TreasureBean(R.drawable.qna, "心动问答"));

                        rvFunction.setAdapter(new MainTreasureAdapter(fucList, new MainTreasureAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int pos, TreasureBean bean) {
                                dialog.dismissAllowingStateLoss();
                                SoundUtil.getInstance().playBtnSound();
                                onTreasureItemClick(0, pos, bean);
                            }
                        }));

                        List<TreasureBean> gameList = new ArrayList<>();
                        gameList.add(new TreasureBean(R.drawable.tacit, "默契挑战"));
                        gameList.add(new TreasureBean(R.drawable.draw_guess, "你画我猜"));
                        gameList.add(new TreasureBean(R.drawable.chess, "〇乂棋"));
                        gameList.add(new TreasureBean(R.drawable.puzze, "拼图比拼"));

                        rvGame.setAdapter(new MainTreasureAdapter(gameList, new MainTreasureAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int pos, TreasureBean bean) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                onTreasureItemClick(1, pos, bean);
                            }
                        }));

                        holder.getView(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setMargin(DisplayUtil.dp2px(mContext, 5))
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    private void onTreasureItemClick(int i, int pos, TreasureBean bean) {
        if (i == 0) {
            switch (pos) {
                case 0:
                    MapLocationActivity.start(mContext);
                    break;
                case 3:
                    CalendarActivity.start(mContext);
                    break;
                case 6:
                    LoveListActivity.start(mContext);
                    break;
                case 1:
                case 5:
                case 7:
                    showDeveloping(bean.getName());
                    break;
                case 2:
                    NotesActivity.start(mContext);
                    break;
                case 4:
                    showFlash();
                    break;
            }
        } else if (i == 1) {
            showDeveloping(bean.getName());
        }
    }

    public void onMarketClick(View v) {
        SoundUtil.getInstance().playBtnSound();
        NiceDialog.init().setLayoutId(R.layout.dialog_market)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.getView(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.ll_couch).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                showDeveloping("家具商城");
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.ll_makeup).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                showDeveloping("装扮商城");
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.ll_pet).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                onPetMarketOpen();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setMargin(DisplayUtil.dp2px(mContext, 5))
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    private void onPetMarketOpen() {
        SoundUtil.getInstance().playBtnSound();
        PetMarketActivity.start(mContext);
    }

    public void onCalendarClick(View v) {
        SoundUtil.getInstance().playBtnSound();
        CalendarActivity.start(mContext);
    }

    private void showDeveloping(String tips) {
        NiceDialog.init().setLayoutId(R.layout.dialog_developing)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        TextView tvTips = holder.getView(R.id.tv_tips);
                        tvTips.setText(tips + "\n开发中...");

                        holder.getView(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setMargin(DisplayUtil.dp2px(mContext, 20))
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    public void onCollectCoins(View v) {
        SoundUtil.getInstance().playCollectCoinsSound();

        int coins = SharedPreferUtil.getInstance().getCoins();
        mBinding.tvCoins.setNumber(coins);
        coins += COINS_PERIOD_ADD;
        mBinding.tvCoins.runWithAnimation(coins);
        SharedPreferUtil.getInstance().addCoins(COINS_PERIOD_ADD);

        SharedPreferUtil.getInstance().setLastCollectCoinsTime(System.currentTimeMillis());
        int collectCoinsCounts = SharedPreferUtil.getInstance().getTodayCollectCoinsCounts();
        SharedPreferUtil.getInstance().setTodayCollectCoinsCounts(++collectCoinsCounts);

        mTimerCount = -1;
        SharedPreferUtil.getInstance().setLastCoinTimerCount(mTimerCount);
        setCollectCoinsStatus();
    }

    public void onTvClick(View view){
        SoundUtil.getInstance().playBtnSound();
//        WatchMovieActivity.start(mContext);
        ChatActivity.start(mContext,true);
    }

    private int mTimerCount;

    private void startTimerCount() {
        switchCoinTimerView(STATUS_START_TIMER);
        mHandler.sendEmptyMessage(WHAT_COIN_TIMER);
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int seconds) {
        return String.format(" %02d:%02d", seconds / 60, seconds % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mMusicIntent);
        if (locationService != null) {
            locationService.unregisterListener(mLocationListener); //注销掉监听
            locationService.stop(); //停止定位服务
        }
        mHandler.removeCallbacksAndMessages(null);
        SharedPreferUtil.getInstance().setLastCoinTimerCount(mTimerCount);
    }

    private void startShakeByPropertyAnim(View view, float scaleSmall, float scaleLarge, float shakeDegrees, long duration) {
        if (view == null) {
            return;
        }

        Animation animation = view.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
        //TODO 验证参数的有效性

        //先变小后变大
        PropertyValuesHolder scaleXValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1.0f),
                Keyframe.ofFloat(0.25f, scaleSmall),
                Keyframe.ofFloat(0.5f, scaleLarge),
                Keyframe.ofFloat(0.75f, scaleLarge),
                Keyframe.ofFloat(1.0f, 1.0f)
        );
        PropertyValuesHolder scaleYValuesHolder = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1.0f),
                Keyframe.ofFloat(0.25f, scaleSmall),
                Keyframe.ofFloat(0.5f, scaleLarge),
                Keyframe.ofFloat(0.75f, scaleLarge),
                Keyframe.ofFloat(1.0f, 1.0f)
        );

        //先往左再往右
        PropertyValuesHolder rotateValuesHolder = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(0.1f, -shakeDegrees),
                Keyframe.ofFloat(0.2f, shakeDegrees),
                Keyframe.ofFloat(0.3f, -shakeDegrees),
                Keyframe.ofFloat(0.4f, shakeDegrees),
                Keyframe.ofFloat(0.5f, -shakeDegrees),
                Keyframe.ofFloat(0.6f, shakeDegrees),
                Keyframe.ofFloat(0.7f, -shakeDegrees),
                Keyframe.ofFloat(0.8f, shakeDegrees),
                Keyframe.ofFloat(0.9f, -shakeDegrees),
                Keyframe.ofFloat(1.0f, 0f)
        );

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleXValuesHolder, scaleYValuesHolder, rotateValuesHolder);
        objectAnimator.setDuration(duration);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.start();
    }
}