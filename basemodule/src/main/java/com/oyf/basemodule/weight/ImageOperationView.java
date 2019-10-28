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
    private static int NONE = 0;
    private static int DRAG = 1;    // 拖动
    private static int ZOOM = 2;    // 缩放
    private static int ROTATE = 3;    // 旋转

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

    Matrix mSaveMatrix;
    Matrix mMatrix;

    int mode = NONE;

    Point mFristPoint;
    Point mSecondPoint;
    PointF mCenterPointF;
    float oldDistance;
    float oldAngle;

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(40);
        mPaint.setColor(Color.RED);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);

        mBitmap = Bitmap.createScaledBitmap(
                mBitmap,
                displayMetrics.widthPixels,
                displayMetrics.widthPixels * mBitmap.getWidth() / mBitmap.getHeight(),
                true);

        mSaveMatrix = new Matrix();
        mMatrix = new Matrix();
        mMatrix.setTranslate(0, 100);
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
        canvas.drawText("单点拖拽，两指缩放，三指旋转", 0, 100, mPaint);
        canvas.drawBitmap(mBitmap, mMatrix, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSaveMatrix.set(mMatrix);
                mode = DRAG;
                mFristPoint.x = (int) event.getX();
                mFristPoint.y = (int) event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDistance = calculationPoint(event);
                oldAngle = getDegree(event);
                if (oldDistance > 10) {
                    if (event.getPointerCount() == 3) {
                        mode = ROTATE;
                    } else {
                        mode = ZOOM;
                    }
                    mSaveMatrix.set(mMatrix);
                    mCenterPointF = midPoint(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    mMatrix.set(mSaveMatrix);
                    mMatrix.postTranslate(event.getX() - mFristPoint.x, event.getY() - mFristPoint.y);
                }
                if (mode == ZOOM) {
                    float nowDistance = calculationPoint(event);
                    if (nowDistance > 10) {
                        mMatrix.set(mSaveMatrix);
                        mMatrix.postScale(nowDistance / oldDistance, nowDistance / oldDistance, mCenterPointF.x, mCenterPointF.y);
                    }
                }
                if (mode == ROTATE) {
                    float nowDegree = getDegree(event);
                    mMatrix.set(mSaveMatrix);
                    mMatrix.postRotate(nowDegree - oldAngle);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }


    /**
     * 计算两个点之间的距离
     *
     * @param event
     * @return
     */
    public float calculationPoint(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }


    /**
     * 计算两个手指触摸时候的角度
     *
     * @param event
     * @return
     */
    private float getDegree(MotionEvent event) {
        return (float) (Math.atan((event.getY(1) - event.getY(0)) / (event.getX(1) - event.getX(0))) * 180f);
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
