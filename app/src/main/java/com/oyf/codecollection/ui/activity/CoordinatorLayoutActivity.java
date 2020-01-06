package com.oyf.codecollection.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;
import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.R;

public class CoordinatorLayoutActivity extends BaseActivity {


    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_coordinator_layout;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);

    }
}
