package com.oyf.codecollection.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.basemodule.utils.SizeUtils;
import com.oyf.codecollection.MainActivity;
import com.oyf.codecollection.R;
import com.oyf.codecollection.company.dialog.CalendarPopWindow;
import com.oyf.codecollection.company.view.CircleChartView;
import com.oyf.codecollection.company.view.HistoryDataView;
import com.oyf.codecollection.company.view.LineChartView;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.position.OnBottomPosCallback;
import zhy.com.highlight.position.OnLeftPosCallback;
import zhy.com.highlight.position.OnRightPosCallback;
import zhy.com.highlight.position.OnTopPosCallback;
import zhy.com.highlight.shape.BaseLightShape;
import zhy.com.highlight.shape.CircleLightShape;
import zhy.com.highlight.shape.OvalLightShape;
import zhy.com.highlight.shape.RectLightShape;
import zhy.com.highlight.view.HightLightView;


public class CompanyActivity extends BaseActivity {
    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_company;
    }

    Button btGuide;
    LineChartView lineChartView;
    CircleChartView circleChartView;
    HistoryDataView hdvLeft;
    HistoryDataView hdvRight;
    Spinner spinner;
    RadioGroup rg;
    Button btPop;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        btGuide = findViewById(R.id.bt_guide);
        lineChartView = findViewById(R.id.lineChartView);
        circleChartView = findViewById(R.id.circleChartView);
        hdvLeft = findViewById(R.id.hdv_left);
        hdvRight = findViewById(R.id.hdv_right);
        spinner = findViewById(R.id.spinner);
        rg = findViewById(R.id.rg);
        btPop = findViewById(R.id.bt_pop);
    }


    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initRg();
        initSpinner();
    }

    HighLight mHightLight;

    public void showGuide(View view) {
        mHightLight = new HighLight(CompanyActivity.this)//
                .autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
//                .intercept(false)//设置拦截属性为false 高亮布局不影响后面布局的滑动效果
                .intercept(true)//拦截属性默认为true 使下方ClickCallback生效
                .enableNext()//开启next模式并通过show方法显示 然后通过调用next()方法切换到下一个提示布局，直到移除自身
                .setClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        Toast.makeText(CompanyActivity.this, "clicked and remove HightLight view by yourself", Toast.LENGTH_SHORT).show();
                        mHightLight.next();
                    }
                })
                //.anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(btGuide, R.layout.item_mrcv, new OnLeftPosCallback(45), new RectLightShape(0, 0, 15, 0, 0))//矩形去除圆角
                .addHighLight(btGuide, R.layout.layout_mine_statistics, new OnRightPosCallback(5), new BaseLightShape(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0) {
                    @Override
                    protected void resetRectF4Shape(RectF viewPosInfoRectF, float dx, float dy) {
                        //缩小高亮控件范围
                        viewPosInfoRectF.inset(dx, dy);
                    }

                    @Override
                    protected void drawShape(Bitmap bitmap, HighLight.ViewPosInfo viewPosInfo) {
                        //custom your hight light shape 自定义高亮形状
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setDither(true);
                        paint.setAntiAlias(true);
                        //blurRadius必须大于0
                        if (blurRadius > 0) {
                            paint.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.SOLID));
                        }
                        RectF rectF = viewPosInfo.rectF;
                        canvas.drawOval(rectF, paint);
                    }
                })
                .addHighLight(btGuide, R.layout.item_mrcv, new OnTopPosCallback(), new CircleLightShape())
                .addHighLight(view, R.layout.layout_mine_statistics, new OnBottomPosCallback(10), new OvalLightShape(5, 5, 20))
                .setOnRemoveCallback(new HighLightInterface.OnRemoveCallback() {//监听移除回调
                    @Override
                    public void onRemove() {
                        Toast.makeText(CompanyActivity.this, "The HightLight view has been removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setOnShowCallback(new HighLightInterface.OnShowCallback() {//监听显示回调
                    @Override
                    public void onShow(HightLightView hightLightView) {
                        Toast.makeText(CompanyActivity.this, "The HightLight view has been shown", Toast.LENGTH_SHORT).show();
                    }
                }).setOnNextCallback(new HighLightInterface.OnNextCallback() {
                    @Override
                    public void onNext(HightLightView hightLightView, View targetView, View tipView) {
                        // targetView 目标按钮 tipView添加的提示布局 可以直接找到'我知道了'按钮添加监听事件等处理
                        Toast.makeText(CompanyActivity.this, "The HightLight show next TipView，targetViewID:" + (targetView == null ? null : targetView.getId()) + ",tipViewID:" + (tipView == null ? null : tipView.getId()), Toast.LENGTH_SHORT).show();
                    }
                });
        mHightLight.show();
    }

    private void initSpinner() {
        String[] t = new String[]{"2020-01", "2020-02", "2020-03", "2020-04"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout_spinner_text,
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

    CalendarPopWindow mCalendarPopWindow;
    int a = 0;

    public void showPop(View view) {
        if (mCalendarPopWindow == null) {
            mCalendarPopWindow = new CalendarPopWindow(this);
        }
        if (a % 5 == 0) {
            mCalendarPopWindow.updateData(CalendarPopWindow.CALENDAR_TYPE_QUARTER, 2);
        } else {
            mCalendarPopWindow.updateData(CalendarPopWindow.CALENDAR_TYPE_MOUTH, a % 4);
        }
        mCalendarPopWindow.setOnCalendarSelectListener(new CalendarPopWindow.OnCalendarSelectListener() {
            @Override
            public void select(int type, int quarter, int mouth) {
                Log.d("mCalendarPopWindow", "type=" + type + ",q=" + quarter + ",mouth=" + mouth);
            }
        });
        a++;
        mCalendarPopWindow.showAsDropDown(btPop);
    }
}
