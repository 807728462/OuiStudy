package com.oyf.codecollection.company;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.R;
import com.oyf.codecollection.ui.adapter.BaseAdapter;
import com.oyf.codecollection.ui.adapter.BaseViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class EstoreExplosiveActivity extends BaseActivity {
    private final static String TAG = "EstoreExplosiveActivity";
    @BindView(R.id.rcv_left)
    RecyclerView rcvLeft;
    @BindView(R.id.rcv_Right)
    RecyclerView rcvRight;

    private BaseAdapter<LeftData> mLeftAdapter;
    private List<LeftData> mLeftLists;

    private BaseAdapter<RightData> mRightAdapter;
    private List<RightData> mRightLists;

    private Map<Integer, List<RightData>> map;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_estore_explosive;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        map = new HashMap<>();
        mLeftLists = new ArrayList<>();
        mLeftAdapter = new BaseAdapter<LeftData>(mLeftLists, R.layout.item_explosive_title) {
            @Override
            protected void convert(BaseViewHolder helper, LeftData data) {
                LinearLayout ll = helper.getView(R.id.ll_bg);
                TextView tv_title = helper.getView(R.id.tv_title);
                TextView view = helper.getView(R.id.view);
                if (data.selected) {
                    ll.setBackgroundColor(Color.WHITE);
                    tv_title.setTextSize(15);
                    tv_title.getPaint().setFakeBoldText(true);
                    view.setVisibility(View.VISIBLE);
                } else {
                    ll.setBackgroundColor(Color.parseColor("#f6f6f6"));
                    tv_title.setTextSize(14);
                    tv_title.getPaint().setFakeBoldText(false);
                    view.setVisibility(View.INVISIBLE);
                }
                tv_title.setText(data.text);
            }
        };
        mLeftAdapter.setOnItemClickListener(new BaseAdapter.OnRecyclerViewItemClickListener<LeftData>() {
            @Override
            public void onItemClick(View view, int viewType, LeftData data, int position) {
                for (int i = 0; i < mLeftLists.size(); i++) {
                    if (i == position) {
                        mLeftLists.get(i).selected = true;
                    } else {
                        mLeftLists.get(i).selected = false;
                    }

                }
                mLeftAdapter.notifyDataSetChanged();
                notifyRightData(creatRightData(mLeftLists.get(position)));
            }
        });
        rcvLeft.setLayoutManager(new LinearLayoutManager(this));
        rcvLeft.setAdapter(mLeftAdapter);


        mRightLists = new ArrayList<>();
        mRightAdapter = new BaseAdapter<RightData>(mRightLists, R.layout.item_explosive_right) {
            @Override
            protected void convert(BaseViewHolder helper, RightData data) {
                ImageView ivImg = helper.getView(R.id.iv_img);
                TextView tvName = helper.getView(R.id.tv_name);
                TextView tvTags = helper.getView(R.id.tv_tags);
                TextView tvMoney = helper.getView(R.id.tv_money);
                TextView tvSaleCount = helper.getView(R.id.tv_sale_count);

                ivImg.setBackgroundColor(data.color);
                tvName.setText(data.name);
                tvTags.setText(data.tags);
                tvMoney.setText(data.money + "");
                tvSaleCount.setText(data.saleCount + "");
            }
        };

        rcvRight.setLayoutManager(new LinearLayoutManager(this));
        rcvRight.setAdapter(mRightAdapter);

    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        for (int i = 0; i < 20; i++) {
            if (i == 0) {
                mLeftLists.add(new LeftData(i, "标题+" + i, true));
            } else {
                mLeftLists.add(new LeftData(i, "标题+" + i, false));
            }
        }
        mLeftAdapter.notifyDataSetChanged();

        List<RightData> rightData = creatRightData(mLeftLists.get(0));
        notifyRightData(rightData);
    }

    public void notifyRightData(List<RightData> rightData) {
        mRightLists.clear();
        mRightLists.addAll(rightData);
        mRightAdapter.notifyDataSetChanged();
    }

    public List<RightData> creatRightData(LeftData leftData) {
        List<RightData> rightData = map.get(leftData.type);
        if (rightData == null) {
            Log.d(TAG, "创建新的type=" + leftData.type);
            rightData = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                rightData.add(new RightData(
                        "type=" + leftData.type + "-商品+" + i,
                        "tag" + i,
                        10.22 + i,
                        100 + i,
                        i % 2 == 0 ? Color.BLUE : Color.GREEN
                ));
            }
        }
        map.put(leftData.type,rightData);
        return rightData;
    }

    public static class LeftData implements Serializable {
        String text;
        boolean selected;
        int type;

        public LeftData() {
        }

        public LeftData(int type, String text, boolean selected) {
            this.type = type;
            this.text = text;
            this.selected = selected;
        }
    }

    public static class RightData {
        String name;
        String tags;
        double money;
        int saleCount;
        int color;

        public RightData() {
        }

        public RightData(String name, String tags, double money, int saleCount, int color) {
            this.name = name;
            this.tags = tags;
            this.money = money;
            this.saleCount = saleCount;
            this.color = color;
        }
    }
}
