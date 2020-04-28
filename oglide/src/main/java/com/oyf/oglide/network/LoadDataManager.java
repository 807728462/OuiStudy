package com.oyf.oglide.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.oyf.oglide.ExecutorUtils;
import com.oyf.oglide.model.OActiveResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @创建者 oyf
 * @创建时间 2020/4/24 14:43
 * @描述
 **/
public class LoadDataManager implements ILoadData,Runnable, Handler.Callback {
    private String path;
    private ResponseListener responseListener;
    private Context context;
    private Handler mHandler;

    public LoadDataManager() {
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    @Override
    public OActiveResource<Bitmap> loadResource(String keyUrl, ResponseListener responseListener, Context context) {
        this.path = keyUrl;
        this.responseListener = responseListener;
        this.context = context;
        // 加载 网络图片/SD本地图片/..

        Uri uri = Uri.parse(path);

        // 网络图片
        if ("HTTP".equalsIgnoreCase(uri.getScheme()) || "HTTPS".equalsIgnoreCase(uri.getScheme())) {
            ExecutorUtils.getInstance().execute(this);
        }

        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(path);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            int responseCode = httpURLConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inputStream = httpURLConnection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;//只那图片的周围信息，内置会只获取图片的一部分而已，值获取高宽的信息 outW，outH
                options.inMutable = true;
                options.inSampleSize = 2;
                options.inPreferredConfig = Bitmap.Config.RGB_565;

                //todo 计算宽高缩放比
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                Message obtain = Message.obtain();
                obtain.what = HttpURLConnection.HTTP_OK;
                obtain.obj = bitmap;
                mHandler.sendMessage(obtain);
            }
        } catch (Exception e) {
            Message obtain = Message.obtain();
            obtain.what = HttpURLConnection.HTTP_INTERNAL_ERROR;
            obtain.obj = e;
            mHandler.sendMessage(obtain);
        } finally {
            try {
                if (null == inputStream) {
                    inputStream.close();
                }
                if (null == httpURLConnection) {
                    httpURLConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        if (null != responseListener) {
            switch (message.what) {
                case 200:
                    responseListener.responseSuccess(new OActiveResource<Bitmap>((Bitmap) message.obj));
                    break;
                case 500:
                    responseListener.responseException((Exception) message.obj);
                    break;
                default:
                    break;
            }
        }
        return false;
    }
}
