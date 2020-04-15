package com.oyf.ookhttp.interceptor;

import com.oyf.basemodule.log.LogUtils;
import com.oyf.ookhttp.OCall;
import com.oyf.ookhttp.OOkHttpClient;
import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.OResponse;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/13 11:12
 * @描述 重试
 **/
public class ORetryAndFollowUpInterceptor implements OInterceptor {

    private static final int MAX_FOLLOW_UPS = 20;
    private boolean isCancel;
    private final OOkHttpClient client;

    public ORetryAndFollowUpInterceptor(OOkHttpClient client) {
        this.client = client;
    }

    @Override
    public OResponse intercept(OChain chain) throws IOException {
        LogUtils.d();
        ORequest request = chain.request();
        ORealInterceptorChain oRealInterceptorChain = (ORealInterceptorChain) chain;

        int followUps = 0;
        while (true) {
            if (isCancel) {
                //streamAllocation.release();
                throw new IOException("Canceled");
            }
            OResponse response = null;
            try {
                response = oRealInterceptorChain.proceed(request);
                return response;
            } catch (IOException e) {
                followUps++;
                /**重试次数*/
                if (followUps > request.getRecount()) {
                    throw e;
                }
            }

        }
    }

    public boolean isCanceled() {
        return isCancel;
    }

    public void cancel() {
        isCancel = true;
    }
}
