package com.oyf.skin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.oyf.skin.view.SkinButton;
import com.oyf.skin.view.SkinImageView;
import com.oyf.skin.view.SkinLinearLayout;
import com.oyf.skin.view.SkinRelativeLayout;
import com.oyf.skin.view.SkinTextView;

/**
 * @创建者 oyf
 * @创建时间 2020/6/4 15:23
 * @描述 主要用于适配，创建响应的需要适配的view
 **/
public class SkinAppCompatViewInflater {
    private Context mContext;

    private String name; // 控件的名字  Button TextView ..
    private AttributeSet attrs; // 控件对应的属性集合

    public SkinAppCompatViewInflater(Context context) {
        this.mContext = context;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAttrs(AttributeSet attrs) {
        this.attrs = attrs;
    }

    public View createViewAction() {
        View resultView = null;

        switch (name) {
            case "TextView":
                resultView = new SkinTextView(mContext, attrs);
                break;
            case "ImageView":
                resultView = new SkinImageView(mContext, attrs);
                break;
            case "Button":
                resultView = new SkinButton(mContext, attrs);
                break;
            case "LinearLayout":
                resultView = new SkinLinearLayout(mContext, attrs);
                break;
            case "RelativeLayout":
                resultView = new SkinRelativeLayout(mContext, attrs);
                break;
        }
        return resultView; // 如果null
    }
}
