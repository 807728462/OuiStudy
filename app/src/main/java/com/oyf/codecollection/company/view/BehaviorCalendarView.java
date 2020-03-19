package com.oyf.codecollection.company.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.oyf.basemodule.utils.SizeUtils;

import java.util.Calendar;
import java.util.Date;

public class BehaviorCalendarView extends View {
    public final static int TYPE_CLOCK_IN = 0;
    public final static int TYPE_SALE = 1;

    public BehaviorCalendarView(Context context) {
        this(context, null);
    }

    public BehaviorCalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BehaviorCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private Context context;
    private int mType = TYPE_SALE;
    private Paint mTextPaint;
    private Paint mBgPaint;

    private String[] titles = new String[]{"日", "一", "二", "三", "四", "五", "六"};

    private int mTitleColor = Color.parseColor("#333333");
    private int mDayColor = Color.parseColor("#333333");
    private int mOtherDayColor = Color.parseColor("#CCCCCC");
    private int mLineColor = Color.parseColor("#CACACA");
    private int mDayDataSaleColor = Color.parseColor("#EB8715");
    private int mDayDataClockColor = Color.WHITE;

    private int mDayDataCirclrColor = Color.parseColor("#EB8715");

    private int mTitleSize = 14;
    private int mDaySize = 14;
    private int mDayDataSaleSize = 12;
    private int mDayClockDataSize = 8;
    private int mLeftPadding = 20;
    private int mRightPadding = 20;
    private int mTopPadding = 0;
    private int mBottomPadding = 20;
    private int mDataPadding = 8;
    private int mLine = 5;
    private int mYear = 2020;
    private int mMouth = 2;

    private int mWidth = 0;
    private int mHeight = 0;
    private int mLineWidth = 0;
    private int mLineHeight = 0;
    private int mMaxLine = 6;
    private int mMaxColumn = 7;

    private CalenderData[] calenderDatas;

