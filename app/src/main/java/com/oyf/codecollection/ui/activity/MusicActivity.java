package com.oyf.codecollection.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.android.vlayout.VirtualLayoutAdapter;
import com.oyf.basemodule.bus.OBus;
import com.oyf.codecollection.R;
import com.oyf.codecollection.ui.bean.ItemBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MusicActivity extends AppCompatActivity {

    ListView lv;
    Toolbar toolbar;
    LinearLayout ll_hide;
    LinearLayout ll_show;
    View headView;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        initView();
        initData();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {
        lv = findViewById(R.id.lv);
        toolbar = findViewById(R.id.toolbar);
        ll_hide = findViewById(R.id.ll_hide);
        headView = getLayoutInflater().inflate(R.layout.head_music, null, true);
        ll_show = headView.findViewById(R.id.ll_show);
        toolbar.setTitle("音乐");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitle("子标题");
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1000);
        headView.setLayoutParams(layoutParams);

        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.topMargin = toolbar.getHeight() + getStateBar();
        ll_hide.setLayoutParams(layoutParams1);
        List<HashMap<String, String>> data = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {    //创建 18  个 map 数据对象 ，每个map 对象 有两个键值数据
            HashMap<String, String> map = new HashMap<>();
            map.put("key_one", "歌曲_" + i);
            data.add(map);
        }
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(
                this,
                data,
                R.layout.listview_item,
                new String[]{"key_one"},
                new int[]{R.id.tv_name});

        lv.addHeaderView(headView);
        lv.setAdapter(mSimpleAdapter);
    }

    private void initData() {
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Rect rect = new Rect();
                ll_show.getGlobalVisibleRect(rect);
                int showHeight = rect.top - toolbar.getHeight() - getStateBar();
                Log.d("test", "height=" + showHeight);
                if (showHeight < 0 && ll_hide.getVisibility() == View.GONE) {
                    Drawable background = toolbar.getBackground().mutate();
                    background.setAlpha(255);
                    toolbar.setTitle("歌曲名称");
                    ll_hide.setVisibility(View.VISIBLE);
                } else {
                    Drawable background = toolbar.getBackground().mutate();
                    background.setAlpha(0);
                    ll_hide.setVisibility(View.GONE);
                    toolbar.setTitle("音乐");
                }
            }

        });
    }

    private int getStateBar() {
        int result = 0;
        int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
