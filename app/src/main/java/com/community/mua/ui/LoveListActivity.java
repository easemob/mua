package com.community.mua.ui;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.adapter.LoveListAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.AvatarBean;
import com.community.mua.bean.Empty;
import com.community.mua.bean.LoveCoverBean;
import com.community.mua.bean.LoveListBean;
import com.community.mua.bean.NoteBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityLoveListBinding;
import com.community.mua.imkit.utils.EaseCompat;
import com.community.mua.imkit.utils.EaseFileUtils;
import com.community.mua.services.QObserver;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.TakeImageUtils;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;

import java.io.File;
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

public class LoveListActivity extends BaseActivity<ActivityLoveListBinding> implements EasyPermissions.PermissionCallbacks {

    private LoveListAdapter mAdapter;
    private static final int PERMISSION_CODE_CAMERA = 1001;
    private static final int PERMISSION_CODE_GALLERY = 1002;
    private Uri mPicUri;
    private File imageCropFile;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoveListActivity.class));
    }

    @Override
    protected ActivityLoveListBinding getViewBinding() {
        return ActivityLoveListBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        initDefaultView();
    }

    private void initDefaultView() {
        View flTop = findViewById(R.id.fl_top);
        if (flTop != null) {
            int statusBarHeight = DisplayUtil.getStatusBarHeight(mContext);
            flTop.setPadding(20, statusBarHeight + 10, 20, 20);
        }
    }

    @Override
    protected void initData() {
        initRv();
    }

    private int mCurrentPos;

    private void initRv() {
        mBinding.rv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new LoveListAdapter(new LoveListAdapter.OnEnterClickListener() {

            @Override
            public void onEnter(int pos) {
                mCurrentPos = pos;
                onEnterDetail();
            }
        });
        mBinding.rv.setAdapter(mAdapter);
    }

    private ViewHolder mDetailHolder;
    private BaseNiceDialog mCurrentDialog;

    private void onEnterDetail() {
        SoundUtil.getInstance().playBtnSound();
        mCurrentDialog = NiceDialog.init().setLayoutId(R.layout.dialog_love_list_detail)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        mDetailHolder = holder;
                        setDialogView();
                    }
                })
                .setAnimStyle(R.style.BottomAnimation)
                .setOutCancel(true)
                .setShowBottom(true);

        mCurrentDialog.show(getSupportFragmentManager());
    }

    private void setDialogView() {
        LoveListBean bean = SharedPreferUtil.getInstance().getLoveListBean(mCurrentPos);
        boolean finish = bean.isFinish();
        mDetailHolder.getView(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();

                if (finish) {
                    onDeleteClick();
                    return;
                }

                mCurrentDialog.dismissAllowingStateLoss();
            }
        });

        ImageView ivForward = mDetailHolder.getView(R.id.iv_forward);
        ivForward.setVisibility(finish ? View.GONE : View.VISIBLE);
        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                UserUtils.sendCustomMsg(mCurrentPos, getTxt(false));
            }
        });

        ImageView ivCover = mDetailHolder.getView(R.id.iv_cover);
        String photo = bean.getPhoto();
        if (!TextUtils.isEmpty(photo)) {
            Glide.with(mContext).load(Constants.HTTP_HOST + photo).into(ivCover);
        }

        mDetailHolder.getView(R.id.ll_unfinished).setVisibility(finish ? View.GONE : View.VISIBLE);

        ImageView ivChange = mDetailHolder.getView(R.id.iv_change);
        ivChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                onPhotoChange();
            }
        });

        mDetailHolder.getView(R.id.iv_finished).setVisibility(finish ? View.VISIBLE : View.GONE);

        TextView tvTitle = mDetailHolder.getView(R.id.tv_title);
        tvTitle.setText(bean.getTitle());

        TextView tvSub = mDetailHolder.getView(R.id.tv_sub);
        tvSub.setText(bean.getSubTitle());

        ImageView ivEdit = mDetailHolder.getView(R.id.iv_edit);
        ivEdit.setVisibility(finish ? View.GONE : View.VISIBLE);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                onEdit(tvSub);
            }
        });

        TextView tvTime = mDetailHolder.getView(R.id.tv_time);
        tvTime.setText(finish ? DateUtil.stampToDateOther(bean.getTime()) : DateUtil.getTimeOther());
        tvTime.setGravity(finish ? Gravity.CENTER : Gravity.START);

        ImageView ivTime = mDetailHolder.getView(R.id.iv_time);
        ivTime.setVisibility(finish ? View.GONE : View.VISIBLE);

        ImageView ivDone = mDetailHolder.getView(R.id.iv_done);
        ivDone.setVisibility(finish ? View.GONE : View.VISIBLE);
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                if (imageCropFile == null) {
                    ToastUtil.show("请上传照片");
                    return;
                }

                mCurrentDialog.dismissAllowingStateLoss();

                uploadPic(bean, tvSub);
            }
        });

        LinearLayout llBottom = mDetailHolder.getView(R.id.ll_bottom);
        llBottom.setPadding(0, DisplayUtil.dp2px(mContext, finish ? 0f : 16f), 0, 0);
    }

    private String getTxt(boolean finish) {
        if (mCurrentPos == 0) {
            return finish?"宝，我完成了和你一起看电影，来看看吧":"宝，我想和你完成一起看电影！";
        }
        if (mCurrentPos == 1) {
            return finish?"宝，我完成了和你一起做饭，来看看吧":"宝，我想和你完成一起做饭！";
        }
        return finish?"宝，我完成了和你一起写一封情书，来看看吧":"宝，我想和你完成一起写一封情书！";
    }

    private void onDeleteClick() {
        NiceDialog.init().setLayoutId(R.layout.dialog_delete_love_list)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                delete();
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
                .setMargin(DisplayUtil.dp2px(mContext, 10))
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    private void delete() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", SharedPreferUtil.getInstance().getUserBean().getUserid());
        params.put("position", mCurrentPos);
        App.getApi().deleteLoveCover(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Empty>(mContext) {
                    @Override
                    public void next(Empty empty) {
                        ToastUtil.show("已删除");
                        mCurrentDialog.dismissAllowingStateLoss();
                        SharedPreferUtil.getInstance().clearLoveListItem(mCurrentPos);
                        mAdapter.notifyItemChanged(mCurrentPos);
                        clear();
                    }
                });
    }

    private void uploadPic(LoveListBean loveListBean, TextView tvSub) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageCropFile);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", imageCropFile.getName(), requestFile);
        Map<String, RequestBody> map = new HashMap<>();
        map.put("userId", RequestBody.create(null, SharedPreferUtil.getInstance().getUserBean().getUserid()));
        map.put("position", RequestBody.create(null, String.valueOf(mCurrentPos)));

        App.getApi().uploadLoveCover(part, map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<LoveCoverBean>(mContext) {
                    @Override
                    public void next(LoveCoverBean bean) {
                        ToastUtil.show("打卡成功！");

                        UserUtils.sendCustomMsg(mCurrentPos, getTxt(true));

                        loveListBean.setTime(System.currentTimeMillis());
                        loveListBean.setSubTitle(tvSub.getText().toString());
                        loveListBean.setPhoto(bean.getImgUrl());
                        loveListBean.setFinish(true);
                        SharedPreferUtil.getInstance().setLoveList(mCurrentPos, loveListBean);
                        mAdapter.notifyItemChanged(mCurrentPos);

                        clear();
                    }
                });
    }

    private void clear() {
        mCurrentDialog = null;
        mCurrentPos = 0;
        mDetailHolder = null;

        mPicUri = null;
        imageCropFile = null;
    }

    public void onPhotoChange() {
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


    public void onEdit(TextView tvSub) {
        NiceDialog.init().setLayoutId(R.layout.dialog_love_list_sub)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        String sub = tvSub.getText().toString();
                        EditText etName = holder.getView(R.id.et_name);
                        ImageView ivClear = holder.getView(R.id.iv_clear);
                        ivClear.setVisibility(TextUtils.isEmpty(sub) ? View.GONE : View.VISIBLE);
                        etName.setText(sub);
                        etName.setSelection(sub.length());
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
                                tvSub.setText(name);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TakeImageUtils.REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            imageCropFile = TakeImageUtils.getInstance().cropPhoto(mContext, mPicUri, DisplayUtil.getWidth(mContext), DisplayUtil.getWidth(mContext));
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
                        coverChange();
                    }
                } else {
                    coverChange();
                }
            }
        }
    }

    private void coverChange() {
        try {
            Uri uriForFile = EaseCompat.getUriForFile(mContext, imageCropFile);
            InputStream is = getContentResolver().openInputStream(uriForFile);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            ImageView ivCover = mDetailHolder.getView(R.id.iv_cover);
            ivCover.setImageBitmap(bitmap);

            ImageView ivChange = mDetailHolder.getView(R.id.iv_change);
            ivChange.setImageResource(R.mipmap.change_cover);
            TextView tvTips = mDetailHolder.getView(R.id.tv_tips);
            tvTips.setText("更换照片");
        } catch (Exception e) {
            ToastUtil.show(e.getMessage());
        }
    }

    @Override
    protected void initListener() {

    }
}
