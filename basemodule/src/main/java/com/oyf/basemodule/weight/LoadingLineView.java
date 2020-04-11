package com.oyf.basemodule.weight;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * @创建者 oyf
 * @创建时间 2020/4/10 10:44
 * @描述
 **/
public class LoadingLineView extends View {
    public LoadingLineView(Context context) {
        super(context);
    }

    public LoadingLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int w;
    private int h;
    private Paint mPaint;
    private int mColor;
    private int mRadius = 20;
    private int mLineNumber = 12;
    private int mLineWidth = 30;
    private int mLineHeight = 7;
    private float mCurrent = 0;//0-1
    private long mTime = 500;//
    private ValueAnimator mValueAnimator;


    private void init() {
        mColor = Color.GRAY;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mLineHeight);
        startRotate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(w / 2, h / 2);
        int rotate = 360 / mLineNumber;
        canvas.rotate(mCurrent);
        for (int i = 0; i < mLineNumber; i++) {
            canvas.rotate(rotate);
            mPaint.setAlpha(i * 255 / mLineNumber);
            canvas.drawLine(mRadius, 0, mRadius + mLineWidth, 0, mPaint);
        }
        canvas.restore();
    }

    public void startRotate() {
        if (null == mValueAnimator) {
            mValueAnimator = ValueAnimator.ofInt(0, mLineNumber);
            mValueAnimator.setDuration(mTime);
            mValueAnimator.setRepeatCount(-1);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    mCurrent = animatedValue * 360 / mLineNumber;
                    invalidate();
                }
            });
            mValueAnimator.start();
        } else {
            if (mValueAnimator.isPaused()) {
                mValueAnimator.resume();
            }
        }
    }

    public void stopRotate() {
        if (null != mValueAnimator && mValueAnimator.isStarted()) {
            mValueAnimator.pause();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            stopRotate();
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != VISIBLE) {
            stopRotate();
        }
    }
}
