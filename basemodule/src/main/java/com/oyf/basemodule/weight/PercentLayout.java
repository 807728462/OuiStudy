package com.oyf.basemodule.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * 百分比适配
 */
public class PercentLayout extends RelativeLayout {

    public PercentLayout(Context context) {
        super(context);
    }

    public PercentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取父容器的尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ViewGroup.LayoutParams params = child.getLayoutParams();
            //如果说是百分比布局属性
            if (checkLayoutParams(params)) {
                LayoutParams lp = (LayoutParams) params;
                float widthPercent = lp.widthPercent;
                if (widthPercent > 0) {
                    params.width = (int) (widthSize * widthPercent);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends RelativeLayout.LayoutParams {

        private float widthPercent;
        private float heightPercent;
        private float marginLeftPercent;
        private float marginRightPercent;
        private float marginTopPercent;
        private float marginBottomPercent;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            //解析自定义属性
            //TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PercentLayout);
            //widthPercent = a.getFloat(R.styleable.PercentLayout_widthPercent, 0);
            //heightPercent = a.getFloat(R.styleable.PercentLayout_heightPercent, 0);
            //marginLeftPercent = a.getFloat(R.styleable.PercentLayout_marginLeftPercent, 0);
            //marginRightPercent = a.getFloat(R.styleable.PercentLayout_marginRightPercent, 0);
            //marginTopPercent = a.getFloat(R.styleable.PercentLayout_marginTopPercent, 0);
            //marginBottomPercent = a.getFloat(R.styleable.PercentLayout_marginBottomPercent, 0);
            //a.recycle();
        }
    }
}
