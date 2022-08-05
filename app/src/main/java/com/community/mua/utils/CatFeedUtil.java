package com.community.mua.utils;

public class CatFeedUtil {
    private static final float PET_FEED_EAT_TIME = 0.5f;
    private static final float PET_FEED_NORMAL_TIME = 10.0f;
    private static final float PET_FEED_HUNGRY_TIME = 200.0f;

    public static boolean canFeedCat() {
        if (isEating()) {
            ToastUtil.show("猪咪说：正在吃，别喂了");
            return false;
        }
        if (isFull()) {
            ToastUtil.show("猪咪说：实在吃不下了");
            return false;
        }
        return true;
    }

    public static boolean isEating() {
        return (System.currentTimeMillis() - SharedPreferUtil.getInstance().getLastFeedPetTime()) < PET_FEED_EAT_TIME * 1000 * 60;
    }

    public static boolean isFull() {
        return (System.currentTimeMillis() - SharedPreferUtil.getInstance().getLastFeedPetTime()) < PET_FEED_NORMAL_TIME * 1000 * 60;
    }

    public static boolean isHungry() {
        return (System.currentTimeMillis() - SharedPreferUtil.getInstance().getLastFeedPetTime()) > PET_FEED_HUNGRY_TIME * 1000 * 60;
    }

    public static boolean isNormal() {
        return (System.currentTimeMillis() - SharedPreferUtil.getInstance().getLastFeedPetTime()) > PET_FEED_NORMAL_TIME * 1000 * 60;
    }
} 
