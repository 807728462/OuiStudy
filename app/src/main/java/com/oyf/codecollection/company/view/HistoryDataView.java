package com.oyf.codecollection.company.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.oyf.basemodule.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public class HistoryDataView extends LinearLayout {
    public HistoryDataView(Context context) {
        this(context, null);
    }

    public HistoryDataView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Context context;
    private String[] mDatas;

    private int mTextColor = Color.parseColor("#EB8715");
    private int mTextSize = 12;
    private int mBgColor = Color.parseColor("#FFE9D1");
    private int mDataOrientation = HORIZONTAL;
    private int mGravity = Gravity.RIGHT;

    private List<TextView> mDataViews = new ArrayList<>();
    private List<TextView> mLineViews = new ArrayList<>();

    private void init(Context context) {
        this.context = context;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(SizeUtils.dip2px(context, 5));//设置4个角的弧度
        drawable.setColor(mBgColor);// 设置颜色
        setBackground(drawable);
        //addTitleData();
    }

    public void initTitleData(String[] args) {
        initTitleData(args, HORIZONTAL);
    }

    public void initTitleData(String[] args, int orientation) {

        this.mDataOrientation = orientation;
        if (null == args) {
            mDatas = new String[]{"打卡次数：", "活跃时长：",
                    "分享次数：", "订单数：",
                    "意见反馈数："
            };
        } else {
            mDatas = args;
        }
        removeAllViews();
        for (int i = 0; i < mDatas.length; i++) {
            TextView tvTips = new TextView(context);
            tvTips.setText(mDatas[i]);
            tvTips.setTextColor(mTextColor);
            tvTips.setTextSize(mTextSize);
            LinearLayout.LayoutParams tvTipsParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            tvTips.setLayoutParams(tvTipsParams);

            TextView tvValue = new TextView(context);
            tvValue.setText("-");
            tvValue.setTextColor(mTextColor);
            tvValue.setTextSize(mTextSize);
            LinearLayout.LayoutParams tvValueParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (mDataOrientation == HORIZONTAL) {
                tvValueParams.weight = 1;
            }
            tvValue.setLayoutParams(tvValueParams);
            tvValue.setGravity(mGravity);
            mDataViews.add(tvValue);

            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(mDataOrientation);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);

            linearLayout.addView(tvTips);
            linearLayout.addView(tvValue);

            LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            llParams.weight = 1;
            if (i != 0) {
                llParams.topMargin = 10;
            }
            linearLayout.setLayoutParams(llParams);
            addView(linearLayout);
            if (i != (mDatas.length - 1)) {
                TextView line = new TextView(context);
                line.setBackgroundColor(Color.parseColor("#333333"));
                line.setVisibility(GONE);
                mLineViews.add(line);
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2);
                addView(line, lineParams);
            }
        }
    }

    public void updateData(int c) {
        for (int i = 0; i < mDataViews.size(); i++) {
            mDataViews.get(i).setText(c + "-" + i);
        }
    }

    public void setLine(boolean isShow) {
        for (int i = 0; i < mLineViews.size(); i++) {
            mLineViews.get(i).setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    public void setTextGravity(int mGravity) {
        this.mGravity = mGravity;
        for (int i = 0; i < mDataViews.size(); i++) {
            mDataViews.get(i).setGravity(mGravity);
        }
    }

    public void setTextSize(int size) {
        for (int i = 0; i < mDataViews.size(); i++) {
            mDataViews.get(i).setTextSize(size);
        }
    }

    public void setTextColor(int color) {
        for (int i = 0; i < mDataViews.size(); i++) {
            mDataViews.get(i).setTextColor(color);
        }
    }

    public void setTextBlod() {
        for (int i = 0; i < mDataViews.size(); i++) {
            mDataViews.get(i).getPaint().setFakeBoldText(true);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mDataViews != null) {
            mDataViews.clear();
        }
        if (mLineViews != null) {
            mLineViews.clear();
        }
    }
}
