package com.john.jxalgorithms.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.john.jxalgorithms.R;
import com.john.jxalgorithms.bean.ColorBean;
import com.john.jxalgorithms.bean.SorterBean;
import com.john.jxalgorithms.sort.SortType;
import com.john.jxalgorithms.util.ArrayUtil;
import com.john.jxalgorithms.util.SortUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Semaphore;

/**
 * 实现的那个callback来控制surfaceview的控制
 * Created by John on 2016/9/4.
 */
public class JXSortView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "John";

    public IThreadState threadState;

    private Canvas canvas;  // 画布
    private Paint paint;    // 其他柱子的画笔
    private Paint paint2;   // 交换前后柱子的画笔
    private SurfaceHolder holder;
    private SorterBean sorter;  // 排序者
    private Paint drawPaint;
    private Paint textPaint;    // 文字的画笔

    private float rectXWidth = 10; // 排序的柱子的宽度
    private int rectTop = 100;
    private int leftPadding = 10;
    private int svWidth;
    private int svHeight;

    public ISurfaceChanged surfaceChanged;

    Semaphore gate = new Semaphore(1);  // 一个信号量
    private boolean run = false;    // 运行的标志量，暂停或者一步一步执行的时候就会被使用了
    public volatile DrawThread thread;
    private boolean holderIsPrepared;
    private double intMax;
    private long endTime;
    private long startTime;
    private long pauseTime;
    private String typeStr;
    private ColorBean colors;

    // 这个是在代码中动态创建的时候的初始化构造器
    public JXSortView(Context context) {
        super(context);
        initDefaultConfigs();
    }

    public JXSortView(Context context, SorterBean sorter) {
        super(context);
        this.sorter = sorter;   // 排序的sorter
        initDefaultConfigs();
        thread = new DrawThread();  // 初始化这个sortview的绘画线程
    }

    // 这个是在布局中初始化的构造器
    public JXSortView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 添加surface的监听，因为surfaceview的初始化可能比较慢，所以需要通过这个holder接口来实施监听
        initDefaultConfigs();
    }

    // 初始化默认的参数门
    private void initDefaultConfigs() {
        if (colors == null) {
            colors = new ColorBean();
            colors.setColor(Color.RED, Color.BLUE, Color.GRAY);
        }

        initPaint();
        initData();
        initEvent();
    }

    // 初始化参数相关
    private void initData() {

    }

    private void initEvent() {
        holder = getHolder();
        holder.addCallback(this);
    }

    private void initPaint() {

        // 默认画笔参数
        paint = new Paint();
        paint.setColor(colors.getEmptyColor());
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);

        // 当前交换矩形的画笔
        paint2 = new Paint();
        paint2.setColor(colors.getFillColor());
        paint2.setStrokeWidth(1);

        drawPaint = paint;

        // text paint
        textPaint = new Paint();
        textPaint.setTextSize(24);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // surfaceview 监听
    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        thread = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        calculateRectParams();  // 计算柱子的参数
        // 这里thread已经启动中的话就不能再次启动，所以在再次启动前先终止他
        // 初始化完成，然后开启一个排序绘图的线程，给线程中传入数组和一个holder用于绘图
        holderIsPrepared = true;
        typeStr = getTypeStr();
        drawRect(sorter.getArray(), null, null);
        surfaceRefreshed();
    }

    public void surfaceRefreshed() {
        if (surfaceChanged != null) {
            Log.e("John", "JXSortView" + " # " + "surface refreshed ...");
            surfaceChanged.onChanged();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private String getTypeStr() {
        switch (sorter.getSortType()) {
            case INSERTION: {
                return "插入排序";
            }
            case MERGE: {
                return "合并排序";
            }
            case QUICK: {
                return "快速排序";
            }
            case HEAP: {
                return "堆排序";
            }
        }
        return null;
    }

    // 根据sorter的数据计算出要绘制的柱状图的参数
    private void calculateRectParams() {
        // 获取surfaceview的宽度和高度
        svWidth = getWidth();
        svHeight = getHeight();
        // 根据sv宽度和数组的数量，计算一个大小
        rectXWidth = (float) (svWidth - 2 * leftPadding) / sorter.getArray().length;
        Log.e("John", "JXSortView" + " # " + "柱子宽度= " + rectXWidth);
        intMax = ArrayUtil.max(sorter.getArray()) + 10;
    }

    public SorterBean getSorter() {
        return sorter;
    }

    // 这里设置为开始排序
    public void setRun() {
        // 当第一次运行的时候
        if (holderIsPrepared || thread == null) {
            thread = new DrawThread();
            thread.start(); // 开始一个线程，可是这里运行很快，那又怎么只运行一下呢
            holderIsPrepared = false;
        }

        run = true;
        gate.release();
    }

    // 开始一步一步的排序
    public void setStep() {
        // 当第一次运行的时候
        if (holderIsPrepared || thread == null) {
            thread = new DrawThread();
            thread.start(); // 开始一个线程，可是这里运行很快，那又怎么只运行一下呢
            holderIsPrepared = false;
        }

        if (holderIsPrepared) {
            pauseTime = System.currentTimeMillis();
        }
        // 这里让run设置为false，
        // 然后在下面进行比较的时候就会进入gate的acquire
        run = false;
        gate.release();
    }

    public void setSortType(SortType type) {
        sorter.setSortType(type);   // 设置了排序类型，之后要再刷新一下surfaceview
    }

    public void setSorter(SorterBean sorter) {
        this.sorter = sorter;
    }

    // 画图
    void drawRect(Double[] a, Double v1, Double v2) {
        canvas = null;
        canvas = holder.lockCanvas();
        if (canvas == null) {
            return;
        }

//        Log.e("John", "JXSortView" + " # " + "colors = " + colors.getBgColor());
        canvas.drawColor(colors.getBgColor());
        rectTop = svHeight - 10;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == v1 || a[i] == v2) {
                drawPaint = paint2;
            } else {
                drawPaint = paint;
            }
            canvas.drawRect(leftPadding + i * rectXWidth,   // 左边
                    rectTop,    // 顶部
                    leftPadding + i * rectXWidth + rectXWidth,  // 右边
                    (float) (rectTop - ((a[i] / intMax) * svHeight)),   // 底部
                    drawPaint);
        }
        drawText();
        holder.unlockCanvasAndPost(canvas);
    }

    private void drawText() {
        if (holderIsPrepared)
            startTime = System.currentTimeMillis();

        endTime = System.currentTimeMillis();
        long peried = 0;
        if (pauseTime > 0) {
            peried = endTime - pauseTime;
        }
        canvas.drawText("类型: " + typeStr,
                40, 30, textPaint);
        canvas.drawText("数组长度: " + sorter.getArray().length +
                        "", 40, 30 + 30,
                textPaint);
        canvas.drawText("排序时间: " +
                        ((endTime - startTime - peried)) + " ms",
                40, 30 + 30 * 2,
                textPaint);
    }

    public void setColors(ColorBean colors) {
        this.colors = colors;
        paint.setColor(colors.getEmptyColor());
        paint2.setColor(colors.getFillColor());
    }

    // 绘图线程类，这个相当于在进行排序的那个线程类chapter14中
    public class DrawThread extends Thread {

        private volatile Boolean stopThread = false;

        // 初始化线程的时候传入一个排序的数组
        public DrawThread() {
        }

        // 线程运行的时候
        @Override
        public void run() {
//            Log.e("John", "DrawThread" + " # " + Arrays.toString(sorter.getArray()));
            startTime = System.currentTimeMillis();
            pauseTime = 0;
            threadState.onThreadStart(this, JXSortView.this);  // 接口
            Comparator<Double> comp = new Comparator<Double>() {
                @Override
                public int compare(Double v1, Double v2) {
                    try {
                        if (run) {
                            SystemClock.sleep(sorter.getDelay());
                        } else {
                            gate.acquire(); // 这里是获取信号量，进入到这里的时候阻塞
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    drawRect(sorter.getArray(), v1, v2);
                    return v1.compareTo(v2);
                }
            };

            // 当马上运行线程的时候就开始

            switch (sorter.getSortType()) {
                case INSERTION: {
                    SortUtil.insertionSort(sorter.getArray(), comp, stopThread);
                }
                break;
                case MERGE: {
                    SortUtil.mergeSort(sorter.getArray(), comp, stopThread);
                }
                break;
                case QUICK: {
                    SortUtil.quickSort(sorter.getArray(), comp, stopThread);
                }
                break;
                case HEAP: {
                    SortUtil.heapSort(sorter.getArray(), comp, stopThread);
                }
                break;
            }
            drawRect(sorter.getArray(), null, null);
            threadState.onThreadEnd(this, JXSortView.this);    // 接口方法
//            Log.e("John", "DrawThread" + " # " + Arrays.toString(sorter.getArray()));
        }
    }

    public interface ISurfaceChanged {
        void onChanged();
    }


}
















































