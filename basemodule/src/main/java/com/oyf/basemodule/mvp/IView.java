package com.oyf.basemodule.mvp;

import android.content.Context;

public interface IView {

    /**
     * 隐藏加载框
     */
    void dismissLoading();

    /**
     * 上下文
     *
     * @return context
     */
    Context getContext();

    /**
     * 隐藏无数据页面
     */
    void hideEmptyView();

    /**
     * 显示空页面或者出错页面
     */
    void showEmptyOrErrorView();

    /**
     * 显示空页面或者出错页面
     */
    void showEmptyOrErrorView(String text, int imageId);

    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 是否使用eventBus
     *
     * @return 使用true，否则false
     */
    boolean useEventBus();

}
