package com.community.mua.ui;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.community.mua.App;
import com.community.mua.adapter.RecordGridAdapter;
import com.community.mua.adapter.RecordLinearAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.LoginRecordBean;
import com.community.mua.bean.RecordBean;
import com.community.mua.bean.UserBean;
import com.community.mua.databinding.ActivityLoginRecordBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.SharedPreferUtil;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginRecordActivity extends BaseActivity<ActivityLoginRecordBinding> implements RecordGridAdapter.OnItemClickListener{

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoginRecordActivity.class));
    }

    List<RecordBean> list;
    RecordGridAdapter mRecordGridAdapter;
    RecordBean mRecordBean;
    Map<String,List<LoginRecordBean>> mRecordListMap;

    @Override
    protected ActivityLoginRecordBinding getViewBinding() {
        return ActivityLoginRecordBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("登录记录");
        mBinding.titleBar.ivMore.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 5);
        mBinding.rvRecord.setLayoutManager(layoutManager);
        long currentMillis = DateUtil.getCurrentMillis();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");

//        long  time = currentMillis - 24 * 60 * 60 * 1000L;//减去一天
        list = new ArrayList<>();
        RecordBean bean = new RecordBean("今天", simpleDateFormat.format(currentMillis), currentMillis + "");
        bean.setSelect(true);
        list.add(bean);
        long t1 = currentMillis - 1 *24 * 60 * 60 * 1000L;
        list.add(new RecordBean("昨天",simpleDateFormat.format(t1),t1+""));
        long t2 = currentMillis - 2 *24 * 60 * 60 * 1000L;
        list.add(new RecordBean(DateUtil.getWeek(t2),simpleDateFormat.format(t2),t2+""));
        long t3 = currentMillis - 3 *24 * 60 * 60 * 1000L;
        list.add(new RecordBean(DateUtil.getWeek(t3),simpleDateFormat.format(t3),t3+""));
        long t4 = currentMillis - 4 *24 * 60 * 60 * 1000L;
        list.add(new RecordBean(DateUtil.getWeek(t4),simpleDateFormat.format(t4),t4+""));

        Collections.reverse(list);
        mRecordGridAdapter = new RecordGridAdapter(list, this);
        mBinding.rvRecord.setAdapter(mRecordGridAdapter);
        getLoginRecordList();

    }

    @Override
    protected void initListener() {
        mBinding.titleBar.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginRecordSettingActivity.start(mContext);
            }
        });
    }

    @Override
    public void onItemClick(int position, RecordBean bean) {
        SoundUtil.getInstance().playBtnSound();
        for (int i = 0; i < 5; i++) {
            if (position == i) {//当前选中的Item改变背景颜色
                list.get(i).setSelect(true);
            } else {
                list.get(i).setSelect(false);
            }
        }
        mRecordBean = list.get(position);
        mRecordGridAdapter.notifyDataSetChanged();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String s = format.format(Long.parseLong(mRecordBean.getTimeStamp()));
        List<LoginRecordBean> recordBeans = mRecordListMap.get(s);
        mBinding.rvLlRecord.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.rvLlRecord.setAdapter(new RecordLinearAdapter(recordBeans,mContext));
    }


    private void getLoginRecordList(){
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
        Map<String, String> params = new HashMap<>();
        params.put("mineId", userBean.getUserid());
        params.put("taId", taBean.getUserid());
        App.getApi().getLoginRecordList(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<List<LoginRecordBean>>(mContext) {
                    @Override
                    public void next(List<LoginRecordBean> beans) {
                        loadLinearRecord(beans);
                    }
                });
    }

    private void loadLinearRecord(List<LoginRecordBean> beans){
        mRecordListMap = new HashMap<>();
        for (int i = 0; i < beans.size(); i++) {
            LoginRecordBean bean = beans.get(i);
            String timeStampLogin = bean.getTimeStampLogin();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = simpleDateFormat.format(Long.parseLong(timeStampLogin));
            List<LoginRecordBean> recordBeans;
            if (mRecordListMap.containsKey(format)) {
                recordBeans = mRecordListMap.get(format);
            } else {
                recordBeans = new ArrayList<>();
            }
            recordBeans.add(bean);
            mRecordListMap.put(format,recordBeans);
        }
        setRecordLinearAdapterData();
    }

    private void setRecordLinearAdapterData(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String s = format.format(System.currentTimeMillis());
        List<LoginRecordBean> recordBeans = mRecordListMap.get(s);

        if (recordBeans != null && recordBeans.size() > 1){
            Collections.sort(recordBeans, new Comparator<LoginRecordBean>() {
                @Override
                public int compare(LoginRecordBean o1, LoginRecordBean o2) {
                    //倒序
                    return Collator.getInstance(java.util.Locale.CHINA).compare(o2.getTimeStampLogin(), o1.getTimeStampLogin());
                }
            });
        }

        mBinding.rvLlRecord.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.rvLlRecord.setAdapter(new RecordLinearAdapter(recordBeans,mContext));
    }
}