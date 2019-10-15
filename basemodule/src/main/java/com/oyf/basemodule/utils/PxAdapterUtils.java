package com.oyf.basemodule.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class PxAdapterUtils {
    private static PxAdapterUtils utils;

    //这里是设计稿参考宽高
    private static final float STANDARD_WIDTH = 360;
    private static final float STANDARD_HEIGHT = 720;

    //这里是屏幕显示宽高
    private int mDisplayWidth;
    private int mDisplayHeight;

    private PxAdapterUtils(Context context){
        //获取屏幕的宽高
        if(mDisplayWidth == 0 || mDisplayHeight == 0){
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null){
                DisplayMetrics displayMetrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(displayMetrics);
                if (displayMetrics.widthPixels > displayMetrics.heightPixels){
                    //横屏
                    mDisplayWidth = displayMetrics.heightPixels;
                    mDisplayHeight = displayMetrics.widthPixels;
                }else{
                    mDisplayWidth = displayMetrics.widthPixels;
                    mDisplayHeight = displayMetrics.heightPixels - getStatusBarHeight(context);
                }
            }
        }

    }
    //得到状态栏高度，这种方式在某些特定的手机上获取不到，需要利用反射
    public int getStatusBarHeight(Context context){
        int resID = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resID > 0){
            return context.getResources().getDimensionPixelSize(resID);
        }
        return 0;
    }

    public static PxAdapterUtils getInstance(Context context){
        if (utils == null){
            utils = new PxAdapterUtils(context.getApplicationContext());
        }
        return utils;
    }

    //获取水平方向的缩放比例
    public float getHorizontalScale(){
        return mDisplayWidth / STANDARD_WIDTH;
    }

    //获取垂直方向的缩放比例
    public float getVerticalScale(){
        return mDisplayHeight / STANDARD_HEIGHT;
    }
}
