package com.community.mua.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.AvatarBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityModifyInfoBinding;
import com.community.mua.imkit.utils.EaseFileUtils;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.services.QObserver;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.TakeImageUtils;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class ModifyInfoActivity extends BaseActivity<ActivityModifyInfoBinding> {

    private File imageCropFile;

    public static void start(Activity context) {
        context.startActivityForResult(new Intent(context, ModifyInfoActivity.class), 1001);
    }

    private static final int PERMISSION_CODE_CAMERA = 1001;
    private static final int PERMISSION_CODE_GALLERY = 1002;


    private Uri mPicUri;

    @Override
    protected ActivityModifyInfoBinding getViewBinding() {
        return ActivityModifyInfoBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("恋爱信息");
    }

    @Override
    protected void initData() {
        UserBean mine = SharedPreferUtil.getInstance().getUserBean();
        UserUtils.setAvatar(mContext, mine, mBinding.ivAvatarMine);
        mBinding.tvNameMine.setText(mine.getNickname());
        mBinding.tvBirthMine.setText(mine.getBirth());
        mBinding.tvGenderMine.setText(mine.getGender() == 0 ? "男生" : "女生");
        mBinding.ivGenderMine.setBackgroundResource(mine.getGender() == 0 ? R.drawable.male_small : R.drawable.female_small);
        mBinding.flAvatarMine.setBackgroundResource(mine.getGender() == 0 ? R.drawable.sp_ff007a : R.drawable.sp_00ffe0);


        UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
        UserUtils.setAvatar(mContext, taBean, mBinding.ivAvatarTa);
        mBinding.tvNameTa.setText(taBean.getNickname());
        mBinding.tvBirthTa.setText(taBean.getBirth());
        mBinding.tvGenderTa.setText(taBean.getGender() == 0 ? "男生" : "女生");
        mBinding.ivGenderTa.setBackgroundResource(taBean.getGender() == 0 ? R.drawable.male_small : R.drawable.female_small);
        mBinding.flAvatarMine.setBackgroundResource(taBean.getGender() == 0 ? R.drawable.sp_ff007a : R.drawable.sp_00ffe0);
    }

    @Override
    protected void initListener() {

        mBinding.flAvatarMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAvatar();
            }
        });
        mBinding.tvNameMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNickName();
            }
        });
        mBinding.ivNameMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNickName();
            }
        });

        LiveDataBus.get().with(Constants.AVATAR_UPDATE, LiveEvent.class).observe(this, this::onAvatarUpdate);
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

    private void onAvatarUpdate(LiveEvent event) {
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        UserUtils.setAvatar(mContext, userBean, mBinding.ivAvatarMine);
    }

    public void updateAvatar() {
        NiceDialog.init().setLayoutId(R.layout.dialog_avatar_edit)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.getView(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismissAllowingStateLoss();
                                onPhotoClick();
                            }
                        });
                        holder.getView(R.id.tv_gallery).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismissAllowingStateLoss();
                                onGalleryClick();
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }

                })
                .setAnimStyle(R.style.BottomAnimation)
                .setMargin(DisplayUtil.dp2px(mContext, 5))
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }

    private void onPhotoClick() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doTakePhoto();
        } else {
            EasyPermissions.requestPermissions(mContext, "需要拍照、文件存储权限", PERMISSION_CODE_CAMERA, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void onGalleryClick() {
        if (EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            doOpenGallery();
        } else {
            EasyPermissions.requestPermissions(mContext, "需要文件存储权限", PERMISSION_CODE_GALLERY, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void doTakePhoto() {
        mPicUri = TakeImageUtils.getInstance().doTakePhoto(mContext);
    }

    private void doOpenGallery() {
        TakeImageUtils.getInstance().doOpenGallery(mContext);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TakeImageUtils.REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            imageCropFile = TakeImageUtils.getInstance().cropPhoto(mContext, mPicUri, 300, 300);
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_CAMERA && resultCode == RESULT_CANCELED) {
            System.out.println("拍照取消");
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                imageCropFile = TakeImageUtils.getInstance().cropPhoto(mContext, data.getData(), 300, 300);
            }
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_CROP && resultCode == RESULT_OK) {
            if (imageCropFile != null && imageCropFile.getAbsolutePath() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (EaseFileUtils.uri != null) {
                        // 通过存储的uri 查询File
                        imageCropFile = EaseFileUtils.getCropFile(this, EaseFileUtils.uri);
                        uploadPic();
                    }
                } else {
                    uploadPic();
                }
            }
        }
    }

    private void uploadPic() {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageCropFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("headImg", imageCropFile.getName(), requestFile);
        Map<String, RequestBody> map = new HashMap<>();
        map.put("userId", RequestBody.create(null, SharedPreferUtil.getInstance().getUserBean().getUserid()));

        App.getApi().uploadAvatar(part, map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<AvatarBean>(mContext) {
                    @Override
                    public void next(AvatarBean avatarBean) {
                        saveAvatarPath(avatarBean);
                    }
                });
    }


    private void saveAvatarPath(AvatarBean avatarBean) {
        UserUtils.saveAvatar(avatarBean.getAvatar());
        UserUtils.sendCmdMsg(Constants.TA_AVATAR_UPDATE, avatarBean.getAvatar());
        LiveDataBus.get().with(Constants.AVATAR_UPDATE).postValue(new LiveEvent(avatarBean.getAvatar()));
    }

    private void editNickName() {
        NiceDialog.init().setLayoutId(R.layout.dialog_nickname_edit)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        EditText etName = holder.getView(R.id.et_name);
                        ImageView ivClear = holder.getView(R.id.iv_clear);
                        etName.setText(mBinding.tvNameMine.getText().toString());
                        etName.post(new Runnable() {
                            @Override
                            public void run() {
                                showKeyboard(etName);
                            }
                        });

                        etName.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                String s = etName.getText().toString().trim();
                                ivClear.setVisibility(TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
                            }
                        });

                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = etName.getText().toString();
                                if (TextUtils.isEmpty(name)) {
                                    return;
                                }

                                mBinding.tvNameMine.setText(name);
                                dialog.dismissAllowingStateLoss();
                                modifyName(name);
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismissAllowingStateLoss();
                            }
                        });

                        ivClear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                etName.setText("");
                            }
                        });
                    }

                })
                .setMargin(DisplayUtil.dp2px(mContext, 10))
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }


    private void modifyName(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("nickName", name);
        params.put("userId", SharedPreferUtil.getInstance().getUserBean().getUserid());
        App.getApi().updateNickName(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Object>(mContext, true) {
                    @Override
                    public void next(Object obj) {
                        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
                        userBean.setNickname(name);
                        SharedPreferUtil.getInstance().setUserBean(userBean);
                        UserUtils.sendCmdMsg(Constants.TA_NICKNAME_UPDATE, name);
                    }
                });

    }
}