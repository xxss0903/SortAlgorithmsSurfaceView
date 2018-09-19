package com.john.jxalgorithms.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.john.jxalgorithms.bean.SorterBean;

/**
 * Created by John on 2016/9/5.
 */
public class SPUtils {

    private static final String TAG = "John";

    private static  SharedPreferences getSP(Context context){
        return  context.getSharedPreferences("version", Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(Context context, String key) {
        boolean value = getSP(context).getBoolean(key, false);
        Log.e("John", "SPUtils" + "获取是否更新 " + key +"=" + value);
        return value;
    }

    public static void setBoolean(Context context, String key, boolean isTrue) {
        Log.e("John", "SPUtils" + "设置是否更新 " + key +"="+key + " #value="+ isTrue);
        getSP(context).edit().putBoolean(key, isTrue).commit(); // 需要提交
    }

// 获取字符串
    public static String getString(Context context, String key) {
        return getSP(context).getString(key, null);    // 如果没有那么就设置为null
    }

    public static void setString(Context context, String key, String value){
        Log.e(TAG, "SPUtils" + "设置字符串 #" + value);
        getSP(context).edit().putString(key, value).commit();
    }

    public static int getInt(Context context, String key) {
        return getSP(context).getInt(key, 30);
    }

    public static void setInt(Context context, String key, int value) {
        getSP(context).edit().putInt(key, value).commit();
    }
}
