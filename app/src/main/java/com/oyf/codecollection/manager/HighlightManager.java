package com.oyf.codecollection.manager;

import android.content.Context;

import zhy.com.highlight.HighLight;


/**
 * @创建者 oyf
 * @创建时间 2020/3/9 10:28
 * @描述
 **/
public class HighlightManager {
    private static HighlightManager instance;

    private HighlightManager() {
    }

    public static HighlightManager getInstance() {
        if (null == instance) {
            instance = new HighlightManager();
        }
        return instance;
    }

    public HighLight creat(Context context) {
        HighLight highLight = new HighLight(context);
        highLight.autoRemove(false)//设置背景点击高亮布局自动移除为false 默认为true
                .intercept(true)//拦截属性默认为true 使下方callback生效/设置拦截属性为false 高亮布局不影响后面布局的滑动效果
                .enableNext();//开启next模式并通过show方法显示 然后通过调用next()方法切换到下一个提示布局，直到移除自身
          /* .setClickCallback(new HighLightInterface.OnClickCallback() {
                            @Override
                            public void onClick() {
                                highLight.next();
                            }
                        })
         .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
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
                });*/
        return highLight;
    }
}
