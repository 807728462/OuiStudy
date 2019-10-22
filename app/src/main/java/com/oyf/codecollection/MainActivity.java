package com.oyf.codecollection;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.oyf.codecollection.ui.activity.BehaviorActivity;
import com.oyf.codecollection.ui.activity.ChinaMapActivity;
import com.oyf.codecollection.ui.activity.VLayoutActivity;


public class MainActivity extends AppCompatActivity {
    @SuppressLint("ObjectAnimatorBinding")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void gotoVlayout(View view){
        startActivity(new Intent(this, VLayoutActivity.class));
    }
    public void gotoBehavior(View view){
        startActivity(new Intent(this, BehaviorActivity.class));
    }
    public void gotoMapView(View view){
        startActivity(new Intent(this, ChinaMapActivity.class));
    }

}
