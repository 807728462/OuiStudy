package com.oyf.codecollection.ui.activity;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.oyf.basemodule.manager.CameraManagerHelper;
import com.oyf.basemodule.mvp.BaseActivity;
import com.oyf.basemodule.mvp.BasePresenter;
import com.oyf.basemodule.weight.ScanQrView;
import com.oyf.codecollection.R;

/**
 * @创建者 oyf
 * @创建时间 2020/3/27 10:32
 * @描述
 **/
public class ScanQRCodeActivity extends BaseActivity implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;
    private ScanQrView mScanQrView;


    private CameraManagerHelper mCameraManager;
    private SurfaceHolder holder;

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
        mSurfaceView = findViewById(R.id.sfv);
        mScanQrView = findViewById(R.id.sqrv);
        //mScanQrView.startPreview();
        initCamera();
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
}
