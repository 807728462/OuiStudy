package com.oyf.codecollection.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class TouchView extends View {
    public TouchView(Context context) {
        this(context, null);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    float lastX;
    float lastY;

    int screenW;
    int screenH;

    private void init() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenW = displayMetrics.widthPixels;
        screenH = displayMetrics.heightPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.RED);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                lastY = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) (event.getRawX() - lastX);
                int dy = (int) (event.getRawY() - lastY);
                Log.d("test", ",mx=" + dx + ",my=" + dy);
                int l = getLeft() + dx;
                int t = getTop() + dy;
                int r = l + getWidth();
                int b = t + getHeight();

                if (l < 0) {
                    l = 0;
                    r = getWidth();
                }
                if (t < 0) {
                    t = 0;
                    b = getHeight();
                }
                if (r > screenW) {
                    r = screenW;
                    l = screenW - getWidth();
                }

                if (b > screenH) {
                    b = screenH;
                    t = screenH - getHeight();
                }
                layout(l, t, r, b);

                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
        }
        return super.onTouchEvent(event);
    }
}
