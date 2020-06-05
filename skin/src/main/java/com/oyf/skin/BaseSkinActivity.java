package com.oyf.skin;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.skin.utils.ActionBarUtils;
import com.oyf.skin.utils.NavigationUtils;
import com.oyf.skin.utils.StatusBarUtils;

/**
 * @创建者 oyf
 * @创建时间 2020/6/4 15:14
 * @描述
 **/
public abstract class BaseSkinActivity<P extends BasePresenter> extends BaseActivity<P> {

    private SkinAppCompatViewInflater mSkinAppCompatViewInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 我们抢先一步，比系统还要早，我们要拿到主动权
        LayoutInflaterCompat.setFactory2(LayoutInflater.from(this), this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        if (openSkin()) {
            if (null == mSkinAppCompatViewInflater) {
                mSkinAppCompatViewInflater = new SkinAppCompatViewInflater(this);
            }
            mSkinAppCompatViewInflater.setName(name);
            mSkinAppCompatViewInflater.setAttrs(attrs);
            return mSkinAppCompatViewInflater.createViewAction();
        }
        return super.onCreateView(parent, name, context, attrs);
    }

    /**
     * 是都需要换肤
     *
     * @return
     */
    public boolean openSkin() {
        return false;
    }

    // TODO 暴漏给使用者使用 换肤操作(内置换肤 没有皮肤包 黑夜和白天)
    public void setDayNightMode(int mode) {
        getDelegate().setLocalNightMode(mode); // 兼容包 提供了 黑夜 白天模式

        final boolean isPost21 = Build.VERSION.SDK_INT >= 21;

        if (isPost21) {

            StatusBarUtils.forStatusBar(this); // 改变状态栏 颜色

            ActionBarUtils.forActionBar(this); // 标题栏 颜色

            NavigationUtils.forNavigation(this); // 虚拟按钮栏 颜色
        }
        View decorView = getWindow().getDecorView();
        changeSkinAction(decorView);
    }

    // 使用递归 + 接口回调方式 换肤
    private void changeSkinAction(View decorView) {
        if (decorView instanceof SkinViewsChange) { // 成为了ViewsChange标准，才有资格换肤
            SkinViewsChange viewsChange = (SkinViewsChange) decorView;
            viewsChange.change();
        }

        if (decorView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) decorView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                changeSkinAction(childAt);
            }
        }
    }
}
