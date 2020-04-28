package com.oyf.oglide;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.oyf.oglide.cache.DiskLruCacheImpl;
import com.oyf.oglide.cache.MemoryCache;
import com.oyf.oglide.cache.OActiveCache;
import com.oyf.oglide.lifecycle.OLifecycleListener;
import com.oyf.oglide.model.OActiveResource;
import com.oyf.oglide.model.OGlideUrl;
import com.oyf.oglide.network.LoadDataManager;
import com.oyf.oglide.network.ResponseListener;
import com.oyf.oglide.utils.OPreconditions;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/23 14:22
 * @描述
 **/
public class ORequestTargetEngine implements OLifecycleListener, OActiveResource.ResourceListener<Bitmap>, MemoryCache.MemoryCacheCallback, ResponseListener {

    private static final int MEMORY_MAX_SIZE = 1024 * 1024 * 10;
    private OActiveCache<Bitmap> mOActiveCache;
    private MemoryCache<Bitmap> mMemoryCache;
    private DiskLruCacheImpl mDiskLruCache;

    private OGlideUrl mOGlideUrl;
    private Context mContext;
    private ImageView mImageView;

    public ORequestTargetEngine() {
        mOActiveCache = new OActiveCache(this);
        mMemoryCache = new MemoryCache(MEMORY_MAX_SIZE);
        mMemoryCache.setMemoryCacheCallback(this);
        try {
            mDiskLruCache = new DiskLruCacheImpl.Buidler().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    public void loadInitAction(String path, Context context) {
        mOGlideUrl = new OGlideUrl(path);
        mContext = context;
    }

    public void into(ImageView imageView) {
        this.mImageView = imageView;
        OPreconditions.checkMainThread();
        OPreconditions.checkNotNull(mImageView);
        OPreconditions.checkNotNull(mOGlideUrl.getUrl());
        OActiveResource<Bitmap> oActiveResource = cacheAction();
        if (null != oActiveResource) {
            mImageView.setImageBitmap(oActiveResource.get());
        }
    }

    /**
     * 获取缓存的图片
     */
    private OActiveResource<Bitmap> cacheAction() {
        OActiveResource<Bitmap> oActiveResource = mOActiveCache.get(mOGlideUrl);
        if (null != oActiveResource) {
            Log.d(OGlide.TAG, "cacheAction: 本次加载是在(mOActiveCache)中获取的资源>>>");
            oActiveResource.acquire();
            return oActiveResource;
        }

        oActiveResource = mMemoryCache.get(mOGlideUrl.getUrl());
        if (null != oActiveResource) {
            Log.d(OGlide.TAG, "cacheAction: 本次加载是在(mMemoryCache)中获取的资源>>>");
            mMemoryCache.manualedRemove(mOGlideUrl.getUrl());
            mOActiveCache.activate(mOGlideUrl, oActiveResource);
            oActiveResource.acquire();
            return oActiveResource;
        }

        oActiveResource = mDiskLruCache.get(mOGlideUrl);
        if (null != oActiveResource) {
            Log.d(OGlide.TAG, "cacheAction: 本次加载是在(磁盘缓存)中获取的资源>>>");
            mOActiveCache.activate(mOGlideUrl, oActiveResource);
            oActiveResource.acquire();
            return oActiveResource;
        }
        oActiveResource = new LoadDataManager().loadResource(mOGlideUrl.getPath(), this, mContext);
        if (oActiveResource != null)
            return oActiveResource;

        return null;

    }


    /**
     * 活动中的缓存，如果gc回收的时候回调次方法
     *
     * @param key
     * @param resource
     */
    @Override
    public void onResourceReleased(OGlideUrl key, OActiveResource<Bitmap> resource) {
        if (null != mMemoryCache) {
            resource.release();
            mMemoryCache.put(key.getUrl(), resource);
        }
    }

    /**
     * 被动的移除lru中的缓存
     *
     * @param key
     * @param oldOActiveResource
     */
    @Override
    public void entryRemovedMemoryCache(String key, OActiveResource oldOActiveResource) {

    }

    /**
     * 网络获取图片成功
     *
     * @param oActiveResource
     */
    @Override
    public void responseSuccess(OActiveResource<Bitmap> oActiveResource) {
        if (null != mImageView) {
            Bitmap bitmap = oActiveResource.get();
            if (null != bitmap) {
                mOActiveCache.activate(mOGlideUrl, oActiveResource);
                oActiveResource.acquire();
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * 网络获取图片失败
     *
     * @param e
     */
    @Override
    public void responseException(Exception e) {
        Log.d(OGlide.TAG, e.getMessage());
    }
}
