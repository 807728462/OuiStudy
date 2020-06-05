package com.oyf.codecollection.ui.activity.test;

import android.content.res.Configuration;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;

import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.R;
import com.oyf.skin.BaseSkinActivity;

/**
 * @创建者 oyf
 * @创建时间 2020/6/4 16:36
 * @描述
 **/
public class TestSkinActivity extends BaseSkinActivity {
    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_skin;
    }

    @Override
    public boolean openSkin() {
        return true;
    }

    /**
     * 白天
     *
     * @param view
     */
    public void lightClick(View view) {
        int uiMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (uiMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;

            case Configuration.UI_MODE_NIGHT_YES:
                setDayNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

}
