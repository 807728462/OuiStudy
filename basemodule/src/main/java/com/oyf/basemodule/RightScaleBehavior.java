package com.oyf.basemodule;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;

public class RightScaleBehavior extends CoordinatorLayout.Behavior {

    int top;
    private boolean isRunning;

    public RightScaleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_INDICATOR_BOTTOM;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyConsumed > 0 && !isRunning && child.getVisibility() == View.VISIBLE) {
            //向下滚动
            onHide(child);
        } else if (dyConsumed < 0 && !isRunning && child.getVisibility() == View.VISIBLE) {
            //向上滚动
            onshow(child);
        }
        Log.d("nestedScroll", "dxConsumed=" + dxConsumed +
                ",dyConsumed=" + dyConsumed +
                ",dxUnconsumed=" + dxUnconsumed +
                ",dyUnconsumed=" + dyUnconsumed +
                ",type=" + type
        );
    }


    private void onshow(View child) {
        ViewCompat.animate(child)
                .scaleX(1)
                .scaleY(1)
                .setDuration(500)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        child.setVisibility(View.VISIBLE);
                        isRunning = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        isRunning = false;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        isRunning = false;
                    }
                }).start();
    }


    private void onHide(View child) {
        top = child.getTop();
        ViewCompat.animate(child)
                .scaleX(0)
                .scaleY(0)
                .setDuration(500)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        isRunning = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        child.setVisibility(View.INVISIBLE);
                        isRunning = false;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        isRunning = false;
                    }
                }).start();
    }
}
