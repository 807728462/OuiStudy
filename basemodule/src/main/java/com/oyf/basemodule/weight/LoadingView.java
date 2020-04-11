package com.oyf.basemodule.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.logging.Level;

/**
 * 加载转圈圈
 */
public class LoadingView extends View {
    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Path mCiclePath;
    private Path mdstPath;
    private Path mTruePath;
    private int mRadius = 100;
    float animatedValue;
    float animatedValue2;
    float length;

    private int count = 0;
    private ValueAnimator animator;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(10);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        mCiclePath = new Path();
        mdstPath = new Path();
        mCiclePath.addCircle(300, 300, mRadius, Path.Direction.CW);
        mTruePath = new Path();
        mTruePath.moveTo(250, 290);
        mTruePath.lineTo(300, 335);
        mTruePath.lineTo(350, 250);
        mPathMeasure = new PathMeasure(mCiclePath, false);
        length = mPathMeasure.getLength();
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(1500);
        animator.setRepeatCount(-1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue = (float) animation.getAnimatedValue();

                if (animatedValue == 0.5) {
                    count++;
                    if (count == 2) {
                        animation.setRepeatCount(0);
                    }
                }
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startRight();
            }
        });
        animator.start();
    }

    private void startRight() {
        mPathMeasure.setPath(mTruePath, false);
        animator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue2 = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mdstPath.reset();
        if (count < 2) {
            int end = (int) (animatedValue * length);
            int start = animatedValue > 0.5 ? (int) (2 * (animatedValue - 0.5) * length) : 0;
            mPathMeasure.getSegment(start, end, mdstPath, true);
            canvas.drawPath(mdstPath, mPaint);
        } else if (count == 2) {
            int end = (int) (animatedValue * length);
            int start = 0;
            mPathMeasure.getSegment(start, end, mdstPath, true);
            canvas.drawPath(mdstPath, mPaint);
        } else {
            canvas.drawPath(mCiclePath, mPaint);
            mPathMeasure.getSegment(0, animatedValue2, mdstPath, true);
            canvas.drawPath(mdstPath, mPaint);
        }
    }

    public void stop() {
        if (null != animator && animator.isStarted()) {
            animator.cancel();
        }
    }
}
