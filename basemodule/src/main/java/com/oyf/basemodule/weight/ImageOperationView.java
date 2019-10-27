package com.oyf.basemodule.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.oyf.basemodule.R;

public class ImageOperationView extends View {
    public ImageOperationView(Context context) {
        this(context, null);
    }

    public ImageOperationView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageOperationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint mPaint;
    Bitmap mBitmap;

    Matrix mBitmapMatrix;
    RectF mBitmapRectf;

    boolean canScale;
    boolean canRotate;
    boolean canTranslate;

    Point mFristPoint;
    Point mSecondPoint;
    float startDistance;

    int currentX = 50;
    int currentY = 200;
    float imgScale;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);

        mBitmap = Bitmap.createScaledBitmap(
                mBitmap,
                displayMetrics.widthPixels - currentX * 2,
                (displayMetrics.widthPixels - currentX * 2) * mBitmap.getWidth() / mBitmap.getHeight(),
                true);

        mBitmapMatrix = new Matrix();
        mBitmapRectf = new RectF();
        mBitmapRectf.left = currentX;
        mBitmapRectf.top = currentY;
        mBitmapRectf.right = mBitmapRectf.left + mBitmap.getWidth();
        mBitmapRectf.bottom = mBitmapRectf.top + mBitmap.getHeight();

        updateTranslate(0, 0);

        mFristPoint = new Point();
        mSecondPoint = new Point();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GREEN);
        mPaint.setColor(Color.GRAY);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        canvas.drawRect(mBitmapRectf, mPaint);
        canvas.saveLayer(mBitmapRectf, mPaint);
        canvas.drawBitmap(mBitmap, mBitmapMatrix, mPaint);
        canvas.restore();
    }

    public void updateTranslate(float x, float y) {
        currentX += x;
        currentY += y;
        mBitmapMatrix.setTranslate(currentX, currentY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canScale = false;
                canRotate = false;
                canTranslate = true;
                mFristPoint.x = (int) event.getX();
                mFristPoint.y = (int) event.getY();

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    canScale = true;
                    canRotate = true;
                    canTranslate = false;
                    startDistance = calculationPoint(event);
                    mSecondPoint.x = (int) event.getX(1);
                    mSecondPoint.y = (int) event.getY(1);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (canTranslate) {
                    updateTranslate(event.getX() - mFristPoint.x, event.getY() - mFristPoint.y);
                    mFristPoint.x = (int) event.getX();
                    mFristPoint.y = (int) event.getY();
                }
                if (canRotate) {

                }

                if (canScale) {

                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getPointerCount() == 2) {
                    canScale = true;
                    canRotate = true;
                    canTranslate = false;
                } else if (event.getPointerCount() == 1) {
                    canScale = false;
                    canRotate = false;
                    canTranslate = true;
                    mSecondPoint.x = 0;
                    mSecondPoint.y = 0;
                    mFristPoint.x = (int) event.getX();
                    mFristPoint.y = (int) event.getY();
                } else {
                    canScale = false;
                    canRotate = false;
                    canTranslate = false;
                    mFristPoint.x = 0;
                    mFristPoint.y = 0;
                }
                break;
        }
        return true;
    }

    //两点间距离公式
    public float calculationPoint(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算两个向量之间的夹角
     *
     * @param lastVector 上一次两只手指形成的向量
     * @param vector     本次两只手指形成的向量
     * @return 返回手指旋转过的角度
     */
    private float calculateDeltaDegree(PointF lastVector, PointF vector) {
        float lastDegree = (float) Math.atan2(lastVector.y, lastVector.x);
        float degree = (float) Math.atan2(vector.y, vector.x);
        float deltaDegree = degree - lastDegree;
        return (float) Math.toDegrees(deltaDegree);
    }

    /**
     * 计算两个手指头之间的中心点的位置
     * x = (x1+x2)/2;
     * y = (y1+y2)/2;
     *
     * @param event 触摸事件
     * @return 返回中心点的坐标
     */
    private PointF midPoint(MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }
}
