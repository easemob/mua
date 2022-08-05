package com.community.mua.imkit.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.community.mua.bean.PairBean;
import com.community.mua.bean.UserBean;
import com.community.mua.common.Constants;
import com.community.mua.imkit.EaseIM;
import com.community.mua.imkit.domain.EaseAvatarOptions;
import com.community.mua.imkit.domain.EaseUser;
import com.community.mua.imkit.provider.EaseUserProfileProvider;
import com.community.mua.imkit.widget.EaseImageView;

import com.community.mua.R;
import com.community.mua.utils.SharedPreferUtil;

import java.util.List;


public class EaseUserUtils {

    static {
        // TODO: 2019/12/30 0030 how to provide userProfileProvider
//        userProvider = EaseUI.getInstance().getUserProfileProvider();
    }

    /**
     * get EaseUser according username
     * @param username
     * @return
     */
    public static EaseUser getUserInfo(String username) {
        EaseUserProfileProvider provider = EaseIM.getInstance().getUserProvider();
        return provider == null ? null : provider.getUser(username);
    }

    /**
     * set user's avatar style
     * @param imageView
     */
    public static void setUserAvatarStyle(EaseImageView imageView) {
        EaseAvatarOptions avatarOptions = EaseIM.getInstance().getAvatarOptions();
        if (avatarOptions == null || imageView == null) {
            return;
        }
        if (avatarOptions.getAvatarShape() != 0)
            imageView.setShapeType(avatarOptions.getAvatarShape());
        if (avatarOptions.getAvatarBorderWidth() != 0)
            imageView.setBorderWidth(avatarOptions.getAvatarBorderWidth());
        if (avatarOptions.getAvatarBorderColor() != 0)
            imageView.setBorderColor(avatarOptions.getAvatarBorderColor());
        if (avatarOptions.getAvatarRadius() != 0)
            imageView.setRadius(avatarOptions.getAvatarRadius());
    }

    /**
     * set user avatar
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        List<UserBean> userRsp = SharedPreferUtil.getInstance().getPairBean().getUserRsp();
        String avatar = "";
        UserBean userBean = SharedPreferUtil.getInstance().getUserBean();
        for (UserBean bean : userRsp) {
            if (TextUtils.equals(bean.getChatId(), username)) {
                avatar = Constants.HTTP_HOST + bean.getAvatar();
                userBean = bean;
                break;
            }
        }

        if (imageView == null) {
            return;
        }
        RequestOptions options = new RequestOptions()
                .placeholder(userBean.getGender() == 0?R.drawable.male_avatar:R.mipmap.female_avatar)                //加载成功之前占位图
                .error(userBean.getGender() == 0?R.drawable.male_avatar:R.mipmap.female_avatar);
        Glide.with(context).applyDefaultRequestOptions(options).load(avatar).into(imageView);
    }

    /**
     * show user avatar
     * @param context
     * @param avatar
     * @param imageView
     */
    public static void showUserAvatar(Context context, String avatar, ImageView imageView) {
        if (TextUtils.isEmpty(avatar)) {
            Glide.with(context).load(R.drawable.ease_default_avatar).into(imageView);
            return;
        }
        try {
            int avatarResId = Integer.parseInt(avatar);
            Glide.with(context).load(avatarResId).into(imageView);
        } catch (Exception e) {
            //use default avatar
            Glide.with(context).load(avatar)
                    .apply(RequestOptions.placeholderOf(R.drawable.ease_default_avatar)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String username, TextView textView) {
        List<UserBean> userRsp = SharedPreferUtil.getInstance().getPairBean().getUserRsp();
        for (UserBean bean : userRsp) {
            if (TextUtils.equals(bean.getChatId(), username)) {
                username = bean.getNickname();
                break;
            }
        }

        if (textView != null) {
            textView.setText(username);
        }
    }

}
