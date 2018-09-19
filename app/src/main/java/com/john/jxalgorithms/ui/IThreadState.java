package com.john.jxalgorithms.ui;

/**
 * Created by John on 2016/9/18.
 */
public interface IThreadState {
    void onThreadEnd(JXSortView.DrawThread thread, JXSortView sv);
    void onThreadStart(JXSortView.DrawThread thread, JXSortView sv);

}
