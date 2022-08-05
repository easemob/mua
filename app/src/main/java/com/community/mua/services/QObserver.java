package com.community.mua.services;


import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.community.mua.base.BaseActivity;
import com.community.mua.bean.CommonBean;
import com.community.mua.utils.GsonUtils;
import com.community.mua.utils.LoadingUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.views.dialog.BaseNiceDialog;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class QObserver<T> implements Observer<CommonBean<T>> {
    private BaseActivity mContext;
    private boolean isDismissDialogAfter = true;
    private boolean mShowError = true;

    public QObserver(BaseActivity context) {
        mContext = context;
        initDialog();
    }

    public QObserver(BaseActivity context, boolean dismissDialogAfter) {
        mContext = context;
        isDismissDialogAfter = dismissDialogAfter;
        initDialog();
    }

    public QObserver(BaseActivity context, boolean dismissDialogAfter, boolean showError) {
        mContext = context;
        isDismissDialogAfter = dismissDialogAfter;
        mShowError = showError;
        initDialog();
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        start();
        LoadingUtil.getInstance().showDialog();
    }

    @Override
    public void onNext(@NonNull CommonBean<T> commonBean) {
        if (TextUtils.equals(commonBean.getCode(), "0000")) {
            Log.e("tagqi", "onNext: " + GsonUtils.toJson(commonBean));
            next(commonBean.getData());
        } else {
            if (mShowError) ToastUtil.show(commonBean.getMsg());
            Log.e("tagqi", "error: " + commonBean.getMsg());
            error(commonBean.getCode(), commonBean.getMsg());
        }
        dismissDialog();
    }

    @Override
    public void onError(@NonNull Throwable e) {
        Log.e("tagqi", "error: " + e.getMessage());
        error("xxxx", e.getMessage());
        if (mShowError) ToastUtil.show(e.getMessage());
        dismissDialog();
    }

    private void dismissDialog() {
        if (!isDismissDialogAfter) {
            return;
        }
        LoadingUtil.getInstance().dismissDialog();
    }

    @Override
    public void onComplete() {
        Log.e("tagqi", "onComplete: ");
        complete();
        dismissDialog();
    }

    private void initDialog() {
        LoadingUtil.getInstance().init(mContext);
    }

    protected void start() {
    }

    public abstract void next(T t);

    public void error(String code, String msg) {

    }

    public void complete() {

    }
}
