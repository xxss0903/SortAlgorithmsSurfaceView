package com.john.jxalgorithms.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.john.jxalgorithms.R;
import com.john.jxalgorithms.bean.ColorBean;
import com.john.jxalgorithms.bean.SorterBean;
import com.john.jxalgorithms.db.SorterDAO;
import com.john.jxalgorithms.ui.ColorBar;
import com.john.jxalgorithms.ui.ConfigDialogParams;
import com.john.jxalgorithms.ui.IThreadState;
import com.john.jxalgorithms.ui.JXSortConfigView;
import com.john.jxalgorithms.ui.JXSortView;
import com.john.jxalgorithms.sort.SortType;
import com.john.jxalgorithms.util.CommonUtils;
import com.john.jxalgorithms.util.Constant;
import com.john.jxalgorithms.util.SPUtils;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 绘图的activity
 * Created by John on 2016/9/4.
 */
public class JXDrawActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int INIT_OK = 200;
    private static final String TAG = "John";

    private boolean isRunning = false;  // 正在运行排序

    private ImageButton btnStart;
    private ImageButton btnStop;
    private ImageButton btnNewArray;
    private ImageButton btnNewSV;
    private ImageButton btnSVConfig;
    private Button btnCancelColor;
    private Button btnOkColor;
    private ColorBar cbFill;
    private ColorBar cbEmpty;
    private ColorBar cbBG;
    private ImageView ivShotScreen;

    private ListView lvLeftMenu;
    private LinearLayout llSortMain;    // 排序的主界面，用来添加新的排序视图的
    private LinearLayout llSorterContainer;   // sortview的父容器
    private DrawerLayout drawerLayout;

    private List<SorterBean> sorters;   // 数据库中取出来的sorter
    private Double[] currentArray; // 当前数组
    private List<JXSortView> sortViews = new ArrayList<>();
    private ColorBean colors;

    private SorterDAO dao;
    private JXSortConfigView dialog;
    private ConfigDialogParams sortParams;

    private int isSortingCount = 0;

    private AlertDialog colorDialog;
    private android.support.v7.app.ActionBar mActionBar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_OK:
                    Toast.makeText(JXDrawActivity.this, "初始化参数完成", Toast.LENGTH_SHORT).show();
                    initViewWithData();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        for (JXSortView sv :
                sortViews) {
            sv.surfaceDestroyed(sv.getHolder());
        }
        dao.deleteAll();
        // 保存当前的sorters
        for (JXSortView sv : sortViews) {
            dao.setSorter(sv.getSorter());
        }
        SPUtils.setInt(getApplicationContext(), Constant.COLOR_FILL, colors.getFillColor());
        SPUtils.setInt(getApplicationContext(), Constant.COLOR_EMPTY, colors.getEmptyColor());
        SPUtils.setInt(getApplicationContext(), Constant.COLOR_BG, colors.getBgColor());
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        initView();
        initData();
        initEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mToggle.onOptionsItemSelected(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 开始排序、暂停排序（可是如何才能暂停排序呢?这里surfaceview的绘制是每个画布进行
    // 一帧一帧的绘制的呀）
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_draw: {
                // 开始排序,循环链表中的sortview
                for (JXSortView sortView :
                        sortViews) {
                    if (sortView != null) {
                        sortView.setRun();
                    }
                }
            }
            break;
            case R.id.btn_step_draw: {
                for (JXSortView sortView :
                        sortViews) {
                    if (sortView != null) {
                        sortView.setStep();
                    }
                }
            }
            break;
            case R.id.btn_new_array: {   // 生成新排序数组
                createNewArray();
            }
            break;
            case R.id.btn_add_sortview: {   //  添加新的排序
                if (sortViews.size() >= 4) {
                    Toast.makeText(JXDrawActivity.this, "排序方式已经到达上限", Toast.LENGTH_SHORT).show();
                    return;
                }

                SorterBean sorter = new SorterBean(SortType.MERGE, 20, currentArray);
                dao.setSorter(sorter);  // 保存sorter到数据库中，下次直接调用
                addSorterView(sorter);
            }
            break;
            case R.id.btn_set_sort_config: {
                showConfigDialog(); // 配置排序的一些参数
            }
            break;
        }
    }

    private void initEvent() {
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnNewArray.setOnClickListener(this);
        btnNewSV.setOnClickListener(this);
        btnSVConfig.setOnClickListener(this);

        // 设置监听
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        //抽屉效果变化的监听
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

            // 滑动的监听，这里设置滑动时候的动画效果
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View content = drawerLayout.getChildAt(0);
                View menu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT")) {
                    float leftScale = 1 - 0.3f * scale;

                    ViewHelper.setScaleX(menu, leftScale);
                    ViewHelper.setScaleY(menu, leftScale);
                    ViewHelper.setAlpha(menu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(content, menu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(content, 0);
                    ViewHelper.setPivotY(content, content.getMeasuredHeight() / 2);
                    content.invalidate();
                    ViewHelper.setScaleX(content, rightScale);
                    ViewHelper.setScaleY(content, rightScale);
                }
            }

            // 当drawer被打开
            @Override
            public void onDrawerOpened(View drawerView) {
                Log.e(TAG, "JXDrawActivity" + "打开抽屉了");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.e(TAG, "JXDrawActivity" + "关闭抽屉了");
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                Log.e(TAG, "JXDrawActivity" + "抽屉状态改变了");
            }
        });

        // 监听排序类型改变
        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (isRunning) {
                            Toast.makeText(JXDrawActivity.this, "正在排序，不能删除", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.e("John", "JXDrawActivity" + " # " + "删除所有sorter");
                        dao.deleteAll();
                        clearScreen();
                        break;
                    case 1:
                        if (isRunning) {
                            Toast.makeText(JXDrawActivity.this, "正在排序...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 颜色
                        showColorSetting();
                        break;
                    case 2:
                        // 关于
                        showAboutView();
                        break;
                }
            }
        });
    }

    // 显示关于对话框
    private void showAboutView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.soft_version);
        builder.setMessage(R.string.soft_description);
        builder.show();
    }

    // 调出颜色选择器
    private void showColorSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 颜色设置的那个view，这个view添加到dialog上面
        View dialogView = LayoutInflater.from(this).inflate(R.layout.view_color_settings, null);
        btnCancelColor = (Button) dialogView.findViewById(R.id.btn_cancel_color_setting);
        btnOkColor = (Button) dialogView.findViewById(R.id.btn_ok_color_setting);
        cbFill = (ColorBar) dialogView.findViewById(R.id.cb_fill);
        cbEmpty = (ColorBar) dialogView.findViewById(R.id.cb_empty);
        cbBG = (ColorBar) dialogView.findViewById(R.id.cb_bg);

        btnOkColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fillColor = cbFill.getColor();
                int emptyColor = cbEmpty.getColor();
                int bgColor = cbBG.getColor();
                SPUtils.setInt(getApplicationContext(), Constant.COLOR_FILL, fillColor);
                SPUtils.setInt(getApplicationContext(), Constant.COLOR_EMPTY, emptyColor);
                SPUtils.setInt(getApplicationContext(), Constant.COLOR_BG, bgColor);
                lvLeftMenu.setBackgroundColor(bgColor);
                colors.setColor(fillColor, emptyColor, bgColor);

                // 循环设置颜色
                for (JXSortView sv :
                        sortViews) {
                    sv.setColors(colors);
                    refreshSortView(sv);
                }
                colorDialog.dismiss();
            }
        });

        btnCancelColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorDialog.dismiss();
            }
        });

        // 回显color的数值
        cbFill.setColor(SPUtils.getInt(getApplicationContext(), Constant.COLOR_FILL));
        Log.e("John", "JXDrawActivity" + " # " + "color fill = " + SPUtils.getInt(getApplicationContext(), Constant.COLOR_FILL));
        cbEmpty.setColor(SPUtils.getInt(getApplicationContext(), Constant.COLOR_EMPTY));
        Log.e("John", "JXDrawActivity" + " # " + "color empty = " + SPUtils.getInt(getApplicationContext(), Constant.COLOR_EMPTY));
        cbBG.setColor(SPUtils.getInt(getApplicationContext(), Constant.COLOR_BG));
        Log.e("John", "JXDrawActivity" + " # " + "color bg = " + SPUtils.getInt(getApplicationContext(), Constant.COLOR_BG));

        // 这里将显示的view放到dialog上面
        builder.setView(dialogView);
        colorDialog = builder.show();
        // 隐藏左边的抽屉栏
