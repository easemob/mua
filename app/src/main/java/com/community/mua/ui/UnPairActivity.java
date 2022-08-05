package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.AppManager;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityUnpairBinding;
import com.community.mua.imchat.ImHelper;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;
import com.community.mua.views.window.FloatWindow;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMPresence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UnPairActivity extends BaseActivity<ActivityUnpairBinding> {

    private static final String content = "解除匹配关系即为清空双方全部数据并回到单身状态界面。 \n\n" +
            "解绑须知： \n" +
            "1. 取消双方订阅服务，即使重新绑定也不再享有订阅服务 \n" +
            "2. 清空所有购买的商品 \n" +
            "3. 清空所有日记内容 \n" +
            "4. 清空所有便利贴内容 \n" +
            "5. 清空所有纪念日 \n" +
            "6. 清空所有照片 \n" +
            "7. 宠物状态清零 \n" +
            "8. 金币清零 \n" +
            "9. 金币记录清零 \n" +
            "10. 清除悄悄话中所有数据。 \n" +
            "11. 清空所有心动相册的数据";
    private PairBean mPairBean;

    public static void start(Context context) {
        context.startActivity(new Intent(context, UnPairActivity.class));
    }

    @Override
    protected ActivityUnpairBinding getViewBinding() {
        return ActivityUnpairBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        mBinding.titleBar.tvName.setText("解除匹配");
    }

    @Override
    protected void initData() {
        mBinding.tvContent.setText(content);

        mPairBean = SharedPreferUtil.getInstance().getPairBean();
    }

    @Override
    protected void initListener() {

    }

    public void onUnpair(View v) {
        SoundUtil.getInstance().playBtnSound();
        NiceDialog.init().setLayoutId(R.layout.dialog_unpair)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {

                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                unPair();
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

    private void unPair() {
        Map<String, String> params = new HashMap<>();
        params.put("matchingId", mPairBean.getMatchingId());
        App.getApi().unPair(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<Integer>(mContext) {
                    @Override
                    public void next(Integer i) {
                        ToastUtil.show("解除匹配成功！");

                        unsubscribePresences();

                        UserUtils.sendCmdMsg(Constants.UNPAIR,  SharedPreferUtil.getInstance().getUserBean().getNickname());

                        FloatWindow.get().hide();

                        UserUtils.resetUser();

                        AppManager.getInstance().finishAllActivity();
                        PairActivity.start(mContext);
                    }
                });
    }

    private void unsubscribePresences(){
        UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
        List<String> list = new ArrayList<>();
        list.add(taBean.getChatId());
        ImHelper.getInstance().getEMClient().presenceManager().unsubscribePresences(list, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "unsubscribePresences onSuccess: ");
            }

            @Override
            public void onError(int i, String s) {
                Log.d("TAG", "unsubscribePresences : onError " +s);
            }
        });
    }
}
