package com.oyf.basemodule.utils.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import mo.singou.vehicle.ai.base.widget.LoadingDialog;

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IBaseView {

    protected P presenter;
    protected View rootView;
    protected Context mContext;
    protected LoadingDialog loadingDialog;
    private ViewStub emptyView;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(mContext);
        presenter = createPresenter();
        if(presenter!=null){
            presenter.takeView(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.layout_base, container, false);
            ((ViewGroup) rootView.findViewById(R.id.fl_content)).addView(getLayoutInflater().inflate(getLayoutId(), null));
        }
        if (useEventBus()){
            EventBus.getDefault().register(this);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        emptyView = rootView.findViewById(R.id.vs_empty);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter!=null){
            presenter.dropView();
        }
        if (useEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        dismissLoading();
    }

    @Override
    public void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showEmptyOrErrorView() {
        showEmptyOrErrorView(getString(R.string.no_data), R.drawable.ic_empty);
    }

    @Override
    public void showEmptyOrErrorView(String text, int imageId) {


        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
            rootView. findViewById(R.id.iv_empty).setBackgroundResource(imageId);
            ((TextView) rootView.findViewById(R.id.tv_empty)).setText(text);
            rootView. findViewById(R.id.ll_empty).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (presenter!=null){
                        presenter.start();
                    }
                }
            });
        }
    }

    @Override
    public void showLoading() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void setLoadingText(int resId) {
        if (loadingDialog!=null){
            loadingDialog.setLoadingText(resId);
        }
    }

    @Override
    public boolean useEventBus() {
        return false;
    }

    protected abstract P createPresenter();
    protected abstract int getLayoutId();
    protected abstract void initView();
}
