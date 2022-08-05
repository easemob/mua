package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.adapter.NoteAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.Empty;
import com.community.mua.bean.NoteBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityAllNotesBinding;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ThreadManager;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AllNotesActivity extends BaseActivity<ActivityAllNotesBinding> {

    private NoteAdapter mAdapter;
    private UserBean mTaBean;
    private LinearLayoutManager mLayoutManager;

    public static void start(Context context) {
        context.startActivity(new Intent(context, AllNotesActivity.class));
    }

    @Override
    protected ActivityAllNotesBinding getViewBinding() {
        return ActivityAllNotesBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotes();
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("全部便笺");
    }

    @Override
    protected void initData() {
        mTaBean = SharedPreferUtil.getInstance().getTaBean();
        initRv();
    }

    private void initRv() {
        mLayoutManager = new LinearLayoutManager(mContext);
        mBinding.rv.setLayoutManager(mLayoutManager);
        mAdapter = new NoteAdapter(mContext, new NoteAdapter.OnTrashListener() {
            @Override
            public void onDelete(NoteBean bean, int pos) {
                onDeleteClick(bean, pos);
            }

            @Override
            public void onTopping(NoteBean bean, int pos) {
                onToppingClick(bean, pos);
            }
        });
        mBinding.rv.setAdapter(mAdapter);
    }

    private void onToppingClick(NoteBean bean, int pos) {
        boolean is2Top = bean.getToppingTimeStamp() == 0;
        long time = System.currentTimeMillis();
        SoundUtil.getInstance().playBtnSound();
        Map<String, Object> params = new HashMap<>();
        params.put("noteId", bean.getNoteId());
        params.put("time", is2Top ? String.valueOf(time) : "0");
        App.getApi().updateToppingTime(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Empty>(mContext) {
                    @Override
                    public void next(Empty empty) {
                        if (is2Top) {
                            mNoteList.get(pos).setToppingTimeStamp(time);
                            Collections.swap(mNoteList, pos, 0);
                            sortList();
                            mAdapter.notifyItemMovedAll(pos,0);
                        } else {
                            mNoteList.get(pos).setToppingTimeStamp(0);
                            Collections.swap(mNoteList, pos, mNoteList.size() - 1);
                            sortList();
                            mAdapter.notifyItemMovedAll(pos,mNoteList.size() - 1);
                        }

                        mBinding.rv.post(new Runnable() {
                            @Override
                            public void run() {
                                mLayoutManager.scrollToPosition(0);
                            }
                        });

                        UserUtils.sendCmdMsg(Constants.NOTE_UPDATE, Constants.NOTE_UPDATE);
                    }
                });
    }

    private void onDeleteClick(NoteBean bean, int pos) {
        SoundUtil.getInstance().playBtnSound();
        NiceDialog.init().setLayoutId(R.layout.dialog_delete_note)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                delete(bean, pos);
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setMargin(DisplayUtil.dp2px(mContext, 10))
                .setOutCancel(false)
                .show(getSupportFragmentManager());


    }

    private void delete(NoteBean bean, int pos) {
        Map<String, String> params = new HashMap<>();
        params.put("noteId", bean.getNoteId());
        App.getApi().deleteNote(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Empty>(mContext) {
                    @Override
                    public void next(Empty empty) {
                        mNoteList.remove(pos);
                        mAdapter.notifyItemRemovedAll(pos);
                        UserUtils.sendCmdMsg(Constants.NOTE_UPDATE, Constants.NOTE_UPDATE);
                    }
                });
    }

    @Override
    protected void initListener() {
        LiveDataBus.get().with(Constants.NOTE_UPDATE, LiveEvent.class).observe(this, this::onNoteUpdate);
    }

    private void onNoteUpdate(LiveEvent event) {
        ToastUtil.show("对方更新了便签");
        getNotes();
    }

    private List<NoteBean> mNoteList;

    private void getNotes() {
        Map<String, String> params = new HashMap<>();
        params.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
        App.getApi().getNoteList(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<List<NoteBean>>(mContext) {

                    @Override
                    public void next(List<NoteBean> beanList) {
                        if (beanList != null && !beanList.isEmpty()) {
                            mNoteList = beanList;
                        }
                        sortList();
                        mAdapter.setData(mNoteList);
                    }
                });
    }

    private void sortList() {
        Collections.sort(mNoteList, new Comparator<NoteBean>() {
            @Override
            public int compare(NoteBean t0, NoteBean t1) {
                if (t0.getToppingTimeStamp() == 0 && t1.getToppingTimeStamp() == 0) {
                    return Long.compare(t1.getNoteTimeStamp(), t0.getNoteTimeStamp());
                }
                return Long.compare(t1.getToppingTimeStamp(), t0.getToppingTimeStamp());
            }
        });
    }

    public void onCreateNotice(View v) {
        SoundUtil.getInstance().playBtnSound();
        CreateNoteActivity.start(mContext);
    }
}
