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

public class Polygon extends View {

    private static final String TAG = "SimpleProgressbar";
    public static final int DEFAULT_UNREACHED_COLOR = 0xFF912CEE;
    public static final int DEFAULT_REACHED_COLOR = 0xFF54FF9F;
    private ValueAnimator valueAnimator;
    private List<Vertex> vertexs = new ArrayList<>();
    private Paint paint;
    private Paint textPaint;
    private int unreachedColor;
    private int reachedColor;
    private int color = Color.RED;
    private int boderColer = Color.GRAY;
    private boolean alarm = false;
    private boolean flink = false;
    private int type = 0;
    private String name = "0A";
    private BlurMaskFilter blurMaskFilter = new BlurMaskFilter(3f, BlurMaskFilter.Blur.SOLID);

    public void setName(String name) {
        this.name = name;
    }

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

    public Polygon(Context context) {
        this(context, null);
    }

    public Polygon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Polygon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAlpha(0x4c000000);
        unreachedColor = DEFAULT_UNREACHED_COLOR;
        reachedColor = DEFAULT_REACHED_COLOR;
        textPaint = new Paint();
        textPaint.setTextSize(16);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setColor(Color.WHITE);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (vertexs.size() <= 0) return;
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //Paint paint = new Paint();//创建画笔
        paint.setColor(this.color);//为画笔设置颜色
        if (flink) {
            paint.setMaskFilter(blurMaskFilter);
        }

        if (alarm) {
            paint.setColor(Color.rgb(225, 140, 0));
        } else {
            paint.setMaskFilter(blurMaskFilter);
            //paint.setColor(Color.rgb(		189,183,107));
            paint.setColor(this.color);
        }

        //paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);//为画笔设置粗细
        if (this.type == 0) {
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setStyle(Paint.Style.STROKE);//设置空心
        }
        //paint.setColor(Color.LTGRAY);
        //canvas.drawColor(Color.GREEN);//为画布设置颜色
        //设置等腰三角形的三点坐标
        Path path = new Path();//绘制多边形的类
        Vertex start = vertexs.get(0);
        path.moveTo(start.x, start.y);//起始点
        for (int i = 1; i < vertexs.size(); i++) {
            Vertex pointer = vertexs.get(i);
            path.lineTo(pointer.x, pointer.y);//右下角
        }

        if (this.type == 0) {
            path.close(); //闭合图形
        }

        //绘制多边形
        canvas.drawPath(path, paint);

        if (vertexs.size() >= 2) {
            canvas.drawText(name, (vertexs.get(0).x + vertexs.get(1).x) / 2 - 8, vertexs.get(0).y - 5, textPaint);
        }
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

    public void setType(int t) {
        this.type = t;
        invalidate();
    }

}
