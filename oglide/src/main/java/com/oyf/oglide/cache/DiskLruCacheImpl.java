package com.oyf.oglide.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.oyf.oglide.cache.disk.DiskLruCache;
import com.oyf.oglide.model.OActiveResource;
import com.oyf.oglide.model.OGlideUrl;
import com.oyf.oglide.model.OResource;
import com.oyf.oglide.utils.OPreconditions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @创建者 oyf
 * @创建时间 2020/4/22 10:08
 * @描述 磁盘缓存
 **/
public class DiskLruCacheImpl {
    private final String TAG = DiskLruCacheImpl.class.getSimpleName();

    private String DISKLRU_CACHE_DIR; // 磁盘缓存的的目录

    private int APP_VERSION; // 我们的版本号，一旦修改这个版本号，之前的缓存失效
    private int VALUE_COUNT; // 通常情况下都是1
    private long MAX_SIZE; // 以后修改成 使用者可以设置的

    private DiskLruCache mDiskLruCache;

    private DiskLruCacheImpl(Buidler buidler) throws IOException {
        this.DISKLRU_CACHE_DIR = buidler.DISKLRU_CACHE_DIR;
        this.APP_VERSION = buidler.APP_VERSION;
        this.VALUE_COUNT = buidler.VALUE_COUNT;
        this.MAX_SIZE = buidler.MAX_SIZE;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DISKLRU_CACHE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        mDiskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
    }

    public void put(OGlideUrl oGlideUrl, OActiveResource oActiveResource) {
        try {
            put(oGlideUrl, oActiveResource, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void put(OGlideUrl oGlideUrl, OActiveResource oActiveResource, boolean isThrow) throws IOException {
        OPreconditions.checkNotEmpty(oGlideUrl);
        DiskLruCache.Editor edit = null;
        OutputStream outputStream = null;
        try {
            edit = mDiskLruCache.edit(oGlideUrl.getUrl());
            outputStream = edit.newOutputStream(VALUE_COUNT);
            if (oActiveResource.getResourceClass().isAssignableFrom(Bitmap.class)) {
                Bitmap bitmap = (Bitmap) oActiveResource.get();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            edit.abort();
            throw e;
        } finally {
            edit.commit();
            mDiskLruCache.flush();
            edit.abort();
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public OActiveResource get(OGlideUrl oGlideUrl) {
        try {
            return get(oGlideUrl, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private OActiveResource get(OGlideUrl oGlideUrl, boolean isThrow) throws IOException {
        OPreconditions.checkNotEmpty(oGlideUrl);
        DiskLruCache.Snapshot snapshot = null;
        InputStream inputStream = null;
        try {
            snapshot = mDiskLruCache.get(oGlideUrl.getUrl());
            if (null != snapshot) {
                inputStream = snapshot.getInputStream(0);

                Bitmap resource = BitmapFactory.decodeStream(inputStream);
                OActiveResource<Bitmap> oActiveResource = new OActiveResource<>(resource);
                return oActiveResource;
            }
        } catch (IOException e) {
            throw e;
        } finally {
            mDiskLruCache.flush();
            if (snapshot != null) {
                snapshot.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return null;
    }

    public static class Buidler {
        private String DISKLRU_CACHE_DIR = "disk_lru_cache_dir"; // 磁盘缓存的的目录

        private int APP_VERSION = 1; // 我们的版本号，一旦修改这个版本号，之前的缓存失效
        private int VALUE_COUNT = 1; // 通常情况下都是1
        private long MAX_SIZE = 1024 * 1024 * 10; // 以后修改成 使用者可以设置的

        public Buidler setDisklruCacheDir(String disklruCachedir) {
            this.DISKLRU_CACHE_DIR = disklruCachedir;
            return this;
        }

        public Buidler setAppVersion(int appVersion) {
            this.APP_VERSION = appVersion;
            return this;
        }

        public Buidler setValueCount(int valueCount) {
            this.VALUE_COUNT = valueCount;
            return this;
        }

        public Buidler setMaxSize(long maxSize) {
            this.MAX_SIZE = maxSize;
            return this;
        }

        public DiskLruCacheImpl build() throws IOException {
            return new DiskLruCacheImpl(this);
        }
    }
}
