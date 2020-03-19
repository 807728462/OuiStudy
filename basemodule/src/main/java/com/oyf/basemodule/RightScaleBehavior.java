package com.oyf.basemodule;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;

public class RightScaleBehavior extends CoordinatorLayout.Behavior {
    private static final String TAG = RightScaleBehavior.class.getName();

    private int top;
    private boolean isRunning;

    public RightScaleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.d(TAG, "axes=" + axes);
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyConsumed > 0 && !isRunning && child.getVisibility() == View.VISIBLE) {
            //向下滚动
            onHide(child);
        } else if (dyConsumed < 0 && !isRunning && child.getVisibility() == View.INVISIBLE) {
            //向上滚动
            onshow(child);
        }
        Log.d(TAG, "dxConsumed=" + dxConsumed +
                ",dyConsumed=" + dyConsumed +
                ",dxUnconsumed=" + dxUnconsumed +
                ",dyUnconsumed=" + dyUnconsumed +
                ",type=" + type
        );
    }


    private void onshow(View child) {
        isRunning = true;
        ViewCompat.animate(child)
                .scaleX(1)
                .scaleY(1)
                .setDuration(500)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        super.onAnimationStart(view);
                        child.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        child.setVisibility(View.VISIBLE);
                        isRunning = false;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        super.onAnimationCancel(view);
                        isRunning = false;
                    }
                }).start();
    }

    private void onHide(View child) {
        isRunning = true;
        top = child.getTop();
        ViewCompat.animate(child)
                .scaleX(0)
                .scaleY(0)
                .setDuration(500)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        super.onAnimationStart(view);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        super.onAnimationEnd(view);
                        child.setVisibility(View.INVISIBLE);
                        isRunning = false;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        super.onAnimationCancel(view);
                        isRunning = false;
                    }
                }).start();
    }
}
