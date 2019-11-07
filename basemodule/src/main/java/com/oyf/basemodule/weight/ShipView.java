package com.oyf.basemodule.weight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class ShipView extends View {
    public ShipView(Context context) {
        this(context, null);
    }

    public ShipView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;
    private int color = Color.BLUE;
    Path mPath;
    Path mBottomPath;

    Point point;
    private int waterH;
    private int w;
    private int h;

    private ValueAnimator valueAnimator;
    float animatedValue;
    float pointValue;

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        mPath = new Path();
        mBottomPath = new Path();
        point = new Point();
        valueAnimator = ValueAnimator.ofFloat(0, 360);
        valueAnimator.setDuration(5000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        waterH = 90;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.BLUE);
        //画底部的
        mBottomPath.reset();
        mBottomPath.moveTo(0, h / 2);
        mBottomPath.lineTo(w, h / 2);
        mBottomPath.lineTo(w, h);
        mBottomPath.lineTo(0, h);
        canvas.drawPath(mBottomPath, mPaint);

        mPath.reset();
        mPath.moveTo(0, h / 2);
        int x = 0;
        int y = 0;
        while (x <= w) {
            y = (int) (waterH * Math.sin(degreeToRad(x + animatedValue)));
            mPath.lineTo(x, h / 2 - Math.abs(y));
            x++;
        }
        mPath.lineTo(w, h / 2);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        float px = animatedValue/360 * w / 360;
        mPaint.setColor(Color.RED);
        canvas.drawPoint(px, (float) (h / 2 - (waterH * Math.sin(degreeToRad(px)))), mPaint);
    }

    private double degreeToRad(double degree) {
        return degree * Math.PI / 180;
    }
}
