package com.oyf.basemodule.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 图层混合模式
 */
public class FrameModeView extends View {
/*    PorterDuff.Mode.CLEAR
    所绘制不会提交到画布上

    PorterDuff.Mode.SRC
    显示上层绘制图片

    PorterDuff.Mode.DST
    显示下层绘制图片

    PorterDuff.Mode.SRC_OVER
    正常绘制显示，上下层绘制叠盖。

    PorterDuff.Mode.DST_OVER
    上下层都显示。下层居上显示。

    PorterDuff.Mode.SRC_IN
    取两层绘制交集。显示上层。

    PorterDuff.Mode.DST_IN
    取两层绘制交集。显示下层。

    PorterDuff.Mode.SRC_OUT
    取上层绘制非交集部分。

    PorterDuff.Mode.DST_OUT
    取下层绘制非交集部分。

    PorterDuff.Mode.SRC_ATOP
    取下层非交集部分与上层交集部分

    PorterDuff.Mode.DST_ATOP
    取上层非交集部分与下层交集部分

    PorterDuff.Mode.XOR
    取两层绘制非交集。两层绘制非交集。

     PorterDuff.Mode.DARKEN
    上下层都显示。变暗

    PorterDuff.Mode.LIGHTEN
    上下层都显示。变量

    PorterDuff.Mode.MULTIPLY
    取两层绘制交集

    PorterDuff.Mode.SCREEN
    上下层都显示。*/


    public FrameModeView(Context context) {
        this(context, null);
    }

    public FrameModeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameModeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint mGreenPaint;
    Paint mRedPaint;
    Paint mGrarPaint;
    Paint mTopPaint;
    PorterDuffXfermode[] porterDuffXfermodes;
    int left = 0;
    int top = -300;
    int w = 100;
    int row = 4;
    RectF rectF;
    Path mPath;

    private void init() {
        mGreenPaint = new Paint();
        mGreenPaint.setAntiAlias(true);
        mGreenPaint.setStyle(Paint.Style.FILL);
        mGreenPaint.setColor(Color.GREEN);
        mRedPaint = new Paint();
        mRedPaint.setAntiAlias(true);
        mRedPaint.setStyle(Paint.Style.FILL);
        mRedPaint.setColor(Color.RED);
        mGrarPaint = new Paint();
        mGrarPaint.setAntiAlias(true);
        mGrarPaint.setStyle(Paint.Style.FILL);
        mGrarPaint.setColor(Color.BLUE);

        mTopPaint = new Paint();
        mTopPaint.setAntiAlias(true);
        mTopPaint.setStyle(Paint.Style.STROKE);
        mTopPaint.setStrokeWidth(10);
        mTopPaint.setColor(Color.RED);


        mPath = new Path();

        porterDuffXfermodes = new PorterDuffXfermode[]{
                new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
                new PorterDuffXfermode(PorterDuff.Mode.SRC),
                new PorterDuffXfermode(PorterDuff.Mode.DST),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),

                new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
                new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),

                new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
                new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
                new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
                new PorterDuffXfermode(PorterDuff.Mode.XOR),

                new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
                new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
                new PorterDuffXfermode(PorterDuff.Mode.SCREEN),
                new PorterDuffXfermode(PorterDuff.Mode.ADD),
/*
                new PorterDuffXfermode(PorterDuff.Mode.OVERLAY),
                new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)*/
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.GRAY);
       /* for (int i = 0; i < porterDuffXfermodes.length; i++) {
            if (i % row == 0) {
                top += 300;
            }
            int l = left + (i % row) * 200;
            int t = top;
            int r = l + 100;
            int b = t + 100;
            canvas.drawRect(l, t, r, b, mGreenPaint);
            mRedPaint.setXfermode(porterDuffXfermodes[i]);
            int l1 = l + 50;
            int t1 = t + 50;
            int r1 = l1 + 100;
            int b1 = t1 + 100;
            canvas.drawRect(l1, t1, r1, b1, mRedPaint);
            mRedPaint.setXfermode(null);
        }*/
        rectF = new RectF(100, top + 300, 500, top + 600);
        canvas.drawRect(rectF, mGreenPaint);

        canvas.saveLayer(rectF, mGrarPaint);
        canvas.drawRect(rectF, mGrarPaint);
        mTopPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawPath(mPath, mTopPaint);
        mTopPaint.setXfermode(null);
        canvas.restore();
        top = -300;
    }

    Region region = new Region();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                region.set(new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
                boolean contains = region.contains((int) event.getX(), (int) event.getY());
                if (contains) {
                    mPath.reset();
                    mPath.moveTo(event.getX(), event.getY());
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                boolean contains1 = region.contains((int) event.getX(), (int) event.getY());
                if (contains1) {
                    mPath.lineTo(event.getX(), event.getY());
                    invalidate();
                }

                break;
        }
        return super.onTouchEvent(event);
    }
}
