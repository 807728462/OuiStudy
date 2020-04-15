package com.oyf.ookhttp.interceptor;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.ookhttp.OOkHttpClient;
import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.OResponse;
import com.oyf.ookhttp.OResponseBody;
import com.oyf.ookhttp.socket.ORealConnection;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/13 14:51
 * @描述
 **/
public class OCallServerInterceptor implements OInterceptor {
    @Override
    public OResponse intercept(OChain chain) throws IOException {
        LogUtils.dTag(OOkHttpClient.TAG, "OCallServerInterceptor.intercept");
        ORequest request = chain.request();
        OResponse.Builder builder = new OResponse.Builder()
                .request(request);

        ORealInterceptorChain oRealInterceptorChain = (ORealInterceptorChain) chain;
        ORealConnection oRealConnection = oRealInterceptorChain.getORealConnection();
        /**发送请求*/
        oRealConnection.writeRequest();
        /**获取请求结果*/
        int code = oRealConnection.readResponseCode();
        builder.code(code);
        if (OResponse.RESPONSE_OK == code) {
            String body = oRealConnection.readResponseBody();
            builder.message(body);
            OResponseBody oResponseBody = new OResponseBody();
            oResponseBody.body = body;
            builder.body(oResponseBody);
        }
        oRealConnection.release();
        return builder.build();
    }
}
