package com.oyf.skin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.oyf.skin.R;
import com.oyf.skin.SkinViewsChange;
import com.oyf.skin.bean.AttrsBean;

import static com.oyf.basemodule.log.LogUtils.context;

/**
 * @创建者 oyf
 * @创建时间 2020/6/4 15:25
 * @描述
 **/
public class SkinButton extends AppCompatButton implements SkinViewsChange {
    private AttrsBean mAttrsBean;

    public SkinButton(Context context) {
        this(context, null);
    }

    public SkinButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSkin(attrs, defStyleAttr);
    }

    private void initSkin(AttributeSet attrs, int defStyleAttr) {
        mAttrsBean = new AttrsBean();

        // 临时存储下而已,只是部分属性需要更换
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.SkinButton, defStyleAttr, 0);
        mAttrsBean.saveViewResource(typedArray, R.styleable.SkinButton);
        // 性能优化 回收
        typedArray.recycle();
    }

    /**
     * 更换皮肤
     */
    @Override
    public void change() {
        //处理文字颜色
        int textColorKey = R.styleable.SkinButton[R.styleable.SkinButton_android_textColor];
        int textColorID = mAttrsBean.getViewResource(textColorKey);
        if (textColorID > 0) {
            // 使用兼容包
            int color = ContextCompat.getColor(getContext(), textColorID);
            setTextColor(color);
        }
        //处理背景色
        int backgroundKey = R.styleable.SkinButton[R.styleable.SkinButton_android_background];
        int backgroundID = mAttrsBean.getViewResource(backgroundKey);
        if (backgroundID > 0) {
            // 使用兼容包
            Drawable drawable = ContextCompat.getDrawable(getContext(), backgroundID);
            setBackground(drawable);
        }
    }
}
