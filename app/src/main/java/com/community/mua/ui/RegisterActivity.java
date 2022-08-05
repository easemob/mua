package com.community.mua.ui;

import static com.community.mua.utils.TimeUtils.getTime;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.AppManager;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.AvatarBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityRegisterBinding;
import com.community.mua.imkit.utils.EaseCompat;
import com.community.mua.imkit.utils.EaseFileUtils;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.LoadingUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.TakeImageUtils;
import com.community.mua.utils.ThreadManager;
import com.community.mua.utils.ToastUtil;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMUserInfo;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> implements EasyPermissions.PermissionCallbacks {
    private static final int PERMISSION_CODE_CAMERA = 1001;
    private static final int PERMISSION_CODE_GALLERY = 1002;

    private boolean isMale = true;
    private TimePickerView pvTime;
    private Uri mPicUri;
    private File imageCropFile;
    private String mAvatarUrl;

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected ActivityRegisterBinding getViewBinding() {
        return ActivityRegisterBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initListener() {
        mBinding.cbMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                isMale = mBinding.cbMale.isChecked();
                switchGender();
            }
        });
        mBinding.cbFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                isMale = !mBinding.cbFemale.isChecked();
                switchGender();
            }
        });
    }

    private void switchGender() {
        mBinding.cbMale.setEnabled(!isMale);
        mBinding.cbFemale.setEnabled(isMale);

        mBinding.cbMale.setChecked(isMale);
        mBinding.cbFemale.setChecked(!isMale);
    }

    public void onNicknameEdit(View v) {
        SoundUtil.getInstance().playBtnSound();
        NiceDialog.init().setLayoutId(R.layout.dialog_nickname_edit)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        EditText etName = holder.getView(R.id.et_name);
                        ImageView ivClear = holder.getView(R.id.iv_clear);
                        etName.setText(mBinding.tvName.getText().toString());
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
                                SoundUtil.getInstance().playBtnSound();
                                String name = etName.getText().toString();
                                if (TextUtils.isEmpty(name)) {
                                    return;
                                }
                                mBinding.tvName.setText(name);
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });

                        ivClear.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                etName.setText("");
                            }
                        });
                    }

                })
                .setMargin(DisplayUtil.dp2px(mContext, 10))
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    public void onAvatarChange(View v) {
        SoundUtil.getInstance().playBtnSound();
        NiceDialog.init().setLayoutId(R.layout.dialog_avatar_edit)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.getView(R.id.tv_photo).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                onPhotoClick();
                            }
                        });
                        holder.getView(R.id.tv_gallery).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                onGalleryClick();
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
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
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if (requestCode == PERMISSION_CODE_CAMERA) {
            doTakePhoto();
        } else if (requestCode == PERMISSION_CODE_GALLERY) {
            doOpenGallery();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        ToastUtil.show("需要权限");
    }

    public void onNext(View v) {
        SoundUtil.getInstance().playBtnSound();
        String name = mBinding.tvName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show("请输入昵称");
            return;
        }

        String birth = mBinding.txtBirth.getText().toString();
        if (TextUtils.isEmpty(birth)) {
            ToastUtil.show("请输入生日");
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("nickname", name);
        params.put("avatar", mAvatarUrl);
        params.put("gender", isMale ? "0" : "1");
        params.put("birth", birth);
        App.getApi().saveAccount(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<UserBean>(mContext, false) {
                    @Override
                    public void next(UserBean userBean) {
                        loginIm(userBean);
                    }
                });
    }

    private void loginIm(UserBean userBean) {
        SharedPreferUtil.getInstance().setUserBean(userBean);
        EMClient.getInstance().login(userBean.getChatId(), Constants.IM_PSW, new EMCallBack() {
            @Override
            public void onSuccess() {
                setImUserInfo(userBean);
            }

            @Override
            public void onError(int i, String s) {
                ToastUtil.show(s);
                LoadingUtil.getInstance().dismissDialog();
            }
        });
    }

    private void setImUserInfo(UserBean userBean) {
        EMUserInfo userInfo = new EMUserInfo();
        userInfo.setNickname(userBean.getNickname());
        userInfo.setAvatarUrl(userBean.getAvatar());
        userInfo.setBirth(userBean.getBirth());
        userInfo.setGender(userBean.getGender());

        EMClient.getInstance().userInfoManager().updateOwnInfo(userInfo, new EMValueCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                ThreadManager.getInstance().runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadingUtil.getInstance().dismissDialog();
                        AppManager.getInstance().finishActivity(RegisterActivity.class);
                        PairActivity.start(mContext);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                ToastUtil.show(s);
                LoadingUtil.getInstance().dismissDialog();
            }
        });
    }


    public void onBirthDayEdit(View v) {
        SoundUtil.getInstance().playBtnSound();
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);

        Calendar endDate = Calendar.getInstance();
        //时间选择器
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                mBinding.txtBirth.setText(getTime(date));
            }
        }).setLayoutRes(R.layout.dialog_birthday_edit, new CustomListener() {

            @Override
            public void customLayout(View v) {
                Button tvSubmit = v.findViewById(R.id.btn_confirm);
                Button ivCancel = v.findViewById(R.id.btn_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundUtil.getInstance().playBtnSound();
                        pvTime.returnData();
                        pvTime.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundUtil.getInstance().playBtnSound();
                        pvTime.dismiss();
                    }
                });
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
//                .setDividerColor(Color.DKGRAY)
                .setContentTextSize(20)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .isDialog(true)
//                .setDecorView(mBinding.llDetail)//非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
                .setOutSideColor(0x00000000)
                .setOutSideCancelable(false)
                .setGravity(Gravity.CENTER)
                .setTextColorCenter(0xFF294DCA)
                .setTextColorOut(0x66000000)
                .setLineSpacingMultiplier(1.7f)
                .isCenterLabel(false)
                .build();

        pvTime.setKeyBackCancelable(false);//系统返回键监听屏蔽掉
        pvTime.show(v, true);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TakeImageUtils.REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            imageCropFile = TakeImageUtils.getInstance().cropPhoto(mContext,mPicUri,300,300);
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_CAMERA && resultCode == RESULT_CANCELED) {
            System.out.println("拍照取消");
        } else if (requestCode == TakeImageUtils.REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                imageCropFile = TakeImageUtils.getInstance().cropPhoto(mContext,data.getData(),300,300);
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
        map.put("userId", RequestBody.create(null, ""));

        App.getApi().uploadAvatar(part,map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<AvatarBean>(mContext) {
                    @Override
                    public void next(AvatarBean avatarBean) {
                        saveAvatarPath(avatarBean);
                    }
                });
    }

    private void saveAvatarPath(AvatarBean avatarBean) {
        mAvatarUrl = avatarBean.getAvatar();
        try {
            mBinding.ivCamera.setVisibility(View.GONE);
            Uri uriForFile = EaseCompat.getUriForFile(mContext, imageCropFile);
            InputStream is = getContentResolver().openInputStream(uriForFile);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            mBinding.ivAvatar.setImageBitmap(bitmap);
        } catch (Exception e) {
            ToastUtil.show(e.getMessage());
        }
    }
}
