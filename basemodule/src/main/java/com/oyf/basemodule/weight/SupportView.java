package com.oyf.basemodule.weight;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @创建者 oyf
 * @创建时间 2020/4/3 13:53
 * @描述
 **/
public class SupportView extends FrameLayout {
    private static final String TAG = "SupportView";

    public SupportView(@NonNull Context context) {
        this(context, null);
    }

    public SupportView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SupportView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private int[] colors = new int[]{Color.RED, Color.GREEN, Color.BLUE};
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private ImageView mMainImgView;
    private volatile List<ImageView> mViewLists = new ArrayList<>();
    private volatile List<ValueAnimator> mAnimators = new ArrayList<>();
    private Random mRandom;

    private int mImageWidth = 100;
    private int mImageHeight = 100;

    private void initView() {
        addMainView();
        setBackgroundColor(Color.GRAY);
    }

    private void addMainView() {
        //removeAllViews();
        mMainImgView = new ImageView(mContext);
        mMainImgView.setBackgroundColor(Color.BLACK);
        LayoutParams layoutParams = new LayoutParams(mImageWidth, mImageHeight);
        /*layoutParams.topMargin = mHeight - mImageHeight;
        layoutParams.rightMargin = mWidth / 2 - mImageWidth / 2;*/
        mMainImgView.setLayoutParams(layoutParams);
        addView(mMainImgView);
        mMainImgView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageView();
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mMainImgView.layout(mWidth / 2 - mImageWidth / 2, mHeight - mImageHeight, mWidth / 2 + mImageWidth / 2, mHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private void addImageView() {
        if (null == mRandom) {
            mRandom = new Random();
        }
        ImageView imageView = new ImageView(mContext);
        imageView.setBackgroundColor(Color.BLACK);
        LayoutParams layoutParams = new LayoutParams(1, 1);
        layoutParams.topMargin = mHeight - mImageHeight * 2 + mImageHeight / 2;
        layoutParams.leftMargin = mWidth / 2;
        imageView.setLayoutParams(layoutParams);
        mViewLists.add(imageView);
        addView(imageView);
        imageView.animate().scaleX(mImageWidth).scaleY(mImageHeight).setDuration(500)
                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        float animatedValue = (float) animation.getAnimatedValue();
                        if (animatedValue == 1) {
                            upAnimate(imageView);
                        }
                    }
                }).start();
    }

    private void upAnimate(View view) {
        int top = view.getTop();
        int left = view.getLeft();
        PointF start = new PointF(left, top);
        PointF end = new PointF(left, 0);

        int dx = left;
        boolean b = mRandom.nextBoolean();
        PointF leftPoint = new PointF(left - dx, top * 3 / 4);
        PointF rightPoint = new PointF(left + dx, top / 4);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BezierEvaluator(leftPoint, rightPoint), start, end);
        valueAnimator.setDuration(3000)
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        PointF animatedValue = (PointF) animation.getAnimatedValue();
                        view.setTranslationY(-top + animatedValue.y);
                        float sx = b ? animatedValue.x - left : -animatedValue.x + left;
                        view.setTranslationX(sx);
                        Log.d(TAG, "坐标=" + sx + "-" + animatedValue.y);
                        if (animatedValue.y == end.y) {
                            mAnimators.remove(valueAnimator);
                            mViewLists.remove(view);
                            removeView(view);
                        }
                    }
                });
        valueAnimator.start();
        mAnimators.add(valueAnimator);
    }


    public static class BezierEvaluator implements TypeEvaluator<PointF> {

        private PointF mControlP1;
        private PointF mControlP2;


        public BezierEvaluator(PointF controlP1, PointF controlP2) {
            this.mControlP1 = controlP1;
            this.mControlP2 = controlP2;
        }


        @Override
        public PointF evaluate(float time, PointF start, PointF end) {
            float timeLeft = 1.0f - time;
            PointF point = new PointF();
            point.x = timeLeft * timeLeft * timeLeft * (start.x) + 3 * timeLeft * timeLeft * time *
                    (mControlP1.x) + 3 * timeLeft * time *
                    time * (mControlP2.x) + time * time * time * (end.x);
            point.y = timeLeft * timeLeft * timeLeft * (start.y) + 3 * timeLeft * timeLeft * time *
                    (mControlP1.y) + 3 * timeLeft * time *
                    time * (mControlP2.y) + time * time * time * (end.y);
            return point;
        }
    }
}
