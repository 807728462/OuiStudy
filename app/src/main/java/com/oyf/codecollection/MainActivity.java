package com.oyf.codecollection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.oyf.basemodule.bus.OBus;
import com.oyf.basemodule.bus.OSubscribe;
import com.oyf.basemodule.bus.OThreadMode;
import com.oyf.codecollection.ui.activity.BehaviorActivity;
import com.oyf.codecollection.ui.activity.ChinaMapActivity;
import com.oyf.codecollection.ui.activity.MusicActivity;
import com.oyf.codecollection.ui.activity.MyRecycleViewActivity;
import com.oyf.codecollection.ui.activity.VLayoutActivity;
import com.oyf.codecollection.ui.bean.ItemBean;


public class MainActivity extends AppCompatActivity {
    Button button;

    @SuppressLint("ObjectAnimatorBinding")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //StatusBarUtil.setStatusBar(this);
        button = findViewById(R.id.bt_vlayout);
        OBus.getDefault().register(this);
        Log.d("test", "MainActivity.oncreat");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("test", "MainActivity.onNewIntent");

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

    @OSubscribe(threadMode = OThreadMode.MAIN)
    public void logMain(ItemBean itemBean) {
        Log.d("test", "logMain.thread=" + Thread.currentThread() + ",name=" + itemBean.name);
    }

    @OSubscribe(threadMode = OThreadMode.BACKGROUND)
    public void logY(ItemBean itemBean) {
        Log.d("test", "logY.thread=" + Thread.currentThread().getName() + ",name=" + itemBean.name);
    }
}
