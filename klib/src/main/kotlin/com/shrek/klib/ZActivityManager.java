package com.shrek.klib;

import android.app.Activity;

import java.util.Stack;

public class ZActivityManager<T extends Activity> {
    private Stack<T> activityStack;
//    private static ZActivityManager<? extends Activity> instance;

    public ZActivityManager() {
        activityStack = new Stack<T>();
    }

//    public static ZActivityManager<? extends Activity> getInstance() {
//        if (instance == null) {
//            instance = new ZActivityManager<Activity>();
//        }
//        return instance;
//    }

    /**
     * 关闭最上层 activity
     */
    public void popActivity() {
        if (activityStack.size() > 0) {
            Activity activity = activityStack.lastElement();
            if (activity != null) {
                activity.finish();
                activity = null;
            }
        }
    }


    /**
     * 关闭 指定的 activity
     *
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }

        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 得到当前 activity
     *  throws NullPointerException 
     * @return
     */
    public T currentActivity(){
        if (activityStack.size() > 0) {
            T activity = activityStack.lastElement();
            return activity;
        }
        return null;
    }


    /**
     * 添加 新的 activity
     *
     * @param activity
     */
    public void pushActivity(T activity) {
        if (activityStack == null) {
            activityStack = new Stack<T>();
        }
        activityStack.add(activity);
    }

    /*
     * 关闭其他除了指定的
     */
    public void popAllActivityExceptOne(Class<? extends Activity> cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                continue;
            }
            popActivity(activity);
        }
    }

    /**
     * 弹出activity 直到遇到cls
     *
     * @param cls
     */
    public void popActivityUntilOne(Class<? extends Activity> cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    /*
     * 关闭其他除了指定的
     */
    public void popAllActivity() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }

}