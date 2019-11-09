package com.oyf.codecollection.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.oyf.basemodule.weight.LoadCircleView;
import com.oyf.basemodule.weight.LoadingView;
import com.oyf.basemodule.weight.RippleView;
import com.oyf.basemodule.weight.ShipView;
import com.oyf.basemodule.weight.WaveView;
import com.oyf.codecollection.utils.TouchView;
import com.oyf.codecollection.R;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.OnClick;

public class ChinaMapActivity extends AppCompatActivity {

    TouchView touchView;
    boolean isNeedMove = false;
    ConstraintLayout.LayoutParams layoutParams;

    float x;
    float y;
    float leftMargin;
    float topMargin;

    RippleView rippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_china_map);
        setContentView(new ShipView(this));
        rippleView = findViewById(R.id.rv_main);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    public void start(View view) {
        if (rippleView.isRunning()) {
            rippleView.stop();
        } else {
            rippleView.start();
        }
    }
}