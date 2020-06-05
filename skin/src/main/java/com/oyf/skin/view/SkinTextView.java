package com.oyf.skin.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.oyf.skin.R;
import com.oyf.skin.SkinViewsChange;
import com.oyf.skin.bean.AttrsBean;

// AppCompatTextView
public class SkinTextView extends AppCompatTextView implements SkinViewsChange {

    private AttrsBean attrsBean;

    public SkinTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();
        // 临时存储下而已
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.SkinTextView, defStyleAttr, 0);
        attrsBean.saveViewResource(typedArray, R.styleable.SkinTextView);
        // 性能优化 回收
        typedArray.recycle();
    }

    @Override
    public void change() {
        int key = R.styleable.SkinTextView[R.styleable.SkinTextView_android_background];
        int backgrounID = attrsBean.getViewResource(key);
        if (backgrounID > 0) {
            // 使用兼容包
            Drawable drawable = ContextCompat.getDrawable(getContext(), backgrounID);
            setBackgroundDrawable(drawable);
        }

        key = R.styleable.SkinTextView[R.styleable.SkinTextView_android_textColor];
        int textColorID = attrsBean.getViewResource(key);
        if (textColorID > 0) {
            // 使用兼容包
            ColorStateList color = ContextCompat.getColorStateList(getContext(), textColorID);
            setTextColor(color);
        }
    }
}
