package com.oyf.oglide.network;

import android.content.Context;
import android.graphics.Bitmap;

import com.oyf.oglide.model.OActiveResource;

/**
 * @创建者 oyf
 * @创建时间 2020/4/25 9:53
 * @描述
 **/
public interface ILoadData {
    OActiveResource<Bitmap> loadResource(String keyUrl, ResponseListener responseListener, Context context);
}
