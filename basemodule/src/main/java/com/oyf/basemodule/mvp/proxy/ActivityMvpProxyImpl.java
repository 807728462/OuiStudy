package com.oyf.basemodule.mvp.proxy;

import com.oyf.basemodule.mvp.IView;

/**
 * @创建者 oyf
 * @创建时间 2020/6/1 16:09
 * @描述 activity中处理多个共同的方法
 **/
public class ActivityMvpProxyImpl<V extends IView> extends MvpProxyImpl<V> implements ActivityMvpProxy {
    public ActivityMvpProxyImpl(V view) {
        super(view);
    }
}
