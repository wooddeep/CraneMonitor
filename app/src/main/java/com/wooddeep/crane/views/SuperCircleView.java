package com.wooddeep.crane.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wooddeep.crane.R;

// https://blog.csdn.net/u013933720/article/details/78261844 dot line

// anroid RGB对照值
// https://blog.csdn.net/leslie___cheung/article/details/80873840

public class SuperCircleView extends View {
    private final String TAG = "SuperCircleView";

    private ValueAnimator valueAnimator;
    private int mViewCenterX;   //view宽的中心点(可以暂时理解为圆心)
    private int mViewCenterY;   //view高的中心点(可以暂时理解为圆心)

    private int mMinRadio; //最里面白色圆的半径
    private float mRingWidth; //圆环的宽度
    private int mMinCircleColor;    //最里面圆的颜色
    private int mRingNormalColor;    //默认圆环的颜色
    private Paint mPaint;
    private int color[] = new int[3];   //渐变颜色

    private RectF mRectF; //圆环的矩形区域
    private int mSelectRing = 0;        //要显示几段彩色
    private int mMaxValue;

    private int defMinRadio = 50;
    private int defRingWidth = 2;
    private int mInnerRadio = 20;
    private float hAngle = 30;    // 大臂水平方向的倾角
    private float vAngle = 0; // 垂直方向夹角

    public void setmInnerRadio(int mInnerRadio) {
        this.mInnerRadio = mInnerRadio;
    }

    public void sethAngle(float hAngle) {
        this.hAngle = hAngle;
        invalidate();
    }

    public void setvAngle(float vAngle) {
        this.vAngle = vAngle;
        invalidate();
    }

    public void setDefMinRadio(int defMinRadio) {
        this.defMinRadio = defMinRadio;
        this.mMinRadio = defMinRadio;
        invalidate();
    }

    public void setDefRingWidth(int defRingWidth) {
        this.defRingWidth = defRingWidth;
        this.mRingWidth = defRingWidth;
        invalidate();
    }

    public void setmRingNormalColor(int mRingNormalColor) {
        this.mRingNormalColor = mRingNormalColor;
        invalidate();
    }

    public SuperCircleView(Context context, int defMinRadio, int defRingWidth) {
        this(context, null);
    }

    public SuperCircleView(Context context) {
        this(context, null);
    }

