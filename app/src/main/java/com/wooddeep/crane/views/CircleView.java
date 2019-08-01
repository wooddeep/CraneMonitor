package com.wooddeep.crane.views;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

// https://blog.csdn.net/u013933720/article/details/78261844 dot line

// anroid RGB对照值
// https://blog.csdn.net/leslie___cheung/article/details/80873840

public class CircleView extends View {
    private final String TAG = "CircleView";

    private float mViewCenterX = 0;   //view宽的中心点(可以暂时理解为圆心)
    private float mViewCenterY = 0;   //view高的中心点(可以暂时理解为圆心)

    private float mMinRadio = 100; //最里面白色圆的半径
    private float mRingWidth; //圆环的宽度
    private int mRingNormalColor;    //默认圆环的颜色

    private float defMinRadio = 50;
    private float defRingWidth = 2;
    private float mInnerRadio = 20;
    private float hAngle = 30;    // 大臂水平方向的倾角
    private float vAngle = 0; // 垂直方向夹角

    private float carRange = 50; // 小车位置

    private float scale = 1.0f;

    private boolean alarm = false;

    private boolean flink = false;


    public boolean getFlink() {
        return flink;
    }

    public void setFlink(boolean flink) {
        this.flink = flink;
        //invalidate();
    }

    public void setmInnerRadio(float mInnerRadio) {
        this.mInnerRadio = mInnerRadio;
        //invalidate();
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
        //invalidate();
    }

    public boolean getAlarm() {
        return this.alarm;
    }

    public void sethAngle(float hAngle) {
        this.hAngle = hAngle;
        //invalidate();
    }

    public void setvAngle(float vAngle) {
        this.vAngle = vAngle;
        //invalidate();
    }

    public void setDefMinRadio(float defMinRadio) {
        this.defMinRadio = defMinRadio;
        this.mMinRadio = defMinRadio;
        //invalidate();
    }

    public void setDefRingWidth(float defRingWidth) {
        this.defRingWidth = defRingWidth;
        this.mRingWidth = defRingWidth;
        //invalidate();
    }

    public void setmRingNormalColor(int mRingNormalColor) {
        this.mRingNormalColor = mRingNormalColor;
        //invalidate();
    }

    public void setCarRange(float carRange) {
        this.carRange = carRange;
        this.invalidate();
    }

    public void setmViewCenterX(float mViewCenterX) {
        this.mViewCenterX = mViewCenterX;
        //invalidate();
    }

    public void setmViewCenterY(float mViewCenterY) {
        this.mViewCenterY = mViewCenterY;
        //invalidate();
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public CircleView(Context context, float defMinRadio, float defRingWidth) {
        this(context, null);
    }

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /*
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperCircleView);
        mMinRadio = a.getFloat(R.styleable.SuperCircleView_min_circle_radio, defMinRadio);
        mRingWidth = a.getFloat(R.styleable.SuperCircleView_ring_width, defRingWidth);

        mMinCircleColor = 0x00000000;
        mRingNormalColor = a.getColor(R.styleable.SuperCircleView_ring_normal_color, context.getResources().getColor(R.color.gray));
        */
    }

    private RectF calRingRectArea(float radio) {
        return new RectF(mViewCenterX - radio - mRingWidth / 2,
            mViewCenterY - radio - mRingWidth / 2,
            mViewCenterX + radio + mRingWidth / 2,
            mViewCenterY + radio + mRingWidth / 2);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawRing(canvas);
        drawRadio(canvas);
    }


