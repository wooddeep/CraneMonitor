package com.wooddeep.crane.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

// https://stackoverflow.com/questions/18931679/dotted-line-is-actually-not-dotted-when-app-is-running-on-real-android-device

@SuppressWarnings("unused")
public class GridLineView extends View {

    private Paint paint;  //绘图

    private int widthSize = 600;

    private int heighSize = 400;

    public int getWidthSize() {
        return widthSize;
    }

    public void setWidthSize(int widthSize) {
        this.widthSize = widthSize;
        invalidate();
    }

    public int getHeighSize() {
        return heighSize;
    }

    public void setHeighSize(int heighSize) {
        this.heighSize = heighSize;
        invalidate();
    }

    public GridLineView(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        //paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
    }


    public GridLineView(Context context, AttributeSet attrs) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        //paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        paint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        widthSize = getMeasuredWidth();
        heighSize = getMeasuredHeight();
        final int space = 100;
        int grids = Math.max(widthSize, heighSize) / space + 1;
        int vertz = 0;
        int hortz = 0;
        for (int i = 0; i < grids; i++) {
            canvas.drawLine(0, vertz, widthSize, vertz, paint);
            canvas.drawLine(hortz, 0, hortz, heighSize, paint);
            vertz += space;
            hortz += space;
        }
    }
}
