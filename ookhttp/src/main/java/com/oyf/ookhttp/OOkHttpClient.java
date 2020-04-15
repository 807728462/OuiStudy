package com.oyf.ookhttp;

import com.oyf.ookhttp.interceptor.OInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:16
 * @描述
 **/
public class OOkHttpClient {
    public final static String TAG = OOkHttpClient.class.getSimpleName();

    final ODispatcher dispatcher;
    final List<OInterceptor> interceptors;
    final List<OInterceptor> networkInterceptors;

    public OOkHttpClient(Builder builder) {
        dispatcher = builder.dispatcher;
        interceptors = builder.interceptors;
        networkInterceptors = builder.networkInterceptors;
    }

    public OCall newCall(ORequest request) {
        return ORealCall.newRealCall(this, request, false /* for web socket */);
    }

    public ODispatcher dispatcher() {
        return dispatcher;
    }

    public List<OInterceptor> networkInterceptors() {
        return networkInterceptors;
    }

    public List<OInterceptor> interceptors() {
        return interceptors;
    }

    public Builder newBuilder() {
        return new Builder(this);
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

        public Builder(OOkHttpClient okHttpClient) {
            dispatcher = okHttpClient.dispatcher;
            interceptors.addAll(okHttpClient.interceptors);
            networkInterceptors.addAll(okHttpClient.networkInterceptors);
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
