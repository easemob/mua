package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.community.mua.App;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.Empty;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityCreateNoteBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateNoteActivity extends BaseActivity<ActivityCreateNoteBinding> {

    private String mContent;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CreateNoteActivity.class));
    }

    @Override
    protected ActivityCreateNoteBinding getViewBinding() {
        return ActivityCreateNoteBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvMore.setText("保存");

        mBinding.tvNote.setVisibility(View.GONE);
        mBinding.tvUserName.setVisibility(View.GONE);
        mBinding.tvTime.setVisibility(View.GONE);
        mBinding.etNote.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        UserUtils.setUserInfo(mContext, null, mBinding.flAvatar, mBinding.ivAvatar, userBean);
    }

    @Override
    protected void initListener() {
        mBinding.etNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mContent = mBinding.etNote.getText().toString();
                mBinding.titleBar.tvMore.setVisibility(TextUtils.isEmpty(mContent) ? View.GONE : View.VISIBLE);
            }
        });
        mBinding.titleBar.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                Map<String, String> params = new HashMap<>();
                params.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
                params.put("content", mContent);
                params.put("userId", SharedPreferUtil.getInstance().getUserBean().getUserid());
                params.put("toppingTime",mBinding.swTop.isChecked()?String.valueOf(System.currentTimeMillis()):"0");

                App.getApi().createNote(params).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new QObserver<Empty>(mContext) {
                            @Override
                            public void next(Empty empty) {
                                UserUtils.sendCmdMsg(Constants.NOTE_UPDATE, Constants.NOTE_UPDATE);
                                ToastUtil.show("创建成功");
                                finish();
                            }
                        });
            }
        });
    }
}
