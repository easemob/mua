package com.community.mua.imkit.interfaces;

public interface OnCallBack<T> {
    void onSuccess(T models);
    void onError(int code, String error);
}
