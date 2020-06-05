package com.oyf.skin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

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
public class SkinImageView extends AppCompatImageView implements SkinViewsChange {
    private AttrsBean mAttrsBean;

    public SkinImageView(Context context) {
        this(context, null);
    }

    public SkinImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSkin(attrs, defStyleAttr);
    }

    private void initSkin(AttributeSet attrs, int defStyleAttr) {
        mAttrsBean = new AttrsBean();

        // 临时存储下而已,只是部分属性需要更换
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.SkinImageView, defStyleAttr, 0);
        mAttrsBean.saveViewResource(typedArray, R.styleable.SkinImageView);
        // 性能优化 回收
        typedArray.recycle();
    }

    /**
     * 更换皮肤
     */
    @Override
    public void change() {
        int key = R.styleable.SkinImageView[R.styleable.SkinImageView_android_src];
        int srcID = mAttrsBean.getViewResource(key);
        if (srcID > 0) {
            // 使用兼容包
            Drawable drawable = ContextCompat.getDrawable(getContext(), srcID);
            setImageDrawable(drawable);
        }
    }
}
