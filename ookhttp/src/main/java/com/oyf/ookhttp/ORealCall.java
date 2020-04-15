package com.oyf.ookhttp;

import android.util.Log;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.ookhttp.interceptor.OBridgeInterceptor;
import com.oyf.ookhttp.interceptor.OCallServerInterceptor;
import com.oyf.ookhttp.interceptor.OConnectInterceptor;
import com.oyf.ookhttp.interceptor.OInterceptor;
import com.oyf.ookhttp.interceptor.ORealInterceptorChain;
import com.oyf.ookhttp.interceptor.ORetryAndFollowUpInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 11:20
 * @描述
 **/
public class ORealCall implements OCall {
    private static final String TAG = ORealCall.class.getSimpleName();

    final ORetryAndFollowUpInterceptor mRetryAndFollowUpInterceptor;
    private OOkHttpClient mOOkHttpClient;
    private ORequest mORequest;
    private boolean forWebSocket;

    private boolean executed;//保证每个请求只能执行一次

    private ORealCall(OOkHttpClient okHttpClient, ORequest request, boolean forWebSocket) {
        this.mOOkHttpClient = okHttpClient;
        this.mORequest = request;
        this.forWebSocket = forWebSocket;
        executed = false;
        mRetryAndFollowUpInterceptor = new ORetryAndFollowUpInterceptor(mOOkHttpClient);
    }

    public static OCall newRealCall(OOkHttpClient okHttpClient, ORequest request, boolean forWebSocket) {
        ORealCall oRealCall = new ORealCall(okHttpClient, request, forWebSocket);
        return oRealCall;
    }

    /**
     * 执行异步
     *
     * @param callBack
     */
    @Override
    public void enqueue(OCallBack callBack) {
        LogUtils.dTag(OOkHttpClient.TAG, "ORealCall.enqueue");
        synchronized (this) {
            if (executed)
                throw new IllegalStateException("Already Executed,已经执行过一次请求 :" + mORequest.url);
            executed = true;
        }
        mOOkHttpClient.dispatcher().enqueue(new AsyncCall(callBack));
    }

    @Override
    public ORequest request() {
        return mORequest;
    }

    /**
     * 是否取消
     *
     * @return
     */
    public boolean isCanceled() {
        return mRetryAndFollowUpInterceptor.isCanceled();
    }

    /**
     * 是否执行过请求
     *
     * @return
     */
    public boolean isExecuted() {
        return executed;
    }

    public void cancel() {
        mRetryAndFollowUpInterceptor.cancel();
    }

    final class AsyncCall implements Runnable {
        private OCallBack mOCallBack;

        public AsyncCall(OCallBack OCallBack) {
            mOCallBack = OCallBack;
        }

        @Override
        public void run() {
            LogUtils.dTag(OOkHttpClient.TAG, "AsyncCall.run");
            boolean signalledCallback = false;
            try {
                OResponse oResponse = getResponseWithInterceptorChain();
                if (mRetryAndFollowUpInterceptor.isCanceled()) {
                    signalledCallback = true;
                    mOCallBack.onFailure(ORealCall.this, new IOException("Canceled"));
                } else {
                    mOCallBack.onResponse(ORealCall.this, oResponse);
                }
            } catch (IOException e) {
                if (signalledCallback) {
                    Log.d(TAG, e.toString());
                } else {
                    mOCallBack.onFailure(ORealCall.this, e);
                }
            } finally {
                mOOkHttpClient.dispatcher().finished(this);
            }
        }
        ORealCall get() {
            return ORealCall.this;
        }
    }

    /**
     * 添加拦截器 去真正的处理请求
     *
     * @return
     */
    private OResponse getResponseWithInterceptorChain() throws IOException {
        LogUtils.dTag(OOkHttpClient.TAG, "AsyncCall.getResponseWithInterceptorChain");
        List<OInterceptor> lists = new ArrayList<>();
        lists.addAll(mOOkHttpClient.interceptors());
        lists.add(mRetryAndFollowUpInterceptor);
        lists.add(new OBridgeInterceptor());
        lists.addAll(mOOkHttpClient.networkInterceptors());
        lists.add(new OConnectInterceptor());
        lists.add(new OCallServerInterceptor());

        OInterceptor.OChain oChain = new ORealInterceptorChain(lists, 0, this);
        return oChain.proceed(mORequest);
    }
}
