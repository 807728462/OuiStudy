package com.oyf.basemodule.utils;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 适配控件
 */
public class ViewCalculateUtil {

    /**
     * 设置viewgroup中 view的高度宽度
     *
     * @param view
     * @param width
     * @param height
     */
    public static void setViewLayoutParam(View view, int width, int height, boolean asWidth) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams != null) {
            if (width != ViewGroup.LayoutParams.MATCH_PARENT &&
                    width != ViewGroup.LayoutParams.WRAP_CONTENT &&
                    width != ViewGroup.LayoutParams.FILL_PARENT) {
                layoutParams.width = PxAdapterUtil.getInstance().getWidth(width);
            } else {
                layoutParams.width = width;
            }
            if (height != ViewGroup.LayoutParams.MATCH_PARENT &&
                    height != ViewGroup.LayoutParams.WRAP_CONTENT &&
                    height != ViewGroup.LayoutParams.FILL_PARENT) {
                layoutParams.height = asWidth ? PxAdapterUtil.getInstance().getWidth(height) : PxAdapterUtil.getInstance().getHeight(height);
            } else {
                layoutParams.height = height;
            }
        }
    }

    /**
     * 父布局为RelativeLayout
     *
     * @param view         处理的view
     * @param width        宽
     * @param height       高
     * @param leftMargin   左边距
     * @param topMargin    上边距
     * @param rightMargin  右边距
     * @param bottomMargin 底边距
     * @param asWidth      是否只根据宽进行适配
     */
    public static void setViewRelativeLayoutParam(View view, int width, int height,
                                                  int leftMargin, int topMargin, int rightMargin, int bottomMargin,
                                                  boolean asWidth) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            if (width != RelativeLayout.LayoutParams.MATCH_PARENT &&
                    width != RelativeLayout.LayoutParams.WRAP_CONTENT &&
                    width != RelativeLayout.LayoutParams.FILL_PARENT) {
                layoutParams.width = PxAdapterUtil.getInstance().getWidth(width);
            } else {
                layoutParams.width = width;
            }
            if (height != RelativeLayout.LayoutParams.MATCH_PARENT &&
                    height != RelativeLayout.LayoutParams.WRAP_CONTENT &&
                    height != RelativeLayout.LayoutParams.FILL_PARENT) {
                layoutParams.height = asWidth ? PxAdapterUtil.getInstance().getWidth(height) :
                        PxAdapterUtil.getInstance().getHeight(height);
            } else {
                layoutParams.height = height;
            }
            layoutParams.leftMargin = PxAdapterUtil.getInstance().getWidth(leftMargin);
            layoutParams.topMargin = asWidth ? PxAdapterUtil.getInstance().getWidth(topMargin) : PxAdapterUtil.getInstance().getHeight(topMargin);
            layoutParams.rightMargin = PxAdapterUtil.getInstance().getWidth(rightMargin);
            layoutParams.bottomMargin = asWidth ? PxAdapterUtil.getInstance().getWidth(bottomMargin) : PxAdapterUtil.getInstance().getHeight(bottomMargin);

            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 父布局为LinearLayout
     *
     * @param view         处理的view
     * @param width        宽
     * @param height       高
     * @param leftMargin   左边距
     * @param topMargin    上边距
     * @param rightMargin  右边距
     * @param bottomMargin 底边距
     * @param asWidth      是否只根据宽进行适配
     */
    public static void setViewLinearLayoutParam(View view, int width, int height,
                                                int leftMargin, int topMargin, int rightMargin, int bottomMargin,
                                                boolean asWidth) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            if (width != LinearLayout.LayoutParams.MATCH_PARENT &&
                    width != LinearLayout.LayoutParams.WRAP_CONTENT &&
                    width != LinearLayout.LayoutParams.FILL_PARENT) {
                layoutParams.width = PxAdapterUtil.getInstance().getWidth(width);
            } else {
                layoutParams.width = width;
            }
            if (height != LinearLayout.LayoutParams.MATCH_PARENT &&
                    height != LinearLayout.LayoutParams.WRAP_CONTENT &&
                    height != LinearLayout.LayoutParams.FILL_PARENT) {
                layoutParams.height = asWidth ? PxAdapterUtil.getInstance().getWidth(height) :
                        PxAdapterUtil.getInstance().getHeight(height);
            } else {
                layoutParams.height = height;
            }
            layoutParams.leftMargin = PxAdapterUtil.getInstance().getWidth(leftMargin);
            layoutParams.topMargin = asWidth ? PxAdapterUtil.getInstance().getWidth(topMargin) : PxAdapterUtil.getInstance().getHeight(topMargin);
            layoutParams.rightMargin = PxAdapterUtil.getInstance().getWidth(rightMargin);
            layoutParams.bottomMargin = asWidth ? PxAdapterUtil.getInstance().getWidth(bottomMargin) : PxAdapterUtil.getInstance().getHeight(bottomMargin);

            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * @param view         处理的view
     * @param width        宽
     * @param height       高
     * @param leftMargin   左边距
     * @param topMargin    上边距
     * @param rightMargin  右边距
     * @param bottomMargin 底边距
     * @param asWidth      是否只根据宽进行适配
     */
    public static void setViewFrameLayoutParam(View view, int width, int height,
                                               int leftMargin, int topMargin, int rightMargin, int bottomMargin,
                                               boolean asWidth) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            if (width != FrameLayout.LayoutParams.MATCH_PARENT &&
                    width != FrameLayout.LayoutParams.WRAP_CONTENT &&
                    width != FrameLayout.LayoutParams.FILL_PARENT) {
                layoutParams.width = PxAdapterUtil.getInstance().getWidth(width);
            } else {
                layoutParams.width = width;
            }
            if (height != FrameLayout.LayoutParams.MATCH_PARENT &&
                    height != FrameLayout.LayoutParams.WRAP_CONTENT &&
                    height != FrameLayout.LayoutParams.FILL_PARENT) {
                layoutParams.height = asWidth ? PxAdapterUtil.getInstance().getWidth(height) :
                        PxAdapterUtil.getInstance().getHeight(height);
            } else {
                layoutParams.height = height;
            }
            layoutParams.leftMargin = PxAdapterUtil.getInstance().getWidth(leftMargin);
            layoutParams.topMargin = asWidth ? PxAdapterUtil.getInstance().getWidth(topMargin) : PxAdapterUtil.getInstance().getHeight(topMargin);
            layoutParams.rightMargin = PxAdapterUtil.getInstance().getWidth(rightMargin);
            layoutParams.bottomMargin = asWidth ? PxAdapterUtil.getInstance().getWidth(bottomMargin) : PxAdapterUtil.getInstance().getHeight(bottomMargin);

            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置字号
     *
     * @param view
     * @param size
     */
    public static void setTextSize(TextView view, int size) {
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, PxAdapterUtil.getInstance().getHeight(size));
    }

    /**
     * 设置view的内边距
     *
     * @param view
     * @param topPadding
     * @param bottomPadding
     * @param leftpadding
     * @param rightPadding
     */
    public static void setViewPadding(View view, int leftpadding, int topPadding,
                                      int rightPadding, int bottomPadding) {
        view.setPadding(PxAdapterUtil.getInstance().getWidth(leftpadding),
                PxAdapterUtil.getInstance().getHeight(topPadding),
                PxAdapterUtil.getInstance().getWidth(rightPadding),
                PxAdapterUtil.getInstance().getHeight(bottomPadding));
    }

}
