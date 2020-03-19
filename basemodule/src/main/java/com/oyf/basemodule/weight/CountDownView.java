package com.oyf.basemodule.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


/**
 * @创建者 oyf
 * @创建时间 2019/11/11 17:01
 * @描述 倒计时控件
 **/
public class CountDownView extends LinearLayout {
    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private TextView[] mTextViews;
    private TextView mDayViews;

    private int mBackColor = Color.GRAY;
    private int mTextColor = Color.WHITE;
    private int radius = 20;
    private int textSize = 14;
    private int textDaySize = 14;
    private int paddingLeft = 10;
    private int paddingTop = 2;
    private CountDownTimer timer;
    private int countSpeed = 1000;

    @SuppressLint("WrongConstant")
    private void init(Context context) {
        setGravity(Gravity.CENTER);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setGradientType(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(mBackColor);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(2, 2, 2, 2);
        layoutParams.gravity = Gravity.CENTER;

        mDayViews = new TextView(context);
        mDayViews.setLayoutParams(layoutParams);
        mDayViews.setTextColor(mBackColor);
        mDayViews.setTextSize(textDaySize);
        mDayViews.setText("-天");
        addView(mDayViews);

        mTextViews = new TextView[3];
        for (int i = 0; i < mTextViews.length; i++) {
            TextView tv = new TextView(context);
            tv.setLayoutParams(layoutParams);
            tv.setTextColor(mTextColor);
            tv.setTextSize(textSize);
            tv.setBackground(drawable);
            tv.setText("--");
            tv.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
            addView(tv);
            mTextViews[i] = tv;

            if (i != mTextViews.length - 1) {
                TextView tvWell = new TextView(context);
                tvWell.setTextSize(Color.BLACK);
                tvWell.setText(":");
                addView(tvWell);
            }
        }
        timerStart(1000 * 60 * 60 * 5);
    }

    /**
     * 取消倒计时
     */
    public void timerCancel() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 开始倒计时
     */
    public void timerStart(long time) {
        timerCancel();
        timer = new CountDownTimer(time, countSpeed) {

            @Override
            public void onTick(long millisUntilFinished) {
                int hour;//小时
                int minute;//分钟
                int second;//秒数
                minute = (int) ((millisUntilFinished / countSpeed) / 60 % 60);
                second = (int) ((millisUntilFinished / countSpeed) % 60);
                hour = (int) ((millisUntilFinished / countSpeed) / 60 / 60 % 24);

                mTextViews[0].setText(hour < 10 ? "0" + hour : hour + "");
                mTextViews[1].setText(minute < 10 ? "0" + minute : minute + "");
                mTextViews[2].setText(second < 10 ? "0" + second : second + "");
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
    }
}
