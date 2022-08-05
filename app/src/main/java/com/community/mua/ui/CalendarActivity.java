package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.adapter.AnniversaryAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.AnniversaryBean;
import com.community.mua.bean.Empty;
import com.community.mua.databinding.ActivityCalendarBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CalendarActivity extends BaseActivity<ActivityCalendarBinding> {

    private PopupWindow mPopupWindow;
    private AnniversaryAdapter mAdapter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CalendarActivity.class));
    }

    @Override
    protected ActivityCalendarBinding getViewBinding() {
        return ActivityCalendarBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("纪念日");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private List<AnniversaryBean> mAnniversaryBeans;

    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
        App.getApi().getAllAnniversary(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<List<AnniversaryBean>>(mContext) {

                    @Override
                    public void next(List<AnniversaryBean> anniversaryBeans) {
                        mAnniversaryBeans = anniversaryBeans;
                        mAdapter.setData(mAnniversaryBeans);
                    }
                });
    }

    @Override
    protected void initData() {
        mBinding.rv.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new AnniversaryAdapter(new AnniversaryAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, AnniversaryBean bean, int pos) {
                SoundUtil.getInstance().playBtnSound();
                showPopup(itemView, bean, pos);
            }
        });
        mBinding.rv.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {

    }

    public void onCreateAnniversary(View v) {
        SoundUtil.getInstance().playBtnSound();
        ModifyAnniversaryActivity.start(mContext);
    }

    public void showPopup(View itemView, AnniversaryBean bean, int pos) {
        itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = itemView.getMeasuredWidth();    //  获取测量后的宽度
        int popupHeight = itemView.getMeasuredHeight();  //获取测量后的高度

        //获取依附view的坐标
        int[] location = new int[2];
        itemView.getLocationOnScreen(location);

        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            return;
        }
        mPopupWindow = new PopupWindow();
        mPopupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.layout_popup_anniversary, null);
        mPopupWindow.setContentView(rootView);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setSplitTouchEnabled(true);
        //pop弹出后，不希望外面的控件收到点击事件 那么就设置focusable 为 true
        //这样就不会出现点击弹出控件 就会出现先popwindow 先消失 再显示
        mPopupWindow.setFocusable(true);
        rootView.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
            }
        });
        rootView.findViewById(R.id.tv_modify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                mPopupWindow.dismiss();
                modify(bean);
            }
        });
        rootView.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                mPopupWindow.dismiss();
                delete(bean, pos);
            }
        });

        mPopupWindow.showAtLocation(mBinding.root, Gravity.NO_GRAVITY, location[0] + popupWidth / 2, location[1] + 50);
    }

    private void delete(AnniversaryBean bean, int pos) {
        NiceDialog.init().setLayoutId(R.layout.dialog_delete_anniversary)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                doDelete(bean, pos);
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

    private void doDelete(AnniversaryBean bean, int pos) {
        Map<String, String> map = new HashMap<>();
        map.put("id", bean.getId());
        App.getApi().deleteAnniversary(map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Empty>(mContext) {
                    @Override
                    public void next(Empty empty) {
                        ToastUtil.show("纪念日已被删除");
                        mAnniversaryBeans.remove(bean);
                        mAdapter.notifyItemRemoved(pos);
                    }
                });
    }

    private void modify(AnniversaryBean bean) {
        ModifyAnniversaryActivity.start(mContext, bean);
    }
}
