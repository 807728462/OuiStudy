package com.oyf.basemodule.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import com.oyf.basemodule.R;

public class GrayImageView extends View {
    public GrayImageView(Context context) {
        this(context, null);
    }

    public GrayImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrayImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Bitmap mBitmap;
    private Paint mPaint;
    private ColorMatrixColorFilter mColorMatrixColorFilter;

    private int width;
    private int height;

    private int mBitmapW;
    private int mBitmapH;
    private RectF rect;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        mColorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        mPaint.setColorFilter(mColorMatrixColorFilter);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        if (width < mBitmap.getWidth()) {
            mBitmapW = width;

            mBitmapH = width * mBitmap.getHeight() / mBitmap.getWidth();
        }

        rect = new RectF(0, 0, mBitmapW, mBitmapH);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, null, rect, mPaint);
    }
}
