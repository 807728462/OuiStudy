package com.oyf.basemodule.itemdecoration;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @创建者 oyf
 * @创建时间 2019/11/22 10:13
 * @描述
 **/
public class LineItemDecoration extends RecyclerView.ItemDecoration {

    Paint mPaint;
    int color = Color.WHITE;
    int height = 10;

    public LineItemDecoration(int color, int height) {
        this.color = color;
        this.height = height;
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            int top = view.getBottom();
            int right = view.getRight();
            int left = view.getLeft();
            int bottom = top + height;
            //这里把left和right的值分别增加item_padding,和减去item_padding.
            c.drawRect(left, top, right, bottom, mPaint);
        }

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            outRect.bottom = outRect.bottom + height;
        } else {
            outRect.right = outRect.right + height;
        }
    }
}
