package com.oyf.basemodule.weight;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;

public class RippleView extends RelativeLayout {
    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Paint mPaint;
    public float radius;
    public int color;
    public int count = 5;

    public int stokeWidth;//如果是使用线的话

    public List<RippleCircleView> mViewLists = new ArrayList<>();
    public List<Animator> mViewAnimation = new ArrayList<>();
    public AnimatorSet mAnimationSet;

    private void init() {
        color = Color.RED;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(2);

        LayoutParams layoutParams = new LayoutParams(100, 100);
        layoutParams.addRule(CENTER_IN_PARENT, TRUE);

        float maxValue = 10;
        float time = 3000;
        float delay = time / (float) count;

        for (int i = 0; i < count; i++) {
            RippleCircleView rippleCircleView = new RippleCircleView(this);
            addView(rippleCircleView, layoutParams);
            mViewLists.add(rippleCircleView);

            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", maxValue, 1);
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", maxValue, 1);
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0, 1);

            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(rippleCircleView, scaleX, scaleY, alpha);
            objectAnimator.setDuration((long) time);
            objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            objectAnimator.setStartDelay((long) (i * delay));
            mViewAnimation.add(objectAnimator);
        }
        mAnimationSet = new AnimatorSet();
        mAnimationSet.setInterpolator(new AccelerateInterpolator());
        mAnimationSet.playTogether(mViewAnimation);
    }

    public boolean isRunning() {
        return mAnimationSet.isRunning();
    }

    public void start() {
        if (isRunning()) {
            return;
        }

        for (int i = 0; i < mViewAnimation.size(); i++) {
            ObjectAnimator animator = (ObjectAnimator) mViewAnimation.get(i);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            mViewLists.get(i).setVisibility(VISIBLE);
        }
        mAnimationSet.start();
    }

    public void stop() {
        if (isRunning()) {
            mAnimationSet.pause();
         /*   for (int i = 0; i < mViewAnimation.size(); i++) {
                ObjectAnimator animator = (ObjectAnimator) mViewAnimation.get(i);
                animator.setRepeatCount(0);
                int finalI = i;
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mViewLists.get(finalI).setVisibility(INVISIBLE);
                    }
                });
            }*/
        }
    }

}
