package com.oyf.codecollection.palymusic;

public class MusicDataBean {
    /*音乐资源id*/
    private int mMusicRes;
    /*专辑图片id*/
    private int mMusicPicRes;
    /*音乐名称*/
    private String mMusicName;
    /*作者*/
    private String mMusicAuthor;

    public MusicDataBean(int mMusicRes, int mMusicPicRes, String mMusicName, String mMusicAuthor) {
        this.mMusicRes = mMusicRes;
        this.mMusicPicRes = mMusicPicRes;
        this.mMusicName = mMusicName;
        this.mMusicAuthor = mMusicAuthor;
    }

    public int getMusicRes() {
        return mMusicRes;
    }

    public void setMusicRes(int mMusicRes) {
        this.mMusicRes = mMusicRes;
    }

    public int getMusicPicRes() {
        return mMusicPicRes;
    }

    public void setMusicPicRes(int mMusicPicRes) {
        this.mMusicPicRes = mMusicPicRes;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public void setMusicName(String mMusicName) {
        this.mMusicName = mMusicName;
    }

    public String getMusicAuthor() {
        return mMusicAuthor;
    }

    public void setMusicAuthor(String mMusicAuthor) {
        this.mMusicAuthor = mMusicAuthor;
    }
}
