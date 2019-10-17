package com.oyf.basemodule.mvp;

public interface IPresenter<M,V> {

    M creatModel();

    void takeView(V v);

    /**
     * 取消加载圈
     */
    void dismissLoading();

    /**
     * 销毁View一般是在页面退出的时候
     */
    void dropView();

    /**
     * 是否已经与View绑定
     * @return true绑定，否则没有绑定
     */
    boolean isAttached();

    /**
     * 显示加载圈
     */
    void showLoading();

    /**
     * 开始任务比如请求网络、读取文件或数据库等操作
     */
    void start();
}
