package com.leoero.intelligentlamp.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import intelligentlamp.leoero.com.intelligentlamp.MainActivity;

/** @brief 此类为全局调用的工具类 **/
public enum Util {

    INSTANCE;

    /**
     * @brief Util的私有构造函数
     */
    Util() {}

    /**
     * @brief 打印Toast
     * @param context 对应的Activity
     * @param message 打印的信息
     */
    public void showToast(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
