package com.oyf.ookhttp.interceptor;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.ookhttp.OCall;
import com.oyf.ookhttp.OOkHttpClient;
import com.oyf.ookhttp.ORealCall;
import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.OResponse;
import com.oyf.ookhttp.socket.ORealConnection;

import java.io.IOException;
import java.util.List;

/**
 * @创建者 oyf
 * @创建时间 2020/4/13 14:57
 * @描述
 **/
public class ORealInterceptorChain implements OInterceptor.OChain {
    private List<OInterceptor> interceptors;
    private int index;
    private ORealCall asyncCall;
    private ORealConnection mORealConnection;

    public ORealInterceptorChain(List<OInterceptor> interceptors, int index, ORealCall asyncCall, ORealConnection ORealConnection) {
        this.interceptors = interceptors;
        this.index = index;
        this.asyncCall = asyncCall;
        mORealConnection = ORealConnection;
    }

    public ORealInterceptorChain(List<OInterceptor> interceptors, int index, ORealCall asyncCall) {
        this.interceptors = interceptors;
        this.index = index;
        this.asyncCall = asyncCall;
    }

    @Override
    public OResponse proceed(ORequest oRequest) throws IOException {
        if (index >= interceptors.size()) throw new AssertionError();

        LogUtils.dTag(OOkHttpClient.TAG, "ORealInterceptorChain.proceed");
        ORealInterceptorChain next = new ORealInterceptorChain(interceptors, index + 1, asyncCall, mORealConnection);

        OInterceptor oInterceptor = interceptors.get(index);
        OResponse oResponse = oInterceptor.intercept(next);
        return oResponse;
    }

    @Override
    public ORequest request() {
        return asyncCall.request();
    }

    public ORealConnection getORealConnection() {
        return mORealConnection;
    }

    public void setORealConnection(ORealConnection ORealConnection) {
        mORealConnection = ORealConnection;
    }

    public ORealInterceptorChain(ORealConnection ORealConnection) {
        mORealConnection = ORealConnection;
    }

    public OCall call() {
        return asyncCall;
    }
}
