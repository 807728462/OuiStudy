package com.oyf.codecollection.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;


import com.oyf.basemodule.utils.FastBlurUtil;
import com.oyf.basemodule.utils.PxAdapterUtil;
import com.oyf.basemodule.utils.StatusBarUtil;
import com.oyf.basemodule.utils.ViewCalculateUtil;
import com.oyf.codecollection.MainActivity;
import com.oyf.codecollection.R;
import com.oyf.codecollection.palymusic.DiscView;
import com.oyf.codecollection.palymusic.MusicDataBean;
import com.oyf.codecollection.palymusic.MusicViewFlipper;

import java.util.ArrayList;
import java.util.List;

public class PlayMusicActivity extends AppCompatActivity {

    int bg_pic_res = -1;
    ImageSwitcher imageSwitcher;
    Toolbar mToolbar;
    MusicViewFlipper mMusicViewFlipper;
    List<MusicDataBean> mMusicLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBar(this);
        setContentView(R.layout.activity_play_music);
        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        imageSwitcher = findViewById(R.id.is_main);
        mToolbar = findViewById(R.id.toolbar);
        mMusicViewFlipper = findViewById(R.id.mvf_main);
        bg_pic_res = R.drawable.bg_defalut;
        //设置toolbar
        mToolbar.setTitle("歌单");
        mToolbar.setSubtitle("歌词");
        setSupportActionBar(mToolbar);
        StatusBarUtil.setStateBar(this, mToolbar);
        StatusBarUtil.setMarqueeForToolbarTitleView(mToolbar);

        //设置背景
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(PlayMusicActivity.this);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        imageSwitcher.setImageResource(R.drawable.bg_defalut);
        Animation animationIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation animationOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        animationIn.setDuration(500);
        animationOut.setDuration(500);
        imageSwitcher.setInAnimation(animationIn);//设置淡入动画
        imageSwitcher.setOutAnimation(animationOut);//设置淡出动画

        mMusicLists = new ArrayList<>();
        MusicDataBean musicData1 = new MusicDataBean(R.raw.music1, R.drawable.poster1, "我喜欢", "梁静茹");
        MusicDataBean musicData2 = new MusicDataBean(R.raw.music2, R.drawable.poster2, "想把我唱给你听", "老狼");
        MusicDataBean musicData3 = new MusicDataBean(R.raw.music3, R.drawable.poster3, "风再起时", "张国荣");
        //添加歌曲
        mMusicLists.add(musicData1);
        mMusicLists.add(musicData2);
        mMusicLists.add(musicData3);

        for (int i = 0; i < 2; i++) {
            View inflate = getLayoutInflater().inflate(R.layout.layout_disc, null);
            ImageView disc = inflate.findViewById(R.id.iv_disc);
            disc.setImageBitmap(getDiscDrawable());
            ImageView poster = inflate.findViewById(R.id.iv_poster);
            poster.setImageDrawable(getDiscPosterDrawable(mMusicLists.get(i).getMusicPicRes()));
            ViewCalculateUtil.setViewRelativeLayoutParam(disc, DiscView.DISC_BG_WIDTH, DiscView.DISC_BG_WIDTH, 0, 0, 0, 0, true);
            ViewCalculateUtil.setViewRelativeLayoutParam(poster, DiscView.DISC_POSTER_WIDTH, DiscView.DISC_POSTER_WIDTH, 0, 0, 0, 0, true);
            mMusicViewFlipper.addView(inflate);
        }
        mMusicViewFlipper.setCurrentItem(0);

    }

    /**
     * 得到唱盘图片
     */
    private Bitmap getDiscDrawable() {
        int discSize = PxAdapterUtil.getInstance().getWidth(DiscView.DISC_BLACK_WIDTH);
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R
                .drawable.ic_disc), discSize, discSize, false);
        return bitmapDisc;
    }

    //获取封面图，切圆角
    private Drawable getDiscPosterDrawable(int musicPicRes) {
        int musicPicSize = PxAdapterUtil.getInstance().getWidth(DiscView.DISC_POSTER_WIDTH);
        Bitmap bitmapMusicPic = getMusicPicBitmap(musicPicSize, musicPicRes);
        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create
                (getResources(), bitmapMusicPic);
        roundMusicDrawable.setCornerRadius(musicPicSize / 2);
        roundMusicDrawable.setAntiAlias(true);
        return roundMusicDrawable;
    }

    private Bitmap getMusicPicBitmap(int musicPicSize, int musicPicRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        int imageWidth = options.outWidth;

        int sample = imageWidth / musicPicSize;
        int dstSample = 1;
        if (sample > dstSample) {
            dstSample = sample;
        }
        options.inJustDecodeBounds = false;
        //设置图片采样率
        options.inSampleSize = dstSample;
        //设置图片解码格式
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                musicPicRes, options), musicPicSize, musicPicSize, true);
    }

    private void try2UpdateMusicPicBackground(final int musicPicRes) {
        if (isNeed2UpdateBackground(musicPicRes)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Drawable foregroundDrawable = getForegroundDrawable(musicPicRes);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageSwitcher.setImageDrawable(foregroundDrawable);
                        }
                    });
                }
            }).start();
        }
    }

    public boolean isNeed2UpdateBackground(int musicPicRes) {
        if (this.bg_pic_res == -1) return true;
        if (musicPicRes != this.bg_pic_res) {
            return true;
        }
        return false;
    }

    private Drawable getForegroundDrawable(int musicPicRes) {
        /*得到屏幕的宽高比，以便按比例切割图片一部分*/
        int screenWidth = PxAdapterUtil.getInstance().getScreenWidth();
        int screenHeight = PxAdapterUtil.getInstance().getScreenHeight();
        final float widthHeightSize = (float) ((float) screenWidth
                * 1.0 / screenHeight * 1.0);

        Bitmap bitmap = getForegroundBitmap(musicPicRes);
        int cropBitmapWidth = (int) (widthHeightSize * bitmap.getHeight());
        int cropBitmapWidthX = (int) ((bitmap.getWidth() - cropBitmapWidth) / 2.0);

        /*切割部分图片*/
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, cropBitmapWidthX, 0, cropBitmapWidth,
                bitmap.getHeight());
        /*缩小图片*/
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, bitmap.getWidth() / 50, bitmap
                .getHeight() / 50, false);
        /*模糊化*/
        final Bitmap blurBitmap = FastBlurUtil.doBlur(scaleBitmap, 8, true);

        final Drawable foregroundDrawable = new BitmapDrawable(blurBitmap);
        /*加入灰色遮罩层，避免图片过亮影响其他控件*/
        foregroundDrawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        return foregroundDrawable;
    }

    private Bitmap getForegroundBitmap(int musicPicRes) {
        int screenWidth = PxAdapterUtil.getInstance().getScreenWidth();
        int screenHeight = PxAdapterUtil.getInstance().getScreenHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        if (imageWidth < screenWidth && imageHeight < screenHeight) {
            return BitmapFactory.decodeResource(getResources(), musicPicRes);
        }

        int sample = 2;
        int sampleX = imageWidth / screenWidth;
        int sampleY = imageHeight / screenHeight;

        if (sampleX > sampleY && sampleY > 1) {
            sample = sampleX;
        } else if (sampleY > sampleX && sampleX > 1) {
            sample = sampleY;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        return BitmapFactory.decodeResource(getResources(), musicPicRes, options);
    }

}
