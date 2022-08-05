package com.community.mua.ui.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.DiaryBean;
import com.community.mua.databinding.ActivityDiaryListBinding;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SharedPreferUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DiaryListActivity extends BaseActivity<ActivityDiaryListBinding> {

    public static void start(Context context ) {
        Intent intent = new Intent(context, DiaryListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected ActivityDiaryListBinding getViewBinding() {
        return ActivityDiaryListBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("日记本");
        mBinding.rvDiaryList.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    protected void initData() {
        getDiaryList("");
    }

    @Override
    protected void initListener() {

    }

    private void getDiaryList(String month){
        Map<String, String> map = new HashMap<>();
        map.put("matchingCode", SharedPreferUtil.getInstance().getPairBean().getMatchingCode());
        map.put("month", month);
        App.getApi().getDiaryList(map).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<List<DiaryBean>>(mContext,true) {
                    @Override
                    public void next(List<DiaryBean> diaryBeans) {
                        Log.e("wyz", "getDiaryList: " + diaryBeans.size());
                        mBinding.rvDiaryList.setAdapter(new DiaryListAdapter(mContext,diaryBeans));
                    }
                });
    }
}