//        llColorSetting.setVisibility(View.VISIBLE);
    }

    // 清除当前屏幕上的所有视图
    private void clearScreen() {
        // 防止屏幕闪烁
        Bitmap bitmap = CommonUtils.shotScreen(this);
        ivShotScreen.setVisibility(View.VISIBLE);
        ivShotScreen.setImageBitmap(bitmap);

        llSorterContainer.removeAllViews();
        isSortingCount = 0;
        sortViews.clear();

        ivShotScreen.setVisibility(View.GONE);
    }

    private void initData() {
        loadColors();
        sortParams = new ConfigDialogParams(30, 200);   // 初始化时30个 数组，200秒的延迟
        dao = new SorterDAO(getApplicationContext());
        loadData();
    }

    /**
     * 加载颜色，从sp中
     */
    private void loadColors() {
        colors = new ColorBean();
        colors.setFillColor(SPUtils.getInt(this, Constant.COLOR_FILL));
        colors.setEmptyColor(SPUtils.getInt(this, Constant.COLOR_EMPTY));
        colors.setBgColor(SPUtils.getInt(this, Constant.COLOR_BG));
    }

    /**
     * 从sp中加载数据
     */
    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取sp中保存的排序的参数（数组长度，排序类型，延时长度）
                int arrayCount = SPUtils.getInt(getApplication(), Constant.ARRAY_COUNT);
                int delay = SPUtils.getInt(getApplicationContext(), Constant.DELAY);
                sortParams.setCount(arrayCount);
                sortParams.setDelay(delay);
                currentArray = CommonUtils.generateRandomArray(sortParams.getCount());
                // 排序对象sorter，从数据库中去获取
                sorters = dao.queryAll();
                // 初始化完成之后就发送一个消息给主线程更新界面
                handler.sendEmptyMessage(INIT_OK);
            }
        }).start();
    }

    private void initView() {
        initActionBar();

        ivShotScreen = (ImageView) findViewById(R.id.iv_screen_shot);
        lvLeftMenu = (ListView) findViewById(R.id.lv_left_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.dr_layout);
        btnStart = (ImageButton) findViewById(R.id.btn_start_draw);
        btnStop = (ImageButton) findViewById(R.id.btn_step_draw);
        btnNewArray = (ImageButton) findViewById(R.id.btn_new_array);
        btnNewSV = (ImageButton) findViewById(R.id.btn_add_sortview);
        btnSVConfig = (ImageButton) findViewById(R.id.btn_set_sort_config);
        llSortMain = (LinearLayout) findViewById(R.id.ll_sort_main);

        llSorterContainer = (LinearLayout) findViewById(R.id.ll_sort_view_container);
    }

    // 初始化actionbar，但是那个横线变箭头的是在哪里设置的呢？
    private void initActionBar() {
        mActionBar = getSupportActionBar();

        mActionBar.setTitle("排序");
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dr_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        mToggle.syncState();
        mDrawerLayout.setDrawerListener(mToggle);
    }

