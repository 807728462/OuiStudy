package com.oyf.codecollection.ui.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.zxing.Result;
import com.oyf.basemodule.manager.CameraManagerHelper;
import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.basemodule.weight.ScanQrView;
import com.oyf.codecollection.R;
import com.oyf.codecollection.weight.ScannerQrCodeView;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * @创建者 oyf
 * @创建时间 2020/3/27 10:32
 * @描述
 **/
public class ScanQRCodeActivity extends BaseActivity implements SurfaceHolder.Callback, ZXingScannerView.ResultHandler {

    private static final String TAG = ScanQRCodeActivity.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private ScanQrView mScanQrView;
    private FrameLayout fl;

    private CameraManagerHelper mCameraManager;
    private SurfaceHolder holder;
    private ScannerQrCodeView mScannerQrCodeView;

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
        //mSurfaceView = findViewById(R.id.sfv);
        //mScanQrView = findViewById(R.id.sqrv);
        //mScanQrView.startPreview();
        //initCamera();
        addScannerView();
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
    }

    public void startPreview() {
        if (null != mCameraManager) {
            try {
                mCameraManager.openDriver();
                mCameraManager.startPreview(holder, null);
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
