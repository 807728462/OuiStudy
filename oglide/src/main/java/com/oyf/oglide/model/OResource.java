package com.oyf.oglide.model;

import androidx.annotation.NonNull;

/**
 * @创建者 oyf
 * @创建时间 2020/4/20 16:46
 * @描述
 **/
public interface OResource<Z> {

    @NonNull
    Class<Z> getResourceClass();

    @NonNull
    Z get();

    int getSize();

    void recycle();
}
