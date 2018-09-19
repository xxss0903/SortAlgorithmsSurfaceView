package com.john.jxalgorithms.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2016/9/7.
 */
public class JXBTView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "John";
    // 这个surfaceView需要哪些参数呢
    private final SurfaceHolder holder;
    private Canvas canvas;
    private Paint paintCircle;
    private Paint paintLine;
    private int dis = 30;
    private int radius = 5;
    private int count = 20;

    private List<BTCircle> circleList;
    private List<BTLine> lineList;


    public JXBTView(Context context, AttributeSet attrs) {
        super(context, attrs);

        holder = getHolder();
        holder.addCallback(this);   // 添加监听

        paintCircle = new Paint();
        paintCircle.setColor(Color.BLUE);
        paintCircle.setStrokeWidth(3);

        paintLine = new Paint();
        paintLine.setColor(Color.BLUE);
        paintLine.setStrokeWidth(3);

        initDatas();
    }

    private void initDatas() {
        lineList = new ArrayList<>();
        circleList = new ArrayList<>();

        Log.e(TAG, "JXBTView" + "height all = " + calcBTHeight());

        int height = 1;
        for (int i = 1; i < count; i++) {
            if (i == 1) {
                circleList.add(new BTCircle(350, 50, radius));
                continue;
            }

            // 计算当前所在的层
            int k = (pow(2, height+1));
            if(i == k){
                height  ++;
            }

            BTCircle fc = circleList.get(i / 2 - 1);
            Log.e(TAG, "JXBTView" + "height =  " + height + " #pow(2,height)=" + k);
            int px = pow(2,(calcBTHeight() - height)) * 4;
            int py = pow(2, height) * 2;
            if(height == 1){
                if (i % 2 == 0) {
                    BTCircle sc = new BTCircle(fc.centerX - dis*3 - px, fc.centerY + dis/2  +  py, radius);
                    circleList.add(sc);
                } else if (i % 2 == 1) {
                    BTCircle sc = new BTCircle(fc.centerX + dis*3 + px, fc.centerY + dis/2 +   py, radius);
                    circleList.add(sc);
                }
                continue;
            }
            if (i % 2 == 0) {
                BTCircle sc = new BTCircle(fc.centerX - dis - px, fc.centerY + dis/2  +  py, radius);
                circleList.add(sc);
            } else if (i % 2 == 1) {
                BTCircle sc = new BTCircle(fc.centerX + dis + px, fc.centerY + dis/2 +   py, radius);
                circleList.add(sc);
            }
        }
    }

    private int calcBTHeight(){
        int height = 0;
        for(int i = 0; i < count; i ++){
            // 计算当前所在的层
            int k = (pow(2, height+1));
            if(i == k){
                height  ++;
            }
        }
        return height;
    }

    private int pow(int a, int b) {
        if (b == 0)
            return 1;
        int result = 1;
        for (int i = 0; i < b; i++)
            result *= a;
        return result;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "JXBTView" + "surfaceview 准备好了");
        new Thread() {
            @Override
            public void run() {
                drawBT();
            }
        }.start();
    }

    // 绘画
    private void drawBT() {
        canvas = holder.lockCanvas();

        canvas.drawColor(Color.BLACK);
//        paintCircle.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < count-1; i++) {
            drawCircle(circleList.get(i));
            if(i == 0)
                continue;
            BTCircle fc = circleList.get((i+1) / 2 - 1);
            BTCircle sc = circleList.get(i);
                drawLine(new BTLine(fc.centerX, fc.centerY, sc.centerX, sc.centerY));
        }

        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    // 绘制圆形
    private void drawCircle(BTCircle circle) {
        canvas.drawCircle(circle.centerX, circle.centerY, circle.radius, paintCircle);
    }

    private void drawLine(BTLine line) {
        canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paintLine);
    }

    class BTCircle {
        int centerX;
        int centerY;
        int radius;

        public BTCircle(int centerX, int centerY, int radius) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
        }
    }

    class BTLine {
        int startX;
        int startY;
        int endX;
        int endY;

        public BTLine(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }

}
