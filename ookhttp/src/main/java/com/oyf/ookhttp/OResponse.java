package com.oyf.ookhttp;

import androidx.annotation.Nullable;

/**
 * @创建者 oyf
 * @创建时间 2020/4/11 10:16
 * @描述
 **/
public final class OResponse {

    public final static int RESPONSE_OK = 200;
    public final static int RESPONSE_NO_FOUND = 400;
    public final static int RESPONSE_REFUSE = 403;
    public final static int RESPONSE_SERVER_ERROR = 500;
    final ORequest request;
    final int code;
    final String message;
    final OHeaders headers;
    final OResponseBody body;

    public OResponse(Builder builder) {
        this.request = builder.request;
        this.code = builder.code;
        this.message = builder.message;
        this.headers = builder.headers.build();
        this.body = builder.body;
    }

    public String string() {
        return message;
    }

    public static class Builder {
        ORequest request;
        int code;
        String message;
        OHeaders.Builder headers;
        OResponseBody body;

        public Builder() {
            headers = new OHeaders.Builder();
        }

        Builder(OResponse response) {
            this.request = response.request;
            this.code = response.code;
            this.message = response.message;
            this.headers = response.headers.newBuilder();
            this.body = response.body;
        }

        public Builder request(ORequest request) {
            this.request = request;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the header named {@code name} to {@code value}. If this request already has any headers
         * with that name, they are all replaced.
         */
        public Builder header(String name, String value) {
            headers.set(name, value);
            return this;
        }

        /**
         * Adds a header with {@code name} and {@code value}. Prefer this method for multiply-valued
         * headers like "Set-Cookie".
         */
        public Builder addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        /**
         * Removes all headers on this builder and adds {@code headers}.
         */
        public Builder headers(OHeaders headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        public Builder body(@Nullable OResponseBody body) {
            this.body = body;
            return this;
        }

        public OResponse build() {
            if (request == null) throw new IllegalStateException("request == null");
            if (code < 0) throw new IllegalStateException("code < 0: " + code);
            if (message == null) throw new IllegalStateException("message == null");
            return new OResponse(this);
        }
    }
}
