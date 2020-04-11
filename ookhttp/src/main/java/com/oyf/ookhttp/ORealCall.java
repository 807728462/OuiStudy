package com.oyf.ookhttp;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 11:20
 * @描述
 **/
public class ORealCall implements OCall {
    private OOkHttpClient mOOkHttpClient;
    private ORequest mORequest;
    private boolean forWebSocket;

    private boolean executed;//保证每个请求只能执行一次

    private ORealCall(OOkHttpClient okHttpClient, ORequest request, boolean forWebSocket) {
        this.mOOkHttpClient = okHttpClient;
        this.mORequest = request;
        this.forWebSocket = forWebSocket;
        executed = false;
    }

    public static OCall newRealCall(OOkHttpClient okHttpClient, ORequest request, boolean forWebSocket) {
        ORealCall oRealCall = new ORealCall(okHttpClient, request, forWebSocket);
        return oRealCall;
    }

    @Override
    public void enqueue(OCallBack callBack) {
        synchronized (this) {
            if (executed)
                throw new IllegalStateException("Already Executed,已经执行过一次请求 :" + mORequest.url);
            executed = true;
        }

        mOOkHttpClient.dispatcher().enqueue(new ODispatcher.AsyncCall(callBack));

    }

}
