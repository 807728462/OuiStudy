package com.oyf.oglide.model;

import androidx.annotation.NonNull;

import com.oyf.oglide.utils.OPreconditions;

/**
 * @创建者 oyf
 * @创建时间 2020/4/20 16:46
 * @描述
 **/
public class OActiveResource<Z> implements OResource<Z> {

    private ResourceListener listener;
    private OGlideUrl key;
    private int acquired;
    private boolean isRecycled;
    private Z resource;

    public OActiveResource(Z z) {
        resource = z;
    }

    /**
     * 监听缓存已经没有使用的时候，回调接口
     */
    public interface ResourceListener<T> {
        void onResourceReleased(OGlideUrl key, OActiveResource<T> resource);
    }

    public void setResourceListener(ResourceListener listener) {
        this.listener = listener;
    }


    public OGlideUrl getKey() {
        return key;
    }

    public void setKey(OGlideUrl key) {
        this.key = key;
    }

    @NonNull
    @Override
    public Class getResourceClass() {
        return resource.getClass();
    }

    @NonNull
    @Override
    public Z get() {
        return resource;
    }

    @Override
    public int getSize() {
        return acquired;
    }

    @Override
    public void recycle() {
        if (acquired > 0) {
            throw new IllegalStateException("Cannot recycle a resource while it is still acquired");
        }
        if (isRecycled) {
            throw new IllegalStateException("Cannot recycle a resource that has already been recycled");
        }
        isRecycled = true;
    }

    /**
     * 每次使用加1
     */
    public void acquire() {
        if (isRecycled) {
            throw new IllegalStateException("Cannot acquire a recycled resource");
        }
        OPreconditions.checkMainThread();
        ++acquired;
    }

    public void release() {
        if (acquired <= 0) {
            throw new IllegalStateException("Cannot release a recycled or not yet acquired resource");
        }
        OPreconditions.checkMainThread();
        if (--acquired == 0) {
            listener.onResourceReleased(key, this);
        }
    }

    @Override
    public String toString() {
        return "EngineResource{"
                + ", listener=" + listener
                + ", key=" + key
                + ", acquired=" + acquired
                + ", isRecycled=" + isRecycled
                + ", resource=" + resource
                + '}';
    }
}
