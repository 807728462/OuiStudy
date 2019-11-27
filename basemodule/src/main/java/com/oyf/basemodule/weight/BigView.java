package com.oyf.basemodule.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;


import com.oyf.basemodule.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * @创建者 oyf
 * @创建时间 2019/11/15 14:18
 * @描述
 **/
public class BigView extends View {
    public BigView(Context context) {
        this(context, null);
    }

    public BigView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BigView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    Paint mPaint;
    Bitmap mBitmap;
    int w;
    int h;
    Rect rect;
    BitmapRegionDecoder mRegionDecoder;
    Bitmap bitmap;
    int mImageHeight;
    int mImageWidth;
    BitmapFactory.Options options = new BitmapFactory.Options();

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        w = getWidth();
        h = getHeight();
    }

    public void setImage() throws IOException {

        InputStream open = getContext().getAssets().open("timg.jpg");
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(open, null, options);
        mImageHeight = options.outHeight;
        mImageWidth = options.outWidth;


        mRegionDecoder = BitmapRegionDecoder.newInstance(open, false);
        rect = new Rect(0, 0, Math.min(w, mImageWidth), Math.min(h, mImageHeight));
        //开启复用
        options.inMutable = true;
        //设置格式成RGB_565
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = false;
        mBitmap = mRegionDecoder.decodeRegion(rect, options);
        requestLayout();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w-50;
        this.h = h-50;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
    }

    private int px;
    private int py;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRegionDecoder == null) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                px = (int) event.getX();
                py = (int) event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                updateWH(event);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                px = 0;
                py = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateWH(MotionEvent event) {

        int dx = (int) (event.getX() - px);
        int dy = (int) (event.getY() - py);
        if (rect.left - dx >= 0 && rect.right - dx <= mImageWidth) {
            rect.left -= dx;
            rect.right -= dx;
            px = (int) event.getX();
        }
        if (rect.top - dy >= 0 && rect.bottom - dy <= mImageHeight) {
            rect.top -= dy;
            rect.bottom -= dy;
            py = (int) event.getY();
        }
        mBitmap = mRegionDecoder.decodeRegion(rect, options);
    }
}

