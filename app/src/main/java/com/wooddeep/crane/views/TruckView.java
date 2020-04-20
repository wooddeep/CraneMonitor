package com.wooddeep.crane.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.wooddeep.crane.R;

// https://www.2cto.com/kf/201509/442508.html
// https://www.stickpng.com/img/tools-and-parts/cranes/tower-crane
// https://pngtree.com/so/tower-crane
// icons: https://icons8.com/icon/81183/tower-crane

@SuppressWarnings("unused")
public class TruckView extends View {

    private Paint paintFill;  // 实心画笔
    private Paint paintFill2;
    private Paint paintFill3;
    private Paint paintBorder; // 边界画笔
    private Paint paintWheel;

    public int craneType = 0;
    public float armLenth = 300;
    public float hookHeight = 100;
    public float armAngle = 0;

    private int hookX = 100;
    private int hookY = 100;

    public TruckView(Context context, int defMinRadio, int defRingWidth) {
        this(context, null);
    }

    public TruckView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TruckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.TruckView);
        craneType = attrArray.getInteger(R.styleable.TruckView_truck_crane_type, 0);
        armLenth = attrArray.getInteger(R.styleable.TruckView_truck_arm_length, 300);
        hookHeight = attrArray.getInteger(R.styleable.TruckView_truck_hook_height, 100);
        armAngle = attrArray.getFloat(R.styleable.TruckView_truck_arm_angle, 0.0f);

        paintFill = new Paint();
        paintBorder = new Paint();
        paintFill2 = new Paint();
        paintFill3 = new Paint();
        paintWheel = new Paint();

        paintFill.setColor(Color.rgb(255, 140, 0));
        paintFill.setStrokeJoin(Paint.Join.ROUND);
        paintFill.setStrokeCap(Paint.Cap.SQUARE);
        paintFill.setStrokeWidth(2);
        paintFill.setAntiAlias(true);
        paintFill.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));

        paintFill2.setColor(Color.rgb(255, 140, 0));
        paintFill2.setStrokeJoin(Paint.Join.ROUND);
        paintFill2.setStrokeCap(Paint.Cap.SQUARE);
        paintFill2.setStrokeWidth(2);
        paintFill2.setAntiAlias(true);
        paintFill2.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));

        paintFill3.setColor(Color.rgb(255, 140, 0));
        paintFill3.setStrokeJoin(Paint.Join.ROUND);
        paintFill3.setStrokeCap(Paint.Cap.SQUARE);
        paintFill3.setStrokeWidth(2);
        paintFill3.setAntiAlias(true);
        paintFill3.setColor(Color.WHITE);
        //paintFill3.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));


        paintFill.setColor(Color.rgb(255, 140, 0));
        paintFill.setStrokeJoin(Paint.Join.ROUND);
        paintFill.setStrokeCap(Paint.Cap.SQUARE);
        paintFill.setStrokeWidth(2);
        paintFill.setAntiAlias(true);
        paintFill.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));

        paintBorder.setColor(Color.rgb(255, 140, 0));
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeWidth(2);
        paintBorder.setAntiAlias(true);

        paintWheel.setColor(Color.GRAY);
        paintWheel.setStrokeJoin(Paint.Join.ROUND);
        paintWheel.setStyle(Paint.Style.STROKE);
        paintWheel.setStrokeCap(Paint.Cap.ROUND);
        paintWheel.setAntiAlias(true);

    }

    /*
     *       1
     *     +---+ 6
     *  2 /     \______________________
     *  3 |     / 5- O O | O O O | O O |
     *    +----+
     *       4
     */

    void drawRotateCap(Canvas canvas, int color, float theta, float widthRate, float heightRate, float wheelRadius, float x, float y) {
        float capWidth = widthRate * wheelRadius;
        float capHeight = heightRate * wheelRadius;

        Paint p = new Paint();
        Path path = new Path();
        p.setColor(color);

        //x = x - length * (float) Math.cos(Math.toRadians(theta));
        //y = y - length * (float) Math.sin(Math.toRadians(theta));

        path.moveTo(x - (capWidth / 2) * (float) Math.sin(Math.toRadians(theta)), y + (capWidth / 2) * (float) Math.cos(Math.toRadians(theta)));

        path.lineTo(x - (capWidth / 2) * (float) Math.sin(Math.toRadians(theta)) - capHeight * (float) Math.cos(Math.toRadians(theta)),
            y + (capWidth / 2) * (float) Math.cos(Math.toRadians(theta)) - capHeight * (float) Math.sin(Math.toRadians(theta)));

        path.lineTo(x + (capWidth / 2) * (float) Math.sin(Math.toRadians(theta)) - capHeight * (float) Math.cos(Math.toRadians(theta)),
            y - (capWidth / 2) * (float) Math.cos(Math.toRadians(theta)) - capHeight * (float) Math.sin(Math.toRadians(theta)));

        //path.lineTo(x + (capWidth / 2) * (float) Math.sin(Math.toRadians(theta) - capHeight * (float) Math.cos(Math.toRadians(theta))),
        //    y - (capWidth / 2) * (float) Math.cos(Math.toRadians(theta)) - capHeight * (float) Math.sin(Math.toRadians(theta)));

        path.lineTo(x + (capWidth / 2) * (float) Math.sin(Math.toRadians(theta)), y - (capWidth / 2) * (float) Math.cos(Math.toRadians(theta)));

        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
    }

    float fastenX1 = 0;
    float fastenY1 = 0;

    void drawFasten(Canvas canvas, int color, float theta, float widthRate, float heightRate, float wheelRadius, float x, float y) {
        float capWidth = widthRate * wheelRadius;
        float capHeight = heightRate * wheelRadius;

        Paint p = new Paint();
        Path path = new Path();
        p.setColor(color);

        path.moveTo(x - 0 * (float) Math.sin(Math.toRadians(theta)), y + 0 * (float) Math.cos(Math.toRadians(theta)));

        path.lineTo(x - 0 * (float) Math.sin(Math.toRadians(theta)) - capHeight * (float) Math.cos(Math.toRadians(theta)),
            y + 0 * (float) Math.cos(Math.toRadians(theta)) - capHeight * (float) Math.sin(Math.toRadians(theta)));

        path.lineTo(x + (capWidth / 2) * (float) Math.sin(Math.toRadians(theta)) - capHeight * (float) Math.cos(Math.toRadians(theta)),
            y - (capWidth / 2) * (float) Math.cos(Math.toRadians(theta)) - capHeight * (float) Math.sin(Math.toRadians(theta)));

        fastenX1 = x + (capWidth / 2) * (float) Math.sin(Math.toRadians(theta));
        fastenY1 = y - (capWidth / 2) * (float) Math.cos(Math.toRadians(theta));

        path.lineTo(x + (capWidth / 2) * (float) Math.sin(Math.toRadians(theta)), y - (capWidth / 2) * (float) Math.cos(Math.toRadians(theta)));

        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
    }

    private void drawTitle(Canvas canvas) {

        Path path = new Path();
        Paint mPaint = new Paint();
        RectF rectF = new RectF(200,200,600,600);
        path.addArc(rectF,0,120);
        mPaint.setTextSize(50);
        mPaint.setStyle(Paint.Style.FILL);
        // 绘制路径
        canvas.drawPath(path, mPaint);
        String text = "happy everyday";
        canvas.drawTextOnPath(text, path, 0f, 0f, mPaint);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        double theta = armAngle;
        int borderWidth = 0;
        int realWidth = getMeasuredWidth();   // 控件的实际宽度
        int realHeight = getMeasuredHeight(); // 控件的实际高度

        float wheelRadius = (float) (realWidth / (17 + 14 * Math.tan(Math.toRadians(30)))); // 轮胎半径

        paintBorder.setColor(Color.DKGRAY);

        // 画机车驾驶舱
        float cockpitThick = wheelRadius * 0.5f; // 驾驶舱厚
        paintFill.setStrokeWidth(cockpitThick);

        // 1.
        canvas.drawLine(1.5f * cockpitThick, realHeight - 3f * wheelRadius, 4f * cockpitThick, realHeight - 3f * wheelRadius, paintFill);
        // 2.
        canvas.drawLine(0.5f * cockpitThick, realHeight - 2f * wheelRadius, 1.5f * cockpitThick, realHeight - 3f * wheelRadius, paintFill);
        // 3.
        canvas.drawLine(0.5f * cockpitThick, realHeight - 2f * wheelRadius, 0.5f * cockpitThick, realHeight - wheelRadius, paintFill);
        // 4
        canvas.drawLine(0, realHeight - wheelRadius, 2f * wheelRadius, realHeight - wheelRadius, paintFill);
        canvas.drawLine(0, realHeight - 1.3f * wheelRadius, 2f * wheelRadius, realHeight - 1.3f * wheelRadius, paintFill);
        // 5.
        paintFill.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(2f * wheelRadius, realHeight - wheelRadius, 2.5f * wheelRadius, realHeight - 2f * wheelRadius, paintFill);
        canvas.drawLine(1.8f * wheelRadius, realHeight - wheelRadius, 2.5f * wheelRadius, realHeight - 2f * wheelRadius, paintFill);

        // 6.
        canvas.drawLine(4f * cockpitThick, realHeight - 3f * wheelRadius, 2.5f * wheelRadius, realHeight - 2f * wheelRadius, paintFill);
        canvas.drawLine(4f * cockpitThick, realHeight - 3f * wheelRadius, 2.4f * wheelRadius, realHeight - 2f * wheelRadius, paintFill);

        Paint p = new Paint();
        p.setColor(Color.rgb(255, 140, 0));
        Path path = new Path();
        path.moveTo(2.3f * wheelRadius, realHeight - 3f * wheelRadius);// 此点为多边形的起点
        path.lineTo(6f * wheelRadius, realHeight - 3f * wheelRadius);
        path.lineTo(6.5f * wheelRadius, realHeight - 2f * wheelRadius);
        path.lineTo(2.8f * wheelRadius, realHeight - 2f * wheelRadius);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 画底板
        p.setColor(Color.GRAY);//
        path = new Path();
        path.moveTo(0, realHeight - 0.73f * wheelRadius);// 此点为多边形的起点
        path.lineTo(0.5f * wheelRadius, realHeight - 0.73f * wheelRadius);
        path.lineTo(0.5f * wheelRadius, realHeight - 0.46f * wheelRadius);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        paintFill2.setStrokeWidth(0.5f * cockpitThick);
        paintFill2.setStrokeCap(Paint.Cap.ROUND);
        paintFill2.setColor(Color.GRAY);
        canvas.drawLine(0.5f * wheelRadius, realHeight - 0.6f * wheelRadius, 2.25f * wheelRadius, realHeight - 0.6f * wheelRadius, paintFill2);
        canvas.drawLine(2.25f * wheelRadius, realHeight - 0.6f * wheelRadius, 2.85f * wheelRadius, realHeight - 1.8f * wheelRadius, paintFill2);
        canvas.drawLine(2.85f * wheelRadius, realHeight - 1.8f * wheelRadius, realWidth - 2f * wheelRadius, realHeight - 1.8f * wheelRadius, paintFill2);

        paintWheel.setStrokeWidth(0.3f * wheelRadius);
        // 画第一组轮子
        canvas.drawCircle(3.5f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.65f * wheelRadius, paintWheel);
        canvas.drawCircle(3.5f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.15f * wheelRadius, paintWheel);
        canvas.drawCircle(5.2f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.65f * wheelRadius, paintWheel);
        canvas.drawCircle(5.2f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.15f * wheelRadius, paintWheel);

        // 第一个分割
        path = new Path();
        p.setColor(Color.GRAY);
        path.moveTo(5.7f * wheelRadius, realHeight - 1.72f * wheelRadius);// 此点为多边形的起点
        path.lineTo(8.4f * wheelRadius, realHeight - 1.72f * wheelRadius);
        path.lineTo(7.5f * wheelRadius, realHeight - 0.4f * wheelRadius);
        path.lineTo(6.5f * wheelRadius, realHeight - 0.4f * wheelRadius);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 画第二组轮子
        canvas.drawCircle(9.0f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.65f * wheelRadius, paintWheel);
        canvas.drawCircle(10.7f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.65f * wheelRadius, paintWheel);
        canvas.drawCircle(9.0f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.15f * wheelRadius, paintWheel);
        canvas.drawCircle(10.7f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.15f * wheelRadius, paintWheel);

        // 第二个分割
        path = new Path();
        p.setColor(Color.GRAY);
        path.moveTo(11.2f * wheelRadius, realHeight - 1.72f * wheelRadius);// 此点为多边形的起点
        path.lineTo(13.9f * wheelRadius, realHeight - 1.72f * wheelRadius);
        path.lineTo(13f * wheelRadius, realHeight - 0.4f * wheelRadius);
        path.lineTo(12f * wheelRadius, realHeight - 0.4f * wheelRadius);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 画第三组轮子
        canvas.drawCircle(14.5f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.65f * wheelRadius, paintWheel);
        canvas.drawCircle(16.2f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.65f * wheelRadius, paintWheel);
        canvas.drawCircle(17.9f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.65f * wheelRadius, paintWheel);

        canvas.drawCircle(14.5f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.15f * wheelRadius, paintWheel);
        canvas.drawCircle(16.2f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.15f * wheelRadius, paintWheel);
        canvas.drawCircle(17.9f * wheelRadius, realHeight - 0.8f * wheelRadius, 0.15f * wheelRadius, paintWheel);

        // 第三个分割
        path = new Path();
        p.setColor(Color.GRAY);
        path.moveTo(18.5f * wheelRadius, realHeight - 1.72f * wheelRadius);// 此点为多边形的起点
        path.lineTo(21.2f * wheelRadius, realHeight - 1.72f * wheelRadius);
        path.lineTo(21.2f * wheelRadius, realHeight - 0.4f * wheelRadius);
        path.lineTo(19f * wheelRadius, realHeight - 0.4f * wheelRadius);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 液压支撑杆
        float x = 19f * wheelRadius;
        float y = realHeight - 3.2f * wheelRadius;

        float hsx = x - 8f * wheelRadius * (float) Math.cos(Math.toRadians(theta));
        float hsy = y - 8f * wheelRadius * (float) Math.sin(Math.toRadians(theta));

        float hsxb = (hsx - 0.9f * wheelRadius * (float) Math.sin(Math.toRadians(theta)) +
            hsx - 2.1f * wheelRadius * (float) Math.sin(Math.toRadians(theta))) / 2;
        float hsyb = (hsy + 0.9f * wheelRadius * (float) Math.cos(Math.toRadians(theta)) +
            hsy + 2.1f * wheelRadius * (float) Math.cos(Math.toRadians(theta))) / 2;
        Paint paint = new Paint();
        paint.setStrokeWidth(0.5f * wheelRadius);
        paint.setColor(Color.GRAY);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(hsxb, hsyb, 14.5f * wheelRadius, realHeight - 2.7f * wheelRadius, paint);

        // 塔基驾驶仓
        path = new Path();
        p.setColor(Color.rgb(255, 140, 0));
        path.moveTo(14f * wheelRadius, realHeight - 2f * wheelRadius);
        path.lineTo(14f * wheelRadius, realHeight - 2.7f * wheelRadius);//
        path.lineTo(14.5f * wheelRadius, realHeight - 3.8f * wheelRadius);// 此点为多边形的起点
        path.lineTo(16.5f * wheelRadius, realHeight - 3.8f * wheelRadius);// 此点为多边形的起点
        path.lineTo(16.8f * wheelRadius, realHeight - 3.2f * wheelRadius);
        path.lineTo(20.3f * wheelRadius, realHeight - 3.2f * wheelRadius);
        path.lineTo(20.3f * wheelRadius, realHeight - 4.5f * wheelRadius);
        path.lineTo(23.2f * wheelRadius, realHeight - 4.5f * wheelRadius);
        path.lineTo(23.2f * wheelRadius, realHeight - 2f * wheelRadius);

        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 玻璃
        path = new Path();
        p.setColor(Color.GRAY);
        path.moveTo(14.2f * wheelRadius, realHeight - 2.7f * wheelRadius);
        path.lineTo(14.7f * wheelRadius, realHeight - 3.6f * wheelRadius);//
        path.lineTo(16.3f * wheelRadius, realHeight - 3.6f * wheelRadius);// 此点为多边形的起点
        path.lineTo(16.6f * wheelRadius, realHeight - 3.0f * wheelRadius);
        path.lineTo(15.5f * wheelRadius, realHeight - 2.5f * wheelRadius);
        path.lineTo(14.2f * wheelRadius, realHeight - 2.5f * wheelRadius);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        canvas.drawLine(15.3f * wheelRadius, realHeight - 3.6f * wheelRadius,
            15.3f * wheelRadius, realHeight - 2.5f * wheelRadius, paintFill3);

        canvas.drawLine(21.3f * wheelRadius, realHeight - 3.6f * wheelRadius,
            23.3f * wheelRadius, realHeight - 3.6f * wheelRadius, paintFill3);

        canvas.drawLine(21.3f * wheelRadius, realHeight - 2.6f * wheelRadius,
            23.3f * wheelRadius, realHeight - 2.6f * wheelRadius, paintFill3);

        // 固定点
        path = new Path();
        p.setColor(Color.DKGRAY);
        path.moveTo(7.0f * wheelRadius, realHeight - 1.9f * wheelRadius);
        path.lineTo(7.0f * wheelRadius, realHeight - 0.4f * wheelRadius); //
        path.lineTo(6.8f * wheelRadius, realHeight - 0.2f * wheelRadius); //
        path.lineTo(7.4f * wheelRadius, realHeight - 0.2f * wheelRadius); //
        path.lineTo(7.2f * wheelRadius, realHeight - 0.4f * wheelRadius); // 此点为多边形的起点
        path.lineTo(7.2f * wheelRadius, realHeight - 1.9f * wheelRadius); //
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 固定点
        path = new Path();
        p.setColor(Color.DKGRAY);
        path.moveTo(20.0f * wheelRadius, realHeight - 1.9f * wheelRadius);
        path.lineTo(20.0f * wheelRadius, realHeight - 0.4f * wheelRadius); //
        path.lineTo(19.8f * wheelRadius, realHeight - 0.2f * wheelRadius); //
        path.lineTo(20.4f * wheelRadius, realHeight - 0.2f * wheelRadius); //
        path.lineTo(20.2f * wheelRadius, realHeight - 0.4f * wheelRadius); // 此点为多边形的起点
        path.lineTo(20.2f * wheelRadius, realHeight - 1.9f * wheelRadius); //
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 画动臂
        float length = 12 * wheelRadius;
        float width = 1.8f * wheelRadius;

        // 画液压撑的上座
        p = new Paint();
        path = new Path();
        p.setColor(Color.rgb(255, 140, 0));

        path.moveTo(hsx - 0.9f * wheelRadius * (float) Math.sin(Math.toRadians(theta)),
            hsy + 0.9f * wheelRadius * (float) Math.cos(Math.toRadians(theta)));

        path.lineTo(hsx - 2.1f * wheelRadius * (float) Math.sin(Math.toRadians(theta)),
            hsy + 2.1f * wheelRadius * (float) Math.cos(Math.toRadians(theta)));

        path.lineTo(hsx - 0.9f * wheelRadius * (float) Math.sin(Math.toRadians(theta)) -
                2f * wheelRadius * (float) Math.cos(Math.toRadians(theta)),

            hsy + 0.9f * wheelRadius * (float) Math.cos(Math.toRadians(theta)) -
                2f * wheelRadius * (float) Math.sin(Math.toRadians(theta)));

        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 固定点0
        float fastenX = x - wheelRadius * (float) Math.cos(Math.toRadians(theta));
        float fastenY = y - wheelRadius * (float) Math.sin(Math.toRadians(theta));
        drawFasten(canvas, Color.rgb(255, 140, 0), (float) theta, 2.8f, 0.5f, wheelRadius, fastenX, fastenY);

        // 大臂
        path = new Path();
        p.setColor(Color.rgb(255, 140, 0));
        path.moveTo(x - width / (2 * (float) Math.sin(Math.toRadians(theta))), y);
        path.lineTo(x - length * (float) Math.cos(Math.toRadians(theta)) - (width / 2) * (float) Math.sin(Math.toRadians(theta)),
            y - length * (float) Math.sin(Math.toRadians(theta)) + (width / 2) * (float) Math.cos(Math.toRadians(theta)));
        path.lineTo(x - length * (float) Math.cos(Math.toRadians(theta)) + (width / 2) * (float) Math.sin(Math.toRadians(theta)),
            y - length * (float) Math.sin(Math.toRadians(theta)) - (width / 2) * (float) Math.cos(Math.toRadians(theta)));
        path.lineTo(x + width / (2 * (float) Math.sin(Math.toRadians(theta))), y);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        ///////////////////////
        //drawTitle(canvas);

        // 帽子0
        x = x - length * (float) Math.cos(Math.toRadians(theta));
        y = y - length * (float) Math.sin(Math.toRadians(theta));
        drawRotateCap(canvas, Color.rgb(255, 140, 0), (float) theta, 2.8f, 0.5f, wheelRadius, x, y);

        // 画细杆
        float thinRate = armLenth; // 细杆的长度是变量
        thinRate = thinRate + 1f;
        x = x - 0.5f * wheelRadius * (float) Math.cos(Math.toRadians(theta));
        y = y - 0.5f * wheelRadius * (float) Math.sin(Math.toRadians(theta));
        drawRotateCap(canvas, Color.GRAY, (float) theta, 1.2f, thinRate, wheelRadius, x, y);

        // 帽子3
        float x0 = x - thinRate * wheelRadius * (float) Math.cos(Math.toRadians(theta));
        float y0 = y - thinRate * wheelRadius * (float) Math.sin(Math.toRadians(theta));
        drawRotateCap(canvas, Color.rgb(255, 140, 0), (float) theta, 2.8f, 0.3f, wheelRadius, x0, y0);

        // 帽子2
        float x1 = x - (thinRate - 0.5f) * wheelRadius * (float) Math.cos(Math.toRadians(theta));
        float y1 = y - (thinRate - 0.5f) * wheelRadius * (float) Math.sin(Math.toRadians(theta));
        drawRotateCap(canvas, Color.rgb(255, 140, 0), (float) theta, 2.8f, 0.3f, wheelRadius, x1, y1);

        // 画细杆2
        x = x0 + 0.2f * wheelRadius * (float) Math.cos(Math.toRadians(theta));
        y = y0 + 0.2f * wheelRadius * (float) Math.sin(Math.toRadians(theta));
        drawRotateCap(canvas, Color.rgb(255, 140, 0), (float) theta, 1.2f, 0.4f, wheelRadius, x, y);

        // 画撑架
        x = x - 0.3f * wheelRadius * (float) Math.cos(Math.toRadians(theta));
        y = y - 0.3f * wheelRadius * (float) Math.sin(Math.toRadians(theta));
        path = new Path();
        float l = 0.8f * wheelRadius;
        float w = 1.1f * wheelRadius;
        float d = 0.6f * wheelRadius;
        float s = 1f * wheelRadius;
        path.moveTo(x - l * (float) Math.sin(Math.toRadians(theta)), y + l * (float) Math.cos(Math.toRadians(theta)));
        path.lineTo(x - l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta - 30)),
            y + l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta - 30))); // point!!!

        path.lineTo(x - l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta - 30)) + d * (float) Math.cos(Math.toRadians(90 - theta)),
            y + l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta - 30)) - d * (float) Math.sin(Math.toRadians(90 - theta)));

        float tx = x - l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta - 30)) + d * (float) Math.cos(Math.toRadians(90 - theta));
        float ty = y + l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta - 30)) - d * (float) Math.sin(Math.toRadians(90 - theta));

        path.lineTo(tx + s * (float) Math.sin(Math.toRadians(120 - theta)), ty + s * (float) Math.cos(Math.toRadians(120 - theta)));

        ////////////
        tx = x + l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta + 30)) - d * (float) Math.sin(Math.toRadians(theta));
        ty = y - l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta + 30)) + d * (float) Math.cos(Math.toRadians(theta));
        path.lineTo(tx + s * (float) Math.sin(Math.toRadians(60 - theta)), ty + s * (float) Math.cos(Math.toRadians(60 - theta)));

        path.lineTo(x + l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta + 30)) - d * (float) Math.sin(Math.toRadians(theta)),
            y - l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta + 30)) + d * (float) Math.cos(Math.toRadians(theta)));

        path.lineTo(x + l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta + 30)),
            y - l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta + 30))); // point
        path.lineTo(x + l * (float) Math.sin(Math.toRadians(theta)), y - l * (float) Math.cos(Math.toRadians(theta)));


        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

        // 画线缆
        canvas.drawLine(fastenX1, fastenY1,
            x + l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta + 30)),
            y - l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta + 30)), paintBorder);

        canvas.drawLine(x - l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta - 30)),
            y + l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta - 30)),
            x + l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta + 30)),
            y - l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta + 30)), paintBorder);

        // 绳子长度
        float slopeLen = hookHeight * wheelRadius;
        canvas.drawLine(x - l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta - 30)),
            y + l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta - 30)),
            x - l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta - 30)),
            y + l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta - 30)) + slopeLen, paintBorder);

        // 画吊钩
        float hookStartX = x - l * (float) Math.sin(Math.toRadians(theta)) - w * (float) Math.cos(Math.toRadians(theta - 30));
        float hookStartY = y + l * (float) Math.cos(Math.toRadians(theta)) - w * (float) Math.sin(Math.toRadians(theta - 30)) + slopeLen;

        p = new Paint();
        p.setColor(Color.DKGRAY);
        path = new Path();
        path.moveTo(hookStartX, hookStartY);// 此点为多边形的起点
        path.lineTo(hookStartX + 0.5f * wheelRadius, hookStartY + 0.5f * wheelRadius);
        path.lineTo(hookStartX + 0.2f * wheelRadius, hookStartY + 1.4f * wheelRadius);
        path.lineTo(hookStartX - 0.2f * wheelRadius, hookStartY + 1.4f * wheelRadius);
        path.lineTo(hookStartX - 0.5f * wheelRadius, hookStartY + 0.5f * wheelRadius);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);
        canvas.drawLine(hookStartX, hookStartY, hookStartX, hookStartY + 1.7f * wheelRadius, paintBorder);// 此点为多边形的起点
        drawHook(canvas, paintBorder, wheelRadius, hookStartX, hookStartY + 1.7f * wheelRadius);
        canvas.drawCircle(hookStartX, hookStartY + 0.7f * wheelRadius, 0.1f * wheelRadius, paintFill3);// 此点为多边形的起点
        //drawTitle(canvas);
    }



    public void drawHook(Canvas canvas, Paint paint, float delta, float xx, float yy) {

        RectF oval = new RectF(xx, yy - delta - 0.5f * delta, xx + 0.7f * delta, yy + delta - 0.5f * delta);

        canvas.drawArc(oval, 30, 120, false, paint);

        //oval = new RectF(xx - 0.7f * delta, yy - delta - 0.5f * delta, xx, yy + delta - 0.5f * delta);

        //canvas.drawArc(oval, 30, 120, false, paint);
    }

    public void setHook(int x, int y) {
        this.hookX = x;
        this.hookY = y;
        invalidate();
    }
}


