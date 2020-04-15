package com.oyf.ookhttp.interceptor;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.ookhttp.OHeaders;
import com.oyf.ookhttp.OOkHttpClient;
import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.OResponse;
import com.oyf.ookhttp.Utils;

import java.io.IOException;
import java.util.Map;

/**
 * @创建者 oyf
 * @创建时间 2020/4/13 14:49
 * @描述 请求与响应 拦截器处理
 **/
public class OBridgeInterceptor implements OInterceptor {
    @Override
    public OResponse intercept(OChain chain) throws IOException {
        LogUtils.dTag(OOkHttpClient.TAG, "OBridgeInterceptor.proceed");
        ORequest request = chain.request();

        OHeaders headers = request.headers();
        Map<String, String> heads = headers.getHeads();
        /**
         * Host: www.jianshu.com
         */
        // get post  hostName    Host: restapi.amap.com
        heads.put("Host", Utils.hostHeader(request.url()));

        //post请求需要添加
        /**
         * Content-Length: 48
         * Content-Type: application/x-www-form-urlencoded
         * */
        if (ORequest.POST.equalsIgnoreCase(request.method())) {
            heads.put("Content-Length", request.body().getBody().length() + "");
            heads.put("Content-Type", ORequest.CONTENT_TYPE);
        }
        OResponse oResponse = chain.proceed(chain.request());
        return oResponse;
    }
}
