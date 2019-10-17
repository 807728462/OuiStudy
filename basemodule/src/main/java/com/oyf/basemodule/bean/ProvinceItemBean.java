package com.oyf.basemodule.bean;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;


public class ProvinceItemBean {
    public Path mPath;
    public int color;
    public boolean isSelect;

    public void draw(Canvas canvas, Paint mPaint) {

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        canvas.drawPath(mPath,mPaint);
        if (isSelect){
            mPaint.setColor(Color.RED);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeMiter(4);
            canvas.drawPath(mPath,mPaint);
        }
    }

    public boolean isContain(float x, float y) {
        RectF rect = new RectF();

        mPath.computeBounds(rect, true);

        Region region = new Region();
        Region region1 = new Region((int) rect.left, (int) rect.top, (int) rect.right, (int) rect.bottom);
        region.setPath(mPath, region1);
        return region.contains((int) x, (int) y);
    }
}
