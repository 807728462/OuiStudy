package com.oyf.basemodule.weight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class LoadCircleView extends View {
    private final static String TAG = LoadCircleView.class.getSimpleName();

    public LoadCircleView(Context context) {
        this(context, null);
    }

    public LoadCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private Path mCirclePath;
    private int length;
    private int mRadius = 200;
    private int width;
    private int height;
    private int rectR = 80;
    private Rect rectF = new Rect();
    private float value;
    private ValueAnimator valueAnimator;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(10);
        mCirclePath = new Path();
        mPathMeasure = new PathMeasure();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        mCirclePath.addCircle(width / 2, height / 2, mRadius, Path.Direction.CW);
        mPathMeasure.setPath(mCirclePath, false);
        length = (int) mPathMeasure.getLength();

        valueAnimator = ValueAnimator.ofFloat(0, length);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(-1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mCirclePath, mPaint);

        float[] pos = new float[2];
        float[] tan = new float[2];
        boolean posTan = mPathMeasure.getPosTan(value, pos, tan);
        if (posTan) {

            double atan = Math.atan2(tan[1], tan[0]) * 180 / Math.PI;
            Log.d(TAG, "degress=" + atan + ",value=" + value);
            canvas.translate(width / 2, height / 2);
            rectF.set(mRadius - rectR / 2, -rectR / 2, mRadius + rectR / 2, rectR / 2);
            canvas.rotate((float) (atan - 90));
            canvas.drawRect(rectF, mPaint);
            canvas.restore();
        }
    }

    public void stop() {
        if (null != valueAnimator && valueAnimator.isStarted()) {
            valueAnimator.cancel();
        }
    }
}
