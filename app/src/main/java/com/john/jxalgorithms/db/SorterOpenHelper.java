package com.john.jxalgorithms.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.john.jxalgorithms.util.Constant;

/**
 * sorter的数据库帮助类
 * Created by John on 2016/9/12.
 */
public class SorterOpenHelper extends SQLiteOpenHelper{

    public SorterOpenHelper(Context context) {
        super(context, Constant.DB_NAME, null, Constant.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*    private SortType sortType;
    private int count;
    private int delay;
    private int[] array;*/
        // 创建一个表
        String sql = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constant.T_SORTER + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
