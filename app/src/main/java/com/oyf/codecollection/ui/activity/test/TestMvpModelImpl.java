package com.oyf.codecollection.ui.activity.test;

import com.oyf.basemodule.mvp.BaseModel;

/**
 * @创建者 oyf
 * @创建时间 2020/6/1 16:35
 * @描述
 **/
public class TestMvpModelImpl extends BaseModel implements TestMvpContract.TestMvpModel {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
