package com.community.mua.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.bean.LoveListBean;
import com.community.mua.bean.PetMarketBean;
import com.community.mua.bean.ChangeChatBgBean;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.SplashSizeBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferUtil {
    private static final String KEY_USER_BEAN = "key_user_bean";
    private static final String KEY_TA_BEAN = "key_ta_bean";
    private static final String KEY_PAIR_BEAN = "key_pair_bean";
    private static final String KEY_BGM = "key_bgm";

    private static final SharedPreferUtil ourInstance = new SharedPreferUtil();
    private static final String KEY_COINS = "key_coins";
    private static final String KEY_CHAT_BG = "key_chat_bg";
    private static final String KEY_SOUND = "key_sound";
    private static final String KEY_VIBRATE = "key_vibrate";
    private static final String KEY_PET_FOODS = "key_pet_foods";
    private static final String KEY_FEED_TIME = "key_feed_time";


    private static final String KEY_LATITUDE = "key_latitude";
    private static final String KEY_LONGITUDE = "key_longitude";
    private static final String KEY_LOCATION_ADDRESS = "key_Location_address";
    private static final String KEY_BATTERY_PROPERTY_CAPACITY = "key_property_capacity";
    private static final String KEY_BATTERY_STATUS_CHARGING = "key_property_capacity_status_change";
    private static final String KEY_SPLASH_SIZE = "key_splash_size";
    private static final String KEY_BTN_SOUND = "key_btn_sound";
    private static final String KEY_CHAT_FLOW = "key_chat_flow";
    private static final String KEY_LAST_COLLECT_COINS_TIME = "key_last_collect_coins_time";
    private static final String KEY_LAST_COIN_TIMER = "key_last_coin_timer";
    private static final String KEY_TODAY_COLLECT_COINS_COUNTS = "key_today_collect_coins_counts";
    private static final String KEY_LOVE_LIST = "key_love_list";


    public static SharedPreferUtil getInstance() {
        return ourInstance;
    }

    private SharedPreferUtil() {
    }

    private SharedPreferences mSharedPreference = App.getContext().getSharedPreferences("mua_config", Context.MODE_PRIVATE);

    public void setUserBean(UserBean bean) {
        mSharedPreference.edit().putString(KEY_USER_BEAN, GsonUtils.toJson(bean)).commit();
    }

    public UserBean getUserBean() {
        String str = mSharedPreference.getString(KEY_USER_BEAN, "");
        if (TextUtils.isEmpty(str)) {
            return null;
        }

        return GsonUtils.toBean(str, UserBean.class);
    }


    public void setTaBean(UserBean bean) {
        mSharedPreference.edit().putString(KEY_TA_BEAN, GsonUtils.toJson(bean)).commit();
    }

    public UserBean getTaBean() {
        String str = mSharedPreference.getString(KEY_TA_BEAN, "");
        if (TextUtils.isEmpty(str)) {
            return null;
        }

        return GsonUtils.toBean(str, UserBean.class);
    }

    public void setPairBean(PairBean bean) {
        mSharedPreference.edit().putString(KEY_PAIR_BEAN, GsonUtils.toJson(bean)).commit();
    }

    public PairBean getPairBean() {
        String str = mSharedPreference.getString(KEY_PAIR_BEAN, "");
        if (TextUtils.isEmpty(str)) {
            return null;
        }

        return GsonUtils.toBean(str, PairBean.class);
    }

    public void setBgm(String bgm) {
        mSharedPreference.edit().putString(KEY_BGM, bgm).commit();
    }

    public String getBgm() {
        return mSharedPreference.getString(KEY_BGM, "");
    }

    public void addCoins(int count) {
        int coins = getCoins();
        coins += count;
        mSharedPreference.edit().putInt(KEY_COINS, coins).commit();
        LiveDataBus.get().with(Constants.COINS_UPDATE, LiveEvent.class).postValue(new LiveEvent());
    }

    public int getCoins() {
        return mSharedPreference.getInt(KEY_COINS, 0);
    }

    public boolean payCoins(int count) {
        int coins = getCoins();
        if (coins == 0) {
            return false;
        }

        coins -= count;

        if (coins < 0) {
            return false;
        }

        LiveDataBus.get().with(Constants.COINS_UPDATE, LiveEvent.class).postValue(new LiveEvent());

        return mSharedPreference.edit().putInt(KEY_COINS, coins).commit();
    }

    public static List<ChangeChatBgBean> sBgBeans;

    public static void initBgBeans() {
        if (sBgBeans == null) {
            sBgBeans = new ArrayList<>();
            sBgBeans.add(new ChangeChatBgBean(R.drawable.stary, "天空的星星"));
            sBgBeans.add(new ChangeChatBgBean(R.drawable.pop_cron, "爆米花"));
            sBgBeans.add(new ChangeChatBgBean(R.drawable.sweetie, "甜甜的味道"));
            sBgBeans.add(new ChangeChatBgBean(R.drawable.little_duck, "小黄鸭"));
            sBgBeans.add(new ChangeChatBgBean(R.drawable.v_gesture, "比个✌"));
            sBgBeans.add(new ChangeChatBgBean(R.drawable.sky, "天空"));
            sBgBeans.add(new ChangeChatBgBean(R.drawable.untitled, "无题"));
            sBgBeans.add(new ChangeChatBgBean(R.drawable.frog, "一只小青蛙"));
            sBgBeans.add(new ChangeChatBgBean(R.drawable.corner, "角"));
        }
    }

    public static List<ChangeChatBgBean> getBgBeans() {
        if (sBgBeans == null) {
            initBgBeans();
        }
        return sBgBeans;
    }

    public void setChatBg(String name) {
        mSharedPreference.edit().putString(KEY_CHAT_BG, name).commit();
    }

    public boolean isChatBgSelected(String name) {
        String s = mSharedPreference.getString(KEY_CHAT_BG, "");
        if (TextUtils.isEmpty(s)) {
            return false;
        }
        return TextUtils.equals(s, name);
    }

    public int getChatBg() {
        String s = mSharedPreference.getString(KEY_CHAT_BG, "");
        if (sBgBeans == null || TextUtils.isEmpty(s)) {
            return R.drawable.bg_main;
        }

        for (ChangeChatBgBean bgBean : sBgBeans) {
            if (TextUtils.equals(bgBean.getTitle(), s)) {
                return bgBean.getImg();
            }
        }

        return R.drawable.bg_main;
    }

    public boolean getNotifySoundEnabled() {
        return mSharedPreference.getBoolean(KEY_SOUND, true);
    }

    public void setNotifySoundEnabled(boolean b) {
        mSharedPreference.edit().putBoolean(KEY_SOUND, b).commit();
    }

    public boolean getNotifyVibrateEnabled() {
        return mSharedPreference.getBoolean(KEY_VIBRATE, true);
    }

    public void setNotifyVibrateEnabled(boolean b) {
        mSharedPreference.edit().putBoolean(KEY_VIBRATE, b).commit();
    }

    public void setPetFoods(List<PetMarketBean> list) {
        String str = GsonUtils.toJson(list);
        mSharedPreference.edit().putString(KEY_PET_FOODS, str).commit();
    }

    public List<PetMarketBean> getPetFoods() {
        String str = mSharedPreference.getString(KEY_PET_FOODS, "");
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return GsonUtils.toList(str, PetMarketBean.class);
    }

    public void clearCoins() {
        mSharedPreference.edit().putInt(KEY_COINS, 0).commit();
    }

//    public void setLastFeedPetTime(long time) {
//        mSharedPreference.edit().putLong(KEY_FEED_TIME, time).commit();
//    }
//
//    public long getLastFeedPetTime() {
//        return mSharedPreference.getLong(KEY_FEED_TIME, 0);
//    }


    public void setLat(double lat) {
        mSharedPreference.edit().putString(KEY_LATITUDE, lat + "").commit();
    }

    public String getLat() {
        return mSharedPreference.getString(KEY_LATITUDE, "");
    }

    public void setLongitude(double longitude) {
        mSharedPreference.edit().putString(KEY_LONGITUDE, longitude + "").commit();
    }

    public String getLongitude() {
        return mSharedPreference.getString(KEY_LONGITUDE, "");
    }

    public void setLocationAdd(String address) {
        mSharedPreference.edit().putString(KEY_LOCATION_ADDRESS, address).commit();
    }

    public String getLocationAdd() {
        return mSharedPreference.getString(KEY_LOCATION_ADDRESS, "");
    }

    public void setBatteryManager(int currentLevel) {
        mSharedPreference.edit().putString(KEY_BATTERY_PROPERTY_CAPACITY, currentLevel + "").commit();
    }

    public String getBatteryManager() {
        return mSharedPreference.getString(KEY_BATTERY_PROPERTY_CAPACITY, "0");
    }

    public void setBatteryStatus(boolean status) {
        mSharedPreference.edit().putBoolean(KEY_BATTERY_STATUS_CHARGING, status).commit();
    }

    public boolean getBatteryStatus() {
        return mSharedPreference.getBoolean(KEY_BATTERY_STATUS_CHARGING, false);
    }

    public SplashSizeBean getSplashSize() {
        String str = mSharedPreference.getString(KEY_SPLASH_SIZE, "");
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return GsonUtils.toBean(str, SplashSizeBean.class);
    }

    public void setSplashSize(SplashSizeBean bean) {
        String str = GsonUtils.toJson(bean);
        mSharedPreference.edit().putString(KEY_SPLASH_SIZE, str).commit();
    }

    public void setBtnSoundEnabled(boolean checked) {
        mSharedPreference.edit().putBoolean(KEY_BTN_SOUND, checked).commit();
    }

    public boolean getBtnSoundEnabled() {
        return mSharedPreference.getBoolean(KEY_BTN_SOUND, true);
    }

    public void setChatFlowEnabled(boolean checked) {
        mSharedPreference.edit().putBoolean(KEY_CHAT_FLOW, checked).commit();
    }

    public boolean getChatFlowEnabled() {
        return mSharedPreference.getBoolean(KEY_CHAT_FLOW, true);
    }

    public void setLastCollectCoinsTime(long time) {
        mSharedPreference.edit().putLong(KEY_LAST_COLLECT_COINS_TIME, time).commit();
    }

    public long getLastCollectCoinsTime() {
        return mSharedPreference.getLong(KEY_LAST_COLLECT_COINS_TIME, 0);
    }

    public int getTodayCollectCoinsCounts() {
        return mSharedPreference.getInt(KEY_TODAY_COLLECT_COINS_COUNTS, 0);
    }

    public void setTodayCollectCoinsCounts(int i) {
        mSharedPreference.edit().putInt(KEY_TODAY_COLLECT_COINS_COUNTS, i).commit();
    }

    public void setLastCoinTimerCount(int timerCount) {
        mSharedPreference.edit().putInt(KEY_LAST_COIN_TIMER, timerCount).commit();
    }

    public int getLastCoinTimerCount() {
        return mSharedPreference.getInt(KEY_LAST_COIN_TIMER, -1);
    }

    public void setLastFeedPetTime(long currentTimeMillis) {
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        pairBean.setKittyStatusTime(currentTimeMillis);
        SharedPreferUtil.getInstance().setPairBean(pairBean);
    }

    public long getLastFeedPetTime() {
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        return pairBean.getKittyStatusTime();
    }

    public LoveListBean getDefaultLoveListBean(int pos){
        if (pos == 0){
            return new LoveListBean("一起看电影", "一起看一场走心电影叭");
        }
        if (pos == 1){
           return new LoveListBean("一起做饭", "一房·两人·三餐·四季");
        }
        return new LoveListBean("写一封情书", "无声的话语，却格外打动人心");
    }

    public List<LoveListBean> getLoveList() {
        String s = mSharedPreference.getString(KEY_LOVE_LIST, "");
        if (TextUtils.isEmpty(s)) {
            List<LoveListBean> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                list.add(getDefaultLoveListBean(i));
            }
            return list;
        }
        return GsonUtils.toList(s, LoveListBean.class);
    }

    public LoveListBean getLoveListBean(int pos) {
        return getLoveList().get(pos);
    }

    public void setLoveList(int pos, LoveListBean bean) {
        List<LoveListBean> list = getLoveList();
        list.set(pos, bean);
        mSharedPreference.edit().putString(KEY_LOVE_LIST, GsonUtils.toJson(list)).commit();
    }

    public void clearLoveListItem(int pos) {
        List<LoveListBean> list = getLoveList();
        list.set(pos, getDefaultLoveListBean(pos));
        mSharedPreference.edit().putString(KEY_LOVE_LIST, GsonUtils.toJson(list)).commit();
    }
}
