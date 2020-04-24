package com.oyf.codecollection.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.R;
import com.oyf.oglide.OGlide;

/**
 * @创建者 oyf
 * @创建时间 2020/4/24 15:38
 * @描述
 **/
public class OGlideActivity extends BaseActivity {

    public ImageView iv1;
    public ImageView iv2;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_activity_oglide;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        iv1 = findViewById(R.id.iv_1);
        iv2 = findViewById(R.id.iv_2);
    }

    public void load1(View view) {
        OGlide.with(this).load("https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg").into(iv1);
    }

    public void load2(View view) {
        OGlide.with(this).load("https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg").into(iv2);
    }
}
