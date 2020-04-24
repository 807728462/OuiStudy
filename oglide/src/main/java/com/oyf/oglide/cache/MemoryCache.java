package com.oyf.oglide.cache;

import android.util.LruCache;

import com.oyf.oglide.model.OActiveResource;

/**
 * @创建者 oyf
 * @创建时间 2020/4/21 10:09
 * @描述
 **/
public class MemoryCache<T> extends LruCache<String, OActiveResource<T>> {
    private boolean isManualed;
    private MemoryCacheCallback<T> mMemoryCacheCallback;

    public void setMemoryCacheCallback(MemoryCacheCallback<T> memoryCacheCallback) {
        mMemoryCacheCallback = memoryCacheCallback;
    }

    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    public OActiveResource<T> manualedRemove(String url) {
        isManualed = true;
        OActiveResource<T> remove = remove(url);
        isManualed = false;
        return remove;
    }

    /**
     * 手动移除 还有使用lru缓存移除会调用此方法
     *
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, OActiveResource oldValue, OActiveResource newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (null != mMemoryCacheCallback && !isManualed) {
            mMemoryCacheCallback.entryRemovedMemoryCache(key, oldValue);
        }
    }

    @Override
    protected int sizeOf(String key, OActiveResource value) {
        Object o = value.get();
        return o.toString().length();
    }

   public interface MemoryCacheCallback<T> {
        void entryRemovedMemoryCache(String key, OActiveResource<T> oldOActiveResource);
    }

}
