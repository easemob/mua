package com.community.mua.imkit.modules.chat.interfaces;

import android.graphics.drawable.Drawable;

public interface IChatExtendMenu {
    /**
     * 清理扩展功能
     */
    void clear();

    /**
     * 设置条目的排序
     * @param itemId
     * @param order
     */
    void setMenuOrder(int itemId, int order);

    /**
     * 添加新的扩展功能
     * @param name
     * @param drawableRes
     * @param itemId
     */
    void registerMenuItem(String name, int drawableRes, int itemId);

    /**
     * 添加新的扩展功能
     * @param name
     * @param drawableRes
     * @param itemId
     * @param order
     */
    void registerMenuItem(String name, int drawableRes, int itemId, int order);

    /**
     * 添加新的扩展功能
     * @param nameRes
     * @param drawableRes
     * @param itemId
     */
    void registerMenuItem(int nameRes, int drawableRes, int itemId);

    /**
     * 添加新的扩展功能
     * @param nameRes
     * @param drawableRes
     * @param itemId
     * @param order
     */
    void registerMenuItem(int nameRes, int drawableRes, int itemId, int order);

    /**
     * 设置条目监听
     * @param listener
     */
    void setEaseChatExtendMenuItemClickListener(EaseChatExtendMenuItemClickListener listener);

    /**
     * 设置背景
     * @param bg
     */
    void setMenuBackground(Drawable bg);
}
