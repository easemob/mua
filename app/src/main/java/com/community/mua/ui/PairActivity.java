package com.community.mua.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.community.mua.App;
import com.community.mua.R;
import com.community.mua.base.AppManager;
import com.community.mua.base.BaseActivity;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.databinding.ActivityPairBinding;
import com.community.mua.imchat.ImHelper;
import com.community.mua.livedatas.LiveDataBus;
import com.community.mua.livedatas.LiveEvent;
import com.community.mua.services.QObserver;
import com.community.mua.utils.SoundUtil;
import com.community.mua.utils.DisplayUtil;
import com.community.mua.utils.GsonUtils;
import com.community.mua.utils.SharedPreferUtil;
import com.community.mua.utils.ToastUtil;
import com.community.mua.utils.UserUtils;
import com.community.mua.views.dialog.BaseNiceDialog;
import com.community.mua.views.dialog.NiceDialog;
import com.community.mua.views.dialog.ViewConvertListener;
import com.community.mua.views.dialog.ViewHolder;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMPresence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PairActivity extends BaseActivity<ActivityPairBinding> {

    private UserBean mBean;

    public static void start(Context context) {
        Intent intent = new Intent(context, PairActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity(intent);
    }

    @Override
    protected ActivityPairBinding getViewBinding() {
        return ActivityPairBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mBean = SharedPreferUtil.getInstance().getUserBean();
        mBinding.tvCode.setText(mBean.getMineCode());
        UserUtils.setUserInfo(mContext,mBinding.tvName, null, mBinding.ivAvatar, mBean);
    }

    @Override
    protected void initListener() {
        mBinding.ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundUtil.getInstance().playBtnSound();
                mBinding.etCode.setText("");
            }
        });

        mBinding.etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = mBinding.etCode.getText().toString().trim();
                mBinding.ivClear.setVisibility(TextUtils.isEmpty(s) ? View.INVISIBLE : View.VISIBLE);
            }
        });

        View vContent = findViewById(android.R.id.content);
        ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener =
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        vContent.getWindowVisibleDisplayFrame(rect);
                        int height = rect.height();
                        boolean isImmShow = height < (DisplayUtil.getHeight(mContext) - DisplayUtil.getNavigationBarHeight(mContext));
                        mBinding.vBottom.setVisibility(!isImmShow ? View.GONE : View.VISIBLE);
                    }
                };

        vContent.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

        LiveDataBus.get().with(Constants.PAIR_INFO, LiveEvent.class).observe(this, this::onReceivePair);
    }

    private void onReceivePair(LiveEvent event) {
        String pairStr = event.event;
        PairBean pairBean = GsonUtils.toBean(pairStr, PairBean.class);
        setPairData(pairBean, false);
    }

    public void onNext(View v) {
        SoundUtil.getInstance().playBtnSound();
        String code = mBinding.etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtil.show("请输入匹配码");
            return;
        }

        if (code.length() < 6) {
            ToastUtil.show("请输入正确的匹配码");
            return;
        }

        if (TextUtils.equals(code, mBean.getMineCode())) {
            ToastUtil.show("你就这么孤独么？");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("mineCode", code);
        App.getApi().getUserByCode(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<UserBean>(mContext) {
                    @Override
                    public void next(UserBean taBean) {
                        showPairDialog(taBean);
                    }
                });
    }

    private void showPairDialog(UserBean taBean) {
        NiceDialog.init().setLayoutId(R.layout.dialog_pair)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                        TextView tvNameMine = holder.getView(R.id.tv_name_mine);
                        FrameLayout flAvatarMine = holder.getView(R.id.fl_avatar_mine);
                        ImageView ivAvatarMine = holder.getView(R.id.iv_avatar_mine);
                        UserUtils.setUserInfo(mContext,tvNameMine, flAvatarMine, ivAvatarMine, mBean);

                        TextView tvNameTa = holder.getView(R.id.tv_name_ta);
                        FrameLayout flAvatarTa = holder.getView(R.id.fl_avatar_ta);
                        ImageView ivAvatarTa = holder.getView(R.id.iv_avatar_ta);
                        UserUtils.setUserInfo(mContext,tvNameTa, flAvatarTa, ivAvatarTa, taBean);

                        ImageView ivHeart = holder.getView(R.id.iv_heart);
                        AnimationDrawable ad = (AnimationDrawable) ivHeart.getDrawable();
                        ad.start();

                        holder.getView(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                                assert taBean != null;
                                if (mBean.getGender().equals(taBean.getGender())) {
                                    ToastUtil.show("目前不支持同性匹配");
                                    return;
                                }
                                pair();
                            }
                        });
                        holder.getView(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SoundUtil.getInstance().playBtnSound();
                                dialog.dismissAllowingStateLoss();
                            }
                        });
                        holder.getView(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
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

    private void pair() {
        Map<String, String> params = new HashMap<>();
        params.put("code1", mBinding.tvCode.getText().toString());
        params.put("code2", mBinding.etCode.getText().toString());

        App.getApi().pair(params).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new QObserver<PairBean>(mContext) {
                    @Override
                    public void next(PairBean pairBean) {
                        setPairData(pairBean, true);
                    }
                });
    }

    private void setPairData(PairBean pairBean, boolean isSend) {
        List<UserBean> userRsp = pairBean.getUserRsp();
        UserBean meBean = null;
        UserBean taBean = null;
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        for (UserBean bean : userRsp) {
            if (TextUtils.equals(userBean.getUserid(), bean.getUserid())) {
                meBean = bean;
                continue;
            }
            taBean = bean;
        }
        if (meBean == null || taBean == null) {
            ToastUtil.show("无用户数据");
            return;
        }



        ToastUtil.show("祝贺你们，匹配成功啦");

        SharedPreferUtil.getInstance().setUserBean(meBean);
        SharedPreferUtil.getInstance().setTaBean(taBean);
        SharedPreferUtil.getInstance().setPairBean(pairBean);
        if (isSend) UserUtils.sendCmdMsg(Constants.PAIR_INFO,GsonUtils.toJson(pairBean));


        if (isSend) addContact(taBean.getChatId());

        AppManager.getInstance().finishActivity(PairActivity.class);
        WelcomeActivity.start(mContext);
    }

    private void addContact(String chatId) {
        ImHelper.getInstance().getEMClient().contactManager().aysncAddContact(chatId, "我们在一起吧", new EMCallBack() {
            @Override
            public void onSuccess() {
                List<String> list = new ArrayList<>();
                list.add(chatId);
                ImHelper.getInstance().getEMClient().presenceManager().subscribePresences(list, 24 * 60 * 60 * 30, new EMValueCallBack<List<EMPresence>>() {
                    @Override
                    public void onSuccess(List<EMPresence> emPresences) {
                        //FIXME 查询状态后刷新界面
                        for (EMPresence p: emPresences) {
                            if (TextUtils.equals(chatId,p.getPublisher())){
                                Map<String, Integer> map = p.getStatusList();
                                for(String key: map.keySet()){
                                    LiveDataBus.get().with(Constants.USER_ONLINE).postValue(new LiveEvent(Constants.USER_ONLINE, map.get(key)+""));
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
