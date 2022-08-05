package com.community.mua.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.community.mua.R;
import com.community.mua.adapter.SettingGridAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.SettingGridBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityMyInfoBinding;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MyInfoActivity extends BaseActivity<ActivityMyInfoBinding> implements SettingGridAdapter.OnItemClickListener {

    public static void start(Context context) {
        context.startActivity(new Intent(context, MyInfoActivity.class));
    }

    private ClipboardManager mClipboard;
    @Override
    protected ActivityMyInfoBinding getViewBinding() {
        return ActivityMyInfoBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("我的");
        mBinding.titleBar.ivSetting.setVisibility(View.VISIBLE);

        AnimationDrawable ad = (AnimationDrawable) mBinding.ivHeart.getDrawable();
        ad.start();
    }

    @Override
    protected void initData() {
        initRv();
        mClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        mBinding.tvCount.setText(DateUtil.getDuringDay(Long.parseLong(pairBean.getMatchingTime())) + "");

        UserUtils.setUserInfo(mContext,mBinding.tvNameMine, mBinding.flAvatarMine, mBinding.ivAvatarMine, SharedPreferUtil.getInstance().getUserBean());
        UserUtils.setUserInfo(mContext,mBinding.tvNameTa, mBinding.flAvatarTa, mBinding.ivAvatarTa, SharedPreferUtil.getInstance().getTaBean());
    }

    private void initRv() {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        mBinding.rv.setLayoutManager(layoutManager);

        List<SettingGridBean> list = new ArrayList<>();
        list.add(new SettingGridBean(R.mipmap.records_online, "登录记录"));
        list.add(new SettingGridBean(R.mipmap.custom_splash, "定制开屏"));
        list.add(new SettingGridBean(R.mipmap.share_app, "分享APP"));
        list.add(new SettingGridBean(R.mipmap.about_us, "关于我们"));

        mBinding.rv.setAdapter(new SettingGridAdapter(list, this));
    }

    @Override
    protected void initListener() {
        mBinding.titleBar.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                SettingActivity.start(mContext);
            }
        });


        mBinding.flAvatarMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                ModifyInfoActivity.start(mContext);
            }
        });

        mBinding.flAvatarTa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                ModifyInfoActivity.start(mContext);
            }
        });

        LiveDataBus.get().with(Constants.TA_AVATAR_UPDATE, LiveEvent.class).observe(this, this::onTaAvatarUpdate);
        LiveDataBus.get().with(Constants.TA_NICKNAME_UPDATE, LiveEvent.class).observe(this, this::onTaNameUpdate);
    }

    private void onTaNameUpdate(LiveEvent event) {
        UserBean userBean = SharedPreferUtil.getInstance().getTaBean();
        mBinding.tvNameTa.setText(userBean.getNickname());
    }

    private void onTaAvatarUpdate(LiveEvent event) {
        UserBean userBean = SharedPreferUtil.getInstance().getTaBean();
        UserUtils.setAvatar(mContext, userBean, mBinding.ivAvatarTa);
    }


    @Override
    public void onItemClick(int pos, SettingGridBean bean) {
        SoundUtil.getInstance().playBtnSound();
        if (pos == 1) {
            //订制开屏
            ModifySplashActivity.start(mContext);
        } else if (pos == 0){
            LoginRecordActivity.start(mContext);
        }else if (pos == 3){
            AboutHxActivity.start(mContext);
        } else {
            mClipboard.setPrimaryClip(ClipData.newPlainText(null, "https://download-sdk.oss-cn-beijing.aliyuncs.com/downloads/IMDemo/mua_v1.0.0.apk"));
            ToastUtil.show("Hello！欢迎来mua和我一起玩！打开⬇️链接:https://download-sdk.oss-cn-beijing.aliyuncs.com/downloads/IMDemo/mua_v1.0.0.apk");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001){
            UserUtils.setUserInfo(mContext,mBinding.tvNameMine, mBinding.flAvatarMine, mBinding.ivAvatarMine, SharedPreferUtil.getInstance().getUserBean());
        }
    }
}
