package com.oyf.basemodule.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class DensityUtil {
    private static final float WINDTH = 1920;

    private static float mDensity = 0;
    private static float mScaledDensity = 0;

    public static void updateDensity(final Application application, Activity activity) {
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (mDensity == 0) {
            mDensity = displayMetrics.density;
            mScaledDensity = displayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        mScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        float targetDensity = displayMetrics.widthPixels / WINDTH;
        float targetScaledDensity = mScaledDensity * targetDensity / mDensity;
        float targetDensityDpi = targetDensity * 160;

        DisplayMetrics activityDisplayMetrice = activity.getResources().getDisplayMetrics();
        activityDisplayMetrice.density = targetDensity;
        activityDisplayMetrice.scaledDensity = targetScaledDensity;
        activityDisplayMetrice.densityDpi = (int) targetDensityDpi;
    }
}
