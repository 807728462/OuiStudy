package com.oyf.codecollection.ui.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.oyf.basemodule.utils.FastBlurUtil;
import com.oyf.basemodule.utils.PxAdapterUtil;
import com.oyf.basemodule.utils.StatusBarUtil;
import com.oyf.basemodule.utils.ViewCalculateUtil;
import com.oyf.codecollection.MainActivity;
import com.oyf.codecollection.R;
import com.oyf.codecollection.palymusic.DiscView;
import com.oyf.codecollection.palymusic.MusicDataBean;
import com.oyf.codecollection.palymusic.MusicService;
import com.oyf.codecollection.palymusic.MusicViewFlipper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static com.oyf.codecollection.palymusic.DiscView.DURATION_NEEDLE_ANIMATOR;

public class PlayMusicActivity extends AppCompatActivity implements View.OnClickListener, DiscView.IPlayInfo {

    private static final String TAG = "testPlayMusicActivity";
    int bg_pic_res = -1;
    ImageSwitcher imageSwitcher;
    Toolbar mToolbar;
    DiscView mDiscView;
    List<MusicDataBean> mMusicLists;


    public static final int MUSIC_MESSAGE = 0;
    TextView tvStartTime;
    TextView tvEndTime;
    SeekBar seekBar;
    ImageView ivPlay;
    ImageView ivNext;
    ImageView ivPrevious;
    MusicReceiver musicReceiver = new MusicReceiver();
    boolean isPlay = false;
    Intent intent;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    seekBar.setProgress(seekBar.getProgress() + 1000);
                    tvStartTime.setText(duration2Time(seekBar.getProgress()));
                    startUpdateSeekBarProgress();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBar(this);
        setContentView(R.layout.activity_play_music);
        init();
        initData();

    }

    @SuppressLint("NewApi")
    private void init() {
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        seekBar = findViewById(R.id.seek);
        ivPlay = findViewById(R.id.iv_play);
        ivNext = findViewById(R.id.iv_next);
        ivPrevious = findViewById(R.id.iv_previous);

        imageSwitcher = findViewById(R.id.is_main);
        mToolbar = findViewById(R.id.toolbar);
        mDiscView = findViewById(R.id.discview);
        bg_pic_res = R.drawable.bg_defalut;
        //设置toolbar
        mToolbar.setTitle("歌单");
        mToolbar.setSubtitle("歌词");
        setSupportActionBar(mToolbar);
        StatusBarUtil.setStateBar(this, mToolbar);
        StatusBarUtil.setMarqueeForToolbarTitleView(mToolbar);
        initMusic();
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

        mDiscView.setPlayInfoListener(this);
        mDiscView.setMusicLists(mMusicLists);
    }

    private void initData() {
        ivPlay.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tvStartTime.setText(duration2Time(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent(MusicService.ACTION_OPT_MUSIC_SEEK_TO);
                intent.putExtra(MusicService.PARAM_MUSIC_SEEK_TO, seekBar.getProgress());
                LocalBroadcastManager.getInstance(PlayMusicActivity.this).sendBroadcast(intent);

                stopUpdateSeekBarProgress();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PLAY);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PAUSE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_COMPLETE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_DURATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(musicReceiver, intentFilter);

    }

    private void initMusic() {
        mMusicLists = new ArrayList<>();
        MusicDataBean musicData1 = new MusicDataBean(R.raw.music1, R.drawable.poster1, "我喜欢", "梁静茹");
        MusicDataBean musicData2 = new MusicDataBean(R.raw.music2, R.drawable.poster2, "想把我唱给你听", "老狼");
        MusicDataBean musicData3 = new MusicDataBean(R.raw.music3, R.drawable.poster3, "风再起时", "张国荣");
        //添加歌曲
        mMusicLists.add(musicData1);
        mMusicLists.add(musicData2);
        mMusicLists.add(musicData3);
        //开启播放服务
        intent = new Intent(this, MusicService.class);
        intent.putExtra(MusicService.PARAM_MUSIC_LIST, (Serializable) mMusicLists);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
                mDiscView.playOrPause();
                break;
            case R.id.iv_next:
                mDiscView.next();
                break;
            case R.id.iv_previous:
                mDiscView.previous();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicReceiver);
    }

    /**
     * 发送广播去service
     *
     * @param action
     */
    private void optMusic(final String action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action));
    }

    /**
     * 用于接收service中的广播
     */
    class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG, "playmusicactivity.接收到的广播" + action);
            assert action != null;//断言 true继续 false 抛出异常并退出
            switch (action) {
                case MusicService.ACTION_STATUS_MUSIC_PLAY:
                    ivPlay.setImageResource(R.mipmap.ic_pause);
                    int currentPosition = intent.getIntExtra(MusicService.PARAM_MUSIC_CURRENT_POSITION, 0);
                    seekBar.setProgress(currentPosition);
                    if (!mDiscView.isPlaying()) {
                        mDiscView.playOrPause();
                    }
                    break;
                case MusicService.ACTION_STATUS_MUSIC_PAUSE:
                    ivPlay.setImageResource(R.mipmap.ic_play);
                    if (mDiscView.isPlaying()) {
                        mDiscView.playOrPause();
                    }
                    break;
                case MusicService.ACTION_STATUS_MUSIC_DURATION:
                    int duration = intent.getIntExtra(MusicService.PARAM_MUSIC_DURATION, 0);
                    updateMusicDurationInfo(duration);
                    break;
                case MusicService.ACTION_STATUS_MUSIC_COMPLETE:
                    boolean isOver = intent.getBooleanExtra(MusicService.PARAM_MUSIC_IS_OVER, true);
                    if (isOver)
                        mDiscView.next();
                    break;
            }
        }
    }

    public void play() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
        startUpdateSeekBarProgress();
    }

    public void pause() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
        stopUpdateSeekBarProgress();
    }

    public void next() {
        //下一曲 歌曲延迟切换 唱针指到碟片上开始播放
        imageSwitcher.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
            }
        }, DURATION_NEEDLE_ANIMATOR);
        stopUpdateSeekBarProgress();
        tvStartTime.setText(duration2Time(0));
        tvEndTime.setText(duration2Time(0));
    }

    public void previous() {
        //上一曲 歌曲延迟切换
        imageSwitcher.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_PREVIOUS);
            }
        }, DURATION_NEEDLE_ANIMATOR);
        stopUpdateSeekBarProgress();
        tvStartTime.setText(duration2Time(0));
        tvEndTime.setText(duration2Time(0));
    }

    public void stop() {
        stopUpdateSeekBarProgress();
        ivPlay.setImageResource(R.mipmap.ic_play);
        tvStartTime.setText(duration2Time(0));
        tvEndTime.setText(duration2Time(0));
        seekBar.setProgress(0);
    }

    /**
     * 初始化seekbar，设置总时长，并开始无限循环
     *
     * @param duration
     */
    private void updateMusicDurationInfo(int duration) {
        seekBar.setProgress(0);
        seekBar.setMax(duration);
        tvEndTime.setText(duration2Time(duration));
        tvStartTime.setText(duration2Time(0));
        startUpdateSeekBarProgress();
    }

    /**
     * 延迟1000ms发送消息
     */
    private void startUpdateSeekBarProgress() {
        /*避免重复发送Message*/
        stopUpdateSeekBarProgress();
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    /**
     * 暂停发送1000ms的延迟
     */
    private void stopUpdateSeekBarProgress() {
        handler.removeMessages(MUSIC_MESSAGE);
    }

    /*根据时长格式化称时间文本*/
    private String duration2Time(int duration) {
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;
        return (min < 10 ? "0" + min : min + "") + ":" + (sec < 10 ? "0" + sec : sec + "");
    }

    @Override
    public void onMusicInfoChanged(String musicName, String musicAuthor) {
        //唱盘接口回调 切歌
        mToolbar.setTitle(musicName);
        mToolbar.setSubtitle(musicAuthor);
    }

    @Override
    public void onMusicPicChanged(int musicPicRes) {
        //唱盘接口回调 换图
        try2UpdateMusicPicBackground(musicPicRes);
    }


    @Override
    public void onMusicChanged(DiscView.MusicChangedStatus musicChangedStatus) {
        //唱盘接口回调 音乐播放状态改变
        switch (musicChangedStatus) {
            case PLAY:
                play();
                break;
            case PAUSE:
                pause();
                break;
            case NEXT:
                next();
                break;
            case PREVIOUS:
                previous();
                break;
            case STOP:
                stop();
                break;

        }
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
