package com.zjp.androidstudy.utils;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/11/04
 *     version: 1.0
 *     desc   :
 * </pre>
 */
public class AppContext {
    private static final String TAG = "AppContext";
    private static Context sContext;
    public static Context getContext() {
        if (sContext == null) {
            try {
                Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
                Method method_currentActivityThread = ActivityThread.getMethod("currentActivityThread");
                Object currentActivityThread = method_currentActivityThread.invoke(ActivityThread);//获取currentActivityThread 对象
                Method method_getApplication = currentActivityThread.getClass().getMethod("getApplication");
                sContext = (Context) method_getApplication.invoke(currentActivityThread);//获取 Context对象
            }catch (Exception e) {
                Log.e(TAG, "getContext: error : " +e.getMessage());
            }
        }
        return sContext;
    }
}
