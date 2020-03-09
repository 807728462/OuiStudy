package com.oyf.codecollection.company.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.oyf.basemodule.utils.AdapterTextSizeUtil;
import com.oyf.basemodule.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

public class CircleChartView extends View {
    public CircleChartView(Context context) {
        this(context, null);
    }

    public CircleChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Context context;
    private float mWidth;
    private float mHeight;
    private int wide = 50;
    private int emptyColor = Color.parseColor("#FCB609");
    private String emptyText = "暂无数据";
    private int totleCount = 30;
    private int currPrencet = 0;
    Paint textPaint;
    Paint circlePaint;
    private int leftPadding = 20;


    private String title = "打卡分布图";
    private int titleColor = Color.parseColor("#333333");
    private int titleSize = 15;//sp
    private float titleHeight = 0;//sp


    private List<Data> mLists = new ArrayList<>();

    private void init(Context context) {
        this.context = context;
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(10);//设置4个角的弧度
        drawable.setColor(Color.WHITE);// 设置颜色
        setBackground(drawable);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        mWidth = w;
        mHeight = 204 * mWidth / 345;
        titleHeight = 44 * mWidth / 345;
        setMeasuredDimension(MeasureSpec.makeMeasureSpec((int) mWidth, wMode),
                MeasureSpec.makeMeasureSpec((int) mHeight, hMode));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        currPrencet = 0;
        drawTitle(canvas);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        if (mLists.size() == 0) {
            //无数据
            textPaint.setColor(emptyColor);
            textPaint.setTextSize(SizeUtils.sp2px(context, 14));
            textPaint.setStyle(Paint.Style.STROKE);
            textPaint.setTypeface(Typeface.DEFAULT);
            Rect rect = new Rect();
            textPaint.getTextBounds(emptyText, 0, emptyText.length(), rect);

            canvas.drawText(emptyText, mWidth / 2 - rect.width() / 2,
                    (mHeight - 44 * mWidth / 345) / 2 + rect.height() / 2, textPaint);
        } else {
            canvas.drawLine(mWidth / 2, 0, mWidth / 2, mHeight, textPaint);
            for (int i = 0; i < mLists.size(); i++) {
                Data data = mLists.get(i);
                circlePaint.setColor(data.color);
                circlePaint.setStrokeWidth(data.widePercen * wide);
                circlePaint.setStyle(Paint.Style.STROKE);

                RectF rectf_head = new RectF(mWidth / 12 + wide / 2, (mHeight - titleHeight) / 6 + wide / 2,
                        mWidth * 5 / 12 - wide / 2, (mHeight - titleHeight) * 5 / 6 - wide / 2);//确定外切矩形范围
                if (i == mLists.size() - 1) {
                    canvas.drawArc(rectf_head, currPrencet, 360 - currPrencet + 1, false, circlePaint);//绘制圆弧，不含圆心
                } else {
                    canvas.drawArc(rectf_head, currPrencet, data.count * 360 / totleCount + 1, false, circlePaint);//绘制圆弧，不含圆心
                    currPrencet += (data.count * 360 / totleCount);
                }

                textPaint.setColor(titleColor);
                textPaint.setTextSize(SizeUtils.sp2px(context, 11));
                textPaint.setTypeface(Typeface.DEFAULT);
                Rect rect = new Rect();
                textPaint.getTextBounds(data.time, 0, data.time.length(), rect);

                float cicleHeight = mHeight - titleHeight;
                Rect rMin = null;
                if (mLists.size() == 1) {
                    rMin = new Rect((int) (mWidth / 2 + leftPadding),
                            (int) (cicleHeight / 2 - rect.height() / 2),
                            (int) (mWidth / 2 + leftPadding + rect.height()),
                            (int) (cicleHeight / 2 + rect.height() / 2));


                    canvas.drawText(data.time, mWidth / 2 + leftPadding + rect.height() + leftPadding,
                            (cicleHeight / 2 + rect.height() / 2), textPaint);
                } else if (mLists.size() == 2) {
                    //int one = (int) (cicleHeight / 6);
                    if (i == 0) {
                        rMin = new Rect((int) (mWidth / 2 + leftPadding),
                                (int) (cicleHeight / 3 - rect.height() / 2),
                                (int) (mWidth / 2 + 20 + rect.height()),
                                (int) (cicleHeight / 3 + rect.height() / 2));

                        canvas.drawText(data.time, mWidth / 2 + leftPadding + rect.height() + leftPadding,
                                (cicleHeight / 3 + rect.height() / 2), textPaint);
                    } else if (i == 1) {
                        rMin = new Rect((int) (mWidth / 2 + leftPadding),
                                (int) (cicleHeight * 2 / 3 - rect.height() / 2),
                                (int) (mWidth / 2 + 20 + rect.height()),
                                (int) (cicleHeight * 2 / 3 + rect.height() / 2));

                        canvas.drawText(data.time, mWidth / 2 + leftPadding + rect.height() + leftPadding,
                                (cicleHeight * 2 / 3 + rect.height() / 2), textPaint);
                    }
                } else {
                    int one = (int) ((cicleHeight * 2 / 3 - rect.height()) / (mLists.size() - 1));
                    if (i == 0) {
                        rMin = new Rect((int) (mWidth / 2 + leftPadding),
                                (int) (cicleHeight / 6),
                                (int) (mWidth / 2 + 20 + rect.height()),
                                (int) (cicleHeight / 6 + rect.height()));
                        canvas.drawText(data.time, mWidth / 2 + leftPadding + rect.height() + leftPadding,
                                (cicleHeight / 6 + rect.height()), textPaint);
                    } else {
                        rMin = new Rect((int) (mWidth / 2 + leftPadding),
                                (int) (cicleHeight / 6 + rect.height() + one * i - rect.height()),
                                (int) (mWidth / 2 + 20 + rect.height()),
                                (int) (cicleHeight / 6 + rect.height() + one * i));
                        canvas.drawText(data.time, mWidth / 2 + leftPadding + rect.height() + leftPadding,
                                (cicleHeight / 6 + rect.height() + one * i), textPaint);
                    }
                }

                circlePaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(rMin, circlePaint);

              /*  else if (i == mLists.size() - 1) {
                    rMin = new Rect((int) (mWidth / 2 + leftPadding),
                            (int) (cicleHeight * 5 / 6 - rect.height()),
                            (int) (mWidth / 2 + 20 + rect.height()),
                            (int) (cicleHeight * 5 / 6));
                    canvas.drawText(data.time, mWidth / 2 + leftPadding + rect.height() + leftPadding,
                            (cicleHeight * 5 / 6), textPaint);
                } else*/
            }
        }
    }

    private void drawTitle(Canvas canvas) {
        //画标题跟横线
        textPaint.setColor(titleColor);
        textPaint.setTextSize(SizeUtils.sp2px(context, titleSize));
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(title, 13 * mWidth / 345, 30 * mWidth / 345, textPaint);

        canvas.translate(0, titleHeight);
        circlePaint.setColor(Color.parseColor("#E7E7E7"));
        canvas.drawLine(0, 0, mWidth, 0, circlePaint);
    }

    public void setData(List<Data> list) {
        if (null == list || list.size() == 0) {
            return;
        }
        totleCount = 0;
        for (Data data : list) {
            totleCount += data.count;
        }
        mLists.clear();
        this.mLists = list;
        invalidate();
    }


    public static class Data {

        int count;
        int color;
        String time;
        int widePercen = 1;

        public Data() {

        }

        public Data(int count,
                    int color,
                    String time) {
            this(count, color, time, 1);
        }

        public Data(int count,
                    int color,
                    String time,
                    int widePercen) {
            this.count = count;
            this.color = color;
            this.time = time;
            this.widePercen = widePercen;
        }
    }
}
