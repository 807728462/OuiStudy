package com.oyf.codecollection.company.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oyf.codecollection.R;
import com.oyf.codecollection.ui.adapter.BaseAdapter;
import com.oyf.codecollection.ui.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarPopWindow extends PopupWindow {

    public static final int CALENDAR_TYPE_YEAR = 0;
    public static final int CALENDAR_TYPE_QUARTER = 1;
    public static final int CALENDAR_TYPE_MOUTH = 2;


    private String[] mQuarters = new String[]{"一季度", "二季度", "三季度", "四季度"};
    private String[][] mMouths = new String[][]{{"全部", "1月", "2月", "3月"}, {"全部", " 4月", "5月", "6月"},
            {"全部", "7月", "8月", "9月"}, {"全部", "10月", "11月", "12月"}};

    private Context context;
    private boolean isNeedBgHalfTrans = false;
    private RecyclerView rcv;
    private BaseAdapter mAdapter;
    private List mList;
    private int mCurrType;
    private int mCurrQuarter;
    private OnCalendarSelectListener onCalendarSelectListener;

    public CalendarPopWindow(Context context) {
        this.context = context;
        View parentView = View.inflate(context, R.layout.pop_calender_select, null);
        rcv = parentView.findViewById(R.id.rcv);
        ViewGroup.LayoutParams layoutParams = rcv.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        rcv.setLayoutParams(layoutParams);
        setContentView(parentView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setFocusable(true);//设置获取焦点
        setTouchable(true);//设置可以触摸
        setOutsideTouchable(true);//设置外边可以点击
        /*ColorDrawable dw = new ColorDrawable(0xffffff);
        setBackgroundDrawable(dw);*/
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.BottomDialogWindowAnim);
        initView(parentView);
        //是否需要屏幕半透明
        setBackgroundHalfTransition(isNeedBackgroundHalfTransition());
    }

    private void initView(View parentView) {
        mList = new ArrayList();
        mAdapter = new BaseAdapter<String>(mList, R.layout.iten_calender_pop) {

            @Override
            protected void convert(BaseViewHolder helper, String data) {
                helper.setText(R.id.tv, data);
            }
        };
        mAdapter.setOnItemClickListener(new BaseAdapter.OnRecyclerViewItemClickListener<String>() {
            @Override
            public void onItemClick(View view, int viewType, String data, int position) {
                if (onCalendarSelectListener != null) {
                    if (mCurrType == CALENDAR_TYPE_QUARTER) {
                        onCalendarSelectListener.select(mCurrType, position, 0);
                    } else if (mCurrType == CALENDAR_TYPE_MOUTH) {
                        onCalendarSelectListener.select(mCurrType, mCurrQuarter, position);
                    }
                }
                dismiss();
            }
        });
        rcv.setLayoutManager(new LinearLayoutManager(context));
        rcv.setAdapter(mAdapter);
    }

    public void updateData(int type, int quarter) {
        mCurrQuarter = quarter;
        mCurrType = type;
        if (type == CALENDAR_TYPE_QUARTER) {
            mList.clear();
            mList.addAll(Arrays.asList(mQuarters));
        } else if (type == CALENDAR_TYPE_MOUTH) {
            mList.clear();
            mList.addAll(Arrays.asList(mMouths[quarter]));
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 为了适配7.0系统以上显示问题(显示在控件的底部)
     *
     * @param anchor
     */
    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);
            int h = anchor.getResources().getDisplayMetrics().heightPixels - rect.bottom;
            //setHeight(h);
        }
        super.showAsDropDown(anchor);
        if (isNeedBgHalfTrans) {
            backgroundAlpha(0.5f);
        }
    }

    public void setOnCalendarSelectListener(OnCalendarSelectListener onCalendarSelectListener) {
        this.onCalendarSelectListener = onCalendarSelectListener;
    }

    /**
     * 是否设置背景半透明
     */
    public boolean isNeedBackgroundHalfTransition() {
        return true;
    }

    private void setBackgroundHalfTransition(boolean isNeed) {
        isNeedBgHalfTrans = isNeed;
        if (isNeedBgHalfTrans) {
            this.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                }
            });
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    private void backgroundAlpha(float bgAlpha) {
        if (null != context) {
            WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            ((Activity) context).getWindow().setAttributes(lp);
        }
    }

    public interface OnCalendarSelectListener {
        void select(int type, int quarter, int mouth);
    }
}
