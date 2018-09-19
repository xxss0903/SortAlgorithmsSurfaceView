package com.john.jxalgorithms.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.john.jxalgorithms.bean.MainAdapterBean;

import java.util.List;

/**
 * Created by John on 2016/9/3.
 */
public class MainAdapter extends BaseAdapter{

    private List<MainAdapterBean> datas;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
