package com.john.jxalgorithms.bean;

import com.john.jxalgorithms.sort.SortType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 排序这bean类
 * 1. 排序的类型
 * 2. 排序的数组的个数，默认30个
 * 3. 排序的速度,也就是每次交换等待的时间
 * 4. 排序的数组
 * Created by John on 2016/9/12.
 */
public class SorterBean implements Serializable{
    private int id;
    private SortType sortType;
    private int delay = 1000;   // 默认1秒
    private Double[] array;

    public SorterBean() {
    }

    public SorterBean(SortType sortType , int delay, Double[] array) {
        this.sortType = sortType;
        this.delay = delay;
        this.array = array;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public int getCount() {
        if(array != null){
            return array.length;
        }
        return 30;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Double[] getArray() {
        return array;
    }

    public void setArray(Double[] array) {
        this.array = array;
    }

    @Override
    public String toString() {
        return "SorterBean{" +
                "sortType=" + sortType +
                ", delay=" + delay +
                ", array=" + Arrays.toString(array) +
                '}';
    }
}
