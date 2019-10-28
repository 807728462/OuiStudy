package com.oyf.basemodule.weight;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.oyf.basemodule.R;

@SuppressLint("AppCompatCustomView")
public class PatImageView extends ImageView {
    private Matrix matrix;
    private Matrix savedMatrix;

    private boolean long_touch = false;
    private static int NONE = 0;
    private static int DRAG = 1;    // 拖动
    private static int ZOOM = 2;    // 缩放
    private static int ROTA = 3;    // 旋转
    private int mode = NONE;

    private PointF startPoint;
    private PointF middlePoint;

    private float oldDistance;
    private float oldAngle;

    private Vibrator vibrator;

    private GestureDetector gdetector;

    public PatImageView(Context context) {
        this(context, null);
    }

    public PatImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        matrix = new Matrix();
        savedMatrix = new Matrix();
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mBitmap = Bitmap.createScaledBitmap(
                mBitmap,
                displayMetrics.widthPixels - 100,
                (displayMetrics.widthPixels - 100 * 2) * mBitmap.getWidth() / mBitmap.getHeight(),
                true);

        setImageBitmap(mBitmap);
        matrix.setTranslate(0f, 0f);
        setScaleType(ScaleType.MATRIX);
        setImageMatrix(matrix);

        startPoint = new PointF();
        middlePoint = new PointF();

        oldDistance = 1f;

        gdetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                long_touch = true;
                vibrator = (Vibrator) getContext().getSystemService(Service.VIBRATOR_SERVICE);
                // 振动50ms，提示后续的操作将是旋转图片，而非缩放图片
                vibrator.vibrate(50);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:           // 第一个手指touch
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        long_touch = false;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:   // 第二个手指touch
                        oldDistance = getDistance(event);   // 计算第二个手指touch时，两指之间的距离
                        oldAngle = getDegree(event);        // 计算第二个手指touch时，两指所形成的直线和x轴的角度
                        if (oldDistance > 10f) {
                            savedMatrix.set(matrix);
                            middlePoint = midPoint(event);
                            if (!long_touch) {
                                mode = ZOOM;
                            } else {
                                mode = ROTA;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (vibrator != null) vibrator.cancel();
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
                        }

                        if (mode == ZOOM) {
                            float newDistance = getDistance(event);

                            if (newDistance > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDistance / oldDistance;
                                matrix.postScale(scale, scale, middlePoint.x, middlePoint.y);
                            }
                        }

                        if (mode == ROTA) {
                            float newAngle = getDegree(event);
                            matrix.set(savedMatrix);
                            float degrees = newAngle - oldAngle;
                            matrix.postRotate(degrees, middlePoint.x, middlePoint.y);
                        }
                        break;
                }
                setImageMatrix(matrix);
                invalidate();
                gdetector.onTouchEvent(event);
                return true;
            }
        });
    }

    // 计算两个手指之间的距离
    private float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 计算两个手指所形成的直线和x轴的角度
    private float getDegree(MotionEvent event) {
        return (float) (Math.atan((event.getY(1) - event.getY(0)) / (event.getX(1) - event.getX(0))) * 180f);
    }

    // 计算两个手指之间，中间点的坐标
    private PointF midPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);

        return point;
    }
}