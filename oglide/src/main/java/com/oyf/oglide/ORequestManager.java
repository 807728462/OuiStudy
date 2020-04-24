package com.oyf.oglide;

import android.content.Context;
import android.util.Log;

import com.oyf.oglide.lifecycle.OLifecycle;
import com.oyf.oglide.lifecycle.OLifecycleListener;

/**
 * @创建者 oyf
 * @创建时间 2020/4/22 14:03
 * @描述
 **/
public class ORequestManager implements OLifecycleListener {
    private OLifecycle mLifecycle;

    private OGlide mGlide;
    private Context mContext;
    private ORequestTargetEngine mORequestTargetEngine;

    public ORequestManager(OGlide glide, OLifecycle lifecycle, Context context) {
        mORequestTargetEngine = new ORequestTargetEngine();
        this.mGlide = glide;
        this.mLifecycle = lifecycle;
        this.mContext = context;
        mLifecycle.addListener(this);
    }

    public ORequestTargetEngine load(String path) {
        // 把值传递给 资源加载引擎
        mORequestTargetEngine.loadInitAction(path, mContext);
        return mORequestTargetEngine;
    }

    @Override
    public void onStart() {
        Log.d(OGlide.TAG, "ORequestManager.onStart");
        if (null != mORequestTargetEngine) {
            mORequestTargetEngine.onStart();
        }
    }

    @Override
    public void onStop() {
        Log.d(OGlide.TAG, "ORequestManager.onStop");
        if (null != mORequestTargetEngine) {
            mORequestTargetEngine.onStop();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(OGlide.TAG, "ORequestManager.onDestroy");
        mLifecycle.removeListener(this);
        if (null != mORequestTargetEngine) {
            mORequestTargetEngine.onDestroy();
        }
    }
}
