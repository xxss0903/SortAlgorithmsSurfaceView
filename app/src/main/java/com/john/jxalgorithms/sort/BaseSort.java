package com.john.jxalgorithms.sort;

import com.john.jxalgorithms.ui.IDrawRect;

import java.util.Comparator;

/**
 * Created by John on 2016/9/6.
 */
public abstract class BaseSort<T extends Comparable<? super T>> {

    public Comparator<Double> c;
    public Double[] a;

    public BaseSort(){}

    public BaseSort(Double[] a, Comparator<Double> c) {
        this.a = a;
        this.c = c;
    }

    protected void exch(Double[] a, int i, int j) {
        Double tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    public abstract void sort(Boolean stop);
}
