package com.community.mua.views.window;

import android.view.MotionEvent;

public interface MagnetViewListener {

    void onRemove(FloatingMagnetView magnetView);

    void onClick(MotionEvent event);

    void onMove2Edge(boolean isLeft);
}
