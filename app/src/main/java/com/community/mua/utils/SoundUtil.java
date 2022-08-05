package com.community.mua.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.SoundPool;

import com.community.mua.R;

public class SoundUtil {
    private static final SoundUtil ourInstance = new SoundUtil();
    private SoundPool mSoundPool;
    private int mBtnSoundID;
    private boolean isEnable;
    private int mCameraId;
    private int mCatEatingId;
    private int mCatFullClickId;
    private int mCatHungryClickId;
    private int mCollectCoinsId;

    public static SoundUtil getInstance() {
        return ourInstance;
    }

    private SoundUtil() {

    }


    @SuppressLint("NewApi")
    public void initSound(Context context) {
        mSoundPool = new SoundPool.Builder().build();
        mBtnSoundID = mSoundPool.load(context, R.raw.btn_sound, 99);
        mCameraId = mSoundPool.load(context, R.raw.take_photo, 99);

        mCatEatingId = mSoundPool.load(context, R.raw.cat_eating, 99);
        mCatFullClickId = mSoundPool.load(context, R.raw.cat_full_click, 99);
        mCatHungryClickId = mSoundPool.load(context, R.raw.cat_hungry_click, 99);
        mCollectCoinsId = mSoundPool.load(context, R.raw.collect_coins, 99);
    }

    public void playCatEatingSound() {
        mSoundPool.play(
                mCatEatingId,
                1f,      //左耳道音量【0~1】
                1f,      //右耳道音量【0~1】
                99,         //播放优先级【0表示最低优先级】
                6,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }

    public void playCatFullSound() {
        mSoundPool.play(
                mCatFullClickId,
                1f,      //左耳道音量【0~1】
                1f,      //右耳道音量【0~1】
                99,         //播放优先级【0表示最低优先级】
                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }

    public void playCatHungrySound() {
        mSoundPool.play(
                mCatHungryClickId,
                1f,      //左耳道音量【0~1】
                1f,      //右耳道音量【0~1】
                99,         //播放优先级【0表示最低优先级】
                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }

    public void playCollectCoinsSound() {
        mSoundPool.play(
                mCollectCoinsId,
                1f,      //左耳道音量【0~1】
                1f,      //右耳道音量【0~1】
                99,         //播放优先级【0表示最低优先级】
                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }

    public void playBtnSound() {
        if (!isEnable) {
            return;
        }
        mSoundPool.play(
                mBtnSoundID,
                1f,      //左耳道音量【0~1】
                1f,      //右耳道音量【0~1】
                99,         //播放优先级【0表示最低优先级】
                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }

    public void playPhotoSound() {
        mSoundPool.play(
                mCameraId,
                1f,      //左耳道音量【0~1】
                1f,      //右耳道音量【0~1】
                99,         //播放优先级【0表示最低优先级】
                0,         //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1          //播放速度【1是正常，范围从0~2】
        );
    }

    public void switchSoundEnabled(boolean b) {
        isEnable = b;
    }
}
