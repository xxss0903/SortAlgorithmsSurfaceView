package com.john.jxalgorithms.sort;

import android.util.Log;

import com.john.jxalgorithms.ui.IDrawRect;

import java.util.Comparator;

/**
 * Created by John on 2016/9/4.
 */
public class HeapSort   extends BaseSort {

public HeapSort(){}

    public HeapSort(Double[] a, Comparator c) {
        super(a, c);
    }

    // 堆判断
    void heapAdjust(Double[] a, int i, int size) {
        int lchild = 2 * i;
        int rchild = 2 * i + 1;
        int max = i;
        if (i < size / 2) {
            if (lchild <= size && c.compare(a[lchild], a[max]) > 0) {
                max = lchild;
            }
            if (rchild <= size && c.compare(a[rchild], a[max]) > 0) {
                max = rchild;
            }
            if (max != i) {
                exch(a, i, max);
                heapAdjust(a, max, size);
            }
        }
    }

    // 创建堆
    void buildHeap(Double[] a, int size) {
        int  i;
        for (i = size / 2; i >= 0; i--) {
            heapAdjust(a, i, size);
        }
    }

    void heapSort(Double[] a, int size, Boolean stop) {
        int i;
        buildHeap(a, size);
        for (i = size-1; i >= 0; i--) {
            exch(a, 0, i);
            heapAdjust(a, 0, i );
        }
    }

    public void sort(Boolean stop) {
        heapSort( a, a.length, stop);
    }

}
