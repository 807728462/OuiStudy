package com.oyf.codecollection.palymusic;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.oyf.codecollection.R;

public class MusicViewFlipper extends ViewFlipper implements View.OnTouchListener {
    public MusicViewFlipper(Context context) {
        this(context, null);
    }

    public MusicViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
    }

    public static final int SCROLL_STATE_IDLE = 0;//空闲状态
    public static final int SCROLL_STATE_DRAGGING = 1;//滑动状态
    public static final int SCROLL_STATE_SETTLING = 2;//滑动后自然沉降的状态
    private int currentItem = 0;
    private int musicSize;
    private int startX;
    private int width;
    private OnPageChangeListener mOnPageChangeListener;

    private void init() {
        setOnTouchListener(this);
        setLongClickable(true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float dx = event.getX() - startX;
        float pageOffset = Math.abs(dx) / width;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                getCurrentView().setTranslationX(dx);
                if (dx > 0) {
                    getOtherView().setTranslationX(dx - width);
                } else {
                    getOtherView().setTranslationX(dx + width);
                }
                getOtherView().setVisibility(VISIBLE);
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrolled(currentItem, pageOffset, dx);
                    mOnPageChangeListener.onPageScrollStateChanged(SCROLL_STATE_DRAGGING);
                }
                break;
            case MotionEvent.ACTION_UP:
                final boolean isNext = dx < 0;
                if (mOnPageChangeListener != null) {
                    mOnPageChangeListener.onPageScrollStateChanged(SCROLL_STATE_SETTLING);
                }
                if (pageOffset > 0.5) {
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(dx, isNext ? -width : width);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            getCurrentView().setTranslationX(value);
                            getOtherView().setTranslationX(value + (isNext ? width : -width));
                            if (Math.abs(value) == width) {
                                if (isNext) {
                                    nextItem(currentItem + 1);
                                    showNext();
                                } else {
                                    previousItem(currentItem - 1);
                                    showPrevious();
                                }
                                if (mOnPageChangeListener != null) {
                                    mOnPageChangeListener.onPageSelected(currentItem, isNext);
                                    mOnPageChangeListener.onPageScrollStateChanged(SCROLL_STATE_IDLE);
                                }
                            }
                        }
                    });
                    valueAnimator.start();
                } else {
                    ValueAnimator backAnimator = ValueAnimator.ofFloat(dx, 0);
                    backAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            getCurrentView().setTranslationX(value);
                            getOtherView().setTranslationX(value - (isNext ? -width : width));
                            if (value == 0) {
                                getOtherView().setVisibility(GONE);
                                if (mOnPageChangeListener != null) {
                                    mOnPageChangeListener.onPageSelected(currentItem, isNext);
                                    mOnPageChangeListener.onPageScrollStateChanged(SCROLL_STATE_IDLE);
                                }
                            }
                        }
                    });
                    backAnimator.start();
                }
                break;
        }
        return true;
    }

    public int getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }

    /**
     * 下一个的下标
     *
     * @param index
     * @return
     */
    private int nextItem(int index) {
        currentItem = index;
        if (currentItem >= musicSize) {
            currentItem = 0;
        } else if (currentItem < 0) {
            currentItem = musicSize - 1;
        }
        return currentItem;
    }

    /**
     * 上一个的下标
     *
     * @param index
     * @return
     */
    public int previousItem(int index) {
        currentItem = index;
        if (currentItem >= musicSize) {
            currentItem = 0;
        } else if (currentItem < 0) {
            currentItem = musicSize - 1;
        }
        return currentItem;
    }

    /**
     * 获取下一个下标值
     *
     * @return
     */
    public int getNextItem() {
        int next = currentItem + 1;
        if (next >= musicSize) {
            return 0;
        } else if (next < 0) {
            return musicSize - 1;
        }
        return next;
    }

    /**
     * 获取上一个下标值
     *
     * @return
     */
    public int getPreviousItem() {
        int previous = currentItem - 1;
        if (previous >= musicSize) {
            return 0;
        } else if (previous < 0) {
            return musicSize - 1;
        }
        return previous;
    }

    public void showPreviousWithAnimation() {
        ValueAnimator previousAnimator = ValueAnimator.ofFloat(0, width);
        getOtherView().setVisibility(VISIBLE);
        previousAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                getCurrentView().setTranslationX(value);
                getOtherView().setTranslationX(value - width);
                if (value == width) {
                    previousItem(currentItem - 1);
                    showPrevious();
                    if (mOnPageChangeListener != null) {
                        mOnPageChangeListener.onPageSelected(currentItem, false);
                        mOnPageChangeListener.onPageScrollStateChanged(SCROLL_STATE_IDLE);
                    }
                }
            }
        });
        previousAnimator.start();
    }

    public void showNextWithAnimation() {
        ValueAnimator nextAnimator = ValueAnimator.ofFloat(0, -width);
        getOtherView().setVisibility(VISIBLE);
        nextAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                getCurrentView().setTranslationX(value);
                getOtherView().setTranslationX(value + width);
                if (Math.abs(value) == width) {
                    previousItem(currentItem + 1);
                    showNext();
                    if (mOnPageChangeListener != null) {
                        mOnPageChangeListener.onPageSelected(currentItem, true);
                        mOnPageChangeListener.onPageScrollStateChanged(SCROLL_STATE_IDLE);
                    }
                }
            }
        });
        nextAnimator.start();
    }

    public int getOtherItem() {
        return getChildCount() - 1 - getDisplayedChild();
    }

    public View getOtherView() {
        return getChildAt(getOtherItem());
    }

    //获取没在屏幕中显示的Poster
    public ImageView getOtherPosterView() {
        return getOtherView().findViewById(R.id.iv_poster);
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    public interface OnPageChangeListener {
        void onPageScrolled(int position, float positionOffset, float positionOffsetPixels);

        void onPageSelected(int position, boolean isNext);

        void onPageScrollStateChanged(int state);
    }
}
