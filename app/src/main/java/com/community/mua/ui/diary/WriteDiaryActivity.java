package com.community.mua.ui.diary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.AppManager;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.AvatarBean;
import com.community.mua.bean.DiaryBean;
import com.community.mua.bean.GvFeelBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityWriteDiaryBinding;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.services.ApiService;
import com.community.mua.services.QObserver;
import com.community.mua.ui.ModifyInfoActivity;
import com.community.mua.utils.BatteryUtils;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class WriteDiaryActivity extends BaseActivity<ActivityWriteDiaryBinding> {


    public static void start(Context context, DiaryBean diaryBean, boolean isEdit) {
        Intent intent = new Intent(context, WriteDiaryActivity.class);
        intent.putExtra("diaryBean",diaryBean);
        intent.putExtra("isEdit",isEdit);
        context.startActivity(intent);
    }

    public static void start(Context context, int pos, long date) {
        Intent intent = new Intent(context, WriteDiaryActivity.class);
        intent.putExtra("date",date);
        intent.putExtra("position",pos);
        context.startActivity(intent);
    }

    private static final int PERMISSION_CODE_GALLERY = 1002;

    private static final int REQUEST_CODE_GALLERY = 1011;

    private int pos;
    private long date;

    private boolean isEdit;
    private DiaryBean diaryBean;

    @Override
    protected ActivityWriteDiaryBinding getViewBinding() {
        return ActivityWriteDiaryBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("添加日记");
        mBinding.titleBar.tvMore.setVisibility(View.VISIBLE);
        mBinding.titleBar.tvMore.setText("保存");

        Bitmap splashBg = BitmapFactory.decodeResource(getResources(), R.drawable.diary_bg);
        int width = splashBg.getWidth();
        int height  = splashBg.getHeight();

        int widthScreen = DisplayUtil.getWidth(mContext) ;
        int heightScreen = widthScreen * height / width;


        ViewGroup.LayoutParams mFlDateLayoutParams = mBinding.flDiary.getLayoutParams();
        mFlDateLayoutParams.width = widthScreen;
        mFlDateLayoutParams.height = heightScreen;
        mBinding.flDiary.setLayoutParams(mFlDateLayoutParams);
        mBinding.flDiary.setBackgroundResource(R.drawable.diary_bg);

    }

    @Override
    protected void initData() {
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (!isEdit) {
            pos = getIntent().getIntExtra("position",0);
            date = getIntent().getLongExtra("date",System.currentTimeMillis());


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
            String format = simpleDateFormat.format(date);
            mBinding.tvDay.setText(format);

            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy年MM月");
            String str = simpleFormat.format(date);

            String weekStr = DateUtil.getWeek(date);
            mBinding.tvYearWeek.setText(weekStr +"\n"+str);

            mBinding.ivDiaryMoon.setImageResource(BatteryUtils.list.get(pos).getDesId());
            mBinding.tvDiaryMoon.setText(BatteryUtils.list.get(pos).getName());
        } else {
            //更新日记ID参选
            diaryBean = (DiaryBean) getIntent().getSerializableExtra("diaryBean");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
            date = diaryBean.getDiaryTimeStamp();
            String format = simpleDateFormat.format(date);
            mBinding.tvDay.setText(format);

            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy年MM月");
            String str = simpleFormat.format(date);

            String weekStr = DateUtil.getWeek(date);
            mBinding.tvYearWeek.setText(weekStr +"\n"+str);

            mBinding.ivDiaryMoon.setImageResource(Integer.parseInt(diaryBean.getMood()));
            mBinding.tvDiaryMoon.setText(diaryBean.getMoodReason());
            mBinding.etContent.setText(diaryBean.getContent());

            if (!TextUtils.isEmpty(diaryBean.getPic())) {
                Glide.with(mContext).load(Constants.HTTP_HOST + diaryBean.getPic()).into(mBinding.ivDiaryPic);
            }
        }

    }

    @Override
    protected void initListener() {
        mBinding.titleBar.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEdit) {
                    editDiary();
                } else {
                    writeDiary();
                }
            }
        });

        mBinding.tvAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开相册选择照片
                onGalleryClick();
            }
        });
    }

    private void onGalleryClick() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doOpenGallery();
        } else {
            EasyPermissions.requestPermissions(mContext, "需要文件存储权限", PERMISSION_CODE_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void doOpenGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    private Uri mTempFileUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                mTempFileUri = data.getData();
                showPic();
            }
        }
    }

    private void showPic(){
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(mTempFileUri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            mBinding.ivDiaryPic.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            ToastUtil.show(e.getMessage());
        }

    }
    private void writeDiary(){
        String content = mBinding.etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show("日记内容不能为空");
            return;
        }



        if (mTempFileUri != null) {
            String path = Utils.getRealFilePath(mContext,mTempFileUri);;
            File file = new File(path);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("media", file.getName(), requestFile);
            Map<String, RequestBody> map = new HashMap<>();
            map.put("matchingCode", RequestBody.create(null, SharedPreferUtil.getInstance().getPairBean().getMatchingCode()));
            map.put("mood", RequestBody.create(null, pos+""));
            map.put("userId", RequestBody.create(null, SharedPreferUtil.getInstance().getUserBean().getUserid()));
            map.put("content", RequestBody.create(null, content));

            map.put("diaryTimeStamp", RequestBody.create(null, date+""));
            map.put("moodReason", RequestBody.create(null, mBinding.tvDiaryMoon.getText().toString()));
            map.put("isPrivate", RequestBody.create(null, "false"));

            App.getApi().writeDiaryFile(part,map).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new QObserver<Object>(mContext) {
                        @Override
                        public void next(Object avatarBean) {
                            refreshDiary();
                            //关闭界面
                            AppManager.getInstance().finishActivity(AddDiaryActivity.class);
                            finish();
                        }
                    });
        } else {
           //没有图片
            Map<String, String> map = new HashMap<>();
            map.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
            map.put("userId", SharedPreferUtil.getInstance().getUserBean().getUserid());
            map.put("mood", pos+"");
            map.put("diaryTimeStamp", date+"");
            map.put("content",  content);
            map.put("moodReason", mBinding.tvDiaryMoon.getText().toString());
            map.put("isPrivate", "false");

            App.getApi().writeDiary(map).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new QObserver<Object>(mContext, true) {
                        @Override
                        public void next(Object userBean) {
                            refreshDiary();
                            AppManager.getInstance().finishActivity(AddDiaryActivity.class);
                            finish();
                        }
                    });
        }
    }


    private void editDiary(){
        String content = mBinding.etContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show("日记内容不能为空");
            return;
        }

        if (mTempFileUri != null) {
            String path = Utils.getRealFilePath(mContext,mTempFileUri);;
            File file = new File(path);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("media", file.getName(), requestFile);
            Map<String, RequestBody> map = new HashMap<>();
            map.put("diaryId", RequestBody.create(null, diaryBean.getDiaryId()));
            map.put("matchingCode", RequestBody.create(null, SharedPreferUtil.getInstance().getPairBean().getMatchingCode()));
            map.put("mood", RequestBody.create(null, diaryBean.getMood()));
            map.put("userId", RequestBody.create(null, SharedPreferUtil.getInstance().getUserBean().getUserid()));
            map.put("content", RequestBody.create(null, content));
            map.put("diaryMonth", RequestBody.create(null, diaryBean.getDiaryMonth()));

            map.put("diaryTimeStamp", RequestBody.create(null, String.valueOf(diaryBean.getDiaryTimeStamp())));
            map.put("moodReason", RequestBody.create(null, diaryBean.getMoodReason()));
            map.put("isPrivate", RequestBody.create(null, "false"));

            App.getApi().editDiaryFile(part,map).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new QObserver<Object>(mContext) {
                        @Override
                        public void next(Object avatarBean) {
                            refreshDiary();
                            //关闭界面
                            AppManager.getInstance().finishActivity(AddDiaryActivity.class);
                            finish();
                        }
                    });
        } else {
            //没有图片
            Map<String, Object> map = new HashMap<>();
            map.put("diaryId", diaryBean.getDiaryId());
            map.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
            map.put("userId", SharedPreferUtil.getInstance().getUserBean().getUserid());
            map.put("mood", diaryBean.getMood());
            map.put("diaryTimeStamp", diaryBean.getDiaryTimeStamp());
            map.put("diaryMonth", diaryBean.getDiaryMonth());
            map.put("content",  content);
            map.put("moodReason", diaryBean.getMoodReason());
            map.put("isPrivate", "false");

            App.getApi().editDiary(map).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new QObserver<Object>(mContext, true) {
                        @Override
                        public void next(Object userBean) {
                            refreshDiary();
                            AppManager.getInstance().finishActivity(AddDiaryActivity.class);
                            finish();
                        }
                    });
        }
    }


    private void refreshDiary(){
        LiveDataBus.get().with(Constants.DIARY_UPDATE).postValue(new LiveEvent(Constants.DIARY_UPDATE, ""));
    }
}