package com.oyf.codecollection.ui.activity.test;

import android.view.View;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.codecollection.R;

/**
 * @创建者 oyf
 * @创建时间 2020/6/1 16:27
 * @描述
 **/
public class TestMvpActivity extends BaseActivity<TestMvpPresenterImpl> implements TestMvpContract.TestMvpView {
    @Override
    protected TestMvpPresenterImpl createPresenter() {
        return new TestMvpPresenterImpl();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_mvp;
    }

    public void addnumber(View view) {
        getPresenter().add(10, 20);
    }

    @Override
    public void result(int result) {
        LogUtils.d("结果=" + result);
    }
}
