package com.wooddeep.crane.views;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ProgressBar;


/**
 * https://blog.csdn.net/u012005313/article/details/76047122
 * <p>
 * paint类：
 * https://blog.csdn.net/qq_37918409/article/details/81560446
 * <p>
 * paint设置画笔端始末端样式：Paint.setStrokeCap(Paint.Cap.ROUND)
 * https://blog.csdn.net/weixin_34130389/article/details/87288672
 * <p>
 * paint颜色 渐变等
 * https://blog.csdn.net/pzm1993/article/details/81054409
 *
 * paint 发光
 * https://www.jianshu.com/p/5c558f43ce2e
 **/

public class SimpleProgressbar extends ProgressBar {
    private static final String TAG = "SimpleProgressbar";
    public static final int DEFAULT_UNREACHED_COLOR = 0xFF912CEE;
    public static final int DEFAULT_REACHED_COLOR = 0xFF54FF9F;

    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 未到达进度条颜色
     */
    private int unreachedColor;
    /**
     * 已到达进度条颜色
     */
    private int reachedColor;

    public SimpleProgressbar(Context context) {
        //        super(context);
        this(context, null);
    }

    public SimpleProgressbar(Context context, AttributeSet attrs) {
        //        super(context, attrs);
        this(context, attrs, 0);
    }

    public SimpleProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        unreachedColor = DEFAULT_UNREACHED_COLOR;
        reachedColor = DEFAULT_REACHED_COLOR;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //        super.onDraw(canvas);
        // 获取画布的宽高
        int width = getWidth();
        int height = getHeight();
        // 获取进度条的实际宽高
        int lineWidth = width - getPaddingLeft() - getPaddingRight();
        int lineHeight = height - getPaddingTop() - getPaddingBottom();
        // 获取当前进度
        float ratio = getProgress() * 1.0f / getMax();
        // 获取未完成进度大小
        int unreachedWidth = (int) (lineWidth * (1 - ratio));
        // 获取已完成进度大小
        int reachedWidth = lineWidth - unreachedWidth;
        // 绘制已完成进度条，设置画笔颜色和大小
        paint.setColor(reachedColor);
        paint.setStrokeWidth(lineHeight);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true); // 防止边缘锯齿

        // 计算已完成进度条起点和终点的坐标
        int startX = getPaddingLeft();
        int startY = getHeight() / 2;
        int stopX = startX + reachedWidth;
        int stopY = startY;
        // 画线

        // 颜色渐变
        Shader shader = new LinearGradient(startX + 20, startY, stopX - 20, stopY, new int[]{Color.GREEN, Color.RED}, null, Shader.TileMode.CLAMP);
        //Shader shader = new LinearGradient(100,100,getWidth()-100,300, Color.parseColor("#E91E63"),Color.parseColor("#E91E63"),Shader.TileMode.CLAMP);
        paint.setShader(shader);

        BlurMaskFilter filter = new BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL); // 发光设置
        paint.setMaskFilter(filter);

        canvas.drawLine(startX + 20, startY, stopX - 20, stopY, paint);

    }

}
