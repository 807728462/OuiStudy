package com.oyf.codecollection.ui.activity;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.oyf.basemodule.mvp.BaseActivity;
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
    List<DelegateAdapter.Adapter> mAdapters=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(this);
        mDelegateAdapter = new DelegateAdapter(virtualLayoutManager, false);

        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(0, 40);
        rcv.setRecycledViewPool(recycledViewPool);
        rcv.setLayoutManager(virtualLayoutManager);

        rcv.setAdapter(mDelegateAdapter);
        initRecycleData();
    }

    private void initRecycleData() {
        List<ItemBean> lists = new ArrayList<>();
        lists.add(new ItemBean("1"));
        lists.add(new ItemBean("2"));
        lists.add(new ItemBean("3"));
        lists.add(new ItemBean("4"));
        lists.add(new ItemBean("5"));
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
}
