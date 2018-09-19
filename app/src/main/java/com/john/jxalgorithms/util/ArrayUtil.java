package com.john.jxalgorithms.util;

/**
 * Created by John on 2016/9/13.
 */
public class ArrayUtil {

    public static Double max(Double[] array){
        Double max = array[0];
        for (int i = 0; i < array.length-1; i++) {
            if(array[i].compareTo(max) > 0){
                max = array[i];
            }
        }
        return max;
    }
}
