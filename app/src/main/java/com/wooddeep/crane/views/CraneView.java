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
import com.wooddeep.crane.tookit.Coordinate;

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
    public float armAngle = 0;

    public final static int minArmLength = 240;
    public final static int maxArmLength = 630;
    public final static int minHookHeight = 0;
    public final static int maxHookHeight = 600;

    private static Bitmap hCraneBitmap = null;
    private static Bitmap hHoookBitMap = null;

    /*一定要重写这个构造方法*/
    public CraneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CraneView);
        craneType = attrArray.getInteger(R.styleable.CraneView_crane_type, 0);
        armLenth = attrArray.getInteger(R.styleable.CraneView_arm_length, 300);
        hookHeight = attrArray.getInteger(R.styleable.CraneView_hook_height, 100);
        armAngle = attrArray.getFloat(R.styleable.CraneView_arm_angle, 0.0f);
    }

    private Offset drawHCrane(Canvas canvas, int id) {
        if (hCraneBitmap == null) {
            hCraneBitmap = BitmapFactory.decodeResource(getResources(), id);
        }
        Bitmap bitmap = hCraneBitmap;
        int realWidth = getMeasuredWidth();   // 控件的实际宽度
        int realHeight = getMeasuredHeight(); // 控件的实际高度
        int picWidth = bitmap.getWidth();     // 图片的实际宽度
        int picHeight = bitmap.getHeight();
        int centerX = realWidth / 2;
        int centerY = realHeight / 2;         // 控件的集合中心
        float rate = (float) picHeight / picWidth;
        Rect src = new Rect(0, 0, picWidth, picHeight);

        float showWidth = realWidth;
        float showHeight = showWidth * rate;
        if (showHeight > realHeight) {
            showHeight = realHeight;
            showWidth = showHeight / rate;
        }

        RectF dst = new RectF(centerX - showWidth / 2, centerY - showHeight / 2,
            centerX + showWidth / 2, centerY + showHeight / 2);
        canvas.drawBitmap(bitmap, src, dst, null);

        return new Offset(centerX - (int) showWidth / 2, centerY - (int) showHeight / 2, showWidth / picWidth);
    }


    // 135 为固定值, 无需修改
    private void drawHHook(Canvas canvas, int id, float scale, int left, int top, int armLen, int cableLen) {
        if (hHoookBitMap == null) {
            hHoookBitMap = BitmapFactory.decodeResource(getResources(), id);
        }
        Bitmap bitmap = hHoookBitMap;
        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Rect srcCableHook = new Rect(0, 50, picWidth, 80);
        RectF srcCableDst = new RectF(left + armLen * scale, top + 135 * scale, (picWidth + armLen) * scale + left, (165 + cableLen) * scale + top);
        canvas.drawBitmap(bitmap, srcCableHook, srcCableDst, null);

        Rect hookTail = new Rect(0, 50, picWidth, picHeight);
        RectF hookTailDst = new RectF(left + armLen * scale, top + (135 + cableLen) * scale, (picWidth + armLen) * scale + left, (picHeight + cableLen + 85) * scale + top);
        canvas.drawBitmap(bitmap, hookTail, hookTailDst, null);

        Rect hookHead = new Rect(0, 0, picWidth, 50);
        RectF hookHeadDst = new RectF(left + armLen * scale, top + 135 * scale, (picWidth + armLen) * scale + left, 180 * scale + top);
        canvas.drawBitmap(bitmap, hookHead, hookHeadDst, null);
    }

    private Offset calcVCrane(Canvas canvas, int id) {

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        int centerX = realWidth / 2;
        int centerY = realHeight / 2;
        float rate = (float) picHeight / picWidth;
        Rect src = new Rect(0, 0, picWidth, picHeight);

        float showWidth = realWidth;
        float showHeight = showWidth * rate;
        if (showHeight > realHeight) {
            showHeight = realHeight;
            showWidth = showHeight / rate;
        }

        return new Offset(centerX - (int) showWidth / 2, centerY - (int) showHeight / 2, showWidth / picWidth);
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
        Rect src = new Rect(0, 0, picWidth, picHeight);

        float showWidth = realWidth;
        float showHeight = showWidth * rate;
        if (showHeight > realHeight) {
            showHeight = realHeight;
            showWidth = showHeight / rate;
        }

        RectF dst = new RectF(centerX - showWidth / 2, centerY - showHeight / 2,
            centerX + showWidth / 2, centerY + showHeight / 2);
        canvas.drawBitmap(bitmap, src, dst, null);


        return new Offset(centerX - (int) showWidth / 2, centerY - (int) showHeight / 2, showWidth / picWidth);
    }

    // 135 为固定值, 无需修改
    private void drawVHookBak(Canvas canvas, int id, float scale, int left, int top, int armLen, int cableLen, float angle, Coordinate coord) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Paint paint = new Paint();//创建画笔
        paint.setStrokeWidth(4);
        paint.setColor(Color.BLACK);//为画笔设置颜色

        float armShowX = picWidth * (float) Math.cos(Math.toRadians(angle));
        float anchorOffsetX = armShowX * 1f;

        float armShowY = picWidth * (float) Math.sin(Math.toRadians(angle));
        float anchorOffsetY = armShowY * 1f;

        float deltaX = (picHeight * (float) Math.sin(Math.toRadians(angle))); // x - deltaX
        float deltaY = (picHeight - picHeight * (float) Math.cos(Math.toRadians(angle))); // y + deltaY

        float x = left + 220 * scale + anchorOffsetX * scale - deltaX * scale / 4;
        float y = top + 400 * scale - anchorOffsetY * scale + deltaY * scale * 3;

        Rect hookHead = new Rect(0, 0, picWidth, 50);
        RectF hookHeadDst = new RectF(coord.x - picWidth * scale / 2, coord.y, coord.x + picWidth * scale / 2, coord.y + 50 * scale);
        canvas.drawBitmap(bitmap, hookHead, hookHeadDst, null);

        Rect srcCableHook = new Rect(0, 50, picWidth, 80);
        RectF srcCableDst = new RectF(coord.x - picWidth * scale / 2,
            coord.y + 50 * scale,
            coord.x + picWidth * scale / 2,
            coord.y + (50 + cableLen) * scale);
        canvas.drawBitmap(bitmap, srcCableHook, srcCableDst, null);

        Rect hookTail = new Rect(0, 50, picWidth, picHeight);
        RectF hookTailDst = new RectF(coord.x - picWidth * scale / 2,
            coord.y + (50 + cableLen) * scale,
            coord.x + picWidth * scale / 2,
            coord.y + (50 + cableLen) * scale + (picHeight - 80) * scale);

        canvas.drawBitmap(bitmap, hookTail, hookTailDst, null);

    }

    private void drawVHook(Canvas canvas, int id, float scale, int left, int top, int armLen, int cableLen, float angle, Coordinate coord) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Paint paint = new Paint();//创建画笔
        paint.setStrokeWidth(4);
        paint.setColor(Color.DKGRAY);//为画笔设置颜色


        int dx = 25; // x方向修正值
        int dy = 0;  // y方向修正值
        if (angle > 70) dx = 60;
        if (angle <= 70 && angle > 40) dx = 10;
        if (angle <= 40) dx = 5;

        if (angle > 70) dy = -60;
        if (angle <= 75 && angle > 50) dy = 10;
        if (angle <= 50 && angle > 30) dy = 15;
        if (angle <= 30) dy = 20;

        Rect srcCableHook = new Rect(0, 50, picWidth, 80);
        RectF srcCableDst = new RectF(
            dx * scale + coord.x - picWidth * scale / 2,
            coord.y + 50 * scale,
            dx * scale + coord.x + picWidth * scale / 2,
            coord.y + (50 + cableLen) * scale);
        canvas.drawBitmap(bitmap, srcCableHook, srcCableDst, null);

        Rect hookTail = new Rect(0, 50, picWidth, picHeight);
        RectF hookTailDst = new RectF(
            dx * scale + coord.x - picWidth * scale / 2,
            dy * scale + coord.y + (30 + cableLen) * scale,
            dx * scale + coord.x + picWidth * scale / 2,
            dy * scale + coord.y + (30 + cableLen) * scale + (picHeight - 80) * scale);

        canvas.drawBitmap(bitmap, hookTail, hookTailDst, null);

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

    private Coordinate drawArmBak(Canvas canvas, int id, float scale, int left, int top, float angle) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Paint paint = new Paint();//创建画笔
        paint.setStrokeWidth(2);
        paint.setColor(Color.DKGRAY);//为画笔设置颜色

        float armShowX = picWidth * (float) Math.cos(Math.toRadians(angle));
        float anchorOffsetX = armShowX * 1.0f;

        float armShowY = picWidth * (float) Math.sin(Math.toRadians(angle));
        float anchorOffsetY = armShowY * 1.0f;

        float deltaX = (picHeight * (float) Math.sin(Math.toRadians(angle))); // x - deltaX
        float deltaY = (picHeight - picHeight * (float) Math.cos(Math.toRadians(angle))); // y + deltaY

        float cableStartX = left + 40 * scale;
        float cableStartY = top + 300 * scale;
        float cableEndX = left + 220 * scale + anchorOffsetX * scale - deltaX * scale / 4;
        float cableEndY = top + 400 * scale - anchorOffsetY * scale + deltaY * scale * 2.2f;

        // 画缆绳
        canvas.drawLine(cableStartX, cableStartY, cableEndX, cableEndY, paint);
        canvas.drawCircle(left + 40 * scale, top + 300 * scale, 2, paint); // 验证塔尖的坐标
        canvas.drawCircle(left + 220 * scale + anchorOffsetX * scale - deltaX * scale / 4,
            top + 400 * scale - anchorOffsetY * scale + deltaY * scale * 2.2f, 2, paint); // 验证臂尖的坐标

        // 画臂
        Bitmap arm = rotateBitmap(bitmap, angle * -1);
        Rect armSrc = new Rect(0, 0, arm.getWidth(), arm.getHeight());
        RectF armDst = new RectF(
            left + 250 * scale - deltaX * scale,
            135 * scale - arm.getHeight() * scale + 300 * scale + 0 * scale,
            left + arm.getWidth() * scale + 250 * scale - deltaX * scale,
            135 * scale + 300 * scale + 0 * scale);

        canvas.drawBitmap(arm, armSrc, armDst, null);

        return new Coordinate(left + 220 * scale + anchorOffsetX * scale - deltaX * scale / 4,
            top + 400 * scale - anchorOffsetY * scale + deltaY * scale * 2.2f);

    }

    private Coordinate drawArm(Canvas canvas, int id, float scale, int left, int top, float angle) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Paint paint = new Paint();//创建画笔
        paint.setStrokeWidth(2);
        paint.setColor(Color.DKGRAY);//为画笔设置颜色

        float armShowX = picWidth * (float) Math.cos(Math.toRadians(angle));
        float anchorOffsetX = armShowX * 1.0f;

        float armShowY = picWidth * (float) Math.sin(Math.toRadians(angle));
        float anchorOffsetY = armShowY * 1.0f;

        float deltaX = (picHeight * (float) Math.sin(Math.toRadians(angle))); // x - deltaX
        float deltaY = (picHeight - picHeight * (float) Math.cos(Math.toRadians(angle))); // y + deltaY

        float cableStartX = left + 40 * scale;
        float cableStartY = top + 300 * scale;
        float cableEndX = left + 220 * scale + anchorOffsetX * scale - deltaX * scale / 4;
        float cableEndY = top + 400 * scale - anchorOffsetY * scale + deltaY * scale * 2.2f;

        // 画缆绳
        canvas.drawLine(cableStartX, cableStartY, cableEndX, cableEndY, paint);
        canvas.drawCircle(left + 40 * scale, top + 300 * scale, 2, paint); // 验证塔尖的坐标
        canvas.drawCircle(left + 220 * scale + anchorOffsetX * scale - deltaX * scale / 4,
            top + 400 * scale - anchorOffsetY * scale + deltaY * scale * 2.2f, 2, paint); // 验证臂尖的坐标

        // 画臂
        Bitmap arm = rotateBitmap(bitmap, angle * -1);
        Rect armSrc = new Rect(0, 0, arm.getWidth(), arm.getHeight());

        int dx = 30; // x方向修正值
        int dy = 0;  // y方向修正值
        if (angle > 70) dx = 30;
        if (angle <= 70 && angle > 40) dx = 15;
        if (angle <= 40) dx = 5;

        if (angle > 75) dy = 0;
        if (angle <= 75 && angle > 50) dy = 10;
        if (angle <= 50 && angle > 30) dy = 15;
        if (angle <= 30) dy = 20;

        RectF armDst = new RectF(
            dx * scale + left + 250 * scale - deltaX * scale,
            dy * scale + 135 * scale - arm.getHeight() * scale + 300 * scale + 0 * scale,
            dx *scale + left + arm.getWidth() * scale + 250 * scale - deltaX * scale,
            dy * scale + 135 * scale + 300 * scale + 0 * scale);

        canvas.drawBitmap(arm, armSrc, armDst, null);

        return new Coordinate(left + 220 * scale + anchorOffsetX * scale - deltaX * scale / 4,
            top + 400 * scale - anchorOffsetY * scale + deltaY * scale * 2.2f);

    }

    /*重写onDraw（）*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (craneType == 0) {
            Offset offset = drawHCrane(canvas, R.drawable.crane_without_hook);
            if (hookHeight > 600) hookHeight = 600;
            if (hookHeight < 0) hookHeight = 0;

            if (armLenth < 250) armLenth = 250;
            if (armLenth > 630) armLenth = 630;

            drawHHook(canvas, R.drawable.crane_hook, offset.scale, offset.left, offset.top, armLenth, 600 - hookHeight);
        }

        if (craneType == 1) {
            drawVCrane(canvas, R.drawable.crane_rotate_arm); // 塔放到最后画，覆盖其他组件
            Offset offset = calcVCrane(canvas, R.drawable.crane_without_arm);
            if (armAngle < 15) armAngle = 15;
            if (armAngle > 85) armAngle = 85;

            Coordinate coord = drawArm(canvas, R.drawable.rotate_arm, offset.scale, offset.left, offset.top, armAngle);
            if (hookHeight > 450) hookHeight = 450;
            if (hookHeight < 0) hookHeight = 0;
            drawVHook(canvas, R.drawable.rotate_hook, offset.scale, offset.left, offset.top, armLenth, 450 - hookHeight, armAngle, coord);
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

    public void setArmAngle(float armAngle) {
        this.armAngle = armAngle;
        invalidate();
    }

    public void setCraneType(int craneType) {
        this.craneType = craneType;
        invalidate();
    }
}
