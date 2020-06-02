package com.oyf.basemodule.mvp;


import com.oyf.basemodule.log.LogUtils;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class BasePresenter<M extends IModel, V extends IView> implements IPresenter<M, V> {

    private M mModel;
    private SoftReference<V> mViewSoftReference;
    private V mProxyView;

    @Override
    public void dismissLoading() {
    }

    @Override
    public void dropView() {
        if (mViewSoftReference.get() != null) {
            mViewSoftReference.clear();
        }
    }

    @Override
    public boolean isAttached() {
        return mViewSoftReference != null && mViewSoftReference.get() != null;
    }

    @Override
    public void showLoading() {
    }

    /**
     * 注册（绑定）View
     */
    @Override
    public void takeView(V view) {
        mViewSoftReference = new SoftReference<V>(view);
        mProxyView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(),
                view.getClass().getInterfaces(), new MVPViewHandler());
        mModel = creatModel();
    }

    public M getModel() {
        return mModel;
    }

    public V getView() {
        return mProxyView;
    }

    /**
     * View动态代理 防止 页面关闭P异步操作调用V 方法 空指针问题
     */
    class MVPViewHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            LogUtils.d("MVPViewHandler.invoke=" + method.getName());
            //如果V层没被销毁, 执行V层的方法.
            if (isAttached()) {
                return method.invoke(mViewSoftReference.get(), args);
            }//无需else因为P层无需关注V的返回值
            return null;
        }
    }
}
