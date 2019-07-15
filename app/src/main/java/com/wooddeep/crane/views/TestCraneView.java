package com.wooddeep.crane.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

// https://www.2cto.com/kf/201509/442508.html
// https://www.stickpng.com/img/tools-and-parts/cranes/tower-crane
// https://pngtree.com/so/tower-crane
// icons: https://icons8.com/icon/81183/tower-crane

@SuppressWarnings("unused")
public class TestCraneView extends View {

    private Paint paintFill;  // 实心画笔
    private Paint paintBorder; // 边界画笔

    private int hookX = 100;
    private int hookY = 100;

    public TestCraneView(Context context, int defMinRadio, int defRingWidth) {
        this(context, null);
    }

    public TestCraneView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestCraneView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context);
        paintFill = new Paint();
        paintBorder = new Paint();

        paintFill.setColor(Color.rgb(255,140,0));
        paintFill.setStrokeJoin(Paint.Join.ROUND);
        paintFill.setStrokeCap(Paint.Cap.ROUND);
        paintFill.setStrokeWidth(2);
        paintFill.setAntiAlias(true);
        paintFill.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));

        paintBorder.setColor(Color.rgb(255,140,0));
        paintBorder.setStrokeJoin(Paint.Join.ROUND);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeCap(Paint.Cap.ROUND);
        paintBorder.setStrokeWidth(2);
        paintBorder.setAntiAlias(true);
        //paintBorder.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
    }


    void variousHooks(Canvas canvas, int x, int y, int X, int Y) {
        // 钩子头部分
        canvas.drawLine(x + 5 + X, y, x + 35 + X, y, paintBorder);
        canvas.drawLine(x + 5 + X, y + 10, x + 35 + X, y + 10, paintBorder);
        canvas.drawLine(x + 10 + X, y, x + 10 + X, y + 10, paintBorder);
        canvas.drawLine(x + 30 + X, y, x + 30 + X, y + 10, paintBorder);
        canvas.drawLine(x + 11 + X, y + 10, x + 11 + X, y + 10 + Y, paintBorder);
        canvas.drawLine(x + 29 + X, y + 10, x + 29 + X, y + 10 + Y, paintBorder);

        canvas.drawLine(x + 20 + X, y + 10 + Y + 5, x + 10 + X, y + 10 + Y + 15, paintBorder);
        canvas.drawLine(x + 20 + X, y + 10 + Y + 5, x + 30 + X, y + 10 + Y + 15, paintBorder);
        canvas.drawLine(x + 30 + X, y + 10 + Y + 15, x + 30 + X, y + 10 + Y + 35, paintBorder);

        canvas.drawLine(x + 30 + X, y + 10 + Y + 35, x + 10 + X, y + 10 + Y + 35, paintBorder);
        canvas.drawLine(x + 10 + X, y + 10 + Y + 35, x + 10 + X, y + 10 + Y + 15, paintBorder);
        canvas.drawLine(x + 10 + X, y + 10 + Y + 15, x + 30 + X, y + 10 + Y + 15, paintBorder);

        //半圆
        canvas.drawArc(new RectF(x + 20 + X - 10, y + 10 + Y - 10, x + 20 + X + 10, y + 10 + Y + 10), 0, 180, true, paintFill);
    }

    void wiredCables(Canvas canvas, int x, int y) {
        canvas.drawLine(x - 10, y + 15, x + 10, y - 15, paintBorder);
        canvas.drawLine(x - 10, y - 15, x + 10, y + 15, paintBorder);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 画骨架
        canvas.drawRect(150, 370, 170, 400, paintBorder);
        canvas.drawRect(150, 340, 170, 370, paintBorder);
        canvas.drawRect(150, 310, 170, 340, paintBorder);
        canvas.drawRect(150, 280, 170, 310, paintBorder);
        canvas.drawRect(150, 250, 170, 280, paintBorder);
        canvas.drawRect(150, 220, 170, 250, paintBorder);
        canvas.drawRect(150, 190, 170, 220, paintBorder);
        canvas.drawRect(150, 160, 170, 190, paintBorder);
        canvas.drawRect(150, 115, 160, 155, paintFill);

        // 左小重物
        canvas.drawRect(57, 113, 66, 128, paintFill);
        canvas.drawRect(67, 113, 76, 128, paintFill);
        canvas.drawRect(77, 113, 86, 128, paintFill);

        // 支柱交点
        canvas.drawArc(new RectF(148, 398, 152, 402), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(148, 368, 152, 372), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(148, 338, 152, 342), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(148, 308, 152, 312), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(148, 278, 152, 282), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(148, 248, 152, 252), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(148, 218, 152, 222), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(148, 188, 152, 192), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(148, 158, 152, 162), 0, 360, true, paintFill);

        canvas.drawArc(new RectF(168, 398, 172, 402), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(168, 368, 172, 372), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(168, 338, 172, 342), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(168, 308, 172, 312), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(168, 278, 172, 282), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(168, 248, 172, 252), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(168, 218, 172, 222), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(168, 188, 172, 192), 0, 360, true, paintFill);
        canvas.drawArc(new RectF(168, 158, 172, 162), 0, 360, true, paintFill);

        // 基座
        canvas.drawLine(110, 400, 210, 400, paintBorder);
        canvas.drawLine(135, 400, 150, 340, paintBorder);
        canvas.drawLine(185, 400, 170, 340, paintBorder);

        // 驾驶室
        canvas.drawLine(160, 115, 170, 115, paintBorder);
        canvas.drawLine(170, 115, 180, 135, paintBorder);
        canvas.drawLine(180, 135, 160, 135, paintBorder);
        canvas.drawLine(180, 135, 170, 155, paintBorder);
        canvas.drawLine(170, 155, 160, 155, paintBorder);

        //塔吊尖
        canvas.drawLine(150, 110, 170, 110, paintBorder);
        canvas.drawLine(150, 110, 160, 20, paintBorder);
        canvas.drawLine(170, 110, 160, 20, paintBorder);
        canvas.drawLine(152, 90, 168, 90, paintBorder);
        canvas.drawLine(155, 70, 165, 70, paintBorder);
        canvas.drawLine(158, 50, 162, 50, paintBorder);
        canvas.drawLine(160, 45, 85, 80, paintBorder);
        canvas.drawLine(160, 45, 88, 80, paintBorder);
        canvas.drawLine(160, 45, 91, 80, paintBorder);
        canvas.drawLine(160, 45, 240, 90, paintBorder);
        canvas.drawLine(160, 45, 243, 90, paintBorder);
        canvas.drawLine(160, 45, 246, 90, paintBorder);
        canvas.drawLine(160, 45, 350, 90, paintBorder);
        canvas.drawLine(160, 45, 353, 90, paintBorder);
        canvas.drawLine(160, 45, 356, 90, paintBorder);

        //右臂上下线条
        canvas.drawLine(170, 110, 390, 110, paintBorder);
        canvas.drawLine(180, 90, 380, 90, paintBorder);

        //上下线见得线    比例1:2
        canvas.drawLine(170, 110, 180, 90, paintBorder);
        canvas.drawLine(190, 110, 180, 90, paintBorder);
        canvas.drawLine(190, 110, 200, 90, paintBorder);
        canvas.drawLine(210, 110, 200, 90, paintBorder);
        canvas.drawLine(210, 110, 220, 90, paintBorder);
        canvas.drawLine(230, 110, 220, 90, paintBorder);
        canvas.drawLine(230, 110, 240, 90, paintBorder);
        canvas.drawLine(250, 110, 240, 90, paintBorder);
        canvas.drawLine(250, 110, 260, 90, paintBorder);
        canvas.drawLine(270, 110, 260, 90, paintBorder);
        canvas.drawLine(270, 110, 280, 90, paintBorder);
        canvas.drawLine(290, 110, 280, 90, paintBorder);
        canvas.drawLine(290, 110, 300, 90, paintBorder);
        canvas.drawLine(310, 110, 300, 90, paintBorder);
        canvas.drawLine(310, 110, 320, 90, paintBorder);
        canvas.drawLine(330, 110, 320, 90, paintBorder);
        canvas.drawLine(330, 110, 340, 90, paintBorder);
        canvas.drawLine(350, 110, 340, 90, paintBorder);
        canvas.drawLine(350, 110, 360, 90, paintBorder);
        canvas.drawLine(370, 110, 360, 90, paintBorder);
        canvas.drawLine(370, 110, 380, 90, paintBorder);
        canvas.drawLine(390, 110, 380, 90, paintBorder);

        //左吊臂
        canvas.drawLine(55, 110, 150, 110, paintBorder);
        canvas.drawLine(55, 110, 55, 80, paintBorder);
        canvas.drawLine(85, 110, 85, 80, paintBorder);
        canvas.drawLine(115, 110, 115, 80, paintBorder);
        canvas.drawLine(145, 110, 145, 80, paintBorder);
        canvas.drawLine(55, 100, 145, 100, paintBorder);
        canvas.drawLine(55, 90, 145, 90, paintBorder);
        canvas.drawLine(55, 80, 145, 80, paintBorder);

        //左小重物竖线
        canvas.drawLine(62, 110, 62, 130, paintBorder);
        canvas.drawLine(72, 110, 72, 130, paintBorder);
        canvas.drawLine(82, 110, 82, 130, paintBorder);

        variousHooks(canvas, 165, 110, hookX, hookY);
        wiredCables(canvas,160, 385);
        wiredCables(canvas,160, 355);
        wiredCables(canvas,160, 325);
        wiredCables(canvas,160, 295);
        wiredCables(canvas,160, 265);
        wiredCables(canvas,160, 235);
        wiredCables(canvas,160, 205);
        wiredCables(canvas,160, 175);


    }

    public void setHook(int x, int y) {
        this.hookX = x;
        this.hookY = y;
        invalidate();
    }
}


