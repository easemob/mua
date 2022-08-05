package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.mua.R;
import com.community.mua.adapter.MusicAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.MusicBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityBgmBinding;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.SharedPreferUtil;

import java.util.ArrayList;
import java.util.List;

public class BgmActivity extends BaseActivity<ActivityBgmBinding> implements MusicAdapter.OnItemClickListener {

    private static final float ROTATE_SENSOR_OFF = -25.0f;

    private static final long SENSOR_DURATION = 300;
    private static final long MUSIC_ROLL_DURATION = 15000;

    private static final float SENSOR_X = 0.26f;
    private static final float SENSOR_Y = 0.09f;

    private int mCurrentPos;

    public static void start(Context context) {
        context.startActivity(new Intent(context, BgmActivity.class));
    }

    @Override
    protected ActivityBgmBinding getViewBinding() {
        return ActivityBgmBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("背景音乐");
        mBinding.titleBar.getRoot().setBackgroundColor(Color.parseColor("#00000000"));

    }

    @Override
    protected void initData() {
        initRv();
    }

    private void initRv() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rv.setLayoutManager(layoutManager);

        List<MusicBean> list = new ArrayList<>();
        list.add(new MusicBean(R.drawable.no_music, "关闭音乐", ""));
        list.add(new MusicBean(R.drawable.beauty_girl, "都市丽人", "beauty.mp3"));
        list.add(new MusicBean(R.drawable.jazz, "美好时光", "jazz.mp3"));
        list.add(new MusicBean(R.drawable.holiday, "田野的风", "holiday.mp3"));
        list.add(new MusicBean(R.drawable.feelinggood, "梦境之旅", "feeling good.mp3"));

        MusicAdapter adapter = new MusicAdapter(list, this);
        mBinding.rv.setAdapter(adapter);

        String bgm = SharedPreferUtil.getInstance().getBgm();
        if (TextUtils.isEmpty(bgm)) {
            switchSensorOff(10, null);
        } else {
            startMusicUI(bgm, false);

            for (int i = 0; i < list.size(); i++) {
                MusicBean bean = list.get(i);
                if (TextUtils.equals(bgm, bean.getMusicName())) {
                    adapter.setCheckPos(i);
                    mCurrentPos = i;
                    mBinding.ivCover.setImageResource(bean.getIcon());
                    break;
                }
            }
        }
    }

    private void startMusicRoll() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RotateAnimation rotateMusicRoll = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateMusicRoll.setInterpolator(new LinearInterpolator());
                rotateMusicRoll.setDuration(MUSIC_ROLL_DURATION);//设置动画持续周期
                rotateMusicRoll.setRepeatCount(-1);//设置重复次数
                mBinding.ivCover.startAnimation(rotateMusicRoll);
            }
        }, SENSOR_DURATION);
    }

    private void stopMusicRoll() {
        stopMusic();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBinding.ivCover.clearAnimation();
            }
        }, 50);
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onItemClick(int pos, MusicBean bean) {
        SoundUtil.getInstance().playBtnSound();
        if (mCurrentPos == pos) {
            return;
        }
        SharedPreferUtil.getInstance().setBgm(bean.getMusicName());

        if (mCurrentPos == 0) {
            startMusicUI(bean.getMusicName(), true);
        } else if (pos == 0) {
            stopMusicUI(null);
        } else {
            //先stop再start
            stopMusicUI(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startMusicUI(bean.getMusicName(), true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }

        mCurrentPos = pos;

        mBinding.ivCover.setImageResource(bean.getIcon());
    }

    private void stopMusicUI(Animation.AnimationListener listener) {
        stopMusicRoll();

        switchSensorOff(SENSOR_DURATION, listener);
    }

    private void switchSensorOff(long l, Animation.AnimationListener listener) {
        RotateAnimation rotateSensor = new RotateAnimation(0f, ROTATE_SENSOR_OFF, Animation.RELATIVE_TO_SELF, SENSOR_X, Animation.RELATIVE_TO_SELF, SENSOR_Y);
        rotateSensor.setInterpolator(new LinearInterpolator());
        rotateSensor.setDuration(l);//设置动画持续周期
        rotateSensor.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotateSensor.setStartOffset(10);//执行前的等待时间
        if (listener != null) rotateSensor.setAnimationListener(listener);
        mBinding.ivSensorOn.startAnimation(rotateSensor);
    }

    private void startMusicUI(String bgm, boolean ctrl) {
        startMusicRoll();

        switchSensorOn(bgm, ctrl);
    }

    private void switchSensorOn(String bgm, boolean ctrl) {
        RotateAnimation rotateSensor = new RotateAnimation(ROTATE_SENSOR_OFF, 0f, Animation.RELATIVE_TO_SELF, SENSOR_X, Animation.RELATIVE_TO_SELF, SENSOR_Y);
        rotateSensor.setInterpolator(new LinearInterpolator());
        rotateSensor.setDuration(SENSOR_DURATION);//设置动画持续周期
        rotateSensor.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotateSensor.setStartOffset(10);//执行前的等待时间
        rotateSensor.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (ctrl) playMusic(bgm);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mBinding.ivSensorOn.startAnimation(rotateSensor);
    }

    private void playMusic(String musicName) {
        LiveDataBus.get().with(Constants.BGM_CTRL).postValue(new LiveEvent(Constants.BGM_START, musicName));
    }


    private void stopMusic() {
        LiveDataBus.get().with(Constants.BGM_CTRL).postValue(new LiveEvent(Constants.BGM_STOP));
    }
}
