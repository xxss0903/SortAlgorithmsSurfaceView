package com.john.jxalgorithms.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.john.jxalgorithms.R;
import com.john.jxalgorithms.util.Constant;
import com.john.jxalgorithms.util.SPUtils;

/**
 * Created by John on 2016/9/9.
 */
public class JXSortConfigView extends DialogFragment{

    private EditText etCount;
    private EditText etDelay;
    private Button btnCancel;
    private Button btnOk;

    public ISortConfigDialog inteface;
    private View view;
    private ConfigDialogParams params;

    public JXSortConfigView(){}

    private void initView(){
        etCount = (EditText) view.findViewById(R.id.et_array_count);
        etDelay = (EditText) view.findViewById(R.id.et_sort_vel);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel_config_dialog);
        btnOk = (Button) view.findViewById(R.id.btn_ok_config_dialog);

        initData();
    }

    private void initData(){
        etCount.setText(SPUtils.getInt(getContext(), Constant.ARRAY_COUNT) + "");
        etDelay.setText(SPUtils.getInt(getContext(), Constant.DELAY) + "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.view_sort_config, container);

        initView();
        initEvent();

        return view;
    }

    private void initEvent() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inteface.cancelClicked(JXSortConfigView.this, null);
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取参数
                String countStr = etCount.getText().toString();
                String delayStr = etDelay.getText().toString();

                if (TextUtils.isEmpty(countStr.trim()) || TextUtils.isEmpty(delayStr.trim()) ){
                    Toast.makeText(getContext(), "请输入数组长度和排序速度", Toast.LENGTH_SHORT).show();
                    return;
                } else if(Integer.parseInt(countStr.trim()) <= 0 || Integer.parseInt(delayStr.trim()) <= 0){
                    Toast.makeText(getContext(), "速度或者数组长度必须大于0", Toast.LENGTH_SHORT).show();
                    return;
                }

                ConfigDialogParams params = new ConfigDialogParams(
                        Integer.parseInt(countStr.trim()),
                        Integer.parseInt(delayStr.trim())
                );

                inteface.okClicked(JXSortConfigView.this, params);
            }
        });
    }


    public interface ISortConfigDialog{
         void cancelClicked(JXSortConfigView view, ConfigDialogParams params);
        void okClicked(JXSortConfigView view, ConfigDialogParams params);
    }

}
