package com.zjp.commonutils;

import android.widget.Toast;

/**
 * <pre>
 *     author : zhangjunpu
 *     e-mail : zhangjp@zhigujinyun.com
 *     time   : 2020/12/02
 *     version: 1.0
 *     desc   :
 * </pre>
 */
public class ToastUtils {
    public static void show(String msg) {
        Toast.makeText(AppContext.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String msg) {
        Toast.makeText(AppContext.getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
