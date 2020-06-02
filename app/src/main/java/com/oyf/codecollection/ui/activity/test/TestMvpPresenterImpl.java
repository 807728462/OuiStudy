package com.oyf.codecollection.ui.activity.test;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.basemodule.mvp.BasePresenter;

/**
 * @创建者 oyf
 * @创建时间 2020/6/1 16:28
 * @描述
 **/
public class TestMvpPresenterImpl extends BasePresenter<TestMvpContract.TestMvpModel, TestMvpContract.TestMvpView> implements TestMvpContract.TestMvpPresenter {
    @Override
    public TestMvpContract.TestMvpModel creatModel() {
        return new TestMvpModelImpl();
    }

    @Override
    public void start() {

    }

    @Override
    public void add(int a, int b) {
        LogUtils.d("TestMvpPresenterImpl.add+");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    int result = getModel().add(a, b);
                    getView().result(result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
