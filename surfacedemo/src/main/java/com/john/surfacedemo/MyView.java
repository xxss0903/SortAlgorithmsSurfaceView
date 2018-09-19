package com.john.surfacedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by John on 2016/9/4.
 */
public class MyView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint;


    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化画笔
        paint  = new Paint();
        paint.setColor(Color.RED);

        getHolder().addCallback(this);  // 是这个surfaceview的控制
    }

    public void draw(){
        // 开始要锁定canvas
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.WHITE);
        canvas.drawRect(0, 0, 100, 100, paint); // 在画布上画画，就是调用画布的画图形方法

        // 结束要解锁画布,上面的画布已经花了一个矩形了，此时结束绘画之后就解锁画布
        getHolder().unlockCanvasAndPost(canvas);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
draw(); // 创建surface的时候执行那个draw方法，锁定画布，那那个画布的锁定和解锁同时在一个方法里面，那么会不会影响呢?
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
// surface改变
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
// surfaceview意外销毁时调用
    }
}
