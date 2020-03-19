package com.oyf.basemodule.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.oyf.basemodule.utils.PxAdapterUtil;


/**
 * 屏幕像素适配
 */
public class ScreenAdapterLayout extends RelativeLayout {
    //防止重复测量
    private boolean flag;

    public ScreenAdapterLayout(Context context) {
        super(context);
    }

    public ScreenAdapterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenAdapterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!flag){
            //得到宽高的缩放比例
            float scaleX = PxAdapterUtil.getInstance().getWidthScale();
            float scaleY = PxAdapterUtil.getInstance().getHeightScale();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                params.width = (int) (params.width * scaleX);
                params.height = (int) (params.height * scaleY);
                params.leftMargin = (int)(params.leftMargin * scaleX);
                params.rightMargin = (int)(params.rightMargin * scaleX);
                params.topMargin = (int)(params.topMargin * scaleY);
                params.bottomMargin = (int)(params.bottomMargin * scaleY);
            }
            flag = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