//    private void writeSorterToDB() {
//        SorterBean sorter = new SorterBean();
//        sorter.setArray(CommonUtils.generateRandomArray(sortParams.getCount()));
//        sorter.setCount(4);
//        sorter.setDelay(1000);
//        sorter.setSortType(SortType.INSERTION);
//
//        dao.setSorter(sorter);
//    }

    // 初始化完毕数据和视图之后给界面中添加带有数据的视图在第一次初始化的时候就需要添加一个sortview
    private void initViewWithData() {
        // 添加sorterview到子控件
        // 1.  sorters中的数据个数添加sortview到llSVContainer中
        for (int i = 0; i < sorters.size(); i++) {
            addSorterView(sorters.get(i));
        }
    }

    // 添加sortview在现有视图下面
    private void addSorterView(final SorterBean sorter) {
        final JXSortView sortView = new JXSortView(this, sorter);
        // 监听线程的状态，当开始排序之后就不能改变数组了，直到排序完成
        sortView.threadState = new IThreadState() {

            @Override
            public void onThreadEnd(JXSortView.DrawThread thread, final JXSortView sv) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sv.setLongClickable(true);
                        isSortingCount++;
                        if (isSortingCount == sortViews.size()) {
                            isRunning = false;
                            btnNewArray.setEnabled(true);
                            btnNewSV.setEnabled(true);
                            btnSVConfig.setEnabled(true);
                        }
                    }
                });
                // 当线程完成的时候流让他制空
                Log.e("John", "JXDrawActivity" + " # " + "排序线程结束");
            }

            @Override
            public void onThreadStart(JXSortView.DrawThread thread, final JXSortView sv) {
                isRunning = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnNewArray.setEnabled(false);
                        btnNewSV.setEnabled(false);
                        btnSVConfig.setEnabled(false);
                        isSortingCount = 0;
                        sv.setLongClickable(false);
                    }
                });
                Log.e("John", "JXDrawActivity" + " # " + "排序线程开始");
            }
        };

        // 给sortview添加参数
        addSortView1(sortView);
        // 添加了之后刷新sortview
        for (JXSortView sv : sortViews) {
            sv.setColors(colors);
            refreshSortView(sv);
        }
        setEventToSortView(sortView);
    }

    // 给新添加的sortview添加监听事件
    private void setEventToSortView(final JXSortView sortView) {
        // 给sortview添加监听
        sortView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(JXDrawActivity.this);
                View view = View.inflate(JXDrawActivity.this, R.layout.dialog_sort_type, null);

                // 初始化那个dialog中子控件
                final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_sort_type_dialog);
                RadioButton rbHeap = (RadioButton) view.findViewById(R.id.rb_heap_dialog);
                RadioButton rbQuick = (RadioButton) view.findViewById(R.id.rb_quick_dialog);
                RadioButton rbInsertion = (RadioButton) view.findViewById(R.id.rb_insertion_dialog);
                RadioButton rbMerge = (RadioButton) view.findViewById(R.id.rb_merge_dialog);

                switch (sortView.getSorter().getSortType()) {
                    case INSERTION:
                        rbInsertion.setChecked(true);
                        break;
                    case MERGE:
                        rbMerge.setChecked(true);
                        break;
                    case HEAP:
                        rbHeap.setChecked(true);
                        break;
                    case QUICK:
                        rbQuick.setChecked(true);
                        break;
                }

