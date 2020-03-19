package com.oyf.basemodule.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Field;

public class StatusBarUtil {
    public static void setStatusBar(Activity activity) {
        setStatusBar(activity, Color.TRANSPARENT);
    }

    public static void setStatusBar(Activity activity, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色透明
            window.setStatusBarColor(color);

            int visibility = window.getDecorView().getSystemUiVisibility();
            //布局内容全屏展示
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            //隐藏虚拟导航栏
            //visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            //防止内容区域大小发生变化
            visibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

            window.getDecorView().setSystemUiVisibility(visibility);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 利用反射获取状态栏高度
     */
    private static int getStatusBarHeight(Activity activity) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setStateBar(Activity activity, View toolbar) {
        toolbar.setPadding(toolbar.getPaddingLeft(), getStatusBarHeight(activity), toolbar.getPaddingRight(), toolbar.getPaddingBottom());
    }

    /**
     * 设置toolbar中的title为滚动的
     *
     * @param toolbar
     */
    public static void setMarqueeForToolbarTitleView(final Toolbar toolbar) {
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                TextView mTitleTextView = getToolbarTitleView(toolbar);
                if (mTitleTextView == null) {
                    return;
                }
                mTitleTextView.setHorizontallyScrolling(true);
                mTitleTextView.setMarqueeRepeatLimit(-1);
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                mTitleTextView.setSelected(true);
            }
        });
    }

    public static TextView getToolbarTitleView(Toolbar toolbar) {
        Class<? extends Toolbar> clazz = toolbar.getClass();
        try {
            Field mTitleTextView = clazz.getDeclaredField("mTitleTextView");
            mTitleTextView.setAccessible(true);
            Object o = mTitleTextView.get(toolbar);
            if (o != null) {
                return (TextView) o;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
