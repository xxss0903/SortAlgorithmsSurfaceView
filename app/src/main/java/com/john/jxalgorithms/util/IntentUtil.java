package com.john.jxalgorithms.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by John on 2016/9/9.
 */
public class IntentUtil {

    public static void startActivity(Context context, Class<?> cls){
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

}
