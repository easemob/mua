package com.community.mua.views.window;

import android.view.View;

public interface IFloatingView {

    void show();
    void hide();

    void setOnClickListener(DkFloatingView.ViewClickListener listener);

    View getView();
}
