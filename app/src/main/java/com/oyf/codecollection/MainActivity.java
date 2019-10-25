package com.oyf.codecollection;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.oyf.basemodule.utils.ViewCalculateUtil;
import com.oyf.codecollection.ui.activity.BehaviorActivity;
import com.oyf.codecollection.ui.activity.ChinaMapActivity;
import com.oyf.codecollection.ui.activity.MusicActivity;
import com.oyf.codecollection.ui.activity.MyRecycleViewActivity;
import com.oyf.codecollection.ui.activity.VLayoutActivity;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class MainActivity extends AppCompatActivity {
    Button button;

    @SuppressLint("ObjectAnimatorBinding")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //StatusBarUtil.setStatusBar(this);
        button = findViewById(R.id.bt_vlayout);
        //ViewCalculateUtil.setViewLayoutParam(ll, 180, 360, true);

    }


    public void gotoVlayout(View view) {
        startActivity(new Intent(this, VLayoutActivity.class));
    }

    public void gotoBehavior(View view) {
        startActivity(new Intent(this, BehaviorActivity.class));
    }

    public void gotoMapView(View view) {
        startActivity(new Intent(this, ChinaMapActivity.class));
    }

    public void gotoRecycleViewView(View view) {
        startActivity(new Intent(this, MyRecycleViewActivity.class));
    }

    public void gotoMusic(View view) {
        startActivity(new Intent(this, MusicActivity.class));
    }
}
