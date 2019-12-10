package com.oyf.plugin;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;

/**
 * @创建者 oyf
 * @创建时间 2019/12/10 14:49
 * @描述
 **/
public class ProxyActivity extends BaseActivity {
    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_no_register;
    }
}
