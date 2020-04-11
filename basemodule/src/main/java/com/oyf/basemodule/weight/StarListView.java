package com.oyf.basemodule.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.oyf.basemodule.R;

import java.util.Random;

/**
 * @创建者 oyf
 * @创建时间 2020/4/10 15:43
 * @描述 星级评级列表
 **/
public class StarListView extends View {
    private static final String TAG = StarListView.class.getSimpleName();

    public StarListView(Context context) {
        this(context, null);
    }

    public StarListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;

    private int w;
    private int h;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mSpace;

    private int mMaxStarNum;
    private int mSelectStarNum;
    private int mDirection;

    private Bitmap mStarSelectBitmap;
    private Bitmap mStarUnSelectBitmap;

    private int count = 0;
    private Random mRandom;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mSpace = 10;
        mMaxStarNum = 5;
        mSelectStarNum = 4;
        mDirection = Gravity.LEFT;
        mStarSelectBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_star_select);
        mStarUnSelectBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_start_unselect);

        mBitmapWidth = mStarSelectBitmap.getWidth();
        mBitmapHeight = mStarSelectBitmap.getHeight();
        h = mStarSelectBitmap.getHeight();
        w = h * mMaxStarNum;

        mRandom = new Random();
        Log.d(TAG, "init");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure");
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        switch (hMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                h = Math.max(h, hSize);
                break;
            case MeasureSpec.EXACTLY:
                h = hSize;
                break;
            default:
                break;
        }
        updateBitmap();
        w = wSize;
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h, hMode));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mMaxStarNum; i++) {
            if (mDirection == Gravity.LEFT) {
                int left = i * mBitmapWidth + i * mSpace;
                if (i < mSelectStarNum) {
                    canvas.drawBitmap(mStarSelectBitmap, left, 0, mPaint);
                } else {
                    canvas.drawBitmap(mStarUnSelectBitmap, left, 0, mPaint);
                }
            } else if (mDirection == Gravity.RIGHT) {
                int left = w - (mMaxStarNum - i) * mBitmapWidth - (mMaxStarNum - i) * mSpace;
                if (i < mSelectStarNum) {
                    canvas.drawBitmap(mStarSelectBitmap, left, 0, mPaint);
                } else {
                    canvas.drawBitmap(mStarUnSelectBitmap, left, 0, mPaint);
                }
            }
        }


        Log.d(TAG, "max=" + mMaxStarNum + ",select=" + mSelectStarNum + ",dir=" + (mDirection == 3 ? "LEFT" : "RIGHT"));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                if (count % 3 == 0) {
                    setMaxStarNum(mRandom.nextInt(6) + 1);
                } else if (count % 3 == 1) {
                    setSelectStarNum(mRandom.nextInt(mMaxStarNum));
                } else {
                    setDirection(mRandom.nextInt(2) == 1 ? Gravity.LEFT : Gravity.RIGHT);
                }
                count++;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateBitmap() {
        this.mBitmapHeight = h;
        this.mBitmapWidth = mBitmapHeight;
        mStarSelectBitmap = creatNewBitmap(mStarSelectBitmap, mBitmapWidth, mBitmapHeight);
        mStarUnSelectBitmap = creatNewBitmap(mStarUnSelectBitmap, mBitmapWidth, mBitmapHeight);
    }

    public void setMaxStarNum(int starNum) {
        this.mMaxStarNum = starNum;
        invalidate();
    }

    public void setSelectStarNum(int selectStarNum) {
        this.mSelectStarNum = selectStarNum;
        invalidate();
    }

    public void setDirection(int direction) {
        this.mDirection = direction;
        invalidate();
    }

    public Bitmap creatNewBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        // 获得图片的宽高.
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBitmap;
    }
}
