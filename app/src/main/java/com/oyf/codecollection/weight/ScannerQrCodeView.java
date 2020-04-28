package com.oyf.codecollection.weight;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.dm7.barcodescanner.core.BarcodeScannerView;
import me.dm7.barcodescanner.core.DisplayUtils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @创建者 oyf
 * @创建时间 2020/4/27 16:07
 * @描述
 **/
public class ScannerQrCodeView extends BarcodeScannerView {
    public static final String TAG = "ScannerQrCodeView";

    private List<BarcodeFormat> all_formats;
    private List<BarcodeFormat> mFormats;
    private volatile ZXingScannerView.ResultHandler mResultHandler;

    private ThreadPoolExecutor mThreadPoolExecutor;
    private long mLastFrameTime = 0;
    private ThreadLocal<MultiFormatReader> mThreadLocal;
    private Handler handler;

    //todo 优化点1  减少不必要的扫码类型

   /*ALL_FORMATS.add(BarcodeFormat.AZTEC);
        ALL_FORMATS.add(BarcodeFormat.CODABAR);
        ALL_FORMATS.add(BarcodeFormat.CODE_39);
        ALL_FORMATS.add(BarcodeFormat.CODE_93);
        ALL_FORMATS.add(BarcodeFormat.CODE_128);
        ALL_FORMATS.add(BarcodeFormat.DATA_MATRIX);
        ALL_FORMATS.add(BarcodeFormat.EAN_8);
        ALL_FORMATS.add(BarcodeFormat.EAN_13);
        ALL_FORMATS.add(BarcodeFormat.ITF);
        ALL_FORMATS.add(BarcodeFormat.MAXICODE);
        ALL_FORMATS.add(BarcodeFormat.PDF_417);
        ALL_FORMATS.add(BarcodeFormat.RSS_14);
        ALL_FORMATS.add(BarcodeFormat.RSS_EXPANDED);
        ALL_FORMATS.add(BarcodeFormat.UPC_A);
        ALL_FORMATS.add(BarcodeFormat.UPC_E);
        ALL_FORMATS.add(BarcodeFormat.UPC_EAN_EXTENSION);*/


    public ScannerQrCodeView(Context context) {
        this(context, null);
    }

    public ScannerQrCodeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        all_formats = new ArrayList<>();
        all_formats.add(BarcodeFormat.QR_CODE);
        mThreadLocal = new ThreadLocal<>();
        handler = new Handler(Looper.getMainLooper());
        initExecutor();
        initMultiFormatReader();
    }

    private void initExecutor() {
        mThreadPoolExecutor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(2),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(false);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void setFormats(List<BarcodeFormat> formats) {
        mFormats = formats;
        initMultiFormatReader();
    }

    public void setResultHandler(ZXingScannerView.ResultHandler resultHandler) {
        mResultHandler = resultHandler;
    }

    public Collection<BarcodeFormat> getFormats() {
        if (mFormats == null) {
            return all_formats;
        }
        return mFormats;
    }

    private MultiFormatReader initMultiFormatReader() {
        MultiFormatReader multiFormatReader = mThreadLocal.get();
        if (null == multiFormatReader) {
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
            multiFormatReader = new MultiFormatReader();
            multiFormatReader.setHints(hints);
            mThreadLocal.set(multiFormatReader);
        }
        return multiFormatReader;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mResultHandler == null) {
            return;
        }
        long curr = System.currentTimeMillis();
        if (curr - mLastFrameTime < 200) {
            camera.setOneShotPreviewCallback(this);
            return;
        }
        mLastFrameTime = curr;
        byte[] clone = data.clone();
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                handlerFrame(clone, camera);
            }
        });
        camera.setOneShotPreviewCallback(this);
    }

    private void handlerFrame(byte[] data, Camera camera) {
        try {
            Log.d(TAG, "开始处理一帧");
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            int width = size.width;
            int height = size.height;

            if (DisplayUtils.getScreenOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
                int rotationCount = getRotationCount();
                if (rotationCount == 1 || rotationCount == 3) {
                    int tmp = width;
                    width = height;
                    height = tmp;
                }
                data = getRotatedData(data, camera);
            }

            Result rawResult = null;
            LuminanceSource source = buildLuminanceSource(data, width, height);

            if (source != null) {
                MultiFormatReader mMultiFormatReader = initMultiFormatReader();
                rawResult = decodeWithState(mMultiFormatReader, source);

                if (rawResult == null) {
                    source = source.invert();
                    rawResult = decodeWithState(mMultiFormatReader, source);
                }
            }

            final Result finalRawResult = rawResult;
            if (finalRawResult != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mResultHandler != null) {
                            ZXingScannerView.ResultHandler resultHandler = mResultHandler;
                            mResultHandler = null;
                            resultHandler.handleResult(finalRawResult);
                            clearQueue();
                        }
                    }
                });
            }
        } catch (RuntimeException e) {
            // TODO: Terrible hack. It is possible that this method is invoked after camera is released.
            Log.e(TAG, e.toString(), e);
        }
    }

    /**
     * 清除线程池中的排队等待队列以及正在执行的线程
     */
    private void clearQueue() {
        if (null != mThreadPoolExecutor && !mThreadPoolExecutor.isShutdown()) {
            mThreadPoolExecutor.getQueue().clear();
        }
    }

    /**
     * 解析二维码
     *
     * @param mMultiFormatReader
     * @param source
     * @return
     */
    private Result decodeWithState(MultiFormatReader mMultiFormatReader, LuminanceSource source) {
        Result rawResult = null;
        //todo 优先GlobalHistogramBinarizer解码，解码失败转为HybridBinarizer解码
        BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));
        if (bitmap == null) {
            bitmap = new BinaryBitmap(new HybridBinarizer(source));
        }
        try {
            rawResult = mMultiFormatReader.decodeWithState(bitmap);
        } catch (Exception e) {
        } finally {
            mMultiFormatReader.reset();
        }
        return rawResult;
    }

    public void resumeCameraPreview(ZXingScannerView.ResultHandler resultHandler) {
        mResultHandler = resultHandler;
        super.resumeCameraPreview();
    }

    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview(width, height);
        if (rect == null) {
            return null;
        }
        // Go ahead and assume it's YUV rather than die.
        PlanarYUVLuminanceSource source = null;

        try {
            source = new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                    rect.width(), rect.height(), false);
        } catch (Exception e) {
        }

        return source;
    }
}
