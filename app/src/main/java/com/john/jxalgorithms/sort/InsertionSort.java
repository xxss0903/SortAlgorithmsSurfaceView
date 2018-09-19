package com.john.jxalgorithms.sort;

import android.util.Log;

import com.john.jxalgorithms.ui.IDrawRect;

import java.util.Comparator;


/**
 * Created by John on 2016/9/4.
 */
public class InsertionSort  extends BaseSort {

    public InsertionSort(){}

    public InsertionSort(Double[] a, Comparator<Double> c) {
        super(a, c);
    }

    @Override
    public void sort( Boolean stop) {
        for (int i = 1; i < a.length; i++) {
            for (int j = i; j > 0; j--) {
                if (c.compare(a[j], a[j-1]) < 0) {
                    exch(a, j, j - 1);
                }
            }
        }
    }


}
