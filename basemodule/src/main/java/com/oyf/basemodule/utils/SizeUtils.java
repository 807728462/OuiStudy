package com.oyf.basemodule.utils;

import android.content.Context;
import android.util.TypedValue;

public class SizeUtils {
    private SizeUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        int scale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
        return scale;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        int fontScale = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
        return fontScale;
    }
}
