package com.oyf.basemodule.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 旋转动画交过，扩散
 */
public class OsplashView extends View {
    public OsplashView(Context context) {
        this(context, null);
    }

    public OsplashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OsplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //旋转圆的画笔
    private Paint mRotatePaint;
    //扩散圆的画笔
    private Paint mHolePaint;
    //动画
    ValueAnimator valueAnimator;

    //背景颜色
    int mBgColor;
    //旋转圆的中心
    float mCenterX;
    float mCenterY;
    //扩散的最大距离
    float mMaxDistance;

    //小圆的半径
    float mRadius = 18f;
    //大圆的半径
    float mRotateRadius = 90f;

    //当前大圆的旋转角度
    private float mCurrentRotateAngle = 0F;
    //当前大圆的半径
    private float mCurrentRotateRadius = mRotateRadius;
    private float mCurrentRadius = mRadius;
    //扩散圆的半径
    private float mCurrentHoleRadius = 0F;
    //表示旋转动画的时长
    private int mRotateDuration = 1200;

    //圆的颜色集合
    private int[] mCircleColors;

    private void init() {
        mCenterX = 500;
        mCenterY = 500;
        mRotatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mHolePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHolePaint.setStyle(Paint.Style.FILL);
        mHolePaint.setColor(Color.GRAY);

        mCircleColors = new int[]{Color.RED, Color.GREEN, Color.YELLOW, Color.BLUE, Color.GRAY, Color.DKGRAY};
    }

    SplashStatus splashStatus;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w * 1f / 2;
        mCenterY = h * 1f / 2;
        mMaxDistance = (float) (Math.hypot(w, h) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (splashStatus == null) {
            splashStatus = new RotateStatus();
        }
        //drawCicles(canvas);
        splashStatus.drawState(canvas);
    }

    @SuppressLint("NewApi")
    private void drawBackGround(Canvas canvas) {
        if (mCurrentHoleRadius>0){
            canvas.drawColor(Color.WHITE);
            RectF rect=new RectF(0, 0, (int)(2 * mCenterX),  (int)(2 * mCenterY));
            canvas.saveLayer(rect,mHolePaint);


            mHolePaint.setColor(Color.GRAY);
            canvas.drawRect(rect,mHolePaint);
            mHolePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mHolePaint.setColor(Color.WHITE);
            canvas.drawCircle(mCenterX,mCenterY,mCurrentHoleRadius,mHolePaint);
            mHolePaint.setXfermode(null);
            canvas.restore();
        }else{
            mBgColor=Color.WHITE;
            canvas.drawColor(mBgColor);
        }
    }

    public void drawCicles(Canvas canvas) {
        canvas.translate(mCenterX, mCenterY);
        float cicleRotate = 360f / mCircleColors.length;
        for (int i = 0; i < mCircleColors.length; i++) {

            canvas.save();
            mRotatePaint.setColor(mCircleColors[i]);

            canvas.rotate(cicleRotate * i + mCurrentRotateAngle);
            canvas.drawCircle(mCurrentRotateRadius + mCurrentRadius, 0, mCurrentRadius, mRotatePaint);

            canvas.restore();
        }
    }

    private class RotateStatus extends SplashStatus {

        public RotateStatus() {
            valueAnimator = ValueAnimator.ofFloat(0, 360f);
            valueAnimator.setDuration(500);
            valueAnimator.setRepeatCount(1);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotateAngle = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    splashStatus = new MerginState();
                }
            });
            valueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackGround(canvas);
            drawCicles(canvas);
        }
    }

    public class MerginState extends SplashStatus {

        public MerginState() {
            valueAnimator = ValueAnimator.ofFloat(mCurrentRotateRadius, 0);
            valueAnimator.setDuration(1000);
            valueAnimator.setInterpolator(new AnticipateInterpolator(10f));
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotateRadius = (float) animation.getAnimatedValue();
                    if (mCurrentRotateRadius / mRotateRadius < 1)
                        mCurrentRadius = (mCurrentRotateRadius * mRadius) / mRotateRadius;
                    else
                        mCurrentRadius = mRadius;
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    splashStatus=new ScaleStatus();
                }
            });
            valueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackGround(canvas);
            drawCicles(canvas);
        }
    }

    public class ScaleStatus extends SplashStatus{

        public ScaleStatus(){
            valueAnimator =ValueAnimator.ofFloat(100,mMaxDistance);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentHoleRadius= (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mCurrentHoleRadius=0;
                    mCurrentRadius=mRadius;
                    mCurrentRotateAngle=0;
                    mCurrentRotateRadius=mRotateRadius;

                }
            });
            valueAnimator.start();
        }

        @Override
        void drawState(Canvas canvas) {
            drawBackGround(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            splashStatus=new RotateStatus();
        }
        return super.onTouchEvent(event);
    }

    public abstract class SplashStatus {
        abstract void drawState(Canvas canvas);
    }
}
