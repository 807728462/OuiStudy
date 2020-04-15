package com.oyf.ookhttp.interceptor;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.ookhttp.OOkHttpClient;
import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.OResponse;
import com.oyf.ookhttp.socket.Http1Codec;
import com.oyf.ookhttp.socket.HttpCodec;
import com.oyf.ookhttp.socket.ORealConnection;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/13 14:51
 * @描述
 **/
public class OConnectInterceptor implements OInterceptor {

    @Override
    public OResponse intercept(OChain chain) throws IOException {

        LogUtils.dTag(OOkHttpClient.TAG, "OConnectInterceptor.intercept");

        ORequest request = chain.request();
        ORealInterceptorChain realInterceptorChain = (ORealInterceptorChain) chain;
        /**创建连接，并且去连接服务器*/
        HttpCodec httpcodec = new Http1Codec(request);
        ORealConnection realConnection = new ORealConnection(httpcodec);
        realConnection.connect();
        realInterceptorChain.setORealConnection(realConnection);
        OResponse oResponse = realInterceptorChain.proceed(request);
        realConnection.release();
        return oResponse;
    }
}
