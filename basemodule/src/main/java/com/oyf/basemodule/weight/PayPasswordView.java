package com.oyf.basemodule.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 九宫格密码支付
 **/
public class PayPasswordView extends View {
    public PayPasswordView(Context context) {
        this(context, null);
    }

    public PayPasswordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PayPasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint;
    private Paint mLinePaint;
    private int mBigRadius = 60;
    private int mNormalRadius = 10;
    private int mSelectRadius = 20;
    private int mNormalColor = Color.GRAY;
    private int mSelectColor = Color.GREEN;

    private List<CornerView> mAllViews;
    private List<CornerView> mSelectViews;
    private CornerView mLastView;
    private Path mPath;

    int w;
    int h;

    private void init() {
        mPaint = new Paint();
        mLinePaint = new Paint();
        mPaint.setAntiAlias(true);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(mSelectColor);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mAllViews = new ArrayList<>();
        mSelectViews = new ArrayList<>();
        mPath = new Path();
    }

    private void setViewWH() {
        int one = w / 3 / 2;
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                CornerView cornerView = new CornerView(mBigRadius, mNormalRadius, mSelectRadius, mNormalColor, mSelectColor);
                int cx = one * j * 2 - one;
                int cy = one * i * 2 - one;
                cornerView.setCenter(cx, cy);
                cornerView.setTag((i - 1) * 3 + j);
                mAllViews.add(cornerView);
            }
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        setViewWH();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (CornerView mAllView : mAllViews) {
            if (mSelectViews.contains(mAllView)) {
                mAllView.draw(canvas, mPaint, true);
            } else {
                mAllView.draw(canvas, mPaint, false);
            }
        }
        canvas.drawPath(mPath, mLinePaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int cy = (int) event.getY();
        int cx = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                move(cx, cy);
                break;
            case MotionEvent.ACTION_UP:
                reset();
                break;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    public void reset() {
        StringBuilder sb = new StringBuilder();
        for (CornerView selectView : mSelectViews) {
            sb.append(selectView.getTag());
        }
        Toast.makeText(getContext(), "结果：" + sb.toString(), Toast.LENGTH_SHORT).show();
        mPath.reset();
        mLastView = null;
        mSelectViews.clear();
    }

    public void move(int x, int y) {
        mPath.reset();
        CornerView cornerView = countIn(x, y);
        if (cornerView != null) {
            if (mSelectViews.contains(cornerView)) {
                //存在就忽略
            } else {
                mLastView = cornerView;
                mSelectViews.add(cornerView);
            }
        } else {
            //点击在外部
        }
        if (mSelectViews.size() >= 2) {
            for (int i = 0; i < mSelectViews.size() - 1; i++) {
                CornerView pView = mSelectViews.get(i);
                CornerView nView = mSelectViews.get(i + 1);
                int distance = distance(nView.cx, nView.cy, pView.cx, pView.cy);
                int ccx = mBigRadius * (nView.cx - pView.cx) / distance;
                int ccy = mBigRadius * (nView.cy - pView.cy) / distance;
                mPath.moveTo(pView.cx + ccx, pView.cy + ccy);
                mPath.lineTo(nView.cx - ccx, nView.cy - ccy);
            }
        }

        if (mLastView != null) {
            int distance = distance(mLastView.cx, mLastView.cy, x, y);
            int ccx = mBigRadius * (x - mLastView.cx) / distance;
            int ccy = mBigRadius * (y - mLastView.cy) / distance;
            mPath.moveTo(mLastView.cx + ccx, mLastView.cy + ccy);
            mPath.lineTo(x, y);
        }

    }

    public int distance(int cx, int cy, int x, int y) {
        return (int) Math.sqrt(Math.pow(cx - x, 2) + Math.pow(cy - y, 2));
    }

    public CornerView countIn(int x, int y) {
        for (CornerView mAllView : mAllViews) {
            if (mAllView.isContain(x, y)) {
                return mAllView;
            }
        }
        return null;
    }

    private static class CornerView {
        private int mTag;
        private int mBigRadius;
        private Path mBigPath;
        private int mNormalRadius;
        private int mSelectRadius;
        private int mNormalColor;
        private int mSelectColor;
        private int cx;
        private int cy;
        Region region;

        public CornerView() {
        }

        public CornerView(int mBigRadius, int mNormalRadius, int mSelectRadius, int mNormalColor, int mSelectColor) {
            this.mBigRadius = mBigRadius;
            this.mNormalRadius = mNormalRadius;
            this.mSelectRadius = mSelectRadius;
            this.mNormalColor = mNormalColor;
            this.mSelectColor = mSelectColor;
            mBigPath = new Path();
        }

        public void setCenter(int x, int y) {
            cx = x;
            cy = y;
            mBigPath.addCircle(cx, cy, mBigRadius, Path.Direction.CW);
        }

        public int getTag() {
            return mTag;
        }

        public void setTag(int mTag) {
            this.mTag = mTag;
        }

        public boolean isContain(int x, int y) {
            if (region == null) {
                region = new Region();
                region.setPath(mBigPath, new Region(cx - mBigRadius, cy - mBigRadius, cx + mBigRadius, cy + mBigRadius));
            }
            return region.contains(x, y);
        }

        public void draw(Canvas canvas, Paint mPaint, boolean isSelect) {
            if (isSelect) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(0);
                mPaint.setColor(mSelectColor);
                canvas.drawCircle(cx, cy, mSelectRadius, mPaint);
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(0);
                mPaint.setColor(mNormalColor);
                canvas.drawCircle(cx, cy, mNormalRadius, mPaint);
            }

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(2);
            canvas.drawCircle(cx, cy, mBigRadius, mPaint);
        }

    }
}
