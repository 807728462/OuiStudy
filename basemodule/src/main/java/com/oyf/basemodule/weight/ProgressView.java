package com.oyf.basemodule.weight;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.oyf.basemodule.R;

/**
 * @创建者 oyf
 * @创建时间 2019/11/11 13:44
 * @描述 进度条控件
 **/
public class ProgressView extends View {

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Paint mBorderPaint;
    private Paint mProgressPaint;
    //开始颜色
    private int startColor;
    //结束颜色
    private int endColor;
    //文字颜色
    private int textColor;
    //边框颜色
    private int borderColor;
    //进度条背景
    private int progressColor;
    //进度
    private float progress;
    private float max;
    //边框宽度
    private int strokeW;
    //文字距离边框宽度
    private int textPadding;
    //内间距
    private int padding;
    //控件宽高
    private int width;
    private int height;
    //显示进度条的宽高
    private int showW;
    private int showH;
    //文字的起始点
    private int textX;
    private int textY;
    //渐变颜色数组
    private int[] colors;
    private float[] positions = new float[]{0, 1};
    //是否显示文字
    private boolean isShowText;
    private String text;
    //动画设置进度
    private ValueAnimator valueAnimator;

    private void init(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.ProgressView);
        startColor = typedArray.getColor(R.styleable.ProgressView_startColor, Color.GREEN);
        endColor = typedArray.getColor(R.styleable.ProgressView_endColor, Color.RED);
        textColor = typedArray.getColor(R.styleable.ProgressView_textColor, Color.WHITE);
        borderColor = typedArray.getColor(R.styleable.ProgressView_textColor, Color.GREEN);
        progressColor = typedArray.getColor(R.styleable.ProgressView_progressColor, Color.GRAY);
        progress = typedArray.getFloat(R.styleable.ProgressView_textColor, 0.5f);
        max = typedArray.getFloat(R.styleable.ProgressView_max, 100);
        strokeW = typedArray.getInt(R.styleable.ProgressView_strokeW, 4);
        textPadding = typedArray.getInt(R.styleable.ProgressView_textPadding, 10);
        typedArray.recycle();
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(borderColor);

        mProgressPaint = new Paint();
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        colors = new int[]{startColor, endColor};
        isShowText = true;
        setProgress(progress);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
        padding = getPaddingLeft();
        this.showW = width - 2 * padding;
        this.showH = height - 2 * padding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawProgress(canvas);
        if (isShowText) {
            drawText(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(strokeW);
        mBorderPaint.setColor(borderColor);
        RectF rect = new RectF(padding, padding, padding + showW, padding + showH);
        canvas.drawRoundRect(rect, showH / 2, showH / 2, mBorderPaint);
        mBorderPaint.setColor(progressColor);
        mBorderPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, showH / 2, showH / 2, mBorderPaint);
    }

    private void drawProgress(Canvas canvas) {
        //计算进度条的数据
        int startX = padding + showH / 2;
        int startY = padding + showH / 2;
        int endX = (int) (startX + showW * progress);
        int endY = padding + showH;
        LinearGradient linearGradient = new LinearGradient(startX, startY, endX, endY, colors, positions, Shader.TileMode.CLAMP);
        mProgressPaint.setShader(linearGradient);
        RectF rect = new RectF(padding - strokeW / 2, padding - strokeW / 2, padding + showW * progress + strokeW / 2, padding + showH + strokeW / 2);
        canvas.drawRoundRect(rect, showH / 2, showH / 2, mProgressPaint);
    }

    private void drawText(Canvas canvas) { //计算文字的位置
        text = (int) (progress * 100) + "%";
        Rect bounds = new Rect();
        mBorderPaint.setTextSize(showH - textPadding);
        mBorderPaint.getTextBounds(text, 0, text.length(), bounds);
        int textW = bounds.right - bounds.left;
        int textH = bounds.bottom - bounds.top;
        textX = width / 2 - textW / 2;
        textY = height / 2 + textH / 2;
        mBorderPaint.setColor(textColor);
        mBorderPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, textX, textY, mBorderPaint);
    }

    public void setProgress(float current) {
        valueAnimator = ObjectAnimator.ofFloat(0, current);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }
}
