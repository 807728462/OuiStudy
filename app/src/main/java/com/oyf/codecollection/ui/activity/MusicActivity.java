package com.oyf.codecollection.ui.activity;

import android.annotation.SuppressLint;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.oyf.codecollection.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicActivity extends AppCompatActivity {

    private ListView lv;
    private Toolbar toolbar;
    private LinearLayout ll_hide;
    private LinearLayout ll_show;
    private View headView;

    private Rect rectShow = new Rect();
    private int maxTop = 0;

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
                if (maxTop == 0) {
                    ll_show.getGlobalVisibleRect(rectShow);
                    maxTop = rectShow.top - getStateBar();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                Rect rectHide = new Rect();
                ll_show.getGlobalVisibleRect(rectShow);
                ll_hide.getGlobalVisibleRect(rectHide);
                if (rectShow.top <= 0) {
                } else if (rectShow.top <= rectHide.top && ll_hide.getVisibility() == View.INVISIBLE) {
                    Drawable background = toolbar.getBackground().mutate();
                    background.setAlpha(255);
                    toolbar.setTitle("歌曲名称");
                    ll_hide.setVisibility(View.VISIBLE);
                } else if (rectShow.top != maxTop && rectShow.top >= rectHide.top && ll_hide.getVisibility() == View.VISIBLE) {
                    Drawable background = toolbar.getBackground().mutate();
                    background.setAlpha(0);
                    ll_hide.setVisibility(View.INVISIBLE);
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
