package com.john.jxalgorithms.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;

import com.john.jxalgorithms.bean.SorterBean;
import com.john.jxalgorithms.util.Constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 2016/9/12.
 */
public class SorterDAO {

    SorterOpenHelper helper;

    public SorterDAO(Context context) {
        helper = new SorterOpenHelper(context);
    }

    // 查询所有的sorter数据
    public List<SorterBean> queryAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM " + Constant.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        List<SorterBean> datas = new ArrayList<>();
        // 取出sorter对象从数据库中
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            byte[] data = cursor.getBlob(cursor.getColumnIndex(Constant.T_SORTER));
            ByteArrayInputStream dataIn = new ByteArrayInputStream(data);
            try {
                ObjectInputStream objIn = new ObjectInputStream(dataIn);
                SorterBean sorter = (SorterBean) objIn.readObject();
                sorter.setId(id);
                datas.add(sorter);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return datas;
    }

    // 保存sorter对象到数据库中
    public void setSorter(SorterBean sorter) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objOut = new ObjectOutputStream(out);    // 将字节码输出转换为对象输出流
            objOut.writeObject(sorter);
            objOut.flush();
            byte[] data = out.toByteArray();    // 对象转换为字节码
            objOut.close();
            out.close();

            SQLiteDatabase db = helper.getWritableDatabase();
            String sql = "INSERT INTO " + Constant.TABLE_NAME +" ("+ Constant.T_SORTER+")"+ " VALUES(?)";
            db.execSQL(sql, new Object[]{data});    // 将字节码数据写入
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void deleteAll(){

        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "DELETE  FROM  " + Constant.TABLE_NAME + " WHERE 1=1";
        db.execSQL(sql);
    }

    public void delete(int id){
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "DELETE  FROM  " + Constant.TABLE_NAME + " WHERE _id=" + id;
        db.execSQL(sql);
    }

}
