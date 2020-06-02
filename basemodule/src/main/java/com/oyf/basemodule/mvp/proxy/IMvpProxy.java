package com.oyf.basemodule.mvp.proxy;

/**
 * @创建者 oyf
 * @创建时间 2020/6/1 16:03
 * @描述 通过代理的方式，进行presenter进行注入第二个presenter
 **/
public interface IMvpProxy {
    void bindMvpProxy();

    void unbindMvpProxy();
}
