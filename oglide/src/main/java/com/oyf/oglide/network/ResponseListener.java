package com.oyf.oglide.network;

import android.graphics.Bitmap;

import com.oyf.oglide.model.OActiveResource;

/**
 * @创建者 oyf
 * @创建时间 2020/4/24 14:45
 * @描述
 **/
public interface ResponseListener {
    void responseSuccess(OActiveResource<Bitmap> oActiveResource);

    void responseException(Exception e);
}
