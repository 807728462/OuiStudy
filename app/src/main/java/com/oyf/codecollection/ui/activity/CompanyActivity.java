package com.oyf.codecollection.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.basemodule.utils.SizeUtils;
import com.oyf.codecollection.R;
import com.oyf.codecollection.company.view.CircleChartView;
import com.oyf.codecollection.company.view.HistoryDataView;
import com.oyf.codecollection.company.view.LineChartView;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;


public class CompanyActivity extends BaseActivity {
    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_company;
    }

    LineChartView lineChartView;
    CircleChartView circleChartView;
    HistoryDataView hdvLeft;
    HistoryDataView hdvRight;
    Spinner spinner;
    RadioGroup rg;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        lineChartView = findViewById(R.id.lineChartView);
        circleChartView = findViewById(R.id.circleChartView);
        hdvLeft = findViewById(R.id.hdv_left);
        hdvRight = findViewById(R.id.hdv_right);
        spinner = findViewById(R.id.spinner);
        rg = findViewById(R.id.rg);
    }


    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initRg();
        initSpinner();
    }

    private void initSpinner() {
        String[] t = new String[]{"2020-01", "2020-02", "2020-03", "2020-04"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.layout_spinner_text,
                t);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    String[] titles = new String[]{"今天", "昨日", "上周", "近一个月"};
    GradientDrawable check;
    List<RadioButton> mRadioLists = new ArrayList<>();

    private void initRg() {
        check = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor("#D3B27F"), Color.parseColor("#C8A063")});
        check.setCornerRadius(SizeUtils.dip2px(this, 10));
        for (int i = 0; i < titles.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(titles[i]);
            rb.setTextSize(12);
            rb.setId(i);
            rb.setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
            rb.setGravity(Gravity.CENTER);
            rb.setPadding(
                    SizeUtils.dip2px(this, 6),
                    SizeUtils.dip2px(this, 2),
                    SizeUtils.dip2px(this, 6),
                    SizeUtils.dip2px(this, 2)
            );
            RadioGroup.LayoutParams rbParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rbParams.leftMargin = 10;
            rbParams.rightMargin = 10;
            rbParams.topMargin = 10;
            rbParams.bottomMargin = 10;
            rb.setLayoutParams(rbParams);
            rg.addView(rb);
            mRadioLists.add(rb);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("conpany", "-------" + checkedId);
                for (RadioButton mRadioList : mRadioLists) {
                    if (mRadioList.getId() == checkedId) {
                        hdvLeft.updateData(checkedId);
                        hdvRight.updateData(checkedId);
                        mRadioList.setBackground(check);
                        mRadioList.setTextColor(Color.WHITE);
                    } else {
                        mRadioList.setBackgroundColor(Color.WHITE);
                        mRadioList.setTextColor(Color.parseColor("#999999"));
                    }
                }
            }
        });
        rg.check(0);
    }

    public void addLineChart(View view) {
        lineChartView.setData(null);
        List<CircleChartView.Data> mlists = new ArrayList<>();
        mlists.add(new CircleChartView.Data(10, Color.YELLOW, "1:00-3:00"));
        mlists.add(new CircleChartView.Data(20, Color.GREEN, "1:00-3:00"));
        mlists.add(new CircleChartView.Data(30, Color.BLUE, "1:00-3:00"));
        mlists.add(new CircleChartView.Data(30, Color.BLUE, "1:00-3:00"));
        mlists.add(new CircleChartView.Data(30, Color.BLUE, "1:00-3:00"));
        mlists.add(new CircleChartView.Data(10, Color.RED, "1:00-3:00"));
        circleChartView.setData(mlists);

        hdvLeft.initTitleData(new String[]{"打卡次数：", "活跃时长：",
                "分享次数：", "订单数：",
                "分享次数：", "订单数：",
                "分享次数：", "订单数：",
                "分享次数：", "订单数：",
                "意见反馈数："
        });
        hdvLeft.updateData(0);
        //hdvLeft.setGravity(Gravity.RIGHT);

        hdvRight.initTitleData(new String[]{"消费金额：", "节省金额："}, LinearLayout.VERTICAL);
        hdvRight.setTextSize(18);
        hdvRight.setTextGravity(Gravity.LEFT);
        hdvRight.setTextBlod();
        hdvRight.setLine(true);
        hdvRight.updateData(0);
    }
}
