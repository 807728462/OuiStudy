package com.oyf.oglide.model;

import com.oyf.oglide.utils.OPreconditions;

/**
 * @创建者 oyf
 * @创建时间 2020/4/20 16:11
 * @描述
 **/
public class OGlideUrl {
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%;$";
    private String url;
    private String keyUrl;

    public OGlideUrl(String url) {
        this.url = url;
        keyUrl = OPreconditions.sha256BytesToHex(url);
    }

    public String getUrl() {
        return keyUrl;
    }

    public String getPath() {
        return url;
    }
}
