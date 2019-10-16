package com.oyf.basemodule.utils.mvp;

/**
 * Presenter基类接口，V与module的通信桥梁
 * @param <M> 业务逻辑
 * @param <V> View接口
 */
public interface IBasePresenter<M ,V > {
    /**
     * 创建业务逻辑实例
     * @return 返回一个业务逻辑实例
     */
    M createModule();

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

    /**
     * 与view绑定
     * @param v view接口{@link IBaseView}
     */
    void takeView(V v);

}
