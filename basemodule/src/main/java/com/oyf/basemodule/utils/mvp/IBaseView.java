package com.oyf.basemodule.utils.mvp;

import android.content.Context;

/**
 * V接口基类，用于显示控制
 */
public interface IBaseView {


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
//    /**
//     * 空数据
//     *
//     * @param tag TAG
//     */
//    void onEmpty(Object tag);
//
//    /**
//     * 错误数据
//     *
//     * @param tag      TAG
//     * @param errorMsg 错误信息
//     */
//    void onError(Object tag, String errorMsg);

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
     * @return 使用true，否则false
     */
    boolean useEventBus();

    void setLoadingText(int resId);
}
