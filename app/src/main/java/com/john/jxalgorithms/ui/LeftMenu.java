package com.john.jxalgorithms.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.john.jxalgorithms.R;

/**
 * 这里如何获取到上下文呢？
 * Created by John on 2016/9/6.
 */
public class LeftMenu extends Fragment {

    private static final String TAG = "John";
    private ListView lvMenus;

    private void initView(View v) {
        lvMenus = (ListView) v.findViewById(R.id.lv_left_menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "LeftMenu" + "初始化左边的单元格那个");
        View view = inflater.inflate(R.layout.layout_menu, container, false);
        initView(view);
        initData();
        initEvent();

        return view;
    }

    private void initData() {
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                new String[]{"删除所有", "颜色设置", "关于"});
        lvMenus.setAdapter(adapter);
    }

    private void initEvent(){

    }
}
