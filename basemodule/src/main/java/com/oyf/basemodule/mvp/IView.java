package com.oyf.basemodule.mvp;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

public interface IView {
    /**
     * 如果返回 0, 则不会调用 setContentView
     *
     * @return
     */
    @LayoutRes
    int getLayoutId();

    /**
     * 初始化 View
     *
     * @param savedInstanceState
     * @return
     */
    void initView(@Nullable Bundle savedInstanceState);

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    void initData(@Nullable Bundle savedInstanceState);

    boolean isRegisterEventBus();
}
