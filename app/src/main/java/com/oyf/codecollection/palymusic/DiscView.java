package com.oyf.codecollection.palymusic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.oyf.basemodule.utils.PxAdapterUtil;
import com.oyf.basemodule.utils.ViewCalculateUtil;
import com.oyf.codecollection.R;

public class DiscView extends RelativeLayout {

    /*手柄起始角度*/
    public static final float ROTATION_INIT_NEEDLE = -20;

    /*唱针宽高、距离等比例*/
    public static final int NEEDLE_WIDTH = 276;
    public static final int NEEDLE_HEIGHT = 382;
    public static final int NEEDLE_MARGIN_LEFT = 495;
    public static final int NEEDLE_PIVOT_X = 56;
    public static final int NEEDLE_PIVOT_Y = 93;
    public static final int NEEDLE_MARGIN_TOP = 32;

    /*唱盘比例*/
    public static final int DISC_BG_WIDTH = 810;
    public static final int DISC_BLACK_WIDTH = 804;
    public static final int DISC_POSTER_WIDTH = 534;
    public static final int DISC_MARGIN_TOP = 210;

    public DiscView(Context context) {
        this(context, null);
    }

    public DiscView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    MusicViewFlipper musicViewFlipper;
    ImageView needleView;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //初始化完成
        initDiscBlackGround();//白色背景初始化
        initViewFlipper();
        initNeedle();
        initObjectAnimator();
    }

    private void initNeedle() {
        needleView = findViewById(R.id.iv_needle);
        ViewCalculateUtil.setViewRelativeLayoutParam(needleView, NEEDLE_WIDTH, NEEDLE_HEIGHT, NEEDLE_MARGIN_LEFT, NEEDLE_MARGIN_TOP, 0, 0, false);
        needleView.setPivotX(PxAdapterUtil.getInstance().getWidth(NEEDLE_PIVOT_X));
        needleView.setPivotY(PxAdapterUtil.getInstance().getHeight(NEEDLE_PIVOT_Y));
        needleView.setRotation(ROTATION_INIT_NEEDLE);
    }


    private void initViewFlipper() {
        musicViewFlipper = findViewById(R.id.mvf);
        musicViewFlipper.setOverScrollMode(View.OVER_SCROLL_NEVER);//关闭渐变
        musicViewFlipper.setOnPageChangeListener(new MusicViewFlipper.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, float positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position, boolean isNext) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initDiscBlackGround() {
        ImageView background = findViewById(R.id.iv_backgroud);
        ViewCalculateUtil.setViewRelativeLayoutParam(background, DISC_BG_WIDTH, DISC_BG_WIDTH,
                0, PxAdapterUtil.getInstance().getHeight(DISC_MARGIN_TOP),
                0, 0, true);
        background.setImageDrawable(getDiscBlackGroundDrawable());
    }

    private void initView() {

    }

    private void initData() {
    }

    /*得到唱盘背后半透明的圆形背景*/
    private Drawable getDiscBlackGroundDrawable() {
        int discSize = PxAdapterUtil.getInstance().getWidth(DISC_BG_WIDTH);
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_disc_blackground), discSize, discSize, false);
        RoundedBitmapDrawable roundDiscDrawable = RoundedBitmapDrawableFactory.create
                (getResources(), bitmapDisc);
        return roundDiscDrawable;
    }

}
