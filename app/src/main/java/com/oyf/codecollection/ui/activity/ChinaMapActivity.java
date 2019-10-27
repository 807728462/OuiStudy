package com.oyf.codecollection.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.oyf.codecollection.utils.TouchView;
import com.oyf.codecollection.R;

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
        setContentView(R.layout.activity_china_map);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }
}