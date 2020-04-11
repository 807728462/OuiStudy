package com.oyf.codecollection.chain;


/**
 * @创建者 oyf
 * @创建时间 2020/4/10 14:17
 * @描述
 **/
public interface Intercept {
    String TAG = Intercept.class.getSimpleName();

    void intercept(Chain chain);


    interface Chain {
        void proceed();
    }
}
