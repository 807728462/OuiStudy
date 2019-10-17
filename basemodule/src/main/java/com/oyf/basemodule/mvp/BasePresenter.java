package com.oyf.basemodule.mvp;


import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class BasePresenter<M extends IModel, V extends IView> implements IPresenter<M, V> {

    protected M mModel;
    protected V mView;
    protected WeakReference weakReference;


    @Override
    public void dismissLoading() {

    }

    @Override
    public void dropView() {
        if (mView != null) {
            mView = null;
        }
    }

    @Override
    public boolean isAttached() {
        return mView == null ? true : false;
    }

    @Override
    public void showLoading() {

    }

    /**
     * 注册（绑定）View
     */
    @Override
    public void takeView(V view) {
        weakReference = new WeakReference<>(view);
        mModel = creatModel();
        mView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(),
                view.getClass().getInterfaces(), new MVPViewHandler((IView) weakReference.get()));

    }

    /**
     * View动态代理 防止 页面关闭P异步操作调用V 方法 空指针问题
     */
    class MVPViewHandler implements InvocationHandler {
        private IView mvpView;

        public MVPViewHandler(IView view) {
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
