package com.oyf.basemodule.weight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import androidx.annotation.Nullable;

public class OdragBubbleView extends View {
    public OdragBubbleView(Context context) {
        this(context, null);
    }

    public OdragBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OdragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * 气泡默认状态--静止
     */
    private final int BUBBLE_STATE_DEFAULT = 0;
    /**
     * 气泡相连
     */
    private final int BUBBLE_STATE_CONNECT = 1;
    /**
     * 气泡分离
     */
    private final int BUBBLE_STATE_APART = 2;
    /**
     * 气泡消失
     */
    private final int BUBBLE_STATE_DISMISS = 3;
    private final int MOVE_OFFEST = 20;

    //当前气泡的状态
    private int mCurrentStatus = BUBBLE_STATE_DEFAULT;
    //气泡的颜色
    private int mBubbleColor = Color.RED;
    //气泡的文字
    private String mBubbleText = "12";
    //气泡文字颜色
    private int mBubbleTextColor = Color.WHITE;
    //不动气泡的半径
    private float mBubFixRadius;
    //随手拖动气泡的半径
    private float mBubMoveRadius = 40;
    //不动气泡的圆心
    private PointF mBubFixPoint;
    //随手拖动气泡的圆心
    private PointF mBubmovePoint;

    private Rect mTextRect;

    //手指触摸点到不可动圆心距离
    private float mDist = 0;

    /**
     * 气泡相连状态最大圆心距离
     */
    private float mMaxDist = 300;
    //气泡的画笔
    private Paint mBubblePaint;
    //文字的画笔
    private Paint mTextPaint;
    //贝塞尔曲线的区域
    private Path mbPath;

    private void init() {
        mBubblePaint = new Paint();
        mBubblePaint.setStyle(Paint.Style.FILL);
        mBubblePaint.setColor(mBubbleColor);
        mBubblePaint.setAntiAlias(true);
        mTextPaint = new Paint();

        mTextPaint.setTextSize(24);
        mTextPaint.setColor(mBubbleTextColor);
        mTextRect = new Rect();
        mbPath=new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mBubmovePoint.x, mBubmovePoint.y, mBubMoveRadius, mBubblePaint);
        mTextPaint.getTextBounds(mBubbleText, 0, mBubbleText.length(), mTextRect);
        canvas.drawText(mBubbleText,
                mBubmovePoint.x - mTextRect.width() / 2,
                mBubmovePoint.y + mTextRect.height() / 2, mTextPaint);

        if (mCurrentStatus==BUBBLE_STATE_CONNECT){
            canvas.drawCircle(mBubFixPoint.x, mBubFixPoint.y, mBubFixRadius, mBubblePaint);

            float sin=(mBubmovePoint.y-mBubFixPoint.y)/mDist;
            float cos=(mBubmovePoint.x-mBubFixPoint.x)/mDist;
            float ax=mBubFixPoint.x-sin*mBubFixRadius;
            float ay=mBubFixPoint.y+cos*mBubFixRadius;

            float bx=mBubmovePoint.x-sin*mBubMoveRadius;
            float by=mBubmovePoint.y+cos*mBubMoveRadius;

            float cx=mBubFixPoint.x+sin*mBubFixRadius;
            float cy=mBubFixPoint.y-cos*mBubFixRadius;

            float dx=mBubmovePoint.x+sin*mBubMoveRadius;
            float dy=mBubmovePoint.y-cos*mBubMoveRadius;

            int ccx= (int) ((mBubFixPoint.x+mBubmovePoint.x)/2);
            int ccy= (int) ((mBubFixPoint.y+mBubmovePoint.y)/2);
            mbPath.reset();
            mbPath.moveTo(ax,ay);
            //mbPath.lineTo(bx,by);
            mbPath.quadTo(ccx,ccy,bx,by);

            mbPath.lineTo(dx,dy);
            //mbPath.lineTo(cx,cy);

            mbPath.quadTo(ccx,ccy,cx,cy);
            mbPath.close();
            canvas.drawPath(mbPath,mBubblePaint);

        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mCurrentStatus == BUBBLE_STATE_DEFAULT) {
                    mDist = (float) Math.hypot(event.getX() - mBubFixPoint.x, event.getY() - mBubFixPoint.y);
                    if (mBubMoveRadius + MOVE_OFFEST > mDist) {
                        mCurrentStatus = BUBBLE_STATE_CONNECT;
                    } else {
                        mCurrentStatus = BUBBLE_STATE_DEFAULT;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentStatus != BUBBLE_STATE_DEFAULT) {
                    mDist = (float) Math.hypot(event.getX() - mBubFixPoint.x, event.getY() - mBubFixPoint.y);

                    mBubmovePoint.x = event.getX();
                    mBubmovePoint.y = event.getY();
                    if (mCurrentStatus == BUBBLE_STATE_CONNECT) {
                        if (mDist > mMaxDist) {
                            mCurrentStatus = BUBBLE_STATE_APART;
                        } else {
                            mBubFixRadius=(1-mDist/mMaxDist)*mBubMoveRadius;
                            mCurrentStatus = BUBBLE_STATE_CONNECT;
                        }
                    }

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentStatus==BUBBLE_STATE_CONNECT){
                    startBack();
                }else{
                    mBubmovePoint.x = mBubFixPoint.x;
                    mBubmovePoint.y = mBubFixPoint.y;
                    invalidate();
                }

                mCurrentStatus=BUBBLE_STATE_DEFAULT;
                break;
        }
        return true;
    }

    private void startBack() {
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(mDist,0.1f);
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator(2f));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDist= (float) animation.getAnimatedValue();
                float hypot = (float) Math.hypot(mBubmovePoint.x - mBubFixPoint.x, mBubmovePoint.y - mBubFixPoint.y);
                mBubmovePoint.x=mBubFixPoint.x+(mDist/hypot)*(mBubmovePoint.x-mBubFixPoint.x);
                mBubmovePoint.y=mBubFixPoint.y+(mDist/hypot)*(mBubmovePoint.y-mBubFixPoint.y);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void init(int w, int h) {
        mCurrentStatus = BUBBLE_STATE_DEFAULT;

        //设置固定气泡圆心初始坐标
        if (mBubFixPoint == null) {
            mBubFixPoint = new PointF(w / 2, h / 2);
        } else {
            mBubFixPoint.set(w / 2, h / 2);
        }
        //设置可动气泡圆心初始坐标
        if (mBubmovePoint == null) {
            mBubmovePoint = new PointF(w / 2, h / 2);
        } else {
            mBubmovePoint.set(w / 2, h / 2);
        }
    }
}
