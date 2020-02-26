package com.oyf.codecollection.company.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.oyf.codecollection.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 折线统计图
 */
public class LineChartView extends LinearLayout {
    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private LineChart lineChart;
    private int xCount = 8;
    private int xTotalCount = 30;

    private List<Entry> mDataLists;

    private LineDataSet lineDataSet;

    private void init(Context context) {
        setOrientation(VERTICAL);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(10);//设置4个角的弧度
        drawable.setColor(Color.WHITE);// 设置颜色
        setBackground(drawable);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_mine_line_chart, null, false);
        lineChart = view.findViewById(R.id.lineChart);
        initLineChart();
        addView(view);
    }

    private void initLineChart() {
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(false);
        //是否可以拖动
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        //是否有触摸事件
        lineChart.setTouchEnabled(true);
        lineChart.setHighlightPerDragEnabled(true);
        lineChart.setPinchZoom(true);

        //设置XY轴动画效果
        lineChart.animateY(1000);
        lineChart.animateX(1000);
        // 没有数据的时候，显示“暂无数据”
        lineChart.setNoDataText("暂无数据");
        lineChart.zoom((float) xTotalCount / (float) xCount, 1f, 0, 0);

        /***XY轴的设置***/
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "日";
            }
        });
        xAxis.setTextColor(Color.parseColor("#999999"));
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.parseColor("#999999"));
        xAxis.setGridColor(Color.parseColor("#999999"));
        xAxis.setGranularityEnabled(true);
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(1f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(xCount,true);

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        //保证Y轴从0开始，不然会上移一点
       /* leftYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisMaximum(3f);
        leftYAxis.setLabelCount(3);*/
        leftYAxis.setGridColor(Color.parseColor("#999999"));
        leftYAxis.setAxisLineColor(Color.WHITE);
        leftYAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int)value + "";
            }
        });
        leftYAxis.setEnabled(true);

        YAxis rightYaxis = lineChart.getAxisRight();
        rightYaxis.setAxisMinimum(0f);
        rightYaxis.setEnabled(false);

        //设置描述
        lineChart.getDescription().setEnabled(false);

        /***折线图例 标签 设置***/
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
        //是否绘制在图表里面
        legend.setDrawInside(false);


        mDataLists = new ArrayList<>();

    }

    private void initLineDataSet(LineDataSet lineDataSet) {
        //避免在 initLineDataSet()方法中 设置了 lineDataSet.setDrawFilled(false); 而无法实现效果
        lineDataSet.setDrawFilled(true);
        //lineDataSet.setFillDrawable(drawable);
        lineDataSet.setColor(Color.parseColor("#EB8715"));
        lineDataSet.setCircleColor(Color.parseColor("#EB8715"));
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setValueTextColor(Color.parseColor("#333333"));
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(false);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return ((int) value) + "";
            }
        });
        //设置曲线展示为圆滑曲线（如果不设置则默认折线）
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);
    }

    public void setData(List list) {
        mDataLists.clear();

        Random random = new Random();
        for (int i = 0; i < xTotalCount; i++) {

            /**
             * 在此可查看 Entry构造方法，可发现 可传入数值 Entry(float x, float y)
             * 也可传入Drawable， Entry(float x, float y, Drawable icon) 可在XY轴交点 设置Drawable图像展示
             */
            Entry entry = new Entry(i, random.nextInt(3));
            mDataLists.add(entry);
        }

        if (lineDataSet == null) {
            mDataLists.get(2).setY(33);
            lineDataSet = new LineDataSet(mDataLists, "");
            initLineDataSet(lineDataSet);
            float max = 0;
            for (Entry mDataList : mDataLists) {
                if (mDataList.getY() > max) {
                    max = mDataList.getY();
                }
            }


            YAxis leftYAxis = lineChart.getAxisLeft();
            leftYAxis.setAxisMinimum(0);
            leftYAxis.setLabelCount(3,max>3&&(max%3!=0));
            leftYAxis.setAxisMaximum(((int) (max / 3) + 1) * 3);
            leftYAxis.setDrawLimitLinesBehindData(true);

            LineData lineData = new LineData(lineDataSet);
            lineChart.setData(lineData);
            lineChart.invalidate();
        }
        // 每一个LineDataSet代表一条线
        if (null != lineChart) {
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }
}
