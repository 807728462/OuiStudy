package com.oyf.codecollection.ui.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.oyf.basemodule.itemdecoration.ExpandItemDecoration;
import com.oyf.basemodule.itemdecoration.LineItemDecoration;
import com.oyf.basemodule.itemdecoration.TimeItemDecoration;
import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.MainActivity;
import com.oyf.codecollection.R;


public class BehaviorActivity extends BaseActivity {

    RecyclerView rcv;
    TextView tv_top;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_behavior;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        rcv = findViewById(R.id.rcv);
        tv_top = findViewById(R.id.tv_top);
    }

    int mCurrentPosition;
    int topHeight;

    @SuppressLint("NewApi")
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        rcv.setAdapter(new Badapter());
        rcv.addItemDecoration(new LineItemDecoration(Color.WHITE, 10));
        rcv.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                topHeight = tv_top.getHeight();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    //上滑
                    int itemViewType = rcv.getAdapter().getItemViewType(mCurrentPosition + 1);
                    View childAt = linearLayoutManager.findViewByPosition(mCurrentPosition + 1);
                    if (childAt != null) {
                        Log.d("test", "type=" + itemViewType + ",top=" + childAt.getTop() + ",position=" + mCurrentPosition);
                        if (itemViewType == 1) {
                            if (childAt.getTop() <= topHeight) {
                                tv_top.setTranslationY(childAt.getTop() - childAt.getHeight());
                            } else {
                                tv_top.setTranslationY(0);
                            }
                        }
                    }

                }

                if (mCurrentPosition != linearLayoutManager.findFirstVisibleItemPosition()) {
                    mCurrentPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    tv_top.setTranslationY(0);
                    int itemViewType = rcv.getAdapter().getItemViewType(mCurrentPosition + 1);
                    if (itemViewType == 1) {
                        tv_top.setText("头部" + mCurrentPosition);
                    }
                }

            }
        });
    }

    public void onclick(View view) {
      /*  Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
    }

    public class Badapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View inflate = null;
            if (viewType == 1) {
                inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item, parent, false);
            } else {
                inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item, parent, false);
            }
            RecyclerView.ViewHolder viewHolder = new MyViewHolder(inflate);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            View viewById = holder.itemView.findViewById(R.id.tv_list);
            TextView tv = (TextView) viewById;
            if (holder.getItemViewType() == 0) {
                tv.setBackgroundColor(Color.GREEN);
                tv.setText("列表" + position);
            } else {
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("头部" + position);
                tv.setGravity(Gravity.CENTER_VERTICAL);
            }


        }

        @Override
        public int getItemViewType(int position) {
            if (position % 5 == 0) {
                return 1;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
