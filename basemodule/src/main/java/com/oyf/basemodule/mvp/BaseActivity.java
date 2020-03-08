package com.oyf.basemodule.mvp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import butterknife.ButterKnife;

public abstract class BaseActivity<P extends BasePresenter> extends Activity implements IView {
    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ButterKnife.bind(this);
        initView(savedInstanceState);
        initData(savedInstanceState);


        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.takeView(this);
        }
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
    protected  void initData(@Nullable Bundle savedInstanceState){

    }

    /**
     * 上下文
     *
     * @return context
     */
    public Context getContext(){
        return this;
    }

    /**
     * 隐藏无数据页面
     */
    public void hideEmptyView(){}
    /**
     * 显示空页面或者出错页面
     */
    public void showEmptyOrErrorView(){}

    /**
     * 显示空页面或者出错页面
     */
    public void showEmptyOrErrorView(String text,int imageId){

    }
    /**
     * 显示加载框
     */
    public void showLoading(){}
    /**
     * 隐藏加载框
     */
    public void dismissLoading(){}

    /**
     * 是否使用eventBus
     * @return 使用true，否则false
     */
    public boolean useEventBus(){
        return false;
    }
}
