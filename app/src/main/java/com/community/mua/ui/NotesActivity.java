package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import com.community.mua.App;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.NoteBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityNotesBinding;
import com.community.mua.databinding.LayoutNoteBinding;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NotesActivity extends BaseActivity<ActivityNotesBinding> {
    public static void start(Context context) {
        context.startActivity(new Intent(context, NotesActivity.class));
    }

    @Override
    protected ActivityNotesBinding getViewBinding() {
        return ActivityNotesBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("便笺");
        mBinding.titleBar.tvMore.setText("查看全部");

        rotateView(mBinding.ivPink, 30.0f);
        rotateView(mBinding.ivYellow, -10.0f);

        rotateView(mBinding.lNoteFirst.root, 10f);
        rotateView(mBinding.lNoteSecond.root, -10f);

        setVisibleLy(mBinding.ivPink, false, true);
        setVisibleLy(mBinding.ivYellow, false, true);
        mBinding.ivCreate.setVisibility(View.GONE);

        setVisibleLy(mBinding.lNoteFirst.root, true, false);
        setVisibleLy(mBinding.lNoteSecond.root, true, false);

        mBinding.lNoteFirst.tvNote.setMovementMethod(ScrollingMovementMethod.getInstance());
        mBinding.lNoteSecond.tvNote.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotes();
    }

    private void getNotes() {
        Map<String, String> params = new HashMap<>();
        params.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
        App.getApi().getNoteList(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<List<NoteBean>>(mContext) {
                    @Override
                    public void next(List<NoteBean> beanList) {
                        setViewsStatus(beanList);
                    }
                });
    }

    private void setViewsStatus(List<NoteBean> beanList) {
        if (beanList != null && !beanList.isEmpty()){
            Collections.sort(beanList, new Comparator<NoteBean>() {
                @Override
                public int compare(NoteBean t0, NoteBean t1) {
                    if (t0.getToppingTimeStamp() == 0 && t1.getToppingTimeStamp() == 0) {
                        return Long.compare(t1.getNoteTimeStamp(), t0.getNoteTimeStamp());
                    }
                    return Long.compare(t1.getToppingTimeStamp(), t0.getToppingTimeStamp());
                }
            });
        }

        boolean showFirst = beanList != null && !beanList.isEmpty();
        boolean showSecond = beanList != null && beanList.size() > 1;

        setVisibleLy(mBinding.ivPink, false, !showFirst);
        setVisibleLy(mBinding.ivYellow, false, !showFirst);
        mBinding.ivCreate.setVisibility(!showFirst ? View.VISIBLE : View.GONE);
        mBinding.ivCreateRound.setVisibility(showFirst ? View.VISIBLE : View.GONE);

        mBinding.titleBar.tvMore.setVisibility(showFirst ? View.VISIBLE : View.GONE);

        setVisibleLy(mBinding.lNoteFirst.root, true, showFirst);
        setVisibleLy(mBinding.lNoteSecond.root, true, showSecond);

        if (showFirst) {
            setNote(mBinding.lNoteFirst, beanList.get(0));
        }

        if (showSecond) {
            setNote(mBinding.lNoteSecond, beanList.get(1));
        }
    }

    private void setVisibleLy(View view, boolean isNote, boolean b) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.height = b ? DisplayUtil.dp2px(mContext, isNote ? 206 : 100) : 0;
        view.setLayoutParams(params);
    }

    private void setNote(LayoutNoteBinding noteBinding, NoteBean bean) {
        List<UserBean> userRsp = SharedPreferUtil.getInstance().getPairBean().getUserRsp();
        UserBean uBean = SharedPreferUtil.getInstance().getUserBean();
        for (UserBean userBean : userRsp) {
            if (TextUtils.equals(userBean.getUserid(), bean.getUserId())) {
                uBean = userBean;
                break;
            }
        }
        noteBinding.tvNote.setText(bean.getContent());
        noteBinding.tvTime.setText(DateUtil.stampToDay(bean.getNoteTimeStamp()) + " " + DateUtil.getWeek(bean.getNoteTimeStamp()));
        UserUtils.setUserInfo(mContext, noteBinding.tvName, true, noteBinding.flAvatar, noteBinding.ivAvatar, uBean);
    }

    @Override
    protected void initListener() {
        mBinding.titleBar.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                AllNotesActivity.start(mContext);
            }
        });

        LiveDataBus.get().with(Constants.NOTE_UPDATE, LiveEvent.class).observe(this, this::onNoteUpdate);
    }

    private void onNoteUpdate(LiveEvent event) {
        ToastUtil.show("对方更新了便签");
        getNotes();
    }

    public void onCreateNotice(View v) {
        SoundUtil.getInstance().playBtnSound();
        CreateNoteActivity.start(mContext);
    }

    private void rotateView(View v, float rotate) {
        RotateAnimation rotateSensor = new RotateAnimation(0f, rotate, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateSensor.setDuration(10);//设置动画持续周期
        rotateSensor.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        v.startAnimation(rotateSensor);
    }
}
