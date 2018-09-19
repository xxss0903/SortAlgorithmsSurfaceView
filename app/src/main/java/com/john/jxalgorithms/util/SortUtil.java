package com.john.jxalgorithms.util;

import com.john.jxalgorithms.sort.HeapSort;
import com.john.jxalgorithms.sort.InsertionSort;
import com.john.jxalgorithms.sort.MergeSort;
import com.john.jxalgorithms.sort.QuickSort;

import java.util.Comparator;

/**
 * 排序工具类
 * 1. 在进行排序的surfaceview中根据具体的类型进行排序
 * 2. 排序的方式是通过创建具体的排序对象进行调用的
 * Created by John on 2016/9/16.
 */
public class SortUtil {

    /**
     * 插入排序
     * @param a 排序数组
     * @param c 排序的比较规则
     */
    public static void insertionSort(Double[] a, Comparator<Double> c, boolean stop){
        InsertionSort sorter = new InsertionSort(a, c);
       sorter.sort(stop);
    }

    /**
     * 合并排序
     * @param a 排序数组
     * @param c 排序的比较规则
     */
    public static void mergeSort(Double[] a, Comparator<Double> c, boolean stop){
        MergeSort sorter = new MergeSort(a, c);
        sorter.sort(stop);
    }

    /**
     * 快速
     * @param a 排序数组
     * @param c 排序的比较规则
     */
    public static  void quickSort(Double[] a, Comparator<Double> c, Boolean stop){
        QuickSort sorter = new QuickSort(a, c);
        sorter.sort(stop);
    }

    /**
     * 堆排序
     * @param a 排序数组
     * @param c 排序的比较规则
     */
    public static void heapSort(Double[] a, Comparator<Double> c, Boolean stop){
        HeapSort sorter = new HeapSort(a, c);
        sorter.sort(stop);
    }

}
