package com.oyf.basemodule.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class PxAdapterUtil {
    private static PxAdapterUtil utils;

    //这里是设计稿参考宽高
    public static final float STANDARD_WIDTH = 1080f;
    public static final float STANDARD_HEIGHT = 1920f;

    //这里是屏幕显示宽高
    private int mDisplayWidth;
    private int mDisplayHeight;

    private PxAdapterUtil(Context context) {
        //获取屏幕的宽高
        if (mDisplayWidth == 0 || mDisplayHeight == 0) {
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                //获取到app的显示测量大小，不包括底部导航栏，跟上面的状态栏
                manager.getDefaultDisplay().getMetrics(displayMetrics);
                if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
                    //横屏
                    mDisplayWidth = displayMetrics.heightPixels;
                    mDisplayHeight = displayMetrics.widthPixels;
                } else {
                    mDisplayWidth = displayMetrics.widthPixels;
                    mDisplayHeight = displayMetrics.heightPixels;
                }
            }
        }
    }

    public static PxAdapterUtil getInstance() {
        if (utils == null) {
            throw new RuntimeException("ViewCalculateUtil请先初始化");
        }
        return utils;
    }

    /**
     * @param context
     */
    public static void init(Context context) {
        if (utils == null) {
            utils = new PxAdapterUtil(context.getApplicationContext());
        }
    }

    /**
     * 得到状态栏高度，这种方式在某些特定的手机上获取不到，需要利用反射
     *
     * @param context
     * @return
     */
    public int getStatusBarHeight(Context context) {
        int resID = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resID > 0) {
            return context.getResources().getDimensionPixelSize(resID);
        }
        return 0;
    }

    /**
     * 获取水平方向的缩放比例
     *
     * @return
     */
    public float getWidthScale() {
        return mDisplayWidth / STANDARD_WIDTH;
    }

    /**
     * 获取水平方向的具体的值
     *
     * @param width
     * @return
     */
    public int getWidth(int width) {
        return Math.round(width * getWidthScale());
    }

    /**
     * 获取垂直方向的缩放比例
     *
     * @return
     */
    public float getHeightScale() {
        return mDisplayHeight / STANDARD_HEIGHT;
    }

    /**
     * 获取垂直方向的具体的值
     *
     * @param height
     * @return
     */
    public int getHeight(int height) {
        return Math.round(height * getHeightScale());
    }

    public int getScreenWidth() {
        return mDisplayWidth;
    }

    public int getScreenHeight() {
        return mDisplayHeight;
    }
}
