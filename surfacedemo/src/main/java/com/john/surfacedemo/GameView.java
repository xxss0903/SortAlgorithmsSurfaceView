package com.john.surfacedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 这个是绘制自视图的那个俯视图，他继承自一个SurfaceView
 * Created by John on 2016/9/4.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private Container container;
    private Rect rect = new Rect();
    private Circle circle = new Circle();
    private Timer timer;
    private TimerTask task;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        container = new Container();
        rect = new Rect();
        circle = new Circle();
        container.addChildrenView(circle);
        container.addChildrenView(rect);
        getHolder().addCallback(this);
    }

    public void startTimer() {
        timer = new Timer();
        task = new TimerTask() {

            @Override
            public void run() {
                Log.e("John", "Start Timer");
                draw();
            }
        };
        timer.schedule(task, 100, 100);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void draw() {
        Canvas canvas = getHolder().lockCanvas();
        container.draw(canvas); // 这里调用了container的绘制方法，这个绘制方法传入了一个画布，然后就在这个画布上进行作画
        getHolder().unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("John", "Surface Created");
        startTimer();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopTimer();
    }
}
