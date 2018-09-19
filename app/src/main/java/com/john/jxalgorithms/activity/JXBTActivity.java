package com.john.jxalgorithms.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.john.jxalgorithms.R;
import com.john.jxalgorithms.ui.JXBTView;

/**
 * Created by John on 2016/9/7.
 */
public class JXBTActivity extends AppCompatActivity {

    private JXBTView btView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        btView = (JXBTView) findViewById(R.id.sv_bt);

    }
}
