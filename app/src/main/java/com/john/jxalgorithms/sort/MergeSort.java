package com.john.jxalgorithms.sort;

import android.util.Log;

import com.john.jxalgorithms.ui.IDrawRect;

import java.util.Comparator;

/**
 * Created by John on 2016/9/4.
 */
public class MergeSort extends BaseSort {

    private Double[] aux;

    public MergeSort(Double[] a, Comparator c) {
        super(a, c);
    }

    public void sort(Boolean stop) {
        aux = new Double[a.length];    // 泛型不能直接组成数组？还有泛型不能继承，在子类中是当做Comparable？
        mergeSort(a, 0, a.length - 1, stop);
    }

    // 排序
    private void mergeSort(Double a[], int low, int high, Boolean stop) {
        if (low >= high)
            return;
        int mid = low + (high - low) / 2;
        mergeSort(a, low, mid, stop);
        mergeSort(a, mid + 1, high, stop);
        merge(a, low, mid, high);
    }

    // 合并
    private void merge(Double[] a, int low, int mid, int high) {
        int i = low, j = mid + 1;

        for (int k = low; k <= high; k++) {
            aux[k] = a[k];
        }

        for (int k = low; k <= high; k++) {
            if (i > mid) {
                a[k] = aux[j++];
            } else if (j > high) {
                a[k] = aux[i++];
            } else if (c.compare(aux[i], aux[j]) < 0) {
                a[k] = aux[i++];
            } else {
                a[k] = aux[j++];
            }
        }
    }

}
