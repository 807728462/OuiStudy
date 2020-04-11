package com.oyf.codecollection.chain;

import android.util.Log;

/**
 * @创建者 oyf
 * @创建时间 2020/4/10 14:49
 * @描述
 **/
public class OneInterceptor implements Intercept {
    @Override
    public void intercept(Chain chain) {
        Log.d(TAG, "OneInterceptor执行了");
        chain.proceed();
    }
}
