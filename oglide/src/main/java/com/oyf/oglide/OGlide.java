package com.oyf.oglide;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentActivity;

/**
 * @创建者 oyf
 * @创建时间 2020/4/20 15:38
 * @描述
 **/
public class OGlide {
    public static final String TAG = "OGlide------------";
    private ORequestManagerRetriever mORequestManagerRetriever;

    private static OGlide mOGlide;

    public OGlide() {
        mORequestManagerRetriever = new ORequestManagerRetriever();
    }

    public static ORequestManager with(Activity activity) {
        return getRetriever(activity).get(activity);
    }

    public static ORequestManager with(FragmentActivity fragmentActivity) {
        return getRetriever(fragmentActivity).get(fragmentActivity);
    }

    public static ORequestManager with(Context context) {
        return getRetriever(context).get(context);
    }

    private static ORequestManagerRetriever getRetriever(Context context) {
        return get(context).getORequestManagerRetriever();
    }

    public static OGlide get(Context context) {
        if (null == mOGlide) {
            synchronized (OGlide.class) {
                if (null == mOGlide) {
                    mOGlide = new OGlide();
                }
            }
        }
        return mOGlide;
    }

    public ORequestManagerRetriever getORequestManagerRetriever() {
        return mORequestManagerRetriever;
    }
}
