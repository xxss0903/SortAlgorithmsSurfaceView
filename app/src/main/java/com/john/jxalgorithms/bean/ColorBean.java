package com.john.jxalgorithms.bean;

/**
 * Created by John on 2016/9/20.
 */
public class ColorBean {
    private int fillColor;
    private int emptyColor;
    private int bgColor;

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public int getEmptyColor() {
        return emptyColor;
    }

    public void setEmptyColor(int emptyColor) {
        this.emptyColor = emptyColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setColor(int fillColor, int emptyColor, int bgColor) {
        this.fillColor = fillColor; this.emptyColor = emptyColor; this.bgColor = bgColor;
    }
}