    public SuperCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuperCircleView);

        //最里面白色圆的半径
        mMinRadio = a.getInteger(R.styleable.SuperCircleView_min_circle_radio, defMinRadio);
        //圆环宽度
        mRingWidth = a.getFloat(R.styleable.SuperCircleView_ring_width, defRingWidth);

        //最里面的圆的颜色(白色)
        //mMinCircleColor = a.getColor(R.styleable.SuperCircleView_circle_color, context.getResources().getColor(R.color.green));
        mMinCircleColor = 0x00000000;

        //圆环的默认颜色(圆环占据的是里面的圆的空间)
        mRingNormalColor = a.getColor(R.styleable.SuperCircleView_ring_normal_color, context.getResources().getColor(R.color.gray));
        //圆环要显示的彩色的区域
        mSelectRing = a.getInt(R.styleable.SuperCircleView_ring_color_select, 0);

        mMaxValue = a.getInt(R.styleable.SuperCircleView_maxValue, 100);

        a.recycle();

        //抗锯齿画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //防止边缘锯齿
        mPaint.setAntiAlias(true);
        //需要重写onDraw就得调用此
        this.setWillNotDraw(false);

        //圆环渐变的颜色
        color[0] = Color.parseColor("#FFD300");
        color[1] = Color.parseColor("#FF0084");
        color[2] = Color.parseColor("#16FF00");
    }

    private RectF calRingRectArea(int radio) {
        return new RectF(mViewCenterX - radio - mRingWidth / 2,
          mViewCenterY - radio - mRingWidth / 2,
          mViewCenterX + radio + mRingWidth / 2,
          mViewCenterY + radio + mRingWidth / 2);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //view的宽和高,相对于父布局(用于确定圆心)
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        mViewCenterX = viewWidth / 2;
        mViewCenterY = viewHeight / 2;
        mRectF = calRingRectArea(mMinRadio);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mMinCircleColor);
        //canvas.drawCircle(mViewCenterX, mViewCenterY, mMinRadio, mPaint);
        //画默认圆环
        drawNormalRing(canvas);
        //画彩色圆环
        drawRingCenter(canvas);
        // draw radio
        drawRadio(canvas);
    }

    /**
     * 画彩色圆环
     *
     * @param canvas
     */
    private void drawColorRing(Canvas canvas) {
        Paint ringColorPaint = new Paint(mPaint);
        ringColorPaint.setStyle(Paint.Style.STROKE);
        ringColorPaint.setStrokeWidth(mRingWidth);
        ringColorPaint.setShader(new SweepGradient(mViewCenterX, mViewCenterX, color, null));
        //左边旋转90度
        canvas.rotate(-90, mViewCenterX, mViewCenterY);
        canvas.drawArc(mRectF, 360, mSelectRing, false, ringColorPaint);
        ringColorPaint.setShader(null);
    }

    private void drawRingCenter(Canvas canvas) {
        Paint ringColorPaint = new Paint(mPaint);
        ringColorPaint.setStyle(Paint.Style.STROKE);
        ringColorPaint.setColor(Color.DKGRAY);
        //ringColorPaint.setStrokeWidth(mRingWidth);
        //ringColorPaint.setShader(new SweepGradient(mViewCenterX, mViewCenterX, color, null));
        //canvas.rotate(-90, mViewCenterX, mViewCenterY);
        //canvas.drawArc(mRectF, 360, mSelectRing, false, ringColorPaint);
        canvas.drawCircle(mViewCenterX, mViewCenterX, 4, ringColorPaint);
        ringColorPaint.setShader(null);
    }

    /**
     * 画默认圆环
     *
     * @param canvas
     */
    private void drawNormalRing(Canvas canvas) {

        Paint ringMaxPaint = new Paint(mPaint);
        ringMaxPaint.setStyle(Paint.Style.STROKE);
        ringMaxPaint.setStrokeWidth(1);
        ringMaxPaint.setColor(Color.GRAY);//圆环默认颜色为灰色
        ringMaxPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));

        Paint ringRealPaint = new Paint(mPaint);
        ringRealPaint.setStyle(Paint.Style.STROKE);
        ringRealPaint.setStrokeWidth(mRingWidth);
        ringRealPaint.setColor(Color.rgb(46, 139, 87));

        // 大臂环
        canvas.drawArc(mRectF, 360, 360, false, ringMaxPaint);

        // 大臂环 * cos(夹角)
        double cos = Math.cos(Math.toRadians(vAngle));
        double realRadio = cos * mMinRadio;
        RectF realRectF = calRingRectArea((int)realRadio);
        canvas.drawArc(realRectF, 360, 360, false, ringRealPaint);

        // 小臂环
        RectF innerRectF = calRingRectArea(mInnerRadio);
        //ringRealPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        canvas.drawArc(innerRectF, 360, 360, false, ringMaxPaint);

        // 小臂环 * cos(夹角)
        double realMinRadio = cos * mInnerRadio;
        RectF realMinRectF = calRingRectArea((int)realMinRadio);
        canvas.drawArc(realMinRectF, 360, 360, false, ringRealPaint);
    }

    /**
     * 画默认半径
     *
     * @param canvas
     */
    private void drawRadio(Canvas canvas) {
        Paint radioPaint = new Paint(mPaint);
        radioPaint.setStyle(Paint.Style.STROKE);
        radioPaint.setColor(Color.rgb(225, 140, 0));
        radioPaint.setStrokeWidth(2.0f);
        //radioPaint.setShadowLayer(2, 1, 1, Color.RED);

        // long arm
        double sin = Math.sin(Math.toRadians(hAngle + 90));
        double cos = Math.cos(Math.toRadians(hAngle + 90));

        // 大臂环 * cos(夹角)
        double cosRate = Math.cos(Math.toRadians(vAngle));

        float xoffset = (float) (mMinRadio * cosRate * sin);
        float yoffset = (float) (mMinRadio * cosRate * cos);
        canvas.drawLine(mViewCenterX, mViewCenterY, mViewCenterX + xoffset, mViewCenterY + yoffset, radioPaint);

        // short arm
        double isin = Math.sin(Math.toRadians(hAngle + 90 + 180));
        double icos = Math.cos(Math.toRadians(hAngle + 90 + 180));
        float ixoffset = (float) (mInnerRadio * cosRate * isin);
        float iyoffset = (float) (mInnerRadio * cosRate * icos);
        canvas.drawLine(mViewCenterX, mViewCenterY, mViewCenterX + ixoffset, mViewCenterY + iyoffset, radioPaint);
    }


    //***************************************用于更新圆环表示的数值*****************************************************

    /**
     * 设置当前值
     *
     * @param value
     */
    public void
    setValue(int value) {
        if (value > mMaxValue) {
            value = mMaxValue;
        }
        int start = 0;
        int end = value;
        startAnimator(start, end, 2000);
    }

    private void startAnimator(int start, int end, long animTime) {
        valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.setDuration(animTime);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int i = Integer.valueOf(String.valueOf(animation.getAnimatedValue()));
                //每个单位长度占多少度
                mSelectRing = (int) (360 * (i / 100f));
                //Log.i(TAG, "onAnimationUpdate: mSelectRing::" + mSelectRing);
                invalidate(); // 调用onDraw方法
            }
        });
        valueAnimator.start();
    }

    public void show() {
        invalidate();
    }
}
