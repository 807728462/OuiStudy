package com.oyf.codecollection.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.basemodule.weight.RippleView;
import com.oyf.codecollection.R;

public class RippleActivity extends BaseActivity {

    private RippleView rippleView;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ripple;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        rippleView = findViewById(R.id.rv_main);
    }

    public void start(View view) {
        if (rippleView.isRunning()) {
            rippleView.stop();
        } else {
            rippleView.start();
        }
    }
}