    private void init(Context context) {
        this.context = context;
        setBackgroundColor(Color.WHITE);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        updateData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        mHeight = 407 * mWidth / 375;
        mLineWidth = (mWidth - mLeftPadding - mRightPadding) / 7;
        mLineHeight = (mHeight - mTopPadding - mBottomPadding) / 6;
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mWidth, wMode),
                MeasureSpec.makeMeasureSpec(mHeight, hMode));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (calenderDatas == null) {
            return;
        }
        canvas.translate(0, mTopPadding);
        for (int i = 0; i < mMaxLine; i++) {
            if (i == 0) {
                drawTitles(canvas);
            } else {
                if (mType == TYPE_SALE) {
                    drawSale(canvas, i - 1);
                } else if (mType == TYPE_CLOCK_IN) {
                    drawClockIn(canvas, i - 1);
                }
            }
            mBgPaint.setColor(mLineColor);
            if (i != mMaxLine - 1) {
                canvas.drawLine(0, mLineHeight, mWidth, mLineHeight, mBgPaint);
                canvas.translate(0, mLineHeight);
            }
        }
        canvas.restore();
    }

    private void drawTitles(Canvas canvas) {
        mTextPaint.setColor(mTitleColor);
        mTextPaint.setTextSize(SizeUtils.sp2px(context, mTitleSize));
        for (int i = 0; i < 7; i++) {
            Rect rect = new Rect();
            mTextPaint.getTextBounds("日", 0, 1, rect);
            canvas.drawText(titles[i],
                    mLeftPadding + mLineWidth * i + mLineWidth / 2 - rect.width() / 2,
                    mTopPadding + mLineHeight / 2 + rect.height() / 2, mTextPaint);
        }
    }

    /**
     * 绘制消费
     *
     * @param canvas
     * @param lineNum
     */
    private void drawSale(Canvas canvas, int lineNum) {
        for (int i = 0; i < mMaxColumn; i++) {
            CalenderData cd = calenderDatas[lineNum * mMaxColumn + i];
            Rect rect = new Rect();
            mTextPaint.setTextSize(SizeUtils.sp2px(context, mDaySize));
            mTextPaint.getTextBounds(cd.getDay(), 0, cd.getDay().length(), rect);
            if (cd.isNowMouth) {
                mTextPaint.setColor(mDayColor);
            } else {
                mTextPaint.setColor(mOtherDayColor);
            }
            canvas.drawText(cd.getDay(),
                    mLeftPadding + mLineWidth * i + mLineWidth / 2 - rect.width() / 2,
                    mLineHeight / 4 + rect.height() / 2 + mDataPadding, mTextPaint);

            //绘制数据
            if (!TextUtils.isEmpty(cd.text)) {
                Rect rectText = new Rect();
                mTextPaint.setTextSize(SizeUtils.sp2px(context, mDayDataSaleSize));
                mTextPaint.getTextBounds(cd.text, 0, cd.text.length(), rectText);
                mTextPaint.setColor(mDayDataSaleColor);
                canvas.drawText(cd.text,
                        mLeftPadding + mLineWidth * i + mLineWidth / 2 - rectText.width() / 2,
                        mLineHeight * 3 / 4 + rectText.height() / 2 - mDataPadding, mTextPaint);
            }
        }
    }

    /**
     * 绘制打卡
     *
     * @param canvas
     * @param lineNum
     */
    private void drawClockIn(Canvas canvas, int lineNum) {
        for (int i = 0; i < mMaxColumn; i++) {
            CalenderData cd = calenderDatas[lineNum * mMaxColumn + i];

            Rect rect = new Rect();
            mTextPaint.setTextSize(SizeUtils.sp2px(context, mDaySize));
            mTextPaint.getTextBounds(cd.getDay(), 0, cd.getDay().length(), rect);
            //绘制数据
            if (!TextUtils.isEmpty(cd.text)) {
                Rect rectText = new Rect();
                mTextPaint.setTextSize(SizeUtils.sp2px(context, mDayClockDataSize));
                mTextPaint.getTextBounds(cd.text, 0, cd.text.length(), rectText);
                mTextPaint.setColor(mDayDataClockColor);

                mBgPaint.setColor(mDayDataCirclrColor);
                mBgPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(mLeftPadding + mLineWidth * i + +mLineWidth / 2,
                        mLineHeight * 1 / 4 + rect.height() / 2 + mDataPadding,
                        mLineHeight * 3 / 8 - SizeUtils.dip2px(context, 5),
                        mBgPaint);

                canvas.drawText(cd.text,
                        mLeftPadding + mLineWidth * i + mLineWidth / 2 - rectText.width() / 2,
                        mLineHeight / 4 + rect.height() / 2 + mDataPadding + rectText.height() + 4, mTextPaint);
            }

            if (cd.isNowMouth) {
                if (TextUtils.isEmpty(cd.text)) {
                    mTextPaint.setColor(mDayColor);
                } else {
                    mTextPaint.setColor(mDayDataClockColor);
                }
            } else {
                mTextPaint.setColor(mOtherDayColor);
            }
            mTextPaint.setTextSize(SizeUtils.sp2px(context, mDaySize));
            canvas.drawText(cd.getDay(),
                    mLeftPadding + mLineWidth * i + mLineWidth / 2 - rect.width() / 2,
                    mLineHeight / 4 + rect.height() / 2 + mDataPadding, mTextPaint);
        }
    }

    public void updateData() {
        mType = TYPE_CLOCK_IN;
        mYear = 2020;
        mMouth = 2;
        calenderDatas = new CalenderData[mLine * 7];
        int monthDay = getMonthLastDay(mYear, mMouth);
        int preDay = getPreMonthDay(mYear, mMouth);
        int oneDay = getSupportBeginDayofMonth(mYear, mMouth);

        int curr = 0;
        for (int i = 0; i < oneDay; i++) {
            CalenderData cd = new CalenderData();
            cd.dayNum = preDay - oneDay;
            cd.text = "";
            calenderDatas[curr] = cd;
            curr++;
        }
        for (int i = 0; i < monthDay; i++) {
            CalenderData cd = new CalenderData();
            cd.dayNum = i + 1;
            if (i % 4 == 0)
                cd.text = "未打";
            cd.isNowMouth = true;
            calenderDatas[curr] = cd;
            curr++;
        }
        int lastNum = mLine * 7 - curr;
        for (int i = 0; i < lastNum; i++) {
            CalenderData cd = new CalenderData();
            cd.dayNum = i + 1;
            cd.text = "";
            calenderDatas[curr] = cd;
            curr++;
        }
    }

    public static class CalenderData {
        public int dayNum;
        public boolean isNowMouth;
        public String text;

        public String getDay() {
            return dayNum + "";
        }
    }

    /**
     * 得到指定月的天数
     */
    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 得到指定月的上一个月天数
     */
    public static int getPreMonthDay(int year, int month) {
        return getMonthLastDay(month == 1 ? year - 1 : year, month == 1 ? 12 : month - 1);
    }

    /**
     * 获取某年某月的第一天星期几
     *
     * @param year
     * @param month
     * @return
     */
    public int getSupportBeginDayofMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }
}
