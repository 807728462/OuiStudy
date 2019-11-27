package com.oyf.codecollection;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oyf.basemodule.bus.OBus;
import com.oyf.basemodule.bus.OSubscribe;
import com.oyf.basemodule.bus.OThreadMode;
import com.oyf.codecollection.ui.activity.BehaviorActivity;
import com.oyf.codecollection.ui.activity.ChinaMapActivity;
import com.oyf.codecollection.ui.activity.GalleryActivity;
import com.oyf.codecollection.ui.activity.MusicActivity;
import com.oyf.codecollection.ui.activity.MyRecycleViewActivity;
import com.oyf.codecollection.ui.activity.NinePasswordActivity;
import com.oyf.codecollection.ui.activity.PlayMusicActivity;
import com.oyf.codecollection.ui.activity.RippleActivity;
import com.oyf.codecollection.ui.activity.VLayoutActivity;
import com.oyf.codecollection.ui.activity.ViewActivity;
import com.oyf.codecollection.ui.adapter.BaseAdapter;
import com.oyf.codecollection.ui.adapter.BaseViewHolder;
import com.oyf.codecollection.ui.bean.ItemBean;
import com.oyf.codecollection.ui.bean.ListBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;


public class MainActivity extends AppCompatActivity {

    RecyclerView rcv;

    List<ListBean> mLists = new ArrayList<>();
    BaseAdapter mAdapter;

    @SuppressLint("ObjectAnimatorBinding")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //StatusBarUtil.setStatusBar(this);

        OBus.getDefault().register(this);
        initView(savedInstanceState);
        initData();
        Log.d("test", "MainActivity.oncreat");

    }

    private void initView(Bundle savedInstanceState) {
        rcv = findViewById(R.id.rcv);
    }

    private void initData() {
        rcv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BaseAdapter<ListBean>(mLists, R.layout.rcv_main_list) {
            @Override
            protected void convert(BaseViewHolder helper, ListBean data) {
                helper.setText(R.id.tv_title, helper.getAdapterPosition() + "-" + data.title)
                        .setText(R.id.tv_details, data.details);
            }
        };
        mAdapter.setOnItemClickListener(new BaseAdapter.OnRecyclerViewItemClickListener<ListBean>() {
            @Override
            public void onItemClick(View view, int viewType, ListBean data, int position) {
                startActivity(new Intent(MainActivity.this, data.clazz));
            }
        });
        rcv.setAdapter(mAdapter);
        initLists();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("test", "MainActivity.onNewIntent");
    }

    public void initLists() {
        mLists.add(new ListBean("vLayout的使用", "vlayout的基本使用方法，添加删除动画", VLayoutActivity.class));
        mLists.add(new ListBean("behavior的使用", "使用behavior，在recyccleview的滑动控制按钮的显示与隐藏", BehaviorActivity.class));
        mLists.add(new ListBean("使用svg画中国地图", "使用svg加path，svg的dom解析", ChinaMapActivity.class));
        mLists.add(new ListBean("语音识别扩散", "识别扩散波纹效果", RippleActivity.class));
        mLists.add(new ListBean("自定义RecycleView", "自己使用ViewGroup，实现RecycleView效果", MyRecycleViewActivity.class));
        mLists.add(new ListBean("网易音乐列表", "网易的音乐列表界面，滑动效果配合", MusicActivity.class));
        mLists.add(new ListBean("播放音乐界面", "音乐播放效果，滑动切歌，service播放音乐，ViewFlipper的基本使用", PlayMusicActivity.class));
        mLists.add(new ListBean("图片滚动配合", "ViewFlipper跟Gallery的配合使用", GalleryActivity.class));
        mLists.add(new ListBean("自定义View", "一些自定义view", ViewActivity.class));
        mLists.add(new ListBean("九宫格密码支付", "一些自定义view", NinePasswordActivity.class));

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
