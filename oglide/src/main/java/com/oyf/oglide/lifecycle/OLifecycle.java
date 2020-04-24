package com.oyf.oglide.lifecycle;

/**
 * @创建者 oyf
 * @创建时间 2020/4/22 14:46
 * @描述
 **/
public interface OLifecycle {
    void addListener(OLifecycleListener listener);

    void removeListener(OLifecycleListener listener);
}
