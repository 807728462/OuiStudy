package com.oyf.ookhttp.socket;

import com.oyf.ookhttp.ORequest;
import com.oyf.ookhttp.OResponseBody;
import com.oyf.ookhttp.Utils;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/15 10:25
 * @描述
 **/
public class Http1Codec implements HttpCodec {
    private ORequest mORequest;

    public Http1Codec(ORequest ORequest) {
        mORequest = ORequest;
    }

    @Override
    public String createRequestBody() {
        // TODO POST请求才有 请求体的拼接
        return mORequest.body().getBody() + Utils.GRGN;
    }

    @Override
    public String writeRequestHeaders() throws IOException {
        String requestAll = Utils.getRequestHeaderAll(mORequest);
        return requestAll;
    }

    public ORequest getORequest() {
        return mORequest;
    }
}
