package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.community.mua.R;
import com.community.mua.adapter.PetMarketAdapter;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.PetMarketBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityPetMarketBinding;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;

import java.util.List;

public class PetMarketActivity extends BaseActivity<ActivityPetMarketBinding> {

    private int mCoins;
    private int mCost;
    private PetMarketAdapter mAdapter;
    private List<PetMarketBean> mList;

    public static void start(Context context) {
        context.startActivity(new Intent(context, PetMarketActivity.class));
    }

    @Override
    protected ActivityPetMarketBinding getViewBinding() {
        return ActivityPetMarketBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.flCoins.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        mCoins = SharedPreferUtil.getInstance().getCoins();
        mBinding.titleBar.tvCoins.setText(mCoins + "");

        initRv();
    }

    private void initRv() {
        mBinding.rv.setLayoutManager(new LinearLayoutManager(mContext));

        mList = SharedPreferUtil.getInstance().getPetFoods();

        mAdapter = new PetMarketAdapter(mList, new PetMarketAdapter.OnCostChangeListener() {
            @Override
            public void onCostChange(int cost) {

                mBinding.tvPay.setBackgroundResource(cost > 0 ? R.drawable.sl_next_small : R.drawable.sl_cancel_small);
                mBinding.tvPay.setTextColor(Color.parseColor(cost > 0 ? "#361DD4" : "#343434"));
                mCost = cost;
                mBinding.tvCost.setText(String.valueOf(mCost));
            }
        });
        mBinding.rv.setAdapter(mAdapter);
    }

    public void onPay(View v) {
        SoundUtil.getInstance().playBtnSound();
        if (mList == null || mList.isEmpty()) {
            return;
        }


        if (mCost == 0) {
            return;
        }
        if (mCoins < mCost) {
            ToastUtil.show("小本生意拒绝赊账");
            return;
        }
        pay();
    }

    @Override
    protected void initListener() {
        LiveDataBus.get().with(Constants.COINS_UPDATE, LiveEvent.class).observe(this, this::onCoinsUpdate);
    }

    private void pay() {
        mCoins -= mCost;
        SharedPreferUtil.getInstance().payCoins(mCost);
        mAdapter.confirmPay();

        List<PetMarketBean> petFoods = SharedPreferUtil.getInstance().getPetFoods();
        for (int i = 0; i < petFoods.size(); i++) {
            PetMarketBean bean = mList.get(i);
            bean.setInventory(bean.getInventory() + petFoods.get(i).getInventory());
        }
        SharedPreferUtil.getInstance().setPetFoods(mList);
        showSuccessDialog();
    }

    private void showSuccessDialog() {
        NiceDialog.init().setLayoutId(R.layout.dialog_pay_success)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        holder.getView(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                LiveDataBus.get().with(Constants.SHOW_PET_FOODS).postValue(new LiveEvent());
                                dialog.dismissAllowingStateLoss();
                                finish();
                            }
                        });
                    }
                })
                .setMargin(DisplayUtil.dp2px(mContext, 10))
                .setOutCancel(false)
                .show(getSupportFragmentManager());
    }

    private void onCoinsUpdate(LiveEvent event) {
        int coins = SharedPreferUtil.getInstance().getCoins();
        mBinding.titleBar.tvCoins.setText(String.valueOf(coins));
        mBinding.tvCost.setText("0");
    }
}
