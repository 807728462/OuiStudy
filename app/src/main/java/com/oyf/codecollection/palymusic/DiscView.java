package com.oyf.codecollection.palymusic;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.viewpager.widget.ViewPager;

import com.oyf.basemodule.utils.PxAdapterUtil;
import com.oyf.basemodule.utils.ViewCalculateUtil;
import com.oyf.codecollection.R;

import java.util.ArrayList;
import java.util.List;

public class DiscView extends RelativeLayout {
    private static final String TAG = "testDiscView";
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
        super(context);
    }

    public DiscView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /*唱针当前所处的状态*/
    private enum NeedleAnimatorStatus {
        /*移动时：从唱盘往远处移动*/
        TO_FAR_END,
        /*移动时：从远处往唱盘移动*/
        TO_NEAR_END,
        /*静止时：离开唱盘*/
        IN_FAR_END,
        /*静止时：贴近唱盘*/
        IN_NEAR_END
    }

    /*音乐当前的状态：只有播放、暂停、停止三种*/
    public enum MusicStatus {
        PLAY, PAUSE, STOP
    }

    /*DiscView需要触发的音乐切换状态：播放、暂停、上/下一首、停止*/
    public enum MusicChangedStatus {
        PLAY, PAUSE, NEXT, PREVIOUS, STOP
    }

    public interface IPlayInfo {
        /*用于更新标题栏变化*/
        void onMusicInfoChanged(String musicName, String musicAuthor);

        /*用于更新背景图片*/
        void onMusicPicChanged(int musicPicRes);

        /*用于更新音乐播放状态*/
        void onMusicChanged(MusicChangedStatus musicChangedStatus);
    }

    //轮盘的转动
    MusicViewFlipper musicViewFlipper;
    //把柄的view
    ImageView needleView;
    //上面的把柄的状态
    NeedleAnimatorStatus needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
    ObjectAnimator needleAnimator;
    //音乐的状态
    MusicStatus musicStatus = MusicStatus.STOP;
    //回调音乐状态
    IPlayInfo iPlayInfo;
    List<MusicDataBean> mMusicLists = new ArrayList<>();
    List<ObjectAnimator> mDiscAnimators = new ArrayList<>();
    /*标记ViewFlipper是否处于偏移的状态*/
    private boolean mViewFlipperIsOffset = false;

    /*标记唱针复位后，是否需要重新偏移到唱片处*/
    private boolean mIsNeed2StartPlayAnimator = false;

    public static final int DURATION_NEEDLE_ANIMATOR = 500;

    private int otherPosterRes = -1;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //初始化完成
        initDiscBlackGround();//白色背景初始化
        initViewFlipper();
        initNeedle();
        initObjectAnimator();
    }

    public void setMusicLists(List<MusicDataBean> mLists) {

        if (mLists == null) return;
        mMusicLists.clear();
        mMusicLists.addAll(mLists);
        mDiscAnimators.clear();
        musicViewFlipper.setMusicSize(mMusicLists.size());
        //获取第一首歌曲信息
        MusicDataBean musicData = mMusicLists.get(0);
        otherPosterRes = musicData.getMusicPicRes();
        for (int i = 0; i < 2; i++) {
            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc, null);
            ImageView disc = inflate.findViewById(R.id.iv_disc);
            disc.setImageBitmap(getDiscDrawable());
            ImageView poster = inflate.findViewById(R.id.iv_poster);
            poster.setImageDrawable(getDiscPosterDrawable(otherPosterRes));
            ViewCalculateUtil.setViewRelativeLayoutParam(disc, DiscView.DISC_BG_WIDTH, DiscView.DISC_BG_WIDTH, 0, 0, 0, 0, true);
            ViewCalculateUtil.setViewRelativeLayoutParam(poster, DiscView.DISC_POSTER_WIDTH, DiscView.DISC_POSTER_WIDTH, 0, 0, 0, 0, true);
            mDiscAnimators.add(getDiscObjectAnimator(inflate));
            musicViewFlipper.addView(inflate);
        }

        //接口回调通知图片更新
        if (iPlayInfo != null) {
            iPlayInfo.onMusicInfoChanged(musicData.getMusicName(), musicData.getMusicAuthor());
            iPlayInfo.onMusicPicChanged(musicData.getMusicPicRes());
        }
    }

    private void initObjectAnimator() {
        needleAnimator = ObjectAnimator.ofFloat(needleView, View.ROTATION, PxAdapterUtil.getInstance().getWidth((int) ROTATION_INIT_NEEDLE), 0);
        needleAnimator.setDuration(500);
        needleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                Log.d(TAG, "needleAnimator.onAnimationStart,needleAnimatorStatus=" + needleAnimatorStatus);
                if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.TO_NEAR_END;
                }
                if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Log.d(TAG, "needleAnimator.onAnimationEnd,needleAnimatorStatus=" + needleAnimatorStatus + ",mIsNeed2StartPlayAnimator=" + mIsNeed2StartPlayAnimator);
                if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_NEAR_END;
                    playDiscAnimator();
                    musicStatus = MusicStatus.PLAY;
                } else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
                    needleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
                    if (musicStatus == MusicStatus.STOP) {
                        mIsNeed2StartPlayAnimator = true;
                    }
                }
                if (mIsNeed2StartPlayAnimator) {
                    mIsNeed2StartPlayAnimator = false;
                    /**
                     * 只有在ViewFlipper不处于偏移状态时，才开始唱盘旋转动画
                     * */
                    if (!mViewFlipperIsOffset) {
                        /*延时500ms*/
                        DiscView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playAnimator();
                            }
                        }, 500);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.e(TAG, "把柄的动画——onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

                Log.e(TAG, "把柄的动画——onAnimationRepeat");
            }
        });
    }

    public void setPlayInfoListener(IPlayInfo listener) {
        this.iPlayInfo = listener;
    }

    /**
     * 初始化指针
     */
    private void initNeedle() {
        needleView = findViewById(R.id.iv_needle);
        ViewCalculateUtil.setViewRelativeLayoutParam(needleView, NEEDLE_WIDTH, NEEDLE_HEIGHT, NEEDLE_MARGIN_LEFT, -NEEDLE_MARGIN_TOP, 0, 0, true);
        needleView.setPivotX(PxAdapterUtil.getInstance().getWidth(NEEDLE_PIVOT_X));
        needleView.setPivotY(PxAdapterUtil.getInstance().getHeight(NEEDLE_PIVOT_Y));
        needleView.setRotation(ROTATION_INIT_NEEDLE);
    }


    /**
     * 初始化flipper，左右滑动的
     */
    private void initViewFlipper() {
        musicViewFlipper = findViewById(R.id.mvf);
        musicViewFlipper.setOverScrollMode(View.OVER_SCROLL_NEVER);//关闭渐变
        ViewCalculateUtil.setViewRelativeLayoutParam(musicViewFlipper, RelativeLayout.LayoutParams.MATCH_PARENT, DISC_BG_WIDTH, 0, DISC_MARGIN_TOP, 0, 0, false);
        musicViewFlipper.setOnPageChangeListener(new MusicViewFlipper.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, float positionOffsetPixels) {
                if (positionOffsetPixels > 0) {
                    //上一曲
                    int previous = musicViewFlipper.getPreviousItem();
                    Log.e(TAG, previous + "   mCurrentItem " + musicViewFlipper.getCurrentItem());
                    if (otherPosterRes != mMusicLists.get(previous).getMusicPicRes()) {
                        otherPosterRes = mMusicLists.get(previous).getMusicPicRes();
                        musicViewFlipper.getOtherPosterView().setImageDrawable(
                                getDiscPosterDrawable(otherPosterRes));
                    }
                    if (positionOffset < 0.5) {
                        notifyMusicInfoChanged(position);
                    } else {
                        notifyMusicInfoChanged(previous);
                    }
                } else {
                    int next = musicViewFlipper.getNextItem();
                    Log.e(TAG, next + " onPageScrolled.  mCurrentItem " + musicViewFlipper.getCurrentItem());
                    if (otherPosterRes != mMusicLists.get(next).getMusicPicRes()) {
                        otherPosterRes = mMusicLists.get(next).getMusicPicRes();
                        musicViewFlipper.getOtherPosterView().setImageDrawable(
                                getDiscPosterDrawable(otherPosterRes));
                    }
                    if (positionOffset < 0.5) {
                        notifyMusicInfoChanged(position);
                    } else {
                        notifyMusicInfoChanged(next);
                    }
                }
            }

            @Override
            public void onPageSelected(int position, boolean isNext) {
                Log.e(TAG, "onPageSelected.   position= " + position + ",isnext=" + isNext);
                resetOtherDiscAnimation();
                notifyMusicPicChanged(position);
                notifyMusicInfoChanged(position);
                if (isNext) {
                    notifyMusicStatusChanged(MusicChangedStatus.NEXT);
                } else {
                    notifyMusicStatusChanged(MusicChangedStatus.PREVIOUS);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                doWithAnimatorOnPageScroll(state);
            }
        });
    }

    private void doWithAnimatorOnPageScroll(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
            case ViewPager.SCROLL_STATE_SETTLING: {
                mViewFlipperIsOffset = false;
                if (musicStatus == MusicStatus.PLAY) {
                    playAnimator();
                }
                break;
            }
            case ViewPager.SCROLL_STATE_DRAGGING: {
                mViewFlipperIsOffset = true;
                pauseAnimator();
                break;
            }
        }
    }

    private void resetOtherDiscAnimation() {
        mDiscAnimators.get(musicViewFlipper.getOtherItem()).cancel();
        musicViewFlipper.getOtherView().setRotation(0);
    }

    /**
     * 通知activity修改歌名
     *
     * @param position
     */
    private void notifyMusicInfoChanged(int position) {
        if (iPlayInfo != null) {
            MusicDataBean musicData = mMusicLists.get(position);
            iPlayInfo.onMusicInfoChanged(musicData.getMusicName(), musicData.getMusicAuthor());
        }
    }

    /**
     * 通知activity修改背景图片
     *
     * @param position
     */
    public void notifyMusicPicChanged(int position) {
        if (iPlayInfo != null) {
            MusicDataBean musicData = mMusicLists.get(position);
            iPlayInfo.onMusicPicChanged(musicData.getMusicPicRes());
        }
    }

    /**
     * 初始化白色背景
     */
    private void initDiscBlackGround() {
        ImageView background = findViewById(R.id.iv_backgroud);
        ViewCalculateUtil.setViewRelativeLayoutParam(background, DISC_BG_WIDTH, DISC_BG_WIDTH,
                0, DISC_MARGIN_TOP,
                0, 0, true);
        background.setImageDrawable(getDiscBlackGroundDrawable());
    }

    public boolean isPlaying() {
        return musicStatus == MusicStatus.PLAY;
    }

    public void playOrPause() {
        if (musicStatus == MusicStatus.PLAY) {
            pause();
        } else {
            play();
        }
    }

    private void pause() {
        pauseAnimator();
    }

    private void pauseAnimator() {
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
            pauseDiscAnimator();
        } else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
            needleAnimator.reverse();

            /**
             * 若动画在没结束时执行reverse方法，则不会执行监听器的onStart方法，此时需要手动设置
             * */
            needleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
        }
        /**
         * 动画可能执行多次，只有音乐处于停止 / 暂停状态时，才执行暂停命令
         * */
        if (musicStatus == MusicStatus.STOP) {
            notifyMusicStatusChanged(MusicChangedStatus.STOP);
        } else if (musicStatus == MusicStatus.PAUSE) {
            notifyMusicStatusChanged(MusicChangedStatus.PAUSE);
        }
    }

    private void notifyMusicStatusChanged(MusicChangedStatus musicChangedStatus) {
        if (iPlayInfo != null) {
            iPlayInfo.onMusicChanged(musicChangedStatus);
        }
    }

    public void next() {
        //TODO 需要注意的修改项
        int next = musicViewFlipper.getNextItem();
        Log.e(TAG, "discview.next方法. next=" + next);
        if (otherPosterRes != mMusicLists.get(next).getMusicPicRes()) {
            otherPosterRes = mMusicLists.get(next).getMusicPicRes();
            musicViewFlipper.getOtherPosterView().setImageDrawable(getDiscPosterDrawable(otherPosterRes));
        }
        musicViewFlipper.showNextWithAnimation();
        selectMusicWithButton();
    }

    public void previous() {
        int previous = musicViewFlipper.getPreviousItem();
        Log.e(TAG, "discview.previous方法. previous=" + previous);
        if (otherPosterRes != mMusicLists.get(previous).getMusicPicRes()) {
            otherPosterRes = mMusicLists.get(previous).getMusicPicRes();
            musicViewFlipper.getOtherPosterView().setImageDrawable(getDiscPosterDrawable(otherPosterRes));
        }
        musicViewFlipper.showPreviousWithAnimation();
        selectMusicWithButton();
    }

    private void selectMusicWithButton() {
        if (musicStatus == MusicStatus.PLAY) {
            mIsNeed2StartPlayAnimator = true;
            pauseAnimator();
        } else if (musicStatus == MusicStatus.PAUSE) {
            play();
        }
    }

    private void pauseDiscAnimator() {
        ObjectAnimator objectAnimator = mDiscAnimators.get(musicViewFlipper.getDisplayedChild());
        objectAnimator.pause();
        needleAnimator.reverse();
    }

    private void playDiscAnimator() {
        ObjectAnimator objectAnimator = mDiscAnimators.get(musicViewFlipper.getDisplayedChild());
        if (objectAnimator.isPaused()) {
            objectAnimator.resume();
        } else {
            objectAnimator.start();
        }
        /**
         * 唱盘动画可能执行多次，只有不是音乐不在播放状态，在回调执行播放
         * */
        if (musicStatus != MusicStatus.PLAY) {
            notifyMusicStatusChanged(MusicChangedStatus.PLAY);
        }
    }

    private void play() {
        playAnimator();
    }

    private void playAnimator() {
        if (needleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
            Log.d("discveiw", "needleAnimator开始");
            needleAnimator.start();
        } else if (needleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
            /*唱针处于往远端移动时，设置标记，等动画结束后再播放动画*/
            mIsNeed2StartPlayAnimator = true;
        }
    }

    /**
     * 获取转动唱片的动画
     *
     * @param disc
     * @return
     */
    private ObjectAnimator getDiscObjectAnimator(View disc) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(disc, View.ROTATION, 0, 360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20 * 1000);
        objectAnimator.setInterpolator(new LinearInterpolator());

        return objectAnimator;
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

    /**
     * 获取poster的图片
     *
     * @param musicPicSize
     * @param musicPicRes
     * @return
     */
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

}
