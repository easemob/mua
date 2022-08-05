package com.community.mua.ui.diary;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.GvFeelBean;
import com.community.mua.bean.PairBean;
import com.community.mua.databinding.ActivityAddDiaryBinding;
import com.community.mua.utils.BatteryUtils;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DateUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddDiaryActivity extends BaseActivity<ActivityAddDiaryBinding> {

    public static void start(Context context,String date) {
        Intent intent = new Intent(context, AddDiaryActivity.class);
        intent.putExtra("date",date);
        context.startActivity(intent);
    }

    private String dateStr;
    private int pos = -1;
    List<GvFeelBean> list;
    @Override
    protected ActivityAddDiaryBinding getViewBinding() {
        return ActivityAddDiaryBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("添加日记");
    }

    @Override
    protected void initData() {
        dateStr = getIntent().getStringExtra("date");
        mBinding.tvDate.setText(dateStr);
        String weekStr = DateUtil.getWeekZhouStr(dateStr);
        mBinding.tvWeek.setText("("+weekStr+")");
        PairBean mPairBean = SharedPreferUtil.getInstance().getPairBean();
        long time = Long.parseLong(mPairBean.getMatchingTime());
        mBinding.tvTogether.setText("这是我们在一起的第 "+DateUtil.getDuringDay(time) + " 天");
        list = BatteryUtils.list;

        GvFeelAdapter gvFeelAdapter = new GvFeelAdapter(list, this);
        mBinding.gvFeel.setAdapter(gvFeelAdapter);
        gvFeelAdapter.setOnBtnClickListener(new GvFeelAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(int position, View v) {
                for (int i = 0; i < list.size(); i++) {
                    if (position == i) {//当前选中的Item改变背景颜色
                        list.get(i).setSelect(true);
                    } else {
                        list.get(i).setSelect(false);
                    }
                }
                pos = position;
                gvFeelAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initListener() {
        mBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundUtil.getInstance().playBtnSound();
                if (pos == -1){
                    ToastUtil.show("必须要选择一个心情哦~");
                    return;
                }

                WriteDiaryActivity.start(mContext,pos,getTimestamp(dateStr));
            }
        });
    }


    private long getTimestamp(String time){
        //
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = df.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            long timestamp = cal.getTimeInMillis();
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
            return  System.currentTimeMillis();
        }
    }

    /**
     * 对高度和宽度进行统计 然后设置gridView的宽高。
     * @param numColumns 设定行数
     * @param gridView
     */
    public static void calGridViewSumWH(int numColumns , GridView gridView) {
        // 获取GridView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int totalWidth = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalWidth = listItem.getMeasuredWidth();
            if ((i+1)%numColumns == 0) {
                if (Build.VERSION.SDK_INT  == Build.VERSION_CODES.JELLY_BEAN) {
                    totalHeight += listItem.getMeasuredHeight() + gridView.getVerticalSpacing(); // 统计所有子项的总高度
                } else {
                    totalHeight += listItem.getMeasuredHeight();
                }
            }

            if ((i+1) == len && (i+1)%numColumns != 0) {
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            totalHeight += 20;
        }
        if (listAdapter.getCount() < numColumns) {
            gridView.setNumColumns(listAdapter.getCount());
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        params.width = totalWidth * listAdapter.getCount();
        gridView.setLayoutParams(params);
    }
}