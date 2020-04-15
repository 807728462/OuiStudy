package com.oyf.ookhttp.socket;

import com.oyf.ookhttp.ORequest;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/15 10:25
 * @描述
 **/
public interface HttpCodec {
    ORequest getORequest();

    String createRequestBody();

    String writeRequestHeaders() throws IOException;
}
