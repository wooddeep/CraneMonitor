package com.wooddeep.crane.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wooddeep.crane.views.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * 绘制多边形，并且填充
 * https://blog.csdn.net/qq_41985689/article/details/81501501
 * https://ask.csdn.net/questions/357751
 * https://blog.csdn.net/tianjian4592/article/details/45234419
 **/

public class Curve extends View {

    private static final String TAG = "SimpleProgressbar";
    public static final int DEFAULT_UNREACHED_COLOR = 0xFF912CEE;
    public static final int DEFAULT_REACHED_COLOR = 0xFF54FF9F;
    private ValueAnimator valueAnimator;
    private List<Vertex> vertexs = new ArrayList<>();
    private Paint paint;
    private int unreachedColor;
    private int reachedColor;
    private int color = Color.RED;
    private int boderColer = Color.GRAY;
    private boolean alarm = false;
    private boolean flink = false;

    public int getBoderColer() {
        return boderColer;
    }

    public void setBoderColer(int boderColer) {
        this.boderColer = boderColer;
        invalidate();
    }

    public boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
        invalidate();
    }

    public boolean getFlink() {
        return flink;
    }

    public void setFlink(boolean flink) {
        this.flink = flink;
        invalidate();
    }

    public Curve(Context context) {
        this(context, null);
    }

    public Curve(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Curve(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        unreachedColor = DEFAULT_UNREACHED_COLOR;
        reachedColor = DEFAULT_REACHED_COLOR;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        Paint paint = new Paint();//创建画笔
        paint.setColor(Color.BLACK);//为画笔设置颜色
        if (flink) {
            paint.setMaskFilter(new BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID));
        }

        if (alarm) {
            paint.setColor(Color.rgb(225, 140, 0));
        } else {
            paint.setMaskFilter(new BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID));
            //paint.setColor(Color.rgb(46, 139, 87));
        }

        //paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);//为画笔设置粗细
        paint.setStyle(Paint.Style.STROKE);//设置空心
        //paint.setColor(Color.LTGRAY);
        //canvas.drawColor(Color.GREEN);//为画布设置颜色
        //设置等腰三角形的三点坐标
        //Path path = new Path();//绘制多边形的类
        //Vertex start = vertexs.get(0);
        //path.moveTo(start.x, start.y);//起始点
        for (int i = 0; i < vertexs.size() - 1; i++) {
            Vertex start = vertexs.get(i);
            Vertex end = vertexs.get(i + 1);
            canvas.drawLine(start.x, start.y, end.x, end.y, paint);
        }

        System.out.println("####### draw protect area!!");

        //path.close();//闭合图形
        //canvas.drawPath(path, paint);
    }

    /**
     * 设置当前值
     *
     * @param vertexs: 顶点
     */
    public void setValue(List<Vertex> vertexs) {
        this.vertexs = vertexs;
        invalidate();
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

}
