package com.community.mua.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.widget.ImageView;

import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;

public class LoadingUtil {
    private static final LoadingUtil ourInstance = new LoadingUtil();
    private BaseActivity mContext;
    private BaseNiceDialog mDialog;

    public static LoadingUtil getInstance() {
        return ourInstance;
    }

    private LoadingUtil() {
    }

    public void init(BaseActivity context){
        mContext = context;
        mDialog = NiceDialog.init().setLayoutId(R.layout.dialog_loading)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        ImageView ivLoading = holder.getView(R.id.iv_loading);
                        AnimationDrawable ad = (AnimationDrawable) ivLoading.getDrawable();
                        ad.start();
                    }
                })
                .useAnim(false)
                .setMargin(DisplayUtil.dp2px(mContext, 10))
                .setOutCancel(false);
    }

    public void showDialog(){
        if (mDialog != null) mDialog.show(mContext.getSupportFragmentManager());
    }

    public void dismissDialog(){
        if (mDialog != null) {
            new Handler().postDelayed(() -> mDialog.dismissAllowingStateLoss(), 1000);
        }
    }
}
