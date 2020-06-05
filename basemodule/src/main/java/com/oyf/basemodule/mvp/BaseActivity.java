package com.oyf.basemodule.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.oyf.basemodule.mvp.proxy.ActivityMvpProxyImpl;
import com.oyf.basemodule.mvp.proxy.IMvpProxy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;


public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IView {
    private P mPresenter;
    private IMvpProxy mIMvpProxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ButterKnife.bind(this);
        initView(savedInstanceState);
        initData(savedInstanceState);
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.takeView(this);
        }
        createMvpProxy();
        mIMvpProxy.bindMvpProxy();
    }

    @Subscribe
    public void onEvent(Object o) {
        Log.d("test", "BaseActivity.onEvent");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter.dropView();
        }
        if (null != mIMvpProxy) {
            mIMvpProxy.unbindMvpProxy();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 创建代理的，通过代理去处理使用注解presenter
     *
     * @return
     */
    private IMvpProxy createMvpProxy() {
        if (null == mIMvpProxy) {
            mIMvpProxy = new ActivityMvpProxyImpl(this);
        }
        return mIMvpProxy;
    }

    protected abstract P createPresenter();

    /**
     * 如果返回 0, 则不会调用 setContentView
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化 View
     *
     * @param savedInstanceState
     * @return
     */
    protected void initView(@Nullable Bundle savedInstanceState) {
    }

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    protected void initData(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 上下文
     *
     * @return context
     */
    public Context getContext() {
        return this;
    }

    /**
     * 隐藏无数据页面
     */
    public void hideEmptyView() {
    }

    /**
     * 显示空页面或者出错页面
     */
    public void showEmptyOrErrorView() {
    }

    /**
     * 显示空页面或者出错页面
     */
    public void showEmptyOrErrorView(String text, int imageId) {

    }

    /**
     * 显示加载框
     */
    public void showLoading() {
    }

    /**
     * 隐藏加载框
     */
    public void dismissLoading() {
    }

    /**
     * 是否使用eventBus
     *
     * @return 使用true，否则false
     */
    public boolean useEventBus() {
        return false;
    }

    protected P getPresenter() {
        return mPresenter;
    }

    public void toast(String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
