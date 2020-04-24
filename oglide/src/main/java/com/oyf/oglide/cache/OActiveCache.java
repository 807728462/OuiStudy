package com.oyf.oglide.cache;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.oyf.oglide.model.OActiveResource;
import com.oyf.oglide.model.OGlideUrl;
import com.oyf.oglide.model.OResource;
import com.oyf.oglide.utils.OPreconditions;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @创建者 oyf
 * @创建时间 2020/4/20 17:01
 * @描述
 **/
public class OActiveCache<T> {
    private final Handler mainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
           /* if (msg.what == MSG_CLEAN_REF) {
                cleanupActiveReference((ResourceWeakReference) msg.obj);
                return true;
            }*/
            return false;
        }
    });

    final Map<String, ResourceWeakReference> activeEngineResources = new HashMap<>();

    private ReferenceQueue<OActiveResource<T>> mReferenceQueue;
    private OActiveResource.ResourceListener listener;

    //是否是手动
    private boolean isManualed;
    //循环监听弱引用是否有自动回收
    private boolean isShutdown;
    private Thread thread;

    public OActiveCache(OActiveResource.ResourceListener listener) {
        this.listener = listener;
    }

    /**
     * 存储一个活动资源
     *
     * @param key
     * @param engineResource
     */
    public void activate(OGlideUrl key, OActiveResource<T> engineResource) {
        String url = OPreconditions.checkNotEmpty(key);
        //（Value没有被使用了，就会发起这个监听，给外界业务需要来使用）
        engineResource.setResourceListener(listener);
        engineResource.setKey(key);
        ResourceWeakReference resourceWeakReference = new ResourceWeakReference(url, engineResource, mReferenceQueue);
        activeEngineResources.put(key.getUrl(), resourceWeakReference);
    }

    /**
     * 获取一个图片资源
     *
     * @param key
     * @return
     */
    public OActiveResource get(OGlideUrl key) {
        ResourceWeakReference resourceWeakReference = activeEngineResources.get(key.getUrl());
        if (null != resourceWeakReference) {
            return resourceWeakReference.get();
        }
        return null;
    }

    /**
     * 手动的移除，不需要使用弱引用在去回收一次
     *
     * @param key
     * @return
     */
    public OActiveResource remove(OGlideUrl key) {
        isManualed = true;
        ResourceWeakReference resourceWeakReference = activeEngineResources.remove(key.getUrl());
        isManualed = false;
        if (null != resourceWeakReference) {
            return resourceWeakReference.get();
        }
        return null;
    }

    /**
     * 关闭弱引用的监听
     */
    public void closeThread() {
        isShutdown = true;
        activeEngineResources.clear();
        System.gc();
    }

    /**
     * 获取每次存储数据的队列
     *
     * @return
     */
    private ReferenceQueue<OActiveResource<T>> getReferenceQueue() {
        if (null == mReferenceQueue) {
            mReferenceQueue = new ReferenceQueue<>();
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    cleanReferenceQueue();
                }
            });
            thread.start();
        }
        return mReferenceQueue;
    }

    /**
     * 循环的监听，当弱引用又自动被回收时候调用 此方法
     */
    void cleanReferenceQueue() {
        while (!isShutdown) {
            try {
                ResourceWeakReference ref = (ResourceWeakReference) mReferenceQueue.remove();
                if (!activeEngineResources.isEmpty() && !isManualed) {
                    activeEngineResources.remove(ref.key);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class ResourceWeakReference extends WeakReference<OActiveResource<T>> {
        String key;
        OResource<T> resource;

        public ResourceWeakReference(String key, OActiveResource<T> referent, ReferenceQueue<? super OActiveResource<T>> q) {
            super(referent, q);
            this.key = key;
            this.resource = referent;

        }

        void reset() {
            resource = null;
            clear();
        }
    }
}
