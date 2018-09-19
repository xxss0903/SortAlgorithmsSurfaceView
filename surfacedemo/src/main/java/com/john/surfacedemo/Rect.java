package com.john.surfacedemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by John on 2016/9/4.
 */
public class Rect extends Container{

    private Paint paint;

    public Rect(){
        paint = new Paint();
        paint.setColor(Color.BLUE);
    }

    @Override
    public void childrenDraw(Canvas canvas) {
        setX(getX() + 1);
        setY(getY() + 2);
        canvas.drawRect(0,0, 100, 100,paint);
    }
}
