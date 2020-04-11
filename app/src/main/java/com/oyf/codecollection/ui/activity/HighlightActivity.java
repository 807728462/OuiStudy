package com.oyf.codecollection.ui.activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.codecollection.MainActivity;
import com.oyf.codecollection.R;
import com.oyf.codecollection.bean.UserBean;
import com.oyf.codecollection.manager.HighlightManager;
import com.oyf.codecollection.ui.fragment.HighLightFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import zhy.com.highlight.HighLight;
import zhy.com.highlight.interfaces.HighLightInterface;
import zhy.com.highlight.position.OnBottomPosCallback;
import zhy.com.highlight.position.OnLeftPosCallback;
import zhy.com.highlight.position.OnRightPosCallback;
import zhy.com.highlight.position.OnTopPosCallback;
import zhy.com.highlight.shape.BaseLightShape;
import zhy.com.highlight.shape.CircleLightShape;
import zhy.com.highlight.shape.RectLightShape;
import zhy.com.highlight.view.HightLightView;


/**
 * @创建者 oyf
 * @创建时间 2020/3/9 9:32
 * @描述
 **/
public class HighlightActivity extends BaseActivity {
    private static final String TAG = HighlightActivity.class.getName();

    @BindView(R.id.bt_top)
    Button btTop;
    @BindView(R.id.bt_left)
    Button btLeft;
    @BindView(R.id.bt_right)
    Button btRight;
    @BindView(R.id.bt_bottom)
    Button btBottom;
    @BindView(R.id.fl)
    FrameLayout fl;

    private HighLight mHightLight;
    private HighLightFragment mHighLightFragment;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_highlight;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initFragment();
        EventBus.getDefault().register(this);
        EventBus.getDefault().removeStickyEvent(UserBean.class);

    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    private void initFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mHighLightFragment = new HighLightFragment();
        fragmentTransaction.replace(R.id.fl, mHighLightFragment);
        fragmentTransaction.commit();
    }

    public void registerStick(View view) {
        Intent intent = new Intent(HighlightActivity.this, MainActivity.class);
        //startActivity(intent);
        EventBus.getDefault().post(new UserBean());
    }

    @Subscribe()
    public void receiveStick(UserBean event) {
        Log.d(TAG, "普通事件+" + event);
    }

    @Subscribe(sticky = true)
    public void receiveStick2(UserBean event) {
        Log.d(TAG, "粘性事件---+" + event);
    }

    public void showHighlight(View view) {
        Log.d(TAG, "按钮点击");
        if (null == mHightLight) {
            mHightLight = HighlightManager.getInstance().creat(this);
            mHightLight.interceptHighlightOver(false)
                    .intercept(false)
                    .setClickCallback(new HighLightInterface.OnClickCallback() {
                        @Override
                        public void onClick() {
                            mHightLight.next();
                        }
                    })
                    // .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                    .addHighLight(btLeft, R.layout.layout_highlight_top, new OnLeftPosCallback(45), new RectLightShape())
                    .addHighLight(btRight, R.layout.layout_highlight_top, new OnRightPosCallback(5), new BaseLightShape(5, 5) {
                        @Override
                        protected void resetRectF4Shape(RectF viewPosInfoRectF, float dx, float dy) {
                            //缩小高亮控件范围
                            viewPosInfoRectF.inset(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dx, getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dy, getResources().getDisplayMetrics()));
                        }

                        @Override
                        protected void drawShape(Bitmap bitmap, HighLight.ViewPosInfo viewPosInfo) {
                            //custom your hight light shape 自定义高亮形状
                            Canvas canvas = new Canvas(bitmap);
                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            paint.setDither(true);
                            paint.setAntiAlias(true);
                            paint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
                            RectF rectF = viewPosInfo.rectF;
                            canvas.drawOval(rectF, paint);
                        }
                    })
                    .addHighLight(btTop, R.layout.layout_highlight_top, new OnTopPosCallback(), new CircleLightShape())
                    .addHighLight(btBottom, R.layout.layout_highlight_top, new OnBottomPosCallback(10), new RectLightShape())
                    .setOnRemoveCallback(new HighLightInterface.OnRemoveCallback() {//监听移除回调 intercept为true时生效
                        @Override
                        public void onRemove() {
                            Log.d(TAG, "onRemove");
                        }
                    })
                    .setOnShowCallback(new HighLightInterface.OnShowCallback() {
                        //监听显示回调 intercept为true时生效
                        @Override
                        public void onShow(HightLightView hightLightView) {
                            Log.d(TAG, "setOnShowCallback");
                        }

                    })
                    .setOnLayoutCallback(new HighLightInterface.OnLayoutCallback() {
                        @Override
                        public void onLayouted() {
                            Log.d(TAG, "setOnLayoutCallback");
                        }
                    });
            mHightLight.setOnNextCallback(new HighLightInterface.OnNextCallback() {
                @Override
                public void onNext(HightLightView hightLightView, View targetView, View tipView) {
                    Log.d(TAG, "onNext");
                }
            });
            mHightLight.show();
        } else {
            mHightLight.next();
        }
    }

}
