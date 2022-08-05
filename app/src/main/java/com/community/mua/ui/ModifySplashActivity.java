package com.community.mua.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.SplashBean;
import com.community.mua.bean.SplashSizeBean;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivityModifySplashBinding;
import com.community.mua.imkit.utils.EaseCompat;
import com.community.mua.imkit.utils.EaseFileUtils;
import com.community.mua.services.QObserver;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.TakeImageUtils;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class ModifySplashActivity extends BaseActivity<ActivityModifySplashBinding> implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSION_CODE_GALLERY = 1002;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ModifySplashActivity.class));
    }

    @Override
    protected ActivityModifySplashBinding getViewBinding() {
        return ActivityModifySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        initFlContainer();

        initDefault();
    }

    private void initDefault() {
        View view = findViewById(R.id.iv_back);
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.topMargin = DisplayUtil.getStatusBarHeight(mContext);
            view.setLayoutParams(layoutParams);
        }
    }

    private void initFlContainer() {
        int width = DisplayUtil.getWidth(mContext) * 3 / 4;
        int height = DisplayUtil.getHeight(mContext) * 3 / 4;

        ViewGroup.LayoutParams layoutParams = mBinding.llContainer.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;

        mBinding.llContainer.setLayoutParams(layoutParams);
    }

    @Override
    protected void initData() {
        initSplash();
    }

    private void initSplash() {
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        if (pairBean != null) {
            UserUtils.setSplashImage(mContext, mBinding.ivSplash, pairBean.getSplashUrl());
        }
    }

    @Override
    protected void initListener() {
        mBinding.ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                onGalleryClick();
            }
        });
        mBinding.ivReplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                uploadSplash();
            }
        });
    }

    private void uploadSplash() {
        if (imageCropFile == null) {
            return;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageCropFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("media", imageCropFile.getName(), requestFile);

        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();

        Map<String, RequestBody> map = new HashMap<>();
        map.put("userId", RequestBody.create(null, userBean.getUserid()));

        App.getApi().uploadSplash(part, map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<SplashBean>(mContext) {
                    @Override
                    public void next(SplashBean avatarBean) {
                        String splashUrl = avatarBean.getSplashUrl();
                        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
                        pairBean.setSplashUrl(splashUrl);

                        SharedPreferUtil.getInstance().setPairBean(pairBean);
                        ToastUtil.show("设置成功");
                    }
                });
    }

    private File imageCropFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TakeImageUtils.REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                SplashSizeBean size = SharedPreferUtil.getInstance().getSplashSize();
                imageCropFile = TakeImageUtils.getInstance().cropPhoto(mContext, data.getData(), size.getX(), size.getY());
            }
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_CROP && resultCode == RESULT_OK) {
            if (imageCropFile != null && imageCropFile.getAbsolutePath() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (EaseFileUtils.uri != null) {
                        // 通过存储的uri 查询File
                        imageCropFile = EaseFileUtils.getCropFile(this, EaseFileUtils.uri);
                        settingPic();
                    }
                } else {
                    settingPic();
                }
            }
        }
    }

    private void settingPic() {
        InputStream is = null;
        try {
            Uri uriForFile = EaseCompat.getUriForFile(mContext, imageCropFile);
            is = getContentResolver().openInputStream(uriForFile);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            mBinding.ivSplash.setImageBitmap(bitmap);
            mBinding.ivUpload.setVisibility(View.GONE);
            mBinding.ivReplace.setVisibility(View.VISIBLE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if (requestCode == PERMISSION_CODE_GALLERY) {
            doOpenGallery();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        ToastUtil.show("需要权限");
    }


    private void onGalleryClick() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doOpenGallery();
        } else {
            EasyPermissions.requestPermissions(mContext, "需要文件存储权限", PERMISSION_CODE_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void doOpenGallery() {
        TakeImageUtils.getInstance().doOpenGallery(mContext);
    }
}
