package com.oyf.codecollection.company.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


public class CountDataView extends LinearLayout {
    public CountDataView(Context context) {
        this(context, null);
    }

    public CountDataView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private TextView tvNum;
    private TextView tvTips;
    private String mTips = "---";
    private String mNum = "0";

    private float mTipsSize = 12;
    private float mNumSize = 20;

    private float radius = 20;
    private GradientDrawable drawable;
    private int[] bgColors = {0xFFFF9326, 0xFFFFC54E};

    @SuppressLint("WrongConstant")
    private void init(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, bgColors);
        drawable.setShape(GradientDrawable.RECTANGLE);
        //drawable.setGradientType(GradientDrawable.RECTANGLE);
        //todo 转角度
        drawable.setCornerRadius(radius);
        setBackground(drawable);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        param.gravity = Gravity.CENTER;

        tvNum = new TextView(context);
        tvNum.setText(mNum);
        tvNum.setTextSize(mNumSize);
        tvNum.setTextColor(Color.WHITE);
        tvNum.setLayoutParams(param);


        tvTips = new TextView(context);
        tvTips.setText(mTips);
        tvTips.setTextSize(mTipsSize);
        tvTips.setTextColor(Color.WHITE);
        tvTips.setLayoutParams(param);
        addView(tvNum);
        addView(tvTips);
    }

    public void setNum(String num) {
        if (null != tvNum) {
            mNum = num;
            tvNum.setText(mNum);
        }
    }

    public void setTips(String tips) {
        if (null != tvTips) {
            mTips = tips;
            tvTips.setText(mTips);
        }
    }

    @SuppressLint("WrongConstant")
    public void setBg(int... colors) {
        if (colors == null) {
            return;
        }
        int c[] = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            c[i] = colors[i];
        }
        drawable.setColors(c);
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setGradientType(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        setBackground(drawable);
    }
}
