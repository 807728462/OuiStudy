package com.oyf.codecollection.ui.activity;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.oyf.basemodule.animation.FlyAnimator;
import com.oyf.basemodule.animation.MyAnimation;
import com.oyf.basemodule.animation.MyDecoration;
import com.oyf.basemodule.animation.TestAnimation;
import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.R;
import com.oyf.codecollection.ui.adapter.BaseVLayoutAdapter;
import com.oyf.codecollection.ui.adapter.BaseViewHolder;
import com.oyf.codecollection.ui.bean.ItemBean;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class VLayoutActivity extends BaseActivity {
    public static final String TAG = VLayoutActivity.class.getSimpleName();
    RefreshLayout srl;
    RecyclerView rcv;
    DelegateAdapter mDelegateAdapter;
    List<DelegateAdapter.Adapter> mAdapters = new ArrayList();
    List<ItemBean> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_vlayout;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        srl = findViewById(R.id.srl);
        rcv = findViewById(R.id.rcv);

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        srl.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                Log.d(TAG, "开始刷新");
                refreshLayout.finishRefresh(1000);
            }
        });

        srl.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                Log.d(TAG, "结束刷新");
                refreshLayout.finishLoadMore(1000);
            }
        });
        // initVlayoutRecycleData();
        initAnimationRecycleData();
    }

    RecyclerView.Adapter adapter;

    private void initAnimationRecycleData() {
        rcv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item, parent, false);
                return new RecyclerView.ViewHolder(inflate) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView tv = holder.itemView.findViewById(R.id.tv_list);
                tv.setText("列表" + position + "-" + lists.get(position).name);
            }

            @Override
            public int getItemCount() {
                return lists.size();
            }
        };
        for (int i = 0; i < 100; i++) {
            lists.add(new ItemBean("" + i));
        }
        rcv.addItemDecoration(new MyDecoration());
        rcv.setItemAnimator(new TestAnimation());
        rcv.setAdapter(adapter);
    }

    private void initVlayoutRecycleData() {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(this);
        mDelegateAdapter = new DelegateAdapter(virtualLayoutManager, false);

        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(0, 40);
        rcv.setRecycledViewPool(recycledViewPool);
        rcv.setLayoutManager(virtualLayoutManager);
        rcv.setAdapter(mDelegateAdapter);

        mAdapters.add(new BaseVLayoutAdapter<ItemBean>(R.layout.rcv_item, lists) {
            @Override
            protected void convert(BaseViewHolder holder, ItemBean item) {
                holder.setText(R.id.tv_list, "列表" + item.name);
            }

            @Override
            public LayoutHelper onCreateLayoutHelper() {
                LinearLayoutHelper linearLayoutHelper = new LinearLayoutHelper();
                return linearLayoutHelper;
            }

        });
        mDelegateAdapter.setAdapters(mAdapters);
    }

    public void insert(View view) {
        if (lists.size() > 0) {
            lists.add(1, new ItemBean("insert"));
            adapter.notifyItemInserted(1);
        }
    }

    public void delete(View view) {
        if (lists.size() > 1) {
            lists.remove(1);
            adapter.notifyItemRemoved(1);

        }


    }
}