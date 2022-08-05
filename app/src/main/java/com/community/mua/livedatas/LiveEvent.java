package com.community.mua.livedatas;

import android.text.TextUtils;


import com.community.mua.imkit.constants.EaseConstant;

import java.io.Serializable;

/**
 *
 */
public class LiveEvent implements Serializable {
    public String event;
    public String message;
    public boolean flag;

    public LiveEvent() {}

    public LiveEvent(String event) {
        this.event = event;
    }

    public LiveEvent(String event, String message) {
        this.event = event;
        this.message = message;
    }

    public LiveEvent(boolean flag) {
        this.flag = flag;
    }
}
