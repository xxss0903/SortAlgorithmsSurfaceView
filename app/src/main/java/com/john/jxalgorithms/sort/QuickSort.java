package com.john.jxalgorithms.sort;

import android.util.Log;

import com.john.jxalgorithms.ui.IDrawRect;

import java.util.Comparator;

/**
 * Created by John on 2016/9/4.
 */
public class QuickSort  extends BaseSort {

    private static final String TAG = "John";

    public QuickSort(Double[] a, Comparator c) {
        super(a, c);
    }

    public void sort(Boolean stop) {
        quickSort(  a, 0, a.length - 1);
    }

    private void quickSort(Double[] a, int lo, int hi) {
        if (hi <= lo)
            return;
        int j = partition(a, lo, hi);
        quickSort(a, lo, j - 1);
        quickSort(a, j + 1, hi);
    }

    // 分割
    private int partition(Double[] a, int lo, int hi) {
        int i = lo, j = hi + 1;
        Double v = a[lo];
        while (true) {
            while (c.compare(a[++i], v) < 0) {
                if (i == hi)
                    break;
            }
            while (c.compare(a[--j], v) > 0) {
                if (j == lo)
                    break;
            }
            if (i >= j)
                break;
            exch(a, i, j);

        }
        exch(a, lo, j);
        return j;
    }
}
