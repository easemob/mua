package com.community.mua.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.DayBean;
import com.community.mua.bean.DiaryBean;
import com.community.mua.bean.MonthBean;
import com.community.mua.callkit.EaseCallKit;
import com.community.mua.callkit.base.EaseCallType;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityMoodDiaryBinding;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.services.QObserver;
import com.community.mua.ui.call.VideoCallActivity;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MoodDiaryActivity extends BaseActivity<ActivityMoodDiaryBinding> {

    public static void start(Context context ) {
        Intent intent = new Intent(context, MoodDiaryActivity.class);
        context.startActivity(intent);
    }

    List<Fragment> fragmentList;
//    private ArrayList<MonthBean> mMonthBeans;
    private BaseFragmentAdapter mFragmentAdapter;

    @Override
    protected ActivityMoodDiaryBinding getViewBinding() {
        return ActivityMoodDiaryBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        fragmentList = new ArrayList<>();
        mFragmentAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList);
        mBinding.vpMonth.setOffscreenPageLimit(3);
    }

    @Override
    protected void initData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        getDiaryList(0);
        mBinding.vpMonth.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBinding.tvMonth.setText(position + 1 + "月");
//                getDiaryList(year+"-"+position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initListener() {
        LiveDataBus.get().with(Constants.DIARY_UPDATE, LiveEvent.class).observe(this, this::updateDiary);

        mBinding.llBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                showMonthDialog();
            }
        });

        mBinding.ivCreateDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                AddDiaryActivity.start(mContext, DateUtil.getStringDateShort());
            }
        });
    }

    private void updateDiary(LiveEvent event){
        getDiaryList(0);
    }

    private void showMonthDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        List<MonthBean> beanList = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            if (i == (month+1)) {
                beanList.add(new MonthBean(i,true));
            } else {
                beanList.add(new MonthBean(i));
            }
        }


        NiceDialog.init().setLayoutId(R.layout.dialog_month)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        ((TextView) holder.getView(R.id.tv_year)).setText(year+"");
                        RecyclerView gridView = holder.getView(R.id.gv_month);
                        gridView.setLayoutManager(new GridLayoutManager(mContext,4));
                        GvMonthAdapter adapter = new GvMonthAdapter(beanList,mContext);
                        gridView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new GvMonthAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int pos) {
                                if (pos > month) {
                                    ToastUtil.show("不能写未来日记哦!");
                                    return;
                                }
                                beanList.get(month).setSelect(true);
                                for (int i = 0; i < beanList.size(); i++) {
                                    if (pos == i) {//当前选中的Item改变背景颜色
                                        beanList.get(i).setSelect(true);
                                    } else {
                                        beanList.get(i).setSelect(false);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                                mBinding.tvMonth.setText(pos + 1 + "月");
                                getDiaryList(pos+1);
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                    }
                })
                .setAnimStyle(R.style.BottomAnimation)
                .setMargin(DisplayUtil.dp2px(mContext, 0))
                .setOutCancel(true)
                .setShowBottom(true)
                .show(getSupportFragmentManager());
    }


    private void getDiaryList(int paramsMonth){
        Map<String, String> map = new HashMap<>();
        map.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
        if (paramsMonth == 0) {
            map.put("month", "");
        } else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            map.put("month", year+"-"+DateUtil.getStrDay(paramsMonth)+"");
        }

        App.getApi().getDiaryList(map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<List<DiaryBean>>(mContext,true) {
                    @Override
                    public void next(List<DiaryBean> diaryBeans) {
                        Log.e("wyz", "getDiaryList: " + diaryBeans.size());
                       initDays(diaryBeans,paramsMonth);
                    }
                });
    }


    private void initDays(List<DiaryBean> diaryBeans,int paramsMonth){

        Map<String,List<DiaryBean>> map = new HashMap<>();
        for (int i = 0; i < diaryBeans.size(); i++) {
            DiaryBean diaryBean = diaryBeans.get(i);
            long diaryTimeStamp = diaryBean.getDiaryTimeStamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String format = simpleDateFormat.format(new Date(diaryTimeStamp));

            if (map.containsKey(format)) {
                List<DiaryBean> beans = map.get(format);
                beans.add(diaryBean);
                map.put(format,beans);
            } else {
                List<DiaryBean> beans = new ArrayList<>();
                beans.add(diaryBean);
                map.put(format,beans);
            }

        }
        Log.e("wyz", "map.entrySet(): " + map.entrySet().size());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= (month + 1); i++) {
            ArrayList<DayBean> dayBeans = new ArrayList<>();
            int day = getDay(i);
            for (int j = 1; j <= 35; j++) {
                if (j > day) {
                    dayBeans.add(new DayBean(true));
                } else {
                    List<DiaryBean> list = map.get(year + "-" + DateUtil.getStrDay(i) + "-" + DateUtil.getStrDay(j));
                    Log.e("wyz", "list : " + (list == null ? 0:list.size()));
                    if (i == (month+1) && dayOfMonth == j) {
                        dayBeans.add(new DayBean(year + "-" + DateUtil.getStrDay(i) + "-" + DateUtil.getStrDay(j), "今天",false,list));
                    } else {
                        dayBeans.add(new DayBean(year + "-" + DateUtil.getStrDay(i) + "-" + DateUtil.getStrDay(j), "" + j,false,list));
                    }
                }
            }
            fragmentList.add(MoodDiaryFragment.newInstance(dayBeans));
        }

        mBinding.tvYear.setText(year+"");
        if (paramsMonth == 0) {
            mBinding.tvMonth.setText(month + 1+"月");
            mBinding.vpMonth.setAdapter(mFragmentAdapter);
            mBinding.vpMonth.setCurrentItem(month);
        } else {
            mBinding.tvMonth.setText(paramsMonth +"月");
            mBinding.vpMonth.setAdapter(mFragmentAdapter);
            mBinding.vpMonth.setCurrentItem(paramsMonth-1);
        }
    }


    public int getDay(int month){
        if (month == 4 ||month == 6 ||month == 9 ||month == 11){
            return 30;
        } else if (month == 2 ){
            return 28;
        } else{
            return 31;
        }
    }
}