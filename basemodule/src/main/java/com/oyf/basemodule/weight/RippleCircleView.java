package com.oyf.basemodule.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerTitleStrip;

public class RippleCircleView extends View {
    private int cx;
    private int cy;
    private float radius;
    public RippleView rippleView;

    public RippleCircleView(RippleView rippleView) {
        this(rippleView.getContext());
        this.rippleView = rippleView;
        setVisibility(VISIBLE);
    }

    public RippleCircleView(Context context) {
        super(context);
    }

    public RippleCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cx = w / 2;
        cy = h / 2;
        radius = Math.min(cx, cy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(cx, cy, radius - rippleView.stokeWidth / 2, rippleView.mPaint);
    }
}
