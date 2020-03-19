package com.oyf.basemodule.itemdecoration;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @创建者 oyf
 * @创建时间 2019/11/22 10:47
 * @描述
 **/
public class ExpandItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    public ExpandItemDecoration() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        View childAt = parent.getChildAt(1);
        if (childAt != null) {
            Rect rect = new Rect(0, -0, 1080, 100);
            c.drawRect(rect, mPaint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = outRect.top + 100;
        }
        outRect.bottom = outRect.bottom + 10;
    }
}
