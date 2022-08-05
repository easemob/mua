package com.community.mua.base;

import android.app.Activity;
import android.text.TextUtils;

import java.util.Iterator;
import java.util.Stack;

public class AppManager {
    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单一实例
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取栈顶Activity（堆栈中最后一个压入的）
     */
    public Activity getTopActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束栈顶Activity（堆栈中最后一个压入的）
     */
    public void finishTopActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls
     */
    public void finishActivity(Class<?> cls) {
        Iterator iterator = activityStack.iterator();
        while (iterator.hasNext()) {
            Activity activity = (Activity) iterator.next();
            if (activity.getClass().equals(cls)) {
                iterator.remove();
                activity.finish();
            }
        }
    }

    /**
     * 结束所有Activity
     */
    @SuppressWarnings("WeakerAccess")
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 结束所有Activity
     */
    @SuppressWarnings("WeakerAccess")
    public void finishAllActivityWithoutMainChat() {
        int size = activityStack.size();
        if (size == 2) {
            return;
        }
        for (int i = 0; i < size; i++) {
            Activity activity = activityStack.get(i);
            if (null != activity) {
                if (TextUtils.equals(activity.getClass().getSimpleName(), "MainActivity") ||
                        TextUtils.equals(activity.getClass().getSimpleName(), "ChatActivity")) {
                    continue;
                }
                activity.finish();
                activityStack.remove(activity);
                i--;
                size--;
            }
        }
    }

    /*
     * 移除栈顶栈底
     * */
    public void finishAllActivityWithoutFirstLast() {
        int size = activityStack.size();
        if (size == 2) {
            return;
        }
        for (int i = 1; i < size - 1; i++) {
            Activity activity = activityStack.get(i);
            if (null != activity) {
                activityStack.remove(activity);
                activity.finish();
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            finishAllActivity();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());

        } catch (Exception e) {
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 得到指定类名的Activity
     */
    public Activity getActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }
}
