package com.oyf.codecollection.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.Result;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.oyf.basemodule.manager.CameraManagerHelper;
import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.basemodule.weight.ScanQrView;
import com.oyf.codecollection.R;
import com.oyf.codecollection.utils.HuaWeiBitmapUtils;
import com.oyf.codecollection.weight.ScannerQrCodeView;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @创建者 oyf
 * @创建时间 2020/3/27 10:32
 * @描述 扫描二维码（华为 or zxing）
 **/
public class ScanQRCodeActivity extends BaseActivity implements SurfaceHolder.Callback, ZXingScannerView.ResultHandler {

    private static final String TAG = ScanQRCodeActivity.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private ScanQrView mScanQrView;
    private FrameLayout fl;

    private CameraManagerHelper mCameraManager;
    private SurfaceHolder holder;
    private ScannerQrCodeView mScannerQrCodeView;

    private HmsScanAnalyzerOptions options;
    private volatile boolean isHandlering = false;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_qrcode;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initHuaweiCamera();
        //addScannerView();
    }

    private void initHuaweiCamera() {
        mSurfaceView = findViewById(R.id.sfv);
        mScanQrView = findViewById(R.id.sqrv);
        initCamera();
    }

    private void addScannerView() {
        fl = findViewById(R.id.fl);

        mScannerQrCodeView = new ScannerQrCodeView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        fl.addView(mScannerQrCodeView);
        mScannerQrCodeView.startCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mScannerQrCodeView) {
            mScannerQrCodeView.resumeCameraPreview(this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (null != mScannerQrCodeView) {
            mScannerQrCodeView.stopCameraPreview();
            mScannerQrCodeView.setResultHandler(null);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mScannerQrCodeView) {
            mScannerQrCodeView.stopCamera();
        }
        super.onDestroy();
    }

    private void initCamera() {
        mCameraManager = new CameraManagerHelper(this);
        holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.CODE128_SCAN_TYPE).setPhotoMode(true).create();

    }

    public void startPreview() {
        if (null != mCameraManager) {
            try {
                mCameraManager.openDriver();
                mCameraManager.startPreview(holder, new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        if (isHandlering) {
                            return;
                        }
                        isHandlering = true;
                        Bitmap bitmap = HuaWeiBitmapUtils.yuv2Bitmap(data.clone(), camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
                        decodeHuaWeiQR(bitmap);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopPreview() {
        if (null != mCameraManager) {
            mCameraManager.stopPreview();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.d(ScannerQrCodeView.TAG, "rawResult=" + rawResult.getText());
        mScannerQrCodeView.setResultHandler(this);
        Toast.makeText(this, rawResult.getText(), Toast.LENGTH_SHORT).show();
    }

    private long time;

    public void decodeHuaWeiQR(Bitmap bitmap) {
        time = System.currentTimeMillis();
        HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(this, bitmap, options);
        long px = System.currentTimeMillis() - time;
        //处理扫码结果
        if (null != hmsScans && hmsScans.length > 0) {
            Log.d(ScannerQrCodeView.TAG, "hmsScans=" + hmsScans[0].getShowResult() + ",time=" + px);
        } else {
            Log.d(ScannerQrCodeView.TAG, "无结果,time=" + px);
        }
        isHandlering = false;
    }

    private static class CustomViewFinderView extends ViewFinderView {

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setSquareViewFinder(true);
            setBorderColor(Color.WHITE);
            setLaserColor(Color.GREEN);
            setLaserEnabled(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }

    }
}
