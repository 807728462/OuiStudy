package com.oyf.basemodule.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @创建者 oyf
 * @创建时间 2019/11/22 14:52
 * @描述
 **/
public class TimeItemDecoration extends RecyclerView.ItemDecoration {
    Paint mPaint;
    int current = 0;
    int color = Color.GRAY;
    int radius = 15;
    int stroke = 6;

    public TimeItemDecoration() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(stroke);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCount = parent.getChildCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);

            int left = layoutManager.getLeftDecorationWidth(childAt);
            int top = layoutManager.getTopDecorationHeight(childAt);
            int bottom = layoutManager.getBottomDecorationHeight(childAt);

            int viewTop = childAt.getTop();
            int height = childAt.getHeight();
            int viewBottom = childAt.getBottom();

            int cx = left / 2;
            int cy = viewTop + height / 2;
            int itemViewType = layoutManager.getItemViewType(childAt);
            if (itemViewType == 1) {//为头部
                mPaint.setStyle(Paint.Style.FILL);
                TextView textView = new TextView(childAt.getContext());
                textView.setText("aaaaaaaaaaa");
                textView.draw(c);
                c.drawCircle(cx, cy, radius + stroke, mPaint);
                c.drawLine(cx, cy + radius + stroke, cx, viewBottom + bottom, mPaint);
            } else {
                //为底部
                mPaint.setStyle(Paint.Style.STROKE);
                c.drawCircle(cx, cy, radius, mPaint);

                c.drawLine(cx, viewTop - top, cx, cy - radius, mPaint);
                c.drawLine(cx, cy + radius, cx, viewBottom + bottom, mPaint);
            }

        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = 10;
        outRect.bottom = 10;
        outRect.left = 150;
    }
}
