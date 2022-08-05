package com.community.mua.base;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewbinding.ViewBinding;

import com.community.mua.R;
import com.community.mua.callkit.EaseCallKit;
import com.community.mua.callkit.base.EaseCallFloatWindow;
import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.callkit.utils.EaseCallState;
import com.community.mua.common.Constants;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.ui.call.VideoCallActivity;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.StatusBarUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.window.FloatWindow;
import com.hyphenate.chat.EMClient;

import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    protected VB mBinding;

    protected BaseActivity mContext;
    private Fragment mCurrentFragment;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucentFullScreen(this, null);
        if (isLightMode()) StatusBarUtil.setLightMode(this);

        mBinding = getViewBinding();
        setContentView(mBinding.getRoot());
        mContext = this;

        initDefaultView();

        initView();
        initData();
        initListener();

        AppManager.getInstance().addActivity(this);

        LiveDataBus.get().with(Constants.RECEIVE_MSG, LiveEvent.class).observe(this, this::onReceiveMsg);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void onReceiveMsg(LiveEvent event) {
        View floatView = FloatWindow.get().getView();
        TextView tvUnread = floatView.findViewById(R.id.tv_unread);
        UserUtils.setShowNum(tvUnread, EMClient.getInstance().chatManager().getUnreadMessageCount());
    }

    private void initDefaultView() {
        View titleBar = findViewById(R.id.title_bar);
        if (titleBar != null) {
            int statusBarHeight = DisplayUtil.getStatusBarHeight(mContext);
            titleBar.setPadding(20, statusBarHeight + 10, 20, 20);
        }

        View ivBack = findViewById(R.id.iv_back);
        if (ivBack != null) {
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SoundUtil.getInstance().playBtnSound();
                    finish();
                }
            });
        }

        View ivMore = findViewById(R.id.iv_more);
        if (ivMore != null) {
            ivMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SoundUtil.getInstance().playBtnSound();
                    onMore();
                }
            });
        }
    }

    protected void onMore() {

    }

    protected boolean isLightMode() {
        return true;
    }

    protected abstract VB getViewBinding();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListener();

    public void onBack(View view) {
        SoundUtil.getInstance().playBtnSound();
        this.onBackPressed();
    }

    protected void showKeyboard(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    protected void replace(Fragment fragment, String tag) {
        if (mCurrentFragment != fragment) {
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if (!fragment.isAdded()) {
                t.add(R.id.fcv, fragment, tag).show(fragment).commit();
            } else {
                t.show(fragment).commit();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        if(EaseCallKit.getInstance().getCallState() != EaseCallState.CALL_IDLE && !EaseCallFloatWindow.getInstance(mContext).isShowing()){
//            if(EaseCallKit.getInstance().getCallType() == EaseCallType.CONFERENCE_CALL){
//                Intent intent = new Intent(mContext, MultipleVideoActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//            }else{
                Intent intent = new Intent(mContext, VideoCallActivity.class).addFlags(FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
//            }
        }
    }
}