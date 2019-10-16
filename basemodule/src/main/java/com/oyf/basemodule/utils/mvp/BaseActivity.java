package com.oyf.basemodule.utils.mvp;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import mo.singou.vehicle.ai.base.widget.LoadingDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 页面基类，包含权限申请与一些初始化逻辑
 *
 * @param <P>
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, IBaseView {
    private static final String[] PERS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    private static final int CODE_REQUEST_PERS = 100;
    protected P presenter;
    private Unbinder unbinder;
    protected Context mContext;
    private ViewStub emptyView;
    protected LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        loadingDialog = new LoadingDialog(mContext);
        if (Build.VERSION.SDK_INT >= 23 && !EasyPermissions.hasPermissions(this, PERS)) {
            EasyPermissions.requestPermissions(this, "权限申请", CODE_REQUEST_PERS, PERS);
        }
        if (hasActionBar()) {
            setContentView(R.layout.layout_base);
            ((ViewGroup) findViewById(R.id.fl_content)).addView(getLayoutInflater().inflate(getLayoutId(), null));
        } else {
            setContentView(getLayoutId());
        }
        //初始化ButterKnife
        unbinder = ButterKnife.bind(this);
        if (useEventBus()) {
            EventBus.getDefault().register(this);
        }
        presenter = createPresenter();
        if (presenter != null) {
            presenter.takeView(this);
        }
        initView();
    }

    /**
     * 重写onRequestPermissionsResult，用于接受请求结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求权限成功。
     * 可以弹窗显示结果，也可执行具体需要的逻辑操作
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    /**
     * 请求权限失败
     *
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        /**
         * 若是在权限弹窗中，用户勾选了'NEVER ASK AGAIN.'或者'不在提示'，且拒绝权限。
         * 这时候，需要跳转到设置界面去，让用户手动开启。
         */
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //new AppSettingsDialog.Builder(this).build().show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        if (presenter != null) {
            presenter.dropView();
        }
        dismissLoading();
    }

    @Override
    public void showLoading() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void setLoadingText(int resId) {
        if (loadingDialog != null) {
            loadingDialog.setLoadingText(resId);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    //***************************************空页面方法*************************************
    protected void showEmptyView(String text) {
        showEmptyOrErrorView(text, R.drawable.ic_empty);
    }

    protected void showEmptyView() {
        showEmptyView(getString(R.string.no_data));
    }


    /**
     * 显示空View
     *
     * @param text 空view提示文字
     * @param img  空view图片
     */
    public void showEmptyOrErrorView(String text, int img) {
        emptyView = findViewById(R.id.vs_empty);

        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
            findViewById(R.id.iv_empty).setBackgroundResource(img);
            ((TextView) findViewById(R.id.tv_empty)).setText(text);
            findViewById(R.id.ll_empty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (presenter != null) {
                        presenter.start();
                    }
                }
            });
        }
    }


    @Override
    public void showEmptyOrErrorView() {
        showEmptyView();
    }

    /**
     * 隐藏无数据view
     */
    @Override
    public void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 是否使用eventBus默认使用后续优化
     *
     * @return true使用，false不使用
     */
    @Override
    public boolean useEventBus() {
        return false;
    }

    /**
     * 是否有actionBar
     *
     * @return true有，false没有
     */
    protected boolean hasActionBar() {
        return false;
    }

    /**
     * 获取页面布局ID
     *
     * @return 返回布局ID
     */
    protected abstract int getLayoutId();

    /**
     * 创建Presenter
     *
     * @return 返回Presenter实例
     */
    protected abstract P createPresenter();

    /**
     * 初始化View
     */
    protected abstract void initView();
}
