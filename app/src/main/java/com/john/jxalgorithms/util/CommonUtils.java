package com.john.jxalgorithms.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Created by John on 2016/9/9.
 */
public class CommonUtils {

    private static int pointNum = 100000;

    // 生成随机数组
    public static Double[] generateRandomArray(int count) {
        Double array[] = new Double[count];
        for (int i = 0; i < array.length; i++) {
            // 这里让产生的double数拥有固定的位数
            DecimalFormat df   = new DecimalFormat("######0.00000");
            array[i] = Double.valueOf(df.format(((double) ((int) (Math.random() * pointNum)) / pointNum * 80d)));
        }
        return array;
    }

    public static void copyArray(Double[] currentArray, Double[] oldArray) {
        if (oldArray == null) {
            oldArray = new Double[currentArray.length];
        }
        for (int i = 0; i < currentArray.length; i++) {
            oldArray[i] = currentArray[i];
        }
    }

    /**
     * 屏幕截图
     * @param activity 截图的activity
     * @return  截图
     */
    public static  Bitmap shotScreen(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

}
