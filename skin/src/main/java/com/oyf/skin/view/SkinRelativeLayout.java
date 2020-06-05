package com.oyf.skin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import com.oyf.skin.R;
import com.oyf.skin.SkinViewsChange;
import com.oyf.skin.bean.AttrsBean;

public class SkinRelativeLayout extends RelativeLayout implements SkinViewsChange {

    private AttrsBean attrsBean;

    public SkinRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();
        // 临时存储下而已
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.SkinRelativeLayout, defStyleAttr, 0);
        attrsBean.saveViewResource(typedArray, R.styleable.SkinRelativeLayout);
        // 性能优化 回收
        typedArray.recycle();
    }

    @Override
    public void change() {

        int key = R.styleable.SkinRelativeLayout[R.styleable.SkinRelativeLayout_android_background];
        int backgrounID = attrsBean.getViewResource(key);
        if (backgrounID > 0) {
            // 使用兼容包
            Drawable drawable = ContextCompat.getDrawable(getContext(), backgrounID);
            setBackground(drawable);
        }
    }
}
