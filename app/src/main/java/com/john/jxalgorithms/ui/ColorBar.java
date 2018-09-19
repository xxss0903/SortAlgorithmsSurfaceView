package com.john.jxalgorithms.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

/**
 * Created by John on 2016/9/20.
 */
public class ColorBar extends View {
    /**
     * Colors to construct the color wheel using {@link android.graphics.SweepGradient}.
     * 用于构造色条的颜色数组，利用android的LinearGradient类
     */
    private static final int[] COLORS = new int[]{0xFFFF0000, 0xFFFF00FF,
            0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFFFFFF, 0xFF000000};

    /**
     * 长条宽度
     */
    private int barHeight = 30;
    /**
     * 长条的高度
     */
    private int barWidth;

    /**
     * 滑块的半径
     */
    private int thumbRadius = barHeight;

    /**
     * 滑块当前的位置
     */
    private int currentThumbOffset = thumbRadius;

    /**
     * 长条开始位置
     */
    private int barStartX, barStartY;

    private static int STATUS;
    private static final int STATUS_INIT = 0;
    /**
     * 移动了action bar
     */
    private static final int STATUS_SEEK = 1;

    Paint thumbPaint = new Paint();

    private int currentColor;

    public void setHeight(int h) {
        thumbRadius = barHeight = h / 2;
    }

    public interface ColorChangeListener {
        void colorChange(int color);
    }

    ColorChangeListener colorChangeListener;

