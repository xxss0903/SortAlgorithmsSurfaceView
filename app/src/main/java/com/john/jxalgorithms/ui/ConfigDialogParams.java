package com.john.jxalgorithms.ui;

/**
 * Created by John on 2016/9/19.
 */
public class ConfigDialogParams {
    private int count;
    private int delay;

    public ConfigDialogParams(int count, int delay) {
        this.count = count;
        this.delay = delay;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
