package com.oyf.codecollection.chain;

import android.util.Log;

import java.util.List;

/**
 * @创建者 oyf
 * @创建时间 2020/4/10 14:16
 * @描述
 **/
public class RealInterceptorChain implements Intercept.Chain {
    private static final String TAG = RealInterceptorChain.class.getSimpleName();
    protected List<Intercept> mLists;
    protected int mIndex;

    public RealInterceptorChain(List<Intercept> lists, int index) {
        mLists = lists;
        mIndex = index;
    }

    @Override
    public void proceed() {
        if (mIndex >= mLists.size()) {
            Log.d(TAG, "index超过数组长度了");
            return;
        }
        RealInterceptorChain interceptorChain = new RealInterceptorChain(mLists, mIndex + 1);
        Intercept intercept = mLists.get(mIndex);
        intercept.intercept(interceptorChain);
    }
}
