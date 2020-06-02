package com.oyf.codecollection.ui.activity.test;

import com.oyf.basemodule.mvp.IModel;
import com.oyf.basemodule.mvp.IView;

/**
 * @创建者 oyf
 * @创建时间 2020/6/1 16:28
 * @描述
 **/
public class TestMvpContract {
    interface TestMvpView extends IView {
        void result(int result);
    }

    interface TestMvpPresenter {
        void add(int a, int b);
    }

    interface TestMvpModel extends IModel {
        int add(int a, int b);
    }
}
