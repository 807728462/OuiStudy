package com.oyf.basemodule.weight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * @创建者 oyf
 * @创建时间 2020/3/27 10:35
 * @描述
 **/
public class ScanQrView extends View {
    public ScanQrView(Context context) {
        this(context, null);
    }

    public ScanQrView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScanQrView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }


    private Context mContext;
    private Paint mBgPaint;
    private Paint mScanLinePaint;
    private Paint mFramePaint;
    private Path mPath;
    private int mWidth;
    private int mHeight;
    private int mCurrentHeight;
    private ValueAnimator mValueAnimator;

    private int mFrameWidth;
    private int mFrameHeight;
    private int mLineFrameHeight;
    private int mLineHeight;
    private int mLineLength;

    private int mBgColor = Color.parseColor("#80000000");
    private int mFrameColor = Color.parseColor("#ffffff");
    private int mLineColor = Color.parseColor("#9045F74C");


    private void init() {
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);

        mScanLinePaint = new Paint();
        mScanLinePaint.setAntiAlias(true);
        mScanLinePaint.setColor(mLineColor);

        mFramePaint = new Paint();
        mFramePaint.setAntiAlias(true);
        mFramePaint.setColor(mFrameColor);

        mValueAnimator = ValueAnimator.ofFloat(1);
        mValueAnimator.setDuration(1500).setRepeatCount(-1);
        mValueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                mCurrentHeight = (int) ((mFrameWidth - 2 * mLineFrameHeight - mLineHeight) * animatedValue);
                invalidate();
            }
        });
        startScan();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        mFrameWidth = mWidth / 2;
        mFrameHeight = mFrameWidth;
        mLineFrameHeight = 10;
        mLineHeight = 100;
        mLineLength = 100;
        mScanLinePaint.setStrokeWidth(mLineFrameHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawLine(canvas);
    }

    LinearGradient topLinearGradient;
    int[] colors = new int[]{Color.TRANSPARENT, mLineColor};
    float[] floats = new float[]{0.5f,1f};

    private void drawLine(Canvas canvas) {
        int sx = mWidth / 2 - mFrameWidth / 2 + mLineFrameHeight;
        int sy = mHeight / 2 - mFrameHeight / 2 + mCurrentHeight + mLineFrameHeight;

        int ex = mFrameWidth - 2 * mLineFrameHeight;
        int ey = mLineHeight;

        canvas.translate(sx, sy);
        Rect rect = new Rect(0, 0, ex, ey);
        topLinearGradient = new LinearGradient(0, 0, 0, mLineHeight, colors, floats, Shader.TileMode.REPEAT);
        mScanLinePaint.setShader(topLinearGradient);
        canvas.drawRect(rect, mScanLinePaint);
        canvas.restore();
    }

    private void drawBg(Canvas canvas) {
        mBgPaint.setColor(mBgColor);
        canvas.drawRect(0, 0, mWidth, mHeight, mBgPaint);
        mBgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        Rect rect = new Rect();
        rect.left = mWidth / 2 - mFrameWidth / 2;
        rect.top = mHeight / 2 - mFrameHeight / 2;
        rect.right = mWidth / 2 + mFrameWidth / 2;
        rect.bottom = mHeight / 2 + mFrameHeight / 2;
        mBgPaint.setColor(mFrameColor);
        mBgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, mBgPaint);
        mBgPaint.setXfermode(null);
        if (mPath == null) {
            mPath = new Path();
            mPath.moveTo(mWidth / 2 - mFrameWidth / 2 + mLineFrameHeight / 2, mHeight / 2 - mFrameHeight / 2 + mLineLength);
            mPath.lineTo(mWidth / 2 - mFrameWidth / 2 + mLineFrameHeight / 2, mHeight / 2 - mFrameHeight / 2 + +mLineFrameHeight / 2);
            mPath.lineTo(mWidth / 2 - mFrameWidth / 2 + mLineLength, mHeight / 2 - mFrameHeight / 2 + +mLineFrameHeight / 2);

            mPath.moveTo(mWidth / 2 + mFrameWidth / 2 - mLineFrameHeight / 2 - mLineLength, mHeight / 2 - mFrameHeight / 2 + +mLineFrameHeight / 2);
            mPath.lineTo(mWidth / 2 + mFrameWidth / 2 - mLineFrameHeight / 2, mHeight / 2 - mFrameHeight / 2 + +mLineFrameHeight / 2);
            mPath.lineTo(mWidth / 2 + mFrameWidth / 2 - mLineFrameHeight / 2, mHeight / 2 - mFrameHeight / 2 + +mLineFrameHeight / 2 + mLineLength);

            mPath.moveTo(mWidth / 2 + mFrameWidth / 2 - mLineFrameHeight / 2, mHeight / 2 + mFrameHeight / 2 - mLineFrameHeight / 2 - mLineLength);
            mPath.lineTo(mWidth / 2 + mFrameWidth / 2 - mLineFrameHeight / 2, mHeight / 2 + mFrameHeight / 2 - mLineFrameHeight / 2);
            mPath.lineTo(mWidth / 2 + mFrameWidth / 2 - mLineLength, mHeight / 2 + mFrameHeight / 2 - mLineFrameHeight / 2);

            mPath.moveTo(mWidth / 2 - mFrameWidth / 2 + mLineFrameHeight / 2 + mLineLength, mHeight / 2 + mFrameHeight / 2 - mLineFrameHeight / 2);
            mPath.lineTo(mWidth / 2 - mFrameWidth / 2 + mLineFrameHeight / 2, mHeight / 2 + mFrameHeight / 2 - mLineFrameHeight / 2);
            mPath.lineTo(mWidth / 2 - mFrameWidth / 2 + mLineFrameHeight / 2, mHeight / 2 + mFrameHeight / 2 - mLineFrameHeight / 2 - mLineLength);
        }
        mFramePaint.setStrokeWidth(mLineFrameHeight);
        mFramePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mFramePaint);
    }

    public void startScan() {
        if (null != mValueAnimator) {
            if (!mValueAnimator.isRunning()) {
                mValueAnimator.start();
            }
        }
    }

    public void stopScan() {
        if (null != mValueAnimator) {
            if (mValueAnimator.isRunning()) {
                mValueAnimator.pause();
            }
        }
    }


}