    /**
     * 画默认圆环
     *
     * @param canvas
     */
    private void drawRing(Canvas canvas) {

        Paint ringMaxPaint = new Paint();
        ringMaxPaint.setStyle(Paint.Style.STROKE);
        ringMaxPaint.setStrokeWidth(1);
        ringMaxPaint.setColor(Color.GRAY);//圆环默认颜色为灰色
        ringMaxPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));

        // 大臂环
        RectF mRectF = calRingRectArea(mMinRadio);
        canvas.drawArc(mRectF, 360, 360, false, ringMaxPaint);

        Paint ringRealPaint = new Paint();
        ringRealPaint.setStyle(Paint.Style.STROKE);
        ringRealPaint.setStrokeWidth(mRingWidth);
        ringRealPaint.setColor(mRingNormalColor);
        ringRealPaint.setMaskFilter(new BlurMaskFilter(3f, BlurMaskFilter.Blur.SOLID));

        // 大臂环 * cos(夹角)
        double cos = Math.cos(Math.toRadians(vAngle));
        double realRadio = cos * mMinRadio;
        RectF realRectF = calRingRectArea((float) realRadio);
        canvas.drawArc(realRectF, 360, 360, false, ringRealPaint);

    }

    /**
     * 画默认半径
     *
     * @param
     */

    private void drawRadio(Canvas canvas) {
        Paint radioPaint = new Paint();
        radioPaint.setStyle(Paint.Style.STROKE);
        radioPaint.setStrokeWidth(2.0f);
        if (flink) {
            radioPaint.setMaskFilter(new BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID));
        }

        if (alarm) {
            radioPaint.setColor(Color.rgb(225, 140, 0));
        } else {
            radioPaint.setMaskFilter(new BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID));
            radioPaint.setColor(mRingNormalColor);
        }

        // long arm
        double sin = Math.sin(Math.toRadians(hAngle + 90));
        double cos = Math.cos(Math.toRadians(hAngle + 90));

        // 大臂环 * cos(夹角)
        double cosRate = Math.cos(Math.toRadians(vAngle));

        float xoffset = (float) (mMinRadio * cosRate * sin);
        float yoffset = (float) (mMinRadio * cosRate * cos);
        canvas.drawLine(mViewCenterX, mViewCenterY, mViewCenterX + xoffset, mViewCenterY + yoffset, radioPaint);

        // 画小车
        Paint carPaint = new Paint();
        carPaint.setColor(Color.rgb(255, 165, 0));
        carPaint.setStyle(Paint.Style.FILL);
        float carXCoord = mViewCenterX + Math.min((float) (carRange * cosRate * sin), (float) (xoffset - 8 * cosRate * sin));
        float carYCoord = mViewCenterY + Math.min((float) (carRange * cosRate * cos), (float) (yoffset - 8 * cosRate * cos));

        carPaint.setStrokeWidth(8); // 车宽6， 车长12
        float carHeadXCoord = carXCoord + (float) (8 * cosRate * sin); // 车头X坐标
        float carHeadYCoord = carYCoord + (float) (8 * cosRate * cos); // 车头Y坐标


        if (scale < 1.0f) {
            carPaint.setStrokeWidth(8 * scale); // 车宽6， 车长12
            carHeadXCoord = carXCoord + Math.min((float) (float) (8 * cosRate * sin * scale), xoffset); // 车头X坐标
            carHeadYCoord = carYCoord + Math.min((float) (float) (8 * cosRate * cos * scale), yoffset); // 车头Y坐标
        }

        canvas.drawLine(carXCoord, carYCoord, carHeadXCoord, carHeadYCoord, carPaint);

        // 平衡臂相关参数
        Paint shortArmPaint = new Paint();
        shortArmPaint.setColor(Color.BLACK);
        shortArmPaint.setStyle(Paint.Style.STROKE);
        shortArmPaint.setStrokeWidth(4.0f);
        double isin = Math.sin(Math.toRadians(hAngle + 90 + 180));
        double icos = Math.cos(Math.toRadians(hAngle + 90 + 180));
        float ixoffset = (float) (mInnerRadio * cosRate * isin);
        float iyoffset = (float) (mInnerRadio * cosRate * icos);

        canvas.drawLine(mViewCenterX, mViewCenterY, mViewCenterX + ixoffset, mViewCenterY + iyoffset, shortArmPaint);

        // 圆心
        Paint ringColorPaint = new Paint();
        ringColorPaint.setStyle(Paint.Style.FILL);
        ringColorPaint.setColor(Color.DKGRAY);
        RectF mRectF = calRingRectArea(3);
        canvas.drawArc(mRectF, 360, 360, false, ringColorPaint);
    }


    public void show() {
        invalidate();
    }
}
