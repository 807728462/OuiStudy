package com.oyf.skin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.oyf.skin.R;
import com.oyf.skin.SkinViewsChange;
import com.oyf.skin.bean.AttrsBean;

public class SkinLinearLayout extends LinearLayoutCompat implements SkinViewsChange {

    private AttrsBean attrsBean;

    public SkinLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();

        // 临时存储下而已
        TypedArray typedArray =
                context.obtainStyledAttributes(
                        attrs,
                        R.styleable.SkinLinearLayout,
                        defStyleAttr,
                        0);
        attrsBean.saveViewResource(typedArray, R.styleable.SkinLinearLayout);
        // 性能优化 回收
        typedArray.recycle();
    }

    @Override
    public void change() {
        int key = R.styleable.SkinLinearLayout[R.styleable.SkinLinearLayout_android_background];
        int backgrounID = attrsBean.getViewResource(key);
        if (backgrounID > 0) {
            // 使用兼容包
            Drawable drawable = ContextCompat.getDrawable(getContext(), backgrounID);
            setBackground(drawable);
        }

    }
}
