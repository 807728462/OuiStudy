package com.oyf.codecollection.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.R;


public class BehaviorActivity extends BaseActivity {

    RecyclerView rcv;

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
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(new Badapter());
    }

    public class Badapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item, parent, false);
            RecyclerView.ViewHolder viewHolder = new MyViewHolder(inflate);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            View viewById = holder.itemView.findViewById(R.id.tv_list);
            TextView tv = (TextView) viewById;
            tv.setText("列表" + position);
        }

        @Override
        public int getItemCount() {
            return 50;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
