package com.oyf.codecollection.ui.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.oyf.basemodule.animators.FadeInAnimator;
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
import java.util.concurrent.CopyOnWriteArrayList;

public class VLayoutActivity extends BaseActivity {
    public static final String TAG = VLayoutActivity.class.getSimpleName();

    private RefreshLayout srl;
    private RecyclerView rcv;
    private DelegateAdapter mDelegateAdapter;
    private List<DelegateAdapter.Adapter> mAdapters = new ArrayList();
    private List<ItemBean> lists = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    private String it = null;
    private DelegateAdapter.Adapter delegateAdapter;

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
        initVlayoutRecycleData();
        //initAnimationRecycleData();
    }

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
            public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
                TextView tv = holder.itemView.findViewById(R.id.tv_list);
                if (holder.getAdapterPosition() % 2 == 0) {
                    holder.itemView.findViewById(R.id.ll).setBackgroundColor(Color.RED);
                } else {
                    holder.itemView.findViewById(R.id.ll).setBackgroundColor(Color.GREEN);
                }
                tv.setText("列表" + position + "-" + lists.get(position).name);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lists.remove(holder.getAdapterPosition());
                        adapter.notifyItemRemoved(holder.getAdapterPosition());
                    }
                });
            }

            @Override
            public int getItemCount() {
                return lists.size();
            }
        };

        for (int i = 0; i < 1; i++) {
            lists.add(new ItemBean("" + i));
        }
        rcv.setItemAnimator(new FadeInAnimator());
        //rcv.setItemAnimator();
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
        it = 11111 + "";
        delegateAdapter = new DelegateAdapter.Adapter() {

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item, parent, false);
                return new BaseViewHolder(inflate);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                TextView viewById = holder.itemView.findViewById(R.id.tv_list);
                viewById.setText(it);
                it.charAt(0);
            }

            @Override
            public int getItemCount() {

                return 1;
            }

            @Override
            public LayoutHelper onCreateLayoutHelper() {
                return new LinearLayoutHelper();
            }
        };


        mDelegateAdapter.addAdapter(delegateAdapter);

        for (int i = 0; i < 10; i++) {
            lists.add(new ItemBean("" + i));
        }
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
        //mDelegateAdapter.addAdapter();
    }

    public void insert(View view) {
        if (lists.size() >= 0) {
            lists.add(0, new ItemBean("insert"));
            mAdapters.get(0).notifyDataSetChanged();
        }
    }

    public void delete(View view) {
        it = null;
        delegateAdapter.notifyDataSetChanged();
    }
}
