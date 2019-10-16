package com.oyf.basemodule.utils.mvp;


import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * presenter基类接口，V与module通信桥梁
 */
public abstract class BasePresenter<M extends IBaseModule,V extends IBaseView> implements IBasePresenter<M,V> {
    protected M module;
    protected V mView;
    private WeakReference<V> weakReference;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 隐藏加载框
     */
    @Override
    public void dismissLoading() {
        if (mView != null) mView.dismissLoading();
    }

    /**
     * 销毁View
     */
    @Override
    public void dropView() {
        if (isAttached()) {
            this.module = null;
            weakReference.clear();
            weakReference = null;
        }
    }

    /**
     * 是否已经绑定View
     * @return 绑定true，否则false
     */
    @Override
    public boolean isAttached() {
        return weakReference != null && weakReference.get() != null;
    }

    /**
     * 显示加载框
     */
    @Override
    public void showLoading() {
        if (mView != null) mView.showLoading();
    }

    /**
     * 注册（绑定）View
     * @param view view {{@link IBaseView}
     */
    @Override
    public void takeView(V view) {
        weakReference = new WeakReference<>(view);
        module = createModule();
        mView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(),
                view.getClass().getInterfaces(), new MVPViewHandler(weakReference.get()));

    }

    /**
     * View动态代理 防止 页面关闭P异步操作调用V 方法 空指针问题
     */
    class MVPViewHandler implements InvocationHandler {
        private IBaseView mvpView;

        public MVPViewHandler(IBaseView view) {
            mvpView = view;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //如果V层没被销毁, 执行V层的方法.
            if (isAttached()) {
                return method.invoke(mvpView, args);
            }//无需else因为P层无需关注V的返回值
            return null;
        }
    }
}
