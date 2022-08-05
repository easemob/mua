package com.community.mua.ui;

import static com.community.mua.utils.TimeUtils.getTime;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.AnniversaryBean;
import com.community.mua.bean.Empty;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityCreateAnniversaryBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ModifyAnniversaryActivity extends BaseActivity<ActivityCreateAnniversaryBinding> {

    private TimePickerView mPvTime;
    private AnniversaryBean mAnniversaryBean;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ModifyAnniversaryActivity.class));
    }

    public static void start(Context context, AnniversaryBean bean) {
        Intent intent = new Intent(context, ModifyAnniversaryActivity.class);
        intent.putExtra(Constants.ANNIVERSARY_BEAN, bean);
        context.startActivity(intent);
    }

    @Override
    protected ActivityCreateAnniversaryBinding getViewBinding() {
        return ActivityCreateAnniversaryBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("新的纪念日");
        mBinding.titleBar.tvMore.setVisibility(View.VISIBLE);
        mBinding.titleBar.tvMore.setText("保存");


        mBinding.tvTime.setText(DateUtil.getTime());

        switchSaveStatus(false);

    }

    private void switchSaveStatus(boolean b) {
        mBinding.tvCount.setText(String.valueOf(mBinding.etName.getText().toString().trim().length()));
        mBinding.titleBar.tvMore.setTextColor(Color.parseColor(b ? "#343434" : "#C4C4C4"));
        mBinding.titleBar.tvMore.setOnClickListener(b ? new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        } : null);
    }

    private void save() {
        if (mDate == null) {
            mDate = new Date();
        }
        if (mAnniversaryBean != null) {
            update();
            return;
        }
        create();
    }

    private void create() {
        Map<String, Object> params = new HashMap<>();
        params.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
        params.put("userId", SharedPreferUtil.getInstance().getUserBean().getUserid());
        params.put("name", mBinding.etName.getText().toString());
        params.put("repeat", mBinding.swRepeat.isChecked());
        params.put("time", mDate.getTime());

        App.getApi().createAnniversary(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Empty>(mContext) {
                    @Override
                    public void next(Empty empty) {
                        ToastUtil.show("纪念日添加成功");
                        finish();
                    }
                });
    }

    private void update() {
        Map<String, Object> params = new HashMap<>();
        params.put("id", mAnniversaryBean.getId());
        params.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
        params.put("userId", SharedPreferUtil.getInstance().getUserBean().getUserid());
        params.put("name", mBinding.etName.getText().toString());
        params.put("repeat", mBinding.swRepeat.isChecked());
        params.put("time", mDate.getTime());

        App.getApi().updateAnniversary(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Empty>(mContext) {
                    @Override
                    public void next(Empty empty) {
                        ToastUtil.show("纪念日修改成功");
                        finish();
                    }
                });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mAnniversaryBean = (AnniversaryBean) intent.getSerializableExtra(Constants.ANNIVERSARY_BEAN);
        if (mAnniversaryBean != null) {
            mBinding.titleBar.tvName.setText("编辑纪念日");
            mBinding.etName.setText(mAnniversaryBean.getAnniversaryName());
            mBinding.tvCount.setText(String.valueOf(mAnniversaryBean.getAnniversaryName().length()));
            mDate = new Date(mAnniversaryBean.getAnniversaryTime());
            mBinding.tvTime.setText(getTime(mDate));
            mBinding.swRepeat.setChecked(mAnniversaryBean.getRepeat());

            switchSaveStatus(true);
        }
    }

    @Override
    protected void initListener() {
        mBinding.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = mBinding.etName.getText().toString();
                switchSaveStatus(s.length() != 0);
            }
        });
    }

    private Date mDate;

    public void onTimePicker(View v) {
        SoundUtil.getInstance().playBtnSound();
        Calendar selectedDate = Calendar.getInstance();
        if (mDate == null) {
            mDate = new Date();
        }
        selectedDate.setTime(mDate);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(2200, 1, 1);
        mPvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date, View v) {
                mDate = date;//选中事件回调
                mBinding.tvTime.setText(getTime(date));
            }
        }).setLayoutRes(R.layout.dialog_birthday_edit, new CustomListener() {

            @Override
            public void customLayout(View v) {
                Button tvSubmit = v.findViewById(R.id.btn_confirm);
                Button ivCancel = v.findViewById(R.id.btn_cancel);
                TextView tvTitle = v.findViewById(R.id.tv_title);
                tvTitle.setText("选择日期");
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundUtil.getInstance().playBtnSound();
                        mPvTime.returnData();
                        mPvTime.dismiss();
                    }
                });
                ivCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundUtil.getInstance().playBtnSound();
                        mPvTime.dismiss();
                    }
                });
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "") //设置空字符串以隐藏单位提示   hide label
                .setContentTextSize(20)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .isDialog(true)
                .setOutSideColor(0x00000000)
                .setOutSideCancelable(false)
                .setGravity(Gravity.CENTER)
                .setTextColorCenter(0xFF294DCA)
                .setTextColorOut(0x66000000)
                .setLineSpacingMultiplier(1.7f)
                .isCenterLabel(false)
                .build();

        mPvTime.setKeyBackCancelable(false);//系统返回键监听屏蔽掉
        mPvTime.show(v, true);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
    }
}
