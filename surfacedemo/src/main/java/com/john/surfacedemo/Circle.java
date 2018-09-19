package com.john.surfacedemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by John on 2016/9/4.
 */
public class Circle extends Container{

private Paint paint;

    public Circle(){
        paint = new Paint();
        paint.setColor(Color.RED);
    }

    @Override
    public void childrenDraw(Canvas canvas) {
        // 通过调用父方法的这个方法实现绘制，这个算是回调了？
        canvas.drawCircle(50, 50, 50, paint);
    }
}
