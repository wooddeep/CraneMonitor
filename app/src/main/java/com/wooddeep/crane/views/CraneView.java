package com.wooddeep.crane.views;

/**
 * Created by niuto on 2019/7/15.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.wooddeep.crane.R;

// https://www.oschina.net/question/565065_72656

// 各种图片操作: https://www.2cto.com/kf/201502/377619.html

// https://android--code.blogspot.com/2015/11/android-how-to-rotate-bitmap-on-canvas.html

// 图层: https://blog.csdn.net/cquwentao/article/details/51423371

// 旋转: https://www.cnblogs.com/rustfisher/p/5071494.html

@SuppressWarnings("unused")
public class CraneView extends View {

    class Offset {
        public int left = 0;
        public int top = 0;
        public float scale = 1.0f;
        public Offset(int left, int top, float scale) {
            this.left = left;
            this.top = top;
            this.scale = scale;
        }
    }

    public int craneType = 0;
    public int armLenth = 300;
    public int hookHeight = 100;

    /*一定要重写这个构造方法*/
    public CraneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CraneView);
        craneType = attrArray.getInteger(R.styleable.CraneView_crane_type, 0);
        armLenth = attrArray.getInteger(R.styleable.CraneView_arm_length, 300);
        hookHeight = attrArray.getInteger(R.styleable.CraneView_hook_height, 100);

    }

    private Offset drawHCrane(Canvas canvas, int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();
        int centerX = realWidth / 2;
        int centerY = realHeight / 2;
        float rate = (float) picHeight / picWidth;
        Rect src = new Rect(0, 0, picWidth , picHeight);

        float showWidth = realWidth;
        float showHeight = showWidth * rate;
        if (showHeight > realHeight) {
            showHeight = realHeight;
            showWidth = showHeight / rate;
        }

        RectF dst = new RectF(centerX - showWidth / 2, centerY - showHeight / 2,
            centerX + showWidth / 2, centerY + showHeight / 2);
        canvas.drawBitmap(bitmap, src, dst, null);


        return new Offset(centerX - (int)showWidth / 2, centerY - (int)showHeight / 2, showWidth / picWidth);
    }


    private Offset drawVCrane(Canvas canvas, int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();
        int centerX = realWidth / 2;
        int centerY = realHeight / 2;
        float rate = (float) picHeight / picWidth;
        Rect src = new Rect(0, 0, picWidth , picHeight);

        float showWidth = realWidth;
        float showHeight = showWidth * rate;
        if (showHeight > realHeight) {
            showHeight = realHeight;
            showWidth = showHeight / rate;
        }

        RectF dst = new RectF(centerX - showWidth / 2, centerY - showHeight / 2,
            centerX + showWidth / 2, centerY + showHeight / 2);
        canvas.drawBitmap(bitmap, src, dst, null);


        return new Offset(centerX - (int)showWidth / 2, centerY - (int)showHeight / 2, showWidth / picWidth);
    }

    // 135 为固定值, 无需修改
    private void drawHHook(Canvas canvas, int id, float scale, int left, int top, int armLen, int cableLen) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Rect srcCableHook = new Rect(0, 50, picWidth , 80);
        RectF srcCableDst = new RectF(left + armLen * scale, top + 135 * scale, (picWidth + armLen) * scale + left, (165 + cableLen) * scale + top);
        canvas.drawBitmap(bitmap, srcCableHook, srcCableDst, null);

        Rect hookTail = new Rect(0, 50, picWidth , picHeight);
        RectF hookTailDst = new RectF(left + armLen * scale, top + (135 + cableLen) * scale, (picWidth + armLen) * scale + left, (picHeight + cableLen + 85) * scale + top);
        canvas.drawBitmap(bitmap, hookTail, hookTailDst, null);

        Rect hookHead = new Rect(0, 0, picWidth , 50);
        RectF hookHeadDst = new RectF(left + armLen * scale, top + 135 * scale, (picWidth + armLen) * scale + left, 180 * scale + top);
        canvas.drawBitmap(bitmap, hookHead, hookHeadDst, null);
    }


    // 135 为固定值, 无需修改
    private void drawVHook(Canvas canvas, int id, float scale, int left, int top, int armLen, int cableLen) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Rect srcCableHook = new Rect(0, 50, picWidth , 80);
        RectF srcCableDst = new RectF(left + armLen * scale, top + 135 * scale, (picWidth + armLen) * scale + left, (165 + cableLen) * scale + top);
        canvas.drawBitmap(bitmap, srcCableHook, srcCableDst, null);

        Rect hookTail = new Rect(0, 50, picWidth , picHeight);
        RectF hookTailDst = new RectF(left + armLen * scale, top + (135 + cableLen) * scale, (picWidth + armLen) * scale + left, (picHeight + cableLen + 85) * scale + top);
        canvas.drawBitmap(bitmap, hookTail, hookTailDst, null);

        Rect hookHead = new Rect(0, 0, picWidth , 50);
        RectF hookHeadDst = new RectF(left + armLen * scale, top + 135 * scale, (picWidth + armLen) * scale + left, 180 * scale + top);
        canvas.drawBitmap(bitmap, hookHead, hookHeadDst, null);
    }

    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    private void drawArm(Canvas canvas, int id, float scale, int left, int top, float angle) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        /*
        Rect srcCableHook = new Rect(0, 50, picWidth , 80);
        RectF srcCableDst = new RectF(left + armLen * scale, top + 135 * scale, (picWidth + armLen) * scale + left, (165 + cableLen) * scale + top);
        canvas.drawBitmap(bitmap, srcCableHook, srcCableDst, null);

        Rect hookTail = new Rect(0, 50, picWidth , picHeight);
        RectF hookTailDst = new RectF(left + armLen * scale, top + (135 + cableLen) * scale, (picWidth + armLen) * scale + left, (picHeight + cableLen + 85) * scale + top);
        canvas.drawBitmap(bitmap, hookTail, hookTailDst, null);
        */

        Bitmap nbm = rotateBitmap(bitmap, -10);
        Rect hookHead = new Rect(0, 0, nbm.getWidth() , nbm.getHeight());

        RectF hookHeadDst = new RectF(left + 240 * scale,
            top ,
            nbm.getWidth() * scale + left + 240 * scale,
            nbm.getHeight() * scale + top);

        canvas.drawBitmap(nbm, hookHead, hookHeadDst, null);

    }

    /*重写onDraw（）*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (craneType == 0) {
            Offset offset = drawHCrane(canvas, R.drawable.crane_without_hook);
            drawHHook(canvas, R.drawable.crane_hook, offset.scale, offset.left, offset.top, armLenth, hookHeight);
        }

        if (craneType == 1) {
            Offset offset = drawVCrane(canvas, R.drawable.crane_without_hook);
            drawVHook(canvas, R.drawable.crane_hook, offset.scale, offset.left, offset.top, armLenth, hookHeight);
            drawArm(canvas, R.drawable.crane_arm, offset.scale, offset.left, offset.top, 0.0f);
        }

    }

    public int getArmLenth() {
        return armLenth;
    }

    public void setArmLenth(int armLenth) {
        this.armLenth = armLenth;
        invalidate();
    }

    public int getHookHeight() {
        return hookHeight;
    }

    public void setHookHeight(int hookHeight) {
        this.hookHeight = hookHeight;
        invalidate();
    }
}
