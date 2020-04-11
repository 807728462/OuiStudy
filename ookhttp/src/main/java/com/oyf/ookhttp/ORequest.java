package com.oyf.ookhttp;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:16
 * @描述
 **/
public final class ORequest {

    final String url;
    final OHeaders headers;
    final String method;
    final ORequestBody body;

    public ORequest() {
        this(new Builder());
    }

    public ORequest(Builder builder) {
        this.url = builder.url;
        this.headers = builder.headers.build();
        this.method = builder.method;
        this.body = builder.body;
    }

    public String url() {
        return url;
    }

    public String method() {
        return method;
    }

    public OHeaders headers() {
        return headers;
    }

    public OHeaders.Builder newBuilder() {
        return headers.newBuilder();
    }

    public ORequestBody body() {
        return body;
    }

    public static final class Builder {
        String url;
        OHeaders.Builder headers;
        String method;
        ORequestBody body;

        public Builder() {
            this.method = "GET";
            this.headers = new OHeaders.Builder();
        }

        Builder(ORequest request) {
            this.url = request.url;
            this.method = request.method;
            this.body = request.body;
            this.headers = request.headers.newBuilder();
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder get() {
            this.method = "GET";
            return this;
        }

        public Builder post(ORequestBody oRequestBody) {
            this.method = "POST";
            body = oRequestBody;
            return this;
        }

        public Builder addHeader(String name, String value) {
            //headers.add(name, value);
            return this;
        }

        public ORequest build() {
            return new ORequest(this);
        }
    }

}