    public ColorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentColor = COLORS[0];
        STATUS = STATUS_INIT;
        invalidate();
    }

    /**
     * onsizechanged时获取组件的长和宽，后面ondraw时就利用它们进行绘图
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        thumbRadius = h / 2;
        barHeight = thumbRadius;
        barWidth = w - thumbRadius * 2;
        barStartX = thumbRadius;//不从0开始，左右边缘用于显示滑块
        barStartY = thumbRadius - barHeight / 2;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setOnColorChangerListener(ColorChangeListener colorChangerListener) {
        this.colorChangeListener = colorChangerListener;
    }

    /**
     * 绘制底部颜色条
     *
     * @param canvas
     */
    private void drawBar(Canvas canvas) {
        Paint barPaint = new Paint();

        barPaint.setShader(
                new LinearGradient(barStartX, barStartY + barHeight / 2,
                        barStartX + barWidth, barStartY + barHeight / 2,
                        COLORS, null, Shader.TileMode.CLAMP));
        canvas.drawRect(
                new Rect(barStartX, barStartY,
                        barStartX + barWidth, barStartY + barHeight),
                barPaint);


        int p1 = barStartX;
        int p2 = barStartY + barHeight / 2;
        int p3 = barStartX + barWidth;
        int p4 = barStartY + barHeight / 2;
        Log.e("John", "ColorBar" + "drawBar  #" + "p1 = " + p1 +
                "  #p2 =" + p2 + "  #p3=" + p3 + "  #p4=" + p4);
    }

    /**
     * 处理点击和滑动事件
     *
     * @param event
     * @return
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {//点击时
            case MotionEvent.ACTION_DOWN:
                currentThumbOffset = (int) event.getX();
                if (currentThumbOffset <= thumbRadius) currentThumbOffset = thumbRadius;
                if (currentThumbOffset >= barWidth + thumbRadius)
                    currentThumbOffset = barWidth + thumbRadius;
                STATUS = STATUS_SEEK;
                break;
            //滑动时
            case MotionEvent.ACTION_MOVE:
                currentThumbOffset = (int) event.getX();
                if (currentThumbOffset <= thumbRadius) currentThumbOffset = thumbRadius;
                if (currentThumbOffset >= barWidth + thumbRadius)
                    currentThumbOffset = barWidth + thumbRadius;
                break;

        }
        //局部更新，好像没什么用
        invalidate(currentThumbOffset - thumbRadius, barStartY + barHeight / 2 - thumbRadius,
                currentThumbOffset + thumbRadius, barStartY + barHeight / 2 + thumbRadius);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (STATUS) {
            case STATUS_INIT:
                drawBar(canvas);
                drawThumb(canvas);
                break;
            case STATUS_SEEK:
                drawBar(canvas);
                currentColor = getCurrentColor();
                drawThumb(canvas);
                if (colorChangeListener != null)
                    colorChangeListener.colorChange(currentColor);
        }
        super.onDraw(canvas);
    }

    private int ave(int s, int t, int unit, int step) {
        return s + (t - s) * step / unit;
    }

    /**
     * 获取当前所在区间，再根据颜色变换算法获取颜色值
     */
    private int getCurrentColor() {
        int unit = barWidth / (COLORS.length - 1);
        int position = currentThumbOffset - thumbRadius;
        int i = position / unit;
        int step = position % unit;
        if (i >= COLORS.length - 1) return COLORS[COLORS.length - 1];
        int c0 = COLORS[i];
        int c1 = COLORS[i + 1];
        Log.e("John", "ColorBar" + "t color # " + "i = " + i);

        int a = ave(Color.alpha(c0), Color.alpha(c1), unit, step);
        int r = ave(Color.red(c0), Color.red(c1), unit, step);
        int g = ave(Color.green(c0), Color.green(c1), unit, step);
        int b = ave(Color.blue(c0), Color.blue(c1), unit, step);

        drawThumbWithColor(Color.argb(a, r, g, b));

        return Color.argb(a, r, g, b);
    }

    public int getColor() {
        return getCurrentColor();
    }

    public void setColor(int color) {
        currentColor = color;
        STATUS = STATUS_INIT;

        invalidate();
    }

    private int reAve(int a, int s,int t,  int unit){
        return (a - s) * unit / (t - s);
    }

    // 绘制小球，根据颜色来绘制
    private void drawThumbWithColor(int color) {

// 十六进制转化为十进制，结果140。
        //r,g,b,a
        // 0xFFFF0000, 0xFFFF00FF,  blue, red
        //0xFF0000FF, 0xFF00FFFF,   green, blue
        // 0xFF00FF00, 0xFFFFFF00,  red, blue
        // 0xFFFFFFFF, 0xFF000000

        int i = 0;
        // 将颜色分解
         int r = Color.red(color);
        int g = Color.green(color);
        int a = Color.alpha(color);
        int b = Color.blue(color);

        int s =0;
        int t = 0;
        int unit = 0;
        int step = 0;

        if(a == 255 && r == 255 && g == 0){
            i = 0;
             s = Color.blue(COLORS[i]);
             t = Color.blue(COLORS[i+1]);
             unit = barWidth / (COLORS.length - 1);
            Log.e("John", "ColorBar" + "t color # 1" + "s = " + s + " t = " + t + " a = " + a + " unit = " + unit + "  width = " + barWidth);
            step = (b - s) * unit / (t -s);
            step += i * unit ;
        } else if(a == 255 && b == 255 &&g == 0){
            i = 1;
             s = Color.red(COLORS[i]);
             t = Color.red(COLORS[i+1]);
             unit = barWidth / (COLORS.length - 1);
            Log.e("John", "ColorBar" + "t color # 2" + "s = " + s + " t = " + t + " a = " + a + " unit = " + unit + "  width = " + barWidth);
            step = (r - s) * unit / (t -s);
            step += i * unit ;
        } else if(a == 255 && b == 255 &&  r == 0) {
            i = 2;
             s = Color.green(COLORS[i]);
             t = Color.green(COLORS[i+1]);
             unit = barWidth / (COLORS.length - 1);
            Log.e("John", "ColorBar" + "t color # 3" + "s = " + s + " t = " + t + " a = " + a + " unit = " + unit + "  width = " + barWidth);
            step = (g - s) * unit / (t -s);
            step += i * unit ;
        } else if(a == 255 && r == 0 && g == 255){
            i = 3;
             s = Color.blue(COLORS[i]);
             t = Color.blue(COLORS[i+1]);
             unit = barWidth / (COLORS.length - 1);
            Log.e("John", "ColorBar" + "t color # 4" + "s = " + s + " t = " + t + " a = " + a + " unit = " + unit + "  width = " + barWidth);
            step = (b - s) * unit / (t -s);
            step += i * unit ;
        } else if(a ==255 && g == 255 && b == 0){
            i = 4;
             s = Color.red(COLORS[i]);
             t = Color.red(COLORS[i+1]);
             unit = barWidth / (COLORS.length - 1);
            Log.e("John", "ColorBar" + "t color # 5" + "s = " + s + " t = " + t + " a = " + a + " unit = " + unit + "  width = " + barWidth);
            step = (r - s) * unit / (t -s);
            step += i * unit ;
        } else if(a == 255 && r == 255 && g == 255){
            i = 5;
             s = Color.blue(COLORS[i]);
             t = Color.blue(COLORS[i+1]);
             unit = barWidth / (COLORS.length - 1);
            Log.e("John", "ColorBar" + "t color # 6" + "s = " + s + " t = " + t + " a = " + a + " unit = " + unit + "  width = " + barWidth);
            step = (b - s) * unit / (t -s);
            step += i * unit ;
        }else if(a == 255){
            i = 6;
            s = Color.blue(COLORS[i]);
            t = Color.blue(COLORS[i+1]);
            unit = barWidth / (COLORS.length - 1);
            Log.e("John", "ColorBar" + "t color # 6" + "s = " + s + " t = " + t + " a = " + a + " unit = " + unit + "  width = " + barWidth);
            step = (b - s) * unit / (t -s);
            step += i * unit ;
        }

        currentThumbOffset = step + thumbRadius;

        Log.e("John", "ColorBar" + "t # " + "step = " + step);
    }

    // 这个是那个圆球把滑动的小球
    private void drawThumb(Canvas canvas) {
        thumbPaint.setColor(currentColor);
        Log.e("John", "ColorBar" + " # " + "drawThumb color = " + currentColor);


        drawThumbWithColor(currentColor);
        canvas.drawOval(getThumbRect(), thumbPaint);

    }

    /**
     * 获取滑块所在的矩形区域
     */
    private RectF getThumbRect() {
        int p1 = currentThumbOffset - thumbRadius;
        int p2 = barStartY + barHeight / 2 - thumbRadius;
        int p3 = currentThumbOffset + thumbRadius;
        int p4 = barStartY + barHeight / 2 + thumbRadius;
//        Log.e("John", "ColorBar" + "getThumbRect  #" + "p1 = " + p1 +
//        "  #p2 =" + p2 + "  #p3=" + p3 + "  #p4=" + p4);

        return new RectF(currentThumbOffset - thumbRadius, barStartY + barHeight / 2 - thumbRadius,
                currentThumbOffset + thumbRadius, barStartY + barHeight / 2 + thumbRadius);
    }
}