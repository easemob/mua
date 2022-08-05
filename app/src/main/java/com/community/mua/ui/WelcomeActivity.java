package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.community.mua.base.BaseActivity;
import com.community.mua.bean.PetMarketBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityWelcomeBinding;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.SharedPreferUtil;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {
    public static void start(Context context) {
        context.startActivity(new Intent(context, WelcomeActivity.class));
    }

    @Override
    protected ActivityWelcomeBinding getViewBinding() {
        return ActivityWelcomeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    private void showAlpha(View v, long duration, OnAfterAniListener listener) {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(duration);//设置动画持续时间
        animation.setFillAfter(true);
        v.setAnimation(animation);
        v.startAnimation(animation);
        v.setVisibility(View.VISIBLE);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (listener != null) listener.onAfter();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void initData() {
        SharedPreferUtil.getInstance().addCoins(1000);

        setPetFoods();

        showAlpha(mBinding.ivMale, 2000, null);
        showAlpha(mBinding.ivReceive, 2000, () -> LiveDataBus.get().with(Constants.AFTER_RECEIVE_ANIM).postValue(new LiveEvent()));
    }

    private void setPetFoods() {
        List<PetMarketBean> list = new ArrayList<>();
        list.add(new PetMarketBean( "富含优质蛋白质，\n添加维生素和牛磺酸", 20));
        list.add(new PetMarketBean( "是时候给主子改善伙食了哦\n满满吞拿鱼和磷虾精华", 40));
        list.add(new PetMarketBean( "一口一个嘎嘣脆！\n新鲜老鼠做的特别脆！", 60));

        SharedPreferUtil.getInstance().setPetFoods(list);
    }

    private interface OnAfterAniListener {
        void onAfter();
    }

    @Override
    protected void initListener() {
//        LiveDataBus.get().with(Constants.AFTER_MALE_ANIM, LiveEvent.class).observe(this, this::afterMaleAnim);
        LiveDataBus.get().with(Constants.AFTER_RECEIVE_ANIM, LiveEvent.class).observe(this, this::afterReceiveAnim);
//        LiveDataBus.get().with(Constants.AFTER_FEMALE_ANIM, LiveEvent.class).observe(this, this::afterFemaleAnim);
        LiveDataBus.get().with(Constants.AFTER_SEND_ANIM, LiveEvent.class).observe(this, this::afterSendAnim);
    }

    private void afterSendAnim(LiveEvent event) {
        showAlpha(mBinding.btnEnter, 3000, null);
    }

    private void afterFemaleAnim(LiveEvent event) {
        showAlpha(mBinding.ivSend, 1500, () -> LiveDataBus.get().with(Constants.AFTER_SEND_ANIM).postValue(new LiveEvent()));
    }

    private void afterReceiveAnim(LiveEvent event) {
        showAlpha(mBinding.ivFemale, 1500, null);
        showAlpha(mBinding.ivSend, 1500, () -> LiveDataBus.get().with(Constants.AFTER_SEND_ANIM).postValue(new LiveEvent()));
    }

    private void afterMaleAnim(LiveEvent event) {
        showAlpha(mBinding.ivReceive, 1500, () -> LiveDataBus.get().with(Constants.AFTER_RECEIVE_ANIM).postValue(new LiveEvent()));
    }

    public void onEnter(View v) {
        SoundUtil.getInstance().playBtnSound();
        finish();
        MainActivity.start(mContext);
    }
}
