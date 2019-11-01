package com.oyf.codecollection.palymusic;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = "testMusicService";
    /*操作指令Service使用*/
    public static final String ACTION_OPT_MUSIC_PLAY = "ACTION_OPT_MUSIC_PLAY";
    public static final String ACTION_OPT_MUSIC_PAUSE = "ACTION_OPT_MUSIC_PAUSE";
    public static final String ACTION_OPT_MUSIC_NEXT = "ACTION_OPT_MUSIC_NEXT";
    public static final String ACTION_OPT_MUSIC_PREVIOUS = "ACTION_OPT_MUSIC_PREVIOUS";
    public static final String ACTION_OPT_MUSIC_SEEK_TO = "ACTION_OPT_MUSIC_SEEK_TO";

    /*状态指令Activity使用*/
    public static final String ACTION_STATUS_MUSIC_PLAY = "ACTION_STATUS_MUSIC_PLAY";
    public static final String ACTION_STATUS_MUSIC_PAUSE = "ACTION_STATUS_MUSIC_PAUSE";
    public static final String ACTION_STATUS_MUSIC_COMPLETE = "ACTION_STATUS_MUSIC_COMPLETE";
    public static final String ACTION_STATUS_MUSIC_DURATION = "ACTION_STATUS_MUSIC_DURATION";

    /*参数Key*/
    public static final String PARAM_MUSIC_DURATION = "PARAM_MUSIC_DURATION";
    public static final String PARAM_MUSIC_SEEK_TO = "PARAM_MUSIC_SEEK_TO";
    public static final String PARAM_MUSIC_CURRENT_POSITION = "PARAM_MUSIC_CURRENT_POSITION";
    public static final String PARAM_MUSIC_IS_OVER = "PARAM_MUSIC_IS_OVER";
    public static final String PARAM_MUSIC_LIST = "PARAM_MUSIC_LIST";

    private int currentMusic;
    private List<MusicDataBean> mMusicLists = new ArrayList<>();
    private boolean mIsMusicPause = false;
    private MediaPlayer mediaPlayer;
    private MusicReceiver musicReceiver = new MusicReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_OPT_MUSIC_PLAY);
        intentFilter.addAction(ACTION_OPT_MUSIC_PAUSE);
        intentFilter.addAction(ACTION_OPT_MUSIC_NEXT);
        intentFilter.addAction(ACTION_OPT_MUSIC_PREVIOUS);
        intentFilter.addAction(ACTION_OPT_MUSIC_SEEK_TO);
        LocalBroadcastManager.getInstance(this).registerReceiver(musicReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initMusicData(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMusicData(Intent intent) {
        ArrayList<MusicDataBean> listExtra = (ArrayList<MusicDataBean>) intent.getSerializableExtra(PARAM_MUSIC_LIST);
        if (listExtra != null) {
            mMusicLists.addAll(listExtra);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "musicService.currentMusic=" + currentMusic);
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_COMPLETE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer = null;
        mMusicLists.clear();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(musicReceiver);
    }

    /**
     * 接收activity的广播
     */
    public class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "musicService.接收到的广播" + intent.getAction());
            switch (intent.getAction()) {
                case ACTION_OPT_MUSIC_PLAY:
                    play(currentMusic);
                    break;
                case ACTION_OPT_MUSIC_PAUSE:
                    pause();
                    break;
                case ACTION_OPT_MUSIC_NEXT:
                    next();
                    break;
                case ACTION_OPT_MUSIC_PREVIOUS:
                    previous();
                    break;
                case ACTION_OPT_MUSIC_SEEK_TO:
                    int seek = intent.getIntExtra(PARAM_MUSIC_SEEK_TO, 0);
                    seekTo(seek);
                    break;
            }
        }
    }

    /**
     * 播放音乐没如果是当前的直接播放，如果是下一首或者其他音乐则重新设置参数重新播放
     *
     * @param currentMusic
     */
    private void play(int currentMusic) {
        Log.d(TAG, "musicService.currentMusic=" + currentMusic);
        if (currentMusic > mMusicLists.size()) {
            return;
        }
        if (this.currentMusic == currentMusic && mIsMusicPause) {
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer = null;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), mMusicLists.get(currentMusic).getMusicRes());
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
            this.currentMusic = currentMusic;
            int duration = mediaPlayer.getDuration();
            mIsMusicPause = false;
            sendMusicDurationBroadCast(duration);
        }

        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PLAY);
    }

    /**
     * 暂停
     */
    private void pause() {
        mediaPlayer.pause();
        mIsMusicPause = true;
        sendMusicStatusBroadCast(ACTION_STATUS_MUSIC_PAUSE);
    }

    /**
     * 下一首
     */
    private void next() {
        if (currentMusic + 1 >= mMusicLists.size()) {
            play(0);
        } else {
            play(currentMusic + 1);
        }

    }

    /**
     * 上一首
     */
    private void previous() {
        if (currentMusic != 0) {
            play(currentMusic - 1);
        } else {
            play(mMusicLists.size() - 1);
        }
    }

    /**
     * 设置播放进度
     *
     * @param seek
     */
    private void seekTo(int seek) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(seek);
        } else {
            mediaPlayer.seekTo(seek);
            mediaPlayer.start();
        }
    }

    /**
     * 发送当前时间的广播
     *
     * @param duration
     */
    public void sendMusicDurationBroadCast(int duration) {
        Intent intent = new Intent();
        intent.setAction(ACTION_STATUS_MUSIC_DURATION);
        intent.putExtra(PARAM_MUSIC_DURATION, duration);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * 发送音乐的状态的广播
     *
     * @param action
     */
    private void sendMusicStatusBroadCast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (action.equals(ACTION_STATUS_MUSIC_PLAY)) {
            intent.putExtra(PARAM_MUSIC_CURRENT_POSITION, mediaPlayer.getCurrentPosition());
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
