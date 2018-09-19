package com.john.surfacedemo;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * 容器类，绘制多个自图形的
 * Created by John on 2016/9/4.
 */
public class Container {

    private int x = 50;
    private int y = 50;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    private List<Container> children;

    public Container(){
        children = new ArrayList<>();
    }

    // 从链表中取出自图形绘制在画布上
    public void draw(Canvas canvas){
        canvas.translate(getX(), getY());
        childrenDraw(canvas);
        for(Container c : children){
            c.draw(canvas);
        }
    }

    // 用来回调的方法
    public void childrenDraw(Canvas canvas) {

    }


    public void addChildrenView(Container child){
        children.add(child);
    }

    public void removeChildrenView(Container child){
        children.remove(child);
    }

}
