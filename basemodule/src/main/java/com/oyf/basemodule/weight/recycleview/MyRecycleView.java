package com.oyf.basemodule.weight.recycleview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.oyf.basemodule.R;

import java.util.ArrayList;
import java.util.List;

public class MyRecycleView extends ViewGroup {
    private final static String TAG = MyRecycleView.class.getName();

    public MyRecycleView(Context context) {
        this(context, null);
    }

    public MyRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Adapter adapter;
    private List<View> viewLists;
    //当前滑动的Y方向的距离
    private int currentY;
    //显示在屏幕的第一个是所有数据中的第几个
    private int mFristRow;
    private int rowCount;
    //滚动的距离
    private int scrollY;
    //第一屏需要
    private boolean isNeedLayout;
    private int width;
    private int height;

    //所有子view的每一个宽高
    private int[] heights;

    //最小滑动距离
    private int touchSlop;

    //回收池
    private MyRecycle recycle;

    private void init() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        viewLists = new ArrayList<>();
        this.isNeedLayout = true;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if (adapter != null) {
            recycle = new MyRecycle(adapter.getViewTypeCount());
            scrollY = 0;
            mFristRow = 0;
            isNeedLayout = true;
            requestLayout();//会调用onMeasure，onLayout
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int h = 0;
        if (adapter != null) {
            this.rowCount = adapter.getCount();
            heights = new int[rowCount];
            for (int i = 0; i < rowCount; i++) {
                heights[i] = adapter.getHeight(i);
            }
        }
        h = sumArray(heights, 0, heights.length);
        int minH = Math.min(h, height);
        setMeasuredDimension(width, minH);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int sumArray(int[] heights, int start, int count) {
        int sumHeight = 0;
        count += start;
        for (int i = start; i < count; i++) {
            sumHeight += heights[i];
        }
        return sumHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isNeedLayout || changed) {
            isNeedLayout = false;
            //清空存放的view，移除所有的view
            viewLists.clear();
            removeAllViews();
            if (adapter != null) {
                width = r - l;
                height = b - t;
                int top = 0, left = 0, right, bottom = 0;

                for (int i = 0; i < rowCount && top < height; i++) {
                    right = width;
                    bottom = top + heights[i];
                    View view = makeAndStepView(i, 0, top, right, bottom);
                    viewLists.add(view);
                    top = bottom;
                }
            }
        }
    }

    private View makeAndStepView(int i, int left, int top, int right, int bottom) {
        View view = obtainView(i, right - left, bottom - top);
        view.layout(left, top, right, bottom);
        return view;
    }

    private View obtainView(int i, int width, int height) {
        //根据pisotion获取itemType
        int itemViewType = adapter.getItemViewType(i);
        //从回收池中取itemType类型的view
        View recycleView = recycle.get(itemViewType);
        View view = recycleView;
        if (recycleView == null) {//取不到的时候，从新取创建
            view = adapter.onCreateViewHodler(i, recycleView, this);
            if (view == null) {
                throw new RuntimeException("onCreateViewHodler  必须填充布局");
            }
        }
        view = adapter.onBinderViewHodler(i, view, this);
        view.setTag(R.id.tag_type_view, itemViewType);
        //取出来需要重新设置宽高
        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        //添加到recycleview的viewgroup中
        addView(view, 0);
        return view;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            currentY = (int) ev.getRawY();
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(currentY - (int) ev.getRawY()) > touchSlop) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            //上滑为正   下滑为负
            int y2 = (int) event.getRawY();
            int diffY = currentY - y2;
            currentY = y2;//不加响应会变慢
            //画布移动  并不影响子控件的位置
            scrollBy(0, diffY);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollY += y;
        //修正scroll， 当在顶部的时候 不能下滑   当在底部的时候不能上滑
        scrollY = scrollBound(scrollY);
        Log.d(TAG, scrollY + "");
        if (scrollY > 0) {
            //头部移除， 添加到recycle中     底部添加
            while (scrollY > heights[mFristRow]) {
                removeView(viewLists.remove(0));
                scrollY -= heights[mFristRow];
                mFristRow++;
            }
            //如果数组的长度减去滚动的距离小于屏幕的宽度，则需要添加view
            while (getFillHeight() < height) {
                int last = mFristRow + viewLists.size();
                View view = obtainView(last, width, heights[last]);
                viewLists.add(viewLists.size(), view);
            }
        } else if (scrollY < 0) {
            // 底部移除    头部添加
            while (scrollY < 0) {
                int firstAddRow = mFristRow - 1;
                View view = obtainView(firstAddRow, width, heights[firstAddRow]);
                mFristRow--;
                viewLists.add(0, view);
                scrollY += heights[mFristRow + 1];
            }
            //下滑移除
            while (sumArray(heights, mFristRow, viewLists.size()) - scrollY - heights[mFristRow + viewLists.size() - 1] >= height) {
                removeView(viewLists.remove(viewLists.size() - 1));
            }
        }
        repositionViews();
    }

    private void repositionViews() {
        int left, top, right, bottom, i;
        top = -scrollY;
        i = mFristRow;
        for (View view : viewLists) {
            bottom = top + heights[i++];
            view.layout(0, top, width, bottom);
            top = bottom;
        }
    }

    private int getFillHeight() {
        //数组的长度减去滑动的距离  去跟屏幕的宽进行比较
        //数据的高度 -scrollY
        return sumArray(heights, mFristRow, viewLists.size()) - scrollY;
    }

    private int scrollBound(int scrollY) {
        //上滑
        if (scrollY > 0) {
            scrollY = Math.min(scrollY, sumArray(heights, mFristRow, heights.length - mFristRow) - height);
        } else {
            //极限值  会取零  非极限值的情况下   socrlly
            scrollY = Math.max(scrollY, -sumArray(heights, 0, mFristRow));
        }
        return scrollY;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        int itemType = (int) view.getTag(R.id.tag_type_view);
        recycle.put(view, itemType);
    }

    public interface Adapter {
        View onCreateViewHodler(int position, View convertView, ViewGroup parent);

        View onBinderViewHodler(int position, View convertView, ViewGroup parent);

        //Item的类型
        int getItemViewType(int row);

        //Item的类型数量
        int getViewTypeCount();

        int getCount();

        public int getHeight(int index);
    }
}
