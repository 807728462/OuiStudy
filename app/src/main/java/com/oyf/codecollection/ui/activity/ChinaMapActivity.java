package com.oyf.codecollection.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.oyf.basemodule.weight.LoadCircleView;
import com.oyf.basemodule.weight.LoadingView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_china_map);
        setContentView(new LoadingView(this));
        Handler handler = new Handler();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }
}