package com.wooddeep.crane.views;

/**
 * Created by niuto on 2019/7/15.
 */

import android.content.Context;
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

@SuppressWarnings("unused")
public class TestView extends View {
    public Bitmap bitmap1 = null;
    public Bitmap bitmap2 = null;
    public int myInterval = 10;

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

    /*一定要重写这个构造方法*/
    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    // 135 为固定值, 无需修改
    private void drawHook(Canvas canvas, int id, float scale, int left, int top, int dx) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);

        int realWidth = getMeasuredWidth();
        int realHeight = getMeasuredHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();

        Rect src = new Rect(0, 0, picWidth , picHeight);

        RectF dst = new RectF(left + dx, top + 135 * scale,
            picWidth * scale + left + dx, picHeight * scale + top + 135 * scale);


        Rect srcOnlyHook = new Rect(0, 50, picWidth , picHeight);

        RectF srcOnlyDst = new RectF(0, 0,
            picWidth * scale, (picHeight - 50) * scale);

        canvas.drawBitmap(bitmap, src, dst, null);

        canvas.drawBitmap(bitmap, srcOnlyHook, srcOnlyDst, null);

    }


    /*重写onDraw（）*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Offset offset = drawHCrane(canvas, R.drawable.crane_without_hook);

        drawHook(canvas, R.drawable.crane_hook, offset.scale, offset.left, offset.top, 300);

        // Set rotation on matrix
        /*
        matrix.setRotate(
            0, // degrees
            canvas.getWidth() / 2, // px
            canvas.getHeight() / 2 // py
        );
        */

        /*
        matrix.reset();

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int imageRealWidth = bitmap.getWidth();
        int imageRealHeight = bitmap.getHeight();

        // // Draw the bitmap at the center position of the canvas both vertically and horizontally

        matrix.postTranslate(
            canvas.getWidth() / 2 - bitmap.getWidth() / 2,
            canvas.getHeight() / 2 - bitmap.getHeight() / 2
        );

        matrix.postScale(0.8f, 0.8f);

        //matrix.postScale((float)0.8, (float)0.8);

        canvas.drawBitmap(
            bitmap, // Bitmap
            matrix, // Matrix
            null // Paint
        );
        */

        /*
        //平移到（100，100）处
        //matrix.postTranslate(-200, -200);
        matrix.setScale((float)canvas.getWidth()/bitmap.getWidth(), (float)canvas.getWidth()/bitmap.getHeight());
        matrix.postTranslate(
            canvas.getWidth() / 2 - bitmap.getWidth() / 2,
            canvas.getHeight() / 2 - bitmap.getHeight() / 2
        );

        //倾斜x和y轴，以（100，100）为中心。
        //matrix.postSkew(0.2f, 0.2f, 100, 100);
        canvas.drawBitmap(bitmap, matrix, null);
        */
        //canvas.drawBitmap(bitmap, 0, 0, null);
        //matrix.reset();
        //matrix.postScale(1.0f, 1.0f);
        //matrix.postTranslate(0, -200);
        //canvas.drawBitmap(bitmap, matrix, null);


    }

}
