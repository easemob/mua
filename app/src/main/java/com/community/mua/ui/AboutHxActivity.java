package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

import com.community.mua.base.BaseActivity;
import com.community.mua.databinding.ActivityAboutHxBinding;
import com.community.mua.utils.SoundUtil;

public class AboutHxActivity extends BaseActivity<ActivityAboutHxBinding> {

    public static void start(Context context) {
        context.startActivity(new Intent(context, AboutHxActivity.class));
    }

    String mVersionName;
    @Override
    protected ActivityAboutHxBinding getViewBinding() {
        return ActivityAboutHxBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("关于我们");
    }

    @Override
    protected void initData() {
        mBinding.itemAppVersion.getTvContent().setText(getVersion());
    }

    String getVersion() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            mVersionName = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "V" + mVersionName;
    }

    @Override
    protected void initListener() {
        mBinding.itemPrivacyRegulations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                Uri uri = Uri.parse("https://www.easemob.com/protocol");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mBinding.itemDisclaimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                DisclaimerActivity.start(mContext);
            }
        });

        mBinding.itemRegisteredEasemobAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                Uri uri1 = Uri.parse("https://console.easemob.com/user/register");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri1);
                startActivity(intent);
            }
        });
    }
}