//                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//                        Log.e("John", "JXDrawActivity" + " # " + "checkedId = " + checkedId);
//                        switch (checkedId) {
//                            case R.id.rb_insertion_dialog:
//                                sortView.setSortType(SortType.INSERTION);
//                                break;
//                            case R.id.rb_merge_dialog:
//                                sortView.setSortType(SortType.MERGE);
//                                break;
//                            case R.id.rb_heap_dialog:
//                                sortView.setSortType(SortType.HEAP);
//                                break;
//                            case R.id.rb_quick_dialog:
//                                sortView.setSortType(SortType.QUICK);
//                                break;
//                        }
//                    }
//                });
                builder.setView(view);
                builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();   // 这里如何在非点击这个按钮的时候dismiss这些dialog呢
                    }
                });
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 这里选择了排序方式之后应该要刷新一下surfaceview，让他重新进入那个准备一下，这样才能重新run进程重置排序方式，否则没什么用
                        switch (rg.getCheckedRadioButtonId()) {
                            case R.id.rb_insertion_dialog:
                                sortView.setSortType(SortType.INSERTION);
                                break;
                            case R.id.rb_merge_dialog:
                                sortView.setSortType(SortType.MERGE);
                                break;
                            case R.id.rb_heap_dialog:
                                sortView.setSortType(SortType.HEAP);
                                break;
                            case R.id.rb_quick_dialog:
                                sortView.setSortType(SortType.QUICK);
                                break;
                        }

                        refreshSortView(sortView);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        dao.delete(sortView.getSorter().getId());
                        llSorterContainer.removeView(sortView);
                        sortViews.remove(sortView);


                    }
                });

                dialog = builder.show();
                return true;
            }
        });
    }

    private void addSortView1(JXSortView sortView) {
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);    // layout_weight
        sortView.setLayoutParams(params);
        llSorterContainer.addView(sortView);
        sortViews.add(sortView);
    }

    private void refreshSortView(JXSortView sortView) {
        // 防止屏幕闪烁
        Bitmap bitmap = CommonUtils.shotScreen(JXDrawActivity.this);
        ivShotScreen.setVisibility(View.VISIBLE);
        ivShotScreen.setImageBitmap(bitmap);
        // 防止屏幕闪烁
        sortView.setVisibility(View.INVISIBLE);
        sortView.setVisibility(View.VISIBLE);

        ivShotScreen.setVisibility(View.GONE);
    }

    private void createNewArray() {
        currentArray = CommonUtils.generateRandomArray(sortParams.getCount());
        Log.e("John", "JXSortView" + " # " + "sorter.array = " + Arrays.toString(currentArray));
        for (JXSortView sv : sortViews) {
            Double[] tmp = new Double[currentArray.length];
            CommonUtils.copyArray(currentArray, tmp);
            sv.getSorter().setArray(tmp);
            refreshSortView(sv);
        }
    }

    // 调用那个显示
    private void showConfigDialog() {
        dialog = new JXSortConfigView();
        // 获取点击的按钮的接口
        dialog.inteface = new JXSortConfigView.ISortConfigDialog() {
            @Override
            public void cancelClicked(JXSortConfigView view, ConfigDialogParams params) {
                dialog.dismiss();
            }

            @Override
            public void okClicked(JXSortConfigView view, ConfigDialogParams params) {
                sortParams = params;
                // 保存配置的参数
                SPUtils.setInt(getApplicationContext(), Constant.ARRAY_COUNT, params.getCount());
                SPUtils.setInt(getApplicationContext(), Constant.DELAY, params.getDelay());
                refreshConfigs();
                dialog.dismiss();
            }
        };
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    // 点击后更新各个视图的参数
    private void refreshConfigs() {
        // 产生新的数组
        createNewArray();
        // 循环设置排序的速度
        for (JXSortView sv : sortViews) {
            sv.getSorter().setDelay(sortParams.getDelay());
            refreshSortView(sv);
        }
    }
}
