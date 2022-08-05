package com.community.mua.utils;

import android.app.Application;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.StringRes;

import com.community.mua.App;
import com.community.mua.R;
import com.hjq.toast.ToastUtils;

public class ToastUtil {

    public static void show(String msg) {
        showMsg(msg);
    }

    public static void show(@StringRes int resId) {
        String msg = App.getContext().getString(resId);
        showMsg(msg);
    }

    public static void show(@StringRes int resId, Object... formatArgs) {
        String msg = App.getContext().getString(resId, formatArgs);
        showMsg(msg);
    }

    private static void showMsg(String msg) {
        if (!TextUtils.isEmpty(msg) && !msg.equalsIgnoreCase("null")) {
            ToastUtils.setView(LayoutInflater.from(App.getContext()).inflate(R.layout.item_toast, null, false));
            ToastUtils.show(msg);
        }
    }
}

