package com.oyf.ookhttp;

import java.io.IOException;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 11:28
 * @描述
 **/
public interface OCallBack {
    void onFailure(OCall call, IOException e);

    void onResponse(OCall call, OResponse response) throws IOException;
}
