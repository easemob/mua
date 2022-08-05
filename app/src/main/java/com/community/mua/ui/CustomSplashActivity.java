package com.community.mua.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.SplashBean;
import com.community.mua.bean.SplashSizeBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityCustomSplashBinding;
import com.community.mua.imkit.utils.EaseCompat;
import com.community.mua.imkit.utils.EaseFileUtils;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.TakeImageUtils;
import com.community.mua.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class CustomSplashActivity extends BaseActivity<ActivityCustomSplashBinding> {

    public static void start(Context context) {
        Intent intent = new Intent(context, CustomSplashActivity.class);
        context.startActivity(intent);
    }

    private static final int PERMISSION_CODE_GALLERY = 1002;

    @Override
    protected ActivityCustomSplashBinding getViewBinding() {
        return ActivityCustomSplashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        Bitmap splashBg = BitmapFactory.decodeResource(getResources(), R.drawable.splash_frame);
        int width = splashBg.getWidth();
        int height = splashBg.getHeight();

//        int widthScreen = DisplayUtil.getWidth(mContext) *3/4;
//        int heightScreen = widthScreen * height / width;
        int heightScreen = DisplayUtil.getHeight(mContext) * 3 / 4;
        int widthScreen = heightScreen * width / height;

        ViewGroup.LayoutParams mFlDateLayoutParams = mBinding.flSplash.getLayoutParams();
        mFlDateLayoutParams.width = widthScreen;
        mFlDateLayoutParams.height = heightScreen;
        mBinding.flSplash.setLayoutParams(mFlDateLayoutParams);
        mBinding.flSplash.setBackgroundResource(R.drawable.splash_frame);

        Bitmap splashDf = BitmapFactory.decodeResource(getResources(), R.drawable.splash_img_default);
        int widthDf = splashDf.getWidth();
        int heightDf = splashDf.getHeight();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.ivSplash.getLayoutParams();
        params.topMargin = DisplayUtil.dp2px(mContext, 67.5f);
        params.bottomMargin = DisplayUtil.dp2px(mContext, 20.5f);
        params.leftMargin = DisplayUtil.dp2px(mContext, 6.5f);
        params.rightMargin = DisplayUtil.dp2px(mContext, 6.5f);
        params.width = DisplayUtil.getWidth(mContext) * 3 / 4;
        params.height = params.width * heightDf / widthDf;
        ;
        mBinding.ivSplash.setLayoutParams(params);
        mBinding.ivSplash.setImageResource(R.drawable.splash_img_default);
    }

    @Override
    protected void initData() {
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        if (!TextUtils.isEmpty(pairBean.getSplashUrl())) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.splash_img_default)
                    .error(R.drawable.splash_img_default);
            Glide.with(mContext).applyDefaultRequestOptions(options).load(Constants.HTTP_HOST + pairBean.getSplashUrl()).into(mBinding.ivSplash);
        } else {
            Bitmap splashDf = BitmapFactory.decodeResource(getResources(), R.drawable.splash_img_default);
            int widthDf = splashDf.getWidth();
            int heightDf = splashDf.getHeight();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.ivSplash.getLayoutParams();
            params.topMargin = DisplayUtil.dp2px(mContext, 67.5f);
            params.bottomMargin = DisplayUtil.dp2px(mContext, 20.5f);
            params.leftMargin = DisplayUtil.dp2px(mContext, 6.5f);
            params.rightMargin = DisplayUtil.dp2px(mContext, 6.5f);
            params.width = DisplayUtil.getWidth(mContext) * 3 / 4;
            params.height = params.width * heightDf / widthDf;
            ;
            mBinding.ivSplash.setLayoutParams(params);
            mBinding.ivSplash.setImageResource(R.drawable.splash_img_default);
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

    private void onGalleryClick() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doOpenGallery();
        } else {
            EasyPermissions.requestPermissions(mContext, "需要文件存储权限", PERMISSION_CODE_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void doOpenGallery() {
        TakeImageUtils.getInstance().doOpenGallery(mContext);
//        EaseCompat.openImage(mContext,REQUEST_CODE_GALLERY);
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.setType("image/*");
//        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }
}