package com.community.mua.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.community.mua.R;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserUtils {

    public static void setShowNum(TextView textView, int num) {
        if (num == 0) {
            textView.setVisibility(View.GONE);
        } else if (num < 10) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(num));
        } else if (num <= 99) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(" " + num + " ");
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(" 99+ ");
        }
    }

    public static void setUserInfo(Context context, TextView tvName, boolean hideGender, FrameLayout flAvatar, ImageView ivAvatar, UserBean bean) {
        if (bean == null) {
            return;
        }
        if (tvName != null) {
            tvName.setText(bean.getNickname());
            if (!hideGender)
                tvName.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(bean.getGender() == 0 ? R.drawable.male_small : R.drawable.female_small), null);
        }

        if (flAvatar != null)
            flAvatar.setBackgroundResource(bean.getGender() == 0 ? R.drawable.sp_ff007a : R.drawable.sp_00ffe0);

        if (ivAvatar != null) setAvatar(context, bean, ivAvatar);
    }

    public static void setUserInfo(Context context, TextView tvName, FrameLayout flAvatar, ImageView ivAvatar, UserBean bean) {
        if (bean == null) {
            return;
        }
        if (tvName != null) {
            tvName.setText(bean.getNickname());
            tvName.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getResources().getDrawable(bean.getGender() == 0 ? R.drawable.male_small : R.drawable.female_small), null);
        }

        if (flAvatar != null)
            flAvatar.setBackgroundResource(bean.getGender() == 0 ? R.drawable.sp_ff007a : R.drawable.sp_00ffe0);

        if (ivAvatar != null) setAvatar(context, bean, ivAvatar);
    }

    public static void setAvatar(Context context, UserBean bean, ImageView imageView) {
        Integer gender = bean.getGender();
        RequestOptions options = new RequestOptions()
                .placeholder(getDefaultAvatar(gender))                //加载成功之前占位图
                .error(getDefaultAvatar(gender));
        Glide.with(context).applyDefaultRequestOptions(options).load(Constants.HTTP_HOST + bean.getAvatar()).into(imageView);
    }

    private static int getDefaultAvatar(int gender) {
        return gender == 0 ? R.mipmap.male_avatar : R.mipmap.female_avatar;
    }

    public static void saveAvatar(String avatar) {
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        userBean.setAvatar(avatar);

        SharedPreferUtil.getInstance().setUserBean(userBean);
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        List<UserBean> userRsp = pairBean.getUserRsp();
        for (UserBean bean : userRsp) {
            if (TextUtils.equals(bean.getUserid(), userBean.getUserid())) {
                userRsp.set(userRsp.indexOf(bean), userBean);
                pairBean.setUserRsp(userRsp);
                SharedPreferUtil.getInstance().setPairBean(pairBean);
                break;
            }
        }

    }

    public static void saveTaAvatar(String avatar) {
        UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
        taBean.setAvatar(avatar);

        SharedPreferUtil.getInstance().setTaBean(taBean);
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        List<UserBean> userRsp = pairBean.getUserRsp();
        for (UserBean bean : userRsp) {
            if (TextUtils.equals(bean.getUserid(), taBean.getUserid())) {
                userRsp.set(userRsp.indexOf(bean), taBean);
                pairBean.setUserRsp(userRsp);
                SharedPreferUtil.getInstance().setPairBean(pairBean);
                break;
            }
        }

    }

    public static void saveTaNickName(String nickName) {
        UserBean taBean = SharedPreferUtil.getInstance().getTaBean();
        taBean.setNickname(nickName);

        SharedPreferUtil.getInstance().setTaBean(taBean);
        PairBean pairBean = SharedPreferUtil.getInstance().getPairBean();
        List<UserBean> userRsp = pairBean.getUserRsp();
        for (UserBean bean : userRsp) {
            if (TextUtils.equals(bean.getUserid(), taBean.getUserid())) {
                userRsp.set(userRsp.indexOf(bean), taBean);
                pairBean.setUserRsp(userRsp);
                SharedPreferUtil.getInstance().setPairBean(pairBean);
                break;
            }
        }

    }

    public static void sendCustomMsg( int pos, String msg) {
        EMMessage customMessage = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);
// `event` 为需要传递的自定义消息事件，比如礼物消息，可以设置：
        EMCustomMessageBody customBody = new EMCustomMessageBody(msg);
// `params` 类型为 `Map<String, String>`。
        Map<String, String> params = new HashMap<>();
        params.put("position", String.valueOf(pos));
        customBody.setParams(params);
        customMessage.addBody(customBody);
// `to` 指另一方环信用户 ID（或者群组 ID，聊天室 ID）
        customMessage.setTo(SharedPreferUtil.getInstance().getTaBean().getChatId());
// 如果是群聊，设置 `ChatType` 为 `GroupChat`，该参数默认是单聊（`Chat`）。
        EMClient.getInstance().chatManager().sendMessage(customMessage);

        ToastUtil.show("已发送至悄悄话");
    }

    public static void sendTxtMsg(String msg) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        EMTextMessageBody messageBody = new EMTextMessageBody(msg);
        messageBody.setMessage(msg);
        message.addBody(messageBody);
        message.setTo(SharedPreferUtil.getInstance().getTaBean().getChatId());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public static void sendCmdMsg(String key, String msg) {
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setAttribute(key, msg);
        EMCmdMessageBody body = new EMCmdMessageBody(key);
        // Only deliver this cmd msg to online users
        body.deliverOnlineOnly(true);
        message.addBody(body);
        message.setTo(SharedPreferUtil.getInstance().getTaBean().getChatId());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public static void resetUser() {
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();

        SharedPreferUtil.getInstance().setPairBean(null);
        SharedPreferUtil.getInstance().setTaBean(null);
        userBean.setMatching(false);
        SharedPreferUtil.getInstance().setUserBean(userBean);
        SharedPreferUtil.getInstance().clearCoins();
    }

    public static UserBean getUserBean(String chatId) {
        List<UserBean> list = SharedPreferUtil.getInstance().getPairBean().getUserRsp();
        for (UserBean userBean : list) {
            if (TextUtils.equals(userBean.getChatId(), chatId)) {
                return userBean;
            }
        }
        return null;
    }

    public static void setSplashImage(Context context, ImageView imageView, String url) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.splash_img_default)                //加载成功之前占位图
                .error(R.drawable.splash_img_default);
        Glide.with(context).applyDefaultRequestOptions(options).load(Constants.HTTP_HOST + url).into(imageView);

    }

    private static final int[] foodIconRes = new int[]{R.drawable.group_719, R.drawable.group_721, R.drawable.group_722};

    public static int getPetFoodIconRes(int pos) {
        return foodIconRes[pos];
    }

    private static final int[] foodTitleRes = new int[]{R.drawable.pet_foods, R.drawable.pet_cans, R.drawable.yammy_snake};

    public static int getPetFoodTitleRes(int pos) {
        return foodTitleRes[pos];
    }
} 
