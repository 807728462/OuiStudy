package com.oyf.ookhttp.interceptor;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.ookhttp.OOkHttpClient;
import com.oyf.ookhttp.OResponse;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/13 10:40
 * @描述
 **/
public class LogInterceptor implements OInterceptor {
    @Override
    public OResponse intercept(OChain chain) throws IOException {
        LogUtils.dTag(OOkHttpClient.TAG, "LogInterceptor.proceed");
        return chain.proceed(chain.request());
    }
}
