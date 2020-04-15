package com.oyf.ookhttp.interceptor;

import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.OResponse;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:39
 * @描述
 **/
public interface OInterceptor {

    OResponse intercept(OChain chain) throws IOException;

    interface OChain {
        OResponse proceed(ORequest oRequest) throws IOException;

        ORequest request();
    }
}
