package com.oyf.ookhttp;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:16
 * @描述
 **/
public class OOkHttpClient {

    final ODispatcher dispatcher;

    public OOkHttpClient(Builder builder) {
        dispatcher = builder.dispatcher;
    }

    public OCall newCall(ORequest request) {
        return ORealCall.newRealCall(this, request, false /* for web socket */);
    }

    public ODispatcher dispatcher() {
        return dispatcher;
    }

    public static final class Builder {
        ODispatcher dispatcher;
        List<OInterceptor> interceptors;
        List<OInterceptor> networkInterceptors;

        public Builder() {
            dispatcher = new ODispatcher();
            interceptors = new ArrayList<>();
            networkInterceptors = new ArrayList<>();
        }

        public Builder addInterceptor(OInterceptor oInterceptor) {
            interceptors.add(oInterceptor);
            return this;
        }

        public Builder addNetworkInterceptor(OInterceptor interceptor) {
            if (interceptor == null) throw new IllegalArgumentException("interceptor == null");
            networkInterceptors.add(interceptor);
            return this;
        }

        public OOkHttpClient build() {
            return new OOkHttpClient(this);
        }
    }
}
